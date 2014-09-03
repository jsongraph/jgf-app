package info.json_graph_format.jgfapp.internal.io;

import java.io.InputStream;

import info.json_graph_format.jgfapp.api.GraphConverter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskIterator;
import info.json_graph_format.jgfapp.api.BELEvidenceMapper;
import info.json_graph_format.jgfapp.api.GraphReader;

public class JGFNetworkReaderFactory implements InputStreamTaskFactory {

    private final CyFileFilter cyFileFilter;
    private final CyApplicationManager appMgr;
    private final CyNetworkFactory cyNetworkFactory;
    private final CyNetworkViewFactory cyNetworkViewFactory;
    private final CyNetworkManager cyNetworkManager;
    private final CyRootNetworkManager cyRootNetworkManager;
    private final CyTableFactory cyTableFactory;
    private final CyTableManager cyTableManager;
    private final VisualMappingManager visMgr;
    private final CyEventHelper eventHelper;
    private final GraphReader graphReader;
    private final GraphConverter belGraphConverter;
    private final BELEvidenceMapper belEvidenceMapper;

    public JGFNetworkReaderFactory(final CyFileFilter cyFileFilter, final CyApplicationManager appMgr,
            final CyNetworkViewFactory cyNetworkViewFactory, final CyNetworkFactory cyNetworkFactory,
            final CyNetworkManager cyNetworkManager, final CyRootNetworkManager cyRootNetworkManager,
            CyTableFactory cyTableFactory, CyTableManager cyTableManager, VisualMappingManager visMgr,
            CyEventHelper eventHelper, GraphReader graphReader, GraphConverter belGraphConverter,
            BELEvidenceMapper belEvidenceMapper) {

        this.cyFileFilter = cyFileFilter;
        this.appMgr = appMgr;
        this.cyNetworkFactory = cyNetworkFactory;
        this.cyNetworkViewFactory = cyNetworkViewFactory;
        this.cyNetworkManager = cyNetworkManager;
        this.cyRootNetworkManager = cyRootNetworkManager;
        this.cyTableFactory = cyTableFactory;
        this.cyTableManager = cyTableManager;
        this.visMgr = visMgr;
        this.eventHelper = eventHelper;
        this.graphReader = graphReader;
        this.belGraphConverter = belGraphConverter;
        this.belEvidenceMapper = belEvidenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CyFileFilter getFileFilter() {
        return cyFileFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReady(final InputStream is, final String inputName) {
        return true;
    }

    @Override
    public TaskIterator createTaskIterator(InputStream is, String inputName) {
        return new TaskIterator(
                new JGFNetworkReader(is, inputName, graphReader, belGraphConverter,
                                     belEvidenceMapper, appMgr, cyNetworkViewFactory,
                                     cyNetworkFactory, cyNetworkManager, cyRootNetworkManager,
                                     cyTableFactory, cyTableManager, visMgr, eventHelper));
    }
}
