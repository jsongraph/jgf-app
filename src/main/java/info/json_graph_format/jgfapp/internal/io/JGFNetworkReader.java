package info.json_graph_format.jgfapp.internal.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import info.json_graph_format.jgfapp.api.GraphConverter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.model.*;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;
import info.json_graph_format.jgfapp.api.BELEvidenceMapper;
import info.json_graph_format.jgfapp.api.GraphReader;
import info.json_graph_format.jgfapp.api.GraphsWithValidation;
import info.json_graph_format.jgfapp.api.model.Edge;
import info.json_graph_format.jgfapp.api.model.Evidence;
import info.json_graph_format.jgfapp.api.util.StyleUtility;
import info.json_graph_format.jgfapp.api.model.Graph;

import javax.swing.*;
import org.cytoscape.io.read.CyNetworkReader;

import static java.lang.String.format;
import static info.json_graph_format.jgfapp.api.util.FormatUtility.getSchemaMessages;
import static info.json_graph_format.jgfapp.api.util.TableUtility.getTable;
import static info.json_graph_format.jgfapp.internal.Constants.APPLIED_VISUAL_STYLE;

/**
 * {@link JGFNetworkReader} implements a {@link CyNetworkReader} to allow creation
 * of {@link CyNetwork networks} and {@link CyNetworkView views} from BEL JSON
 * {@link Graph graphs}.
 */
public class JGFNetworkReader extends AbstractCyNetworkReader {

    protected final InputStream inputStream;
    protected final String inputName;
    protected final CyApplicationManager appMgr;
    protected final CyNetworkManager networkMgr;
    protected final CyTableFactory tableFactory;
    protected final CyTableManager tableMgr;
    protected final VisualMappingManager visMgr;
    protected final CyEventHelper eventHelper;
    protected final GraphReader graphReader;
    protected final GraphConverter belGraphConverter;
    protected final BELEvidenceMapper belEvidenceMapper;

