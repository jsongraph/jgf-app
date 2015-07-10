package info.json_graph_format.jgfapp.api.util;

/**
 * Function defines a function to apply to the type {@code T}.
 *
<p> * <p>
 * The applied function might typically have some side effect using type
 * {@code T}.
 * </p>
 *
 * @param <T> the type to apply the function to
 */
public interface Function<T> {

    /**
     * Applies this method block to a type {@code T}.
     *
    * @param t {@code <T>} the type to apply this function to
     */
    public void apply(T t);
}
