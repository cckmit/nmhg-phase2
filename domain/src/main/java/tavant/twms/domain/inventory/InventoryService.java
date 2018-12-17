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

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.MultiInventorySearch;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.OwnershipState;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

import com.domainlanguage.time.CalendarDate;

/**
 * @author kamal.govindraj
 *
 */
@Transactional(readOnly = true)
public interface InventoryService {

	@Transactional(readOnly = false)
	void createInventoryItem(InventoryItem inventoryItem);
	@Transactional(readOnly = false)
	void createInventoryItems(List<InventoryItem> inventoryItems);
	
	@Transactional(readOnly = false)
	void deleteInventoryItem(InventoryItem inventoryItem);
	@Transactional(readOnly = false)
	void deleteInventoryItems(List<InventoryItem> inventoryItems);
	
	@Transactional(readOnly = false)
	void updateInventoryItem(InventoryItem inventoryItem);

	InventoryItem findInventoryItem(Long id);
	
	public InventoryItem findInventoryBySerialNumberAndType(String serialNumber, InventoryType inventoryType) throws ItemNotFoundException;

    PageResult<InventoryItem> findAllInventoryItemsForDealer(ServiceProvider dealer,
            PageSpecification pageSpecification);

    PageResult<InventoryItem> findAllInventoryItemsOfTypeForDealer(
			InventoryListCriteria inventoryListCriteria);
    
    List<InventoryItem> findAllInventoriesByTypeStartingWith(
			String partialSerialNumber, Long currentOwnerId, String type,
			int pageNumber, int pageSize);

	InventoryItem findSerializedItem(String id)
			throws ItemNotFoundException;
	
	InventoryItem findMachine(String serialNumber)	throws ItemNotFoundException;
	
	InventoryItem findSerializedItem ( String serialNumber , String modelName)
			throws ItemNotFoundException;
	
	InventoryItem findSerializedItemWithOutActiveFilter(final String serialNumber ,final String sequenceNumber,final InventoryType inventoryType)throws ItemNotFoundException;
	
	InventoryItem findMachine(final String serialNumber , final String modelName)throws ItemNotFoundException;
	InventoryItem findSerializedItemByConNumAndModel ( String containerNumber , String modelName)
	throws ItemNotFoundException;

	List<InventoryItemCondition> listInventoryItemConditionTypes();

	/**
	 * Returns the list of serial numbers that start with the given
	 * partialSerialNumber
	 *
	 * @param partialSerialNumber
	 * @param pageNumber -
	 *            page to fetch (first page is 0)
	 * @param pageSize -
	 *            number of items per page
	 * @return
	 */
	List<String> findAllSerialNumbersStartingWith(String partialSerialNumber,
			int pageNumber, int pageSize);

	List<InventoryItem> findPartsWhoseSerialNumbersStartWith(
			String partialSerialNumber, int pageNumber, int pageSize);
	
	List<InventoryItem> findInventoryItemsWhoseSerialNumbersStartWith(
			String partialSerialNumber,String itemType, int pageNumber, int pageSize);
	
	List<InventoryItem> findNationalAccountInventoryItemsWhoseSerialNumbersStartWith(String partialSerialNumber,String claimType, int pageNumber, int pageSize, Long orgId);

	List<InventoryItem> findAllItemsMatchingCriteria(
            InventorySearchCriteria inventorySearchCriteria,
            Pagination pagination, PageSpecification pageSpecification);

	PageResult<InventoryItem> findAllItemsMatchingQuery(Long domainPredicateId,
			ListCriteria listCriteria, Organization organization);

	PageResult<InventoryItem> findAllInventoryItemsForMultiClaim(
			MultiInventorySearch multiInventorySearch, Long dealerId,
			ListCriteria listCriteria);

	public OwnershipState findOwnershipStateByName(String name);

	public List<InventoryItem> findInventoryItemsForSerialNumbers(
			String[] serialNumbers);

	public PageResult<InventoryItem> findAllInventoryItemsForCampaignMultiClaim(
			final MultiInventorySearch multiInventorySearch,
			final Long dealerId, final ListCriteria listCriteria,
			final String campaignCode);

	public List<InventoryItem> findAllInventoryItemsByLabel(
			final String labelName);

	public PageResult<?> findAllInventoryLabels(final ListCriteria listCriteria);
	
	public boolean customersBelongsToConfigParam(InventoryItem item);
	
	public InventoryType findInventoryTypeByType(final String type);

	
	public List<InventoryItem> findInventoryItemsForIds(List<Long> inventoryIds);

	
	List<InventoryItem> findItemBySerialNumber(String serialNumber)	throws ItemNotFoundException;
	
