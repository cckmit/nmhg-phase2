package tavant.twms.external;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemBasePrice;
import tavant.twms.domain.catalog.ItemBasePriceRepository;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimCurrencyConversionAdvice;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.payment.rates.ItemPriceAdminService;
import tavant.twms.domain.claim.payment.rates.PartPrice;
import tavant.twms.domain.claim.payment.rates.PartPriceAdminService;
import tavant.twms.domain.claim.payment.rates.PartPriceValues;
import tavant.twms.domain.claim.payment.rates.PartPrices;
import tavant.twms.domain.claim.payment.rates.PriceFetchData;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.BrandType;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class ItemBasePriceRepositoryWSImpl implements ItemBasePriceRepository,
		ApplicationContextAware, InitializingBean {

	private static final Logger logger = Logger
			.getLogger(ItemBasePriceRepositoryWSImpl.class);

	IntegrationBridge integrationBridge;
	
	private final String BUSINESS_UNIT_AMER="AMER";
	
	private ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice;

	private ApplicationContext applicationContext;

	private String externalServiceEnabled;

    private String hysterBillToCode;

    private String yaleBillToCode;

    private CriteriaHelper criteriaHelper;
    
    private ConfigParamService configParamService;
    
    private SupplierService supplierService;

	private ClaimService claimService;
	
	private PartPriceAdminService partPriceAdminService;
	
	private SecurityHelper securityHelper;

    public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setIntegrationBridge(IntegrationBridge integrationBridge) {
		this.integrationBridge = integrationBridge;
	}

	public void setExternalServiceEnabled(String externalServiceEnabled) {
		this.externalServiceEnabled = externalServiceEnabled;
	}

	public void afterPropertiesSet() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside afterPropertiesSet");
			logger.debug("externalServiceEnabled "
					+ this.externalServiceEnabled);
		}
		if (logger.isInfoEnabled()) {
			logger.info("@@@@@@@@@@@@@ Inside afterPropertiesSet");
			logger.info("@@@@@@@@@@@@@ externalServiceEnabled "
					+ this.externalServiceEnabled);
		}

		ItemPriceAdminService itemPriceAdminService = (ItemPriceAdminService) this.applicationContext
				.getBean("itemPriceAdminService");
		itemPriceAdminService.setItemBasePriceRepository(this);
	}	
	

	public void findCostPriceByItem(RecoveryClaimInfo recoveryClaimInfo, List<PriceFetchData> costPriceFetchList){
		
		if (costPriceFetchList != null && costPriceFetchList.size() > 0) {
			//this  is added because EPO will send the prices for 10 aprts at time if there are more than 10 parts we have to split the request xml and send the request.
			List<PriceCheckResponse> priceCheckResponses=new ArrayList<PriceCheckResponse>();
			 for (int i = 0;i<costPriceFetchList.size(); i += 10) {
				 List<PriceFetchData> subCostFetchList=costPriceFetchList.subList(i, Math.min(costPriceFetchList.size(), i + 10));
			PriceCheckRequest costPriceCheckRequest = populateCostPriceCheckRequest(recoveryClaimInfo, subCostFetchList);
			PriceCheckResponse priceCheckResponse = this.integrationBridge.checkPrice(costPriceCheckRequest,recoveryClaimInfo.getRecoveryClaim().getClaim());
			priceCheckResponses.add(priceCheckResponse);
			 }
			 Set<String> zeroPriceItems=new HashSet<String>();
			 for (PriceCheckResponse priceCheckResponse: priceCheckResponses) {
			List<PriceCheckItem> costCheckItems = priceCheckResponse.getCostCheckItemList();
			if(priceCheckResponse.isGlobalPriceCheckResponse()){
			mapPriceCheckItemsToPriceFetchDataForGlobalPriceFetch(null,costCheckItems,
						costPriceFetchList,recoveryClaimInfo.getRecoveryClaim().getClaim(),zeroPriceItems);
					if (!zeroPriceItems.isEmpty()) {
						recoveryClaimInfo.getRecoveryClaim().getClaim().getActiveClaimAudit().setIsPriceFetchReturnZero(
								Boolean.TRUE);
						String errorMessage="Price Fetch has returned Zero Prices for parts # "
								+ zeroPriceItems;
						recoveryClaimInfo.getRecoveryClaim().getClaim().getActiveClaimAudit().setPriceFetchErrorMessage(
								errorMessage.length() > 3999 ? errorMessage
										.substring(0, 3999) : errorMessage);
					}else{
						recoveryClaimInfo.getRecoveryClaim().getClaim().getActiveClaimAudit().setIsPriceFetchReturnZero(
								Boolean.FALSE);
						recoveryClaimInfo.getRecoveryClaim().getClaim().getActiveClaimAudit().setPriceFetchErrorMessage(null);
					}
			}else{
				mapPriceCheckItemsToPriceFetchData(costCheckItems, costPriceFetchList, 
						recoveryClaimInfo.getRecoveryClaim().getClaim().getCurrencyForCalculation(), recoveryClaimInfo.getRecoveryClaim().getClaim().getRepairDate(),recoveryClaimInfo.getRecoveryClaim().getClaim().getBrand());
			}
			for (PriceFetchData priceFetchData : costPriceFetchList) {
				priceFetchData.setItemPrice(getPrice(priceFetchData.getBrandItem().getItemNumber()
						, costCheckItems));
			}
		}
		}
	}
			 

	public void findByItem(Claim claim, List<PriceFetchData> priceFetchList) {

		if (priceFetchList != null && priceFetchList.size() > 0) {
			//this  is added because EPO will send the prices for 10 aprts at time if there are more than 10 parts we have to split the request xml and send the request.
			List<PriceCheckResponse> priceCheckResponses=new ArrayList<PriceCheckResponse>();
			 for (int i = 0;i<priceFetchList.size(); i += 10) {
				 List<PriceFetchData> subPriceFetchList=priceFetchList.subList(i, Math.min(priceFetchList.size(), i + 10));
            PriceCheckResponse priceCheckResponse = this.integrationBridge.checkPrice(
                        populatePriceCheckRequest(claim, subPriceFetchList,true),claim);
            priceCheckResponses.add(priceCheckResponse);
			 }
            /**
			 * @ToDo make sure List Price and adjusted price are properly populated in the Domain objects
			 */
			 Set<String> zeroPriceItems=new HashSet<String>();
			 for (PriceCheckResponse priceCheckResponse: priceCheckResponses) {
			List<PriceCheckItem> priceCheckItems = priceCheckResponse.getPriceCheckItemList();
			if(priceCheckResponse.isGlobalPriceCheckResponse()){
				 mapPriceCheckItemsToPriceFetchDataForGlobalPriceFetch(priceCheckItems,null,
						priceFetchList,claim,zeroPriceItems);
					if (!zeroPriceItems.isEmpty()) {
						claim.getActiveClaimAudit().setIsPriceFetchReturnZero(
								Boolean.TRUE);
						String errorMessage="Price Fetch has returned Zero Prices for parts # "
								+ zeroPriceItems;
						claim.getActiveClaimAudit().setPriceFetchErrorMessage(
								errorMessage.length() > 3999 ? errorMessage
										.substring(0, 3999) : errorMessage);
					}else{
						claim.getActiveClaimAudit().setIsPriceFetchReturnZero(
								Boolean.FALSE);
						claim.getActiveClaimAudit().setPriceFetchErrorMessage(null);
					}
			}else{
				mapPriceCheckItemsToPriceFetchData(priceCheckItems, priceFetchList, 
						claim.getCurrencyForCalculation(), claim.getRepairDate(),claim.getBrand());
			}
			if(!priceFetchList.isEmpty()){
			for (PriceFetchData priceFetchData : priceFetchList) {
				priceFetchData.setItemPrice(getPrice(priceFetchData.getBrandItem()
						.getItemNumber(), priceCheckItems));
			}
			}
			 }
		}else{
			claim.getActiveClaimAudit().setIsPriceFetchDown(Boolean.FALSE);
			claim.getActiveClaimAudit().setIsPriceFetchReturnZero(
					Boolean.FALSE);
			claim.getActiveClaimAudit().setPriceFetchErrorMessage(null);
		}
		if(claim.getActiveClaimAudit().getIsPriceFetchDown()||claim.getActiveClaimAudit().getIsPriceFetchReturnZero())
		claimService.updateEPOerrorMessagesDetails(claim);
	}
	
	



	private Collection<String> getZeroPricedInstalledParts(Claim claim,
			List<HussmanPartsReplacedInstalled> partsReplacedInstalled) {
		Set<String> installedItemSet = new HashSet<String>();
		if(CollectionUtils.isNotEmpty(partsReplacedInstalled)){
            for (HussmanPartsReplacedInstalled part : partsReplacedInstalled) {
                if(part == null  ) {
                    continue; // Null check to avoid the sporadic NPE due to indexing problems
                }
                List<InstalledParts> installedParts = part.getHussmanInstalledParts();
                if(CollectionUtils.isNotEmpty(installedParts)){
               	for(InstalledParts installedPart : installedParts){
               	  if(installedPart != null && installedPart.getItem() != null
               			  && StringUtils.hasText(installedPart.getItem().getNumber())){	
                   installedItemSet.add(installedPart.getItem().getBrandItemNumber(claim.getBrand()));
                  }
                }
             }         
           }
		}
		return installedItemSet;
	}

	private PriceCheckRequest populateCostPriceCheckRequest(RecoveryClaimInfo recovClaimInfo, List<PriceFetchData> priceFetchList) {
	        PriceCheckRequest priceCheckRequest = new PriceCheckRequest();
	        priceCheckRequest.setBusinessUnitName(recovClaimInfo.getRecoveryClaim().getClaim().getBusinessUnitInfo().getName());
	        priceCheckRequest.setUniqueId(recovClaimInfo.getRecoveryClaim().getRecoveryClaimNumber());
	        if (recovClaimInfo.getRecoveryClaim().getClaim().getClaimedItems() != null && recovClaimInfo.getRecoveryClaim().getClaim().getClaimedItems().size() > 0
	                && recovClaimInfo.getRecoveryClaim().getClaim().getClaimedItems().get(0).getApplicablePolicy() != null
	                && recovClaimInfo.getRecoveryClaim().getClaim().getClaimedItems().get(0).getApplicablePolicy().getWarrantyType() != null) {
	            priceCheckRequest.setWarrantyType(recovClaimInfo.getRecoveryClaim().getClaim().getClaimedItems().get(0).getApplicablePolicy()
	                                .getWarrantyType().getType());
	        }
	        if(null != recovClaimInfo.getRecoveryClaim().getClaim().getServicingLocation() && recovClaimInfo.getRecoveryClaim().getClaim().getServicingLocation().getSiteNumber()!=null){
	        priceCheckRequest.setDealerSiteNumber(recovClaimInfo.getRecoveryClaim().getClaim().getServicingLocation().getSiteNumber());
	        }
	        priceCheckRequest.setDateOfRepair(recovClaimInfo.getRecoveryClaim().getClaim().getRepairDate());
	        priceCheckRequest.setDealerNumber(recovClaimInfo.getRecoveryClaim().getClaim().getForDealerShip().getDealerNumber());
	        priceCheckRequest.setCurrencyCode(claimCurrencyConversionAdvice.getCurrencyForERPInteractions(recovClaimInfo.getRecoveryClaim().getClaim()));
	        List<PriceCheckItem> costCheckItems = new ArrayList<PriceCheckItem>(priceFetchList.size());
	        for (PriceFetchData priceFetchData : priceFetchList) {
	            PriceCheckItem priceCheckItem = new PriceCheckItem();
	            //priceCheckItem.setPartNumber(priceFetchData.getItem().getNumber());
	            priceCheckItem.setPartNumber(priceFetchData.getBrandItem().getItemNumber());
	            priceCheckItem.setQuantity(priceFetchData.getQuantity() == null
	                    ? 1 : priceFetchData.getQuantity().intValue());
	            priceCheckItem.setUom(priceFetchData.getBrandItem().getItem().getUom());
	            if(priceFetchData.getSupplierItem()!=null){
	            	Supplier supplier = supplierService.findByIdWithOutActivateInactivate(priceFetchData.getSupplierItem().getOwnedBy().getId());
		            priceCheckItem.setSupplierNumber(supplier.getSupplierNumber());
		            priceCheckItem.setCurrencyCode(supplier.getPreferredCurrency().getCurrencyCode());
	            }
	            costCheckItems.add(priceCheckItem);
	        }

	        priceCheckRequest.setCostCheckItemList(costCheckItems);
	        return priceCheckRequest;
	    }

   

    private PriceCheckRequest populatePriceCheckRequest(Claim claim, List<PriceFetchData> priceFetchList,boolean ispricecheckRequest) {
        PriceCheckRequest priceCheckRequest = new PriceCheckRequest();
        priceCheckRequest.setBusinessUnitName(claim.getBusinessUnitInfo().getName());
        priceCheckRequest.setUniqueId(StringUtils.hasText(claim.getClaimNumber()) ? claim.getClaimNumber() : "DRAFT_" + claim.getId());
        priceCheckRequest.setDateOfRepair(claim.getRepairDate());

        // NMHGSLMS-154 - Information Only Claim (Warranty Orders)
        if(claim.getWarrantyOrder()!=null && claim.getWarrantyOrder()){
            if(BrandType.HYSTER.getType().equals(claim.getBrand())){
                priceCheckRequest.setDealerNumber(hysterBillToCode);
            }else if(BrandType.YALE.getType().equals(claim.getBrand())){
                priceCheckRequest.setDealerNumber(yaleBillToCode);
            }
        }else{
            priceCheckRequest.setDealerNumber(getDealerNumber(claim.getForDealerShip().getDealerNumber()));
        }
        priceCheckRequest.setCurrencyCode(claimCurrencyConversionAdvice.getCurrencyForERPInteractions(claim));
        List<PriceCheckItem> items = new ArrayList<PriceCheckItem>(priceFetchList.size());
        for (PriceFetchData priceFetchData : priceFetchList) {
            PriceCheckItem priceCheckItem = new PriceCheckItem();
           priceCheckItem.setPartNumber(priceFetchData.getBrandItem().getItemNumber());
            //priceCheckItem.setPartNumber(priceFetchData.getItem().getNumber());
            priceCheckItem.setQuantity(priceFetchData.getQuantity() == null
                    ? 1 : priceFetchData.getQuantity().intValue());
            items.add(priceCheckItem);
        }
        priceCheckRequest.setPriceCheckItemList(items);
		if (!ispricecheckRequest) {
			if (!configParamService.getBooleanValue(
					ConfigName.IS_SUPPLIER_NEEDED_FOR_COST_FETCH.getName())
					.booleanValue()) {
				priceCheckRequest.setCostCheckItemList(items);
			}
		}
        return priceCheckRequest;
    }
    private String getDealerNumber(String dealerNumberString) {
		if (dealerNumberString != null) {
			String dealerNumbers = dealerNumberString.length() <= 5
					? dealerNumberString
					: dealerNumberString
							.substring(dealerNumberString.length() - 5);
			return dealerNumbers;
		}
		return "";
	}

    private void mapPriceCheckItemsToPriceFetchData(
			List<PriceCheckItem> priceCheckItems,
			List<PriceFetchData> priceFetchList, Currency naturalCurrency, CalendarDate repairDate,String brand) {

		for (PriceCheckItem priceCheckItem : priceCheckItems) {
			String itemNumber = priceCheckItem.getPartNumber();
			PriceFetchData priceFetchData = getPriceFetchData(priceFetchList,
					itemNumber,brand);
			if (priceFetchData != null) {
				priceFetchData.setListPrice(priceCheckItem.getListPrice());
				
				Money standardCost = priceCheckItem.getStandardCost();
				priceFetchData.setStandardCost(standardCost);
//				priceFetchData
//						.setStandardCost(claimCurrencyConversionAdvice.convertMoneyFromBaseToNaturalCurrency(standardCost, repairDate, naturalCurrency));
				Money materialCost = priceCheckItem
						.getMaterialCost();
				priceFetchData.setMaterialPrice(materialCost);
//				priceFetchData.setMaterialPrice(claimCurrencyConversionAdvice.convertMoneyFromBaseToNaturalCurrency(materialCost, repairDate, naturalCurrency));
			}
		}

	}
    
	public BusinessUnit getCurrentBusinessUnit() {
		return this.securityHelper.getDefaultBusinessUnit();
	}
	public boolean isBuConfigAMER() {
		return getCurrentBusinessUnit() == null ? true : getCurrentBusinessUnit().getName().equals(BUSINESS_UNIT_AMER); 
	}
	
	/**
	 * This method set the Item prices from the Price Response
	 * @param priceCheckItems
	 * @param costCheckItems
	 * @param priceFetchList
	 */
	private  void mapPriceCheckItemsToPriceFetchDataForGlobalPriceFetch(
			List<PriceCheckItem> priceCheckItems, List<PriceCheckItem> costCheckItems,
			List<PriceFetchData> priceFetchList, Claim claim,Set<String> zeroPriceItemsList) {
        String erpCurrency = "";
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
        List<Object> configValues = configParamService.getListofObjects(
                 ConfigName.ERP_CURRENCY.getName());
        if (configValues == null || !(configValues.get(0) instanceof String) || configValues.get(0) == null) {
            logger.error("Could not fetch erpCurrency BU config for claim " + claim.getClaimNumber() +
                        ". There will be no currency conversion");
        } else {
            erpCurrency = (String) configValues.get(0);
            logger.debug("erpCurrency is " + erpCurrency + " for BU " + claim.getBusinessUnitInfo().getName());
        }

        // Set List Price and adjusted List price
		if(priceCheckItems != null && !priceCheckItems.isEmpty()){
			for (PriceCheckItem priceCheckItem : priceCheckItems) {
				boolean isPartPriceFetchInternal = false;
				PartPriceValues partPriceValues = null;
				String itemNumber = priceCheckItem.getPartNumber();
				PriceFetchData priceFetchData = getPriceFetchData(priceFetchList,
						itemNumber,claim.getBrand());
				if (priceFetchData != null) {
					if (configParamService
							.getBooleanValue(ConfigName.USE_ADJUSTED_PRICE_ON_CLAIM
									.getName())
							&& priceCheckItem.getAdjustedListPrice() != null) {
						if ((priceCheckItem.getAdjustedListPrice().isZero()
								&& priceCheckItemInInstalledParts(priceCheckItem,claim)) || priceCheckItem.getStandardCost().isZero()) {
							if(isBuConfigAMER()){
								partPriceValues = fetchPartPriceValueByPartNumber(priceFetchData.getBrandItem(), claim.getRepairDate(), claim.getCurrencyForCalculation());
								if((partPriceValues == null || (partPriceValues.getDealerNetPrice().isZero() && !priceFetchData.getPriceUpdated()) || partPriceValues.getStandardCostPrice().isZero()) && priceFetchData.getIsInstalledPart())
									zeroPriceItemsList.add(priceCheckItem.getPartNumber());
								else
									if(partPriceValues != null)
										isPartPriceFetchInternal = true;
							}else if(priceFetchData.getIsInstalledPart()){
								zeroPriceItemsList.add(priceCheckItem.getPartNumber());
							}
						}
					} else if ((priceCheckItem.getListPrice().isZero()&&
							priceCheckItemInInstalledParts(priceCheckItem,claim)) || priceCheckItem.getStandardCost().isZero()) {
						if(isBuConfigAMER()){
							partPriceValues = fetchPartPriceValueByPartNumber(priceFetchData.getBrandItem(), claim.getRepairDate(), claim.getCurrencyForCalculation());
							if((partPriceValues == null || (partPriceValues.getDealerNetPrice().isZero() && !priceFetchData.getPriceUpdated()) || partPriceValues.getStandardCostPrice().isZero()) && priceFetchData.getIsInstalledPart())
								zeroPriceItemsList.add(priceCheckItem.getPartNumber());
							else{
								if(partPriceValues != null)
									isPartPriceFetchInternal = true;
							}
						}else if(priceFetchData.getIsInstalledPart()){
							zeroPriceItemsList.add(priceCheckItem.getPartNumber());
						}
					}
                    //Since we are always converting from erpCurrency to base and again from base to natural (dealers),
                    //we do not need to do it seperately for usd and eur. Infact we can do this conversion all the time,
                    //as the conversion api returns the same value if both currencies are same.
                    if (StringUtils.hasText(erpCurrency) &&
                            ("usd".equals(erpCurrency) || "eur".equals(erpCurrency))) {
                    	Money listPriceInBaseCurrency = null;
                    	Money adjListPriceInBaseCurrency = null;
                    	Money standardCostInBaseCurrency = null;
                    	Money materialCostInBaseCurrency = null;
                    	if(isPartPriceFetchInternal){
                            listPriceInBaseCurrency = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(
                            		partPriceValues.getDealerNetPrice(),claim);
                            adjListPriceInBaseCurrency = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(
                            		partPriceValues.getDealerNetPrice(), claim);
                            standardCostInBaseCurrency = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(
                            		partPriceValues.getStandardCostPrice(), claim);
    						materialCostInBaseCurrency = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(
    								partPriceValues.getStandardCostPrice(), claim);                    		
                    	}else{
                            listPriceInBaseCurrency = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(
                                    priceCheckItem.getListPrice(),claim);
                            adjListPriceInBaseCurrency = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(
                                    priceCheckItem.getAdjustedListPrice(), claim);
                            standardCostInBaseCurrency = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(
    								priceCheckItem.getStandardCost(), claim);
    						materialCostInBaseCurrency = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(
    								priceCheckItem.getMaterialCost(), claim);                    		
                    	}

                        Money listPriceInDealerCurrency = claimCurrencyConversionAdvice.convertMoneyFromBaseToNaturalCurrency(
                                listPriceInBaseCurrency, claim.getRepairDate(), claim.getCurrencyForCalculation());
                        Money adjListPriceDealerCurrency = claimCurrencyConversionAdvice.convertMoneyFromBaseToNaturalCurrency(
                                adjListPriceInBaseCurrency, claim.getRepairDate(), claim.getCurrencyForCalculation());
						Money standardCostInDealerCurrency = claimCurrencyConversionAdvice.convertMoneyFromBaseToNaturalCurrency(
								standardCostInBaseCurrency, claim.getRepairDate(), claim.getCurrencyForCalculation());
						Money materilaCostInDealerCurrency = claimCurrencyConversionAdvice.convertMoneyFromBaseToNaturalCurrency(
								materialCostInBaseCurrency, claim.getRepairDate(), claim.getCurrencyForCalculation());
						
                        priceFetchData.setListPrice(listPriceInDealerCurrency);
                        priceFetchData.setAdjustedListPrice(adjListPriceDealerCurrency);
						priceFetchData.setStandardCost(standardCostInDealerCurrency);
						priceFetchData.setMaterialPrice(materilaCostInDealerCurrency);
						
                    } else if ("dealersCurrency".equalsIgnoreCase(erpCurrency)) {
                    	if(isPartPriceFetchInternal){
                            priceFetchData.setListPrice(partPriceValues.getDealerNetPrice());
                            priceFetchData.setAdjustedListPrice(partPriceValues.getDealerNetPrice());
                            priceFetchData.setMaterialPrice(partPriceValues.getPlantCostPrice());
                            priceFetchData.setStandardCost(partPriceValues.getStandardCostPrice());
                    	}else{
                            priceFetchData.setListPrice(priceCheckItem.getListPrice().dividedBy(priceCheckItem.getQuantity()));
                            priceFetchData.setAdjustedListPrice(priceCheckItem.getAdjustedListPrice().dividedBy(priceCheckItem.getQuantity()));
                            priceFetchData.setMaterialPrice(priceCheckItem.getMaterialCost().dividedBy(priceCheckItem.getQuantity()));
                            priceFetchData.setStandardCost(priceCheckItem.getStandardCost().dividedBy(priceCheckItem.getQuantity()));                    		
                    	}
                    } else {
                    	if(isPartPriceFetchInternal){
                            priceFetchData.setListPrice(partPriceValues.getDealerNetPrice());
                            priceFetchData.setAdjustedListPrice(partPriceValues.getDealerNetPrice());
                            priceFetchData.setMaterialPrice(partPriceValues.getPlantCostPrice());
                            priceFetchData.setStandardCost(partPriceValues.getStandardCostPrice());
                    	}else{
                            priceFetchData.setListPrice(priceCheckItem.getListPrice().dividedBy(priceCheckItem.getQuantity()));
                            priceFetchData.setAdjustedListPrice(priceCheckItem.getAdjustedListPrice().dividedBy(priceCheckItem.getQuantity()));
                            priceFetchData.setMaterialPrice(priceCheckItem.getMaterialCost().dividedBy(priceCheckItem.getQuantity()));
                            priceFetchData.setStandardCost(priceCheckItem.getStandardCost().dividedBy(priceCheckItem.getQuantity()));
                    	}
                    }

				}
			}
		}
		// Set Material Price and Standard Cost
		
		if(costCheckItems != null && !costCheckItems.isEmpty()){
			for (PriceCheckItem priceCheckItem : costCheckItems) {
				boolean isPartPriceFetchInternal = false;
				PartPriceValues partPriceValues = null;
				PriceFetchData priceFetchData = getCostFetchData(priceFetchList,
						priceCheckItem,claim.getBrand());
				if (priceFetchData != null) {
					if(ClaimType.MACHINE.equals(claim.getType()) || ClaimType.FIELD_MODIFICATION.equals(claim.getType())){
						if (getConfigParamService().getStringValue(
								ConfigName.SUPPLIER_RECOVERY_CONTRACT_VALUE_CONFIGURATION.getName())
								.equalsIgnoreCase("StandardCost")
								&& priceCheckItem.getStandardCost() != null
								&& priceCheckItem.getStandardCost().isZero() && priceCheckItem.getMaterialCost().isZero()) {
							if(isBuConfigAMER()){
								partPriceValues = fetchPartPriceValueByPartNumber(priceFetchData.getBrandItem(), claim.getRepairDate(), claim.getCurrencyForCalculation());
								if(partPriceValues == null || (partPriceValues.getStandardCostPrice().isZero() && partPriceValues.getPlantCostPrice().isZero()))
									zeroPriceItemsList.add(priceCheckItem.getPartNumber());
								else
									isPartPriceFetchInternal = true;
							}else{
								zeroPriceItemsList.add(priceCheckItem.getPartNumber());
							}
						}else if(getConfigParamService().getStringValue(
								ConfigName.SUPPLIER_RECOVERY_CONTRACT_VALUE_CONFIGURATION.getName())
								.equalsIgnoreCase("MaterialCost")
								&& priceCheckItem.getMaterialCost() != null
								&& priceCheckItem.getMaterialCost().isZero() && priceCheckItem.getStandardCost().isZero()) {
							if(isBuConfigAMER()){
								partPriceValues = fetchPartPriceValueByPartNumber(priceFetchData.getBrandItem(), claim.getRepairDate(), claim.getCurrencyForCalculation());
								if(partPriceValues == null || (partPriceValues.getPlantCostPrice().isZero() && partPriceValues.getStandardCostPrice().isZero()))
									zeroPriceItemsList.add(priceCheckItem.getPartNumber());
								else
									isPartPriceFetchInternal = true;
							}else{
								zeroPriceItemsList.add(priceCheckItem.getPartNumber());
							}
						}
					}else{
						if(priceCheckItem.getStandardCost() != null && priceCheckItem.getStandardCost().isZero()){
							partPriceValues = fetchPartPriceValueByPartNumber(priceFetchData.getBrandItem(), claim.getRepairDate(), claim.getCurrencyForCalculation());
							if(partPriceValues == null || partPriceValues.getStandardCostPrice().isZero())
								zeroPriceItemsList.add(priceCheckItem.getPartNumber());
							else
								isPartPriceFetchInternal = true;
						}
					}
                    if (StringUtils.hasText(erpCurrency) &&
                            ("usd".equals(erpCurrency) || "eur".equals(erpCurrency))) {
                        // since direct conversion wont be available in exchange rate
						// 1. convert cost price currency to base currency
						// 2. convert base currency to claim currency
                    	Money standardCostInBaseCurrency = null;
                    	Money materialCostInBaseCurrency = null; 
                    	if(isPartPriceFetchInternal){
    						standardCostInBaseCurrency = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(
    								partPriceValues.getStandardCostPrice(), claim);
    						materialCostInBaseCurrency = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(
    								partPriceValues.getPlantCostPrice(), claim);
                    	}else{
    						standardCostInBaseCurrency = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(
    								priceCheckItem.getStandardCost(), claim);
    						materialCostInBaseCurrency = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(
    								priceCheckItem.getMaterialCost(), claim);                    		
                    	}
						
						Money standardCostInDealerCurrency = claimCurrencyConversionAdvice.convertMoneyFromBaseToNaturalCurrency(
								standardCostInBaseCurrency, claim.getRepairDate(), claim.getCurrencyForCalculation());
						Money materilaCostInDealerCurrency = claimCurrencyConversionAdvice.convertMoneyFromBaseToNaturalCurrency(
								materialCostInBaseCurrency, claim.getRepairDate(), claim.getCurrencyForCalculation());
						
						priceFetchData.setStandardCost(standardCostInDealerCurrency);
						priceFetchData.setMaterialPrice(materilaCostInDealerCurrency);
                    } else if ("dealersCurrency".equalsIgnoreCase(erpCurrency)) {
                    	if(isPartPriceFetchInternal){
                            priceFetchData.setMaterialPrice(partPriceValues.getPlantCostPrice());
                            priceFetchData.setStandardCost(partPriceValues.getStandardCostPrice());
                    	}else{
                            priceFetchData.setMaterialPrice(priceCheckItem.getMaterialCost().dividedBy(priceCheckItem.getQuantity()));
                            priceFetchData.setStandardCost(priceCheckItem.getStandardCost().dividedBy(priceCheckItem.getQuantity()));                    		
                    	}
                    } else {
                    	if(isPartPriceFetchInternal){
                            priceFetchData.setMaterialPrice(partPriceValues.getPlantCostPrice());
                            priceFetchData.setStandardCost(partPriceValues.getStandardCostPrice());
                    	}else{
                            priceFetchData.setMaterialPrice(priceCheckItem.getMaterialCost().dividedBy(priceCheckItem.getQuantity()));
                            priceFetchData.setStandardCost(priceCheckItem.getStandardCost().dividedBy(priceCheckItem.getQuantity()));                    		
                    	}
                    }
				}
			}
		}
	}

	/**
	 * @param partNumber
	 * @param date
	 * @param currency
	 * @return PartPriceValues for passed partNumber, date, currency
	 */
	private PartPriceValues fetchPartPriceValueByPartNumber(BrandItem partNumber, CalendarDate date, Currency currency){
		PartPrices partPrices = partPriceAdminService.findPartPricesByPartNumber(partNumber);
		if(partPrices != null){
			for(PartPrice partPrice : partPrices.getRates()){
				if(partPrice.getDuration().includes(date)){
					for(PartPriceValues partPriceValues : partPrice.getPartPriceValues()){
						if(partPriceValues.getDealerNetPrice().breachEncapsulationOfCurrency().getCurrencyCode().equals(currency.getCurrencyCode()))
							return partPriceValues;
					}
				}
			}
		}
		return null;
	}

	private boolean priceCheckItemInInstalledParts(
			PriceCheckItem priceCheckItem, Claim claim) {
		List<HussmanPartsReplacedInstalled> partsReplacedInstalledList = 
			claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled();
	    Collection<String> installedParts = getZeroPricedInstalledParts(claim,partsReplacedInstalledList);	  
		
		if (CollectionUtils.isEmpty(installedParts)) {
			return false;
		}
		for (String installedPart : installedParts) {
			if (false == StringUtils.hasText(priceCheckItem.getPartNumber())) {
				return false;
			}
			if (priceCheckItem.getPartNumber().trim().equals(
					installedPart.trim())) {
				return true;
			}
		}
		return false;
	}

	private PriceFetchData getPriceFetchData(
			List<PriceFetchData> priceFetchDataList, String itemNumber,String brand) {

		for (PriceFetchData priceFetchData : priceFetchDataList) {
			if (priceFetchData.getBrandItem().getItemNumber().trim().equalsIgnoreCase(itemNumber.trim()) && 
					priceFetchData.getListPrice() == null && priceFetchData.getAdjustedListPrice() == null) {
				return priceFetchData;
			}
		}
		return null;
	}
	
	private PriceFetchData getCostFetchData(
			List<PriceFetchData> priceFetchDataList, PriceCheckItem priceCheckItem,String brand) {
		for (PriceFetchData costFetchData : priceFetchDataList) {
			if (costFetchData.getBrandItem().getItemNumber().trim().equalsIgnoreCase(priceCheckItem.getPartNumber().trim()) &&
				costFetchData.getMaterialPrice() == null && costFetchData.getStandardCost() == null)
			{
				return costFetchData;
			}
		}
		return null;
	}

	private Money getPrice(String itemNumber,
			List<PriceCheckItem> priceCheckItems) {
		Money itemBasePrice = null;
		for (PriceCheckItem priceCheckItem : priceCheckItems) {
			if (priceCheckItem.getPartNumber().equalsIgnoreCase(itemNumber)) {
				// Price should be set based on BU Configuration
				// itemBasePrice = priceCheckItem.getListPrice();
				if (configParamService
						.getBooleanValue(ConfigName.USE_ADJUSTED_PRICE_ON_CLAIM
								.getName())
						&& priceCheckItem.getAdjustedListPrice() != null) {
					itemBasePrice = priceCheckItem.getAdjustedListPrice();
				} else { 
					itemBasePrice = priceCheckItem.getListPrice();
				} 
			}
		}
		return itemBasePrice;
	}

	public ItemBasePrice findByItem(Item item) {
		throw new UnsupportedOperationException();
	}

	public void delete(ItemBasePrice entity) {
		throw new UnsupportedOperationException();

	}

    public void deleteAll(List<ItemBasePrice> itemBasePrices) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ItemBasePrice> findAll() {
		throw new UnsupportedOperationException();

	}

	public PageResult<ItemBasePrice> findAll(PageSpecification pageSpecification) {
		throw new UnsupportedOperationException();

	}

	public ItemBasePrice findById(Long id) {
		throw new UnsupportedOperationException();

	}

	public List<ItemBasePrice> findByIds(Collection<Long> collectionOfIds) {
		throw new UnsupportedOperationException();

	}

	public List<ItemBasePrice> findByIds(String propertyNameOfId,
			Collection<Long> collectionOfIds) {
		throw new UnsupportedOperationException();

	}

	public List<ItemBasePrice> findEntitiesThatMatchPropertyValue(
			String property, ItemBasePrice entity) {
		throw new UnsupportedOperationException();

	}

	public List<ItemBasePrice> findEntitiesThatMatchPropertyValues(
			Set<String> property, ItemBasePrice entity) {
		throw new UnsupportedOperationException();

	}

	public PageResult<ItemBasePrice> findPage(String queryString,
			ListCriteria listCriteria) {
		throw new UnsupportedOperationException();

	}

    public PageResult<ItemBasePrice> fetchPage(Criteria criteria, ListCriteria listCriteria, List<String> strings) {
        throw new UnsupportedOperationException();
    }

    public void save(ItemBasePrice entity) {
		throw new UnsupportedOperationException();

	}

    public void saveAll(List<ItemBasePrice> itemBasePrices) {
        throw new UnsupportedOperationException();
    }

    public void update(ItemBasePrice entity) {
		throw new UnsupportedOperationException();

	}

    public void updateAll(List<ItemBasePrice> itemBasePrices) {
        throw new UnsupportedOperationException();
    }

    public void setClaimCurrencyConversionAdvice(
			ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice) {
		this.claimCurrencyConversionAdvice = claimCurrencyConversionAdvice;
	}

    public CriteriaHelper getCriteriaHelper() {
        return criteriaHelper;
    }

    public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
        this.criteriaHelper = criteriaHelper;
    }

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}
    
	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public SupplierService getSupplierService() {
		return supplierService;
	}

	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

    public String getHysterBillToCode() {
        return hysterBillToCode;
    }

    public void setHysterBillToCode(String hysterBillToCode) {
        this.hysterBillToCode = hysterBillToCode;
    }

    public String getYaleBillToCode() {
        return yaleBillToCode;
    }

    public void setYaleBillToCode(String yaleBillToCode) {
        this.yaleBillToCode = yaleBillToCode;
    }
    public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public PartPriceAdminService getPartPriceAdminService() {
		return partPriceAdminService;
	}

	public void setPartPriceAdminService(PartPriceAdminService partPriceAdminService) {
		this.partPriceAdminService = partPriceAdminService;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public PageResult<ItemBasePrice> findPage(String selectClause,
			String fromClause, ListCriteria listCriteria) {
		// TODO Auto-generated method stub
		return null;
	}

}
