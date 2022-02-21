package utils;

import java.util.Objects;

/**
 * An UnorderedPair is an OrderedPair of 2 non-null elements for which order does not matter in equality
 * checking.
 *
 * <p>If the individual elements are immutable, then the utils.UnorderedPair is also immutable.
 *
 * @param <T> the type of the elements.
 */
public class UnorderedPair<T> extends APair<T> {

  /**
   * Constructs this from the two given elements, parameter names correspond to fields.
   *
   * @param first one of the elements.
   * @param second another element.
   * @throws NullPointerException if either element is null.
   */
  public UnorderedPair(T first, T second) {
    super(first, second);
  }

  /**
   * Constructs this from the given unordered pair, preserving parameter name and references to
   * original elements.
   *
   * @param toClone the unordered pair to clone.
   * @throws NullPointerException if either element is null.
   */
  public UnorderedPair(UnorderedPair<T> toClone) {
    this(Objects.requireNonNull(toClone).first, Objects.requireNonNull(toClone).second);
  }

  /**
   * Creates a new OrderedPair from this UnorderedPair.
   * @return the OrderedPair with the same fields.
   */
  public OrderedPair<T> toOrdered() {
    return new OrderedPair<>(this.first, this.second);
  }

  /**
   * Computes the hashcode for this unordered pair.
   *
   * @return the sum of the hashcodes of the individual elements.
   */
  @Override
  public int hashCode() {
    return first.hashCode() + second.hashCode();
  }

  /**
   * Determines whether this unordered pair is equal to another object, relying on equality for the
   * type of element in this pair.
   *
   * @param other the other object to compare to.
   * @return a boolean indicating whether the other object is an utils.UnorderedPair that contains the
   *     same elements as this.
   */
  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }

    if (!(other instanceof UnorderedPair<?>)) {
      return false;
    }

    UnorderedPair<?> otherUnorderedPair = (UnorderedPair<?>) other;
    OrderedPair<?> otherOrderedPair = otherUnorderedPair.toOrdered();
    OrderedPair<T> thisOrderedPair = this.toOrdered();

    return thisOrderedPair.equals(otherOrderedPair) ||
            thisOrderedPair.equals(otherOrderedPair.reverse());
  }
}
