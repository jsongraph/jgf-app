package info.json_graph_format.jgfapp.api.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link BiologicalContext} represents the "biological_context" object within
 * the "evidence" definition from the BEL JSON graph schema.
 */
public class BiologicalContext {

    @JsonProperty("ncbi_tax_id")
    public Integer ncbiTaxId;

    @JsonProperty("species_common_name")
    public String speciesCommonName;

    @JsonIgnore
    public Map<String, Object> variedAnnotations = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return variedAnnotations;
    }
}
