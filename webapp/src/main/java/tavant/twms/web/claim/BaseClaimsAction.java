/*
 *   Copyright (c)2008 Tavant Technologies
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
package tavant.twms.web.claim;

import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.ASM_CHILDREN;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.SP_CHILDREN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.json.JSONException;
import org.json.JSONObject;

import tavant.twms.domain.alarmcode.AlarmCode;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAttributes;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.failurestruct.ActionNode;
import tavant.twms.domain.failurestruct.Assembly;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.inventory.InvClassDealerMapping;
import tavant.twms.domain.inventory.InvClassDealerMappingService;
import tavant.twms.domain.inventory.InventoryClass;
import tavant.twms.domain.laborType.LaborType;
import tavant.twms.domain.laborType.LaborTypeService;
import tavant.twms.domain.loa.LimitOfAuthorityScheme;
import tavant.twms.domain.loa.LimitOfAuthoritySchemeService;
import tavant.twms.domain.orgmodel.AdditionalLaborEligibility;
import tavant.twms.domain.orgmodel.AdditionalLaborEligibilityService;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.PageResult;
import tavant.twms.jbpm.assignment.AssignmentRuleExecutor;
import tavant.twms.jbpm.assignment.LoadBalancingService;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.security.authz.infra.SecurityHelper;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

public abstract class BaseClaimsAction extends SummaryTableAction {

	private AssemblyTreeJSONifier asmTreeJSONifier;
	protected AssignmentRuleExecutor assignmentRuleExecutor;
	private AdditionalLaborEligibilityService additionalLaborService;
	private LimitOfAuthoritySchemeService loaService;
	public ClaimService claimService;
	private ConfigParamService configParamService;
	private CostCategoryRepository costCategoryRepository;
	protected FailureStructureService failureStructureService;
	private LoadBalancingService loadBalancingService;
	private CatalogService catalogService;
	protected LaborTypeService laborTypeService;
	private Item causalPart;
	private boolean alarmCodesSectionVisible;
	private boolean partsReplacedInstalledSectionVisible;
	private boolean buPartReplaceableByNonBUPart;
	public List<CostCategory> configuredCostCategories = new ArrayList<CostCategory>();
	protected LaborDetail roundUpLaborDetail;
	protected List<String> eligibleLOAProcessors = new ArrayList<String>();
	protected boolean loaSchemeApplicable = false;
	private boolean displayCPFlagOnClaimPgOne;
	private boolean isAllAuditsHistoryShownToDealer;
	protected boolean showPartSerialNumber;
	protected List<ClaimAttributes> claimSpecificAttributes = new ArrayList<ClaimAttributes>();
	 private Long assemblyId;
    
    private InvClassDealerMappingService invClassDlrMappingService;

	public boolean isShowPartSerialNumber() {
		return showPartSerialNumber;
	}

	public void setShowPartSerialNumber(boolean showPartSerialNumber) {
		this.showPartSerialNumber = showPartSerialNumber;
	}

	public abstract Claim getClaimDetail();

	public Collection<String> getProcessors() {
		return findUsersBelongingToRole("processor",
				SelectedBusinessUnitsHolder.getSelectedBusinessUnit());
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> getWarrantySupervisors() {
		Set<User> serviceManagers = this.orgService
				.findUsersBelongingToRole(Role.WARRANTY_SUPERVISOR);
		if (serviceManagers != null && !serviceManagers.isEmpty())
			for (Iterator<User> iterator = serviceManagers.iterator(); iterator
					.hasNext();) {
				User serviceManager = (User) iterator.next();
								if (null != serviceManager.getPreferredBu()) {
						if (!serviceManager.getPreferredBu().equals(
								SelectedBusinessUnitsHolder
										.getSelectedBusinessUnit()))
							iterator.remove();
					} else {
						for (BusinessUnit bu : this.orgService
								.findAllBusinessUnitsForUser(serviceManager)) {
							if (bu.getName().equals(
									SelectedBusinessUnitsHolder
											.getSelectedBusinessUnit()))
								break;
							else
								iterator.remove();
						}
					}
			}
		return CollectionUtils.collect(serviceManagers, new Transformer() {
			public Object transform(Object input) {
				return ((User) input).getCompleteNameAndLogin();
			}
		});
	}
	
	public List<LaborType> getPrepareLaborTypeList() {
		List<LaborType> list = this.laborTypeService.findAll();
		LaborType lbrType = null;
		Iterator<LaborType> it = list.iterator();
		while (it.hasNext()) {
			lbrType = it.next();
			if (TWMSWebConstants.STATUS_INACTIVE.equalsIgnoreCase(lbrType
					.getStatus())) {
				it.remove();
			}
		}
		return list;
	}

	public LaborTypeService getLaborTypeService() {
		return laborTypeService;
	}

	public void setLaborTypeService(LaborTypeService laborTypeService) {
		this.laborTypeService = laborTypeService;
	}

	@SuppressWarnings("unchecked")
	public List<ListOfValues> getLovsForClass(String className, Claim claim) {
		return claimService.getLovsForClass(className, claim);
	}

	public String listProcessors() {
		SortedMap<String, String> processors = new TreeMap<String, String>();
		processors.put(getText("label.common.selectHeader"), "");
		for (String processor : getEligibleProcessors()) {
			processors.put(
					processor,
					processor.substring(processor.indexOf("(") + 1,
							processor.indexOf(")")));
		}
		return generateAndWriteComboboxJson(processors.entrySet(), "value",
				"key");
	}

	/**
	 * This method will list internal users with "warrantySupervisor" role and
	 * they can only approve late fee modified by processor.
	 * 
	 * @return
	 */
	public String listWarrantySupervisors() {
		Collection<String> warrantySupervisorNames = getWarrantySupervisors();
		warrantySupervisorNames.remove(getLoggedInUser()
				.getCompleteNameAndLogin());
		SortedMap<String, String> warrantySupervisors = new TreeMap<String, String>();
		warrantySupervisors.put(getText("label.common.selectHeader"), "");
		for (String warrantySupervisor : warrantySupervisorNames) {
			warrantySupervisors.put(warrantySupervisor, warrantySupervisor
					.substring(warrantySupervisor.indexOf("(") + 1,
							warrantySupervisor.indexOf(")")));
		}
		return generateAndWriteComboboxJson(warrantySupervisors.entrySet(),
				"value", "key");
	}

	public List<String> getEligibleProcessors() {
		List<String> eligibleProcessors = new ArrayList<String>();
		String claimAdvisor = null;
		if (!this.getClaimDetail().isNcr()) {
			eligibleProcessors.addAll(getProcessors());
			eligibleProcessors.remove(getLoggedInUser()
					.getCompleteNameAndLogin());
			claimAdvisor = Role.PROCESSOR;
		} else {
			claimAdvisor = Role.NCR_PROCESSOR;
			List<User> users = findEligibleUsersForAdvice(
					this.getClaimDetail(), claimAdvisor);
			Collections.sort(users, User.SORT_BY_COMPLETE_NAME);
			for (User advisor : users) {
				if (getLoggedInUser() != null
						&& !advisor.getName().equalsIgnoreCase(
								getLoggedInUser().getName())) {

					eligibleProcessors.add(advisor.getCompleteNameAndLogin());
				}
			}

		}

		if (eligibleProcessors.isEmpty()) {
			User defaulfUser = this.orgService
					.findDefaultUserBelongingToRole(claimAdvisor);
			eligibleProcessors.add(defaulfUser.getCompleteNameAndLogin());
		}

		Collections.sort(eligibleProcessors);
		return eligibleProcessors;
	}

	public String listServiceManagers() {
		return generateAndWriteComboboxJson(getServiceManagers().entrySet(),
				"key", "value");
	}

	public Map<String, String> getServiceManagers() {
		LinkedHashMap serviceMgrList = new LinkedHashMap();
		serviceMgrList.put("", getText("label.common.selectHeader"));
		String claimAdvisor = null;
		if (this.getClaimDetail().isNcr()) {
			claimAdvisor = Role.NCR_ADVISOR;
		} else {
			claimAdvisor = Role.DSM_ADVISOR;
		}
		List<User> users = findEligibleUsersForAdvice(this.getClaimDetail(),
				claimAdvisor);
		Collections.sort(users, User.SORT_BY_COMPLETE_NAME);
		for (User advisor : users) {
			if (getLoggedInUser() != null
					&& !advisor.getName().equalsIgnoreCase(
							getLoggedInUser().getName())) {

				serviceMgrList.put(advisor.getName(),
						advisor.getCompleteNameAndLogin());
			}
		}
		if (serviceMgrList.isEmpty()) {
			User defaulfUser = this.orgService
					.findDefaultUserBelongingToRole(claimAdvisor);
			if (defaulfUser != null)
				serviceMgrList
						.put(defaulfUser.getName(), defaulfUser.getName());
		}
		return serviceMgrList;
	}

	@SuppressWarnings("unchecked")
	private List<User> findEligibleUsersForAdvice(Claim claim,
			String claimAdvisor) {
		List<User> serviceManagers = new ArrayList<User>();
		List<String> advisorNames = this.assignmentRuleExecutor
				.fetchEligibleDSMAdvisorsUsingAssignmentRules(claim);
		if (serviceManagers != null && !advisorNames.isEmpty()) {
			serviceManagers = this.orgService
					.findUsersWithLoginIds(advisorNames);
		} else {
			Set<User> allServiceManagers = this.orgService
					.findUsersBelongingToRole(claimAdvisor);
			if (serviceManagers != null && !serviceManagers.isEmpty())
				filterAvailableUsers(allServiceManagers, claim
						.getBusinessUnitInfo().getName(), claimAdvisor);
			serviceManagers.addAll(allServiceManagers);
		}
		return serviceManagers;
	}

	@SuppressWarnings("unchecked")
	protected Collection<String> findUsersBelongingToRole(String role,
			String businessUnitName) {
		Set<User> serviceManagers = this.orgService
				.findUsersBelongingToRole(role);
		if (serviceManagers != null && !serviceManagers.isEmpty())
			filterAvailableUsers(serviceManagers, businessUnitName, role);
		return CollectionUtils.collect(serviceManagers, new Transformer() {
			public Object transform(Object input) {
				return ((User) input).getCompleteNameAndLogin();
			}
		});
	}

	public static class ServiceProcedureTreeFilter implements
			AssemblyTreeJSONifier.Filter {
		public boolean preTestNode(Assembly assembly) {
			if ((assembly.getComposedOfAssemblies() != null)
					&& (assembly.getComposedOfAssemblies().size() > 0)) {
				return true;
			}
			return (assembly.getActions() != null)
					&& (assembly.getActions().size() > 0);
		}

		public boolean preTestNode(ActionNode actionNode) {
			return true;
		}

		 public boolean postTestNode(JSONObject node, Assembly assembly) throws JSONException {
	            if ((assembly.getComposedOfAssemblies() != null) && (assembly.getComposedOfAssemblies().size() > 0)) {
	                return true;
	            }
	            return node.getJSONArray(SP_CHILDREN).length() > 0;
	        }


		public boolean postTestNode(JSONObject node, ActionNode actionNode) {
			return true;
		}

		public boolean includeFaultCodeInfo() {
			return false;
		}
	}

	protected void setClaimReasons(Claim claim, String transition) {

		if (claim.getAcceptanceReason() != null
				&& claim.getAcceptanceReason().getCode() == null) {
			claim.setAcceptanceReason(null);
		}

		if (claim.getAcceptanceReasonForCp() != null
				&& claim.getAcceptanceReasonForCp().getCode() == null) {
			claim.setAcceptanceReasonForCp(null);
		}
		if (null == claim.getRejectionReasons() || claim.getRejectionReasons().size()==0) {
			claim.setRejectionReasons(null);
		}

		if (claim.getAccountabilityCode() != null
				&& claim.getAccountabilityCode().getCode() == null) {
			claim.setAccountabilityCode(null);
		}

		if (transition.equalsIgnoreCase("Deny")) {
			claim.setAcceptanceReason(null);
		}
	}

	public String getFaultCodeJSON() throws JSONException {
		return writeJsonResponse(getJsonFaultCodeTree());
	}

	public String getJsonFaultCodeTree() throws JSONException {
		AssemblyTreeJSONifier.Filter filter = new AssemblyTreeJSONifier.Filter() {

			public boolean preTestNode(Assembly assembly) {
				if ((assembly.getComposedOfAssemblies() != null)
						&& (assembly.getComposedOfAssemblies().size() > 0)) {
					return true;
				}

				return assembly.isFaultCode();
			}

			public boolean preTestNode(ActionNode actionNode) {
				return false;
			}

			  public boolean postTestNode(JSONObject node, Assembly assembly) throws JSONException {
	                return assembly.getComposedOfAssemblies().size() > 0 || assembly.isFaultCode();
	            }

			public boolean postTestNode(JSONObject node, ActionNode actionNode) {
				return false;
			}

			public boolean includeFaultCodeInfo() {
				return true;
			}
		};
			 if(getAssemblyId() == null){
		            return this.asmTreeJSONifier.getSerializedJSONString(getFailureStructure(), filter, null);
		        }else{
		            return this.asmTreeJSONifier.getSerializedJSONString(failureStructureService.findAssemblyById(getAssemblyId()), filter, null);
		        }
	}

	public void validateAlarmCodes(Claim theClaim) {
		List<AlarmCode> alarmCodeList = theClaim.getAlarmCodes();
		if (alarmCodeList != null && !alarmCodeList.isEmpty()) {
			for (AlarmCode alarmCode : alarmCodeList) {
				if (alarmCode.getCode() == null) {
					addActionError("alarmcode.invalid");
				}
			}
		}

	}

	public String getServiceProcedureJSON() throws JSONException {
		return writeJsonResponse(getJsonServiceProcedureTree());
	}

	public String getJsonServiceProcedureTree() throws JSONException { if(getAssemblyId() == null){
        return this.asmTreeJSONifier.getSerializedJSONString(
                getFailureStructure(), new ServiceProcedureTreeFilter(), getClaimDetail());            
    }else{
        return this.asmTreeJSONifier.getSerializedJSONString(
                failureStructureService.findAssemblyById(getAssemblyId()),
                new ServiceProcedureTreeFilter(), getClaimDetail());            
    }}

	public void populateBusinessUnitConfigParameters() {
		if (getClaimDetail() != null) {
			SelectedBusinessUnitsHolder
					.setSelectedBusinessUnit(getClaimDetail()
							.getBusinessUnitInfo().getName());
			partsReplacedInstalledSectionVisible = configParamService
					.getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE
							.getName());
			buPartReplaceableByNonBUPart = configParamService
					.getBooleanValue(ConfigName.BUPART_REPLACEABLEBY_NONBUPART
							.getName());
			alarmCodesSectionVisible = configParamService
					.getBooleanValue(ConfigName.ALARM_CODE_SECTION_VISIBLE
							.getName());
			showPartSerialNumber = configParamService
					.getBooleanValue(ConfigName.SHOW_PART_SN_ON_INSTALLED_REMOVED_SECTION
							.getName());
			displayCPFlagOnClaimPgOne = configParamService
					.getBooleanValue(ConfigName.COMMERCIAL_POLICY_CLAIM_PAGE
							.getName());
			isAllAuditsHistoryShownToDealer = configParamService
					.getBooleanValue(ConfigName.ALL_AUDITS_HISTORY_SHOWN_TO_DEALER
							.getName());
		}
	}

	protected List<SummaryTableColumn> getHeader() {
		return null;
	}

	protected PageResult<?> getBody() {
		return null;
	}

	  protected FailureStructure getFailureStructure() {
	        if (causalPart == null) {
	            causalPart = getClaimDetail().getServiceInformation().getCausalPart();
	        }
	        if (causalPart != null || ClaimType.CAMPAIGN.getType().equals(getClaimDetail().getType().getType())) {
	            return this.failureStructureService.getFailureStructure(getClaimDetail(), causalPart);
	        } else {
	            return null;
	        }
	    }

	public boolean isEligibleForAdditionalLaborDetails(Claim claim) {
		boolean isAdditionalLaborDetailsToDisplay = false;
		List<ServiceProvider> serviceProviders = new ArrayList<ServiceProvider>();
		AdditionalLaborEligibility additionalLaborEligibility = additionalLaborService
				.findAddditionalLabourEligibility();
		if (additionalLaborEligibility != null) {
			serviceProviders = additionalLaborEligibility.getServiceProviders();
		}
		if (claim.getServiceInformation() != null
				&& claim.getServiceInformation().getServiceDetail() != null
				&& claim.getServiceInformation().getServiceDetail()
						.getLaborPerformed() != null
				&& claim.getServiceInformation().getServiceDetail()
						.getLaborPerformed().size() > 0) {
			for (LaborDetail labor : claim.getServiceInformation()
					.getServiceDetail().getLaborPerformed()) {
				// with AdditionalLaborHours
				if ((labor.getAdditionalLaborHours() != null && labor
						.getAdditionalLaborHours().signum() == 1)
						|| (serviceProviders.isEmpty())
						|| (!serviceProviders.isEmpty() && serviceProviders
								.contains(claim.getForDealer()))) {
					isAdditionalLaborDetailsToDisplay = true;
					break;
				}
			}
		}
		// filing new claim (no need to check AdditionalLaborHours)
		else if ((serviceProviders.isEmpty())
				|| (!serviceProviders.isEmpty() && serviceProviders
						.contains(claim.getForDealer()))) {
			isAdditionalLaborDetailsToDisplay = true;
		}
		return isAdditionalLaborDetailsToDisplay;

	}

	/**
	 * This part of code checks if there are any Pending Failure Reports on the
	 * claim The pending logic is abstracted out in ClaimServiceImpl class. This
	 * validation has to be called from ClaimsAction and ReopenClaimsAction
	 * class
	 */
	public boolean isFailureReportsPendingOnClaim(Claim claim) {
		if (ClaimState.DRAFT.getState().equals(claim.getState().getState())
				|| ClaimState.FORWARDED.getState().equals(
						claim.getState().getState())) {
			if (isFailureReportsPending(claim)) {
				claim.setFailureReportPending(true);
				return true;
			}
		}
		return false;
	}

	public boolean isFailureReportsPending(Claim claim) {
		if (claimService.isAnyFailureReportPendingOnClaim(claim)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public void setConfiguredCostCategories(Claim claim) {
		List<Object> costCategoryObjects = getConfigParamService()
				.getListofObjects(
						ConfigName.CONFIGURED_COST_CATEGORIES.getName());
		for (Object object : costCategoryObjects) {
			CostCategory costCategory = new HibernateCast<CostCategory>()
					.cast(object);
			this.configuredCostCategories.add(costCategory);
		}

		// Set all cost categories in a map
		List<CostCategory> allCostCategories = costCategoryRepository
				.findAllCostCategories();
		Map<String, CostCategory> allCostCategoriesMap = new HashMap<String, CostCategory>();
		for (CostCategory costCategory : allCostCategories) {
			allCostCategoriesMap.put(costCategory.getCode(), costCategory);
		}
		if (claim.getCommercialPolicy() != null && claim.getCommercialPolicy()) {
			setConfiguredCostCategoriesOnClaim(claim);
			return;
		}
		if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)) {
			PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
			if (!partsClaim.getPartInstalled()
					|| (partsClaim.getPartInstalled() && (claim
							.getCompetitorModelBrand() != null
							&& !claim.getCompetitorModelBrand().isEmpty()
							&& !claim.getCompetitorModelDescription().isEmpty() && !claim
							.getCompetitorModelTruckSerialnumber().isEmpty()))) {
				setConfiguredCostCategoriesOnClaim(claim);
				return;
			}
		}
		ItemGroup productToConsider = null;
		if (claim.getItemReference().isSerialized()
				&& claim.getItemReference().getReferredInventoryItem() != null) {
			productToConsider = claim.getItemReference()
					.getReferredInventoryItem().getOfType().getProduct();
		} else {
			productToConsider = getProduct(claim.getItemReference().getModel());
		}
		List<Long> costCategoryIds = new ArrayList<Long>(
				configuredCostCategories.size());
		for (CostCategory costCat : this.configuredCostCategories) {
			costCategoryIds.add(costCat.getId());
		}
		this.configuredCostCategories = costCategoryRepository
				.findCostCategoryApplicableForProduct(productToConsider,
						costCategoryIds);

		if (ClaimState.DRAFT.equals(claim.getState())) {
			setConfiguredCostCategoriesOnClaim(claim);
		} else {
			Map<String, CostCategory> eligibleCostCategoriesMap = new HashMap<String, CostCategory>();
			for (String costCategoryCode : claim.getIncludedCostCategories()
					.keySet()) {
				if (Boolean.TRUE == claim.getIncludedCostCategories().get(
						costCategoryCode)) {
					eligibleCostCategoriesMap.put(costCategoryCode,
							allCostCategoriesMap.get(costCategoryCode));
				}
			}
			for (CostCategory costCategory : configuredCostCategories) {
				eligibleCostCategoriesMap.put(costCategory.getCode(),
						costCategory);
			}
			configuredCostCategories.clear();
			configuredCostCategories.addAll(eligibleCostCategoriesMap.values());
			setConfiguredCostCategoriesOnClaim(claim);
		}
	}

	public ItemGroup getProduct(ItemGroup itemGroup) {
		if (itemGroup == null)
			return null;
		if (itemGroup.getIsPartOf() != null
				&& !ItemGroup.PRODUCT.equals(itemGroup.getIsPartOf()
						.getItemGroupType())) {
			return getProduct(itemGroup.getIsPartOf());
		} else {
			return itemGroup.getIsPartOf();
		}
	}

	public void filterAvailableUsers(Set<User> serviceManagers,
			String businessUnitName, String role) {
		for (Iterator<User> iterator = serviceManagers.iterator(); iterator
				.hasNext();) {
			User serviceManager = (User) iterator.next();
			if (!serviceManager.isAvailableForBU(businessUnitName, role))
				iterator.remove();
		}
	}

	public String getUserWithLeastLoad(List<String> eligibleProcessors) {
		List<String> sortedUsers = loadBalancingService
				.findUsersSortedByLoad(eligibleProcessors);

		// The first condition usually occurs only in the initial stages or when
		// a new user is added.
		if (sortedUsers == null
				|| sortedUsers.size() < eligibleProcessors.size()) {
			return findAnUnassignedUser(sortedUsers, eligibleProcessors);
		} else {
			return sortedUsers.get(0); // The first one will be the least
			// loaded.
		}
	}

	private String findAnUnassignedUser(List<String> usersWithTasks, List<String> eligibleUsers) {
        if (usersWithTasks == null)
            return eligibleUsers.get(0);

        for (String eligibleUser : eligibleUsers) {
            if (!usersWithTasks.contains(eligibleUser)) {
                return eligibleUser;
            }
        }
        return null; // this case shouldn't arise !
    }


    public void setConfiguredCostCategoriesOnClaim(Claim claim) {
        if (isConfigured(CostCategory.TRAVEL_DISTANCE_COST_CATEGORY_CODE)) {
            claim.setTravelDisConfig(true);
        }else{
            claim.setTravelDisConfig(false);
        }
        if (isConfigured(CostCategory.TRAVEL_TRIP_COST_CATEGORY_CODE)) {
            claim.setTravelTripConfig(true);
        }else{
            claim.setTravelTripConfig(false);
        }
        if (isConfigured(CostCategory.TRAVEL_HOURS_COST_CATEGORY_CODE)) {
            claim.setTravelHrsConfig(true);
        }else{
            claim.setTravelHrsConfig(false);
        }
        if (isConfigured(CostCategory.FREIGHT_DUTY_CATEGORY_CODE)) {
            claim.setItemDutyConfig(true);
        }else{
            claim.setItemDutyConfig(false);
        }
        
        if (isConfigured(CostCategory.HANDLING_FEE_CODE)) {
            claim.setHandlingFeeConfig(true);
        }else{
        	 claim.setHandlingFeeConfig(false);
        }
        
        if (isConfigured(CostCategory.MEALS_HOURS_COST_CATEGORY_CODE)) {
            claim.setMealsConfig(true);
        }else{
            claim.setMealsConfig(false);
        }
        if (isConfigured(CostCategory.PARKING_COST_CATEGORY_CODE)) {
            claim.setParkingConfig(true);
        }else{
            claim.setParkingConfig(false);
        }
        if (isConfigured(CostCategory.OEM_PARTS_COST_CATEGORY_CODE)) {
            claim.setOemConfig(true);
        }else{
            claim.setOemConfig(false);
        }
        if (isConfigured(CostCategory.NON_OEM_PARTS_COST_CATEGORY_CODE)) {
            claim.setNonOemConfig(true);
        }else{
            claim.setNonOemConfig(false);
        }
        if (isConfigured(CostCategory.LABOR_COST_CATEGORY_CODE)) {
            claim.setLaborConfig(true);
        }else{
            claim.setLaborConfig(false);
        }
        if (isConfigured(CostCategory.MISC_PARTS_COST_CATEGORY_CODE)) {
            claim.setMiscPartsConfig(true);
        }else{
            claim.setMiscPartsConfig(false);
        }
        if (isConfigured(CostCategory.PER_DIEM_COST_CATEGORY_CODE)) {
            claim.setPerDiemConfig(true);
        }else{
            claim.setPerDiemConfig(false);
        }
        if (isConfigured(CostCategory.RENTAL_CHARGES_COST_CATEGORY_CODE)) {
            claim.setRentalChargesConfig(true);
        }else{
            claim.setRentalChargesConfig(false);
        }
        if (isConfigured(CostCategory.ADDITIONAL_TRAVEL_HOURS_COST_CATEGORY_CODE)) {
            claim.setAdditionalTravelHoursConfig(true);
        }else{
            claim.setAdditionalTravelHoursConfig(false);
        }
        if (isConfigured(CostCategory.LOCAL_PURCHASE_COST_CATEGORY_CODE)) {
            claim.setLocalPurchaseConfig(true);
        }else{
            claim.setLocalPurchaseConfig(false);
        }
        if (isConfigured(CostCategory.TOLLS_COST_CATEGORY_CODE)) {
            claim.setTollsConfig(true);
        }else{
            claim.setTollsConfig(false);
        }
        if (isConfigured(CostCategory.OTHER_FREIGHT_DUTY_COST_CATEGORY_CODE)) {
            claim.setOtherFreightDutyConfig(true);
        }else{
            claim.setOtherFreightDutyConfig(false);
        }
        if (isConfigured(CostCategory.OTHERS_CATEGORY_CODE)) {
            claim.setOthersConfig(true);
        }else{
            claim.setOthersConfig(false);
        }
        if (isConfigured(CostCategory.TRANSPORTATION_COST_CATEGORY_CODE)){
			claim.setTransportation(true);
		} else {
			claim.setTransportation(false);
		}
    }

	public boolean isConfigured(String costCategoryCode) {
		CostCategory category = new CostCategory(costCategoryCode);
		return configuredCostCategories.contains(category);
	}	

	public String getOEMPartCrossRefForDisplay(Item item, Item oemDealerItem,
			boolean isNumber, Organization dealership) {
		Organization loggedInUserOrganization = getLoggedInUser()
				.getBelongsToOrganization();
		return catalogService.findOEMPartCrossRefForDisplay(item,
				oemDealerItem, isNumber, dealership, loggedInUserOrganization,
				Dealership.class);
	}

	public String getOEMPartCrossRefForDisplay(Item item, Item oemDealerItem,
			boolean isNumber, Organization dealership, String brand) {
		if (brand != null && !brand.isEmpty() && isNumber) {
			return item.getBrandItemNumber(brand);
		}
		Organization loggedInUserOrganization = getLoggedInUser()
				.getBelongsToOrganization();
		return catalogService.findOEMPartCrossRefForDisplay(item,
				oemDealerItem, isNumber, dealership, loggedInUserOrganization,
				Dealership.class);
	}

	public void setCausalPart(Item causalPart) {
		this.causalPart = causalPart;
	}

	public boolean isBuPartReplaceableByNonBUPart() {
		return buPartReplaceableByNonBUPart;
	}

	public void setBuPartReplaceableByNonBUPart(
			boolean buPartReplaceableByNonBUPart) {
		this.buPartReplaceableByNonBUPart = buPartReplaceableByNonBUPart;
	}

	public boolean isPartsReplacedInstalledSectionVisible() {
		return partsReplacedInstalledSectionVisible;
	}

	public void setPartsReplacedInstalledSectionVisible(
			boolean partsReplacedInstalledSectionVisible) {
		this.partsReplacedInstalledSectionVisible = partsReplacedInstalledSectionVisible;
	}

	public boolean isAlarmCodesSectionVisible() {
		return alarmCodesSectionVisible
				|| (getClaimDetail().getAlarmCodes() != null && getClaimDetail()
						.getAlarmCodes().size() > 0);
	}

	public void setAlarmCodesSectionVisible(boolean alarmCodesSectionVisible) {
		this.alarmCodesSectionVisible = alarmCodesSectionVisible;
	}

	public List<CostCategory> getConfiguredCostCategories() {
		return configuredCostCategories;
	}

	public LaborDetail getRoundUpLaborDetail() {
		return roundUpLaborDetail;
	}

	public void setRoundUpLaborDetail(LaborDetail roundUpLaborDetail) {
		this.roundUpLaborDetail = roundUpLaborDetail;
	}

	public void setAssemblyTreeJsonifier(AssemblyTreeJSONifier jsonifier) {
		this.asmTreeJSONifier = jsonifier;
	}

	public void setAssignmentRuleExecutor(
			AssignmentRuleExecutor assignmentRuleExecutor) {
		this.assignmentRuleExecutor = assignmentRuleExecutor;
	}

	public AdditionalLaborEligibilityService getAdditionalLaborService() {
		return additionalLaborService;
	}

	public void setAdditionalLaborService(
			AdditionalLaborEligibilityService additionalLaborService) {
		this.additionalLaborService = additionalLaborService;
	}

	public void setLoaService(LimitOfAuthoritySchemeService loaService) {
		this.loaService = loaService;
	}

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public void setCostCategoryRepository(
			CostCategoryRepository costCategoryRepository) {
		this.costCategoryRepository = costCategoryRepository;
	}

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public void setLoadBalancingService(
			LoadBalancingService loadBalancingService) {
		this.loadBalancingService = loadBalancingService;
	}

	public List<LimitOfAuthorityScheme> findLOASchemesForUser() {
		return loaService.findLOASchemesForUser(new SecurityHelper()
				.getLoggedInUser().getName());
	}

	public boolean isLoaSchemeApplicable() {
		return loaSchemeApplicable;
	}

	public void setLoaSchemeApplicable(boolean loaSchemeApplicable) {
		this.loaSchemeApplicable = loaSchemeApplicable;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public boolean getDisplayCPFlagOnClaimPgOne() {
		return displayCPFlagOnClaimPgOne;
	}

	public int getApprovedClaimedItems(Claim claim) {
		int approvedClaimedItems = 0;
		List<ClaimedItem> claimedItems = claim.getClaimedItems();
		for (ClaimedItem claimedItem : claimedItems) {
			if (claimedItem.isProcessorApproved()) {
				approvedClaimedItems++;
			}
		}
		return approvedClaimedItems;
	}

	public void setQtyForUIView(Claim claim) {
		int approvedClaimedItems = getApprovedClaimedItems(claim);
		if (!isPartsReplacedInstalledSectionVisible()) {
			List<OEMPartReplaced> oemPartReplaced = claim
					.getServiceInformation().getServiceDetail()
					.getOemPartsReplaced();
			List<NonOEMPartReplaced> nonOemPartReplaced = claim
					.getServiceInformation().getServiceDetail()
					.getNonOEMPartsReplaced();
			for (OEMPartReplaced oemPart : oemPartReplaced) {
				if (oemPart.getInventoryLevel().booleanValue()) {
					if (approvedClaimedItems != 0) {
						oemPart.setNumberOfUnits(oemPart.getNumberOfUnits()
								/ approvedClaimedItems);
					} else {
						oemPart.setNumberOfUnits(0);
					}
				}
			}
			for (NonOEMPartReplaced nonOemPart : nonOemPartReplaced) {
				if (nonOemPart.getInventoryLevel().booleanValue()) {
					if (approvedClaimedItems != 0) {
						nonOemPart.setNumberOfUnits(nonOemPart
								.getNumberOfUnits() / approvedClaimedItems);
					} else {
						nonOemPart.setNumberOfUnits(0);
					}
				}
			}
		} else {
			List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled = claim
					.getServiceInformation().getServiceDetail()
					.getHussmanPartsReplacedInstalled();
			if (hussmanPartsReplacedInstalled != null) {
				for (HussmanPartsReplacedInstalled hussmanPartReplacedInstalled : hussmanPartsReplacedInstalled) {
					if (hussmanPartReplacedInstalled == null) {
						continue; // Null check to avoid the sporadic NPE due to
									// indexing problems
					}
					boolean inventoryClaimLevel;
					if (hussmanPartReplacedInstalled.getInventoryLevel() != null) {
						inventoryClaimLevel = hussmanPartReplacedInstalled
								.getInventoryLevel();
					} else {
						inventoryClaimLevel = false;
					}
					List<OEMPartReplaced> oemPartReplaced = hussmanPartReplacedInstalled
							.getReplacedParts();
					List<InstalledParts> oemPartInstalled = hussmanPartReplacedInstalled
							.getHussmanInstalledParts();
					List<InstalledParts> nonOemPartInstalled = hussmanPartReplacedInstalled
							.getNonHussmanInstalledParts();
					if (inventoryClaimLevel) {
						for (OEMPartReplaced oemPart : oemPartReplaced) {
							if (oemPart == null) {
								continue; // Null check to avoid the sporadic
											// NPE due to indexing problems
							}
							if (approvedClaimedItems != 0) {
								oemPart.setNumberOfUnits(oemPart
										.getNumberOfUnits()
										/ approvedClaimedItems);
							} else {
								oemPart.setNumberOfUnits(0);
							}
						}
						for (InstalledParts oemPart : oemPartInstalled) {
							if (oemPart == null) {
								continue; // Null check to avoid the sporadic
											// NPE due to indexing problems
							}
							if (approvedClaimedItems != 0) {
								oemPart.setNumberOfUnits(oemPart
										.getNumberOfUnits()
										/ approvedClaimedItems);
							} else {
								oemPart.setNumberOfUnits(0);
							}
						}
						for (InstalledParts nonOemPart : nonOemPartInstalled) {
							if (nonOemPart == null) {
								continue; // Null check to avoid the sporadic
											// NPE due to indexing problems
							}
							if (approvedClaimedItems != 0) {
								nonOemPart.setNumberOfUnits(nonOemPart
										.getNumberOfUnits()
										/ approvedClaimedItems);
							} else {
								nonOemPart.setNumberOfUnits(0);
							}
						}
					}
				}
			}
			List<NonOEMPartReplaced> nonOEMPartsReplaced = claim
					.getServiceInformation().getServiceDetail()
					.getNonOEMPartsReplaced();
			if (nonOEMPartsReplaced != null) {
				for (NonOEMPartReplaced nonOemPart : nonOEMPartsReplaced) {
					if (nonOemPart == null || !nonOemPart.getInventoryLevel()) {
						continue; // Null check to avoid the sporadic NPE due to
									// indexing problems
					}
					if (approvedClaimedItems != 0) {
						nonOemPart.setNumberOfUnits(nonOemPart
								.getNumberOfUnits() / approvedClaimedItems);
					} else {
						nonOemPart.setNumberOfUnits(0);
					}
				}
			}
		}
	}

	protected String getCurrentClaimAssignee(User claimAssignee) {
		if (claimAssignee == null) {
			return "";
		}
		StringBuilder assignee = new StringBuilder();
		String firstName = claimAssignee.getFirstName();
		String lastName = claimAssignee.getLastName();
		String login = claimAssignee.getName();
		assignee.append(firstName == null ? "" : firstName);
		assignee.append(" ");
		assignee.append(lastName == null ? "" : lastName);
		assignee.append(" (");
		assignee.append(login);
		assignee.append(")");
		return assignee.toString();
	}

	public void getAttributesForClaim(Claim claimDetail) throws JSONException {
		Long srmId = claimDetail.isServiceManagerRequest() ? claimDetail
				.getReasonForServiceManagerRequest().getId() : 0L;
		getClaimService().prepareAttributesForClaim(claimDetail, srmId);
		this.claimSpecificAttributes = claimDetail
				.getClaimAdditionalAttributes();

	}

	public List<ClaimAttributes> getClaimSpecificAttributes() {
		return claimSpecificAttributes;
	}

	public void setClaimSpecificAttributes(
			List<ClaimAttributes> claimSpecificAttributes) {
		this.claimSpecificAttributes = claimSpecificAttributes;
	}

	public boolean isAllAuditsHistoryShownToDealer() {
		return isAllAuditsHistoryShownToDealer;
	}

	public void setAllAuditsHistoryShownToDealer(
			boolean isAllAuditsHistoryShownToDealer) {
		this.isAllAuditsHistoryShownToDealer = isAllAuditsHistoryShownToDealer;
	}

	public boolean checkIfReturnProcessAvailable() {
		List<OEMPartReplaced> oemPartsReplaced = getClaimDetail()
				.getServiceInformation().getServiceDetail().getReplacedParts();
		if (oemPartsReplaced.size() > 0) {
			for (OEMPartReplaced part : oemPartsReplaced) {
				for (PartReturn pr : part.getPartReturns()) {
					if (returnStatusMap().contains(pr.getStatus())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public List<PartReturnStatus> returnStatusMap() {
		List<PartReturnStatus> listForStatus = new ArrayList<PartReturnStatus>();
		listForStatus.add(PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER);
		listForStatus.add(PartReturnStatus.DEALER_PARTIALLY_REQUESTED);
		listForStatus.add(PartReturnStatus.NMHG_TO_DEALER_SHIPMENT_GENERATED);
		listForStatus
				.add(PartReturnStatus.NMHG_TO_DEALER_PARTIALLY_SHIPMENT_GENERATED);

		return listForStatus;

	}

	public boolean isClaimProcessedAsOutOfWarranty(Claim claim) {
		return Constants.VALID_ITEM_OUT_OF_WARRANTY.equals(claim
				.getClaimProcessedAs());
	}
    public int getDefaultDueDays(){
        return configParamService.getLongValue(ConfigName.DEFAULT_DUE_DAYS_FOR_PART_RETURN.getName()).intValue();
    }

	public boolean useDefaultDueDays() {
		return configParamService
				.getBooleanValue(ConfigName.ENABLE_DEFAULT_DUE_DAYS_FOR_PART_RETURN
						.getName());
	}

	public boolean displayWarningIfPartPricesDifferent() {
		return configParamService
				.getBooleanValue(ConfigName.FLAG_WARNING_IF_PART_PRICE_DIFFERENT
						.getName());
	}

	public void displayServicingLocationWithBrand(Claim claimDetail) {
		// TODO Auto-generated method stub
		List<OrganizationAddress> orgAddresses = orgService
				.getAddressesForOrganization(claimDetail.getForDealer());
		Dealership dealer = new HibernateCast<Dealership>().cast(claimDetail
				.getForDealer());
		String brand = "";
		if (orgAddresses.contains(claimDetail.getServicingLocation())) {
			brand = orgService.findMarketingGroupCodeBrandByDealership(dealer);
		} else {
			if (null != dealer.getDualDealer()) {
				List<OrganizationAddress> orgAddressOfDualDealer = new ArrayList<OrganizationAddress>();
				orgAddressOfDualDealer = orgService
						.getAddressesForOrganization(dealer.getDualDealer());
				if (orgAddressOfDualDealer.contains(claimDetail
						.getServicingLocation())) {
					brand = orgService
							.findMarketingGroupCodeBrandByDealership(dealer
									.getDualDealer());
				}
			}
		}
		if (null != claimDetail.getServicingLocation()) {
			claimDetail.getServicingLocation().setLocationWithBrand(
					brand
							+ " - "
							+ claimDetail.getServicingLocation()
									.getShipToCodeAppended());
		}
	}

	public boolean enableComponentDateCode() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ENABLE_COMPONENT_DATE_CODE.getName());
	}
	
	// using previousState since the audits other than activeClaimAudit store the claim state in the previousState field
	public boolean isClaimAcceptedEarlier(Claim claim) {
		for (ClaimAudit audit : claim.getClaimAudits()) {
			if (audit.getPreviousState().getState()
					.equals(ClaimState.ACCEPTED_AND_CLOSED.getState())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean amerClaimAcceptedEarlier(Claim claim) {
		return claim.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGAMER)
				&& isClaimAcceptedEarlier(claim);
	}
	
	/**
	 * Returns a list containing {@link InventoryClass}es for which current dealership
	 * can file a 30 day NCR Claim. If current logged-in user does not belong to a dealership
	 * then empty collection is returned.
	 */
	public List<InventoryClass> getAllowed30DayNcrClasses() {
    	
    	ServiceProvider mLoggedInDealer = getLoggedInUsersDealership();
    	
    	ArrayList<InventoryClass> mCurrentAllowed30DayNcrClasses = new ArrayList<InventoryClass>();
    	
    	if (mLoggedInDealer != null) {
	    	List<InvClassDealerMapping> mDealerEligibleClassMappings = 
	    			invClassDlrMappingService.findInvClassDealerMappings(mLoggedInDealer);
	        
	        for (InvClassDealerMapping icdm : mDealerEligibleClassMappings) {
	        	mCurrentAllowed30DayNcrClasses.add(icdm.getInventoryClass());
	        }
	        
	        Collections.sort(mCurrentAllowed30DayNcrClasses);
    	}
        
        return mCurrentAllowed30DayNcrClasses;
    }
    
    public void setInvClassDlrMappingService(InvClassDealerMappingService invClassDlrMappingService) {
		this.invClassDlrMappingService = invClassDlrMappingService;
	}
	
	public InvClassDealerMappingService getInvClassDlrMappingService() {
		return invClassDlrMappingService;
	}

	public Long getAssemblyId() {
		return assemblyId;
	}

	public void setAssemblyId(Long assemblyId) {
		this.assemblyId = assemblyId;
	}
	
	public void validateDocumentTypeForAttachment(Claim claim) {
		List<Document> attachments = claim.getAttachments();
		attachments.removeAll(Collections.singleton(null));
		if (attachments != null) {
			for (Document doc : attachments) {
				if (doc.getDocumentType() == null) {
					addActionError("error.selectDocumentType");
				}
			}
		}
	}
}