package org.openbel.belnetwork.internal.read.jgf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;
import org.openbel.belnetwork.api.BELGraphConverter;
import org.openbel.belnetwork.api.BELGraphReader;
import org.openbel.belnetwork.api.GraphsWithValidation;
import org.openbel.belnetwork.internal.util.StyleUtility;
import org.openbel.belnetwork.model.Graph;

import javax.swing.*;

import static java.lang.String.format;
import static org.openbel.belnetwork.api.FormatUtility.getSchemaMessages;
import static org.openbel.belnetwork.internal.Constants.APPLIED_VISUAL_STYLE;

public class JGFNetworkReader extends AbstractCyNetworkReader {

    protected final InputStream inputStream;
    protected final String inputName;
    protected final CyApplicationManager appMgr;
    protected final CyNetworkManager networkMgr;
    protected final CyTableFactory tableFactory;
    protected final CyTableManager tableMgr;
    protected final VisualMappingManager visMgr;
    protected final CyEventHelper eventHelper;
    protected final BELGraphReader belGraphReader;
    protected final BELGraphConverter belGraphConverter;

    public JGFNetworkReader(InputStream inputStream, String inputName,
            BELGraphReader belGraphReader, BELGraphConverter belGraphConverter,
            CyApplicationManager appMgr, CyNetworkViewFactory networkVieFactory,
            CyNetworkFactory networkFactory, CyNetworkManager networkMgr,
            CyRootNetworkManager rootNetworkMgr, CyTableFactory tableFactory,
            CyTableManager tableMgr, VisualMappingManager visMgr,
            CyEventHelper eventHelper) {
        super(inputStream, networkVieFactory, networkFactory, networkMgr, rootNetworkMgr);

        if (inputStream == null) throw new NullPointerException("inputStream cannot be null");
        if (inputName == null) throw new NullPointerException("inputName cannot be null");
        if (appMgr == null) throw new NullPointerException("appMgr cannot be null");
        if (networkMgr == null) throw new NullPointerException("networkMgr cannot be null");
        this.inputStream = inputStream;
        this.inputName = inputName;
        this.belGraphReader = belGraphReader;
        this.belGraphConverter = belGraphConverter;
        this.appMgr = appMgr;
        this.networkMgr = networkMgr;
        this.tableFactory = tableFactory;
        this.tableMgr = tableMgr;
        this.visMgr = visMgr;
        this.eventHelper = eventHelper;
    }

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

    @Override
    public void run(TaskMonitor m) throws Exception {
        m.setTitle(format("Import BEL JSON Graph"));

        m.setStatusMessage(format("Read and validate \"%s\" against the BEL JSON Graph schema.", inputName));
        GraphsWithValidation gv = checkSchema(inputStream, inputName, belGraphReader);
        Graph[] graphs = gv.getGraphs();
        m.setProgress(0.50);

        m.setStatusMessage(format("Creating %d networks from \"%s\".", graphs.length, inputName));
        this.networks = mapNetworks(graphs);
        for (CyNetwork n : this.networks)
            networkMgr.addNetwork(n);
        m.setProgress(1.0);
    }

    protected GraphsWithValidation checkSchema(InputStream inputStream, String inputName, BELGraphReader belGraphReader) throws IOException {
        final GraphsWithValidation gv = belGraphReader.validatingRead(inputStream);
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

    protected CyNetwork[] mapNetworks(Graph[] graphs) {
        List<CyNetwork> cyNetworks = new ArrayList<CyNetwork>();
        for (Graph graph : graphs) {
            cyNetworks.add(belGraphConverter.convert(graph));
        }
        return cyNetworks.toArray(new CyNetwork[cyNetworks.size()]);
    }
}
