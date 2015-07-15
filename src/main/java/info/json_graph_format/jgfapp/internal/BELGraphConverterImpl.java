package info.json_graph_format.jgfapp.internal;

import info.json_graph_format.jgfapp.api.GraphConverter;
import info.json_graph_format.jgfapp.api.model.Edge;
import info.json_graph_format.jgfapp.api.model.Graph;
import info.json_graph_format.jgfapp.api.model.MetadataProvider;
import info.json_graph_format.jgfapp.api.model.Node;
import org.cytoscape.model.*;

import java.util.*;
import java.util.Map.Entry;

import static info.json_graph_format.jgfapp.api.util.FormatUtility.translateCoordinates;
import static info.json_graph_format.jgfapp.api.util.TableUtility.getOrCreateColumn;
import static info.json_graph_format.jgfapp.api.util.TableUtility.getOrCreateColumnByPrototypes;
import static info.json_graph_format.jgfapp.api.util.TableUtility.setColumnValue;
import static info.json_graph_format.jgfapp.api.util.Utility.*;

/**
 * {@link BELGraphConverterImpl} implements a {@link GraphConverter} that converts
 * {@link Graph} to {@link CyNetwork} and vice versa. This implementation understands
 * BEL format as well.
 */
public class BELGraphConverterImpl implements GraphConverter {

    private static final String X_COORD = "x coordinate";
    private static final String Y_COORD = "y coordinate";
    private static final String Z_COORD = "z coordinate";

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
        final CyRow row = network.getRow(network);

        CyTable networkTable = network.getDefaultNetworkTable();
        CyColumn beljgfColumn = getOrCreateColumn("BELJGF Version", String.class, true, networkTable);
        row.set(beljgfColumn.getName(), graph.beljgfVersion);

        if (metadata != null) {
            Map<String, CyColumn> columns = inferColumns(networkTable, graph);
            for (Entry<String, Object> metadataEntry : metadata.entrySet()) {
                String name = metadataEntry.getKey();
                Object value = metadataEntry.getValue();

                CyColumn column = columns.get(name);
                setColumnValue(name, value, row, column);
            }
        }

        if (hasLength(graph.label)) row.set(CyNetwork.NAME, graph.label);
    }

    private static Map<String, CyNode> mapNodeData(Graph graph, CyNetwork network) {
        network.getDefaultNodeTable().createColumn(X_COORD, Double.class, true);
        network.getDefaultNodeTable().createColumn(Y_COORD, Double.class, true);
        network.getDefaultNodeTable().createColumn(Z_COORD, Double.class, true);
        Map<String, CyNode> createdNodes = new HashMap<>();
        if (hasItems(graph.nodes)) {
            Map<String, CyColumn> columns = inferColumns(
                    network.getDefaultNodeTable(),
                    graph.nodes.toArray(new Node[graph.nodes.size()]));

            Map<CyRow, List<Double>> nodeCoordinates = new LinkedHashMap<>();
            for (Node n : graph.nodes) {
                final CyNode cyNode = network.addNode();
                n.cyNode = cyNode;

                final CyRow row = network.getRow(cyNode);
                row.set(CyNetwork.NAME, n.label);

                Map<String, Object> metadata = n.metadata;
                if (metadata != null) {
                    for (Entry<String, Object> metadataEntry : metadata.entrySet()) {
                        String name = metadataEntry.getKey();
                        Object value = metadataEntry.getValue();

                        CyColumn column = columns.get(name);
                        setColumnValue(name, value, row, column);
                    }

                    if (metadata.containsKey("coordinate")) {
                        Object coords = n.metadata.get("coordinate");
                        if (coords != null && (coords instanceof List)) {
                            List<Double> coordinates = typedList((List) coords, Double.class);
                            nodeCoordinates.put(row, coordinates);
                        }
                    }
                }

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
        }

        return createdNodes;
    }

    private static void mapEdgeData(Map<String, CyNode> createdNodes, Graph graph, CyNetwork network) {
        if (hasItems(graph.edges)) {
            Map<String, CyColumn> columns = inferColumns(
                    network.getDefaultEdgeTable(),
                    graph.edges.toArray(new Edge[graph.edges.size()]));

            for (Edge edge : graph.edges) {
                CyNode sourceNode = createdNodes.get(edge.source);
                CyNode targetNode = createdNodes.get(edge.target);
                final CyEdge cyEdge = network.addEdge(sourceNode,
                        targetNode, edge.directed);
                edge.cyEdge = cyEdge;

                final CyRow row = network.getRow(cyEdge);
                row.set(CyNetwork.NAME, edge.label);
                row.set(CyEdge.INTERACTION, edge.relation);

                Map<String, Object> metadata = edge.metadata;
                if (metadata != null) {
                    for (Entry<String, Object> metadataEntry : metadata.entrySet()) {
                        String name = metadataEntry.getKey();
                        Object value = metadataEntry.getValue();

                        CyColumn column = columns.get(name);
                        setColumnValue(name, value, row, column);
                    }
                }
            }
        }
    }

    private static Map<String, CyColumn> inferColumns(CyTable table, MetadataProvider... items) {
        // combine metadata together...
        Map<String, Collection<Object>> combinedMetadata =
                new HashMap<>(items.length);
        for (MetadataProvider item : items) {
            Map<String, Object> metadata = item.getMetadata();
            if (metadata == null) continue;

            for (Entry<String, Object> e : metadata.entrySet()) {
                Collection<Object> combinedValues = combinedMetadata.get(e.getKey());
                if (combinedValues == null) {
                    combinedValues = new ArrayList<>();
                    combinedMetadata.put(e.getKey(), combinedValues);
                }
                combinedValues.add(e.getValue());
            }
        }

        // ...infer Cytoscape column for each metadata key / domain
        Map<String, CyColumn> columns = new HashMap<>(combinedMetadata.size());
        for (Entry<String, Collection<Object>> e : combinedMetadata.entrySet()) {
            String name = e.getKey();
            CyColumn column = getOrCreateColumnByPrototypes(name, e.getValue(), false, table);
            columns.put(name, column);
        }

        return columns;
    }
}
