package info.json_graph_format.jgfapp.api.util;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Predicates.*;
import static info.json_graph_format.jgfapp.api.util.Utility.noItems;
import static java.lang.String.format;

/**
 * {@link TableUtility} provides convenience methods for working with Cytoscape
 * table objects.
 */
public class TableUtility {

    private static final Collection<Class<?>> SUPPORTED_TYPES;

    static {
        SUPPORTED_TYPES = new ArrayList<>();
        SUPPORTED_TYPES.add(Boolean.class);
        SUPPORTED_TYPES.add(Double.class);
        SUPPORTED_TYPES.add(Float.class);
        SUPPORTED_TYPES.add(Integer.class);
        SUPPORTED_TYPES.add(String.class);
    }

    public static CyColumn getOrCreateColumnByPrototypes(String name, Collection<Object> prototypes,
                                                         boolean immutable, CyTable table) {
        if (name == null) throw new NullPointerException("name cannot be null");
        if (table == null) throw new NullPointerException("table cannot be null");

        Class<?> type;
        if (noItems(prototypes)) {
            type = String.class;
        } else {
            // map prototypes to classes and filter for non-null objects...
            Iterable<Class<?>> types = FluentIterable.from(prototypes).
                    transform(new Function<Object, Class<?>>() {
                        @Nullable
                        public Class<?> apply(@Nullable Object o) {
                            return (o == null) ? null : o.getClass();
                        }
                    }).filter(and(notNull(), not(assignableFrom(Map.class))));

            if (Iterables.all(types, assignableFrom(Collection.class))) {
                Collection<Collection<Object>> all = new ArrayList<>();
                for (Object o : prototypes) {
                    all.add((Collection<Object>) o);
                }
                return getOrCreateListColumnByPrototypes(name, all, false, table);
            }

            // ...reduce to representative type for all prototypes
            Iterator<Class<?>> it = types.iterator();
            if (!it.hasNext()) return null;

            type = it.next();
            while (it.hasNext()) {
                Class<?> nextType = it.next();
                // skip; no further work needed
                if (type == nextType) continue;

                // skip; already chose higher precision data type
                if (type == Double.class && nextType == Integer.class)
                    continue;

                // choose higher precision data type
                if (type == Integer.class && nextType == Double.class) {
                    type = nextType;
                    // incompatible types; resort to loose String type
                } else {
                    type = String.class;
                }
            }
        }

        return getOrCreateColumn(name, type, immutable, table);
    }

    public static CyColumn getOrCreateColumnByPrototype(String name, Object prototype,
                                                        boolean immutable, CyTable table) {
        if (name == null) throw new NullPointerException("name cannot be null");
        if (table == null) throw new NullPointerException("table cannot be null");

        Class<?> type = (prototype == null) ? String.class : prototype.getClass();
        if (!SUPPORTED_TYPES.contains(type)) {
            type = String.class;
        }

        return getOrCreateColumn(name, type, immutable, table);
    }

    /**
     * Returns a {@link CyColumn}, referenced by {@code name}, contained in the
     * {@code table}.
     * <p>
     * <br><br>
     * If the {@link CyColumn}
     * does not exist it is created in {@code table} and returned.
     *
     * @param name      the column name; cannot be {@code null}
     * @param type      the column type; cannot be {@code null}
     * @param immutable the column immutability flag
     * @param table     the table where this column exists or is created;
     *                  cannot be {@code null}
     * @return the {@link CyColumn column} contained in the {@link CyTable table}
     * @throws java.lang.NullPointerException when {@code name}, {@code type}, or
     *                                        {@code table} is {@code null}
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

    public static CyColumn getOrCreateListColumn(String name, Class<?> type, boolean immutable, CyTable table) {
        if (name == null) throw new NullPointerException("name cannot be null");
        if (type == null) throw new NullPointerException("type cannot be null");
        if (table == null) throw new NullPointerException("table cannot be null");

        CyColumn column = table.getColumn(name);
        if (column == null) {
            table.createListColumn(name, type, immutable);
            column = table.getColumn(name);
        }
        return column;
    }

    public static void setColumnValue(String name, Object value, CyRow row, CyColumn column) {
        if (name == null) throw new NullPointerException("name cannot be null");
        if (row == null) throw new NullPointerException("row cannot be null");

        // Cannot set value to null column
        if (column == null)
            return;

        // set name to value when value is null or fits the column type
        Class<?> type = column.getType();
        if (value == null || type.isAssignableFrom(value.getClass())) {
            row.set(name, value);
            return;
        }

        if (type == Double.class) {
            // type convert Integer to Double for more precision
            if (value instanceof Double)
                row.set(name, value);
            else if (value instanceof Integer)
                row.set(name, ((Integer) value).doubleValue());
            else if (value instanceof Float)
                row.set(name, ((Float) value).doubleValue());
            else if (value instanceof String) {
                try {
                    Double doubleValue = Double.valueOf((String) value);
                    row.set(name, doubleValue);
                } catch (NumberFormatException e) {
                    // string value does not represent a double
                    String fmt = "value of type %s cannot be converted to %s";
                    throw new IllegalArgumentException(
                            format(fmt, value.getClass(), type.getClass()), e);
                }
            } else {
                String fmt = "value of type %s cannot be converted to %s";
                throw new IllegalArgumentException(
                        format(fmt, value.getClass(), type.getClass()));
            }
        }
    }

    /**
     * Returns the {@link CyTable table} associated with {@code name} or {@code null}
     * if it does not exist.
     *
     * @param name     the {@link String name}; cannot be {@code null}
     * @param tableMgr the {@link CyTableManager} Cytoscape service;
     *                 cannot be {@code null}
     * @return the {@link CyTable} or {@code null} if it does not exist
     * @throws java.lang.NullPointerException if {@code name} or {@code tableMgr} is
     *                                        {@code null}
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

    private static CyColumn getOrCreateListColumnByPrototypes(
            String name, Collection<Collection<Object>> prototypes,
            boolean immutable, CyTable table) {
        if (name == null) throw new NullPointerException("name cannot be null");
        if (table == null) throw new NullPointerException("table cannot be null");

        Class<?> type;
        if (noItems(prototypes)) {
            type = String.class;
        } else {
            // flatten collections, map prototypes to classes, and filter for non-null objects...
            Iterator<Class<?>> classIterator = prototypes.
                    stream().
                    filter(Objects::nonNull).
                    flatMap(Collection::stream).
                    map(Object::getClass).
                    filter(Objects::nonNull).
                    filter(((Predicate<Class<?>>) Map.class::isAssignableFrom).negate()).
                    collect(Collectors.toList()).
                    iterator();

            // ...reduce to representative type for all prototypes
            if (!classIterator.hasNext()) return null;

            type = classIterator.next();
            while (classIterator.hasNext()) {
                Class<?> nextType = classIterator.next();
                // skip; no further work needed
                if (type == nextType) continue;

                // skip; already chose higher precision data type
                if (type == Double.class && nextType == Integer.class)
                    continue;

                // choose higher precision data type
                if (type == Integer.class && nextType == Double.class) {
                    type = nextType;
                    // incompatible types; resort to loose String type
                } else {
                    type = String.class;
                }
            }
        }

        return getOrCreateListColumn(name, type, immutable, table);
    }

    private TableUtility() {
        //static accessors only
    }
}
