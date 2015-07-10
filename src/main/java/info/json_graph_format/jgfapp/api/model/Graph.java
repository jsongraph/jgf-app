package info.json_graph_format.jgfapp.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cytoscape.model.CyNetwork;

import java.util.List;
import java.util.Map;

/**
 * {@link Graph} represents the "graph" definition from the JSON graph schema.
 */
public class Graph implements MetadataProvider {

    @JsonProperty("type")
    public String type;

    @JsonProperty("label")
    public String label;

    @JsonProperty("directed")
    public Boolean directed = true;

    @JsonProperty("nodes")
    public List<Node> nodes;

    @JsonProperty("edges")
    public List<Edge> edges;

    @JsonProperty("metadata")
    public Map<String, Object> metadata;

    /**
     * Reference to {@link CyNetwork} used for easy association.
     */
    @JsonIgnore
    public CyNetwork cyNetwork;

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
