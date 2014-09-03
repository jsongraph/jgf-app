package info.json_graph_format.jgfapp.api.util;

/**
 * A triple of types {@code T}, {@code U}, and {@code V}.
 * .
 * <p>
 * For example, to represent an RGB value:
 * <pre>
 *     <code>
 *         Triple<Integer, Integer, Integer> rgb = new Triple<>(128, 128, 128);
 *         // it's never black and white
 *     </code>
 * </pre>
 * </p>
 *
 * @param <T> first type {@code T}
 * @param <U> second type {@code T}
 * @param <V> third type {@code T}
 */
public final class Triple<T, U, V> {
    
    private final T first;
    private final U second;
    private final V third;
    private final int hash;

    /**
     * Construct with first type {@code T}, second type {@code U}, and third
     * type {@code V}.
     * 
     * @param first type {@code T}
     * @param second type {@code U}
     * @param third type {@code V}
     */
    public Triple(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.hash = hash();
    }
    
    /**
     * Return the first type {@code T}.
     * 
     * @return {@code T}
     */
    public T first() {
        return first;
    }
    
    /**
     * Return the second type {@code U}.
     * 
     * @return {@code U}
     */
    public U second() {
        return second;
    }
    
    /**
     * Return the third type {@code V}.
     * 
     * @return {@code V}
     */
    public V third() {
        return third;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Triple)) return false;

        final Triple<?,?,?> other = (Triple<?,?,?>) o;

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
        
        if (third == null) {
            if (other.third != null) return false;
        } else if (!third.equals(other.third)) {
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
        if (third != null) {
            result *= prime;
            result += third.hashCode();
        }
        return result;
    }
}
