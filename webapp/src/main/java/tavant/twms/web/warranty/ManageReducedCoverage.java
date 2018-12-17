package tavant.twms.web.warranty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.util.StringUtils;

import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionService;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.RegisteredPolicyAudit;
import tavant.twms.domain.policy.RegisteredPolicyStatusType;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyCoverageRequest;
import tavant.twms.domain.policy.WarrantyCoverageRequestAudit;
import tavant.twms.domain.policy.WarrantyCoverageRequestService;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.i18n.I18nActionSupport;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.CalendarInterval;
import com.domainlanguage.timeutil.Clock;


@SuppressWarnings("serial")
public class ManageReducedCoverage extends I18nActionSupport {

	private List<MultipleInventoryAttributesMapper> inventoryItemMappings = new ArrayList<MultipleInventoryAttributesMapper>(
			5);

	private List<WarrantyCoverageRequest> itemsWithReducedCoverage;
	
	private Map<String, Integer> reductionInCvg ;
	
	private Map<String, Integer> reductionInCvgDays ;
	
	private Map<String, CalendarDate> warrantyEndDateOfPolicy ;
	
	private Map<String, Integer> serviceHoursCovered ;
	
	
	private Long inventoryItemId;
	
	private Long id;
	
	private List<PolicyDefinition> goodWillPolicies;
	
	private Map<Long, Boolean> itemSelected ; 

	private Map<Long, Boolean> itemRequestStatus ;
	
	private Map<Long, String> itemComments ;	
	
	private Map<Long, Boolean> policesApproved;
		
	private String adminAction;
	
	private List<CalendarDate> selectedWarrantyEndDates;
	
	private WarrantyCoverageRequestAudit audit;
	
	private List<ReducedCoveragePolicySelectForm> goodWillPoliciesSelected;
	
	private PolicyService policyService;
		
	private WarrantyCoverageRequestService warrantyCoverageRequestService;

	private InventoryService inventoryService;
	
	private PolicyDefinitionService policyDefinitionService;
	
	private WarrantyService warrantyService;
	
	/**
	 *TODO To be removed
	 * @return
	 * @throws Exception
	 */
	public String storeReducedCoverageInformation() throws Exception {
		for (Iterator<MultipleInventoryAttributesMapper> miapIter = this.inventoryItemMappings
				.iterator(); miapIter.hasNext();) {			
			MultipleInventoryAttributesMapper miap = miapIter.next();
			InventoryItem item = this.inventoryService
					.findInventoryItem(miap.inventoryItem.getId());
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(item.getBusinessUnitInfo().getName());
			WarrantyCoverageRequest wcr = new WarrantyCoverageRequest();
			wcr.setInventoryItem(item);
			wcr.setStatus(WarrantyCoverageRequestAudit.WAITING_FOR_YOUR_RESPONSE);
			WarrantyCoverageRequestAudit wcra = new WarrantyCoverageRequestAudit();
			wcra.setStatus(WarrantyCoverageRequestAudit.WAITING_FOR_YOUR_RESPONSE);
			wcra.setComments("System Created");
			wcra.setAssignedBy(getLoggedInUser());
			wcr.getAudits().add(wcra);
			warrantyCoverageRequestService.save(wcr);
			populateUIData(wcr);
		}
		return SUCCESS;
	}


	@Override
	public void validate() {
		Map<Long,Long> uniqueGoodWillPlans = new HashMap<Long,Long>();
		if("Approve".equals(adminAction) && goodWillPoliciesSelected != null){
			for (ReducedCoveragePolicySelectForm inputPolicy : goodWillPoliciesSelected) {
				if(inputPolicy.getSelected()){
					if(uniqueGoodWillPlans.get(inputPolicy.getGoodWillPolicyId())!= null){
						PolicyDefinition policy = policyDefinitionService.findPolicyDefinitionById(inputPolicy.getGoodWillPolicyId());
						addActionError("message.reduced.coverage.policy.duplicates",policy.getDescription());
						break;				
					}else{
						uniqueGoodWillPlans.put(inputPolicy.getGoodWillPolicyId(),
								inputPolicy.getGoodWillPolicyId());
					}		
				}
			}
		}
	}
   //added to sort std policies based on priority
	private void sortStandardPoliciesForUIDisplay() {

		Collections.sort(itemsWithReducedCoverage.get(0).getPoliciesWithReducedCoverage(),
				new Comparator<PolicyDefinition>() {
					public int compare(PolicyDefinition p1, PolicyDefinition p2) {
						return p1.getPriority().compareTo(p2.getPriority());
					}
				});
	}


