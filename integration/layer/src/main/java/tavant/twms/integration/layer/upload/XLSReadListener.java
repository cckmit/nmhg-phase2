/*
 *   Copyright (c)2007 Tavant Technologies
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
 *
 */
package tavant.twms.integration.layer.upload;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * An XLS reader based on HSSFListener
 * @author prasad.r
 */
public abstract class XLSReadListener implements HSSFListener {
	private POIFSFileSystem fs;

	private int lastRowNumber;
	private int lastColumnNumber;

    protected int startColumn;
    protected int headerRow;
    protected int rowsToStartConsuming;
    protected int lastColumn;

    
    /** Should we output the formula, or the value it has? */
	private boolean outputFormulaValues = true;

	/** For parsing Formulas */
	private EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener;
	private HSSFWorkbook stubWorkbook;

	// Records we pick up as we process
	private SSTRecord sstRecord;
	private FormatTrackingHSSFListener formatListener;
	
	/** So we known which sheet we're on */
	private int sheetIndex = -1;
	private BoundSheetRecord[] orderedBSRs;
	private ArrayList boundSheetRecords = new ArrayList();

	// For handling formulas with string results
	private int nextRow;
	private int nextColumn;
	private boolean outputNextStringRecord;

	public XLSReadListener(POIFSFileSystem fs, int startColumn, int headerRow,
            int rowsToStartConsuming, int lastColumn) {
		this.fs = fs;
        this.startColumn = startColumn;
        this.headerRow = headerRow;
        this.rowsToStartConsuming = rowsToStartConsuming;
        this.lastColumn = lastColumn;
	}


	/**
	 * Initiates the processing of the XLS 
	 */
	public void process() throws IOException {
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
		formatListener = new FormatTrackingHSSFListener(listener);

		HSSFEventFactory factory = new HSSFEventFactory();
		HSSFRequest request = new HSSFRequest();

		if(outputFormulaValues) {
			request.addListenerForAllRecords(formatListener);
		} else {
			workbookBuildingListener = new EventWorkbookBuilder.SheetRecordCollectingListener(formatListener);
			request.addListenerForAllRecords(workbookBuildingListener);
		}

		factory.processWorkbookEvents(request, fs);
	}

