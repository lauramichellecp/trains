package json;

import com.google.gson.*;
import map.*;
import org.junit.Assert;
import org.junit.Test;
import utils.UnorderedPair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Test class for utilities related to ITrainMap json.
 */
public class TestMapJson {
    private static final String json = "{\n" +
            "            \"width\" : 800,\n" +
            "            \"height\": 800,\n" +
            "            \"cities\": [[\"Seattle\", [0, 0]], [\"Texas\", [800, 0]], [\"Boston\", [0, 800]]],\n" +
            "            \"connections\": {\"Boston\": {\"Seattle\": {\"red\": 3},\n" +
            "                            \"Texas\": {\"green\": 5}},\n" +
            "                            \"Seattle\": {\"Texas\": {\"blue\": 4}}}\n" +
            "       }";
    public static final JsonElement mapJson = JsonParser.parseString(json).getAsJsonObject();;
    private final ITrainMap map;

    ICity boston;
    ICity seattle;
    ICity texas;

    Set<ICity> cities;
    Set<IRailConnection> rails;

    public TestMapJson() {
        boston = new City("Boston", 0, 1);
        seattle = new City("Seattle", 0, 0);
        texas = new City("Texas", 1, 0);
        cities = new HashSet<>();
        cities.add(boston);
        cities.add(seattle);
        cities.add(texas);

        rails = new HashSet<>();
        rails.add(new RailConnection(new UnorderedPair<>(boston, seattle), 3, RailColor.RED));
        rails.add(new RailConnection(new UnorderedPair<>(boston, texas), 5, RailColor.GREEN));
        rails.add(new RailConnection(new UnorderedPair<>(seattle, texas), 4, RailColor.BLUE));

        MapDimensions dimensions = new MapDimensions(800, 800);

        map = new TrainMap(cities, rails, dimensions);
    }

    @Test
    public void testCreateMapFromJson() {
        ITrainMap newMapFromJson = MapJson.mapFromJson(mapJson);
        Assert.assertEquals(this.map.getCities(), newMapFromJson.getCities());
        Assert.assertEquals(this.map.getRailConnections(), newMapFromJson.getRailConnections());
        Assert.assertEquals(this.map.getAllPossibleDestinations(), newMapFromJson.getAllPossibleDestinations());
        Assert.assertEquals(this.map.getMapDimension(), newMapFromJson.getMapDimension());
    }

    @Test
    public void testCreateJsonFromMap() {
        Assert.assertEquals(this.mapJson, MapJson.mapToJson(this.map));
    }


    @Test
    public void testCityToJsonArray() {
        ICity city1 = new City("name", 0.1, 0.8);
        ICity city2 = new City("another", 0, 1); // should be the min map dimensions

        JsonArray citiesArray = createCitiesJsonArray();

        Assert.assertEquals(citiesArray.get(0), MapJson.cityToJsonArray(city1, new MapDimensions(800, 800)));
        Assert.assertEquals(citiesArray.get(1), MapJson.cityToJsonArray(city2, new MapDimensions(800, 800)));

    }

    @Test
    public void testGetCitiesFromJson() {
        Map<String, ICity> cityMap = new HashMap<>();

        JsonArray citiesArray = createCitiesJsonArray();
        citiesArray.forEach(jsonElement -> cityMap.put(jsonElement.getAsJsonArray().get(0).getAsString(),
                new City(jsonElement.getAsJsonArray().get(0).getAsString(),
                        jsonElement.getAsJsonArray().get(1).getAsJsonArray().get(0).getAsDouble() / 800,
                        jsonElement.getAsJsonArray().get(1).getAsJsonArray().get(1).getAsDouble() / 800)));

        Assert.assertEquals(cityMap, MapJson.getCitiesFromJson(800, 800, citiesArray));
    }

    /**
     * Creates a JsonArray of two cities.
     * @return JsonArray of cities with absolute coordinates
     */
    private JsonArray createCitiesJsonArray() {
        JsonArray citiesResult = new JsonArray();

        JsonArray city1json = new JsonArray();
        JsonArray position1 = new JsonArray();
        position1.add(80);
        position1.add(640);

        city1json.add("name");
        city1json.add(position1);

        JsonArray city2json = new JsonArray();
        JsonArray position2 = new JsonArray();
        position2.add(0);
        position2.add(800);

        city2json.add("another");
        city2json.add(position2);

        citiesResult.add(city1json);
        citiesResult.add(city2json);
        return citiesResult;
    }
}