	public String saveRequestInfoDealer() throws Exception {
		validateDataDealer();
		if (hasFieldErrors() || hasActionErrors()) {
			showDetailDealer();
			return INPUT;
		}
		for (Long itemId : getInventoriesSelected()) {
		 WarrantyCoverageRequest wcr = this.warrantyCoverageRequestService.findByInventoryItemId(itemId);
		 
		 
		 if(Boolean.FALSE.equals(this.itemRequestStatus.get(itemId))){
			 wcr.setStatus(WarrantyCoverageRequestAudit.EXTENSION_NOT_REQUESTED);
			 WarrantyCoverageRequestAudit wcra = new WarrantyCoverageRequestAudit();
			 wcra.setStatus(WarrantyCoverageRequestAudit.EXTENSION_NOT_REQUESTED);
			 wcra.setAssignedBy(getLoggedInUser());
			 wcra.setComments(this.itemComments.get(itemId));
			 wcra.getD().setCreatedOn(CalendarDate.from(Clock.now(),TimeZone.getDefault()));
			 wcra.getD().setCreatedTime(Clock.now().asJavaUtilDate());
			 wcr.getAudits().add(wcra);
			 this.warrantyCoverageRequestService.save(wcr);
			 addActionMessage("message.reduced.coverage.notRequested");
		 } else{			 
			 WarrantyCoverageRequestAudit wcra = new WarrantyCoverageRequestAudit();
			 
			 if (wcr.getAudits().size() > 1) {
					wcr.setStatus(WarrantyCoverageRequestAudit.REPLIED);
					wcra.setStatus(WarrantyCoverageRequestAudit.REPLIED);
				} else {
					wcr.setStatus(WarrantyCoverageRequestAudit.SUBMITTED);
					wcra.setStatus(WarrantyCoverageRequestAudit.SUBMITTED);
				}
			 
			 wcra.setAssignedBy(getLoggedInUser());
			 wcra.setComments(this.itemComments.get(itemId));
			 wcra.getD().setCreatedOn(CalendarDate.from(Clock.now(),TimeZone.getDefault()));
			 wcra.getD().setCreatedTime(Clock.now().asJavaUtilDate());
			 wcr.getAudits().add(wcra);
			 this.warrantyCoverageRequestService.save(wcr);
			 addActionMessage("message.reduced.coverage.requested");
		 	}
		}
		
		
		return SUCCESS;
	}

	
	
