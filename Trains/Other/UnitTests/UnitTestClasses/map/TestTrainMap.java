package map;

import static org.junit.Assert.fail;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import map.City;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.RailColor;
import map.RailConnection;
import map.TrainMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.UnorderedPair;

/** A Set of unit tests for the TrainMap class. */
public class TestTrainMap {
  ICity boston;
  ICity nyc;
  Set<ICity> cities;
  IRailConnection connection;
  Set<IRailConnection> rails;
  ITrainMap map;

  @BeforeEach
  public void init() {
    boston = new City("Boston", 0, 0);
    nyc = new City("NYC", 0, 0);
    cities = new HashSet<>();
    cities.add(boston);
    cities.add(nyc);

    connection = new RailConnection(new UnorderedPair<>(boston, nyc), 3, RailColor.BLUE);
    // Set should only have 1 element due to equality rules
    rails = new HashSet<>();
    rails.add(connection);

    // Basic test
    map = new TrainMap(cities, rails);
  }

  @Test
  public void TestNullConstruction() {
    try {
      ITrainMap m = new TrainMap(null, new HashSet<>());
      fail();
    } catch (NullPointerException ignored) {
    }
    try {
      ITrainMap m = new TrainMap(new HashSet<>(), null);
      fail();
    } catch (NullPointerException ignored) {
    }
  }

  @Test
  public void TestBadRailConnections() {
    ICity c1 = new City("Boston", 0, 0);
    IRailConnection rc1 =
        new RailConnection(new UnorderedPair<>(c1, new City("mystery", 0, 0)), 3, RailColor.BLUE);
    Set<ICity> citySet = new HashSet<>();
    citySet.add(c1);

    Set<IRailConnection> railSet1 = new HashSet<>();
    railSet1.add(rc1);
    try {
      ITrainMap m = new TrainMap(citySet, railSet1);
      fail();
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  public void TestGetCities() {
    // Basic test
    ITrainMap m = new TrainMap(cities, new HashSet<>());
    Set<ICity> returnedCities = m.getCities();
    assertEquals(m.getCities(), cities);

    // Test defensive copies, both for input set and output set
    returnedCities.clear();
    assertFalse(m.getCities().isEmpty());
    assertFalse(cities.isEmpty());
  }

  @Test
  public void TestGetCityNames() {
    // Basic test
    ITrainMap m = new TrainMap(cities, new HashSet<>());
    Set<String> expected = new HashSet<>();
    expected.add("NYC");
    expected.add("Boston");
    assertEquals(expected, m.getCityNames());
  }

  @Test
  public void TestGetRails() {
    // Basic test
    rails.add(new RailConnection(new UnorderedPair<>(nyc, boston), 4, RailColor.GREEN));
    rails.add(new RailConnection(new UnorderedPair<>(nyc, boston), 5, RailColor.GREEN));
    map = new TrainMap(cities, rails);

    Set<IRailConnection> returnedRails = map.getRailConnections();
    assertEquals(map.getRailConnections(), rails);
    assertEquals(map.getRailConnections().size(), 2);

    // Test defensive copies, both for input set and output set
    returnedRails.clear();
    assertFalse(map.getRailConnections().isEmpty());
    assertFalse(rails.isEmpty());
  }

  @Test
  public void TestBasicGetPossibleDestinations() {
    ITrainMap m = new TrainMap(new HashSet<>(), new HashSet<>());
    assertEquals(m.getAllPossibleDestinations(), new HashSet<>());

    Set<UnorderedPair<ICity>> expected = new HashSet<>();
    expected.add(connection.getCities());
    assertEquals(expected, map.getAllPossibleDestinations());
  }

  @Test
  public void TestSubstantialPossibleDestinations() {
    Function<String, City> cityMaker = (str) -> new City(str, 0, 0);

    Function<UnorderedPair<String>, UnorderedPair<ICity>> cityPairMaker =
        (strs) -> new UnorderedPair<>(cityMaker.apply(strs.first), cityMaker.apply(strs.second));
    Function<UnorderedPair<String>, RailConnection> railMaker =
        (strs) -> new RailConnection(cityPairMaker.apply(strs), 3, RailColor.BLUE);
    // A graph where A,B,C are strongly connected, D connected to B, and E-F connected but separate
    // from A,B,C,D
    cities = Stream.of("A", "B", "C", "D", "E", "F").map(cityMaker).collect(Collectors.toSet());
    List<UnorderedPair<String>> connectionList =
        new ArrayList<>(
            Arrays.asList(
                new UnorderedPair<>("A", "B"),
                new UnorderedPair<>("B", "C"),
                new UnorderedPair<>("C", "A"),
                new UnorderedPair<>("D", "B"),
                new UnorderedPair<>("E", "F")));
    rails = connectionList.stream().map(railMaker).collect(Collectors.toSet());
    rails.add(new RailConnection(new UnorderedPair<ICity>(new City("A", 0, 0),
        new City("B", 0, 0)), 3, RailColor.RED));
    map = new TrainMap(cities, rails);

    // There are two more destinations possible than number of connections
    assertNotEquals(
        connectionList.stream().map(cityPairMaker).collect(Collectors.toSet()),
        map.getAllPossibleDestinations());
    assertEquals(connectionList.size() + 2, map.getAllPossibleDestinations().size());

    // Adding them gives equivalent sets
    connectionList.add(new UnorderedPair<>("D", "C"));
    connectionList.add(new UnorderedPair<>("A", "D"));
    assertEquals(
        connectionList.stream().map(cityPairMaker).collect(Collectors.toSet()),
        map.getAllPossibleDestinations());
  }
}
