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
 */

package tavant.twms.domain.upload.errormgt;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.I18NUploadErrorText;
import tavant.twms.domain.upload.controller.ReceivedFileDetails;
import tavant.twms.domain.upload.controller.UploadError;
import tavant.twms.domain.upload.controller.UploadManagement;

/**
 * @author jhulfikar.ali
 *
 */
public abstract class ErrorReportGenerator {

	private static Logger logger = Logger.getLogger(ErrorReportGenerator.class.getName());

	@SuppressWarnings("unchecked")
	public void generateErrorReport(Connection conn, OutputStream out,
			ReceivedFileDetails data, UploadManagement dataUpload) 
		throws SQLException, IOException {
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(" SELECT id,error_code FROM " +
					dataUpload.getStagingTable() +
					" WHERE NVL(error_status, '-') = 'N' or NVL(upload_status, 'N')='N' ");
						
			int totalCols = dataUpload.getColumnsToCapture()!=null?
					dataUpload.getColumnsToCapture().intValue():0;
			Long rowsToStartConsuming = dataUpload.getConsumeRowsFrom();
			int headerRowToConsume = dataUpload.getHeaderRowToCapture().intValue();
			HSSFWorkbook wb = new HSSFWorkbook(data.getFileContents());
			HSSFSheet inSheet = wb.getSheetAt(0);
			int rowIndex = rowsToStartConsuming.intValue();
			int rowId = 2;
			int totalRows = inSheet.getLastRowNum();
			HashMap<Integer, String> errorIds = new HashMap<Integer, String>();
			
			while(rs.next()) {
				int tempKey = rs.getInt(1);
				String tempValue = rs.getString(2);
				errorIds.put(tempKey, getTempValue(tempKey,tempValue));
			}
			if(errorIds.isEmpty())
				return;
			
            HSSFCellStyle errorStyle = wb.createCellStyle();
            errorStyle.setFillForegroundColor(HSSFColor.RED.index);
            errorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            errorStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            errorStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            errorStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            errorStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

			HashMap<String, Integer> fieldIndexMap = new HashMap<String, Integer>();
			HSSFRow headerRow = (HSSFRow) inSheet.getRow(headerRowToConsume);
			for(int i=1; i<totalCols ; i++)
				fieldIndexMap.put(headerRow.getCell(i).getRichStringCellValue().toString().toLowerCase(), i);
			
			HSSFCell errCodeCell = (HSSFCell) headerRow.getCell((totalCols));
			if(errCodeCell == null)
				errCodeCell = headerRow.createCell( ((totalCols)) );
			errCodeCell.setCellValue(new HSSFRichTextString("Error_Code"));
			
			while(rowIndex <= totalRows) {
				HSSFRow inRow = (HSSFRow) inSheet.getRow(rowIndex);
				String errorMsg = errorIds.get(rowId);				
				for(int i=1; i<totalCols; i++) {
					HSSFCell cell = (HSSFCell) inRow.getCell(i);
					if(cell == null)
						continue;
					HSSFCellStyle cellStyle = cell.getCellStyle();					
					cellStyle.setFillForegroundColor((short)9);
					cellStyle.setFillBackgroundColor((short)65);
					cellStyle.setFillPattern((short)1);
				}
				if(errorMsg == null) {
					//inSheet.removeRow(inRow);
						if (rowIndex != totalRows)
							inSheet.shiftRows(rowIndex + 1, totalRows, -1);
						else
							inSheet.shiftRows(rowIndex + 1, rowIndex + 1, -1);
					totalRows--;
					//rowIndex ++;
				}else {
					List<UploadError> uploadErrors = getUploadErrorsForUploadMgt(conn, dataUpload.getId());
					HashMap<String, String> fieldErrorMap = createFieldErrorMap(errorMsg,data.getUploaderLocale(),uploadErrors);
					StringBuilder errString = new StringBuilder();
					for(String field : fieldErrorMap.keySet()) {
						Integer fieldIdx = fieldIndexMap.get(field.toLowerCase());
						if(fieldIdx != null) {
							HSSFCell inCell = (HSSFCell) inRow.getCell((fieldIdx.intValue()));
							if(inCell == null)
								inCell = inRow.createCell( (fieldIdx.intValue()) );
							inCell.setCellStyle(errorStyle);
						}
						String error = fieldErrorMap.get(field);
						if(error != null && error.length() > 0) {
							if(errString.length() != 0)
								errString.append(" , ");
							errString.append(error);
						}
					}
					HSSFCell inCell = (HSSFCell) inRow .getCell((totalCols));
					if(inCell == null)
						inCell = inRow.createCell( ((totalCols)) );
					inCell.setCellValue(new HSSFRichTextString(errString.toString()));
					
					rowIndex++;
				}
				rowId++;
			}

			wb.write(out);
		}
		finally {
			if (stmt !=null )
				stmt.close();
			if (rs!=null)
				rs.close();
		}
	}

	
	private String getTempValue(int tempKey,String tempValue){
		if(tempKey>0 && tempValue == null){
			tempValue = AdminConstants.UPLOAD_FAILED_ERROR_CODE;
		}
		return tempValue;
	}
	
	private HashMap<String, String> createFieldErrorMap(String errorCodes, String uploaderLocale, List<UploadError> uploadErrors) {
		HashMap<String, String> fieldErrorMap = new HashMap<String, String>();
		if(errorCodes == null || errorCodes.length() == 0)
			return fieldErrorMap;
		String[] codes = errorCodes.split(";");
		if(uploaderLocale == null || uploaderLocale.length() == 0)
			uploaderLocale = "en_US";
		for(int i=0 ; i<codes.length ; i++) {
			String code = codes[i].trim();
			String field = null;
			String msg = null;
			try{
			for(UploadError uploadError : uploadErrors) {
				if(uploadError.getCode().equalsIgnoreCase(code)) {
					field = uploadError.getUploadField();
					msg = uploadError.getDescription(uploaderLocale);
					break;
				}
			}
			}
			catch(Exception e){
				field = null;
			}
			if(field == null) {
				field = "unknown";
				msg = code;
			}
			if(fieldErrorMap.containsKey(field))
				msg = fieldErrorMap.get(field)+" , "+msg;
			fieldErrorMap.put(field, msg);
		}
		return fieldErrorMap;
	}
	
	@SuppressWarnings("unchecked")
	public void generateErrorReport(Connection conn, OutputStream out, String errorReportQuery) 
		throws SQLException, IOException {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(errorReportQuery);
			/*
			RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
			Map<String, List> beans = new HashMap<String, List>(5);
			beans.put(getAliasName(), rsdc.getRows());
			
			XLSTransformer transformer = new XLSTransformer();
			//TODO: Do it for our own models
			//InputStream is = DealerErrorReportGenerator.class.getClassLoader().getResourceAsStream(getErrorTemplateName());
			InputStream is = null;
			*/			
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Error Report");
			HSSFCellStyle headerStyle = wb.createCellStyle();
			HSSFFont headerFont = wb.createFont();
			headerStyle.setFillBackgroundColor(new HSSFColor.GREY_25_PERCENT().getIndex());			
			headerFont.setColor(new HSSFColor.BLACK().getIndex());
			headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			headerStyle.setFont(headerFont);
			
			HSSFCellStyle dataStyle = wb.createCellStyle();
			HSSFFont dataFont = wb.createFont();
			headerStyle.setFillBackgroundColor(new HSSFColor.WHITE().getIndex());			
			dataFont.setColor(new HSSFColor.BLACK().getIndex());
			headerStyle.setFont(dataFont);
			
			HSSFRow row = null;
			HSSFCell cell = null;
			
			int rowIter = 0;
			short colIter = 0;
			ResultSetMetaData metaData = rs.getMetaData();
			int avlColumnLength = metaData.getColumnCount();
			
			row = sheet.createRow(rowIter++); // Header Row
			for (colIter = 2; colIter < avlColumnLength; colIter++) {
				// Ignore the first 2 columns since those 2 are internal purpose always
				cell =  row.createCell( ((colIter-2)) );
				if (!"".equals(metaData.getColumnName( ((colIter-2)) +1)))
					cell.setCellValue(new HSSFRichTextString(metaData.getColumnName( ((short)(colIter-2)) +1).replaceAll("_", " ")));				
				cell.setCellStyle(headerStyle);
			}
			
			// Error records population
			while (rs.next())
			{
				row = sheet.createRow(rowIter++); // Value Row
				
				for (colIter=2; colIter < avlColumnLength;colIter++)
				{
					// Ignore the first 2 columns since those 2 are internal purpose always
					cell = row.createCell( ((colIter-2)) );
					cell.setCellValue(new HSSFRichTextString(rs.getString( ((colIter-2)) +1)));				
					cell.setCellStyle(dataStyle);
				}
			}
			wb.write(out);
		}
		finally {
			if (stmt !=null )
				stmt.close();
			if (rs!=null)
				rs.close();
		}
	}
	private List<UploadError> getUploadErrorsForUploadMgt(Connection conn, long uploadMgtId)
	throws SQLException {
		Statement stmt = null;
		List<UploadError> uploadErrors = new ArrayList<UploadError>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT e.id, e.code, e.upload_field, t.locale, t.description " +
					"FROM upload_mgt_upload_errors me, upload_error e, " +
					"    i18nupload_error_text t " +
					"WHERE e.id = t.upload_error AND e.id = me.upload_errors " +
					"      AND me.upload_mgt = "+uploadMgtId +
					" ORDER BY e.id ");
			UploadError currentError = null;
			while(rs.next()) {
				Long id = rs.getLong(1);
				if(currentError != null && currentError.getId() != id) {
					uploadErrors.add(currentError);
					currentError = null;
				}
				if(currentError == null) {
					currentError = new UploadError();
					currentError.setId(id);
					currentError.setCode(rs.getString(2));
					currentError.setUploadField(rs.getString(3));
				}
				I18NUploadErrorText text = new I18NUploadErrorText();
				text.setLocale(rs.getString(4));
				text.setDescription(rs.getString(5));
				currentError.getI18nUploadErrorTexts().add(text);			
			}
			if(currentError != null)
				uploadErrors.add(currentError);
		} finally {
			 if (stmt!=null)
				 stmt.close();
		}
		return uploadErrors;
	}
	public abstract String getAliasName();
	
	public abstract String getErrorTemplateName();

}
