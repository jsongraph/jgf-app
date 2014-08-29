package org.openbel.belnetwork.internal;

import org.cytoscape.model.*;
import org.openbel.belnetwork.api.BELGraphConverter;
import org.openbel.belnetwork.model.*;

import java.util.*;

import static org.openbel.belnetwork.api.util.FormatUtility.getOrEmptyString;
import static org.openbel.belnetwork.api.util.FormatUtility.getOrZero;
import static org.openbel.belnetwork.api.util.Utility.typedList;
import static org.openbel.belnetwork.internal.Constants.COORDINATE_TRANSLATION;

/**
 * {@link BELGraphConverterImpl} implements a {@link BELGraphConverter} that converts
 * {@link Graph} to {@link CyNetwork} and vice versa.
 */
public class BELGraphConverterImpl implements BELGraphConverter {

    private static final String TYPE = "type";
    private static final String DIRECTED = "directed";

    private static final String GRAPH_DESCRIPTION = "description";
    private static final String GRAPH_SPECIES = "species";
    private static final String GRAPH_VERSION = "version";

    private static final String X_COORD = "x coordinate";
    private static final String Y_COORD = "y coordinate";
    private static final String Z_COORD = "z coordinate";
    private static final String BEL_FUNCTION = "bel function";

    private static final String EDGE_SOURCE = "source node";
    private static final String EDGE_TARGET= "target node";
    private static final String EDGE_CAUSAL = "causal";

    private final CyNetworkFactory networkFactory;

