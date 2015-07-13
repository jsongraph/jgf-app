package info.json_graph_format.jgfapp.internal.ui;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

import javax.swing.*;

public class EvidenceCreateWindow extends JFrame {

    public EvidenceCreateWindow(EvidencePanelComponent evComponent, CyTable table, CyNetwork cyN, CyEdge cyE) {
        JFrame frame = new JFrame("Create Evidence");
        frame.add(new EvidenceCreatePanel(this, evComponent, table, cyN, cyE));
        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
