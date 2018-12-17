/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.infra;

import org.hibernate.AssertionFailure;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.util.StringHelper;

/**
 * This is the ImprovedNamingStragey which is also *component safe*. It just
 * doesnt call the method StringHelper.unqualify :)
 * 
 * @author kannan.ekanath
 * 
 */
public class ComponentSafeNamingStrategy implements NamingStrategy {

    /**
     * Return the unqualified class name, mixed case converted to underscores
     */
    public String classToTableName(String className) {
        return addUnderscores(StringHelper.unqualify(className));
    }

    /**
     * Return the full property path with underscore seperators, mixed case
     * converted to underscores
     */
    public String propertyToColumnName(String propertyName) {
        return addUnderscores(propertyName);
    }

    /**
     * Convert mixed case to underscores
     */
    public String tableName(String tableName) {
        return addUnderscores(tableName);
    }

    /**
     * Convert mixed case to underscores
     */
    public String columnName(String columnName) {
        return addUnderscores(columnName);
    }

    protected static String addUnderscores(String name) {
        StringBuffer buf = new StringBuffer(name.replace('.', '_'));
        for (int i = 1; i < buf.length() - 1; i++) {
            if (Character.isLowerCase(buf.charAt(i - 1)) && Character.isUpperCase(buf.charAt(i))
                    && Character.isLowerCase(buf.charAt(i + 1))) {
                buf.insert(i++, '_');
            }
        }
        return buf.toString().toLowerCase();
    }

    public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity,
            String associatedEntityTable, String propertyName) {
        return tableName(ownerEntityTable + '_' + propertyToColumnName(propertyName));
    }

    /**
     * Return the argument
     */
    public String joinKeyColumnName(String joinedColumn, String joinedTable) {
        return columnName(joinedColumn);
    }

    /**
     * Return the property name or propertyTableName
     */
    public String foreignKeyColumnName(String propertyName, String propertyEntityName,
            String propertyTableName, String referencedColumnName) {
        String header = propertyName != null ? propertyName : propertyTableName;
        if (header == null)
            throw new AssertionFailure("NamingStrategy not properly filled");
        return columnName(header); // + "_" + referencedColumnName not used for
                                    // backward compatibility
    }

    /**
     * Return the column name or the unqualified property name
     */
    public String logicalColumnName(String columnName, String propertyName) {
        return StringHelper.isNotEmpty(columnName) ? columnName : propertyName;
    }

    /**
     * Returns either the table name if explicit or if there is an associated
     * table, the concatenation of owner entity table and associated table
     * otherwise the concatenation of owner entity table and the unqualified
     * property name
     */
    public String logicalCollectionTableName(String tableName, String ownerEntityTable,
            String associatedEntityTable, String propertyName) {
        if (tableName != null) {
            return tableName;
        } else {
            // use of a stringbuffer to workaround a JDK bug
            return new StringBuffer(ownerEntityTable).append("_").append(
                    associatedEntityTable != null ? associatedEntityTable : StringHelper
                            .unqualify(propertyName)).toString();
        }
    }

    /**
     * Return the column name if explicit or the concatenation of the property
     * name and the referenced column
     */
    public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
        return StringHelper.isNotEmpty(columnName) ? columnName : StringHelper.unqualify(propertyName) + "_"
                + referencedColumn;
    }

}
