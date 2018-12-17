/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.admin.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.payment.rates.TravelRate;
import tavant.twms.domain.claim.payment.rates.TravelRateValues;
import tavant.twms.domain.claim.payment.rates.TravelRates;
import tavant.twms.domain.claim.payment.rates.TravelRatesAdminService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.CurrencyExchangeRateService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.i18n.I18nActionSupport;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

/**
 * @author Kiran.Kollipara
 * 
 */
@SuppressWarnings("serial")
public class ManageTravelRate extends I18nActionSupport implements Validateable, Preparable {

    private final Logger logger = Logger.getLogger(ManageTravelRate.class);

    private static final String MESSAGE_KEY_OVERLAP = "error.manageRates.durationOverlapForValidityDate";

    private static final String MESSAGE_KEY_DUPLICATE = "error.manageRates.duplicateTravelRate";

    private static final String MESSAGE_KEY_UPDATE = "message.manageRates.updateTravelRateSuccess";

    private static final String MESSAGE_KEY_CREATE = "message.manageRates.createTravelRateSuccess";

    private static final String MESSAGE_KEY_DELETE = "message.manageRates.deleteTravelRateSuccess";

    private String id;

    private TravelRates definition;

    private List<TravelRate> rates = new ArrayList<TravelRate>();
    
    private List<Currency> currencyList = new ArrayList<Currency>();
    
    private List<TravelRateValues> tRateValues = new ArrayList<TravelRateValues>();

    public List<ClaimType> claimTypes = new ArrayList<ClaimType>();

    // Fields for Autocomplete
    private String dealerCriterion;

    private String dealerGroupName;

    private String productType;

    private CatalogService catalogService;

    private TravelRatesAdminService travelRatesAdminService;
    
    private CurrencyExchangeRateService currencyExchangeRateService;

    private DealerGroupService dealerGroupService;

    private boolean dealerGroupSelected;
    
    private Map<Object,Object> customerTypes= new HashMap<Object,Object>();
    
    private ConfigParamService configParamService;

    private ClaimService claimService;
    
    private List<WarrantyType> warrantyTypes = new ArrayList<WarrantyType>();

    private WarrantyService warrantyService;
    
	public void prepare() throws Exception {
        if (StringUtils.isNotBlank(this.id)) {
            Long pk = Long.parseLong(this.id);
            this.definition = this.travelRatesAdminService.findById(pk);
            if (this.definition.getCriteria() != null) {
                DealerCriterion dealerCriterion = this.definition.getCriteria()
                        .getDealerCriterion();
                if (dealerCriterion != null && dealerCriterion.isGroupCriterion()) {
                    this.dealerGroupSelected = true;
                }
            }
        } else {
            this.definition = new TravelRates();
        }

        setClaimTypes();
        populateCustomerTypes();
        setWarrantyTypes(this.warrantyService.listWarrantyTypes());
        currencyList = orgService.listUniqueCurrencies();
    	for(Currency currency :currencyList )
		{
			TravelRateValues travelRateValues = new TravelRateValues();
			Money rate = Money.valueOf(new BigDecimal(0.00), currency,2);
			travelRateValues.setDistanceRate(rate);
			travelRateValues.setHourlyRate(rate);
			travelRateValues.setTripRate(rate);
			this.tRateValues.add(travelRateValues);
			
		}
    }

    @Override
    public void validate() {
        for (Iterator iter = this.rates.iterator(); iter.hasNext();) {
            TravelRate trate = (TravelRate) iter.next();
            if (trate == null
                    || (trate.getDuration().getFromDate() == null
                            && trate.getDuration().getTillDate() == null)) {
                iter.remove();
            }
            
        }
        Criteria forCriteria = this.definition.getForCriteria();
        if (forCriteria == null) {
            this.definition.setForCriteria(new Criteria());
        }
            	 
	   	for (TravelRate travelRate : getRates())
	   	 {
	   		travelRate.setTravelRates(this.definition);
	       	
	   	 }
	   	

        super.validate();
        validateDealerCriterion();
        validateProductName();
        validateTravelRate();
        if(hasActionErrors() || hasFieldErrors())
        	this.prepareRatesForUI();
    }

    private Currency getCurrencyOfTravelRateValues(TravelRateValues travelRateValues) {
    	Money money = null;
		if(travelRateValues.getCurrency() != null)
			money = travelRateValues.getCurrency();
		else if(travelRateValues.getHourlyRate()!=null)
			money = travelRateValues.getHourlyRate();
		else if(travelRateValues.getDistanceRate() != null)
			money = travelRateValues.getDistanceRate();
		else if(travelRateValues.getTripRate() != null)
			money = travelRateValues.getTripRate();
		if(money != null)
			return money.breachEncapsulationOfCurrency();
		return null;
    }
    
