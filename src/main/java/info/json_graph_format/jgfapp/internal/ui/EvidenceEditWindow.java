package info.json_graph_format.jgfapp.internal.ui;

import info.json_graph_format.jgfapp.api.model.Evidence;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

import javax.swing.*;

public class EvidenceEditWindow extends JFrame {

    public EvidenceEditWindow(Evidence evidence, EvidencePanelComponent evComponent,
                              CyTable table, CyNetwork cyN, CyEdge cyE) {
        JFrame frame = new JFrame("Edit Evidence");
        frame.add(new EvidenceEditPanel(evidence, evComponent, table, cyN, cyE));
        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
