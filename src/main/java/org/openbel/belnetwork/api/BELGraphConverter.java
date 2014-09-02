package org.openbel.belnetwork.api;

import org.cytoscape.model.CyNetwork;
import org.openbel.belnetwork.api.model.Graph;

/**
 * {@link BELGraphConverter} converts a {@link Graph graph} into a
 * {@link CyNetwork cytoscape network} and vice versa.
 */
public interface BELGraphConverter {

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
