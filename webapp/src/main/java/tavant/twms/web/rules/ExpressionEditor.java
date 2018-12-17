/**
 *
 */
package tavant.twms.web.rules;

import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.CLAIM_DUPLICACY_RULES;
import static tavant.twms.web.admin.rules.CreateProcessorRoutingRules.CLAIM_ASSIGNMENT_PURPOSE;
import static tavant.twms.web.admin.rules.CreateProcessorRoutingRules.DSM_ADVISOR_ASSIGNMENT_PURPOSE;
import static tavant.twms.web.admin.rules.CreateProcessorRoutingRules.DSM_ASSIGNMENT_PURPOSE;
import static tavant.twms.web.admin.rules.CreateProcessorRoutingRules.CP_ADVISOR_ASSIGNMENT_PURPOSE;
import static tavant.twms.domain.common.AdminConstants.DAYS_TO_FILE_CLAIM_PURPOSE;
import static tavant.twms.domain.common.AdminConstants.DAYS_TO_REPAIR_PURPOSE;
import static tavant.twms.domain.common.AdminConstants.DEFECTIVE_RETURN;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

 
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.businessobject.ClaimSearchBusinessObjectModel;
import tavant.twms.domain.businessobject.IBusinessObjectModel;
import tavant.twms.domain.businessobject.InventoryItemBusinessObjectModel;
import tavant.twms.domain.businessobject.ItemSearchBusinessObjectModel;
import tavant.twms.domain.businessobject.PartReturnSearchBusinessObjectModel;
import tavant.twms.domain.businessobject.RecoveryClaimSearchBusinessObjectModel;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.category.CategoryService;
import tavant.twms.domain.claim.ClaimAttributes;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.common.LabelService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.failurestruct.*;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.partreturn.Warehouse;
import tavant.twms.domain.partreturn.WarehouseService;
import tavant.twms.domain.policy.ApplicablePolicy;
import tavant.twms.domain.rules.*;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.rules.RuleJSONSerializer.ObjectificationContext;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.ValidationException;

/**
 * @author radhakrishnan.j
 */
@SuppressWarnings("serial")
public class ExpressionEditor extends I18nActionSupport implements Preparable {

	private static Logger logger = LogManager.getLogger(ExpressionEditor.class);

    protected static final String TRUE = "true";

	protected String context;

	protected Long id;
	/**
	 * Used by the UI to discard the changes and revert back to the "saved
	 * state".
	 */
	private String savedRuleName;

	private String ruleName;
	private String availableDuplicacyVariablesJSON;

	private String allAvailableDuplicacyVariablesJSON;

	private String availableVariablesJSON;

	private String allAvailableVariablesJSON;

	/** Perf Fix **/
	//private String availableRulesJSON;

	private boolean invertPredicate;

	/**
	 * Used by the UI to discard the changes and revert back to the "saved
	 * state".
	 */
	private String savedRuleTreeJSON;

	private String ruleTreeJSON;

        private String allCurrenciesJSON;

	private String referringPredicates;

	protected DomainPredicate domainPredicate = new DomainPredicate();

	protected PredicateAdministrationService predicateAdministrationService;

	private RuleJSONSerializer ruleJSONSerializer;

	private SerializerFactory serializerFactory;

	private SortedSet<String> ruleContexts = new TreeSet<String>();

	private List<DomainPredicate> rules = new ArrayList<DomainPredicate>();

	private boolean readOnly = false;
	
	private boolean systemCondition = false;

	private boolean duplicateCheck = false;
		
	private boolean inventoryContext = false;

	private final Map<Boolean, String> conditionTypes = new HashMap<Boolean, String>(5);

	private String categoryKind;

	private CategoryService categoryService;

	private LabelService labelService;
	
	private DealerGroupService dealerGroupService;
	
	private ItemGroupService itemGroupService;

    private String isCreateLabelForInventory;
    
    private boolean partsReplacedInstalledSectionVisible;
    
    private boolean buPartReplaceableByNonBUPart;
    
    private ConfigParamService configParamService;

    private RuleAdministrationService ruleAdministrationService;   
	

	// TODO: Better?
	private static final Map<String, Class> domainName2ClassNameMap = new HashMap<String, Class>() {
		{
			put("label.common.serviceProvider", ServiceProvider.class);
			put("label.claim.applicablePolicy", ApplicablePolicy.class);
			put("label.failure.failureCode", FaultCode.class);
			put("label.inventory.inventoryItem", InventoryItem.class);
			put("label.common.jobCode", ServiceProcedure.class);
			put("label.supplier.supplierTitle", Supplier.class);			
			put("label.common.causalPart",Item.class);			
			put("label.common.partReplaced",Item.class);
            put("label.common.model",ItemGroup.class);
            put("label.common.model",ItemGroup.class); 
            put("label.common.product",ItemGroup.class);
            put("label.item.unserializedItemModel", ItemGroup.class);
            put("label.common.status",PartReturnStatus.class); 
            put("label.common.item",Item.class);  
            put("label.campaign.campaign",Campaign.class);
            put("label.claim.claimState",ClaimState.class);
            put("label.common.warehouseLabelofReturnLocation",Warehouse.class);
            put("label.claim.part",Item.class);	
        }
	};


	@Required
	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public String getAvailableVariablesJSON() {
		return this.availableVariablesJSON;
	}

	public String getAllAvailableVariablesJSON() {
		return this.allAvailableVariablesJSON;
	}

