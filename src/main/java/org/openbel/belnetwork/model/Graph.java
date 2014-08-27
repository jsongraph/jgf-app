package org.openbel.belnetwork.model;

import java.util.HashMap;
import java.util.List;

public class Graph {

    public String type;
    public String label;
    public Boolean directed = true;
    public List<Node> nodes;
    public List<Edge> edges;
    public HashMap<String, Object> metadata;
}
