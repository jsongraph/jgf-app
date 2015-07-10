package info.json_graph_format.jgfapp.internal.ui;

import javax.swing.*;

public class EvidenceCreateWindow extends JFrame {

    public EvidenceCreateWindow(Long networkSUID, Long edgeSUID) {
        JFrame frame = new JFrame("Create Evidence");
        frame.add(new EvidenceCreatePanel());
        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
