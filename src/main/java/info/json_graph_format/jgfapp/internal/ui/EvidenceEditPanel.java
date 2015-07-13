package info.json_graph_format.jgfapp.internal.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import info.json_graph_format.jgfapp.api.BELEvidenceMapper;
import info.json_graph_format.jgfapp.api.EvidenceReader;
import info.json_graph_format.jgfapp.api.EvidenceWriter;
import info.json_graph_format.jgfapp.api.model.Evidence;
import info.json_graph_format.jgfapp.internal.BELEvidenceMapperImpl;
import info.json_graph_format.jgfapp.internal.EvidenceReaderImpl;
import info.json_graph_format.jgfapp.internal.EvidenceWriterImpl;
import javafx.scene.web.WebEngine;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

import java.io.IOException;
import java.util.Objects;

public class EvidenceEditPanel extends HTMLPanel {

    private final Long evidenceId;
    private final EvidencePanelComponent evComponent;
    private final CyTable evTable;
    private final CyNetwork cyN;
    private final CyEdge cyE;
    private final String evidenceJSON;

    EvidenceEditPanel(Evidence evidence, EvidencePanelComponent evComponent, CyTable evTable, CyNetwork cyN, CyEdge cyE) {
        super("/form-html/form.html");

        this.evidenceId  = evidence.evidenceId;
        this.evComponent = evComponent;
        this.evTable     = evTable;
        this.cyN         = cyN;
        this.cyE         = cyE;

        try {
            EvidenceWriter evidenceWriter = new EvidenceWriterImpl();
            evidenceJSON                  = evidenceWriter.serialize(evidence);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDocumentLoaded(WebEngine webEngine) {
        HTMLElement htmlBody = ((HTMLDocument) webEngine.getDocument()).getBody();
        Element pEvidence = createElementWithText(webEngine.getDocument(), "p", evidenceJSON);
        pEvidence.setAttribute("id", "evidence-json");
        pEvidence.setAttribute("style", "display: none;");
        htmlBody.appendChild(pEvidence);

        webEngine.executeScript("populateForm();");

        // handle save button
        Element saveButton = webEngine.getDocument().getElementById("save-button");
        ((EventTarget) saveButton).addEventListener("click", evt -> {
            if (!Objects.equals(((Element) evt.getCurrentTarget()).getTagName(), "BUTTON")) {
                return;
            }

            BELEvidenceMapper mapper = new BELEvidenceMapperImpl();
            mapper.mapToTable(evidenceId, cyN, cyE, convertEvidence(), evTable);
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

    private static Element createElementWithText(Document doc, String tag, String textContent) {
        Element el = doc.createElement(tag);
        el.setTextContent(textContent);
        return el;
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
