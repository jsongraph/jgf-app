package org.openbel.belnetwork.api;

import java.util.Map;
import java.util.Map.Entry;

/**
 * EntryFunction defines a function to apply to every {@link Entry map entry}
 * in a {@link Map}.
 * 
 * @param <K> the {@link Entry#getKey() key}
 * @param <V> the {@link Entry#getValue() value}
 */
public interface EntryFunction<K, V> {

    /**
     * Applies the function to the {@link Entry map entry}.
     * 
     * @param key Map entry {@link Entry#getKey() key}
     * @param value Map entry {@link Entry#getValue() value}
     */
    public abstract void apply(K key, V value);
}
