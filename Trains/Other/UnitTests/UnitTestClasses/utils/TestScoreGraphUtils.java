package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import map.City;
import map.Destination;
import map.ICity;
import map.IRailConnection;
import map.RailColor;
import map.RailConnection;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.ScoreGraphUtils;
import utils.UnorderedPair;

public class TestScoreGraphUtils {
  @Test
  public void testCalculatePlayersWithLongestPath() {

    Set<IRailConnection> p1 = new HashSet<>();
    p1.add(rail("A", "B", 3, RailColor.RED));
    // No players -> no indices
    Assertions.assertEquals(
        new HashSet<Integer>(), ScoreGraphUtils.calculatePlayersWithLongestPath(new ArrayList<>()));

    // All players have longest path of 0
    Assertions.assertEquals(
        new HashSet<>(Arrays.asList(0, 1, 2)),
        ScoreGraphUtils.calculatePlayersWithLongestPath(Arrays.asList(empty(), empty(), empty())));

    // Two players tie for longest path
    Assertions.assertEquals(
        new HashSet<>(Arrays.asList(0, 2)),
        ScoreGraphUtils.calculatePlayersWithLongestPath(
            Arrays.asList(
                ScoreGraphUtils.occupiedConnectionsToGraph(p1),
                empty(),
                ScoreGraphUtils.occupiedConnectionsToGraph(p1))));

    // Now the 0th player has two connections between the same cities, so should pick the highest
    // one and beat out 2nd player
    Set<IRailConnection> p2 = new HashSet<>(p1);
    p2.add(rail("A", "B", 4, RailColor.BLUE));
    Assertions.assertEquals(
        new HashSet<>(Arrays.asList(0)),
        ScoreGraphUtils.calculatePlayersWithLongestPath(
            Arrays.asList(
                ScoreGraphUtils.occupiedConnectionsToGraph(p2),
                empty(),
                ScoreGraphUtils.occupiedConnectionsToGraph(p1))));
  }

  private static Graph<String, DefaultWeightedEdge> empty() {
    return ScoreGraphUtils.occupiedConnectionsToGraph(new HashSet<>());
  }

  private static IRailConnection rail(String name1, String name2, int length, RailColor color) {
    return new RailConnection(new UnorderedPair<>(city(name1), city(name2)), length, color);
  }

  private static ICity city(String name) {
    return new City(name, 0, 0);
  }

  @Test
  public void testCalculateNumDestinationsConnected() {
    Function<String, City> cityMaker = (str) -> new City(str, 0, 0);

    Function<UnorderedPair<String>, UnorderedPair<ICity>> cityPairMaker =
        (strs) -> new UnorderedPair<>(cityMaker.apply(strs.first), cityMaker.apply(strs.second));

    Assertions.assertEquals(
        0,
        ScoreGraphUtils.calculateNumDestinationsConnected(
            ScoreGraphUtils.occupiedConnectionsToGraph(new HashSet<>()), new HashSet<>()));

    Assertions.assertEquals(
        0,
        ScoreGraphUtils.calculateNumDestinationsConnected(
            ScoreGraphUtils.occupiedConnectionsToGraph(new HashSet<>()),
            new HashSet<>(
                Collections.singletonList(
                    new Destination(cityMaker.apply("A"), cityMaker.apply("B"))))));

    Function<UnorderedPair<String>, RailConnection> railMaker =
        (strs) -> new RailConnection(cityPairMaker.apply(strs), 3, RailColor.BLUE);
    // A graph where A,B,C are strongly connected, D connected to B, and E-F connected but separate
    // from A,B,C,D
    List<UnorderedPair<String>> connectionList =
        new ArrayList<>(
            Arrays.asList(
                new UnorderedPair<>("A", "B"),
                new UnorderedPair<>("B", "C"),
                new UnorderedPair<>("C", "A"),
                new UnorderedPair<>("D", "B"),
                new UnorderedPair<>("E", "F")));
    Set<IRailConnection> rails = connectionList.stream().map(railMaker).collect(Collectors.toSet());

    List<UnorderedPair<String>> destinationList =
        new ArrayList<>(
            Arrays.asList(
                new UnorderedPair<>("A", "E"),
                new UnorderedPair<>("B", "F"),
                new UnorderedPair<>("A", "B"),
                new UnorderedPair<>("B", "C"),
                new UnorderedPair<>("C", "A"),
                new UnorderedPair<>("D", "B"),
                new UnorderedPair<>("E", "F"),
                new UnorderedPair<>("A", "D")));
    Set<Destination> destinations =
        destinationList.stream()
            .map((p) -> new Destination(cityPairMaker.apply(p)))
            .collect(Collectors.toSet());
    Assertions.assertEquals(
        6,
        ScoreGraphUtils.calculateNumDestinationsConnected(
            ScoreGraphUtils.occupiedConnectionsToGraph(rails), destinations));
  }
}
