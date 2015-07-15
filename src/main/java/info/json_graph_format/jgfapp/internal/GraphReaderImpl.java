package info.json_graph_format.jgfapp.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import info.json_graph_format.jgfapp.api.GraphReader;
import info.json_graph_format.jgfapp.api.GraphsWithValidation;
import info.json_graph_format.jgfapp.api.model.Graph;
import info.json_graph_format.jgfapp.api.model.Root;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * {@link GraphReaderImpl} implements {@link info.json_graph_format.jgfapp.api.GraphReader} to provide
 * reading and validation of content encoded in <em>JSON Graph Format</em> and
 * adhering to the <em>BEL JSON Graph</em> child schema.
 * <br><br>
 * Reading and de-serializing of <em>JSON</em> to {@link Graph} uses the
 * <a href="https://github.com/FasterXML/jackson">Jackson</a> library.
 * <br><br>
 * JSON schema validation uses the
 * <a href="https://github.com/fge/json-schema-validator">json-schema-validator</a>
 * library.
 */
public class GraphReaderImpl implements GraphReader {

    protected final ObjectMapper mapper;

    public GraphReaderImpl() {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Graph[] read(InputStream input) throws IOException {
        if (input == null) throw new NullPointerException("input cannot be null");

        return _readGraph(_readJSON(input));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Graph[] read(File input) throws IOException {
        if (input == null) throw new NullPointerException("input cannot be null");
        if (!input.exists() || !input.canRead())
            throw new FileNotFoundException("input does not exist");

        return _readGraph(_readJSON(new FileInputStream(input)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphsWithValidation validatingRead(InputStream input) throws IOException {
        if (input == null) throw new NullPointerException("input cannot be null");

        JsonNode json = _readJSON(input);

        // Try validation with 2.0; fallback to 1.0.
        ProcessingReport report = _validate(json, Constants.BELJGFSchema.VERSION_2_0);
        if (report.isSuccess()) {
            Graph[] graphs = _readGraph(json);
            Arrays.stream(graphs).
                    forEach(graph -> graph.beljgfVersion = Constants.BELJGFSchema.VERSION_2_0.version);
            return new GraphsWithValidation(graphs, report);
        }

        report = _validate(json, Constants.BELJGFSchema.VERSION_1_0);
        if (report.isSuccess()) {
            Graph[] graphs = _readGraph(json);
            Arrays.stream(graphs).
                    forEach(graph -> graph.beljgfVersion = Constants.BELJGFSchema.VERSION_1_0.version);
            return new GraphsWithValidation(graphs, report);
        }

        // Unsuccessful validation; Only includes failure messages for 1.0.
        return new GraphsWithValidation(new Graph[0], report);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphsWithValidation validatingRead(File input) throws IOException {
        if (input == null) throw new NullPointerException("input cannot be null");
        if (!input.exists()) throw new FileNotFoundException("input does not exist");
        if (!input.exists() || !input.canRead())
            throw new FileNotFoundException("input does not exist");

        JsonNode json = _readJSON(new FileInputStream(input));

        // Try validation with 2.0; fallback to 1.0.
        ProcessingReport report = _validate(json, Constants.BELJGFSchema.VERSION_2_0);
        if (!report.isSuccess()) {
            report = _validate(json, Constants.BELJGFSchema.VERSION_1_0);
        }

        if (report.isSuccess()) {
            return new GraphsWithValidation(_readGraph(json), report);
        }

        // Unsuccessful validation; Only includes failure messages for 1.0.
        return new GraphsWithValidation(new Graph[0], report);
    }

    protected JsonNode _readJSON(InputStream input) throws IOException {
        return mapper.readTree(input);
    }

    protected void _writeJSON(OutputStream output, JsonNode node) throws IOException {
        mapper.writeValue(output, node);
    }

    protected String _toJSON(JsonNode node) throws JsonProcessingException {
        return mapper.writeValueAsString(node);
    }

    protected ProcessingReport _validate(JsonNode json, Constants.BELJGFSchema beljgfSchema) throws IOException {
        final JsonNode belSchema = JsonLoader.fromResource(beljgfSchema.resourcePath);
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema;
        try {
            schema = factory.getJsonSchema(belSchema);
            return schema.validate(json, true);
        } catch (ProcessingException e) {
            throw new IOException(e);
        }
    }

    protected Graph[] _readGraph(JsonNode json) throws IOException {
        Root root = mapper.readValue(json.toString(), Root.class);
        return Root.determineGraphs(root);
    }
}
