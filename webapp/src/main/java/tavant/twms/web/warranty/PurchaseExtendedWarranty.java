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
package tavant.twms.web.warranty;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InvTransationType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.ExtWarrantyPlan;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionService;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.RegisteredPolicyStatusType;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.external.ExtWarrantyPriceCheckResponse;
import tavant.twms.external.ExtWarrantyRequest;
import tavant.twms.external.IntegrationBridge;
import tavant.twms.infra.BaseDomain;
import tavant.twms.integration.layer.IntegrationPropertiesBean;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.web.multipleinventorypicker.MultipleInventoryPickerAction;
import tavant.twms.web.util.DocumentTransportUtils;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

/**
 * @author kiran.sg
 */
@SuppressWarnings("serial")
public class PurchaseExtendedWarranty extends MultipleInventoryPickerAction
		implements Preparable, Validateable {
	private static Logger logger = LogManager
			.getLogger(PurchaseExtendedWarranty.class);

	private PolicyService policyService;

	private String serialNumber;

	private WarrantyService warrantyService;

	private List<MultipleInventoryAttributesMapper> selectedInvItemsPolicies = new ArrayList<MultipleInventoryAttributesMapper>();

	private String purchaseComments;

	private Long policyDefinitionId;

	private PolicyDefinitionService policyDefinitionService;

	private PolicyDefinition policyDefinition;

	private CalendarDate purchaseDate;

	private String purchaseOrderNumber;

	private IntegrationBridge integrationBridge;

	private IntegrationPropertiesBean integrationPropertiesBean;

	private CatalogService catalogService;
	
	private ConfigParamService configParamService;

	private final BeanLocator beanLocator = new BeanLocator();

	// Creating a EWP transaction
	private InventoryTransaction invTransaction;

	// Creating a EWP transaction
	private final String transactionTypeString = InvTransationType.EXTENED_WNTY_PURCHASE
			.toString();
	
	private InventoryItem inventoryItem;
	
	private long inventoryIndex = -1;
	
	private InventoryItem selectedInventoryItem;

	public long getInventoryIndex() {
		return inventoryIndex;
	}

	public void setInventoryIndex(long inventoryIndex) {
		this.inventoryIndex = inventoryIndex;
	}

	@Override
	protected void addSearchConstraints() {
		super.addSearchConstraints();
		setCoverageAction(InvTransationType.EXTENED_WNTY_PURCHASE.getTransactionType());
		getInventorySearchCriteria().setInventoryType(InventoryType.RETAIL);
		getInventorySearchCriteria().setConditionTypeNot(
				InventoryItemCondition.SCRAP);
	}

	@Override
	public void validate() {
		removeUnselectedItems();
		if (this.getInventoryItems().size() == 0) {
			if (this.selectedInvItemsPolicies.size() == 0) {
				addActionError(getText("error.extendedwarrantyplan.inventoryNotChosen"));
			}
			if (this.purchaseComments == null
					|| this.purchaseComments.equals("")) {
				addActionError(getText("error.extendedwarrantyplan.commentsMandatory"));
			}
			for (MultipleInventoryAttributesMapper selectedInvItemPolicy : this.selectedInvItemsPolicies) {
				if (selectedInvItemPolicy.getSelectedPolicies() == null
						|| selectedInvItemPolicy.getSelectedPolicies().size() == 0) {
					addActionError("error.extendedwarrantyplan.planNotChosen",
							new String[] { selectedInvItemPolicy
									.getInventoryItem().getSerialNumber() });
					
				} else {
					for(RegisteredPolicy pol : selectedInvItemPolicy.getSelectedPolicies()) {
						if(pol.getPolicyDefinition().getAttachmentMandatory()) {
							if(pol.getAttachments() == null
								|| pol.getAttachments().size() == 0)
								addActionError("error.extendedwarrantyplan.noAttachment",
										new String[] { 
										selectedInvItemPolicy.getInventoryItem().getSerialNumber(),
										pol.getPolicyDefinition().getCode() });
						}
					}
				}
			}
			if (this.purchaseDate == null
					|| this.purchaseDate.isAfter(Clock.today())) {
				addActionError(getText("error.extendedwarrantyplan.dateOfPurchase"));
			}
            /*if (this.purchaseDate != null && !this.purchaseDate.isAfter(Clock.today())) {
                InventoryItem defaultItem = this.selectedInvItemsPolicies.get(0).getInventoryItem();
                SelectedBusinessUnitsHolder.setSelectedBusinessUnit(defaultItem.getBusinessUnitInfo().getName());
                String dateConsidered = configParamService.getStringValue(ConfigName.DATE_CONSIDERED_FOR_EWP.getName());
                if (PURCHASE_DATE.equalsIgnoreCase(dateConsidered)) {
                    for (MultipleInventoryAttributesMapper selectedInvItemPolicy : this.selectedInvItemsPolicies) {
                        if (purchaseDate.isBefore(selectedInvItemPolicy.getInventoryItem().getDeliveryDate())) {
                            addActionError("error.extendedwarrantyplan.invalidDateOfPurchase",
                                    new String[]{selectedInvItemPolicy
                                            .getInventoryItem().getSerialNumber()});
                        }
                    }
                }
            }*/
            if (!StringUtils.hasText(this.purchaseOrderNumber)) {
				addActionError(getText("error.extendedwarrantyplan.purchaseOrderNumber"));
			}
		}
	}

	@Override
	public String searchInventories() throws IOException {
        super.searchInventories();
		return SUCCESS;
	}

	public String displaySearchPage() {
		return SUCCESS;
	}
	
	public boolean isExtWarrantyPriceCheckEnabled() {
		return this.configParamService
				.getBooleanValue(ConfigName.EXT_WARRANTY_PRICE_CHECK_ENABLED.getName());
	}
	
	private Boolean inventoryItemsInitialized(){
    	if(!getInventoryItems().isEmpty() && getInventoryItems().size() > 0){
    		for(InventoryItem inventoryItem : getInventoryItems()){
    			if(inventoryItem != null){
    				return Boolean.FALSE;
    			}
    		}
    	}
    	return Boolean.TRUE;
    }
	
	public String show() {
		if(getInventoryItems().isEmpty() || getInventoryItems().size() == 0 || inventoryItemsInitialized()){
    		addActionError("error.newClaim.invalidInventory");
    		return ERROR;
    	}
        if(!this.getInventoryItems().isEmpty()){
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(this.getInventoryItems().get(0).getBusinessUnitInfo().getName());
            if(!configParamService.getBooleanValue(ConfigName.CAN_EXTERNAL_USER_PURCHASE_EXTENDED_WARRANTY.getName())
                    && !isLoggedInUserAnInternalUser()){
                addActionError("error.inventory.notAuthorized");
                return NONE;
            }
        }
        if (!this.getInventoryItems().isEmpty()
				&& this.getInventoryItems().get(0).getConditionType().getItemCondition().equals("SCRAP")) {
			addActionError("message.scrap.machineScrapped", this
					.getInventoryItems().get(0).getSerialNumber());
			return NONE;
		}
        if (!this.getInventoryItems().isEmpty()
				&& this.getInventoryItems().get(0).getConditionType().getItemCondition().equals("STOLEN")) {
			addActionError("message.stole.machineStolen", this
					.getInventoryItems().get(0).getSerialNumber());
			return NONE;
		}
		for (Iterator<InventoryItem> invIterator = this.getInventoryItems()
				.iterator(); invIterator.hasNext();) {
			InventoryItem invItem = invIterator.next();
			if (invItem == null || invItem.getId() == null) {
				invIterator.remove();
			}
		}	
		fetchAvailablePolicies();
		return SUCCESS;
	}	
	
	public String showPEWFromQuickSearch() {
		if (this.inventoryItem != null 
				&& this.inventoryItem.getConditionType()
						.getItemCondition().equals("SCRAP")) {
			addActionError("message.scrap.machineScrapped", this.inventoryItem.getSerialNumber());
			return NONE;
		}
        if(getInventoryItems()!=null && getInventoryItems().isEmpty()
                && getInventoryItem()!=null){
             getInventoryItems().add(inventoryItem);
        }
        for (Iterator<InventoryItem> invIterator = this.getInventoryItems()
				.iterator(); invIterator.hasNext();) {
			InventoryItem invItem = invIterator.next();
			if (invItem == null || invItem.getId() == null) {
				invIterator.remove();
			}
		}	
		fetchAvailablePolicies();
		return SUCCESS;
	}
    
	@Override
	public String handleInventorySelection() throws IOException {
		super.handleInventorySelection();
		if(selectedInventoryItem!=null)
			 this.getInventoryItems().add(selectedInventoryItem);
		if (!this.getInventoryItems().isEmpty()
				&& this.getInventoryItems().get(0).getConditionType()
						.getItemCondition().equals("SCRAP")) {
			addActionError("message.scrap.machineScrapped", this
					.getInventoryItems().get(0).getSerialNumber());
			return NONE;
		}
		fetchAvailablePolicies();
		return SUCCESS;
	}
	
	public String confirmPurchase() {
		for (MultipleInventoryAttributesMapper selectedInvItemPolicy : this.selectedInvItemsPolicies) {
			ExtWarrantyRequest priceFetchRequest = createExtWarrantyRequest(selectedInvItemPolicy);
			checkPriceForPlans(priceFetchRequest, selectedInvItemPolicy
					.getSelectedPolicies());
		}
		return SUCCESS;
	}

	private ExtWarrantyRequest createExtWarrantyRequest(
			MultipleInventoryAttributesMapper selectedInvItemPolicy) {
		InventoryItem invItem = selectedInvItemPolicy.getInventoryItem();
		String dealerNumber = getDealerNumber(selectedInvItemPolicy);

		ExtWarrantyRequest extWarrantyRequest = new ExtWarrantyRequest();
		extWarrantyRequest.setDealerNo(dealerNumber);
		extWarrantyRequest.setItemNumber(invItem.getOfType().getNumber());
		extWarrantyRequest.setSerialNumber(invItem.getSerialNumber());
		extWarrantyRequest.setDescription(invItem.getOfType().getDescription());
		extWarrantyRequest.setPurchaseDate(CalendarUtil.convertToJavaDate(this.purchaseDate));

		return extWarrantyRequest;
	}

	private String getDealerNumber(
			MultipleInventoryAttributesMapper selectedInvItemPolicy) {
		ServiceProvider dealer = getLoggedInUsersDealership();
		if (dealer == null) {
			Party party = selectedInvItemPolicy.inventoryItem
					.getTransactionHistory().get(0).getOwnerShip();
			dealer = orgService.findDealerById(party.getId());
		}
		String dealerNumber = null;
		if (dealer != null) {
			dealerNumber = dealer.getDealerNumber();
		}
		return dealerNumber;
	}

	public void checkPriceForPlans(ExtWarrantyRequest priceFetchRequest, List<RegisteredPolicy> policyDetails) {
		if (isExtWarrantyPriceCheckEnabled()) {
			checkPriceExternal(priceFetchRequest, policyDetails);
		} else {
			Currency currency = getLoggedInUser().getBelongsToOrganization() != null ? getLoggedInUser()
					.getBelongsToOrganization().getPreferredCurrency() : Currency.getInstance(Locale.US);
			for (RegisteredPolicy policy : policyDetails) {
				BigDecimal num = new BigDecimal(0);
				policy.setPrice(new Money(num.setScale(currency.getDefaultFractionDigits()), currency));
			}
		}
	}

	private void checkPriceExternal(ExtWarrantyRequest extWarrantyRequest,
			List<RegisteredPolicy> policyDetails) {
		integrationBridge = (IntegrationBridge) beanLocator
				.lookupBean("integrationBridge");

		List<ExtWarrantyPlan> extWarrantyPlans = createPlanList(policyDetails,extWarrantyRequest);

		extWarrantyRequest.setPlans(extWarrantyPlans);

		ExtWarrantyPriceCheckResponse response = integrationBridge
				.checkPrice(extWarrantyRequest);

		List<ExtWarrantyPlan> plans = response.getPlans();
		Map<String, ExtWarrantyPlan> priceMap = new HashMap<String, ExtWarrantyPlan>();

		for (ExtWarrantyPlan extWarrantyPlan : plans) {
			priceMap.put(extWarrantyPlan.getPlanCode(), extWarrantyPlan);
		}
		for (RegisteredPolicy policy : policyDetails) {
			ExtWarrantyPlan plan = priceMap.get(policy.getCode());
			policy.setPrice(plan.getAmount());
		}
	}

	private List<ExtWarrantyPlan> createPlanList(
			List<RegisteredPolicy> policyDetails,ExtWarrantyRequest extWarrantyRequest) {
		List<ExtWarrantyPlan> extWarrantyPlans = new ArrayList<ExtWarrantyPlan>();
		String itemNumber = null;
		for (RegisteredPolicy policy : policyDetails) {
			List<Item> items = catalogService.findItemsWithModelName(policy
					.getCode());
			if (items != null && !items.isEmpty()) {
				itemNumber = items.get(0).getNumber();
			}
			ExtWarrantyPlan plan = new ExtWarrantyPlan();
			plan.setPlanCode(policy.getCode());
			plan.setPlanItemNumber(itemNumber);
			Money price= policy.getPrice();
			if(price != null ){
				plan.setAmount(price);
			}
			extWarrantyPlans.add(plan);
		}
		extWarrantyRequest.setItemNumber(itemNumber);
		return extWarrantyPlans;
	}

	public String register() {
		integrationPropertiesBean = (IntegrationPropertiesBean) beanLocator.lookupBean("integrationPropertiesBean");
        InventoryItem defaultItem =  this.selectedInvItemsPolicies.get(0).getInventoryItem();
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(defaultItem.getBusinessUnitInfo().getName());
		for (MultipleInventoryAttributesMapper selectedInvItemPolicy : this.selectedInvItemsPolicies) {
			try {
				createSelectedPoliciesForPurchase(selectedInvItemPolicy
						.getSelectedPolicies(),
						selectedInvItemPolicy.inventoryItem);
			} catch (PolicyException e) {
				logger.error("Error saving warranty for "
						+ selectedInvItemPolicy.inventoryItem, e);
				addActionError(getText("error.purchasingExtendedWarranty"));
				return INPUT;
			}
			for (RegisteredPolicy registeredPolicy : selectedInvItemPolicy
					.getSelectedPolicies()) {
				registeredPolicy
						.setPurchaseOrderNumber(this.purchaseOrderNumber);
				RegisteredPolicy newPolicy = null;
				if (integrationPropertiesBean.isExtWarrantyDebitSubmitEnabled()) {
					newPolicy = this.warrantyService.createPolicyInProgress(
							selectedInvItemPolicy.getInventoryItem()
									.getWarranty(), registeredPolicy
									.getPolicyDefinition(), registeredPolicy
									.getWarrantyPeriod(), registeredPolicy
									.getPrice(), this.purchaseComments,
							this.purchaseOrderNumber);
				} else {
					newPolicy = this.warrantyService.register(selectedInvItemPolicy
							.getInventoryItem().getWarranty(), registeredPolicy
							.getPolicyDefinition(), registeredPolicy
							.getWarrantyPeriod(),
							registeredPolicy.getPrice(),
							this.purchaseComments, this.purchaseOrderNumber, this.purchaseDate);
				}
				if(newPolicy != null && registeredPolicy.getAttachments() != null
						&& !registeredPolicy.getAttachments().isEmpty()) {
					newPolicy.getAttachments().addAll(registeredPolicy.getAttachments());
					DocumentTransportUtils.markDocumentsAsAttached(registeredPolicy.getAttachments());
				}
			}
			this.warrantyService.save(selectedInvItemPolicy.getInventoryItem()
					.getWarranty());

			if (integrationPropertiesBean.isExtWarrantyDebitSubmitEnabled()) {
				submitForDebit(selectedInvItemPolicy);
			}

			// Creating a EWP transaction
			InventoryTransaction newTransaction = new InventoryTransaction();
			newTransaction.setTransactionOrder(new Long(
					selectedInvItemPolicy.inventoryItem.getTransactionHistory()
							.size() + 1));
			Collections.sort(selectedInvItemPolicy.inventoryItem
					.getTransactionHistory());
			newTransaction
					.setTransactedItem(selectedInvItemPolicy.inventoryItem);
			if (getLoggedInUsersDealership() != null) {
				// if logged in user is dealer, he should be the owner
				newTransaction.setSeller(getLoggedInUsersDealership());
				newTransaction.setOwnerShip(getLoggedInUsersDealership());
			} else {
				// if logged in user is admin, the latest transaction ownership
				newTransaction.setSeller(selectedInvItemPolicy.inventoryItem
						.getTransactionHistory().get(0).getOwnerShip());
				newTransaction.setOwnerShip(selectedInvItemPolicy.inventoryItem
						.getTransactionHistory().get(0).getOwnerShip());
			}
			newTransaction.setBuyer(selectedInvItemPolicy.inventoryItem
					.getOwnedBy());
			newTransaction.setInvTransactionType(this.invTransaction
					.getInvTransactionType());
			newTransaction.setTransactionDate(Clock.today());
			newTransaction.setStatus(BaseDomain.ACTIVE);
			selectedInvItemPolicy.inventoryItem.getTransactionHistory().add(
					newTransaction);
			selectedInvItemPolicy.inventoryItem.getWarranty().setForTransaction(newTransaction);
			getWarrantyService().updateInventoryForWarrantyDates(selectedInvItemPolicy.inventoryItem);
			getInventoryService().updateInventoryItem(
					selectedInvItemPolicy.inventoryItem);

		}
		addActionMessage(getText("success.message.extendedWarranty"));
		return SUCCESS;
	}
	
	private void submitForDebit(
			MultipleInventoryAttributesMapper selectedInvItemPolicy) {
		integrationBridge = (IntegrationBridge) beanLocator.lookupBean("integrationBridge");
		
		ExtWarrantyRequest extWarrantyRequest = createExtWarrantyRequest(selectedInvItemPolicy);
		
		List<ExtWarrantyPlan> extWarrantyPlans = createPlanList(selectedInvItemPolicy.getSelectedPolicies(),extWarrantyRequest);
		
		extWarrantyRequest.setPlans(extWarrantyPlans);
		
		integrationBridge.submitExtWarrantyDebit(extWarrantyRequest);
	}

	private List<RegisteredPolicy> createPolicies(
			List<PolicyDefinition> withDefinitions, InventoryItem forItem)
			throws PolicyException {
		List<RegisteredPolicy> policies = new ArrayList<RegisteredPolicy>();
		for (PolicyDefinition definition : withDefinitions) {
			String policyName = definition.getCode();
			if ((policyName == null) || (policyName.trim().equals(""))) {
				continue; // ignore this policy definition
			}
			RegisteredPolicy policy = new RegisteredPolicy();
			policy.setPolicyDefinition(definition);
			policy.setWarrantyPeriod(definition.warrantyPeriodFor(forItem));
			policies.add(policy);
		}
		Collections.sort(policies, new Comparator<RegisteredPolicy>() {
			public int compare(RegisteredPolicy p1, RegisteredPolicy p2) {
				if (p1.getPolicyDefinition().isAvailableByDefault()) {
					if (p2.getPolicyDefinition().isAvailableByDefault()) {
						return 0;
					} else {
						return -1;
					}
				} else {
					if (p2.getPolicyDefinition().isAvailableByDefault()) {
						return 1;
					} else {
						return 0;
					}
				}
			}
		});
		return policies;
	}

	private void createSelectedPoliciesForPurchase(
			List<RegisteredPolicy> selectedPolicies, InventoryItem forItem)
			throws PolicyException {
		for (RegisteredPolicy selectedPolicy : selectedPolicies) {
			CalendarDate endDate = selectedPolicy.getPolicyDefinition().getCoverageTerms().computeWarrantyEndDateForEWP(forItem);
            String dateConsidered = configParamService.getStringValue(ConfigName.DATE_CONSIDERED_FOR_EWP.getName());
            if(PURCHASE_DATE.equalsIgnoreCase(dateConsidered)){
            	if(endDate == null || endDate.isBefore(this.purchaseDate))
            		endDate = this.purchaseDate;
                selectedPolicy.setWarrantyPeriod(new CalendarDuration(
					this.purchaseDate, endDate));
			    selectedPolicy.getWarrantyPeriod().setFromDate(this.purchaseDate);
            }else if(DELIVERY_DATE.equalsIgnoreCase(dateConsidered)){
            	if(endDate == null || endDate.isBefore(forItem.getDeliveryDate()))
            		endDate = forItem.getDeliveryDate();
                selectedPolicy.setWarrantyPeriod(new CalendarDuration(
					forItem.getDeliveryDate(), endDate));
			    selectedPolicy.getWarrantyPeriod().setFromDate(forItem.getDeliveryDate());
            }
            selectedPolicy.setPurchaseDate(this.purchaseDate);
			selectedPolicy.getWarrantyPeriod().setTillDate(endDate);
		}
	}

	public String fetchAvailablePolicies() {
		try {
			
			Map<String, List<Object>> buConfigMap = this.configParamService.getValuesForAllBUs(
						ConfigName.DATE_CONSIDERED_FOR_PLAN_AVAILABILITY_ON_EWP.getName());
			for (Iterator<InventoryItem> inventoryItemIter = this
					.getInventoryItems().iterator(); inventoryItemIter
					.hasNext();) {
				InventoryItem inventoryItem = inventoryItemIter.next();
				boolean ewpDrivenByPurchaseDate = "ewpPurchaseDate".equalsIgnoreCase(
							(String)buConfigMap.get(inventoryItem.getBusinessUnitInfo().getName()).get(0));
				List<PolicyDefinition> policyDefinitions = null;
				try {
					if(ewpDrivenByPurchaseDate && purchaseDate == null)
						policyDefinitions = new ArrayList<PolicyDefinition>();
					else
						policyDefinitions = this.policyService
							.findExtendedPoliciesAvailableForRegistration(
									inventoryItem, inventoryItem
											.getDeliveryDate());
				} catch (PolicyException pex) {
					logger.error(
							"Error fetching policies available for InventoryItem[slNo:"
									+ inventoryItem.getSerialNumber() + "]",
							pex);
				}

				// Remove the existing policies
				for (Iterator<PolicyDefinition> policyIterator = policyDefinitions
						.iterator(); policyIterator.hasNext();) {
					PolicyDefinition policyDefinition = policyIterator.next();
					for (RegisteredPolicy registeredPolicy : inventoryItem
							.getWarranty().getPolicies()) {
						if (registeredPolicy.getPolicyDefinition().getId()
								.equals(policyDefinition.getId())) {
							policyIterator.remove();
							break;
						} else if (RegisteredPolicyStatusType.INPROGRESS
								.getStatus().equals(
										registeredPolicy.getLatestPolicyAudit()
												.getStatus())) {
							// as long as a coverage is pending on the inventory
							// for debit submission, new coverage cannot be
							// kicked.
							policyIterator.remove();
							break;
						}
					}
				}

				boolean isInternal = isLoggedInUserAnInternalUser();
				
                // Remove the invalid Policies
				for (Iterator<PolicyDefinition> policyIterator = policyDefinitions
						.iterator(); policyIterator.hasNext();) {
					PolicyDefinition policyDefinition = policyIterator.next();
					
					if(!policyDefinition.getCoverageTerms().isAppliableForEWP(
							inventoryItem, (ewpDrivenByPurchaseDate ? purchaseDate : null))
							|| (!isInternal && policyDefinition.getForInternalUsersOnly())){
                        policyIterator.remove();
                    }
				}

                // Remove the invalid Policies for BlackListedDealers
                if (isLoggedInUserADealer() && !isLoggedInUserAnAdmin() && !isLoggedInUserAnInvAdmin()) {
                    for (Iterator<PolicyDefinition> policyIterator = policyDefinitions
                            .iterator(); policyIterator.hasNext();) {
                        PolicyDefinition policyDefinition = policyIterator.next();
                        if (policyDefinition.isServiceProviderBlackListed(getLoggedInUsersDealership())) {
                            policyIterator.remove();
                        }
                    }
                }

				MultipleInventoryAttributesMapper selectedInvItemPolicy = new MultipleInventoryAttributesMapper();
				selectedInvItemPolicy.setInventoryItem(inventoryItem);
				selectedInvItemPolicy.setEwpDrivenByPurchaseDate(ewpDrivenByPurchaseDate);
				if (policyDefinitions != null && !policyDefinitions.isEmpty())
					selectedInvItemPolicy.setAvailablePolicies(createPolicies(
						policyDefinitions, inventoryItem));
					this.selectedInvItemsPolicies
							.add(selectedInvItemPolicy);

			}
			return SUCCESS;
		} catch (PolicyException e) {
			addActionError(getText("error.fetching.policies"));
			return INPUT;
		}
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public void prepare() throws Exception {

	}

	public String getSerialNumber() {
		return this.serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public List<MultipleInventoryAttributesMapper> getSelectedInvItemsPolicies() {
		return this.selectedInvItemsPolicies;
	}

	public void setSelectedInvItemsPolicies(
			List<MultipleInventoryAttributesMapper> selectedInvItemsPolicies) {
		this.selectedInvItemsPolicies = selectedInvItemsPolicies;
	}

	public String getPurchaseComments() {
		return this.purchaseComments;
	}

	public void setPurchaseComments(String purchaseComments) {
		this.purchaseComments = purchaseComments;
	}

	public String viewPlanInformation() {

		if (this.policyDefinitionId != null) {
			this.policyDefinition = this.policyDefinitionService
					.findPolicyDefinitionById(this.policyDefinitionId);
		}

		return SUCCESS;

	}

	public String getPolicyFeeTotalForInv(Long inventoryItemId) {
		Long policyFeeSum = new Long(0);
		String currency = null;
		if (inventoryItemId != null) {

			for (MultipleInventoryAttributesMapper selectedInvItemPolicy : this.selectedInvItemsPolicies) {
				if (selectedInvItemPolicy.inventoryItem.getId().longValue() == inventoryItemId
						.longValue()) {
					for (RegisteredPolicy selectedPolicy : selectedInvItemPolicy
							.getSelectedPolicies()) {
						currency = selectedPolicy.getPrice()
								.breachEncapsulationOfCurrency().getSymbol();
						policyFeeSum = policyFeeSum
								+ selectedPolicy.getPrice()
										.breachEncapsulationOfAmount()
										.longValue();
					}
					return currency + " " + policyFeeSum.toString();
				}
			}
		}
		return null;
	}

	public String getPolicyFeeTotal() {
		Long policyFeeSum = new Long(0);
		String currency = null;
		for (MultipleInventoryAttributesMapper selectedInvItemPolicy : this.selectedInvItemsPolicies) {
			for (RegisteredPolicy selectedPolicy : selectedInvItemPolicy
					.getSelectedPolicies()) {
				currency = selectedPolicy.getPrice()
						.breachEncapsulationOfCurrency().getSymbol();
				policyFeeSum = policyFeeSum
						+ selectedPolicy.getPrice()
								.breachEncapsulationOfAmount().longValue();
			}

		}
		return currency + " " + policyFeeSum.toString();
	}

	private void removeUnselectedItems() {
		for (Iterator<MultipleInventoryAttributesMapper> mapperIterator = this.selectedInvItemsPolicies
				.iterator(); mapperIterator.hasNext();) {
			MultipleInventoryAttributesMapper mapper = mapperIterator.next();
			if (mapper == null || mapper.getInventoryItem() == null
					|| mapper.getInventoryItem().getId() == null) {
				mapperIterator.remove();
			}
			if (mapper != null && mapper.getSelectedPolicies() != null) {
				for (Iterator<RegisteredPolicy> policyIterator = mapper
						.getSelectedPolicies().iterator(); policyIterator
						.hasNext();) {
					RegisteredPolicy selectedPolicy = policyIterator.next();
					if (selectedPolicy == null
							|| selectedPolicy.getPolicyDefinition() == null
							|| selectedPolicy.getPolicyDefinition().getId() == null) {
						policyIterator.remove();
					}
				}
			}
		}
	}

	public WarrantyService getWarrantyService() {
		return this.warrantyService;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public Long getPolicyDefinitionId() {
		return this.policyDefinitionId;
	}

	public void setPolicyDefinitionId(Long policyDefinitionId) {
		this.policyDefinitionId = policyDefinitionId;
	}

	public PolicyService getPolicyService() {
		return this.policyService;
	}

	public PolicyDefinition getPolicyDefinition() {
		return this.policyDefinition;
	}

	public void setPolicyDefinition(PolicyDefinition policyDefinition) {
		this.policyDefinition = policyDefinition;
	}

	public PolicyDefinitionService getPolicyDefinitionService() {
		return this.policyDefinitionService;
	}

	public void setPolicyDefinitionService(
			PolicyDefinitionService policyDefinitionService) {
		this.policyDefinitionService = policyDefinitionService;
	}

	public CalendarDate getPurchaseDate() {
		return this.purchaseDate;
	}

	public void setPurchaseDate(CalendarDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getPurchaseOrderNumber() {
		return this.purchaseOrderNumber;
	}

	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
	}

	public void setIntegrationBridge(IntegrationBridge integrationBridge) {
		this.integrationBridge = integrationBridge;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public InventoryTransaction getInvTransaction() {
		return invTransaction;
	}

	public void setInvTransaction(InventoryTransaction invTransaction) {
		this.invTransaction = invTransaction;
	}

	public String getTransactionTypeString() {
		return transactionTypeString;
	}
	
	public ConfigParamService getConfigParamService() {
		return this.configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public boolean fetchAvailablePoliciesForUIDisplay(InventoryItem invItem) {
		try {			
			List<PolicyDefinition> policyDefinitions = null;
			try {
				policyDefinitions = this.policyService
				.findExtendedPoliciesAvailableForRegistration(
						invItem, invItem
						.getDeliveryDate());
			} catch (PolicyException pex) {
				logger.error(
						"Error fetching policies available for InventoryItem[slNo:"
						+ invItem.getSerialNumber() + "]",
						pex);
			}

			// Remove the existing policies
			for (Iterator<PolicyDefinition> policyIterator = policyDefinitions
					.iterator(); policyIterator.hasNext();) {
				PolicyDefinition policyDefinition = policyIterator.next();
				for (RegisteredPolicy registeredPolicy : invItem
						.getWarranty().getPolicies()) {
					if (registeredPolicy.getPolicyDefinition().getId()
							.equals(policyDefinition.getId())) {
						policyIterator.remove();
						break;
					} else if (RegisteredPolicyStatusType.INPROGRESS
							.getStatus().equals(
									registeredPolicy.getLatestPolicyAudit()
									.getStatus())) {
						// as long as a coverage is pending on the inventory
						// for debit submission, new coverage cannot be
						// kicked.
						policyIterator.remove();
						break;
					}
				}
			}

			if (policyDefinitions != null && !policyDefinitions.isEmpty()) {
				MultipleInventoryAttributesMapper selectedInvItemPolicy = new MultipleInventoryAttributesMapper();
				selectedInvItemPolicy.setInventoryItem(invItem);
				selectedInvItemPolicy.setAvailablePolicies(createPolicies(
						policyDefinitions, invItem));
				if (selectedInvItemPolicy.getAvailablePolicies() != null
						&& selectedInvItemPolicy.getAvailablePolicies()
						.size() > 0) {
					this.selectedInvItemsPolicies
					.add(selectedInvItemPolicy);						
				}
				return true;
			} else {
				return false;
			}
		} catch (PolicyException e) {
			addActionError(getText("error.fetching.policies"));
			return false;
		}
	}

	public InventoryItem getSelectedInventoryItem() {
		return selectedInventoryItem;
	}

	public void setSelectedInventoryItem(InventoryItem selectedInventoryItem) {
		this.selectedInventoryItem = selectedInventoryItem;
	}	
	
}
