/**
 * Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.xls.writer;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author kaustubhshobhan.b This class takes the list and then writes the contents passed to it
 *         into the excel sheet and returns the Workbook
 * 
 */
public class XLSWriterImpl implements XLSWriter {

    private static final Logger logger = Logger.getLogger(XLSWriterImpl.class);

    public void write(List<List<XLSCell>> tableData, OutputStream outputStream) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        // Assuming that only one sheet is needed :)
        HSSFSheet sheet = workbook.createSheet();
        populateSheetWithData(sheet, tableData);
        workbook.write(outputStream);
    }

    void populateSheetWithData(HSSFSheet sheet, List<List<XLSCell>> tableData) {
        int rowCount = 0;
        for (List<XLSCell> rowData : tableData) {
            HSSFRow row = sheet.createRow(rowCount);
            populateRowWithData(row, rowData);
            ++rowCount;
        }
    }

    void populateRowWithData(HSSFRow row, List<XLSCell> rowData) {
        int cellCount = 0;
        for (XLSCell cell : rowData) {
            cell.writeToCell(row.createCell((short) cellCount));
            ++cellCount;
        }
    }
}
