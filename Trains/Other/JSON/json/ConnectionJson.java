package json;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.RailColor;
import map.RailConnection;
import map.TrainMap;
import utils.UnorderedPair;
import utils.OrderedPair;
import utils.ComparatorUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/** Utility class for serializing and deserializing Json related to IRailConnection. */
public class ConnectionJson {

    /**
     * Gets a pair of cities representing the lexicographically ordered cities in a given IRailConnection.
     * @param connection the IRailConnection to get the cities from
     * @return an OrderedPair of ICity from an IRailConnection
     */
    public static OrderedPair<ICity> getOrderedCity(IRailConnection connection) {
        OrderedPair<ICity> ret = null;
        if(ComparatorUtils.lexicographicCompareCity(connection.getCities().first, connection.getCities().second) < 0) {
            ret = connection.getCities().toOrdered();
        } else {
            ret = new OrderedPair<ICity>(connection.getCities().first, connection.getCities().second);
        }
        return ret;
    }

    /**
     * Parses the JSON 'Segment' object to calculate the specified direct connections between two cities.
     * @param segment the JSON specifying the connections between two cities.
     * @param endPoints the two cities being connected.
     * @return a list of IRailConnection specified for the two cities.
     */
    public static List<IRailConnection> parseConnections(JsonObject segment,
        UnorderedPair<ICity> endPoints) {
        List<IRailConnection> railConnections = new ArrayList<>();

        // For every connection, parse color and length info, and add IRailConnection to accumulator
        for (Map.Entry<String, JsonElement> connection : segment.entrySet()) {
            String color = connection.getKey();
            int length = connection.getValue().getAsInt();
            RailColor railColor = RailColor.valueOf(color.toUpperCase());
            railConnections.add(new RailConnection(endPoints, length, railColor));
        }
        return railConnections;
    }

    /**
     * Creates a list of IRailConnection objects for all of the connections from one city specified in the JSON input.
     *
     * The JSON input will be in the form:
     *      {"Seattle": {"red": 3},
     *       "Texas": {"green": 5}}
     * Representing that the given city is connected to Seattle by a red connection with length 3, and to Texas by a
     * green connection of length 5.
     *
     * @param targets The JSON specifying connections from the given city to other cities
     * @param cities A map of city names to ICity objects for all cities in the map
     * @param startCity The city that is the source of all connections specified in targets
     * @return A list of IRailConnection objects for all of the connections specified in the JSON input
     */
    public static List<IRailConnection> parseConnectionsFromCity(
        JsonObject targets, Map<String, ICity> cities, ICity startCity) {

        List<IRailConnection> railConnections = new ArrayList<>();

        // For every city the startCity is connected to, add all associated connections to
        // accumulator railConnections
        for (Map.Entry<String, JsonElement> target : targets.entrySet()) {
            ICity endCity = cities.get(target.getKey());
            UnorderedPair<ICity> endPoints = new UnorderedPair<>(startCity, endCity);
            JsonObject segment = target.getValue().getAsJsonObject();
            railConnections.addAll(parseConnections(segment, endPoints));
            }

        return railConnections;
    }

    /**
     * Creates a list of IRailConnection objects for all of the connections specified in the JSON input.
     *
     * The JSON input will be in the form:
     *      {"Boston": {"Seattle": {"red": 3},
     *                  "Texas": {"green": 5}},
     *       "Seattle": {"Texas": {"blue": 4}}}
     *
     * @param connectionsSpecification The JSON specifying the connections
     * @param cities A map of city names to ICity objects for all cities in the map
     * @return A list of IRailConnection objects for all of the connections specified in the JSON input
     */
    public static List<IRailConnection> getRailConnectionsFromJson(
        JsonObject connectionsSpecification, Map<String, ICity> cities) {

        List<IRailConnection> railConnections = new ArrayList<>();
        for (Map.Entry<String, JsonElement> connectionEntry : connectionsSpecification.entrySet()) {
            ICity startCity = cities.get(connectionEntry.getKey());
            JsonObject targets = connectionEntry.getValue().getAsJsonObject();
            railConnections.addAll(parseConnectionsFromCity(targets, cities, startCity));
        }

        return railConnections;
    }

    /**
     * Creates a JsonObject representing a given connection
     * @param connection the IRailConnection to be converted to Json
     * @return a new JsonObject
     */
    public static JsonObject connectionToJson(IRailConnection connection) {
        JsonObject connectionJson = new JsonObject();
        connectionJson.addProperty(connection.getColor().toString(), connection.getLength());
        return connectionJson;
    }

    /**
     * Creates an 'Acquired' representing a given IRailConnection
     * @param connection an IRailConnection to be converted
     * @return a new JsonArray representing an 'Acquired'
     */
    public static JsonArray connectionToAcquiredJson(IRailConnection connection) {
        JsonArray ret = new JsonArray();
        OrderedPair<ICity> orderedCities = getOrderedCity(connection);
        ret.add(orderedCities.first.getName());
        ret.add(orderedCities.second.getName());
        ret.add(connection.getColor().toString());
        ret.add(connection.getLength());
        return ret;
    }

    /**
     * Creates a connection from an 'Acquired' Json, given an ITrainMap.
     * @param acquired a JsonArray representing an Acquired
     * @param map an ITrainMap to build the IRailConnection from
     * @return an IRailConnection representing the given 'Acquired'
     */
    public static IRailConnection connectionFromAcquiredJson(JsonArray acquired, ITrainMap map) {
        Optional<ICity> city1Opt = map.getCityFromName(acquired.get(0).getAsString());
        Optional<ICity> city2Opt = map.getCityFromName(acquired.get(1).getAsString());
        RailColor color = RailColor.fromString(acquired.get(2).getAsString());

        if(city1Opt.isPresent() && city2Opt.isPresent()) {
            return new RailConnection(new UnorderedPair<ICity>(city1Opt.get(), city2Opt.get()), acquired.get(3).getAsInt(), color);
        }
        throw new IllegalArgumentException("Invalid connection acquired json");
    }
}
