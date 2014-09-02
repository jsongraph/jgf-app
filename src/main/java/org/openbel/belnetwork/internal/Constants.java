package org.openbel.belnetwork.internal;

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
     * Multiplier for coordinate translation: {@value}
     */
    public static final int COORDINATE_TRANSLATION = 1500;

    /**
     * Log name for Cytoscape user messages: {@value}
     */
    public static final String CY_USER_MESSAGES = "CyUserMessages";

    /**
     * The name of the Cytoscape evidence table: {@value}
     */
    public static final String BEL_EVIDENCE_TABLE = "BEL.Evidence";

    /**
     * Name of the cytoscape column holding the network identifier in the
     * {@link Constants#BEL_EVIDENCE_TABLE}: {@value}
     */
    public static final String NETWORK_SUID = "network suid";

    /**
     * Name of the cytoscape column holding the network name in the
     * {@link Constants#BEL_EVIDENCE_TABLE}: {@value}
     */
    public static final String NETWORK_NAME = "network name";

    /**
     * Name of the cytoscape column holding the edge identifier in the
     * {@link Constants#BEL_EVIDENCE_TABLE}: {@value}
     */
    public static final String EDGE_SUID = "edge suid";

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

    private Constants() {
        // static accessors only
    }
}
