package harnesses;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonStreamParser;

import map.ITrainMap;
import player.IPlayer;
import player.RefereePlayer;
import remote.ProxyAdmin;
import json.MapJson;
import json.PlayerInstanceJson.PlayerWithName;
import json.PlayerInstanceJson;

public class XClient {
    public static final String BUY_NOW_PATH = "../Trains/target/classes/strategy/BuyNow.class";
    public static final String HOLD_10_PATH = "../Trains/target/classes/strategy/Hold10.class";
    public static final String CHEAT_PATH = "../Trains/target/classes/strategy/Cheat.class";

    static String domain;
    static int port;
    static ExecutorService executor;

    /**
     * Performs the integration test, given a port and an optional domain.
     */
    public static void main(String[] args) {
        port = Integer.parseInt(args[0]);
        if(args.length == 2) {
            domain = args[1];
        } else {
            domain = "127.0.0.1";
        }
        executor = Executors.newCachedThreadPool();
        RunTest(new InputStreamReader(System.in), System.out);
    }

    /**
     * Creates a single client from the given domain port and player.
     * @param domain the String domain to connect
     * @param port the port number
     * @param playerWithName a PlayerWithName composed of an IPlayer with a name (String)
     * @return the Runnable to be executed.
     */
    private static Runnable createNormalClient(String domain, int port, PlayerWithName playerWithName) {
        return () -> {
            new ProxyAdmin(domain, port, playerWithName.name, playerWithName.player).listen();
        };
    }


    /**
     * Runs client threads created from the given JSON with PlayerInstance(s) and a map of Trains.
     * @param input the input to run the clients
     * @param output n/a
     */
    public static void RunTest(Reader input, PrintStream output) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try {
            JsonElement mapJson = parser.next();
            JsonElement playerInstances = parser.next();
            JsonElement colors = parser.next();

            // Construct objects from JSON
            ITrainMap map = MapJson.mapFromJson(mapJson);
            List<PlayerWithName> playersWithName = deserializePlayers(playerInstances, map);

            List<Future> clientThreads = createClientThreads(playersWithName);

            boolean clientsStillRunning;
            do {
                clientsStillRunning = false;
                for(Future client : clientThreads) {
                    if(!client.isDone()) {
                        clientsStillRunning = true;
                        break;
                    }
                }
            } while (clientsStillRunning);
            executor.shutdownNow();
        } catch (JsonIOException ignored) {
        }
    }

    /**
     * Deserializes the PlayerInstance(s) given, and creates a List of PlayerWithName
     * @param playerInstances the Json representing PlayerInstance
     * @param map an ITrainMap
     * @return new List of players with names
     */
    private static List<PlayerWithName> deserializePlayers(JsonElement playerInstances, ITrainMap map) {
        return PlayerInstanceJson.playerInstancesFromJson(playerInstances, XClient::createStrategyFilePath, map);
    }

    /**
     * Creates the client threads from a list of players
     * @param playersWithName List of PlayerWithName, which represents the players
     * @return List of client threads to run
     */
    private static List<Future> createClientThreads(List<PlayerWithName> playersWithName) {
        Map<IPlayer, String> playerNameLookup = new HashMap<>();
        for (PlayerWithName p : playersWithName) {
            playerNameLookup.put(p.player, p.name);
        }

        List<Future> clientThreads = new ArrayList<Future>();
        playersWithName.forEach((p) -> {
            clientThreads.add(executor.submit(createNormalClient(domain, port, p)));
            try {
                Thread.sleep(50);
            } catch (Exception e) {
            }
        });
        return clientThreads;
    }

    /**
     * Creates a file path from a given strategy.
     *
     * @param strategy the strategy to convert into a file path
     * @return a String representing the path to the specified strategy
     */
    private static String createStrategyFilePath(String strategy) {
        switch (strategy) {
            case "Hold-10":
                return HOLD_10_PATH;
            case "Buy-Now":
                return BUY_NOW_PATH;
            case "Cheat":
                return CHEAT_PATH;
            default:
                throw new IllegalArgumentException("wrong strategy");
        }
    }
}
