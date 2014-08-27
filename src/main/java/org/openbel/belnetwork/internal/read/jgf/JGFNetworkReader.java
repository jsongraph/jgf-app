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

import javax.swing.*;

import static org.openbel.belnetwork.internal.Constants.APPLIED_VISUAL_STYLE;

public class JGFNetworkReader extends AbstractCyNetworkReader {

    private final InputStream inputStream;
    private final CyApplicationManager appMgr;
    private final CyNetworkManager cyNetManager;
    private final CyTableFactory cyTableFactory;
    private final CyTableManager cyTableManager;
    private final VisualMappingManager visMgr;
    private final CyEventHelper eventHelper;

    public JGFNetworkReader(InputStream inputStream, CyApplicationManager appMgr,
            CyNetworkViewFactory cyNetworkViewFactory, CyNetworkFactory cyNetworkFactory,
            CyNetworkManager cyNetworkManager, CyRootNetworkManager cyRootNetworkManager,
            CyTableFactory cyTableFactory, CyTableManager cyTableManager,
            VisualMappingManager visMgr, CyEventHelper eventHelper) {
        super(inputStream, cyNetworkViewFactory, cyNetworkFactory, cyNetworkManager, cyRootNetworkManager);
        
        if (inputStream == null) throw new NullPointerException("inputStream cannot be null.");
        this.inputStream = inputStream;
        this.appMgr = appMgr;
        this.cyNetManager = cyNetworkManager;
        this.cyTableFactory = cyTableFactory;
        this.cyTableManager = cyTableManager;
        this.visMgr = visMgr;
        this.eventHelper = eventHelper;
    }

    @Override
    public CyNetworkView buildCyNetworkView(CyNetwork network) {
        final CyNetworkView view = cyNetworkViewFactory.createNetworkView(network);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                while (view != appMgr.getCurrentNetworkView()) {
                    System.out.println("Nope!");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {}
                }
                VisualStyle style = StyleUtility.findVisualStyleByTitle(APPLIED_VISUAL_STYLE, visMgr);
                if (view == null || style == null) {
                    return;
                }

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
            Graph graph =  converter.CreateGraph(inputStream);
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