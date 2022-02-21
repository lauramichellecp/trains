package utils;

import com.google.common.primitives.Chars;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestInitializationUtils {

  /**
   * Parses the given string representing a 2D (not necessarily square) list of alphanumeric
   * characters.
   *
   * <p>The first split occurs over spaces (' ') and the second split by characters.
   *
   * <p>For example, "abc e fg" -> "[[a,b,c],[e],[f,g]]"
   *
   * @param input
   * @return
   */
  private static ArrayList<ArrayList<Character>> parse2DList(String input) {
    List<String> firstSplit = Arrays.asList(input.split(" "));
    return firstSplit.stream()
        .map((str) -> new ArrayList<>(Chars.asList(str.toCharArray())))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private static ArrayList<Character> toCharList(String input) {
    return new ArrayList<>(Chars.asList(input.toCharArray()));
  }

  @Test
  public void testOrderedMaximumGroupingExceptions() {
    ArrayList<Character> l = new ArrayList<>();
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> InitializationUtils.orderedMaximumGrouping(l, 0, 5));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> InitializationUtils.orderedMaximumGrouping(l, 1, 0));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> InitializationUtils.orderedMaximumGrouping(l, -3, 5));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> InitializationUtils.orderedMaximumGrouping(l, 3, 4));

    Assertions.assertThrows(
        IllegalArgumentException.class, () -> InitializationUtils.orderedMaximumGrouping(l, 2, 8));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> InitializationUtils.orderedMaximumGrouping(toCharList("abcde"), 1, 5));
  }

  @Test
  public void testOrderedMaximumGrouping() {

    Assertions.assertEquals(
        parse2DList("abcd efgh"),
        InitializationUtils.orderedMaximumGrouping(toCharList("abcdefgh"), 1, 4));

    Assertions.assertEquals(
        parse2DList("abcd e"),
        InitializationUtils.orderedMaximumGrouping(toCharList("abcde"), 1, 4));

    // Group sizes
    int min = 2;
    int max = 8;

    // All games at max
    Assertions.assertEquals(
        parse2DList("abcdefgh ijklmnop"),
        InitializationUtils.orderedMaximumGrouping(toCharList("abcdefghijklmnop"), min, max));

    // One game not at max, no borrowing
    Assertions.assertEquals(
        parse2DList("abcdefgh ijklmnop qr"),
        InitializationUtils.orderedMaximumGrouping(toCharList("abcdefghijklmnopqr"), min, max));

    // Borrowing
    Assertions.assertEquals(
        parse2DList("abcdefgh ijklmno pq"),
        InitializationUtils.orderedMaximumGrouping(toCharList("abcdefghijklmnopq"), min, max));
  }
}
