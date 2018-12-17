package tavant.twms.integration.layer.component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import tavant.extwarranty.ApplicationAreaTypeDTO;
import tavant.extwarranty.ExtWarrantyDebitSubmissionDataAreaTypeDTO;
import tavant.extwarranty.IRInvoiceTypeDTO;
import tavant.extwarranty.PlanTypeDTO;
import tavant.extwarranty.PlansListTypeDTO;
import tavant.extwarranty.ProcessIriInvoiceDocumentDTO;
import tavant.extwarranty.SenderTypeDTO;
import tavant.extwarranty.ProcessIriInvoiceDocumentDTO.ProcessIriInvoice;
import tavant.twms.domain.policy.ExtWarrantyPlan;
import tavant.twms.external.ExtWarrantyRequest;
import tavant.twms.integration.layer.IntegrationPropertiesBean;


public class ProcessExtWarrantyDebitSubmission {
	private static Logger logger = Logger.getLogger(ProcessExtWarrantyDebitSubmission.class
			.getName());

	IntegrationPropertiesBean integrationPropertiesBean;
	
	public String syncClaim(ExtWarrantyRequest extWarrantyDebitSubmitRequest) {
		return createBodFromObject(extWarrantyDebitSubmitRequest);
	}

	private String createBodFromObject(ExtWarrantyRequest extWarrantyDebitSubmitRequest) {

		ProcessIriInvoiceDocumentDTO doc = ProcessIriInvoiceDocumentDTO.Factory
				.newInstance();
		ProcessIriInvoice processIriInvoice = ProcessIriInvoice.Factory
				.newInstance();

		ApplicationAreaTypeDTO applicationAreaTypeDTO = createApplicationArea(extWarrantyDebitSubmitRequest);
		processIriInvoice.setApplicationArea(applicationAreaTypeDTO);

		// Create Data area
		ExtWarrantyDebitSubmissionDataAreaTypeDTO dataArea = ExtWarrantyDebitSubmissionDataAreaTypeDTO.Factory
				.newInstance();

		IRInvoiceTypeDTO irInvoice = populateBasicData(extWarrantyDebitSubmitRequest);
		
		PlansListTypeDTO planListTypeDTO = populatePlanList(extWarrantyDebitSubmitRequest.getPlans());
		
		irInvoice.setPlansList(planListTypeDTO);
		
		dataArea.setIriInvoice(irInvoice);
		processIriInvoice.setDataArea(dataArea);
		doc.setProcessIriInvoice(processIriInvoice);
		
		String xml = doc.xmlText(createXMLOptions());
		if(logger.isInfoEnabled())
		{
        		logger.info("ProcessClaim :: createBodFromObject , The XML formed is :    ");
        		logger.info(xml);
		}
		return xml;

	}

	private PlansListTypeDTO populatePlanList(
			List<ExtWarrantyPlan> extWarrantyPlans) {
		
		PlansListTypeDTO plansList = PlansListTypeDTO.Factory.newInstance();
		PlanTypeDTO[] plans = new PlanTypeDTO[extWarrantyPlans.size()];
		int i = 0;
		for (ExtWarrantyPlan extWarrantyPlan : extWarrantyPlans) {
			PlanTypeDTO planDTO = PlanTypeDTO.Factory.newInstance();
			planDTO.setPlanItemNumber(extWarrantyPlan.getPlanItemNumber());
			planDTO.setPlanCode(extWarrantyPlan.getPlanCode());
			BigDecimal amt = new BigDecimal(0.0);
			if(extWarrantyPlan.getAmount()!=null){
				amt = extWarrantyPlan.getAmount().breachEncapsulationOfAmount();
			}
			planDTO.setAmount(amt);
			
			plans[i]=planDTO;
			i++;
		}
		plansList.setPlanArray(plans);
		
		return plansList;
		
	}

	private ApplicationAreaTypeDTO createApplicationArea(
			ExtWarrantyRequest extWarrantyDebitSubmitRequest) {
		// Create application area
		ApplicationAreaTypeDTO applicationAreaTypeDTO = ApplicationAreaTypeDTO.Factory
				.newInstance();
		SenderTypeDTO senderTypeDto = SenderTypeDTO.Factory.newInstance();
		senderTypeDto.setLogicalId(integrationPropertiesBean.getLogicalId());
		senderTypeDto.setTask(integrationPropertiesBean.getTask());
		applicationAreaTypeDTO.setSender(senderTypeDto);
		applicationAreaTypeDTO.setCreationDateTime(Calendar.getInstance());
		
		// We might need to set it to the Purchase Order Number (good to have).
		// Mel however would not be using the BOD id.
		// As a quick fix setting it to the serial number.
		applicationAreaTypeDTO.setBODId(extWarrantyDebitSubmitRequest.getSerialNumber());
		return applicationAreaTypeDTO;
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




	/**
	 * @param claim
	 * @return
	 */
	private IRInvoiceTypeDTO populateBasicData(ExtWarrantyRequest extWarrantyDebitSubmitRequest) {
		IRInvoiceTypeDTO irInvoice = IRInvoiceTypeDTO.Factory.newInstance();

		// Set basic data

		irInvoice.setDealerNumber(extWarrantyDebitSubmitRequest.getDealerNo());
		irInvoice.setUserItemType("Extended");
		irInvoice.setItemNumber(extWarrantyDebitSubmitRequest.getItemNumber());
		irInvoice.setSerialNumber(extWarrantyDebitSubmitRequest.getSerialNumber());
		irInvoice.setDescription(extWarrantyDebitSubmitRequest.getDescription());
		Calendar purchaseDateCal = Calendar.getInstance();
		purchaseDateCal.setTime(extWarrantyDebitSubmitRequest.getPurchaseDate());
		irInvoice.setPurchaseDate(purchaseDateCal);
		return irInvoice;
	}

	public void setIntegrationPropertiesBean(
			IntegrationPropertiesBean integrationPropertiesBean) {
		this.integrationPropertiesBean = integrationPropertiesBean;
	}


}
