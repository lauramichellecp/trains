package map;

import java.util.Objects;
import utils.OrderedPair;

/**
 * Represents the width and height dimensions of a game board for the game Trains.
 * <p>
 * The width and height must both be in the range [10, 800].
 */
public class MapDimensions extends OrderedPair<Integer> {

    /**
     * Refers to the minimum and maximum number of pixels allowed for given map dimensions.
     */
    public static int MIN_DIMENSION = 10;
    public static int MAX_DIMENSION = 800;

    /**
     * Constructs the MapDimensions and enforces that its width and height are in [10, 800].
     *
     * @param width  The width of the map
     * @param height THe height of the map
     * @throws NullPointerException if either Width or Height is null.
     */
    public MapDimensions(Integer width, Integer height) throws NullPointerException {
        super(width, height);
        assertWithinValidRange(width, "width");
        assertWithinValidRange(height, "height");
    }

    /**
     * Constructs a new MapDimensions from the given one with references to the original width and
     * height.
     *
     * @param toClone containing the elements to construct another utils.MapDimensions from.
     * @throws NullPointerException if either the given utils.MapDimensions or either element is
     *                              null.
     */
    public MapDimensions(MapDimensions toClone) throws NullPointerException {
        this(Objects.requireNonNull(toClone).first, Objects.requireNonNull(toClone).second);
    }

    /**
     * Asserts that the given integer is in the valid range for a dimension of a game map.
     *
     * @param num           The potential
     * @param dimensionType The type of the dimension, usually "width" or "height". Used in error
     *                      reporting.
     * @throws IllegalArgumentException if num is less than the minimum dimension or greater than
     *                                  the maximum dimension
     */
    private void assertWithinValidRange(Integer num, String dimensionType) {
        if (num < MIN_DIMENSION) {
            throw new IllegalArgumentException(
                String.format("Dimension [%s] must be at least [%d].",
                    dimensionType, MIN_DIMENSION));
        }

        if (num > MAX_DIMENSION) {
            throw new IllegalArgumentException(
                String.format("Dimension [%s] must be at most [%d].", dimensionType,
                    MAX_DIMENSION));
        }
    }

    /**
     * Gets the width dimension of this map.
     *
     * @return The width of this map
     */
    public Integer getWidth() {
        return this.first;
    }

    /**
     * Gets the height dimension of this map.
     *
     * @return The height of this map
     */
    public Integer getHeight() {
        return this.second;
    }
}
