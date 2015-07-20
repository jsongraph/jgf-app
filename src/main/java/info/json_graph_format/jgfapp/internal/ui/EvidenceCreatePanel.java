package info.json_graph_format.jgfapp.internal.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import info.json_graph_format.jgfapp.api.BELEvidenceMapper;
import info.json_graph_format.jgfapp.api.EvidenceReader;
import info.json_graph_format.jgfapp.api.EvidenceWriter;
import info.json_graph_format.jgfapp.api.model.Citation;
import info.json_graph_format.jgfapp.api.model.Evidence;
import info.json_graph_format.jgfapp.internal.BELEvidenceMapperImpl;
import info.json_graph_format.jgfapp.internal.EvidenceReaderImpl;
import info.json_graph_format.jgfapp.internal.EvidenceWriterImpl;
import javafx.scene.web.WebEngine;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

import static info.json_graph_format.jgfapp.internal.Constants.dynamicExperimentContextColumns;
import static info.json_graph_format.jgfapp.internal.Constants.dynamicMetadataColumns;
import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

public class EvidenceCreatePanel extends HTMLPanel {

    private final EvidencePanelComponent evComponent;
    private final CyTable evTable;
    private final CyNetwork cyN;
    private final CyEdge cyE;
    private final String evidenceJSON;

    public EvidenceCreatePanel(EvidencePanelComponent evComponent, CyTable evTable, CyNetwork cyN, CyEdge cyE) {
        super("form.html");

        this.evComponent  = evComponent;
        this.evTable      = evTable;
        this.cyN          = cyN;
        this.cyE          = cyE;
        this.evidenceJSON = createNewEvidenceJSON(cyN, cyE, evTable);
    }

    @Override
    protected void onDocumentLoaded(WebEngine webEngine) {
        if (webEngine.getDocument() == null) return;

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

    private static String createNewEvidenceJSON(CyNetwork cyN, CyEdge cyE, CyTable evTable) {
        try {
            Evidence evidence = new Evidence();
            String source   = cyN.getDefaultNodeTable().getRow(cyE.getSource().getSUID()).get("name", String.class);
            String relation = cyN.getDefaultEdgeTable().getRow(cyE.getSUID()).get("interaction", String.class);
            String target   = cyN.getDefaultNodeTable().getRow(cyE.getTarget().getSUID()).get("name", String.class);
            evidence.belStatement      = format("%s %s %s", source, relation, target);
            evidence.citation          = new Citation();
            evidence.summaryText       = "";

            evidence.experimentContext =
                    evTable.getColumns().
                        stream().
                        filter(dynamicExperimentContextColumns()).
                        map(CyColumn::getName).
                        collect(toMap(Function.<String>identity(), s -> ""));

            evidence.metadata =
                    evTable.getColumns().
                            stream().
                            filter(dynamicMetadataColumns()).
                            map(CyColumn::getName).
                            collect(toMap(Function.<String>identity(), s -> ""));

            EvidenceWriter evidenceWriter = new EvidenceWriterImpl();
            return evidenceWriter.serialize(evidence);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
