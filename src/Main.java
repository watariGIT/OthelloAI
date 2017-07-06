import OthelloAI.AbstractOthelloAI;
import OthelloAI.BoardRecord;
import OthelloAI.RandomOthelloAI;
import OthelloAI.TableMonteOthelloAI;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import com.iciql.Db;

/**
 * Created by watariMac on 2017/06/23.
 */
public class Main {
    static int serverPort = 25033;
    static String serverIp = "localhost";
    static ArrayList<BoardRecord>[] trainingRecord;

    static public void main(String args[]) {
        Db db=Db.open("jdbc:mysql://127.0.0.1:3306/othelloDB","othello","password");
        String mode = "vs";
        int count = 1000;
        String inputFileName = "data";
        String outputFileName = "outputData";

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
            } else if ("-fi".equals(args[i])) {
                inputFileName = args[++i];
            } else if ("-fo".equals(args[i])) {
                outputFileName = args[++i];
            } else {
                System.err.println("引数指定の誤り");
            }
        }

        try {
            trainingRecord = readRecord(inputFileName);
            System.out.println("Read " + inputFileName);

            switch (mode) {
                case "t":
                    //テストデータ収集用
                    training(outputFileName, count);
                    break;
                case "vs":
                    //対人用
                    vsHuman(outputFileName);
                    break;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param inputFile 読み込むファイル名
     * @return ファイルから生成されたテストデータ
     * @throws IOException
     */
    public static ArrayList<BoardRecord>[] readRecord(String inputFile) throws IOException {
        File file = new File(inputFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<BoardRecord>[] record = new ArrayList[57];

        for (int i = 0; i < record.length; i++) {
            record[i] = new ArrayList<>();
        }

        String line;
        while ((line = br.readLine()) != null) {
            BoardRecord b = new BoardRecord(line);
            record[b.getKey() % record.length].add(b);
        }
        return record;
    }


    /**
     * AI対AIをしてデータを集める
     *
     * @param outputFile 出力先のファイル名
     * @param count      対戦数
     * @throws InterruptedException
     * @throws IOException
     */
    private static void training(String outputFile, int count) throws InterruptedException, IOException {

        //出力データの初期化
        ArrayList<BoardRecord>[] outputRecord = new ArrayList[57];
        for (int i = 0; i < outputRecord.length; i++) {
            outputRecord[i] = new ArrayList<>();
            outputRecord[i].addAll(trainingRecord[i]);
        }

        for (int j = 0; j < count / 10; j++) {
            for (int i = 0; i < 10; i++) {
                AbstractOthelloAI ai1;

                if (i < 3) {
                    ai1 = new RandomOthelloAI(new Socket(serverIp, serverPort), "Player1");
                } else {
                    ai1 = new TableMonteOthelloAI(new Socket(serverIp, serverPort), "player0.4", outputRecord,0.4);
                }

                AbstractOthelloAI ai2 = new TableMonteOthelloAI(new Socket(serverIp, serverPort), "うんこまる", outputRecord,0.0);

                ai1.start();
                ai2.start();

                ai1.join();
                ai2.join();

                //データの集計
                for (BoardRecord airecord : ai1.getRecordList()) {
                    if (outputRecord[airecord.getKey()%outputRecord.length].stream().noneMatch(br -> br.isActionEqual(airecord)))
                        outputRecord[airecord.getKey()%outputRecord.length].add(airecord);
                    else
                        outputRecord[airecord.getKey()%outputRecord.length].stream()
                                .filter(br -> br.isActionEqual(airecord)).forEach(br -> br.add(airecord));
                }

                for (BoardRecord airecord : ai2.getRecordList()) {
                    if (outputRecord[airecord.getKey() % outputRecord.length].stream().noneMatch(br -> br.isActionEqual(airecord)))
                        outputRecord[airecord.getKey() % outputRecord.length].add(airecord);
                    else
                        outputRecord[airecord.getKey() % outputRecord.length].stream()
                                .filter(br -> br.isActionEqual(airecord)).forEach(br -> br.add(airecord));
                }

                System.out.println("GAME " + (i + j * 10) + " END");
            }

            //ファイル出力
            File file = new File(outputFile);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (ArrayList<BoardRecord> brList : outputRecord) {
                for (BoardRecord br : brList) {
                    bw.write(br.toString());
                    bw.newLine();
                }
            }
            bw.close();
            System.out.println("Print " + outputFile);
        }
    }


    /**
     * 対人用AI
     *
     * @param outputFile ファイル出力先
     * @throws InterruptedException
     * @throws IOException
     */
    private static void vsHuman(String outputFile) throws InterruptedException, IOException {
        ArrayList<BoardRecord>[] outputRecord = new ArrayList[57];
        for (int i = 0; i < outputRecord.length; i++) {
            outputRecord[i] = new ArrayList<>();
            outputRecord[i].addAll(trainingRecord[i]);
        }

        while (true) {
            AbstractOthelloAI ai1 = new TableMonteOthelloAI(new Socket(serverIp, serverPort), "Table", trainingRecord,0.0);
            ai1.start();
            ai1.join();

            //データ集計
            for (BoardRecord airecord : ai1.getRecordList()) {
                if (outputRecord[airecord.getKey() % outputRecord.length].stream().noneMatch(br -> br.isActionEqual(airecord)))
                    outputRecord[airecord.getKey() % outputRecord.length].add(airecord);
                else
                    outputRecord[airecord.getKey() % outputRecord.length].stream()
                            .filter(br -> br.isActionEqual(airecord)).forEach(br -> br.add(airecord));
            }

            //ファイル出力
            File file = new File(outputFile);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (ArrayList<BoardRecord> brList : outputRecord) {
                for (BoardRecord br : brList) {
                    bw.write(br.toString());
                    bw.newLine();
                }
            }
            bw.close();
            System.out.print("save " + outputFile);

        }
    }

}
