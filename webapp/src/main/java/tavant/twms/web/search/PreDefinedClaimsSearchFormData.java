package tavant.twms.web.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.ManufacturingSiteInventory;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.rules.RuleAdministrationService;

public class PreDefinedClaimsSearchFormData {

	private List<User> userList; 

	
	private OrgService orgService;

	private RuleAdministrationService ruleAdministrationService;
	
	private DealerGroupService dealerGroupService;

	private CampaignService campaignService;
	
	private ItemGroupService itemGroupService;
	
	private List<Campaign> campaignList;
	
	private List<DealerGroup> dealerGroups;

	private List<ItemGroup> partGroups;
		
	private List<ListOfValues> accountabilityCodeList;
	
	private List<ListOfValues> manufacturingSiteList;
	
	private List<ListOfValues> acceptanceReasonList;
	
	private List<DomainRule> rulesList; 
	
	private LovRepository lovRepository; 
	
	private List<User> assignToUserList;
	
	private List<ListOfValues> onHoldReasonList;
	
	private List<ListOfValues> forwardedReasonList;
	
	private List<ListOfValues> rejectionReasonList;

	private ConfigParamService configParamService;

	public Map<String, List<ClaimState>> getState() {
		SortedMap<String, List<ClaimState>> state = new TreeMap<String, List<ClaimState>>();
		List<ClaimState> draftState = new ArrayList<ClaimState>();
		draftState.add(ClaimState.DRAFT);
		state.put(ClaimState.DRAFT.name(), draftState);
		List<ClaimState> claimClosedStates = new ArrayList<ClaimState>();
		claimClosedStates.add(ClaimState.DENIED_AND_CLOSED);
		claimClosedStates.add(ClaimState.ACCEPTED_AND_CLOSED);
		state.put("CLOSED", claimClosedStates);
		List<ClaimState> claimOpenStates = new ArrayList<ClaimState>();
		claimOpenStates.add(ClaimState.IN_PROGRESS);
		
		if(configParamService.getBooleanValue(ConfigName.SMR_CLAIM_ALLOWED.getName())){
			claimOpenStates.add(ClaimState.SERVICE_MANAGER_REVIEW);
			claimOpenStates.add(ClaimState.SERVICE_MANAGER_RESPONSE);
		}
		
		claimOpenStates.add(ClaimState.FORWARDED);
		state.put("OPEN", claimOpenStates);		
		return state;

	}

	public Map<PartReturnStatus, Boolean> getPartReturnStatuses() {
		Map<PartReturnStatus, Boolean> map = new HashMap<PartReturnStatus, Boolean>();
		map.put(PartReturnStatus.PART_TO_BE_SHIPPED, Boolean.FALSE);		
		map.put(PartReturnStatus.PART_SHIPPED, Boolean.FALSE);
		map.put(PartReturnStatus.PART_RECEIVED, Boolean.FALSE);
		
		return map;

	}
	
	
	public List<PartReturnStatus> getPartReturnStatusesForInternal() {
		List<PartReturnStatus> list = new ArrayList<PartReturnStatus>();
		list.add(PartReturnStatus.PART_TO_BE_SHIPPED);
		list.add(PartReturnStatus.PART_SHIPPED);
		//TODO OVERDUE
		
		list.add(PartReturnStatus.PART_RECEIVED);		
		list.add(PartReturnStatus.PART_ACCEPTED);
		list.add(PartReturnStatus.PART_REJECTED);
		
		return list;

	}	
	
