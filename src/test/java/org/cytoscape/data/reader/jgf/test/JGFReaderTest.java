package org.cytoscape.data.reader.jgf.test;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;

import java.io.FileInputStream;
import java.io.InputStream;

import org.cytoscape.jgfnetwork.internal.mapperclasses.Graph;
import org.cytoscape.jgfnetwork.internal.read.jgf.JsonToNetworkConverter;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JGFReaderTest {
	private static Logger userMessages;
	
	@Before
	public void setUp() throws Exception {
		userMessages = LoggerFactory.getLogger("CyUserMessages");
	}

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
			graph = converter.CreateGraph(stream);					
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
			graph = converter.CreateGraph(stream);
						
		} catch (Exception e) {
		
			e.printStackTrace();
			fail("Exception thrown.");
		}
		
		assertTrue(graph == null);
	}
	
}
