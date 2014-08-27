package org.openbel.belnetwork.internal.read.jgf;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.model.SavePolicy;
import org.openbel.belnetwork.model.*;

import static org.openbel.belnetwork.internal.Constants.COORDINATE_TRANSLATION;
import static org.openbel.belnetwork.internal.util.FormatUtility.getOrEmptyString;
import static org.openbel.belnetwork.internal.util.FormatUtility.getOrZero;

public class JGFMapper {

    private final Graph graph;
    private final CyNetwork network;
    private final CyTableFactory cyTableFactory;
    private final CyTableManager cyTableManager;
    final Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();
    
    private static final String JGF_ID = "id";
    private static final String JGF_TYPE = "type";
    private static final String JGF_DIRECTED = "directed";
    
    private static final String JGF_GRAPH_DESCRIPTION = "description";
    private static final String JGF_GRAPH_SPECIES = "species";
    private static final String JGF_GRAPH_VERSION = "version";
    
    private static final String JGF_NODE_X = "x coordinate";
    private static final String JGF_NODE_Y = "y coordinate";
    private static final String JGF_NODE_Z = "z coordinate";
    private static final String JGF_BEL_FUNC = "bel function";
    
    private static final String JGF_EDGE_SOURCE = "source node";
    private static final String JGF_EDGE_TARGET= "target node";
    private static final String JGF_EDGE_CAUSAL = "causal";
    
    public JGFMapper(final Graph graph, final CyNetwork network,CyTableFactory cyTableFactory, CyTableManager cyTableManager) {
        this.graph = graph;
        this.network = network;
        this.cyTableFactory = cyTableFactory;
        this.cyTableManager = cyTableManager;
        mapGraphMetadata(graph, network);
    }

    public void doMapping() throws IOException {
        createJGFNodeTable();
        createJGFEdgeTable();
        mapNodes();
        mapEdges();
    }

    private void mapGraphMetadata(final Graph graph, final CyNetwork network) {
    
        HashMap<String, Object> graphMetadata = graph.metadata;

        String version = "1.0";
        if (graphMetadata.containsKey("version"))
            version = graphMetadata.get("version").toString();

        String description = "";
        if (graphMetadata.containsKey("description"))
            description = graphMetadata.get("description").toString();

        String species = "";
        if (graphMetadata.containsKey("species_common_name"))
            species = graphMetadata.get("species_common_name").toString();

        final String graphTitle = graph.label + " ver: " + version;

        final CyRow networkRow = network.getRow(network);
        networkRow.set(CyNetwork.NAME, graphTitle);

        network.getDefaultNetworkTable().createColumn(JGF_GRAPH_VERSION,
                String.class, true);
        network.getDefaultNetworkTable().createColumn(JGF_GRAPH_DESCRIPTION,
                String.class, true);
        network.getDefaultNetworkTable().createColumn(JGF_TYPE,
                String.class, true);
        network.getDefaultNetworkTable().createColumn(JGF_DIRECTED,
                Boolean.class, true);
        network.getDefaultNetworkTable().createColumn(JGF_GRAPH_SPECIES,
                String.class, true);

        networkRow.set(JGF_GRAPH_VERSION, version );
        networkRow.set(JGF_GRAPH_DESCRIPTION, description);
        networkRow.set(JGF_GRAPH_SPECIES, species);
        networkRow.set(JGF_TYPE, graph.type);
        networkRow.set(JGF_DIRECTED, graph.directed);
    }

    private void mapNodes() {
        nodeMap.clear();
        for (Node n : graph.nodes) {
            final CyNode cyNode = network.addNode();
            final CyRow row = network.getRow(cyNode);
            row.set(CyNetwork.NAME, n.label);
            if (n.metadata.containsKey("coordinate")) {
                // FIXME Check type
                @SuppressWarnings("unchecked")
                ArrayList<Double> loc = (ArrayList<Double>)n.metadata.get("coordinate");
                if (loc.size() >= 1) row.set(JGF_NODE_X, loc.get(0) * COORDINATE_TRANSLATION);
                if (loc.size() >= 2) row.set(JGF_NODE_Y, loc.get(1) * COORDINATE_TRANSLATION);
                if (loc.size() >= 3) row.set(JGF_NODE_Z, loc.get(2) * COORDINATE_TRANSLATION);
            }
            if (n.metadata.containsKey("bel_function_type")) {
                row.set(JGF_BEL_FUNC, n.metadata.get("bel_function_type"));
            }
            //parse the label or bel_function for apply visual styles?
            nodeMap.put(n.id, cyNode); // for edges to use later for source and target mapping
        }
    }

    private void createJGFNodeTable() {
        network.getDefaultNodeTable().createColumn(JGF_NODE_X, Double.class, true);
        network.getDefaultNodeTable().createColumn(JGF_NODE_Y, Double.class, true);
        network.getDefaultNodeTable().createColumn(JGF_NODE_Z, Double.class, true);
        network.getDefaultNodeTable().createColumn(JGF_BEL_FUNC, String.class, true);
    }
    
