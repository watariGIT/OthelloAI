package OthelloAI;

import java.util.Arrays;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by watariMac on 2017/06/23.
 * テストデータを表すクラス
 */
public class BoardRecord {
    int[] board; //盤面を表す配列。
    int putX; //おく座標
    int putY; //おく座標
    private double score = 0.0; //評価値

    BoardRecord(String[] b, String c, int x, int y) {
        board = Arrays.stream(b)
                .mapToInt(p -> Integer.parseInt(p) * Integer.parseInt(c))
                .toArray();
        putX = x;
        putY = y;
    }


    public BoardRecord(String[] b, int x, int y, double s) {
        board = Arrays.stream(b)
                .mapToInt(Integer::parseInt)
                .toArray();
        putX = x;
        putY = y;
        score = s;
    }


    BoardRecord(BoardRecord b) {
        board = b.board;
        putX = b.putX;
        putY = b.putY;
        score = b.score;
    }


    public BoardRecordModel getModel(){
        String[] boards = Arrays.stream(board).mapToObj(String::valueOf).toArray(String[]::new);
        return new BoardRecordModel(String.join(",", boards),putX,putY,score,1);
    }


    /**
     * 盤面を回転するメソッド
     * @return
     */
    BoardRecord rotationBoard() {
        String[] rotationBoard = new String[64];
        for (int i = 0; i < 64; i++) {
            int rotationBoardX = i % 8;
            int rotationBoardY = 7 - i / 8;
            rotationBoard[rotationBoardX % 8 + rotationBoardY * 8] = String.valueOf(board[i]);
        }
        int rotationPutX = putY;
        int rotationPutY = 7 - putX;
        return new BoardRecord(rotationBoard, rotationPutX, rotationPutY, score);
    }


    /**
     * 評価値をプラスする
     *
     * @param s 加える評価値
     */
    void addScore(double s) {
        score += s;
    }


    @Override
    public String toString() {
        String[] boards = Arrays.stream(board).mapToObj(String::valueOf).toArray(String[]::new);
        return String.join(",", boards) + "/" + putX + "/" + putY + "/" + score;
    }
}
