package info.json_graph_format.jgfapp.api;

import info.json_graph_format.jgfapp.api.model.Evidence;

import java.io.IOException;

public interface EvidenceReader {

    Evidence read(String json) throws IOException;
}
