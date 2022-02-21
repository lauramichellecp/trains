package harnesses;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import game_state.RailCard;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import utils.InitializationUtils;

import json.CardStarJson;
import json.OutcomesJson;

import manager.ITournamentManager;
import manager.TournamentResult;
import manager.KnockOutTournamentManager;

import remote.TrainsServer;


public class XServer {

    private static final Function<ITrainMap, List<Destination>> orderedDestinationProvider = (m) -> m.getAllPossibleDestinations()
            .stream().map(Destination::new).sorted().collect(Collectors.toList());

    static int port;

    /**
     * Performs the integration test given a port.
     */
    public static void main(String[] args) {
        port = Integer.parseInt(args[0]);
        RunTest(new InputStreamReader(System.in), System.out);
    }

    /**
     * Creates a server to signup players, run a tournament and report the results.
     * @param input the JsonInput to run the server.
     * @param output the stream to write the result of the tournament to
     */
    public static void RunTest(Reader input, PrintStream output) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try {
            // Parse JSON
            JsonElement mapJson = parser.next();
            JsonElement playerInstances = parser.next();
            JsonElement colors = parser.next();

            List<RailCard> cards = CardStarJson.cardsFromJson(colors);

            TrainsServer server = new TrainsServer(port);
            List<IPlayer> players = server.startSignup();
            Map<IPlayer, String> playerNameLookup = server.getLookupTable();

            if(players.size() == 0) {
                printEmptyResult(output);
                return;
            }

            ITournamentManager manager = new KnockOutTournamentManager.ManagerBuilder(players).
                    destinationProvider(orderedDestinationProvider).deckProvider(() -> cards).build();

            // run game
            try {
                TournamentResult report = manager.runTournament();
                JsonElement result = OutcomesJson.tournamentOutcomeToJson(report, playerNameLookup);
                output.println(result);    
            } catch (RuntimeException notEnoughDestinations) {
                output.println(new JsonPrimitive("error: not enough destinations"));
            }
        } catch (JsonIOException ignored) {
        }
    }

    /**
     * If there are no players in the tournament after signup, outputs an empty JsonArray
     * @param output the stream to write the result to.
     */
    private static void printEmptyResult(PrintStream output) {
        JsonArray empty = new JsonArray();
        empty.add(new JsonArray());
        empty.add(new JsonArray());
        output.println(empty);
    }
}