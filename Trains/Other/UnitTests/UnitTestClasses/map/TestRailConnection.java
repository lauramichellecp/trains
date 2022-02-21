package map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import map.City;
import map.RailColor;
import map.RailConnection;
import org.junit.jupiter.api.Test;
import utils.UnorderedPair;

//TODO: Add tests for bad construction
public class TestRailConnection {
    @Test
    public void testGetCities() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("New York", 0.2, 0.6);

        RailConnection rail1 = new RailConnection(
            new UnorderedPair<>(city1, city2), 3, RailColor.BLUE);

        assertEquals(rail1.getCities(), new UnorderedPair<>(city1, city2));
    }

    @Test
    public void testGetCitiesRail2() {
        City city3 = new City("LA", 0.1, 0.1);
        City city4 = new City("Toronto", 0.7, 0.9);

        RailConnection rail2 = new RailConnection(
            new UnorderedPair<>(city3, city4), 5, RailColor.RED);

        assertEquals(rail2.getCities(), new UnorderedPair<>(city3, city4));
    }

    @Test
    public void testGetLength() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("New York", 0.2, 0.6);

        RailConnection rail1 = new RailConnection(
            new UnorderedPair<>(city1, city2), 3, RailColor.BLUE);

        assertEquals(rail1.getLength(), 3);
    }

    @Test
    public void testGetLengthRail2() {
        City city3 = new City("LA", 0.1, 0.1);
        City city4 = new City("Toronto", 0.7, 0.9);

        RailConnection rail2 = new RailConnection(
            new UnorderedPair<>(city3, city4), 5, RailColor.RED);

        assertEquals(rail2.getLength(), 5);
    }

    @Test
    public void testGetColor() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("New York", 0.2, 0.6);

        RailConnection rail1 = new RailConnection(
            new UnorderedPair<>(city1, city2), 3, RailColor.BLUE);

        assertEquals(rail1.getColor(), RailColor.BLUE);
    }

    @Test
    public void testGetColorRail2() {
        City city3 = new City("LA", 0.1, 0.1);
        City city4 = new City("Toronto", 0.7, 0.9);

        RailConnection rail2 = new RailConnection(
            new UnorderedPair<>(city3, city4), 5, RailColor.RED);

        assertEquals(rail2.getColor(), RailColor.RED);
    }

    @Test
    public void testEqualsMatch() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("New York", 0.2, 0.6);

        RailConnection rail1 = new RailConnection(
            new UnorderedPair<>(city1, city2), 3, RailColor.BLUE);

        RailConnection rail2 = new RailConnection(
            new UnorderedPair<>(city1, city2), 5, RailColor.BLUE);

        assertEquals(rail1, rail2);
        assertEquals(rail2, rail1);
        assertTrue(rail1.sameRailConnection(rail2));
    }

    @Test
    public void testEqualsSameRail() {
        City city3 = new City("LA", 0.1, 0.1);
        City city4 = new City("Toronto", 0.7, 0.9);

        RailConnection rail2 = new RailConnection(
            new UnorderedPair<>(city3, city4), 5, RailColor.RED);
        assertTrue(rail2.sameRailConnection(rail2));
        assertEquals(rail2, rail2);
    }

    @Test
    public void testEqualsDifferentRail() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("New York", 0.2, 0.6);

        RailConnection rail1 = new RailConnection(
            new UnorderedPair<>(city1, city2), 3, RailColor.BLUE);

        City city3 = new City("LA", 0.1, 0.1);
        City city4 = new City("Toronto", 0.7, 0.9);

        RailConnection rail2 = new RailConnection(
            new UnorderedPair<>(city3, city4), 5, RailColor.RED);

        assertNotEquals(rail1, rail2);
        assertNotEquals(rail2, rail1);
        assertFalse(rail2.sameRailConnection(rail1));
        assertFalse(rail1.sameRailConnection(rail2));
    }
}
