package info.json_graph_format.jgfapp.api.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FilterFunctions provides general {@link FilterFunction}s through static
 * methods.
 */
public class FilterFunctions {

    /**
     * Returns a {@link NonNull} for a specific type {@code <T>}.
     * 
     * @return {@link NonNull} fx
     */
    public static <T> NonNull<T> nonNull() {
        return new NonNull<T>();
    }
    
    /**
     * Returns a {@link Regex} for a specific {@link Pattern}.
     * 
     * @return {@link Regex} fx
     */
    public static Regex regex(Pattern pattern) {
        return new Regex(pattern);
    }

    /**
     * Defines a regular expression {@link FilterFunction} that will filter
     * input that matches the regular expression {@link Pattern}.
     */
    public static final class Regex implements FilterFunction<String> {
        private final Pattern pattern;

        /**
         * Construct with the regular expression {@link Pattern}.
         * 
         * @param pattern {@link Pattern}; may not be {@code null}
         */
        public Regex(final Pattern pattern) {
            if (pattern == null)
                throw new NullPointerException("pattern cannot be null");
            this.pattern = pattern;
        }
        
        /**
         * {@inheritDoc}
         * <br><br>
         * 
         * The input {@code t} is a match iff it {@link Matcher#matches()} the
         * regular expression {@link Pattern}.
         */
        @Override
        public boolean match(String t) {
            if (t == null) {
                return false;
            }
            return pattern.matcher(t).matches();
        }
    }
    
    /**
     * Defines a {@link FilterFunction} that filters out all non-{@code null}
     * values of type {@code <T>}.
     * 
     * @param <T> type {@code <T>}
     */
    public static final class NonNull<T> implements FilterFunction<T> {
        /**
         * {@inheritDoc}
         * <br><br>
         */
        @Override
        public boolean match(Object t) {
            return t != null;
        }
    }
    
    private FilterFunctions() {
    }
}