	private List<ClaimState> getClaimOpenStates() {
		List<ClaimState> claimOpenStates = new ArrayList<ClaimState>();
		//Claim state APPEALED added to fix ESESA-1642
        claimOpenStates.add(ClaimState.APPEALED);
		claimOpenStates.add(ClaimState.FORWARDED);
		//claimOpenStates.add(ClaimState.PROCESSOR_REVIEW);
		claimOpenStates.add(ClaimState.ADVICE_REQUEST);
		claimOpenStates.add(ClaimState.ACCEPTED);
		claimOpenStates.add(ClaimState.REPLIES);
		claimOpenStates.add(ClaimState.SUBMITTED);

		//Claim state APPEALED added to fix ESESA-1743
        claimOpenStates.add(ClaimState.EXTERNAL_REPLIES);
		
		claimOpenStates.add(ClaimState.PENDING_PAYMENT_SUBMISSION);
		claimOpenStates.add(ClaimState.PENDING_PAYMENT_RESPONSE);
		claimOpenStates.add(ClaimState.TRANSFERRED);
		claimOpenStates.add(ClaimState.APPROVED);
		
		if(configParamService.getBooleanValue(ConfigName.SMR_CLAIM_ALLOWED.getName())){
			claimOpenStates.add(ClaimState.SERVICE_MANAGER_REVIEW);		
		    claimOpenStates.add(ClaimState.SERVICE_MANAGER_RESPONSE);
		}
		
		claimOpenStates.add(ClaimState.PROCESSOR_REVIEW);
		claimOpenStates.add(ClaimState.ON_HOLD);
		
		claimOpenStates.add(ClaimState.ON_HOLD_FOR_PART_RETURN);
		claimOpenStates.add(ClaimState.REOPENED);
		return claimOpenStates;
	}
	
	private List<ClaimState> getClaimOpenStatesForDealer() {
		List<ClaimState> claimOpenStates = new ArrayList<ClaimState>();
		
		claimOpenStates.add(ClaimState.FORWARDED);
		if(configParamService.getBooleanValue(ConfigName.SMR_CLAIM_ALLOWED.getName())){
			claimOpenStates.add(ClaimState.SERVICE_MANAGER_REVIEW);
	        claimOpenStates.add(ClaimState.SERVICE_MANAGER_RESPONSE);
		}
		claimOpenStates.add(ClaimState.ON_HOLD);		
	    return claimOpenStates;
	}			
	
	private List<ClaimState> getClaimClosedStates() {
		List<ClaimState> claimClosedStates = new ArrayList<ClaimState>();
		claimClosedStates.add(ClaimState.ACCEPTED_AND_CLOSED); 
		claimClosedStates.add(ClaimState.DENIED_AND_CLOSED);
		return claimClosedStates;
	}
	
	public Map<String, List<ClaimState>> getClaimStatesForInternal() {
		SortedMap<String, List<ClaimState>> claimStates = new TreeMap<String, List<ClaimState>>();
		claimStates.put("OPEN", getClaimOpenStates());
		claimStates.put("CLOSED", getClaimClosedStates());
				
		List<ClaimState> claimDeletedStates = new ArrayList<ClaimState>();
		claimDeletedStates.add(ClaimState.DEACTIVATED);
		claimStates.put(ClaimState.DEACTIVATED.name(), claimDeletedStates);

		return claimStates;
	}
	
	public Map<String, List<ClaimState>> getClaimAllOpenStatesForExternal() {
		SortedMap<String, List<ClaimState>> claimStates = new TreeMap<String, List<ClaimState>>();
		claimStates.put("OPEN", getClaimOpenStates());
		claimStates.put("CLOSED", getClaimClosedStates());
				
		List<ClaimState> claimDeletedStates = new ArrayList<ClaimState>();
		claimDeletedStates.add(ClaimState.DRAFT);
		claimStates.put(ClaimState.DRAFT.name(), claimDeletedStates);

		return claimStates;
	}
	
	public Map<String, List<ClaimState>> getClaimStatesForExternal() {
		SortedMap<String, List<ClaimState>> claimStates = new TreeMap<String, List<ClaimState>>();
		claimStates.put("OPEN", getClaimOpenStatesForDealer());
		claimStates.put("CLOSED", getClaimClosedStates());
				
		List<ClaimState> claimDeletedStates = new ArrayList<ClaimState>();
		claimDeletedStates.add(ClaimState.DRAFT);
		claimStates.put(ClaimState.DRAFT.name(), claimDeletedStates);

		return claimStates;
	}
	public void populateSearchFields(){
		this.userList = new ArrayList<User>();
		userList.addAll(orgService.findAllProcessors());
		this.assignToUserList=new ArrayList<User>();
		/*getting assign to user values to display under predefined search asignto textbox*/
		assignToUserList.addAll(orgService.findAllAssignToUsers());
		Collections.sort(assignToUserList, User.SORT_BY_COMPLETE_NAME);
		Collections.sort(userList, User.SORT_BY_COMPLETE_NAME);
		
		this.campaignList = campaignService.findAll();
		this.dealerGroups=dealerGroupService.findAll();
		this.partGroups = itemGroupService.findAll();
		Collections.sort(dealerGroups, DEALER_GROUP_SORT);
		Collections.sort(partGroups, PART_GROUP_SORT);
		//this.rulesList = ruleAdministrationService.findRulesByContext("ClaimRules");
		this.acceptanceReasonList = lovRepository.findAllActive("AcceptanceReason"); //SLMSPROD-1291,SLMSPROD-1287 -- only active LOVs required
		this.accountabilityCodeList = lovRepository.findAllActive("AccountabilityCode"); 
		this.manufacturingSiteList = lovRepository.findAllActive("ManufacturingSiteInventory"); 
		this.onHoldReasonList = lovRepository.findAllActive("PutOnHoldReason"); 
		this.forwardedReasonList = lovRepository.findAllActive("RequestInfoFromUser");
		this.rejectionReasonList = lovRepository.findAllActive("RejectionReason"); 
		
		sortManufacturingSiteByBuAppendedName();
	}
	
