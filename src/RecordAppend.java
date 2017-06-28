import OthelloAI.BoardRecord;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by watariMac on 2017/06/25.
 */
public class RecordAppend {

    /**
     * 二つのテストデータを結合し一つにする
     *
     * @param args
     */
    static public void main(String args[]) {
        String file1 = "log.txt";
        String file2 = "result5.txt";
        String file3 = "result5l.txt";

        for (int i = 0; i < args.length; ++i) {
            if ("-fi1".equals(args[i])) {
                file1 = args[++i];
            } else if ("-fi2".equals(args[i])) {
                file2 = args[++i];
            } else if ("-fo".equals(args[i])) {
                file3 = args[++i];
            } else {
                System.out.print("おかしい");
            }
        }


        try

        {
            ArrayList<BoardRecord>[] boardRecordList1 = readRecord(file1);
            ArrayList<BoardRecord>[] boardRecordList2 = readRecord(file2);

            System.out.println(file1 + ": size");
            System.out.println(file2 + ": size");

            //結合してboardRecordList2に代入
            int index = 0;
            for (ArrayList<BoardRecord> brList1 : boardRecordList1) {
                for (BoardRecord br1 : brList1) {
                    ArrayList<BoardRecord> brList2 = boardRecordList2[br1.getKey() % boardRecordList2.length];
                    if (brList2.stream().anyMatch(br2 -> br2.isActionEqual(br1))) {
                        brList2.stream().filter(br2 -> br2.isActionEqual(br1)).forEach(br2 -> br2.add(br1));
                        //System.out.println("maerge "+index);
                    } else {
                        brList2.add(br1);
                    }
                }
                System.out.println(index + "/ " + boardRecordList1.length);
                index++;
            }

            //ファイル出力
            File appendFile = new File(file3);
            BufferedWriter bw = new BufferedWriter(new FileWriter(appendFile));
            for (ArrayList<BoardRecord> brList2 : boardRecordList2) {
                for (BoardRecord br2 : brList2) {
                    bw.write(br2.toString());
                    bw.newLine();
                }
            }
            bw.close();
            System.out.print("join-> " + appendFile);
        } catch (
                IOException e
                )

        {
            e.printStackTrace();
        }

    }

    /**
     * @param inputFile 読み込むファイル名
     * @return ファイルから生成されたテストデータ
     * @throws IOException
     */
    private static ArrayList<BoardRecord>[] readRecord(String inputFile) throws IOException {
        File file = new File(inputFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<BoardRecord>[] brListArray = new ArrayList[57];

        for (int i = 0; i < brListArray.length; i++)
            brListArray[i] = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            BoardRecord record = new BoardRecord(line);
            brListArray[record.getKey() % brListArray.length].add(record);
        }
        return brListArray;
    }
}
