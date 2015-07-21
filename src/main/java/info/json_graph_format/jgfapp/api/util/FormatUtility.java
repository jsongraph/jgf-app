package info.json_graph_format.jgfapp.api.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;

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
     *            cannot be {@code null}
     * @return {@code value} as a {@link String} or {@code ""}
     * @throws java.lang.NullPointerException when {@code map} is {@code null}
     */
    public static String getOrEmptyString(String key, Map<String, Object> map) {
        if (map == null) throw new NullPointerException("map cannot be null");

        Object value = map.get(key);

        return (value == null ? "" : value.toString());
    }

    /**
     * Return a {@link String} representing all schema validation messages.
     *
     * @param report the {@link ProcessingReport}; cannot be {@code null}
     * @return the {@link String} including all schema errors
     * @throws java.lang.NullPointerException when {@code report} is {@code null}
     */
    public static String getSchemaMessages(ProcessingReport report) {
        List<String> errors = new ArrayList<>();
        for (ProcessingMessage m : report) {
            JsonNode data = m.asJson();
            Iterator<Map.Entry<String, JsonNode>> entries = data.get("reports").fields();
            while (entries.hasNext()) {
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
     * Return a point (x,y,z) in an absolute coordinate space.
     *
     * @param point the {@link Collection} of points (x,y,z) as a {@link List} of {@link Double}
     * @return absolute coordinate {@link List}; {@code null} if {@code points} is {@code null};
     * an empty immutable {@link List} if {@code points} is empty
     */
    public static List<Double> absoluteCoordinates(List<Double> point, int width, int height, double multiplier) {
        if (point == null) return null;

        // Calculate absolute coordinates given relative.
        Double x = null, y = null, z = null;
        if (point.size() > 0) x = width * point.get(0) * multiplier;
        if (point.size() > 1) y = height * point.get(1) * multiplier;
        if (point.size() > 2) z = point.get(2);
        return asList(x, y, z);
    }

    /**
     * Return a point (x,y,z) in a relative coordinate space.
     * as is.
     *
     * @param point the {@link Collection} of points (x,y,z) as a {@link List} of {@link Double}
     * @return relative coordinate {@link List}; {@code null} if {@code points} is {@code null};
     * an empty immutable {@link List} if {@code points} is empty
     */
    public static List<Double> relativeCoordinates(List<Double> point, int width, int height, double multiplier) {
        if (point == null) return null;

        // Calculate relative coordinates given absolute.
        Double x = null, y = null, z = null;
        if (point.size() > 0) x = (point.get(0) / width)  / multiplier;
        if (point.size() > 1) y = (point.get(1) / height) / multiplier;
        if (point.size() > 2) z = point.get(2);
        return asList(x, y, z);
    }

    private FormatUtility() {
        //static accessors only
    }
}
