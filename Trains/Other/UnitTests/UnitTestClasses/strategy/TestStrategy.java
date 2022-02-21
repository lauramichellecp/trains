package strategy;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import game_state.IPlayerGameState;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import map.City;
import map.Destination;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.RailColor;
import map.RailConnection;
import map.TrainMap;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import json.PlayerStateJson;
import json.MapJson;
import utils.UnorderedPair;

public class TestStrategy {
    // TODO: Add unit tests for not having enough rail to buy a connection

    ICity boston;
    ICity nyc;
    ICity seattle;
    Set<ICity> cities;
    IRailConnection connection;
    Set<IRailConnection> rails;
    ITrainMap map;
    public Set<Destination> destinations = new HashSet<>();

    public static Pair<ITrainMap, IPlayerGameState> readAndParseTestMap(String jsonFileName) {
        try {
            JsonStreamParser parser =
                new JsonStreamParser(
                    new FileReader("Other/UnitTests/PlayerGameStateInput/" + jsonFileName));
            JsonElement map = parser.next();
            JsonElement state = parser.next();
            return new Pair<>(MapJson.mapFromJson(map), PlayerStateJson.playerStateFromJson(state));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void init() {
        boston = new City("Boston", 0, 0);
        nyc = new City("NYC", 0, 0);
        seattle = new City("Seattle", 0, 0);
        cities = new HashSet<>();
        cities.add(boston);
        cities.add(nyc);
        cities.add(seattle);

        destinations.add(new Destination(boston, nyc));
        destinations.add(new Destination(boston, seattle));
        destinations.add(new Destination(seattle, nyc));

        connection = new RailConnection(new UnorderedPair<>(boston, nyc), 3, RailColor.BLUE);
        // Set should only have 1 element due to equality rules
        rails = new HashSet<>();
        rails.add(connection);

        // Basic test
        map = new TrainMap(cities, rails);
    }

    @Test
    public void testChooseDestinations() {
        Set<Destination> chosenDestinations = new BuyNow()
            .chooseDestinations(destinations, 3, this.map, 0, null);
        Assertions.assertEquals(3, chosenDestinations.size());

        chosenDestinations = new BuyNow().chooseDestinations(destinations, 2, this.map, 0, null);
        Assertions.assertEquals(2, chosenDestinations.size());
        Assertions.assertFalse(chosenDestinations.contains(new Destination(boston, nyc)));

        chosenDestinations = new Hold10().chooseDestinations(destinations, 3, this.map, 0, null);
        Assertions.assertEquals(3, chosenDestinations.size());

        chosenDestinations = new Hold10().chooseDestinations(destinations, 2, this.map, 0, null);
        Assertions.assertEquals(2, chosenDestinations.size());
        Assertions.assertFalse(chosenDestinations.contains(new Destination(nyc, seattle)));
    }

    @Test
    public void testForcedDraw() {
        // Both strategies forced to draw cards if they cannot afford a connection
        // 'cant-afford'.json has the player with > 10 cards, but cannot afford any connection
        Pair<ITrainMap, IPlayerGameState> state = readAndParseTestMap("cant-afford.json");
        Assertions.assertEquals(Action.DRAW_CARDS, new BuyNow().takeTurn(state.getSecond(), state.getFirst(), null).getActionType());
        Assertions.assertEquals(Action.DRAW_CARDS, new Hold10().takeTurn(state.getSecond(), state.getFirst(), null).getActionType());
    }

    @Test
    public void testChooseCards() {
        Assertions.assertFalse(new BuyNow().chooseDrawCards(map.getRailConnections(), null, null));
        // Won't draw cards if > 10 total
        Pair<ITrainMap, IPlayerGameState> state = readAndParseTestMap("bos-sea-red-white.json");
        Assertions.assertFalse(
            new Hold10().chooseDrawCards(state.getFirst().getRailConnections(), state.getSecond(), null));
        // Will draw cards at 10 or fewer cards
        state = readAndParseTestMap("bos-sea-red-blue.json");
        Assertions.assertTrue(
            new Hold10().chooseDrawCards(state.getFirst().getRailConnections(), state.getSecond(), null));
    }

    @Test
    public void testChooseRailConnection() {
        testChooseRailConnection(new BuyNow());
        testChooseRailConnection(new Hold10());
    }

    public void testChooseRailConnection(AStrategy strategy) {
        // Tests preference of rail connection between same cities based on length while ensuring
        // affordability
        Pair<ITrainMap, IPlayerGameState> state = readAndParseTestMap("bos-sea-red-white.json");
        Assertions.assertEquals(
            new RailConnection(new UnorderedPair<>(boston, seattle), 3, RailColor.RED),
            strategy.takeTurn(state.getSecond(), state.getFirst(), null).getRailConnection());

        // Tests preference of rail connection between same cities based on color
        state = readAndParseTestMap("bos-sea-red-blue.json");
        Assertions.assertEquals(
            new RailConnection(new UnorderedPair<>(boston, seattle), 3, RailColor.BLUE),
            strategy.getPreferredConnection(
                state.getSecond().calculateUnoccupiedConnections(state.getFirst()),
                state.getSecond(), null));

        // Tests preference of rail connection among different cities based on alphabetization
        state = readAndParseTestMap("bos-sea-tex.json");
        Assertions.assertEquals(
            new RailConnection(new UnorderedPair<>(boston, seattle), 5, RailColor.WHITE),
            strategy.takeTurn(state.getSecond(), state.getFirst(), null).getRailConnection());
    }
}
