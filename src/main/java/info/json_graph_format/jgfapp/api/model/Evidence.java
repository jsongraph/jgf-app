package info.json_graph_format.jgfapp.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * {@link Evidence} represents the "evidence" definition from the
 * BEL JSON graph schema.
 */
public class Evidence {

    /**
     * Reference to the evidence ID in the Evidence table.
     * Only mapped from Row to Evidence.
     */
    @JsonIgnore
    public Long evidenceId;

    @JsonProperty("bel_statement")
    public String belStatement;

    @JsonProperty("citation")
    public Citation citation;

    @JsonProperty("biological_context")
    public BiologicalContext biologicalContext;

    @JsonProperty("summary_text")
    public String summaryText;

    @JsonProperty("metadata")
    public Map<String, Object> metadata;
}
