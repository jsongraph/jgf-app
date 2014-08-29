package org.openbel.belnetwork.model;

import java.util.List;

/**
 * {@link Root} represents the root object of a BEL JSON graph. It may contain one
 * {@link Graph} or a {@link List} of {@link Graph}.
 */
public class Root {

    public Graph graph;
    public List<Graph> graphs;
}
