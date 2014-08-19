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

public class JGFReaderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void test() {
		
		Graph graph=null;
		try {
				
			InputStream stream  = new FileInputStream ("C:\\Users\\cfletcher\\git\\KEGGscape\\src\\test\\resources\\testData\\jgf\\Response to DNA Damage1.1 formatted.jgf");
			
			
			JsonToNetworkConverter converter  = new JsonToNetworkConverter();
			graph = converter.CreateGraph(stream);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Exception thrown.");
		}
		
		assertTrue(graph != null);
		assertTrue(graph.getMetadata().get("description") != null);
	}

}
