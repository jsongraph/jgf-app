package org.openbel.belnetwork.internal.mapperclasses;

import java.util.HashMap;


public class Edge {
    private String source;
    private String target;
    private String relation;
    private Boolean directed = true;
    private String label;
    //can contain evidences array and other attributes like causal
    private HashMap<String, Object> metadata;
    
    
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    public String getRelation() {
        return relation;
    }
    public void setRelation(String relation) {
        this.relation = relation;
    }
    public Boolean getDirected() {
        return directed;
    }
    public void setDirected(Boolean directed) {
        this.directed = directed;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public HashMap<String, Object> getMetadata() {
        return metadata;
    }
    public void setMetadata(HashMap<String, Object> metadata) {
        this.metadata = metadata;
    }
    
}
