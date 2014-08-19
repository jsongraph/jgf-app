package org.cytoscape.jgfnetwork.internal.read.jgf;

import java.io.InputStream;

import org.cytoscape.jgfnetwork.internal.mapperclasses.*;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonToNetworkConverter {
	
	public JsonToNetworkConverter()
	{		
		
	}
	
	//Take the Graph Object model for our JGF Json and convert it into an instance of a CyNetwork
	public CyNetwork CreateNetwork(Graph graph, CyNetwork network, CyTableFactory cyTableFactory, CyTableManager cyTableManager) throws Exception 
	{		
		try {		
			JGFMapper mapper = new JGFMapper(graph, network, cyTableFactory, cyTableManager);
			mapper.doMapping();					
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			
		}		
		return network;
	}
	
	
	public Graph CreateGraph(InputStream is) throws Exception 
	{
		Graph graph = null;
        		
		try {			
			 //create ObjectMapper instance
	        ObjectMapper objectMapper = new ObjectMapper();         
	        //convert json string to object
	         RootObject ro  = objectMapper.readValue(is, RootObject.class);
	         graph = ro.getGraph();
		
		} catch (Throwable re) {
			re.printStackTrace();
			//throw new IOException("Could not deserialize JGF json file");
		} 
		finally {
			if (is != null) {
				is.close();
			}
		}				
		return graph;
	}
}
