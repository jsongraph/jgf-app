package info.json_graph_format.jgfapp.api;

import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.common.collect.Iterators;
import org.junit.Test;
import info.json_graph_format.jgfapp.internal.BELGraphReaderImpl;
import info.json_graph_format.jgfapp.api.model.Graph;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class BELGraphReaderImplTest {

    @Test
    public void testReadSingleGraph() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/single_graph.jgf");
        BELGraphReader reader = new BELGraphReaderImpl();
        Graph[] graphs = reader.read(stream);

        assertThat(graphs, arrayWithSize(1));
        assertThat(graphs[0], is(notNullValue()));
    }

    @Test
    public void testReadSingleGraphWithValidation() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/single_graph.jgf");
        BELGraphReader reader = new BELGraphReaderImpl();
        GraphsWithValidation gv = reader.validatingRead(stream);

        // check validation
        assertThat(gv, is(notNullValue()));
        ProcessingReport validation = gv.getValidationReport();
        assertThat(validation, is(notNullValue()));
        assertThat(validation.isSuccess(), is(true));
        assertThat(validation, is(emptyIterable()));

        // check graph
        Graph[] graphs = gv.getGraphs();
        assertThat(graphs, arrayWithSize(1));
        assertThat(graphs[0], is(notNullValue()));
    }

    @Test
    public void testReadMultipleGraphs() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/multiple_graphs.jgf");
        BELGraphReader reader = new BELGraphReaderImpl();
        Graph[] graphs = reader.read(stream);

        assertThat(graphs, is(notNullValue()));
        assertThat(graphs, arrayWithSize(2));
    }

    @Test
    public void testReadMultipleGraphsWithValidation() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/multiple_graphs.jgf");
        BELGraphReader reader = new BELGraphReaderImpl();
        GraphsWithValidation gv = reader.validatingRead(stream);

        // check validation
        assertThat(gv, is(notNullValue()));
        ProcessingReport validation = gv.getValidationReport();
        assertThat(validation, is(notNullValue()));
        assertThat(validation, is(notNullValue()));
        assertThat(validation.isSuccess(), is(true));
        assertThat(validation, is(emptyIterable()));

        // check graph
        Graph[] graphs = gv.getGraphs();
        assertThat(graphs, arrayWithSize(2));
    }

    @Test
    public void testEmptyGraphs() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/empty_graphs.jgf");
        BELGraphReader reader = new BELGraphReaderImpl();
        Graph[] graphs = reader.read(stream);

        assertThat(graphs, is(notNullValue()));
        assertThat(graphs, arrayWithSize(0));
    }

    @Test
    public void testValidationErrorForGraphId() throws IOException {
        ProcessingReport validation = expectValidationError("testData/jgf/malformed_graph_has_id.jgf");
        ProcessingMessage[] messages = Iterators.toArray(validation.iterator(), ProcessingMessage.class);

        assertThat(messages, arrayWithSize(1));
        assertThat(messages[0].getLogLevel(), is(LogLevel.ERROR));
        assertThat(messages[0].asJson().get("keyword").asText(), equalTo("oneOf"));
        assertThat(messages[0].getMessage(), equalTo("instance failed to match exactly one schema (matched 0 out of 2)"));
        assertThat(messages[0].toString(), containsString("\"id\""));
    }

    @Test
    public void testValidationErrorForInvalidEvidence() throws IOException {
        ProcessingReport validation = expectValidationError("testData/jgf/malformed_graph_invalid_evidence.jgf");
        ProcessingMessage[] messages = Iterators.toArray(validation.iterator(), ProcessingMessage.class);

        assertThat(messages, arrayWithSize(1));
        assertThat(messages[0].getLogLevel(), is(LogLevel.ERROR));
        assertThat(messages[0].asJson().get("keyword").asText(), equalTo("oneOf"));
        assertThat(messages[0].getMessage(), equalTo("instance failed to match exactly one schema (matched 0 out of 2)"));
        assertThat(messages[0].toString(), containsString("\"bel_statement\""));
        assertThat(messages[0].toString(), containsString("\"citation\""));
    }

    private ProcessingReport expectValidationError(String testResource) throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(testResource);
        BELGraphReader reader = new BELGraphReaderImpl();
        GraphsWithValidation gv = reader.validatingRead(stream);

        // check validation
        assertThat(gv, is(notNullValue()));
        ProcessingReport validation = gv.getValidationReport();
        assertThat(validation, is(notNullValue()));
        assertThat(validation.isSuccess(), is(false));
        assertThat(validation, is(not(emptyIterable())));

        // check graph
        Graph[] graphs = gv.getGraphs();
        assertThat(graphs, is(notNullValue()));
        assertThat(graphs, arrayWithSize(0));

        return validation;
    }
}
