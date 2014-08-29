package org.openbel.belnetwork.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.openbel.belnetwork.api.BELGraphReader;
import org.openbel.belnetwork.api.GraphsWithValidation;
import org.openbel.belnetwork.api.model.Graph;
import org.openbel.belnetwork.api.model.Root;

import java.io.*;

/**
 * {@link BELGraphReaderImpl} implements {@link BELGraphReader} to provide
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
public class BELGraphReaderImpl implements BELGraphReader {

    protected final ObjectMapper mapper;

    public BELGraphReaderImpl() {
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

        ProcessingReport report = _validate(json);

        if (report.isSuccess()) {
            return new GraphsWithValidation(_readGraph(json), report);
        }
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

        ProcessingReport report = _validate(json);

        if (report.isSuccess()) {
            return new GraphsWithValidation(_readGraph(json), report);
        }
        return new GraphsWithValidation(new Graph[0], report);
    }

    protected JsonNode _readJSON(InputStream input) throws IOException {
        return mapper.readTree(input);
    }

    protected ProcessingReport _validate(JsonNode json) throws IOException {
        final JsonNode belSchema = JsonLoader.fromResource("/bel-json-graph.schema.json");
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema;
        try {
            schema = factory.getJsonSchema(belSchema);
            return schema.validate(json);
        } catch (ProcessingException e) {
            throw new IOException(e);
        }
    }

    protected Graph[] _readGraph(JsonNode json) throws IOException {
        Root root = mapper.readValue(json.toString(), Root.class);
        return Root.determineGraphs(root);
    }
}
