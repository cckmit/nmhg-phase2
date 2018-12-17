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
package tavant.twms.web.admin.campaign;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import tavant.twms.domain.campaign.CampaignAssignmentService;
import tavant.twms.domain.campaign.CampaignSerialNumbers;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

/**
 * @author Kiran.Kollipara
 */
@SuppressWarnings("serial")
public class FileUploadAction extends SummaryTableAction {

	private static final Logger logger = Logger
			.getLogger(FileUploadAction.class);

	private String contentType;

	private File upload;

	private String fileName;

	private InventoryService inventoryService;
	protected CampaignAssignmentService campaignAssignmentService;
	private String campaignId;
	private UploadSummary summary = new UploadSummary();

	private final List<CampaignSerialNumbers> serialNumbers = new ArrayList<CampaignSerialNumbers>();

	@Override
	public void validate() {
		try {
			if (this.upload == null) {
				throw new IOException();
			}
			InputStream myxls = new FileInputStream(this.upload);
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheet("SERIAL_NUMBERS");
			if (sheet == null) {
				this.upload = null;
				throw new IOException();
			}
		} catch (IOException e) {
			logger.error("Invalid input. Please download the template and proceed.",e);
			addActionError("error.campaign.invalidInput");
		}
	}

	protected List<InventoryItem> parseExcelContent() {
		List<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();
		if (this.upload == null) {
			return inventoryItems;
		}
		try {
			InputStream myxls = new FileInputStream(this.upload);
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheet("SERIAL_NUMBERS");
			int startRow = sheet.getFirstRowNum();
			int numRowsInSheet = sheet.getLastRowNum() + 1;
			for (int i = startRow + 1; i < numRowsInSheet; i++) {
				HSSFRow row = sheet.getRow(i);				
				String aSerialNumber = getCellValue(row,0);				
				if (StringUtils.hasText(aSerialNumber)) {
					parseRow(inventoryItems, row);
				} 
				else {
			        if(logger.isDebugEnabled()) {
                        logger.debug("finished parsing data...");
                    }
					break;
				}
			}
			if(!summary.getInvalidNumbers().equals("")){
				addActionError("error.campaign.invalidSerialNumbers");
			}
			this.upload = null;
		} catch (Exception e) {
			logger.error("Exception in parsing excel", e);
		}
		return inventoryItems;
	}

