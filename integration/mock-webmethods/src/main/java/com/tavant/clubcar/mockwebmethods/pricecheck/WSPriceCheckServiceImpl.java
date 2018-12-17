package com.tavant.clubcar.mockwebmethods.pricecheck;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import tavant.globalsync.pricefetchrequest.PriceCheckRequestTypeDTO;
import tavant.globalsync.pricefetchrequest.RequestLineItemTypeDTO;
import tavant.globalsync.pricefetchresponse.CodeDocumentDTO;
import tavant.globalsync.pricefetchresponse.ListPriceTypeDTO;
import tavant.globalsync.pricefetchresponse.MaterialCostTypeDTO;
import tavant.globalsync.pricefetchresponse.PriceCheckResponseTypeDTO;
import tavant.globalsync.pricefetchresponse.PriceFetchResponseDocumentDTO;
import tavant.globalsync.pricefetchresponse.PriceFetchResponseDocumentDTO.PriceFetchResponse;
import tavant.globalsync.pricefetchresponse.ResponseLineItemTypeDTO;
import tavant.globalsync.pricefetchresponse.StandardCostTypeDTO;
import tavant.globalsync.pricefetchresponse.StatusTypeDTO;

import com.tavant.clubcar.mockwebmethods.transformers.PriceCheckRequestTransformer;

public class WSPriceCheckServiceImpl implements WSPriceCheckService {

	private static final Logger log = Logger
			.getLogger(WSPriceCheckServiceImpl.class);

	public String checkPrice(String bod) {
		log.info("Inside WSPriceCheckServiceImpl, recieved BOD " + bod);
		PriceCheckRequestTypeDTO requestDTO = new PriceCheckRequestTransformer()
				.transform(bod);
		PriceFetchResponse priceFetchResponse = createResponse(requestDTO);
        PriceFetchResponseDocumentDTO responseDoc = PriceFetchResponseDocumentDTO.Factory
				.newInstance();
		responseDoc.setPriceFetchResponse(priceFetchResponse);
		String response = responseDoc.xmlText(createXMLOptions());
		log.info("Inside WSPriceCheckServiceImpl, sending response BOD"
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

	private PriceFetchResponse createResponse(
			PriceCheckRequestTypeDTO requestDTO) {

		PriceFetchResponse priceFetchResponse = PriceFetchResponse.Factory
				.newInstance();
        priceFetchResponse.setPriceCheckResponse(PriceCheckResponseTypeDTO.Factory.newInstance());
		StatusTypeDTO status = StatusTypeDTO.Factory.newInstance();
		status.setCode(CodeDocumentDTO.Code.SUCCESS);
		status.setErrorMessage("");

		RequestLineItemTypeDTO[] lineItems = requestDTO
				.getReplacedParts().getLineItemArray();
        ResponseLineItemTypeDTO[] responseLineItems = new ResponseLineItemTypeDTO[lineItems.length];

		for (int i = 0; i < lineItems.length; i++) {
            ResponseLineItemTypeDTO responseLineItem = ResponseLineItemTypeDTO.Factory
					.newInstance();
			responseLineItem.setPartNo(lineItems[i].getPartNumber());
			responseLineItem.setQuantity(lineItems[i].getQuantity());
			responseLineItem.setListPrice(createListPrice());
			responseLineItem.setMaterialCost(createMaterialCost());
			responseLineItem.setStandardCost(createStandardCost());

			responseLineItems[i] = responseLineItem;

		}

		priceFetchResponse.getPriceCheckResponse().setLineItemArray(responseLineItems);
		return priceFetchResponse;

	}

	private ListPriceTypeDTO createListPrice() {
		ListPriceTypeDTO listPrice = ListPriceTypeDTO.Factory.newInstance();
		listPrice.setCurrencyCode("USD");
		BigDecimal listPriceBD = new BigDecimal(100.55);
		listPriceBD.setScale(2,BigDecimal.ROUND_HALF_UP);
		listPrice.setBigDecimalValue(listPriceBD);
		return listPrice;
	}


	private MaterialCostTypeDTO createMaterialCost() {
		MaterialCostTypeDTO materialCost = MaterialCostTypeDTO.Factory
				.newInstance();
		BigDecimal materialCostBD = new BigDecimal(75.55);
		materialCostBD.setScale(2, BigDecimal.ROUND_HALF_UP);
		materialCost.setBigDecimalValue(materialCostBD);
		materialCost.setCurrencyCode("USD");
		return materialCost;
	}

	private StandardCostTypeDTO createStandardCost() {
		StandardCostTypeDTO standardCost = StandardCostTypeDTO.Factory
				.newInstance();
		BigDecimal standardCostBD = new BigDecimal(50.55);
		standardCostBD.setScale(2, BigDecimal.ROUND_HALF_UP);
		standardCost.setBigDecimalValue(standardCostBD);
		standardCost.setCurrencyCode("USD");
		return standardCost;
	}

}
