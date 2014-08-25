package org.cytoscape.jgfnetwork.internal.read.jgf;

import java.io.InputStream;

import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.jgfnetwork.internal.JGFVisualStyleBuilder;
import org.cytoscape.jgfnetwork.internal.mapperclasses.Graph;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory; //added
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskMonitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JGFNetworkReader extends AbstractCyNetworkReader {
    
    private static final String PACKAGE_NAME = "org.cytoscape.jgfnetwork.internal.generated";

    private Graph graph;
    private final InputStream is;
    private JGFMapper mapper;
    
    private final VisualMappingManager vmm;
    private final JGFVisualStyleBuilder vsBuilder;
    
    private final CyNetworkViewFactory cyNetViewFactory;
    private final CyNetworkFactory cyNetFactory;
    private final CyNetworkManager cyNetManager;
    
    private final CyTableFactory cyTableFactory;
    private final CyTableManager cyTableManager;
    
    public JGFNetworkReader(InputStream is, CyNetworkViewFactory cyNetworkViewFactory,
            CyNetworkFactory cyNetworkFactory, CyNetworkManager cyNetworkManager,
            CyRootNetworkManager cyRootNetworkManager, final JGFVisualStyleBuilder vsBuilder,
            final VisualMappingManager vmm, CyTableFactory cyTableFactory, CyTableManager cyTableManager ) {
        super(is, cyNetworkViewFactory, cyNetworkFactory, cyNetworkManager, cyRootNetworkManager);
        
        if (is == null) {
            throw new NullPointerException("Input Stream cannot be null.");
        }
        
        
        this.is = is;
        this.vmm = vmm;
        this.vsBuilder = vsBuilder;
        this.cyNetFactory = cyNetworkFactory;
        this.cyNetManager = cyNetworkManager;
        this.cyTableFactory = cyTableFactory;
        this.cyTableManager = cyTableManager;
        this.cyNetViewFactory = cyNetworkViewFactory;

    }
    
    
    
    @Override
    public CyNetworkView buildCyNetworkView(CyNetwork network) {
        final CyNetworkView view = cyNetworkViewFactory.createNetworkView(network);
            
        //vsBuilder.LoadApplyVizMap(); // need to write this
        // get the VizmapFileTaskFactory from constructor parameter.
         // Use the service to load visual style, 'f' is the File object to hold the visual properties 
         // Set<VisualStyle> vsSet = loadVizmapFileTaskFactory.loadStyles(f);

        return view;
    }

    
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {

        final CyNetwork network = cyNetworkFactory.createNetwork();    
        JsonToNetworkConverter converter = new JsonToNetworkConverter();
        this.networks = new CyNetwork[1];
        //Why the collection of networks?  Why are we only updating networks[0]?
        
        try {
            Graph graph =  converter.CreateGraph(is);
            this.networks[0] = converter.CreateNetwork(graph, network, cyTableFactory, cyTableManager);    
            cyNetManager.addNetwork(network);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
        
//        While building your network, create instances of DelayedVizProp and store them in a List. 
//        After calling org.cytoscape.event.CyEventHelper.flushPayloadEvents(), 
//        call applyAll on the network view that contains your nodes and edges.
//        
        
//    
//        VisualStyle jgfStyle = null;
//        vmm.setCurrentVisualStyle(jgfStyle);
    
    }

    
}
