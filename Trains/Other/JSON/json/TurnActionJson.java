package json;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import utils.OrderedPair;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;

import strategy.TurnAction;
import strategy.Action;

import java.util.stream.Collectors;

import static utils.ComparatorUtils.fromUnordered;

/** Utility class for serializing and deserializing Json related to TurnAction */
public class TurnActionJson {
    /**
     * Creates a JsonElement representing a given TurnAction.
     *
     * @param turnAction a strategy.TurnAction representing this strategy's choice of action
     * @return a JSON representation of that TurnAction
     */
    public static JsonElement turnActionToJSON(TurnAction turnAction) {
        if (turnAction.getActionType() == Action.DRAW_CARDS) {
            return new JsonPrimitive("more cards");
        }
        else if (turnAction.getActionType() == Action.ACQUIRE_CONNECTION) {
            return railConnectionToJSON(turnAction.getRailConnection());
        }
        return new JsonPrimitive("");
    }

    /**
     * Creates a JsonElement representing an Acquired given an IRailConnection.
     *
     * @param railConnection an IRailConnection representing the acquired connection
     * @return a JSON representation of that Acquired
     */
    public static JsonElement railConnectionToJSON(IRailConnection railConnection) {
        JsonArray acquired = new JsonArray();

        OrderedPair<ICity> orderedCities = fromUnordered(railConnection.getCities());
        acquired.add(orderedCities.first.getName());
        acquired.add(orderedCities.second.getName());

        acquired.add(railConnection.getColor().name().toLowerCase());
        acquired.add(railConnection.getLength());

        return acquired;
    }

    public static TurnAction turnActionFromJson(JsonElement turnActionJson, ITrainMap map) {
        if(turnActionJson.isJsonPrimitive() && turnActionJson.getAsJsonPrimitive().isString() && turnActionJson.getAsString().equals("more cards")) {
            return TurnAction.createDrawCards(); 
        } else {
            IRailConnection connection = ConnectionJson.connectionFromAcquiredJson(turnActionJson.getAsJsonArray(), map);
            return TurnAction.createAcquireConnection(connection);
        }
    }
}
