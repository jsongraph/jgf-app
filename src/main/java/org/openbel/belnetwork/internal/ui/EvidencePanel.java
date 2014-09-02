package org.openbel.belnetwork.internal.ui;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import org.cytoscape.model.CyEdge;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTable;
import org.openbel.belnetwork.api.model.Evidence;
import org.openbel.belnetwork.api.util.Pair;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import static java.lang.String.format;
import static org.openbel.belnetwork.api.util.Utility.hasItems;
import static org.openbel.belnetwork.api.util.Utility.hasLength;
import static org.openbel.belnetwork.internal.Constants.PUBMED;
import static org.openbel.belnetwork.internal.Constants.ONLINE_RESOURCE;
import static org.openbel.belnetwork.internal.Constants.PUBMED_URL_PREFIX;

/**
 * {@link EvidencePanel} provides the user interface to show
 * {@link Evidence evidence}
 * (e.g. BEL statement, summary text, citation, and biological context) for a
 * cytoscape {@link CyEdge}.
 * <br><br>
 * The interface can be updated to show new {@link Evidence} objects by calling
 * the {@link EvidencePanel#update(java.util.List)} method.
 */
class EvidencePanel extends JPanel implements ListSelectionListener {

    private EventList<Pair<String, Evidence>> statements = new BasicEventList<Pair<String, Evidence>>();
    private final JXTable statementTable;
    private final JLabel citationName;
    private final JXHyperlink citationLink;
    private final JTextPane annotationPane;

    /**
     * Constructor for the {@link EvidencePanel} that creates the user interface
     * components.
     */
    public EvidencePanel() {
        setPreferredSize(new Dimension(400, 400));

        setLayout(new GridBagLayout());
        add(new JLabel("Statements"),
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.1,
                                   GridBagConstraints.LINE_START,
                                   GridBagConstraints.NONE,
                                   new Insets(0, 0, 0, 0), 0, 0));

        // Statement table
        TableModel statementTableModel = new DefaultEventTableModel<Pair<String, Evidence>>(statements, new StatementTableFormat());
        statementTable = new JXTable(statementTableModel);
        statementTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        statementTable.getSelectionModel().addListSelectionListener(this);

