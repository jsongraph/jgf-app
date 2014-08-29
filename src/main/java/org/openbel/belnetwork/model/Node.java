package org.openbel.belnetwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cytoscape.model.CyNode;

import java.util.HashMap;

public class Node {

    public String id;
    public String label;
    public HashMap<String, Object> metadata;

    @JsonIgnore
    public CyNode cyNode;
}
