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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.payment.rates.LaborHistoryRepository;
import tavant.twms.domain.claim.payment.rates.LaborRate;
import tavant.twms.domain.claim.payment.rates.LaborRateAudit;
import tavant.twms.domain.claim.payment.rates.LaborRateValues;
import tavant.twms.domain.claim.payment.rates.LaborRates;
import tavant.twms.domain.claim.payment.rates.LaborRatesAdminService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.web.actions.SortedHashMap;
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
public class ManageLaborRate extends I18nActionSupport implements Validateable, Preparable {

    private final Logger logger = Logger.getLogger(ManageLaborRate.class);

    private static final String MESSAGE_KEY_OVERLAP = "error.manageRates.durationOverlapForValidityDate";

    private static final String MESSAGE_KEY_DUPLICATE = "error.manageRates.duplicateLabourRate";

    private static final String MESSAGE_KEY_CONTINUOUS = "error.manageRates.continuous";

    private static final String MESSAGE_KEY_UPDATE = "message.manageRates.updateLabourRateSuccess";

    private static final String MESSAGE_KEY_CREATE = "message.manageRates.createLabourRateSuccess";

    private static final String MESSAGE_KEY_DELETE = "message.manageRates.deleteLabourRateSuccess";

    private String id;

    private LaborRates definition;

    private List<LaborRate> rates = new ArrayList<LaborRate>();
    
    public List<ClaimType> claimTypes = new ArrayList<ClaimType>();
    
    private List<WarrantyType> warrantyTypes = new ArrayList<WarrantyType>();
    
	public List<WarrantyType> getWarrantyTypes() {
		return warrantyTypes;
	}

	public void setWarrantyTypes(List<WarrantyType> warrantyTypes) {
		this.warrantyTypes = warrantyTypes;
	}

	// Fields for Autocomplete
    private String dealerCriterion;

    private String productType;

    private String dealerGroupName;
    
    private String customerName;

    private CatalogService catalogService;

    private LaborRatesAdminService laborRatesAdminService;

    private DealerGroupService dealerGroupService;
    
    private List<Currency> currencyList = new ArrayList<Currency>();
    
    private List<LaborRateValues> lbrRateValues = new ArrayList<LaborRateValues>();
    
    private Map<Object,Object> customerTypes= new SortedHashMap<Object,Object>();
        

    private boolean dealerGroupSelected;
    
    private ConfigParamService configParamService;

    private ClaimService claimService;
    
    private String laborComments;
    
    private Long laborAuditID;
    
    private LaborHistoryRepository laborHistoryRepository;
    private LaborRateAudit laborRateAudit;
    private static final String DATE_FORMAT = "MM-dd-yyyy";
    private WarrantyService warrantyService;
      
    @Required
	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public LaborRateAudit getLaborRateAudit() {
		return laborRateAudit;
	}

	public void setLaborRateAudit(LaborRateAudit laborRateAudit) {
		this.laborRateAudit = laborRateAudit;
	}

	public LaborHistoryRepository getLaborHistoryRepository() {
		return laborHistoryRepository;
	}

	public void setLaborHistoryRepository(
			LaborHistoryRepository laborHistoryRepository) {
		this.laborHistoryRepository = laborHistoryRepository;
	}

	public Long getLaborAuditID() {
		return laborAuditID;
	}

	public void setLaborAuditID(Long laborAuditID) {
		this.laborAuditID = laborAuditID;
	}

