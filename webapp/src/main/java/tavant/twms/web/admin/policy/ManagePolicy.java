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
package tavant.twms.web.admin.policy;

import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.POLICY_RULES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.common.I18NPolicyTermsAndConditions;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.ApplicableCustomerTypes;
import tavant.twms.domain.policy.Availability;
import tavant.twms.domain.policy.OwnershipState;
import tavant.twms.domain.policy.PolicyAdminService;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionService;
import tavant.twms.domain.policy.PolicyFees;
import tavant.twms.domain.policy.PolicyProductMapping;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.domain.orgmodel.ServiceProviderCertificationStatus;
import tavant.twms.domain.policy.TransferDetails;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.rules.DomainRuleAudit;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.i18n.ProductLocale;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.rules.SerializerFactory;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

@SuppressWarnings("serial")
public final class ManagePolicy extends I18nActionSupport implements Preparable, Validateable {
	
	@Override
	public void validate() {
        validatePolicyFees();
        if (!this.policyDefinitionService.isCodeUnique(this.policyDefinition)) {
            addActionError("error.managePolicy.codeNotUnique",getPolicyDefinition().getCode());
        }
        /*if (getPolicyDefinition()!=null && getPolicyDefinition().getId()==null && isInvalidPriority()) {
			addActionError("error.managePolicy.priorityNotUnique",getPolicyDefinition().getPriority().toString());
		}*/
        if(getPolicyDefinition()!=null && getPolicyDefinition().getWarrantyType().getType().equals(POLICY) && StringUtils.hasText(getPolicyDefinition().getNomsPolicyOptionCode())){
        	addActionError("error.policy.nomsCode");
        }
        // Get the Applicability Terms JSON
		updateApplicabilityTerms();
		updateApplicabilityTermsJSON();
		validateIfSeriesExist();
	}

	private void validateIfSeriesExist() {
		if(!this.selectedProducts.isEmpty()){
			for(PolicyProductMapping eachSeries:this.selectedProducts){
				if(eachSeries.getProduct().getId()==null){
					addActionError("error.managePolicy.invalidSeriesName");
					break;
				}
			}
		}
		
	}

	private void validatePolicyFees() {
        boolean invalidRegistrationFee = false;
        boolean invalidTransferFee = false;
        for (PolicyFees regFee : getPolicyDefinition().getPolicyFees()) {            
            if(!regFee.getIsTransferable()
                    && regFee.getPolicyFee().breachEncapsulationOfAmount().doubleValue()<new Double(0.00).doubleValue()){
                invalidRegistrationFee=true;
                break;
            }
        }
        if (getPolicyDefinition().getTransferDetails().getTransferable()) {
            for (PolicyFees transferFee : getPolicyDefinition().getPolicyFees()) {
                if (!transferFee.getIsTransferable()
                        && transferFee.getPolicyFee().breachEncapsulationOfAmount().doubleValue() < new Double(0.00).doubleValue()) {
                    invalidTransferFee = true;
                    break;
                }
            }
        }
        if(invalidRegistrationFee){
           addActionError("error.manageRates.invalidRegistrationFee");
        }
        if(invalidTransferFee){
           addActionError("error.managePolicy.invalidTransferFee");
        }
	}

