package tavant.twms.integration.layer.component;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import tavant.extwarranty.ExtendedWarrantyPriceFetchDocumentDTO;
import tavant.extwarranty.ExtendedWarrantyPriceFetchDocumentDTO.ExtendedWarrantyPriceFetch;
import tavant.extwarranty.ApplicationAreaTypeDTO;
import tavant.extwarranty.PriceCheckDataAreaTypeDTO;
import tavant.extwarranty.PriceCheckRequestLineItemTypeDTO;
import tavant.extwarranty.PriceCheckRequestTypeDTO;
import tavant.extwarranty.SenderTypeDTO;
import tavant.extwarranty.PlansTypeDTO;
import tavant.twms.domain.policy.ExtWarrantyPlan;
import tavant.twms.external.ExtWarrantyRequest;
import tavant.twms.integration.layer.IntegrationPropertiesBean;

public class ProcessExtWarrantyPriceCheck {

	private static Logger logger = Logger
			.getLogger(ProcessExtWarrantyPriceCheck.class.getName());

	IntegrationPropertiesBean integrationPropertiesBean;
	
	public String syncPriceCheck(
			ExtWarrantyRequest extWarrantyPriceCheckRequest) {
		return createBodFromObject(extWarrantyPriceCheckRequest);
	}

	private String createBodFromObject(ExtWarrantyRequest extWarrantyPriceCheckRequest) {

		ExtendedWarrantyPriceFetchDocumentDTO documentDTO = ExtendedWarrantyPriceFetchDocumentDTO.Factory.newInstance();
		
		ExtendedWarrantyPriceFetch extWarrantyPriceFetch = ExtendedWarrantyPriceFetch.Factory.newInstance();
		// Create application area
		ApplicationAreaTypeDTO applicationAreaTypeDTO = createApplicationArea();
		
		extWarrantyPriceFetch.setApplicationArea(applicationAreaTypeDTO);

		PriceCheckDataAreaTypeDTO priceCheckDataAreaDTO = PriceCheckDataAreaTypeDTO.Factory
				.newInstance();
		PriceCheckRequestTypeDTO priceCheckRequest = PriceCheckRequestTypeDTO.Factory
				.newInstance();
		setBasicData(extWarrantyPriceCheckRequest, priceCheckRequest);
		priceCheckRequest.setPlans(createPlans(extWarrantyPriceCheckRequest));
		priceCheckDataAreaDTO.setPriceCheckRequest(priceCheckRequest);
		extWarrantyPriceFetch.setDataArea(priceCheckDataAreaDTO);
		documentDTO.setExtendedWarrantyPriceFetch(extWarrantyPriceFetch);
		String xml = documentDTO.xmlText(createXMLOptions());
		if(logger.isInfoEnabled())
		{
        		logger.info("ProcessExtWarrantyPriceCheck :: createBodFromObject , The XML formed is :    ");
        		logger.info(xml);
		}
		return xml;
	}

	private ApplicationAreaTypeDTO createApplicationArea() {
		ApplicationAreaTypeDTO applicationAreaTypeDTO = ApplicationAreaTypeDTO.Factory
				.newInstance();
		// Create application area
		SenderTypeDTO senderTypeDto = SenderTypeDTO.Factory.newInstance();
		senderTypeDto.setLogicalId(integrationPropertiesBean.getLogicalId());
		senderTypeDto.setTask(integrationPropertiesBean.getTask());
		applicationAreaTypeDTO.setSender(senderTypeDto);
		applicationAreaTypeDTO.setCreationDateTime(Calendar.getInstance());
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

	private void setBasicData(
			ExtWarrantyRequest extWarrantyPriceCheckRequest,
			PriceCheckRequestTypeDTO priceCheckRequest) {
		priceCheckRequest.setDealerNo(extWarrantyPriceCheckRequest
				.getDealerNo());
		priceCheckRequest.setUserItemType("Extended");
		priceCheckRequest.setItemNumber(extWarrantyPriceCheckRequest
				.getItemNumber());
		priceCheckRequest.setDescription(extWarrantyPriceCheckRequest
				.getDescription());
	}

	private PlansTypeDTO createPlans(ExtWarrantyRequest extWarrantyPriceCheckRequest) {

		List<ExtWarrantyPlan> plans = extWarrantyPriceCheckRequest.getPlans();
		PlansTypeDTO planTypeDTO = PlansTypeDTO.Factory.newInstance();
		PriceCheckRequestLineItemTypeDTO[] lineItems = new PriceCheckRequestLineItemTypeDTO[plans.size()];
		int i=0;
		for(ExtWarrantyPlan plan: plans){
			PriceCheckRequestLineItemTypeDTO lineItem = PriceCheckRequestLineItemTypeDTO.Factory
			.newInstance();
			lineItem.setPlanCode(plan.getPlanCode());
			lineItem.setPlanItemNumber(plan.getPlanItemNumber());
			lineItems[i++] = lineItem;
		}
		planTypeDTO.setLineItemArray(lineItems);

		return planTypeDTO;
	}

	public void setIntegrationPropertiesBean(
			IntegrationPropertiesBean integrationPropertiesBean) {
		this.integrationPropertiesBean = integrationPropertiesBean;
	}
	
	
}
