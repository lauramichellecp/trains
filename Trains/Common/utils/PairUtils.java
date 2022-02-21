package utils;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Utility class for integer and Vector2D coordinate calculations and conversions.
 */
public class PairUtils {

    /**
     * Returns a vector corresponding to the given integer coordinates.
     * @param coordinates the integer coordinates to convert.
     * @return a Vector2D of doubles (implicitly converting int to double)
     */
    public static Vector2D orderedPairToVect(OrderedPair<Integer> coordinates) {
        return new Vector2D(coordinates.first, coordinates.second);
    }

    /**
     * Returns a integer coordinate pair corresponding to the given vector coordinates.
     * @param coordinates the vector coordinates to convert.
     * @return an OrderedPair of integers (explicitly converting double to int)
     */
    public static OrderedPair<Integer> vectToOrderedPair(Vector2D coordinates) {
        return new OrderedPair<>((int) coordinates.getX(), (int) coordinates.getY());
    }

    /**
     * Returns the component-wise scaling of the given integer pair by the given double pair.
     * Result is explicitly converted to integer coordinates by flooring.
     * @param intOrderedPair the pair of integers to scale.
     * @param orderedPair2 the pair of doubles scaling the integers.
     * @return the scaled integer coordinates.
     */
    public static OrderedPair<Integer> scalePair(
            OrderedPair<Integer> intOrderedPair, OrderedPair<Double> orderedPair2) {
        return new OrderedPair<>(
            (int) (intOrderedPair.first * orderedPair2.first), (int) (intOrderedPair.second * orderedPair2.second));
    }
}
