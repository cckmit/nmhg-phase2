package tavant.twms.web.admin.warranty;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionService;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.RegisteredPolicyStatusType;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.web.multipleinventorypicker.MultipleInventoryPickerAction;
import tavant.twms.web.warranty.MultipleInventoryAttributesMapper;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

@SuppressWarnings("serial")
public class ManageFleetWarrantyCoverage extends MultipleInventoryPickerAction
		implements Preparable, Validateable {

	private static Logger logger = LogManager
			.getLogger(ManageFleetWarrantyCoverage.class);

	private static final String MANAGE_FLEET_WARRANTY_COVERAGE = "MFWC";

	private static final String MANAGE_FLEET_GOODWILL_COVERAGE = "MFGC";

	String manageCoverageAction;

	String manageCoverageComments;

	private PolicyService policyService;

	private PolicyDefinitionService policyDefinitionService;

	private WarrantyService warrantyService;

	private CalendarDate manageCoverageDate;

	private Integer serviceHoursCovered;

	private final List<MultipleInventoryAttributesMapper> inventoryItemMappings = new ArrayList<MultipleInventoryAttributesMapper>();

	PolicyDefinition selectedGoodWillPolicy = null;

	List<PolicyDefinition> availableGoodWillPlans = null;

	private Long id;

	private InventoryItem inventoryItem;

	private Collection<RegisteredPolicy> registeredPolicies = new ArrayList<RegisteredPolicy>();

	public void prepare() throws Exception {

	}

	private void removeUnselectedItems() {
		for (Iterator<MultipleInventoryAttributesMapper> mapperIterator = this.inventoryItemMappings
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
							|| selectedPolicy.getId() == null) {
						policyIterator.remove();
					}
				}
			}
		}
	}

	@Override
	protected void addSearchConstraints() {
		super.addSearchConstraints();
		getInventorySearchCriteria().setInventoryType(InventoryType.RETAIL);
		getInventorySearchCriteria().setConditionTypeNot(
				InventoryItemCondition.SCRAP);
	}

	@Override
	public String searchInventories() throws IOException {
		setCoverageAction(MANAGE_FLEET_WARRANTY_COVERAGE);
		super.searchInventories();
		return SUCCESS;
	}

	public String searchInventoriesForGWExtension() throws IOException {
		setCoverageAction(MANAGE_FLEET_GOODWILL_COVERAGE);
		super.searchInventories();
		return SUCCESS;
	}

	/*
	 * This API is to fetch existing active/terminated coverages based on action
	 * chosen
	 * 
	 */
	private void filterInventoriesWithAvailablePolicies() {
		for (Iterator<InventoryItem> inventoryItemIter = this
				.getInventoryItems().iterator(); inventoryItemIter.hasNext();) {
			InventoryItem inventoryItem = inventoryItemIter.next();
			MultipleInventoryAttributesMapper inventoryItemAttributeMap = new MultipleInventoryAttributesMapper();
			Set<RegisteredPolicy> existingPolicies = inventoryItem
					.getWarranty().getPolicies();
			for (RegisteredPolicy existingPolicy : existingPolicies) {
				if (RegisteredPolicyStatusType.ACTIVE.toString().equals(
						this.manageCoverageAction)) {
					   if(existingPolicy.getLatestPolicyAudit()!=null){
					if (RegisteredPolicyStatusType.TERMINATED.getStatus()
							.equalsIgnoreCase(
									existingPolicy.getLatestPolicyAudit()
											.getStatus())) {
						inventoryItemAttributeMap.getAvailablePolicies().add(
								existingPolicy);
					}
				}
			}else {
				   if(existingPolicy.getLatestPolicyAudit()!=null){
					if (RegisteredPolicyStatusType.ACTIVE.getStatus().equalsIgnoreCase(
							existingPolicy.getLatestPolicyAudit().getStatus())) {
						inventoryItemAttributeMap.getAvailablePolicies().add(
								existingPolicy);
					}
				   }
				}
			}
			// Adding the inventory item only if it has plans eligible for
			// activation/termination
			if (!inventoryItemAttributeMap.getAvailablePolicies().isEmpty()) {
				inventoryItemAttributeMap.setInventoryItem(inventoryItem);
				Collections.sort(inventoryItemAttributeMap
						.getAvailablePolicies());
				this.inventoryItemMappings.add(inventoryItemAttributeMap);
			} else {
				inventoryItemIter.remove();
			}
		}
	}

	/*
	 * This API is to filter inventories based on applicabiltiy of chosen GW
	 * plan
	 * 
	 */
	private void filterInventoriesWithApplicableGWPolicy() {
		for (Iterator<InventoryItem> inventoryItemIter = this
				.getInventoryItems().iterator(); inventoryItemIter.hasNext();) {
			InventoryItem inventoryItem = inventoryItemIter.next();
			if (this.selectedGoodWillPolicy != null) {
				if (!this.selectedGoodWillPolicy.getAvailability()
						.isAvailableFor(inventoryItem)
						|| doesCoverageExistAlready(inventoryItem)) {
					inventoryItemIter.remove();// Adding the inventory item
					// only the selected GW is
					// available
				}
			}
		}
	}

	private boolean doesCoverageExistAlready(InventoryItem inventoryItem) {
		for (RegisteredPolicy registeredPolicy : inventoryItem.getWarranty()
				.getPolicies()) {
			if (registeredPolicy.getPolicyDefinition().getId().longValue() == this.selectedGoodWillPolicy
					.getId().longValue()) {

				return true;
			}
		}
		return false;
	}

	public String showInventoriesGWPolicy() {
		for (InventoryItem inventoryItem : this.getInventoryItems()) {
			MultipleInventoryAttributesMapper inventoryItemAttributeMap = new MultipleInventoryAttributesMapper();
			inventoryItemAttributeMap.setInventoryItem(inventoryItem);
			this.inventoryItemMappings.add(inventoryItemAttributeMap);
		}
		return SUCCESS;
	}

	public String showPolicyDetailsForGWPolicy() {
		inventoryItem = getInventoryService().findInventoryItem(id);
		try {
			this.registeredPolicies = this.policyService
					.findRegisteredPolicies(this.inventoryItem);
		} catch (PolicyException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public String showGoodWillPlans() {
		try {
			this.availableGoodWillPlans = this.policyService
					.getAllGoodWillPolicies();
		} catch (PolicyException e) {
			logger.error("Error fetching available goodwill policies ", e);
		}
		return SUCCESS;
	}

	@Override
	public void validate() {
		removeUnselectedItems();
		if (this.manageCoverageAction != null
				&& !this.manageCoverageAction.equals("")) {
			if (this.inventoryItemMappings.isEmpty()) {
				addActionError("error.extendedwarrantyplan.inventoryNotChosen");
			}
			for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings) {
				if (inventoryItemMapping != null
						&& inventoryItemMapping.getInventoryItem() != null
						&& (inventoryItemMapping.getSelectedPolicies() == null || inventoryItemMapping
								.getSelectedPolicies().isEmpty())) {
					addActionError("error.manageFleetCoverage.planNotChosen",
							new String[] { inventoryItemMapping
									.getInventoryItem().getSerialNumber() });
				}
				if (!inventoryItemMapping.getSelectedPolicies().isEmpty() && !(RegisteredPolicyStatusType.TERMINATED.toString().equals(
						this.manageCoverageAction) || RegisteredPolicyStatusType.ACTIVE.toString().equals(
								this.manageCoverageAction))) {
					for (RegisteredPolicy policy : inventoryItemMapping.getSelectedPolicies()) {
						if (policy.getWarrantyPeriod().getTillDate() != null) {
							if (policy.getWarrantyPeriod().getTillDate().isBefore(policy.getLatestButOnePolicyAudit().getWarrantyPeriod().getFromDate())) {
								addActionError("error.manageFleetCoverage.tillDateGreaterThanStartDate");
								break;
							}
						} else {
							if (this.manageCoverageDate != null) {
								if (this.manageCoverageDate.isBefore(policy.getLatestButOnePolicyAudit().getWarrantyPeriod().getFromDate())) {
									addActionError("error.manageFleetCoverage.tillDateGreaterThanStartDate");
									break;
								}
							} else {
								addActionError("error.manageFleetCoverage.tillDateOrEndDateRequired");
								break;
							}
						}
					}
				}
			}
		} else {
			if (this.inventoryItemMappings.isEmpty()) {
				addActionError("error.extendedwarrantyplan.inventoryNotChosen");
			}
			if (this.manageCoverageDate == null) {
				addActionError("error.fleetmanagement.tillDateMandatory");
			}
			if (this.serviceHoursCovered == null
					|| this.serviceHoursCovered.intValue() < 0) {
				addActionError("error.fleetmanagement.serviceHoursMandatory");
			}
			validateTillDate();
		
		}
	}
	public void validateTillDate() {
		for (MultipleInventoryAttributesMapper inventoryItemMap : this.inventoryItemMappings) {
			
			if(inventoryItemMap.getInventoryItem().getDeliveryDate().isAfter(this.manageCoverageDate))
			{
				addActionError("error.fleetmanagement.invalidTillDate", new String[] {
						inventoryItemMap.getInventoryItem().getDeliveryDate().toString()});
			}
			
		}
	}

	public String manageFleetWarrantyCoverage() {
		if (RegisteredPolicyStatusType.TERMINATED.toString().equals(
				this.manageCoverageAction)) {
			terminateCoverages();
		} else if (RegisteredPolicyStatusType.ACTIVE.toString().equals(
				this.manageCoverageAction)) {
			activateCoverages();
		} else {
			modifyEndDates();
		}

		return SUCCESS;
	}

	public String fleetGWExtension() {
		applyGoodWillCoverage();
		return SUCCESS;
	}

	private void activateCoverages() {
		for (MultipleInventoryAttributesMapper inventoryItemMap : this.inventoryItemMappings) {
			for (RegisteredPolicy policyToActivate : inventoryItemMap
					.getSelectedPolicies()) {
				// setting the end date of coverage to the admin chosen date.
				if (this.manageCoverageDate != null) {
					policyToActivate
							.setWarrantyPeriod(new CalendarDuration(
									inventoryItemMap.getInventoryItem()
											.getDeliveryDate(),
									this.manageCoverageDate));
				} else {
					policyToActivate.setWarrantyPeriod(policyToActivate
							.getLatestPolicyAudit().getWarrantyPeriod());
				}
				policyToActivate.getLatestPolicyAudit().setComments(
						this.manageCoverageComments);
				this.warrantyService.activateRegisteredPolicyForAdmin(
						inventoryItemMap.getInventoryItem().getWarranty(),
						policyToActivate);
				this.warrantyService.updateInventoryForWarrantyDates(inventoryItemMap.getInventoryItem());
				this.warrantyService.update(inventoryItemMap.getInventoryItem()
						.getWarranty());
			}
		}
		addActionMessage(getText("success.message.activateWarrantyCoverage"));
	}

	private void modifyEndDates() {
		for (MultipleInventoryAttributesMapper inventoryItemMap : this.inventoryItemMappings) {
			for (RegisteredPolicy policyToActivate : inventoryItemMap
					.getSelectedPolicies()) {
				policyToActivate.getWarrantyPeriod().setFromDate(
						inventoryItemMap.getInventoryItem().getDeliveryDate());
				// if end date is given against a plan ,it takes priority, //
				// setting the end date of coverage to the master end date
				if ((policyToActivate.getWarrantyPeriod() == null || policyToActivate
						.getWarrantyPeriod().getTillDate() == null)
						&& this.manageCoverageDate != null) {
					policyToActivate.getWarrantyPeriod().setTillDate(
							this.manageCoverageDate);
				}
				policyToActivate.getLatestPolicyAudit().setComments(
						this.manageCoverageComments);
				this.warrantyService.activateRegisteredPolicyForAdmin(
						inventoryItemMap.getInventoryItem().getWarranty(),
						policyToActivate);
				this.warrantyService.updateInventoryForWarrantyDates(inventoryItemMap.getInventoryItem());
				this.warrantyService.update(inventoryItemMap.getInventoryItem()
						.getWarranty());
			}
		}
		// for (MultipleInventoryAttributesMapper inventoryItemMap :
		// this.inventoryItemMappings) {
		// for (RegisteredPolicy policyToActivate :
		// inventoryItemMap.getSelectedPolicies()) {
		// // setting the end date of coverage to the admin chosen date.
		// if (this.manageCoverageDate != null) {
		// policyToActivate.setWarrantyPeriod(new
		// CalendarDuration(inventoryItemMap
		// .getInventoryItem().getDeliveryDate(), this.manageCoverageDate));
		// } else {
		// policyToActivate.setWarrantyPeriod(policyToActivate.getLatestPolicyAudit()
		// .getWarrantyPeriod());
		// }
		// policyToActivate.getLatestPolicyAudit().setComments(this.manageCoverageComments);
		// this.warrantyService.activateRegisteredPolicyForAdmin(inventoryItemMap
		// .getInventoryItem().getWarranty(), policyToActivate);
		// this.warrantyService.update(inventoryItemMap.getInventoryItem().getWarranty());
		// }
		// }
		addActionMessage(getText("success.message.modifyEndDateWarrantyCoverage"));
	}

	private RegisteredPolicy createPolicy(InventoryItem forItem) {
		RegisteredPolicy policy = new RegisteredPolicy();
		Currency currency = Currency.getInstance(Locale.US);
		BigDecimal num = new BigDecimal(0);

		policy.setWarranty(forItem.getWarranty());
		policy.setPolicyDefinition(this.selectedGoodWillPolicy);
		policy.setPrice(new Money(num.setScale(currency
				.getDefaultFractionDigits()), currency));

		
		policy.setWarrantyPeriod(new CalendarDuration(
				forItem.getDeliveryDate(), this.manageCoverageDate));

		return policy;
	}

	private void applyGoodWillCoverage() {
		for (MultipleInventoryAttributesMapper inventoryItemMap : this.inventoryItemMappings) {
			RegisteredPolicy goodWillPolicy = createPolicy(inventoryItemMap
					.getInventoryItem());
			goodWillPolicy.getLatestPolicyAudit().setComments(
					this.manageCoverageComments);
			goodWillPolicy.getLatestPolicyAudit().setServiceHoursCovered(
					this.serviceHoursCovered);
			this.warrantyService.applyGoodWillPolicyForAdmin(inventoryItemMap
					.getInventoryItem().getWarranty(), goodWillPolicy);
			this.warrantyService.update(inventoryItemMap.getInventoryItem()
					.getWarranty());
			warrantyService.updateInventoryForWarrantyDates(inventoryItemMap.getInventoryItem());
			getInventoryService().updateInventoryItem(inventoryItemMap.getInventoryItem());

		}
		addActionMessage(getText("success.message.activateWarrantyCoverage"));
	}

	private void terminateCoverages() {
		for (MultipleInventoryAttributesMapper inventoryItemMap : this.inventoryItemMappings) {
			for (RegisteredPolicy policyToActivate : inventoryItemMap
					.getSelectedPolicies()) {
				policyToActivate.setWarrantyPeriod(policyToActivate
						.getLatestPolicyAudit().getWarrantyPeriod());
				policyToActivate.getLatestPolicyAudit().setComments(
						this.manageCoverageComments);
				this.warrantyService.terminateRegisteredPolicyForAdmin(
						inventoryItemMap.getInventoryItem().getWarranty(),
						policyToActivate);
				this.warrantyService.updateInventoryForWarrantyDates(inventoryItemMap.getInventoryItem());
				this.warrantyService.update(inventoryItemMap.getInventoryItem()
						.getWarranty());
			}
		}
		addActionMessage(getText("success.message.terminateWarrantyCoverage"));
	}

	public List<RegisteredPolicy> getTerminatedPolicies(Long inventoryItemId) {
		List<RegisteredPolicy> terminatedPolicies = new ArrayList<RegisteredPolicy>();

		if (inventoryItemId != null) {
			for (InventoryItem inventoryItem : this.getInventoryItems()) {
				if (inventoryItem.getId().longValue() == inventoryItemId
						.longValue()) {
					Set<RegisteredPolicy> existingPolicies = inventoryItem
							.getWarranty().getPolicies();
					for (RegisteredPolicy existingPolicy : existingPolicies) {
						if (RegisteredPolicyStatusType.TERMINATED.getStatus()
								.equalsIgnoreCase(
										existingPolicy.getLatestPolicyAudit()
												.getStatus())) {
							terminatedPolicies.add(existingPolicy);
						}
					}
				}
			}
		}
		return terminatedPolicies;
	}

	public List<RegisteredPolicy> getActivePolicies(Long inventoryItemId) {
		List<RegisteredPolicy> activePolicies = new ArrayList<RegisteredPolicy>();
		if (inventoryItemId != null) {
			for (InventoryItem inventoryItem : this.getInventoryItems()) {
				if (inventoryItem.getId().longValue() == inventoryItemId
						.longValue()) {
					Set<RegisteredPolicy> existingPolicies = inventoryItem
							.getWarranty().getPolicies();
					for (RegisteredPolicy existingPolicy : existingPolicies) {
						if (RegisteredPolicyStatusType.ACTIVE.getStatus()
								.equalsIgnoreCase(
										existingPolicy.getLatestPolicyAudit()
												.getStatus())) {
							activePolicies.add(existingPolicy);
						}
					}
				}
			}
		}
		return activePolicies;

	}

	public String getManageCoverageComments() {
		return this.manageCoverageComments;
	}

	public void setManageCoverageComments(String manageCoverageComments) {
		this.manageCoverageComments = manageCoverageComments;
	}

	public List<MultipleInventoryAttributesMapper> getInventoryItemMappings() {
		return this.inventoryItemMappings;
	}

	public WarrantyService getWarrantyService() {
		return this.warrantyService;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public CalendarDate getManageCoverageDate() {
		return this.manageCoverageDate;
	}

	public void setManageCoverageDate(CalendarDate manageCoverageDate) {
		this.manageCoverageDate = manageCoverageDate;
	}

	public List<PolicyDefinition> getAvailableGoodWillPlans() {
		return this.availableGoodWillPlans;
	}

	public void setAvailableGoodWillPlans(
			List<PolicyDefinition> availableGoodWillPlans) {
		this.availableGoodWillPlans = availableGoodWillPlans;
	}

	public PolicyDefinition getSelectedGoodWillPolicy() {
		return this.selectedGoodWillPolicy;
	}

	public void setSelectedGoodWillPolicy(
			PolicyDefinition selectedGoodWillPolicy) {
		this.selectedGoodWillPolicy = selectedGoodWillPolicy;
	}

	public PolicyDefinitionService getPolicyDefinitionService() {
		return this.policyDefinitionService;
	}

	public void setPolicyDefinitionService(
			PolicyDefinitionService policyDefinitionService) {
		this.policyDefinitionService = policyDefinitionService;
	}

	public String displayManageCoverageActions() {
		return SUCCESS;
	}

	public String selectInventoriesFleetCoverage() {
		return SUCCESS;
	}

	public String showFleetWarrantyCoverage() throws IOException {
		super.handleInventorySelection();
		filterInventoriesWithAvailablePolicies();
		return SUCCESS;
	}

	public String getManageCoverageAction() {
		return this.manageCoverageAction;
	}

	public void setManageCoverageAction(String manageCoverageAction) {
		this.manageCoverageAction = manageCoverageAction;
	}

	public PolicyService getPolicyService() {
		return this.policyService;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public String confirmFleetWarrantyCoverage() {
		return SUCCESS;
	}

	public String confirmFleetGWExtension() {
		return SUCCESS;
	}

	public Integer getServiceHoursCovered() {
		return this.serviceHoursCovered;
	}

	public void setServiceHoursCovered(Integer serviceHoursCovered) {
		this.serviceHoursCovered = serviceHoursCovered;
	}

	public boolean filterInventoriesWithAvailablePoliciesForUIDisplay(
			InventoryItem invItem) {
		MultipleInventoryAttributesMapper inventoryItemAttributeMap = new MultipleInventoryAttributesMapper();
		Set<RegisteredPolicy> existingPolicies = invItem.getWarranty()
				.getPolicies();
		for (RegisteredPolicy existingPolicy : existingPolicies) {
			if (RegisteredPolicyStatusType.ACTIVE.toString().equals(
					this.manageCoverageAction)) {
			if(existingPolicy.getLatestPolicyAudit()!=null){
				if (RegisteredPolicyStatusType.TERMINATED.getStatus().equalsIgnoreCase(
						existingPolicy.getLatestPolicyAudit().getStatus())) {
					inventoryItemAttributeMap.getAvailablePolicies().add(
							existingPolicy);
				}
			}
			} else {
				if(existingPolicy.getLatestPolicyAudit()!=null){
				if (RegisteredPolicyStatusType.ACTIVE.getStatus().equalsIgnoreCase(
						existingPolicy.getLatestPolicyAudit().getStatus())) {
					inventoryItemAttributeMap.getAvailablePolicies().add(
							existingPolicy);
				}
			}
			}
		}
		// Adding the inventory item only if it has plans eligible for
		// activation/termination
		if (!inventoryItemAttributeMap.getAvailablePolicies().isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean filterInventoriesWithApplicableGWPolicyForUIDisplay(
			InventoryItem invItem) {
		if (this.selectedGoodWillPolicy != null) {
			if (!this.selectedGoodWillPolicy.getAvailability().isAvailableFor(
					invItem)
					|| doesCoverageExistAlready(invItem)) {
				return false;// Adding the inventory item
				// only the selected GW is
				// available
			} else {
				return true;
			}
		}
		return false;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public Collection<RegisteredPolicy> getRegisteredPolicies() {
		return registeredPolicies;
	}

	public void setRegisteredPolicies(
			Collection<RegisteredPolicy> registeredPolicies) {
		this.registeredPolicies = registeredPolicies;
	}
	
	@Override
	public boolean isSendInventoryId() {
		return true;
	}
}
