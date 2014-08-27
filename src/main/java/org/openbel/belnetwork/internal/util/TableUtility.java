package org.openbel.belnetwork.internal.util;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;

/**
 * {@link TableUtility} provides convenience methods for working with Cytoscape
 * table objects.
 */
public class TableUtility {

    /**
     * Returns a {@link CyColumn}, referenced by {@code name}, contained in the
     * {@code table}.
     *
     * <br><br>
     * If the {@link CyColumn}
     * does not exist it is created in {@code table} and returned.
     *
     * @param name the column name; cannot be {@code null}
     * @param type the column type; cannot be {@code null}
     * @param immutable the column immutability flag
     * @param table the table where this column exists or is created;
     * cannot be {@code null}
     * @return the {@link CyColumn column} contained in the {@link CyTable table}
     * @throws java.lang.NullPointerException when {@code name}, {@code type}, or
     * {@code table} is {@code null}
     */
    public static CyColumn getOrCreateColumn(String name, Class<?> type, boolean immutable, CyTable table) {
        if (name == null) throw new NullPointerException("name cannot be null");
        if (type == null) throw new NullPointerException("type cannot be null");
        if (table == null) throw new NullPointerException("table cannot be null");

        CyColumn column = table.getColumn(name);
        if (column == null) {
            table.createColumn(name, type, immutable);
            column = table.getColumn(name);
        }
        return column;
    }

    private TableUtility() {
        //static accessors only
    }
}