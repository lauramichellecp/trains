package map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Objects;
import map.MapDimensions;
import org.junit.jupiter.api.Test;

public class TestMapDimensions {

    /**
     * Tests various invalid and valid constructions of MapDimensions
     */
    @Test
    public void testConstructors() {
        try {
            MapDimensions p = new MapDimensions(null, 100);
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            MapDimensions p = new MapDimensions(100, null);
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            MapDimensions p = new MapDimensions(null);
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            MapDimensions p = new MapDimensions(new MapDimensions(null, 100));
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            MapDimensions p = new MapDimensions(new MapDimensions(1, 100));
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            MapDimensions p = new MapDimensions(new MapDimensions(100, 1));
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            MapDimensions p = new MapDimensions(new MapDimensions(1000, 100));
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            MapDimensions p = new MapDimensions(new MapDimensions(100, 1000));
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        MapDimensions success1 = new MapDimensions(200, 300);
        MapDimensions success2 = new MapDimensions(success1);
    }

    @Test
    public void testHashCode() {
        Integer a = 100;
        Integer b = 200;
        MapDimensions pair = new MapDimensions(a, b);
        MapDimensions reversedPair = new MapDimensions(b, a);
        // Equality means equality of hashcode
        assertNotEquals(pair.hashCode(), reversedPair.hashCode());
        // Hashcode from the element hashcodes
        assertEquals(pair.hashCode(), Objects.hash(a,b));
    }

    @Test
    public void testEquals() {
        Integer a = 100;
        Integer b = 200;
        MapDimensions pair = new MapDimensions(a, b);
        MapDimensions samePair = new MapDimensions(100, 200);
        MapDimensions reversedPair = new MapDimensions(b, a);
        assertEquals(pair, pair);
        // Tests that .equals is used on elements
        assertEquals(pair, samePair);
        // Order matters
        assertNotEquals(pair, reversedPair);
        // Null and other types don't break
        assertNotEquals(null, pair);
    }

    @Test
    public void testReverse() {
        Integer a = 100;
        MapDimensions pair = new MapDimensions(a, 200);
        MapDimensions reversedPair = new MapDimensions(200, a);
        assertEquals(pair.reverse(), reversedPair);
        assertEquals(pair.reverse().reverse(), pair);

        // Reverse gives a new OrderedPair
        assertNotSame(pair, pair.reverse().reverse());

        // But still has references to original elements
        assertSame(pair.first, pair.reverse().reverse().first);
    }
}
