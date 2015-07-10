package info.json_graph_format.jgfapp.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cytoscape.model.CyNode;

import java.util.Map;

/**
 * {@link Node} represents the "node" definition from the JSON graph schema.
 */
public class Node implements MetadataProvider {

    @JsonProperty("id")
    public String id;

    @JsonProperty("label")
    public String label;

    @JsonProperty("metadata")
    public Map<String, Object> metadata;

    /**
     * Reference to {@link CyNode} used for easy association.
     */
    @JsonIgnore
    public CyNode cyNode;

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