	public String getAvailableDuplicacyVariablesJSON() {
		return this.availableDuplicacyVariablesJSON;
	}

	public String getAllAvailableDuplicacyVariablesJSON() {
		return this.allAvailableDuplicacyVariablesJSON;
	}

	/** Perf Fix - Begin **/
	/* public String getAvailableRulesJSON() {
		return this.availableRulesJSON;
	}*/
	/** Perf Fix - End **/

	public String getContext() {
		return this.context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getRuleName() {
		return this.ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleTreeJSON() {
		return this.ruleTreeJSON;
	}

	public void setRuleTreeJSON(String ruleTreeJSON) {
		this.ruleTreeJSON = ruleTreeJSON;
	}

	public boolean isDuplicateCheck() {
		return this.duplicateCheck;
	}

	public void setDuplicateCheck(boolean duplicateCheck) {
		this.duplicateCheck = duplicateCheck;

		// TODO: Better?!
		if (duplicateCheck) {
			this.context = CLAIM_DUPLICACY_RULES;

			if (this.ruleJSONSerializer != null) {
			   ruleJSONSerializer=getRuleJSONSerializer(CLAIM_DUPLICACY_RULES);
			}
		}
	}

	public PredicateAdministrationService getPredicateAdministrationService() {
		return this.predicateAdministrationService;
	}

	public SortedSet<String> getRuleContexts() {
		return this.ruleContexts;
	}

	public List<DomainPredicate> getRules() {
		return this.rules;
	}

	public DomainPredicate getDomainPredicate() {
		return this.domainPredicate;
	}

	public void setDomainPredicate(DomainPredicate domainPredicate) {
		this.domainPredicate = domainPredicate;
	}

	public Map<Boolean, String> getConditionTypes() {
		return this.conditionTypes;
	}

	public void prepare() throws Exception {

		this.conditionTypes.put(Boolean.FALSE,
				getText("label.manageBusinessCondition.normal"));
		this.conditionTypes.put(Boolean.TRUE,
				getText("label.manageBusinessCondition.duplicateCheck"));

		this.ruleJSONSerializer =getRuleJSONSerializer(this.context);

		if (this.id != null) {
		        if(logger.isDebugEnabled()){
		            logger.debug("Looking up object with id " + this.id);
		        }

			try {
				this.domainPredicate = this.predicateAdministrationService.findById(this.id);
				if(domainPredicate == null){
					try {
						this.ruleTreeJSON = this.ruleJSONSerializer.toJSON(new All()).toString();
						this.savedRuleName = "";
						return;
					} catch (JSONException e) {
						addActionError("error.manageBusinessCondition.unknown");
						return;
					}
				}
				this.ruleName = this.domainPredicate.getName();
				systemCondition=domainPredicate.isSystemDefinedCondition();
				Predicate corePredicate = this.domainPredicate.getPredicate();

				
				if (corePredicate instanceof Not) {
					this.invertPredicate = true;
					corePredicate = ((Not) corePredicate).getOperand();
				}
				// The UI expects an NaryPredicate, so if what we have is not
				// Nary, just wrap it in an All
				if (!(corePredicate instanceof NAryPredicate)) {

					All all = new All();
					all.getPredicates().add(corePredicate);
					corePredicate = all;
				} else {
					NAryPredicate nAryPredicate = (NAryPredicate) corePredicate;
					setDuplicateCheck(nAryPredicate.isQueryPredicate());
				}
				if(domainPredicate != null && domainPredicate.getContext()!=null && this.context.equalsIgnoreCase(domainPredicate.getContext())){
				JSONObject _JSONObject = this.ruleJSONSerializer
						.toJSON(corePredicate);
				
				this.ruleTreeJSON = _JSONObject.toString();
				IdentifyReadOnlyRules identifyReadOnlyRules = new IdentifyReadOnlyRules();
				
				corePredicate.accept(identifyReadOnlyRules);
				this.readOnly = identifyReadOnlyRules.isReadOnly();

				if(systemCondition){
					readOnly=true;
				}

				if (logger.isDebugEnabled()) {
					logger.debug(MessageFormat.format(" ruleTreeJSON = {0}",
							_JSONObject.toString(2)));
				 }
				}
				this.savedRuleName = this.domainPredicate.getName();

			} catch (JSONException e) {
				addActionError("error.manageBusinessCondition.unknown");
			}

		} else {
			try {
				this.ruleTreeJSON = this.ruleJSONSerializer.toJSON(new All()).toString();
				this.savedRuleName = "";
			} catch (JSONException e) {
				addActionError("error.manageBusinessCondition.unknown");
			}
		}
		
		this.savedRuleTreeJSON = this.ruleTreeJSON;
		initializeAvailableVariablesAndRules();
	}

	public String newExpression() {
		return SUCCESS;
	}

	public String listAllRuleContexts() {
		// IBusinessObjectModel bom = getBusinessObjectModel();
		Set<String> listAllContexts = BusinessObjectModelFactory.getInstance()
				.listAllRuleContexts();
		SortedSet<String> list = new TreeSet<String>();
		list.addAll(listAllContexts);
		this.ruleContexts = list;
		if (logger.isDebugEnabled()) {
			logger.debug(" rule contexts are -> " + this.ruleContexts);
		}
		return SUCCESS;
	}

	public String listRulesInContext() {
		this.rules = this.predicateAdministrationService.findAllRulesInContext(this.context,
				new PageSpecification()).getResult();
		return SUCCESS;
	}

	public String previewExpression() {
		this.readOnly = true;
		return SUCCESS;
	}

	public String viewExpression() {
		return SUCCESS;
	}

	protected void prepareDomainPredicate(boolean validate) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(MessageFormat.format(" ruleTreeJSON = {0} ",
					this.ruleTreeJSON));
		}

		ObjectificationContext objectificationContext = new ObjectificationContext();
		objectificationContext.setContextName(getContext());
		objectificationContext.setActionSupport(this);

		// try {
		Predicate predicate = this.ruleJSONSerializer.fromJSON(this.ruleTreeJSON,
				objectificationContext);
		if (this.invertPredicate) {
			predicate = new Not(predicate);
		}
		if (validate) {
			validateSaveOrUpdate(predicate);
		}

		if (errorsExist()) {
			// Temporary workaround for One2One breaking while re-rendering
			// post-validation.
			this.ruleTreeJSON = this.ruleJSONSerializer.toJSON(predicate).toString();

			throw new ValidationException("There are errors in the input.");
		}

		IdentifyReadOnlyRules identifyReadOnlyRules = new IdentifyReadOnlyRules();
		predicate.accept(identifyReadOnlyRules);
		this.readOnly = identifyReadOnlyRules.isReadOnly();

		this.domainPredicate.setPredicate(predicate);

		this.domainPredicate.setContext(this.duplicateCheck ? CLAIM_DUPLICACY_RULES
				: getContext());

		JSONArray referringPredicatesJSONArray = (JSONArray) new JSONTokener(
				this.referringPredicates).nextValue();

		this.domainPredicate.getRefersToPredicates().clear();
		int arrayLength = referringPredicatesJSONArray.length();

		for (int i = 0; i < arrayLength; i++) {
			Long predicateId = Long.parseLong(referringPredicatesJSONArray
					.getString(i));
			DomainPredicate referredPredicate = this.predicateAdministrationService
					.findById(predicateId);

			if (referredPredicate != null) {
				this.domainPredicate.addRefersToPredicate(referredPredicate);
			}
		}

	}

