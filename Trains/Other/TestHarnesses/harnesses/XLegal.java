package harnesses;

import json.MapJson;
import json.PlayerStateJson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import game_state.IPlayerGameState;
import java.util.Set;
import referee.PlayerData;
import game_state.PlayerGameState;
import game_state.RailCard;
import referee.TrainsPlayerHand;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import map.City;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.RailColor;
import map.RailConnection;
import referee.ActionChecker;
import utils.RailCardUtils;
import utils.UnorderedPair;

public class XLegal {

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
   * @param input A stream of two JSON values representing a game map for a game of Trains, a state
   *              of the game of a player, and a desired connection.
   * @param output The output stream where a JSON boolean representing whether the requested action
   *               is legal according to the rules with respect to the given map and state.
   */
  private static void RunTest(Reader input, PrintStream output) {
    JsonStreamParser parser = new JsonStreamParser(input);
    try (input) {
      // Parse JSON
      JsonElement mapJson = parser.next();
      JsonElement playerStateJson = parser.next();
      JsonElement desiredConnectionJson = parser.next();

      // Construct objects from JSON
      ITrainMap map = MapJson.mapFromJson(mapJson);
      IRailConnection desiredConnection = PlayerStateJson.acquiredConnectionFromJson(desiredConnectionJson);
      IPlayerGameState playerGameState = PlayerStateJson.playerStateFromJson(playerStateJson);

      // Calculate and output result
      boolean result =
          new ActionChecker().canAcquireConnection(playerGameState, map, desiredConnection);
      output.println(result);
    } catch (JsonIOException | IOException ignored) {
    }
  }
}