	public void setDealerGroupSelected(boolean dealerGroupSelected) {
        this.dealerGroupSelected = dealerGroupSelected;
    }

    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }

    public void prepare() throws Exception {
        if (StringUtils.isNotBlank(this.id)) {
            Long pk = Long.parseLong(this.id);
            this.definition = this.laborRatesAdminService.findById(pk);
            if (this.definition.getCriteria() != null) {
                DealerCriterion dealerCriterion = this.definition.getCriteria()
                        .getDealerCriterion();
                if (dealerCriterion != null && dealerCriterion.isGroupCriterion()) {
                    this.dealerGroupSelected = true;
                }
            }
        } else {
            this.definition = new LaborRates();
        }

        setClaimTypes();
        populateCustomerTypes();
        setWarrantyTypes(this.warrantyService.listWarrantyTypes());
        
        currencyList = orgService.listUniqueCurrencies();
    	
    	for(Currency currency : currencyList )
		{
			LaborRateValues laborRateValues = new LaborRateValues();
			Money rate = Money.valueOf(new BigDecimal(0.00), currency,2);
			laborRateValues.setRate(rate);
			this.lbrRateValues.add(laborRateValues);
		}

    }

	@Override
	public void validate() {
		validateDealerCriterion();
		validateProductName();
		validateLaborRate();
		if (!hasActionErrors()) {
			sortRatesForDisplay();
			for (Iterator<LaborRate> iter = this.rates.iterator(); iter.hasNext();) {
				LaborRate lrate = iter.next();
				if (lrate == null
						|| (lrate.getDuration().getFromDate() == null && lrate.getDuration().getTillDate() == null)) {
					iter.remove();
				}
			}

			Criteria forCriteria = this.definition.getForCriteria();
			if (forCriteria == null) {
				this.definition.setForCriteria(new Criteria());
			}
			for (LaborRate laborRate : getRates()) {
				for (LaborRateValues laborRateValues : laborRate.getLaborRateValues()) {
					if (laborRateValues.getRate() == null) {
						Money rate = Money.valueOf(new BigDecimal(0.00), laborRateValues.getCurrency()
								.breachEncapsulationOfCurrency(), 2);
						laborRateValues.setRate(rate);
					}

				}
				laborRate.setLaborRates(this.definition);

			}
		}
	}

	public String showPrice() {
 		//isCreatePage = true;
		if (this.definition.getId() != null) {
			this.rates.addAll(this.definition.getRates());
		}
		sortRatesForDisplay();
		Criteria forCriteria = this.definition.getCriteria();
		if (forCriteria != null && forCriteria.getDealerCriterion() != null) {
			this.dealerCriterion = forCriteria.getDealerCriterion().getIdentifier();
			this.dealerGroupName = forCriteria.getDealerCriterion().getIdentifier();
		}
		if (forCriteria != null && forCriteria.getProductType() != null) {
			this.productType = forCriteria.getProductType().getName();
		}
		return SUCCESS;
	}

	private void sortRatesForDisplay() {
		if(rates.size() > 0){
			Collections.sort(this.rates);
			for (LaborRate rate : rates) {
				Collections.sort(rate.getLaborRateValues(),new Comparator<LaborRateValues>(){
					public int compare(LaborRateValues arg0, LaborRateValues arg1) {
						return arg0.getRate().breachEncapsulationOfCurrency().getCurrencyCode().compareToIgnoreCase(arg1.getRate().breachEncapsulationOfCurrency().getCurrencyCode());
					}
	    		});
			}
		}
	}

    public String createPrice() {
        String action = preparePrice();
        if (SUCCESS.equals(action)) {
            try {
                this.laborRatesAdminService.saveLaborRates(this.definition,this.laborComments);
            } catch (Exception e) {
                this.logger.error("Exception in Creating LaborRate Configuration", e);
                return INPUT;
            }
            addActionMessage(MESSAGE_KEY_CREATE);
        }
        return action;
    }

    public String updatePrice() {
        String action = preparePrice();
        if (SUCCESS.equals(action)) {
            try {
                this.laborRatesAdminService.updateLaborRates(this.definition,this.laborComments);
                Criteria forCriteria = this.definition.getCriteria();
        		if (forCriteria != null && forCriteria.getDealerCriterion() != null) {
        			this.dealerCriterion = forCriteria.getDealerCriterion().getIdentifier();
        			this.dealerGroupName = forCriteria.getDealerCriterion().getIdentifier();
        		}
            } catch (Exception e) {
                this.logger.error("Exception in Updating LaborRate Configuration", e);
                return INPUT;
            }
            addActionMessage(MESSAGE_KEY_UPDATE);
        }
        return action;
    }

    public String deleteLaborPrice() {
    	Long pk = Long.parseLong(this.id);
    	definition = this.laborRatesAdminService.findById(pk);
         try {
                this.laborRatesAdminService.delete(this.definition);
             } catch (Exception e) {
                this.logger.error("Exception in Deleteing LaborRate Configuration", e);
                return INPUT;
            }
            addActionMessage(MESSAGE_KEY_DELETE);
        
        return SUCCESS;
    }

    public String getWarrantyTypeString() {
        Criteria forCriteria = this.definition.getForCriteria();
        if (forCriteria == null || forCriteria.getWarrantyType() == null) {
            return getText("label.common.allWarrantyTypes");
        }
        return forCriteria.getWarrantyType();
    }

    public String getClaimTypeString() {
        Criteria forCriteria = this.definition.getForCriteria();
        if (forCriteria == null || forCriteria.getClaimType() == null) {
            return getText("label.common.allClaimTypes");
        }
        return getText(forCriteria.getClaimType().getDisplayType());
    }

    public String getDealerString() {
        Criteria forCriteria = this.definition.getCriteria();
        if (forCriteria == null || forCriteria.getDealerCriterion() == null) {
            return getText("label.common.allDealers");
        }
        return forCriteria.getDealerCriterion().getIdentifier();
    }

    public String getProductTypeString() {
        Criteria forCriteria = this.definition.getForCriteria();
        if (forCriteria == null || forCriteria.getProductType() == null) {
            return getText("label.common.allProductTypes");
        }
        return forCriteria.getProductType().getName();
    }
    
    public String getHistory(){
    	setLaborRateAudit(laborHistoryRepository.find(laborAuditID));
        return "success";
    }

