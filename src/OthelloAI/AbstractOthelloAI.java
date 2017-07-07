package OthelloAI;

import com.iciql.Dao;
import com.iciql.Db;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by watariMac on 2017/06/23.
 * ベースとなるAIのアブストラクトクラス。
 */
public abstract class AbstractOthelloAI extends Thread {
    protected Socket socket;
    protected PrintWriter pw;
    protected BufferedReader br;
    protected String nickName;
    protected String color;
    protected String[] board;
    public Db othelloDb;


    ArrayList<BoardRecord> recordList = new ArrayList<>();
    Boolean[] lawfullArray = new Boolean[64]; //そこに手が置けるかのフラグ。

    AbstractOthelloAI(Socket sc, String nick, Db db) throws IOException {
        socket = sc;
        this.othelloDb = db;
        pw = new PrintWriter(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        nickName = nick;
        pw.println("NICK " + nickName);
        pw.flush();
        board = new String[64];
        Arrays.fill(board, "0");
        Arrays.stream(lawfullArray).forEach(i -> i = true);
    }


    public ArrayList<BoardRecord> getRecordList() {
        return recordList;
    }


    abstract void sendPut();


    /**
     * ランダムにおくメソッド。
     */
    void sendRandomPut() {
        int put;
        do {
            put = (int) (Math.random() * 64);
        } while (!lawfullArray[put]);

        int x = put / 8;
        int y = put % 8;

        lawfullArray[put] = false;
        recordList.add(new BoardRecord(board, color, x, y));
        pw.println("PUT " + x + " " + y);
        pw.flush();
    }


    /**
     * DBを更新するメソッド
     */
    public void updateBoardRecordDb() throws SQLException{
        
        ArrayList<BoardRecordModel> recordModelList = new ArrayList<>();
        recordList.forEach(r -> recordModelList.add(r.getModel()));
        /*recordList.forEach(r -> recordModelList.add(r.rotationBoard().getModel()));
        recordList.forEach(r -> recordModelList.add(r.rotationBoard().rotationBoard().getModel()));
        recordList.forEach(r -> recordModelList.add(r.rotationBoard().rotationBoard().rotationBoard().getModel()));*/

        for (BoardRecordModel model : recordModelList) {
            BoardRecordModel brcord = new BoardRecordModel();
            List<BoardRecordModel> selectList =
                    othelloDb.executeQuery(brcord.getClass(),
                            "select * from board_record where board=? and put_x=? and put_y=? for update",
                            model.board,model.put_x,model.put_y);

            if (selectList.isEmpty()) {
                othelloDb.insert(model);
            } else {
                for (BoardRecordModel s : selectList) {
                    s.score += model.score;
                    s.try_count += model.try_count;
                    othelloDb.update(s);
                }
            }
            othelloDb.getConnection().commit();
        }
    }


    @Override
    public void run() {
        Boolean isWhile = true;
        try {
            while (isWhile) {
                String message = br.readLine();
                String[] messageArray = message.split(" ");

                switch (messageArray[0]) {

                    case "START":
                        color = messageArray[1];
                        break;

                    case "TURN":
                        if (messageArray[1].equals(color)) {
                            sendPut();
                        }
                        break;

                    case "BOARD":
                        board = Arrays.copyOfRange(messageArray, 1, messageArray.length);
                        for (int i = 0; i < lawfullArray.length; i++)
                            lawfullArray[i] = board[i].equals("0") ? true : false;
                        break;

                    case "ERROR":
                        //エラーの解決
                        recordList.remove(recordList.size() - 1);
                        sendPut();

                        break;

                    case "END":
                        // END処理
                        isWhile = false;
                        break;
                }
            }

            socket.close();

        } catch (IOException e) {
            System.out.println("message Error");
            e.printStackTrace();
        }

        int MyCount = (int) Arrays.stream(board).filter(b -> b.equals(color)).count();//自分の数
        int NoneCount = (int) Arrays.stream(board).filter(b -> b.equals("0")).count();//置かれていないますの数
        int EnemyCount = 64 - MyCount - NoneCount; //相手の数

        for (int i = 0; i < recordList.size(); i++) {
            BoardRecord br = recordList.get(i);
            int w = MyCount >= EnemyCount ? 1 : -1;
            br.addScore(w * Math.pow(0.9, recordList.size() - i));
        }

        System.out.println(nickName + "-" + MyCount);
    }

}
