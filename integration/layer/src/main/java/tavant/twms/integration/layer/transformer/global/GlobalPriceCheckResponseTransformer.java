package tavant.twms.integration.layer.transformer.global;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import tavant.globalsync.pricefetchresponse.PriceFetchResponseDocumentDTO;
import tavant.globalsync.pricefetchresponse.ResponseLineItemTypeDTO;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemUOMTypes;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplaced;
import tavant.twms.domain.supplier.ItemMapping;
import tavant.twms.domain.supplier.ItemMappingService;
import tavant.twms.external.PriceCheckItem;
import tavant.twms.external.PriceCheckResponse;
import tavant.twms.external.PriceCheckRequest;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import com.domainlanguage.money.Money;

public class GlobalPriceCheckResponseTransformer extends IntegrationConstants{

	
	private static Logger logger = Logger.getLogger(GlobalPriceCheckResponseTransformer.class.getName());
	
	/**
	 * Populate Price Response object from the web method price check response
	 * @param src
	 * @return
	 */
	public PriceCheckResponse transform(String src, Claim claim){
		PriceFetchResponseDocumentDTO priceCheckResponseTypeDTO = null;
		try {
			priceCheckResponseTypeDTO = PriceFetchResponseDocumentDTO.Factory.parse(src);
		} catch (XmlException e) {
			throw new RuntimeException(e);
		}
		if(logger.isInfoEnabled()){
			logger.info("GlobalPriceCheckResponseTransformer :: transform : Webmethods Response for PriceFetch " 
					+ src);
		}
		PriceCheckResponse priceCheckResponse = new PriceCheckResponse();
		priceCheckResponse.setStatusCode(priceCheckResponseTypeDTO.getPriceFetchResponse().getStatus()
											.getCode().toString());
		priceCheckResponse.setErrorMessage(priceCheckResponseTypeDTO.getPriceFetchResponse().getStatus()
											.getErrorMessage());
		if(ERROR.equalsIgnoreCase(priceCheckResponse.getStatusCode()) 
				|| FAILURE.equalsIgnoreCase(priceCheckResponse.getStatusCode())){
			// Send Price Response with Zero Price for error response
			logger.error("GlobalPriceCheckResponseTransformer :: transform : ERROR Response from Webmethods " +
					"for Price Fetch Request with error message " + priceCheckResponse.getErrorMessage() 
					+" Response XML  " + src);
			return transformErrorResponse(claim, false, true);
		}
		boolean isError = validate(priceCheckResponseTypeDTO);
		if(isError){
			logger.error("GlobalPriceCheckResponseTransformer :: transform : Validation Failed " +
					"Please refer response XML " + src);
			return transformErrorResponse(claim, false, true);
		}
		
		if(priceCheckResponseTypeDTO.getPriceFetchResponse().getPriceCheckResponse() != null 
				&& priceCheckResponseTypeDTO.getPriceFetchResponse().getPriceCheckResponse().getLineItemArray() != null){
			priceCheckResponse.setPriceCheckItemList(populateLineItemList(priceCheckResponseTypeDTO.getPriceFetchResponse().getPriceCheckResponse()
										.getLineItemArray(), claim.getCurrencyForCalculation()));
		}
		if(priceCheckResponseTypeDTO.getPriceFetchResponse().getCostCheckResponse() != null 
				&& priceCheckResponseTypeDTO.getPriceFetchResponse().getCostCheckResponse().getLineItemArray() != null){
			priceCheckResponse.setCostCheckItemList(populateLineItemList(priceCheckResponseTypeDTO.getPriceFetchResponse().getCostCheckResponse()
						.getLineItemArray(), claim.getCurrencyForCalculation()));
		}
		priceCheckResponse.setGlobalPriceCheckResponse(true);
		return priceCheckResponse;
		
	}