	private String getCellValue(HSSFRow row, int i) {
		String SerialOrModelNumber = "";
		if(row != null && row.getCell((short) i) != null){
			if (row.getCell((short) i).getCellType() == HSSFCell.CELL_TYPE_STRING)
			{
				SerialOrModelNumber = row.getCell((short) i).getStringCellValue();
			}
			else if (row.getCell((short) i).getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
			{
				SerialOrModelNumber = String.valueOf(row.getCell((short) i).getNumericCellValue());
				// This logic is for converting exponential value to integer value.
				Pattern exponentialPattern = Pattern.compile("[0-9]\\.[0-9]*[E][0-9]{1,2}");
				if (SerialOrModelNumber != null && StringUtils.hasText(SerialOrModelNumber.trim()) && exponentialPattern.matcher(SerialOrModelNumber).matches()) {
					try {
						SerialOrModelNumber = new BigDecimal(SerialOrModelNumber).toBigInteger().toString();
					} catch (NumberFormatException e) {
						if (logger.isInfoEnabled()) {
							logger.info("Serial or Model Number : "+SerialOrModelNumber);
						}
					}
				}
			}
			else if (row.getCell((short) i).getCellType() == HSSFCell.CELL_TYPE_BOOLEAN)
			{
				SerialOrModelNumber = String.valueOf(row.getCell((short) i).getBooleanCellValue());
			}
		}
		return SerialOrModelNumber;
	}

	public String getUploadFileName() {
		return this.fileName;
	}

	public void setUploadFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUploadContentType() {
		return this.contentType;
	}

	public void setUploadContentType(String contentType) {
		this.contentType = contentType;
	}

	public File getUpload() {
		return this.upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public UploadSummary getSummary() {
		return this.summary;
	}

	@Required
	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	// Private Methods

	private void parseRow(List<InventoryItem> inventoryItems, HSSFRow row) {
		String aSerialNumber = getCellValue(row,0);
		String aModelNumber = getCellValue(row,1);
		
		boolean itemAdded = addItem(inventoryItems, aSerialNumber, aModelNumber);
		this.summary.incrementTotalCount();
		if (itemAdded) {
			this.summary.incrementValidCount();
			this.serialNumbers.add(populateSNoObj(aSerialNumber));
		} else {
			this.summary.addInvalidNumber(aSerialNumber);
		}
	}

	private CampaignSerialNumbers populateSNoObj(String serialNumber) {
		CampaignSerialNumbers sNo = new CampaignSerialNumbers();
		sNo.setSerialNumber(serialNumber);
		return sNo;
	}

	private boolean addItem(List<InventoryItem> inventoryItems,
			String serialNumber, String modelNumber) {
		InventoryItem inventoryItem = null;
		if (serialNumber == null || !StringUtils.hasText(serialNumber.trim()))
			return false;		
		try {			
			if ((serialNumber != null && StringUtils.hasText(serialNumber.trim()))
					&& (modelNumber != null && StringUtils.hasText(modelNumber.trim()))) {
				inventoryItem = this.inventoryService.findMachine(serialNumber,
								modelNumber.toUpperCase());
				inventoryItems.add(inventoryItem);
			}
			else if(modelNumber == null || !StringUtils.hasText(modelNumber.trim())) {
				inventoryItem = this.inventoryService.findMachine(serialNumber);
				inventoryItems.add(inventoryItem);
			}
		} catch (ItemNotFoundException e) {
			if ((serialNumber != null && StringUtils.hasText(serialNumber.trim()))
					&& (modelNumber != null && StringUtils.hasText(modelNumber.trim()))) {
				/*addActionError("error.campaign.serialAndModelNumberNotFound",
						new String[] { serialNumber, modelNumber });*/
				return false;
			} else if (modelNumber == null || !StringUtils.hasText(modelNumber.trim())) {
				/*addActionError("error.campaign.serialNumberNotFound",
						new String[] { serialNumber });*/
				return false;
			}
		} catch (Exception e) {
			if ((serialNumber != null && StringUtils.hasText(serialNumber.trim()))
					&& (modelNumber != null && StringUtils.hasText(modelNumber.trim()))) {
				addActionError("error.campaign.duplicateSerialAndModelNumber",
						new String[] { serialNumber, modelNumber });
				return false;
			} else if (modelNumber == null || !StringUtils.hasText(modelNumber.trim())) {
				addActionError("error.campaign.duplicateSerialNumber",
						new String[] { serialNumber });
				return false;
			}
		}
		return true;
	}

	public void setSummary(UploadSummary summary) {
		this.summary = summary;
	}

	public List<CampaignSerialNumbers> getSerialNumbers() {
		return this.serialNumbers;
	}

	@Override
	protected PageResult<?> getBody() {
		return campaignAssignmentService.findAllCampaignNotificationsForCampaign(new Long(campaignId), new PageSpecification(this.page-1, this.pageSize));
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.machineSerialNo",
				"item.serialNumber", 5, "string",
				"item.serialNumber", true, false, false,
				false));
		tableHeadData.add(new SummaryTableColumn("", "id", 0, "String",
				"id", false, true, true, false));
		tableHeadData.add(new SummaryTableColumn("label.common.description",
				"item.ofType.description", 8, "String"));
		tableHeadData.add(new SummaryTableColumn("label.common.make",
				"item.ofType.make", 5, "String"));
		tableHeadData.add(new SummaryTableColumn("label.common.model",
				"item.ofType.model.name", 5, "String"));
		tableHeadData.add(new SummaryTableColumn("label.common.unitStatus",
				"statuswithReasonForExcel", 7, "String"));
				
		//Added for NMHGSLMS-578
		//End customer new information
		tableHeadData.add(new SummaryTableColumn("label.common.endCustomer.name",
				"getEndCustomerForUnit()", 8, "String"));
		tableHeadData.add(new SummaryTableColumn("label.common.endCustomer.city",
				"getEndCustomer().address.city", 8, "String"));
		tableHeadData.add(new SummaryTableColumn("label.common.endCustomer.state",
				"getEndCustomer().address.state", 8, "String"));
		tableHeadData.add(new SummaryTableColumn("label.common.endCustomer.country",
				"getEndCustomer().address.country", 8, "String"));	
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.dealerName",
				"item.currentOwner.name",8, "String"));
		//Dealer new information
		tableHeadData.add(new SummaryTableColumn("label.common.dealer.city",
				"item.currentOwner.address.city", 8, "String"));
		tableHeadData.add(new SummaryTableColumn("label.common.dealer.state",
				"item.currentOwner.address.state", 8, "String"));
		tableHeadData.add(new SummaryTableColumn("label.common.dealer.country",
				"item.currentOwner.address.country", 8, "String"));
	
		//-NMHGSLMS-578 End
		
		tableHeadData.add(new SummaryTableColumn("label.warrantyAdmin.campaignComplete",
				"notificationStatusForAdmin", 6, "String"));
		return tableHeadData;
	}
	public void setCampaignAssignmentService(CampaignAssignmentService campaignAssignmentService) {
		this.campaignAssignmentService = campaignAssignmentService;
	}
	
	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
}
