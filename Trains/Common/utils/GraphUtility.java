package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

/**
 * This Class contains a set of useful functions for operating on simple, undirected graphs.
 */
public class GraphUtility {
    /**
     * Returns all pairs of nodes in the graph that are connected by some path.
     *
     * @param adjacencyList The graph represented as a mapping of nodes to a set of nodes that it
     *                      is directly connected to.
     * @param <T> The Object that represents one Node in the graph.
     * @return A Set of all pairs of nodes in the graph that are connected by some path.
     */
    public static <T> Set<UnorderedPair<T>> getConnectedPairs(Map<T, Set<T>> adjacencyList) {
        Set<UnorderedPair<T>> result = new HashSet<>();
        List<Set<T>> allConnectedComponents = getConnectedComponents(adjacencyList);
        for (Set<T> connectedComponent : allConnectedComponents) {
            result.addAll(calculateAllPairs(connectedComponent));
        }
        return result;
    }

    /**
     * Calculates the n choose 2 unordered pairs among the given set of nodes.
     * @param nodes the set of nodes to form pairs from.
     * @param <T> the type of the node.
     * @return A set of unordered pairs containing every possible undirected pairing among nodes.
     */
    public static <T> Set<UnorderedPair<T>> calculateAllPairs(Set<T> nodes) {
        Set<UnorderedPair<T>> result = new HashSet<>();
        List<T> nodeList = new ArrayList<>(nodes);
        for (int i = 0; i < nodeList.size(); i += 1) {
            for (int j = i + 1; j < nodeList.size(); j += 1) {
                result.add(new UnorderedPair<>(nodeList.get(i), nodeList.get(j)));
            }
        }
        return result;
    }

    /**
     * Calculates a list of all connected components for the given graph. Although designed for
     * simple graphs, it should work for self-loops as well.
     * @param adjacencyList the simple, undirected graph represented as an adjacency list.
     * @param <T> the type of the nodes in the graph.
     * @return A list of sets of nodes, where each set of nodes is a connected component.
     * No sets will be empty and no node will be in more than one set.
     */
    public static <T> List<Set<T>> getConnectedComponents(Map<T, Set<T>> adjacencyList) {
        // Accumulator to avoid running getConnectedComponent repetitively
        Set<T> visitedNodes = new HashSet<>();
        List<Set<T>> result = new ArrayList<>();
        for (T city : adjacencyList.keySet()) {
            if(! visitedNodes.contains(city)) {
                Set<T> connectedComponent = getConnectedComponent(city, adjacencyList);
                visitedNodes.addAll(connectedComponent);
                result.add(connectedComponent);
            }
        }
        return result;
    }

    /**
     * Gets the set of all vertices in the connected component that contains the given start vertex
     * by performing BFS from the given start node.
     *
     * @param start The node in the graph that must be in the returned connected component.
     * @param adjacencyList The graph represented as a mapping of nodes to a set of nodes that it
     *                      is directly connected to.
     * @param <T> The Object that represents one Node in the graph.
     * @return The Set of all vertices in the connected component that contains the start component.
     */
    public static <T> Set<T> getConnectedComponent(T start, Map<T, Set<T>> adjacencyList) {
        // Accumulator of nodes in the connected component
        Set<T> connectedComponent = new HashSet<>();

        Queue<T> unvisited = new LinkedList<>();
        unvisited.add(start);
        // Process unvisited nodes from Queue by adding to accumulator and adding unvisited
        // neighbors to the unvisited Queue. Because only unvisited nodes are added and each step
        // removes a node from Queue, Queue will eventually be empty.
        while (!unvisited.isEmpty()) {
            T nextVisited = unvisited.poll();
            connectedComponent.add(nextVisited);
            for (T neighbor : adjacencyList.get(nextVisited)) {
                if (! connectedComponent.contains(neighbor)) {
                    unvisited.add(neighbor);
                }
            }
        }
        return connectedComponent;
    }

    /**
     * Construct a representation of a simple, undirected graph as a mapping of nodes to a set of
     * nodes that the node is directly connected to.
     *
     * @param vertices A Set of all vertices in the graph.
     * @param edges A Set of all Edges in the graph.
     * @param neighbors A function that takes in an edge, and returns the unordered pair of nodes
     *                  that the edge connects.
     * @param <T> The Object that represents one Node in the graph.
     * @param <U> The Object that represents one Edge in the graph.
     * @return The graph represented as a mapping of nodes to a set of nodes that it is directly
     *         connected to.
     */
    public static <T, U> Map<T, Set<T>> constructAdjacencyList(
        Set<T> vertices, Set<U> edges, Function<U, UnorderedPair<T>> neighbors) {
        Map<T, Set<T>>
            adjacencyList = new HashMap<>();

        // Add all vertices
        for (T vertex : vertices) {
            adjacencyList.put(vertex, new HashSet<>());
        }

        // For each connection, add node to adjacency set of other node and vice versa
        for (U edge : edges) {
            UnorderedPair<T> endpoints = neighbors.apply(edge);
            adjacencyList.get(endpoints.first).add(endpoints.second);
            adjacencyList.get(endpoints.second).add(endpoints.first);
        }
        return adjacencyList;
    }
}
