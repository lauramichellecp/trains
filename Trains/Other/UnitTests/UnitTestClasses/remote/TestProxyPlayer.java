package remote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import game_state.IOpponentInfo;
import game_state.OpponentInfo;
import manager.ITournamentManager;
import manager.KnockOutTournamentManager;
import manager.TournamentResult;
import map.ITrainMap;
import map.Destination;

import org.junit.jupiter.api.AfterEach;

import remote.TrainsServer;
import player.IPlayer;
import player.MockRemotePlayer;
import referee.IReferee;
import referee.TrainsReferee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import com.google.gson.JsonParser;

import json.MapJson;

/**
 * Tests for ProxyPlayer(s)
 */
public class TestProxyPlayer {
    public static final int PORT = 48755;
    private static final String json = "{\"cities\":[[\"19\",[62,565]],[\"18\",[69,479]],[\"17\",[29,460]],[\"16\",[149,423]],[\"15\",[122,412]],[\"14\",[9,610]],[\"13\",[130,655]],[\"12\",[69,467]],[\"11\",[5,469]],[\"10\",[107,636]],[\"9\",[199,704]],[\"8\",[38,689]],[\"7\",[189,727]],[\"6\",[188,221]],[\"5\",[42,93]],[\"4\",[144,538]],[\"3\",[111,229]],[\"2\",[51,486]],[\"1\",[168,607]],[\"0\",[61,202]]],\"connections\":{\"0\":{\"13\":{\"green\":3},\"19\":{\"white\":3},\"3\":{\"white\":3},\"7\":{\"red\":3}},\"1\":{\"10\":{\"blue\":3},\"11\":{\"white\":3},\"12\":{\"green\":3},\"15\":{\"green\":3},\"3\":{\"blue\":3}},\"10\":{\"13\":{\"green\":3},\"2\":{\"blue\":3},\"7\":{\"green\":3}},\"11\":{\"13\":{\"green\":3},\"8\":{\"red\":3}},\"12\":{\"16\":{\"green\":3},\"18\":{\"green\":3},\"19\":{\"red\":3},\"2\":{\"white\":3},\"6\":{\"green\":3,\"white\":3}},\"13\":{\"4\":{\"green\":3}},\"14\":{},\"15\":{\"7\":{\"blue\":3},\"9\":{\"white\":3}},\"16\":{\"17\":{\"green\":3},\"2\":{\"green\":3}},\"17\":{\"3\":{\"blue\":3},\"5\":{\"green\":3,\"red\":3},\"7\":{\"blue\":3,\"red\":3},\"9\":{\"blue\":3}},\"18\":{\"6\":{\"white\":3},\"9\":{\"white\":3}},\"19\":{\"6\":{\"blue\":3},\"8\":{\"red\":3}},\"2\":{\"5\":{\"green\":3}},\"3\":{\"9\":{\"blue\":3}},\"4\":{},\"5\":{\"6\":{\"white\":3},\"7\":{\"green\":3},\"8\":{\"blue\":3}},\"6\":{},\"7\":{},\"8\":{},\"9\":{}},\"height\":800,\"width\":200}";
    private static final ITrainMap map = MapJson.mapFromJson(JsonParser.parseString(json).getAsJsonObject());
    private static final Set<Destination> destinations = map.getAllPossibleDestinations().stream()
        .map((pair) -> new Destination(pair)).collect(Collectors.toSet());
    // Function<ITrainMap, List<Destination>> orderedDestinationProvider = (m) -> m.getAllPossibleDestinations()
    //     .stream().map((up) -> new Destination(up)).sorted().collect(Collectors.toList());
        
    TrainsServer signupServer;
    ExecutorService executor;
    ArrayList<MockRemotePlayer> connections;
    ArrayList<ProxyAdmin> admins;
    private Runnable createNormalClient(int timeoutMilli, String name, ITrainMap map, String lastOp) {
        return () -> {
            MockRemotePlayer player = new MockRemotePlayer(map);
            synchronized(connections) {
                connections.add(player);
            }
            ProxyAdmin admin = new ProxyAdmin(PORT, name, player);
            admins.add(admin);
            do {
                admin.handleOp();
            } while (!player.lastCalledFunc.equals(lastOp));

        };
    }

    private static void runClient(Runnable clientCreate) {
        Thread thread = new Thread(clientCreate);
        thread.start();
    }

    @BeforeEach
    public void initTrainsServer() {
        executor = Executors.newCachedThreadPool();
        signupServer = new TrainsServer(PORT, 1000, 500);
        connections = new ArrayList<MockRemotePlayer>();
        admins = new ArrayList<ProxyAdmin>();
    }

