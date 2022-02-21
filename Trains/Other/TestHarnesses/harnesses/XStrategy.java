package harnesses;

import json.MapJson;
import json.PlayerStateJson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;
import game_state.IPlayerGameState;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import strategy.Action;
import strategy.Hold10;
import strategy.TurnAction;
import utils.OrderedPair;
import json.TurnActionJson;

public class XStrategy {
    /**
     * Performs the integration test consuming JSON input from stdin, and outputting the result to stdout.
     *
     * @param args ignored
     */
    public  static void main(String[] args) {
        RunTest(new InputStreamReader(System.in), System.out);
    }

    /**
     * Performs the integration test consuming JSON input from the given reader, and outputting the result to the given
     * PrintStream.
     *
     * @param input A stream of two JSON values representing a game map for a game of Trains, and a state
     *              of the game of a player.
     * @param output The output stream where a JSON Element representing one of two Actions: the JSON String
     *               "more cards" or a JSON Element representing an Acquired will be written.
     */
    private static void RunTest(Reader input, PrintStream output) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try (input) {
            // Parse JSON
            JsonElement mapJson = parser.next();
            JsonElement playerStateJson = parser.next();

            // Construct objects from JSON
            ITrainMap map = MapJson.mapFromJson(mapJson);
            IPlayerGameState playerGameState = PlayerStateJson.playerStateFromJson(playerStateJson);

            // Calculate and output result
            TurnAction result = new Hold10().takeTurn(playerGameState, map, null);
            output.println(TurnActionJson.turnActionToJSON(result).toString());
        } catch (JsonIOException | IOException ignored) {
        }
    }
}
