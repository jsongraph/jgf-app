package org.openbel.belnetwork.api;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.openbel.belnetwork.api.model.Edge;
import org.openbel.belnetwork.api.model.Evidence;
import org.openbel.belnetwork.api.model.Graph;

public interface BELEvidenceMapper {

    public Evidence[] mapEdgeToEvidence(Graph graph, Edge edge);

    public void mapToTable(Graph graph, Edge edge, Evidence evidence, CyTable table);

    public Evidence[] mapFromTable(CyEdge edge, CyTable table);
}
