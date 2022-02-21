package map;

import utils.UnorderedPair;

/**
 * Represents an undirected connection between two distinct cities with a particular length and color. An
 * map.IRailConnection should be immutable and should use sameRailConnection() for equality with other IRailConnections.
 */
public interface IRailConnection {

  /**
   * The distinct cities this connection connects.
   *
   * @return the distinct cities as an unordered pair.
   */
  UnorderedPair<ICity> getCities();

    /**
     * Determines whether this IRailConnection and the other connect the same two cities and are the
     * same color.
     * @param other the other IRailConnection to query.
     * @return true if the pair of cities and colors are the same, false if not.
     */
  boolean sameRailConnection(IRailConnection other);

  /**
   * The length of this connection in terms of the number of trains it uses.
   *
   * @return an integer that is 3,4, or 5.
   */
  int getLength();

  /**
   * Gets the color of the train card required to claim this connection.
   *
   * @return the color as a map.RailColor.
   */
  RailColor getColor();
}
