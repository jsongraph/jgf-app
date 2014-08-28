package org.openbel.data.reader.jgf.test;

import java.io.InputStream;

import org.openbel.belnetwork.internal.read.jgf.JsonToNetworkConverter;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openbel.belnetwork.model.Graph;

public class JGFReaderTest {

    @Test
    public void test() {
        Graph[] graphs = null;
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/single_graph.json");
            JsonToNetworkConverter converter  = new JsonToNetworkConverter();
            graphs = converter.createGraphs(stream);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown.");
        }

        assertEquals(1, graphs.length);
        assertNotNull(graphs[0]);
        assertNotNull(graphs[0].metadata.get("description"));
    }

    @Test
    public void testForGraphs() {
        Graph[] graphs = null;
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/multiple_graphs.json");
            JsonToNetworkConverter converter  = new JsonToNetworkConverter();
            graphs = converter.createGraphs(stream);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown.");
        }

        assertNotNull(graphs);
        assertEquals(2, graphs.length);
    }
}
