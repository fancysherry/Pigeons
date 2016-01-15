package unique.fancysherry.pigeons.io;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by fancysherry on 16-1-15.
 */
public class SocketIOUtil {
    public static Socket mSocket;

    public static void connect() {
        try {
            if (mSocket == null)
                mSocket = IO.socket(Constants.BASE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.connect();
    }

    public static void disconnect() {
        if (mSocket != null)
            mSocket.disconnect();
    }

    public static Socket getSocket() {
        try {
            if (mSocket == null)
                mSocket = IO.socket(Constants.BASE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return mSocket;
    }
}
