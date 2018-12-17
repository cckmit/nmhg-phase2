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

import java.io.InputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import junit.framework.TestCase;

/**
 * @author vineeth.varghese
 * @date May 31, 2007
 */
public class SheetReaderImplTest extends TestCase {

    SheetReaderImpl sheetReaderImpl;

    RowCursor cursor;

    protected void setUp() throws Exception {
        InputStream input = this.getClass().getResourceAsStream("excel-impl-test.xls");
        HSSFSheet sheet = (new HSSFWorkbook(new POIFSFileSystem(input))).getSheetAt(2);
        cursor = new RowCursor(sheet);
        sheetReaderImpl = new SheetReaderImpl();
        sheetReaderImpl.addSheetEndEvaluator(new DummySheetEndEvaluatorImpl());
        sheetReaderImpl.addBlockEndEvaluator(new DummyBlockEndEvaluatorImpl());
    }

    public void testGetBlocks() {
        cursor.setCurrentRowNum(1);
        List<Block> list = sheetReaderImpl.getBlocks(cursor);
        assertEquals(5, list.size());
        assertEquals(1, list.get(0).getStartRow());
        assertEquals(1, list.get(0).getEndRow());
        assertEquals(2, list.get(1).getStartRow());
        assertEquals(3, list.get(1).getEndRow());
        assertEquals(4, list.get(2).getStartRow());
        assertEquals(6, list.get(2).getEndRow());
        assertEquals(7, list.get(3).getStartRow());
        assertEquals(8, list.get(3).getEndRow());
        assertEquals(9, list.get(4).getStartRow());
        assertEquals(9, list.get(4).getEndRow());
    }

}

class DummySheetEndEvaluatorImpl implements SheetEndEvaluator {

    public boolean hasSheetEnded(RowCursor cursor) {
        HSSFCell cell = cursor.getCurrentRow().getCell((short) 0);
        if (Utility.isCellEmpty(cell)) {
            return false;
        } else {
            return "End".equals(cell.getStringCellValue());
        }
    }

}

class DummyBlockEndEvaluatorImpl implements BlockEndEvaluator {

    public boolean hasBlockEnded(RowCursor cursor) {
        HSSFCell cell = cursor.getCurrentRow().getCell((short) 0);
        if (Utility.isCellEmpty(cell)) {
            return false;
        } else {
            return true;
        }
    }

}
