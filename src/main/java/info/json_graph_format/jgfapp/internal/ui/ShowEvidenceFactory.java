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

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

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
        selectEvidenceListPanel(eastPanel);

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

    private void selectEvidenceListPanel(CytoPanel eastPanel) {
        // find tabs
        Optional<JTabbedPane> tabs = Arrays.stream(((JPanel) eastPanel).getComponents()).
                filter(component -> component instanceof JTabbedPane).
                map(component -> (JTabbedPane) component).
                findFirst();
        if (!tabs.isPresent()) return;

        // find evidence list panel
        JTabbedPane tabPane = tabs.get();
        Optional<EvidenceListPanel> evidenceList = Arrays.stream(tabPane.getComponents()).
                filter(component -> component instanceof EvidenceListPanel).
                map(component -> (EvidenceListPanel) component).
                findFirst();
        if (!evidenceList.isPresent()) return;

        tabPane.setSelectedComponent(evidenceList.get());
    }
}
