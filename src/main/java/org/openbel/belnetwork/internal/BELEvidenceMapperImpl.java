package org.openbel.belnetwork.internal;

import org.cytoscape.model.*;
import org.openbel.belnetwork.api.BELEvidenceMapper;
import org.openbel.belnetwork.api.model.*;

import java.util.*;
import java.util.Map.Entry;

import static java.util.Arrays.asList;
import static org.openbel.belnetwork.api.util.FormatUtility.getOrEmptyString;
import static org.openbel.belnetwork.api.util.FormatUtility.getOrZero;
import static org.openbel.belnetwork.api.util.TableUtility.getOrCreateColumn;
import static org.openbel.belnetwork.api.util.Utility.typedList;
import static org.openbel.belnetwork.internal.Constants.*;

/**
 * {@link BELEvidenceMapperImpl} implements a {@link BELEvidenceMapper} to
 * map between {@link Evidence} and {@link CyTable} objects.
 */
public class BELEvidenceMapperImpl implements BELEvidenceMapper {

    /**
     * {@inheritDoc}
     */
    @Override
    public Evidence[] mapEdgeToEvidence(Graph graph, Edge edge) {
        if (graph == null) throw new NullPointerException("graph cannot be null");
        if (edge == null) throw new NullPointerException("edge cannot be null");

        if (edge.metadata == null) return new Evidence[0];
        Object evidences = edge.metadata.get("evidences");
        if (evidences == null || !(evidences instanceof List))
            return new Evidence[0];
        List<Map> evidenceMap = typedList(((List) evidences), Map.class);

        List<Evidence> evidenceList = new ArrayList<Evidence>();
        for (Map item : evidenceMap) {
            @SuppressWarnings("unchecked")
            Map<String, Object> evMap = (Map<String, Object>) item;

            Evidence ev = new Evidence();
            ev.belStatement = getOrEmptyString("bel_statement", evMap).replace("\\\"", "\"");
            ev.summaryText = getOrEmptyString("summary_text", evMap);
            Citation citation = new Citation();
            @SuppressWarnings("unchecked")
            Map<String, Object> citationMap = (Map<String, Object>) evMap.get("citation");
            if (citationMap != null) {
                citation.id = getOrEmptyString("id", citationMap);
                citation.type = getOrEmptyString("type", citationMap);
                citation.name = getOrEmptyString("name", citationMap);
            }
            ev.citation = citation;
            BiologicalContext context = new BiologicalContext();
            @SuppressWarnings("unchecked")
            Map<String, Object> contextMap = (Map<String, Object>) evMap.get("biological_context");
            if (contextMap != null) {
                context.speciesCommonName = getOrEmptyString("species_common_name", contextMap);
                context.ncbiTaxId = getOrZero("ncbi_tax_id", contextMap);
                Set<String> varying = new HashSet<String>(contextMap.keySet());
                varying.removeAll(asList("species_common_name", "ncbi_tax_id"));
                for (String key : varying) {
                    context.variedAnnotations.put(key, contextMap.get(key));
                }
                ev.biologicalContext = context;
            }
            evidenceList.add(ev);
        }

        return evidenceList.toArray(new Evidence[evidenceList.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapToTable(Graph graph, Edge edge, Evidence evidence, CyTable table) {
        if (graph == null) throw new NullPointerException("graph cannot be null");
        if (edge == null) throw new NullPointerException("edge cannot be null");
        if (evidence == null) throw new NullPointerException("evidence cannot be null");
        if (table == null) throw new NullPointerException("table cannot be null");
        if (graph.cyNetwork == null)
            throw new IllegalArgumentException("graph's cyNetwork cannot be null");
        if (edge.cyEdge == null)
            throw new IllegalArgumentException("edge's cyEdge cannot be null");

        CyNetwork cyN = graph.cyNetwork;
        CyEdge cyE = edge.cyEdge;

        CyRow networkRow = cyN.getRow(cyN);
        String networkName = networkRow.get(CyNetwork.NAME, String.class);
        CyRow row = table.getRow(SUIDFactory.getNextSUID());

        row.set(NETWORK_SUID, cyN.getSUID());
        row.set(NETWORK_NAME, networkName);
        row.set(EDGE_SUID, cyE.getSUID());
        row.set(BEL_STATEMENT, evidence.belStatement);
        row.set(SUMMARY_TEXT, evidence.summaryText);

        if (evidence.citation != null) {
            row.set(CITATION_TYPE, evidence.citation.type);
            row.set(CITATION_ID, evidence.citation.id);
            row.set(CITATION_NAME, evidence.citation.name);
        }

        if (evidence.biologicalContext != null) {
            // create any annotation columns that do not already exist
            BiologicalContext bc = evidence.biologicalContext;
            for (String varyingKey : bc.variedAnnotations.keySet()) {
                getOrCreateColumn(varyingKey, String.class, false, table);
            }

            // set annotation values
            row.set(SPECIES, bc.speciesCommonName);
            Map<String, Object> varying = bc.variedAnnotations;
            for (Entry<String, Object> entry : varying.entrySet()) {
                row.set(entry.getKey(), getOrEmptyString(entry.getKey(), varying));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Evidence[] mapFromTable(CyEdge edge, CyTable table) {
        if (edge == null) throw new NullPointerException("edge cannot be null");
        if (table == null) throw new NullPointerException("table cannot be null");

        Set<String> nonAnnotationColumns = new HashSet<String>(
                asList(CyNetwork.SUID, NETWORK_SUID, NETWORK_NAME, EDGE_SUID,
                       BEL_STATEMENT, SUMMARY_TEXT, CITATION_ID, CITATION_NAME,
                       CITATION_TYPE, SPECIES));

        Collection<CyRow> evidenceRows = table.getMatchingRows(EDGE_SUID, edge.getSUID());
        List<Evidence> evidences = new ArrayList<Evidence>(evidenceRows.size());
        if (!evidenceRows.isEmpty()) {
            for (CyRow evRow : evidenceRows) {
                Evidence e = new Evidence();
                e.belStatement = evRow.get(BEL_STATEMENT, String.class);
                e.summaryText = evRow.get(SUMMARY_TEXT, String.class);
                e.citation = new Citation();
                e.citation.id = evRow.get(CITATION_ID, String.class);
                e.citation.name = evRow.get(CITATION_NAME, String.class);
                e.citation.type = evRow.get(CITATION_TYPE, String.class);
                e.biologicalContext = new BiologicalContext();
                e.biologicalContext.speciesCommonName = evRow.get(SPECIES, String.class);
                e.biologicalContext.variedAnnotations = new HashMap<String, Object>();
                for (Entry<String, Object> columnValue : evRow.getAllValues().entrySet()) {
                    if (nonAnnotationColumns.contains(columnValue.getKey()))
                        continue;
                    e.biologicalContext.variedAnnotations.put(columnValue.getKey(), columnValue.getValue());
                }
                evidences.add(e);
            }
        }

        return evidences.toArray(new Evidence[evidences.size()]);
    }
}
