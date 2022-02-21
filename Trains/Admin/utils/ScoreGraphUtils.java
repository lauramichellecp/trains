package utils;

import com.google.common.collect.Iterators;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import map.Destination;
import map.IRailConnection;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.YenShortestPathIterator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/** Utility class for graph algorithms related to scoring players. */
public class ScoreGraphUtils {

  /**
   * Determines the set of players who have the longest path (ties allowed). If all players have a
   * longest path of length 0, then all players have that longest path.
   *
   * @param playerConnectionGraphs the graphs of owned connections from which longest paths are
   *     calculated.
   * @return a Set of Integer where each Integer is the index of the graph in the given list of
   *     graphs that has or ties for the longest path. The set will be empty iff the given list is
   *     empty.
   */
  public static Set<Integer> calculatePlayersWithLongestPath(
      List<Graph<String, DefaultWeightedEdge>> playerConnectionGraphs) {
    int overallLongestPathLength = 0;
    Set<Integer> result = new HashSet<>();
    for (int index = 0; index < playerConnectionGraphs.size(); index += 1) {
      int longestPathLength = calculateLongestPathLength(playerConnectionGraphs.get(index));
      // If new record for longest path, clear previous records and add new one
      // If tied for record, add to other records
      // If below record, continue on
      if (longestPathLength > overallLongestPathLength) {
        overallLongestPathLength = longestPathLength;
        result.clear();
        result.add(index);
      } else if (longestPathLength == overallLongestPathLength) {
        result.add(index);
      }
    }
    return result;
  }

  /**
   * Calculates the length of the longest simple path for the given undirected weighted graph.
   *
   * @param occupiedConnectionsGraph the undirected, weighted graph with cities (by name) as
   *     vertices and edges connecting them weighted by the length of the longest IRailConnection
   *     between the two cities.
   * @return an integer >= 0 of the longest simple path that exists in the given graph.
   */
  private static int calculateLongestPathLength(
      Graph<String, DefaultWeightedEdge> occupiedConnectionsGraph) {

    Set<UnorderedPair<String>> allVertexPairs =
        GraphUtility.calculateAllPairs(occupiedConnectionsGraph.vertexSet());

    ToIntFunction<UnorderedPair<String>> longestLengthCalculator =
        (pair) -> longestPathLengthBetweenCities(pair.first, pair.second, occupiedConnectionsGraph);

    OptionalInt maxPathLength = allVertexPairs.stream().mapToInt(longestLengthCalculator).max();
    return maxPathLength.orElse(0);
  }

  /**
   * Calculates the length of the longest path between two cities in the graph using YenShortestPath
   * - this operation is brute force, running at Omega(k) where k is the number of paths between two
   * vertices.
   *
   * @param vertex1 first vertex
   * @param vertex2 second vertex (order doesn't matter, should be distinct from first).
   * @param occupiedConnectionsGraph graph containing the vertices.
   * @return integer of the weight of the longest simple path connecting the cities, or 0 if no path
   *     exists.
   */
  private static int longestPathLengthBetweenCities(
      String vertex1, String vertex2, Graph<String, DefaultWeightedEdge> occupiedConnectionsGraph) {
    Iterator<GraphPath<String, DefaultWeightedEdge>> pathIterator =
        new YenShortestPathIterator<>(occupiedConnectionsGraph, vertex1, vertex2);
    if (pathIterator.hasNext()) {
      return (int) Iterators.getLast(pathIterator).getWeight();
    } else {
      return 0;
    }
  }

  /**
   * Constructs a simple graph from the given set of connections. The vertices of the graph are the
   * names of the cities specified as endpoints on the connections and the edges are weighted by the
   * length of the longest connection between two cities.
   *
   * @param ownedConnections the set of connections providing edges and implicitly vertices for this
   *     graph.
   * @return the graph of String vertices and DefaultWeightEdge edges.
   */
  public static Graph<String, DefaultWeightedEdge> occupiedConnectionsToGraph(
      Set<IRailConnection> ownedConnections) {
    Graph<String, DefaultWeightedEdge> occupiedConnectionsGraph =
        new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

    // add all edges and nodes from occupied connections
    for (IRailConnection connection : ownedConnections) {
      String city1Name = connection.getCities().first.getName();
      String city2Name = connection.getCities().second.getName();
      occupiedConnectionsGraph.addVertex(city1Name);
      occupiedConnectionsGraph.addVertex(city2Name);

      updateOrAddEdge(city1Name, city2Name, connection.getLength(), occupiedConnectionsGraph);
    }

    return occupiedConnectionsGraph;
  }

  /**
   * Adds an edge connecting the given cities with the given weight to the given graph, or updates
   * an existing edge if the given length is greater weight than existing edge.
   *
   * @param city1Name first vertex
   * @param city2Name second vertex (order irrelevant, must be distinct from first vertex).
   * @param connectionLength length of connection to become the weight of the edge.
   * @param occupiedConnectionsGraph the graph to add or update the edge.
   */
  private static void updateOrAddEdge(
      String city1Name,
      String city2Name,
      int connectionLength,
      Graph<String, DefaultWeightedEdge> occupiedConnectionsGraph) {
    if (!occupiedConnectionsGraph.containsEdge(city1Name, city2Name)) {
      DefaultWeightedEdge newEdge = occupiedConnectionsGraph.addEdge(city1Name, city2Name);
      occupiedConnectionsGraph.setEdgeWeight(newEdge, connectionLength);
    } else {
      // We only want the graph to contain the edge with the longest length between two
      // cities because the shorter edges necessarily are not included in any longest
      // simple paths.
      DefaultWeightedEdge oldEdge = occupiedConnectionsGraph.getEdge(city1Name, city2Name);
      occupiedConnectionsGraph.setEdgeWeight(
          oldEdge, Math.max(occupiedConnectionsGraph.getEdgeWeight(oldEdge), connectionLength));
    }
  }

  /**
   * Calculates the number of the given destinations that are completed by the given graph of
   * occupied connections.
   *
   * @param occupiedConnectionsGraph the graph of occupied connections.
   * @param destinations the destinations to complete.
   * @return an integer in the range [0, destinations.size()] representing the number of
   *     destinations completed.
   */
  public static int calculateNumDestinationsConnected(
      Graph<String, DefaultWeightedEdge> occupiedConnectionsGraph, Set<Destination> destinations) {
    ConnectivityInspector<String, DefaultWeightedEdge> connectivityInspector =
        new ConnectivityInspector<>(occupiedConnectionsGraph);

    Predicate<Destination> destinationFulfilled =
        (destination) -> {
          String city1Name = destination.first.getName();
          String city2Name = destination.second.getName();
          return (occupiedConnectionsGraph.containsVertex(city1Name)
              && occupiedConnectionsGraph.containsVertex(city2Name)
              && connectivityInspector.pathExists(city1Name, city2Name));
        };

    return (int) destinations.stream().filter(destinationFulfilled).count();
  }
}
