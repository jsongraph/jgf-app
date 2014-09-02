package org.openbel.belnetwork.api.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import java.util.Iterator;
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
     * Return a {@link String} representing all schema validation messages.
     *
     * @param report the {@link ProcessingReport}; cannot be {@code null}
     * @return the {@link String} including all schema errors
     * @throws java.lang.NullPointerException when {@code report} is {@code null}
     */
    public static String getSchemaMessages(ProcessingReport report) {
        final StringBuilder b = new StringBuilder();

        for (ProcessingMessage m : report) {
            JsonNode data = m.asJson();
            Iterator<Map.Entry<String, JsonNode>> entries = data.get("reports").fields();
            while(entries.hasNext()) {
                Map.Entry<String, JsonNode> entry = entries.next();
                String path = entry.getKey();
                if (!(entry.getValue() instanceof ArrayNode)) continue;

                Iterator<JsonNode> items = entry.getValue().elements();
                while (items.hasNext()) {
                    JsonNode valNode = items.next();
                    if (valNode.has("instance") && valNode.get("instance").has("pointer")) {
                        path += valNode.get("instance").get("pointer").asText();
                    }
                    b.append("JSON Path: ").append(path).append("\n");
                    b.append("    ").append(valNode.get("message").asText()).append("\n");
                }
            }
        }
        return b.toString();
    }

    private FormatUtility() {
        //static accessors only
    }
}
