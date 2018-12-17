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

package tavant.twms.domain.download;

import com.domainlanguage.time.CalendarDate;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.mail.MethodNotSupportedException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import au.com.bytecode.opencsv.CSVWriter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.springframework.util.StringUtils;
import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.failurestruct.AssemblyDefinition;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.supplier.CostLineItem;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.upload.connection.ReportTaskDAO;
import tavant.twms.domain.upload.controller.DataUploadConfig;

/**
 * @author jhulfikar.ali
 *
 */
public abstract class WarrantyReportGenerator implements WarrantyReport {

	private Logger logger = Logger.getLogger(WarrantyReportGenerator.class);

	private ReportTaskDAO reportTaskDAO;
	 
	private DataUploadConfig dataUploadConfig;

	public boolean isPaginationSupported() {
		return false;
	}

	protected String getOrderByClause() {return "";}
	
	protected String getGroupByClause() {return "";}
	
	protected String getProjectionClause(ReportSearchBean reportSearchBean)
	throws MethodNotSupportedException {
		throw new MethodNotSupportedException();
	}
	protected String getFromClause(ReportSearchBean reportSearchBean)
	throws MethodNotSupportedException {
		throw new MethodNotSupportedException();
	}
	protected String getWhereClause(ReportSearchBean reportSearchBean)
	throws MethodNotSupportedException {
		throw new MethodNotSupportedException();
	}
	protected String getProjectionClauseForPagination(ReportSearchBean reportSearchBean)
	throws MethodNotSupportedException {
		throw new MethodNotSupportedException();
	}
	protected String getOrderByClauseForPagination(ReportSearchBean reportSearchBean)
	throws MethodNotSupportedException {
		throw new MethodNotSupportedException();
	}
	
	protected String getReportQuery(ReportSearchBean reportSearchBean) {
		try {
			StringBuffer buffer = new StringBuffer(getSelectClause())
				.append(getProjectionClause(reportSearchBean))
				.append(getFromClause(reportSearchBean))
				.append(getWhereClause(reportSearchBean));
			return buffer.toString();
		} catch (MethodNotSupportedException e) {
			return null;
		}
	}
	
	protected String getReportCountQuery(ReportSearchBean reportSearchBean) {
		try {
			StringBuffer buffer = new StringBuffer(getSelectClause())
				.append(" count(*) ")
				.append(getFromClause(reportSearchBean))
				.append(getWhereClause(reportSearchBean));
			return buffer.toString();
		} catch (MethodNotSupportedException e) {
			return null;
		}
	}
	
	protected String getPaginatedReportQuery(ReportSearchBean reportSearchBean) {
		try {
			StringBuffer buffer = new StringBuffer(getSelectClause())
				.append(getProjectionClause(reportSearchBean))
				.append(" from ( ")
				.append(getSelectClause())
				.append(" row_.*,rownum rownum_ from ( ")
					.append(getSelectClause())
					.append(getProjectionClauseForPagination(reportSearchBean))
					.append(getFromClause(reportSearchBean))
					.append(getWhereClause(reportSearchBean))
					.append(getOrderByClauseForPagination(reportSearchBean))
				.append(" ) row_ ");
			return buffer.toString();
		} catch(MethodNotSupportedException mnse) {
			return null;
		}
	}

	public abstract String getReportFileName() throws MethodNotSupportedException;
	
	protected abstract List<String> getReportColumnHeading(ReportSearchBean reportSearchBean) throws MethodNotSupportedException;
	
	
	
