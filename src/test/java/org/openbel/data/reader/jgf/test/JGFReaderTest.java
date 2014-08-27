package org.openbel.data.reader.jgf.test;

import java.io.InputStream;

import org.openbel.belnetwork.internal.mapperclasses.Graph;
import org.openbel.belnetwork.internal.read.jgf.JsonToNetworkConverter;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

public class JGFReaderTest {

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test() {
        Graph graph=null;
        try {
            //Response to DNA Damage1.1 formatted.jgf
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/Response to DNA Damage1.1 formatted.jgf");            
            JsonToNetworkConverter converter  = new JsonToNetworkConverter();
            graph = converter.createGraph(stream);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown.");
        }
        
        assertTrue(graph != null);
        assertTrue(graph.getMetadata().get("description") != null);
    }

    @Test
    public void testForGraphs() {
        Graph graph=null;
        try {
                
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testData/jgf/Xenobiotic Metabolism Response_v1.2 Graphs.json");
            JsonToNetworkConverter converter  = new JsonToNetworkConverter();
            graph = converter.createGraph(stream);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown.");
        }
        
        assertTrue(graph == null);
    }
}