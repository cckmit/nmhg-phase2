package tavant.twms.domain.reports;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.domainlanguage.time.CalendarDate;

@SuppressWarnings("serial")
@Entity
@Table(name="VENDOR_RECOVERY_EXTRACT")
public class VendorRecoveryExtract implements Serializable {

	@Id
	private String id;

	private String claimNumber;
	
	private String recoveryClaimNumber;
	
	private String documentNumber;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate filedOnDate;

	private String recoveryClaimState;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate recClaimCreatedDate;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate recClaimUpdatedDate;

	private String dealerNumber;

	private String dealerName;

	private String causalPartNumber;

	private String replacedPartNumber;

	private String supplierNumber;

	private String supplierName;

	private String serialNumber;

	private String modelDesc;

	private String claimType;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate buildDate;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate invoiceDate;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate deliveryDate;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate failureDate;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate repairDate;

	private String jobCode;

	private BigDecimal totalLaborHours;

	private BigDecimal hoursInService;

	private String faultFound;

	private String causedBy;

	private String dealerComments;

	private String processorComments;

	private BigDecimal materialCostTotal;

	private BigDecimal nonTkPartsTotal;

	private BigDecimal materialPartsTotal;

	private BigDecimal miscCostTotal;

	private BigDecimal laborCostTotal;

	private BigDecimal totalContractAmt;

	private String supplierCurrency;

	private String businessUnitInfo;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate creditMemoDate;

	private String creditMemoNumber;

	private String recClaimUpdatedBy;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate recClaimModifiedDate;

	private String recoveryComments;

	private String contractName;

	private String recClaimAcceptanceReason;

	private String recClaimRejectionReason;

	private BigDecimal totalActualAmt;

	private BigDecimal totalWarrantyAmt;
	
	private String dealerCurrency;
	
	private BigDecimal actualValueAcceptedAmount;

	private BigDecimal creditMemoAmount;
	
	private String creditNoteAcceptedCurr;

	public BigDecimal getTotalActualAmt() {
		return totalActualAmt;
	}

	public void setTotalActualAmt(BigDecimal totalActualAmt) {
		this.totalActualAmt = totalActualAmt;
	}

	public BigDecimal getTotalWarrantyAmt() {
		return totalWarrantyAmt;
	}

	public void setTotalWarrantyAmt(BigDecimal totalWarrantyAmt) {
		this.totalWarrantyAmt = totalWarrantyAmt;
	}

	public String getRecoveryAcceptanceReason() {
		return (recClaimAcceptanceReason == null? recClaimRejectionReason: recClaimAcceptanceReason);
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public String getRecClaimAcceptanceReason() {
		return recClaimAcceptanceReason;
	}

	public void setRecClaimAcceptanceReason(String recClaimAcceptanceReason) {
		this.recClaimAcceptanceReason = recClaimAcceptanceReason;
	}

	public String getRecClaimRejectionReason() {
		return recClaimRejectionReason;
	}

	public void setRecClaimRejectionReason(String recClaimRejectionReason) {
		this.recClaimRejectionReason = recClaimRejectionReason;
	}


	public String getRecoveryComments() {
		return recoveryComments;
	}

	public void setRecoveryComments(String recoveryComments) {
		this.recoveryComments = recoveryComments;
	}

	public CalendarDate getRecClaimUpdatedDate() {
		return recClaimUpdatedDate;
	}

	public void setRecClaimUpdatedDate(CalendarDate recClaimUpdatedDate) {
		this.recClaimUpdatedDate = recClaimUpdatedDate;
	}

	public String getRecClaimUpdatedBy() {
		return recClaimUpdatedBy;
	}

	public void setRecClaimUpdatedBy(String recClaimUpdatedBy) {
		this.recClaimUpdatedBy = recClaimUpdatedBy;
	}

	public CalendarDate getRecClaimModifiedDate() {
		return recClaimModifiedDate;
	}

	public void setRecClaimModifiedDate(CalendarDate recClaimModifiedDate) {
		this.recClaimModifiedDate = recClaimModifiedDate;
	}

	public CalendarDate getCreditMemoDate() {
		return creditMemoDate;
	}

	public void setCreditMemoDate(CalendarDate creditMemoDate) {
		this.creditMemoDate = creditMemoDate;
	}

	public String getCreditMemoNumber() {
		return creditMemoNumber;
	}

	public void setCreditMemoNumber(String creditMemoNumber) {
		this.creditMemoNumber = creditMemoNumber;
	}

	public VendorRecoveryExtract() {
	}

	public CalendarDate getBuildDate() {
		return buildDate;
	}

	public void setBuildDate(CalendarDate buildDate) {
		this.buildDate = buildDate;
	}

	public String getCausalPartNumber() {
		return causalPartNumber;
	}

	public void setCausalPartNumber(String causalPartNumber) {
		this.causalPartNumber = causalPartNumber;
	}

	public String getCausedBy() {
		return causedBy;
	}

	public void setCausedBy(String causedBy) {
		this.causedBy = causedBy;
	}

	public String getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}

	public String getRecoveryClaimNumber() {
		return recoveryClaimNumber;
	}

