package info.json_graph_format.jgfapp.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cytoscape.model.CyEdge;

import java.util.HashMap;

/**
 * {@link Edge} represents the "edge" definition from the BEL JSON graph schema.
 */
public class Edge {

    public String source;
    public String target;
    public String relation;
    public Boolean directed = true;
    public String label;
    public HashMap<String, Object> metadata;

    /**
     * Reference to {@link CyEdge} used for easy association.
     */
    @JsonIgnore
    public CyEdge cyEdge;
}