    public JGFNetworkReader(InputStream inputStream, String inputName,
            GraphReader graphReader, GraphConverter belGraphConverter,
            BELEvidenceMapper belEvidenceMapper, CyApplicationManager appMgr,
            CyNetworkViewFactory networkVieFactory, CyNetworkFactory networkFactory,
            CyNetworkManager networkMgr, CyRootNetworkManager rootNetworkMgr,
            CyTableFactory tableFactory, CyTableManager tableMgr,
            VisualMappingManager visMgr, CyEventHelper eventHelper) {
        super(inputStream, networkVieFactory, networkFactory, networkMgr, rootNetworkMgr);

        if (inputStream == null) throw new NullPointerException("inputStream cannot be null");
        if (inputName == null) throw new NullPointerException("inputName cannot be null");
        if (appMgr == null) throw new NullPointerException("appMgr cannot be null");
        if (networkMgr == null) throw new NullPointerException("networkMgr cannot be null");
        this.inputStream = inputStream;
        this.inputName = inputName;
        this.graphReader = graphReader;
        this.belGraphConverter = belGraphConverter;
        this.belEvidenceMapper = belEvidenceMapper;
        this.appMgr = appMgr;
        this.networkMgr = networkMgr;
        this.tableFactory = tableFactory;
        this.tableMgr = tableMgr;
        this.visMgr = visMgr;
        this.eventHelper = eventHelper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CyNetworkView buildCyNetworkView(CyNetwork network) {
        final CyNetworkView view = cyNetworkViewFactory.createNetworkView(network);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // find the style we would like to apply...
                VisualStyle style = StyleUtility.findVisualStyleByTitle(APPLIED_VISUAL_STYLE, visMgr);

                // ...return if view or style do not exist
                if (view == null || style == null) return;

                // ...wait for view to be active before setting visual style
                while (view != appMgr.getCurrentNetworkView()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }

                // ...then set visual style and fit network content in window
                eventHelper.flushPayloadEvents();
                visMgr.setVisualStyle(style, view);
                style.apply(view);
                view.fitContent();
                view.updateView();
            }
        });

        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(TaskMonitor m) throws Exception {
        m.setTitle("Import BEL JSON Graph");

        m.setStatusMessage(format("Read and validate \"%s\" against the BEL JSON Graph schema.", inputName));
        GraphsWithValidation gv = checkSchema(inputStream, graphReader);
        Graph[] graphs = gv.getGraphs();
        m.setProgress(0.50);

        m.setStatusMessage(format("Creating %d networks from \"%s\".", graphs.length, inputName));
        this.networks = mapNetworks(graphs);
        for (CyNetwork n : this.networks)
            networkMgr.addNetwork(n);

        // set up evidence table if we have cytoscape networks
        if (this.networks.length > 0) {
            CyTable table = getOrCreateEvidenceTable();
            mapGraphsToEvidenceTable(graphs, table);
            tableMgr.addTable(table);
        }
        m.setProgress(1.0);
    }

    /**
     * Validates the BEL JSON graph against its schema and returns a
     * {@link GraphsWithValidation} result.
     * <br><br>
     * Validation errors are conveyed to the user by popping open a {@link JOptionPane}
     * and throwing {@link RuntimeException} which will stop the current task.
     *
     * @param inputStream {@link InputStream} input
     * @param graphReader {@link info.json_graph_format.jgfapp.api.GraphReader} graph reader
     * @return {@link GraphsWithValidation} when validation succeeds
     * @throws IOException when an IO error occurred reading {@code inputStream}
     * @throws RuntimeException when schema validation does not succeed, the user
     * will receive the error
     */
    protected GraphsWithValidation checkSchema(InputStream inputStream, GraphReader graphReader) throws IOException {
        final GraphsWithValidation gv = graphReader.validatingRead(inputStream);
        final ProcessingReport report = gv.getValidationReport();
        if (!report.isSuccess()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String msg = format("Schema validation error: \n\n%s", getSchemaMessages(report));
                    JOptionPane.showMessageDialog(null, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            String msg = format("Schema validation error, details in Task History: %s", getSchemaMessages(report));
            throw new RuntimeException(msg);
        }
        return gv;
    }

    /**
     * Convert an array of {@link Graph} to array of {@link CyNetwork}.
     *
     * @param graphs {@link Graph} array
     * @return array of {@link CyNetwork}
     */
    protected CyNetwork[] mapNetworks(Graph[] graphs) {
        List<CyNetwork> cyNetworks = new ArrayList<CyNetwork>();
        for (Graph graph : graphs) {
            cyNetworks.add(belGraphConverter.convert(graph));
        }
        return cyNetworks.toArray(new CyNetwork[cyNetworks.size()]);
    }

    protected CyTable getOrCreateEvidenceTable() {
        CyTable tbl = getTable("BEL.Evidence", tableMgr);
        if (tbl != null) return tbl;

        tbl = tableFactory.createTable("BEL.Evidence", "SUID", Long.class, true, false);
        tbl.setSavePolicy(SavePolicy.SESSION_FILE);
        tbl.createColumn("network suid", Long.class, true, null);
        tbl.createColumn("network name", String.class, true);
        tbl.createColumn("edge suid", Long.class, true, null);
        tbl.createColumn("bel statement", String.class, false);
        tbl.createColumn("citation type", String.class, false);
        tbl.createColumn("citation id", String.class, false);
        tbl.createColumn("citation name", String.class, false);
        tbl.createColumn("summary text", String.class, false);
        tbl.createColumn("species", String.class, false);
        return tbl;
    }

    protected void mapGraphsToEvidenceTable(Graph[] graphs, CyTable table) {
        for (Graph graph : graphs) {
            for (Edge edge : graph.edges) {
                Evidence[] evidences = belEvidenceMapper.mapEdgeToEvidence(graph, edge);
                for (Evidence ev : evidences) {
                    belEvidenceMapper.mapToTable(graph, edge, ev, table);
                }
            }
        }
    }
}