    private void prepareTravelRateValuesForUI(TravelRateValues travelRateValues,Currency currency) {
    	Money rate = Money.valueOf(new BigDecimal(-1), currency,2);
    	if(travelRateValues.getHourlyRate() == null)
    		travelRateValues.setHourlyRate(rate);
    	if(travelRateValues.getDistanceRate() == null)
    		travelRateValues.setDistanceRate(rate);
    	if(travelRateValues.getTripRate() == null)
    		travelRateValues.setTripRate(rate);
    }
    
    private void prepareRatesForUI() {
    	List<Currency> currencies = new ArrayList<Currency>();
    	for(TravelRate rate : rates) {
            for (Iterator<TravelRateValues> it = rate.getTravelRateValues().iterator(); it.hasNext();) {
                TravelRateValues values = it.next();
    			Currency currency = getCurrencyOfTravelRateValues(values);
                // currency is null, so no value has been entered
                if(currency == null){
                    it.remove(); // we are removing null rate values here as default values are added in next loop
                    continue;
                } 
    			currencies.add(currency);
    			prepareTravelRateValuesForUI(values, currency);                
            }
    		for(Currency currency : currencyList) {
    			if(!currencies.contains(currency)) {
    				TravelRateValues values = new TravelRateValues();
    				prepareTravelRateValuesForUI(values, currency);
    				rate.getTravelRateValues().add(values);
    			}
    		}
    		Collections.sort(rate.getTravelRateValues(),new Comparator<TravelRateValues>(){
				public int compare(TravelRateValues arg0, TravelRateValues arg1) {
					return arg0.getCurrencyCode().compareToIgnoreCase(arg1.getCurrencyCode());
				}
    		});
    	}
    }
    
    public String showPrice() {
        
    	if(this.definition.getId()!=null)
		{
			this.rates.clear();
			this.rates.addAll(this.definition.getRates());
		}
    	
    	if(rates.size() > 0){
			Collections.sort(this.rates);
		}
		
        Criteria forCriteria = this.definition.getCriteria();
        if (forCriteria != null && forCriteria.getDealerCriterion() != null) {
            this.dealerGroupName = forCriteria.getDealerCriterion().getIdentifier();
            this.dealerCriterion = forCriteria.getDealerCriterion().getIdentifier();
        }
        if (forCriteria != null && forCriteria.getProductType() != null) {
            this.productType = forCriteria.getProductType().getName();
        }
        this.prepareRatesForUI();
        return SUCCESS;
    }

    public String createPrice() throws Exception {
        String action = preparePrice();
        if (SUCCESS.equals(action)) {
            this.travelRatesAdminService.save(this.definition);
            addActionMessage(MESSAGE_KEY_CREATE);
        }
        prepareRatesForUI();
        return action;
    }

    public String updatePrice() throws Exception {
        String action = preparePrice();
        if (SUCCESS.equals(action)) {
            this.travelRatesAdminService.update(this.definition);
            addActionMessage(MESSAGE_KEY_UPDATE);
        }
        prepareRatesForUI();
        return action;
    }

    public String deletePrice() throws Exception {
        this.travelRatesAdminService.delete(this.definition);
        addActionMessage(MESSAGE_KEY_DELETE);
        return SUCCESS;
    }

    public String getWarrantyTypeString() {
        Criteria forCriteria = this.definition.getForCriteria();
        if (forCriteria == null || forCriteria.getWarrantyType() == null) {
            return getText("all.warranty.types");
        }
        return forCriteria.getWarrantyType();
    }

    public String getClaimTypeString() {
        Criteria forCriteria = this.definition.getForCriteria();
        if (forCriteria == null || forCriteria.getClaimType() == null) {
            return getText("all.claim.types");
        }
        return getText(forCriteria.getClaimType().getDisplayType());
    }

    public String getDealerString() {
        Criteria forCriteria = this.definition.getCriteria();
        if (forCriteria == null || forCriteria.getDealerCriterion() == null) {
            return getText("all.dealers");
        }
        return forCriteria.getDealerCriterion().getIdentifier();
    }

    public String getProductTypeString() {
        Criteria forCriteria = this.definition.getForCriteria();
        if (forCriteria == null || forCriteria.getProductType() == null) {
            return getText("all.product.types");
        }
        return forCriteria.getProductType().getName();
    }

    // ********************* Accessors & Mutators ****************//

    public TravelRates getDefinition() {
        return this.definition;
    }