    public PriceCheckResponse transform(String src, PriceCheckRequest priceCheckRequest){
        PriceFetchResponseDocumentDTO priceCheckResponseTypeDTO = null;
        try {
            priceCheckResponseTypeDTO = PriceFetchResponseDocumentDTO.Factory.parse(src);
        } catch (XmlException e) {
            throw new RuntimeException(e);
        }
        if(logger.isInfoEnabled()){
            logger.info("GlobalPriceCheckResponseTransformer :: transform : Webmethods Response for PriceFetch "
                    + src);
        }
        PriceCheckResponse priceCheckResponse = new PriceCheckResponse();
        priceCheckResponse.setStatusCode(priceCheckResponseTypeDTO.getPriceFetchResponse().getStatus()
                                            .getCode().toString());
        priceCheckResponse.setErrorMessage(priceCheckResponseTypeDTO.getPriceFetchResponse().getStatus()
                                            .getErrorMessage());
        if(ERROR.equalsIgnoreCase(priceCheckResponse.getStatusCode())
                || FAILURE.equalsIgnoreCase(priceCheckResponse.getStatusCode())){
            // Send Price Response with Zero Price for error response
            logger.error("GlobalPriceCheckResponseTransformer :: transform : ERROR Response from Webmethods " +
                    "for Price Fetch Request with error message " + priceCheckResponse.getErrorMessage()
                    +" Response XML  " + src);
            return transformErrorResponse(priceCheckRequest, false, true);
        }
        boolean isError = validate(priceCheckResponseTypeDTO);
        if(isError){
            logger.error("GlobalPriceCheckResponseTransformer :: transform : Validation Failed " +
                    "Please refer response XML " + src);
            return transformErrorResponse(priceCheckRequest, false, true);
        }

        if(priceCheckResponseTypeDTO.getPriceFetchResponse().getPriceCheckResponse() != null 
                && priceCheckResponseTypeDTO.getPriceFetchResponse().getPriceCheckResponse().getLineItemArray() != null){
            priceCheckResponse.setPriceCheckItemList(populateLineItemList(priceCheckResponseTypeDTO.getPriceFetchResponse().getPriceCheckResponse()
                                        .getLineItemArray(), Currency.getInstance(priceCheckRequest.getCurrencyCode())));
        }
        if(priceCheckResponseTypeDTO.getPriceFetchResponse().getCostCheckResponse() != null
                && priceCheckResponseTypeDTO.getPriceFetchResponse().getCostCheckResponse().getLineItemArray() != null){
            priceCheckResponse.setCostCheckItemList(populateLineItemList(priceCheckResponseTypeDTO.getPriceFetchResponse().getCostCheckResponse()
                        .getLineItemArray(), Currency.getInstance(priceCheckRequest.getCurrencyCode())));
        }
        priceCheckResponse.setGlobalPriceCheckResponse(true);
        return priceCheckResponse;

    }

    private boolean validate(
			PriceFetchResponseDocumentDTO priceCheckResponseTypeDTO) {
		boolean isError = false;
		if(priceCheckResponseTypeDTO.getPriceFetchResponse().getPriceCheckResponse() != null 
				&& priceCheckResponseTypeDTO.getPriceFetchResponse().getPriceCheckResponse().getLineItemArray() != null){
			ResponseLineItemTypeDTO[] responseLineItemTypeDTO = priceCheckResponseTypeDTO.getPriceFetchResponse().getPriceCheckResponse().getLineItemArray();
			for (int i = 0; i < responseLineItemTypeDTO.length; i++) {
				if(StringUtils.isBlank(responseLineItemTypeDTO[i].getStatus().getCode().toString())){
					isError = true;
				}
				if(StringUtils.isBlank(responseLineItemTypeDTO[i].getPartNo())){
					isError = true;
				}
			}
		}
			
		if(priceCheckResponseTypeDTO.getPriceFetchResponse().getCostCheckResponse() != null 
				&& priceCheckResponseTypeDTO.getPriceFetchResponse().getCostCheckResponse().getLineItemArray() != null){
			ResponseLineItemTypeDTO[] responseLineItemTypeDTO = priceCheckResponseTypeDTO.getPriceFetchResponse().getCostCheckResponse().getLineItemArray();
			for (int i = 0; i < responseLineItemTypeDTO.length; i++) {
				if(StringUtils.isBlank(responseLineItemTypeDTO[i].getStatus().getCode().toString())){
					isError = true;
				}
				if(StringUtils.isBlank(responseLineItemTypeDTO[i].getPartNo())){
					isError = true;
				}
			}
		
		}
		return isError;
	}