	public String saveRequestInfoForAdmin() throws Exception {
		WarrantyCoverageRequest wcr = this.warrantyCoverageRequestService.findById(id);
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(wcr.getInventoryItem().getBusinessUnitInfo().getName());
		validateDataForAdmin(wcr.getInventoryItem().getLatestWarranty(),wcr.getInventoryItem());
		validate();
		if(hasActionErrors() || hasFieldErrors()){
			showDetail();
			return INPUT;
		}
		
		if("Deny".equals(adminAction)){
     		audit.setStatus(WarrantyCoverageRequestAudit.DENIED);
			audit.setAssignedBy(getLoggedInUser());
			audit.getD().setCreatedOn(CalendarDate.from(Clock.now(),TimeZone.getDefault()));
			audit.getD().setCreatedTime(Clock.now().asJavaUtilDate());
			wcr.setStatus(WarrantyCoverageRequestAudit.DENIED);		
			WarrantyCoverageRequestAudit firstAudit= wcr.getAudits().last();
			audit.setAssignedTo(firstAudit.getAssignedBy());
			addActionMessage("message.reduced.coverage.denied");
		} else if ("RequestMoreInfo".equals(adminAction)){
     		audit.setStatus(WarrantyCoverageRequestAudit.FORWARDED);
     		audit.getD().setCreatedOn(CalendarDate.from(Clock.now(),TimeZone.getDefault()));
     		audit.getD().setCreatedTime(Clock.now().asJavaUtilDate());
			audit.setAssignedBy(getLoggedInUser());
			WarrantyCoverageRequestAudit latestAudit= wcr.getAudits().first();
			audit.setAssignedTo(latestAudit.getAssignedBy());
			wcr.setStatus(WarrantyCoverageRequestAudit.FORWARDED);
			addActionMessage("message.reduced.coverage.forwarded");
		} else if("Approve".equals(adminAction)){			
			List<RegisteredPolicy> extensionList = createPoliciesForWarranty(wcr.getInventoryItem());
			wcr.getInventoryItem().getWarranty().getPolicies().addAll(extensionList);
			warrantyService.updateInventoryForWarrantyDates(wcr.getInventoryItem());
			this.warrantyService.update(wcr.getInventoryItem().getWarranty());
			audit.setStatus(WarrantyCoverageRequestAudit.APPROVED);
			audit.getD().setCreatedOn(CalendarDate.from(Clock.now(),TimeZone.getDefault()));
			audit.getD().setCreatedTime(Clock.now().asJavaUtilDate());
			wcr.setStatus(WarrantyCoverageRequestAudit.APPROVED);
		    audit.setAssignedBy(getLoggedInUser());
		    WarrantyCoverageRequestAudit firstAudit= wcr.getAudits().last();
			audit.setAssignedTo(firstAudit.getAssignedTo());
		    addActionMessage("message.reduced.coverage.approved");			
		}
		
		wcr.getAudits().add(audit);
		this.warrantyCoverageRequestService.update(wcr);		
		
		return SUCCESS;
	}

	private List<RegisteredPolicy>  createPoliciesForWarranty(InventoryItem item) {
		List<RegisteredPolicy> extensionList = new ArrayList<RegisteredPolicy>();
		List<PolicyDefinition> dbPolicies = fetchSelectedPoliciesFromDB();
		for (ReducedCoveragePolicySelectForm inputPolicy : goodWillPoliciesSelected) {
		   RegisteredPolicy policy = new RegisteredPolicy();
	       policy.setPolicyDefinition(findDefinitionById(dbPolicies,inputPolicy.getGoodWillPolicyId() ));
	       policy.setWarranty(item.getWarranty());
	       policy.setPrice(Money.dollars(0));	       
	       RegisteredPolicyAudit audit = new RegisteredPolicyAudit();
	       Clock.setDefaultTimeZone(TimeZone.getDefault());
	       audit.setCreatedBy(getLoggedInUser());
	       audit.setServiceHoursCovered(inputPolicy.getServiceHoursCovered());
	       audit.setCreatedOn(Clock.now());	       
	       CalendarDate startDate = item.getWarranty().getStartDate();				       
	       CalendarDuration warrantyPeriod  = new CalendarDuration(startDate,inputPolicy.getWarrantyEndDate());	       
	       audit.setWarrantyPeriod(warrantyPeriod);
	       audit.setComments(this.audit.getComments());
	       audit.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
	       policy.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
	       policy.getPolicyAudits().add(audit);
	       extensionList.add(policy);	       
		}
		
		return extensionList;
	}

	private PolicyDefinition findDefinitionById(List<PolicyDefinition> list ,Long id ){		
		for (PolicyDefinition policyDefinition : list) {
			if(policyDefinition.getId().longValue() == id.longValue()){
				return policyDefinition;
			}
		}		
		return null;		
	}

	private List<PolicyDefinition> fetchSelectedPoliciesFromDB() {
		List<Long> collectionOfIds = new ArrayList<Long>();
		for (ReducedCoveragePolicySelectForm inputPolicy : goodWillPoliciesSelected) {
            if(inputPolicy.getSelected()){
            collectionOfIds.add(inputPolicy.getGoodWillPolicyId());
            }
        }
		return  policyDefinitionService.findByIds(collectionOfIds);
	}
	
	public String showReducedCoverageDealer() throws Exception {
		WarrantyCoverageRequest wcr = this.warrantyCoverageRequestService.findByInventoryItemId(inventoryItemId);
		populateUIData(wcr);
		return SUCCESS;
	}

	public String preview(){
		fetchCoverageRequestDetails();
		return SUCCESS;		
	}
	
	/**
	 * This is the view for the administrator to approve
	 * @return
	 */
	
