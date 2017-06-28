import OthelloAI.BoardRecord;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by watariMac on 2017/06/25.
 */
public class OldRecord2New {

    /**
     * 古い書式を新しい書式のテストデータを生成
     *
     * @param args
     */
    static public void main(String args[]) {
        String fileName = "result.txt";
        String newFileName = "result0.txt";

        for (int i = 0; i < args.length; ++i) {
            if ("-fi".equals(args[i])) {
                fileName = args[++i];
            } else if ("-fo".equals(args[i])) {
                newFileName = args[++i];
            }else{
            System.out.print("おかしい");
            }
        }

        try {
            ArrayList<BoardRecord> oldRecord = readRecord(fileName);
            ArrayList<BoardRecord>[] newRecordArray = new ArrayList[57];

            for (int i = 0; i < newRecordArray.length; i++) {
                newRecordArray[i] = new ArrayList<>();
            }

            System.out.println("Read " + fileName);
            System.out.println("size " + oldRecord.size());

            for (int i = 0; i < oldRecord.size(); i++) {
                BoardRecord br = oldRecord.get(i);

                ArrayList<BoardRecord> newRecord = newRecordArray[
                        br.getKey() > 0 ?
                                br.getKey() % newRecordArray.length : br.getKey() % newRecordArray.length * -1];
                if (newRecord.stream().anyMatch(n -> n.isActionEqual(br))) {
                    System.out.println("match " + i);
                    newRecord.stream().filter(n -> n.isActionEqual(br)).forEach(n -> n.add(br));
                } else {
                    newRecord.add(br);
                }

                if(i%10000==0)
                    System.out.println(i);
            }

            ArrayList<BoardRecord> newRecord = new ArrayList<>();
            Arrays.stream(newRecordArray).forEach(n -> newRecord.addAll(n));
            System.out.println(oldRecord.size() + " -> " + newRecord.size());

            File file = new File(newFileName);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (BoardRecord br : newRecord) {
                bw.write(br.toString());
                bw.newLine();
            }
            bw.close();
            System.out.println("Write " + newFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param inputFile 読み込むファイル名
     * @return ファイルから生成されたテストデータ
     * @throws IOException
     */
    private static ArrayList<BoardRecord> readRecord(String inputFile) throws IOException {
        File file = new File(inputFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<BoardRecord> brList = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            brList.add(new BoardRecord(line));
        }
        return brList;
    }


    static ArrayList<BoardRecord> readOldRecord(String inputFile) throws IOException {
        File file = new File(inputFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<BoardRecord> brList = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            String[] fileLineArray = line.split("/");
            IntStream board = Arrays.stream(fileLineArray[0].split(","))
                    .mapToInt(Integer::parseInt);
            board = board.map(b -> b * Integer.parseInt(fileLineArray[1]));
            brList.add(
                    new BoardRecord(
                            board.mapToObj(String::valueOf).toArray(String[]::new)
                            , Integer.parseInt(fileLineArray[2])
                            , Integer.parseInt(fileLineArray[3])
                            , Integer.parseInt(fileLineArray[4])
                            , Double.parseDouble(fileLineArray[5])));
        }
        return brList;
    }
}