	/**
	 * Populate the Line items
	 * @param responseLineItemTypeDTO
	 * @return
	 */
	private List<PriceCheckItem> populateLineItemList(ResponseLineItemTypeDTO[] responseLineItemTypeDTO,
                                                      Currency currency){
		
		List<PriceCheckItem> lineItemList = new ArrayList<PriceCheckItem>();
		for (int i = 0; i < responseLineItemTypeDTO.length; i++) {
			PriceCheckItem checkItem = new PriceCheckItem();
			checkItem.setStatusCode(responseLineItemTypeDTO[i].getStatus().getCode().toString());
			checkItem.setErrorMessage(responseLineItemTypeDTO[i].getStatus().getErrorMessage());
			checkItem.setPartNumber(responseLineItemTypeDTO[i].getPartNo());
			checkItem.setQuantity(responseLineItemTypeDTO[i].getQuantity());
			//FOR NMHG UOM attribute has been removed
			if(StringUtils.isNotBlank(responseLineItemTypeDTO[i].getUOM())){
				String uom = responseLineItemTypeDTO[i].getUOM().toUpperCase();
				if(uom.equals("SQUARE METER")){
					uom ="SQUARE_METER";
				}else if(uom.equals("SQUARE FEET")){
					uom="SQUARE_FEET";
				}else if(uom.equals("PACK OF 2")){
					uom="PACK_OF_2";
				}else if(uom.equals("PACK OF 4")){
					uom="PACK_OF_4";
				}else if(uom.equals("PACK OF 5")){
					uom="PACK_OF_5";
				}else if(uom.equals("PACK OF 6")){
					uom="PACK_OF_6";
				}else if(uom.equals("PACK OF 8")){
					uom="PACK_OF_8";
				}else if(uom.equals("PACK OF 10")){
					uom="PACK_OF_10";
				}else if(uom.equals("PACK OF 12")){
					uom="PACK_OF_12";
				}else if(uom.equals("PACK OF 25")){
					uom="PACK_OF_25";
				}else if(uom.equals("PACK OF 50")){
					uom="PACK_OF_50";
				}
				checkItem.setUom(ItemUOMTypes.valueOf(ItemUOMTypes.class, uom));	
			}	else{
				checkItem.setUom(ItemUOMTypes.EACH);
			}
			
			if (ERROR.equalsIgnoreCase(checkItem.getStatusCode())
					|| FAILURE.equalsIgnoreCase(checkItem.getStatusCode())) {
				// in case of error response for individual item
				logger
						.error("GlobalPriceCheckResponseTransformer :: populateLineItemList : ERROR Response from "
								+ "Webmethods for part number "
								+ checkItem.getPartNumber()
								+ " with error message "
								+ checkItem.getErrorMessage());
				checkItem.setListPrice(Money.valueOf(0.00, currency));
				checkItem.setMaterialCost(Money.valueOf(0.00, currency));
				checkItem.setAdjustedListPrice(Money.valueOf(0.00,currency));
				checkItem.setStandardCost(Money.valueOf(0.00, currency));
			}

			else {

				if (responseLineItemTypeDTO[i].getListPrice() != null) {
					Currency listPriceCurrency = getCurrency(responseLineItemTypeDTO[i].getListPrice().xmlText());
					double listPriceDoubleVal = responseLineItemTypeDTO[i].getListPrice().getBigDecimalValue().doubleValue();
					Money listPrice = Money.valueOf(listPriceDoubleVal,listPriceCurrency);
					checkItem.setListPrice(listPrice);
				}

				if (responseLineItemTypeDTO[i].getMaterialCost() != null) {
					Currency materialCostCurrency = getCurrency(responseLineItemTypeDTO[i].getMaterialCost().xmlText());
					double materialCostDoubleVal = responseLineItemTypeDTO[i].getMaterialCost().getBigDecimalValue().doubleValue();
					Money materialCost = Money.valueOf(materialCostDoubleVal,materialCostCurrency);
					checkItem.setMaterialCost(materialCost);
				}

				if (responseLineItemTypeDTO[i].getAdjustedListPrice() != null) {
					Currency adjustedListPriceCurrency = getCurrency(responseLineItemTypeDTO[i].getAdjustedListPrice().xmlText());
					double adjustedListDoubleVal = responseLineItemTypeDTO[i].getAdjustedListPrice().getBigDecimalValue().doubleValue();
					Money adjustedListPrice = Money.valueOf(adjustedListDoubleVal, adjustedListPriceCurrency);
					checkItem.setAdjustedListPrice(adjustedListPrice);
				}

				if (responseLineItemTypeDTO[i].getStandardCost() != null) {
					Currency standardCostCurrency = getCurrency(responseLineItemTypeDTO[i].getStandardCost().xmlText());
					double standardCostDoubleVal = responseLineItemTypeDTO[i].getStandardCost().getBigDecimalValue().doubleValue();
					Money standardCost = Money.valueOf(standardCostDoubleVal,standardCostCurrency);
					checkItem.setStandardCost(standardCost);
				}

			}
			lineItemList.add(checkItem);
		}
		return lineItemList;
	}
	
