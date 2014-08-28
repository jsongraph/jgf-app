package org.openbel.belnetwork.api;

import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.common.collect.Iterators;
import org.junit.Test;
import org.openbel.belnetwork.internal.BELGraphReaderImpl;
import org.openbel.belnetwork.model.Graph;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class BELGraphReaderImplTest {

    @Test
    public void testReadSingleGraph() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/single_graph.jgf");
        BELGraphReader reader = new BELGraphReaderImpl();
        Graph[] graphs = reader.read(stream);

        assertEquals(1, graphs.length);
        assertNotNull(graphs[0]);
    }

    @Test
    public void testReadSingleGraphWithValidation() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/single_graph.jgf");
        BELGraphReader reader = new BELGraphReaderImpl();
        GraphsWithValidation gv = reader.validatingRead(stream);

        // check validation
        assertNotNull(gv);
        ProcessingReport validation = gv.getValidationReport();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertThat(validation, is(emptyIterable()));

        // check graph
        Graph[] graphs = gv.getGraphs();
        assertEquals(1, graphs.length);
        assertNotNull(graphs[0]);
    }

    @Test
    public void testReadMultipleGraphs() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/multiple_graphs.jgf");
        BELGraphReader reader = new BELGraphReaderImpl();
        Graph[] graphs = reader.read(stream);

        assertNotNull(graphs);
        assertEquals(2, graphs.length);
    }

    @Test
    public void testReadMultipleGraphsWithValidation() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/multiple_graphs.jgf");
        BELGraphReader reader = new BELGraphReaderImpl();
        GraphsWithValidation gv = reader.validatingRead(stream);

        // check validation
        assertNotNull(gv);
        ProcessingReport validation = gv.getValidationReport();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertThat(validation, is(emptyIterable()));

        // check graph
        Graph[] graphs = gv.getGraphs();
        assertEquals(2, graphs.length);
    }

    @Test
    public void testEmptyGraphs() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/empty_graphs.jgf");
        BELGraphReader reader = new BELGraphReaderImpl();
        Graph[] graphs = reader.read(stream);

        assertNotNull(graphs);
        assertEquals(0, graphs.length);
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
        assertNotNull(gv);
        ProcessingReport validation = gv.getValidationReport();
        assertNotNull(validation);
        assertFalse(validation.isSuccess());
        assertThat(validation, is(not(emptyIterable())));

        // check graph
        Graph[] graphs = gv.getGraphs();
        assertNotNull(graphs);
        assertEquals(0, graphs.length);

        return validation;
    }
}
