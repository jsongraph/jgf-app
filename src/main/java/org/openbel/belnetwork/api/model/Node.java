package org.openbel.belnetwork.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cytoscape.model.CyNode;

import java.util.HashMap;

/**
 * {@link Node} represents the "node" definition from the BEL JSON graph schema.
 */
public class Node {

    public String id;
    public String label;
    public HashMap<String, Object> metadata;

    /**
     * Reference to {@link CyNode} used for easy association.
     */
    @JsonIgnore
    public CyNode cyNode;
}
