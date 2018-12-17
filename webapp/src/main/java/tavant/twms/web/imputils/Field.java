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
package tavant.twms.web.imputils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Capture information related to a column in the excel file 
 * and the subfields.
 * @author kamal.govindraj
 *
 */
class Field {
    private String name;

    private ColumnRange range;

    private List<Field> subFields = new ArrayList<Field>();

    public Field(String name, ColumnRange range) {
        this.name = name;
        this.range = range;
    }

    public String getName() {
        return name;
    }

    public ColumnRange getRange() {
        return this.range;
    }

    public void addSubField(Field field) {
        if (!tryAddingToSubfield(field)) {
            subFields.add(field);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean tryAddingToSubfield(Field field) {
        for (Iterator<Field> iter = subFields.iterator(); iter.hasNext();) {
            Field element = iter.next();
            if (element.getRange().contains(field.getRange())) {
                element.addSubField(field);
                return true;
            }
        }
        return false;
    }

    public List<Field> getSubFields() {
        return subFields;
    }

    @Override
    public String toString() {
        return name + (subFields.isEmpty() ? "" : subFields);
    }
}
