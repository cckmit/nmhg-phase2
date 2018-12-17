package tavant.twms.integration.layer.component.global;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import com.domainlanguage.money.Money;

import tavant.globalsync.supplierdebitsubmission.ApplicationAreaTypeDTO;
import tavant.globalsync.supplierdebitsubmission.DataAreaTypeDTO;
import tavant.globalsync.supplierdebitsubmission.InvoiceTypeDTO;
import tavant.globalsync.supplierdebitsubmission.SupplierDebitSubmissionDocumentDTO;

import tavant.globalsync.supplierdebitsubmission.TotalAmountTypeDTO;
import tavant.globalsync.supplierdebitsubmission.SenderTypeDTO;
import tavant.globalsync.supplierdebitsubmission.SupplierDebitSubmissionDocumentDTO.SupplierDebitSubmission;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimAudit;
import tavant.twms.domain.claim.payment.RecoveryPayment;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.integration.layer.IntegrationPropertiesBean;
import tavant.twms.integration.layer.constants.IntegrationConstants;


public class ProcessGlobalSupplierDebitSubmission extends IntegrationConstants{
	private static Logger logger = Logger
	.getLogger(ProcessGlobalSupplierDebitSubmission.class.getName());
	
	IntegrationPropertiesBean integrationPropertiesBean;
	public String syncGlobalSupplierDebit(RecoveryClaim recoveryClaim) {
		return createBodFromObject(recoveryClaim);
	}
	
	private String createBodFromObject(RecoveryClaim recoveryClaim){
		
		if (logger.isInfoEnabled()) {
			logger
					.info("ProcessGlobalSupplierSubmission :: createBodFromObject , The Claim :    "
							+ recoveryClaim.getClaim().getClaimNumber());
		}
		SupplierDebitSubmissionDocumentDTO doc = SupplierDebitSubmissionDocumentDTO.Factory.newInstance();
		SupplierDebitSubmission supplierDebitSubmission = SupplierDebitSubmission.Factory.newInstance();
						
		if (logger.isInfoEnabled()) {
			logger
					.info("ProcessGlobalClaim :: Create application area , The Claim :    "
							+ recoveryClaim.getClaim().getClaimNumber());
		}
		ApplicationAreaTypeDTO appAreaTypeDto = createApplicationArea(recoveryClaim);
		supplierDebitSubmission.setApplicationArea(appAreaTypeDto);
		
		if (logger.isInfoEnabled()) {
			logger
					.info("ProcessGlobalClaim :: Create data area , The Claim :    "
							+ recoveryClaim.getClaim().getClaimNumber());
		}
		DataAreaTypeDTO dataAreaDTO = DataAreaTypeDTO.Factory.newInstance();
		
		if (logger.isInfoEnabled()) {
			logger
					.info("ProcessGlobalClaim :: Create Basic Data Area , The Claim :    "
							+ recoveryClaim.getClaim().getClaimNumber());
		}
		InvoiceTypeDTO supSubmissionInvoice = populateBasicData(recoveryClaim); 
		dataAreaDTO.setInvoice(supSubmissionInvoice);
		
		supplierDebitSubmission.setDataArea(dataAreaDTO);
		doc.setSupplierDebitSubmission(supplierDebitSubmission);
		
		String xml = doc.xmlText(createXMLOptions());
		
		if(logger.isInfoEnabled()){
		    logger.info("ProcessClaim :: createBodFromObject , The XML formed is :    ");
		    logger.info(xml);
		}
		return xml;
		
	}
	
	private XmlOptions createXMLOptions() {
		// Generate the XML document
		XmlOptions xmlOptions = new XmlOptions();
		xmlOptions.setSavePrettyPrint();
		xmlOptions.setSavePrettyPrintIndent(4);
		xmlOptions.setSaveAggressiveNamespaces();
		xmlOptions.setUseDefaultNamespace();
		return xmlOptions;
	}
	
	private ApplicationAreaTypeDTO createApplicationArea(RecoveryClaim recoveryClaim){
				
		ApplicationAreaTypeDTO applicationAreaTypeDTO = ApplicationAreaTypeDTO.Factory.newInstance();
		SenderTypeDTO senderTypeDTO = SenderTypeDTO.Factory.newInstance();
		
		int auditOrder = recoveryClaim.getClaim().getClaimAudits().size() + 1;
		int asciiA = 65;
		int asciiValueToAppend = asciiA + (auditOrder - 1);
		senderTypeDTO.setReferenceId(recoveryClaim.getClaim().getClaimNumber() + "_"
				+ (char) asciiValueToAppend);
		{
			logger
					.info("ProcessGlobalClaim :: exiting createApplicationArea , The Claim :    "
							+ recoveryClaim.getClaim().getClaimNumber());
		}
		populateTaskLogicalId(senderTypeDTO, recoveryClaim);
		applicationAreaTypeDTO.setSender(senderTypeDTO);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		applicationAreaTypeDTO.setCreationDateTime(calendar);
		applicationAreaTypeDTO.setInterfaceNumber(EMPTY_STRING);
		applicationAreaTypeDTO.setBODId(BOD_NAME_DEBIT_SUBMISSION);
		return applicationAreaTypeDTO;
		
		/*		
		senderTypeDTO.setLogicalId(integrationPropertiesBean.getLogicalId());
		senderTypeDTO.setReferenceId(integrationPropertiesBean.getReferenceId());
		senderTypeDTO.setTask(integrationPropertiesBean.getTask());		
		applicationAreaTypeDTO.setSender(senderTypeDTO);				
		applicationAreaTypeDTO.setInterfaceNumber(EMPTY_STRING);
				
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		applicationAreaTypeDTO.setCreationDateTime(calendar);		
		
		Claim claim = recoveryClaim.getClaim();		
		List<RecoveryClaimAudit> recoveryClaimAuditList = recoveryClaim.getRecoveryClaimAudits()== null ? new ArrayList<RecoveryClaimAudit>(): recoveryClaim.getRecoveryClaimAudits();		
		int auditOrder = recoveryClaimAuditList.size() + 1;
		int asciiA = 65;
		int asciiValueToAppend = asciiA + (auditOrder - 1);		
		applicationAreaTypeDTO.setBODId(claim.getClaimNumber() + "_" +(char)asciiValueToAppend);			
		return applicationAreaTypeDTO;		*/
	}
	
