package org.openbel.belnetwork.api;

import org.junit.Test;
import org.openbel.belnetwork.internal.JSONGraphReaderImpl;
import org.openbel.belnetwork.internal.read.jgf.JsonToNetworkConverter;
import org.openbel.belnetwork.model.Graph;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JSONGraphReaderImplTest {

    @Test
    public void testReadSingleGraph() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/single_graph.json");
        JSONGraphReader reader = new JSONGraphReaderImpl();
        Graph[] graphs = reader.read(stream);

        assertEquals(1, graphs.length);
        assertTrue(graphs[0] != null);
        assertTrue(graphs[0].metadata.get("description") != null);
    }

    @Test
    public void testReadMultipleGraphs() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/multiple_graphs.json");
        JSONGraphReader reader = new JSONGraphReaderImpl();
        Graph[] graphs = reader.read(stream);

        assertTrue(graphs != null);
        assertEquals(2, graphs.length);
    }
}