	public String showDetailDealer() throws PolicyException {
		fetchCoverageRequestDetails();
		sortStandardPoliciesForUIDisplay();
		if (itemsWithReducedCoverage.get(0).isRequestWithDealer()) {
			return SUCCESS;
		} else {
			return "readOnly";
		}
	}
	
	
	/**
	 * This is the view for the administrator to approve
	 * @return
	 */	
	public String showDetail() throws PolicyException {
		fetchCoverageRequestDetails();
		InventoryItem invItem = itemsWithReducedCoverage.iterator().next().getInventoryItem();
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(invItem.getBusinessUnitInfo().getName());	
		sortStandardPoliciesForUIDisplay();
		if(!invItem.getSerializedPart())
			this.goodWillPolicies = policyService.findGoodWillPoliciesForInventory
			(invItem.getSerialNumber(),CalendarDate.from(Clock.now(),TimeZone.getDefault()));
		else
			this.goodWillPolicies = policyService.findGoodWillPoliciesForMajorComponent
			(invItem.getSerialNumber(),CalendarDate.from(Clock.now(),TimeZone.getDefault()));
		return SUCCESS;		
	}	
	
	
	private void fetchCoverageRequestDetails() {
		WarrantyCoverageRequest wcr = this.warrantyCoverageRequestService.findById(id);
		populateUIData(wcr);
	}
	

	private int computeReductionInCoverage(RegisteredPolicy registeredPolicy) {
		CalendarInterval calendarInterval = CalendarInterval.inclusive(
				registeredPolicy.getWarrantyPeriod().getFromDate(),
				registeredPolicy.getWarrantyPeriod().getTillDate());
		Integer actualCoverage = calendarInterval.lengthInMonthsInt();
		Integer coverageTerms = registeredPolicy.getPolicyDefinition()
				.getCoverageTerms().getMonthsCoveredFromDelivery();
		int reduction = coverageTerms.intValue() - actualCoverage.intValue();
		return reduction;
	}

	private void validateDataDealer() {
		if (this.itemSelected == null || this.itemSelected.size() == 0) {
			addActionError("message.reduced.coverage.inventories.notselected");
			return;
		}
		List<Long> inventoriesSelected = getInventoriesSelected();
		if (inventoriesSelected == null || inventoriesSelected.size() == 0) {
			addActionError("message.reduced.coverage.inventories.notselected");
			return;
		}
		// check comments
		for (Long itemId : inventoriesSelected) {
			if (itemComments == null
					|| !StringUtils.hasText(itemComments.get(itemId))) {
				addFieldError("itemsComments['"
						+ String.valueOf(itemId.longValue()) + "']",
						"message.reduced.coverage.comments.notInput");
			}
		}
		// check if coverage requested or not used
		for (Long itemId : inventoriesSelected) {
			if (this.itemRequestStatus == null
					|| this.itemRequestStatus.get(itemId) == null) {
				addFieldError("itemRequestStatus['"
						+ String.valueOf(itemId.longValue()) + "']",
						"message.reduced.coverage.request.noAction");
			}
		}
		

	}