	public void prepare() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(" prepare() ");
		}
		loadGlobals();
		loadEntities();
		locales = productLocaleService.findAll();
        currencyList = orgService.listUniqueCurrencies();
    }

	public String newPolicy() {
        CalendarDate today = Clock.today();
		CalendarDate oneYearFromToday = today.plusMonths(12);
		Availability availability = new Availability();
		availability.setDuration(new CalendarDuration());
		this.policyDefinition.setAvailability(availability);
		availability.getDuration().setFromDate(today);
		availability.getDuration().setTillDate(oneYearFromToday);
		return SUCCESS;
	}

	public String activatePolicy() throws Exception {
		this.policyDefinition.activate();
		Collections.reverse(policyDefinition.getPolicyDefinitionAudits());
		this.policyDefinitionService.update(this.policyDefinition);
		Collections.reverse(policyDefinition.getPolicyDefinitionAudits());
		addActionMessage("message.managePolicy.activateSuccess");
		synchronizeViewWithModel();
		return SUCCESS;
	}

	public String deActivatePolicy() throws Exception {
		this.policyDefinition.deactivate();
		Collections.reverse(policyDefinition.getPolicyDefinitionAudits());
		this.policyDefinitionService.update(this.policyDefinition);
		Collections.reverse(policyDefinition.getPolicyDefinitionAudits());
		addActionMessage("message.managePolicy.deactivateSuccess");
		synchronizeViewWithModel();
		return SUCCESS;
	}

	public String createPolicy() throws Exception {
		synchronizeModelWithView();
		this.policyDefinitionService.save(this.policyDefinition);
		this.policyDefinitionId = this.policyDefinition.getId();
		if (logger.isDebugEnabled()) {
			logger.debug("Done creating policy. Id of new policy is \""
					+ this.policyDefinition.getId() + "\"");
		}
		synchronizeViewWithModel();
		addActionMessage("message.managePolicy.createSuccess");
		return SUCCESS;
	}

	public String updatePolicy() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Updating Policy#" + this.policyDefinition.getId()
					+ "");
			logger.debug("applicableTerms (rule ids) = "
					+ this.applicabilityTerms);
		}
		//prepareTermsAndConditions();
		Collections.reverse(policyDefinition.getPolicyDefinitionAudits());
		synchronizeModelWithView();
		this.policyDefinitionService.update(this.policyDefinition);
		synchronizeViewWithModel();
		Collections.reverse(policyDefinition.getPolicyDefinitionAudits());
		addActionMessage("message.managePolicy.updateSuccess");
		return SUCCESS;
	}

	public String updateI18nTerms() {
		prepareTermsAndConditions();
		this.policyDefinitionService.update(this.policyDefinition);
		addActionMessage("message.policy.termsCreationSuccess");
		return SUCCESS;
	}

	public String showRuleTemplateSearchPage() {
		return SUCCESS;
	}

	public String deleteTerm() throws Exception {
		this.policyDefinitionService.update(this.policyDefinition);
		return SUCCESS;
	}

	public String showPreview() {
		synchronizeViewWithModel();
		return SUCCESS;
	}

	public String showDetail() {
		synchronizeViewWithModel();
		return SUCCESS;
	}

	public String  listItemgroupsOfDifferentPurposes() {
		try {
			
			List<ItemGroup> itemGroups = catalogService.findAllItemGroupsWithNameLike(
					getSearchPrefix(), 0, 10);
			return generateAndWriteComboboxJson(itemGroups,"id","nameAndParentName");
		} catch (Exception e) {
			logger.error("Error while generating JSON", e);
			throw new RuntimeException("Error while generating JSON", e);
		}
	}

	public String getServiceProviderNames() {
		try {
			List<ServiceProvider> serviceProviders = orgService
					.findDealersWhoseNameStartsWith(getSearchPrefix(), 0, 10);
			return generateAndWriteComboboxJson(serviceProviders, "id", "name");
		} catch (Exception e) {
			logger.error("Error while generating JSON", e);
			throw new RuntimeException("Error while generating JSON", e);
		}
	}
	
	public String listDealerGroupsStandard(){
		try {
			
			List<DealerGroup> dealerGroups = dealerGroupService.findDealerGroupsWithNameLike(getSearchPrefix(), new PageSpecification(0, 10), AdminConstants.STANDARD);
			return generateAndWriteComboboxJson(dealerGroups, "name", "name");
		} catch (Exception e) {
			logger.error("Error while generating JSON", e);
			throw new RuntimeException("Error while generating JSON", e);
		}
	}
	
	public String listDealerGroupsExtended(){
		try {
			
			List<DealerGroup> dealerGroups = dealerGroupService.findDealerGroupsWithNameLike(getSearchPrefix(), new PageSpecification(0, 10), AdminConstants.EXTENDED);
			return generateAndWriteComboboxJson(dealerGroups, "name", "name");
		} catch (Exception e) {
			logger.error("Error while generating JSON", e);
			throw new RuntimeException("Error while generating JSON", e);
		}
	}

	/**
	 *
	 */
	protected void loadEntities() {
        Long idToBeUsed = null;
		if (this.policyDefinitionId != null) {
			idToBeUsed = this.policyDefinitionId;
		} else if (this.policyDefinition != null
				&& this.policyDefinition.getId() != null) {
			idToBeUsed = this.policyDefinition.getId();
		}

		if (idToBeUsed != null) {
			this.policyDefinition = this.policyDefinitionService
					.findPolicyDefinitionById(idToBeUsed);
			Collections.reverse(policyDefinition.getPolicyDefinitionAudits());
			if (logger.isDebugEnabled()) {
				if (this.policyDefinition != null) {
					logger.debug(" Loaded Policy#"
							+ this.policyDefinition.getId());
				} else {
					logger.debug(" Failued to load Policy#" + idToBeUsed);
				}
			}

		} else {
			this.policyDefinition = new PolicyDefinition();
		}
	}

	public void prepareTermsAndConditions() {
		policyDefinition.getI18NPolicyTermsAndConditions().clear();
		Map params = ActionContext.getContext().getParameters();
		for (ProductLocale locale : locales) {
			String i18nLocale = locale.getLocale();
			String localizedMessages[] = (String[]) params
					.get("localizedFailureMessages_" + locale.getLocale());
			if (localizedMessages != null) {
				I18NPolicyTermsAndConditions i18nPolicyTerms = new I18NPolicyTermsAndConditions();
				i18nPolicyTerms.setTermsAndConditions(localizedMessages[0]);
				i18nPolicyTerms.setLocale(i18nLocale);
				policyDefinition.getI18NPolicyTermsAndConditions().add(
						i18nPolicyTerms);
			}
		}

	}

	public void synchronizeViewWithModel() {
        populateSelectedProducts(getPolicyDefinition());
        populateSelectedDealersAndDealerGroups(getPolicyDefinition());
        populateSelectedCustomerTypes(getPolicyDefinition());
        updateApplicabilityTermsJSON();
	}

	private void updateApplicabilityTermsJSON() {
		List<DomainPredicate> domainPredicates = new ArrayList<DomainPredicate>();
		for (DomainRule domainRule : this.policyDefinition
				.getApplicabilityTerms()) {
			domainPredicates.add(domainRule.getPredicate());
		}
		try {
			this.applicabilityTermsJSON = this.serializerFactory
					.getRuleJSONSerializer(POLICY_RULES).toJSONArray(
							domainPredicates).toString();
			if (logger.isDebugEnabled()) {
				logger.debug(" JSON string for applicability terms ["
						+ this.applicabilityTermsJSON + "] ");
			}
		} catch (JSONException e) {
			logger.error("Failed to JSONize ", e);
		}
	}

	public void synchronizeModelWithView() {
        addSelectedProductsToPolicy(getPolicyDefinition());
        addSelectedDealersAndDealerGroupsToPolicy(getPolicyDefinition());
        populateApplicableCustomerType(getPolicyDefinition());
        updateApplicabilityTerms();
	}

	protected void updateApplicabilityTerms() {
		String[] ids = StringUtils.tokenizeToStringArray(
				this.applicabilityTerms != null ? this.applicabilityTerms : "",
				",");
		List<String> readOnlyList = Arrays.asList(ids);

		if (logger.isDebugEnabled()) {
			logger.debug(" term ids from UI [" + readOnlyList + "]");
		}

		// Remove the missing ones.
		List<String> idsFromUI = new ArrayList<String>();
		idsFromUI.addAll(readOnlyList);
		if (logger.isDebugEnabled()) {
			logger.debug(" applicability terms are [" + this.applicabilityTerms
					+ "] ");
		}

		Set<DomainRule> applicabilityTermsInStore = this.policyDefinition
				.getApplicabilityTerms();
		for (Iterator<DomainRule> iterator = applicabilityTermsInStore
				.iterator(); iterator.hasNext();) {
			DomainRule rule = iterator.next();
			DomainPredicate predicate = rule.getPredicate();
			String idAsString = predicate.getId().toString();
			boolean presentInStoreButMissingInView = !idsFromUI
					.contains(idAsString);
			if (presentInStoreButMissingInView) {
				iterator.remove();
				if (logger.isDebugEnabled()) {
					logger.debug(" removed predicate [" + idAsString + "]");
				}
			} else {
				// Existing ones won't get added if we remove it now.
				idsFromUI.remove(idAsString);
			}
		}

		// Only the missing ones get added.
		Set<Long> idsFromUIAsLong = new HashSet<Long>();
		for (String termId : idsFromUI) {
			Long id = Long.parseLong(termId);
			idsFromUIAsLong.add(id);
		}

		if (idsFromUIAsLong.size() > 0) {
			Integer latestRuleNumber = this.predicateAdministrationService
					.findMaxRuleNumberForContext();
			List<DomainPredicate> domainPredicates = this.predicateAdministrationService
					.findByIds(idsFromUIAsLong);
			for (DomainPredicate domainPredicate : domainPredicates) {
				DomainRule domainRule = new DomainRule();
				domainRule.setPredicate(domainPredicate);
				domainRule.setContext(POLICY_RULES);
				domainRule.setRuleNumber(latestRuleNumber + 1);
				domainRule.updateOgnlExpression();
				DomainRuleAudit domainRuleAudit = new DomainRuleAudit();
				domainRuleAudit.setName(domainPredicate.getName());
				domainRule.getRuleAudits().add(domainRuleAudit);
				this.policyDefinition.addApplicabilityTerm(domainRule);
				latestRuleNumber++;
			}
		}
	}

	protected void loadGlobals() {
        this.yesNo.put(0, "No");
        this.yesNo.put(1, "Yes");
        List<WarrantyType> allWarrantyTypes = this.warrantyService.listWarrantyTypes();
       if (allWarrantyTypes != null && !allWarrantyTypes.isEmpty()) {
            setWarrantyTypes(allWarrantyTypes);
       	 }       
        for (ServiceProviderCertificationStatus certificationStaus: ServiceProviderCertificationStatus.values())
        {
        	certificationTypes.add(certificationStaus);
        }
        populateCustomerTypesBasedOnBUConfig();
        setAllInventoryItemCondition(this.inventoryService.listInventoryItemConditionTypes());
		removeScrapInvItemCondition(getAllInventoryItemCondition());
		allOwnershipStates = this.policyAdminService.findAllOwnershipStates();
	}

	private static Logger logger = LogManager.getLogger(ManagePolicy.class);

	private InventoryService inventoryService;
	private WarrantyService warrantyService;
	private PolicyDefinitionService policyDefinitionService;
	private PredicateAdministrationService predicateAdministrationService;
	private SerializerFactory serializerFactory;
	private static final String STANDARD = "STANDARD";
	private static final String POLICY = "POLICY";

	/*
	 * private RuleJSONSerializer ruleJSONSerializer = new
	 * RuleJSONSerializer(POLICY_RULES);
	 */
	private CatalogService catalogService;

	@Required
	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}

	@Required
	public void setPolicyDefinitionService(
			PolicyDefinitionService policyDefinitionService) {
		this.policyDefinitionService = policyDefinitionService;
	}

	@Required
	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	@Required
	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@Required
	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	// Model elements and their shadow copies for view.
 //   List<ItemGroup> selectedProducts = new ArrayList<ItemGroup>();
    List<PolicyProductMapping> selectedProducts = new ArrayList<PolicyProductMapping>();
    
	
	public List<PolicyProductMapping> getSelectedProducts() {
		return selectedProducts;
	}

	public void setSelectedProducts(List<PolicyProductMapping> selectedProducts) {
		this.selectedProducts = selectedProducts;
	}

	List<ServiceProvider> selectedDealers = new ArrayList<ServiceProvider>();
	List<String> selectedDealerGroups = new ArrayList<String>();
	private PolicyDefinition policyDefinition;
	private Long policyDefinitionId;
	private List<OwnershipState> allOwnershipStates = new ArrayList<OwnershipState>();
    private List<InventoryItemCondition> allInventoryItemCondition = new ArrayList<InventoryItemCondition>();
	private final Map<Integer, String> yesNo = new HashMap<Integer, String>();
	private List<WarrantyType> warrantyTypes = new ArrayList<WarrantyType>();
	private List<ServiceProviderCertificationStatus> certificationTypes = new ArrayList<ServiceProviderCertificationStatus>();
	private List<Currency> currencyList = new ArrayList<Currency>();
    private List<String> allCustomerTypes = new ArrayList<String>();
    private String type;
	private String templateNamePattern;
	private String applicabilityTerms;// comma separated string of ids added.
	private String applicabilityTermsJSON;
	private String ownerState;
	private List<ProductLocale> locales;
	private ProductLocaleService productLocaleService;
	private Set<String> selectedCustomerTypes = new HashSet<String>();
	private ConfigParamService configParamService;
	private Set<String> itemConditions = new HashSet<String>();
	private DealerGroupService dealerGroupService;
	String baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency().getSymbol();

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public DealerGroupService getDealerGroupService() {
		return dealerGroupService;
	}

	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}

	public String getApplicabilityTermsJSON() {
		return this.applicabilityTermsJSON;
	}

	public PolicyDefinition getPolicyDefinition() {
		return this.policyDefinition;
	}

	public void setPolicyDefinition(PolicyDefinition policyDefinition) {
		this.policyDefinition = policyDefinition;
	}

	public String getTemplateNamePattern() {
		return this.templateNamePattern;
	}

	public void setTemplateNamePattern(String searchFor) {
		this.templateNamePattern = searchFor;
	}

	public Map<Integer, String> getYesNo() {
		return this.yesNo;
	}

	public Long getPolicyDefinitionId() {
		return this.policyDefinitionId;
	}

	public void setPolicyDefinitionId(Long id) {
		this.policyDefinitionId = id;
	}

	public Integer getTransferable() {
		return this.policyDefinition != null ? this.policyDefinition
				.isTransferable() ? 1 : 0 : 0;
	}

	public void setTransferable(Integer integer) {
		if (this.policyDefinition != null) {
			TransferDetails transferDetails = this.policyDefinition
					.getTransferDetails();
			transferDetails.setTransferable(integer == 1);
		}
	}

	public void setId(Long id) {
		this.policyDefinitionId = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getApplicabilityTerms() {
		return this.applicabilityTerms;
	}

	public void setApplicabilityTerms(String applicabilityTerms) {
		this.applicabilityTerms = applicabilityTerms;
	}

	private String productsPrefix;
	private String jsonString;
	private PolicyAdminService policyAdminService;

	public String getJsonString() {
		return this.jsonString;
	}

	public String getProductsPrefix() {
		return this.productsPrefix;
	}

	public void setProductsPrefix(String productsPrefix) {
		this.productsPrefix = productsPrefix;
	}

	@Required
	public void setPolicyAdminService(PolicyAdminService policyAdminService) {
		this.policyAdminService = policyAdminService;
	}

	/**
	 * This method is directly called from the jsp.
	 *
	 * @return list of OwnershipState.
	 */

	public SerializerFactory getSerializerFactory() {
		return this.serializerFactory;
	}

	@Required
	public void setSerializerFactory(SerializerFactory serializerFactory) {
		this.serializerFactory = serializerFactory;
	}
	
	private boolean isInvalidPriority() {
		Long priority = this.policyDefinition.getPriority();
		PolicyDefinition policy = this.policyDefinitionService
				.findPolicyDefinitionsWithPriority(priority);
		if (policy != null) {
			return true;
		}
		return false;
	}

	public List<OwnershipState> getAllOwnershipStates() {
		return allOwnershipStates;
	}

	public String getOwnerState() {
		return ownerState;
	}

	public void setOwnerState(String ownerState) {
		this.ownerState = ownerState;
	}



	public List<ProductLocale> getLocales() {
		return locales;
	}

	public void setLocales(List<ProductLocale> locales) {
		this.locales = locales;
	}

	public ProductLocaleService getProductLocaleService() {
		return productLocaleService;
	}

	public void setProductLocaleService(
			ProductLocaleService productLocaleService) {
		this.productLocaleService = productLocaleService;
	}
	
	public List<WarrantyType> getWarrantyTypes() {
		return warrantyTypes;
	}

	public void setWarrantyTypes(List<WarrantyType> warrantyTypes) {
		this.warrantyTypes = warrantyTypes;
	}

	protected void populateCustomerTypesBasedOnBUConfig() {
		Map<Object, Object> keyValueOfCustomerTypes = this.configParamService
				.getKeyValuePairOfObjects(ConfigName.CUSTOMERS_FOR_POLICY.getName());
		if (keyValueOfCustomerTypes != null
				&& !keyValueOfCustomerTypes.isEmpty()) {
            allCustomerTypes.clear();
            for(Object customerTypeObject: keyValueOfCustomerTypes.keySet()){
            	allCustomerTypes.add((String)customerTypeObject);
            }
        }

	}

	public Set<String> getSelectedCustomerTypes() {
		return selectedCustomerTypes;
	}

	public void setSelectedCustomerTypes(Set<String> selectedCustomerTypes) {
		this.selectedCustomerTypes = selectedCustomerTypes;
	}

    @Required
    public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public Set<String> getItemConditions() {
		return itemConditions;
	}

	public void setItemConditions(Set<String> itemConditions) {
		this.itemConditions = itemConditions;
	}

	public List<Currency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<Currency> currencyList) {
		this.currencyList = currencyList;
	}

    public List<InventoryItemCondition> getAllInventoryItemCondition() {
        return allInventoryItemCondition;
    }

    public void setAllInventoryItemCondition(List<InventoryItemCondition> allInventoryItemCondition) {
        this.allInventoryItemCondition = allInventoryItemCondition;
    }

    private void removeScrapInvItemCondition(List<InventoryItemCondition> itemConditions){
        for(Iterator iter = itemConditions.iterator();iter.hasNext();){
            InventoryItemCondition itemCondition = (InventoryItemCondition)iter.next();
            if(InventoryItemCondition.SCRAP.getItemCondition()
                    .equals(itemCondition.getItemCondition())
                    || InventoryItemCondition.BOTH.getItemCondition()
                    .equals(itemCondition.getItemCondition())){
                iter.remove();
            }
        }
    }

    public List<String> getSelectedDealerGroups() {
		return selectedDealerGroups;
	}

	public void setSelectedDealerGroups(List<String> selectedDealerGroups) {
		this.selectedDealerGroups = selectedDealerGroups;
	}

	public List<ServiceProvider> getSelectedDealers() {
		return selectedDealers;
	}

	public void setSelectedDealers(List<ServiceProvider> selectedDealers) {
		this.selectedDealers = selectedDealers;
	}

    private void addSelectedProductsToPolicy(PolicyDefinition policyDefn){
    	policyDefn.getAvailability().getProducts().clear();
    	policyDefn.getAvailability().getProducts().addAll(getSelectedProducts());
    }    
    
    private void addSelectedDealersAndDealerGroupsToPolicy(
    		PolicyDefinition policyDefinition) {
    		policyDefinition.getApplicableServiceProviders().clear();
    		policyDefinition.getApplicableDealerGroups().clear();
    		policyDefinition.getApplicableServiceProviders().addAll(getSelectedDealers());
    		String warrantyType = this.policyDefinition.getWarrantyType().getType();
    		String purpose = warrantyType.equals(STANDARD) ? AdminConstants.STANDARD
    					: AdminConstants.EXTENDED;
    		if(selectedDealerGroups.size()>0)
    		{
    			List<DealerGroup> dealerGroup = dealerGroupService.findByNamesAndPurpose(
    					selectedDealerGroups, purpose);
    						policyDefinition.getApplicableDealerGroups().addAll(dealerGroup);
    		}
    	}

    public List<String> getAllCustomerTypes() {
		return allCustomerTypes;
	}

	public void setAllCustomerTypes(List<String> allCustomerTypes) {
		this.allCustomerTypes = allCustomerTypes;
	}

	private void populateSelectedProducts(PolicyDefinition policyDefinition){
       getSelectedProducts().clear();
       getSelectedProducts().addAll(policyDefinition.getAvailability().getProducts());
       Collections.sort(getSelectedProducts());
    }

	private void populateSelectedDealersAndDealerGroups(
		PolicyDefinition policyDefinition) {
		getSelectedDealers().clear();
		getSelectedDealerGroups().clear();
		getSelectedDealers().addAll(policyDefinition.getApplicableServiceProviders());
		for (DealerGroup dealerGroup : policyDefinition.getApplicableDealerGroups()) {
			if(dealerGroup!=null)
			{
			getSelectedDealerGroups().add(dealerGroup.getName().toUpperCase());
			}
		}
		
	}
	
    private void populateApplicableCustomerType(PolicyDefinition policyDefinition){
        Set<String> selectedCustomerList = new HashSet<String>();
		Set<ApplicableCustomerTypes> customerList = new HashSet<ApplicableCustomerTypes>();
        for (Iterator<String> selectCusList = this.selectedCustomerTypes.iterator(); selectCusList.hasNext();) {
            String type = selectCusList.next();
            selectedCustomerList.add(type);
        }

		for (Iterator<String> iterator = selectedCustomerList.iterator(); iterator.hasNext();) {
			String type = iterator.next();
			customerList.add(new ApplicableCustomerTypes(type));

		}
        policyDefinition.getCustomertypes().clear();
		policyDefinition.getCustomertypes().addAll(customerList);
    }

    private void populateSelectedCustomerTypes(PolicyDefinition policyDefinition){
        if (policyDefinition.getCustomertypes() != null
				&& !policyDefinition.getCustomertypes().isEmpty()) {
			for (Iterator<ApplicableCustomerTypes> iterator = policyDefinition.getCustomertypes().iterator(); iterator.hasNext();) {
				ApplicableCustomerTypes customerType = iterator.next();
				this.selectedCustomerTypes.add(customerType.getType());
			}
		}
    }

	/**
	 * @param certificationTypes the certificationTypes to set
	 */
	public void setCertificationTypes(List<ServiceProviderCertificationStatus> certificationTypes) {
		this.certificationTypes = certificationTypes;
	}

	/**
	 * @return the certificationTypes
	 */
	public List<ServiceProviderCertificationStatus> getCertificationTypes() {
		return certificationTypes;
	}
}