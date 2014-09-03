package org.openbel.belnetwork.api.util;

import com.google.common.collect.ComparisonChain;

/**
 * A pair of some types, {@code T1} and {@code T2} that implement {@link Comparable}.
 * <p>
 * For example, to represent a point:
 * <pre>
 * Pair&lt;Integer, Integer&gt; point = new Pair&lt;&gt;(0, 0);
 * Pair&lt;String, Integer&gt; nameAge = new Pair&lt;&gt;("Tony", 32);
 * // java.lang.Integer and java.lang.Double are Comparable
 * </pre>
 * but not:
 * <pre>
 * Pair&lt;Object, Object&gt; point = new Pair&lt;&gt;(new Object(), new Object());
 * // java.lang.Object is not Comparable
 * </pre>
 * </p>
 */
public final class ComparablePair<T1 extends Comparable<T1>, T2 extends Comparable<T2>>
        implements Comparable<ComparablePair<T1, T2>> {
    private final T1 first;
    private final T2 second;
    private final int hash;

    /**
     * Creates a pair.
     *
     * @param t1 {@code T1}
     * @param t2 {@code T2}
     */
    public ComparablePair(T1 t1, T2 t2) {
        this.first = t1;
        this.second = t2;
        this.hash = hash();
    }

    /**
     * Returns the pair's first ({@code T1}).
     */
    public T1 first() {
        return first;
    }

    /**
     * Returns the pair's second ({@code T2}).
     */
    public T2 second() {
        return second;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof ComparablePair)) return false;

        final ComparablePair<?,?> other = (ComparablePair<?,?>) o;

        if (first == null) {
            if (other.first != null) return false;
        } else if (!first.equals(other.first)) {
            return false;
        }

        if (second == null) {
            if (other.second != null) return false;
        } else if (!second.equals(other.second)) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return hash;
    }

    private int hash() {
        final int prime = 31;
        int result = 1;
        if (first != null) {
            result *= prime;
            result += first.hashCode();
        }
        if (second != null) {
            result *= prime;
            result += second.hashCode();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(ComparablePair<T1, T2> other) {
        return ComparisonChain.start().
                compare(first(), other.first()).
                compare(second(), other.second()).
                result();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Pair [ " + first + ", " + second + " ]";
    }
}
