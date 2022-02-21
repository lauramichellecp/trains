package json;

import com.google.gson.*;
import map.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import utils.UnorderedPair;

import java.util.*;

/**
 * Test class for utilities related to RailConnection json.
 */
public class TestConnectionJson {
    ITrainMap map = MapJson.mapFromJson(TestMapJson.mapJson);

    JsonObject connections;
    Map<String, ICity> cities;

    ICity boston;
    ICity seattle;
    ICity texas;

    public TestConnectionJson() {
        this.cities = new HashMap<>();

        String jsonConnections = "{\"Boston\": {\"Seattle\": {\"red\": 3},\n" +
                "                       \"Texas\": {\"green\": 5}},\n" +
                "           \"Seattle\": {\"Texas\": {\"blue\": 4}}}";
        this.connections =  JsonParser.parseString(jsonConnections).getAsJsonObject();

        boston = new City("Boston", 0, 1);
        seattle = new City("Seattle", 0, 0);
        texas = new City("Texas", 1, 0);

        cities.put("Boston", boston);
        cities.put("Seattle", seattle);
        cities.put("Texas", texas);
    }

    @Test
    public void testConnectionFromAcquiredJson() {
        IRailConnection expected = new RailConnection(new UnorderedPair<>(boston, seattle), 3, RailColor.RED);

        JsonArray acquired1 = new JsonArray();
        acquired1.add(new JsonPrimitive("Boston"));
        acquired1.add(new JsonPrimitive("Seattle"));
        acquired1.add(new JsonPrimitive("red"));
        acquired1.add(new JsonPrimitive(3));

        Assertions.assertEquals(expected, ConnectionJson.connectionFromAcquiredJson(acquired1, map));
    }
    @Test
    public void testConnectionToAcquiredJson() {
        IRailConnection acquired = new RailConnection(new UnorderedPair<>(boston, texas), 5, RailColor.BLUE);

        JsonArray expected = new JsonArray();
        expected.add(new JsonPrimitive("Boston"));
        expected.add(new JsonPrimitive("Texas"));
        expected.add(new JsonPrimitive("blue"));
        expected.add(new JsonPrimitive(5));

        Assertions.assertEquals(expected, ConnectionJson.connectionToAcquiredJson(acquired));
    }
    @Test
    public void testGetRailConnectionsFromJson() {
        List<IRailConnection> expectedConnections = new ArrayList<>();
        Map<String, ICity> citiesCopy = new HashMap<>(cities);

        expectedConnections.add(new RailConnection(new UnorderedPair<>(boston, seattle), 3, RailColor.RED));
        expectedConnections.add(new RailConnection(new UnorderedPair<>(boston, texas), 5, RailColor.GREEN));
        expectedConnections.add(new RailConnection(new UnorderedPair<>(seattle, texas), 4, RailColor.BLUE));

        Assertions.assertEquals(expectedConnections, ConnectionJson.getRailConnectionsFromJson(connections, citiesCopy));
    }
    @Test
    public void testConnectionToJson() {
        JsonObject connectionsSegmentJson = JsonParser.parseString("{\"red\":3}").getAsJsonObject();

        IRailConnection connection = new RailConnection(new UnorderedPair<>(boston, seattle), 3, RailColor.RED);
        Assertions.assertEquals(connectionsSegmentJson, ConnectionJson.connectionToJson(connection));
    }
}
