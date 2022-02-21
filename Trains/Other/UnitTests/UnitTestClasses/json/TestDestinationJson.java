package json;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import map.City;
import map.Destination;
import map.ICity;
import map.ITrainMap;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TestDestinationJson {
    ITrainMap map = MapJson.mapFromJson(TestMapJson.mapJson);

    ICity boston = new City("Boston", 0, 1);
    ICity seattle = new City("Seattle", 0, 0);

    @Test
    public void testDestinationToJson() {
        JsonArray expectedDestination = new JsonArray();
        expectedDestination.add(new JsonPrimitive("Boston"));
        expectedDestination.add(new JsonPrimitive("Seattle"));

        Destination destination1 = new Destination(boston, seattle);

        Assertions.assertEquals(expectedDestination, DestinationJson.destinationToJson(destination1));
    }

    @Test
    public void testDestinationFromJson() {
        // Destination destinationFromJson(JsonArray destinationJson, ITrainMap map)

        JsonArray destinationJson = new JsonArray();
        destinationJson.add(new JsonPrimitive("Boston"));
        destinationJson.add(new JsonPrimitive("Seattle"));

        Destination destination1 = new Destination(boston, seattle);
        Destination destination2 = new Destination(seattle, boston);

        Assertions.assertEquals(destination1, DestinationJson.destinationFromJson(destinationJson, map));
        Assertions.assertEquals(destination2, DestinationJson.destinationFromJson(destinationJson, map));
    }

    @Test
    public void testDestinationFromInvalidJson() {
        JsonArray badDestinationJson = new JsonArray();
        badDestinationJson.add(new JsonPrimitive("BadCity"));
        badDestinationJson.add(new JsonPrimitive("Seattle"));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                DestinationJson.destinationFromJson(badDestinationJson, map));
    }

}
