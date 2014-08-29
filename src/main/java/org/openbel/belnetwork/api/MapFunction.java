package org.openbel.belnetwork.api;

/**
 * MapFunction defines a function to map type {@code <T>} to type {@code <U>}.
 *
 * @param <T> the type to map from
 * @param <U> the type to map to
 */
public interface MapFunction<T, U> {

    /**
     * Maps type {@code T} to {@code U}.
     * 
     * @param o instance of type {@code T}
     * @return instance of type {@code U}
     */
    public U map(T o);
}
