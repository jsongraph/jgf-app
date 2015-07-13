package info.json_graph_format.jgfapp.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.json_graph_format.jgfapp.api.EvidenceReader;
import info.json_graph_format.jgfapp.api.model.Evidence;

import java.io.IOException;

public class EvidenceReaderImpl implements EvidenceReader {

    private final ObjectMapper mapper;

    public EvidenceReaderImpl() {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public Evidence read(String json) throws IOException {
        return mapper.readValue(json, Evidence.class);
    }
}