	public String saveExpression() throws Exception {
		boolean isUpdate = this.domainPredicate.getId() != null;
		this.invertPredicate = (this.request.getParameter("invertPredicate") != null);
		try {
			prepareDomainPredicate(true);
			String messageKey = "message.manageBusinessCondition.creatSuccess";

			if (isUpdate) {
				this.predicateAdministrationService.update(this.domainPredicate);
				messageKey = "message.manageBusinessCondition.updateSuccess";
			} else {
				this.predicateAdministrationService.save(this.domainPredicate);
			}

            List<DomainRule> domainRules = predicateAdministrationService.findRulesUsingPredicate(domainPredicate);
            for (DomainRule domainRule : domainRules) {
                domainRule.updateOgnlExpression();
                ruleAdministrationService.update(domainRule);
            }

			addActionMessage(messageKey);

		} catch (ValidationException e) {
			return INPUT;
		} catch (RuntimeException e) {
			logger.error("Failed to convert back from JSON", e);
			addActionError("error.manageBusinessCondition.unknown");
		} catch (PredicateAdministrationException ex) {
			logger.error("Failed to convert back from JSON", ex);
			addActionError("error.manageBusinessCondition.unknown");
		}

		if (isUpdate) {
			prepare();
			return INPUT;
		}

		return SUCCESS;
	}

	private boolean errorsExist() {
		return hasActionErrors() || hasFieldErrors();
	}

	private void validateSaveOrUpdate(Predicate predicate) {

		if (!StringUtils.hasText(this.domainPredicate.getName())) {
			addFieldError("domainPredicate.name",
					"error.manageBusinessCondition.nonEmptyNameRequired");
		}

		if (!isExpressionValid()) {
			addActionError("error.manageBusinessCondition.invalidExpression");
		}
		validatePredicateContext(predicate);
		validateExpressionUniqueness(predicate);
	}
	
	private String validatePredicateContext(Predicate predicate){
		if(domainPredicate != null && domainPredicate.getContext()!=null && !this.context.equalsIgnoreCase(domainPredicate.getContext())){
		/*if(domainPredicate != null && domainPredicate.getContext()!=null && ( (this.isDuplicateCheck() && domainPredicate.getContext().equalsIgnoreCase("ClaimRules")) 
				|| (!this.isDuplicateCheck() && domainPredicate.getContext().equalsIgnoreCase("ClaimDuplicacyRules")) ) ){*/
			addActionError("error.manageBusinessCondition.context");
		}
		if (errorsExist()) {
			return INPUT;
		}
		return SUCCESS;
	}

	private boolean isExpressionValid() {
		boolean expressionIsValid = StringUtils.hasText(this.ruleTreeJSON);

		if (expressionIsValid) {
			expressionIsValid = false;

			try {
				Object ruleTreeObj = new JSONTokener(this.ruleTreeJSON).nextValue();
				if (ruleTreeObj instanceof JSONObject) {
					JSONObject ruleTreeJSON = (JSONObject) ruleTreeObj;
					JSONArray nodes = ruleTreeJSON.getJSONArray("nodes");
					expressionIsValid = nodes.length() > 0;
				}
			} catch (JSONException e) {
				expressionIsValid = false;
			}
		}

		return expressionIsValid;
	}

	public String deleteExpression() throws PredicateAdministrationException {
		validateExpressionDeletion();

		if (errorsExist()) {
			return INPUT;
		}

		return SUCCESS;
	}

