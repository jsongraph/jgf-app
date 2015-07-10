package info.json_graph_format.jgfapp.internal.ui;

import info.json_graph_format.jgfapp.api.model.Evidence;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;

import javax.swing.*;

public class EvidenceEditWindow extends JFrame {

    public EvidenceEditWindow(CyNetwork cyN, CyEdge cyE, Evidence evidence) {
        JFrame frame = new JFrame("Edit Evidence");
        frame.add(new EvidenceEditPanel(evidence));
        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
