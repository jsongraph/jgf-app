package info.json_graph_format.jgfapp.internal.io;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
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
    private final CharsetEncoder charsetEncoder;

    public JGFWriter(OutputStream output, CyNetwork cyN) {
        Objects.requireNonNull(output, "output cannot be null");
        Objects.requireNonNull(cyN, "cyN cannot be null");

        this.output = output;
        this.cyN = cyN;

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

        try (final OutputStreamWriter streamWriter = new OutputStreamWriter(output, charsetEncoder)) {
            streamWriter.write("{}");
        }

        m.setProgress(1.0);
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
