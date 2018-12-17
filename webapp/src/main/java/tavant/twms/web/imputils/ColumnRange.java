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
 * 
 * @author kamal.govindraj
 *
 */
class ColumnRange implements Comparable<ColumnRange> {
    private short start;

    private short end;

    public ColumnRange(short start, short end) {
        this.start = start;
        this.end = end;
    }

    public ColumnRange(short start) {
        this.start = start;
        this.end = start;
    }

    public boolean isCompoundField() {
        return end - start > 0;
    }

    public short getEnd() {
        return end;
    }

    public short getStart() {
        return start;
    }

    @Override
    public String toString() {
        return "[" + start + ":" + end + "]";
    }

    public int compareTo(ColumnRange o) {
        return start - o.start;
    }

    public boolean contains(ColumnRange other) {
        return start <= other.start && end >= other.end && !(start == other.start && end == other.end);
    }

}
