package info.json_graph_format.jgfapp.internal;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import info.json_graph_format.jgfapp.api.GraphWriter;
import info.json_graph_format.jgfapp.api.model.Graph;

import java.io.IOException;
import java.io.Writer;

public class GraphWriterImpl implements GraphWriter {

    protected final JsonFactory jsonFactory;
    protected final ObjectMapper mapper;

    public GraphWriterImpl() {
        jsonFactory = new JsonFactory();
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
    }

    @Override
    public void write(Graph graph, Writer writer) throws IOException {
        mapper.writeValue(writer, graph);
    }
}
