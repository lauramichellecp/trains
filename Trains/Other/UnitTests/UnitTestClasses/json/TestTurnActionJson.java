package json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import map.*;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import strategy.TurnAction;
import utils.UnorderedPair;

public class TestTurnActionJson {
    ITrainMap map = MapJson.mapFromJson(TestMapJson.mapJson);

    ICity boston = new City("Boston", 0, 1);
    ICity seattle = new City("Seattle", 0, 0);

    IRailConnection connection = new RailConnection(new UnorderedPair<>(boston, seattle), 3, RailColor.RED);
    IRailConnection connection2 = new RailConnection(new UnorderedPair<>(seattle, boston), 4, RailColor.BLUE);

    private final TurnAction drawCards = TurnAction.createDrawCards();
    private final TurnAction acquireConnection = TurnAction.createAcquireConnection(connection);

    @Test
    public void testTurnActionToJson() {
        JsonElement drawCardsJson = new JsonPrimitive("more cards");
        JsonArray acquired = new JsonArray();
        acquired.add(new JsonPrimitive("Boston"));
        acquired.add(new JsonPrimitive("Seattle"));
        acquired.add(new JsonPrimitive("red"));
        acquired.add(new JsonPrimitive(3));

        Assertions.assertEquals(drawCardsJson, TurnActionJson.turnActionToJSON(drawCards));
        Assertions.assertEquals(acquired, TurnActionJson.turnActionToJSON(acquireConnection));
    }

    @Test
    public void testRailConnectionToJson() {
        JsonArray acquired = new JsonArray();
        acquired.add(new JsonPrimitive("Boston"));
        acquired.add(new JsonPrimitive("Seattle"));
        acquired.add(new JsonPrimitive("blue"));
        acquired.add(new JsonPrimitive(4));

        Assertions.assertEquals(acquired, TurnActionJson.railConnectionToJSON(connection2));
    }

    @Test
    public void testTurnActionFromJson() {
        JsonArray acquired = new JsonArray();
        acquired.add(new JsonPrimitive("Boston"));
        acquired.add(new JsonPrimitive("Seattle"));
        acquired.add(new JsonPrimitive("red"));
        acquired.add(new JsonPrimitive(3));

        JsonArray acquired2 = new JsonArray();
        acquired2.add(new JsonPrimitive("Boston"));
        acquired2.add(new JsonPrimitive("CityThatDoesntExist"));
        acquired2.add(new JsonPrimitive("red"));
        acquired2.add(new JsonPrimitive(3));

        Assertions.assertEquals(drawCards.getActionType(),
                TurnActionJson.turnActionFromJson(new JsonPrimitive("more cards"), map).getActionType());
        Assertions.assertEquals(acquireConnection.getRailConnection(),
                TurnActionJson.turnActionFromJson(acquired, map).getRailConnection());

        Assertions.assertThrows(IllegalArgumentException.class, () -> TurnActionJson.turnActionFromJson(acquired2, map));
    }
}