	private void validateExpressionDeletion()
			throws PredicateAdministrationException {

		List<DomainPredicate> predicatesReferringToPredicate = this.predicateAdministrationService
				.findPredicatesReferringToPredicate(this.domainPredicate);

		List<DomainRule> rulesUsingCurrentPredicate = this.predicateAdministrationService
				.findRulesUsingPredicate(this.domainPredicate);

		boolean proceedWithDeletion = predicatesReferringToPredicate.isEmpty()
				&& rulesUsingCurrentPredicate.isEmpty();

		if (proceedWithDeletion) {
			this.predicateAdministrationService.delete(this.domainPredicate);
			addActionMessage("message.manageBusinessCondition.deleteSuccess");
		} else {
			if (predicatesReferringToPredicate.isEmpty()) {
				StringBuffer sbRuleNames = new StringBuffer(100);

				for (DomainRule domainRule : rulesUsingCurrentPredicate) {
					sbRuleNames.append("\"");
					if (domainRule.getRuleAudits()!=null && !domainRule.getRuleAudits().isEmpty() && 
							domainRule.getRuleAudits().iterator().next()!=null)
						sbRuleNames.append(domainRule.getName());
					sbRuleNames.append("\"");
					sbRuleNames.append(", ");
				}

				int len = sbRuleNames.length();
				sbRuleNames.delete(len - 2, len);

				addActionError(
						"error.manageBusinessCondition.deleteFailedByRules",
						new String[] { sbRuleNames.toString() });
			} else {
				addActionErrorForPredicates(predicatesReferringToPredicate,
						"error.manageBusinessCondition.deleteFailedByConditions");
			}
		}

	}

	private void validateExpressionUniqueness(Predicate predicate) {
		DomainPredicate examplePredicate = new DomainPredicate();
		examplePredicate.setContext(getContext());
		examplePredicate.setName(this.domainPredicate.getName());

		examplePredicate.setPredicate(predicate);

		if (this.domainPredicate.getId() != null) {
			examplePredicate.setId(this.domainPredicate.getId());
		}

		List<DomainPredicate> clashingPredicates = findClashingPredicates(examplePredicate);

		if (clashingPredicates.size() > 0) {
			List<DomainPredicate> expressionClashes = new ArrayList<DomainPredicate>(
					5);

			boolean nameClashed = false;
			boolean expressionClashed = false;

			for (DomainPredicate clashingPredicate : clashingPredicates) {

				nameClashed = clashingPredicate.getName().equals(
						this.domainPredicate.getName());
				expressionClashed = clashingPredicate.getPredicateAsXML()
						.equals(new XStreamRuleSerializer().toXML(predicate));

				if (!nameClashed && expressionClashed) {
					expressionClashes.add(clashingPredicate);
				}
			}

			if (nameClashed && expressionClashed) {
				addActionError("error.manageBusinessCondition."
						+ "nameAndExpressionDuplicateExists");
				return;
			}

			if (nameClashed) {
				addActionError("error.manageBusinessCondition."
						+ "nameDuplicateExists");

			} else {
				addActionErrorForPredicates(expressionClashes,
						"error.manageBusinessCondition.expressionDuplicateExists");
			}
		}
	}

	protected List<DomainPredicate> findClashingPredicates(
			DomainPredicate examplePredicate) {
		return this.predicateAdministrationService
				.findClashingPredicates(examplePredicate);
	}

	private void addActionErrorForPredicates(List<DomainPredicate> predicates,
			String messageKey) {

		if (predicates.isEmpty()) {
			return;
		}

		StringBuffer sbPredicateNames = new StringBuffer(100);

		for (DomainPredicate predicate : predicates) {

			sbPredicateNames.append("\"");
			sbPredicateNames.append(predicate.getName());
			sbPredicateNames.append("\"");
			sbPredicateNames.append(", ");
		}

		int len = sbPredicateNames.length();
		sbPredicateNames.delete(len - 2, len);

		addActionError(messageKey, new String[] { sbPredicateNames.toString() });
	}
	
