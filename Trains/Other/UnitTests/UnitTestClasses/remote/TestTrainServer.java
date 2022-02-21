package remote;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import remote.TrainsServer;
import player.IPlayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Tests for a TrainsServer.
 */
public class TestTrainServer {
    public static final int PORT = 48755;

    TrainsServer signupServer;
    ExecutorService executor;

    Runnable doesNothing = () -> {
        new DoesNothingAfterConnectRemoteClient("127.0.0.1");
    };

    Runnable closesConnection = () -> {
        new ClosesConnectionAfterConnect("127.0.0.1");
    };

    private static Runnable createNormalClient(int timeoutMilli, String name) {
        return () -> {
            new GivesNameAfterTimeoutClient("127.0.0.1", timeoutMilli, name);
        };
    }

    private static void runClient(Runnable clientCreate) {
        Thread thread = new Thread(clientCreate);
        thread.start();
    }

    @BeforeEach
    public void initTrainsServer() {
        executor = Executors.newCachedThreadPool();
        signupServer = new TrainsServer(PORT, 5000, 1000);
    }

    @AfterEach
    public void shutdownTrainsServer() {
        signupServer.shutdown();
        executor.shutdown();
    }

    @Test
    public void testNormalClientsStopEarly() {
        Future<ArrayList<IPlayer>> players = executor.submit(signupServer::startSignup);

        for(int i = 0; i < 50; i++) {
            if((i + 1) % 10 == 0) {
                runClient(createNormalClient(1200, "Slow"));
            } else if((i + 1) % 5 == 0) {
                runClient(doesNothing);
            } else {
                runClient(createNormalClient(900, "Player"));
            }
        }
        
        List<IPlayer> playersList = null;
        try {
            playersList = players.get();
        } catch(InterruptedException interruptedException) {
            interruptedException.printStackTrace();
            fail();
        } catch(ExecutionException executionException) {
            executionException.printStackTrace();
            fail();
        }

        assertEquals(playersList.size(), 40);
    }

    @Test
    public void testNoConnections() {
        signupServer.shutdown();
        signupServer = new TrainsServer(PORT, 500, 250);

        Future<ArrayList<IPlayer>> players = executor.submit(signupServer::startSignup);
        List<IPlayer> playersList = null;
        try {
            playersList = players.get();
        } catch(InterruptedException interruptedException) {
            interruptedException.printStackTrace();
            fail();
        } catch(ExecutionException executionException) {
            executionException.printStackTrace();
            fail();
        }

        assertEquals(playersList, List.of());
    }

    @Test
    public void allImmediatelyClose() {
        Future<ArrayList<IPlayer>> players = executor.submit(signupServer::startSignup);

        for(int i = 0; i < 70; i++) {
            runClient(closesConnection);
        }

        List<IPlayer> playersList = null;
        try {
            playersList = players.get();
        } catch(InterruptedException interruptedException) {
            interruptedException.printStackTrace();
            fail();
        } catch(ExecutionException executionException) {
            executionException.printStackTrace();
            fail();
        }
        assertEquals(playersList, List.of());
    }

    @Test
    public void variousTimes() {
        signupServer.shutdown();
        signupServer = new TrainsServer(PORT, 2000, 300);

        for(int i = 0; i < 70; i++) {
            if((i + 1) % 10 == 0) {
                runClient(createNormalClient(500, "Slow"));
            } else if((i + 1) % 5 == 0) {
                runClient(doesNothing);
            } else {
                runClient(createNormalClient(100 * (i % 3), "Player"));
            }
        }

        Future<ArrayList<IPlayer>> players = executor.submit(signupServer::startSignup);
        List<IPlayer> playersList = null;
        try {
            playersList = players.get();
        } catch(InterruptedException interruptedException) {
            interruptedException.printStackTrace();
            fail();
        } catch(ExecutionException executionException) {
            executionException.printStackTrace();
            fail();
        }

        assertEquals(playersList.size(), 50);
    }
}
