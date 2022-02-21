package referee;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import map.City;
import map.Destination;
import map.ICity;
import map.RailColor;
import map.RailConnection;
import org.junit.jupiter.api.Test;
import referee.IPlayerData;
import referee.PlayerData;
import referee.ScoreCalculator;
import referee.TrainsPlayerHand;
import utils.UnorderedPair;

public class TestScoreCalculator {

    @Test
    public void testScorePlayersEmptyData() {
        assertEquals(new ArrayList<Integer>(),
            ScoreCalculator.scorePlayers(new ArrayList<>()));
    }

    @Test
    public void testScoreNoConnections() {
        IPlayerData data = new PlayerData(new TrainsPlayerHand(new HashMap<>()), 5,
            new HashSet<>(), new HashSet<>());

        // 0 total segments
        // + 0 destinations completed
        // + 0 destinations uncompleted
        // + 20 longest path
        // = 20
        assertEquals(List.of(20),
            ScoreCalculator.scorePlayers(List.of(data)));
    }

    @Test
    public void testScoreCompletedDestination() {
        ICity boston = new City("Boston", 0, 0);
        ICity nyc = new City("NYC", 0, 0);
        IPlayerData data = new PlayerData(
            new TrainsPlayerHand(new HashMap<>()),
            5,
            new HashSet<>(List.of(new Destination(boston, nyc))),
            new HashSet<>(List.of(new RailConnection(new UnorderedPair<>(boston, nyc),
                3, RailColor.BLUE))));

        // 3 total segments
        // + 10 * 1 destinations completed
        // + 0 destinations uncompleted
        // + 20 longest path
        // = 33
        assertEquals(List.of(33),
            new ScoreCalculator().scorePlayers(List.of(data)));
    }

    @Test
    public void testScoreIncompleteDestination() {
        ICity boston = new City("Boston", 0, 0);
        ICity nyc = new City("NYC", 0, 0);
        IPlayerData data = new PlayerData(
            new TrainsPlayerHand(new HashMap<>()),
            5,
            new HashSet<>(List.of(new Destination(boston, nyc))),
            new HashSet<>());

        // 0 total segments
        // + 0 destinations completed
        // + -10 * 1 destinations uncompleted
        // + 20 longest path
        // = 10
        assertEquals(List.of(10),
            new ScoreCalculator().scorePlayers(List.of(data)));
    }

    @Test
    public void testScoreMultiplePlayers() {
        ICity boston = new City("Boston", 0, 0);
        ICity nyc = new City("NYC", 0, 0);
        ICity texas = new City("Texas", 0, 0);
        ICity chicago = new City("Chicago", 0, 0);

        IPlayerData data1 = new PlayerData(
            new TrainsPlayerHand(new HashMap<>()),
            5,
            new HashSet<>(List.of(new Destination(boston, nyc))),
            new HashSet<>());

        IPlayerData data2 = new PlayerData(
            new TrainsPlayerHand(new HashMap<>()),
            3,
            new HashSet<>(List.of(new Destination(texas, chicago))),
            new HashSet<>(List.of(
                new RailConnection(new UnorderedPair<>(texas, nyc),3, RailColor.BLUE),
                new RailConnection(new UnorderedPair<>(nyc, boston),4, RailColor.BLUE),
                new RailConnection(new UnorderedPair<>(boston, chicago),5, RailColor.BLUE))));

        IPlayerData data3 = new PlayerData(
            new TrainsPlayerHand(new HashMap<>()),
            3,
            new HashSet<>(List.of(new Destination(nyc, chicago), new Destination(boston, texas))),
            new HashSet<>(List.of(
                new RailConnection(new UnorderedPair<>(chicago, nyc),3, RailColor.RED),
                new RailConnection(new UnorderedPair<>(texas, boston),4, RailColor.GREEN))));


        assertEquals(List.of(-10, 42, 27),
            new ScoreCalculator().scorePlayers(List.of(data1, data2, data3)));
    }
}
