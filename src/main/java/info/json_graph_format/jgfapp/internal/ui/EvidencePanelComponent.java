package info.json_graph_format.jgfapp.internal.ui;

import info.json_graph_format.jgfapp.api.BELEvidenceMapper;
import info.json_graph_format.jgfapp.api.model.Evidence;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.*;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

import static info.json_graph_format.jgfapp.api.util.TableUtility.getTable;
import static info.json_graph_format.jgfapp.internal.Constants.EDGE_SUID;
import static info.json_graph_format.jgfapp.internal.Constants.NETWORK_SUID;
import static java.util.Arrays.asList;

/**
 * {@link EvidencePanelComponent} defines a {@link CytoPanelComponent} tab for
 * the cytoscape <em>Results Panel</em>.
 * <br><br>
 * The {@link EvidencePanel} provides the user interface for this
 * {@link CytoPanelComponent}.
 * <br><br>
 * The {@link EvidencePanelComponent#handleEvent(org.cytoscape.model.events.RowsSetEvent)}
 * method will call {@link EvidencePanel#update(java.util.List)} when an
 * {@link CyEdge edge} is clicked.
 */
public class EvidencePanelComponent implements CytoPanelComponent, RowsSetListener {

    private final EvidencePanel evidencePanel;
    private final BELEvidenceMapper belEvidenceMapper;
    private final CyTableManager tableManager;
    private final CyNetworkManager networkManager;

    public EvidencePanelComponent(BELEvidenceMapper belEvidenceMapper,
                                  CyTableManager tableManager, CyNetworkManager networkManager) {
        this.evidencePanel = new EvidencePanel();
        this.belEvidenceMapper = belEvidenceMapper;
        this.tableManager = tableManager;
        this.networkManager = networkManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getComponent() {
        return evidencePanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.EAST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return "Evidence";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Icon getIcon() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleEvent(RowsSetEvent event) {
        Collection<RowSetRecord> selectedRecords = event.getColumnRecords(CyNetwork.SELECTED);
        if (selectedRecords.isEmpty()) return;

        RowSetRecord value = null;
        for (RowSetRecord record : selectedRecords) {
            if (record.getValue() == Boolean.TRUE)
                value = record;
        }
        if (value == null) return;

        Long suid = value.getRow().get(CyNetwork.SUID, Long.class);
        if (suid == null) return;

        CyTable evTable = getTable("BEL.Evidence", tableManager);
        if (evTable == null) return;

        Collection<CyRow> evidenceRows = evTable.getMatchingRows(EDGE_SUID, suid);
        if (!evidenceRows.isEmpty()) {
            CyRow row = evidenceRows.iterator().next();
            Long networkSUID = row.get(NETWORK_SUID, Long.class);
            CyNetwork cyN = networkManager.getNetwork(networkSUID);
            if (cyN == null) return;
            CyEdge edge = cyN.getEdge(row.get(EDGE_SUID, Long.class));
            if (edge == null) return;

            Evidence[] evidences = belEvidenceMapper.mapFromTable(edge, evTable);
            evidencePanel.update(asList(evidences));
        } else {
            evidencePanel.update(new ArrayList<Evidence>());
        }
    }
}
