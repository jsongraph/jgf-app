package org.openbel.belnetwork.api;

/**
 * A pair of some types, {@code T1} and {@code T2}.
 * <p>
 * For example, to represent a point:
 * <pre>
 *     <code>
 *         Pair<Integer, Integer> point = new Pair<>(0, 0);
 *     </code>
 * </pre>
 * </p>
 *
 * @author Nick Bargnesi
 */
public final class Pair<T1, T2> {
    private final T1 first;
    private final T2 second;
    private final int hash;

    /**
     * Creates a pair.
     *
     * @param t1 {@code T1}
     * @param t2 {@code T2}
     */
    public Pair(T1 t1, T2 t2) {
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
        if (o == null || !(o instanceof Pair)) return false;

        final Pair<?,?> other = (Pair<?,?>) o;

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
    public String toString() {
        return "Pair [ " + first + ", " + second + " ]";
    }
}
