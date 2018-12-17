/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.claim.payment.rates;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemBasePrice;
import tavant.twms.domain.catalog.ItemBasePriceRepository;
import tavant.twms.domain.catalog.ItemCriterion;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplaced;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.CriteriaEvaluationPrecedenceRepository;
import tavant.twms.domain.common.CurrencyConversionException;
import tavant.twms.domain.common.CurrencyConversionFactor;
import tavant.twms.domain.common.CurrencyExchangeRateRepository;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.supplier.ItemMapping;
import tavant.twms.domain.supplier.ItemMappingService;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 * 
 */
public class ItemPriceAdminServiceImpl
		extends
		GenericServiceImpl<AdministeredItemPrice, Long, PriceAdministrationException>
		implements ItemPriceAdminService {

	private ItemMappingService itemMappingService;
	
	public void setItemMappingService(ItemMappingService itemMappingService) {
		this.itemMappingService = itemMappingService;
	}

	public List<PriceFetchData> findPrice(Claim claim, Policy policy) {
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		List<PartReplaced> partsReplacedList = claim.getServiceInformation().getServiceDetail().getPriceFetchedParts();
		List<PriceFetchData> priceFetchList = fetchPriceForParts(claim, partsReplacedList, policy);
		return priceFetchList;
	}
	
	private Map<String, PriceFetchData> buildItemCostPriceMap(
			List<PriceFetchData> priceFetchDataList) {
		Map<String, PriceFetchData> mapOfItemPrices = new HashMap<String, PriceFetchData>();
		for (PriceFetchData priceFetchData : priceFetchDataList) {
			mapOfItemPrices.put(priceFetchData.getBrandItem().getItemNumber(),priceFetchData);
		}
		return mapOfItemPrices;
	}
	
	public void populateCostPriceForRecoverableParts(RecoveryClaimInfo recoveryClaimInfo){
		List<PriceFetchData> costPriceFetchList = new ArrayList<PriceFetchData>();
		for(RecoverablePart recoverablePart : recoveryClaimInfo.getRecoverableParts()){
            PriceFetchData priceFetchData = populatePriceFetchData(recoverablePart);
			costPriceFetchList.add(priceFetchData);
		}
		itemBasePriceRepository.findCostPriceByItem(recoveryClaimInfo, costPriceFetchList);
		Map<String,PriceFetchData> priceMap = buildItemCostPriceMap(costPriceFetchList);
		
		for (RecoverablePart recoverablePart : recoveryClaimInfo.getRecoverableParts()){
			PriceFetchData  fetchData = priceMap.get(recoverablePart.getOemPart().getBrandItem().getItemNumber());
			recoverablePart.setMaterialCost(fetchData.getMaterialPrice());
			recoverablePart.setCostPricePerUnit(fetchData.getStandardCost());
		}
	}

    private PriceFetchData populatePriceFetchData(RecoverablePart recoverablePart) {
        PriceFetchData priceFetchData = new PriceFetchData();
        priceFetchData.setBrandItem(recoverablePart.getOemPart().getBrandItem());
        priceFetchData.setQuantity(recoverablePart.getQuantity());
        priceFetchData.setSupplierItem(recoverablePart.getSupplierItem());
        return priceFetchData;
    }

    public void populateCostPriceForRecoverablePart(RecoveryClaimInfo recoveryClaimInfo, RecoverablePart recoverablePart){
        List<PriceFetchData> costPriceFetchList = new ArrayList<PriceFetchData>();
        PriceFetchData pfd = populatePriceFetchData(recoverablePart);
        costPriceFetchList.add(pfd);
        itemBasePriceRepository.findCostPriceByItem(recoveryClaimInfo, costPriceFetchList);
        // only one fetch here no need to iterate
        recoverablePart.setMaterialCost(pfd.getMaterialPrice());
        recoverablePart.setCostPricePerUnit(pfd.getStandardCost());
    }
    
	
	public List<PriceFetchData> fetchPriceForParts(Claim claim, List<PartReplaced> partsReplaced, Policy policy){
		List<PriceFetchData> priceFetchList = new ArrayList<PriceFetchData>();
		for (PartReplaced partReplaced : partsReplaced) {
			BrandItem replacedItem = null;
			PriceFetchData priceFetchData = new PriceFetchData();
			if(claim.getServiceInformation().getServiceDetail().isOEMPartReplaced(partReplaced)) {
				replacedItem = ((OEMPartReplaced) partReplaced).getBrandItem();
			} else {
				replacedItem = ((InstalledParts)partReplaced).getBrandItem();
				priceFetchData.setPriceUpdated(((InstalledParts)partReplaced).getPriceUpdated());
				priceFetchData.setIsInstalledPart(true);
			}
			
			priceFetchData.setBrandItem(replacedItem);
            priceFetchData.setQuantity(partReplaced.getNumberOfUnits());

			priceFetchList.add(priceFetchData);
		}

		CalendarDate asOfDate = claim.getRepairDate();

		itemBasePriceRepository.findByItem(claim, priceFetchList);

		for (PriceFetchData priceFetchData : priceFetchList) {
			Criteria withCriteria = claim.priceCriteriaForReplacedItem(
					priceFetchData.getBrandItem(), policy);
			priceFetchData
					.setItemPrice(applyModifiers(priceFetchData.getItem(),
							withCriteria, asOfDate, priceFetchData
									.getListPrice()
									.breachEncapsulationOfAmount()));
		}

		return priceFetchList;
	}

/*	public Money findPrice(Item replacedPart, Claim claim, Policy policy) {
		Criteria priceCriteria = claim.priceCriteriaForReplacedItem(
				replacedPart, policy);
		Currency dealerCurrency = claim.getCurrencyForCalculation();
		CalendarDate repairDate = claim.getRepairDate();
		return findPrice(replacedPart, priceCriteria, repairDate,
				dealerCurrency);
	}*/

	public Money findPrice(Item forItem, Criteria withCriteria,
			CalendarDate asOfDate, Currency inCurrency) {
		ItemBasePrice basePrice = itemBasePriceRepository.findByItem(forItem);
		if (basePrice == null || basePrice.getEntryAsOf(asOfDate) == null) {
			logger.warn(" Base price not known for item [" + forItem
					+ "] as of date [" + asOfDate + "]");
			return null;
		}
		BigDecimal basePriceValue = basePrice.getValueAsOf(asOfDate);
		return applyModifiers(forItem, withCriteria, asOfDate, basePriceValue);
	}

	private Money applyModifiers(Item forItem, Criteria withCriteria,
			CalendarDate asOfDate, BigDecimal basePriceValue) {
		GlobalConfiguration globalConfiguration = GlobalConfiguration
				.getInstance();
		Currency baseCurrency = globalConfiguration.getBaseCurrency();
		Currency inCurrency = GlobalConfiguration.getInstance()
				.getBaseCurrency();
		Money priceInOEMCurrency = Money.valueOf(basePriceValue, baseCurrency);

		if (logger.isDebugEnabled()) {
			logger.debug(" base price for item [" + forItem + "] as of ["
					+ asOfDate + "] is " + priceInOEMCurrency);
		}
// Commenting as item price modifiers are not applicable to IR.    
		
		
//		CriteriaEvaluationPrecedence evaluationPrecedence = criteriaEvaluationPrecedenceRepository
//				.findByName("Parts Price List");
//		ItemPriceModifier priceModifier = administeredItemPriceRepository
//				.findPriceModifier(forItem, withCriteria, evaluationPrecedence,
//						asOfDate);
//		if (priceModifier != null) {
//			BigDecimal scalingFactor = priceModifier.getScalingFactor();
//			scalingFactor = scalingFactor.multiply(new BigDecimal(0.01D),
//					globalConfiguration.getMathContext());
//			priceInOEMCurrency = priceInOEMCurrency.times(scalingFactor);
//			if (logger.isDebugEnabled()) {
//				logger.debug(" administered price for item [" + forItem
//						+ "] as of [" + asOfDate + "] is -> base price * "
//						+ scalingFactor + " = " + priceInOEMCurrency);
//			}
//		}

		if (baseCurrency.equals(inCurrency)) {
			return priceInOEMCurrency;
		}

		CurrencyConversionFactor conversionFactor = currencyExchangeRateRepository
				.findConversionFactor(inCurrency, baseCurrency, asOfDate);
		if (conversionFactor == null) {
			logger.error("Failed to find exchange rate from currency ["
					+ baseCurrency + "]  to currency [" + inCurrency + "]");
		}
		try {
			return conversionFactor
					.reverseConvert(priceInOEMCurrency, asOfDate);
		} catch (CurrencyConversionException e) {
			logger.error("Failed to convert price to currency [" + inCurrency
					+ "]", e);
			throw new RuntimeException(e);
		}
	}

	private static Logger logger = LogManager
			.getLogger(ItemPriceAdminServiceImpl.class);

	private AdministeredItemPriceRepository administeredItemPriceRepository;

	@SuppressWarnings("unused")
	private CriteriaEvaluationPrecedenceRepository criteriaEvaluationPrecedenceRepository;

	private CurrencyExchangeRateRepository currencyExchangeRateRepository;

	private ItemBasePriceRepository itemBasePriceRepository;

	/**
	 * @param itemBasePriceRepository
	 *            the itemBasePriceRepository to set
	 */
	@Required
	public void setItemBasePriceRepository(
			ItemBasePriceRepository itemBasePriceRepository) {
		this.itemBasePriceRepository = itemBasePriceRepository;
	}

	@Required
	public void setCurrencyExchangeRateRepository(
			CurrencyExchangeRateRepository currencyExchangeRateRepository) {
		this.currencyExchangeRateRepository = currencyExchangeRateRepository;
	}

	@Required
	public void setAdministeredItemPriceRepository(
			AdministeredItemPriceRepository itemPriceFactorsRepository) {
		this.administeredItemPriceRepository = itemPriceFactorsRepository;
	}

	@Required
	public void setCriteriaEvaluationPrecedenceRepository(
			CriteriaEvaluationPrecedenceRepository criteriaEvaluationPrecedenceRepository) {
		this.criteriaEvaluationPrecedenceRepository = criteriaEvaluationPrecedenceRepository;
	}

	@Override
	public GenericRepository<AdministeredItemPrice, Long> getRepository() {
		return administeredItemPriceRepository;
	}

	public boolean isUnique(AdministeredItemPrice itemPrice) {
		boolean isUnique = false;
		Criteria forCriteria = itemPrice.getForCriteria();
		ItemCriterion itemCriterion = itemPrice.getItemCriterion();
		AdministeredItemPrice example = null;
		example = administeredItemPriceRepository.findItemPrice(itemCriterion,
				forCriteria);
		if (example == null || same(itemPrice, example)) {
			isUnique = true;
		}
		return isUnique;
	}

	private boolean same(AdministeredItemPrice source,
			AdministeredItemPrice target) {
		return source.getId() != null && target.getId() != null
				&& source.getId().compareTo(target.getId()) == 0;
	}

	/*
	 * Accepts information about price of an item for a particular duration.
	 * Criteria may be a useless parameter here.
	 * 
	 * @see tavant.twms.domain.claim.payment.rates.ItemPriceAdminService#createPrice(tavant.twms.domain.catalog.ItemBasePrice)
	 */

	public void saveOrUpdateItemPrice(ItemBasePrice price, Criteria forCriteria) {
		// TODO Method signature needs to be modified or approved by Radha.
		// TODO Also, a real implementation is needed here.
		logger.warn("I am an empty method waiting to be implemented.");
	}

	public Money findPrice(Item replacedPart, Claim ofClaim, Policy policy) {
		// TODO Auto-generated method stub
		return null;
	}

}