	public InventoryItem findItemBySerialNumberAndProduct(String serialNumber, String productCode) throws ItemNotFoundException;
	
	public InventoryItem findItemBySerialNumberAndModelNumber(String serialNumber, Item ofType)
			throws ItemNotFoundException;
	
	public InventoryItem findItemBySerialNumberAndModelNumberAndType(String serialNumber, Item ofType,
			InventoryType inventoryType) throws ItemNotFoundException;

	InventoryItem findInventoryItemBySerialNumber(String serialNumber) throws ItemNotFoundException;
	
	public InventoryItem findInventoryItemByContainerNumber(final String containerNumber) throws ItemNotFoundException;

    public InventoryItemCondition findInventoryItemConditionByType(String itemCondition);

    public boolean isInvItemOwnedByAllowedCustomerType(InventoryItem item, Map<Object, Object> keyValueOfCustomerTypes);

    public PageResult<?> findAllItemsMatchingSearch(
			InventorySearchCriteria inventorySearchCriteria,
			Pagination pagination,PageSpecification pageSpecification);
    public InventoryItem findItemBySerialNumberAndItemNumber(final String serialNumber, final String itemNumber)
    throws ItemNotFoundException;

    List<InventoryItem> findAllSerializedSerialNumbersStartingWith(
			String partialSerialNumber,InventoryItemSource source, int pageNumber, int pageSize);
    
    public List<InventoryItem> findInventoryItemCompositionForInvItem(
			final String partialSerialNumber, final Long inventoryItemId);
    
    public Boolean doesInvItemExistWithSNAndItem(final String serialNumber,final Item item);

	public List<InventoryItem> findInvItemCompositionForInvItem(final Long inventoryItemId);
    
    public InventoryItem findSerializedPartBySerialNumber (
			final String partialSerialNumber,final InventoryItemSource source)  throws ItemNotFoundException;
    
	List<InventoryItem> findAllRetailMachinesBySerialNumber(
			String partialSerialNumber, int pageNumber, int pageSize);
	
	@Transactional(readOnly=false)
	public void createSerializedPartsForInventories(List<InventoryItem> inventoryItem);
	
	@Transactional(readOnly=false)
	public void createMajorCompAndUpdateInvItemBOM(InventoryItem inventoryItem, InventoryItem part);
	
	public InventoryItem findInventoryItemForMajorComponent(final Long inventoryId);

	public List<InventoryItem> findMajorComponentBySerialNumber(String serialNumber) throws ItemNotFoundException;
	
	InventoryItemComposition findInvItemCompForInvItemAndInvItemComposition(final Long inventoryItemId, final Long majorCompInvItemId);

    public boolean areInventoriesPresentforThisServiceProvider(final String serviceProvider);
    
    public List<InventoryItem> getPartsToBeDeleted(InventoryItem inventoryItem);
    
    public InventoryItem findInvItemByIdWithoutInactiveFilter(String id)throws ItemNotFoundException;
    
    Item findPdiNameBySerialNumber(final String serialNumber)throws ItemNotFoundException;
    
    public Long getCountOfInventoryItems(final InventoryType type, boolean vintageStock, CalendarDate shipmentDate);
	public List<InventoryItem> findInventoryItemsWhoseSerialNumbersStartWithAndBrand(
			String upperCase, String itemType, int pageNumber, int pageSize, List<String> brands);
	
	public List<InventoryItem> findAllSerializedSerialNumbersStartingWithAndBrand(
			String partialSerialNumber, int pageNumber, int pageSize, String claimBrand);
	void saveComponentAuditHistory(ComponentAuditHistory componentAuditHistory);

	public List<ComponentAuditHistory> findComponentAuditForInventoryAndSequenceNumber(InventoryItem inventoryItem, String sequenceNumber);
	
	List<InventoryItemComposition> getComponentDetailForMajorComponent(
			List<InventoryItem> inventoryItemsList);

    //CR 387
    List<InventoryItem> findAllInventoriesByTypeForChildDealersTooStartingWith(
            String partialSerialNumber, List<Long> parentAndChildIDs, String type,
            int pageNumber, int pageSize);

    public List<InventoryItem> findAllInventoriesByTypeStartingWithDisableCuurentOwnerFilter(
            String partialSerialNumber,List<Long> currentOwnersId,Long currentOwnerId,String type, int pageNumber, int pageSize);
    

	public List<ListOfValues> getLovsForClass(String className);
	
//	public void saveInternalTruckDocuments(InventoryItem );

}


