package org.cytoscape.jgfnetwork.internal.ui;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyRow;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;



public class ShowEvidenceFactory implements EdgeViewTaskFactory {

	
	
	@Override
	public TaskIterator createTaskIterator(View<CyEdge> arg0, CyNetworkView arg1) {
		   // show evidence panel in "Results Panel" (east cyto panel)
		return null;
	}

	@Override
	public boolean isReady(View<CyEdge> edgeV, CyNetworkView cyNv) {
		 CyRow row = cyNv.getModel().getRow(edgeV.getModel());
	     return row.isSet("linked") && row.get("linked", Boolean.class) == true;

	}

}
