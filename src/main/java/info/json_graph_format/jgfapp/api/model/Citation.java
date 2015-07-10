package info.json_graph_format.jgfapp.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link Citation} represents the "citation" object within the "evidence" definition
 * from the BEL JSON graph schema.
 */
public class Citation {

    @JsonProperty("type")
    public String type;

    @JsonProperty("id")
    public String id;

    @JsonProperty("name")
    public String name;
}
