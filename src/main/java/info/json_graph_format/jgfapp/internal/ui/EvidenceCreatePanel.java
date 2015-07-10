package info.json_graph_format.jgfapp.internal.ui;

import javafx.scene.web.WebEngine;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

public class EvidenceCreatePanel extends HTMLPanel {

    public EvidenceCreatePanel() {
        super("/form-html/form.html");
    }

    @Override
    protected void onDocumentLoaded(WebEngine webEngine) {
        Element createButton = webEngine.getDocument().getElementById("create-button");
        ((EventTarget) createButton).addEventListener("click", evt -> {
            String action = ((Element) evt.getCurrentTarget()).getAttribute("action");
            System.out.println("Button clicked, action: " + action);
            evt.preventDefault();
        }, false);
    }
}
