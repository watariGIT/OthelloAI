package OthelloAI;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by watariMac on 2017/06/23.
 */
public class RandomOthelloAI extends AbstractOthelloAI {

    public RandomOthelloAI(Socket sc, String nick) throws IOException {
        super(sc, nick);
    }

    @Override
    void sendPut(){
        sendRandomPut();
    }
}
