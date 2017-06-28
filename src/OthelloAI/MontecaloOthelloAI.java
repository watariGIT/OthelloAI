package OthelloAI;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by watariMac on 2017/06/23.
 */
public class MontecaloOthelloAI extends AbstractOthelloAI {
    ArrayList<BoardRecord>[] trainingData = new ArrayList[57];

    MontecaloOthelloAI(Socket sc, String nick, ArrayList<BoardRecord>[] tr) throws IOException {
        super(sc, nick);
        for (int i = 0; i < trainingData.length; i++) {
            trainingData[i] = tr[i];
        }

    }


    @Override
    void sendPut() {
        int px = -1;
        int py = -1;
        ArrayList<BoardRecord> tdList = trainingData[BoardRecord.getKey(board,color) % trainingData.length];
        Stream<BoardRecord> tdStream = tdList.stream().filter(b -> b.isBoardEqual(board, color));
        tdList.stream().filter(b -> b.isBoardEqual(board, color))
                .forEach(b -> System.out.println(b.putX + "," + b.putY + ": " + b.getAverageScore()));

        Optional<BoardRecord> obr = tdStream.max((br1, br2) -> (int) (br1.getAverageScore() * 10000 - br2.getAverageScore() * 10000));
        if (obr.isPresent()) {
            int rotationCount = obr.get().getRotationCount(board, color);
            px = obr.get().putX;
            py = obr.get().putY;
            for (int i = 0; i < rotationCount; i++) {
                px = py;
                py = 7 - px;
            }
            System.out.println("PUT" + px + "," + py);
        } else {
            sendRandomPut();
        }

        recordList.add(new BoardRecord(board, color, px, py));
        pw.println("PUT " + px + " " + py);
        pw.flush();
    }

}
