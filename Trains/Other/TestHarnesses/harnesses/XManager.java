package harnesses;

import com.google.gson.*;
import game_state.RailCard;
import manager.ITournamentManager;
import manager.KnockOutTournamentManager;
import manager.TournamentResult;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import utils.InitializationUtils;
import utils.RailCardUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import json.CardStarJson;
import json.PlayerInstanceJson;
import json.PlayerInstanceJson.PlayerWithName;
import json.RankJson;
import json.OutcomesJson;
import json.MapJson;

public class XManager {
    public static final String BUY_NOW_PATH = "../Trains/target/classes/strategy/BuyNow.class";
    public static final String HOLD_10_PATH = "../Trains/target/classes/strategy/Hold10.class";
    public static final String CHEAT_PATH = "../Trains/target/classes/strategy/Cheat.class";

    /**
     * Performs the integration test consuming JSON input from stdin, and outputting the result to stdout.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        RunTest(new InputStreamReader(System.in), System.out);
    }

    /**
     * Performs the integration test consuming JSON input from the given reader, and outputting the result to the given
     * PrintStream.
     *
     * @param input  A stream of two JSON values representing a game map for a game of Trains, an array of PlayerInstance,
     *               and an array of 250 colors
     * @param output The output stream of either the JSON string "error: not enough destinations" or the
     *               outcome of running the game.
     */
    private static void RunTest(Reader input, PrintStream output) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try (input) {
            // Parse JSON
            JsonElement mapJson = parser.next();
            JsonElement playerInstances = parser.next();
            JsonElement colors = parser.next();

            // Construct objects from JSON
            ITrainMap map = MapJson.mapFromJson(mapJson);
            List<PlayerWithName> playersWithName = PlayerInstanceJson.playerInstancesFromJson(playerInstances,XManager::createStrategyFilePath, map);

            List<IPlayer> players = playersWithName.stream().map(playerWithName -> playerWithName.player).collect(Collectors.toList());
            Map<IPlayer, String> playerNameLookup = new HashMap<>();
            for (PlayerWithName p : playersWithName) {
                playerNameLookup.put(p.player, p.name);
            }

            List<RailCard> cards = CardStarJson.cardsFromJson(colors);

            Function<ITrainMap, List<Destination>> orderedDestinationProvider = (m) -> m.getAllPossibleDestinations()
                    .stream().map((up) -> new Destination(up)).sorted().collect(Collectors.toList());

            if (InitializationUtils.notEnoughDestinations(map.getAllPossibleDestinations().size(),
                    players.size(), 5, 2)) {
                output.println(new JsonPrimitive("error: not enough destinations"));
            } else {
                // construct a ref
                ITournamentManager manager = new KnockOutTournamentManager.ManagerBuilder(players).
                        destinationProvider(orderedDestinationProvider).deckProvider(() -> cards).build();

                // run game
                TournamentResult report = manager.runTournament();

                JsonElement result = OutcomesJson.tournamentOutcomeToJson(report, playerNameLookup);
                output.println(result);
            }

        } catch (JsonIOException | IOException ignored) {
        }
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

