package info.json_graph_format.jgfapp.internal;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.vizmap.VisualStyle;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;

import static java.util.Arrays.asList;

/**
 * {@link Constants} defines constant parameters central to the app configuration.
 */
public class Constants {

    public enum BELJGFSchema {
        VERSION_2_0("2.0", "/bel-json-graph-2.0.schema.json"),
        VERSION_1_0("1.0", "/bel-json-graph-1.0.schema.json");

        public final String version;
        public final String resourcePath;

        BELJGFSchema(String version, String resourcePath) {
            this.version = version;
            this.resourcePath = resourcePath;
        }
    }
    /**
     * Defines the BELJGF schemas supported by this application.
     */
    public static final Map<String, String> BELJGF_SCHEMAS = new HashMap<>();
    static {
        BELJGF_SCHEMAS.put("1.0", "/bel-json-graph-1.0.schema.json");
        BELJGF_SCHEMAS.put("2.0", "/bel-json-graph-2.0.schema.json");
    }

    /**
     * Classpath for style resource: {@value}
     */
    public static final String STYLE_RESOURCE_PATH = "/style.xml";

    /**
     * Default {@link VisualStyle} title to apply to BEL JSON graph
     * {@link CyNetwork networks}: {@value}
     */
    public static final String APPLIED_VISUAL_STYLE = "BEL Visualization";

    /**
     * Default view width: {@value}
     */
    public static final int DEFAULT_VIEW_WIDTH = 480;

    /**
     * Default view height: {@value}
     */
    public static final int DEFAULT_VIEW_HEIGHT = 370;

    /**
     * Coordinate multiplier: {@value}
     */
    public static final double COORDINATE_MULTIPLIER = 3.0;

    /**
     * Log name for Cytoscape user messages: {@value}
     */
    public static final String CY_USER_MESSAGES = "CyUserMessages";

    /**
     * The name of the Cytoscape evidence table: {@value}
     */
    public static final String BEL_EVIDENCE_TABLE = "BEL.Evidence";

    /**
     * The name of the pubmed citation type: {@value}
     */
    public static final String PUBMED = "PubMed";

    /**
     * The name of the online resource citation type: {@value}
     */
    public static final String ONLINE_RESOURCE = "Online Resource";

    /**
     * The URL prefix for the NBCI PubMed resource: {@value}
     */
    public static final String PUBMED_URL_PREFIX = "http://www.ncbi.nlm.nih.gov/pubmed/";

    /**
     * Name of the cytoscape column holding the network identifier in the
     * {@link Constants#BEL_EVIDENCE_TABLE}: {@value}
     * <br><br>
     * The columns ends must end in <em>.SUID</em> so Cytoscape can associate
     * the SUID values to the Cytoscape networks when they change.
     */
    public static final String NETWORK_SUID = "network.SUID";

    /**
     * Name of the cytoscape column holding the network name in the
     * {@link Constants#BEL_EVIDENCE_TABLE}: {@value}
     */
    public static final String NETWORK_NAME = "network name";

    /**
     * Name of the cytoscape column holding the name for the
     * {@link org.cytoscape.model.CyIdentifiable}: {@value}
     */
    public static final String NAME = "name";

    /**
     * Name of the cytoscape column holding the shared name for the
     * {@link org.cytoscape.model.CyIdentifiable}: {@value}
     */
    public static final String SHARED_NAME = "shared name";

    /**
     * Name of the cytoscape column holding the edge identifier in the
     * {@link Constants#BEL_EVIDENCE_TABLE}: {@value}
     * <br><br>
     * The columns ends must end in <em>.SUID</em> so Cytoscape can associate
     * the SUID values to the Cytoscape edges when they change.
     */
    public static final String EDGE_SUID = "edge.SUID";

    /**
     * Name of the cytoscape column holding the bel statement in the
     * {@link Constants#BEL_EVIDENCE_TABLE}: {@value}
     */
    public static final String BEL_STATEMENT = "bel statement";

