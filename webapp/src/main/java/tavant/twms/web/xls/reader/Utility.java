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

import com.domainlanguage.time.CalendarDate;
import org.apache.commons.digester.Digester;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author vineeth.varghese
 * @date May 31, 2007
 */
public class Utility {

    public static boolean isCellEmpty(HSSFCell cell) {
        if (cell == null) {
            return true;
        } else {
            switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_BLANK:
                return true;
            case HSSFCell.CELL_TYPE_STRING:
                String cellValue = cell.getStringCellValue();
                return cellValue == null || cellValue.length() == 0
                        || cellValue.trim().length() == 0;
            default:
                return false;
            }
        }
    }

    public static Object getCellValue(HSSFCell cell, Class type) {
        if (cell == null) {
            return null;
        }
        Object value = null;
        if (Date.class.isAssignableFrom(type)){
            value = cell.getDateCellValue();
        } else if (CalendarDate.class.isAssignableFrom(type)) {
            Date date = cell.getDateCellValue();
            if (date == null) {
                return null;
            }
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);
            value = CalendarDate.from(year, month, day);
        } else if (String.class.isAssignableFrom(type)) {
            if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                return getStringValueOf(cell.getNumericCellValue());
            } else {
                value = cell.getStringCellValue();
            }
        } else if (Long.class.isAssignableFrom(type)) {
            value = Long.valueOf(getStringValueOf(cell.getNumericCellValue()));
        } else if (Integer.class.isAssignableFrom(type)) {
            value = Integer.valueOf(getStringValueOf(cell.getNumericCellValue()));
        } else if (Boolean.class.isAssignableFrom(type)) {
            value = cell.getBooleanCellValue();
        } else if (BigDecimal.class.isAssignableFrom(type)) {
            value = Integer.valueOf(getStringValueOf(cell.getNumericCellValue()));
        } else {
            if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                return getStringValueOf(cell.getNumericCellValue());
            } else if (HSSFCell.CELL_TYPE_STRING == cell.getCellType()) {
                value = cell.getStringCellValue();
            }
        }
        return value;
    }

    static String getStringValueOf(Double num) {
        NumberFormat format = NumberFormat.getIntegerInstance();
        String value;
        if (num == null) {
            return "";
        } else {
            try {
                value = format.parse(format.format(num)).toString().trim();
            } catch (ParseException e) {
                //Fix later!!!
                throw new RuntimeException(e);
            }
        }
        return value;
    }

    public static Reader getReaderFromXML(InputStream input) throws IOException, SAXException {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("excel", ReaderImpl.class);
        digester.addSetProperties("excel");
        digester.addObjectCreate("excel/sheet", SheetReaderImpl.class);
        digester.addSetProperties("excel/sheet");
        digester.addSetNext("excel/sheet", "addSheetReader");
        digester.addObjectCreate("excel/sheet/block", BlockReaderImpl.class);
        digester.addSetProperties("excel/sheet/block");
        digester.addSetNext("excel/sheet/block", "addBlockReader");
        digester.addObjectCreate("excel/sheet/block-break", NonEmptyCellEndsBlockEvaluatorImpl.class);
        digester.addSetProperties("excel/sheet/block-break");
        digester.addSetNext("excel/sheet/block-break", "addBlockEndEvaluator");
        digester.addObjectCreate("excel/sheet/sheet-end", EmptyRowEndsSheetEvaluatorImpl.class);
        digester.addSetProperties("excel/sheet/sheet-end");
        digester.addSetNext("excel/sheet/sheet-end", "addSheetEndEvaluator");
        digester.addObjectCreate("excel/sheet/block/column-mapping", BeanColumnMapping.class);
        digester.addSetProperties("excel/sheet/block/column-mapping");
        digester.addSetNext("excel/sheet/block/column-mapping", "addBeanColumnMapping");
        return (Reader)digester.parse(input);
    }
}