	/**
	 * Get Currency from price list element
	 * @param currency
	 * @return
	 */
	private Currency getCurrency(String currency){ 
		String currencyCode = getCurrencyCode(currency);
		Currency fromCurrency = Currency.getInstance(currencyCode);
		return fromCurrency;
	}
	
	/**
	 * Get Currency code from price list element
	 * @param currency
	 * @return
	 */
	private String getCurrencyCode(String currency) {
		String currencyCode = null;
		
		if(currency!=null){
			int index = StringUtils.indexOf(currency, "=");
			if(index>0){
				currencyCode = StringUtils.substring(currency, index+2,index+5);
			}
		}
		return currencyCode;
	}
	
	public PriceCheckResponse transformErrorResponse(Claim claim, boolean isMockResponse, boolean isGlobalPriceCheckResponse) {
		List<PartReplaced> itemList = claim.getServiceInformation().getServiceDetail().getPriceFetchedParts();
		List<PriceCheckItem> priceCheckItemList = new ArrayList<PriceCheckItem>();
		List<PriceCheckItem> costCheckItemList = new ArrayList<PriceCheckItem>();
		PriceCheckResponse priceCheckResponse= new PriceCheckResponse();
		for (PartReplaced partReplaced : itemList) {
			PriceCheckItem checkItem = null;
			PriceCheckItem costcheckItem = null;
			if(isMockResponse) {
				checkItem = populateItemWithMockPrice(claim,partReplaced, claim.getCurrencyForCalculation());
				costcheckItem = populateItemWithCostMockPrice(claim,partReplaced, claim.getCurrencyForCalculation());
			} else {
				checkItem = populateItemWithZeroPrice(claim,partReplaced, claim.getCurrencyForCalculation());
				costcheckItem = populateItemWithCostZeroPrice(claim,partReplaced, claim.getCurrencyForCalculation());
			}
			priceCheckItemList.add(checkItem);
			costCheckItemList.add(costcheckItem);
		}
		priceCheckResponse.setPriceCheckItemList(priceCheckItemList);
		priceCheckResponse.setCostCheckItemList(costCheckItemList);
		priceCheckResponse.setGlobalPriceCheckResponse(isGlobalPriceCheckResponse);
		return priceCheckResponse;
	}
	
