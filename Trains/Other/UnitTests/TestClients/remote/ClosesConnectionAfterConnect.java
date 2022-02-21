package remote;

import java.io.IOException;

public class ClosesConnectionAfterConnect extends DoesNothingAfterConnectRemoteClient {

    public ClosesConnectionAfterConnect(String domain) {
        super(domain);
        try {
            socket.close();
        } catch(IOException io) {
            io.printStackTrace();
        }
    }
}
