package org.openbel.belnetwork.internal.ui;

import org.cytoscape.model.CyEdge;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

public class ShowEvidenceFactory implements EdgeViewTaskFactory {

    @Override
    public TaskIterator createTaskIterator(View<CyEdge> arg0, CyNetworkView arg1) {
        return null;
    }

    @Override
    public boolean isReady(View<CyEdge> edgeV, CyNetworkView cyNv) {
        // TODO true if records exist in evidence table for network/edge
        return true;
    }
}