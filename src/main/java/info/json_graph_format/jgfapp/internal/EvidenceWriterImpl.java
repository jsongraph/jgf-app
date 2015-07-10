package info.json_graph_format.jgfapp.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import info.json_graph_format.jgfapp.api.EvidenceWriter;
import info.json_graph_format.jgfapp.api.model.Evidence;

import java.io.IOException;
import java.io.OutputStream;

public class EvidenceWriterImpl implements EvidenceWriter {

    protected final ObjectMapper mapper;

    public EvidenceWriterImpl() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    @Override
    public void write(Evidence evidence, OutputStream output) throws IOException {
        mapper.writeValue(output, evidence);
    }

    @Override
    public String serialize(Evidence evidence) throws JsonProcessingException {
        return mapper.writeValueAsString(evidence);
    }
}
