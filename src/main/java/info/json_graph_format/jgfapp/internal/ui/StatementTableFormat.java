package info.json_graph_format.jgfapp.internal.ui;

import ca.odell.glazedlists.gui.TableFormat;
import info.json_graph_format.jgfapp.api.model.Evidence;
import info.json_graph_format.jgfapp.api.util.Pair;

/**
 * {@link StatementTableFormat} defines the data for the statement table in the
 * {@link EvidencePanel}.
 */
class StatementTableFormat implements TableFormat<Pair<String, Evidence>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(int i) {
        return "BEL Statement";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getColumnValue(Pair<String, Evidence> obj, int i) {
        return obj.first();
    }
}