	public String fetchAllCategoriesOfAKind() throws IOException {
		Class clazz = domainName2ClassNameMap.get(this.categoryKind);
		JSONArray categoriesJSON = new JSONArray();
		if(clazz.equals(PartReturnStatus.class)) {
			boolean isInternalUser = this.isLoggedInUserAnInternalUser();
			List<PartReturnStatus> statusList= PartReturnStatus.getStatusListForSearch(isInternalUser);
			for(PartReturnStatus status : statusList) {
				JSONArray categoryLVPairJSON = new JSONArray();
				categoryLVPairJSON.put(status.getStatus());
				categoryLVPairJSON.put(status.name());
				categoriesJSON.put(categoryLVPairJSON);
			}
		}
		else if (clazz.equals(ServiceProvider.class)) {
			/*List<String> purposes = new ArrayList<String>(7);
			purposes.add(CLAIM_ASSIGNMENT_PURPOSE);
			purposes.add(DSM_ASSIGNMENT_PURPOSE);
			purposes.add(DSM_ADVISOR_ASSIGNMENT_PURPOSE);
			purposes.add(CP_ADVISOR_ASSIGNMENT_PURPOSE);
			purposes.add(DAYS_TO_REPAIR_PURPOSE);
			purposes.add(DAYS_TO_FILE_CLAIM_PURPOSE);
     		purposes.add(DEFECTIVE_RETURN);
			List<DealerGroup> dealerGroups = dealerGroupService.findDealerGroupsByPurposes(purposes);*/
			
			List<DealerGroup> dealerGroups = dealerGroupService.findAll();
			for (DealerGroup dealerGroup : dealerGroups) {
				JSONArray categoryLVPairJSON = new JSONArray();
				categoryLVPairJSON.put(dealerGroup.getName());
				categoryLVPairJSON.put(dealerGroup.getName());
				categoriesJSON.put(categoryLVPairJSON);
			}
		}else if (clazz.equals(Item.class)) {
			/*List<String> purposes = new ArrayList<String>(5);
			purposes.add(AdminConstants.PART_RETURNS_PURPOSE);
			purposes.add(AdminConstants.ITEM_REVIEW_WATCHLIST);
            purposes.add(AdminConstants.WARRANTY_COVERAGE_PURPOSE);
            purposes.add(AdminConstants.FAIURE_REPORT_PURPOSE);
			List<ItemGroup> itemGroups = itemGroupService.findItemGroupsByPurposes(purposes);*/
			
			List<ItemGroup> itemGroups = itemGroupService.findAll();
			for (ItemGroup itemGroup : itemGroups) {
				JSONArray categoryLVPairJSON = new JSONArray();
				categoryLVPairJSON.put(itemGroup.getName());
				categoryLVPairJSON.put(itemGroup.getName());
				categoriesJSON.put(categoryLVPairJSON);
			}
		}
		else if(clazz.equals(ClaimState.class)) {	
			boolean isInternalUser = this.isLoggedInUserAnInternalUser();
			List<ClaimState> claimState= ClaimState.getStatusListForSearch(isInternalUser);
			for(ClaimState state : claimState) {
				JSONArray categoryLVPairJSON = new JSONArray();
				categoryLVPairJSON.put(state.getState());
				categoryLVPairJSON.put(state.name());
				categoriesJSON.put(categoryLVPairJSON);
			}
		}
		else {
            String type=null;
            if(clazz.equals(InventoryItem.class)){
                type= Label.INVENTORY;
            }else if(clazz.equals(ApplicablePolicy.class)){
                type= Label.POLICY;
            }else if(clazz.equals(Supplier.class)){
                type= Label.SUPPLIER;
            }else if(clazz.equals(ServiceProcedure.class)){
                type= Label.SERVICE_PROCEDURE_DEFINITION;
            }else if(clazz.equals(FaultCode.class)){
                type= Label.FAULT_CODE_DEFINITION;
            }else if(clazz.equals(ItemGroup.class)){
                type= Label.MODEL;
			} else if (clazz.equals(Campaign.class)) {
				type = Label.CAMPAIGN;
			} else if (clazz.equals(Warehouse.class)) {							
				type = Label.WAREHOUSE;
			}
            List<Label> labels = this.labelService.findLabelsForType(type);
            for (Label label : labels) {
				JSONArray categoryLVPairJSON = new JSONArray();
				categoryLVPairJSON.put(label.getName());
				categoryLVPairJSON.put(label.getName());
				categoriesJSON.put(categoryLVPairJSON);
			}
		}
		sendJSONResponse(categoriesJSON.toString());
		return null;
	}

        private String fetchAllCurrenciesJSON() {
            List<Currency> allCurrencies = new ArrayList<Currency>();
            allCurrencies = this.orgService.listUniqueCurrencies();

            return generateComboboxJson(allCurrencies);
        }
	
	private void sendJSONResponse(final String jsonResponse) throws IOException {
		// Prevent Caching.
		this.response.setHeader("Pragma", "no-cache");
		this.response.addHeader("Cache-Control", "must-revalidate");
		this.response.addHeader("Cache-Control", "no-cache");
		this.response.addHeader("Cache-Control", "no-store");
		this.response.setDateHeader("Expires", 0);

		this.response.setContentType("text/json");
		this.response.getWriter().write(jsonResponse);
		this.response.flushBuffer();
	}

	protected void filterDataElementsForLoggedInUser(
			SortedMap<String, FieldTraversal> dataElements) {
		// does nothing
	}

	protected void initializeAvailableVariablesAndRules() {
		// IBusinessObjectModel bom = getBusinessObjectModel();
		IBusinessObjectModel busObject;
		if(this.context.equalsIgnoreCase(BusinessObjectModelFactory.ITEM_SEARCHES) && isLoggedInUserADealer())
		{
			this.context=BusinessObjectModelFactory.BRAND_ITEM_SEARCHES;
	
		}
		
		if(this.context.equals(BusinessObjectModelFactory.INVENTORY_SEARCHES) && (!isLoggedInUserADealer() && displayInternalInstallType())){
			this.context = BusinessObjectModelFactory.AMER_INVENTORY_SEARCHES;
		}
			busObject = BusinessObjectModelFactory
			.getInstance().getBusinessObjectModel(this.context);	
		 
		busObject.getDataElementsForType(new DomainType("Attributes", ClaimAttributes.class.getSimpleName()));
		 
		try {
		 
			this.ruleJSONSerializer.setContext(this.context);
			 
			this.availableVariablesJSON = jsonifyAvailableVars(busObject);
			
			this.allAvailableVariablesJSON = jsonifyAllAvailableVars(busObject);

			this.availableDuplicacyVariablesJSON = jsonifyAvailableDuplicacyVars();

			this.allAvailableDuplicacyVariablesJSON = jsonifyAllAvailableDuplicacyVars();

            this.allCurrenciesJSON = fetchAllCurrenciesJSON();
			
			 if(("InventorySearches").equalsIgnoreCase(context)){
	    		setInventoryContext(true);
	    	}

		} catch (JSONException e) {
			addActionError("error.manageBusinessCondition.unknown");
		}
	}
	
	 
	private void updateDataElementsForDealers(SortedMap<String, FieldTraversal> topLevelDataElementsForRule){
		if(!getLoggedInUser().isInternalUser()){
			//topLevelDataElementsForRule.remove("claim.activeClaimAudit.state");
			topLevelDataElementsForRule.remove("size(claim.activeClaimAudit.attachments)");
			topLevelDataElementsForRule.remove("{alias}.name='Claim Amount' and {alias}.baseAmount.amount");
			
		}
	}

