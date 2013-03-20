package com.hellblazer.CoRE.test;

import java.util.HashMap;
import java.util.Map;

import org.dbunit.dataset.CompositeTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredTableMetaData;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.ReplacementTable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUnitSupport {

    /**
     * The columns that DBUnit should use to sort an Attribute table.
     */
    public static final String[]              ATTRIBUTE_SORT_COLUMNS                = { "name" };

    /**
     * The columns that DBUnit should use to sort a Entity Attribute table.
     */
    public static final String[]              ENTITY_ATTRIBUTE_SORT_COLUMNS         = {
            "resource", "entity", "attribute"                                      };
    public static final String[]              ENTITY_NETWORK_ATTRIBUTE_SORT_COLUMNS = {
            "resource", "network_rule", "attribute", "sequence_number"             };
    /**
     * The columns that DBUnit should use to sort a Entity Network table.
     */
    public static final String[]              ENTITY_NETWORK_SORT_COLUMNS           = {
            "resource", "parent", "relationship", "child"                          };
    /**
     * The columns that DBUnit should use to sort a Entity table.
     */
    public static final String[]              ENTITY_SORT_COLUMNS                   = { "name" };
    private static final Logger               LOG                                   = LoggerFactory.getLogger(DBUnitSupport.class);
    /**
     * The String that represents the replacement of SQL NULL in DBUnit
     * datasets.
     */
    public static final String                NULL_STRING                           = "[NULL]";

    /**
     * <p>
     * Maps CoRE ruleform table names (in the form of "schema_name.table_name")
     * to the appropriate column name sorting list.
     * </p>
     * <p>
     * All table names are from the "readable" schema, which contains views that
     * replace all numerical references to Existential entities with their
     * textual names. We do this so we can create more readable comparison data
     * sets, as well as avoid issues where the insertion order of rules by
     * Hibernate is not the same as might be expected based on the order the
     * entities were created in code, due to Hibernate's flushing methods.
     * </p>
     */
    public static final Map<String, String[]> READABLE_VIEW_MAP;

    /**
     * The columns that DBUnit should use to sort a Relationship table.
     */
    public static final String[]              RELATIONSHIP_SORT_COLUMNS             = { "name" };

    /**
     * The columns that DBUnit should use to sort a Resource table.
     */
    public static final String[]              RESOURCE_SORT_COLUMNS                 = { "name" };
    static {
        READABLE_VIEW_MAP = new HashMap<String, String[]>();
        READABLE_VIEW_MAP.put("readable.entity", ENTITY_SORT_COLUMNS);
        READABLE_VIEW_MAP.put("readable.resource", RESOURCE_SORT_COLUMNS);
        READABLE_VIEW_MAP.put("readable.attribute", ATTRIBUTE_SORT_COLUMNS);
        READABLE_VIEW_MAP.put("readable.entity_attribute",
                              ENTITY_ATTRIBUTE_SORT_COLUMNS);
        READABLE_VIEW_MAP.put("readable.entity_network",
                              ENTITY_NETWORK_SORT_COLUMNS);
        READABLE_VIEW_MAP.put("readable.relationship",
                              RELATIONSHIP_SORT_COLUMNS);

        READABLE_VIEW_MAP.put("ruleform.entity_network_attribute",
                              ENTITY_NETWORK_ATTRIBUTE_SORT_COLUMNS);
    }

    /**
     * Return an ITable version of <code>table</code> that has been filtered to
     * exclude the "update_date" and "id" columns.
     * 
     * @param table
     *            the table to be filtered
     * @return a version of <code>table</code> that has been filtered to remove
     *         the "update_date" and "id" columns (and the "file" column if the
     *         table is "ruleform.resource").
     * @throws DataSetException
     *             if there's a problem interacting with the DataSet
     */
    public static ITable filterTable(ITable table) throws DataSetException {
        DefaultColumnFilter columnFilter = new DefaultColumnFilter();
        columnFilter.excludeColumn("update_date");
        columnFilter.excludeColumn("id");

        /*
         * Eventually we might want to set it up so that each table would have
         * specifically-filtered columns. Until then, this should work.
         */
        if ("ruleform.resource".equals(table.getTableMetaData().getTableName())) {
            // We do NOT want to deal with binary data in our expected output files!
            columnFilter.excludeColumn("file");
        }
        ITableMetaData meta = new FilteredTableMetaData(
                                                        table.getTableMetaData(),
                                                        columnFilter);

        return new CompositeTable(meta, table);
    }

    /**
     * Completely processes a table by replacing all nulls, sorting the table,
     * and filtering out columns.
     * 
     * @param table
     *            the table to process
     * @return the table processed by all the table manipulations in this class
     * @throws DataSetException
     */
    public static ITable processTable(ITable table) throws DataSetException {
        table = DBUnitSupport.replaceNulls(table);
        table = DBUnitSupport.sortTable(table);
        table = DBUnitSupport.filterTable(table);
        return table;
    }

    /**
     * Replaces instances of <code>NULL_STRING</code> with a Java
     * <code>null</code>.
     * 
     * @param table
     *            a table containing instances of <code>NULL_STRING</code>
     * @return the contents of <code>table</code> with instances of
     *         <code>NULL_STRING</code> replaced.
     */
    public static ITable replaceNulls(ITable table) {
        ReplacementTable replaced = new ReplacementTable(table);
        replaced.addReplacementObject(NULL_STRING, null);
        return replaced;
    }

    /**
     * Orders the contents of a table. If the name of the table is mapped in
     * <code>READABLE_VIEW_MAP</code>, it will be sorted by the appropriate
     * columns. Otherwise, it is sorted by its own column order.
     * 
     * @param table
     *            the table to be sorted
     * @return a sorted version of <code>table</code>
     * @throws DataSetException
     */
    public static ITable sortTable(ITable table) throws DataSetException {
        SortedTable sorted;

        String tableName = table.getTableMetaData().getTableName();
        if (READABLE_VIEW_MAP.containsKey(tableName)) {
            String[] names = READABLE_VIEW_MAP.get(tableName);
            sorted = new SortedTable(table, names);
        } else {
            LOG.warn(String.format("\"%s\" not found in map; sorting by table's own columns",
                                   tableName));
            sorted = new SortedTable(table);
        }
        return sorted;
    }

}
