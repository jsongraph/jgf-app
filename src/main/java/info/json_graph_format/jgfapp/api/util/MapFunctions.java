package info.json_graph_format.jgfapp.api.util;

/**
 * Provides general {@link MapFunction map functions} through static methods.
 */
public class MapFunctions {

    /**
     * Returns a no-op {@link MapFunction}.
     *
     * @return no-op {@link MapFunction}
     */
    public static <T, U> NoOp<T, U> noOp() {
        return new NoOp<T, U>();
    }

    /**
     * Defines a no-op {@link MapFunction}.  Type {@code <T>} is both input and
     * output.
     *
     * @param <T> the type to map from
     * @param <U> the type to map to; <em>this type is ignored</em>
     */
    public static final class NoOp<T, U> implements MapFunction<T, T> {

        /**
         * {@inheritDoc}
         * <br><br>
         * <p>
         * Simply returns {@code o} of type {@code <T>} without any mapping.
         */
        @Override
        public T map(T o) {
            return o;
        }
    }
}