        // Statement scrollpane
        JScrollPane statementScrollPane = new JScrollPane(statementTable);
        add(statementScrollPane,
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.5,
                                   GridBagConstraints.LINE_START,
                                   GridBagConstraints.BOTH,
                                   new Insets(0, 5, 0, 5), 0, 0));

        // Citation panel
        add(new JLabel("Citation"),
                new GridBagConstraints(0, 2, 1, 1, 1.0, 0.1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 0, 0));
        JPanel citationPanel = new JPanel(new GridBagLayout());
        JLabel nameLabel = new JLabel("Name:");
        citationPanel.add(nameLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.20, 0.5,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 0, 0));
        citationName = new JLabel();
        citationPanel.add(citationName,
                new GridBagConstraints(1, 0, 1, 1, 0.80, 0.5,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE,
                        new Insets(0, 10, 0, 0), 0, 0));
        JLabel linkLabel = new JLabel("Link:");
        citationPanel.add(linkLabel,
                new GridBagConstraints(0, 1, 1, 1, 0.20, 0.5,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 0, 0));
        citationLink = new JXHyperlink();
        citationPanel.add(citationLink,
                new GridBagConstraints(1, 1, 1, 1, 0.80, 0.5,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE,
                        new Insets(0, 10, 0, 0), 0, 0));

        add(citationPanel,
                new GridBagConstraints(0, 3, 1, 1, 1.0, 0.1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.BOTH,
                        new Insets(0, 20, 0, 0), 0, 0));

        // Annotations scrollpane
        add(new JLabel("Annotations"),
                new GridBagConstraints(0, 4, 1, 1, 1.0, 0.1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 0, 0));
        annotationPane = new JTextPane();
        annotationPane.setContentType("text/html");
        annotationPane.setBackground(null);
        annotationPane.setBorder(null);
        annotationPane.setEditable(false);
        add(new JScrollPane(annotationPane),
                new GridBagConstraints(0, 5, 1, 1, 1.0, 0.5,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.BOTH,
                        new Insets(0, 5, 5, 5), 0, 0));
    }

    /**
     * Update the {@link EvidencePanel} user interface with a new list of
     * {@link Evidence evidences}. If {@code evidences} is {@code null} or empty
     * then the user interface will be cleared.
     *
     * @param evidences the {@link List} of {@link Evidence}; {@code null} implies
     * clearing the user interface
     */
    public void update(final List<Evidence> evidences) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                citationName.setText("");
                citationLink.setText("");
                Iterator<Pair<String, Evidence>> stmtIt = statements.iterator();
                while (stmtIt.hasNext()) {
                    stmtIt.next();
                    stmtIt.remove();
                }

                if (hasItems(evidences)) {
                    List<Pair<String, Evidence>> l = new ArrayList<Pair<String, Evidence>>();
                    for (Evidence e : evidences) {
                        l.add(new Pair<String, Evidence>(e.belStatement, e));
                    }
                    Collections.sort(l, new Comparator<Pair<String, Evidence>>() {
                        @Override
                        public int compare(Pair<String, Evidence> p1, Pair<String, Evidence> p2) {
                            if (p1.first() == null && p2.first() == null)
                                return 0;
                            if (p1.first() == null && p2.first() != null)
                                return 1;
                            if (p1.first() != null && p2.first() == null)
                                return -1;
                            return p1.first().compareTo(p2.first());
                        }
                    });
                    statements.addAll(l);

                    if (statementTable.getRowCount() > 0)
                        statementTable.getSelectionModel().setSelectionInterval(0, 0);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int[] rows = statementTable.getSelectedRows();
            final List<Pair<String, Evidence>> selection = new ArrayList<Pair<String, Evidence>>();
            for (int index : rows) {
                int modelIndex = statementTable.convertRowIndexToModel(index);
                selection.add(statements.get(modelIndex));
            }
            try {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (selection.isEmpty()) {
                            citationName.setText("");
                            citationLink.setText("");
                            annotationPane.setText("");
                            return;
                        }

                        Evidence ev = selection.get(0).second();
                        if (ev.citation == null) {
                            citationName.setText("");
                            citationLink.setText("");
                        } else {
                            citationName.setText(ev.citation.name);
                            citationLink.setText(ev.citation.id == null ? "" : ev.citation.id);
                            citationLink.setURI(makeCitationURI(ev.citation.type, ev.citation.id));
                        }

                        String html = "<html><table width=\"100%\" height=\"100%\">";
                        Map<String, Object> annotations = new TreeMap<String, Object>();
                        annotations.put("species", ev.biologicalContext.speciesCommonName);
                        annotations.putAll(ev.biologicalContext.variedAnnotations);
                        String row = "<tr valign=\"top\"><td><strong>%s</strong></td><td>%s</td></tr>";
                        for (Entry<String, Object> entry : annotations.entrySet()) {
                            if (entry.getValue() != null && hasLength(entry.getValue().toString()))
                                html += format(row, entry.getKey(), entry.getValue().toString());
                        }
                        html += "</table></html>";

                        annotationPane.setText(html);
                        annotationPane.setCaretPosition(0);
                    }
                });
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Converts citation identifier to a {@link URI}.
     *
     * @param type {@link String} type
     * @param id {@link String} id
     * @return citation {@link URI}; {@code null} is returned if {@code type}
     * and/or {@code id} is {@code null}
     */
    private static URI makeCitationURI(String type, String id) {
        if (id == null) return null;
        if (type == null) return null;

        try {
            if (type.equals(PUBMED)) {
                return new URI(PUBMED_URL_PREFIX + id);
            } else if (type.equals(ONLINE_RESOURCE)) {
                return new URI(id);
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