    private void mapEdges() {
        //edges do not have an ID from the JSon - but each added CyEdge has its own SUID
        for (Edge edge : graph.edges) {
        // need to find the source and target by NodeID in the nodemap
            CyNode sourceNode = nodeMap.get(edge.source);
            CyNode targetNode = nodeMap.get(edge.target);
            final CyEdge newEdge = network.addEdge(sourceNode,
                    targetNode, edge.directed);
            final CyRow row = network.getRow(newEdge);
            row.set(CyNetwork.NAME, edge.label);
            row.set(CyEdge.INTERACTION, edge.relation);
            
            final CyRow srow = network.getRow(sourceNode);
            final CyRow trow = network.getRow(targetNode);
            row.set(JGF_EDGE_SOURCE, srow.get(CyNetwork.NAME,String.class));
            row.set(JGF_EDGE_TARGET, trow.get(CyNetwork.NAME,String.class));                
            //if( n.getMetadata().containsKey("Evidences"))
            // Create an unassigned Table "JGF.Evidence" create columns for all properties and 2 List Columns for the Biological Context.
            if (edge.metadata != null) {
                HashMap<String, Object> mdata = edge.metadata;

                if (mdata.containsKey("evidences")) {
                    @SuppressWarnings("unchecked")
                    List<LinkedHashMap<String, Object>> evidenceMap = (List<LinkedHashMap<String,Object>>)mdata.get("evidences");
                    List<Evidence> evidences = new ArrayList<Evidence>();
                    for (HashMap<String,Object> item : evidenceMap) {
                        Evidence ev = new Evidence();
                        ev.belStatement = getOrEmptyString("bel_statement", item);
                        ev.summaryText = getOrEmptyString("summary_text", item);

                        Citation citation = new Citation();
                        @SuppressWarnings("unchecked")
                        LinkedHashMap<String, Object> citationMap = (LinkedHashMap<String, Object>)item.get("citation");
                        citation.id = getOrEmptyString("id", citationMap);
                        citation.type = getOrEmptyString("type", citationMap);
                        citation.name = getOrEmptyString("name", citationMap);
                        ev.citation = citation;
                   
                        BiologicalContext context = new BiologicalContext();
                        @SuppressWarnings("unchecked")
                        LinkedHashMap<String, Object> contextMap = (LinkedHashMap<String, Object>)item.get("biological_context");
                        context.speciesCommonName = getOrEmptyString("species_common_name", contextMap);
                        context.ncbiTaxId = getOrZero("ncbi_tax_id", contextMap);
                        Set<String> varying = new HashSet<String>(contextMap.keySet());
                        varying.removeAll(Arrays.asList("species_common_name", "ncbi_tax_id"));

                        for (String key : varying) {
                            context.variedAnnotations.put(key, contextMap.get(key));
                        }
                        ev.biologicalContext = context;
                        evidences.add(ev);
                    }                      
                    processEvidences(newEdge, evidences);
                }
                if (mdata.containsKey("casual")) {
                    row.set(JGF_EDGE_CAUSAL, mdata.get("casual"));
                }                
            }
        }
    }
    
    private void createJGFEdgeTable() {
        network.getDefaultEdgeTable().createColumn(JGF_EDGE_SOURCE, String.class, true);
        network.getDefaultEdgeTable().createColumn(JGF_EDGE_TARGET, String.class, true);
        network.getDefaultEdgeTable().createColumn(JGF_EDGE_CAUSAL, Boolean.class, true);
    }

    private void processEvidences(CyEdge edge, List<Evidence> evidences) {
        CyTable eviTable = getOrCreateEvidenceTable(evidences, cyTableManager, cyTableFactory);
        CyRow networkRow = network.getRow(network);
        String graphTitle = networkRow.get(CyNetwork.NAME, String.class);
        Long netSUID = network.getSUID();
        Long edgeSUID = edge.getSUID();
        //put the evidence into the table.
        for (Evidence ev: evidences) {
            CyRow row = eviTable.getRow(SUIDFactory.getNextSUID());
            row.set("network suid", netSUID);
            row.set("network name", graphTitle);
            row.set("edge suid", edgeSUID);
            row.set("bel statement", ev.belStatement);
            row.set("citation type", ev.citation.type);
            row.set("citation id", ev.citation.id);
            row.set("citation name", ev.citation.name);
            row.set("summary text", ev.summaryText);
            row.set("species", ev.biologicalContext.speciesCommonName);

            Map<String, Object> varying = ev.biologicalContext.variedAnnotations;
            for (Entry<String, Object> entry : varying.entrySet()) {
                row.set(entry.getKey(), getOrEmptyString(entry.getKey(), varying));
            }
        }
    }

    private CyTable getOrCreateEvidenceTable(List<Evidence> evidences, CyTableManager tableMgr, CyTableFactory tableFactory) {
        CyTable tbl = null;
        Set<CyTable> allTables =tableMgr.getAllTables(true);
        for (CyTable table : allTables) {
            if (table.getTitle().equals("JGF.Evidence")) {
                tbl = table;
                break;
            }
        }
        if (tbl == null) {
            tbl = tableFactory.createTable("JGF.Evidence", "SUID", Long.class, true, false);
            tbl.setSavePolicy(SavePolicy.DO_NOT_SAVE);
            //Add all the columns
            tbl.createColumn("network suid", Long.class, true, null);
            tbl.createColumn("network name", String.class, true);
            tbl.createColumn("edge suid", Long.class, true, null);
            tbl.createColumn("bel statement", String.class, false);
            tbl.createColumn("citation type", String.class, false);
            tbl.createColumn("citation id", String.class, false);
            tbl.createColumn("citation name", String.class, false);
            tbl.createColumn("summary text", String.class, false);
            tbl.createColumn("species", String.class, false);

            Set<String> union = new HashSet<String>();
            for (Evidence ev : evidences) {
                union.addAll(ev.biologicalContext.variedAnnotations.keySet());
            }

            for (String varyingKey : union) {
                tbl.createColumn(varyingKey, String.class, false);
            }
            tableMgr.addTable(tbl);
        }
        return tbl;
    }
}