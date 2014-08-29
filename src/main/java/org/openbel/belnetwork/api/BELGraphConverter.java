package org.openbel.belnetwork.api;

import org.cytoscape.model.CyNetwork;
import org.openbel.belnetwork.api.model.Graph;

public interface BELGraphConverter {

    public CyNetwork convert(Graph graph);

    public Graph convert(CyNetwork network);
}
