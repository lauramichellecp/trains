package harnesses;

import com.google.gson.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import map.*;

import json.MapJson;
import json.ConnectionJson;
import utils.UnorderedPair;

/**
 * Performs an integration test on the map for a game of trains by consuming a specification for a Trains game map, and
 * two cities on the map, then outputting whether the two cities are connected by any path.
 *
 * The specifications for the JSON input format can be found at:
 *      https://www.ccs.neu.edu/home/matthias/4500-f21/3.html
 *
 * Example input:
 *      "Texas"
 *      "Boston"
 *      {
 *          "width" : 800,
 *          "height": 800,
 *          "cities": [["Seattle", [0, 0]], ["Boston", [800, 50]], ["Texas", [500, 800]]],
 *          "connections": {"Boston": {"Seattle": {"red": 3},
 *                                      "Texas": {"green": 5}},
 *                          "Seattle": {"Texas": {"blue": 4}}}
 *      }
 */
public class XMap {
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
     * @param input A stream of three JSON values representing two cities on the map, and the game map for a game of
     *              Trains
     * @param output The output stream where a JSON boolean representing whether or not the two cities are connected
     *               will be written.
     */
    private static void RunTest(Reader input, PrintStream output) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try (input) {
            JsonElement startCity = parser.next();
            JsonElement endCity = parser.next();
            JsonElement mapSpecification = parser.next();
            String startName = startCity.getAsString();
            String endName = endCity.getAsString();
            UnorderedPair<ICity> destination =
                new UnorderedPair<>(new City(startName, 0, 0), new City(endName, 0, 0));
            ITrainMap map = MapJson.mapFromJson(mapSpecification);
            boolean result = map.getAllPossibleDestinations().contains(destination);
            output.println(result);
        } catch (JsonIOException | IOException ignored) {
        }
    }
}
