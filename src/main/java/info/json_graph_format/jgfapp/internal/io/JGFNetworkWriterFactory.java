package info.json_graph_format.jgfapp.internal.io;

import info.json_graph_format.jgfapp.api.GraphConverter;
import info.json_graph_format.jgfapp.api.GraphWriter;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

import java.io.OutputStream;

public class JGFNetworkWriterFactory implements CyNetworkViewWriterFactory {

    private final CyFileFilter jgfFileFilter;
    private final GraphConverter graphConverter;
    private final GraphWriter graphWriter;

    public JGFNetworkWriterFactory(CyFileFilter jgfFileFilter, GraphConverter graphConverter,
                                   GraphWriter graphWriter) {
        this.jgfFileFilter  = jgfFileFilter;
        this.graphConverter = graphConverter;
        this.graphWriter    = graphWriter;
    }

    @Override
    public CyWriter createWriter(OutputStream output, CyNetworkView cyNv) {
        return new JGFWriter(output, cyNv.getModel(), graphConverter, graphWriter);
    }

    @Override
    public CyWriter createWriter(OutputStream output, CyNetwork cyN) {
        return new JGFWriter(output, cyN, graphConverter, graphWriter);
    }

    @Override
    public CyFileFilter getFileFilter() {
        return jgfFileFilter;
    }
}