	public long getReportDataCount(ReportSearchBean reportSearchBean) throws SQLException {
		Connection conn = null;
		Statement statement = null;
		String countQuery = getReportCountQuery(reportSearchBean);
		if(countQuery == null)
			return -1;
		try {
			conn = reportTaskDAO.getSQLConnection();
			
			StringBuffer reportCountQuery = new StringBuffer(countQuery)
				.append(getGroupByClause());
			statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(reportCountQuery.toString());
			int rowCount = -1;
			if(rs.next()) {
				rowCount = rs.getInt(1);
			}
			return rowCount;
		}
		finally {
			if (statement != null) {
				statement.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	
	
	public void exportReportData(ReportSearchBean reportSearchBean, OutputStream os, int downloadPageNumber) throws SQLException, IOException, MethodNotSupportedException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = reportTaskDAO.getSQLConnection();
			long maxRecords = dataUploadConfig.getExportRecordsLimit();
			StringBuffer reportQuery = new StringBuffer("");
			if(isPaginationSupported()) {
				reportQuery.append(getPaginatedReportQuery(reportSearchBean));
				long startRow = downloadPageNumber * maxRecords;
				long endRow = (downloadPageNumber+1) * maxRecords;
				reportQuery.append(" where rownum <= ")
				.append(String.valueOf(endRow))
				.append(" ) where rownum_ > ")
				.append(String.valueOf(startRow))
				.append(" order by rownum_ ");
			}else {
				reportQuery.append(getReportQuery(reportSearchBean));
				reportQuery.append(" and rownum <= "+String.valueOf(maxRecords))
				.append(getGroupByClause())
				.append(getOrderByClause());
			}
			
			logger.info("Executing the reporting query : " + reportQuery.toString());
			preparedStatement = conn.prepareStatement(reportQuery.toString());
			ResultSet viewSet = preparedStatement.executeQuery();
		
			CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new OutputStreamWriter(os)), 
									reportSearchBean.getDelimiter().charAt(0));
			List<String> headingList = getReportColumnHeading(reportSearchBean);
			String[] headings = new String[headingList.size()];
			headingList.toArray(headings);
			csvWriter.writeNext(headings);
			csvWriter.writeAll(viewSet, false);
			csvWriter.close();
		}
		finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	public HSSFWorkbook exportReportData(ReportSearchBean reportSearchBean) throws SQLException, MethodNotSupportedException
	{
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		HSSFWorkbook wb = new HSSFWorkbook();
		try
		{
		conn = reportTaskDAO.getSQLConnection();
		StringBuffer reportQuery = new StringBuffer(getReportQuery(reportSearchBean))
			.append(" and rownum <= " + String.valueOf(dataUploadConfig.getExportRecordsLimit()));
		
		logger.info("Executing the reporting query : " + reportQuery.toString());
		preparedStatement = conn.prepareStatement(reportQuery.toString());
		ResultSet viewSet = preparedStatement.executeQuery();
		HSSFSheet sheet = wb.createSheet(getReportFileName());
		int rowIter = 0;
		int colIter = 0, iter = 0;
		// Heading
		HSSFRow row = sheet.createRow(rowIter++);
		HSSFCell cell = row.createCell((short) colIter++);
		cell.setCellValue(prepareHeaderCell(getReportColumnHeading(reportSearchBean), reportSearchBean.getDelimiter()));
		// Data that we are populating
		if (!viewSet.next())
		{
			row = sheet.createRow(rowIter++);
			cell = row.createCell((short) iter);
			cell.setCellValue(NO_RECORD_FOUND);
		}
		else
		{
			row = sheet.createRow(rowIter++);
			cell = row.createCell((short) iter);
			cell.setCellValue(viewSet.getString(iter+1));
			while (viewSet.next())
			{
				row = sheet.createRow(rowIter++);
				cell = row.createCell((short) iter);
				cell.setCellValue(viewSet.getString(iter+1));
			}
		}
		}
		finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		
		return wb;
	}
	
	protected String getSelectClause()
	{
		return "select ";
	}
	
	/* ReadMe: ignoreAllDealerClaimStatus -- to Ignore All Dealers check and Claim status 
	 * 				for Supplier Recovery Claim Report.
	 */

