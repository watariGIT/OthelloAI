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
    private double score = 0.0; //評価値の合計
    private int tryCount = 0; //このテストデータの試行回数


    BoardRecord(String[] b, String c, int x, int y) {
        board = Arrays.stream(b)
                .mapToInt(p -> Integer.parseInt(p) * Integer.parseInt(c))
                .toArray();
        putX = x;
        putY = y;
        tryCount++;
    }


    public BoardRecord(String[] b, int x, int y, int t, double s) {
        board = Arrays.stream(b)
                .mapToInt(Integer::parseInt)
                .toArray();
        putX = x;
        putY = y;
        score = s;
        tryCount = t;
    }


    BoardRecord(BoardRecord b) {
        board = b.board;
        putX = b.putX;
        putY = b.putY;
        score = b.score;
        tryCount = b.tryCount;
    }


    public BoardRecord(String fileLine) {
        String[] fileLineArray = fileLine.split("/");
        board = Arrays.stream(fileLineArray[0].split(",")).mapToInt(Integer::parseInt).toArray();
        putX = Integer.parseInt(fileLineArray[1]);
        putY = Integer.parseInt(fileLineArray[2]);
        tryCount = Integer.parseInt(fileLineArray[3]);
        score = Double.parseDouble(fileLineArray[4]);
    }


    /**
     * 同じ盤面で同じ動作ならTRUE
     *
     * @param b 比較対象
     * @return 同じ盤面で同じ動作
     */
    public boolean isActionEqual(BoardRecord b) {
        if (getKey() != b.getKey())
            return FALSE;

        if (b.putX == putX && b.putY == putY && Arrays.equals(b.board, board))
            return TRUE;
        BoardRecord rb = new BoardRecord(b);

        for (int i = 0; i < 3; i++) {
            rb = rb.rotationBoard();
            if (putX == rb.putX && putY == rb.putY && Arrays.equals(board, rb.board))
                return TRUE;
        }
        return FALSE;
    }


    /**
     * 同じ盤面ならTRUE
     *
     * @param b 盤面
     * @param c 色
     * @return 同じならTRUE
     */
    boolean isBoardEqual(String b[], String c) {
        int[] bArray = Arrays.stream(b)
                .mapToInt(p -> Integer.parseInt(p) * Integer.parseInt(c))
                .toArray();
        int key = BoardRecord.getKey(b, c);

        if (getKey() != key)
            return FALSE;

        if (Arrays.equals(bArray, board))
            return TRUE;
        BoardRecord rb = new BoardRecord(this);
        for (int i = 0; i < 3; i++) {
            rb = rb.rotationBoard();
            if (Arrays.equals(bArray, rb.board))
                return TRUE;
        }
        return FALSE;
    }

    BoardRecord rotationBoard() {
        String[] rotationBoard = new String[64];
        for (int i = 0; i < 64; i++) {
            int rotationBoardX = i % 8;
            int rotationBoardY = 7 - i / 8;
            rotationBoard[rotationBoardX % 8 + rotationBoardY * 8] = String.valueOf(board[i]);
        }
        int rotationPutX = putY;
        int rotationPutY = 7 - putX;
        return new BoardRecord(rotationBoard, rotationPutX, rotationPutY, tryCount, score);
    }

    int getRotationCount(String[] b, String c) {
        int[] bArray = Arrays.stream(b)
                .mapToInt(p -> Integer.parseInt(p) * Integer.parseInt(c))
                .toArray();

        if (Arrays.equals(bArray, board))
            return 0;
        BoardRecord rb = new BoardRecord(this);
        for (int i = 0; i < 3; i++) {
            rb = rb.rotationBoard();
            if (Arrays.equals(bArray, rb.board))
                return i + 1;
        }
        return -1;
    }


    /**
     * 評価値をプラスする
     *
     * @param s 加える評価値
     */
    void addScore(double s) {
        score += s;
    }


    /**
     * 同じ盤面・動作の時に評価値と試行回数をプラスする
     *
     * @param br
     */
    public void add(BoardRecord br) {
        if (this.isActionEqual(br)) {
            score += br.score;
            tryCount += br.tryCount;
        }
    }


    public int getKey() {
        return Arrays.stream(board).map(n -> n == -1 ? 1 : n).sum();
    }


    static int getKey(String[] board, String color) {
        int[] bArray = Arrays.stream(board)
                .mapToInt(p -> Integer.parseInt(p) * Integer.parseInt(color))
                .toArray();
        return Arrays.stream(bArray).map(n -> n == -1 ? 1 : n).sum();
    }


    /**
     * 試行回数と評価値の合計値からの平均値を返す
     *
     * @return 評価値の平均値
     */
    double getAverageScore() {
        return score / tryCount;
    }


    @Override
    public String toString() {
        String[] boards = Arrays.stream(board).mapToObj(String::valueOf).toArray(String[]::new);
        return String.join(",", boards) + "/" + putX + "/" + putY + "/" + tryCount + "/" + score;
    }
}
