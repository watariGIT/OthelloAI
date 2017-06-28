package OthelloAI;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by watariMac on 2017/06/27.
 */
public class TableMonteOthelloAI extends AbstractOthelloAI {
    ArrayList<BoardRecord>[] trainingData = new ArrayList[57];
    double scoreTable[][] = new double[64][64];
    double random = 0.0;

    public TableMonteOthelloAI(Socket sc, String nick, ArrayList<BoardRecord>[] tr, double rand) throws IOException {
        super(sc, nick);
        random = rand;
        Arrays.stream(scoreTable).map(t -> Arrays.stream(t).map(d -> d = 0.0));

        for (int i = 0; i < trainingData.length; i++) {
            trainingData[i] = tr[i];
        }

        //評価値テーブルの生成
        for (ArrayList<BoardRecord> tList : tr) {
            for (BoardRecord t : tList) {
                for (int j = 0; j < 64; j++) {
                    int x = t.putX;
                    int y = t.putY;
                    for (int i = 0; i < 4; i++) {
                        scoreTable[j][x * 8 + y] += t.getAverageScore();
                        int a = x;
                        x = y;
                        y = 7 - a;
                    }
                }
            }
        }
        System.out.print(nickName + " <make score table");

    }


    @Override
    void sendPut() {
        int px;
        int py;

        if (Math.random() < random) {
            sendRandomPut();
            return;
        }

        ArrayList<BoardRecord> tdList = trainingData[BoardRecord.getKey(board, color) % trainingData.length];
        Stream<BoardRecord> tdStream = tdList.stream().filter(b -> b.isBoardEqual(board, color));
        Optional<BoardRecord> obr = tdStream.max((br1, br2) -> (int) (br1.getAverageScore() * 10000 - br2.getAverageScore() * 10000));

        if (obr.isPresent() && obr.get().getAverageScore() >=0) {
            int rotationCount = obr.get().getRotationCount(board, color);
            px = obr.get().putX;
            py = obr.get().putY;
            for (int i = 0; i < rotationCount; i++) {
                int a = px;
                px = py;
                py = 7 - a;
            }
            System.out.println(nickName + "< PUT" + px + "," + py);

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
            System.out.println(nickName + "< TABLE PUT" + px + "," + py);
        }

        recordList.add(new BoardRecord(board, color, px, py));
        pw.println("PUT " + px + " " + py);
        pw.flush();
    }

}
