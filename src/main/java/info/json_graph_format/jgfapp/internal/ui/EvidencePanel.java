package info.json_graph_format.jgfapp.internal.ui;

import info.json_graph_format.jgfapp.api.model.Evidence;
import org.cytoscape.model.CyEdge;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * {@link EvidencePanel} provides the user interface to show
 * {@link Evidence evidence}
 * (e.g. BEL statement, summary text, citation, and biological context) for a
 * cytoscape {@link CyEdge}.
 * <br><br>
 * The interface can be updated to show new {@link Evidence} objects by calling
 * the {@link EvidencePanel#update(java.util.List)} method.
 */
class EvidencePanel extends JPanel {

    private final EvidenceListPanel listPanel;

    /**
     * Constructor for the {@link EvidencePanel} that creates the user interface
     * components.
     */
    public EvidencePanel() {
        setPreferredSize(new Dimension(600, 400));
        setLayout(new GridBagLayout());

        listPanel = new EvidenceListPanel();
        add(listPanel,
                new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5), 5, 5));
    }

    /**
     * Update the {@link EvidencePanel} user interface with a new list of
     * {@link Evidence evidences}. If {@code evidences} is {@code null} or empty
     * then the user interface will be cleared.
     *
     * @param evidences the {@link List} of {@link Evidence}; {@code null} implies
     *                  clearing the user interface
     */
    public void update(final List<Evidence> evidences, EvidencePanelComponent evidenceComponent) {
        listPanel.update(evidences, evidenceComponent);
    }
}