	@SuppressWarnings("unchecked")
	private void sortManufacturingSiteByBuAppendedName(){		
		if(manufacturingSiteList != null){			
			Collections.sort(manufacturingSiteList,new Comparator(){
				public int compare(Object obj0, Object obj1) {
					ManufacturingSiteInventory manufSite0 =(ManufacturingSiteInventory) obj0;
					ManufacturingSiteInventory manufSite1 =(ManufacturingSiteInventory) obj1;
					return manufSite0.getBuAppendedName().compareTo(manufSite1.getBuAppendedName());
				}

			});
		}
	}
	
	private Comparator<DealerGroup> DEALER_GROUP_SORT = new Comparator<DealerGroup>() {
		public int compare(DealerGroup e1, DealerGroup e2) {
			return e1.getName().toLowerCase()
					.compareTo(e2.getName().toLowerCase());
		}
	};

	private Comparator<ItemGroup> PART_GROUP_SORT = new Comparator<ItemGroup>() {
		public int compare(ItemGroup e1, ItemGroup e2) {
			return e1.getName().toLowerCase()
					.compareTo(e2.getName().toLowerCase());
		}
	};
	
	        
	public List<User> getUserList() {
		return this.userList;
	}

	public List<DomainRule> getRulesList() {
		return this.rulesList;
	}

	
	public List<ListOfValues> getAccountabilityCodeList() {
		return accountabilityCodeList;
	}
	
	public List<ListOfValues> getManufacturingSiteList() {
		return manufacturingSiteList;
	}

	public List<ListOfValues> getAcceptanceReasonList() {
		return acceptanceReasonList;
	}

	public List<Campaign> getCampaignList(){
		return this.campaignList;
	}
	

	public List<DealerGroup> getDealerGroups() {
		return dealerGroups;
	}

	public void setDealerGroups(List<DealerGroup> dealerGroups) {
		this.dealerGroups = dealerGroups;
	}

	public List<ItemGroup> getPartGroups() {
		return partGroups;
	}

	public void setPartGroups(List<ItemGroup> partGroups) {
		this.partGroups = partGroups;
	}
	
	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void setRuleAdministrationService(
			RuleAdministrationService ruleAdministrationService) {
		this.ruleAdministrationService = ruleAdministrationService;
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}
	
	public List<User> getAssignToUserList() {
		return this.assignToUserList;
	}
	

	public DealerGroupService getDealerGroupService() {
		return dealerGroupService;
	}

	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}
	
	public ItemGroupService getItemGroupService() {
		return itemGroupService;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	public List<ListOfValues> getOnHoldReasonList() {
		return onHoldReasonList;
	}

	public void setOnHoldReasonList(List<ListOfValues> onHoldReasonList) {
		this.onHoldReasonList = onHoldReasonList;
	}

	public List<ListOfValues> getForwardedReasonList() {
		return forwardedReasonList;
	}

	public void setForwardedReasonList(List<ListOfValues> forwardedReasonList) {
		this.forwardedReasonList = forwardedReasonList;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
	public List<ListOfValues> getRejectionReasonList() {
		return rejectionReasonList;
	}

	public void setRejectionReasonList(List<ListOfValues> rejectionReasonList) {
		this.rejectionReasonList = rejectionReasonList;
	}



}