	/**
	 * Main HSSFListener method, processes events
	 */
	public void processRecord(Record record) {
		int thisRow = -1;
		int thisColumn = -1;
        boolean emptyRow = false;
		String thisStr = null;

		switch (record.getSid())
		{
		case BoundSheetRecord.sid:
			boundSheetRecords.add(record);
			break;
		case BOFRecord.sid:
			BOFRecord br = (BOFRecord)record;
			if(br.getType() == BOFRecord.TYPE_WORKSHEET) {
				// Create sub workbook if required
				if(workbookBuildingListener != null && stubWorkbook == null) {
					stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
				}
				
				// Output the worksheet name
				// Works by ordering the BSRs by the location of
				//  their BOFRecords, and then knowing that we
				//  process BOFRecords in byte offset order
				sheetIndex++;
				if(orderedBSRs == null) {
					orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
				}
			}
			break;

		case SSTRecord.sid:
			sstRecord = (SSTRecord) record;
			break;
        case MulBlankRecord.sid:
            // These appear in the middle of the cell records, to
            //  specify that the next bunch are empty but styled
            // Expand this out into multiple blank cells
            MulBlankRecord mbr = (MulBlankRecord)record;
            thisRow = mbr.getRow();
            thisColumn = mbr.getLastColumn();
            emptyRow = (mbr.getNumColumns() == mbr.getLastColumn());
            if(!emptyRow && thisRow >= rowsToStartConsuming){
                for (int i = mbr.getFirstColumn(); i < mbr.getLastColumn(); i++) {
                    setColumnValue(thisRow, i, "");
                }
            }
            break;

		case BlankRecord.sid:
			BlankRecord brec = (BlankRecord) record;

			thisRow = brec.getRow();
			thisColumn = brec.getColumn();
			thisStr = "";
			break;
		case BoolErrRecord.sid:
			BoolErrRecord berec = (BoolErrRecord) record;

			thisRow = berec.getRow();
			thisColumn = berec.getColumn();
			thisStr = "";
			break;

		case FormulaRecord.sid:
			FormulaRecord frec = (FormulaRecord) record;

			thisRow = frec.getRow();
			thisColumn = frec.getColumn();

			if(outputFormulaValues) {
				if(Double.isNaN( frec.getValue() )) {
					// Formula result is a string
					// This is stored in the next record
					outputNextStringRecord = true;
					nextRow = frec.getRow();
					nextColumn = frec.getColumn();
				} else {
					thisStr = formatListener.formatNumberDateCell(frec);
				}
			} else {
				thisStr = HSSFFormulaParser.toFormulaString(stubWorkbook, frec.getParsedExpression());
			}
			break;
		case StringRecord.sid:
			if(outputNextStringRecord) {
				// String for formula
				StringRecord srec = (StringRecord)record;
				thisStr = srec.getString();
				thisRow = nextRow;
				thisColumn = nextColumn;
				outputNextStringRecord = false;
			}
			break;

		case LabelRecord.sid:
			LabelRecord lrec = (LabelRecord) record;

			thisRow = lrec.getRow();
			thisColumn = lrec.getColumn();
			thisStr = lrec.getValue();
			break;
		case LabelSSTRecord.sid:
			LabelSSTRecord lsrec = (LabelSSTRecord) record;

			thisRow = lsrec.getRow();
			thisColumn = lsrec.getColumn();
			if(sstRecord == null) {
				thisStr = "";
			} else {
				thisStr = sstRecord.getString(lsrec.getSSTIndex()).toString();
			}
			break;
		case NumberRecord.sid:
			NumberRecord numrec = (NumberRecord) record;

			thisRow = numrec.getRow();
			thisColumn = numrec.getColumn();

			// Format
			thisStr = formatListener.formatNumberDateCell(numrec);
			break;
		default:
			break;
		}

		// Handle new row
		if(thisRow != -1 && thisRow != lastRowNumber) {
			lastColumnNumber = -1;
		}

		// Handle missing column
		if(record instanceof MissingCellDummyRecord) {
			MissingCellDummyRecord mc = (MissingCellDummyRecord)record;
			thisRow = mc.getRow();
			thisColumn = mc.getColumn();
			thisStr = "";
		}

		// If we got something to print out, do so
		if(thisStr != null) {
            if((thisColumn >= startColumn && thisColumn <= lastColumn) && (thisRow == headerRow || thisRow >= rowsToStartConsuming)){
                if(thisRow == headerRow)
                    setHeader(thisRow, thisColumn, thisStr);
                else
                    setColumnValue( thisRow, thisColumn,thisStr);
            }
		}
		
		// Update column and row count
		if(thisRow > -1)
			lastRowNumber = thisRow;
		if(thisColumn > -1 ){
			lastColumnNumber = thisColumn;
        }

        
        // the ending few rows might be the dummy rows, need to put comma separated vals, untill last column to be read
        // and spit the new line to denote end of record
        if(record instanceof LastCellOfRowDummyRecord){
            if(lastColumn > 0) {
				// Columns are 0 based
                if(lastColumnNumber > -1){
                    for(int i=lastColumnNumber; i<(lastColumn-1); i++) {
                        lastColumnNumber++;
                    }
                }
			}
            // this increment is done to accomdate for this dummy row
            lastColumnNumber++;
			// End the row
            if(lastColumnNumber == lastColumn && (lastRowNumber == headerRow || lastRowNumber >= rowsToStartConsuming)){
                rowReadcompleted(lastRowNumber);
                // We're onto a new row
                lastColumnNumber = -1;
            }
        }
        
        		// Handle end of row
        // if we have read all the columns, then spit new line
        if(lastColumnNumber == lastColumn && (thisRow == headerRow || thisRow >= rowsToStartConsuming)){
            if(!emptyRow)
                rowReadcompleted(lastRowNumber);
            lastColumnNumber = -1;
        }
    }
    
    /**
     * Call back method to set the header values
     * @param rowNum
     * @param columnNum
     * @param thisStr 
     */
    protected abstract void setHeader(int rowNum, int columnNum, String thisStr);

    /**
     * Call back method to set the column values
     * @param rowNum
     * @param columnNum
     * @param thisStr 
     */
    protected abstract void setColumnValue(int rowNum, int columnNum, String thisStr);

    /**
     * Sub classes can override this method if they need to be notified of row read complete event
     */
    protected void rowReadcompleted(int rowNum){
        
    }
    
}
