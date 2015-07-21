package info.json_graph_format.jgfapp.api;

import info.json_graph_format.jgfapp.api.model.Graph;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

/**
 * {@link GraphConverter} converts a {@link Graph graph} into a
 * {@link CyNetwork cytoscape network} and vice versa.
 */
public interface GraphConverter {

    /**
     * Converts a {@link Graph graph} into a {@link CyNetwork cytoscape network}.
     *
     * @param graph the {@link Graph} to convert
     * @return a {@link CyNetwork} or {@code null} if {@code graph} is
     * {@code null}
     */
    CyNetwork convert(Graph graph);

    /**
     * Converts a {@link CyNetwork cytoscape network} into a {@link Graph graph}.
     * If the {@link CyNetworkView cytoscape network view} is provides this should
     * inform the graph node coordinates.
     *
     * @param network the {@link CyNetwork cytoscape network} to convert
     * @param networkView the {@link CyNetworkView cytoscape network view}
     * @return a {@link Graph} or {@code null} if {@code network} is
     * {@code null}
     */
    Graph convert(CyNetwork network, CyNetworkView networkView);
}
