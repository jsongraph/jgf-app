package info.json_graph_format.jgfapp.internal.listeners;

import org.cytoscape.io.read.VizmapReaderManager;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import info.json_graph_format.jgfapp.internal.CyActivator;

public class SessionListener implements SessionLoadedListener {

    private final VisualMappingManager visMgr;
    private final VizmapReaderManager vizmapReaderMgr;

    public SessionListener(VisualMappingManager visMgr, VizmapReaderManager vizmapReaderMgr) {
        if (visMgr == null) throw new NullPointerException("visMgr cannot be null");
        if (vizmapReaderMgr == null) throw new NullPointerException("vizmapReaderMgr cannot be null");
        this.visMgr = visMgr;
        this.vizmapReaderMgr = vizmapReaderMgr;
    }

    @Override
    public void handleEvent(SessionLoadedEvent event) {
        CyActivator.contributeStyles(visMgr, vizmapReaderMgr);
    }
}
