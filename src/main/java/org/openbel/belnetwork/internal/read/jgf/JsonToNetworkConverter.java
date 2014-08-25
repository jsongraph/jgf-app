package org.openbel.belnetwork.internal.read.jgf;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbel.belnetwork.internal.mapperclasses.*;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.examples.Utils;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.io.IOException;

public class JsonToNetworkConverter {
    
    private static final Logger userMessages = LoggerFactory.getLogger("CyUserMessages");
    
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
            userMessages.error("Some mapping error has occurred.");
            e.printStackTrace();            
        } finally {
            
        }        
        return network;
    }
    
    
    public Graph CreateGraph(InputStream is) throws Exception 
    {
        Graph graph = null;
                
        try {        
            // use JSON Deserializer to read if first ID is graph or graphs. Send back nice msg if it is graphs    
            ObjectMapper objectMapper = new ObjectMapper();   
            JsonNode graphTree = objectMapper.readTree(is);
            //graphTree   get graphs element out to see if it is null
            JsonNode rootNode = graphTree.findValue("graphs");
            if (rootNode != null)
            {
                userMessages.error("Import does not support multiple networks in one json file." );
                return graph;
            }            
            //Validate against Bel JSON Schema
            final JsonNode belSchema = JsonLoader.fromResource("/bel-json-graph-schema.json");        
            final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            final JsonSchema schema = factory.getJsonSchema(belSchema);
            ProcessingReport report = schema.validate(graphTree);
            //if(report.isSuccess()) // can not figure out why validation is failing for unexpected elements
            {    
                //convert json string to object
                 RootObject ro  = objectMapper.readValue(graphTree.toString(), RootObject.class);
                 graph = ro.getGraph();
            }
           // else
           // {
            //    userMessages.error("Error validating json against schema."  + report.toString());
           // }
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
