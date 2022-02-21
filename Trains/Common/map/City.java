package map;

import java.util.Objects;
import java.util.regex.Pattern;

import utils.OrderedPair;

/**
 * Represents one City on the game board in a game of Trains.
 */
public class City implements ICity {

    private final String name;
    private final OrderedPair<Double> relativePosition;

    /**
     * Constructs a map.City from the city's name, and its location on the game board. The x and y
     * components of location are represented as a percent of the width and height of the game board
     * because an absolute position is meaningless when any visualization of the board will be
     * scaled.
     *
     * @param name The name of the city. eg. "Boston", or "New York"
     * @param relativeX The distance away from the the top left of the board in the x direction,
     *                  stored as a percent of the x dimension of the board.
     * @param relativeY The distance away from the the top left of the board in the y direction,
     *                  stored as a percent of the y dimension of the board.
     */
    public City(String name, double relativeX, double relativeY) {
        ensureValidCoordinate(relativeX, "x");
        ensureValidCoordinate(relativeY, "y");
        ensureValidCityName(name);

        this.name = name;
        this.relativePosition = new OrderedPair<>(relativeX, relativeY);
    }

    /**
     * Determines if another map.City has the same name as this map.City.
     *
     * @param otherCity The other city to compare names to.
     * @return Whether this city has the same name as another city.
     */
    public boolean sameName(ICity otherCity) {
        return this.name.equals(otherCity.getName());
    }

    /**
     * Gets the name of this map.City as a String.
     *
     * @return The name of this city.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the relative position of this city to the width and height of the game board as an
     * utils.OrderedPair.
     *
     * @return The relative position of this city on the game board.
     */
    @Override
    public OrderedPair<Double> getRelativePosition() {
        return new OrderedPair<>(this.relativePosition);
    }

    /**
     * Validates that a given double is a relative coordinate. A relative coordinate must be between
     * 0.0 and 1.0 inclusive, and represent a percent of another coordinate.
     *
     * For Example: 0.5 relativeX coordinate of a city represents that the city is located at 50% of
     * the board's x coordinate away from the top left of the board in the x direction.
     *
     * @param relativeCoordinate The potential relative coordinate to be validated.
     * @param coordinateName The name of the coordinate, usually x or y. Will be used in error
     *                       reporting for doubles that are not relative coordinates.
     */
    private static void ensureValidCoordinate(double relativeCoordinate, String coordinateName) {
        if (relativeCoordinate < 0.0 || relativeCoordinate > 1.0) {
            throw new IllegalArgumentException(
                String.format(
                    "The relative %s coordinate of the city must be in [0, 1]", coordinateName));
        }
    }

    /**
     * Validates that a given String is a valid name for a city. A valid name for a city matches the regular expression
     * "[a-zA-Z0-9\\ \\.\\,]+" and has at most 25 characters.
     *
     * @param name The potentially valid name for a city.
     */
    private void ensureValidCityName(String name) {
        Objects.requireNonNull(name);
        if(!(Pattern.matches("[a-zA-Z0-9 .,]+", name) && name.length() <= 25)) {
            throw new IllegalArgumentException("City name must be 25 or fewer alpha-numeric characters or space, dot, and comma.");
        }
    }

    /**
     * Override hashCode to only use the name of the city.
     * @return A hash generated from the name of the city.
     */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * Override equals to only compare the name of the city.
     *
     * @param obj The other object to compare to.
     * @return Whether this map.City is equal to the other object.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof City)) {
            return false;
        }

        ICity otherCity = (ICity) obj;
        return this.sameName(otherCity);
    }
}
