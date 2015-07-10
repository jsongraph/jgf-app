package info.json_graph_format.jgfapp.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    public String belStatement;
    public Citation citation;
    public BiologicalContext biologicalContext;
    public String summaryText;
}
