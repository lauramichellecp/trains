package map;

import java.util.Objects;

import utils.UnorderedPair;

/**
 * Represents one connection between two distinct cities on the game board in a game of Trains.
 */
public class RailConnection implements IRailConnection {
    private final UnorderedPair<ICity> cities;
    private final int length;
    private final RailColor color;

    /**
     * Constructs a map.RailConnection from the distinct cities at its end points, its length, and its Color.
     *
     * @param cities An UnorderedPair of cities that this RailConnection connects.
     * @param length The Length of this RailConnection in segments on the game board. It must be one
     *               of (3, 4, 5).
     * @param color The Color of this RailConnection on the game board. It must be one of the valid
     *              colors defined in RailColor.java.
     * @throws IllegalArgumentException if the cities are the same or the length is not one of (3, 4, 5).
     */
    public RailConnection(UnorderedPair<ICity> cities, int length, RailColor color) throws IllegalArgumentException {
        Objects.requireNonNull(cities);
        ensureValidCities(cities);
        ensureValidLength(length);

        this.cities = new UnorderedPair<>(cities);
        this.length = length;
        this.color = color;
    }

    /**
     * Gets the utils.UnorderedPair of cities that this map.RailConnection connects.
     *
     * @return The utils.UnorderedPair of cities that this map.RailConnection connects.
     */
    public UnorderedPair<ICity> getCities() {
        return new UnorderedPair<>(this.cities);
    }

  /**
   * Determines whether this IRailConnection and the other connect the same two cities.
   *
   * @param other the other IRailConnection to query.
   * @return true if the pair of cities are the same, false if not.
   */
  public boolean sameRailConnection(IRailConnection other) {
        return this.cities.equals(other.getCities())
            && this.color.equals(other.getColor());
    }

    /**
     * Gets the length of this map.RailConnection in segments. It will be one of (3, 4, 5).
     *
     * @return The length of this map.RailConnection.
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets the color of this RialConnection.
     *
     * @return The color of this map.RailConnection.
     */
    public RailColor getColor() {
        return color;
    }

    /**
     * Validates that a given pair of cities do not have the same name. A map.RailConnection must not
     * connect the same city to itself, so it must not connect two cities that have the same name.
     *
     * @param cities The pair of cities that could be connected by a map.RailConnection.
     */
    private static void ensureValidCities(UnorderedPair<ICity> cities) {
        if (cities.first.sameName(cities.second)) {
            throw new IllegalArgumentException(
                "A Rail Path must connect two cities with different names.");
        }
    }

    /**
     * Validates that a given int is a valid length for a map.RailConnection.
     * In the game of Trains, all RailConnections must have a length that is one of (3, 4, 5).
     *
     * @param length The int the could be a length of a map.RailConnection.
     */
    private static void ensureValidLength(int length) {
        if (length < 3 || length > 5) {
            throw new IllegalArgumentException(
                "The length of a rail path must be one of (3, 4, 5).");
        }
    }

    /**
     * Overrides hashCode to use the map.RailConnection's endpoints and color.
     *
     * @return A hash of the map.RailConnection's endpoints and color.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.cities, this.color);
    }

    /**
     * Override equals to compare the map.RailConnection's endpoints and color.
     *
     * @param obj The other object to compare to.
     * @return Whether this map.RailConnection is equal to the other object.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof RailConnection)) {
            return false;
        }

        RailConnection otherRailConnection = (RailConnection) obj;
        return this.sameRailConnection(otherRailConnection);
    }
}