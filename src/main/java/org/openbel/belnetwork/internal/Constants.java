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

    private Constants() {
        // static accessors only
    }
}
