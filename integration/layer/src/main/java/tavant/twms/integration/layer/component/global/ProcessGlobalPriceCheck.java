package tavant.twms.integration.layer.component.global;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.util.StringUtils;

import tavant.globalsync.pricefetchrequest.ApplicationAreaTypeDTO;
import tavant.globalsync.pricefetchrequest.CostCheckRequestTypeDTO;
import tavant.globalsync.pricefetchrequest.CurrencyCodeDocumentDTO.CurrencyCode;
import tavant.globalsync.pricefetchrequest.PriceCheckDataAreaTypeDTO;
import tavant.globalsync.pricefetchrequest.PriceCheckRequestTypeDTO;
import tavant.globalsync.pricefetchrequest.PriceFetchDocumentDTO;
import tavant.globalsync.pricefetchrequest.PriceFetchDocumentDTO.PriceFetch;
import tavant.globalsync.pricefetchrequest.ReplacedPartsTypeDTO;
import tavant.globalsync.pricefetchrequest.RequestLineItemTypeDTO;
import tavant.globalsync.pricefetchrequest.SenderTypeDTO;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemUOMTypes;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimCurrencyConversionAdvice;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplaced;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.supplier.ItemMapping;
import tavant.twms.domain.supplier.ItemMappingService;
import tavant.twms.external.PriceCheckItem;
import tavant.twms.external.PriceCheckRequest;
import tavant.twms.integration.layer.IntegrationPropertiesBean;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.util.CalendarUtil;


public class ProcessGlobalPriceCheck extends IntegrationConstants {

	private static Logger logger = Logger.getLogger(ProcessGlobalPriceCheck.class.getName());
	
	IntegrationPropertiesBean integrationPropertiesBean;
	private ItemMappingService itemMappingService;
    private ConfigParamService configParamService;
    private ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice;

    public void setIntegrationPropertiesBean(
			IntegrationPropertiesBean integrationPropertiesBean) {
		this.integrationPropertiesBean = integrationPropertiesBean;
	}
	
	public void setItemMappingService(ItemMappingService itemMappingService) {
		this.itemMappingService = itemMappingService;
	}

	public String syncGlobalPriceCheck(Claim claim){
		return createBodFromObject(claim);
	}
	
    public String syncGlobalPriceCheck(PriceCheckRequest priceCheckRequest,Claim claim){
        return createBodFromObject(priceCheckRequest,claim);
    }

	private String createBodFromObject(Claim claim){

		PriceFetchDocumentDTO priceFetchDocumentDTO = PriceFetchDocumentDTO.Factory.newInstance();
		PriceFetch priceFetch = PriceFetch.Factory.newInstance();
		// Create Application Area
		if(logger.isInfoEnabled()){
			logger.info("ProcessGlobalPriceCheck :: createBodFromObject :: create Application area " 
								+ claim.getClaimNumber());
		}
		ApplicationAreaTypeDTO applicationAreaTypeDTO = createApplicationArea(claim);
		priceFetch.setApplicationArea(applicationAreaTypeDTO);
		
		// Create Data Area
		if(logger.isInfoEnabled()){
			logger.info("ProcessGlobalPriceCheck :: createBodFromObject :: create Data area " 
								+ claim.getClaimNumber());
		}
		PriceCheckDataAreaTypeDTO priceCheckDataAreaTypeDTO = populateDataArea(claim);
		priceFetch.setDataArea(priceCheckDataAreaTypeDTO);
		
		priceFetchDocumentDTO.setPriceFetch(priceFetch);
		String xml = priceFetchDocumentDTO.xmlText(createXMLOptions());
		if(logger.isInfoEnabled())
		{
        		logger.info("ProcessGlobalPriceCheck :: End createBodFromObject , The XML formed is :    ");
        		logger.info(xml);
		}
		return xml;
	}