	private void validateDataForAdmin(Warranty warranty, InventoryItem inventoryItem) {

		if (!"Approve".equals(adminAction)) {
			if (!StringUtils.hasText(audit.getComments())) {
				addFieldError("audit.comments", "message.reduced.coverage.comments.notInput");
			}
		}

		if ("Approve".equals(adminAction)) {
				boolean isAnyPolicySelected = false;
				if (goodWillPoliciesSelected != null) {
					int index = 0;
					ReducedCoveragePolicySelectForm goodWillPolicy = null; 
					for (Iterator<ReducedCoveragePolicySelectForm> it = 
						goodWillPoliciesSelected.iterator();it.hasNext();) {
	
						goodWillPolicy = it.next();
						if (Boolean.TRUE.equals(goodWillPolicy.getSelected())) {
							isAnyPolicySelected = true;
	
							if (goodWillPolicy.getGoodWillPolicyId() == null) {
								addFieldError("goodWillPoliciesSelected[" + index
										+ "].goodWillPolicyId",
										"message.reduced.coverage.goodWillpolicy.notselected");
							}
	
							if (goodWillPolicy.getWarrantyEndDate() == null) {
								addFieldError("goodWillPoliciesSelected[" + index
										+ "].warrantyEndDate",
										"message.reduced.coverage.warrantyEndDate.notselected");
							}
	
							if (!inventoryItem.getSerializedPart() && goodWillPolicy.getServiceHoursCovered() == null) {
								addFieldError("goodWillPoliciesSelected[" + index
										+ "].serviceHoursCovered",
										"message.reduced.coverage.hoursOnMachine.notInput");
							}
	
							if(goodWillPolicy.getServiceHoursCovered()!=null && ((int)goodWillPolicy.getServiceHoursCovered().doubleValue() <=0)){
								addFieldError("goodWillPoliciesSelected[" + index
										+ "].serviceHoursCovered",
										"message.reduced.coverage.hoursOnMachine.notValid");								
							}
							
							if(goodWillPolicy.getWarrantyEndDate() !=null && !goodWillPolicy.getWarrantyEndDate().isAfter(warranty.getStartDate())){
								addFieldError("goodWillPoliciesSelected[" + index
										+ "].warrantyEndDate",
										"message.reduced.coverage.warrantyEndDate.invalid");								
							}
							
						} else{
							it.remove();
						}
	
					 }
			    }
	
				if (!isAnyPolicySelected) {
					addActionError("message.reduced.coverage.policy.notselected");
				}

		 }
   	 }
	
	private List<Long> getInventoriesSelected() {
		List<Long> inventoriesSelected = new ArrayList<Long>();
		for (Map.Entry<Long, Boolean> element : this.itemSelected.entrySet()) {
			if (Boolean.TRUE.equals(element.getValue())) {
				inventoriesSelected.add(element.getKey());
			}
		}
		return inventoriesSelected;
	}

	private void populateUIData(WarrantyCoverageRequest wcr) {
		if (itemsWithReducedCoverage == null) {
			itemsWithReducedCoverage = new ArrayList<WarrantyCoverageRequest>();
		}
		if (reductionInCvg == null) {			
			reductionInCvg = new HashMap<String, Integer>();
		}
		
		if (reductionInCvgDays == null) {			
			reductionInCvgDays = new HashMap<String, Integer>();
		}
		
		if(warrantyEndDateOfPolicy == null){
			warrantyEndDateOfPolicy = new HashMap<String, CalendarDate>();
		}
		
		if (serviceHoursCovered == null) {			
			serviceHoursCovered = new HashMap<String, Integer>();
		}
				
		itemsWithReducedCoverage.add(wcr);

		for (RegisteredPolicy policy : wcr.getInventoryItem().getWarranty().getPolicies()) {
			if(!WarrantyType.POLICY.getType()
					.equals(policy.getPolicyDefinition().getWarrantyType().getType())){
				/*int reductionInCoverageMonths = computeReductionInCoverage(policy);*/
				CalendarInterval ci = policy.reductionInCoverage(); 
				if(ci != null ){
					wcr.addPoliciesWithReducedCoverage(policy.getPolicyDefinition());
					int reductionInCoverageMonths = ci.lengthInMonthsInt();
					if(reductionInCoverageMonths >0){
						reductionInCvg.put(policy.getPolicyDefinition().getCode(), reductionInCoverageMonths);
					} else{
						int reductionInCoverageDays = ci.lengthInDaysInt();
						reductionInCvgDays.put(policy.getPolicyDefinition().getCode(), reductionInCoverageDays);
					}
				}

				Integer monthsCoveredFromDelivery = policy.getPolicyDefinition().getCoverageTerms()
				.getMonthsCoveredFromDelivery();
				CalendarDate maxDateForWarrantyCoverage = policy.getWarrantyPeriod().getFromDate().plusMonths(monthsCoveredFromDelivery).previousDay();			
				warrantyEndDateOfPolicy.put(policy.getPolicyDefinition().getCode(), maxDateForWarrantyCoverage);			
				serviceHoursCovered.put(policy.getPolicyDefinition().getCode(), policy.getPolicyDefinition().getCoverageTerms().getServiceHoursCovered());
			}
		}
	
	}

	public List<MultipleInventoryAttributesMapper> getInventoryItemMappings() {
		return inventoryItemMappings;
	}

