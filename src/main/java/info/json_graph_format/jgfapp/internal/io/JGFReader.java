package info.json_graph_format.jgfapp.internal.io;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import info.json_graph_format.jgfapp.api.BELEvidenceMapper;
import info.json_graph_format.jgfapp.api.GraphConverter;
import info.json_graph_format.jgfapp.api.GraphReader;
import info.json_graph_format.jgfapp.api.GraphsWithValidation;
import info.json_graph_format.jgfapp.api.model.Edge;
import info.json_graph_format.jgfapp.api.model.Evidence;
import info.json_graph_format.jgfapp.api.model.Graph;
import info.json_graph_format.jgfapp.api.util.StyleUtility;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.model.*;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskMonitor;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static info.json_graph_format.jgfapp.api.util.FormatUtility.getSchemaMessages;
import static info.json_graph_format.jgfapp.api.util.TableUtility.getTable;
import static info.json_graph_format.jgfapp.internal.Constants.*;
import static java.lang.String.format;

/**
 * {@link JGFReader} implements a {@link CyNetworkReader} to allow creation
 * of {@link CyNetwork networks} and {@link CyNetworkView views} from BEL JSON
 * {@link Graph graphs}.
 */
public class JGFReader extends AbstractCyNetworkReader {

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
    protected final CyNetworkViewManager networkViewMgr;

    public JGFReader(InputStream inputStream, String inputName,
                     GraphReader graphReader, GraphConverter belGraphConverter,
                     BELEvidenceMapper belEvidenceMapper, CyApplicationManager appMgr,
                     CyNetworkViewFactory networkViewFactory, CyNetworkFactory networkFactory,
                     CyNetworkManager networkMgr, CyNetworkViewManager networkViewMgr,
                     CyRootNetworkManager rootNetworkMgr, CyTableFactory tableFactory,
                     CyTableManager tableMgr, VisualMappingManager visMgr, CyEventHelper eventHelper) {
        super(inputStream, networkViewFactory, networkFactory, networkMgr, rootNetworkMgr);

        if (inputName == null) throw new NullPointerException("inputName cannot be null");
        if (appMgr == null) throw new NullPointerException("appMgr cannot be null");
        this.inputStream = inputStream;
        this.inputName = inputName;
        this.graphReader = graphReader;
        this.belGraphConverter = belGraphConverter;
        this.belEvidenceMapper = belEvidenceMapper;
        this.appMgr = appMgr;
        this.networkMgr = networkMgr;
        this.networkViewMgr = networkViewMgr;
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

        SwingUtilities.invokeLater(() -> {
            // find the style we would like to apply...
            VisualStyle style = StyleUtility.findVisualStyleByTitle(APPLIED_VISUAL_STYLE, visMgr);

            // ...return if view or style do not exist
            if (view == null || style == null) return;

            // ...wait for view to be active before setting visual style
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            Set<CyNetworkView> views = networkViewMgr.getNetworkViewSet();

            if (views.contains(view)) {
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
     * @throws IOException      when an IO error occurred reading {@code inputStream}
     * @throws RuntimeException when schema validation does not succeed, the user
     *                          will receive the error
     */
    protected GraphsWithValidation checkSchema(InputStream inputStream, GraphReader graphReader) throws IOException {
        final GraphsWithValidation gv = graphReader.validatingRead(inputStream);
        final ProcessingReport report = gv.getValidationReport();
        if (!report.isSuccess()) {
            SwingUtilities.invokeLater(() -> {
                String msg = format("Schema validation error: \n\n%s", getSchemaMessages(report));
                JOptionPane.showMessageDialog(null, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
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
        List<CyNetwork> cyNetworks = new ArrayList<>();
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
        tbl.createColumn(NETWORK_SUID, Long.class, true, null);
        tbl.createColumn(NETWORK_NAME, String.class, true);
        tbl.createColumn(EDGE_SUID, Long.class, true, null);
        tbl.createColumn(BEL_STATEMENT, String.class, false);
        tbl.createColumn(CITATION_TYPE, String.class, false);
        tbl.createColumn(CITATION_ID, String.class, false);
        tbl.createColumn(CITATION_NAME, String.class, false);
        tbl.createColumn(SUMMARY_TEXT, String.class, false);
        return tbl;
    }

    protected void mapGraphsToEvidenceTable(Graph[] graphs, CyTable table) {
//        List<Evidence> allEvidence = Arrays.stream(graphs).
//                flatMap(g -> g.edges.stream()).
//                flatMap(edge -> Arrays.stream(belEvidenceMapper.mapEdgeToEvidence(edge))).
//                collect(toList());
//        Map<String, Class> experimentContextValueClasses = determineTypes(allEvidence, e -> e.experimentContext);
//        Map<String, Class> metadataValueClasses          = determineTypes(allEvidence, e -> e.metadata);

        for (Graph graph : graphs) {
            for (Edge edge : graph.edges) {
                Evidence[] evidences = belEvidenceMapper.mapEdgeToEvidence(edge);
                for (Evidence ev : evidences) {
                    belEvidenceMapper.mapToTable(graph, edge, ev, table);
                }
            }
        }
    }

//    private static Map<String, Class> determineTypes(List<Evidence> evidence,
//                                                     Function<Evidence, Map<String, Object>> mappingFunction) {
//        Map<String, Class> classMap = new HashMap<>();
//        for (Evidence ev : evidence) {
//            Map<String, Object> data = mappingFunction.apply(ev);
//            for (Map.Entry<String, Object> entry : data.entrySet()) {
//                String key = entry.getKey();
//                Class<?> klass = entry.getValue().getClass();
//
//                Class<?> recordedClass = classMap.get(key);
//                if (recordedClass == null) {
//                    classMap.put(key, klass);
//                } else {
//                    if (!klass.equals(recordedClass)) {
//                        classMap.put(key, String.class);
//                    }
//                }
//            }
//        }
//        return classMap;
//    }
}
