package info.json_graph_format.jgfapp.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cytoscape.model.CyNetwork;

import java.util.HashMap;
import java.util.List;

/**
 * {@link Graph} represents the "graph" definition from the JSON graph schema.
 */
public class Graph {

    public String type;
    public String label;
    public Boolean directed = true;
    public List<Node> nodes;
    public List<Edge> edges;
    public HashMap<String, Object> metadata;

    /**
     * Reference to {@link CyNetwork} used for easy association.
     */
    @JsonIgnore
    public CyNetwork cyNetwork;
}