    public void setDefinition(TravelRates configuration) {
        this.definition = configuration;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TravelRate> getRates() {
        return this.rates;
    }

    public void setRates(List<TravelRate> rates) {
        this.rates = rates;
    }

    public void setTravelRatesAdminService(TravelRatesAdminService travelRatesAdminService) {
        this.travelRatesAdminService = travelRatesAdminService;
    }

    public String getDealerCriterion() {
        return this.dealerCriterion;
    }

    public void setDealerCriterion(String dealerCriterion) {
        this.dealerCriterion = dealerCriterion;
    }

    public String getProductType() {
        return this.productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }
    
	public List<WarrantyType> getWarrantyTypes() {
		return warrantyTypes;
	}

	public void setWarrantyTypes(List<WarrantyType> warrantyTypes) {
		this.warrantyTypes = warrantyTypes;
	}

	// **************** Private Methods ******************//
    private String preparePrice() {
        if (!this.travelRatesAdminService.isUnique(this.definition)) {
            addActionError(MESSAGE_KEY_DUPLICATE);
            return INPUT;
        }
        Set<Long> idsFromUI = new HashSet<Long>();
        for (TravelRate rate : this.rates) {
			if (rate.getId() != null) {
				idsFromUI.add(rate.getId());
			} else {
				this.definition.getRates().add(rate);
			}
		}

        for (Iterator<TravelRate> it = this.definition.getRates().iterator(); it.hasNext();) {
            TravelRate rate = it.next();
            if (rate.getId() != null && !idsFromUI.contains(rate.getId())) {
            	rate.getD().setActive(Boolean.FALSE);
            }
            for(Iterator<TravelRateValues> iter = rate.getTravelRateValues().iterator(); iter.hasNext();) {
	   			TravelRateValues values = iter.next();
	   			if(values.getHourlyRate()==null && values.getDistanceRate()==null && values.getTripRate()==null)
	   				iter.remove();
	   		}
        }
        
//        this.definition.getRates().clear();
//        this.definition.getRates().addAll(rates);
        return SUCCESS;
    }

    // ****** Private Methods *************//
    private void validateDealerCriterion() {
        if (!this.dealerGroupSelected && StringUtils.isNotBlank(this.dealerCriterion)) {
        	ServiceProvider dealership = this.orgService.findDealerByName(this.dealerCriterion);
            if (dealership == null) {
                addFieldError("dealerCriterion", "error.manageRates.invalidDealer",
                        new String[] { this.dealerCriterion });
            } else {
                Criteria forCriteria = this.definition.getForCriteria();
                DealerCriterion criterion = new DealerCriterion();
                criterion.setDealer(dealership);
                forCriteria.setDealerCriterion(criterion);
            }
        } else if (this.dealerGroupSelected && StringUtils.isNotBlank(this.dealerGroupName)) {
            DealerGroup dealerGroup = this.dealerGroupService.findByNameAndPurpose(
                    this.dealerGroupName, AdminConstants.DEALER_RATES_PURPOSE);
            if (dealerGroup == null) {
                addFieldError("dealerGroupName", "error.manageRates.invalidDealerGroup",
                        new String[] { this.dealerGroupName });
            } else {
                Criteria forCriteria = this.definition.getForCriteria();
                DealerCriterion criterion = new DealerCriterion();
                criterion.setDealerGroup(dealerGroup);
                forCriteria.setDealerCriterion(criterion);
            }
        } else {
            Criteria forCriteria = this.definition.getForCriteria();
            forCriteria.setDealerCriterion(null);
        }
    }

    private void validateProductName() {
        if (StringUtils.isNotBlank(this.productType)) {
            ItemGroup itemGroup = this.catalogService.findItemGroupByName(this.productType);
            if (itemGroup == null) {
                addFieldError("productType", "error.manageRates.invalidProductType",
                        new String[] { this.productType });
            }
            Criteria forCriteria = this.definition.getForCriteria();
            forCriteria.setProductType(itemGroup);
        } else {
            Criteria forCriteria = this.definition.getForCriteria();
            forCriteria.setProductType(null);
        }
    }

    private void validateTravelRate() {

		int numRates = this.rates.size();
		if(numRates==0){
			addActionError("error.manageRates.moreConfigRequired");
			return;
		}
		for (int i = 0; i < numRates; i++) {            
		   validateTravelRateDuration(rates.get(i).getDuration(), 
				   (i==0 ? null : rates.get(i - 1).getDuration()));
		   validateTravelRatePriceEntries(rates.get(i));
		}
	}

    private void validateTravelRatePriceEntries(TravelRate travelRate) {
    	boolean hasPriceEntry = false;
	   	for(TravelRateValues travelRateValues:travelRate.getTravelRateValues()) {
	   		if(travelRateValues.getDistanceRate()!= null && 
	   				travelRateValues.getDistanceRate().breachEncapsulationOfAmount().signum()<0)
	   		{
	   			addActionError("error.manageRates.invalidDistanceRate");
	   		}
	   		if(travelRateValues.getHourlyRate()!=null && 
	   				travelRateValues.getHourlyRate().breachEncapsulationOfAmount().signum()<0)
	   		{
	   			addActionError("error.manageRates.invalidHourlyRate");
	   		}
	   		if(travelRateValues.getTripRate()!=null &&
	   				travelRateValues.getTripRate().breachEncapsulationOfAmount().signum()<0)
	   		{
	   			addActionError("error.manageRates.invalidTripRate");
	   		}
	   		if(!hasPriceEntry && (travelRateValues.getDistanceRate()!= null 
	   				|| travelRateValues.getHourlyRate()!=null || travelRateValues.getTripRate()!=null))
	   			hasPriceEntry = true;		   	
	   	}
	   	if(!hasPriceEntry) {
	   		addActionError("error.manageRates.morePriceEntriesRequired");
	   	}
    }
    
    private void validateTravelRateDuration(CalendarDuration duration,CalendarDuration prevDuration) {
    	CalendarDate startDate = duration.getFromDate();
    	CalendarDate endDate = duration.getTillDate();
    	if(startDate==null) {
    		addActionError("error.manageRates.startDateNotSpecified");
    	}
    	if(endDate==null) {
    		addActionError("error.manageRates.endDateNotSpecified");        		
    	}
    	if(startDate !=null && endDate !=null && startDate.isAfter(endDate)) {
    		addActionError("error.manageRates.endDateBeforeStartDate", new String[] {
                    endDate.toString(), startDate.toString() });
      	
    	}
    	if(prevDuration != null) {
    		CalendarDate prevEndDate = prevDuration.getTillDate();
	    	if ((prevEndDate != null && startDate != null)
	               && !(prevEndDate.plusDays(1).equals(startDate))) {
	    		addActionError("error.manageRates.noGapsInConsecutiveDateRange", new String[] {
	                   prevEndDate.toString(), startDate.toString() });
	    	}
    	}
    }

    protected void populateCustomerTypes() {    	
    	/*customerTypes.put("DEALER", "Dealer");
    	customerTypes.put("END CUSTOMER", "End Customer");
    	customerTypes.put("NATIONAL ACCOUNT", "National Account");*/
    	
    	if(this.configParamService == null || this.claimService == null){
    		initDomainRepository();
    	}
    	
    	// need to put BU filter
    	
    	Map<Object, Object> keyValueOfCustomerTypes = this.configParamService.getKeyValuePairOfObjects(ConfigName.
    			CUSTOMERS_FILING_CLAIMS.getName());
    	
    	if(keyValueOfCustomerTypes != null && !keyValueOfCustomerTypes.isEmpty()){
	    			customerTypes.putAll(keyValueOfCustomerTypes);
	 	}
	}
    
    
    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }

