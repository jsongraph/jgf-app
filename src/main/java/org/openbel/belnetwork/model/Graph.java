package org.openbel.belnetwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cytoscape.model.CyNetwork;

import java.util.HashMap;
import java.util.List;

public class Graph {

    public String type;
    public String label;
    public Boolean directed = true;
    public List<Node> nodes;
    public List<Edge> edges;
    public HashMap<String, Object> metadata;

    @JsonIgnore
    public CyNetwork cyNetwork;
}
