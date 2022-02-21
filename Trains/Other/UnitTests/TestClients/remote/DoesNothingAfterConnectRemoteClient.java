package remote;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class DoesNothingAfterConnectRemoteClient {
    static final int PORT = 48755;
    Socket socket;
    public DoesNothingAfterConnectRemoteClient(String domain) {
        try {
            socket = new Socket(domain, PORT);
        } catch (ConnectException connect) {
            //Some tests involve clients trying to connect to closed server
            connect.printStackTrace();
        } catch(UnknownHostException unknownHostException) {
            unknownHostException.printStackTrace();
        } catch(IOException io) {
            io.printStackTrace();
        }
    }
}