    /**
     * Name of the cytoscape column holding the summary text in the
     * {@link Constants#BEL_EVIDENCE_TABLE}: {@value}
     */
    public static final String SUMMARY_TEXT = "summary text";

    /**
     * Name of the cytoscape column holding the citation type in the
     * {@link Constants#BEL_EVIDENCE_TABLE}: {@value}
     */
    public static final String CITATION_TYPE = "citation type";

    /**
     * Name of the cytoscape column holding the citation id in the
     * {@link Constants#BEL_EVIDENCE_TABLE}: {@value}
     */
    public static final String CITATION_ID = "citation id";

    /**
     * Name of the cytoscape column holding the citation name in the
     * {@link Constants#BEL_EVIDENCE_TABLE}: {@value}
     */
    public static final String CITATION_NAME = "citation name";

    /**
     * Name of the cytoscape column holding the selected status
     * ({@code boolean}) of the row.
     */
    public static final String SELECTED = "selected";

    /**
     * Name of the cytoscape column holding the annotations list for the
     * {@link org.cytoscape.model.CyNetwork}: {@value}
     */
    public static final String __ANNOTATIONS = "__Annotations";

    /**
     * Name of the cytoscape column holding the interaction: {@value}
     */
    public static final String INTERACTION = "interaction";

    /**
     * Name of the cytoscape column holding the shared interaction: {@value}
     */
    public static final String SHARED_INTERACTION = "shared interaction";

    /**
     * The fixed columns that are part of the Evidence Table. This
     * allows one to determine the dynamic columns within the
     * experiment context and metadata sections.
     */
    public static final Set<String> FIXED_EVIDENCE_COLUMNS = new HashSet<>(
            asList(CyNetwork.SUID, NETWORK_SUID, NETWORK_NAME, EDGE_SUID,
                    BEL_STATEMENT, SUMMARY_TEXT, CITATION_ID, CITATION_NAME,
                    CITATION_TYPE));

    public static final Set<String> FIXED_NETWORK_COLUMNS = new HashSet<>(
            asList(CyNetwork.SUID, SELECTED, NAME, SHARED_NAME, __ANNOTATIONS)
    );

    public static final Set<String> FIXED_NODE_COLUMNS = new HashSet<>(
            asList(CyNetwork.SUID, SELECTED, NAME, SHARED_NAME)
    );

    public static final Set<String> FIXED_EDGE_COLUMNS = new HashSet<>(
            asList(CyNetwork.SUID, SELECTED, NAME, SHARED_NAME, INTERACTION, SHARED_INTERACTION)
    );

    public static Predicate<Map.Entry<String, Object>> dynamiceNetworkEntries() {
        return entry -> ! FIXED_NETWORK_COLUMNS.contains(entry.getKey());
    }

    public static Predicate<Map.Entry<String, Object>> dynamiceNodeEntries() {
        return entry -> ! FIXED_NODE_COLUMNS.contains(entry.getKey());
    }

    public static Predicate<Map.Entry<String, Object>> dynamiceEdgeEntries() {
        return entry -> ! FIXED_EDGE_COLUMNS.contains(entry.getKey());
    }

    public static Predicate<CyColumn> dynamicEvidenceColumns() {
        return column -> ! FIXED_EVIDENCE_COLUMNS.contains(column.getName());
    }

    public static Predicate<CyColumn> dynamicExperimentContextColumns() {
        return column -> column.getName().startsWith("experiment_context_");
    }

    public static Predicate<CyColumn> dynamicMetadataColumns() {
        return column -> column.getName().startsWith("metadata_");
    }

    public static Predicate<Map.Entry<String, Object>> experimentContextEntries() {
        return entry -> entry.getKey().startsWith("experiment_context_");
    }

    public static Predicate<Map.Entry<String, Object>> metadataEntries() {
        return entry -> entry.getKey().startsWith("metadata_");
    }

    public static <T> Collector<T, List<T>, List<T>> toImmutableList() {
        return Collector.of(
                ArrayList::new,
                List::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                Collections::unmodifiableList
        );
    }

    private Constants() {
        // static accessors only
    }
}
