import OthelloAI.BoardRecord;
import com.iciql.Iciql;

import java.io.Serializable;
import java.io.SerializablePermission;

/**
 * Created by watariMac on 2017/07/07.
 */
@Iciql.IQTable(name = "board_record")
public class BordRecordModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Iciql.IQColumn(primaryKey = true, nullable = false, length = 256)
    public String board;

    @Iciql.IQColumn(nullable = false)
    public int put_x;

    @Iciql.IQColumn(nullable = false)
    public int put_y;

    @Iciql.IQColumn(nullable = false)
    public double score;

    @Iciql.IQColumn(nullable = false)
    public int try_count;

    BordRecordModel(){

    }
}
