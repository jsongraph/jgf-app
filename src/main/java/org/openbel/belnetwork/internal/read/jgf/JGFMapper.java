package org.openbel.belnetwork.internal.read.jgf;

import java.util.*;
import java.util.Map.Entry;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.model.SavePolicy;
import org.openbel.belnetwork.model.*;

import static org.openbel.belnetwork.api.FormatUtility.getOrEmptyString;
import static org.openbel.belnetwork.internal.util.TableUtility.getOrCreateColumn;
import static org.openbel.belnetwork.internal.util.TableUtility.getTable;

public class JGFMapper {

    private final Graph graph;
    private final CyNetwork network;
    private final CyTableFactory cyTableFactory;
    private final CyTableManager cyTableManager;

    public JGFMapper(final Graph graph, final CyNetwork network, CyTableFactory cyTableFactory, CyTableManager cyTableManager) {
        this.graph = graph;
        this.network = network;
        this.cyTableFactory = cyTableFactory;
        this.cyTableManager = cyTableManager;
    }

    private void processEvidences(CyEdge edge, List<Evidence> evidences) {
        CyTable eviTable = createOrExtendEvidenceTable(evidences, cyTableManager, cyTableFactory);
        CyRow networkRow = network.getRow(network);
        String graphTitle = networkRow.get(CyNetwork.NAME, String.class);
        Long netSUID = network.getSUID();
        Long edgeSUID = edge.getSUID();
        //put the evidence into the table.
        for (Evidence ev: evidences) {
            CyRow row = eviTable.getRow(SUIDFactory.getNextSUID());
            row.set("network.SUID", netSUID);
            row.set("network name", graphTitle);
            row.set("edge.SUID", edgeSUID);
            row.set("bel statement", ev.belStatement);
            row.set("summary text", ev.summaryText);

            if (ev.citation != null) {
                row.set("citation type", ev.citation.type);
                row.set("citation id", ev.citation.id);
                row.set("citation name", ev.citation.name);
            }

            if (ev.biologicalContext != null) {
                row.set("species", ev.biologicalContext.speciesCommonName);

                Map<String, Object> varying = ev.biologicalContext.variedAnnotations;
                for (Entry<String, Object> entry : varying.entrySet()) {
                    row.set(entry.getKey(), getOrEmptyString(entry.getKey(), varying));
                }
            }
        }
    }

    private CyTable createOrExtendEvidenceTable(List<Evidence> evidences, CyTableManager tableMgr, CyTableFactory tableFactory) {
        CyTable evTable = getTable("BEL.Evidence", tableMgr);

        if (evTable == null) {
            // Create the table...
            evTable = tableFactory.createTable("BEL.Evidence", "SUID", Long.class, true, false);
            // ...allow save to Cytoscape session files (*.cys)
            evTable.setSavePolicy(SavePolicy.SESSION_FILE);

            // ...create basic, one-time columns
            evTable.createColumn("network.SUID", Long.class, true, null);
            evTable.createColumn("network name", String.class, true);
            evTable.createColumn("edge.SUID", Long.class, true, null);
            evTable.createColumn("bel statement", String.class, false);
            evTable.createColumn("citation type", String.class, false);
            evTable.createColumn("citation id", String.class, false);
            evTable.createColumn("citation name", String.class, false);
            evTable.createColumn("summary text", String.class, false);
            evTable.createColumn("species", String.class, false);

            // ...add table to Cytoscape table manager
            tableMgr.addTable(evTable);
        }

        // ...determine set of biological context annotation keys
        Set<String> union = new HashSet<String>();
        for (Evidence ev : evidences) {
            if (ev.biologicalContext != null) {
                union.addAll(ev.biologicalContext.variedAnnotations.keySet());
            }
        }

        /// ...add string columns for keys if they do not already exist
        for (String varyingKey : union) {
            getOrCreateColumn(varyingKey, String.class, false, evTable);
        }

        return evTable;
    }
}
