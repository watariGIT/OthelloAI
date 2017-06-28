package OthelloAI;

import OthelloAI.AbstractOthelloAI;
import OthelloAI.BoardRecord;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by watariMac on 2017/06/27.
 */
public class TableOthelloAI extends AbstractOthelloAI {
    double scoreTable[][] = new double[64][64];

    TableOthelloAI(Socket sc, String nick, ArrayList<BoardRecord>[] tr) throws IOException {
        super(sc, nick);
        Arrays.stream(scoreTable).map(t -> Arrays.stream(t).map(d -> d = 0.0));

        //評価値テーブルの生成
        for (ArrayList<BoardRecord> tList : tr) {
            for (BoardRecord t : tList) {
                int turn = (int) Arrays.stream(t.board).filter(s -> s != 0).count() + 1;
                for (int j = 0; j < 64; j++) {
                    int x = t.putX;
                    int y = t.putY;
                    for (int i = 0; i < 4; i++) {
                        scoreTable[j][x * 8 + y] += t.getAverageScore() * Math.pow(0.9, Math.abs(turn - j));
                        int a = x;
                        x = y;
                        y = 7 - a;
                    }
                }
            }
        }
        System.out.print(nick + "< " + "make scortable");
    }


    @Override
    void sendPut() {
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

        int x = put / 8;
        int y = put % 8;

        lawfullArray[put] = false;
        recordList.add(new BoardRecord(board, color, x, y));
        pw.println("PUT " + x + " " + y);
        pw.flush();
    }
}
