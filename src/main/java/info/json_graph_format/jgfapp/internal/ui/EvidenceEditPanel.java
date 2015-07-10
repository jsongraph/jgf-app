package info.json_graph_format.jgfapp.internal.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import info.json_graph_format.jgfapp.api.EvidenceWriter;
import info.json_graph_format.jgfapp.api.model.Evidence;
import info.json_graph_format.jgfapp.internal.EvidenceWriterImpl;
import javafx.scene.web.WebEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

public class EvidenceEditPanel extends HTMLPanel {

    private final String evidenceJSON;

    protected EvidenceEditPanel(Evidence evidence) {
        super("/form-html/form.html");

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
        htmlBody.appendChild(pEvidence);

        webEngine.executeScript("populateForm();");
    }

    private static Element createElementWithText(Document doc, String tag, String textContent) {
        Element el = doc.createElement(tag);
        el.setTextContent(textContent);
        return el;
    }
}
