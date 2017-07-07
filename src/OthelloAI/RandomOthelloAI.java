package OthelloAI;

import com.iciql.Db;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by watariMac on 2017/06/23.
 */
public class RandomOthelloAI extends AbstractOthelloAI {

    public RandomOthelloAI(Socket sc, String nick,Db othelloDb) throws IOException {
        super(sc, nick,othelloDb);
    }

    @Override
    void sendPut(){
        sendRandomPut();
    }

}