    @AfterEach
    public void shutdownTrainsServer() {
        signupServer.shutdown();
        executor.shutdown();
    }

    public List<IPlayer> signupPlayers(int num, String lastOp) {
        Future<ArrayList<IPlayer>> players = executor.submit(signupServer::startSignup);
        for(int i = 0; i < num; i++) {
            runClient(createNormalClient(0, "player" + (char)('A' + i), map, lastOp));
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

        assertEquals(playersList.size(), num);
        synchronized(connections) {
            assertEquals(connections.size(), num);
        }
        return playersList;
    }

    @Test
    public void testStartCalls() {
        List<IPlayer> playersList = signupPlayers(9,"tournamentStart");
        KnockOutTournamentManager manager = new KnockOutTournamentManager.ManagerBuilder(playersList).build();
        manager.initializeTournament();

        for(MockRemotePlayer mock : connections) {
            assertEquals(mock.lastCalledFunc, "tournamentStart");
            assertEquals(mock.numberOperations, 1);
        }
    }
    
    @Test
    public void testSetupCalls() {
        List<IPlayer> playersList = signupPlayers(8, "setup");
        TrainsReferee ref = new TrainsReferee.RefereeBuilder(map, new ArrayList<>(playersList)).build();

        ref.playGame();
        for(MockRemotePlayer mock : connections) {
            assertEquals(mock.lastCalledFunc, "setup");
            assertEquals(mock.numberOperations, 1);
            assertEquals(mock.map.getCities(), map.getCities());
            assertEquals(mock.map.getRailConnections(), map.getRailConnections());
            assertEquals(mock.rails, 45);
        }
    }

    @Test
    public void testPickCalls() {
        List<IPlayer> playersList = signupPlayers(8, "chooseDestinations");
        TrainsReferee ref = new TrainsReferee.RefereeBuilder(map, new ArrayList<>(playersList)).build();

        ref.playGame();
        for(MockRemotePlayer mock : connections) {
            assertEquals(mock.lastCalledFunc, "chooseDestinations");
            assertEquals(mock.numberOperations, 2);
            assertEquals(mock.destinations.size(), 5);
            assertTrue(destinations.containsAll(mock.destinations));
        }
    }
    
    @Test
    public void testTakeTurnCalls() {
        List<IPlayer> playersList = signupPlayers(8, "takeTurn");
        TrainsReferee ref = new TrainsReferee.RefereeBuilder(map, new ArrayList<>(playersList)).build();

        ref.playGame();
        for(MockRemotePlayer mock : connections) {
            assertEquals(mock.lastCalledFunc, "takeTurn");
            assertEquals(mock.numberOperations, 3);
            assertEquals(mock.gamestate.getNumRails(), 45);
            int numCards = mock.gamestate.getCardsInHand().values().stream().mapToInt(n -> n).sum();
            assertEquals(numCards, 4);
            assertTrue(destinations.containsAll(mock.gamestate.getDestinations()));
            assertEquals(mock.gamestate.getOwnedConnections(), Set.of());
            for(IOpponentInfo info : mock.gamestate.getOpponentInfo()) {
                assertEquals(info.getOwnedConnections(), Set.of());
            }
        }
    }

    @Test
    public void testReceiveCards() {
        List<IPlayer> playersList = signupPlayers(8, "receiveCards");
        TrainsReferee ref = new TrainsReferee.RefereeBuilder(map, new ArrayList<>(playersList)).build();

        ref.playGame();
        for(MockRemotePlayer mock : connections) {
            assertEquals(mock.lastCalledFunc, "receiveCards");
            assertEquals(mock.numberOperations, 4);
            assertEquals(mock.cards.size(), 2);
        }
    }

    @Test
    public void testWinNotification() {
        List<IPlayer> playersList = signupPlayers(8, "winNotification");
        TrainsReferee ref = new TrainsReferee.RefereeBuilder(map, new ArrayList<>(playersList)).build();

        ref.playGame();
        for(MockRemotePlayer mock : connections) {
            assertEquals(mock.lastCalledFunc, "winNotification");
            assertTrue(mock.flag);
            assertTrue(mock.numberOperations == 31 || mock.numberOperations == 33);
        }
    }

    @Test
    public void testTournamentResult() {
        List<IPlayer> playersList = signupPlayers(16, "tournamentResult");
        KnockOutTournamentManager manager = new KnockOutTournamentManager.ManagerBuilder(playersList).build();
        manager.runTournament();

        for(MockRemotePlayer mock : connections) {
            assertEquals(mock.lastCalledFunc, "tournamentResult");
            assertTrue(mock.flag);
            assertTrue(mock.numberOperations == 64 || mock.numberOperations == 68);
        }
    }
}
