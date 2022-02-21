package map;

import java.util.Set;
import java.util.Optional;

import utils.UnorderedPair;

/**
 * Represents the static, unchanging game-board for a game of Trains with cities and undirected
 * connections between those cities. Supports querying and some calculation operations. There should
 * not be any duplicate city names or more than one connection of the same color between two cities.
 *
 * <p>An ITrainMap should be immutable.
 */
public interface ITrainMap {

  /**
   * All of the cities in the map.
   *
   * @return a defensively-copied set of all the cities.
   */
  Set<ICity> getCities();

  /**
   * All of the connections that exist among the cities in the map.
   *
   * @return a defensively-copied set of all the rail connections.
   */
  Set<IRailConnection> getRailConnections();

  /**
   * The names of all of the cities in the map.
   *
   * @return a set of all the city names.
   */
  Set<String> getCityNames();

  /**
   * Calculates every possible pair of cities that can be connected by any series of rail
   * connections on this map.
   *
   * @return a set of destinations, which are unordered pairs indicating the two endpoint cities.
   */
  Set<UnorderedPair<ICity>> getAllPossibleDestinations();

    /**
     * Get teh dimensions of this map in pixels.
     * @return a MapDimensions object, an ordered pair representing the width and height of this map in pixels.
     */
  MapDimensions getMapDimension();

  Optional<ICity> getCityFromName(String name);
}
