package referee;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import map.City;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.RailColor;
import map.RailConnection;
import map.TrainMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.RefereePlayer;
import strategy.Hold10;
import utils.UnorderedPair;

public class TestActionChecker {

    ICity cityA;
    ICity cityB;
    ICity cityC;
    IActionChecker checker;
    RefereeGameState simpleRefereeGameState;
    IPlayerGameState simplePlayerGameState;
    ITrainMap map;

    @BeforeEach
    public void SetupGameState() {
        Set<ICity> cities = new HashSet<>();
        this.cityA = new City("A", 0.5, 0.5);
        this.cityB = new City("B", 0.2, 0.7);
        this.cityC = new City("C", 0.0, 0.0);
        cities.add(this.cityA);
        cities.add(this.cityB);
        cities.add(this.cityC);

        Set<IRailConnection> rails = new HashSet<>();
        rails.add(new RailConnection(new UnorderedPair<>(cityA, cityB), 4, RailColor.BLUE));
        rails.add(new RailConnection(new UnorderedPair<>(cityA, cityC), 5, RailColor.BLUE));
        rails.add(new RailConnection(new UnorderedPair<>(cityC, cityB), 3, RailColor.RED));

        this.map = new TrainMap(cities, rails);

        List<RailCard> deck = new ArrayList<>();
        deck.add(RailCard.GREEN);
        deck.add(RailCard.WHITE);
        deck.add(RailCard.BLUE);
        deck.add(RailCard.GREEN);
        deck.add(RailCard.GREEN);

        Map<RailCard, Integer> cardsInHand = new HashMap<>();
        cardsInHand.put(RailCard.BLUE, 4);
        cardsInHand.put(RailCard.RED, 1);

        List<PlayerInfoPair> playerInfo = new ArrayList<>();
        playerInfo.add(new PlayerInfoPair(new RefereePlayer(new Hold10()),
            new PlayerData(new TrainsPlayerHand(cardsInHand), 6, new HashSet<>(),
                new HashSet<>())));

        this.simpleRefereeGameState = new RefereeGameState(playerInfo, new HashSet<>(), deck, map);
        this.simplePlayerGameState = this.simpleRefereeGameState.getActivePlayerState();
        this.checker = simpleRefereeGameState.getActionChecker();
    }

    @Test
    public void TestCanAcquireValid() {
        IRailConnection connection = new RailConnection(new UnorderedPair<>(this.cityA, this.cityB),
            4, RailColor.BLUE);
        assertTrue(
            this.checker.canAcquireConnection(this.simplePlayerGameState, this.map, connection));
    }

    @Test
    public void TestCanAcquireRailDoesntExist() {
        IRailConnection connection = new RailConnection(new UnorderedPair<>(this.cityA, this.cityB),
            3, RailColor.RED);
        assertFalse(
            this.checker.canAcquireConnection(this.simplePlayerGameState, this.map, connection));
    }

    @Test
    public void TestCanAcquireNotEnoughCards() {
        IRailConnection connection = new RailConnection(new UnorderedPair<>(this.cityA, this.cityC),
            5, RailColor.BLUE);
        assertFalse(
            this.checker.canAcquireConnection(this.simplePlayerGameState, this.map, connection));
    }

    @Test
    public void TestCanAcquireNotEnoughRails() {
        IRailConnection connection = new RailConnection(new UnorderedPair<>(this.cityA, this.cityB),
            4, RailColor.BLUE);
        assertTrue(
            this.checker.canAcquireConnection(this.simplePlayerGameState, this.map, connection));

        this.simpleRefereeGameState.acquireConnectionForActivePlayer(connection);
        assertFalse(this.checker
            .canAcquireConnection(this.simpleRefereeGameState.getActivePlayerState(), this.map,
                connection));
    }

    @Test
    public void TestCanAcquireAlreadyOccupied() {
        IRailConnection connection = new RailConnection(new UnorderedPair<>(this.cityA, this.cityB),
            4, RailColor.BLUE);
        assertTrue(this.checker.canAcquireConnection(this.simplePlayerGameState, this.map, connection));

        this.simpleRefereeGameState.acquireConnectionForActivePlayer(connection);
        assertFalse(this.checker
            .canAcquireConnection(this.simpleRefereeGameState.getActivePlayerState(), this.map,
                connection));
    }
}
