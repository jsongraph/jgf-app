package info.json_graph_format.jgfapp.internal.io;

import info.json_graph_format.jgfapp.api.GraphConverter;
import info.json_graph_format.jgfapp.api.GraphWriter;
import info.json_graph_format.jgfapp.api.model.Graph;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

public class JGFWriter implements CyWriter {

    private final static Logger logger = LoggerFactory.getLogger(JGFWriter.class);

    private final OutputStream output;
    private final CyNetwork cyN;
    private final CyNetworkView cyNv;
    private final GraphConverter graphConverter;
    private final GraphWriter graphWriter;
    private final CharsetEncoder charsetEncoder;

    public JGFWriter(OutputStream output, CyNetwork cyN, CyNetworkView cyNv,
                     GraphConverter graphConverter, GraphWriter graphWriter) {
        Objects.requireNonNull(output,         "output cannot be null");
        Objects.requireNonNull(cyN,            "cyN cannot be null");
        Objects.requireNonNull(graphConverter, "graphConverter cannot be null");
        Objects.requireNonNull(graphWriter,    "graphWriter cannot be null");

        this.output         = output;
        this.cyN            = cyN;
        this.cyNv           = cyNv;
        this.graphConverter = graphConverter;
        this.graphWriter    = graphWriter;
        this.charsetEncoder = singletonList("UTF-8").
                stream().
                map(Charset::forName).
                findFirst().orElseGet(Charset::defaultCharset).newEncoder();
    }

    @Override
    public void run(TaskMonitor m) throws Exception {
        if (Objects.nonNull(m)) {
            m.setProgress(0.0);
            m.setTitle("Exporting to JGF");

            String status = "The \"%s\" network is being exported to JGF.";
            m.setStatusMessage(format(status, cyN.getRow(cyN).get("name", String.class)));
        }

        try (final OutputStreamWriter writer = new OutputStreamWriter(output, charsetEncoder)) {
            Graph graph = graphConverter.convert(cyN, cyNv);
            graphWriter.write(graph, writer);

            m.setProgress(1.0);
        }
    }

    @Override
    public void cancel() {
        try {
            output.close();
        } catch (IOException e) {
            logger.error("Error closing output for " + JGFWriter.class.getSimpleName(), e);
        }
    }
}
