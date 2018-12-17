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

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Converts an Excel file to XML. The Excel file should follow these
 * conventions. Each sheet should start with the header rows as the first few
 * rows. There need to be as many rows as the levels of nesting. For nested
 * element the parent tag will be specified in the first cell of the range of
 * cells under it. There should be a blank row separating the header rows from
 * the data rows. e.g.
 * 
 * |claim                                                       | 
 * |claim-no|serial-number|date-of-repair|OEM-parts-replaced    | 
 * |        |             |              |item-number|quantity  |
 * 
 * Will produce the following XML structure, where the root node is the sheet
 * name
 * 
 * <Claims> 
 *      <claim> 
 *      <claim-no/> 
 *      <serial-number/> 
 *      <date-of-repair/>
 *      <OEM-parts-replaced> 
 *              <item-number/> 
 *              <quantity/> 
 *      </OEM-parts-replaced>
 *      </claim>
 * </Claims>
 * 
 * The first column of each group is assumed to be the key, if it is
 * left null or blank then the record is assumed to be a nested record.
 * 
 * @author kamal.govindraj
 *
 */
@SuppressWarnings("unused")
public class ExcelToXMLConverter {

    private static final Logger logger = Logger.getLogger(ExcelToXMLConverter.class);

    /**
     * 
     * @param input -
     *            excel file confirming to the template
     * @param output -
     *            XML output will be written onto this.
     * @throws IOException
     */
    public void convert(InputStream input, Writer output) throws IOException {
        HSSFWorkbook workbook = open(input);
        Document document = DocumentHelper.createDocument();
        for (int i=0;i < workbook.getNumberOfSheets();i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            String sheetName = workbook.getSheetName(i);
            Element sheetElement = document.addElement(sheetName);
            Header header = extractHeader(sheet);            
            Field sheetContentField = header.getRoot();
            int numRowsInSheet = sheet.getLastRowNum() + 1;
            int lastRowInSheet = numRowsInSheet;
            int firstRowAfterHeader = header.getNumberOfRowsOccupied();
            convertRecords(sheet, sheetContentField, firstRowAfterHeader, lastRowInSheet, sheetElement);
        }
        
        writeDocument(output, document);
    }

