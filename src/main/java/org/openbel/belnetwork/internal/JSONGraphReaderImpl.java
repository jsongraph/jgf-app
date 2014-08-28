package org.openbel.belnetwork.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.openbel.belnetwork.api.GraphsWithValidation;
import org.openbel.belnetwork.api.JSONGraphReader;
import org.openbel.belnetwork.model.Graph;
import org.openbel.belnetwork.model.Root;

import java.io.*;

import static org.openbel.belnetwork.api.FormatUtility.determineGraphs;

public class JSONGraphReaderImpl implements JSONGraphReader {

    private final ObjectMapper mapper;

    public JSONGraphReaderImpl() {
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
        if (!input.exists()) throw new FileNotFoundException("input does not exist");
        if (!input.canRead()) throw new IOException("input cannot be read");

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
        if (!input.canRead()) throw new IOException("input cannot be read");

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
        final JsonNode belSchema = JsonLoader.fromResource("/bel-json-graph-schema.json");
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
        return determineGraphs(root);
    }
}
