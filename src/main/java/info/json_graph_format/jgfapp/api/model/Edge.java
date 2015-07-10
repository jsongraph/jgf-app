package info.json_graph_format.jgfapp.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cytoscape.model.CyEdge;

import java.util.Map;

/**
 * {@link Edge} represents the "edge" definition from the JSON graph schema.
 */
public class Edge implements MetadataProvider {

    @JsonProperty("source")
    public String source;

    @JsonProperty("target")
    public String target;

    @JsonProperty("relation")
    public String relation;

    @JsonProperty("directed")
    public Boolean directed = true;

    @JsonProperty("label")
    public String label;

    @JsonProperty("metadata")
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