    private void writeDocument(Writer output, Document document) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setSuppressDeclaration(false);
        XMLWriter writer = new XMLWriter( output, format );
        writer.write( document );
        writer.flush();
        writer.close();
    }

    private void convertRecords(HSSFSheet currentSheet, Field field, int startAtRow, int lastRow, Element element) throws IOException {
        while (startAtRow < lastRow) {
            int endAtRow = findBeginingOfNextRecord(currentSheet, startAtRow,lastRow, field);
            convertSubRecords(currentSheet, startAtRow, endAtRow, field, element);
            startAtRow = endAtRow;
        }
    }

    private void convertSubRecords(HSSFSheet currentSheet, int startAtRow, int endAtRow, 
            Field field,  Element element)
            throws IOException {
        element = element.addElement(covertToXmlFormat(field.getName()));
        for (Iterator<Field> iter = field.getSubFields().iterator(); iter.hasNext();) {
            Field subField = iter.next();
            if (!subField.getRange().isCompoundField()) {
                Object cellValue = getCellValue(currentSheet, startAtRow, subField.getRange().getStart());
                element.addElement(covertToXmlFormat(subField.getName()))
                        .addText(cellValue != null ? cellValue.toString() : "");
            } else {
                convertRecords(currentSheet, subField, startAtRow, endAtRow, element);
            }
        }
    }

    private String covertToXmlFormat(String name) {
        return StringUtils.replace(name, " ", "_").toLowerCase();
    }

    private Object getCellValue(HSSFSheet currentSheet, int row, short cell) {
        return getValue(currentSheet.getRow(row).getCell(cell));
    }

    private int findBeginingOfNextRecord(HSSFSheet currentSheet, int startAtRow, int lastRow, Field field) {
        int i = startAtRow + 1;
        for (; i < lastRow; i++) {
            if (getValue(currentSheet.getRow(i).getCell(field.getRange().getStart())) != null) {
                break;
            }
        }
        return i;
    }

    Header extractHeader(HSSFSheet sheet) {
        Header header = new Header("Claim", new ColumnRange((short) 0, (short) 255));
        int numRowsInSheet = sheet.getLastRowNum();
        for (int rowIndex = 0; rowIndex < numRowsInSheet; rowIndex++) {
            HSSFRow row = sheet.getRow(rowIndex);
            header.incrementNumberOfRowsOccupied();
            if (isHeaderRow(row)) {
                short numCellsInRow = row.getLastCellNum();
                for (short cellIndex = 0; cellIndex < numCellsInRow;) {
                    HSSFCell cell = row.getCell(cellIndex);
                    short indexOfFirstCellOfNextField = getIndexOfFirstCellOfNextField(row, cellIndex);
                    if (isNotEmpty(cell)) {
                        Object value = getValue(cell);
                        String fieldName = value.toString();
                        short lastCellOfThisField = (short) (indexOfFirstCellOfNextField - 1);
                        ColumnRange cellsAsPartOfThisField = new ColumnRange(cellIndex,lastCellOfThisField);
                        Field fieldDescription = new Field(fieldName, cellsAsPartOfThisField);
                        header.add(fieldDescription);
                    }
                    cellIndex = indexOfFirstCellOfNextField;
                }
            } else {
                //Header processed.
                break;
            }

        }

        return header;
    }

    private boolean isNotEmpty(HSSFCell cell) {
        return cell != null && cell.getCellType() != HSSFCell.CELL_TYPE_BLANK;
    }

    private short getIndexOfFirstCellOfNextField(HSSFRow currentRow, short fromCellIndex) {
        short indexOfNextCell = fromCellIndex;
        short numCellsInRow = currentRow.getLastCellNum();
        while (indexOfNextCell++ < numCellsInRow) {
            HSSFCell nextCell = currentRow.getCell(indexOfNextCell);
            if (isFirstCellOfNextField(nextCell)) {
                break;
            }
        }
        return indexOfNextCell;
    }

    private boolean isFirstCellOfNextField(HSSFCell cell) {
        return cell == null || cell.getCellType() != HSSFCell.CELL_TYPE_BLANK;
    }

    private Object getValue(HSSFCell cell) {
        if (cell == null) {
            return null;
        }
        Object retValue = null;
        switch (cell.getCellType()) {
        case HSSFCell.CELL_TYPE_STRING:
            retValue = cell.getStringCellValue();
            break;
        case HSSFCell.CELL_TYPE_NUMERIC:
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                Date javaDate = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                retValue = dateFormat.format(javaDate);
            } else {
                double numericValue = cell.getNumericCellValue();
                NumberFormat format = NumberFormat.getNumberInstance();
                format.setGroupingUsed(false);
                retValue = format.format(numericValue);
            }
            break;
        case HSSFCell.CELL_TYPE_BOOLEAN:
            retValue = cell.getBooleanCellValue();
            break;
        case HSSFCell.CELL_TYPE_BLANK:
            retValue = null;
            break;
        default:
            throw new RuntimeException("Cell type " + cell.getCellType() + " not currently supported");
        }
        return retValue;
    }

    private boolean isHeaderRow(HSSFRow row) {
        if ( row != null && row.getPhysicalNumberOfCells() != 0) {
            for(short i = 0;i < row.getPhysicalNumberOfCells();i++) {
                if (isNotEmpty(row.getCell(i))) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    HSSFWorkbook open(InputStream input) {
        try {
            return new HSSFWorkbook(input);
        } catch (IOException e) {
            throw new RuntimeException("Error opening workbook", e);
        }
    }
}