    public PriceCheckResponse transformErrorResponse(PriceCheckRequest priceCheckRequest, boolean isMockResponse,
                                                     boolean isGlobalPriceCheckResponse) {
        List<PriceCheckItem> priceCheckRespItemList = new ArrayList<PriceCheckItem>();
        List<PriceCheckItem> costCheckRespItemList = new ArrayList<PriceCheckItem>();
        PriceCheckResponse priceCheckResponse= new PriceCheckResponse();
        for (PriceCheckItem priceCheckPart : priceCheckRequest.getPriceCheckItemList()) {
            PriceCheckItem priceCheckRespPart = null;
            if(isMockResponse){
                priceCheckRespPart = populateItemWithMockPrice(priceCheckPart,
                        Currency.getInstance(priceCheckRequest.getCurrencyCode()));//For Claims we have to populate StandardCost and Metearial cost using PriceFetchResponse
            } else {
                priceCheckRespPart = populateItemWithZeroPrice(priceCheckPart,
                        Currency.getInstance(priceCheckRequest.getCurrencyCode()));//For Claims we have to populate StandardCost and Metearial cost using PriceFetchResponse
            }
            priceCheckRespItemList.add(priceCheckRespPart);
        }
        
        for (PriceCheckItem costCheckPart : priceCheckRequest.getCostCheckItemList()) {
            PriceCheckItem costCheckRespPart = null;
            if(isMockResponse){
                costCheckRespPart = populateItemWithMockPrice(costCheckPart,
                        Currency.getInstance(priceCheckRequest.getCurrencyCode()));
            }else{
                costCheckRespPart = populateItemWithZeroPrice(costCheckPart,
                        Currency.getInstance(priceCheckRequest.getCurrencyCode()));
            }
            costCheckRespItemList.add(costCheckRespPart); 
        }
        priceCheckResponse.setPriceCheckItemList(priceCheckRespItemList);
       priceCheckResponse.setCostCheckItemList(costCheckRespItemList);
        priceCheckResponse.setGlobalPriceCheckResponse(isGlobalPriceCheckResponse);
        return priceCheckResponse;
    }

    /**
	 * Populate Price Check Item with Zero Price
	 * @param partReplaced
	 * @return
	 */
	private PriceCheckItem populateItemWithZeroPrice( Claim claim,
			PartReplaced partReplaced,Currency currency) {
		PriceCheckItem checkItem = new PriceCheckItem();
		checkItem.setListPrice(Money.valueOf(0.00, currency));
		checkItem.setMaterialCost(Money.valueOf(0.00, currency));
		if( claim.getServiceInformation().getServiceDetail().isOEMPartReplaced(partReplaced)) {
			checkItem.setPartNumber(((OEMPartReplaced)partReplaced).getItemReference().getReferredItem().getNumber());
			checkItem.setUom(((OEMPartReplaced)partReplaced).getItemReference().getReferredItem().getUom());
		} else {
			checkItem.setPartNumber(((InstalledParts)partReplaced).getItem().getNumber());
			//checkItem.setUom() --- For the time being UOM is disabled for the Hussmann Installed Parts.
		}
		checkItem.setQuantity(partReplaced.getNumberOfUnits());
		checkItem.setStandardCost(Money.valueOf(0.00, currency));
		checkItem.setAdjustedListPrice(Money.valueOf(0.00, currency));
		
		return checkItem;
	}
	
	private PriceCheckItem populateItemWithMockPrice( Claim claim,
			PartReplaced partReplaced, Currency currency) {
		PriceCheckItem checkItem = new PriceCheckItem();
		checkItem.setListPrice(Money.valueOf(50.55, currency));
		checkItem.setMaterialCost(Money.valueOf(30.55, currency));
		if( claim.getServiceInformation().getServiceDetail().isOEMPartReplaced(partReplaced)) {
			checkItem.setPartNumber(((OEMPartReplaced)partReplaced).getItemReference().getReferredItem().getNumber());
			checkItem.setUom(((OEMPartReplaced)partReplaced).getItemReference().getReferredItem().getUom());
		} else {
			checkItem.setPartNumber(((InstalledParts)partReplaced).getItem().getNumber());
			//checkItem.setUom() --- For the time being UOM is disabled for the Hussmann Installed Parts.
		}
		
		checkItem.setQuantity(partReplaced.getNumberOfUnits());
		checkItem.setStandardCost(Money.valueOf(100.55, currency));
		checkItem.setAdjustedListPrice(Money.valueOf(80.55, currency));
		
		return checkItem;
	}

    private PriceCheckItem populateItemWithZeroPrice(PriceCheckItem checkItem, Currency currency ) {
		    checkItem.setMaterialCost(Money.valueOf(0.00, currency));
            checkItem.setStandardCost(Money.valueOf(0.00, currency));
            checkItem.setListPrice(Money.valueOf(0.00, currency));
            checkItem.setAdjustedListPrice(Money.valueOf(0.00, currency));
		return checkItem;
	}

