package info.json_graph_format.jgfapp.api;

import info.json_graph_format.jgfapp.api.model.Edge;
import info.json_graph_format.jgfapp.api.model.Evidence;
import info.json_graph_format.jgfapp.api.model.Graph;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

import java.util.Optional;

/**
 * {@link BELEvidenceMapper} handles {@link Evidence} conversions.
 */
public interface BELEvidenceMapper {

    /**
     * Maps an {@link Edge edge} to an array of {@link Evidence} objects.
     *
     * @param graph the {@link Graph}; cannot be {@code null}
     * @param edge  the {@link Edge}; cannot be {@code null}
     * @return the {@link Evidence} array; will not be {@code null} but may be empty
     * @throws java.lang.NullPointerException if {@code graph} or {@code edge} is
     *                                        {@code null}
     */
    public Evidence[] mapEdgeToEvidence(Graph graph, Edge edge);

    /**
     * Map an {@link Evidence} object to a {@link CyTable table}.
     * <br><br>
     * The {@link Graph graph} should provide the cytoscape network information
     * (see {@link Graph#cyNetwork}). This can provide access to the network SUID.
     * <br><br>
     * The {@link Edge edge} should provide the cytoscape edge information
     * (see {@link Edge#cyEdge}). This can provide access to the edge SUID.
     *
     * @param graph    the {@link Graph} containg the evidence; cannot be {@code null}
     *                 and {@link Graph#cyNetwork} cannot be {@code null}
     * @param edge     the {@link Edge} containing the evidence; cannot be {@code null}
     *                 and {@link Edge#cyEdge} cannot be {@code null}
     * @param evidence the {@link Evidence} to map; cannot be {@code null}
     * @param table    the {@link CyTable} to map to; cannot be {@code null}
     * @throws NullPointerException     when {@code graph}, {@code edge},
     *                                  {@code evidence}, or {@code table} is {@code null}
     * @throws IllegalArgumentException when the {@code graph}'s
     *                                  {@link Graph#cyNetwork} is {@code null} or the {@code edge}'s
     *                                  {@link Edge#cyEdge} is {@code null}
     */
    public void mapToTable(Graph graph, Edge edge, Evidence evidence, CyTable table);

    /**
     * Map an {@link Evidence} object to a {@link CyTable table}.
     * <br><br>
     * The {@link CyNetwork network} provides the network information.
     * <br><br>
     * The {@link CyEdge edge} provides the edge information.
     *
     * @param cyN      the {@link CyNetwork} containg the evidence; cannot be {@code null}
     * @param cyE      the {@link Edge} containing the evidence; cannot be {@code null}
     * @param evidence the {@link Evidence} to map; cannot be {@code null}
     * @param table    the {@link CyTable} to map to; cannot be {@code null}
     * @throws NullPointerException     when {@code graph}, {@code edge},
     *                                  {@code evidence}, or {@code table} is {@code null}
     * @throws IllegalArgumentException when the {@code graph}'s
     *                                  {@link Graph#cyNetwork} is {@code null} or the {@code edge}'s
     *                                  {@link Edge#cyEdge} is {@code null}
     */
    public void mapToTable(Long suid, CyNetwork cyN, CyEdge cyE, Evidence evidence, CyTable table);

    /**
     * Maps an {@link Edge edge} of the {@link CyTable table} to an array of
     * {@link Evidence} objects.
     *
     * @param edge  the {@link CyEdge}; cannot be {@code null}
     * @param table the {@link CyTable}; cannot be {@code null}
     * @return the {@link Evidence} array; will not be {@code null} but may be empty
     */
    public Evidence[] mapFromTable(CyEdge edge, CyTable table);
}
