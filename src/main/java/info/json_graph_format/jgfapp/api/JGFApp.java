package info.json_graph_format.jgfapp.api;

import info.json_graph_format.jgfapp.api.util.Pair;

import javax.swing.*;
import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * {@link JGFApp} describes the application and version.
 */
public class JGFApp {

    public static final String NAME = "JGF App";
    public static final String DESC =
            "This application provides import of Cytoscape Networks in " +
                    "JSON Graph Format. Additionally it can visualize BEL networks " +
                    "and show evidence detail on edges.";
    public static final String VERSION = "1.0.2";
    public static final Icon ICON = new ImageIcon(JGFApp.class.getResource("/icon.png"));
    public static final Collection<Pair<String, String>> LINKS = asList(
            new Pair<String, String>("Source code", "https://github.com/jsongraph/jgf-app"),
            new Pair<String, String>("Submit issues", "https://github.com/jsongraph/jgf-app/issues")
    );

    private JGFApp() {
        //static accessors only
    }
}
