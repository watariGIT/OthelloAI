import OthelloAI.AbstractOthelloAI;
import OthelloAI.BoardRecord;
import OthelloAI.RandomOthelloAI;
import OthelloAI.TableMonteOthelloAI;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

import com.iciql.Db;

/**
 * Created by watariMac on 2017/06/23.
 */
public class Main {
    static int serverPort = 25033;
    static String serverIp = "localhost";

    static public void main(String args[]) {
        String sqlip = "127.0.0.1:3306";
        String mode = "t";
        int count = 100;

        //標準入力の処理
        for (int i = 0; i < args.length; ++i) {
            if ("-mode".equals(args[i])) {
                mode = args[++i];
            } else if ("-count".equals(args[i])) {
                count = Integer.parseInt(args[++i]);
            } else if ("-ip".equals(args[i])) {
                serverIp = args[++i];
            } else if ("-port".equals(args[i])) {
                serverPort = Integer.parseInt(args[++i]);
            } else if ("-mode".equals(args[i])) {
                mode = args[++i];
            } else if ("-sqlip".equals(args[i])) {
                sqlip = args[++i];
            } else {
                System.err.println("引数指定の誤り");
            }
        }

        try {
            Db othelloDB = Db.open("jdbc:mysql://" + sqlip + "/othelloDB", "othello", "password");
            othelloDB.setAutoSavePoint(false);
            othelloDB.getConnection().setAutoCommit(false);

            switch (mode) {
                case "t":
                    //テストデータ収集用
                    training(othelloDB, count);
                    break;
                case "vs":
                    //対人用
                    vsHuman(othelloDB);
                    break;
            }

        } catch (IOException | SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * AI対AIをしてデータを集める
     *
     * @param othelloDb
     * @param count     対戦数
     * @throws InterruptedException
     * @throws IOException
     */
    private static void training(Db othelloDb, int count) throws InterruptedException, IOException, SQLException {
        for (int j = 0; j < count; j++) {
            double[][] scoreTable = TableMonteOthelloAI.getScoreTable(othelloDb);

            for (int i = 0; i < 100; i++) {

                AbstractOthelloAI ai1 = new TableMonteOthelloAI(new Socket(serverIp, serverPort), "player0.4", othelloDb, scoreTable, 0.4);
                AbstractOthelloAI ai2 = new RandomOthelloAI(new Socket(serverIp, serverPort), "Player2", othelloDb);

                ai1.start();
                ai2.start();

                ai1.join();
                ai2.join();

                //DBの更新
                ai1.updateBoardRecordDb();
                ai2.updateBoardRecordDb();
                System.out.println("GAME " + (i + j * 100) + " END");
            }
        }
    }


    /**
     * 対人用AI
     *
     * @param othelloDb ファイル出力先
     * @throws InterruptedException
     * @throws IOException
     */
    private static void vsHuman(Db othelloDb) throws InterruptedException, IOException {
        while (true) {
            AbstractOthelloAI ai1 = new TableMonteOthelloAI(new Socket(serverIp, serverPort), "Table", othelloDb, 0.0);
            ai1.start();
            ai1.join();

        }
    }

}
