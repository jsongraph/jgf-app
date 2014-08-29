package org.openbel.belnetwork.api;

/**
 * FilterFunction defines a function to test for filter inclusion on type
 * {@code T}.
 * 
 * @param <T> the type to filter by match function
 */
public interface FilterFunction<T> {

    /**
     * Filter type {@code <T>} for inclusion.
     * 
     * @param t {@code <T>} the type to filter by match function
     * @return boolean {@code true} to include, {@code false} to exclude
     */
    public abstract boolean match(T t);
}
