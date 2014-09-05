package info.json_graph_format.jgfapp.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cytoscape.model.CyNetwork;

import java.util.List;
import java.util.Map;

/**
 * {@link Graph} represents the "graph" definition from the JSON graph schema.
 */
public class Graph implements MetadataProvider {

    public String type;
    public String label;
    public Boolean directed = true;
    public List<Node> nodes;
    public List<Edge> edges;
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
