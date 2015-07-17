package info.json_graph_format.jgfapp.internal;

import info.json_graph_format.jgfapp.api.BELEvidenceMapper;
import info.json_graph_format.jgfapp.api.model.Citation;
import info.json_graph_format.jgfapp.api.model.Edge;
import info.json_graph_format.jgfapp.api.model.Evidence;
import info.json_graph_format.jgfapp.api.model.Graph;
import org.cytoscape.model.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static info.json_graph_format.jgfapp.api.util.FormatUtility.getOrEmptyString;
import static info.json_graph_format.jgfapp.api.util.TableUtility.getOrCreateColumn;
import static info.json_graph_format.jgfapp.api.util.Utility.typedList;
import static info.json_graph_format.jgfapp.internal.Constants.*;

/**
 * {@link BELEvidenceMapperImpl} implements a {@link BELEvidenceMapper} to
 * map between {@link Evidence} and {@link CyTable} objects.
 */
public class BELEvidenceMapperImpl implements BELEvidenceMapper {

    /**
     * {@inheritDoc}
     */
    @Override
    public Evidence[] mapEdgeToEvidence(Edge edge) {
        if (edge == null) throw new NullPointerException("edge cannot be null");

        if (edge.metadata == null) return new Evidence[0];
        Object evidences = edge.metadata.get("evidences");
        if (evidences == null || !(evidences instanceof List))
            return new Evidence[0];
        List<Map> evidenceMap = typedList(((List) evidences), Map.class);

        List<Evidence> evidenceList = new ArrayList<>();
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

            Map<String, Object> context = new HashMap<>();
            @SuppressWarnings("unchecked")
            Map<String, Object> contextMap = (Map<String, Object>) evMap.get("experiment_context");
            if (contextMap != null) {
                context.putAll(contextMap);
            }
            ev.experimentContext = context;

            Map<String, Object> metadata = new HashMap<>();
            @SuppressWarnings("unchecked")
            Map<String, Object> metadataMap = (Map<String, Object>) evMap.get("metadata");
            if (metadataMap != null) {
                metadata.putAll(metadataMap);
            }
            ev.metadata = metadata;

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

        mapToTable(null, graph.cyNetwork, edge.cyEdge, evidence, table);
    }

    public void mapToTable(Long suid, CyNetwork cyN, CyEdge cyE, Evidence evidence, CyTable table) {
        if (cyN == null) throw new NullPointerException("CyN cannot be null");
        if (cyE == null) throw new NullPointerException("edge cannot be null");
        if (evidence == null) throw new NullPointerException("evidence cannot be null");
        if (table == null) throw new NullPointerException("table cannot be null");

        Long evidenceSUID = Optional.ofNullable(suid).orElse(SUIDFactory.getNextSUID());

        CyRow networkRow = cyN.getRow(cyN);
        String networkName = networkRow.get(CyNetwork.NAME, String.class);
        CyRow row = table.getRow(evidenceSUID);

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

        // unset dynamic columns (e.g. experiment context and metadata columns)
        table.getColumns().
                stream().
                filter(dynamicEvidenceColumns()).
                map(CyColumn::getName).
                forEach(s -> row.set(s, null));

        if (evidence.experimentContext != null) {
            Map<String, Object> reIndexed = evidence.experimentContext.
                    entrySet().
                    stream().
                    collect(
                            Collectors.toMap(
                                    entry -> "experiment_context_" + entry.getKey(),
                                    Entry::getValue
                            )
                    );

            // create any experiment context columns that do not already exist
            for (String key : reIndexed.keySet()) {
                getOrCreateColumn(key, String.class, false, table);
            }

            // set experiment context values
            for (Entry<String, Object> entry : reIndexed.entrySet()) {
                row.set(entry.getKey(), getOrEmptyString(entry.getKey(), reIndexed));
            }
        }

        if (evidence.metadata != null) {
            Map<String, Object> reIndexed = evidence.metadata.
                    entrySet().
                    stream().
                    collect(
                            Collectors.toMap(
                                    entry -> "metadata_" + entry.getKey(),
                                    Entry::getValue
                            )
                    );

            // create any metadata columns that do not already exist
            for (String key : reIndexed.keySet()) {
                getOrCreateColumn(key, String.class, false, table);
            }

            // set metadata values
            for (Entry<String, Object> entry : reIndexed.entrySet()) {
                row.set(entry.getKey(), getOrEmptyString(entry.getKey(), reIndexed));
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

        Collection<CyRow> evidenceRows = table.getMatchingRows(EDGE_SUID, edge.getSUID());
        List<Evidence> evidences = new ArrayList<>(evidenceRows.size());
        if (!evidenceRows.isEmpty()) {
            for (CyRow evRow : evidenceRows) {
                Evidence e = new Evidence();
                e.evidenceId = evRow.get(CyIdentifiable.SUID, Long.class);
                e.belStatement = evRow.get(BEL_STATEMENT, String.class);
                e.summaryText = evRow.get(SUMMARY_TEXT, String.class);
                e.citation = new Citation();
                e.citation.id = evRow.get(CITATION_ID, String.class);
                e.citation.name = evRow.get(CITATION_NAME, String.class);
                e.citation.type = evRow.get(CITATION_TYPE, String.class);

                e.experimentContext = new HashMap<>();
                evRow.getAllValues().entrySet().stream().filter(experimentContextEntries()).forEach(entry ->
                        e.experimentContext.put(entry.getKey(), entry.getValue()));

                e.metadata = new HashMap<>();
                evRow.getAllValues().entrySet().stream().filter(metadataEntries()).forEach(entry ->
                        e.metadata.put(entry.getKey(), entry.getValue()));

                evidences.add(e);
            }
        }

        return evidences.toArray(new Evidence[evidences.size()]);
    }
}
