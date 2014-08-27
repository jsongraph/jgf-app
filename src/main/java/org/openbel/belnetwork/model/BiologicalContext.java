package org.openbel.belnetwork.model;

import java.util.HashMap;
import java.util.Map;

public class BiologicalContext {

    public Integer ncbiTaxId;
    public String speciesCommonName;
    public Map<String, Object> variedAnnotations = new HashMap<String, Object>();
}