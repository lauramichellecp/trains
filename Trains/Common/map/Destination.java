package map;

import utils.ComparatorUtils;
import utils.UnorderedPair;

/**
 * A destination is an unordered pair of distinct cities.
 */
public class Destination extends UnorderedPair<ICity> implements Comparable<Destination> {

    /**
     * Constructs this from the given distinct cities. Order does not matter.
     *
     * @param left  one city.
     * @param right another city.
     * @throws IllegalArgumentException if the two cities have the same name.
     */
    public Destination(ICity left, ICity right) throws IllegalArgumentException {
        super(left, right);

        if (left.sameName(right)) {
            throw new IllegalArgumentException(
                "Two cities in a destination must have different names");
        }
    }

    /**
     * Constructs this destination from the given UnorderedPair of city, respecting the order.
     * @param cityPair the pair of city comprising this destination.
     */
    public Destination(UnorderedPair<ICity> cityPair) {
        this(cityPair.first, cityPair.second);
    }

    /**
     * Compares Destinations using pair-wise comparison of city names according to {@link
     * ComparatorUtils#lexicographicCompareUnorderedPair(UnorderedPair, UnorderedPair)}
     *
     * @param otherDestination the other Destination to compare to.
     * @return < 0 if this destination is lexicographically before the other destination, >0 if the
     * opposite is true, and 0 if both are equal.</>
     */
    @Override
    public int compareTo(Destination otherDestination) {
        return ComparatorUtils.lexicographicCompareUnorderedPair(this, otherDestination);
    }
}
