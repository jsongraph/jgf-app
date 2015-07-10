package info.json_graph_format.jgfapp.api.model;

import java.util.List;

/**
 * {@link Root} represents the root object of a JSON graph. It may contain one
 * {@link Graph} or a {@link List} of {@link Graph}.
 */
public class Root {

    public Graph graph;
    public List<Graph> graphs;

    /**
     * Determines the array of {@link Graph} from the {@link Root root object}.
     *
     * @param root the {@link Root} object;
     * @return array of {@link Graph} objects; returns {@code null} when
     * {@code root} or {@code root}'s graph fields are all {@code null}
     */
    public static Graph[] determineGraphs(Root root) {
        if (root == null) return null;

        if (root.graph != null)
            return new Graph[]{root.graph};
        if (root.graphs != null)
            return root.graphs.toArray(new Graph[root.graphs.size()]);

        return null;
    }
}
