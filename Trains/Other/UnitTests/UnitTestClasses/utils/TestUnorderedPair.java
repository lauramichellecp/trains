package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;
import utils.OrderedPair;
import utils.UnorderedPair;

public class TestUnorderedPair {

    /**
     * Tests various invalid and valid constructions of UnorderedPair
     */
    @Test
    public void testConstructors() {
        try {
            UnorderedPair<Integer> p = new UnorderedPair<>(null, 1);
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            UnorderedPair<Integer> p = new UnorderedPair<>(1, null);
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            UnorderedPair<Integer> p = new UnorderedPair<>(null);
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            UnorderedPair<Integer> p = new UnorderedPair<>(new UnorderedPair<Integer>(null, 1));
            fail();
        } catch (NullPointerException ignored) {
        }
        UnorderedPair<Integer> success1 = new UnorderedPair<>(2, 3);
        UnorderedPair<Integer> success2 = new UnorderedPair<>(success1);
    }

    /**
     * Tests that if utils.UnorderedPair elements are mutated, the references are carried to clones.
     */
    @Test
    public void testMutability() {
        UnorderedPair<StringBuilder> mutable = new UnorderedPair<>(new StringBuilder(), new StringBuilder());
        UnorderedPair<StringBuilder> cloneOfMutable = new UnorderedPair<>(mutable);
        mutable.first.append("mutation");
        assertEquals(mutable.first.toString(), cloneOfMutable.first.toString());
    }

    @Test
    public void testHashCode() {
        String a = "a";
        String b = "b";
        UnorderedPair<String> pair = new UnorderedPair<>(a, b);
        UnorderedPair<String> samePair = new UnorderedPair<>(b, a);
        // Equality means equality of hashcode
        assertEquals(pair.hashCode(), samePair.hashCode());
        // Hashcode from the sum of component hashcodes
        assertEquals(pair.hashCode(), a.hashCode() + b.hashCode());
    }

    @Test
    public void testEquals() {
        String a = "a";
        String b = "b";
        UnorderedPair<String> pair = new UnorderedPair<>(a, b);
        UnorderedPair<String> samePair = new UnorderedPair<>("b", "a");
        assertEquals(pair, pair);
        // Tests that order doesn't matter and that .equals is used on elements
        assertEquals(pair, samePair);
        // Null and other types don't break
        assertNotEquals(null, pair);
        assertNotEquals(pair, new OrderedPair<>(b, a));
        assertNotEquals(new OrderedPair<>(a, b), pair);

    }
}
