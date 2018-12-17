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
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.NoSuchElementException;

/**
 * @author vineeth.varghese
 * @date May 30, 2007
 */
// TODO: Need to look at this later. Not sure whether we need
// a row cursor. I think we need a Block cursor..a 2 dimension
// space on the the excel.
public class RowCursor implements Cloneable {

    int currentRowNum;

    HSSFSheet sheet;

    String sheetName;

    /**
     *
     */
    public RowCursor(HSSFSheet sheet) {
        this.sheet = sheet;
    }

    public RowCursor(String sheetName, HSSFSheet sheet) {
        this.sheet = sheet;
        this.sheetName = sheetName;
    }

    public int getCurrentRowNum() {
        return currentRowNum;
    }

    public HSSFRow getCurrentRow() {
        return sheet.getRow(currentRowNum);
    }

    public HSSFSheet getSheet() {
        return sheet;
    }

    public void setSheet(HSSFSheet sheet) {
        this.sheet = sheet;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public HSSFRow next() {
        if (hasNext()) {
            return sheet.getRow(currentRowNum++);
        } else {
            throw new NoSuchElementException();
        }
    }

    public boolean hasNext() {
        return (currentRowNum <= sheet.getLastRowNum());
    }

    public void reset() {
        currentRowNum = 0;
    }

    public void setCurrentRowNum(int rowNum) {
        currentRowNum = rowNum;
    }

    public void moveForward() {
        currentRowNum++;
    }

    public void moveBackward() {
        currentRowNum--;
    }

    public RowCursor clone() {
        return new RowCursor(sheetName, sheet);
    }

}
