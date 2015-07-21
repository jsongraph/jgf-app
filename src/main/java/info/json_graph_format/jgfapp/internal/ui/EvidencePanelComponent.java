package info.json_graph_format.jgfapp.internal.ui;

import info.json_graph_format.jgfapp.api.BELEvidenceMapper;
import info.json_graph_format.jgfapp.api.model.Evidence;
import org.cytoscape.application.CyApplicationManager;
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
import java.util.Collections;
import java.util.Optional;

import static info.json_graph_format.jgfapp.api.util.TableUtility.getTable;
import static info.json_graph_format.jgfapp.internal.Constants.*;
import static java.util.Arrays.asList;

/**
 * {@link EvidencePanelComponent} defines a {@link CytoPanelComponent} tab for
 * the cytoscape <em>Results Panel</em>.
 * <br><br>
 * The {@link EvidenceListPanel} provides the user interface for this
 * {@link CytoPanelComponent}.
 * <br><br>
 * The {@link EvidencePanelComponent#handleEvent(org.cytoscape.model.events.RowsSetEvent)}
 * method will call {@link EvidenceListPanel#update(java.util.List, EvidencePanelComponent)} when an
 * {@link CyEdge edge} is clicked.
 */
public class EvidencePanelComponent implements CytoPanelComponent, RowsSetListener {

    private final BELEvidenceMapper belEvidenceMapper;
    private final CyTableManager tableManager;
    private final CyNetworkManager networkManager;
    private final CyApplicationManager appManager;
    private EvidenceListPanel evidenceListPanel;

    public EvidencePanelComponent(BELEvidenceMapper belEvidenceMapper,
                                  CyTableManager tableManager, CyNetworkManager networkManager,
                                  CyApplicationManager appManager) {
        this.belEvidenceMapper = belEvidenceMapper;
        this.tableManager = tableManager;
        this.networkManager = networkManager;
        this.appManager = appManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getComponent() {
        return this.evidenceListPanel = new EvidenceListPanel();
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

    public void createEvidence() {
        final CyTable evTable = getTable("BEL.Evidence", tableManager);
        if (evTable == null) return;

        final CyNetwork cyN = appManager.getCurrentNetwork();

        Optional<CyEdge> cyE = CyTableUtil.
                getEdgesInState(cyN, SELECTED, true).stream().findFirst();
        if (cyE.isPresent()) {
            CyEdge theEdge = cyE.get();
            new EvidenceCreateWindow(this, evTable, cyN, theEdge);
        }
    }

    public void editEvidence(Evidence evidence) {
        CyTable evTable = getTable("BEL.Evidence", tableManager);
        if (evTable == null) return;

        Long evid = evidence.evidenceId;
        Optional<CyRow> opt = evTable.getMatchingRows(CyIdentifiable.SUID, evid).stream().findFirst();
        if (opt.isPresent()) {
            CyRow row = opt.get();

            // access evidence's network and edge
            Long networkSUID = row.get(NETWORK_SUID, Long.class);
            CyNetwork cyN = networkManager.getNetwork(networkSUID);
            CyEdge cyE = cyN.getEdge(row.get(EDGE_SUID, Long.class));
            if (cyE == null) return;

            new EvidenceEditWindow(evidence, this, evTable, cyN, cyE);
        }
    }

    public void deleteEvidence(Long evidenceId) {
        CyTable evTable = getTable("BEL.Evidence", tableManager);
        if (evTable == null) return;

        // only if the evidenceId resolves to a row in Evidence Table
        Optional<CyRow> opt = evTable.getMatchingRows(CyIdentifiable.SUID, evidenceId).stream().findFirst();
        if (opt.isPresent()) {
            CyRow row = opt.get();

            // access evidence's network and edge
            Long networkSUID = row.get(NETWORK_SUID, Long.class);
            CyNetwork cyN = networkManager.getNetwork(networkSUID);
            CyEdge edge = cyN.getEdge(row.get(EDGE_SUID, Long.class));
            if (edge == null) return;

            // delete this evidence
            evTable.deleteRows(Collections.singletonList(evidenceId));

            // find remaining evidence and update panel
            Evidence[] evidences = belEvidenceMapper.mapFromTable(edge, evTable);
            evidenceListPanel.update(asList(evidences), this);
        }
    }

    public void refresh(CyEdge cyE) {
        CyTable evTable = getTable("BEL.Evidence", tableManager);
        if (evTable == null) return;

        Collection<CyRow> evidenceRows = evTable.getMatchingRows(EDGE_SUID, cyE.getSUID());
        if (!evidenceRows.isEmpty()) {
            CyRow row = evidenceRows.iterator().next();
            Long networkSUID = row.get(NETWORK_SUID, Long.class);
            CyNetwork cyN = networkManager.getNetwork(networkSUID);
            if (cyN == null) return;
            CyEdge edge = cyN.getEdge(row.get(EDGE_SUID, Long.class));
            if (edge == null) return;

            Evidence[] evidences = belEvidenceMapper.mapFromTable(edge, evTable);
            evidenceListPanel.update(asList(evidences), this);
        } else {
            evidenceListPanel.update(new ArrayList<>(), this);
        }
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
            evidenceListPanel.update(asList(evidences), this);
        } else {
            evidenceListPanel.update(new ArrayList<>(), this);
        }
    }
}
