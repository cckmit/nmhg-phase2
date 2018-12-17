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

package tavant.twms.domain.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.ClaimRepository;
import tavant.twms.domain.claim.MultiInventorySearch;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.OwnershipState;
import tavant.twms.domain.policy.OwnershipStateRepository;
import tavant.twms.domain.query.HibernateQuery;
import tavant.twms.domain.query.HibernateQueryGenerator;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;
import tavant.twms.infra.TypedQueryParameter;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.time.CalendarDate;

/**
 * @author kamal.govindraj
 * 
 */
public class InventoryServiceImpl implements InventoryService {

	private InventoryItemRepository inventoryItemRepository;
	
	private PredicateAdministrationService predicateAdministrationService;

	private OwnershipStateRepository ownershipStateRepository;

	private SecurityHelper securityHelper;

	private ClaimRepository claimRepository;

	private ConfigParamService configParamService;
	
	private InventoryItemUtil inventoryItemUtil;
	
	protected LovRepository lovRepository;
	
	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public InventoryItemUtil getInventoryItemUtil() {
		return inventoryItemUtil;
	}
	public void setInventoryItemUtil(InventoryItemUtil inventoryItemUtil) {
		this.inventoryItemUtil = inventoryItemUtil;
	}
	public void createInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItemRepository.save(inventoryItem);
	}
	public void createInventoryItems(List<InventoryItem> inventoryItems) {
		this.inventoryItemRepository.saveAll(inventoryItems);
	}
	public void deleteInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItemRepository.delete(inventoryItem);
	}
	public void deleteInventoryItems(List<InventoryItem> inventoryItems) {
		this.inventoryItemRepository.deleteAll(inventoryItems);
	}
	
	public void updateInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItemRepository.update(inventoryItem);
	}
	public void updateInventoryItems(List<InventoryItem> inventoryItems) {
		this.inventoryItemRepository.updateAll(inventoryItems);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tavant.twms.domain.inventory.ItemService#findSerializedItem(java.lang
	 * .String)
	 */
	public InventoryItem findSerializedItem(String id)
			throws ItemNotFoundException {
		return this.inventoryItemRepository.findSerializedItem(id);
	}
	
	public InventoryItem findInvItemByIdWithoutInactiveFilter(String id)
			throws ItemNotFoundException {
		return this.inventoryItemRepository.findInvItemByIdWithoutInactiveFilter(id);
	}
	
	public InventoryItem findMachine(String serialNumber) throws ItemNotFoundException {
		return this.inventoryItemRepository.findMachine(serialNumber);
	}

	public InventoryItem findSerializedItem(String serialNumber , String modelNumber)
			throws ItemNotFoundException {
			return this.inventoryItemRepository.findSerializedItem(serialNumber , modelNumber);
	}
	
	public InventoryItem findSerializedItemWithOutActiveFilter(String serialNumber,String sequencNumber,InventoryType inventoryType)
	throws ItemNotFoundException {
	return this.inventoryItemRepository.findSerializedItemWithOutActiveFilter(serialNumber ,sequencNumber,inventoryType);
}
	
	public InventoryItem findMachine(String serialNumber, String modelNumber) throws ItemNotFoundException {
		return this.inventoryItemRepository.findMachine(serialNumber, modelNumber);
	}
	
	public InventoryItem findSerializedItemByConNumAndModel(String containerNumber, String modelName)
			throws ItemNotFoundException {
		return this.inventoryItemRepository.findSerializedItemByConNumAndModel(containerNumber, modelName);
	}
	
	public InventoryItem findInventoryBySerialNumberAndType(String serialNumber, InventoryType inventoryType)throws ItemNotFoundException {
			return this.inventoryItemRepository.findInventoryBySerialNumberAndType(serialNumber, inventoryType);
	}
	
	public List<String> findAllSerialNumbersStartingWith(
			String partialSerialNumber, int pageNumber, int pageSize) {
		return this.inventoryItemRepository.findAllSerialNumbersStartingWith(
				partialSerialNumber, pageNumber, pageSize);
	}

	public List<InventoryItem> findInventoryItemsWhoseSerialNumbersStartWith(
			String partialSerialNumber,String itemType, int pageNumber, int pageSize) {
		return this.inventoryItemRepository
				.findInventoryItemsWhoseSerialNumbersStartWith(
						partialSerialNumber, itemType, pageNumber, pageSize);
	}
	
	public List<InventoryItem> findNationalAccountInventoryItemsWhoseSerialNumbersStartWith(
			String partialSerialNumber,String claimType, int pageNumber, int pageSize, Long orgId) {
		return this.inventoryItemRepository
				.findNationalAccountInventoryItemsWhoseSerialNumbersStartWith(
						partialSerialNumber, claimType, pageNumber, pageSize,orgId);
	}
	
	public List<InventoryItem> findPartsWhoseSerialNumbersStartWith(
			String partialSerialNumber, int pageNumber, int pageSize) {
		return this.inventoryItemRepository
				.findPartsWhoseSerialNumbersStartWith(
						partialSerialNumber, pageNumber, pageSize);
	}

	public PageResult<InventoryItem> findAllInventoryItemsForDealer(
			ServiceProvider dealer, PageSpecification pageSpecification) {
		return this.inventoryItemRepository.findAllInventoryItemsForDealer(
				dealer, pageSpecification);
	}
	

	public List<InventoryItem> findAllInventoriesByTypeStartingWith(
			String partialSerialNumber,Long currentOwnerId,String type, int pageNumber, int pageSize) {
            return this.inventoryItemRepository.findAllInventoriesByTypeStartingWith(
				partialSerialNumber,currentOwnerId,type,pageNumber, pageSize);
	}

	public PageResult<InventoryItem> findAllInventoryItemsOfTypeForDealer(
			InventoryListCriteria inventoryListCriteria) {
            return this.inventoryItemRepository
                    .findAllInventoryItemsOfTypeForDealer(inventoryListCriteria,securityHelper.getLoggedInUser());
    }

	public InventoryItem findInventoryItem(Long id) {
		return this.inventoryItemRepository.findInventoryItem(id);
	}

	public List<InventoryItem> findAllItemsMatchingCriteria(
            InventorySearchCriteria inventorySearchCriteria,
            Pagination pagination, PageSpecification pageSpecification) {
		return this.inventoryItemRepository.findAllItemsMatchingCriteria(
				inventorySearchCriteria, pagination,pageSpecification);
	}

	public PageResult<InventoryItem> findAllItemsMatchingQuery(
			Long domainPredicateId, ListCriteria listCriteria,
			Organization organization) {
		DomainPredicate predicate = this.predicateAdministrationService
				.findById(domainPredicateId);
		HibernateQueryGenerator generator = new HibernateQueryGenerator(
				BusinessObjectModelFactory.INVENTORY_SEARCHES);
		generator.visit(predicate);
		HibernateQuery query = generator.getHibernateQuery();
		PageSpecification pageSpecification = listCriteria
				.getPageSpecification();
		String queryWithoutSelect = query.getQueryWithoutSelect();
		queryWithoutSelect = queryWithoutSelect + " and ( inventoryItem.serializedPart = 0 ) ";
		boolean isInternalUser = new SecurityHelper().getLoggedInUser().isInternalUser();
		if(!isInternalUser){
			queryWithoutSelect = queryWithoutSelect + " and inventoryItem.conditionType not in ("
					+ "'" + InventoryItemCondition.SCRAP.getItemCondition() + "'"
					+ ") ";
		}
		if (organization instanceof ServiceProvider) {
			queryWithoutSelect = queryWithoutSelect
					+ prepareInvSearchQueryForDealer(organization);
		}
		if (listCriteria.isFilterCriteriaSpecified()) {
			queryWithoutSelect = queryWithoutSelect + " and ("
					+ listCriteria.getParamterizedFilterCriteria() + " )";
		}
		if(query.getQueryWithoutSelect().contains("inventoryItem.latestWarranty.transactionType.trnxTypeKey")){
        	for(TypedQueryParameter parameter:query.getParameters()){
        		if(parameter.getValue()!= null && (parameter.getValue().toString().toUpperCase().contains("TTR"))){
        			parameter.setValue(parameter.getValue().toString().replace("TTR", "ETR"));
        		}
        	}       
        }
		if(query.getQueryWithoutSelect().contains("inventoryItem.latestWarranty.transactionType.trnxTypeKey")){
        	for(TypedQueryParameter parameter:query.getParameters()){
        		if(parameter.getValue()!= null && (parameter.getValue().toString().toUpperCase().startsWith("T"))){
        			if(parameter.getValue().toString().toUpperCase().equals("TT%")){
        				parameter.setValue(parameter.getValue().toString().replace("TT", "ETR"));
        			} else {
        				parameter.setValue(parameter.getValue().toString().replace("T", "ETR"));
        			}
        		}
        	}       
        }

		queryWithoutSelect = queryWithoutSelect.replaceAll("USER_LOCALE",
				securityHelper.getLoggedInUser().getLocale().toString());

		QueryParameters params = new QueryParameters(query.getParameters(),
				listCriteria.getTypedParameterMap());
		return this.inventoryItemRepository
				.findInventoryItemsUsingDynamicQuery(queryWithoutSelect,
						listCriteria.getSortCriteriaString(), query
								.getSelectClause(), pageSpecification, params);
	}

	public List<InventoryItemCondition> listInventoryItemConditionTypes() {
		return this.inventoryItemRepository.listInventoryItemConditionTypes();
	}

	public PageResult<InventoryItem> findAllInventoryItemsForMultiClaim(
			MultiInventorySearch multiInventorySearch, Long dealerId,
			ListCriteria listCriteria) {
		return this.inventoryItemRepository.findAllInventoryItemsForMultiClaim(
				multiInventorySearch, dealerId, listCriteria);
	}

	public PageResult<InventoryItem> findAllInventoryItemsForCampaignMultiClaim(
			final MultiInventorySearch multiInventorySearch,
			final Long dealerId, final ListCriteria listCriteria,
			final String campaignCode) {
		return inventoryItemRepository
				.findAllInventoryItemsForCampaignMultiClaim(
						multiInventorySearch, dealerId, listCriteria,
						campaignCode);
	}

	public OwnershipState findOwnershipStateByName(String name) {
		return this.ownershipStateRepository.findOwnershipStateByName(name);
	}

	/**
	 * @param itemRespository
	 *            the itemRespository to set
	 */
	public void setInventoryItemRepository(
			InventoryItemRepository itemRespository) {
		this.inventoryItemRepository = itemRespository;
	}

	
	public PredicateAdministrationService getPredicateAdministrationService() {
		return this.predicateAdministrationService;
	}

	@Required
	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}

	public OwnershipStateRepository getOwnershipStateRepository() {
		return this.ownershipStateRepository;
	}

	public void setOwnershipStateRepository(
			OwnershipStateRepository ownershipStateRepository) {
		this.ownershipStateRepository = ownershipStateRepository;
	}

	public List<InventoryItem> findInventoryItemsForSerialNumbers(
			String[] serialNumbers) {
		return this.inventoryItemRepository
				.findInventoryItemsForSerialNumbers(serialNumbers);
	}
	
	public List<InventoryItem> findInventoryItemsForIds(
			List<Long> inventoryIds) {
		return this.inventoryItemRepository
				.findInventoryItemsForIds(inventoryIds);
	}

	private String prepareInvSearchQueryForDealer(Organization organization) {
		String currentOwnerClause, shipToClause;
		currentOwnerClause = " and ( currentOwner.id in ( ";
		currentOwnerClause += organization.getId();
		
		shipToClause = " or shipTo.id in ( ";
		shipToClause += organization.getId();
		for(Long org : inventoryItemRepository.getChildOrganizationIds(organization.getId())){
			currentOwnerClause += ", " + org;
			shipToClause += ", " + org;
		}
		currentOwnerClause += " ) ";
		shipToClause += " ) ";
		return currentOwnerClause + shipToClause + " ) ";
	}

	public List<InventoryItem> findAllInventoryItemsByLabel(
			final String labelName) {
		return inventoryItemRepository.findAllInventoryItemsByLabel(labelName);
	}

	public PageResult<?> findAllInventoryLabels(final ListCriteria listCriteria) {
		return inventoryItemRepository.findAllInventoryLabels(listCriteria);
	}

	public ClaimRepository getClaimRepository() {
		return claimRepository;
	}

	public void setClaimRepository(ClaimRepository claimRepository) {
		this.claimRepository = claimRepository;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public boolean customersBelongsToConfigParam(InventoryItem item) {
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(item.getBusinessUnitInfo().getName());
		Map<Object, Object> keyValueOfCustomerTypes = this.configParamService
				.getKeyValuePairOfObjects(ConfigName.WARRANTY_CONFIG_CUSTOMER_TYPES_ALLOWED_IN_QUICK_SEARCH
						.getName());
		return isInvItemOwnedByAllowedCustomerType(item, keyValueOfCustomerTypes);		
	}
	
	public boolean isInvItemOwnedByAllowedCustomerType(InventoryItem item, Map<Object, Object> keyValueOfCustomerTypes) {		
		
		Party itemOwner = inventoryItemUtil.getInventoryItemOwner(item);
		if (keyValueOfCustomerTypes.toString().contains("DirectCustomer")) {			
			if (itemOwner.isDirectCustomer()) {
				return true;
			}
			
		}
		if (keyValueOfCustomerTypes.toString().contains("InterCompany")) {		
			if (itemOwner.isInterCompany()) {
				return true;
			}
			
		}
		if (keyValueOfCustomerTypes.toString().contains("NationalAccount")) {			
			if (itemOwner.isNationalAccount()) {
				return true;
			}
			
		}
		if (keyValueOfCustomerTypes.toString().equals("Dealer")) {			
			if (itemOwner.isDealer()) {
				return true;
			}
			
		}
		if (keyValueOfCustomerTypes.toString().contains("OEM")) {			
			if (itemOwner.isOriginalEquipManufacturer()) {
				return true;
			}			
		}
		return false;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public InventoryType findInventoryTypeByType(String type) {
		return this.inventoryItemRepository.findInventoryTypeByType(type);
	}
	
	public List<InventoryItem> findItemBySerialNumber(String serialNumber)
			throws ItemNotFoundException {
		return this.inventoryItemRepository.findItemBySerialNumber(serialNumber);
	}
	
	public InventoryItem findItemBySerialNumberAndProduct(String serialNumber, String productCode)	throws ItemNotFoundException{
		return this.inventoryItemRepository.findItemBySerialNumberAndProduct(serialNumber, productCode);
	}
	public InventoryItem findItemBySerialNumberAndModelNumber(String serialNumber, Item ofType)
			throws ItemNotFoundException {
		return this.inventoryItemRepository.findItemBySerialNumberAndModelNumber(serialNumber,ofType);
	}
	
	public InventoryItem findItemBySerialNumberAndModelNumberAndType(String serialNumber, Item ofType,InventoryType inventoryType)
			throws ItemNotFoundException {
		return this.inventoryItemRepository.findItemBySerialNumberAndModelNumberAndType(serialNumber, ofType, inventoryType);
	}
	public InventoryItem findInventoryItemBySerialNumber(String serialNumber)
			throws ItemNotFoundException {
		return this.inventoryItemRepository.findInventoryItemBySerialNumber(serialNumber);
	}
	
	public InventoryItem findInventoryItemByContainerNumber(final String containerNumber) throws ItemNotFoundException{
		return this.inventoryItemRepository.findInventoryItemByContainerNumber(containerNumber);
	}

    public InventoryItemCondition findInventoryItemConditionByType(String itemCondition){
        return this.inventoryItemRepository.findInventoryItemConditionByType(itemCondition);
    }

    public PageResult<?> findAllItemsMatchingSearch(
			InventorySearchCriteria inventorySearchCriteria,
			Pagination pagination,PageSpecification pageSpecification){
        List<InventoryItem> items = this.inventoryItemRepository.findAllItemsMatchingCriteria(
				inventorySearchCriteria, pagination, pageSpecification);
        PageResult<?> pageResult = new PageResult(items, pageSpecification,
				pagination.getNoOfPages());
        return pageResult;
    }
    
    public InventoryItem findItemBySerialNumberAndItemNumber(final String serialNumber, final String itemNumber)
    throws ItemNotFoundException {
        return this.inventoryItemRepository.findItemBySerialNumberAndItemNumber(serialNumber, itemNumber);
    }
    
  public List<InventoryItem> findAllSerializedSerialNumbersStartingWith(
			String partialSerialNumber,InventoryItemSource source, int pageNumber, int pageSize) {
		return this.inventoryItemRepository
				.findAllSerializedSerialNumbersStartingWith(partialSerialNumber,source,
						pageNumber, pageSize);
	}
    
    public List<InventoryItem> findInventoryItemCompositionForInvItem(
			final String partialSerialNumber, final Long inventoryItemId){
    	return this.inventoryItemRepository.findInventoryItemCompositionForInvItem(partialSerialNumber, inventoryItemId);
    }
    
    public List<InventoryItem> findInvItemCompositionForInvItem(final Long inventoryItemId) {
    	return this.inventoryItemRepository.findInvItemCompositionForInvItem(inventoryItemId);
    }
    public InventoryItem findSerializedPartBySerialNumber (
			final String partialSerialNumber,final InventoryItemSource source)  throws ItemNotFoundException {
    	return this.inventoryItemRepository.findSerializedPartBySerialNumber(partialSerialNumber, source);
    }

    public boolean areInventoriesPresentforThisServiceProvider(String serviceProvider) {
		return this.inventoryItemRepository.areInventoriesPresentforThisServiceProvider(serviceProvider);
	}

    public Boolean doesInvItemExistWithSNAndItem(final String serialNumber,final Item item){
    	return this.inventoryItemRepository.doesInvItemExistWithSNAndItem(serialNumber, item);    	
    }
	public List<InventoryItem> findAllRetailMachinesBySerialNumber(
			String partialSerialNumber, int pageNumber, int pageSize) {
		return this.inventoryItemRepository.findAllRetailMachinesBySerialNumber(partialSerialNumber, pageNumber, pageSize);
	}
	
	public void createSerializedPartsForInventories(List<InventoryItem> inventoryItems) {
		for (InventoryItem inventoryItem : inventoryItems) {
			List<InventoryItemComposition> parts = inventoryItem.getComposedOf();
			for (InventoryItemComposition composition : parts) {
				InventoryItem part = composition.getPart();
				part.setOwnershipState(inventoryItem.getOwnershipState());
				part.setType(InventoryType.RETAIL);
				part.setConditionType(InventoryItemCondition.NEW);
				part.setCurrentOwner(inventoryItem.getCurrentOwner());
				part.setLatestBuyer(inventoryItem.getLatestBuyer());
				part.setBusinessUnitInfo(inventoryItem.getBusinessUnitInfo());
				part.setDeliveryDate(inventoryItem.getDeliveryDate());
				part.setSerializedPart(true);			
				composition.setPartOf(inventoryItem);
				if (part.getSource() == null) {
					part.setSource(InventoryItemSource.UNITREGISTRATION);
				}
				this.createInventoryItem(part);
			}
		}
		this.updateInventoryItems(inventoryItems);
	}
	
	public void createMajorCompAndUpdateInvItemBOM(InventoryItem inventoryItem, InventoryItem part) {		
		part.setType(InventoryType.RETAIL);
		part.setConditionType(InventoryItemCondition.NEW);
		part.setSerializedPart(true);
		part.setSource(InventoryItemSource.MAJORCOMPREGISTRATION);
		part.setBusinessUnitInfo(part.getBusinessUnitInfo());
		part.setOwnershipState(this.findOwnershipStateByName(OwnershipState.FIRST_OWNER.getName()));		
		part.setInstallationDate(part.getDeliveryDate());		
		
		this.createInventoryItem(part);
		if (inventoryItem != null) {
			InventoryItemComposition inventoryItemComposition = new InventoryItemComposition(part);
			inventoryItemComposition.setPartOf(inventoryItem);
			inventoryItem.getComposedOf().add(inventoryItemComposition);
			this.updateInventoryItem(inventoryItem);	
		}
	}
	
	public List<InventoryItem> findMajorComponentBySerialNumber(String serialNumber)
			throws ItemNotFoundException {
		return this.inventoryItemRepository.findMajorComponentBySerialNumber(serialNumber);
	}
    
	public InventoryItem findInventoryItemForMajorComponent(final Long inventoryId) {
		return this.inventoryItemRepository.findInventoryItemForMajorComponent(inventoryId);
	}	
	
	
	public InventoryItemComposition findInvItemCompForInvItemAndInvItemComposition(final Long inventoryId,final Long majorCompInvItemId) {
		return this.inventoryItemRepository.findInvItemCompForInvItemAndInvItemComposition(inventoryId,majorCompInvItemId);
	}	
	
	public List<InventoryItem> getPartsToBeDeleted(InventoryItem inventoryItem) {
		List<InventoryItem> partsToBeRemoved = new ArrayList<InventoryItem>();
		if (inventoryItem.getComposedOf() != null && inventoryItem.getComposedOf().size() > 0) {
			List<InventoryItemComposition> invComposition = new ArrayList<InventoryItemComposition>();
			for (InventoryItemComposition part : inventoryItem.getComposedOf()) {
				if (part.getPart().getSource() != null
						&& !part.getPart().getSource().equals(InventoryItemSource.INSTALLBASE)) {
					partsToBeRemoved.add(part.getPart());
				} else {
					invComposition.add(part);
				}
			}
			if (invComposition.size() > 0) {
				inventoryItem.getComposedOf().clear();
				inventoryItem.getComposedOf().addAll(invComposition);
			} else {
				inventoryItem.getComposedOf().clear();
			}
		}
		return partsToBeRemoved;
	}
	
	public Item findPdiNameBySerialNumber(String serialNumber)throws ItemNotFoundException{
		return this.inventoryItemRepository.findPdiNameBySerialNumber(serialNumber);
	}
	/**
	 * To find count of stock with dealer or admin and display on home page. 
	 */
	public Long getCountOfInventoryItems(final InventoryType type, boolean vintageStock, CalendarDate shipmentDate){
		Organization org = securityHelper.getLoggedInUser().getBelongsToOrganization();
		if(securityHelper.getLoggedInUser().isInternalUser()){
		return this.inventoryItemRepository.findCountOfInventoryItems(type,null ,vintageStock, shipmentDate);
		}else{
		return this.inventoryItemRepository.findCountOfInventoryItems(type,org ,vintageStock, shipmentDate);
	    }
     }
	public List<InventoryItem> findInventoryItemsWhoseSerialNumbersStartWithAndBrand(
			String partialSerialNumber, String itemType, int pageNumber, int pageSize, List<String> brands) {
		return this.inventoryItemRepository
		.findInventoryItemsWhoseSerialNumbersStartWithAndBrand(
				partialSerialNumber, itemType, pageNumber, pageSize,brands);
	}
	public List<InventoryItem> findAllSerializedSerialNumbersStartingWithAndBrand(
			String partialSerialNumber, 
			int pageNumber, int pageSize, String claimBrand) {
		return this.inventoryItemRepository
		.findAllSerializedSerialNumbersStartingWithAndBrand(partialSerialNumber,
				pageNumber, pageSize,claimBrand);
	}
	public void saveComponentAuditHistory(
			ComponentAuditHistory componentAuditHistory) {
		this.inventoryItemRepository.saveComponentAuditHistory(componentAuditHistory);
		
	}

	public List<ComponentAuditHistory> findComponentAuditForInventoryAndSequenceNumber(InventoryItem inventoryItem, String sequenceNumber){
		return this.inventoryItemRepository.findComponentAuditForInventoryAndSequenceNumber(inventoryItem, sequenceNumber);
	}
	public List<InventoryItemComposition> getComponentDetailForMajorComponent(
			List<InventoryItem> inventoryItemsList) {
		// TODO Auto-generated method stub
		return this.inventoryItemRepository.getComponentDetailsFromMajorComponentInventoryItem(inventoryItemsList);
	}

    public List<InventoryItem> findAllInventoriesByTypeForChildDealersTooStartingWith(
            String partialSerialNumber,List<Long> parentAndChildIDs,String type, int pageNumber, int pageSize) {
        return this.inventoryItemRepository.findAllInventoriesByTypeForChildDealersTooStartingWith(
                partialSerialNumber,parentAndChildIDs,type,pageNumber, pageSize);
    }

    public List<InventoryItem> findAllInventoriesByTypeStartingWithDisableCuurentOwnerFilter(
            String partialSerialNumber,List<Long> currentOwnersId,Long currentOwnerId,String type, int pageNumber, int pageSize){

        return this.inventoryItemRepository.findAllInventoriesByTypeStartingWithDisableCuurentOwnerFilter(partialSerialNumber,currentOwnersId, currentOwnerId, type, pageNumber, pageSize);
    }
    
    public List<ListOfValues> getLovsForClass(String className) {
		List<ListOfValues> lovs = new ArrayList<ListOfValues>();
		lovs = this.lovRepository.findAllActive(className);
		return lovs;
	}

	public void saveInternalTruckDocuments(InventoryItem inventoryItem) {
		this.inventoryItemRepository.save(inventoryItem);
		
	}
}
