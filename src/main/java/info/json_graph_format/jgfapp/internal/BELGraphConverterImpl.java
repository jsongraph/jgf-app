package info.json_graph_format.jgfapp.internal;

import org.cytoscape.model.*;
import info.json_graph_format.jgfapp.api.BELGraphConverter;
import info.json_graph_format.jgfapp.api.model.*;

import java.util.*;

import static info.json_graph_format.jgfapp.api.util.FormatUtility.translateCoordinates;
import static info.json_graph_format.jgfapp.api.util.Utility.typedList;

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
        if (graph == null) return null;

        CyNetwork network = networkFactory.createNetwork();
        mapNetworkData(graph, network);
        Map<String, CyNode> createdNodes = mapNodeData(graph, network);
        mapEdgeData(createdNodes, graph, network);

        return network;
    }

    /**
     * {@inheritDoc}
     * <br><br>
     * FIXME Unsupported.
     */
    @Override
    public Graph convert(CyNetwork network) {
        if (network == null) return null;

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

        Map<CyRow, List<Double>> nodeCoordinates = new LinkedHashMap<CyRow, List<Double>>();
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
                    nodeCoordinates.put(row, coordinates);
                }
            }
            if (n.metadata.containsKey("bel_function_type"))
                row.set(BEL_FUNCTION, n.metadata.get("bel_function_type"));

            createdNodes.put(n.id, cyNode);
        }

        List<List<Double>> translateCoordinates = translateCoordinates(nodeCoordinates.values());
        CyRow[] rows = nodeCoordinates.keySet().toArray(new CyRow[nodeCoordinates.size()]);
        for (int i = 0; i < rows.length; i++) {
            CyRow row = rows[i];
            List<Double> coord = translateCoordinates.get(i);
            if (coord.size() > 0) row.set(X_COORD, coord.get(0));
            if (coord.size() > 1) row.set(Y_COORD, coord.get(1));
            if (coord.size() > 2) row.set(Z_COORD, coord.get(2));
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
                if (mdata.containsKey("casual")) {
                    row.set(EDGE_CAUSAL, mdata.get("casual"));
                }
            }
        }
    }
}
