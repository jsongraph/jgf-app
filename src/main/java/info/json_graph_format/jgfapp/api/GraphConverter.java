package info.json_graph_format.jgfapp.api;

import org.cytoscape.model.CyNetwork;
import info.json_graph_format.jgfapp.api.model.Graph;

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
    public CyNetwork convert(Graph graph);

    /**
     * Converts a {@link CyNetwork cytoscape network} into a {@link Graph graph}.
     *
     * @param network the {@link CyNetwork cytoscape network} to convert
     * @return a {@link Graph} or {@code null} if {@code network} is
     * {@code null}
     */
    public Graph convert(CyNetwork network);
}
