package info.json_graph_format.jgfapp.internal.ui;

import info.json_graph_format.jgfapp.api.BELEvidenceMapper;
import info.json_graph_format.jgfapp.api.EvidenceReader;
import info.json_graph_format.jgfapp.api.model.Evidence;
import info.json_graph_format.jgfapp.internal.BELEvidenceMapperImpl;
import info.json_graph_format.jgfapp.internal.EvidenceReaderImpl;
import javafx.scene.web.WebEngine;
import org.cytoscape.model.*;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

import java.io.IOException;
import java.util.Objects;

public class EvidenceCreatePanel extends HTMLPanel {

    private final EvidencePanelComponent evComponent;
    private final CyTable evTable;
    private final CyNetwork cyN;
    private final CyEdge cyE;

    public EvidenceCreatePanel(EvidencePanelComponent evComponent, CyTable evTable, CyNetwork cyN, CyEdge cyE) {
        super("/form-html/form.html");

        this.evComponent = evComponent;
        this.evTable     = evTable;
        this.cyN         = cyN;
        this.cyE         = cyE;
    }

    @Override
    protected void onDocumentLoaded(WebEngine webEngine) {
        // handle save button
        Element saveButton = webEngine.getDocument().getElementById("save-button");
        ((EventTarget) saveButton).addEventListener("click", evt -> {
            if (!Objects.equals(((Element) evt.getCurrentTarget()).getTagName(), "BUTTON")) {
                return;
            }

            BELEvidenceMapper mapper = new BELEvidenceMapperImpl();
            mapper.mapToTable(null, cyN, cyE, convertEvidence(), evTable);
            evComponent.refresh(cyE);
        }, false);

        // handle reset button
        Element resetButton = webEngine.getDocument().getElementById("reset-button");
        ((EventTarget) resetButton).addEventListener("click", evt -> {
            if (!Objects.equals(((Element) evt.getCurrentTarget()).getTagName(), "BUTTON")) {
                return;
            }
            webEngine.executeScript("reset();");
        }, false);
    }

    private Evidence convertEvidence() {
        try {
            String json = (String) webEngine.executeScript("readFormAsJSON();");
            EvidenceReader evidenceReader = new EvidenceReaderImpl();
            return evidenceReader.read(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
