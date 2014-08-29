package org.openbel.belnetwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cytoscape.model.CyEdge;

import java.util.HashMap;

public class Edge {

    public String source;
    public String target;
    public String relation;
    public Boolean directed = true;
    public String label;
    public HashMap<String, Object> metadata;

    @JsonIgnore
    public CyEdge cyEdge;
}
