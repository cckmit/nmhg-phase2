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
package tavant.twms.web.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.apache.struts2.util.ServletContextAware;
import org.hibernate.proxy.HibernateProxy;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import tavant.twms.domain.WarrantyTask.WarrantyFolderView;
import tavant.twms.domain.WarrantyTask.WarrantyTaskInstanceService;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.campaign.CampaignAssignmentService;
import tavant.twms.domain.campaign.FieldModUpdateStatus;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.domain.inventory.InvTransationType;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.login.LoginHistory;
import tavant.twms.domain.login.LoginHistoryService;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.RoleCategory;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.policy.WarrantyCoverageRequestService;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.query.SavedQuery;
import tavant.twms.domain.query.SavedQueryService;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.LoginUtil;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.jbpm.nodes.FormTaskNode;
import tavant.twms.security.model.OrgAwareUserDetails;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.i18n.I18nJBPMSpecificNames;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListService;

import com.domainlanguage.time.CalendarDate;

@SuppressWarnings("serial")
public class HomeAction extends TwmsActionSupport implements
		ServletContextAware, I18nJBPMSpecificNames {

	public static final String CSS_THEME_SESSION_KEY = "session.cssTheme";

	private List<WarrantyFolderView> warrantyFoldersForDR = new ArrayList<WarrantyFolderView>();

	private List<WarrantyFolderView> warrantyFoldersForETR = new ArrayList<WarrantyFolderView>();

	private WarrantyTaskInstanceService warrantyTaskInstanceService;

	@SuppressWarnings("unchecked")
	private String selectedBusinessUnit;

	private String warrantyAdminSelectedBusinessUnit;

	private Long selectedOrganization;

	private Boolean allowSessionTimeOut;

	private boolean isEnableDealersToViewPR;

    private boolean showWpraInboxes;

    private String token;

	@SuppressWarnings("unchecked")
	public static abstract class RefreshManagerResponder {
		public String getResponse() {
			Map tasks = getMap();
			JSONArray root = new JSONArray();
			for (Object key : tasks.keySet()) {
				root.put(new JSONArray().put(getFolderName(key)).put(
						getFolderCount(key, tasks)));
			}
			return root.toString();
		}

		public String getFolderName(Object keyInstance) {
			return keyInstance.toString();
		}

		public String getFolderCount(Object keyInstance, Map map) {
			return map.get(keyInstance).toString();
		}

		public abstract Map getMap();
	}

	private static final Logger logger = Logger.getLogger(HomeAction.class);
	private WorkListService workListService;
	private SortedSet<String> ruleContexts = new TreeSet<String>();

	private List<SavedQuery> savedQueries = new ArrayList<SavedQuery>();

	private List<SavedQuery> savedQueriesForInventory = new ArrayList<SavedQuery>();

	private List<SavedQuery> savedQueriesForPartInventory = new ArrayList<SavedQuery>();

	private List<SavedQuery> savedQueriesForPartReturn = new ArrayList<SavedQuery>();

	private List<SavedQuery> savedQueriesForRecoveryClaims = new ArrayList<SavedQuery>();

	// each item in this list is a String array of size 2 - the first
	// item is a unique id for this claim folder, the second is the name
	// of the claim folder.
	private List<String[]> claimFolders;

	// each item here is a list of 6 items
	private ArrayList<Object[]> partsReturnFolders;

	private List<String[]> supplierRecoveryFolders;

	private List<String[]> supplierRecoveryPartReturnFolders;

	public static final String ACEGI_SECURITY_CONTEXT_KEY = "ACEGI_SECURITY_CONTEXT";

	private String jsonString;

	private String businessUnit;

	private int pendingCampaignsCount;

	private int updatingCampaignsCount;

	private int deniedCampaignsCount;

	private int pendingReviewCampaignsCount;

	private int requestForExtensionCount;

	private long extensionRequestsCount;

	private WarrantyCoverageRequestService warrantyCoverageRequestService;

	PredicateAdministrationService predicateAdministrationService;
	private ServletContext servletContext;

	private CampaignAssignmentService campaignAssignmentService;

	private List<SavedQuery> savedQueriesForItem = new ArrayList<SavedQuery>();

	private SavedQueryService savedQueryService;



	private InventoryService inventoryService;

	private List<SavedQuery> preDefinedClaimSavedQueries = new ArrayList<SavedQuery>();

	private List<SavedQuery> preDefinedInventoryStockSavedQueries = new ArrayList<SavedQuery>();

	private List<SavedQuery> preDefinedInventoryRetailSavedQueries = new ArrayList<SavedQuery>();

	private List<SavedQuery> preDefinedCampaignSavedQueries = new ArrayList<SavedQuery>();

	private List<SavedQuery> preDefinedPartReturnsSavedQueries = new ArrayList<SavedQuery>();

	private List<SavedQuery> preDefinedPartRecoverySavedQueries = new ArrayList<SavedQuery>();

	private List<SavedQuery> preDefinedRecClaimSavedQueries = new ArrayList<SavedQuery>();


    private String transactionType;

    private Long stockCount;

    private Long vintageStockCount;

    private String userMessage;

	private LoginHistoryService loginHistoryService;

	private Long recoveryPendingClaimsCount;

	private ClaimService claimService;

	@Override
	public String execute() {
		if (CollectionUtils.isEmpty(getLoggedInUser().getRoles())) {
			userMessage = getText("message.invalidPermissions", new String[] { getLoggedInUser().getName() });
			return INPUT;
		}
		setOrganizationOnLogin();
		fetchBusinessUnitInfo();
		setBusinessUnitOnLogin();
        populateRecoveryPendingClaimsCount();
		fetchClaimFolders();
		fetchPartsReturnFolders();

		if(isSupplierRecoveryUser()){
			fetchSupplierRecoveryFolders();
		}

		fetchRuleContexts();
		fetchSavedQueriesForCampaignSearch(); // All other saved queries get
												// fetched through ajax calls

		fetchFoldersForTransactionType(InvTransationType.DR.name(), warrantyFoldersForDR);
		fetchFoldersForTransactionType(InvTransationType.ETR.name(), warrantyFoldersForETR);
		// updateBusinessUnit();

		   populateRequestForExtensionCount();
		if (getLoggedInUsersDealership() != null) {
			populatePendingCampaignsCount();
			populateUpdatingCampaignsCount();
			populateDeniedCampaignsCount();


		}
		if(getLoggedInUser().hasRole("internalUserAdmin"))
		{
			populatePendingReviewCampaignsCount();
		}

		if (getLoggedInUser().hasRole("reducedCoverageRequestsApprover")) {
			populateExtensionForCoverageRequestsCount();
		}

		if (!isAllowSessionTimeOut()) {
			request.getSession().setMaxInactiveInterval(-1); // never timeout
		}
        if(islogHistoryEnabled()){
        	Date date =new Date(request.getSession().getCreationTime());
        	OrgAwareUserDetails userDetail = (OrgAwareUserDetails) SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();
        	User orgUser=userDetail.getOrgUser();
        	loginHistoryLog(orgUser,date);
        }

		return SUCCESS;
	}

	private void loginHistoryLog(User orgUser, Date loginDate) {
		// TODO Auto-generated method stub
		LoginHistory loginHistory=new LoginHistory();
    	loginHistory.setLoggedInUser(orgUser);
    	loginHistory.setLoggedInTime(loginDate);
    	try {
			loginHistoryService.save(loginHistory);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public LoginHistoryService getLoginHistoryService() {
		return loginHistoryService;
	}


	public void setLoginHistoryService(LoginHistoryService loginHistoryService) {
		this.loginHistoryService = loginHistoryService;
	}


	private boolean islogHistoryEnabled() {
		// TODO Auto-generated method stub
		ConfigParam configParam = configParamService.getConfigParamByName(ConfigName.LOGIN_HISTORY_PARAM_NAME.getName());
		String businessUnitInfo=null;
		SortedSet<BusinessUnit> businessUnits = getSecurityHelper().getLoggedInUser().getBusinessUnits();
		Iterator<BusinessUnit> businessUnitInfomation=businessUnits.iterator();
		while(businessUnitInfomation.hasNext()){
			businessUnitInfo=businessUnitInfomation.next().getName();
			 for (ConfigValue configValue : configParam.getValues()) {
			    if (businessUnitInfo.equals(configValue.getBusinessUnitInfo().getName())) {
		    	return configValue.getConfigParamOption().getValue().equalsIgnoreCase("true");
			}
		}
	 }
		return false;
	}

	public String getPartReturnTaskCount(String taskName) throws JSONException {
		if (logger.isDebugEnabled()) {
			logger.debug("Attempting to retrive count for [" + taskName + "]");
		}
		for (Object[] jsonArray : this.partsReturnFolders) {
			if (((String)jsonArray[1]).equals(taskName)) {
				return (String)jsonArray[2];
			}
		}
		throw new JSONException("No Count found for Task [" + taskName + "]");
	}

	public String getPartReturnActionUrl(String taskName) throws JSONException {
		if (logger.isDebugEnabled()) {
			logger.debug("Attempting to retrive action url for [" + taskName
					+ "]");
		}
		for (Object[] jsonArray : this.partsReturnFolders) {
			if (((String)jsonArray[1]).equals(taskName)) {
				return (String)jsonArray[3];
			}
		}
		throw new JSONException("No Action Url for Task [" + taskName + "]");
	}

	public String getSupplierRecoveryTaskCount(String taskName)
			throws JSONException {
		if (logger.isDebugEnabled()) {
			logger.debug("Attempting to retrive count for [" + taskName + "]");
		}
		for (String[] jsonArray : this.supplierRecoveryFolders) {
			if (jsonArray[1].equals(taskName)) {
				return jsonArray[2];
			}
		}
		for (String[] jsonArray : this.supplierRecoveryPartReturnFolders) {
			if (jsonArray[1].equals(taskName)) {
				return jsonArray[2];
			}
		}
		throw new JSONException("No Count found for Task [" + taskName + "]");
	}

	public String getSupplierRecoveryActionUrl(String taskName)
			throws JSONException {
		if (logger.isDebugEnabled()) {
			logger.debug("Attempting to retrive action url for [" + taskName
					+ "]");
		}
		for (String[] jsonArray : this.supplierRecoveryFolders) {
			if (jsonArray[1].equals(taskName)) {
				return jsonArray[3];
			}
		}
		for (String[] jsonArray : this.supplierRecoveryPartReturnFolders) {
			if (jsonArray[1].equals(taskName)) {
				return jsonArray[3];
			}
		}
		throw new JSONException("No Action Url for Task [" + taskName + "]");
	}

	private void fetchRuleContexts() {
		BusinessObjectModelFactory businessObjectModel = BusinessObjectModelFactory
				.getInstance();
		Set<String> listAllContexts = businessObjectModel.listAllRuleContexts();
		this.ruleContexts.addAll(listAllContexts);
	}

	private void fetchSavedQueriesForCampaignSearch() {
		this.preDefinedCampaignSavedQueries = this.savedQueryService
				.findByName("CampaignSearch");
	}

	public List<SavedQuery> getSavedQueriesForInventory() {
		return this.savedQueriesForInventory;
	}

	public void setSavedQueriesForInventory(
			List<SavedQuery> savedQueriesForInventory) {
		this.savedQueriesForInventory = savedQueriesForInventory;
	}

	public String refreshFieldModSearchFolders() {
		this.preDefinedCampaignSavedQueries = this.savedQueryService
				.findByName("CampaignSearch");
		return SUCCESS;

    }

    public String refreshClaimSearchFolders() {
		this.savedQueries = this.predicateAdministrationService
				.findSavedQueriesByContextAndUser(
						BusinessObjectModelFactory.CLAIM_SEARCHES, getLoggedInUser().getId());
		this.preDefinedClaimSavedQueries = this.savedQueryService
				.findByName("ClaimSearches");
        return SUCCESS;
    }

    public String refreshInventorySearchFolders() {
		this.savedQueriesForInventory = this.predicateAdministrationService
				.findSavedQueriesByContextAndUser(
						BusinessObjectModelFactory.INVENTORY_SEARCHES, getLoggedInUser().getId());
		this.preDefinedInventoryStockSavedQueries = this.savedQueryService
				.findByName("InventorySTOCKSearch");
		this.preDefinedInventoryRetailSavedQueries = this.savedQueryService
				.findByName("InventoryRETAILSearch");
        return SUCCESS;
    }

    public String refreshRecoveryClaimSearchFolders() {
		this.savedQueriesForRecoveryClaims = this.predicateAdministrationService
				.findSavedQueriesByContextAndUser(
						BusinessObjectModelFactory.RECOVERY_CLAIM_SEARCHES, getLoggedInUser().getId());
		this.preDefinedRecClaimSavedQueries = this.savedQueryService
				.findByName("RecoveryClaimSearches");
        return SUCCESS;
    }

   public String refreshPartReturnSearchFolders() {
		this.savedQueriesForPartReturn = this.predicateAdministrationService
				.findSavedQueriesByContextAndUser(
						BusinessObjectModelFactory.PART_RETURN_SEARCHES, getLoggedInUser().getId());
		this.preDefinedPartReturnsSavedQueries = this.savedQueryService
				.findByName("PartReturnSearches");
        return SUCCESS;
    }

    public String refreshPartRecoverySearchFolders() {
		this.preDefinedPartRecoverySavedQueries = this.savedQueryService
				.findByName("PartRecoverySearches");
        return SUCCESS;
    }

    public String refreshItemSearchFolders() {
		this.savedQueriesForItem = this.predicateAdministrationService
				.findSavedQueriesByContextAndUser(
						BusinessObjectModelFactory.ITEM_SEARCHES, getLoggedInUser().getId());
        return SUCCESS;
    }

	public String refreshClaimFolders() {
		this.jsonString = refreshCountForTask("ClaimSubmission");
		return SUCCESS;
	}

	public String refreshPartsReturnFolders() {
		this.jsonString = refreshCountForTask("PartsReturn");
		return SUCCESS;
	}

	public String refreshSupplierRecoveryFolders() {
		this.jsonString = refreshCountForTask("SupplierRecovery");
		return SUCCESS;
	}

	public String refreshSupplierRecoveryPartReturnFolders() {
		this.jsonString = refreshCountForTask("SupplierPartReturn");
		return SUCCESS;
	}

	private void fetchClaimFolders() {
		Map<Task, Long> tasks = fetchTasks("ClaimSubmission");
		this.claimFolders = new ArrayList<String[]>();
		for (Task task : tasks.keySet()) {
			this.claimFolders.add(new String[] { Long.toString(task.getId()),
					task.getName(), tasks.get(task).toString(),getText(getDisplayValue(task.getName())) });
		}
		Collections.sort(this.claimFolders, new Comparator<String[]>() {
			public int compare(String[] o1, String[] o2) {
				return o1[1].compareTo(o2[1]); // compare the names, which is
				// the second item on the array
			}
		});
	}

	@SuppressWarnings("unchecked")
	public String refreshPendingCampaignsCount() {
		if (getLoggedInUsersDealership() != null) {
			this.jsonString = (new RefreshManagerResponder() {
				@Override
				public Map getMap() {
					Map data = new HashMap();
					populatePendingCampaignsCount();
					data.put("PENDING CAMPAIGNS",
							HomeAction.this.pendingCampaignsCount);
					return data;
				}
			}).getResponse();
		}
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String refreshUpdatingCampaignsCount() {
		if (getLoggedInUsersDealership() != null) {
			this.jsonString = (new RefreshManagerResponder() {
				@Override
				public Map getMap() {
					Map data = new HashMap();
					populateUpdatingCampaignsCount();
					data.put("UPDATING CAMPAIGNS",
							HomeAction.this.updatingCampaignsCount);
					return data;
				}
			}).getResponse();
		}
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String refreshDeniedCampaignsCount() {
		if (getLoggedInUsersDealership() != null) {
			this.jsonString = (new RefreshManagerResponder() {
				@Override
				public Map getMap() {
					Map data = new HashMap();
					populateDeniedCampaignsCount();
					data.put("DENIED CAMPAIGNS",
							HomeAction.this.deniedCampaignsCount);
					return data;
				}
			}).getResponse();
		}
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String refreshPendingReviewCampaignsCount() {
			this.jsonString = (new RefreshManagerResponder() {
				@Override
				public Map getMap() {
					Map data = new HashMap();
					populatePendingReviewCampaignsCount();
					data.put("PENDING REVIEW CAMPAIGNS",
							HomeAction.this.pendingReviewCampaignsCount);
					return data;
				}
			}).getResponse();

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String refreshRequestForExtensionCount() {

			this.jsonString = (new RefreshManagerResponder() {
				@Override
				public Map getMap() {
					Map data = new HashMap();
					populateRequestForExtensionCount();
					data.put("WAITING_FOR_RESPONSE",
							HomeAction.this.requestForExtensionCount);
					return data;
				}
			}).getResponse();

		return SUCCESS;
	}

	public String refreshExtensionRequestsCount(){

		this.jsonString = new RefreshManagerResponder(){
			@Override
			public Map getMap() {
				Map data = new HashMap();
				populateExtensionForCoverageRequestsCount();
				data.put("EXTENSION_REQUESTS_COUNT", HomeAction.this.extensionRequestsCount);
				return data;
			}
		}.getResponse();

		return SUCCESS;
	}



	private void fetchPartsReturnFolders() {
		Map<Task, Long> tasks = fetchTasks("PartsReturn");
		List<String> inboxOrders= fetchPartReturnInboxOrder();
		this.partsReturnFolders = new ArrayList<Object[]>();
		for (Task task : tasks.keySet()) {
			//removing 'Third Party Due Parts' from view for both EMEA and AMER users.
			if(task.getName().equals(WorkflowConstants.THIRD_PARTY_DUE_PARTS_TASK)){
				tasks.keySet().remove(WorkflowConstants.THIRD_PARTY_DUE_PARTS_TASK);
			} else
            //putting a check for not to show the wpra inboxes for US users
             if(!isShowWpraInboxes() && (task.getName().equals(WorkflowConstants.PREPARE_DUE_PARTS) ||
                   task.getName().equals(WorkflowConstants.WPRA_TO_BE_GENERATED) || task.getName().equals(WorkflowConstants.GENERATED_WPRA) || task.getName().equals(WorkflowConstants.CEVA_TRACKING))){
                  continue;
            }
            else{


			this.partsReturnFolders.add(new Object[] {
					Long.toString(task.getId()), task.getName(),
					tasks.get(task).toString(),
					getUrlForPartsReturnTask(task, "actionUrl"),
					getText(getDisplayValue(task.getName())),
					inboxOrders.indexOf(task.getName())
		});
            }
		}

		Collections.sort(this.partsReturnFolders, new Comparator<Object[]>() {
          	public int compare(Object[] o1, Object[] o2) {
				// TODO Auto-generated method stub
				return ((Integer) o1[5]).compareTo((Integer)o2[5]);
			}
		});
	}

	private List<String> fetchPartReturnInboxOrder() {

		return workListService.getPartReturnInboxOrders();
	}

	private void fetchSupplierRecoveryFolders() {
		Map<Task, Long> tasks = fetchTasks("SupplierRecovery");
		Map<Task, Long> task2 = fetchTasks("SupplierPartReturn");

		this.supplierRecoveryFolders = new ArrayList<String[]>();
		for (Task task : tasks.keySet()) {
			String taskName = task.getName();
			if(!(taskName.equalsIgnoreCase("Debited"))){
			this.supplierRecoveryFolders.add(new String[] {
					Long.toString(task.getId()), taskName,
					tasks.get(task).toString(),
					getUrlForPartsReturnTask(task, "actionUrl"),
					getTaskDisplayName(taskName),});}
		}
		Collections.sort(this.supplierRecoveryFolders,
				new Comparator<String[]>() {
					public int compare(String[] o1, String[] o2) {
						return o1[1].compareTo(o2[1]); // compare the names,
						// which is
						// the second item on the array
					}
				});

		this.supplierRecoveryPartReturnFolders = new ArrayList<String[]>();
		for (Task partReturnTask : task2.keySet()) {
			String taskName = partReturnTask.getName();
            if(configParamService.getBooleanValue(ConfigName.PART_RECOVERY_DIRECTLY_THROUGH_DEALER.getName()) &&
                    taskName.equalsIgnoreCase(WorkflowConstants.PART_NOT_IN_WAREHOUSE)){
                continue;
            }
			this.supplierRecoveryPartReturnFolders.add(new String[]{
                    Long.toString(partReturnTask.getId()), taskName,
                    task2.get(partReturnTask).toString(),
                    getUrlForPartsReturnTask(partReturnTask, "actionUrl"),
                    getTaskDisplayName(taskName),});
		}
		Collections.sort(this.supplierRecoveryPartReturnFolders,
				new Comparator<String[]>() {
					public int compare(String[] o1, String[] o2) {
						return o1[1].compareTo(o2[1]); // compare the names,
						// which is
						// the second item on the array
					}
				});
	}

	public String getTaskDisplayName(String taskName) {
		if (taskName.equalsIgnoreCase("For Recovery")) {
			return getText(getDisplayValue("New"));
		} else if (taskName.equalsIgnoreCase("Supplier Accepted")) {
			return getText(getDisplayValue("Accepted"));
		} else if (taskName.equalsIgnoreCase("Supplier Response")) {
			return getText(getDisplayValue("Disputed"));
		}
          else if (taskName.equalsIgnoreCase(WorkflowConstants.SUPPLIER_SHIPMENMT_GENERATED)){
			return getText("label.jbpm.task.vpra.generated");
		} else if(taskName.equals(WorkflowConstants.ROUTED_TO_NMHG)){
			return getText("label.jbpm.routedToNMHG");
		} else if(taskName.equals(WorkflowConstants.SUPPLIER_PARTS_CLAIMED)) {
			return (getText("label.jbpm.task.supplierPartsClaimed"));
		}
		else {
			return getText(getDisplayValue(taskName));
		}
	}

	@SuppressWarnings("unchecked")
	private String refreshCountForTask(final String taskName) {
		return (new RefreshManagerResponder() {
			@Override
			public Map getMap() {
				return fetchTasks(taskName);
			}

			@Override
			public String getFolderName(Object keyInstance) {
				return ((Task) keyInstance).getName();
			}
		}).getResponse();
	}

	public List<String[]> getClaimFolders() {
		return this.claimFolders;
	}

	public void setWorkListService(WorkListService workListService) {
		this.workListService = workListService;
	}

	public String getUser() {
		User user = getLoggedInUser();
		if (user != null) {
			return user.getName();
		}
		return "";
	}

	public String getDate(){
		DateFormat dateFormat =DateFormat.getDateInstance(DateFormat.LONG,getLoggedInUser().getLocale());
		return dateFormat.format( new java.util.Date());
	}

	public String getJsonString() {
		return this.jsonString;
	}

	@SuppressWarnings("unchecked")
	public void setCssTheme(String theme) {
		this.session.put(CSS_THEME_SESSION_KEY, theme);
	}

	// Doesn't perform any business function. Used only by the twms
	// SessionTimeoutNotifier widget to provide the user
	// with an option of cancelling an imminent session timeout. Yes, we can
	// ping an existing action instead, but why
	// put such a dependency and overhead.
	public String ping() throws Exception {
		return null;
	}

	public String getRevision() {
		try {
			String appServerHome = this.servletContext.getRealPath("/");
			if (logger.isDebugEnabled()) {
				logger.debug("App Server Home is [" + appServerHome + "]");
			}
			File manifestFile = new File(appServerHome, "META-INF/MANIFEST.MF");
			if (logger.isDebugEnabled()) {
				logger.debug("Manifest File is  [" + manifestFile + "]");
			}
			Manifest mf = new Manifest();
			mf.read(new FileInputStream(manifestFile));
			Attributes atts = mf.getMainAttributes();
			String build = atts.getValue("Implementation-Build");
			if (logger.isDebugEnabled()) {
				logger.debug("Build number is [" + build + "]");
			}
			String branch = atts.getValue("Implementation-Version");
			return "Version [" + branch + "] - Revision [" + build + "]";
		} catch (IOException e) {
			return "Undefined";
		}
	}

	@SuppressWarnings("unchecked")
	private Map<Task, Long> fetchTasks(String processName) {
		WorkListCriteria workListCriteria = new WorkListCriteria(
				getLoggedInUser());
//		 Let this pathetic fix be forgiven.
		ServiceProvider loggedInUsersDealership = getLoggedInUsersDealership();
		if(loggedInUsersDealership == null || isLoggedInUserAnEnterpriseDealer()){
			ServiceProvider s = new ServiceProvider();
			s.setId(-1L);
			s.setVersion(0);
			workListCriteria.setServiceProvider(s);
		}else{
			workListCriteria.setServiceProvider(loggedInUsersDealership);
		}
			workListCriteria.setServiceProviderList(getLoggedInUser().getBelongsToOrganizations());
		workListCriteria.setProcess(processName);
        workListCriteria.setDisplayNewClaimToAllProcessors(configParamService.getBooleanValue(ConfigName.DISPLAY_NEW_CLAIMS_TO_ALL_PROCESSOR.getName()));
		Map<Task, Long> tasks = this.workListService
				.getAllTasks(workListCriteria,getLoggedInUser());
		//Fix for NMHGSLMS-1020
		//commenting because of it is duplicating action folders
		//TODO : Proper fix for this
		/*if("SupplierRecovery".equalsIgnoreCase(processName)){
			workListCriteria.setProcess("SupplierPartReturn");
			//running for Confirm Part Returns,Parts Shipped to NMHG, Parts for Return To NMHG,Shipment Generated To NMHG inboxes
			Map<Task, Long> partReturntasks = this.workListService
					.getAllTasks(workListCriteria,getLoggedInUser());
			tasks.putAll(partReturntasks);
			partReturntasks.clear();
			//running for Confirm Dealer Part Returns inbox
			workListCriteria.setProcess("PartsReturn");
			partReturntasks = this.workListService
					.getAllTasks(workListCriteria,getLoggedInUser());
			tasks.putAll(partReturntasks);
		}*/
		return tasks;
	}

	private FormTaskNode getFormTaskNode(Task task) {
		// TODO Method kind of a hack to get underlying FormTaskNode.
		TaskNode taskNode = task.getTaskNode();
		if (taskNode instanceof FormTaskNode) {
			return (FormTaskNode) taskNode;
		} else if (taskNode instanceof HibernateProxy) {
			return (FormTaskNode) ((HibernateProxy) taskNode)
					.getHibernateLazyInitializer().getImplementation();
		}
		throw new RuntimeException("Node not a task node");
	}

	private String getUrlForPartsReturnTask(Task task, String type) {
		FormTaskNode taskNode = getFormTaskNode(task);
		String url = taskNode.getFormNodes().get(type);
		Assert.hasText(url, "Task Node [" + taskNode
				+ "] doesnt have a form of type [" + type + "]");
		return url;
	}

	public List<Object[]> getPartsReturnFolders() {
		return this.partsReturnFolders;
	}

	public SortedSet<String> getRuleContexts() {
		return this.ruleContexts;
	}

	public void setRuleContexts(SortedSet<String> ruleContexts) {
		this.ruleContexts = ruleContexts;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public List<SavedQuery> getSavedQueries() {
		return this.savedQueries;
	}

	public void setSavedQueries(List<SavedQuery> sq) {
		this.savedQueries = sq;
	}

	public PredicateAdministrationService getPredicateAdministrationService() {
		return this.predicateAdministrationService;
	}

	@Required
	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}

	public List<SavedQuery> getSavedQueriesForPartReturn() {
		return this.savedQueriesForPartReturn;
	}

	public void setSavedQueriesForPartReturn(
			List<SavedQuery> savedQueriesForPartReturn) {
		this.savedQueriesForPartReturn = savedQueriesForPartReturn;
	}

	public void setCampaignAssignmentService(
			CampaignAssignmentService campaignAssignmentService) {
		this.campaignAssignmentService = campaignAssignmentService;
	}

	public int getPendingCampaignsCount() {
		return this.pendingCampaignsCount;
	}

	public int getUpdatingCampaignsCount() {
		return this.updatingCampaignsCount;
	}

	public int getDeniedCampaignsCount() {
		return this.deniedCampaignsCount;
	}

	public int getPendingReviewCampaignsCount() {
		return this.pendingReviewCampaignsCount;
	}

	public int getRequestForExtensionCount() {
		return this.requestForExtensionCount;
	}

	private void populatePendingCampaignsCount() {
		this.pendingCampaignsCount = this.campaignAssignmentService
				.findNotificationsCountForDealer(getLoggedInUsersDealership());
	}

	private void populateUpdatingCampaignsCount() {
		this.updatingCampaignsCount = this.campaignAssignmentService
		.findAllCampaignNotificationCountByStatus(getLoggedInUsersDealership(),FieldModUpdateStatus.SUBMITTED,isLoggedInUserADealer(),getCurrentBusinessUnitName());
	}

	private void populateDeniedCampaignsCount() {
		this.deniedCampaignsCount = this.campaignAssignmentService
		.findAllCampaignNotificationCountByStatus(getLoggedInUsersDealership(),FieldModUpdateStatus.REJECTED,isLoggedInUserADealer(),getCurrentBusinessUnitName());
	}

	private void populatePendingReviewCampaignsCount() {
		this.pendingReviewCampaignsCount = this.campaignAssignmentService
		.findAllCampaignNotificationCountByStatus(getLoggedInUsersDealership(),FieldModUpdateStatus.SUBMITTED,isLoggedInUserADealer(),getCurrentBusinessUnitName());
	}


	private void populateRequestForExtensionCount() {
		this.requestForExtensionCount = warrantyCoverageRequestService
				.findExtensionCountForDealer(getLoggedInUser(), getLoggedInUsersDealership());

	}

	private void populateExtensionForCoverageRequestsCount() {
			Long count = warrantyCoverageRequestService.findExtensionForCoverageRequestsCount();
			if(count !=null){
				extensionRequestsCount = count.longValue();
			} else{
				extensionRequestsCount = 0l;
			}
	}

	private void populateStockCount(){
		stockCount=this.inventoryService.getCountOfInventoryItems(InventoryType.STOCK, false, getVintageStockShipmentDate());
	}

	private void populateVintageStockCount(){
		vintageStockCount=this.inventoryService.getCountOfInventoryItems(InventoryType.STOCK, true, getVintageStockShipmentDate());
	}

	public List<String[]> getSupplierRecoveryFolders() {
		return this.supplierRecoveryFolders;
	}

	public List<SavedQuery> getSavedQueriesForRecoveryClaims() {
		return this.savedQueriesForRecoveryClaims;
	}

	public void setSavedQueriesForRecoveryClaims(
			List<SavedQuery> savedQueriesForRecoveryClaims) {
		this.savedQueriesForRecoveryClaims = savedQueriesForRecoveryClaims;
	}

	public List<SavedQuery> getSavedQueriesForPartInventory() {
		return savedQueriesForPartInventory;
	}

	public void setSavedQueriesForPartInventory(
			List<SavedQuery> savedQueriesForPartInventory) {
		this.savedQueriesForPartInventory = savedQueriesForPartInventory;
	}

	public List<SavedQuery> getSavedQueriesForItem() {
		return this.savedQueriesForItem;
	}

	public void setSavedQueriesForItem(List<SavedQuery> savedQueriesForItem) {
		this.savedQueriesForItem = savedQueriesForItem;
	}

	// Modified for : There should be a space between the username and login (in
	// braces)
	// on the user's home page. E.g. Welcome Erika Bobbitt(71468:e_bobbitt).
	// Here there must be a space between Bobbitt and the opening bracket
	public String getUserForDisplay() {
		User user = getLoggedInUser();
		StringBuffer nameBuffer = new StringBuffer();
		if (user != null) {
			if (user.getFirstName() != null) {
				nameBuffer.append(user.getFirstName());
				if (user.getLastName() != null) {
					nameBuffer.append(" " + user.getLastName());
				}
				if (isPageReadOnly()) {
					nameBuffer.append(" " + "ReadOnly") ;
				}
				nameBuffer.append(" " + "(" + user.getName() + ")");
				return nameBuffer.toString();
			}

			return user.getName();
		}
		return "";
	}

	public String logout() {
		userMessage = getText("message.userLoggedOut");
		return SUCCESS;
	}

	public String loginAfterTimeout() {
		userMessage = getText("message.userTimedOut");
		return SUCCESS;
	}

	@SuppressWarnings("unused")
	private void updateBusinessUnit() {
		businessUnits = getSecurityHelper().getLoggedInUser()
				.getBusinessUnits();
		OrgAwareUserDetails userDetail = (OrgAwareUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		// TODO: Its a hack,since as of now there is only one business unit
		// If one user has multiple business units, then it should be handled in
		// a different way
		// Which RamaLakshmi is implementing it in OEM branch
		for (BusinessUnit businessUnit : businessUnits) {
			userDetail.setCurrentBusinessUnit(businessUnit);
		}
		HttpSession httpSession = ((HttpServletRequest) request).getSession();
		request.getSession().setAttribute("currentBusinessUnit",
				userDetail.getCurrentBusinessUnit().getName());

	}

	public void setSessionTimeOutInterval(int timeOutInSecs) {
		request.getSession().setMaxInactiveInterval(timeOutInSecs);
	}

	public int getSessionTimeOutInterval() {
		return request.getSession().getMaxInactiveInterval();
	}

	public String getUserMessage() {
		return userMessage;
	}

	@SuppressWarnings("unused")
	private void fetchBusinessUnitInfo() {
		businessUnits = getSecurityHelper().getLoggedInUser()
				.getBusinessUnits();
		OrgAwareUserDetails userDetail = (OrgAwareUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		if (userDetail.getWarrantyAdminSelectedBusinessUnit() != null) {
			selectedBusinessUnit = userDetail
					.getWarrantyAdminSelectedBusinessUnit();
		}
	}

	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit==null?getLoggedInUser().getPreferredBu():selectedBusinessUnit;
	}

	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}

	public String updateSelectedBusinessUnit() {
		OrgAwareUserDetails userDetail = (OrgAwareUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		if (getWarrantyAdminSelectedBusinessUnit() != null) {
			setWarrantyAdminSelectedBusinessUnit(getWarrantyAdminSelectedBusinessUnit());
			userDetail
					.setWarrantyAdminSelectedBusinessUnit(getWarrantyAdminSelectedBusinessUnit());
		}
		return SUCCESS;
	}

	public String updateSelectedOrganization() {
		getLoggedInUser().setCurrentlyActiveOrganization(orgService.findOrgById(selectedOrganization));
		return SUCCESS;
	}

	public void setOrganizationOnLogin() {
		if(getLoggedInUser().getCurrentlyActiveOrganization() != null)
			return;
		SortedSet<Organization> orgs = getBelongsToOrganizations();
		if(orgs.size() > 0)
			getLoggedInUser().setCurrentlyActiveOrganization(orgs.iterator().next());
	}

	public void setBusinessUnitOnLogin() {
		if (isLoggedInUserAnAdmin() || isLoggedInUserContractAdmin()) {
			OrgAwareUserDetails userDetail = (OrgAwareUserDetails) SecurityContextHolder
					.getContext().getAuthentication().getPrincipal();
			if (getSelectedBusinessUnit() != null) {
				setWarrantyAdminSelectedBusinessUnit(getSelectedBusinessUnit());
				userDetail
						.setWarrantyAdminSelectedBusinessUnit(getSelectedBusinessUnit());
				for(BusinessUnit bu:businessUnits){
					if(bu.getName().equalsIgnoreCase(getSelectedBusinessUnit())){
						userDetail.setDefaultBusinessUnit(bu);
					}
				}
			} else {
				if (businessUnits != null && !businessUnits.isEmpty()) {
					userDetail
							.setWarrantyAdminSelectedBusinessUnit(businessUnits
									.iterator().next().getName());
					setWarrantyAdminSelectedBusinessUnit(businessUnits
							.iterator().next().getName());
				}
				if (businessUnits != null && !businessUnits.isEmpty()
						&& businessUnits.size() == 1) {
					userDetail.setDefaultBusinessUnit(businessUnits.iterator()
							.next());
				}
			}
		} else {
			// for all other users, if the user is mapped to just 1 bu, set it
			// as his current business unit
			OrgAwareUserDetails userDetail = (OrgAwareUserDetails) SecurityContextHolder
					.getContext().getAuthentication().getPrincipal();
			if (getSelectedBusinessUnit() != null) {
				userDetail.setDefaultBusinessUnit(getBusinessUnitService()
						.findBusinessUnit(getSelectedBusinessUnit()));
			} else {
				if (businessUnits != null && !businessUnits.isEmpty()
						&& businessUnits.size() == 1) {
					userDetail.setDefaultBusinessUnit(businessUnits.iterator()
							.next());
				}
			}
		}
	}

	private void fetchFoldersForTransactionType(String transactionType,
			List<WarrantyFolderView> warrantyFolders) {
		setDefaultWarrantyFoldersCount(warrantyFolders);
		boolean isAdmin = false;
		boolean isDealer = false;
		for (Role role : getLoggedInUser().getRoles()) {
			if (TWMSWebConstants.ROLE_WARRANTY_PROCESSOR.equals(role.getName())) {
				isAdmin = true;
			}
			// Added DEALER_SALES_ADMIN role to fix TSESA-499
			if (TWMSWebConstants.ROLE_DEALER.equals(role.getName())
					|| TWMSWebConstants.ROLE_INVENTORY_ADMIN.equals(role.getName()) || Role.DEALER_SALES_ADMIN.equals(role.getName())){
                                        // Commented to fix TWMS4.3-672
                                        //|| Role.DEALER_SALES_ADMIN.equals(role.getName())) {
				isDealer = true;
			}
		}
		if (isAdmin || isDealer) {
			List<WarrantyFolderView> folders = warrantyTaskInstanceService
					.fetchWarrantyFoldersForTransactionType(transactionType,
							isAdmin, isDealer, getLoggedInUser());
			for (WarrantyFolderView warrantyFolder : warrantyFolders) {
				for (WarrantyFolderView folder : folders) {
					if (folder.getStatus() == warrantyFolder.getStatus())
						warrantyFolder.setFolderCount(folder.getFolderCount());
				}
			}
		}
	}

	public boolean isInternalUser() {
		return this.isLoggedInUserAnInternalUser();
	}

	public void setWarrantyTaskInstanceService(
			WarrantyTaskInstanceService warrantyTaskInstanceService) {
		this.warrantyTaskInstanceService = warrantyTaskInstanceService;
	}

	private void setDefaultWarrantyFoldersCount(
			List<WarrantyFolderView> warrantyFolders) {
		createDefaultFolderCount(WarrantyStatus.DRAFT, warrantyFolders);
		createDefaultFolderCount(WarrantyStatus.SUBMITTED, warrantyFolders);
		createDefaultFolderCount(WarrantyStatus.FORWARDED, warrantyFolders);
		createDefaultFolderCount(WarrantyStatus.REJECTED, warrantyFolders);
		createDefaultFolderCount(WarrantyStatus.REPLIED, warrantyFolders);
		createDefaultFolderCount(WarrantyStatus.RESUBMITTED, warrantyFolders);
		createDefaultFolderCount(WarrantyStatus.DELETED, warrantyFolders);
	}

	private void createDefaultFolderCount(WarrantyStatus status,
			List<WarrantyFolderView> warrantyFolders) {
		WarrantyFolderView folder = new WarrantyFolderView();
		folder.setFolderCount(new Long(0));
		folder.setStatus(status);
		setFolderName(status, folder);
		warrantyFolders.add(folder);
	}

	private void setFolderName(WarrantyStatus status, WarrantyFolderView folder) {
		if (WarrantyStatus.DRAFT == status) {
			folder.setFolderName(getText("label.common.draft"));
		} else if (WarrantyStatus.SUBMITTED == status) {
			folder.setFolderName(getText("label.common.drPendingForApproval"));
		} else if (WarrantyStatus.FORWARDED == status) {
			folder.setFolderName(getText("label.common.drForwarded"));
		} else if (WarrantyStatus.REPLIED == status) {
			folder.setFolderName(getText("label.common.drReplied"));
		} else if (WarrantyStatus.REJECTED == status) {
			folder.setFolderName(getText("label.common.drRejected"));
		} else if (WarrantyStatus.RESUBMITTED == status) {
			folder.setFolderName(getText("label.common.drResubmitted"));
		} else if (WarrantyStatus.DELETED == status) {
			folder.setFolderName(getText("label.common.drDeleted"));
		}
	}


	public List<WarrantyFolderView> getWarrantyFoldersForDR() {
		return warrantyFoldersForDR;
	}

	public void setWarrantyFoldersForDR(
			List<WarrantyFolderView> warrantyFoldersForDR) {
		this.warrantyFoldersForDR = warrantyFoldersForDR;
	}

	public String getWarrantyAdminSelectedBusinessUnit() {
		return warrantyAdminSelectedBusinessUnit;
	}

	public void setWarrantyAdminSelectedBusinessUnit(
			String warrantyAdminSelectedBusinessUnit) {
		this.warrantyAdminSelectedBusinessUnit = warrantyAdminSelectedBusinessUnit;
	}

	public SavedQueryService getSavedQueryService() {
		return savedQueryService;
	}

	public void setSavedQueryService(SavedQueryService savedQueryService) {
		this.savedQueryService = savedQueryService;
	}

	public List<SavedQuery> getPreDefinedClaimSavedQueries() {
		return preDefinedClaimSavedQueries;
	}

	public void setPreDefinedClaimSavedQueries(
			List<SavedQuery> preDefinedClaimSavedQueries) {
		this.preDefinedClaimSavedQueries = preDefinedClaimSavedQueries;
	}

	public List<SavedQuery> getPreDefinedInventoryStockSavedQueries() {
		return preDefinedInventoryStockSavedQueries;
	}

	public void setPreDefinedInventoryStockSavedQueries(
			List<SavedQuery> preDefinedInventoryStockSavedQueries) {
		this.preDefinedInventoryStockSavedQueries = preDefinedInventoryStockSavedQueries;
	}

	public List<SavedQuery> getPreDefinedInventoryRetailSavedQueries() {
		return preDefinedInventoryRetailSavedQueries;
	}

	public void setPreDefinedInventoryRetailSavedQueries(
			List<SavedQuery> preDefinedInventoryRetailSavedQueries) {
		this.preDefinedInventoryRetailSavedQueries = preDefinedInventoryRetailSavedQueries;
	}

	public List<SavedQuery> getPreDefinedCampaignSavedQueries() {
		return preDefinedCampaignSavedQueries;
	}

	public void setPreDefinedCampaignSavedQueries(
			List<SavedQuery> preDefinedCampaignSavedQueries) {
		this.preDefinedCampaignSavedQueries = preDefinedCampaignSavedQueries;
	}

	public List<SavedQuery> getPreDefinedPartReturnsSavedQueries() {
		return preDefinedPartReturnsSavedQueries;
	}

	public void setPreDefinedPartReturnsSavedQueries(
			List<SavedQuery> preDefinedPartReturnsSavedQueries) {
		this.preDefinedPartReturnsSavedQueries = preDefinedPartReturnsSavedQueries;
	}

	public List<SavedQuery> getPreDefinedRecClaimSavedQueries() {
		return preDefinedRecClaimSavedQueries;
	}

	public void setPreDefinedRecClaimSavedQueries(
			List<SavedQuery> preDefinedRecClaimSavedQueries) {
		this.preDefinedRecClaimSavedQueries = preDefinedRecClaimSavedQueries;
	}

	public List<SavedQuery> getPreDefinedPartRecoverySavedQueries() {
		return preDefinedPartRecoverySavedQueries;
	}

	public void setPreDefinedPartRecoverySavedQueries(
			List<SavedQuery> preDefinedPartRecoverySavedQueries) {
		this.preDefinedPartRecoverySavedQueries = preDefinedPartRecoverySavedQueries;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	public String refreshWarrantyRegistrationTransferFolders() {
		this.jsonString = refreshCountForWarrantyTask();
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	private String refreshCountForWarrantyTask() {
		return (new RefreshManagerResponder() {
			@Override
			public Map getMap() {
				if (InvTransationType.DR.name().equalsIgnoreCase(transactionType)) {
					return fetchWarrantyTasks(InvTransationType.DR.name());
				} else {
					return fetchWarrantyTasks(InvTransationType.ETR.name());
				}
			}

			@Override
			public String getFolderName(Object keyInstance) {
				return keyInstance.toString();
			}
		}).getResponse();
	}

	public String refreshUserStockCount(){
		this.jsonString = (new RefreshManagerResponder() {
			@Override
			public Map getMap() {
				Map data = new HashMap();
				populateStockCount();
				data.put("STOCK COUNT",
						HomeAction.this.stockCount);
				return data;
			}
		}).getResponse();
	return SUCCESS;
	}

	public String refreshUserVintageStockCount(){
		this.jsonString = (new RefreshManagerResponder() {
			@Override
			public Map getMap() {
				Map data = new HashMap();
				populateVintageStockCount();
				data.put("VINTAGE STOCK COUNT",
						HomeAction.this.vintageStockCount);
				return data;
			}
		}).getResponse();
	return SUCCESS;
	}

	public String refreshRecoveryPendingClaimsCount(){
		this.jsonString = (new RefreshManagerResponder() {
			@Override
			public Map getMap() {
				Map data = new HashMap();
				populateRecoveryPendingClaimsCount();
				data.put("RECOVERY_PENDING_CLAIMS_COUNT",
						HomeAction.this.recoveryPendingClaimsCount);
				return data;
			}
		}).getResponse();
	return SUCCESS;
	}

	private Map<String, Long> fetchWarrantyTasks(String transactionType) {
		boolean isAdmin = false;
		boolean isDealer = false;
		boolean isInvAdmin = false;
		for (Role role : getLoggedInUser().getRoles()) {
			if (TWMSWebConstants.ROLE_WARRANTY_PROCESSOR.equals(role.getName())) {
				isAdmin = true;
			}
			if (TWMSWebConstants.ROLE_DEALER.equals(role.getName())
					|| TWMSWebConstants.ROLE_INVENTORY_ADMIN.equals(role.getName())) {
				isDealer = true;
			}
			if (TWMSWebConstants.ROLE_INVENTORY_ADMIN.equals(role.getName())) {
				isInvAdmin = true;
			}
		}
                // Added to fix TWMS4.3-672
                if(!isAdmin && !isDealer) return Collections.emptyMap();
		List<Object[]> result = warrantyTaskInstanceService
				.fetchCountsForTransactionType(transactionType, isAdmin,
						isDealer, getLoggedInUser());
		Map<String, Long> tasks = new HashMap<String, Long>();
		for (Object[] resultValues : result) {
			boolean wrntyStatus=verifyIfFwdOrReject(resultValues,isAdmin,isInvAdmin);
			if(!(((WarrantyStatus) resultValues[1]).getStatus().equals(WarrantyStatus.DELETED.getStatus()) && isDealer)) {
				if(!wrntyStatus){
				tasks.put(((WarrantyStatus) resultValues[1]).getStatus(),
						(Long) resultValues[0]);
			}
			}
		}
		List<String> statusList = new ArrayList<String>();
		if (isDealer) {
			statusList.add(WarrantyStatus.DRAFT.getStatus());
			if(!isInvAdmin){
			statusList.add(WarrantyStatus.FORWARDED.getStatus());
			statusList.add(WarrantyStatus.REJECTED.getStatus());
			}
			if(!transactionType.equals(InvTransationType.DR.name()) && !isInvAdmin)
			statusList.add(WarrantyStatus.DELETED.getStatus());
		}
		if (isAdmin) {
			statusList.add(WarrantyStatus.SUBMITTED.getStatus());
			statusList.add(WarrantyStatus.REPLIED.getStatus());
			statusList.add(WarrantyStatus.RESUBMITTED.getStatus());
		}
		for (String status : statusList) {
			if (tasks.get(status) == null) {
				tasks.put(status, new Long(0));
			}
		}
		return tasks;
	}


	private boolean verifyIfFwdOrReject(Object[] resultValues, boolean isAdmin, boolean isInvAdmin) {
		boolean wrntyStatus = false;
		if ((isAdmin||isInvAdmin) &&(((WarrantyStatus) resultValues[1]).getStatus()
				.equals(WarrantyStatus.FORWARDED.getStatus()))
				|| (((WarrantyStatus) resultValues[1]).getStatus()
						.equals(WarrantyStatus.REJECTED.getStatus()))) {
			wrntyStatus = true;
		}
		return wrntyStatus;
	}

	public List<WarrantyFolderView> getWarrantyFoldersForETR() {
		return warrantyFoldersForETR;
	}

	public void setWarrantyFoldersForETR(
			List<WarrantyFolderView> warrantyFoldersForETR) {
		this.warrantyFoldersForETR = warrantyFoldersForETR;
	}

	public WarrantyCoverageRequestService getWarrantyCoverageRequestService() {
		return warrantyCoverageRequestService;
	}

	public void setWarrantyCoverageRequestService(
			WarrantyCoverageRequestService warrantyCoverageRequestService) {
		this.warrantyCoverageRequestService = warrantyCoverageRequestService;
	}

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
	public long getExtensionRequestsCount() {
		return extensionRequestsCount;
	}

	public void setExtensionRequestsCount(long extensionRequestsCount) {
		this.extensionRequestsCount = extensionRequestsCount;
	}

	public Long getSelectedOrganization() {
		return this.selectedOrganization;
	}

	public void setSelectedOrganization(Long selectedOrganization) {
		this.selectedOrganization = selectedOrganization;
	}

	public void setAllowSessionTimeOut(Boolean allowSessionTimeOut) {
		this.allowSessionTimeOut = allowSessionTimeOut;
	}

	public void setEnableDealersToViewPR(Boolean enableDealersToViewPR) {
		this.isEnableDealersToViewPR = enableDealersToViewPR;
	}

	public Boolean isEnableDealersToViewPR() {
		return  configParamService.getBooleanValue(ConfigName.ENABLE_DEALERS_TO_VIEW_PART_RETURNS.getName());
    }

    public boolean isShowWpraInboxes() {
        return configParamService.getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName());
    }

    public void setShowWpraInboxes(boolean showWpraInboxes) {
        this.showWpraInboxes = showWpraInboxes;
    }

    public SortedSet<Organization> getBelongsToOrganizations() {
    	SortedSet<Organization> sortedOrgs = new TreeSet<Organization>(
				new Comparator<Organization>() {
					public int compare(Organization org1, Organization org2) {
						return org1.getName().compareTo(org2.getName());
					}
				});
		User liu = getLoggedInUser();
		List<Organization> orgList = liu.getBelongsToOrganizations();
		if (orgList != null && orgList.size() > 0)
			for (Organization org : orgList)
				if (org.getD().isActive()) {
					if (InstanceOfUtil
							.isInstanceOfClass(Dealership.class, org)) {
						Dealership dealer = (Dealership) org;
						String brand = orgService
								.findMarketingGroupCodeBrandByDealership(dealer);
						if (null != dealer && null != dealer.getBrand()
								&& null != org.getName()) {
							org.setNameWithBrand(org.getName() + "-" + brand);
						}
						sortedOrgs.add(org);
					}
				}

		return sortedOrgs;
	}

	public boolean checkForSessionTimeOut(String name) {
		List<ConfigValue> configValues = this.configParamService
				.getValuesForConfigParam(name);
		if (configValues != null && !configValues.isEmpty()) {
			for (ConfigValue configValue : configValues) {
				if (configValue.getConfigParamOption() != null
						&& ("false").equalsIgnoreCase(
								configValue.getConfigParamOption().getValue())) {
					return false;
				}
			}
		}
		return true;
	}


	public Boolean isAllowSessionTimeOut() {
		if(allowSessionTimeOut == null){
		  allowSessionTimeOut = checkForSessionTimeOut("isSessionTimeOutAllowed");
		}

        return allowSessionTimeOut;
    }

	public String getDisplayValue(String taskName) {
		if(NAMES_AND_KEY.get(taskName) != null){
			return NAMES_AND_KEY.get(taskName);
		}else{
			return taskName;
		}
	}

	public List<String[]> getSupplierRecoveryPartReturnFolders() {
		return supplierRecoveryPartReturnFolders;
	}

	public void setSupplierRecoveryPartReturnFolders(
			List<String[]> supplierRecoveryPartReturnFolders) {
		this.supplierRecoveryPartReturnFolders = supplierRecoveryPartReturnFolders;
	}

	public boolean isDealerEligibleToPerformRMT(){
		boolean isEligible = false;
		if(isLoggedInUserADealer() && !isLoggedInUserAnInternalUser()){
			Map<String, List<Object>> buValues = configParamService.
			getValuesForAllBUs(ConfigName.CAN_DEALER_PERFORM_RMT.getName());
			for (String buName : buValues.keySet()) {
				Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
				if(booleanValue){
					isEligible=true;
					break;
				}
			}
		}
		return isEligible;
    }

	public boolean smrClaimAllowed() {
		return configParamService.getBooleanValue(ConfigName.SMR_CLAIM_ALLOWED.getName());
	}

	public boolean isPartShipperLimitedViewOnly(){
		return
			getLoggedInUser().getRoles().size() == 2 &&
			getLoggedInUser().hasRole(Role.PART_SHIPPER_LIMITED_VIEW) &&
			getLoggedInUser().hasRole(Role.PARTSHIPPER);
	}

	public String getHtmlFileBasedOnFolderName(String category, String folderName) {
		if("Shipment Generated".equals(folderName)) {
			category = "Part_Returns";
		} else if("Forwarded".equals(folderName) || "Forwarded Externally".equals(folderName) || "Forwarded Internally".equals(folderName)) {
			folderName = "Forwarded";
		}
		folderName = folderName.replace(" ", "_");
		return category + "/" + folderName + ".htm";
	}

	public String getHtmlFileBasedOnRole(String userRole) {
		userRole = userRole.replace(" ", "_");
		return userRole;
	}
	public String getBusinessUnitName()
	{
		return getLoggedInUser().getBusinessUnits().first().getName();
	}

	private String getCurrentBusinessUnitName() {
		return getCurrentBusinessUnit().getName();
	}

	public void setClaimService(ClaimService claimService){
		this.claimService = claimService;
	}

	public Long getStockCount() {
		return stockCount;
	}

	public void setStockCount(Long stockCount) {
		this.stockCount = stockCount;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public Long getRecoveryPendingClaimsCount() {
		return recoveryPendingClaimsCount;
	}

	public void setRecoveryPendingClaimsCount(Long recoveryPendingClaimsCount) {
		this.recoveryPendingClaimsCount = recoveryPendingClaimsCount;
	}

	private void populateRecoveryPendingClaimsCount(){
		recoveryPendingClaimsCount=this.claimService.getCountOfPendingRecoveryClaims();
	}

	public Long getVintageStockCount() {
		return vintageStockCount;
}

	public void setVintageStockCount(Long vintageStockCount) {
		this.vintageStockCount = vintageStockCount;
	}

	public boolean displayVintageStockInbox(){
		return configParamService.getBooleanValue(ConfigName.DISPLAY_VINTAGE_STOCK.getName());
	}

	//"Go to Fleet" implementation
	public String getAccessToken() throws Exception{
        JSONObject object = new JSONObject();
        object.put("token", LoginUtil.generateTokenKeySpec(getLoggedInUser().getName()));
        return writeJsonResponse(object);
    }

    public String goToFleet() throws Exception{
        this.token = LoginUtil.generateTokenKeySpec(getLoggedInUser().getName());
        return SUCCESS;
    }

    public String getToken() {
        return token;
    }

    public Boolean appSSOEnabledForLoggedInUser() {
        Boolean fleetLinkEnabled = Boolean.FALSE;
        for (Role role : getLoggedInUser().getRoles()) {
            if (role.getRoleCategory().equals(RoleCategory.FLEET)) {
            	fleetLinkEnabled = Boolean.TRUE;
                break;
            }
        }
        return fleetLinkEnabled;
    }

    // Fleet Impl ends

	public CalendarDate getVintageStockShipmentDate(){
		if(displayVintageStockInbox()){
			String dateString = configParamService.getStringValue(ConfigName.SHIPMENT_DATE_FOR_VINTAGE_STOCK.getName());
			if(dateString != null){
				return CalendarDate.from(dateString, "dd/MM/yyyy");
			}
		}
		return null;
	}

}