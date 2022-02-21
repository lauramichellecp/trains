package utils;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import map.City;
import map.ICity;
import map.IRailConnection;
import map.RailColor;
import map.RailConnection;
import org.junit.jupiter.api.Test;
import utils.GraphUtility;
import utils.UnorderedPair;

/**
 * A Set of unit tests for the utils.GraphUtility class.
 */
public class TestGraphUtility {
    @Test
    public void testConstructAdjacencyListConnectedGraph() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("New York", 0.2, 0.6);
        City city3 = new City("LA", 0.1, 0.1);
        City city4 = new City("Toronto", 0.7, 0.9);
        Set<ICity> cities = new HashSet<>();
        cities.add(city1);
        cities.add(city2);
        cities.add(city3);
        cities.add(city4);

        RailConnection rail1 = new RailConnection(new UnorderedPair<>(city1, city2),
            3, RailColor.BLUE);
        RailConnection rail2 = new RailConnection(new UnorderedPair<>(city4, city2),
            3, RailColor.WHITE);
        RailConnection rail3 = new RailConnection(new UnorderedPair<>(city1, city3),
            3, RailColor.GREEN);
        RailConnection rail4 = new RailConnection(new UnorderedPair<>(city3, city4),
            3, RailColor.RED);
        Set<IRailConnection> rails = new HashSet<>();
        rails.add(rail1);
        rails.add(rail2);
        rails.add(rail3);
        rails.add(rail4);

        Map<ICity, Set<ICity>> expectedAdjacencyList = new HashMap<>();
        for (ICity city : cities) {
            expectedAdjacencyList.put(city, new HashSet<>());
        }

        for (IRailConnection rail : rails) {
            UnorderedPair<ICity> endpoints = rail.getCities();
            expectedAdjacencyList.get(endpoints.first).add(endpoints.second);
            expectedAdjacencyList.get(endpoints.second).add(endpoints.first);
        }

        assertEquals(expectedAdjacencyList,
            GraphUtility.constructAdjacencyList(cities, rails, IRailConnection::getCities));
    }

    @Test
    public void testConstructAdjacencyListTwoComponentConnectedGraph() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("New York", 0.2, 0.6);
        City city3 = new City("LA", 0.1, 0.1);
        City city4 = new City("Toronto", 0.7, 0.9);
        Set<ICity> cities = new HashSet<>();
        cities.add(city1);
        cities.add(city2);
        cities.add(city3);
        cities.add(city4);

        RailConnection rail1 = new RailConnection(new UnorderedPair<>(city1, city2),
            3, RailColor.BLUE);
        RailConnection rail2 = new RailConnection(new UnorderedPair<>(city3, city4),
            3, RailColor.WHITE);
        Set<IRailConnection> rails = new HashSet<>();
        rails.add(rail1);
        rails.add(rail2);

        Map<ICity, Set<ICity>> expectedAdjacencyList = new HashMap<>();
        for (ICity city : cities) {
            expectedAdjacencyList.put(city, new HashSet<>());
        }

        for (IRailConnection rail : rails) {
            UnorderedPair<ICity> endpoints = rail.getCities();
            expectedAdjacencyList.get(endpoints.first).add(endpoints.second);
            expectedAdjacencyList.get(endpoints.second).add(endpoints.first);
        }

        assertEquals(expectedAdjacencyList,
            GraphUtility.constructAdjacencyList(cities, rails, IRailConnection::getCities));
    }

    @Test
    public void testGetConnectedPairsConnectedGraph() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("New York", 0.2, 0.6);
        City city3 = new City("LA", 0.1, 0.1);
        City city4 = new City("Toronto", 0.7, 0.9);
        Set<ICity> cities = new HashSet<>();
        cities.add(city1);
        cities.add(city2);
        cities.add(city3);
        cities.add(city4);

        RailConnection rail1 = new RailConnection(new UnorderedPair<>(city1, city2),
            3, RailColor.BLUE);
        RailConnection rail2 = new RailConnection(new UnorderedPair<>(city4, city2),
            3, RailColor.WHITE);
        RailConnection rail3 = new RailConnection(new UnorderedPair<>(city1, city3),
            3, RailColor.GREEN);
        RailConnection rail4 = new RailConnection(new UnorderedPair<>(city3, city4),
            3, RailColor.RED);
        Set<IRailConnection> rails = new HashSet<>();
        rails.add(rail1);
        rails.add(rail2);
        rails.add(rail3);
        rails.add(rail4);


        Map<ICity, Set<ICity>> adjacencyList = GraphUtility.constructAdjacencyList(
            cities, rails, IRailConnection::getCities);

        Set<UnorderedPair<ICity>> expected_pairs = new HashSet<>();
        expected_pairs.add(new UnorderedPair<>(city1, city2));
        expected_pairs.add(new UnorderedPair<>(city1, city3));
        expected_pairs.add(new UnorderedPair<>(city1, city4));
        expected_pairs.add(new UnorderedPair<>(city2, city3));
        expected_pairs.add(new UnorderedPair<>(city2, city4));
        expected_pairs.add(new UnorderedPair<>(city3, city4));

        assertEquals(expected_pairs, GraphUtility.getConnectedPairs(adjacencyList));
    }

    @Test
    public void testGetConnectedPairsTwoComponentConnectedGraph() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("New York", 0.2, 0.6);
        City city3 = new City("LA", 0.1, 0.1);
        City city4 = new City("Toronto", 0.7, 0.9);
        Set<ICity> cities = new HashSet<>();
        cities.add(city1);
        cities.add(city2);
        cities.add(city3);
        cities.add(city4);

        RailConnection rail1 = new RailConnection(new UnorderedPair<>(city1, city2),
            3, RailColor.BLUE);
        RailConnection rail2 = new RailConnection(new UnorderedPair<>(city3, city4),
            3, RailColor.WHITE);
        Set<IRailConnection> rails = new HashSet<>();
        rails.add(rail1);
        rails.add(rail2);

        Map<ICity, Set<ICity>> adjacencyList = GraphUtility.constructAdjacencyList(
            cities, rails, IRailConnection::getCities);

        Set<UnorderedPair<ICity>> expected_pairs = new HashSet<>();
        expected_pairs.add(new UnorderedPair<>(city1, city2));
        expected_pairs.add(new UnorderedPair<>(city4, city3));

        assertEquals(expected_pairs, GraphUtility.getConnectedPairs(adjacencyList));
    }
}
