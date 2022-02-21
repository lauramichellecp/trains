package remote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.google.gson.JsonParser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import game_state.IPlayerGameState;
import game_state.RailCard;
import map.ITrainMap;
import map.Destination;
import player.IPlayer;
import player.RefereePlayer;
import player.ProxyPlayer;
import referee.IPlayerHand;
import referee.TrainsPlayerHand;
import strategy.Hold10;
import strategy.TurnAction;
import json.MapJson;
import manager.KnockOutTournamentManager;

public class TestProxyAdmin {
    private static class AdminTestingPlayer implements IPlayer {
        IPlayer nonProxyPlayer;
        IPlayer proxyPlayer;

        public AdminTestingPlayer(IPlayer nonProxyPlayer, IPlayer proxyPlayer) {
            this.nonProxyPlayer = nonProxyPlayer;
            this.proxyPlayer = proxyPlayer;
        }

        @Override
        public ITrainMap tournamentStart() {
            ITrainMap map = proxyPlayer.tournamentStart();
            assertEquals(nonProxyPlayer.tournamentStart().getAllPossibleDestinations(),
             proxyPlayer.tournamentStart().getAllPossibleDestinations());
            assertEquals(nonProxyPlayer.tournamentStart().getRailConnections(),
             proxyPlayer.tournamentStart().getRailConnections());
            return map;
        }
        
        @Override
        public void tournamentResult(boolean winner) {
        }

        @Override
        public void setup(ITrainMap map, int numRails, List<RailCard> cards) {
            
        }

        @Override
        public Set<Destination> chooseDestinations(Set<Destination> options) {
            Set<Destination> destinations = nonProxyPlayer.chooseDestinations(options);
            assertEquals(nonProxyPlayer.chooseDestinations(options), proxyPlayer.tournamentStart());            
            return destinations;
        }
        @Override
        public TurnAction takeTurn(IPlayerGameState playerGameState) {
            TurnAction action = nonProxyPlayer.takeTurn(playerGameState);
            assertEquals(nonProxyPlayer.takeTurn(playerGameState), proxyPlayer.takeTurn(playerGameState));            
            return action;
        }
        @Override
        public void receiveCards(List<RailCard> drawnCards) {            
        }

        @Override
        public void winNotification(boolean thisPlayerWon) {
        }
    }
    public static final String BUY_NOW_PATH = "target/classes/strategy/BuyNow.class";
    public static final String HOLD_10_PATH = "target/classes/strategy/Hold10.class";
    public static final String CHEAT_PATH = "target/classes/strategy/Cheat.class";

    IPlayer hold10FromStrategy;
    IPlayer buyNowFromStrategy;
    IPlayer hold10FromFile;
    IPlayer buyNowFromFile;

    public static final int PORT = 48755;
    private static final String json = "{\"cities\":[[\"19\",[62,565]],[\"18\",[69,479]],[\"17\",[29,460]],[\"16\",[149,423]],[\"15\",[122,412]],[\"14\",[9,610]],[\"13\",[130,655]],[\"12\",[69,467]],[\"11\",[5,469]],[\"10\",[107,636]],[\"9\",[199,704]],[\"8\",[38,689]],[\"7\",[189,727]],[\"6\",[188,221]],[\"5\",[42,93]],[\"4\",[144,538]],[\"3\",[111,229]],[\"2\",[51,486]],[\"1\",[168,607]],[\"0\",[61,202]]],\"connections\":{\"0\":{\"13\":{\"green\":3},\"19\":{\"white\":3},\"3\":{\"white\":3},\"7\":{\"red\":3}},\"1\":{\"10\":{\"blue\":3},\"11\":{\"white\":3},\"12\":{\"green\":3},\"15\":{\"green\":3},\"3\":{\"blue\":3}},\"10\":{\"13\":{\"green\":3},\"2\":{\"blue\":3},\"7\":{\"green\":3}},\"11\":{\"13\":{\"green\":3},\"8\":{\"red\":3}},\"12\":{\"16\":{\"green\":3},\"18\":{\"green\":3},\"19\":{\"red\":3},\"2\":{\"white\":3},\"6\":{\"green\":3,\"white\":3}},\"13\":{\"4\":{\"green\":3}},\"14\":{},\"15\":{\"7\":{\"blue\":3},\"9\":{\"white\":3}},\"16\":{\"17\":{\"green\":3},\"2\":{\"green\":3}},\"17\":{\"3\":{\"blue\":3},\"5\":{\"green\":3,\"red\":3},\"7\":{\"blue\":3,\"red\":3},\"9\":{\"blue\":3}},\"18\":{\"6\":{\"white\":3},\"9\":{\"white\":3}},\"19\":{\"6\":{\"blue\":3},\"8\":{\"red\":3}},\"2\":{\"5\":{\"green\":3}},\"3\":{\"9\":{\"blue\":3}},\"4\":{},\"5\":{\"6\":{\"white\":3},\"7\":{\"green\":3},\"8\":{\"blue\":3}},\"6\":{},\"7\":{},\"8\":{},\"9\":{}},\"height\":800,\"width\":200}";
    private static final ITrainMap map = MapJson.mapFromJson(JsonParser.parseString(json).getAsJsonObject());
    private static final Set<Destination> destinations = map.getAllPossibleDestinations().stream()
        .map((pair) -> new Destination(pair)).collect(Collectors.toSet());
    // Function<ITrainMap, List<Destination>> orderedDestinationProvider = (m) -> m.getAllPossibleDestinations()
    //     .stream().map((up) -> new Destination(up)).sorted().collect(Collectors.toList());

    TrainsServer signupServer;
    ExecutorService executor;
    ArrayList<IPlayer> connections;
    ArrayList<IPlayer> nonProxyPlayers;
    private Runnable createNormalClient(int timeoutMilli, String name, ITrainMap map) {
        return () -> {
            IPlayer player = new RefereePlayer(new Hold10(), map);
            synchronized (nonProxyPlayers) {
                nonProxyPlayers.add(player);
            }
            synchronized(connections) {
                connections.add(player);
            }
            ProxyAdmin admin = new ProxyAdmin(PORT, name, player);
            admin.listen();
        };
    }

    private static void runClient(Runnable clientCreate) {
        Thread thread = new Thread(clientCreate);
        thread.start();
    }

    public List<IPlayer> signupPlayers(int num) {
        Future<ArrayList<IPlayer>> players = executor.submit(signupServer::startSignup);
        for(int i = 0; i < num; i++) {
            runClient(createNormalClient(0, "player" + (char)('A' + i), map));
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

        List<IPlayer> testingPlayers = new ArrayList<IPlayer>();
        synchronized (nonProxyPlayers) {
            for(int i = 0; i < num; i++) {
                testingPlayers.add(new AdminTestingPlayer(nonProxyPlayers.get(i), playersList.get(i)));
            }
        }

        return testingPlayers;
    }

    @BeforeEach
    public void initTrainsServer() {
        executor = Executors.newCachedThreadPool();
        signupServer = new TrainsServer(PORT, 1000, 500);
        connections = new ArrayList<IPlayer>();
        nonProxyPlayers = new ArrayList<IPlayer>();
    }

    @AfterEach
    public void shutdownTrainsServer() {
        signupServer.shutdown();
        executor.shutdown();
    }

    @Test
    public void TestFullTournament() {
        List<IPlayer> playersList = signupPlayers(9);
        KnockOutTournamentManager manager = new KnockOutTournamentManager.ManagerBuilder(playersList).build();
        manager.runTournament();
    }
}
