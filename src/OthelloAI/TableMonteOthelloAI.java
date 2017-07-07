package OthelloAI;

import com.iciql.Db;
import com.iciql.Iciql;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by watariMac on 2017/06/27.
 */
public class TableMonteOthelloAI extends AbstractOthelloAI {
    private double scoreTable[][] = new double[64][64];
    private double random = 0.0;


    public TableMonteOthelloAI(Socket sc, String nick, Db othelloDb, double rand) throws IOException {
        super(sc, nick, othelloDb);
        random = rand;
        Arrays.stream(scoreTable).map(t -> Arrays.stream(t).map(d -> d = 0.0));

        //DBから評価値テーブルの生成
        scoreTable=getScoreTable(othelloDb);
    }


    public TableMonteOthelloAI(Socket sc, String nick, Db othelloDb, double[][] scoreTable, double rand) throws IOException {
        super(sc, nick, othelloDb);
        random = rand;
        Arrays.stream(scoreTable).map(t -> Arrays.stream(t).map(d -> d = 0.0));

        //DBから評価値テーブルの生成
        this.scoreTable = scoreTable;
    }


    @Override
    void sendPut() {
        int px;
        int py;

        if (Math.random() < random) {
            sendRandomPut();
            return;
        }

        //DBから同じ盤面があったか調べる
        BoardRecordModel br = new BoardRecordModel();
        List<BoardRecordModel> recordModelList = othelloDb.from(br)
                .where(br.board)
                .is(new BoardRecord(board, color, 0, 0).getModel().board)
                .select();


        Optional<BoardRecordModel> maxOpt
                = recordModelList.stream()
                .max((a, b) -> (int) (a.score / (double) a.try_count - b.score / (double) b.try_count));

        if (maxOpt.isPresent() && maxOpt.get().score >= 0) {

            BoardRecordModel maxModel = maxOpt.get();
            px = maxModel.put_x;
            py = maxModel.put_y;
            System.out.println("put (" + px + ", " + py + ")");

        } else {

            //データになければ評価テーブルに従う
            int turn = (int) Arrays.stream(board).filter(s -> !s.equals("0")).count() - 1;
            double[] turnScoreTable = scoreTable[turn];
            int put = 0;
            for (int i = 0; i < lawfullArray.length; i++) {
                if (lawfullArray[i]) {
                    put = i;
                    break;
                }
            }
            for (int i = 0; i < turnScoreTable.length; i++) {
                if (turnScoreTable[put] < turnScoreTable[i] && lawfullArray[i])
                    put = i;
            }
            px = put / 8;
            py = put % 8;
            lawfullArray[put] = false;
            //System.out.println(nickName + "< TABLE PUT" + px + "," + py);
        }

        recordList.add(new BoardRecord(board, color, px, py));
        pw.println("PUT " + px + " " + py);
        pw.flush();
    }


    public static double[][] getScoreTable(Db db) {
        //DBから評価値テーブルの生成
        double[][] scoreTable = new double[64][64];
        BoardRecordModel br = new BoardRecordModel();
        List<BoardRecordModel> recordModelList = db.from(br).select();
        for (BoardRecordModel model : recordModelList) {
            String[] boards = model.board.split(",");
            int turn = (int) Arrays.stream(boards).filter(s -> !s.equals("0")).count() - 1;
            int x = model.put_x;
            int y = model.put_y;
            scoreTable[turn][x * 8 + y] += model.score / model.try_count;
        }
        System.out.println("Create Score Table");
        return scoreTable;
    }
}
