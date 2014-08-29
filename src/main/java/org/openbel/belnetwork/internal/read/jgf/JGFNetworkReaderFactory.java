package org.openbel.belnetwork.internal.read.jgf;

import java.io.InputStream;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskIterator;
import org.openbel.belnetwork.api.BELGraphConverter;
import org.openbel.belnetwork.api.BELGraphReader;

public class JGFNetworkReaderFactory extends AbstractReaderFactory {

    private final CyApplicationManager appMgr;
    private final CyNetworkManager cyNetworkManager;
    private final CyRootNetworkManager cyRootNetworkManager;
    private final CyTableFactory cyTableFactory;
    private final CyTableManager cyTableManager;
    private final VisualMappingManager visMgr;
    private final CyEventHelper eventHelper;
    private final BELGraphReader belGraphReader;
    private final BELGraphConverter belGraphConverter;

    public JGFNetworkReaderFactory(final CyFileFilter filter, final CyApplicationManager appMgr,
            final CyNetworkViewFactory cyNetworkViewFactory, final CyNetworkFactory cyNetworkFactory,
            final CyNetworkManager cyNetworkManager, final CyRootNetworkManager cyRootNetworkManager,
            CyTableFactory cyTableFactory, CyTableManager cyTableManager, VisualMappingManager visMgr,
            CyEventHelper eventHelper, BELGraphReader belGraphReader, BELGraphConverter belGraphConverter) {
        super(filter, cyNetworkViewFactory, cyNetworkFactory);

        this.appMgr = appMgr;
        this.cyNetworkManager = cyNetworkManager;
        this.cyRootNetworkManager = cyRootNetworkManager;
        this.cyTableFactory = cyTableFactory;
        this.cyTableManager = cyTableManager;
        this.visMgr = visMgr;
        this.eventHelper = eventHelper;
        this.belGraphReader = belGraphReader;
        this.belGraphConverter = belGraphConverter;
    }

    @Override
    public TaskIterator createTaskIterator(InputStream is, String inputName) {
        return new TaskIterator(
                new JGFNetworkReader(is, inputName, belGraphReader, belGraphConverter, appMgr, cyNetworkViewFactory,
                                     cyNetworkFactory, cyNetworkManager, cyRootNetworkManager,
                                     cyTableFactory, cyTableManager, visMgr, eventHelper));
    }
}