    public String getDealerGroupName() {
        return this.dealerGroupName;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }

    public boolean isDealerGroupSelected() {
        return this.dealerGroupSelected;
    }

    public void setDealerGroupSelected(boolean dealerGroupSelected) {
        this.dealerGroupSelected = dealerGroupSelected;
    }

	public Map<Object, Object> getCustomerTypes() {
		return customerTypes;
	}
	
	private void initDomainRepository() {
        BeanLocator beanLocator = new BeanLocator();
        this.configParamService = (ConfigParamService) beanLocator.lookupBean("configParamService");
        this.claimService = (ClaimService) beanLocator.lookupBean("claimService");
    }
	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

    public ClaimService getClaimService() {
        return claimService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public CurrencyExchangeRateService getCurrencyExchangeRateService() {
		return currencyExchangeRateService;
	}

	public void setCurrencyExchangeRateService(
			CurrencyExchangeRateService currencyExchangeRateService) {
		this.currencyExchangeRateService = currencyExchangeRateService;
	}

	public List<TravelRateValues> getTRateValues() {
		return tRateValues;
	}

	public void setTRateValues(List<TravelRateValues> rateValues) {
		tRateValues = rateValues;
	}

    public List<ClaimType> getClaimTypes() {
		return claimTypes;
	}

    public void setClaimTypes() {
		  this.claimTypes = this.claimService.fetchAllClaimTypesForBusinessUnit();
	}
    
    @Required
    public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}
}
