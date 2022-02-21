package utils;

import java.util.Objects;

/**
 * Represents a collection of 2 non-null elements of a particular type.
 *
 * @param <T> The type of an individual element.
 */
public abstract class APair<T> {
    /** Gets the first element. */
    public final T first;
    /** Gets the second element. */
    public final T second;

    /**
     * Constructs this pair from the given elements in order.
     *
     * @param first the first element.
     * @param second the second element.
     * @throws NullPointerException if either element is null.
     */
    public APair(T first, T second) throws NullPointerException {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }
}