	public void setRecoveryClaimNumber(String recoveryClaimNumber) {
		this.recoveryClaimNumber = recoveryClaimNumber;
	}

	public String getClaimType() {
		return claimType;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	public CalendarDate getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(CalendarDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public CalendarDate getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(CalendarDate invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getDealerComments() {
		return dealerComments;
	}

	public void setDealerComments(String dealerComments) {
		this.dealerComments = dealerComments;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getDealerNumber() {
		return dealerNumber;
	}

	public void setDealerNumber(String dealerNumber) {
		this.dealerNumber = dealerNumber;
	}

	public CalendarDate getFailureDate() {
		return failureDate;
	}

	public void setFailureDate(CalendarDate failureDate) {
		this.failureDate = failureDate;
	}

	public String getFaultFound() {
		return faultFound;
	}

	public void setFaultFound(String faultFound) {
		this.faultFound = faultFound;
	}

	public BigDecimal getHoursInService() {
		return hoursInService;
	}

	public void setHoursInService(BigDecimal hoursInService) {
		this.hoursInService = hoursInService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public BigDecimal getLaborCostTotal() {
		return laborCostTotal;
	}

	public void setLaborCostTotal(BigDecimal laborCostTotal) {
		this.laborCostTotal = laborCostTotal;
	}

	public BigDecimal getMaterialCostTotal() {
		return materialCostTotal;
	}

	public void setMaterialCostTotal(BigDecimal materialCostTotal) {
		this.materialCostTotal = materialCostTotal;
	}

	public BigDecimal getMaterialPartsTotal() {
		return materialPartsTotal;
	}

	public void setMaterialPartsTotal(BigDecimal materialPartsTotal) {
		this.materialPartsTotal = materialPartsTotal;
	}

	public BigDecimal getMiscCostTotal() {
		return miscCostTotal;
	}

	public void setMiscCostTotal(BigDecimal miscCostTotal) {
		this.miscCostTotal = miscCostTotal;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	public BigDecimal getNonTkPartsTotal() {
		return nonTkPartsTotal;
	}

	public void setNonTkPartsTotal(BigDecimal nonTkPartsTotal) {
		this.nonTkPartsTotal = nonTkPartsTotal;
	}

	public String getProcessorComments() {
		return processorComments;
	}

	public void setProcessorComments(String processorComments) {
		this.processorComments = processorComments;
	}

	public CalendarDate getRecClaimCreatedDate() {
		return recClaimCreatedDate;
	}

	public void setRecClaimCreatedDate(CalendarDate recClaimCreatedDate) {
		this.recClaimCreatedDate = recClaimCreatedDate;
	}

	public String getRecoveryClaimState() {
		return recoveryClaimState;
	}

	public void setRecoveryClaimState(String recoveryClaimState) {
		this.recoveryClaimState = recoveryClaimState;
	}

	public CalendarDate getRepairDate() {
		return repairDate;
	}

	public void setRepairDate(CalendarDate repairDate) {
		this.repairDate = repairDate;
	}

	public String getReplacedPartNumber() {
		return replacedPartNumber;
	}

	public void setReplacedPartNumber(String replacedPartNumber) {
		this.replacedPartNumber = replacedPartNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getSupplierCurrency() {
		return supplierCurrency;
	}

	public void setSupplierCurrency(String supplierCurrency) {
		this.supplierCurrency = supplierCurrency;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierNumber() {
		return supplierNumber;
	}

	public void setSupplierNumber(String supplierNumber) {
		this.supplierNumber = supplierNumber;
	}

	public BigDecimal getTotalContractAmt() {
		return totalContractAmt;
	}

	public void setTotalContractAmt(BigDecimal totalContractAmt) {
		this.totalContractAmt = totalContractAmt;
	}

	public BigDecimal getTotalLaborHours() {
		return totalLaborHours;
	}

	public void setTotalLaborHours(BigDecimal totalLaborHours) {
		this.totalLaborHours = totalLaborHours;
	}

	public String getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(String bussinessUnitInfo) {
		this.businessUnitInfo = bussinessUnitInfo;
	}
	
	public String getDealerCurrency() {
		return dealerCurrency;
	}

	public void setDealerCurrency(String dealerCurrency) {
		this.dealerCurrency = dealerCurrency;
	}
	
	public BigDecimal getActualValueAcceptedAmount() {
		return actualValueAcceptedAmount;
	}

	public void setActualValueAcceptedAmount(BigDecimal actualValueAcceptedAmount) {
		this.actualValueAcceptedAmount = actualValueAcceptedAmount;
	}

	public CalendarDate getFiledOnDate() {
		return filedOnDate;
	}

	public void setFiledOnDate(CalendarDate filedOnDate) {
		this.filedOnDate = filedOnDate;
	}

	public BigDecimal getCreditMemoAmount() {
		return creditMemoAmount;
	}

	public void setCreditMemoAmount(BigDecimal creditMemoAmount) {
		this.creditMemoAmount = creditMemoAmount;
	}
	
	public String getCreditNoteAcceptedCurr() {
		return creditNoteAcceptedCurr;
	}

	public void setCreditNoteAcceptedCurr(String creditNoteAcceptedCurr) {
		this.creditNoteAcceptedCurr = creditNoteAcceptedCurr;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}



}
