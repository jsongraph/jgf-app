package org.openbel.belnetwork.api.model;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link BiologicalContext} represents the "biological_context" object within
 * the "evidence" definition from the BEL JSON graph schema.
 */
public class BiologicalContext {

    public Integer ncbiTaxId;
    public String speciesCommonName;
    public Map<String, Object> variedAnnotations = new HashMap<String, Object>();
}
