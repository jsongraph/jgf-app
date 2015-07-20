package info.json_graph_format.jgfapp.internal.ui;

import info.json_graph_format.jgfapp.api.model.Evidence;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.String.valueOf;

public class EvidenceListPanel extends HTMLPanel {

    private EvidencePanelComponent evidenceComponent;
    private EventListener createClickEvent;

    public EvidenceListPanel() {
        super("list.html");

    }

    @Override
    protected void onDocumentLoaded(WebEngine webEngine) {
        // All elements bind in the update method.
    }

    public void update(final List<Evidence> evidences, EvidencePanelComponent evidenceComponent) {
        this.evidenceComponent = evidenceComponent;
        Platform.runLater(() -> {
            Document doc = webEngine.getDocument();

            // Bind to create-button once and lazily. HTML document was null when binding
            // in onDocumentLoaded.
            if (createClickEvent == null) {
                Element createButton = doc.getElementById("create-button");
                createClickEvent = evt -> {
                    if (!Objects.equals(((Element) evt.getCurrentTarget()).getTagName(), "BUTTON")) {
                        return;
                    }
                    evidenceComponent.createEvidence();
                };
                ((EventTarget) createButton).addEventListener("click", createClickEvent, false);
            }

            Element evidenceTable = doc.getElementById("evidence-table");
            Element tableBody = (Element) evidenceTable.getElementsByTagName("tbody").item(0);
            while (tableBody.hasChildNodes()) {
                tableBody.removeChild(tableBody.getFirstChild());
            }

            // Curry mapTableRow, can now apply toEvidence with arity 1.
            // Look ma, *less* ceremony!
            final Function<Document, Function<Evidence, Element>> mapFunc = document -> (evidence -> mapTableRow(document, evidence));
            final Function<Evidence, Element> toEvidenceAnd = mapFunc.apply(doc);
            Consumer<Element> addToTableBody = tableBody::appendChild;

            /* hey */ evidences.stream().map(toEvidenceAnd).forEach(addToTableBody);
        });
    }

    private Element mapTableRow(Document doc, Evidence evidence) {
        Element tr = doc.createElement("tr");
        tr.appendChild(createElementWithText(doc, "td", evidence.belStatement));
        tr.appendChild(createElementWithText(doc, "td", evidence.citation.id));
        tr.appendChild(createElementWithText(doc, "td", evidence.citation.name));

        Element editButton = createEditButton(doc, evidence);
        ((EventTarget) editButton).addEventListener("click",
                evt -> evidenceComponent.editEvidence(evidence), false);
        tr.appendChild(createElementWithChild(doc, "td", editButton));

        Element deleteButton = createDeleteButton(doc, evidence);
        ((EventTarget) deleteButton).addEventListener("click", evt -> {
            String evidenceId = ((Element) evt.getCurrentTarget()).getAttribute("evidence-id");
            System.out.println("Delete evidence: " + evidenceId);
            this.evidenceComponent.deleteEvidence(Long.valueOf(evidenceId));
        }, false);
        tr.appendChild(createElementWithChild(doc, "td", deleteButton));

        tr.appendChild(createInput(doc, "hidden", "evidence-id", valueOf(evidence.evidenceId)));

        return tr;
    }

    private static Element createEditButton(Document doc, Evidence evidence) {
        Element button = doc.createElement("button");
        button.setAttribute("evidence-id", valueOf(evidence.evidenceId));
        button.setAttribute("type", "button");
        button.setAttribute("class", "btn btn-info");
        button.setTextContent("Edit");

        return button;
    }

    private static Element createDeleteButton(Document doc, Evidence evidence) {
        Element button = doc.createElement("button");
        button.setAttribute("evidence-id", valueOf(evidence.evidenceId));
        button.setAttribute("type", "button");
        button.setAttribute("class", "btn btn-danger");
        button.setTextContent("Delete");

        return button;
    }
}
