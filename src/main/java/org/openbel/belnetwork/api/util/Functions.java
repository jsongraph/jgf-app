package org.openbel.belnetwork.api.util;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * Functions provides general {@link Function}s through static methods.
 */
public class Functions {
    
    private static final Out outfx = new Out();
    private static final Err errfx = new Err();
    
    /**
     * Return the {@link Functions.Out} function.
     * 
     * @return {@link Functions.Out}
     */
    public static Out printStdOut() {
        return outfx;
    }
    
    /**
     * Return the {@link Functions.Err} function.
     * 
     * @return {@link Functions.Err}
     */
    public static Err printStdErr() {
        return errfx;
    }
    
    /**
     * StdOut defines a {@link Function} to print to {@link System#out}.
     */
    private static final class Out implements Function<Object> {

        /**
         * {@inheritDoc}
         * <br><br>
         * Print to {@link System#out} the {@code toString()} {@link String}
         * for {@code o}.
         */
        @Override
        public void apply(Object o) {
            out.println(o.toString());
        }
    }
    
    /**
     * StdErr defines a {@link Function} to print to {@link System#out}.
     */
    private static final class Err implements Function<Object> {

        /**
         * {@inheritDoc}
         * <br><br>
         * Print to {@link System#err} the {@code toString()} {@link String}
         * for {@code o}.
         */
        @Override
        public void apply(Object o) {
            err.println(o.toString());
        }
    }
    
    private Functions() {
    }
}