    private String createBodFromObject(PriceCheckRequest priceCheckRequest,Claim claim){

        PriceFetchDocumentDTO priceFetchDocumentDTO = PriceFetchDocumentDTO.Factory.newInstance();
        PriceFetch priceFetch = PriceFetch.Factory.newInstance();
        // Create Application Area
        if(logger.isInfoEnabled()){
            logger.info("ProcessGlobalPriceCheck :: createBodFromObject :: createApplicationArea ::"
                                + priceCheckRequest.getUniqueId());
        }
        ApplicationAreaTypeDTO applicationAreaTypeDTO = createApplicationArea(priceCheckRequest.getBusinessUnitName());
        priceFetch.setApplicationArea(applicationAreaTypeDTO);

        // Create Data Area
        if(logger.isInfoEnabled()){
            logger.info("ProcessGlobalPriceCheck :: createBodFromObject :: createDataArea ::"
                                + priceCheckRequest.getUniqueId());
        }
        PriceCheckDataAreaTypeDTO priceCheckDataAreaTypeDTO = populateDataArea(priceCheckRequest,claim);
        priceFetch.setDataArea(priceCheckDataAreaTypeDTO);

        priceFetchDocumentDTO.setPriceFetch(priceFetch);
        String xml = priceFetchDocumentDTO.xmlText(createXMLOptions());
        Logger priceCheckLogger = Logger.getLogger(integrationPropertiesBean.getBodIdIdForPriceFetch());
        if(priceCheckLogger.isInfoEnabled())
        {
                priceCheckLogger.info("ProcessGlobalPriceCheck :: End createBodFromObject , The XML formed is :    ");
                priceCheckLogger.info(xml);
        }
        return xml;
    }


