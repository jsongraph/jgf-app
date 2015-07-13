package info.json_graph_format.jgfapp.internal;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 * {@link Constants} defines constant parameters central to the app configuration.
 */
public class Constants {

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
     * Name of the cytoscape column holding the species in the
     * {@link Constants#BEL_EVIDENCE_TABLE}: {@value}
     */
    public static final String SPECIES = "species";

    /**
     * Name of the cytoscape column holding the selected status
     * ({@code boolean}) of the row.
     */
    public static final String SELECTED = "selected";

    private Constants() {
        // static accessors only
    }
}
