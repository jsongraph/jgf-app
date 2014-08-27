package org.openbel.belnetwork.internal.read.jgf;

import java.io.InputStream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import org.cytoscape.model.CyNetworkFactory;
import org.openbel.belnetwork.model.Graph;
import org.openbel.belnetwork.model.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import static org.openbel.belnetwork.internal.util.FormatUtility.determineGraphs;

public class JsonToNetworkConverter {
    
    private static final Logger userMessages = LoggerFactory.getLogger("CyUserMessages");
    
    //Take the Graph Object model for our JGF Json and convert it into an instance of a CyNetwork
    //TODO Method comment
    public CyNetwork[] createNetworks(Graph[] graphs, CyNetworkFactory networkFactory,
                                      CyTableFactory tableFactory, CyTableManager tableMgr) throws Exception {
        // create N number of CyNetwork for N number of Graph...
        final CyNetwork[] networks = new CyNetwork[graphs.length];
        for (int i = 0; i < networks.length; i++)
            networks[i] = networkFactory.createNetwork();

        // ...map each Graph to CyNetwork
        try {
            for (int i = 0; i < graphs.length; i++) {
                JGFMapper mapper = new JGFMapper(graphs[i], networks[i], tableFactory, tableMgr);
                mapper.doMapping();
            }
        } catch (Exception e) {
            userMessages.error("Some mapping error has occurred.");
            e.printStackTrace();
        }

        return networks;
    }

    public Graph[] createGraphs(InputStream is) throws Exception {
        try {
            // use JSON Deserializer to read if first ID is graph or graphs. Send back nice msg if it is graphs    
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            JsonNode graphTree = objectMapper.readTree(is);

            //Validate against Bel JSON Schema
            final JsonNode belSchema = JsonLoader.fromResource("/bel-json-graph-schema.json");        
            final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            final JsonSchema schema = factory.getJsonSchema(belSchema);
            ProcessingReport report = schema.validate(graphTree);
            for (ProcessingMessage err : report)
                System.out.println(err.getMessage());

            Root root = objectMapper.readValue(graphTree.toString(), Root.class);
            return determineGraphs(root);
        } catch (Throwable re) {
            re.printStackTrace();
            //throw new IOException("Could not deserialize JGF json file");
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
        return new Graph[0];
    }
}