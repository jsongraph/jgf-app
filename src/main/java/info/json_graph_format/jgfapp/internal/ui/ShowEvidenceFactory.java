package info.json_graph_format.jgfapp.internal.ui;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

import java.util.Collection;

import static info.json_graph_format.jgfapp.api.util.TableUtility.getTable;
import static info.json_graph_format.jgfapp.api.util.Utility.hasItems;
import static info.json_graph_format.jgfapp.internal.Constants.BEL_EVIDENCE_TABLE;
import static info.json_graph_format.jgfapp.internal.Constants.EDGE_SUID;

public class ShowEvidenceFactory implements EdgeViewTaskFactory {

    private final CyTableManager tableManager;
    private final CySwingApplication swingApplication;
    private final EvidencePanelComponent evidencePanelComponent;

    public ShowEvidenceFactory(CyTableManager tableManager, CySwingApplication swingApplication,
                               EvidencePanelComponent evidencePanelComponent) {
        this.tableManager = tableManager;
        this.swingApplication = swingApplication;
        this.evidencePanelComponent = evidencePanelComponent;
    }

    @Override
    public TaskIterator createTaskIterator(View<CyEdge> view, CyNetworkView networkView) {
        CytoPanel eastPanel = swingApplication.getCytoPanel(CytoPanelName.EAST);
        eastPanel.setState(CytoPanelState.DOCK);
        int index = eastPanel.indexOfComponent(evidencePanelComponent.getComponent());
        eastPanel.setSelectedIndex(index);

        return new TaskIterator(new NoOpTask());
    }

    @Override
    public boolean isReady(View<CyEdge> view, CyNetworkView cyNv) {
        Long suid = view.getModel().getSUID();
        CyTable evTable = getTable(BEL_EVIDENCE_TABLE, tableManager);
        if (evTable == null) return false;

        Collection<CyRow> rows = evTable.getMatchingRows(EDGE_SUID, suid);
        return hasItems(rows);
    }
}
