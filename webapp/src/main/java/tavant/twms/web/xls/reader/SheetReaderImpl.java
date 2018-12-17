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

import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vineeth.varghese
 * @date May 30, 2007
 */
public class SheetReaderImpl implements SheetReader {

    String sheetName;

    BlockReader blockReader;

    SheetEndEvaluator sheetEndEvaluator;

    BlockEndEvaluator blockEndEvaluator;

    int startRow;

    Class type;

    public List<ConversionResult> read(HSSFSheet sheet) {
        List<ConversionResult> results = new ArrayList<ConversionResult>();
        RowCursor cursor = new RowCursor(sheetName, sheet);
        cursor.setCurrentRowNum(startRow);
        List<Block> blocks = getBlocks(cursor);
        for (Block block : blocks) {
            results.add(blockReader.read(block, getObject()));
        }
        return results;
    }

    List<Block> getBlocks(RowCursor cursor) {
        List<Block> blocks = new ArrayList<Block>();
        while (!(sheetEndEvaluator.hasSheetEnded(cursor))) {
            int blockStartRow = cursor.getCurrentRowNum();
            cursor.moveForward();
            while (!(sheetEndEvaluator.hasSheetEnded(cursor))
                    && !(blockEndEvaluator.hasBlockEnded(cursor))) {
                cursor.moveForward();
            }
            int blockEndRow = cursor.getCurrentRowNum() - 1;
            blocks.add(new Block(cursor.clone(), blockStartRow, blockEndRow));
        }
        return blocks;
    }

    Object getObject() {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This implementation supports only one {@link BlockReader}.
     */
    public void addBlockReader(BlockReader blockReader) {
        this.blockReader = blockReader;
    }

    public void addSheetEndEvaluator(SheetEndEvaluator sheetEndEvaluator) {
        this.sheetEndEvaluator = sheetEndEvaluator;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public void addBlockEndEvaluator(BlockEndEvaluator blockEndEvaluator) {
        this.blockEndEvaluator = blockEndEvaluator;
    }

    BlockEndEvaluator getBlockEndEvaluator() {
        return blockEndEvaluator;
    }

    BlockReader getBlockReader() {
        return blockReader;
    }

    SheetEndEvaluator getSheetEndEvaluator() {
        return sheetEndEvaluator;
    }

    int getStartRow() {
        return startRow;
    }

    Class getType() {
        return type;
    }
}
