package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utils.ComparatorUtils.lexicographicCompareCity;
import static utils.ComparatorUtils.lexicographicCompareUnorderedPair;
import static utils.ComparatorUtils.fromUnordered;
import static utils.ComparatorUtils.lexicographicCompareConnection;

import map.City;
import map.ICity;
import map.IRailConnection;
import map.RailColor;
import map.RailConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.OrderedPair;
import utils.UnorderedPair;

public class TestComparatorUtils {

    ICity Boston;
    ICity NewBoston;
    ICity NewYork;
    ICity Chicago;

    IRailConnection BostonNewYorkRed4;
    IRailConnection BostonNewYorkRed3;
    IRailConnection BostonNewYorkBlue3;
    IRailConnection BostonChicago;
    IRailConnection ChicagoNewYork;

    @BeforeEach
    public void Setup() {
        this.Boston = new City("Boston", 0.0, 0.0);
        this.NewBoston = new City("Boston", 0.2, 0.5);
        this.NewYork = new City("NewYork", 0.7, 0.1);
        this.Chicago = new City("Chicago", 0.1, 0.1);

        this.BostonNewYorkRed4 = new RailConnection(new UnorderedPair<>(Boston, NewYork), 4,
            RailColor.RED);
        this.BostonNewYorkRed3 = new RailConnection(new UnorderedPair<>(Boston, NewYork), 3,
            RailColor.RED);
        this.BostonNewYorkBlue3 = new RailConnection(new UnorderedPair<>(Boston, NewYork), 3,
            RailColor.BLUE);
        this.BostonChicago = new RailConnection(new UnorderedPair<>(Boston, Chicago), 3,
            RailColor.BLUE);
        this.ChicagoNewYork = new RailConnection(new UnorderedPair<>(Chicago, NewYork), 3,
            RailColor.BLUE);
    }

    @Test
    public void TestLexicographicCompareSameCities() {
        assertEquals(0, lexicographicCompareUnorderedPair(new UnorderedPair<>(this.Boston, this.NewYork),
            new UnorderedPair<>(this.Boston, this.NewYork)));
        assertEquals(0, lexicographicCompareUnorderedPair(new UnorderedPair<>(this.Boston, this.NewYork),
            new UnorderedPair<>(this.NewYork, this.Boston)));
    }

    @Test
    public void TestLexicographicCompareFirstDifferent() {
        UnorderedPair<ICity> pair1 = new UnorderedPair<>(this.Boston, this.NewYork);
        UnorderedPair<ICity> pair2 = new UnorderedPair<>(this.Chicago, this.NewYork);
        assertTrue(lexicographicCompareUnorderedPair(pair1, pair2) < 0);
        assertTrue(lexicographicCompareUnorderedPair(pair2, pair1) > 0);
    }

    @Test
    public void TestLexicographicCompareSecondDifferent() {
        UnorderedPair<ICity> pair1 = new UnorderedPair<>(this.Boston, this.Chicago);
        UnorderedPair<ICity> pair2 = new UnorderedPair<>(this.Boston, this.NewYork);
        assertTrue(lexicographicCompareUnorderedPair(pair1, pair2) < 0);
        assertTrue(lexicographicCompareUnorderedPair(pair2, pair1) > 0);
    }

    @Test
    public void TestICityCompareSameCity() {
        assertEquals(0, lexicographicCompareCity(this.Boston, this.Boston));
    }

    @Test
    public void TestICityCompareSameName() {
        assertEquals(0, lexicographicCompareCity(this.Boston, this.NewBoston));
    }

    @Test
    public void TestICityCompareDiffCity() {
        assertTrue(lexicographicCompareCity(this.Boston, this.NewYork) < 0);
        assertTrue(lexicographicCompareCity(this.NewYork, this.Boston) > 0);
    }

    @Test
    public void TestFromUnordered() {
        OrderedPair<ICity> orderedPair1 = fromUnordered(
            new UnorderedPair<>(this.Boston, this.NewYork));
        OrderedPair<ICity> orderedPair2 = fromUnordered(
            new UnorderedPair<>(this.NewYork, this.Boston));

        assertEquals(orderedPair1, orderedPair2);
    }

    @Test
    public void TestRailComparatorSameRail() {
        assertEquals(0, lexicographicCompareConnection(this.BostonNewYorkRed4, this.BostonNewYorkRed4));
    }

    @Test
    public void TestRailComparatorFirstDifferent() {
        assertTrue(lexicographicCompareConnection(this.BostonNewYorkBlue3, this.ChicagoNewYork) < 0);
        assertTrue(lexicographicCompareConnection(this.ChicagoNewYork, this.BostonNewYorkBlue3) > 0);
    }

    @Test
    public void TestRailComparatorSecondDifferent() {
        assertTrue(lexicographicCompareConnection(this.BostonChicago, this.BostonNewYorkBlue3) < 0);
        assertTrue(lexicographicCompareConnection(this.BostonNewYorkBlue3, this.BostonChicago) > 0);
    }

    @Test
    public void TestRailComparatorLength() {
        assertTrue(lexicographicCompareConnection(this.BostonNewYorkRed4, this.BostonNewYorkRed3) > 0);
        assertTrue(lexicographicCompareConnection(this.BostonNewYorkRed3, this.BostonNewYorkRed4) < 0);
    }

    @Test
    public void TestRailComparatorColor() {
        assertTrue(lexicographicCompareConnection(this.BostonNewYorkRed3, this.BostonNewYorkBlue3) > 0);
        assertTrue(lexicographicCompareConnection(this.BostonNewYorkBlue3, this.BostonNewYorkRed3) < 0);
    }
}