	@SuppressWarnings("unchecked")
	private String jsonifyAvailableVars(IBusinessObjectModel busObject)
			throws JSONException {
		SortedMap<String, FieldTraversal> topLevelDataElementsForRule = busObject
				.getTopLevelDataElements();
		if(! (busObject instanceof ItemSearchBusinessObjectModel))
		{
			updateDataElementsForDealers(topLevelDataElementsForRule);				
		    partsReplacedInstalledSectionVisible = configParamService
					.getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE
							.getName());
		    buPartReplaceableByNonBUPart = configParamService
			.getBooleanValue(ConfigName.BUPART_REPLACEABLEBY_NONBUPART
					.getName());
		    if(busObject instanceof InventoryItemBusinessObjectModel && isLoggedInUserADealer()){
		    	 Iterator iter = topLevelDataElementsForRule.keySet().iterator();
		           while (iter.hasNext()){
		               String key = (String)iter.next();
		               if(key.equals("inventoryItem.conditionType.itemCondition") || key.equals("inventoryItem.hoursOnMachine")){
		                   iter.remove();
		               }
		           }
		    }
		    
		    if(busObject instanceof ClaimSearchBusinessObjectModel  && isLoggedInUserADealer()){
		    	 Iterator iter = topLevelDataElementsForRule.keySet().iterator();
		    	 while (iter.hasNext()){
		               String key = (String)iter.next();
		               if(key.equals("claim.commercialPolicy") || key.equals("{alias}.locale= 'USER_LOCALE'  and lower({alias}.description)") || key.equals("claim.activeClaimAudit.serviceInformation.serviceDetail") || key.equals("claim.supplierRecovery")
		            		   || key.equals("claim.serviceManagerRequest") || key.equals("claim.activeClaimAudit.serviceInformation.faultCodeRef") || key.equals("claim.activeClaimAudit.serviceInformation.causalPart")){
		                   iter.remove();
		               }
		           }
		    }
		    
		    if(busObject instanceof PartReturnSearchBusinessObjectModel  && isLoggedInUserADealer()){
		    	 Iterator iter = topLevelDataElementsForRule.keySet().iterator();
		    	 while (iter.hasNext()){
		               String key = (String)iter.next();
		               if(key.equals("claim.activeClaimAudit.probableCause") || key.equals("claim.serviceManagerRequest") || key.equals("claim.activeClaimAudit.serviceInformation.faultCodeRef")){
		                   iter.remove();
		               }
		           }
		    }
		      
	       if(busObject instanceof InventoryItemBusinessObjectModel && TRUE.equals(getIsCreateLabelForInventory())){
	           Iterator iter = topLevelDataElementsForRule.keySet().iterator();
	           while (iter.hasNext()){
	               String key = (String)iter.next();
	               if(key.equals("inventoryItem.businessUnitInfo")){
	                   iter.remove();
	               }
	           }
	       }
	       
	       if(busObject instanceof RecoveryClaimSearchBusinessObjectModel && isLoggedInUserASupplier() && isBuConfigAMER()){
	           Iterator iter = topLevelDataElementsForRule.keySet().iterator();
	           while (iter.hasNext()){
	               String key = (String)iter.next();
	               if(key.equals("recoveryClaim.claim.histClmNo") || key.equals("recoveryClaim.claim.claimNumber") || key.equals("recoveryClaim.updatedDate") || key.equals("recoveryClaim.claim.forDealer") || key.equals("{alias}.locale= 'USER_LOCALE'  and lower({alias}.description)")){
	                   iter.remove();
	               }
	           }
	       }
	       
	       if (partsReplacedInstalledSectionVisible) {
				Iterator iter = topLevelDataElementsForRule.keySet().iterator();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					if (key.equalsIgnoreCase("claim.activeClaimAudit.serviceInformation.serviceDetail.oemPartsReplaced")
						|| key.equalsIgnoreCase("claim.activeClaimAudit.serviceInformation.serviceDetail.oemPartsReplaced.size")
						|| key.equalsIgnoreCase("sumOfIntegers(claim.activeClaimAudit.serviceInformation."
								+ "serviceDetail.oemPartsReplaced.{numberOfUnits})")						
						|| key.equalsIgnoreCase("size(claim.activeClaimAudit.serviceInformation.serviceDetail.oemPartsReplaced)")
						|| key.equalsIgnoreCase("sum({alias}.numberOfUnits)")){
						iter.remove();
					}
					if(buPartReplaceableByNonBUPart){
						if(key.equalsIgnoreCase("claim.activeClaimAudit.serviceInformation.serviceDetail.nonOEMPartsReplaced")
								|| key.equalsIgnoreCase("claim.activeClaimAudit.serviceInformation.serviceDetail.nonOEMPartsReplaced.size")
								|| key.equalsIgnoreCase("sumOfIntegers(claim.activeClaimAudit.serviceInformation."
							+ "serviceDetail.nonOEMPartsReplaced.{numberOfUnits})")
							|| key.equalsIgnoreCase("size(claim.activeClaimAudit.serviceInformation.serviceDetail.nonOEMPartsReplaced)")
							|| key.equalsIgnoreCase("sum({nonoemalias}.numberOfUnits)")){
							iter.remove();
						}
					}else{
						if(key.equalsIgnoreCase("claim.activeClaimAudit.serviceInformation.serviceDetail.nonHussmanInstalledParts")){
							iter.remove();
						}
					}					
				}
			}else{
				Iterator iter = topLevelDataElementsForRule.keySet().iterator();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					if(key.equalsIgnoreCase("claim.activeClaimAudit.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled")
						|| key.equalsIgnoreCase("numberOfReplacedParts(claim.activeClaimAudit.serviceInformation.serviceDetail." +
								"hussmanPartsReplacedInstalled)")
						|| key.equalsIgnoreCase("numberOfHussmannInstalledParts(claim.activeClaimAudit.serviceInformation.serviceDetail." +
								"hussmanPartsReplacedInstalled)")
						|| key.equalsIgnoreCase("numberOfNonHussmanInstalledParts(claim.activeClaimAudit.serviceInformation.serviceDetail." +
							"hussmanPartsReplacedInstalled)")
						|| key.equalsIgnoreCase("sumOfHussmannReplacedPartsQuantity(claim.activeClaimAudit.serviceInformation."
										+ "serviceDetail.hussmanPartsReplacedInstalled)")
						|| key.equalsIgnoreCase("sumOfHussmannInstalledPartsQuantity(claim.activeClaimAudit.serviceInformation."
										+ "serviceDetail.hussmanPartsReplacedInstalled)")
						|| key.equalsIgnoreCase("sumOfNONHussmannPartsInstalledQuantity(claim.activeClaimAudit.serviceInformation."
										+ "serviceDetail.hussmanPartsReplacedInstalled)")){
						iter.remove();
					}
				}
			}
	        filterBuildDateAndManufacturingSiteAndFactoryOrder(topLevelDataElementsForRule);	        
		}
		filterDataElementsForLoggedInUser(topLevelDataElementsForRule);

        List<DomainSpecificVariable> listOfFields = sortAndSegregateFieldsByType(new ArrayList<FieldTraversal>(
				topLevelDataElementsForRule.values()));

		return this.ruleJSONSerializer._JSONifiedListOfVaribles(listOfFields)
				.toString();
	}

	private String jsonifyAllAvailableVars(IBusinessObjectModel busObject)
			throws JSONException {
		SortedMap<String, FieldTraversal> allLevelDataElementsForRule = busObject
				.getAllLevelDataElements();
		List<DomainSpecificVariable> listOfFieldsForAll = new ArrayList<DomainSpecificVariable>(
				allLevelDataElementsForRule.size());

		for (Map.Entry<String, FieldTraversal> entry : allLevelDataElementsForRule
				.entrySet()) {
			listOfFieldsForAll
					.add(entry.getValue().getDomainSpecificVariable());
		}

		return this.ruleJSONSerializer._JSONifiedListOfVaribles(listOfFieldsForAll)
				.toString();
	}

	private String jsonifyAvailableDuplicacyVars() throws JSONException {
		BusinessObjectModelFactory bomf = BusinessObjectModelFactory
				.getInstance();
		SortedMap<String, FieldTraversal> duplicacyDataElementsForRule = bomf
				.getBusinessObjectModel(CLAIM_DUPLICACY_RULES)
				.getTopLevelDataElements();

		List<DomainSpecificVariable> listOfFieldsForDuplicacy = sortAndSegregateFieldsByType(new ArrayList<FieldTraversal>(
				duplicacyDataElementsForRule.values()));

			RuleJSONSerializer jsonSerializer =getRuleJSONSerializer(CLAIM_DUPLICACY_RULES); 
		return jsonSerializer
				._JSONifiedListOfVaribles(listOfFieldsForDuplicacy).toString();
	}

	private String jsonifyAllAvailableDuplicacyVars() throws JSONException {
		BusinessObjectModelFactory bomf = BusinessObjectModelFactory
				.getInstance();
		SortedMap<String, FieldTraversal> allDuplicacyLevelDataElementsForRule = bomf
				.getBusinessObjectModel(CLAIM_DUPLICACY_RULES)
				.getAllLevelDataElements();

		List<DomainSpecificVariable> listOfDuplicacyFieldsForAll = new ArrayList<DomainSpecificVariable>(
				allDuplicacyLevelDataElementsForRule.size());

		for (Map.Entry<String, FieldTraversal> entry : allDuplicacyLevelDataElementsForRule
				.entrySet()) {
			listOfDuplicacyFieldsForAll.add(entry.getValue()
					.getDomainSpecificVariable());
		}

		RuleJSONSerializer jsonSerializer = getRuleJSONSerializer(CLAIM_DUPLICACY_RULES);
		return jsonSerializer._JSONifiedListOfVaribles(
				listOfDuplicacyFieldsForAll).toString();
	}
	

	private RuleJSONSerializer getRuleJSONSerializer(String key) {
		RuleJSONSerializer jsonSerializer = this.serializerFactory
				.getRuleJSONSerializer(key);
		jsonSerializer.setTextProvider(getTextProvider());
		return jsonSerializer;
	}

	private List<DomainSpecificVariable> sortAndSegregateFieldsByType(
			ArrayList<FieldTraversal> fieldTraversals) {

		this.ruleJSONSerializer.sortFieldTraversalsByType(fieldTraversals);

		int numFields = fieldTraversals.size();
		int firstOne2OneIndex = -1;
		int firstOne2ManyIndex = -1;

		List<DomainSpecificVariable> listOfFields = new ArrayList<DomainSpecificVariable>();
		DomainSpecificVariable separatorVar = new DomainSpecificVariable(
				void.class, "", this.context);
		for (int i = 0; i < numFields; i++) {

			FieldTraversal field = fieldTraversals.get(i);
			if (field.endsInAOne2One() && firstOne2OneIndex == -1) {
				firstOne2OneIndex = i;
				listOfFields.add(separatorVar);
			} else if (field.endsInACollection() && firstOne2ManyIndex == -1) {
				listOfFields.add(separatorVar);
				firstOne2ManyIndex = i;
			}

			listOfFields.add(field.getDomainSpecificVariable());
		}

		return listOfFields;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}

	public String getReadableEquivalent() {
		DetailedDescriptionGenerator detailedDescriptionGenerator = new DetailedDescriptionGenerator(
				this.domainPredicate);
		this.domainPredicate.accept(detailedDescriptionGenerator);
		return detailedDescriptionGenerator.getDetailedDescription();
	}

	public String getCategoryKind() {
		return this.categoryKind;
	}

	public void setCategoryKind(String categoryKind) {
		this.categoryKind = categoryKind;
	}

	public void setServletResponse(HttpServletResponse httpServletResponse) {
		this.response = httpServletResponse;
	}

	public void setReferringPredicates(String referringPredicates) {
		this.referringPredicates = referringPredicates;
	}

	public String getSavedRuleTreeJSON() {
		return this.savedRuleTreeJSON;
	}

	public String getSavedRuleName() {
		return this.savedRuleName;
	}

	public boolean isInvertPredicate() {
		return this.invertPredicate;
	}

	public void setInvertPredicate(boolean invertPredicate) {
		this.invertPredicate = invertPredicate;
	}

	public void setLabelService(LabelService labelService) {
		this.labelService = labelService;
	}

	public SerializerFactory getSerializerFactory() {
		return this.serializerFactory;
	}

	@Required
	public void setSerializerFactory(SerializerFactory serializerFactory) {
		this.serializerFactory = serializerFactory;
	}

	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

    public String getIsCreateLabelForInventory() {
        return isCreateLabelForInventory;
    }

    public void setIsCreateLabelForInventory(String createLabelForInventory) {
        isCreateLabelForInventory = createLabelForInventory;
    }

	public boolean isPartsReplacedInstalledSectionVisible() {
		return partsReplacedInstalledSectionVisible;
	}

	public void setPartsReplacedInstalledSectionVisible(
			boolean partsReplacedInstalledSectionVisible) {
		this.partsReplacedInstalledSectionVisible = partsReplacedInstalledSectionVisible;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	public boolean isSystemCondition() {
		return systemCondition;
	}

	public void setSystemCondition(boolean systemCondition) {
		this.systemCondition = systemCondition;
	}

    protected void filterBuildDateAndManufacturingSiteAndFactoryOrder(
			SortedMap<String, FieldTraversal> dataElements) {
        if (!isLoggedInUserAnInternalUser()) {
            boolean isBuildDateSearchAllowed = isVisibleAcrossAnyBu(ConfigName.BUILD_DATE_VISIBLE.getName());
            boolean isManufacturingSearchAllowed =isVisibleAcrossAnyBu(ConfigName.MANUFACTURING_SITE_VISIBLE.getName());
            boolean isFactoryOrderSearchAllowed=isVisibleAcrossAnyBu(ConfigName.ENABLE_FACTORY_ORDER_NUMBER.getName());
            Iterator iter = dataElements.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                if ((key.equals("inventoryItem.builtOn") && !isBuildDateSearchAllowed)
                        || (key.equals("inventoryItem.factoryOrderNumber") && !isFactoryOrderSearchAllowed)
                        || (key.equals("{alias1}.locale= 'USER_LOCALE'  and lower({alias1}.description)") && !isManufacturingSearchAllowed)) {
                    iter.remove();
                }
            }
        }
    }

     protected boolean isVisibleAcrossAnyBu(String configName){
        boolean isVisible = false;
        Map<String, List<Object>> buValues = configParamService.getValuesForAllBUs(configName);
        for (String buName : buValues.keySet()) {
              Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
              if(booleanValue){
                 isVisible=true;
                 break;
              }
        }
        return isVisible;
    }

    public String getAllCurrenciesJSON() {
        return allCurrenciesJSON;
    }

    public void setRuleAdministrationService(RuleAdministrationService ruleAdministrationService) {
        this.ruleAdministrationService = ruleAdministrationService;
    }

	public boolean isInventoryContext() {
		return inventoryContext;
	}

	public void setInventoryContext(boolean inventoryContext) {
		this.inventoryContext = inventoryContext;
	}
	
	public boolean displayInternalInstallType() {
		return configParamService
				.getBooleanValue(ConfigName.DISPLAY_INTERNAL_INSTALL_TYPE
						.getName());
	}
    
}
