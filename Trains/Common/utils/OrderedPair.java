package utils;

import java.util.Objects;

/**
 * Represents a collection of 2 non-null elements of a particular type in a specified order.
 *
 * If the individual elements are immutable, then the utils.OrderedPair is also immutable.
 *
 * @param <T> The type of an individual element.
 */
public class OrderedPair<T> extends APair<T> {

  /**
   * Constructs this OrderedPair from the given elements in order.
   *
   * @param first the first element.
   * @param second the second element.
   * @throws NullPointerException if either element is null.
   */
  public OrderedPair(T first, T second) throws NullPointerException {
    super(first, second);
  }
  /**
   * Constructs a new ordered pair from the given one with references to the original elements.
   *
   * @param toClone containing the elements to construct another utils.OrderedPair from.
   * @throws NullPointerException if either the given utils.OrderedPair or either element is null.
   */
  public OrderedPair(OrderedPair<T> toClone) throws NullPointerException {
    this(Objects.requireNonNull(toClone).first, Objects.requireNonNull(toClone).second);
  }

  /**
   * Returns a new ordered pair with the order of elements reversed.
   *
   * @return the utils.OrderedPair with references to original elements.
   */
  public OrderedPair<T> reverse() {
    return new OrderedPair<>(this.second, this.first);
  }

  /**
   * Computes the hashcode using Objects.hash on the 2 elements in order.
   *
   * @return the hashcode for this utils.OrderedPair.
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.first, this.second);
  }

  /**
   * Returns whether the other Object is an ordered pair whose elements equal this pair's elements
   * in order, according to that element's .equals() method.
   *
   * @param other the object to check equality to this.
   * @return boolean indicating equality between this and the other object.
   */
  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof OrderedPair<?>)) {
      return false;
    }
    OrderedPair<?> otherOrderedPair = (OrderedPair<?>) other;
    return this.first.equals(otherOrderedPair.first) && this.second.equals(otherOrderedPair.second);
  }
}