	protected String betweenClauseForDateColumn(ReportSearchBean reportSearchBean, String dateCriteria) {
		StringBuffer buff = new StringBuffer(); 
		buff.append(" and (" + dateCriteria
			+ " >= to_date('"
			+ reportSearchBean.getFromDate().toString(TWMSDateFormatUtil.DATE_FORMAT_CALENDAR_DD_MMM_YYYY)
			+ "', " + TWMSDateFormatUtil.DATE_FORMAT_SQL_DD_MMM_YYYY + ") AND "
			+dateCriteria +" < to_date('"
			+ reportSearchBean.getToDate().toString(TWMSDateFormatUtil.DATE_FORMAT_CALENDAR_DD_MMM_YYYY)
			+ "', " + TWMSDateFormatUtil.DATE_FORMAT_SQL_DD_MMM_YYYY + ")+1 )");
		return buff.toString();
	}
	
	protected String whereClauseForBusinessUnits(ReportSearchBean reportSearchBean, String alias) {
		StringBuffer buff = new StringBuffer(); 
		buff.append(" and "+alias+".business_unit_info in (" + 
				WarrantyReportHelper.populateBusinessUnitAsCSVForQuery(reportSearchBean.getBusinessUnitName()) + ") ");
		return buff.toString();
	}
	
	protected String whereClauseForBusinessUnits(ReportSearchBean reportSearchBean) {
		StringBuffer buff = new StringBuffer(); 
		buff.append(" and cl.business_unit_info in (" + 
				WarrantyReportHelper.populateBusinessUnitAsCSVForQuery(reportSearchBean.getBusinessUnitName()) + ") ");
		return buff.toString();
	}

    private String isOEMPart(Claim claim, OEMPartReplaced partReplaced) {
        String isOEMPart = "N";
        List<HussmanPartsReplacedInstalled> irRepolcedParts = claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled();
        for (HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled : irRepolcedParts) {
            if(hussmanPartsReplacedInstalled.getReplacedParts().contains(partReplaced)){
            	isOEMPart = "Y";
                break;
            }
        }
        return isOEMPart;
    }
	
	private String prepareHeaderCell(List<String> columnsHeading, String delimiter) {
		StringBuffer columnHeading = new StringBuffer();
		for (Iterator<String> iterator = columnsHeading.iterator(); iterator.hasNext();) {
			String heading = (String) iterator.next();
			columnHeading.append(heading).append(delimiter);	
		}	
		return columnHeading.toString();
	}

	protected String populateSelectClauseWithDelimiter(List<String> columns, String delimiter) {
		return WarrantyReportHelper.populateSelectClauseWithDelimiter(columns, delimiter);
	}
	
	protected String populateSelectClause(List<String> columns) {
		return WarrantyReportHelper.populateSelectClause(columns);
	}
	
	public DataUploadConfig getDataUploadConfig() {
		return dataUploadConfig;
	}

	public void setDataUploadConfig(DataUploadConfig dataUploadConfig) {
		this.dataUploadConfig = dataUploadConfig;
	}

	public ReportTaskDAO getReportTaskDAO() {
		return reportTaskDAO;
	}

	public void setReportTaskDAO(ReportTaskDAO reportTaskDAO) {
		this.reportTaskDAO = reportTaskDAO;
	}

	public void exportReportData(ReportSearchBean reportSearchBean, OutputStream os) throws SQLException, IOException, MethodNotSupportedException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = reportTaskDAO.getSQLConnection();
			StringBuffer reportQuery = new StringBuffer("");

			reportQuery.append(getReportQuery(reportSearchBean));
			reportQuery.append(getGroupByClause())
			.append(getOrderByClause());

			logger.info("Executing the reporting query : " + reportQuery.toString());
			preparedStatement = conn.prepareStatement(reportQuery.toString());
			ResultSet viewSet = preparedStatement.executeQuery();

			CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new OutputStreamWriter(os)), 
									reportSearchBean.getDelimiter().charAt(0));
			List<String> headingList = getReportColumnHeading(reportSearchBean);
			String[] headings = new String[headingList.size()];
			headingList.toArray(headings);
			csvWriter.writeNext(headings);
			csvWriter.writeAll(viewSet, false);
			csvWriter.close();
		}
		finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

    public void exportData(ReportSearchBean reportSearchBean, OutputStream os, List data) throws MethodNotSupportedException, IOException{
        CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new OutputStreamWriter(os)), 
                                reportSearchBean.getDelimiter().charAt(0));
        List<String> headingList = getReportColumnHeading(reportSearchBean);
        String[] headings = new String[headingList.size()];
        headingList.toArray(headings);
        csvWriter.writeNext(headings);
        for (Object o : data) {
            RecoveryClaim rc = (RecoveryClaim) o;
            if(rc.getRecoveryClaimInfo() != null){
                try{
                csvWriter.writeAll(getLineDate(rc));
                }catch(Exception e){
                    logger.error("Error creating rows for download for Recovery Claim Num : " + rc.getRecoveryClaimNumber(), e);
                }
            }
        }
        csvWriter.close();
    }

    private List getLineDate(RecoveryClaim rc) {
        Claim claim = rc.getClaim();
        String buName = rc.getBusinessUnitInfo().getName();
        String claimNumber = claim.getClaimNumber();
        String claimType =  claim.getClmTypeName();
        String rcState = rc.getRecoveryClaimState().getState();
        String rcCreatedOn = getFormattedDate(rc.getD().getCreatedOn());
        String claimFailureDate = getFormattedDate(claim.getFailureDate());
        String claimRepairDate = getFormattedDate(claim.getRepairDate());
        String dealerNum = claim.getForDealer().getDealerNumber();
        String dealerName = claim.getForDealer().getName();
        String supplierNum = rc.getContract().getSupplier().getSupplierNumber();
        String supplierName = rc.getContract().getSupplier().getName();
        Item causalPart = claim.getServiceInformation().getCausalPart();
        String causalPartNum = (causalPart != null) ? 
                causalPart.getNumber() : ""; // for field mod claims
        String [] commonColumnInfo = new String[]{buName,claimNumber, claimType, rcState, rcCreatedOn, claimFailureDate,
                claimRepairDate, dealerNum, dealerName, supplierNum, supplierName, causalPartNum};
        String [] claimColumnInfo = getClaimColumnInfo(claim);
        List<String[]> l = new ArrayList<String[]>();
        List<RecoverablePart> parts = rc.getRecoveryClaimInfo().getRecoverableParts();
        boolean needToClone = false;
        if(parts.size() > 1)
            needToClone = true;
        for (Iterator<RecoverablePart> it = parts.iterator(); it.hasNext();) {
            RecoverablePart recoverablePart = it.next();
            String [] commonInfo = (needToClone) ? commonColumnInfo.clone() : commonColumnInfo;
            OEMPartReplaced partReplaced = recoverablePart.getOemPart();
            Item replacedItemPart = partReplaced.getItemReference().getUnserializedItem();
            String replacedPartNum = replacedItemPart.getNumber();
            String isCausalPart = causalPartNum.equals(replacedPartNum) ? "Y" : "N";
            String isOEMPart = isOEMPart(claim, partReplaced);
            String replacedPartDesc = replacedItemPart.getDescription();
            int replacePartQuantity = recoverablePart.getQuantity();
            boolean isUomMapped = partReplaced.getUomMapping() != null;
            String uom = (isUomMapped) ? 
                    partReplaced.getUomMapping().getMappedUom() : 
                    partReplaced.getItemReference().getReferredItem().getUom().getName();
            String costPricePerUnit = (partReplaced.getCostPricePerUnit() != null) ? ((isUomMapped) ?
                     partReplaced.getCostPricePerUnit().dividedBy(partReplaced.getUomMapping().getMappingFraction().doubleValue()).breachEncapsulationOfAmount().toString():
                    partReplaced.getCostPricePerUnit().breachEncapsulationOfAmount().toString()) : "";
            String supplierCurrency = rc.getContract().getSupplier().getPreferredCurrency().getCurrencyCode();
            String replacePartCostCurrency = supplierCurrency;

            String irPartsTotal = (!it.hasNext()) ?
                    rc.getCostLineItem(Section.OEM_PARTS).getRecoveredCost().breachEncapsulationOfAmount().toString() : "";
            String nonOEMPartsTotal = (!it.hasNext()) ?
                    rc.getCostLineItem(Section.NON_OEM_PARTS).getRecoveredCost().breachEncapsulationOfAmount().toString() : "";
            String miscPartsTotal = (!it.hasNext()) ?
                    rc.getCostLineItem(Section.MISCELLANEOUS_PARTS).getRecoveredCost().breachEncapsulationOfAmount().toString() : "";
            String totalLaborHours = (!it.hasNext()) ?
                    getTotalLaborHours(claim) : "";
            String laborCostTotal = (!it.hasNext()) ?
                    rc.getCostLineItem(Section.LABOR).getRecoveredCost().breachEncapsulationOfAmount().toString() : "";
            String incidentalCost = (!it.hasNext()) ?
                    getIncidentalCost(rc.getCostLineItems()) : "";
            String totalCost = (!it.hasNext()) ?
                    getTotalCost(rc.getCostLineItems()) : "";
            
            commonInfo = StringUtils.addStringToArray(commonInfo, isCausalPart);
            commonInfo = StringUtils.addStringToArray(commonInfo, isOEMPart);
            commonInfo = StringUtils.addStringToArray(commonInfo, replacedPartNum);
            commonInfo = StringUtils.addStringToArray(commonInfo, replacedPartDesc);
            commonInfo = StringUtils.addStringToArray(commonInfo, String.valueOf(replacePartQuantity));
            commonInfo = StringUtils.addStringToArray(commonInfo, uom);
            commonInfo = StringUtils.addStringToArray(commonInfo, costPricePerUnit);
            commonInfo = StringUtils.addStringToArray(commonInfo, replacePartCostCurrency);
            commonInfo = StringUtils.concatenateStringArrays(commonInfo, claimColumnInfo);
            commonInfo = StringUtils.addStringToArray(commonInfo, supplierCurrency);
            commonInfo = StringUtils.addStringToArray(commonInfo, irPartsTotal);
            commonInfo = StringUtils.addStringToArray(commonInfo, nonOEMPartsTotal);
            commonInfo = StringUtils.addStringToArray(commonInfo, miscPartsTotal);
            commonInfo = StringUtils.addStringToArray(commonInfo, totalLaborHours);
            commonInfo = StringUtils.addStringToArray(commonInfo, laborCostTotal);
            commonInfo = StringUtils.addStringToArray(commonInfo, incidentalCost);
            commonInfo = StringUtils.addStringToArray(commonInfo, totalCost);
            l.add(commonInfo);
        }
        return l;
    }

    private String getJobCodeDesc(Claim claim) {
        StringBuilder sb = new StringBuilder();
        List<LaborDetail> laborDetails = claim.getServiceInformation().getServiceDetail().getLaborPerformed();
        for (Iterator<LaborDetail> it = laborDetails.iterator(); it.hasNext();) {
            LaborDetail laborDetail = it.next();
            if(laborDetail.getServiceProcedure() == null) continue;
            String actionName = laborDetail.getServiceProcedure().getDefinition().getActionDefinition().getName();
            List<AssemblyDefinition> ads = laborDetail.getServiceProcedure().getDefinition().getComponents();
            for (Iterator<AssemblyDefinition> adsIt = ads.iterator(); adsIt.hasNext();) {
                AssemblyDefinition assemblyDefinition = adsIt.next();
                sb.append(assemblyDefinition.getName()).append("-").append(actionName);
                if(adsIt.hasNext())
                    sb.append("-");
            }
        }
        return sb.toString();
    }

    private String getHoursInService(Claim claim) {
        BigDecimal hrsInService = BigDecimal.ZERO;
        for (ClaimedItem claimedItem : claim.getClaimedItems()) {
            if(claimedItem.getHoursInService() == null) continue;
            hrsInService = hrsInService.add(claimedItem.getHoursInService());
        }
        return hrsInService.toString();
    }

    private String getTotalLaborHours(Claim claim) {
        Double totLbrhrs = 0d;
        List<LaborDetail> laborDetails = claim.getServiceInformation().getServiceDetail().getLaborPerformed();
        for (Iterator<LaborDetail> it = laborDetails.iterator(); it.hasNext();) {
            LaborDetail laborDetail = it.next();
            if(laborDetail.getServiceProcedure() != null)
                totLbrhrs += laborDetail.getServiceProcedure().getSuggestedLabourHours();
            if(laborDetail.getAdditionalLaborHours() != null)
                totLbrhrs += laborDetail.getAdditionalLaborHours().doubleValue();
        }
        return totLbrhrs.toString();
    }

    private String getIncidentalCost(List<CostLineItem> costLineItems) {
        BigDecimal incCost = BigDecimal.ZERO;
        for (Iterator<CostLineItem> it = costLineItems.iterator(); it.hasNext();) {
            CostLineItem costLineItem = it.next();
            String sec = costLineItem.getSection().getName();
            if(!(Section.OEM_PARTS.equals(sec) || Section.NON_OEM_PARTS.equals(sec) ||
                    Section.MISCELLANEOUS_PARTS.equals(sec) || Section.LABOR.equals(sec)))
                incCost = incCost.add(costLineItem.getRecoveredCost().breachEncapsulationOfAmount());
        }
        return incCost.toString();
    }

    private String getTotalCost(List<CostLineItem> costLineItems) {
        BigDecimal totalCost = BigDecimal.ZERO;
        for (Iterator<CostLineItem> it = costLineItems.iterator(); it.hasNext();) {
            CostLineItem costLineItem = it.next();
            String sec = costLineItem.getSection().getName();
            if(!Section.TOTAL_CLAIM.equals(sec))
                totalCost = totalCost.add(costLineItem.getRecoveredCost().breachEncapsulationOfAmount());
        }
        return totalCost.toString();
    }

    private String getProcessorComments(Claim claim) {
        List<ClaimAudit> audits = claim.getClaimAudits();
        Collections.sort(audits, new Comparator<ClaimAudit>() {
            public int compare(ClaimAudit o1, ClaimAudit o2) {
                return o2.getId().compareTo(o1.getId());
            }
        });
        for (int i = 0; i < audits.size(); i++) {
            ClaimAudit claimAudit = audits.get(i);
            if(ClaimState.ACCEPTED.equals(claimAudit.getPreviousState()))
                return claimAudit.getInternalComments();
        }
        return "";
    }

    private String getFormattedDate(CalendarDate date) {
        if(date != null)
            return date.toString(TWMSDateFormatUtil.DATE_FORMAT_CALENDAR_DD_MMM_YYYY);
        return "";
    }

    private String[] getClaimColumnInfo(Claim claim) {
            String serialNumber = claim.getSerialNumber();
            ItemReference itemReference = claim.getItemReference();
            InventoryItem referredInvItem = itemReference.getReferredInventoryItem();
            String modelDesc = (referredInvItem != null ) ?
                    referredInvItem.getOfType().getModel().getName() :
                    (itemReference.getUnserializedItem() != null) ?
                        itemReference.getUnserializedItem().getModel().getName() : "";
            String buildDate = getFormattedDate(claim.getBuildDate());
            String invoiceDate = (referredInvItem != null) ?
                    getFormattedDate(referredInvItem.getShipmentDate()) : "";
            String deliveryDate = (referredInvItem != null) ?
                    getFormattedDate(referredInvItem.getDeliveryDate()) : "";
            String jobCodeDesc = getJobCodeDesc(claim);
            String hrsInService = getHoursInService(claim);
            String faultFound = (claim.getServiceInformation().getFaultFound() != null) ?
                    claim.getServiceInformation().getFaultFound().getName() : "";
            String causedBy = (claim.getServiceInformation().getCausedBy() != null) ? 
                    claim.getServiceInformation().getCausedBy().getDescription() : "";
            String dealerComments = claim.getConditionFound() + " ## " + claim.getWorkPerformed()  + " ##";
            String processorComments = getProcessorComments(claim); 
            return new String[]{serialNumber, modelDesc, buildDate, invoiceDate, deliveryDate,
                    jobCodeDesc, hrsInService, faultFound, causedBy, dealerComments, processorComments};
    }

    
}
