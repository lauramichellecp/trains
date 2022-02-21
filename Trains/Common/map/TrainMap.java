package map;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import utils.GraphUtility;
import utils.UnorderedPair;

/**
 * Represents the static game map of cities and connections between those cities.
 *
 * <p>Can be thought of as a simple graph where cities are vertices and rail connections are edges.
 *
 * <p>Cities must have unique names, at most one rail connection of the same color can connect two
 * cities, and rail connections cannot connect a city to itself.
 */
public class TrainMap implements ITrainMap {

    private final Set<ICity> cities;
    private final Set<IRailConnection> railConnections;
    private final MapDimensions dimensions;

    /**
     * Constructs this TrainMap from the given set of cities, defaults to a map size of 400 pixels
     * by 400 pixels.
     *
     * @param cities the cities of the map with unique names.
     * @param rails connections among the given set of cities.
     * @throws IllegalArgumentException if a rail connection includes non-existent cities, or
     *                                  connects to the same city.
     */
    public TrainMap(Set<ICity> cities, Set<IRailConnection> rails) throws IllegalArgumentException {
        this(cities, rails, new MapDimensions(400, 400));
    }

    /**
     * Constructs this TrainMap from the given set of cities and a width and height for the game
     * board.
     *
     * @param cities the cities of the map with unique names.
     * @param rails connections among the given set of cities.
     * @param dimensions The width and height of the game board.
     * @throws IllegalArgumentException if a rail connection includes non-existent cities, or connects
     *     to the same city.
     */
    public TrainMap(Set<ICity> cities, Set<IRailConnection> rails, MapDimensions dimensions) throws IllegalArgumentException {
        Objects.requireNonNull(cities);
        Objects.requireNonNull(rails);
        Objects.requireNonNull(dimensions);
        if (!allConnectionsValid(cities, rails)) {
            throw new IllegalArgumentException(
                "Rail connections contains paths to non-existent cities or self-connections.");
        }

        this.cities = new HashSet<>(cities);
        this.railConnections = new HashSet<>(rails);
        this.dimensions = dimensions;
    }

    /**
     * Determines if all the given rail connections correspond to cities in the given set with no
     * self-connections.
     *
     * @param cities the set of cities to be connected by the rails.
     * @param rails  the rail connections among the cities to check validity on.
     * @return a boolean that is true if every given IRailConnection has distinct endpoint cities
     * that are in the given set of ICity, false otherwise.
     */
    private static boolean allConnectionsValid(Set<ICity> cities, Set<IRailConnection> rails) {
        for (IRailConnection path : rails) {
            UnorderedPair<ICity> endpoints = path.getCities();
            boolean selfConnection = endpoints.first.sameName(endpoints.second);
            boolean endpointsInCities =
                cities.contains(endpoints.first) && cities.contains(endpoints.second);
            if (selfConnection || !endpointsInCities) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the set of cities.
     *
     * @return a defensive copy of this map's cities.
     */
    public Set<ICity> getCities() {
        return new HashSet<>(this.cities);
    }

    /**
     * Gets the set of rail connections.
     *
     * @return a defensive copy of this map's rail connections.
     */
    public Set<IRailConnection> getRailConnections() {
        return new HashSet<>(this.railConnections);
    }

    /**
     * Gets the names of all the cities.
     *
     * @return a set of the names of all the cities in this map.
     */
    public Set<String> getCityNames() {
        // Don't reuse getCities() in case a subclass overwrites that
        return new HashSet<>(this.cities).stream().map((ICity::getName))
            .collect(Collectors.toSet());
    }

    /**
     * Return the city from the name
     * @param name
     * @return
     */
    public Optional<ICity> getCityFromName(String name) {
        for(ICity city : cities) {
            if(city.getName().equals(name)) {
                return Optional.of(city);
            }
        }
        return Optional.empty();
    }

    /**
     * Calculates every possible pair of cities that can be connected by any series of rail
     * connections on this map.
     *
     * @return a set of destinations, which are unordered pairs indicating the two endpoint cities.
     */
    public Set<UnorderedPair<ICity>> getAllPossibleDestinations() {
        Map<ICity, Set<ICity>> adjacencyList =
            GraphUtility.constructAdjacencyList(
                this.cities, this.railConnections, IRailConnection::getCities);
        return GraphUtility.getConnectedPairs(adjacencyList);
    }

    /**
     * Gets the dimensions of this map.
     *
     * @return a defensive copy of this map's MapDimensions.
     */
    public MapDimensions getMapDimension() {
        return new MapDimensions(this.dimensions);
    }
}