	private InvoiceTypeDTO populateBasicData(RecoveryClaim recoveryClaim){
		if (logger.isInfoEnabled()) {
			logger
					.info("ProcessGlobalSupplierSubmission :: pupulateBasicData , The Claim :    "
							+ recoveryClaim.getClaim().getClaimNumber());
		}
		InvoiceTypeDTO supplierSubmissionInvoiceTypeDTO = InvoiceTypeDTO.Factory.newInstance();
		
		if (logger.isInfoEnabled()) {
			logger
					.info("ProcessGlobalClaim :: start populateInvoiceData() for the Claim :  "
							+ recoveryClaim.getClaim().getClaimNumber());
		}
		supplierSubmissionInvoiceTypeDTO.setLineContext(RECOVERY);
		if (logger.isInfoEnabled()) {
			logger
					.info("ProcessGlobalClaim :: populateInvoiceData() audit size , The Claim :    "
							+ recoveryClaim.getClaim().getClaimAudits().size());
		}
		Claim claim = recoveryClaim.getClaim();		
		
		List<RecoveryClaimAudit> recoveryClaimAudit = recoveryClaim.getRecoveryClaimAudits() == null ? new ArrayList<RecoveryClaimAudit>() : recoveryClaim.getRecoveryClaimAudits();		
		int auditOrder = recoveryClaimAudit.size() + 1;
		int asciiA = 65;
		int asciiValueToAppend = asciiA + (auditOrder - 1);		
		
		//Warranty Claim Number
		supplierSubmissionInvoiceTypeDTO.setWarrantyClaimNumber(claim.getClaimNumber()+ "_" +(char)asciiValueToAppend);	 
		
		//Recovery Item Number
		supplierSubmissionInvoiceTypeDTO.setRecoveryItemNumber(claim.getServiceInformation().getCausalPart().getNumber());
		
		//Warranty Claim Type
		supplierSubmissionInvoiceTypeDTO.setWarrantyClaimType(claim.getType().getType());
		
		//Batch Source Name
		supplierSubmissionInvoiceTypeDTO.setBatchSourceName(RECOVERY_TAVANT);		
		Supplier supplier = recoveryClaim.getContract().getSupplier();		
		
		//Supplier Number
		supplierSubmissionInvoiceTypeDTO.setSupplierNumber(supplier.getSupplierNumber());		
		if(claim.getForItem() != null){
			//Warranty Serial Number
			supplierSubmissionInvoiceTypeDTO.setWarrantySerialNumber(claim.getForItem().getSerialNumber());			
		}		
		//Site Number and Operating Unit
		supplierSubmissionInvoiceTypeDTO.setSiteNumber(EMPTY_STRING);
		supplierSubmissionInvoiceTypeDTO.setOperatingUnit(EMPTY_STRING);
		
		//Total Amount
		TotalAmountTypeDTO totalAmountTypeDTO = TotalAmountTypeDTO.Factory.newInstance();
		RecoveryPayment recPayment = recoveryClaim.getRecoveryPayment();
		Money moneyForRecovery = recPayment.getTotalRecoveryAmount().minus(recPayment.getPreviousPaidAmount());
		totalAmountTypeDTO.setAmount(moneyForRecovery.breachEncapsulationOfAmount().negate().toString());
		totalAmountTypeDTO.setCurrencyCode(moneyForRecovery.breachEncapsulationOfCurrency().getCurrencyCode());
		supplierSubmissionInvoiceTypeDTO.setTotalAmount(totalAmountTypeDTO);
		return supplierSubmissionInvoiceTypeDTO;
		
	}
	
	public void setIntegrationPropertiesBean(
			IntegrationPropertiesBean integrationPropertiesBean) {
		this.integrationPropertiesBean = integrationPropertiesBean;
	}

    private void populateTaskLogicalId(SenderTypeDTO senderTypeDTO, RecoveryClaim rClaim) {
        senderTypeDTO.setTask(integrationPropertiesBean.getTaskIdForTWMS());
        senderTypeDTO.setLogicalId(integrationPropertiesBean.getLogicalIdForTWMS());
    }
}