/*    public String getCustomerString() {
        Criteria forCriteria = this.definition.getForCriteria();
        if (forCriteria == null || forCriteria.getCustomer() == null) {
            return getText("label.common.allCustomers");
        }
        return forCriteria.getCustomer().getCompanyName();
    }*/

    // ********************* Accessors & Mutators ****************//

    public LaborRates getDefinition() {
        return this.definition;
    }

    public void setDefinition(LaborRates configuration) {
        this.definition = configuration;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<LaborRate> getRates() {
        return this.rates;
    }

    public void setRates(List<LaborRate> rates) {
        this.rates = rates;
    }

    public void setLaborRatesAdminService(LaborRatesAdminService laborRatesAdminService) {
        this.laborRatesAdminService = laborRatesAdminService;
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

    // **************** Private Methods ******************//
    private String preparePrice() {
        Set<Long> idsFromUI = new HashSet<Long>();
        for (LaborRate rate : this.rates) {
            if (rate.getId() != null) {
                idsFromUI.add(rate.getId());
            }
            else {
				this.definition.getRates().add(rate);
			}
        }

        for (Iterator<LaborRate> it = this.definition.getRates().iterator(); it.hasNext();) {
            LaborRate rate = it.next();
            if (rate.getId() != null && !idsFromUI.contains(rate.getId())) {
            	rate.getD().setActive(Boolean.FALSE);
            }
        }

        if (!this.laborRatesAdminService.isUnique(this.definition)) {
            addActionError(MESSAGE_KEY_DUPLICATE);
            return INPUT;
        }
//        this.definition.getRates().clear();
//        this.definition.getRates().addAll(rates);
        return SUCCESS;
    }

    // ****** Private Methods *************//
    private void validateDealerCriterion() {
    	Criteria forCriteria = this.definition.getForCriteria();
        if (!this.dealerGroupSelected && StringUtils.isNotBlank(this.dealerCriterion)) {
        	ServiceProvider dealership = this.orgService.findDealerByName(this.dealerCriterion);
            if (dealership == null) {
                addFieldError("dealerCriterion", "error.manageRates.invalidDealer",
                        new String[] { this.dealerCriterion });
            } else {
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
                DealerCriterion criterion = new DealerCriterion();
                criterion.setDealerGroup(dealerGroup);
                forCriteria.setDealerCriterion(criterion);
            }
        } else {
            forCriteria.setDealerCriterion(null);
        }
    }

    private void validateProductName() {
        Criteria forCriteria = this.definition.getForCriteria();
        if (StringUtils.isNotBlank(this.productType)) {
            ItemGroup itemGroup = this.catalogService.findItemGroupByDescription(this.productType);
            if (itemGroup == null) {
                addActionError("error.manageRates.invalidProductType", new String[] { this.productType });
            }
            forCriteria.setProductType(itemGroup);
        } else {
            forCriteria.setProductType(null);
        }
    }
    
	private void validateLaborRate() {
		int numRates = this.rates.size();
		if (this.rates.size() == 0) {
			addActionError("error.manageRates.moreConfigRequired");
			return;
		}

		for (LaborRate rate : rates) {
			if (rate != null && rate.getDuration() != null) {
				CalendarDate startDate = rate.getDuration().getFromDate();
				CalendarDate endDate = rate.getDuration().getTillDate();
				if (startDate == null) {
					addActionError("error.manageRates.startDateNotSpecified");
				}
				if (endDate == null) {
					addActionError("error.manageRates.endDateNotSpecified");
				}
			} else {
				addActionError("error.manageRates.invalidDateSpecified");
			}
		}
		if (hasActionErrors()) {
			return;
		}
		if (numRates < 2) {
			CalendarDate startDate = this.rates.get(0).getDuration().getFromDate();
			CalendarDate endDate = this.rates.get(0).getDuration().getTillDate();
			if (startDate.isAfter(endDate)) {
				addActionError("error.manageRates.endDateBeforeStartDate", new String[] { endDate.toString(DATE_FORMAT),
						startDate.toString(DATE_FORMAT) });
			}

			for (LaborRateValues laborRateValues : this.rates.get(0).getLaborRateValues()) {

				if (laborRateValues.getRate().breachEncapsulationOfAmount().signum() < 0) {
					addActionError("error.manageRates.invalidLabourRate");

				}
			}
			return;
		}
		for (int i = 1; i < numRates; i++) {
			CalendarDuration thisLRDuration = this.rates.get(i).getDuration();
			CalendarDuration prevLRDuration = this.rates.get(i - 1).getDuration();
			CalendarDate preStartDate = prevLRDuration.getFromDate();
			CalendarDate prevEndDate = prevLRDuration.getTillDate();
			CalendarDate currentStartDate = thisLRDuration.getFromDate();
			CalendarDate currentEndDate = thisLRDuration.getTillDate();
			if ((preStartDate.isAfter(prevEndDate) || currentStartDate.isAfter(currentEndDate))){
				addActionError("error.manageRates.endDateBeforeStartDate", new String[] { prevEndDate.toString(DATE_FORMAT),
						currentStartDate.toString(DATE_FORMAT) });
			}

			if (!(prevEndDate.plusDays(1).equals(currentStartDate))) {
				addActionError("error.manageRates.noGapsInConsecutiveDateRange", new String[] { prevEndDate.toString(DATE_FORMAT),
						currentStartDate.toString(DATE_FORMAT) });

			}
			LaborRate laborRate = this.rates.get(i);
			for (LaborRateValues laborRateValues : laborRate.getLaborRateValues()) {
				if (laborRateValues.getRate().breachEncapsulationOfAmount().signum() < 0) {
					addActionError("error.manageRates.invalidLabourRate");

				}
			}
			if (hasActionErrors()) {
				return;
			}
		}
	}

	protected void populateCustomerTypes() {
		// need to put BU filter
		Map<Object, Object> keyValueOfCustomerTypes = this.configParamService
				.getKeyValuePairOfObjects(ConfigName.CUSTOMERS_FILING_CLAIMS.getName());

		if (keyValueOfCustomerTypes != null && !keyValueOfCustomerTypes.isEmpty()) {
			customerTypes.putAll(keyValueOfCustomerTypes);
		}
	}
    
    
    public String getDealerGroupName() {
        return this.dealerGroupName;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}


	public Map<Object, Object> getCustomerTypes() {
		return customerTypes;
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

    public List<Currency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<Currency> currencyList) {
		this.currencyList = currencyList;
	}

	public List<LaborRateValues> getLRateValues() {
		return lbrRateValues;
	}

	public void setLRateValues(List<LaborRateValues> rateValues) {
		lbrRateValues = rateValues;
	}

    public List<ClaimType> getClaimTypes() {
		return claimTypes;
	}

    public void setClaimTypes() {
        this.claimTypes = this.claimService.fetchAllClaimTypesForBusinessUnit();
	}

	public String getLaborComments() {
		return laborComments;
	}

	public void setLaborComments(String laborComments) {
		this.laborComments = laborComments;
	}
}
