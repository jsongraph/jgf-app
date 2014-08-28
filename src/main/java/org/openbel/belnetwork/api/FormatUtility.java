package org.openbel.belnetwork.api;

import org.openbel.belnetwork.model.Graph;
import org.openbel.belnetwork.model.Root;

import java.util.Map;

/**
 * {@link FormatUtility} provides convenience methods for serializing / de-serializing
 * JSON Graph Format content.
 */
public class FormatUtility {

    /**
     * Return {@code value} as a {@link String} or {@code ""} if {@code key}
     * is not in {@code map}.
     *
     * @param key the {@link String} key
     * @param map the {@link Map} of {@link String} key / {@link Object} value;
     * cannot be {@code null}
     * @return {@code value} as a {@link String} or {@code ""}
     * @throws java.lang.NullPointerException when {@code map} is {@code null}
     */
    public static String getOrEmptyString(String key, Map<String, Object> map) {
        if (map == null) throw new NullPointerException("map cannot be null");

        Object value = map.get(key);

        return (value == null ? "" : value.toString());
    }

    /**
     * Return {@code value} as an {@link Integer} or {@code 0} if {@code key}
     * is not in {@code map}.
     *
     * @param key the {@link String} key
     * @param map the {@link Map} of {@link String} key / {@link Object} value;
     * cannot be {@code null}
     * @return {@code value} as an {@link Integer} or {@code 0}
     * @throws java.lang.NullPointerException when {@code map} is {@code null}
     */
    public static Integer getOrZero(String key, Map<String, Object> map) {
        if (map == null) throw new NullPointerException("map cannot be null");

        Object value = map.get(key);
        return (value == null ? 0 : (Integer) value);
    }

    /**
     * Determines the array of {@link Graph} from the {@link Root root object}.
     *
     * @param root the {@link Root} object;
     * @return array of {@link Graph} objects; returns {@code null} when
     * {@code root} or {@code root}'s graph fields are all {@code null}
     */
    public static Graph[] determineGraphs(Root root) {
        if (root == null) return null;

        if (root.graph != null)
            return new Graph[] {root.graph};
        if (root.graphs != null)
            return root.graphs.toArray(new Graph[root.graphs.size()]);

        return null;
    }

    private FormatUtility() {
        //static accessors only
    }
}
