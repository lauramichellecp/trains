package remote;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import remote.ProxyAdmin;

public class GivesNameAfterTimeoutClient extends DoesNothingAfterConnectRemoteClient {
    public GivesNameAfterTimeoutClient(String domain, int timeout, String name) {
        super(domain);
        try {
            Thread.sleep(timeout);
            if(socket != null && !socket.isClosed()) {
                OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
                writer.write("\"" + name + "\"");
                writer.flush();
            }
        } catch(SocketException socketException) {
            //Some tests involve players sockets being closed
        } catch(InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        } catch(IOException io) {
            io.printStackTrace();
        }
    }
}
