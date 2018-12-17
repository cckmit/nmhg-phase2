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
package tavant.twms.web.xls.reader;

import org.apache.poi.hssf.usermodel.HSSFRow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vineeth.varghese
 * @date May 30, 2007
 */
public class Block {

    private RowCursor cursor;
    private int startRow;
    private int endRow;

    public Block(RowCursor cursor, int startRow, int endRow) {
        this.cursor = cursor;
        this.startRow = startRow;
        this.endRow = endRow;
        if (this.startRow > this.endRow) {
            throw new RuntimeException("Start Row cannot be more that the End Row");
        }
    }

    public RowCursor getCursor() {
        return cursor;
    }

    public int getEndRow() {
        return endRow;
    }

    public int getStartRow() {
        return startRow;
    }

    @SuppressWarnings("unchecked")
    public List<Object> getCellsOfColumn(ColumnDescription colDesc) {
        List cellValues = new ArrayList();
        for (int i = startRow; i <= endRow; i++) {
            cursor.setCurrentRowNum(i);
            HSSFRow row = cursor.getCurrentRow();
            if (row != null) {
                cellValues.add(Utility.getCellValue(row.getCell(colDesc.getColumn()), colDesc.getType()));
            } else {
                cellValues.add(null);
            }
        }
        return cellValues;
    }
}
