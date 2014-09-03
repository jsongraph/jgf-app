package info.json_graph_format.jgfapp.api.util;

import org.cytoscape.model.*;

import java.util.Set;

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

    /**
     * Returns the {@link CyTable table} associated with {@code name} or {@code null}
     * if it does not exist.
     *
     * @param name the {@link String name}; cannot be {@code null}
     * @param tableMgr the {@link CyTableManager} Cytoscape service;
     * cannot be {@code null}
     * @return the {@link CyTable} or {@code null} if it does not exist
     * @throws java.lang.NullPointerException if {@code name} or {@code tableMgr} is
     * {@code null}
     */
    public static CyTable getTable(String name, CyTableManager tableMgr) {
        if (name == null) throw new NullPointerException("name cannot be null");
        if (tableMgr == null) throw new NullPointerException("tableMgr cannot be null");

        // find table by name and return it if exists...
        Set<CyTable> allTables = tableMgr.getAllTables(true);
        for (CyTable table : allTables) {
            if (table.getTitle().equals(name)) {
                return table;
            }
        }
        return null;
    }

    private TableUtility() {
        //static accessors only
    }
}
