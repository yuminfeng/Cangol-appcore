package mobi.cangol.mobile.socket;

import android.test.AndroidTestCase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Created by weixuewu on 16/6/11.
 */
public class SocketClientTest extends AndroidTestCase {

    public void testSend() {
        final SocketClient socketClient=new SocketClient();
        socketClient.connect("SocketClientTest","127.0.0.1", 8080, true, 10 * 1000, new SocketHandler() {

            @Override
            public boolean handleSocketWrite(DataOutputStream outputStream) throws IOException {
                return false;
            }

            @Override
            public boolean handleSocketRead(DataInputStream inputStream) throws IOException, ClassNotFoundException {
                return false;
            }

            @Override
            protected Object getSend() {
                return null;
            }

            @Override
            protected void onFail(Object obj, Exception e) {

            }
        });
        socketClient.connect("SocketClientTest","127.0.0.1", 8080, true, 10 * 1000, new SocketSerializableHandler() {

            @Override
            protected Object getSend() {
                return null;
            } @Override
            public void onReceive(Serializable msg) {

            }
            @Override
            protected void onFail(Object obj, Exception e) {

            }
        });
    }
}