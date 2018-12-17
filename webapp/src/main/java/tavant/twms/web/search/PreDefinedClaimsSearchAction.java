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

package tavant.twms.web.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.claim.ClaimSearchCriteria;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.query.SavedQuery;
import tavant.twms.domain.query.SavedQueryService;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;

@SuppressWarnings("serial")
public class PreDefinedClaimsSearchAction extends SummaryTableAction implements
		ServletRequestAware {

	ClaimSearchCriteria claimSearchCriteria;
	private String searchString = null;
	private ClaimService claimService;
        private DealerGroupService dealerGroupService;
	private SavedQueryService savedQueryService;
	private Long queryId;
	private boolean notATemporaryQuery;
	private PreDefinedClaimsSearchFormData preDefinedClaimsSearchFormData;
	private Map<String, String> listOfManufacturingSite;
	private PredicateAdministrationService predicateAdministrationService;
	private boolean isInternalUser;
	private String savedQueryName;
	private String context;
	private List<ItemGroup> productTypes = new ArrayList<ItemGroup>();
	private List<ItemGroup> productCodes;
	private List<ItemGroup> modelTypes = new ArrayList<ItemGroup>();
	private ConfigParamService configParamService;
	private LovRepository lovRepository;
	private Map<String, List<Object>> buClaimTypeMap = new HashMap<String, List<Object>>(); 
	private Set<ClaimType> claimTypes = new TreeSet<ClaimType>();
    private Map<String, String> totalAmountOperators = new HashMap<String, String>();
        private List<Currency> allCurrencies = new ArrayList<Currency>();
    private List<ServiceProvider> childDealerShip = new ArrayList<ServiceProvider>() ;
    private boolean isSaveQuery;
	public boolean isSaveQuery() {
		return isSaveQuery;
	}

	public void setSaveQuery(boolean isSaveQuery) {
		this.isSaveQuery = isSaveQuery;
	}

	private static Logger logger = LogManager
			.getLogger(PreDefinedClaimsSearchAction.class);

	public PreDefinedClaimsSearchAction() {
		super();
	}
	
	public boolean showAuthorizationNumber() {
		return getConfigParamService()
					.getBooleanValue(
							ConfigName.SHOW_AUTHORIZATION_NUMBER_ON_CLAIM
									.getName());
	}
	
	public boolean isPageReadOnly() {
		return false;
	}
	
	public String deletePredefinedQuery(){
		if(queryId != null){
			savedQueryService.deleteQueryWithId(queryId);
		}
		return SUCCESS;
	}

	private void saveSearchQuery() {
		if (queryId == null) {
			if (claimSearchCriteria != null) {
				XStream xstream = new XStream(new DomDriver());
				searchString = xstream.toXML(claimSearchCriteria);
				removeNewLineCharactersFromSearchString();
				SavedQuery savedQuery = new SavedQuery();
				savedQuery.setSearchQuery(searchString);
				savedQuery.setContext(context);
				savedQuery.setSearchQueryName(savedQueryName);
				try {
					savedQueryService.saveSearchQuery(savedQuery);
					queryId = savedQuery.getId();
				} catch (Exception e) {
					logger.error("Exception occured is", e);
				}
			}
		} else {
			try {
				SavedQuery savedQuery = savedQueryService.findById(queryId);
				XStream xstream = new XStream(new DomDriver());
				searchString = xstream.toXML(claimSearchCriteria);
				removeNewLineCharactersFromSearchString();
				savedQuery.setSearchQuery(searchString);
				savedQuery.setContext(context);
				savedQuery.setSearchQueryName(savedQueryName);
				savedQueryService.update(savedQuery);
			} catch (Exception e) {
				logger.error("Exception occured is", e);
			}
		}
	}

	@Override
	protected PageResult<?> getBody() {
		if (claimSearchCriteria == null) {
			if (notATemporaryQuery) {
				SavedQuery savedQuery = savedQueryService.findById(queryId);
				searchString = savedQuery.getSearchQuery();
				XStream xstream = new XStream(new DomDriver());
				claimSearchCriteria = (ClaimSearchCriteria) xstream
						.fromXML(searchString);
			} else {
				claimSearchCriteria = (ClaimSearchCriteria) session
						.get("claimSearchCriteria");
			}
		}
		if (isLoggedInUserADealer()) {
			claimSearchCriteria.setDealerNumber(getLoggedInUsersDealership()
					.getDealerNumber());
		}
        claimSearchCriteria.setLoggedInUser(getLoggedInUser());
		claimSearchCriteria.setPageSpecification(getCriteria()
				.getPageSpecification());
		addSortCriteria(claimSearchCriteria);
        if(null == claimSearchCriteria.getRestrictSearch()){
           claimSearchCriteria.setRestrictSearch(Boolean.TRUE);
        }
		addFilterCriteria(claimSearchCriteria);
		claimSearchCriteria.setShowClaimStatusToDealer(isClaimStatusShownToDealer());
		PageResult<?> claims = null;
		try {
			claims = this.claimService
					.findAllClaimsMatchingCriteria(claimSearchCriteria);
		} catch (Exception e) {
			logger.error("Exception Occurred is ", e);
		}

		return claims;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<SummaryTableColumn> getHeader() {
		if (claimSearchCriteria != null) {
			if (notATemporaryQuery && isSaveQuery) {
				saveSearchQuery();
			} else {								
				session.put("claimSearchCriteria", claimSearchCriteria);
			}
		}
		this.tableHeadData = new ArrayList<SummaryTableColumn>();
		this.tableHeadData.add(new SummaryTableColumn(
				"label.inboxView.claimNumber", "claimNumber", 16, "string",
				"claimNumber", true, false, false, false));
		
		this.tableHeadData.add(new SummaryTableColumn("", "id", 0, "number",
				"id", false, true, true, false));
		
		if (inboxViewFields())
        	addInboxViewFieldsToHeader(tableHeadData, LABEL_COLUMN_WIDTH);
        else {
			if(isLoggedInUserAnInternalUser() || isClaimStatusShownToDealer())
				this.tableHeadData.add(new SummaryTableColumn(
						"label.inboxView.claimStatus", "enum:ClaimState:activeClaimAudit.state", 12,
						"string", "activeClaimAudit.state"));
			else
				this.tableHeadData.add(new SummaryTableColumn(
						"label.inboxView.claimStatus", "enum:ClaimState:activeClaimAudit.state", 12,
						"string", "activeClaimAudit.state.displayStatus", SummaryTableColumnOptions.NO_SORT));
			
			this.tableHeadData.add(new SummaryTableColumn(
					"label.claim.historicalClaimNumber", "histClmNo", 12, "string"));
			/*this.tableHeadData.add(new SummaryTableColumn(
					"label.inboxView.servProviderName", "forDealer.name", 12, "string"));
			this.tableHeadData.add(new SummaryTableColumn(
					"columnTitle.common.model",
					"model", 12, "String",
					SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));*/
			
			this.tableHeadData.add(new SummaryTableColumn(
					"label.inboxView.servProviderName", "forDealer.name", 12, "string"));
			this.tableHeadData.add(new SummaryTableColumn(
					"columnTitle.common.model",
					"itemReference.model.name", 12, "String"));
			
			this.tableHeadData.add(new SummaryTableColumn(
					"columnTitle.newClaim.failureCode",
					"activeClaimAudit.serviceInformation.faultCode", 12, "String"));
			this.tableHeadData.add(new SummaryTableColumn(
					"columnTitle.newClaim.causalPart",
					"activeClaimAudit.serviceInformation.causalBrandPart.itemNumber", 12, "String", "causalPartBrandItemNumber"));
			this.tableHeadData.add(new SummaryTableColumn(
					"label.inboxView.failureDate", "activeClaimAudit.failureDate", 12, "date"));
	        this.tableHeadData.add(new SummaryTableColumn(
					"label.inboxView.repairDate", "activeClaimAudit.repairDate",12, "date"));
        }
		if(!isLoggedInUserAnInternalUser() && !isClaimStatusShownToDealer()) {
			List<SummaryTableColumn> headerData = new ArrayList<SummaryTableColumn>();
			for(SummaryTableColumn col : tableHeadData) {
				if(col.getId().equalsIgnoreCase("enum:ClaimState:state")) {
					col = new SummaryTableColumn(
							"label.inboxView.claimStatus", "enum:ClaimState:state", 12,
							"string", "activeClaimAudit.state.displayStatus", SummaryTableColumnOptions.NO_SORT);
				}
				headerData.add(col);
			}
			tableHeadData = headerData;
		}
		
		return this.tableHeadData;
	}
	
	
	

	@SuppressWarnings("unchecked")
	public String showSearchPage() {
            populateOperators();
            fetchAllCurrencies();
        if(claimSearchCriteria==null){
        	claimSearchCriteria=new ClaimSearchCriteria();
        	claimSearchCriteria.setIncludeNCRClaims("false");
        }    
        childDealerShip = fetchChildDealerShips();
		this.isInternalUser = this.orgService.isInternalUser(getLoggedInUser());
		this.preDefinedClaimsSearchFormData.populateSearchFields();
		if(!getLoggedInUser().getUserType().equals(AdminConstants.SUPPLIER_USER) && this.orgService.isDealer(getLoggedInUser())){
			String brandType=this.orgService.findDealerBrands(getLoggedInUsersOrganization());
			productTypes = claimService.findProductTypesByBrand(ItemGroup.PRODUCT,brandType);
			modelTypes = claimService.findModelTypesByBrand(ItemGroup.MODEL,brandType);
		}
		else{
		productTypes = claimService.findProductTypes(ItemGroup.PRODUCT);
		modelTypes = claimService.findModelTypes(ItemGroup.MODEL);		
		}
		setProductCodes(new ArrayList<ItemGroup>(productTypes));
		if(productCodes != null){
			Collections.sort(productCodes, new Comparator(){
				public int compare(Object obj0, Object obj1){
					ItemGroup item0 = (ItemGroup)obj0;
					ItemGroup item1 = (ItemGroup)obj1;
					return item0.getGroupCode().compareTo(item1.getGroupCode());
				}
			});
		}
		if(productTypes != null){
			Collections.sort(productTypes, new Comparator(){
				public int compare(Object obj0, Object obj1){
					ItemGroup item0 = (ItemGroup)obj0;
					ItemGroup item1 = (ItemGroup)obj1;
					return item0.getItemGroupDescription().compareTo(item1.getItemGroupDescription());
				}
			});
		}
		if(modelTypes != null){
			Collections.sort(modelTypes, new Comparator(){
				public int compare(Object obj0, Object obj1){
					ItemGroup item0 = (ItemGroup)obj0;
					ItemGroup item1 = (ItemGroup)obj1;
					return item0.getItemGroupDescription().compareTo(item1.getItemGroupDescription());
				}
			});
		}
		buClaimTypeMap = configParamService.getValuesForAllBUs(ConfigName.CLAIM_TYPE.getName());
		for (Iterator<Entry<String, List<Object>>> iter = buClaimTypeMap.entrySet().iterator(); iter.hasNext();) {
			Entry<String, List<Object>> entry = iter.next();
			for( Object claimType : entry.getValue()) {				
					claimTypes.add(ClaimType.typeFor((String)claimType));				
			}
		}
		try {
			listOfManufacturingSite = getLovsForClass("ManufacturingSiteInventory");
		} catch (Exception ex) {
			logger
					.error("Not able to instantiate object for the class ManufacturingSiteInventory"
							+ ex.getMessage());
		}
		return SUCCESS;
	}

    private void fetchAllCurrencies() {       
        if(!getLoggedInUser().isInternalUser()){
            final Currency currency = getLoggedInUsersDealership().getPreferredCurrency();           
            allCurrencies.add(currency);
        }else{
        	allCurrencies = this.orgService.listUniqueCurrencies();
        }       
        Collections.sort(allCurrencies,new Comparator<Currency>(){
            public int compare(Currency o1, Currency o2) {
                return o1.getCurrencyCode().compareTo(o2.getCurrencyCode());
            }
        });
    }


        private void populateOperators(){
            getTotalAmountOperators().put(getText("label.operators.isLessThan"), " < ");
            getTotalAmountOperators().put(getText("label.operators.isLessThanOrEqualTo"), " <= ");
            getTotalAmountOperators().put(getText("label.operators.is"), " = ");
            getTotalAmountOperators().put(getText("label.operators.isGreaterThan"), " > ");
            getTotalAmountOperators().put(getText("label.operators.isGreaterThanOrEqualTo"), " >= ");            
        }

	public Map<String, String> getLovsForClass(String className) {
		Map<String, String> lovs = new HashMap<String, String>();
		List<ListOfValues> tempList;
        tempList = this.lovRepository.findAllActive(className);
        for (ListOfValues listOfValues : tempList) {
            lovs.put(listOfValues.getId().toString(), listOfValues
                    .getDescription());
        }
		return lovs;
	}

	@SuppressWarnings("unchecked")
	public String showPreDefinedClaimsSearchQuery() {
		this.isInternalUser = this.orgService.isInternalUser(getLoggedInUser());
		if (claimSearchCriteria == null) {
			if (notATemporaryQuery) {
				SavedQuery savedQuery = savedQueryService.findById(queryId);
				searchString = savedQuery.getSearchQuery();
				XStream xstream = new XStream(new DomDriver());
				claimSearchCriteria = (ClaimSearchCriteria) xstream
						.fromXML(searchString);
			} else {
				claimSearchCriteria = (ClaimSearchCriteria) session
						.get("claimSearchCriteria");
			}
		}
		this.preDefinedClaimsSearchFormData.populateSearchFields();
		if(!getLoggedInUser().getUserType().equals(AdminConstants.SUPPLIER_USER) && this.orgService.isDealer(getLoggedInUser())){
			String brandType=this.orgService.findDealerBrands(getLoggedInUsersOrganization());
			productTypes = claimService.findProductTypesByBrand(ItemGroup.PRODUCT,brandType);
			modelTypes = claimService.findModelTypesByBrand(ItemGroup.MODEL,brandType);
		}
		else{
		productTypes = claimService.findProductTypes(ItemGroup.PRODUCT);
		modelTypes = claimService.findModelTypes(ItemGroup.MODEL);
		}
		setProductCodes(new ArrayList<ItemGroup>(productTypes));
		if(productCodes != null){
			Collections.sort(productCodes, new Comparator(){
				public int compare(Object obj0, Object obj1){
					ItemGroup item0 = (ItemGroup)obj0;
					ItemGroup item1 = (ItemGroup)obj1;
					return item0.getGroupCode().compareTo(item1.getGroupCode());
				}
			});
		}
		if(productTypes != null){
			Collections.sort(productTypes, new Comparator(){
				public int compare(Object obj0, Object obj1){
					ItemGroup item0 = (ItemGroup)obj0;
					ItemGroup item1 = (ItemGroup)obj1;
					return item0.getItemGroupDescription().compareTo(item1.getItemGroupDescription());
				}
			});
		}
		if(modelTypes != null){
			Collections.sort(modelTypes, new Comparator(){
				public int compare(Object obj0, Object obj1){
					ItemGroup item0 = (ItemGroup)obj0;
					ItemGroup item1 = (ItemGroup)obj1;
					return item0.getItemGroupDescription().compareTo(item1.getItemGroupDescription());
				}
			});
		}
		buClaimTypeMap = configParamService.getValuesForAllBUs(ConfigName.CLAIM_TYPE.getName());
		for (Iterator<Entry<String, List<Object>>> iter = buClaimTypeMap.entrySet().iterator(); iter.hasNext();) {
			Entry<String, List<Object>> entry = iter.next();
			for( Object claimType : entry.getValue()) {				
					claimTypes.add(ClaimType.typeFor((String)claimType));				
			}
		}
		try {
			listOfManufacturingSite = getLovsForClass("ManufacturingSiteInventory");
		} catch (Exception ex) {
			logger
					.error("Not able to instantiate object for the class ManufacturingSiteInventory"
							+ ex.getMessage());
		}
                populateOperators();
                fetchAllCurrencies();
                childDealerShip = fetchChildDealerShips();
		return SUCCESS;
	}

	private void removeNewLineCharactersFromSearchString() {
		StringBuilder sb = new StringBuilder();
		if (searchString != null) {
			for (int i = 0; i < searchString.length(); i++) {
				char element = searchString.charAt(i);
				if (element != 10 && element != 13 && element != 9) {
					sb.append(element);
				}
			}
			searchString = sb.toString();
		}
	}

	private void addSortCriteria(ListCriteria criteria) {
		criteria.removeSortCriteria();
		for (String[] sort : sorts) {
			String sortOnColumn = sort[0];
			boolean ascending = !sort[1].equals(SORT_DESCENDING);
			criteria.addSortCriteria(sortOnColumn, ascending);
		}
	}

	private void addFilterCriteria(ListCriteria criteria) {
		criteria.removeFilterCriteria();
		for (String filterName : filters.keySet()) {
			String filterValue = filters.get(filterName);
			if (isBuConfigAMER() && filterName.equals("claim.clmTypeName") && filterValue.toUpperCase().startsWith("U")){
				Pattern pattern = Pattern.compile("\\b(U|UN|UNI|UNIT)\\b");
				Matcher matcher = pattern.matcher(filters.get(filterName)
				.toUpperCase());
				if (matcher.find()){
					filterValue = "machine";
					}
				}
			criteria.addFilterCriteria(filterName, filterValue);
		}
	}

	public String validateSearchFields() throws Exception {
		if (notATemporaryQuery) {
			if (StringUtils.isBlank(savedQueryName)) {
				addActionError("error.predefinedsearch.queryLabel.mandatory");
				return INPUT;
			} else if (queryId==null && savedQueryService
					.isQueryNameUniqueForUserAndContext(savedQueryName,context)) {
				addActionError("error.predefinedsearch.queryLabel.duplicate");
				return INPUT;
			}
		}
		if(claimSearchCriteria.getOfDate() != null && 
				!claimSearchCriteria.getOfDate().equalsIgnoreCase("filedOnDate")) {
			if((claimSearchCriteria.getInProgressState() == null || !claimSearchCriteria.getInProgressState()) 
					&& claimSearchCriteria.getStatesList().isEmpty()) {
				if(claimSearchCriteria.getOfDate().equalsIgnoreCase("dateLastModified"))
					addActionError("error.predefinedsearch.lastModified.status.mandatory");
				else
					addActionError("error.predefinedsearch.lastUpdated.status.mandatory");
			}
		}
                if(claimSearchCriteria.getTotalAmountOperator()!= null &&
                        ! "".equals(claimSearchCriteria.getTotalAmountOperator())){
                    if(claimSearchCriteria.getTotalAmountClaim() == null){
                        addActionError("error.predefinedsearch.totalAmountClaim.money");
                    }
                    
                }
                if(claimSearchCriteria.getTotalAmountClaim() != null){
                    if(claimSearchCriteria.getTotalAmountOperator()== null ||
                         "".equals(claimSearchCriteria.getTotalAmountOperator())){
                        addActionError("error.predefinedsearch.totalAmountClaim.operator");
                    }
                }
                if(claimSearchCriteria.getFromDate()!=null && claimSearchCriteria.getToDate()!=null
                		&& claimSearchCriteria.getFromDate().isAfter(claimSearchCriteria.getToDate())){
                	addActionError("error.partSource.invalidFromDate");
                }
                if(claimSearchCriteria.getBuildForm()!=null && claimSearchCriteria.getBuildTo()!=null
                		&& claimSearchCriteria.getBuildForm().isAfter(claimSearchCriteria.getBuildTo())){
                	addActionError("error.partSource.invalidBuildFromDate");
                }
                if(getFieldErrors() != null && !getFieldErrors().isEmpty()){
                }

                if(hasActionErrors()){
        			return INPUT;
        		}
        		else{
        			isSaveQuery= true;
        			return SUCCESS;
        		}
	}

	public String suggestOrgDealerGroups() {
		List<DealerGroup> matchingGroups = dealerGroupService
				.findGroupsForOrganisationHierarchy(searchPrefix);
		generateAndWriteComboboxJson(matchingGroups, "name");

		return null;
	}
	
	public boolean displayClaimSearchBasedOnSMR(){
		return configParamService.getBooleanValue(ConfigName.SMR_CLAIM_ALLOWED.getName());
	}
        
        
	public List<ItemGroup> listItaTruckClass() {
		return claimService.listGroupCodeBasedOnGroupType();
	}

	public ClaimSearchCriteria getClaimSearchCriteria() {
		return claimSearchCriteria;
	}

	public void setClaimSearchCriteria(ClaimSearchCriteria claimSearchCriteria) {
		this.claimSearchCriteria = claimSearchCriteria;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public void setSavedQueryService(SavedQueryService savedQueryService) {
		this.savedQueryService = savedQueryService;
	}

	public Long getQueryId() {
		return queryId;
	}

	public void setQueryId(Long queryId) {
		this.queryId = queryId;
	}

	public void setPreDefinedClaimsSearchFormData(
			PreDefinedClaimsSearchFormData preDefinedClaimsSearchFormData) {
		this.preDefinedClaimsSearchFormData = preDefinedClaimsSearchFormData;
	}

	public PreDefinedClaimsSearchFormData getPreDefinedClaimsSearchFormData() {
		return preDefinedClaimsSearchFormData;
	}

	public Map<String, String> getListOfManufacturingSite() {
		return listOfManufacturingSite;
	}

	public void setListOfManufacturingSite(
			Map<String, String> listOfManufacturingSite) {
		this.listOfManufacturingSite = listOfManufacturingSite;
	}

	public PredicateAdministrationService getPredicateAdministrationService() {
		return predicateAdministrationService;
	}

	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}

	public boolean isNotATemporaryQuery() {
		return notATemporaryQuery;
	}

	public void setNotATemporaryQuery(boolean notATemporaryQuery) {
		this.notATemporaryQuery = notATemporaryQuery;
	}

	public boolean isInternalUser() {
		return isInternalUser;
	}

	public void setInternalUser(boolean isInternalUser) {
		this.isInternalUser = isInternalUser;
	}

	public String getSavedQueryName() {
		return savedQueryName;
	}

	/** 
	* @param searchQueryName 
	* Updated for SLMSPROD-747 
	* Handled the single,double quote and other types of single quotes 
	* which can harm opening the saved query. 
	*/ 
	public void setSavedQueryName(String savedQueryName) {
		if (savedQueryName != null) {
			String updatedQueryName = savedQueryName.replaceAll(
					"\\u0022|\u0026|\u0027|\u00B4|\u2018|\u2019|\u201C|\u201D|\u201E", "");
			this.savedQueryName = updatedQueryName;
		} else {
		this.savedQueryName = savedQueryName;
	}
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public List<ItemGroup> getProductTypes() {
		return productTypes;
	}

	public void setProductTypes(List<ItemGroup> productTypes) {
		this.productTypes = productTypes;
	}

	public List<ItemGroup> getModelTypes() {
		return modelTypes;
	}

	public void setModelTypes(List<ItemGroup> modelTypes) {
		this.modelTypes = modelTypes;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
	public LovRepository getLovRepository() {
		return lovRepository;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public Map<String, List<Object>> getBuClaimTypeMap() {
		return buClaimTypeMap;
	}

	public void setBuClaimTypeMap(Map<String, List<Object>> buClaimTypeMap) {
		this.buClaimTypeMap = buClaimTypeMap;
	}

	public Set<ClaimType> getClaimTypes() {
		return claimTypes;
	}

	public void setClaimTypes(Set<ClaimType> claimTypes) {
		this.claimTypes = claimTypes;
	}

    public List<ClaimType> getSortedClaimTypes(){
        List<ClaimType> sortedClaimList = new ArrayList<ClaimType>();
        sortedClaimList.addAll(getClaimTypes());
        return sortedClaimList;
    }

    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }

    protected String getInboxViewContext() {
    	return BusinessObjectModelFactory.CLAIM_SEARCHES;
	}
    
    public boolean isAllAuditsHistoryShownToDealer(){
        return configParamService.getBooleanValue(ConfigName.ALL_AUDITS_HISTORY_SHOWN_TO_DEALER.getName());
    }
    
    public boolean isClaimStatusShownToDealer(){
        return configParamService.getBooleanValue(ConfigName.ALL_CLAIM_STATUS_SHOWN_TO_DEALER.getName());
    }
    
    public boolean isClaimAssigneeShownToDealer(){
        return configParamService.getBooleanValue(ConfigName.CLAIM_ASSIGNEE_SHOWN_TO_DEALER.getName());
    }
    
	public boolean enableWarrantyOrderClaims() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ENABLE_WARRANTY_ORDER_CLAIM.getName());
	}

	 public boolean isAllOpenStatesShownToDealer(){
	        return configParamService.getBooleanValue(ConfigName.DISPLAY_ALL_OPEN_STATES_TO_DEALER.getName());
	}
	 
    public Map<String, String> getTotalAmountOperators() {
        return totalAmountOperators;
    }

    public void setTotalAmountOperators(Map<String, String> totalAmountOperators) {
        this.totalAmountOperators = totalAmountOperators;
    }

    public List<Currency> getAllCurrencies() {
        return allCurrencies;
    }

    public void setAllCurrencies(List<Currency> allCurrencies) {
        this.allCurrencies = allCurrencies;
    }

    public List<ServiceProvider> getChildDealerShip() {
        return childDealerShip;
    }

    public void setChildDealerShip(List<ServiceProvider> childDealerShip) {
        this.childDealerShip = childDealerShip;
    }
	public List<ItemGroup> getProductCodes() {
		return productCodes;
	}

	public void setProductCodes(List<ItemGroup> productCodes) {
		this.productCodes = productCodes;
	}
}
