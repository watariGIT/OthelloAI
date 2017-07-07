package OthelloAI;

import OthelloAI.BoardRecord;
import com.iciql.Iciql;

import java.io.Serializable;
import java.io.SerializablePermission;

/**
 * Created by watariMac on 2017/07/07.
 */
@Iciql.IQTable(name = "board_record")
public class BoardRecordModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Iciql.IQColumn(primaryKey = true, nullable = false, length = 256)
    public String board;

    @Iciql.IQColumn(primaryKey = true, nullable = false)
    public int put_x;

    @Iciql.IQColumn(primaryKey = true, nullable = false)
    public int put_y;

    @Iciql.IQColumn(nullable = false)
    public double score;

    @Iciql.IQColumn(nullable = false)
    public int try_count;

    public BoardRecordModel() {

    }

    public BoardRecordModel(String board, int put_x, int put_y, double score, int trycount) {
        this.board = board;
        this.put_x = put_x;
        this.put_y = put_y;
        this.score = score;
        this.try_count = trycount;
    }

    @Override
    public String toString() {
        return board + "/x " + put_x + "/y " + put_y + "/s " + score + "/t " + try_count;
    }
}
