package info.json_graph_format.jgfapp.api.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import java.util.*;

import static info.json_graph_format.jgfapp.internal.Constants.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

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
     * Return a {@link String} representing all schema validation messages.
     *
     * @param report the {@link ProcessingReport}; cannot be {@code null}
     * @return the {@link String} including all schema errors
     * @throws java.lang.NullPointerException when {@code report} is {@code null}
     */
    public static String getSchemaMessages(ProcessingReport report) {
        List<String> errors = new ArrayList<String>();
        for (ProcessingMessage m : report) {
            JsonNode data = m.asJson();
            Iterator<Map.Entry<String, JsonNode>> entries = data.get("reports").fields();
            while(entries.hasNext()) {
                Map.Entry<String, JsonNode> entry = entries.next();
                if (!(entry.getValue() instanceof ArrayNode)) continue;

                Iterator<JsonNode> items = entry.getValue().elements();
                while (items.hasNext()) {
                    String path = entry.getKey();
                    JsonNode valNode = items.next();
                    if (valNode.has("instance") && valNode.get("instance").has("pointer")) {
                        path += valNode.get("instance").get("pointer").asText();
                    }
                    errors.add(format("JSON Path: %s\n    %s\n", path, valNode.get("message").asText()));
                }
            }
        }

        int show = 10;
        StringBuilder b = new StringBuilder(
                format("%d validation errors.%s\n\n",
                        errors.size(),
                        (errors.size() > show) ?
                                format(" Showing first %d.", Collections.min(asList(errors.size(), show))) :
                                ""
                )
        );
        for (int i = 0; i < errors.size() && i < show; i++)
            b.append(errors.get(i));
        return b.toString();
    }

    /**
     * Translate x, y, z coordinates. If points are percentage-based then they are translated
     * to absolute positioning according to Cytoscape, otherwise they are passed through as is.
     *
     * @param points the {@link Collection} of points (x,y,z) as a {@link List} of {@link Double}
     * @return translated coordinate {@link List}; {@code null} if {@code points} is {@code null};
     * an empty immutable {@link List} if {@code points} is empty
     */
    public static List<List<Double>> translateCoordinates(Collection<List<Double>> points) {
        if (points == null) return null;
        if (points.isEmpty()) return emptyList();

        boolean isPercentage = true;
        for (List<Double> point : points) {
            isPercentage &= Iterables.all(point, new Predicate<Double>() {
                @Override
                public boolean apply(@Nullable Double d) {
                    return d == null || d >= 0.0 && d <= 1.0;
                }
            });
        }

        List<List<Double>> translated = new ArrayList<List<Double>>(points.size());
        for (List<Double> point : points) {
            if (point == null) {
                translated.add(null);
                continue;
            }

            if (isPercentage) {
                Double x = null, y = null, z = null;
                if (point.size() > 0)
                    x = DEFAULT_VIEW_WIDTH * point.get(0) * COORDINATE_MULTIPLIER;
                if (point.size() > 1)
                    y = DEFAULT_VIEW_HEIGHT * point.get(1) * COORDINATE_MULTIPLIER;
                if (point.size() > 2)
                    z = point.get(2);
                translated.add(asList(x, y, z));
            } else {
                translated.add(point);
            }
        }
        return translated;
    }

    private FormatUtility() {
        //static accessors only
    }
}
