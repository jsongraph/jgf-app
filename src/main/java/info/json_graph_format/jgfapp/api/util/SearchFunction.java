package info.json_graph_format.jgfapp.api.util;

/**
 * Interface defining a function that can be applied over every {@code T} of an
 * {@link Iterable}.
 */
public interface SearchFunction<T> {

    /**
     * Returns {@code true} if {@code t} matches, {@code false} otherwise.
     *     * @param t {@code <T>}
     * @return boolean
     */
    public abstract boolean match(T t);

}
