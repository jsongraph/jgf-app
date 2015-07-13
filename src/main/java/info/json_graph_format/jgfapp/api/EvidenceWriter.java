package info.json_graph_format.jgfapp.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import info.json_graph_format.jgfapp.api.model.Evidence;

import java.io.IOException;
import java.io.OutputStream;

public interface EvidenceWriter {

    void write(Evidence evidence, OutputStream output) throws IOException;

    String serialize(Evidence evidence) throws JsonProcessingException;
}