    public BELGraphConverterImpl(CyNetworkFactory networkFactory) {
        if (networkFactory == null)
            throw new NullPointerException("networkFactory cannot be null");
        this.networkFactory = networkFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CyNetwork convert(Graph graph) {
        CyNetwork network = networkFactory.createNetwork();
        mapNetworkData(graph, network);
        Map<String, CyNode> createdNodes = mapNodeData(graph, network);
        mapEdgeData(createdNodes, graph, network);

        return network;
    }

    /**
     * {@inheritDoc}
     * <br><br>
     * FIXME Unsupported at the moment.
     */
    @Override
    public Graph convert(CyNetwork network) {
        throw new UnsupportedOperationException("TODO, convert CyNetwork -> Graph");
    }

    private static void mapNetworkData(Graph graph, CyNetwork network) {
        graph.cyNetwork = network;

        Map<String, Object> metadata = graph.metadata;

        String version = "1.0";
        if (metadata.containsKey("version"))
            version = metadata.get("version").toString();

        String description = "";
        if (metadata.containsKey("description"))
            description = metadata.get("description").toString();

        String species = "";
        if (metadata.containsKey("species_common_name"))
            species = metadata.get("species_common_name").toString();

        final String graphTitle = graph.label + " ver: " + version;

        final CyRow networkRow = network.getRow(network);
        networkRow.set(CyNetwork.NAME, graphTitle);

        network.getDefaultNetworkTable().createColumn(GRAPH_VERSION, String.class, true);
        network.getDefaultNetworkTable().createColumn(GRAPH_DESCRIPTION, String.class, true);
        network.getDefaultNetworkTable().createColumn(TYPE, String.class, true);
        network.getDefaultNetworkTable().createColumn(DIRECTED, Boolean.class, true);
        network.getDefaultNetworkTable().createColumn(GRAPH_SPECIES, String.class, true);

        networkRow.set(GRAPH_VERSION, version );
        networkRow.set(GRAPH_DESCRIPTION, description);
        networkRow.set(GRAPH_SPECIES, species);
        networkRow.set(TYPE, graph.type);
        networkRow.set(DIRECTED, graph.directed);
    }

    private static Map<String, CyNode> mapNodeData(Graph graph, CyNetwork network) {
        network.getDefaultNodeTable().createColumn(X_COORD, Double.class, true);
        network.getDefaultNodeTable().createColumn(Y_COORD, Double.class, true);
        network.getDefaultNodeTable().createColumn(Z_COORD, Double.class, true);
        network.getDefaultNodeTable().createColumn(BEL_FUNCTION, String.class, true);

        Map<String, CyNode> createdNodes = new HashMap<String, CyNode>();
        for (Node n : graph.nodes) {
            final CyNode cyNode = network.addNode();
            n.cyNode = cyNode;

            final CyRow row = network.getRow(cyNode);
            row.set(CyNetwork.NAME, n.label);
            if (n.metadata.containsKey("coordinate")) {
                Object coords = n.metadata.get("coordinate");
                if (coords != null && (coords instanceof List)) {
                    List<Double> coordinates = typedList((List) coords, Double.class);
                    if (coordinates.size() >= 2) {
                        row.set(X_COORD, coordinates.get(0) * COORDINATE_TRANSLATION);
                        row.set(Y_COORD, coordinates.get(1) * COORDINATE_TRANSLATION);
                        if (coordinates.size() == 3) {
                            row.set(Z_COORD, coordinates.get(2) * COORDINATE_TRANSLATION);
                        }
                    }
                }
            }
            if (n.metadata.containsKey("bel_function_type"))
                row.set(BEL_FUNCTION, n.metadata.get("bel_function_type"));

            createdNodes.put(n.id, cyNode);
        }
        return createdNodes;
    }

    private static void mapEdgeData(Map<String, CyNode> createdNodes, Graph graph, CyNetwork network) {
        network.getDefaultEdgeTable().createColumn(EDGE_SOURCE, String.class, true);
        network.getDefaultEdgeTable().createColumn(EDGE_TARGET, String.class, true);
        network.getDefaultEdgeTable().createColumn(EDGE_CAUSAL, Boolean.class, true);

        for (Edge edge : graph.edges) {
            CyNode sourceNode = createdNodes.get(edge.source);
            CyNode targetNode = createdNodes.get(edge.target);
            final CyEdge cyEdge = network.addEdge(sourceNode,
                    targetNode, edge.directed);
            edge.cyEdge = cyEdge;

            final CyRow row = network.getRow(cyEdge);
            row.set(CyNetwork.NAME, edge.label);
            row.set(CyEdge.INTERACTION, edge.relation);

            final CyRow srow = network.getRow(sourceNode);
            final CyRow trow = network.getRow(targetNode);
            row.set(EDGE_SOURCE, srow.get(CyNetwork.NAME,String.class));
            row.set(EDGE_TARGET, trow.get(CyNetwork.NAME,String.class));

            if (edge.metadata != null) {
                HashMap<String, Object> mdata = edge.metadata;

                if (mdata.containsKey("evidences")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> evidenceMap = (List<Map<String,Object>>)mdata.get("evidences");
                    for (Map<String,Object> item : evidenceMap) {
                        Evidence ev = new Evidence();
                        ev.belStatement = getOrEmptyString("bel_statement", item);
                        ev.summaryText = getOrEmptyString("summary_text", item);

                        Citation citation = new Citation();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> citationMap = (Map<String, Object>)item.get("citation");
                        if (citationMap != null) {
                            citation.id = getOrEmptyString("id", citationMap);
                            citation.type = getOrEmptyString("type", citationMap);
                            citation.name = getOrEmptyString("name", citationMap);
                        }
                        ev.citation = citation;

                        BiologicalContext context = new BiologicalContext();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> contextMap = (Map<String, Object>)item.get("biological_context");
                        if (contextMap != null) {
                            context.speciesCommonName = getOrEmptyString("species_common_name", contextMap);
                            context.ncbiTaxId = getOrZero("ncbi_tax_id", contextMap);
                            Set<String> varying = new HashSet<String>(contextMap.keySet());
                            varying.removeAll(Arrays.asList("species_common_name", "ncbi_tax_id"));

                            for (String key : varying) {
                                context.variedAnnotations.put(key, contextMap.get(key));
                            }
                            ev.biologicalContext = context;
                        }
                    }
                }
                if (mdata.containsKey("casual")) {
                    row.set(EDGE_CAUSAL, mdata.get("casual"));
                }
            }
        }
    }
}
