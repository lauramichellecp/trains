package utils;

import map.ICity;
import map.IRailConnection;

/**
 * Utility methods for comparing basic game data definitions for use by strategies and other
 * tie-breakers.
 */
public class ComparatorUtils {

    /**
     * Compares the two rail connections according to the following rules, treated like
     * railConnection1.compareTo(railConnection2).
     *
     * <p>First, the pair of endpoint cities are checked using
     * {@link #lexicographicCompareUnorderedPair(UnorderedPair, UnorderedPair)}.
     *
     * <p>If this does not yield an ordering, then the lengths are compared such that smaller
     * length is earlier in ordering.
     *
     * <p>If length fails to break the tie, then finally the names of the connection colors are
     * compared lexicographically.
     *
     * @param railConnection1 the first rail connection.
     * @param railConnection2 the second rail connection.
     * @return an int indicating the comparison between the rail connections, where negative means
     * railConnection1 < railConnection2, 0 means they are equal, and positive means railConnection1
     * > railConnection2.
     */
    public static int lexicographicCompareConnection(
        IRailConnection railConnection1, IRailConnection railConnection2) {
        int cityNameComparison =
            lexicographicCompareUnorderedPair(railConnection1.getCities(),
                railConnection2.getCities());
        if (cityNameComparison != 0) {
            return cityNameComparison;
        }

        if (railConnection1.getLength() != railConnection2.getLength()) {
            return railConnection1.getLength() - railConnection2.getLength();
        }

        return railConnection1.getColor().name().compareTo(railConnection2.getColor().name());
    }

    /**
     * Compares the two pairs of cities lexicographically according to the names of cities.
     *
     * <p>A pair of cities X is lexicographically before a pair of cities 'Y' if the earliest city
     * name in X precedes the earliest city name in 'Y'; in the case of a tie, the latest city names
     * of the pairs are compared.
     *
     * @param pair1 the first pair.
     * @param pair2 the second pair.
     * @return an int corresponding to conventional interpretation of pair1.compareTo(pair2) using
     * the above rules.
     */
    public static int lexicographicCompareUnorderedPair(UnorderedPair<ICity> pair1,
                                                        UnorderedPair<ICity> pair2) {
        OrderedPair<ICity> orderedDestination1 = fromUnordered(pair1);
        OrderedPair<ICity> orderedDestination2 = fromUnordered(pair2);

        int firstCityComparison = lexicographicCompareCity(orderedDestination1.first,
            orderedDestination2.first);
        return firstCityComparison != 0
            ? firstCityComparison
            : lexicographicCompareCity(orderedDestination1.second, orderedDestination2.second);
    }

    /**
     * Constructs an ordering of the pair of cities using lexicographic order. The left element of
     * the unordered pair is the first element of the resulting pair iff it is lexicographically <
     * or = to the right element.
     *
     * @param unorderedCityPair the unordered pair of cities.
     * @return a lexicographically ordered pairing of the cities.
     */
    public static OrderedPair<ICity> fromUnordered(UnorderedPair<ICity> unorderedCityPair) {
        if (lexicographicCompareCity(unorderedCityPair.first, unorderedCityPair.second) > 0) {
            return new OrderedPair<>(unorderedCityPair.second, unorderedCityPair.first);
        } else {
            return new OrderedPair<>(unorderedCityPair.first, unorderedCityPair.second);
        }
    }

    /**
     * Returns comparison indicating lexicographic order between the name of the cities. < 0 if
     * city1 < city2, 0 if city1 = city2, and > 0 if city1 > city2.</>
     *
     * @param city1 the first city.
     * @param city2 the second city.
     * @return the result of city1.getName().compareTo(city2.getName()).
     */
    public static int lexicographicCompareCity(ICity city1, ICity city2) {
        return city1.getName().compareTo(city2.getName());
    }
}
