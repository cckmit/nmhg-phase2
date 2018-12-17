package tavant.twms.web.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.DiscountType;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.ManufacturingSiteInventory;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.inventory.ContractCode;
import tavant.twms.domain.inventory.InternalInstallType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventorySearchCriteria;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.Pagination;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionRepository;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.domain.query.SavedQuery;
import tavant.twms.domain.query.SavedQueryService;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import com.opensymphony.xwork2.Preparable;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("serial")
public class PredefinedInventorySearchAction extends SummaryTableAction implements Preparable {
	InventorySearchCriteria inventorySearchCriteria;
	private InventoryService inventoryService;
	private SavedQueryService savedQueryService;
	private PredicateAdministrationService predicateAdministrationService;
	private String searchString;
	private Long queryId;
	private int page;
	private boolean isInternalUser;
	private boolean notATemporaryQuery;
	private static Logger logger = LogManager
			.getLogger(PredefinedInventorySearchAction.class);
	private List<ListOfValues> listOfManufacturingSite;
	private String savedQueryName;
	private String context_Predefined;
	private List<ItemGroup> productTypes = new ArrayList<ItemGroup>();
	private List<ItemGroup> productCodes;
	private List<ItemGroup> modelTypes = new ArrayList<ItemGroup>();
	//private List<Option> optionsList = new ArrayList<Option>(); 
	private LovRepository lovRepository;
	private List<PolicyDefinition> listOfPolicies;
	private PolicyDefinitionRepository policyDefinitionRepository;
	private boolean isSaveQuery;
    private ItemGroupService itemGroupService;
    private boolean refreshPage;
    private WarrantyService warrantyService;
    private List<WarrantyType> warrantyTypes = new ArrayList<WarrantyType>();
    private List<ServiceProvider> childDealerShip = new ArrayList<ServiceProvider>() ;
    private Map<Object, Object> customerTypes = new HashMap<Object, Object>();
    private String brandType;   
    private List<ListOfValues> listOfDiscountType;
    private final String TERMINATED="Terminated";
    
	public String getBrandType() {
		return brandType;
	}

	public void setBrandType(String brandType) {
		this.brandType = brandType;
	}

	public boolean isRefreshPage() {
		return refreshPage;
	}

	public void setRefreshPage(boolean refreshPage) {
		this.refreshPage = refreshPage;
	}

	public String getContext_Predefined() {
		return context_Predefined;
	}