    /**
	 * Populate the application area for Price Fetch request
	 * @param claim
	 * @return
	 */
	private ApplicationAreaTypeDTO createApplicationArea(Claim claim){
		ApplicationAreaTypeDTO applicationAreaTypeDTO = ApplicationAreaTypeDTO.Factory.newInstance();
		SenderTypeDTO senderTypeDTO = SenderTypeDTO.Factory.newInstance();
		populateTaskLogicalId(senderTypeDTO, claim.getBusinessUnitInfo().getName());
		senderTypeDTO.setReferenceId(getUniqueReferenceId().toString());
		applicationAreaTypeDTO.setSender(senderTypeDTO);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone(IntegrationConstants.GMT));
		applicationAreaTypeDTO.setCreationDateTime(calendar);
		// Interface Number is used between Web methods and ERP interaction
		// It doesn't drive any logic in our system, but a empty tag need to be sent 
		// to web methods so that they can populate it and send to ERP
		applicationAreaTypeDTO.setInterfaceNumber(IntegrationConstants.EMPTY_STRING);
		// this field doesn't have any Business logic in the system 
		applicationAreaTypeDTO.setBODId(integrationPropertiesBean.getBodIdIdForPriceFetch());
		return applicationAreaTypeDTO;
	}
	
    private ApplicationAreaTypeDTO createApplicationArea(String businessUnitName){
        ApplicationAreaTypeDTO applicationAreaTypeDTO = ApplicationAreaTypeDTO.Factory.newInstance();
        SenderTypeDTO senderTypeDTO = SenderTypeDTO.Factory.newInstance();
        populateTaskLogicalId(senderTypeDTO, businessUnitName);
        senderTypeDTO.setReferenceId(getUniqueReferenceId().toString());
        applicationAreaTypeDTO.setSender(senderTypeDTO);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(IntegrationConstants.GMT));
        applicationAreaTypeDTO.setCreationDateTime(calendar);
        // Interface Number is used between Web methods and ERP interaction
        // It doesn't drive any logic in our system, but a empty tag need to be sent
        // to web methods so that they can populate it and send to ERP
        applicationAreaTypeDTO.setInterfaceNumber(IntegrationConstants.EMPTY_STRING);
        // this field doesn't have any Business logic in the system
        applicationAreaTypeDTO.setBODId(integrationPropertiesBean.getBodIdIdForPriceFetch());
        return applicationAreaTypeDTO;
    }


	/**
	 * Generate a unique reference number
	 * @return
	 */
	public Long getUniqueReferenceId(){
		long current= System.currentTimeMillis();
		return new Long(current++);
    }
	
	/**
	 * Populate Data Area for price fetch request
	 * @param claim
	 * @return
	 */
	private PriceCheckDataAreaTypeDTO populateDataArea(Claim claim){
		if(logger.isInfoEnabled()){
			logger.info("ProcessGlobalPriceCheck :: Start populateDataArea :: populate Price Check Data Area " 
								+ claim.getClaimNumber());
		}
		
		PriceCheckDataAreaTypeDTO priceCheckDataAreaTypeDTO = PriceCheckDataAreaTypeDTO.Factory.newInstance();
		priceCheckDataAreaTypeDTO.setBUName(claim.getBusinessUnitInfo().getName());
		// Create Price Check Request for claim
		PriceCheckRequestTypeDTO priceCheckRequestTypeDTO = populatePriceCheckRequest(claim);
		priceCheckDataAreaTypeDTO.setPriceCheckRequest(priceCheckRequestTypeDTO);
		
		//Create Cost Check request for supplier recovery
		if(null != claim.getServiceInformation().getContract() 
				&& claim.getServiceInformation().getContract().getSupplier() != null ){
			CostCheckRequestTypeDTO costCheckRequestTypeDTO = populateCostCheckRequest(claim);
			priceCheckDataAreaTypeDTO.setCostCheckRequest(costCheckRequestTypeDTO);
		}
		
		
		if(logger.isInfoEnabled()){
			logger.info("ProcessGlobalPriceCheck :: End populateDataArea :: populate Price Check Data Area :: "
								+ claim.getClaimNumber());
		}
		return priceCheckDataAreaTypeDTO;
	}

    private PriceCheckDataAreaTypeDTO populateDataArea(PriceCheckRequest priceCheckRequest,Claim claim){
        if(logger.isInfoEnabled()){
            logger.info("ProcessGlobalPriceCheck :: Start populateDataArea :: populatePriceCheckDataArea :: "
                                + priceCheckRequest.getUniqueId());
        }

        PriceCheckDataAreaTypeDTO priceCheckDataAreaTypeDTO = PriceCheckDataAreaTypeDTO.Factory.newInstance();
        priceCheckDataAreaTypeDTO.setBUName(priceCheckRequest.getBusinessUnitName());
        priceCheckDataAreaTypeDTO.setClaimType(priceCheckRequest.getWarrantyType());
        // Create Price Check Request for claim if sent in PriceCheckRequest
        if (null != priceCheckRequest.getPriceCheckItemList() &&
                priceCheckRequest.getPriceCheckItemList().size() > 0) {
            priceCheckDataAreaTypeDTO.setPriceCheckRequest(populatePriceCheckRequest(priceCheckRequest, claim));
            priceCheckDataAreaTypeDTO.setClaimType(AdminConstants.WARRANTYCLAIM);

        }
        

        //Create Cost Check request for cost on warranty and recovery claim if sent in PriceCheckRequest
        if(null != priceCheckRequest.getCostCheckItemList() &&
                priceCheckRequest.getCostCheckItemList().size() > 0) {
            priceCheckDataAreaTypeDTO.setCostCheckRequest(populateCostCheckRequest(priceCheckRequest, claim));
            priceCheckDataAreaTypeDTO.setClaimType(AdminConstants.RECOVERYCLAIM);
        }


        if(logger.isInfoEnabled()){
            logger.info("ProcessGlobalPriceCheck :: End populateDataArea :: populate Price Check Data Area "
                                +  priceCheckRequest.getUniqueId());
        }
        return priceCheckDataAreaTypeDTO;
    }

  
    /**
	 * Populate price check request for replaced parts
	 * @param claim
	 * @return
	 */
	private PriceCheckRequestTypeDTO populatePriceCheckRequest(Claim claim){
		if(logger.isInfoEnabled()){
			logger.info("ProcessGlobalPriceCheck :: start populatePriceCheckRequest :: populate Prick Check " 
								+ claim.getClaimNumber());
		}
		PriceCheckRequestTypeDTO priceCheckRequestTypeDTO = PriceCheckRequestTypeDTO.Factory.newInstance();
		priceCheckRequestTypeDTO.setDealerNo(getDealerNumber(claim.getForDealerShip().getDealerNumber()));
		priceCheckRequestTypeDTO.setCurrencyCode(CurrencyCode.Enum.forString(claimCurrencyConversionAdvice.getCurrencyForERPInteractions(claim)));
		if(null != claim.getClaimedItems().get(0).getApplicablePolicy()){
			priceCheckRequestTypeDTO.setWarrantyType(claim.getClaimedItems().get(0).getApplicablePolicy()
													.getWarrantyType().toString());
		}
		priceCheckRequestTypeDTO.setDateOfRepair(CalendarUtil.convertToJavaCalendar(claim.getRepairDate()));
		ReplacedPartsTypeDTO replacedPartsTypeDTO = populateReplacedParts(claim);
		priceCheckRequestTypeDTO.setReplacedParts(replacedPartsTypeDTO);
		if(logger.isInfoEnabled()){
			logger.info("ProcessGlobalPriceCheck :: End populatePriceCheckRequest :: populate Prick Check " 
								+ claim.getClaimNumber());
		}
		return priceCheckRequestTypeDTO;
	}
	private String getDealerNumber(String dealerNumberString) {
		if (dealerNumberString != null) {
			String dealerNumbers = dealerNumberString.length() <= 6
					? dealerNumberString
					: dealerNumberString
							.substring(dealerNumberString.length() - 6);
			return dealerNumbers;
		}
		return EMPTY_STRING;
	}

    private PriceCheckRequestTypeDTO populatePriceCheckRequest(PriceCheckRequest priceCheckRequest,Claim claim){
        if(logger.isInfoEnabled()){
            logger.info("ProcessGlobalPriceCheck :: start populatePriceCheckRequest :: populatePrickCheck :: "
                                + priceCheckRequest.getUniqueId());
        }
        PriceCheckRequestTypeDTO priceCheckRequestTypeDTO = PriceCheckRequestTypeDTO.Factory.newInstance();
        priceCheckRequestTypeDTO.setDealerNo(priceCheckRequest.getDealerNumber());
        priceCheckRequestTypeDTO.setCurrencyCode(CurrencyCode.Enum.forString(priceCheckRequest.getCurrencyCode()));
        
        priceCheckRequestTypeDTO.setDateOfRepair(CalendarUtil.convertToJavaCalendar(priceCheckRequest.getDateOfRepair()));
        priceCheckRequestTypeDTO.setReplacedParts(populatePriceLineItems(
                priceCheckRequest.getPriceCheckItemList(), priceCheckRequest.getBusinessUnitName(),claim));
       
        if(logger.isInfoEnabled()){
            logger.info("ProcessGlobalPriceCheck :: End populatePriceCheckRequest :: populatePrickCheck :: "
                                + priceCheckRequest.getUniqueId());
        }
        return priceCheckRequestTypeDTO;
    }



    /**
	 * Populate Cost check request for replaced parts
	 * @param claim
	 * @return
	 */
	private CostCheckRequestTypeDTO populateCostCheckRequest(Claim claim){
		if(logger.isInfoEnabled()){
			logger.info("ProcessGlobalPriceCheck :: start populateCostCheckRequest :: populate Cost price " 
								+ claim.getClaimNumber());
		}
		CostCheckRequestTypeDTO costCheckRequestTypeDTO = CostCheckRequestTypeDTO.Factory.newInstance();
		
		if(null != claim.getClaimedItems().get(0).getApplicablePolicy()){
			costCheckRequestTypeDTO.setWarrantyType(claim.getClaimedItems().get(0).getApplicablePolicy()
													.getWarrantyType().toString());
		}
		costCheckRequestTypeDTO.setDateOfRepair(CalendarUtil.convertToJavaCalendar(claim.getRepairDate()));
		if(claim.getServicingLocation().getSiteNumber()!=null){
		costCheckRequestTypeDTO.setDealerSiteNumber(claim.getServicingLocation().getSiteNumber());
		}
		ReplacedPartsTypeDTO replacedPartsTypeDTO = populateSupplierReplacedParts(claim);
		costCheckRequestTypeDTO.setReplacedParts(replacedPartsTypeDTO);
		if(logger.isInfoEnabled()){
			logger.info("ProcessGlobalPriceCheck :: end populateCostCheckRequest :: populate Cost price " 
								+ claim.getClaimNumber());
		}
		return costCheckRequestTypeDTO;
	}
	
    private CostCheckRequestTypeDTO populateCostCheckRequest(PriceCheckRequest priceCheckRequest,Claim claim){
        if(logger.isInfoEnabled()){
            logger.info("ProcessGlobalPriceCheck :: start populateCostCheckRequest :: populateCostPrice :: "
                                + priceCheckRequest.getUniqueId());
        }
        CostCheckRequestTypeDTO costCheckRequestTypeDTO = CostCheckRequestTypeDTO.Factory.newInstance();
        costCheckRequestTypeDTO.setDateOfRepair(CalendarUtil.convertToJavaCalendar(priceCheckRequest.getDateOfRepair()));
        costCheckRequestTypeDTO.setReplacedParts(populateCostLineItems(
                priceCheckRequest.getCostCheckItemList(), priceCheckRequest.getBusinessUnitName(),claim));
        if(logger.isInfoEnabled()){
            logger.info("ProcessGlobalPriceCheck :: end populateCostCheckRequest :: populateCostPrice :: "
                                + priceCheckRequest.getUniqueId());
        }
        return costCheckRequestTypeDTO;
    }

    //Do not use this method, does not conform to global cost fetch
    //instead use populateCostLineItems(List<PriceCheckItem>, String buName)
    private ReplacedPartsTypeDTO populateSupplierReplacedParts(Claim claim){
		
		if(logger.isInfoEnabled()){
			logger.info("ProcessGlobalPriceCheck :: Start populateSupplierReplacedParts :: populate Supplier line items " 
								+ claim.getClaimNumber());
		}
		ReplacedPartsTypeDTO replacedPartsTypeDTO = ReplacedPartsTypeDTO.Factory.newInstance();
        List<OEMPartReplaced> replacedParts = claim.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced();
        Item causalItem = claim.getServiceInformation().getCausalPart();
		// currently we are not using the Build Date check while fetch item mapping from DB
		ItemMapping itemMappings = itemMappingService.findItemMappingForOEMItem(causalItem,
				claim.getServiceInformation().getContract().getSupplier(), 
				null);
		if(itemMappings != null && itemMappings.getToItem() != null && itemMappings.getFromItem() != null){
			// There will always be "ONE" supplier item for cost price fetch
			RequestLineItemTypeDTO[] lineItems = new RequestLineItemTypeDTO[1];
			RequestLineItemTypeDTO lineItemTypeDTO = RequestLineItemTypeDTO.Factory.newInstance();
            lineItemTypeDTO.setPartNumber(itemMappings.getFromItem().getNumber());
            lineItemTypeDTO.setQuantity(1);
            lineItemTypeDTO.setUOM(StringUtils.hasText(itemMappings.getFromItem().getUom().getType())
                    ? itemMappings.getFromItem().getUom().getType()
                            :ItemUOMTypes.EACH.getType());
            lineItemTypeDTO.setDivisionCode(claim.getBusinessUnitInfo().getName());
			lineItems[0] = lineItemTypeDTO;
			replacedPartsTypeDTO.setLineItemArray(lineItems);
		}
		
		return replacedPartsTypeDTO;
	}
    


	private ReplacedPartsTypeDTO populateCostLineItems(List<PriceCheckItem> costCheckItems,
                                                       String businessUnitName,Claim claim){

		if(logger.isInfoEnabled()){
			logger.info("ProcessGlobalPriceCheck :: Start populateSupplierReplacedParts :: populateCostLineItems");
		}
		ReplacedPartsTypeDTO replacedPartsTypeDTO = ReplacedPartsTypeDTO.Factory.newInstance();
        RequestLineItemTypeDTO[] lineItems = new RequestLineItemTypeDTO[costCheckItems.size()];
        int i = 0;
        for (PriceCheckItem costCheckItem : costCheckItems) {
            RequestLineItemTypeDTO lineItemTypeDTO = populateBUSpecificDataForLineItems(
                    costCheckItem, businessUnitName);
            
            lineItemTypeDTO.setQuantity(costCheckItem.getQuantity());
            if (StringUtils.hasText(costCheckItem.getSupplierNumber())) {
                lineItemTypeDTO.setSupplierNumber(costCheckItem.getSupplierNumber());
                lineItemTypeDTO.setCurrencyCode(CurrencyCode.Enum.forString(costCheckItem.getCurrencyCode()));
            }
            String manufactureLocCode="";
            if(claim.getForItem()!=null&&claim.getForItem().getManufacturingSiteInventory()!=null){
            	manufactureLocCode=claim.getForItem().getManufacturingSiteInventory().getCode();       	
            }
            lineItemTypeDTO.setManufacturingLocation(manufactureLocCode);

            lineItems[i++] = lineItemTypeDTO;

        }

        replacedPartsTypeDTO.setLineItemArray(lineItems);
		return replacedPartsTypeDTO;
	}

    private RequestLineItemTypeDTO populateBUSpecificDataForLineItems(PriceCheckItem partItem,
                                                                      String businessUnitName) {
        RequestLineItemTypeDTO lineItemTypeDTO = RequestLineItemTypeDTO.Factory.newInstance();
        lineItemTypeDTO.setPartNumber(partItem.getPartNumber());
        return lineItemTypeDTO;
    }

    /**
	 * Populate replaced parts for price request
	 * @param claim
	 * @return
	 */
    //Do not use this method
    //Instead use populatePriceLineItems(List<PriceCheckItem>, String buName)
    private ReplacedPartsTypeDTO populateReplacedParts(Claim claim){
		if(logger.isInfoEnabled()){
			logger.info("ProcessGlobalPriceCheck :: Start populateReplacedParts :: populate line items " 
								+ claim.getClaimNumber());
		}
		ReplacedPartsTypeDTO replacedPartsTypeDTO = ReplacedPartsTypeDTO.Factory.newInstance();
		List<PartReplaced> partsReplaced = claim.getServiceInformation().getServiceDetail()
													.getPriceFetchedParts();
		
		RequestLineItemTypeDTO[] lineItems = new RequestLineItemTypeDTO[partsReplaced.size()];
		int i = 0;
		for(PartReplaced partReplaced : partsReplaced){
			RequestLineItemTypeDTO lineItemTypeDTO = RequestLineItemTypeDTO.Factory.newInstance();
			// if partReplaced is instance of OEM PartsReplaced
			if(claim.getServiceInformation().getServiceDetail().isOEMPartReplaced(partReplaced)){
				OEMPartReplaced oemPartReplaced = (OEMPartReplaced)partReplaced;
                lineItemTypeDTO.setPartNumber(oemPartReplaced.getItemReference().getUnserializedItem().getNumber());
                lineItemTypeDTO.setQuantity(oemPartReplaced.getNumberOfUnits());
                lineItemTypeDTO.setDivisionCode(claim.getBusinessUnitInfo().getName());
				lineItemTypeDTO.setUOM(StringUtils.hasText(oemPartReplaced.getItemReference().getUnserializedItem().getUom().getType())
						? oemPartReplaced.getItemReference().getUnserializedItem().getUom().getType()
								:ItemUOMTypes.EACH.getType());
			} else{
				InstalledParts installedPart = (InstalledParts)partReplaced;
                lineItemTypeDTO.setPartNumber(installedPart.getItem().getNumber());
                lineItemTypeDTO.setQuantity(installedPart.getNumberOfUnits());
                lineItemTypeDTO.setDivisionCode(claim.getBusinessUnitInfo().getName());
				lineItemTypeDTO.setUOM(StringUtils.hasText(installedPart.getItem().getUom().getType())
						? installedPart.getItem().getUom().getType()
								:ItemUOMTypes.EACH.getType());
			}
				
			lineItems[i++] = lineItemTypeDTO;
			
		}
		if(logger.isInfoEnabled()){
			logger.info("ProcessGlobalPriceCheck :: End populateReplacedParts :: populate line items " 
								+ claim.getClaimNumber());
		}
		replacedPartsTypeDTO.setLineItemArray(lineItems);
		return replacedPartsTypeDTO;
	}

    private ReplacedPartsTypeDTO populatePriceLineItems(List<PriceCheckItem> priceCheckItems,
                                                             String businessUnitName,Claim claim){
        if(logger.isInfoEnabled()){
            logger.info("ProcessGlobalPriceCheck :: Start populateReplacedParts :: populatePriceLineItems");
        }
        ReplacedPartsTypeDTO replacedPartsTypeDTO = ReplacedPartsTypeDTO.Factory.newInstance();

        RequestLineItemTypeDTO[] lineItems = new RequestLineItemTypeDTO[priceCheckItems.size()];
        int i = 0;
        for(PriceCheckItem priceCheckItem : priceCheckItems){
            RequestLineItemTypeDTO lineItemTypeDTO = populateBUSpecificDataForLineItems(
                    priceCheckItem, businessUnitName);

            lineItemTypeDTO.setQuantity(priceCheckItem.getQuantity());
            String manufactureLocCode="";
            if(claim.getForItem()!=null&&claim.getForItem().getManufacturingSiteInventory()!=null){
            	manufactureLocCode=claim.getForItem().getManufacturingSiteInventory().getCode();       	
            }
            lineItemTypeDTO.setManufacturingLocation(manufactureLocCode);
            lineItems[i++] = lineItemTypeDTO;

        }
        if(logger.isInfoEnabled()){
            logger.info("ProcessGlobalPriceCheck :: End populateReplacedParts :: populateLineItems");
        }
        replacedPartsTypeDTO.setLineItemArray(lineItems);
        return replacedPartsTypeDTO;
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

    private void populateTaskLogicalId(SenderTypeDTO senderTypeDTO, String businessUnitName) {
    	if(businessUnitName.equalsIgnoreCase(AdminConstants.NMHGAMERICA)){
        senderTypeDTO.setTask(AdminConstants.PDC);
    	}
    	else {
    		senderTypeDTO.setTask(AdminConstants.EPO);
		}
        senderTypeDTO.setLogicalId(integrationPropertiesBean.getLogicalIdForPriceFetch());
    }

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }

    public String validatePriceCheckRequest(PriceCheckRequest priceCheckRequest) {
        String errorMsg = "";
        //validate buname, uniqueId
        validateCommonFields(priceCheckRequest,errorMsg);
        //if price check is requested, validate
        //dealername, currency, repairdate, partnumber, quantity, uom
        validateForDealer(priceCheckRequest,errorMsg);
        //if price check is requested validate
        //suppliername, currency, repairdate, partnumber, quantity, uom
        validateForSupplier(priceCheckRequest,errorMsg);
        
        return errorMsg;
    }
    
    private String validateForDealer(PriceCheckRequest priceCheckRequest, String errorMsg){
    	 if (priceCheckRequest.getPriceCheckItemList() != null
                 && priceCheckRequest.getPriceCheckItemList().size() > 0) {
             if (!StringUtils.hasText(priceCheckRequest.getDealerNumber())) {
                 errorMsg = appendError(errorMsg, "Dealer Number not provided");
             }
             if (!StringUtils.hasText(priceCheckRequest.getCurrencyCode())) {
                 errorMsg = appendError(errorMsg, "Currency Code not provided");
             }
             for (PriceCheckItem priceCheckItem : priceCheckRequest.getPriceCheckItemList()) {
             	errorMsg = validatePartNumberAndQuantity(priceCheckItem,errorMsg);
             }
         }
    	return errorMsg;
    }
    
    private String validateForSupplier(PriceCheckRequest priceCheckRequest, String errorMsg){
    	if (priceCheckRequest.getCostCheckItemList() != null
                && priceCheckRequest.getCostCheckItemList().size() > 0) {
            for (PriceCheckItem costCheckItem : priceCheckRequest.getCostCheckItemList()) {
            	errorMsg = validatePartNumberAndQuantity(costCheckItem,errorMsg);
                boolean isSupplierNeeded = configParamService.getBooleanValue(
                        ConfigName.IS_SUPPLIER_NEEDED_FOR_COST_FETCH.getName()).booleanValue();
                if (isSupplierNeeded) {
                    if (!StringUtils.hasText(costCheckItem.getSupplierNumber())) {
                        errorMsg =  appendError(errorMsg, "Supplier Number not provided");
                    }
                    if (!StringUtils.hasText(costCheckItem.getCurrencyCode())) {
                        errorMsg = appendError(errorMsg, "Supplier Currency not provided");
                    }
                }
            }
        }
    	return errorMsg;
    }
    
    private String validateCommonFields(PriceCheckRequest priceCheckRequest, String errorMsg){
    	if (!StringUtils.hasText(priceCheckRequest.getBusinessUnitName())) {
            errorMsg =  appendError(errorMsg, "BU Name not provided");
        }
        if (!StringUtils.hasText(priceCheckRequest.getUniqueId())) {
            errorMsg =  appendError(errorMsg, "Unique Id not provided");
        }
        
        if (priceCheckRequest.getDateOfRepair() == null) {
            errorMsg = appendError(errorMsg, "Repair Date not provided");
        }
        return errorMsg;
    }
    
    private String validatePartNumberAndQuantity(PriceCheckItem checkItem, String errorMsg){
    	if (!StringUtils.hasText(checkItem.getPartNumber())) {
            errorMsg = appendError(errorMsg, "Part Number not provided");
        }
        if (checkItem.getQuantity() < 1) {
            errorMsg = appendError(errorMsg, "Quantity is < 1 for part " + checkItem.getPartNumber());
        }
        return errorMsg;
    }

    private String appendError(String errorMsg, String error) {
        return "".equals(errorMsg) ? error : errorMsg + "|" + error;
    }

	public ClaimCurrencyConversionAdvice getClaimCurrencyConversionAdvice() {
		return claimCurrencyConversionAdvice;
	}

	public void setClaimCurrencyConversionAdvice(ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice) {
		this.claimCurrencyConversionAdvice = claimCurrencyConversionAdvice;
	}

}
