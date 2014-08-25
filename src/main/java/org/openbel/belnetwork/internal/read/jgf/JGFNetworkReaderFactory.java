package org.openbel.belnetwork.internal.read.jgf;

import java.io.InputStream;

import org.cytoscape.io.CyFileFilter;
import org.openbel.belnetwork.internal.JGFVisualStyleBuilder;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;

public class JGFNetworkReaderFactory extends AbstractReaderFactory {
    
    private final CyNetworkManager cyNetworkManager;
    private final CyRootNetworkManager cyRootNetworkManager;
    
    private final JGFVisualStyleBuilder vsBuilder;
    private final VisualMappingManager vmm;
    
    private final CyTableFactory cyTableFactory;
    private final CyTableManager cyTableManager;
    
    public JGFNetworkReaderFactory(final CyFileFilter filter, final CyNetworkViewFactory cyNetworkViewFactory,
            final CyNetworkFactory cyNetworkFactory, final CyNetworkManager cyNetworkManager,
            final CyRootNetworkManager cyRootNetworkManager, JGFVisualStyleBuilder vsBuilder,
            VisualMappingManager vmm, CyTableFactory cyTableFactory, CyTableManager cyTableManager ) {
        super(filter, cyNetworkViewFactory, cyNetworkFactory);
        
        this.cyNetworkManager = cyNetworkManager;
        this.cyRootNetworkManager = cyRootNetworkManager;
        this.vsBuilder = vsBuilder;
        this.vmm = vmm;
        this.cyTableFactory = cyTableFactory;
        this.cyTableManager = cyTableManager;
    }

    @Override
    public TaskIterator createTaskIterator(InputStream is, String inputName) {
        return new TaskIterator(new JGFNetworkReader(is, cyNetworkViewFactory, cyNetworkFactory,
                cyNetworkManager, cyRootNetworkManager, vsBuilder, vmm, cyTableFactory, cyTableManager ));
    }

}
