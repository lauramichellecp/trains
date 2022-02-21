package map;

import utils.OrderedPair;

/**
 * Represents a single city on the board with a name and relative position. An ICity should be
 * immutable, and it should be equal to any other ICity with the same name.
 */
public interface ICity {

  /**
   * Gets the name of the city.
   *
   * @return the String name.
   */
  String getName();

  /**
   * Determines if this ICity has the same name as the given map.ICity.
   *
   * @param other the other map.ICity to compare names.
   * @return a boolean indicating whether this and the other map.ICity have the same name.
   */
  boolean sameName(ICity other);

  /**
   * Gets the relative position of this city as x,y coordinates for a rectangular board. (0, 0) is
   * the top-left and (1,1) is the bottom-right.
   *
   * @return an ordered pair of doubles in the range [0, 1]
   */
  OrderedPair<Double> getRelativePosition();
}
