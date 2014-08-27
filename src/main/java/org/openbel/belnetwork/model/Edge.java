package org.openbel.belnetwork.model;

import java.util.HashMap;

public class Edge {

    public String source;
    public String target;
    public String relation;
    public Boolean directed = true;
    public String label;
    public HashMap<String, Object> metadata;
}