	public void setInventoryItemMappings(
			List<MultipleInventoryAttributesMapper> inventoryItemMappings) {
		this.inventoryItemMappings = inventoryItemMappings;
	}

	public void setWarrantyCoverageRequestService(
			WarrantyCoverageRequestService warrantyCoverageRequestService) {
		this.warrantyCoverageRequestService = warrantyCoverageRequestService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public List<WarrantyCoverageRequest> getItemsWithReducedCoverage() {
		return itemsWithReducedCoverage;
	}

	public void setItemsWithReducedCoverage(
			List<WarrantyCoverageRequest> itemsWithReducedCoverage) {
		this.itemsWithReducedCoverage = itemsWithReducedCoverage;
	}

	public Map<Long, Boolean> getItemSelected() {
		return itemSelected;
	}

	public void setItemSelected(Map<Long, Boolean> itemSelected) {
		this.itemSelected = itemSelected;
	}

	public Map<Long, Boolean> getItemRequestStatus() {
		return itemRequestStatus;
	}

	public void setItemRequestStatus(Map<Long, Boolean> itemRequestStatus) {
		this.itemRequestStatus = itemRequestStatus;
	}


	public Map<Long, String> getItemComments() {
		return itemComments;
	}

	public void setItemComments(Map<Long, String> itemComments) {
		this.itemComments = itemComments;
	}

	public Map<String, Integer> getReductionInCvg() {
		return reductionInCvg;
	}

	public void setReductionInCvg(Map<String, Integer> reductionInCvg) {
		this.reductionInCvg = reductionInCvg;
	}

	public Long getInventoryItemId() {
		return inventoryItemId;
	}

	public void setInventoryItemId(Long inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<Long, Boolean> getPolicesApproved() {
		return policesApproved;
	}


	public void setPolicesApproved(Map<Long, Boolean> policesApproved) {
		this.policesApproved = policesApproved;
	}


	public List<PolicyDefinition> getGoodWillPolicies() {
		return goodWillPolicies;
	}


	public void setGoodWillPolicies(List<PolicyDefinition> goodWillPolicies) {
		this.goodWillPolicies = goodWillPolicies;
	}


	public String getAdminAction() {
		return adminAction;
	}

	public WarrantyCoverageRequestAudit getAudit() {
		return audit;
	}


	public void setAudit(WarrantyCoverageRequestAudit audit) {
		this.audit = audit;
	}


	public void setUserComments(WarrantyCoverageRequestAudit userComments) {
		this.audit = userComments;
	}


	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}


	public List<CalendarDate> getSelectedWarrantyEndDates() {
		return selectedWarrantyEndDates;
	}


	public void setSelectedWarrantyEndDates(
			List<CalendarDate> selectedWarrantyEndDates) {
		this.selectedWarrantyEndDates = selectedWarrantyEndDates;
	}


	public void setAdminAction(String adminAction) {
		this.adminAction = adminAction;
	}


	public List<ReducedCoveragePolicySelectForm> getGoodWillPoliciesSelected() {
		return goodWillPoliciesSelected;
	}


	public void setGoodWillPoliciesSelected(
			List<ReducedCoveragePolicySelectForm> goodWillPoliciesSelected) {
		this.goodWillPoliciesSelected = goodWillPoliciesSelected;
	}


	public void setPolicyDefinitionService(
			PolicyDefinitionService policyDefinitionService) {
		this.policyDefinitionService = policyDefinitionService;
	}


	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}


	public Map<String, Integer> getReductionInCvgDays() {
		return reductionInCvgDays;
	}


	public void setReductionInCvgDays(Map<String, Integer> reductionInCvgDays) {
		this.reductionInCvgDays = reductionInCvgDays;
	}


	public Map<String, CalendarDate> getWarrantyEndDateOfPolicy() {
		return warrantyEndDateOfPolicy;
	}


	public void setWarrantyEndDateOfPolicy(
			Map<String, CalendarDate> warrantyEndDateOfPolicy) {
		this.warrantyEndDateOfPolicy = warrantyEndDateOfPolicy;
	}


	public Map<String, Integer> getServiceHoursCovered() {
		return serviceHoursCovered;
	}


	public void setServiceHoursCovered(Map<String, Integer> serviceHoursCovered) {
		this.serviceHoursCovered = serviceHoursCovered;
	}


}


 
