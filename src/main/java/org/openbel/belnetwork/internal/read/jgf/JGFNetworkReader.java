package org.openbel.belnetwork.internal.read.jgf;

import java.io.InputStream;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.openbel.belnetwork.internal.mapperclasses.Graph;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;
import org.openbel.belnetwork.internal.util.StyleUtility;

import javax.swing.SwingUtilities;

import static org.openbel.belnetwork.internal.Constants.APPLIED_VISUAL_STYLE;

public class JGFNetworkReader extends AbstractCyNetworkReader {

    private final InputStream inputStream;
    private final CyApplicationManager appMgr;
    private final CyNetworkManager networkMgr;
    private final CyTableFactory tableFactory;
    private final CyTableManager tableMgr;
    private final VisualMappingManager visMgr;
    private final CyEventHelper eventHelper;

    public JGFNetworkReader(InputStream inputStream, CyApplicationManager appMgr,
            CyNetworkViewFactory networkVieFactory, CyNetworkFactory networkFactory,
            CyNetworkManager networkMgr, CyRootNetworkManager rootNetworkMgr,
            CyTableFactory tableFactory, CyTableManager tableMgr,
            VisualMappingManager visMgr, CyEventHelper eventHelper) {
        super(inputStream, networkVieFactory, networkFactory, networkMgr, rootNetworkMgr);
        
        if (inputStream == null) throw new NullPointerException("inputStream cannot be null");
        if (appMgr == null) throw new NullPointerException("appMgr cannot be null");
        if (networkMgr == null) throw new NullPointerException("networkMgr cannot be null");
        this.inputStream = inputStream;
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
    public void run(TaskMonitor taskMonitor) throws Exception {
        final CyNetwork network = cyNetworkFactory.createNetwork();
        JsonToNetworkConverter converter = new JsonToNetworkConverter();
        this.networks = new CyNetwork[1];
        //Why the collection of networks?  Why are we only updating networks[0]?
        
        try {
            Graph graph =  converter.createGraph(inputStream);
            this.networks[0] = converter.createNetwork(graph, network, tableFactory, tableMgr);
            networkMgr.addNetwork(network);
            
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