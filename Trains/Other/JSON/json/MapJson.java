package json;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import map.ICity;
import map.City;
import map.MapDimensions;
import map.TrainMap;
import map.ITrainMap;
import map.IRailConnection;

import utils.UnorderedPair;
import utils.OrderedPair;
import utils.ComparatorUtils;

import json.ConnectionJson;

/** Utility class for serializing and deserializing Json related to ITrainMap. */
public class MapJson {
    /**
    *      {
    *          "width" : 800,
    *          "height": 800,
    *          "cities": [["Seattle", [0, 0]], ["Boston", [800, 50]], ["Texas", [500, 800]]],
    *          "connections": {"Boston": {"Seattle": {"red": 3},
    *                                      "Texas": {"green": 5}},
    *                          "Seattle": {"Texas": {"blue": 4}}}
    *      }
    */
    public static JsonObject mapToJson(ITrainMap map) {
        JsonObject mapJson = new JsonObject();
        MapDimensions dimensions = map.getMapDimension();
        mapJson.addProperty("width", dimensions.getWidth());
        mapJson.addProperty("height", dimensions.getHeight());
        
        JsonArray cities = new JsonArray();
        for(ICity c : map.getCities()) {
            cities.add(cityToJsonArray(c, dimensions));
        }
        mapJson.add("cities", cities);

        JsonObject connections = new JsonObject();
        for(IRailConnection con : map.getRailConnections()) {
            UnorderedPair<ICity> connectionCities = con.getCities();
            OrderedPair<ICity> connectionCitiesOrdered = ConnectionJson.getOrderedCity(con);
            
            if(connections.keySet().contains(connectionCitiesOrdered.first.getName())) {
                JsonObject currentConnections = connections.getAsJsonObject(connectionCitiesOrdered.first.getName());
                if(currentConnections.keySet().contains(connectionCitiesOrdered.second.getName())) {
                    JsonObject currentConnectionsForDestination = currentConnections.getAsJsonObject(connectionCitiesOrdered.second.getName());
                    currentConnectionsForDestination.addProperty(con.getColor().toString(), con.getLength());
                } else {
                    currentConnections.add(connectionCitiesOrdered.second.getName(), ConnectionJson.connectionToJson(con));
                }
            } else {
                JsonObject citySecond = new JsonObject();
                citySecond.add(connectionCitiesOrdered.second.getName(), ConnectionJson.connectionToJson(con));
                connections.add(connectionCitiesOrdered.first.getName(), citySecond);
            }
        }
        mapJson.add("connections", connections);

        return mapJson;
    }

    /**
     * Creates a JsonArray from a given ICity and MapDimensions
     * @param city city to encode
     * @param dimensions the real dimensions of a map in the game of Trains
     * @return a new JsonArray with the given city (with absolute coordinates)
     */
    public static JsonArray cityToJsonArray(ICity city, MapDimensions dimensions) {
        JsonArray cityJson = new JsonArray();
        cityJson.add(city.getName());
        JsonArray position = new JsonArray();
        position.add((int)(city.getRelativePosition().first * dimensions.getWidth()));
        position.add((int)(city.getRelativePosition().second * dimensions.getHeight()));
        
        cityJson.add(position);
        return cityJson;
    }

    /**
     * Creates a map of city names to ICity objects for all of the cities specified in the JSON input.
     *
     * The JSON input will be in the form:
     *      [["Seattle", [0, 0]], ["Boston", [800, 50]], ["Texas", [500, 800]]]
     *
     * @param mapWidth The width of the game map in pixels
     * @param mapHeight The height of the game map in pixels
     * @param citiesSpecification The JSON specifying a list of cities
     * @return A map of city names to ICity objects for all of the cities specified in the JSON input
     */
    public static Map<String, ICity> getCitiesFromJson(int mapWidth, int mapHeight,
        JsonArray citiesSpecification) {

        Map<String, ICity> cities = new HashMap<>();
        for (JsonElement citySpecification : citiesSpecification) {
            JsonArray cityArray = citySpecification.getAsJsonArray();
            // Extract city information from nested array
            String name = cityArray.get(0).getAsString();
            JsonArray position = cityArray.get(1).getAsJsonArray();
            int xPosition = position.get(0).getAsInt();
            int yPosition = position.get(1).getAsInt();
            // Calculate relative position while creating city
            cities.put(name, new City(name, ((double) xPosition) / ((double) mapWidth),
                ((double) yPosition) / ((double) mapHeight)));
        }
        return cities;
    }

    /**
     * Creates an ITrainMap from JSON input representing a map for the game Trains.
     *
     * The JSON input will be in the form:
     *      {
     *          "width" : 800,
     *          "height": 800,
     *          "cities": [["Seattle", [0, 0]], ["Boston", [800, 50]], ["Texas", [500, 800]]],
     *          "connections": {"Boston": {"Seattle": {"red": 3},
     *                                      "Texas": {"green": 5}},
     *                          "Seattle": {"Texas": {"blue": 4}}}
     *      }
     *
     * @param mapSpecification the JSON element representing the entire map specification.
     * @return An ITrainMap representing the game board specified in the JSON input
     */
    public static ITrainMap mapFromJson(JsonElement mapSpecification) {
        JsonObject mapObject = mapSpecification.getAsJsonObject();
        int width = mapObject.get("width").getAsInt();
        int height = mapObject.get("height").getAsInt();

        Map<String, ICity> cities =
            getCitiesFromJson(width, height, mapObject.getAsJsonArray("cities"));
        Set<ICity> citySet = new HashSet<>(cities.values());

        List<IRailConnection> railConnections =
            ConnectionJson.getRailConnectionsFromJson(mapObject.getAsJsonObject("connections"), cities);
        Set<IRailConnection> railConnectionSet = new HashSet<>(railConnections);

        return new TrainMap(citySet, railConnectionSet, new MapDimensions(width, height));
    }
}
