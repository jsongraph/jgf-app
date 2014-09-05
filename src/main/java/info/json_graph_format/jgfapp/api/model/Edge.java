package info.json_graph_format.jgfapp.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cytoscape.model.CyEdge;

import java.util.Map;

/**
 * {@link Edge} represents the "edge" definition from the JSON graph schema.
 */
public class Edge implements MetadataProvider {

    public String source;
    public String target;
    public String relation;
    public Boolean directed = true;
    public String label;
    public Map<String, Object> metadata;

    /**
     * Reference to {@link CyEdge} used for easy association.
     */
    @JsonIgnore
    public CyEdge cyEdge;

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
