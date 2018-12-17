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

import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 * @author vineeth.varghese
 * @date Jun 5, 2007
 */
public class NonEmptyCellEndsBlockEvaluatorImpl implements BlockEndEvaluator {

    private int column;

    /**
     * Expects the cursor to give a <b>Non Null</b> row
     */
    public boolean hasBlockEnded(RowCursor cursor) {
        HSSFCell cell = cursor.getCurrentRow().getCell((short)column);
        if (Utility.isCellEmpty(cell)) {
            return false;
        }
        return true;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