	public void prepare() throws Exception {
		if (inventorySearchCriteria == null) {
			if (notATemporaryQuery) {
				SavedQuery savedQuery = savedQueryService.findById(queryId);

				searchString = savedQuery.getSearchQuery();
				XStream xstream = new XStream(new DomDriver());
				inventorySearchCriteria = (InventorySearchCriteria) xstream
						.fromXML(searchString);
			} else {
				if (!refreshPage) {
					inventorySearchCriteria = (InventorySearchCriteria) session
					.get("inventorySearchCriteria");
				}

			}
		}
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

	public void setContext_Predefined(String context_Predefined) {
		this.context_Predefined = context_Predefined;
	}

	public LovRepository getLovRepository() {
		return lovRepository;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
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

	@SuppressWarnings("unchecked")
	@Override
	protected PageResult<?> getBody() {
		if (isLoggedInUserADealer()) {
			inventorySearchCriteria.setDealerId(getLoggedInUsersDealership()
                    .getId());
		}

        List<Long> childDealers = orgService.getChildOrganizationsIds(getLoggedInUser().getBelongsToOrganization().getId());
        if(null != getInventorySearchCriteria().getDealerId()){
            childDealers.add(getInventorySearchCriteria().getDealerId());

        }
        //setting null value to avoid appending 'Warranty.forItem' property in query 
        if("".equals(inventorySearchCriteria.getMarketingGroupCode())){
        	inventorySearchCriteria.setMarketingGroupCode(null);
        }
        /*for(Organization org: getLoggedInUser().getBelongsToOrganization().getChildOrgs()){
            childDealers.add(org.getId());
        }*/
        inventorySearchCriteria.setAllowedDealers(childDealers);

		Pagination pagination = new Pagination();
		pagination.setPageNumber(page - 1);
		pagination.setPageSize(pageSize);
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageSize(pageSize);
		pageSpecification.setPageNumber(page - 1);

		addSortCriteria(inventorySearchCriteria);
		addFilterCriteria(inventorySearchCriteria);
        return this.inventoryService.findAllItemsMatchingSearch(inventorySearchCriteria,
                pagination,pageSpecification);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<SummaryTableColumn> getHeader() {
		if (inventorySearchCriteria != null) {
			if (notATemporaryQuery && isSaveQuery) {
				saveSearchQuery();
			} else {
				session.put("inventorySearchCriteria", inventorySearchCriteria);
			}
		}
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.inventoryAction.serial_no", "serialNumber", 13,
				"string", "serialNumber", true, false, false, false));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.inventoryAction.hidden", "id", 0, "string",
				"id", false, true, true, false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.product",
				"ofType.product.groupCode", 8, "String", SummaryTableColumnOptions.NO_SORT));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.inventoryAction.item_model", "ofType.model.itemGroupDescription",
				14, "String", SummaryTableColumnOptions.NO_SORT));
		/*tableHeadData.add(new SummaryTableColumn(
				"columnTitle.inventoryAction.item_number", "ofType.number", 13,
				"Number", SummaryTableColumnOptions.NO_SORT));*/
		tableHeadData.add(new SummaryTableColumn(
				"label.common.seriesDescription", "ofType.product.itemGroupDescription", 18,
				"string", SummaryTableColumnOptions.NO_SORT));
		/*tableHeadData.add(new SummaryTableColumn(
				"label.common.description", "ofType.description",
				14, "String",SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));*/
		if(inventorySearchCriteria.getInventoryType().getType()
				.equalsIgnoreCase("STOCK")){
			tableHeadData.add(new SummaryTableColumn(
				"label.common.machineAge",
				"machineAge", 10, "String",SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
		}else{
			tableHeadData.add(new SummaryTableColumn(
					"columnTitle.common.warrantyStartDate",
					"wntyStartDate", 15, "date"));
			tableHeadData.add(new SummaryTableColumn(
					"columnTitle.common.warrantyEndDate",
					"wntyEndDate", 15, "date"));
		}

		return tableHeadData;
	}

	public void saveSearchQuery() {
		if(queryId == null) {
			if (inventorySearchCriteria != null ) {
				XStream xstream = new XStream(new DomDriver());
				searchString = xstream.toXML(inventorySearchCriteria);
				SavedQuery savedQuery = new SavedQuery();
				savedQuery.setSearchQuery(searchString);
				savedQuery.setSearchQueryName(savedQueryName);
				savedQuery.setContext(context_Predefined);
				try {
					savedQueryService.saveSearchQuery(savedQuery);
					queryId = savedQuery.getId();
				} catch (Exception e) {
					logger.error("Exception occured is", e);
					e.printStackTrace();
				}
			}
		} else {
			try {
				SavedQuery savedQuery = savedQueryService.findById(queryId);
				XStream xstream = new XStream(new DomDriver());
				searchString = xstream.toXML(inventorySearchCriteria);
				savedQuery.setSearchQuery(searchString);
				savedQuery.setContext(context_Predefined);
				savedQuery.setSearchQueryName(savedQueryName);
				savedQueryService.update(savedQuery);

			} catch (Exception e) {
				logger.error("Exception occured is", e);
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public String searchExpression() {
		this.isInternalUser = this.orgService.isInternalUser(getLoggedInUser());
		if(!getLoggedInUser().getUserType().equals(AdminConstants.SUPPLIER_USER) && this.orgService.isDealer(getLoggedInUser())){
			brandType = this.orgService.findDealerBrands(getLoggedInUsersOrganization());
			productTypes = itemGroupService.findItemGroupForNameAndTypeAndBrand("",ItemGroup.PRODUCT,brandType);
			//optionsList = itemGroupService.findOptionsList(brandType);
			modelTypes = itemGroupService.findModelForNameAndTypeAndBrand("",ItemGroup.MODEL,brandType);
		}
		else{
			productTypes = itemGroupService.findItemGroupForNameAndType("",ItemGroup.PRODUCT);
			//optionsList = itemGroupService.findOptionsList();
			modelTypes = itemGroupService.findItemGroupForNameAndType("",ItemGroup.MODEL);
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
		try {
			listOfManufacturingSite = this.lovRepository.findAll("ManufacturingSiteInventory");
			sortManufacturingSiteByBuAppendedName();

		} catch (Exception ex) {
			logger
					.error("Not able to instantiate object for the class ManufacturingSiteInventory"
							+ ex.getMessage());
		}
		
		try {
			listOfDiscountType = this.lovRepository.findAll("DiscountType");
			sortDiscountType();

		} catch (Exception ex) {
			logger.error("Not able to instantiate object for the class DiscountType"
					+ ex.getMessage());
		}
		
		fetchListOfPolicies();
		listOfCoverageTypes();
        childDealerShip = fetchChildDealerShips();
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	private void sortManufacturingSiteByBuAppendedName(){
		if(listOfManufacturingSite != null){
			Collections.sort(listOfManufacturingSite,new Comparator(){
				public int compare(Object obj0, Object obj1) {
					ManufacturingSiteInventory manufSite0 =(ManufacturingSiteInventory) obj0;
					ManufacturingSiteInventory manufSite1 =(ManufacturingSiteInventory) obj1;
					return manufSite0.getBuAppendedName().compareTo(manufSite1.getBuAppendedName());
				}

			});
		}
	}
	
	@SuppressWarnings("unchecked")
	private void sortDiscountType(){
		if(listOfDiscountType != null){
			Collections.sort(listOfDiscountType,new Comparator(){
				public int compare(Object obj0, Object obj1) {
					DiscountType discountType0 =(DiscountType) obj0;
					DiscountType discountType1 =(DiscountType) obj1;
					return discountType0.getDescription().compareTo(discountType1.getDescription());
				}

			});
		}
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

	public Map<Boolean, String> getYesNo() {
		Map<Boolean, String> yesNo = new HashMap<Boolean, String>();
		yesNo.put(true, getText("yes"));
		yesNo.put(false, getText("no"));
		return yesNo;
	}

	public Map<String, String> getConditions() {
		Map<String, String> conditions = new HashMap<String, String>();
		conditions.put(InventoryItemCondition.NEW.getItemCondition(), "New");
		conditions.put(InventoryItemCondition.REFURBISHED.getItemCondition(),
				"Refurbished");
		conditions
				.put(InventoryItemCondition.SCRAP.getItemCondition(), "Scrap");
		conditions.put(InventoryItemCondition.STOLEN.getItemCondition(), "Stolen");
		return conditions;

	}

	private void fetchListOfPolicies() {
		try {
			listOfPolicies = this.policyDefinitionRepository.findAll();
	/*		if(listOfPolicies != null){
				for(PolicyDefinition policyDefinition : listOfPolicies){
					policyDefinition.setDescription(policyDefinition.getBusinessUnitInfo()+" - "+
							policyDefinition.getDescription());
				}
				Collections.sort(listOfPolicies,new Comparator(){
					public int compare(Object obj0, Object obj1) {
						PolicyDefinition policyDefinition0 =(PolicyDefinition) obj0;
						PolicyDefinition policyDefinition1 =(PolicyDefinition) obj1;
						return policyDefinition0.getDescription().compareTo(policyDefinition1.getDescription());
					}

				});
			}*/
		} catch (Exception ex) {
			logger
					.error("Not able to fetch the list of policies"
							+ ex.getMessage());
		}
	}
	public void listOfCoverageTypes()
	{
            setWarrantyTypes(this.warrantyService.listWarrantyTypes());
	}
	@SuppressWarnings("unchecked")
	public String showPreDefinedInventoryQuery() {
		if (inventorySearchCriteria == null) {
			if (notATemporaryQuery) {
				SavedQuery savedQuery = savedQueryService.findById(queryId);
				searchString = savedQuery.getSearchQuery();
				XStream xstream = new XStream(new DomDriver());
				inventorySearchCriteria = (InventorySearchCriteria) xstream
						.fromXML(searchString);
			} else {
				inventorySearchCriteria = (InventorySearchCriteria) session
						.get("inventorySearchCriteria");
			}
		}
		try {
			//Fix for NMHGSLMS-1209
			listOfDiscountType = this.lovRepository.findAll("DiscountType");
			listOfManufacturingSite = this.lovRepository.findAll("ManufacturingSiteInventory");
			sortManufacturingSiteByBuAppendedName();

		} catch (Exception ex) {
			logger
					.error("Not able to instantiate object for the class ManufacturingSiteInventory"
							+ ex.getMessage());
		}
		fetchListOfPolicies();
		listOfCoverageTypes();
        childDealerShip = fetchChildDealerShips();
		this.isInternalUser = this.orgService.isInternalUser(getLoggedInUser());
		if(!getLoggedInUser().getUserType().equals(AdminConstants.SUPPLIER_USER) && this.orgService.isDealer(getLoggedInUser())){
			brandType = this.orgService.findDealerBrands(getLoggedInUsersOrganization());
			productTypes = itemGroupService.findItemGroupForNameAndTypeAndBrand("",ItemGroup.PRODUCT,brandType);
			//optionsList = itemGroupService.findOptionsList(brandType);
			modelTypes = itemGroupService.findModelForNameAndTypeAndBrand("",ItemGroup.MODEL,brandType);
		}
		else{
			productTypes = itemGroupService.findItemGroupForNameAndType("",ItemGroup.PRODUCT);
			//optionsList = itemGroupService.findOptionsList();
			modelTypes = itemGroupService.findItemGroupForNameAndType("",ItemGroup.MODEL);
		}
		//optionsList = itemGroupService.findOptionsList();
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
		if (inventorySearchCriteria.getInventoryType().getType()
				.equalsIgnoreCase("STOCK")) {
			return "stock";
		} else {
			return "retail";
		}
	}

	@SuppressWarnings( { "unused", "static-access" })
	private void addFilterCriteria(InventorySearchCriteria criteria) {
		criteria.removeFilterCriteria();
		for (Iterator<String> iter = this.filters.keySet().iterator(); iter
				.hasNext();) {

			String filterName = iter.next();
			//'item' is an alias used in search query
			//In order by clause, somehow hibernate is not able to recognize without this alias
			String filterValue = this.filters.get(filterName).toUpperCase();
			 if(filterName.contains("itemGroupDescription")){
             	filterName=filterName.replace("itemGroupDescription","description");
             }
			filterName = "item." + filterName;
			if (this.logger.isInfoEnabled()) {
				this.logger.info("Adding filter criteria " + filterName + " : "
						+ filterValue);
			}
			criteria.addFilterCriteria(filterName, filterValue);
		}
	}

	@SuppressWarnings( { "unused", "static-access" })
	private void addSortCriteria(InventorySearchCriteria criteria) {
		criteria.removeSortCriteria();
		for (Iterator<String[]> iter = this.sorts.iterator(); iter.hasNext();) {
			String[] sort = iter.next();
			String sortOnColumn = sort[0];

			//'item' is an alias used in search query
			//In order by clause, somehow hibernate is not able to recognize without this alias
			sortOnColumn = "item." + sortOnColumn;
			boolean ascending = sort[1].equals(SORT_DESCENDING) ? false : true;
			if (this.logger.isInfoEnabled()) {
				this.logger.info("Adding sort criteria " + sortOnColumn + " "
						+ (ascending ? "ascending" : "descending"));
			}
			criteria.addSortCriteria(sortOnColumn, ascending);
		}
	}

	public String validateSearchFields() throws Exception {
		if (notATemporaryQuery) {
			if (StringUtils.isBlank(savedQueryName)) {
				addActionError("error.predefinedsearch.queryLabel.mandatory");	
			} else if (savedQueryService
					.isQueryNameUniqueForUserAndContext(savedQueryName,context_Predefined)) {
				//Fix for NMHGSLMS-992
				if(null == queryId){
					addActionError("error.predefinedsearch.queryLabel.duplicate");	
				}				
			}
		}
		if(inventorySearchCriteria != null && inventorySearchCriteria.getPolicies() !=null
				&& inventorySearchCriteria.getPolicies().length> 0){
			if(inventorySearchCriteria.getPolicyFromDate()==null
					|| inventorySearchCriteria.getPolicyToDate()==null){
				addActionError("error.predefinedSearch.planDateRange");
			}
		}
		if(inventorySearchCriteria != null){
			if(inventorySearchCriteria.getBuildFromDate()!=null && inventorySearchCriteria.getBuildToDate()!=null
	        		&& inventorySearchCriteria.getBuildFromDate().isAfter(inventorySearchCriteria.getBuildToDate())){
				addActionError("error.partSource.invalidBuildFromDate");
			}
			if(inventorySearchCriteria.getFromDate()!=null && inventorySearchCriteria.getToDate()!=null
	        		&& inventorySearchCriteria.getFromDate().isAfter(inventorySearchCriteria.getToDate())){
				addActionError("error.partSource.invalidShipmentFromDate");
			}
			if(inventorySearchCriteria.getDeliveryFromDate()!=null && inventorySearchCriteria.getDeliveryToDate()!=null
	        		&& inventorySearchCriteria.getDeliveryFromDate().isAfter(inventorySearchCriteria.getDeliveryToDate())){
				addActionError("error.partSource.invalidDeliveryFromDate");
			}
			if(inventorySearchCriteria.getSubmitFromDate()!=null && inventorySearchCriteria.getSubmitToDate()!=null
	        		&& inventorySearchCriteria.getSubmitFromDate().isAfter(inventorySearchCriteria.getSubmitToDate())){
				addActionError("error.partSource.invalidSubmitFromDate");
			}
			
			if(inventorySearchCriteria.getPolicyFromDate()!=null && inventorySearchCriteria.getPolicyToDate()!=null
	        		&& inventorySearchCriteria.getPolicyFromDate().isAfter(inventorySearchCriteria.getPolicyToDate())){
				addActionError("error.partSource.invalidPolicyFromDate");
			}
		}
		if(hasActionErrors()){
			return INPUT;
		}
		else{
			isSaveQuery= true;
			return SUCCESS;
		}
	}


	public InventorySearchCriteria getInventorySearchCriteria() {
		return inventorySearchCriteria;
	}

	public void setInventorySearchCriteria(
			InventorySearchCriteria inventorySearchCriteria) {
		this.inventorySearchCriteria = inventorySearchCriteria;
	}

	public void setSavedQueryService(SavedQueryService savedQueryService) {
		this.savedQueryService = savedQueryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public Long getQueryId() {
		return queryId;
	}

	public void setQueryId(Long queryId) {
		this.queryId = queryId;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public boolean isInternalUser() {
		return isInternalUser;
	}

	public PredicateAdministrationService getPredicateAdministrationService() {
		return predicateAdministrationService;
	}

	public void setInternalUser(boolean isInternalUser) {
		this.isInternalUser = isInternalUser;
	}

	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}


	public List<ListOfValues> getListOfManufacturingSite() {
		return listOfManufacturingSite;
	}

	public void setListOfManufacturingSite(
			List<ListOfValues> listOfManufacturingSite) {
		this.listOfManufacturingSite = listOfManufacturingSite;
	}

	public boolean isNotATemporaryQuery() {
		return notATemporaryQuery;
	}

	public void setNotATemporaryQuery(boolean notATemporaryQuery) {
		this.notATemporaryQuery = notATemporaryQuery;
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

	public boolean isBuildDateVisible() {
        return
                ((!isLoggedInUserAnInternalUser() && isVisibleAcrossAnyBu(ConfigName.BUILD_DATE_VISIBLE.getName()))
                || isLoggedInUserAnInternalUser());
	}

	public boolean isManufacturingSiteVisible() {
		return ((!isLoggedInUserAnInternalUser() && isVisibleAcrossAnyBu(ConfigName.MANUFACTURING_SITE_VISIBLE.getName()))
                || isLoggedInUserAnInternalUser());
	}

    protected boolean isVisibleAcrossAnyBu(String configName){
        boolean isVisible = false;
        Map<String, List<Object>> buValues = getConfigParamService().getValuesForAllBUs(configName);
        for (String buName : buValues.keySet()) {
              Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
              if(booleanValue){
                 isVisible=true;
                 break;
              }
        }
        return isVisible;
    }

	public List<PolicyDefinition> getListOfPolicies() {
		return listOfPolicies;
	}

	public void setListOfPolicies(List<PolicyDefinition> listOfPolicies) {
		this.listOfPolicies = listOfPolicies;
	}

	public PolicyDefinitionRepository getPolicyDefinitionRepository() {
		return policyDefinitionRepository;
	}

	public void setPolicyDefinitionRepository(
			PolicyDefinitionRepository policyDefinitionRepository) {
		this.policyDefinitionRepository = policyDefinitionRepository;
	}


    public ItemGroupService getItemGroupService() {
        return itemGroupService;
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }
	public boolean isSaveQuery() {
		return isSaveQuery;
	}

	public void setSaveQuery(boolean isSaveQuery) {
		this.isSaveQuery = isSaveQuery;
	}

    public boolean isEligibleForExtendedWarrantyPurchase(){
            boolean isEligible = false;
            Map<String, List<Object>> buValues = getConfigParamService().
                    getValuesForAllBUs(ConfigName.CAN_EXTERNAL_USER_PURCHASE_EXTENDED_WARRANTY.getName());
            for (String buName : buValues.keySet()) {
                Boolean booleanValue = new Boolean(buValues.get(buName).get(0).toString());
                if (booleanValue) {
                    isEligible = true;
                    break;
                }
            }
            return isEligible;
        }

    public boolean isDealerEligibleToPerformRMT() {
        boolean isEligible = false;
        if (isLoggedInUserADealer()) {
            Map<String, List<Object>> buValues = getConfigParamService().
                    getValuesForAllBUs(ConfigName.CAN_DEALER_PERFORM_RMT.getName());
            for (String buName : buValues.keySet()) {
                Boolean booleanValue = new Boolean(buValues.get(buName).get(0).toString());
                if (booleanValue) {
                    isEligible = true;
                    break;
                }
            }
        }
        return isEligible;
    }
 
    public List<ItemGroup> listItaTruckClass(){
    	return itemGroupService.listGroupCodeBasedOnGroupType();
    }
    
	public Boolean getWntyRegAllowed() {
		//if (this.inventoryItem != null) {
			if (this.orgService.isInternalUser(getLoggedInUser())) {
				if (!this.orgService.doesUserHaveRole(getLoggedInUser(),"inventoryAdmin"))
					return new Boolean(false);
			}
		//}
		return new Boolean(true);
	}


	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}


	public List<WarrantyType> getWarrantyTypes() {
		return warrantyTypes;
	}

	public void setWarrantyTypes(List<WarrantyType> warrantyTypes) {
		this.warrantyTypes = warrantyTypes;
	}

	public boolean isStockClaimAllowed(){
		    	return getConfigParamService()
		                .getBooleanValue(ConfigName.STOCK_CLAIM_ALLOWED
		                                .getName());
		    }

	public boolean isD2DAllowed(){
    	return getConfigParamService()
                .getBooleanValue(ConfigName.D2D_ALLOWED
                                .getName());
    }
	
	
    public List<ServiceProvider> getChildDealerShip() {
        return childDealerShip;
    }

    public void setChildDealerShip(List<ServiceProvider> childDealerShip) {
        this.childDealerShip = childDealerShip;
    }
    
    public Map<Object, Object> getCustomerTypes() {
        customerTypes = getConfigParamService().getKeyValuePairOfObjects(ConfigName.
    			CUSTOMERS_FILING_DR.getName());
    	/*Map.Entry<Object, Object> customerType;
    	for(Iterator<Map.Entry<Object, Object>> entry = customerTypes.entrySet().iterator(); entry.hasNext();){
    		customerType = entry.next();
    		if(customerType.getValue().equals("Demo")){
    			entry.remove();
    			break;
    		}
    	}*/
    	return customerTypes;
	}
    
    public List<InternalInstallType> listInternalInstallTypes(){
    	return warrantyService.listInternalInstallType();
    }
    
    public List<ContractCode> listContractCodes(){
    	return warrantyService.listContractCode();
    }

	public void setCustomerTypes(Map<Object, Object> customerTypes) {
		this.customerTypes = customerTypes;
	}

	public List<ItemGroup> getProductCodes() {
		return productCodes;
	}

	public void setProductCodes(List<ItemGroup> productCodes) {
		this.productCodes = productCodes;
	}

	/*public List<Option> getOptionsList() {
		return optionsList;
	}

	public void setOptionsList(List<Option> optionsList) {
		this.optionsList = optionsList;
	}*/

  
    
	public boolean displayInternalInstallType() {
		return getConfigParamService().getBooleanValue(
				ConfigName.DISPLAY_INTERNAL_INSTALL_TYPE.getName());
	}

	public List<ListOfValues> getListOfDiscountType() {
		return listOfDiscountType;
	}

	public void setListOfDiscountType(List<ListOfValues> listOfDiscountType) {
		this.listOfDiscountType = listOfDiscountType;
	}
	
}