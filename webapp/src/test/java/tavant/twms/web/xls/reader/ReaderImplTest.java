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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import junit.framework.TestCase;

/**
 * @author vineeth.varghese
 * @date May 30, 2007
 */
public class ReaderImplTest extends TestCase {

    Reader reader;

    protected void setUp() throws Exception {
        reader = new ReaderImpl();
        SheetReader sheetReader1 = new DummySheetReaderImpl("Test Sheet1");
        SheetReader sheetReader2 = new DummySheetReaderImpl("Test Sheet3");
        reader.addSheetReader(sheetReader1);
        reader.addSheetReader(sheetReader2);
    }

    public void testRead() {
        InputStream input = this.getClass().getResourceAsStream("excel-impl-test.xls");
        Map<String, List<ConversionResult>> result = reader.read(input);
        assertEquals(2, result.size());
        List<ConversionResult> convResults = result.get("Test Sheet1");
        assertEquals("Test Sheet1", convResults.get(0).getResult());
        convResults = result.get("Test Sheet3");
        assertEquals("Test Sheet3", convResults.get(0).getResult());
    }

}

class DummySheetReaderImpl implements SheetReader {

    private String sheetName;

    public DummySheetReaderImpl(String sheetName) {
        this.sheetName = sheetName;
    }

    public void addBlockReader(BlockReader blockReader) {
    }

    public void addSheetEndEvaluator(SheetEndEvaluator sheetEndEvaluator) {
    }

    public String getSheetName() {
        return sheetName;
    }

    public List<ConversionResult> read(HSSFSheet sheet) {
        List<ConversionResult> result = new ArrayList<ConversionResult>();
        result.add(new ConversionResult(getSheetName()));
        return result;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public void addBlockEndEvaluator(BlockEndEvaluator blockEndEvaluator) {
    }

}
