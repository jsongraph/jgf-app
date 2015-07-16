package info.json_graph_format.jgfapp.api;

import info.json_graph_format.jgfapp.api.model.Graph;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface GraphWriter {

    /**
     * Writes a {@link Graph graph} model to the
     * {@link OutputStream output stream}.
     *
     * @param graph {@link Graph} written to {@code writer};
     *        may not be {@code null}
     * @param writer {@link Writer} to which {@code graph} is written;
     *        may not be {@code null}
     */
    void write(Graph graph, Writer writer) throws IOException;
}
