package info.json_graph_format.jgfapp.api;

import info.json_graph_format.jgfapp.api.model.Graph;

import java.io.OutputStream;

public interface GraphWriter {

    /**
     * Writes a {@link Graph graph} model to the
     * {@link OutputStream output stream}.
     *
     * @param graph {@link Graph} written to {@code output};
     *        may not be {@code null}
     * @param output {@link OutputStream} to which {@code graph} is written;
     *        may not be {@code null}
     */
    void write(Graph graph, OutputStream output);
}
