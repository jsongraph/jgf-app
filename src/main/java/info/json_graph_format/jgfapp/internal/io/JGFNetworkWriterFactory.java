package info.json_graph_format.jgfapp.internal.io;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

import java.io.OutputStream;

public class JGFNetworkWriterFactory implements CyNetworkViewWriterFactory {

    private final CyFileFilter jgfFileFilter;

    public JGFNetworkWriterFactory(CyFileFilter jgfFileFilter) {
        this.jgfFileFilter = jgfFileFilter;
    }

    @Override
    public CyWriter createWriter(OutputStream output, CyNetworkView cyNv) {
        return new JGFWriter(output, cyNv.getModel());
    }

    @Override
    public CyWriter createWriter(OutputStream output, CyNetwork cyN) {
        return new JGFWriter(output, cyN);
    }

    @Override
    public CyFileFilter getFileFilter() {
        return jgfFileFilter;
    }
}
