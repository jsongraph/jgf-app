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

    public static final String BEL_EVIDENCE_TABLE = "BEL.Evidence";

    public static final String NETWORK_SUID = "network suid";
    public static final String NETWORK_NAME = "network name";
    public static final String EDGE_SUID = "edge suid";
    public static final String BEL_STATEMENT = "bel statement";
    public static final String SUMMARY_TEXT = "summary text";
    public static final String CITATION_TYPE = "citation type";
    public static final String CITATION_ID = "citation id";
    public static final String CITATION_NAME = "citation name";
    public static final String SPECIES = "species";

    private Constants() {
        // static accessors only
    }
}
