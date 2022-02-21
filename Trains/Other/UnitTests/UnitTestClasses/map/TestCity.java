package map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import map.City;
import org.junit.jupiter.api.Test;
import utils.OrderedPair;
//TODO: test invalid relativeX and relativeY
/**
 * A Set of unit tests for the map.City class.
 */
public class TestCity {
    @Test
    public void testSameNameMatch() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("Boston", 0.2, 0.6);

        assertTrue(city1.sameName(city2));
        assertTrue(city2.sameName(city1));
    }

    @Test
    public void testSameNameSameCity() {
        City city1 = new City("Boston", 0.5, 0.5);

        assertTrue(city1.sameName(city1));
    }

    @Test
    public void testSameNameDifferent() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("New York", 0.2, 0.6);

        assertFalse(city1.sameName(city2));
        assertFalse(city2.sameName(city1));
    }

    @Test
    public void testEqualsSameName() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("Boston", 0.2, 0.6);

        assertEquals(city1, city2);
        assertEquals(city2, city1);
    }

    @Test
    public void testEqualsDifferentName() {
        City city1 = new City("Boston", 0.5, 0.5);

        assertEquals(city1, city1);
    }

    @Test
    public void testEqualsDifferent() {
        City city1 = new City("Boston", 0.5, 0.5);
        City city2 = new City("New York", 0.2, 0.6);

        assertNotEquals(city1, city2);
        assertNotEquals(city2, city1);
    }

    @Test
    public void testGetNameBoston() {
        City city1 = new City("Boston", 0.5, 0.5);

        assertEquals("Boston", city1.getName());
    }

    @Test
    public void testGetNameNewYork() {
        City city1 = new City("New York", 0.2, 0.6);

        assertEquals("New York", city1.getName());
    }

    @Test
    public void testGetRelativePositionBoston() {
        City city1 = new City("Boston", 0.5, 0.5);

        assertEquals(new OrderedPair<>(0.5, 0.5), city1.getRelativePosition());
    }

    @Test
    public void testGetRelativePositionNewYork() {
        City city1 = new City("New York", 0.2, 0.6);

        assertEquals(new OrderedPair<>(0.2, 0.6), city1.getRelativePosition());
    }
}
