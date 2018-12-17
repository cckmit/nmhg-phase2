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

/**
 * Capture information obtained by parsing the header rows of
 * the input excel file. This is used to drive processing the data
 * records and writing them out to XML
 * @author kamal.govindraj
 *
 */
class Header {
    Field root;

    int numberOfRowsOccupied = 0;

    public Header(String name, ColumnRange range) {
        root = new Field(name, range);
    }

    public Field getRoot() {
        return root;
    }

    public void add(Field field) {
        root.addSubField(field);
    }

    public void incrementNumberOfRowsOccupied() {
        numberOfRowsOccupied++;
    }

    public int getNumberOfRowsOccupied() {
        return numberOfRowsOccupied;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}

