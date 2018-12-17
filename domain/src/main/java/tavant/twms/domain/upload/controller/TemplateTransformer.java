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

package tavant.twms.domain.upload.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

/**
 * @author jhulfikar.ali
 *
 */
public abstract class TemplateTransformer {

	private String stagingTable;
	
	@SuppressWarnings("unchecked")
	public abstract UploadStatusDetail transform(InputStream file, FileOutputStream out, 
			long fileUploadMgtId, int maxRowsAllowed, UploadManagement currentDataUpload,List<UploadManagementMetaData> uploadManagementMetaDatas)
			throws IOException ;
	/*
	 * Checks if the given cell is date formatted.
	 */
	protected boolean isDate(HSSFCell inCell) {
		return HSSFDateUtil.isCellDateFormatted(inCell);
		/*boolean isDate = false;
		if (HSSFDateUtil.isCellDateFormatted(inCell))
			return true;
		else
		{
			//check for date formats not covered by isCelldateFormatted method
			HSSFCellStyle style = inCell.getCellStyle();
		    int i = style.getDataFormat();
		    switch (i) {
		    	case 0xA5: //
				case 0xA6: // Monday, July 24, 2006
				case 0xA7: // 7/24	
				case 0xA8: // 7/24/06	
				case 0xA9: // 07/24/06	
				case 0xAA: // 24-Jul	
				case 0xAB: // 24-Jul-06	
				case 0xAD: // 24-Jul-06	
				case 0xAE: // Jul-06	
				case 0xAF: // July-06	
				case 0xB0: // July 24, 2006	
				case 0xB1: // 7/24/06 12:00 AM	
				case 0xB2: // 7/24/06 0:00	
				case 0xB3: // J	
				case 0xB4: // J-06	
				case 0xB5: // 7/24/2006	
				case 0xB6: // 24-Jul-2006
		        isDate = true;
		        break;
		      default:
		        isDate = false;
		       break;
		     }
		}
		return isDate;*/
	}

	/*
	 * Checks if the given cell is date formatted.
	 */
	protected boolean isNumber(HSSFCell inCell) {
		boolean isNumeric = false;
		// check for Number formats
		HSSFCellStyle style = inCell.getCellStyle();
		int i = style.getDataFormat();
		switch (i) {
		case 0 : // General format with Number value
		case 1: //0-Decimal Place
		case 2: // 2-Decimal Place
		case 49 : // Text format with Number value
		
		/*case 196: // 1-Decimal Place
		case 175: // 1-Decimal Place with 0 places
		case 187: // 3-Decimal Place
		case 174: // 3-Decimal Place with 0 places
		case 197: // 4-Decimal Place
		case 173: //4-Decimal Place with 0 places
*/		
			isNumeric = true;
			break;
		default:
			isNumeric = false;
			break;
		}

		return isNumeric;
	}

	public String getStagingTable() {
		return stagingTable;
	}

	public void setStagingTable(String stagingTable) {
		this.stagingTable = stagingTable;
	}
	
}