    private PriceCheckItem populateItemWithMockPrice(PriceCheckItem checkItem, Currency currency ) {
		    checkItem.setMaterialCost(Money.valueOf(30.55, currency).times(checkItem.getQuantity()));
            checkItem.setStandardCost(Money.valueOf(50.55, currency).times(checkItem.getQuantity()));
            checkItem.setListPrice(Money.valueOf(90.55, currency).times(checkItem.getQuantity()));
            checkItem.setAdjustedListPrice(Money.valueOf(70.55, currency).times(checkItem.getQuantity()));
      
		return checkItem;
	}


    private PriceCheckItem populateItemWithCostMockPrice( Claim claim, PartReplaced partReplaced,
											Currency currency) {
		PriceCheckItem checkItem = new PriceCheckItem();
		checkItem.setListPrice(Money.valueOf(50.55, currency));
		checkItem.setMaterialCost(Money.valueOf(30.55, currency));
		Item causalItem = claim.getServiceInformation().getCausalPart();
		ItemMapping itemMappings = null;
		if(causalItem != null && claim.getServiceInformation().getContract() != null && 
				claim.getServiceInformation().getContract().getSupplier() != null){
			
			itemMappings = itemMappingService.findItemMappingForOEMItem(causalItem,
					claim.getServiceInformation().getContract().getSupplier(), 
					null);
		}
		if(itemMappings != null && itemMappings.getToItem() != null){
			if( claim.getServiceInformation().getServiceDetail().isOEMPartReplaced(partReplaced)) {
				checkItem.setPartNumber(itemMappings.getToItem().getNumber());
				checkItem.setUom(((OEMPartReplaced)partReplaced).getItemReference().getReferredItem().getUom());
			} else {
				checkItem.setPartNumber(itemMappings.getToItem().getNumber());
				//checkItem.setUom() --- For the time being UOM is disabled for the Hussmann Installed Parts.
			}
		}
		checkItem.setQuantity(partReplaced.getNumberOfUnits());
		checkItem.setStandardCost(Money.valueOf(100.55, currency));
		checkItem.setAdjustedListPrice(Money.valueOf(80.55, currency));
		
		return checkItem;
	}
	private PriceCheckItem populateItemWithCostZeroPrice( Claim claim, PartReplaced partReplaced, 
			Currency currency) {
		PriceCheckItem checkItem = new PriceCheckItem();
		checkItem.setListPrice(Money.valueOf(0.0, currency));
		checkItem.setMaterialCost(Money.valueOf(0.0, currency));
		Item causalItem = claim.getServiceInformation().getCausalPart();
		ItemMapping itemMappings = null;
		if(causalItem != null && claim.getServiceInformation().getContract() != null && 
				claim.getServiceInformation().getContract().getSupplier() != null){
		
			itemMappings = itemMappingService.findItemMappingForOEMItem(causalItem,
					claim.getServiceInformation().getContract().getSupplier(),null);
		}
		if(itemMappings != null && itemMappings.getToItem() != null){
			if( claim.getServiceInformation().getServiceDetail().isOEMPartReplaced(partReplaced)) {
				checkItem.setPartNumber(itemMappings.getToItem().getNumber());
				checkItem.setUom(((OEMPartReplaced)partReplaced).getItemReference().getReferredItem().getUom());
			} else {
				checkItem.setPartNumber(itemMappings.getToItem().getNumber());
				//checkItem.setUom() --- For the time being UOM is disabled for the Hussmann Installed Parts.
			}
		}
		checkItem.setQuantity(partReplaced.getNumberOfUnits());
		checkItem.setStandardCost(Money.valueOf(0.0, currency));
		checkItem.setAdjustedListPrice(Money.valueOf(0.0, currency));
		
		return checkItem;
	}
	
	
	
	
	
	private ItemMappingService itemMappingService;

	public void setItemMappingService(ItemMappingService itemMappingService) {
		this.itemMappingService = itemMappingService;
	}
}
