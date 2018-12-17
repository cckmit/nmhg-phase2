package tavant.twms.integration.layer.transformer;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import tavant.extwarranty.AmountTypeDTO;
import tavant.extwarranty.ExtendedWarrantyPriceCheckResponseDocumentDTO;
import tavant.extwarranty.ExtendedWarrantyPriceFetchDocumentDTO;
import tavant.extwarranty.LineItemsTypeDTO;
import tavant.extwarranty.PlansTypeDTO;
import tavant.extwarranty.PriceCheckRequestLineItemTypeDTO;
import tavant.extwarranty.PriceCheckRequestTypeDTO;
import tavant.extwarranty.PriceCheckResponseLineItemTypeDTO;
import tavant.extwarranty.StatusTypeDTO;
import tavant.extwarranty.ExtendedWarrantyPriceCheckResponseDocumentDTO.ExtendedWarrantyPriceCheckResponse;
import tavant.extwarranty.ExtendedWarrantyPriceFetchDocumentDTO.ExtendedWarrantyPriceFetch;

public class ExtWarrantyPriceCheckRequestTransformer {
	
	private static final Logger log = Logger
	.getLogger(ExtWarrantyPriceCheckRequestTransformer.class);
	
	
	private ExtendedWarrantyPriceFetch transform(String src) {
		ExtendedWarrantyPriceFetchDocumentDTO dto = null;
		try {
			dto = ExtendedWarrantyPriceFetchDocumentDTO.Factory.parse(src);
		} catch (XmlException e) {
			throw new RuntimeException(e);
		}
		return dto.getExtendedWarrantyPriceFetch();
	}
	
	public String convert(String bod) {
		log.info("Inside WSExtWarrantyPriceCheckServiceImpl, recieved BOD " + bod);
		ExtendedWarrantyPriceFetch requestDTO = transform(bod);
		ExtendedWarrantyPriceCheckResponse priceCheckResponse = createResponse(requestDTO);
		ExtendedWarrantyPriceCheckResponseDocumentDTO responseDoc = ExtendedWarrantyPriceCheckResponseDocumentDTO.Factory
				.newInstance();
		responseDoc.setExtendedWarrantyPriceCheckResponse(priceCheckResponse);
		String response = responseDoc.xmlText(createXMLOptions());
		log.info("Inside WSExtWarrantyPriceCheckServiceImpl, sending response BOD"
				+ response);
		return response;
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

	private ExtendedWarrantyPriceCheckResponse createResponse(
			ExtendedWarrantyPriceFetch requestDTO) {

		ExtendedWarrantyPriceCheckResponse priceCheckResponse = ExtendedWarrantyPriceCheckResponse.Factory
				.newInstance();
		StatusTypeDTO status = StatusTypeDTO.Factory.newInstance();
		status.setCode("SUCCESS");
		status.setErrorMessage("");

		PriceCheckRequestTypeDTO priceCheckRequest = requestDTO
				.getDataArea().getPriceCheckRequest();
		
		priceCheckResponse.setDealerNo(priceCheckRequest.getDealerNo());
		PlansTypeDTO plans = priceCheckRequest.getPlans();
		PriceCheckRequestLineItemTypeDTO[] lineItems = plans.getLineItemArray();
		
		PriceCheckResponseLineItemTypeDTO[] responseLineItems = new PriceCheckResponseLineItemTypeDTO[lineItems.length];
		
		int i = 0;
		
		for (PriceCheckRequestLineItemTypeDTO priceCheckRequestLineItemTypeDTO : lineItems) {
			PriceCheckResponseLineItemTypeDTO responseLineItem = PriceCheckResponseLineItemTypeDTO.Factory.newInstance();
			responseLineItem.setPlanCode(priceCheckRequestLineItemTypeDTO.getPlanCode());
			responseLineItem.setPlanItemNumber(priceCheckRequestLineItemTypeDTO.getPlanItemNumber());
			AmountTypeDTO amt = AmountTypeDTO.Factory.newInstance();
			amt.setCurrencyCodeAttributeType("USD");
			amt.setBigDecimalValue(new BigDecimal(0.0));
			responseLineItem.setAmount(amt);
			
			responseLineItems[i] = responseLineItem;
			i++;
		}
		LineItemsTypeDTO responseLineItemTypes = LineItemsTypeDTO.Factory.newInstance();
		responseLineItemTypes.setLineItemArray(responseLineItems);
		priceCheckResponse.setPlans(responseLineItemTypes);
		return priceCheckResponse;

	}


}
