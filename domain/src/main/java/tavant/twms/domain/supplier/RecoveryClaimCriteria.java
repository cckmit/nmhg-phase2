package tavant.twms.domain.supplier;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import tavant.twms.domain.claim.RecoveryClaimState;

import com.domainlanguage.time.CalendarDate;

/*
 * Simple pojo class to store all search conditions
 */
public class RecoveryClaimCriteria implements Serializable{

	private String claimNumber;
	private String recoveryClaimNumber;
	private String documentNumber;
	private String supplierNumber;
	private String[] state;
	private String supplierName;
	private String partNumber;
	private String supplierMemoNumber;
	private CalendarDate startWarrantyRequestDate;  
	private CalendarDate endWarrantyRequestDate;
	private CalendarDate startClosedDate;  
	private CalendarDate endClosedDate;
	private CalendarDate startClaimPayDate;  
	private CalendarDate endClaimPayDate;
	private List<String> selectedBusinessUnits = null;

	public List<String> getSelectedBusinessUnits() {
		return selectedBusinessUnits;
	}

	public void setSelectedBusinessUnits(List<String> selectedBusinessUnits) {
		this.selectedBusinessUnits = selectedBusinessUnits;
	}

	public String getSelectedBusinessUnitInfoDelimitedByComma()
	{
		String buNamesDelimitedByComma = "";
		Object[] businessUnits = (Object[])selectedBusinessUnits.toArray();
		for(int i=0;i<businessUnits.length;i++)
		{
			String buName  = (String)businessUnits[i];
			buNamesDelimitedByComma = buNamesDelimitedByComma + "'" + buName + "'";
			if(i < (businessUnits.length - 1) )
			{
				buNamesDelimitedByComma = buNamesDelimitedByComma + ",";
			}
		}
		return buNamesDelimitedByComma;
	}
	public String getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = StringUtils.trim(claimNumber);
	}

	public String getSupplierNumber() {
		return supplierNumber;
	}

	public void setSupplierNumber(String supplierNumber) {
		this.supplierNumber = StringUtils.trim(supplierNumber);
	}


	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = StringUtils.trim(supplierName);
	}
	//fix for 'unable to save processor recovery predefined search with 'status 'as search parameter'
	public String[] getState() {
		return state;
	}

	public void setState(String[] state) {
		this.state = state;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = StringUtils.trim(partNumber);
	}

	
	public String getSupplierMemoNumber(){
		return this.supplierMemoNumber;
	}

	public void setSupplierMemoNumber(String suppMemoNumber){
		this.supplierMemoNumber = StringUtils.trim(suppMemoNumber);
	}

	public CalendarDate getStartWarrantyRequestDate() {
		return startWarrantyRequestDate;
	}

	public void setStartWarrantyRequestDate(CalendarDate startWarrantyRequestDate) {
		this.startWarrantyRequestDate = startWarrantyRequestDate;
	}

	public CalendarDate getEndWarrantyRequestDate() {
		return endWarrantyRequestDate;
	}

	public void setEndWarrantyRequestDate(CalendarDate endWarrantyRequestDate) {
		this.endWarrantyRequestDate = endWarrantyRequestDate;
	}

	public CalendarDate getStartClosedDate() {
		return startClosedDate;
	}

	public void setStartClosedDate(CalendarDate startClosedDate) {
		this.startClosedDate = startClosedDate;
	}

	public CalendarDate getEndClosedDate() {
		return endClosedDate;
	}

	public void setEndClosedDate(CalendarDate endClosedDate) {
		this.endClosedDate = endClosedDate;
	}

	public CalendarDate getStartClaimPayDate() {
		return startClaimPayDate;
	}

	public void setStartClaimPayDate(CalendarDate startClaimPayDate) {
		this.startClaimPayDate = startClaimPayDate;
	}

	public CalendarDate getEndClaimPayDate() {
		return endClaimPayDate;
	}

	public void setEndClaimPayDate(CalendarDate endClaimPayDate) {
		this.endClaimPayDate = endClaimPayDate;
	}

	public String getRecoveryClaimNumber() {
		return recoveryClaimNumber;
	}

	public void setRecoveryClaimNumber(String recoveryClaimNumber) {
		this.recoveryClaimNumber = recoveryClaimNumber;
	}
	
	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	
}
