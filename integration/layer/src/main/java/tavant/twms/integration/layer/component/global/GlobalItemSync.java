package tavant.twms.integration.layer.component.global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import tavant.globalsync.itemsync.BrandItemNumberTypeDTO;
import tavant.globalsync.itemsync.DescriptionTypeDTO;
import tavant.globalsync.itemsync.HeaderDocumentDTO.Header;
import tavant.globalsync.itemsync.ItemMasterTypeDTO;
import tavant.globalsync.itemsync.SupplierLocationTypeDTO;
import tavant.globalsync.itemsync.SupplierTypeDTO;
import tavant.globalsync.itemsync.SyncItemMasterDocumentDTO.SyncItemMaster;
import tavant.globalsync.itemsync.UnitPackagingDocumentDTO.UnitPackaging;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.ItemTypeMapping;
import tavant.twms.domain.catalog.ItemTypeMappingService;
import tavant.twms.domain.catalog.ItemUOMTypes;
import tavant.twms.domain.catalog.SupersessionItem;
import tavant.twms.domain.catalog.SupplierItemLocation;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.domain.supplier.ItemMapping;
import tavant.twms.domain.supplier.ItemMappingService;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.constants.ItemSyncInterfaceErrorConstants;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.time.CalendarDate;
import com.nmhg.itemsynch_response.ItemSyncResponse;
import com.nmhg.itemsynch_response.ItemSyncResponse.ApplicationArea;
import com.nmhg.itemsynch_response.ItemSyncResponse.ApplicationArea.Sender;
import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse;
import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse.Status.Code;
import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse.Status.Exceptions;
import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse.Status.Exceptions.Error;
import com.nmhg.itemsynch_response.MTItemSyncResponseDocument;

public class GlobalItemSync {
	
	private ItemSyncInterfaceErrorConstants itemSyncInterfaceErrorConstants;
	
	private static Logger logger = Logger.getLogger(GlobalItemSync.class
			.getName());

	private TransactionTemplate transactionTemplate;

	private CatalogService catalogService;

	private ItemGroupService itemGroupService;

	private ItemMappingService itemMappingService;

	private OrgService orgService;

	private SupplierService supplierService;
	
	private BusinessUnitService businessUnitService;
	
	private ItemTypeMappingService  itemTypeMappingService;
	
public MTItemSyncResponseDocument sync(final SyncItemMaster syncItemMaster) {
		boolean failure = false;
		List<ItemMasterTypeDTO> items = Arrays.asList(syncItemMaster.getDataArea().getItemMasterArray());
		MTItemSyncResponseDocument itemResponseDoc = MTItemSyncResponseDocument.Factory.newInstance();
		ItemSyncResponse itemSyncResponse = itemResponseDoc.addNewMTItemSyncResponse();
		itemSyncResponse.addNewStatus();
		addResponseApplicationArea(syncItemMaster, itemSyncResponse);
		List<ItemMasterResponse> itemMasterResponses= new ArrayList<ItemMasterResponse>();
		if (null != items && items.size() > 0) {
			ListIterator<ItemMasterTypeDTO> itemIterator = items.listIterator();
			while (itemIterator.hasNext()) {
				final Map<String,String> errorMessageCodes = new HashMap<String,String>();
				itemSyncResponse.addNewItemMasterResponse();
				ItemMasterTypeDTO itemMasterTypeDTO = itemIterator.next();
				final Header itemDTO = itemMasterTypeDTO.getHeader();
				ItemMasterResponse itemMasterResponseTypeDTO = ItemMasterResponse.Factory.newInstance();
				itemMasterResponseTypeDTO.addNewStatus();
				String brandItemNumber = null;
				try {
					validate(itemDTO,errorMessageCodes,syncItemMaster);
					setDefaultValues(itemDTO,errorMessageCodes);
					if(errorMessageCodes.isEmpty()){
					if (itemDTO.getItemId() != null	&& StringUtils.hasText(itemDTO.getItemId().getId())) {
						brandItemNumber = itemDTO.getItemId().getId().trim();
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Received " + brandItemNumber + " item for synchronising.");//TODO Hari  don't need to get it from property message -done
					}
					this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
								@Override
								protected void doInTransactionWithoutResult(
										TransactionStatus transactionStatus) {
									Date date = null;		
									if(syncItemMaster.getApplicationArea().getCreationDateTime()!=null){
										date=syncItemMaster.getApplicationArea().getCreationDateTime().getTime();
									}
									sync(itemDTO,date,errorMessageCodes,syncItemMaster);
								}
							});
					}
					if (itemDTO.getItemId()
							.getBrandItemNumbers()!=null&&itemDTO.getItemId()
							.getBrandItemNumbers().getBrandItemNumberArray()!=null&&itemDTO.getItemId()
							.getBrandItemNumbers().getBrandItemNumberArray().length>0){
						 brandItemNumber=getBrandItemNumber(itemDTO);
						if(brandItemNumber!=null){
							itemMasterResponseTypeDTO.setItemId(brandItemNumber);
						}
					}
					else if (itemDTO.getItemId().getId() != null
							&& StringUtils.hasText(itemDTO.getItemId().getId())) {
						brandItemNumber = itemDTO.getItemId().getId().trim();
						itemMasterResponseTypeDTO.setItemId(brandItemNumber);
					} 
					if(!errorMessageCodes.isEmpty()){
				           buildErrorResponseForItemResponse(errorMessageCodes,brandItemNumber,itemMasterResponseTypeDTO);
				           failure=true;
				        }else{
				        	itemMasterResponseTypeDTO.getStatus().setCode(Code.Enum.forString(IntegrationConstants.SUCCESS));
				        }
				}catch(IllegalArgumentException  ex){
					failure = true;
					String error = ex.getMessage();
					if(error != null){
						errorMessageCodes.put(ItemSyncInterfaceErrorConstants.I0049, error);
					}
					itemMasterResponseTypeDTO = buildErrorResponseForItemResponse(
							errorMessageCodes, brandItemNumber,
							itemMasterResponseTypeDTO);
				}catch (RuntimeException e) {
					failure = true;
					logger.error("Exception Occurred in GlobalItemSync !!",e);
					if(e.getMessage()!=null){
						if(e.getMessage().contains("##")){
						String[] message = e.getMessage().split("##");
		                 if(message.length>1){
		                	 String messageKey=itemSyncInterfaceErrorConstants.getErrorMessage(message[0]);
		                	 if(messageKey!=null){
		                		errorMessageCodes.put(message[0],message[1]);
		                	 }
		                }
						}else{
							 errorMessageCodes.put(ItemSyncInterfaceErrorConstants.I0038, itemSyncInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(
												ItemSyncInterfaceErrorConstants.I0038));
						}
				}else{
					 errorMessageCodes.put(ItemSyncInterfaceErrorConstants.I0038, itemSyncInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(
												ItemSyncInterfaceErrorConstants.I0038));
				}
				if (itemDTO.getItemId()
							.getBrandItemNumbers()!=null&&itemDTO.getItemId()
									.getBrandItemNumbers().getBrandItemNumberArray()!=null&&itemDTO.getItemId()
							.getBrandItemNumbers().getBrandItemNumberArray().length>0){
					brandItemNumber=getBrandItemNumber(itemDTO);
					}
					 itemMasterResponseTypeDTO = buildErrorResponseForItemResponse(
								errorMessageCodes, brandItemNumber,
								itemMasterResponseTypeDTO);
				}
				itemMasterResponses.add(itemMasterResponseTypeDTO);
			}
		}
		itemSyncResponse = setSuccessOrFailure(itemSyncResponse, failure);
		itemSyncResponse.setItemMasterResponseArray(((ItemMasterResponse[]) itemMasterResponses.toArray(new ItemMasterResponse[itemMasterResponses.size()])));
		itemResponseDoc.setMTItemSyncResponse(itemSyncResponse);
		return itemResponseDoc;

}

	private String getBrandItemNumber(Header itemDTO) {
		String brandItemNumber = null;
		BrandItemNumberTypeDTO[] brandItems = itemDTO.getItemId()
				.getBrandItemNumbers().getBrandItemNumberArray();
		for (BrandItemNumberTypeDTO brand : brandItems) {
			if (StringUtils.hasText(brand.getItemNumber())) {
				brandItemNumber = brand.getItemNumber();
				break;
			}
		}
		return brandItemNumber;
	}

	private Item getItemforSupplierParts(Header itemDTO,
			Map<String, String> errorMessageCodesMap) {
		String brandItemNumber = null;
		String brandName = null;
		BrandItemNumberTypeDTO[] brandItems = itemDTO.getItemId()
				.getBrandItemNumbers().getBrandItemNumberArray();
		for (BrandItemNumberTypeDTO brand : brandItems) {
			if (StringUtils.hasText(brand.getItemNumber())) {
				brandItemNumber = brand.getItemNumber();
				brandName = brand.getBrand().toString();
				break;
			}
		}
		if (brandItemNumber == null || brandName == null) {
			throw new RuntimeException(
					ItemSyncInterfaceErrorConstants.I0036
							+ "##"
							+ itemSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0036));

		} else {
			List<BrandItem> brandItemsList = catalogService
					.findBrandItemsByName(brandItemNumber, brandName);
			if (brandItems == null || brandItemsList.isEmpty()) {
				throw new RuntimeException(
						ItemSyncInterfaceErrorConstants.I0050
								+ "##"
								+ itemSyncInterfaceErrorConstants
										.getPropertyMessage(
												ItemSyncInterfaceErrorConstants.I0050,
												new String[] { brandItemNumber }));
			} else {
				List<Item> items = new ArrayList<Item>();
				StringBuffer itemNumbers = new StringBuffer();
				for (BrandItem brandItem : brandItemsList) {
					if (brandItem.getItem() != null
							&& brandItem.getItem().getStatus() != null
							&& StringUtils.hasText(brandItem.getItem()
									.getStatus())
							&& IntegrationConstants.ACTIVE
									.equalsIgnoreCase(brandItem.getItem()
											.getStatus())) {
						items.add(brandItem.getItem());
						itemNumbers.append(brandItem.getItem().getNumber());
						itemNumbers.append(",");
					}
				}
				if (items.isEmpty()) {
					throw new RuntimeException(
							ItemSyncInterfaceErrorConstants.I0050
									+ "##"
									+ itemSyncInterfaceErrorConstants
											.getPropertyMessage(
													ItemSyncInterfaceErrorConstants.I0050,
													new String[] { brandItemNumber }));
				} else if (items.size() > 1) {
					throw new RuntimeException(
							ItemSyncInterfaceErrorConstants.I0052
									+ "##"
									+ itemSyncInterfaceErrorConstants
											.getPropertyMessage(
													ItemSyncInterfaceErrorConstants.I0052,
													new String[] {
															brandItemNumber,
															itemNumbers
																	.toString() }));
				} else {
					return items.get(0);
				}
			}
		}
	}
	private ItemSyncResponse setSuccessOrFailure(ItemSyncResponse itemSyncResponse, Boolean failure){
		if(failure){
			itemSyncResponse.getStatus().setCode(ItemSyncResponse.Status.Code.Enum.forString(IntegrationConstants.FAILURE));
		}
		else{
			itemSyncResponse.getStatus().setCode(ItemSyncResponse.Status.Code.Enum.forString(IntegrationConstants.SUCCESS));
		}
		return itemSyncResponse;
	}
	
	private ItemMasterResponse buildErrorResponseForItemResponse(
			Map<String, String> errorMessageCodesMap, String itemNumber,
			ItemMasterResponse itemMasterResponseTypeDTO) {
		itemMasterResponseTypeDTO.setItemId(itemNumber);
		itemMasterResponseTypeDTO.getStatus().setCode(
				Code.Enum.forString(IntegrationConstants.FAILURE));
		Exceptions exceptions = itemMasterResponseTypeDTO.getStatus()
				.addNewExceptions();
		if (errorMessageCodesMap.isEmpty()) {
			errorMessageCodesMap
					.put(ItemSyncInterfaceErrorConstants.I0038,
							itemSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0038));
		}
		for (Map.Entry<String, String> errorMessage : errorMessageCodesMap
				.entrySet()) {
			Error error = exceptions.addNewError();
			error.setErrorCode(errorMessage.getKey());
			error.setErrorMessage(errorMessage.getValue());
		}
		itemMasterResponseTypeDTO.getStatus().setExceptions(exceptions);
		return itemMasterResponseTypeDTO;
	}

	private void addResponseApplicationArea(
			final SyncItemMaster syncItemMaster,
			ItemSyncResponse itemSyncResponse) {
		ApplicationArea applicationArea = itemSyncResponse.addNewApplicationArea();
		if (syncItemMaster.getApplicationArea() != null) {
			applicationArea.setBODId(syncItemMaster.getApplicationArea()
					.getBODId());
			applicationArea.setCreationDateTime(CalendarUtil
					.convertToDateTimeToString(syncItemMaster
					.getApplicationArea().getCreationDateTime().getTime()));
			applicationArea.setInterfaceNumber(syncItemMaster
					.getApplicationArea().getInterfaceNumber());
		}
		Sender Sender=applicationArea.addNewSender();
		if (syncItemMaster.getApplicationArea().getSender() != null) {
			Sender.setLogicalId(
					syncItemMaster.getApplicationArea().getSender()
							.getTask());
			Sender.setReferenceId(
					syncItemMaster.getApplicationArea().getSender()
							.getReferenceId());
			Sender.setTask(
					syncItemMaster.getApplicationArea().getSender().getLogicalId());
		}
	}

	public void sync(final Header itemDTO,final Date syncReceivedDate,Map<String,String> errorMessageCodes,SyncItemMaster syncItemMaster) {
		 if(itemDTO.getType().toString().equals(IntegrationConstants.PART_ITEM_TYPE)){
			 SelectedBusinessUnitsHolder.setSelectedBusinessUnit(IntegrationConstants.NMHG_EMEA); 
		 }
		String itemType = itemDTO.getType().toString();
		if(!itemType.equals(IntegrationConstants.SERIES_TYPE)){
		String itemNumber = null;
		ItemGroup product = null;
//		StringBuilder errorMessage = new StringBuilder();
		ItemTypeMapping itemTypeMapping = itemTypeMappingService
				.findByExternalItemType(itemDTO.getType().toString().trim());
			if (itemTypeMapping == null) {
				errorMessageCodes
						.put(ItemSyncInterfaceErrorConstants.I0025,
								itemSyncInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0025));
			}
		//Hari TODO: below code should be part of validate if it is not used any where below -done
		
		//TODO Hari: this is newly added, check it once more-done
		//TODO Hari: this is newly added, check it once more-done
			Item item=null;
			if (itemDTO.getItemId() != null
					&& StringUtils.hasText(itemDTO.getItemId().getId())) {
				itemNumber = itemDTO.getItemId().getId().trim();
				if(IntegrationConstants.MACHINE.equalsIgnoreCase(itemDTO.getType().toString()) || IntegrationConstants.OPTIONS.equalsIgnoreCase(itemDTO.getType().toString())){
					String productCode = itemDTO.getUserArea().getProductCode();
					product = itemGroupService
					.findItemGroupByCodeAndType(productCode.trim(),ItemGroup.PRODUCT);
					item = fetchItemForOEMAndProduct(itemNumber.trim().toUpperCase(),product);
				}else{
					item = fetchItemForOEM(itemNumber.trim().toUpperCase());
				}
			} else if ((itemNumber == null || itemNumber.toString().isEmpty())
					&& IntegrationConstants.SUPPLIER_PARTS_ITEM_TYPE
							.equalsIgnoreCase(itemDTO.getType().toString())
					&& ((IntegrationConstants.EPO
							.equalsIgnoreCase(syncItemMaster
									.getApplicationArea().getSender().getLogicalId())) || (IntegrationConstants.PDC
							.equalsIgnoreCase(syncItemMaster
									.getApplicationArea().getSender().getLogicalId())))) {
				item = getItemforSupplierParts(itemDTO, errorMessageCodes);
				itemDTO.getItemId().setId(item.getId().toString());
			}
		
		boolean create = false;

		updateBusinessUnit(itemDTO,errorMessageCodes);
	    //TODO Hari: put the below code in  fetchItemForOEM - done
		if (item == null) {
			if(itemDTO.getType().toString().equalsIgnoreCase(IntegrationConstants.SUPPLIER_PARTS_ITEM_TYPE)){
				errorMessageCodes
				.put(ItemSyncInterfaceErrorConstants.I0013,
						itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0013));
				throw new RuntimeException(ItemSyncInterfaceErrorConstants.I0013+"##"+
						itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0013));
			}
			item = new Item();
			create = true;
		}
		//TODO Hari: put the below code in  fetchItemForOEM - done
		//TODO Hari: Make sure NPE is not thrown here, check whether it is already validated - already checking in validation
		//TODO Hari: one method for each item type 
		if(!itemDTO.getType().toString().equalsIgnoreCase(IntegrationConstants.SUPPLIER_PARTS_ITEM_TYPE)){
			createOrUpdateItem(create,itemDTO,item, itemType, syncReceivedDate,errorMessageCodes);
		  }
		//TODO Hari: Move supersessionItem to new private method - done
		if(itemType.equalsIgnoreCase(IntegrationConstants.PART_ITEM_TYPE) && itemDTO.getUserArea().getSupersessionItem()!=null){
				setValuesForSuperSessionItem(itemDTO,item,errorMessageCodes);
    		}
		
		if(itemType.equalsIgnoreCase(IntegrationConstants.SUPPLIER_PARTS_ITEM_TYPE)){
			createOrUpdateSupplierItem(itemDTO,item,create,syncReceivedDate,itemNumber,errorMessageCodes);
		}
		} else if (itemType.equalsIgnoreCase(IntegrationConstants.SERIES_TYPE)) {
			syncSeries(itemDTO,errorMessageCodes);
		}
	}
	
	private void createOrUpdateItem(Boolean create, Header itemDTO, Item item, String itemType, final Date syncReceivedDate,Map<String,String> errorMessageCodes){
		if(create){
			merge(itemDTO, item, false, null, false,errorMessageCodes);
			createNewItem(itemDTO, item);
		    if(itemType.equalsIgnoreCase(IntegrationConstants.PART_ITEM_TYPE)&&itemDTO.getItemId().getBrandItemNumbers()!=null && 
		        	itemDTO.getItemId().getBrandItemNumbers().getBrandItemNumberArray()!=null&&
		        	itemDTO.getItemId().getBrandItemNumbers().getBrandItemNumberArray().length>0){
		        	final BrandItemNumberTypeDTO[] brandItems = itemDTO.getItemId().getBrandItemNumbers().getBrandItemNumberArray();
		        	createBrands(item, brandItems);
		      }
		} else {
			if(item.getD().getUpdatedTime() == null || item.getD().getUpdatedTime().before(syncReceivedDate)){
                merge(itemDTO, item, false, null, true,errorMessageCodes);
                updateItem(item);
                if(itemType.equalsIgnoreCase(IntegrationConstants.PART_ITEM_TYPE) && itemDTO.getItemId().getBrandItemNumbers()!=null && 
    		        	itemDTO.getItemId().getBrandItemNumbers().getBrandItemNumberArray()!=null&&
    		        	itemDTO.getItemId().getBrandItemNumbers().getBrandItemNumberArray().length>0){
                	final BrandItemNumberTypeDTO[] brandItems = itemDTO.getItemId().getBrandItemNumbers().getBrandItemNumberArray();
                	updateBrands(item, brandItems);
                }
			}
		}  
	}
	
	private void createOrUpdateSupplierItem(Header itemDTO,Item item, Boolean create, final Date syncReceivedDate, String itemNumber,Map<String,String> errorMessageCodes){
		List<SupplierTypeDTO> suppliers = new ArrayList<SupplierTypeDTO>();
		if(itemDTO.getUserArea().getSuppliers()!=null){
			suppliers = Arrays.asList(itemDTO.getUserArea().getSuppliers().getSupplierArray());
		}
		Item itemWithSupplier=null;
		for (SupplierTypeDTO supplierTypeDTO : suppliers) {
            String supplierNumber = supplierTypeDTO.getSupplierNumber();
            String supplierItemNumber = supplierTypeDTO.getSupplierItemNumber();
            if(supplierItemNumber==null||!StringUtils.hasText(supplierItemNumber)){
            	errorMessageCodes
				.put(ItemSyncInterfaceErrorConstants.I0053,
						itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0053));	
            }else{
            boolean supplierItemExistsNoMapping = false;
            Supplier ownedBy =null;
            if(!StringUtils.hasText(supplierItemNumber)){
                 supplierItemNumber = itemNumber;
            }
            CalendarDate fromDate = null;
            CalendarDate toDate = null;
            String supplierSitecode =null;
            if(StringUtils.hasText(supplierTypeDTO.getSupplierSiteCode())){
                supplierSitecode=supplierTypeDTO.getSupplierSiteCode();
            }
            ItemMapping itemMapping = null;
            try {
				if(StringUtils.hasText(supplierNumber)){
					supplierNumber = supplierNumber.trim();
				}else{
					errorMessageCodes
					.put(ItemSyncInterfaceErrorConstants.I0030,
							itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0030));
				}//TODO
				//Checking Supplier with  BU Association
				ownedBy = supplierService.findSupplierByNumber(supplierNumber);
				if (ownedBy == null) {
					ownedBy = supplierService
							.findSupplierByNumberWithOutBU(supplierNumber);
					if (ownedBy == null) {
						throw new RuntimeException(ItemSyncInterfaceErrorConstants.I0014+"##"+itemSyncInterfaceErrorConstants
								.getPropertyMessage(
										ItemSyncInterfaceErrorConstants.I0014,
										new String[]{supplierNumber}));
					}
				}
				
				// As per business, Supplier Item number can be changed at any time
				// Hence the logic should be
				// 1. For a given OEM & Supplier need to find out item mappings 
				// 2. If item mapping does not exist, create new supplier copy 
				// 3. If exists merge with sync data and update.
				
				itemMapping = itemMappingService.findItemMappingForOEMandSupplierItem(item, supplierItemNumber, ownedBy);
				if(null!=itemMapping){
					itemWithSupplier = itemMapping.getToItem();
				}
			} catch (HibernateException hibernateException) {
				logger.error("Did not find item mapping from sync for item number "+ itemNumber);
                itemWithSupplier = catalogService.findSupplierItem(supplierItemNumber, ownedBy.getId());
                //As supplier items can be migrated, but mapping my not be provided.
                supplierItemExistsNoMapping = (itemWithSupplier != null);
			}
			if (itemWithSupplier == null) {
				itemWithSupplier = new Item();
				create = true;
			}
			if(supplierItemExistsNoMapping){
				if((item.getD().getUpdatedTime() == null || 
						(item.getD().getUpdatedTime()!=null&&item.getD().getUpdatedTime().before(syncReceivedDate))) || 
                        (itemWithSupplier.getD().getUpdatedTime() == null || 
                        (itemWithSupplier.getD().getUpdatedTime()!=null&&itemWithSupplier.getD().getUpdatedTime().before(syncReceivedDate)))){ 
                    mergeItemToItemWithSupplier(itemDTO,item,itemWithSupplier, true, supplierItemNumber, true,errorMessageCodes);
					mergeItemMapping(itemMapping, item, itemWithSupplier, fromDate, toDate, supplierSitecode,itemDTO,supplierTypeDTO,errorMessageCodes);
					itemWithSupplier.setMake(ownedBy.getName());
					updateItem(itemWithSupplier);
				}
				
			} else {
				mergeItemToItemWithSupplier(itemDTO,item,itemWithSupplier, true, supplierItemNumber, false,errorMessageCodes);
                if (supplierItemExistsNoMapping) {
                    updateItem(itemWithSupplier);
                    createorUpdateMapping(item, itemWithSupplier, fromDate, toDate, supplierSitecode,itemMapping,itemDTO,supplierTypeDTO,errorMessageCodes);
                } else {
				    createItemWithSupplier(item, itemWithSupplier, ownedBy, fromDate, toDate, supplierSitecode,itemMapping, itemDTO,supplierTypeDTO,errorMessageCodes);
                }
			}
		  }
		}
	}
	
	private ItemMapping setLocationsForItemMapping(SupplierTypeDTO supplierTypeDTO, ItemMapping itemMapping, Header itemDTO,Map<String,String> errorMessageCodes){
		SupplierLocationTypeDTO[] supplierLocationArray = supplierTypeDTO.getSupplierLocations().getSupplierLocationArray();
    	List<SupplierItemLocation> supplierItemLocations = itemMapping.getSupplierItemLocations();
		for (SupplierLocationTypeDTO supplierLocationTypeDTO : supplierLocationArray) {
			boolean issupplierItemLocationExist=false;
			if (itemMapping.getSupplierItemLocations() != null
					&& !itemMapping.getSupplierItemLocations().isEmpty()) {
				for (SupplierItemLocation supplierLocation : itemMapping
						.getSupplierItemLocations()) {
					if ((supplierLocation.getLocationCode().equals(
							supplierLocationTypeDTO.getNMHGLocation()))
							&& ((supplierLocation.getFromDate()!=null&& supplierLocationTypeDTO.getFromDate()!=null)&&(CalendarUtil.convertToJavaCalendarInGMT(supplierLocation.getFromDate()).compareTo(
									supplierLocationTypeDTO.getFromDate())==0))
							&& ((supplierLocation.getToDate()!=null&& supplierLocationTypeDTO.getToDate()!=null)&&(CalendarUtil.convertToJavaCalendarInGMT(supplierLocation.getToDate()).compareTo(
									supplierLocationTypeDTO.getToDate())==0)))
					{
						supplierLocation.setStatus(getStatus(itemDTO.getStatus().getCode().toString().toUpperCase()));
						issupplierItemLocationExist=true;
						break;
					}
				}
			}
			if(issupplierItemLocationExist==false){
				SupplierItemLocation supplierItemLocation = new SupplierItemLocation();
				if (supplierLocationTypeDTO.getFromDate() != null) {
					supplierItemLocation
							.setFromDate(CalendarUtil
									.convertToCalendarDate(supplierLocationTypeDTO
											.getFromDate()));
				}else{
					errorMessageCodes
					.put(ItemSyncInterfaceErrorConstants.I0047,
							itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0047));
				}
				if (supplierLocationTypeDTO.getToDate() != null) {
					supplierItemLocation
							.setToDate(CalendarUtil
									.convertToCalendarDate(supplierLocationTypeDTO
											.getToDate()));
				}else{
					errorMessageCodes
					.put(ItemSyncInterfaceErrorConstants.I0048,
							itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0048));
				}
				if (StringUtils.hasText(supplierLocationTypeDTO
						.getNMHGLocation())){
					supplierItemLocation
							.setLocationCode(supplierLocationTypeDTO
									.getNMHGLocation());
				}else{
					throw new RuntimeException(ItemSyncInterfaceErrorConstants.I0046+"##"+
							itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0046));
				}
				supplierItemLocation.setStatus(getStatus(itemDTO.getStatus().getCode().toString().toUpperCase()));
				supplierItemLocations.add(supplierItemLocation);
				
			}
    	}
		itemMapping.setSupplierItemLocations(supplierItemLocations);
    	return itemMapping;
	}

	
	//private void setSupllierLoaction()
	
	private void setValuesForSuperSessionItem(Header itemDTO, Item item,Map<String,String> errorMessageCodes){
		final tavant.globalsync.itemsync.SupersessionItemDocumentDTO.SupersessionItem supersessionItemDTO = itemDTO.getUserArea().getSupersessionItem();
    	final SupersessionItem supersessionItem = new SupersessionItem();
			if (StringUtils.hasText(supersessionItemDTO.getItemNumber())) {
				final Item newItem = fetchItemForOEM(supersessionItemDTO.getItemNumber());
				if (newItem != null) {
					supersessionItem.setNewItem(newItem);
					supersessionItem.setOldItem(item);
					if (supersessionItemDTO.getStartDate() != null) {
						supersessionItem.setStartDate(supersessionItemDTO.getStartDate().getTime());
					}
					if (supersessionItemDTO.getEndDate() != null) {
						supersessionItem.setEndDate(supersessionItemDTO.getEndDate().getTime());
					}
					catalogService.createSupersessionItem(supersessionItem);
				}else{
					errorMessageCodes.put(ItemSyncInterfaceErrorConstants.I0040,itemSyncInterfaceErrorConstants
							.getPropertyMessage(
									ItemSyncInterfaceErrorConstants.I0040,
									new String[]{supersessionItemDTO.getItemNumber()}));
				}
			  }
	}

	/**
	 * It will take the series Item sync request xml and update the series
	 * details in to ItemGroup table
	 * 
	 * @param itemDTO
	 */
	private void syncSeries(Header itemDTO,Map<String,String> errorMessageCodes) {
		updateBusinessUnit(itemDTO,errorMessageCodes);
		ItemGroup productType = itemGroupService.findItemGroupByCodeAndType(
				IntegrationConstants.MACHINE, ItemGroup.PRODUCT_TYPE); //TODO Hari: get it from constants MACHINE
		if (productType == null) {
			throw new RuntimeException(ItemSyncInterfaceErrorConstants.I0031+"##"+itemSyncInterfaceErrorConstants
					.getPropertyMessageFromErrorCode(
							ItemSyncInterfaceErrorConstants.I0031));
		}
		ItemGroup series = itemGroupService.findItemGroupByCodeAndTypeIncludeInactive(itemDTO
				.getItemId().getId().trim(), ItemGroup.PRODUCT);
		ItemGroup productFamily = null;
		if (StringUtils.hasText(itemDTO.getUserArea().getProductFamilyCode())) {
			String productFamilyCode = itemDTO.getUserArea()
			.getProductFamilyCode().trim();
			productFamily = itemGroupService.findProductFamilyForProductType(
					productType, productFamilyCode.toUpperCase());
			if (productFamily == null) {
				productFamily = new ItemGroup();
				createItemGroupForProductStructure(itemDTO.getUserArea().getProductFamilyCode().trim(),productFamilyCode,
						productType, productFamily, ItemGroup.PRODUCT_FAMILY,errorMessageCodes);
			}
		}else{
			throw new RuntimeException(ItemSyncInterfaceErrorConstants.I0039+"##"+itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0039));
		}
		if(productFamily!=null){
			if (series == null) {
				series = new ItemGroup();
			}
				series.setBrandType(itemDTO.getUserArea().getBrand().toString());
				series.setCompanyType(itemDTO.getUserArea().getCompany());
				series.setBuildPlant(itemDTO.getUserArea().getBuildPlant());
				//TODO Hari: move this to validate method, basically validate should be split for each item type. inside validate method,
				//do all common validations and in the end write one API for each item type
				if(StringUtils.hasText(itemDTO.getUserArea().getOppositeSeriesCode())){
					ItemGroup oppositeSeriesCode = itemGroupService
					.findItemGroupByCodeAndType(itemDTO.getUserArea()
							.getOppositeSeriesCode().trim(), ItemGroup.PRODUCT);
					if (oppositeSeriesCode != null) {
						series.setOppositeSeries(oppositeSeriesCode);
					}else{
						errorMessageCodes
						.put(ItemSyncInterfaceErrorConstants.I0033,
								itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0033));
					}
				}
			if(!series.equals(itemGroupService.findProductFamilyForProductType(productFamily, itemDTO.getItemId().getId().trim()))){
				series.setIsPartOf(productFamily);
				createItemGroupForProductStructure(itemDTO.getDescription(),itemDTO.getItemId().getId().trim(),
						productFamily, series, ItemGroup.PRODUCT,errorMessageCodes);
			}else{
				throw new RuntimeException(ItemSyncInterfaceErrorConstants.I0015+"##"+itemSyncInterfaceErrorConstants
						.getPropertyMessageFromErrorCode(
								ItemSyncInterfaceErrorConstants.I0015));
			}
			if(StringUtils.hasText(itemDTO.getDescription())){
				series.setName(itemDTO.getDescription());
				series.setDescription(itemDTO.getDescription()+"-"+itemDTO
						.getItemId().getId().trim());
			}
			series.getD().setActive(getDactive(itemDTO.getStatus().getCode().toString()));
			itemGroupService.updateItemGroup(series);
		}
	}
	
	private void createBrands(Item item,
			final BrandItemNumberTypeDTO[] brandItems) {
		for(BrandItemNumberTypeDTO brand : brandItems){
			if (StringUtils.hasText(brand.getItemNumber())
					&& StringUtils.hasText(brand.getBrand().toString())&&item!=null) {
		 BrandItem brandItem = new BrandItem();
		 brandItem.setBrand(brand.getBrand().toString().trim());
		 if(StringUtils.hasText(brand.getItemNumber())){
			 brandItem.setItemNumber((brand.getItemNumber()));
		 }
		 brandItem.setItem(item);
		 createNewBrandItem(brandItem);
		}
		}
	}

	private void updateBrands(Item item,
			final BrandItemNumberTypeDTO[] brandItems) {
		for(BrandItemNumberTypeDTO brand : brandItems){
			    BrandItem brandItem = null;
				brandItem  = catalogService.findBrandByItemIdAndBrand(item, brand.getBrand().toString().trim());
				if(brandItem!=null){
					 brandItem.setBrand(brand.getBrand().toString().trim());
		        	 if(StringUtils.hasText(brand.getItemNumber())){
		        		 brandItem.setItemNumber(brand.getItemNumber().trim());
		        	 }
		        	 updateBrandItem(brandItem);
				}
				else{
					brandItem = new BrandItem();
					brandItem.setBrand(brand.getBrand().toString().trim());
		        	 if(StringUtils.hasText(brand.getItemNumber())){
		        		 brandItem.setItemNumber(brand.getItemNumber().trim());
		        	 }
		        	 brandItem.setItem(item);
		        	 createNewBrandItem(brandItem);
			}
		}
	}
	
    private void mergeItemToItemWithSupplier(Header itemDTO,Item item, Item itemWithSupplier,
			boolean isSupplier, String supplierItemNumber, boolean isExistingItem,Map<String,String> errorMessageCodes) {
    		if(isSupplier){
    			itemWithSupplier.setNumber(supplierItemNumber.trim().toUpperCase());
    		}
    		if (item.getStatus() != null
    				&& item.getStatus()!= null) {
    			itemWithSupplier.setStatus(item.getStatus().toUpperCase());
    		}
    		/*if (StringUtils.hasText(item.getDescription())) {
    			itemWithSupplier.setDescription(itemDTO.getDescription().trim());
    			itemWithSupplier.setName(item.getDescription().trim());
    		}*/
    		if(null!=itemDTO.getUserArea().getDescriptions()&& itemDTO.getUserArea().getDescriptions().sizeOfDescriptionArray()!=0){
		    	List<DescriptionTypeDTO> descriptions = Arrays.asList(itemDTO.getUserArea().getDescriptions().getDescriptionArray());
		    	descriptions.isEmpty();
		    	for(DescriptionTypeDTO descriptionTypeDTO:descriptions){
		    		if(null!=descriptionTypeDTO.getDescriptionLanguage()&&!descriptionTypeDTO.getDescriptionLanguage().isEmpty()&& null!=descriptionTypeDTO.getItemDescription()&&!descriptionTypeDTO.getItemDescription().isEmpty()){
		    			itemWithSupplier.setDescription(descriptionTypeDTO.getItemDescription(),descriptionTypeDTO.getDescriptionLanguage());
		    			if(itemDTO.getUserArea().getDivisionCode().trim().equals(IntegrationConstants.NMHG_EMEA) && descriptionTypeDTO.getDescriptionLanguage().equals(IntegrationConstants.GB_LOCALE)){
		    				itemWithSupplier.setName(descriptionTypeDTO.getItemDescription());
		    				itemWithSupplier.setDescription(descriptionTypeDTO.getItemDescription());
		    			}else if(itemDTO.getUserArea().getDivisionCode().trim().equals(IntegrationConstants.NMHG_US) && descriptionTypeDTO.getDescriptionLanguage().equals(IntegrationConstants.US_LOCALE)){
		    				itemWithSupplier.setName(descriptionTypeDTO.getItemDescription());
		    				itemWithSupplier.setDescription(descriptionTypeDTO.getItemDescription());
		    			}
		    		}else {
		    			if(itemDTO.getDescription()!=null){
		    				if(itemDTO.getUserArea().getDivisionCode().trim().equals(IntegrationConstants.NMHG_EMEA)){
		    					itemWithSupplier.setDescription(itemDTO.getDescription(),IntegrationConstants.GB_LOCALE);
		    		    	}else{
		    		    		itemWithSupplier.setDescription(itemDTO.getDescription(),IntegrationConstants.US_LOCALE);
		    		    	}
		    				itemWithSupplier.setDescription(itemDTO.getDescription());
		    				itemWithSupplier.setName(itemDTO.getDescription());
		    			}
		    		}
		    	}		    	
		    }else if (StringUtils.hasText(item.getDescription())) {
		    	if(itemDTO.getUserArea().getDivisionCode().trim().equals(IntegrationConstants.NMHG_EMEA)){
		    		itemWithSupplier.setDescription(itemDTO.getDescription(),IntegrationConstants.GB_LOCALE);
		    	}else{
		    		itemWithSupplier.setDescription(itemDTO.getDescription(),IntegrationConstants.US_LOCALE);
		    	}
		    	itemWithSupplier.setDescription(itemDTO.getDescription());
		    	itemWithSupplier.setName(itemDTO.getDescription());
	    	}
    		
    		if(StringUtils.hasText(item.getLongDescription())){
			 itemWithSupplier.setLongDescription(item.getLongDescription());
    		}
			String itemType = item.getItemType();
			if(StringUtils.hasText(itemType)){
			 itemWithSupplier.setItemType(item.getItemType().trim());
			}
			if(StringUtils.hasText(item.getMake())){
			 itemWithSupplier.setMake(item.getMake());
			}
			if(StringUtils.hasText(item.getDivisionCode())){
				itemWithSupplier.setDivisionCode(item.getDivisionCode());
			}
			ItemTypeMapping itemTypeMapping=itemTypeMappingService
            .findByExternalItemType(itemType.trim());
			
            if (itemTypeMapping.getItemType().equalsIgnoreCase(IntegrationConstants.PART_ITEM_TYPE)) {
             if(item.getClassCode()!=null){
               itemWithSupplier.setClassCode(item.getClassCode());
             }
            }
        	if(StringUtils.hasText(item.getServiceCategory())){
        	itemWithSupplier.setServiceCategory(item.getServiceCategory());
        	}/*else{
        		errorMessageCodes
				.put(ItemSyncInterfaceErrorConstants.I0045,
						itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0045));
        	}*/
        	if(item.getServicePart()!=null){
              itemWithSupplier.setServicePart(item.getServicePart() != null ? getBooleanValue(item.getServicePart().toString()) : true);
        	 }
        	if(StringUtils.hasText(item.getMachineCode())){
			 item.setMachineCode(item.getMachineCode());
        	}
			if (StringUtils.hasText(item.getPdiFormName())){
             itemWithSupplier.setPdiFormName(item.getPdiFormName());
			}
			if(StringUtils.hasText(item.getMachineCodeDescription())){
			 itemWithSupplier.setMachineCodeDescription(item.getMachineCodeDescription());
			}
			if(StringUtils.hasText(item.getItemClassCode())){
			 itemWithSupplier.setItemClassCode(item.getItemClassCode());
			}
			if(StringUtils.hasText(item.getMarketingGroupCode())){
			  itemWithSupplier.setMarketingGroupCode(item.getMarketingGroupCode());	
			}
			itemWithSupplier.setSerialized(item.isSerialized());
			//Hari TODO: Move this to private method and call that in another places also
			if(StringUtils.hasText(item.getUom().getType())){
				itemWithSupplier.setUom(ItemUOMTypes.valueOf(ItemUOMTypes.class, getUomValues(item.getUom().getType().toUpperCase())));
			}else{
				itemWithSupplier.setUom(ItemUOMTypes.EACH);
			}
		    if(StringUtils.hasText(item.getDimensionPackageHeight())){
			 itemWithSupplier.setDimensionPackageHeight(item.getDimensionPackageHeight());
		    }
		    if(StringUtils.hasText(item.getDimensionPackageLength())){
			 itemWithSupplier.setDimensionPackageLength(item.getDimensionPackageLength());
		    }
		    if(StringUtils.hasText(item.getDimensionPackageWidth())){
			 itemWithSupplier.setDimensionPackageWidth(item.getDimensionPackageWidth());
		    }
			if (StringUtils.hasText(item.getWeightUom())){
			 itemWithSupplier.setWeightUom(item.getWeightUom());
			}
			if(StringUtils.hasText(item.getWeight())){
			 itemWithSupplier.setWeight(item.getWeight());
			}
			if (isSupplier) {
	         itemWithSupplier.setAlternateNumber(supplierItemNumber.trim().toUpperCase());
			}
			if(itemWithSupplier.getName()==null){
				itemWithSupplier.setName(itemWithSupplier.getNumber());
			}
			/*if(StringUtils.hasText(item.getDescription())){
			 itemWithSupplier.setDescription(item.getDescription());
			}*/
	
       	}

	private void mergeItemMapping(ItemMapping itemMapping,Item item, Item itemWithSupplier,CalendarDate fromDate, CalendarDate toDate,String supplierSitecode,Header itemDTO,SupplierTypeDTO supplierTypeDTO,Map<String,String> errorMessageCodes) {
		if(itemMapping==null){
			itemMapping=new ItemMapping();
		}
		itemMapping.setFromItem(item);
		itemMapping.setToItem(itemWithSupplier);
		if(null!=fromDate){
			itemMapping.setFromDate(fromDate);	
		} else {
			itemMapping.setFromDate(CalendarUtil.convertToCalendarDate(Calendar.getInstance()));
		}
		if(null!=toDate){
			itemMapping.setToDate(toDate);
		} 
		if(null!=supplierSitecode){
			itemMapping.setSupplierSitecode(supplierSitecode);
		}	
		if(supplierTypeDTO.getSupplierLocations()!=null && supplierTypeDTO.getSupplierLocations().getSupplierLocationArray().length > 0 ){
        	itemMapping = setLocationsForItemMapping(supplierTypeDTO,itemMapping,itemDTO,errorMessageCodes);
        }
		//itemMapping.getD().setActive(getStatus(itemDTO.getStatus().getCode().toString()));
		itemMappingService.save(itemMapping);
		
	}

	private void updateBusinessUnit(final Header itemDTO,Map<String,String> errorMessageCodes) {
		if (itemDTO.getUserArea() != null) {
			if (StringUtils.hasText(itemDTO.getUserArea().getDivisionCode())) {
				try {
					/*DivisionBusinessUnitMapping buMapping = businessUnitService
							.findBusinessUnitForDivisionCode(itemDTO
									.getUserArea().getDivisionCode());
					if (buMapping == null) {
						throw new RuntimeException(itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0016)
								"Division Code sent is not mapped in Tavant, Please contact Tavant team for any further information");
					}*/
					SelectedBusinessUnitsHolder
					.setSelectedBusinessUnit(itemDTO.getUserArea().getDivisionCode());
				} catch (Exception e) {
					errorMessageCodes
					.put(ItemSyncInterfaceErrorConstants.I0016,
							itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0016));
				}
			}
		}
	}

	private Item fetchItemForOEMAndProduct(String itemNumber,ItemGroup product) {
		try {
			return catalogService.findItemByItemNumberOwnedByManufAndProduct(itemNumber,product);
		} catch (CatalogException e) {
			//We don't need to handle exception here, the calling method should take care of it.
			return null;
		}
	}
	
	private Item fetchItemForOEM(String itemNumber) {
		try {
			return catalogService.findItemByItemNumberOwnedByManuf(itemNumber);
		} catch (CatalogException e) {
			//We don't need to handle exception here, the calling method should take care of it.
			return null;
		}
	}

	private void createItemWithSupplier(Item oemItem, Item itemWithSupplier,
			Supplier ownedBy, CalendarDate fromDate, CalendarDate toDate, String supplierSitecode,ItemMapping itemMapping, Header itemDTO,SupplierTypeDTO supplierTypeDTO,Map<String,String> errorMessageCodes) {
		setDuplicateAlternateNumber(itemWithSupplier, ownedBy);
		itemWithSupplier.setOwnedBy(ownedBy);
		itemWithSupplier.setMake(ownedBy.getName());
		addBusinessUnit(itemDTO, itemWithSupplier);
		catalogService.createItem(itemWithSupplier);
		createorUpdateMapping(oemItem, itemWithSupplier, fromDate, toDate, supplierSitecode, itemMapping, itemDTO, supplierTypeDTO,errorMessageCodes);
	}
	
	private void createorUpdateMapping(Item item, Item itemWithSupplier,CalendarDate fromDate, CalendarDate toDate,String supplierSitecode,ItemMapping itemMapping, Header itemDTO,SupplierTypeDTO supplierTypeDTO,Map<String,String> errorMessageCodes) {
		ItemMapping itemMappingObj =null;
		if(itemMapping!=null){
			itemMappingObj = itemMapping;
		}else{
			itemMappingObj=new ItemMapping();
		}
		itemMappingObj.setFromItem(item);
		itemMappingObj.setToItem(itemWithSupplier);
		if(null!=fromDate){
			itemMappingObj.setFromDate(fromDate);	
		}
		if(null!=toDate){
			itemMappingObj.setToDate(toDate);
		} 
		if(null!=supplierSitecode){
			itemMappingObj.setSupplierSitecode(supplierSitecode);
		}
		/*if(null!=itemDTO.getStatus().getCode()){
			itemMappingObj.getD().setActive(getDactive(itemDTO.getStatus().getCode().toString()));
		}*/
		if(supplierTypeDTO.getSupplierLocations()!=null && supplierTypeDTO.getSupplierLocations().getSupplierLocationArray().length > 0 ){
        	itemMapping = setLocationsForItemMapping(supplierTypeDTO,itemMappingObj,itemDTO,errorMessageCodes);
        }
			itemMappingService.save(itemMappingObj);
	}

	private void createNewItem(Header itemDTO, Item item) {
		Party supplierOEM = orgService.findOrganizationByName(IntegrationConstants.OEM); // get it from constant for OEM and PART
		setDuplicateAlternateNumber(item, supplierOEM);
		item.setOwnedBy(supplierOEM);
		if(itemDTO.getStatus()!=null){
			item.setStatus(itemDTO.getStatus().getCode().toString().toUpperCase());
		}
        if (item.getItemType().equalsIgnoreCase(IntegrationConstants.PART_ITEM_TYPE) && item.getClassCode() != null) {
            item.getBelongsToItemGroups().add(item.getClassCode());
        } else {
        	if(item.getModel()!=null){
              item.getBelongsToItemGroups().add(item.getModel());
        	}
        }
        addBusinessUnit(itemDTO, item);
        catalogService.createItem(item);
	}
	
	
	private void addBusinessUnit(Header itemDTO, Item item) {

		List<String> businessUnitList = new ArrayList<String>();
		if(item.getItemType().equalsIgnoreCase(IntegrationConstants.MACHINE)||item.getItemType().equalsIgnoreCase(IntegrationConstants.OPTIONS)){
			if(item.getDivisionCode().equalsIgnoreCase(IntegrationConstants.NMHG_EMEA)){
				businessUnitList.add(IntegrationConstants.NMHG_EMEA);
			}else if(item.getDivisionCode().equalsIgnoreCase(IntegrationConstants.NMHG_US)){
				businessUnitList.add(IntegrationConstants.NMHG_US);	
			}
		}else{
		businessUnitList.add(IntegrationConstants.NMHG_EMEA);
		businessUnitList.add(IntegrationConstants.NMHG_US);
		}
		final TreeSet<BusinessUnit> businessUnitMapping = new TreeSet<BusinessUnit>();
		if (!CollectionUtils.isEmpty(businessUnitList)) {
			for (String buName : businessUnitList) {
				BusinessUnit bu = null;
				if (StringUtils.hasText(buName)) {
					bu=businessUnitService.findBusinessUnit(buName.trim());
				}if(bu!=null){
					businessUnitMapping.add(bu);
				}
			}
			if(!businessUnitMapping.isEmpty())
			item.setBusinessUnits(businessUnitMapping);
		}
	}

	private void createNewBrandItem(BrandItem brandItem){
		catalogService.createBrandItem(brandItem);
	}
	
	private void updateItem(Item item) {
		catalogService.updateItem(item);
	}
	
	private void updateBrandItem(BrandItem brandItem) {
		catalogService.updateBrandItem(brandItem);
	}

	private void merge(Header itemDTO, Item item, boolean isSupplier, String supplierItemNumber, boolean isExistingItem,Map<String,String> errorMessageCodes) {
		if(isSupplier){
			item.setNumber(supplierItemNumber.trim().toUpperCase());
		}
		if (itemDTO.getUserArea() != null) {
			item.setLongDescription(itemDTO.getUserArea().getLongDescription());
			String itemType = itemDTO.getType().toString();
			String productCode = itemDTO.getUserArea().getProductCode();
			String modelCode = itemDTO.getUserArea().getModelCode();
			ItemTypeMapping itemTypeMapping=itemTypeMappingService
			                                    .findByExternalItemType(itemType.trim());
			setItemTypeFromMapping(item,itemTypeMapping,errorMessageCodes);
			if (itemTypeMapping.getItemType().equalsIgnoreCase(IntegrationConstants.MACHINE) //TODO: Hari get it from integration constants-done
					|| itemTypeMapping.getItemType()
					.equalsIgnoreCase(IntegrationConstants.OPTIONS)) {
                //Hari TODO: get product type. first of all, the way code should be: check for product family, 
                //Hari TODO: create if not present. Check for product, create if not present, check for model, create if not present 
				setValuesForMachineAndOptions(item,itemDTO,productCode, modelCode, isSupplier,isExistingItem,errorMessageCodes);
			} 
			else{
				setModelFromMapping(item,itemTypeMapping,errorMessageCodes);
			}
			if(!isSupplier){
                item.setMake(itemDTO.getUserArea().getManufName());
			}
			
			if(itemTypeMapping.getItemType().equalsIgnoreCase(IntegrationConstants.PART_ITEM_TYPE)){
	        	setValuesForPartType(item,itemDTO,itemTypeMapping,isSupplier,isExistingItem,errorMessageCodes);
	        }
			
			setCommonValues(item,itemDTO);
			
			//TODO hari: this validate should happen only for particular item type
//			String uom = null;
			//TODO Hari: write method for storing uom details-done
			if(StringUtils.hasText(itemDTO.getUOM())){
				item.setUom(ItemUOMTypes.valueOf(ItemUOMTypes.class, getUomValues(itemDTO.getUOM().trim().toUpperCase())));
			}else{
				item.setUom(ItemUOMTypes.EACH);
			}
			if (itemDTO.getUnitPackaging() != null) {
				setPackageDimensions(itemDTO, item);
			}
			if(StringUtils.hasText(itemDTO.getMarketingGroupCode())){
			  item.setMarketingGroupCode(itemDTO.getMarketingGroupCode());	
			}
			}
			setAlternateItemNumber(itemDTO,item,isSupplier,supplierItemNumber);
		    if(null!=itemDTO.getUserArea().getDescriptions()&& itemDTO.getUserArea().getDescriptions().sizeOfDescriptionArray()!=0){
		    	List<DescriptionTypeDTO> descriptions = Arrays.asList(itemDTO.getUserArea().getDescriptions().getDescriptionArray());
		    	for(DescriptionTypeDTO descriptionTypeDTO:descriptions){
		    		if(null!=descriptionTypeDTO.getDescriptionLanguage()&&!descriptionTypeDTO.getDescriptionLanguage().isEmpty()&& null!=descriptionTypeDTO.getItemDescription()&&!descriptionTypeDTO.getItemDescription().isEmpty()){
		    			item.setDescription(descriptionTypeDTO.getItemDescription(),descriptionTypeDTO.getDescriptionLanguage());
		    			if(itemDTO.getUserArea().getDivisionCode().trim().equals(IntegrationConstants.NMHG_EMEA) && descriptionTypeDTO.getDescriptionLanguage().equals(IntegrationConstants.GB_LOCALE)){
		    				item.setName(descriptionTypeDTO.getItemDescription());
		    				item.setDescription(descriptionTypeDTO.getItemDescription());
		    			}else if(itemDTO.getUserArea().getDivisionCode().trim().equals(IntegrationConstants.NMHG_US) && descriptionTypeDTO.getDescriptionLanguage().equals(IntegrationConstants.US_LOCALE)){
		    				item.setName(descriptionTypeDTO.getItemDescription());
		    				item.setDescription(descriptionTypeDTO.getItemDescription());
		    			}
		    		}else {
		    			if(itemDTO.getDescription()!=null){
		    				if(itemDTO.getUserArea().getDivisionCode().trim().equals(IntegrationConstants.NMHG_EMEA)){
		    					item.setDescription(itemDTO.getDescription(),IntegrationConstants.GB_LOCALE);
		    				}else{
		    					item.setDescription(itemDTO.getDescription(),IntegrationConstants.US_LOCALE);
		    				}
		    				item.setDescription(itemDTO.getDescription());
		    				item.setName(itemDTO.getDescription());
		    			}
		    		}
		    	}    	
		    }
		    else if(itemDTO.getDescription()!=null){
		    	if(itemDTO.getUserArea().getDivisionCode().trim().equals(IntegrationConstants.NMHG_EMEA)){
		    		item.setDescription(itemDTO.getDescription(),IntegrationConstants.GB_LOCALE);
		    	}else{
		    		item.setDescription(itemDTO.getDescription(),IntegrationConstants.US_LOCALE);
		    	}
		    	item.setDescription(itemDTO.getDescription());
		    	item.setName(itemDTO.getDescription());
		    }
		    if(itemDTO.getType().toString().equalsIgnoreCase(IntegrationConstants.PART)){
			    setItemGroupListForPart(item);
		    }
	}
	
	private void setItemGroupListForPart(Item item) {
		List<ItemGroup> modelsList = itemGroupService
				.findItemGroupByCodeAndTypeForPartsItemSync(IntegrationConstants.PARTS_CODE,ItemGroup.MODEL);
				Set<ItemGroup> itemGroupsSet=new HashSet<ItemGroup>();
				itemGroupsSet.addAll(modelsList);
				item.setModel(modelsList.get(0));
				item.setBelongsToItemGroups(itemGroupsSet);
	}

	private void setModelFromMapping(Item item,ItemTypeMapping itemTypeMapping,Map<String,String> errorMessageCodes){
		ItemGroup model = itemGroupService
				.findItemGroupByCodeAndType(IntegrationConstants.PARTS_CODE,ItemGroup.MODEL);
				item.setModel(model);
		/*if(model==null){
			errorMessageCodes
			.put(ItemSyncInterfaceErrorConstants.I0022,
					itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0022)+" "+itemTypeMapping.getItemType());
		}*/
	}
	
	private void setItemTypeFromMapping(Item item,ItemTypeMapping itemTypeMapping,Map<String,String> errorMessageCodes){
		if (itemTypeMapping != null) 
		{
			if (item.getItemType()!= null  && !item.getItemType().equalsIgnoreCase(itemTypeMapping.getItemType().trim()))		  
			{
				errorMessageCodes
				.put(ItemSyncInterfaceErrorConstants.I0018,
						itemSyncInterfaceErrorConstants.getPropertyMessage("I0018",new String[]{item.getItemType(),itemTypeMapping.getItemType().trim()}));
			}
		}
		item.setItemType(itemTypeMapping.getItemType().trim());
	}
	
	private void setCommonValues(Item item,Header itemDTO){
		if (itemDTO.getItemId() != null
				&& StringUtils.hasText(itemDTO.getItemId().getId())) {
			item.setNumber(itemDTO.getItemId().getId().trim().toUpperCase());
		}
		if (itemDTO.getStatus() != null
				&& itemDTO.getStatus().getCode() != null) {
			item.setStatus(itemDTO.getStatus().getCode().toString().toUpperCase());
		}
	
		//TODO: Hari check whey this is not working for all item types, may be its not working for options item type
		if (StringUtils.hasText(itemDTO.getDescription())) {
			//item.setDescription(itemDTO.getDescription().trim());
			item.setName(itemDTO.getDescription().trim());
		}
		item.setDivisionCode(itemDTO.getUserArea().getDivisionCode());
        item.setServicePart(itemDTO.getUserArea().getServicePart() != null ? getBooleanValue(itemDTO.getUserArea().getServicePart().toString()) : true);
		if (StringUtils.hasText(itemDTO.getUserArea().getPDIFormName())){
         item.setPdiFormName(itemDTO.getUserArea().getPDIFormName());
		}
		item.setItemClassCode(itemDTO.getUserArea().getItemClassCode());
		item.setSerialized(getBooleanValue(itemDTO.getUserArea().getSerialStatusEnabled()));
		item.setSpecialOptionStatus(itemDTO.getUserArea().getSpecialOptionStatus());
		item.setSpecialOptionStatusDesc(itemDTO.getUserArea().getSpecialOptionStatusDesc());
		item.setOptionDesigantion(itemDTO.getUserArea().getOptionDesignation());
		item.setModelPower(itemDTO.getUserArea().getModelPower());
		item.setModelPowerDesc(itemDTO.getUserArea().getModelPowerDescr());
		if(itemDTO.getUserArea().getDieselTier()!=null && !itemDTO.getUserArea().getDieselTier().toString().isEmpty())
		item.setDieselTier(itemDTO.getUserArea().getDieselTier().toString());
	}
	
	private void setValuesForMachineAndOptions(Item item, Header itemDTO, String productCode, String modelCode, Boolean isSupplier, Boolean isExistingItem,Map<String,String> errorMessageCodes){
		ItemGroup machine = itemGroupService.findItemGroupByCodeAndType(ItemGroup.MACHINE,ItemGroup.PRODUCT_TYPE);
		ItemGroup productFamily = null;
		ItemGroup product = itemGroupService
		.findItemGroupByCodeAndType(productCode.trim(),ItemGroup.PRODUCT);
		if(product == null){
			throw new RuntimeException(ItemSyncInterfaceErrorConstants.I0019+"##"+itemSyncInterfaceErrorConstants
					.getPropertyMessageFromErrorCode(
							ItemSyncInterfaceErrorConstants.I0019));
		}
		
		if (StringUtils.hasText(itemDTO.getUserArea().getProductFamilyCode())) {
			String productFamilyCode = itemDTO.getUserArea().getProductFamilyCode().trim();
			productFamily = itemGroupService.findProductFamilyForProduct(product,productFamilyCode.toUpperCase());
			//Hari TODO: get product type. first of all, the way code should be: check for product family,
			//Hari TODO: create if not present. Check for product, create if not present, check for model, create if not present 
			if(productFamily == null){
				productFamily = itemGroupService.findItemGroupByCode(productFamilyCode.toUpperCase());
				if(productFamily == null){
					productFamily = new ItemGroup();
					createItemGroupForProductStructure(productFamilyCode,productFamilyCode, machine,
							productFamily, ItemGroup.PRODUCT_FAMILY,errorMessageCodes);
					product.setIsPartOf(productFamily);
				}
			}
		}
		if (StringUtils.hasText(itemDTO.getUserArea().getProductCodeDescription())) {
			product.setName(itemDTO.getUserArea().getProductCodeDescription());
			product.setDescription(itemDTO.getUserArea().getProductCodeDescription()+"-"+itemDTO.getUserArea().getProductCode());
		}
		item.setProduct(product);


		ItemGroup model=null;
		//TODO Hari: provide model description and product description while creating model/product since we are getting it in xml-done
		//TODO Hari: check for product first
		if (StringUtils.hasText(itemDTO.getUserArea().getModelCode())) {
			model = itemGroupService.findModelForProduct(product,modelCode.trim());
			if (model == null) {
				model = new ItemGroup();
				createItemGroupForProductStructure(itemDTO.getUserArea().getModelCodeDescription(),modelCode, product, model, ItemGroup.MODEL,errorMessageCodes);
			}
			if(model.getItemGroupType().equalsIgnoreCase(ItemGroup.MODEL)){
				if (!isSupplier && isExistingItem && item.getModel() != null && !item.getModel().equals(model)) {
					item.getBelongsToItemGroups().remove(item.getModel());
					item.getBelongsToItemGroups().add(model);
				}
				if (StringUtils.hasText(itemDTO.getUserArea().getModelCodeDescription())){
					model.setName(itemDTO.getUserArea().getModelCodeDescription());
					model.setDescription(itemDTO.getUserArea().getModelCodeDescription()+"-"+product.getGroupCode());
				}
				itemGroupService.updateItemGroup(model);
				item.setModel(model);
			} else
			{
				errorMessageCodes
				.put(ItemSyncInterfaceErrorConstants.I0020,
						itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0020)+" "+modelCode);
			}
		}

	}

	
	private void setValuesForPartType(Item item,Header itemDTO,ItemTypeMapping itemTypeMapping,Boolean isSupplier,Boolean isExistingItem,Map<String,String> errorMessageCodes){
		 if (StringUtils.hasText(itemDTO.getUserArea().getClassCode())) {
             ItemGroup partModel = itemGroupService.findItemGroupByCodeAndType(
            		 IntegrationConstants.PARTS_CODE, ItemGroup.MODEL);
             String partClassCode = itemDTO.getUserArea().getClassCode();
             ItemGroup partClass = itemGroupService.findItemGroupByCodeAndIsPartOf(
                                                                         partClassCode.trim(), partModel);
             if (partClass == null) {
                 partClass = new ItemGroup();
                 createItemGroupForProductStructure(itemDTO.getDescription(),partClassCode, partModel, partClass, ItemGroup.PART_CLASS,errorMessageCodes);
             }
             if(partClass.getItemGroupType().equalsIgnoreCase(ItemGroup.PART_CLASS)){
                 if (!isSupplier && isExistingItem && item.getClassCode() != null && !item.getClassCode().equals(partClass)) {
                     item.getBelongsToItemGroups().remove(item.getClassCode());
                     item.getBelongsToItemGroups().add(partClass);
                 }
                 item.setClassCode(partClass);
             } else
             {
            	 errorMessageCodes
     			.put(ItemSyncInterfaceErrorConstants.I0023,
     					itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0023)+" "+partClassCode);
             }
         }
    	if(StringUtils.hasText(itemDTO.getServiceCategory())){
    	item.setServiceCategory(itemDTO.getServiceCategory());
    	}
	}
	
	private void setPackageDimensions(Header itemDTO, Item item){
		UnitPackaging unitPackaging = itemDTO.getUnitPackaging();
		item.setDimensionPackageHeight(unitPackaging.getHeight());
		item.setDimensionPackageLength(unitPackaging.getLength());
		item.setDimensionPackageWidth(unitPackaging.getWidth());
		if (unitPackaging.getWeightQuantity() != null) {
			String weightQuantity = itemDTO.getUnitPackaging()
					.getWeightQuantity();
			item.setWeight(weightQuantity);
		}
	}
	

	private boolean getBooleanValue(String serialized){
		if(serialized.equalsIgnoreCase(IntegrationConstants.TRUE)||serialized.equalsIgnoreCase(IntegrationConstants.YES)){
			return true;
		}
		else
		return false;
	}
	
    private void createItemGroupForProductStructure(String description,String groupCode,
                                                    ItemGroup parentGroup,
                                                    ItemGroup newItemGroup,
                                                    String itemGroupType,Map<String,String> errorMessageCodes) {
    	if(description!=null&&!description.isEmpty()){
    		if(ItemGroup.MODEL.equalsIgnoreCase(itemGroupType)){
	        newItemGroup.setName(description);
	        newItemGroup.setDescription(description+"-"+parentGroup.getGroupCode());
    		}else {
    			newItemGroup.setName(description);
    	        newItemGroup.setDescription(description);
    		}
    	}else{
    		if(ItemGroup.MODEL.equalsIgnoreCase(itemGroupType)){
    			newItemGroup.setName(groupCode.toUpperCase());
    	        newItemGroup.setDescription(groupCode.toUpperCase()+"-"+parentGroup.getGroupCode().toUpperCase());
    		}else{
    		newItemGroup.setName(groupCode.toUpperCase());
	        newItemGroup.setDescription(groupCode.toUpperCase());
    		}
    	}
        newItemGroup.setGroupCode(groupCode.toUpperCase());
        newItemGroup.setIsPartOf(parentGroup);
        newItemGroup.setItemGroupType(itemGroupType);
        itemGroupService.createItemGroupForProductStructure(newItemGroup);
        String buGroupCode = getGroupCodeForBusinessUnit(errorMessageCodes);
        String buName = parentGroup.getBusinessUnitInfo().getName();
        itemGroupService.updateTreeInfo(buGroupCode, IntegrationConstants.PROD_STRUCT_SCHEME, buName); //TODO Hari
    }


	private String getGroupCodeForBusinessUnit(Map<String,String> errorMessageCodes) {
		ItemGroup itemGroup = itemGroupService
				.findItemGroupByType(IntegrationConstants.BUSINESS_UNIT);
		if (itemGroup == null) {
			 errorMessageCodes
  			.put(ItemSyncInterfaceErrorConstants.I0026,
  					itemSyncInterfaceErrorConstants
					.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0026));
		}
		return itemGroup.getGroupCode();
	}

	public void validate(Header itemDTO ,Map<String,String> errorMessageCodes,SyncItemMaster syncItemMaster) {
		if ((itemDTO.getItemId().getId() == null || !StringUtils
				.hasText(itemDTO.getItemId().getId()))
				&& !(IntegrationConstants.SUPPLIER_PARTS_ITEM_TYPE
						.equalsIgnoreCase(itemDTO.getType().toString()) && ((IntegrationConstants.EPO
						.equalsIgnoreCase(syncItemMaster.getApplicationArea()
								.getSender().getLogicalId())) || (IntegrationConstants.PDC
						.equalsIgnoreCase(syncItemMaster.getApplicationArea()
								.getSender().getLogicalId()))))) {
			errorMessageCodes
					.put(ItemSyncInterfaceErrorConstants.I001,
							itemSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I001));
		}
		//hari: check it properly
		if (itemDTO.getType()==null||!StringUtils.hasText(itemDTO.getType().toString())) {
			errorMessageCodes.put(ItemSyncInterfaceErrorConstants.I002,itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I002));
		}
		if (itemDTO.getType()!=null&&!StringUtils.hasText(itemDTO.getType().toString())) {
		ItemTypeMapping itemTypeMapping = itemTypeMappingService.findByExternalItemType(itemDTO.getType().toString().trim());
		if (itemTypeMapping == null) {
			errorMessageCodes.put(ItemSyncInterfaceErrorConstants.I0010,itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0010));
		} 
		}
		//TO DO for parts Item sync
		if(itemDTO.getType()!=null&&!itemDTO.getType().toString().equalsIgnoreCase(IntegrationConstants.SUPPLIER_PARTS_ITEM_TYPE)){
	  		if (itemDTO.getDescription() == null || !StringUtils.hasText(itemDTO.getDescription())) {
	  			errorMessageCodes.put(ItemSyncInterfaceErrorConstants.I004,itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I004));
	  		}
	  		if(itemDTO.getType().toString().equalsIgnoreCase(IntegrationConstants.MACHINE)){  
	  			validateMachine(itemDTO,errorMessageCodes);
	    	}
	  		
	     }
    }
	
	private void validateMachine(Header itemDTO,Map<String,String> errorMessageCodes){
		if (itemDTO.getUserArea().getProductCode() == null
				|| !StringUtils.hasText(itemDTO.getUserArea().getProductCode())) {
			errorMessageCodes.put(ItemSyncInterfaceErrorConstants.I007,itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I007));
		}
	
		if (itemDTO.getUserArea().getModelCode() == null
				|| !StringUtils.hasText(itemDTO.getUserArea().getModelCode())) {
			errorMessageCodes.put(ItemSyncInterfaceErrorConstants.I008,itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I008));
		 }
	}

	private void setDefaultValues(Header itemDTO,Map<String,String> errorMessageCodes){
		 if(itemDTO.getUserArea().getSerialStatusEnabled()==null){
        	 itemDTO.getUserArea().setSerialStatusEnabled("Y");
         }
		 if (itemDTO.getUserArea().getManufName() == null
	  				|| !StringUtils.hasText(itemDTO.getUserArea().getManufName())) {
	  			itemDTO.getUserArea().setManufName(IntegrationConstants.NMHG); 
	  	}
		if ((itemDTO.getUserArea().getDivisionCode() == null || !StringUtils
				.hasText(itemDTO.getUserArea().getDivisionCode()))
				&& (itemDTO.getType().toString().equalsIgnoreCase(
						IntegrationConstants.MACHINE) || itemDTO.getType()
						.toString().equalsIgnoreCase(
								IntegrationConstants.OPTIONS))) {
			errorMessageCodes.put(ItemSyncInterfaceErrorConstants.I0051,
					itemSyncInterfaceErrorConstants.getPropertyMessage("I0051",
							new String[] { IntegrationConstants.MACHINE }));
		}
		 if(itemDTO.getUserArea().getDivisionCode() != null&&StringUtils.hasText(itemDTO.getUserArea().getDivisionCode())&&((itemDTO.getType().toString()
					.equalsIgnoreCase(IntegrationConstants.MACHINE))||itemDTO.getType()
					.toString().equalsIgnoreCase(
							IntegrationConstants.OPTIONS))){
	  		 if (itemDTO.getUserArea().getDivisionCode()
					.equalsIgnoreCase(IntegrationConstants.US)) {
	  		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(IntegrationConstants.NMHG_US);
	  		 }else if(itemDTO.getUserArea().getDivisionCode()
					.equalsIgnoreCase(IntegrationConstants.EMEA)){
	  			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(IntegrationConstants.NMHG_EMEA); 
	  		 }
	  	}else if (itemDTO.getUserArea().getDivisionCode() == null
						|| !StringUtils.hasText(itemDTO.getUserArea().getDivisionCode())) {
					itemDTO.getUserArea().setDivisionCode(IntegrationConstants.EMEA); //Hari TODO: get it from constant 003 (NMHG US) and 056 (NMHG EMEA)-done
	  	}
		 if(itemDTO.getType().toString().equals(IntegrationConstants.PART_ITEM_TYPE)){
			 SelectedBusinessUnitsHolder.setSelectedBusinessUnit(IntegrationConstants.NMHG_EMEA); 
		 }
		 if ((itemDTO.getUserArea().getDivisionCode() != null && !itemDTO.getType().toString().equals(IntegrationConstants.PART_ITEM_TYPE) && (!(itemDTO
					.getUserArea().getDivisionCode().equalsIgnoreCase(IntegrationConstants.US) || itemDTO
					.getUserArea().getDivisionCode().equalsIgnoreCase(IntegrationConstants.EMEA))))) {
				errorMessageCodes.put(ItemSyncInterfaceErrorConstants.I0027,itemSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0027));
			} else if (itemDTO.getUserArea().getDivisionCode()
					.equalsIgnoreCase(IntegrationConstants.US)) {
				itemDTO.getUserArea().setDivisionCode(IntegrationConstants.NMHG_US);
			} else if (itemDTO.getUserArea().getDivisionCode()
					.equalsIgnoreCase(IntegrationConstants.EMEA)) {
				itemDTO.getUserArea().setDivisionCode(IntegrationConstants.NMHG_EMEA);
			}
	}
	
	public void setDuplicateAlternateNumber(Item item, Party ownedBy){
		 List<Item> itemList = catalogService.findItemsByAlternateItemNumber(item
				.getAlternateNumber(), ownedBy.getId());
		if (itemList == null || itemList.size() == 0) { //TODO Hari
			item.setDuplicateAlternateNumber(false);
		} else {
			for (Item duplicateitem : itemList) {
				duplicateitem.setDuplicateAlternateNumber(true);
				catalogService.updateItem(duplicateitem);
			}
			item.setDuplicateAlternateNumber(true);
		}
	}
	
	public void setAlternateItemNumber(Header itemDTO, Item item, boolean isSupplier, String supplierItemNumber) {
		if (isSupplier) {
            item.setAlternateNumber(supplierItemNumber.trim().toUpperCase());
		} else {
            item.setAlternateNumber(itemDTO.getItemId().getId().trim().toUpperCase());
		}
	}
	
	private String getUomValues(String uom){
		if(uom.equals("SQUARE METER")){
			uom ="SQUARE_METER";
		}else if(uom.equals("SQUARE FEET")){
			uom="SQUARE_FEET";
		}else if(uom.equals("PACK OF 2")){
			uom="PACK_OF_2";
		}else if(uom.equals("PACK OF 4")){
			uom="PACK_OF_4";
		}else if(uom.equals("PACK OF 5")){
			uom="PACK_OF_5";
		}else if(uom.equals("PACK OF 6")){
			uom="PACK_OF_6";
		}else if(uom.equals("PACK OF 8")){
			uom="PACK_OF_8";
		}else if(uom.equals("PACK OF 10")){
			uom="PACK_OF_10";
		}else if(uom.equals("PACK OF 12")){
			uom="PACK_OF_12";
		}else if(uom.equals("PACK OF 25")){
			uom="PACK_OF_25";
		}else if(uom.equals("PACK OF 50")){
			uom="PACK_OF_50";
		}
		else if(uom.equalsIgnoreCase("US GALLON")){
			uom="US_GALLON";
		}else if(uom.equalsIgnoreCase("CUBIC CENTIMETER")){
			uom="CUBIC_CENTIMETER";
		}else if(uom.equalsIgnoreCase("FEET/FOOT")){
			uom="FEET_FOOT";
		}else if(uom.equalsIgnoreCase("AS REQUIRED")){
			uom="AS_REQUIRED";
		}else if(uom.equalsIgnoreCase("CUBIC METER")){
			uom="CUBIC_METER";
		}else if(uom.equalsIgnoreCase("METRIC TONNE")){
			uom="METRIC_TONNE";
		}
		return uom;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public ItemGroupService getItemGroupService() {
		return itemGroupService;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	public ItemMappingService getItemMappingService() {
		return itemMappingService;
	}

	public void setItemMappingService(ItemMappingService itemMappingService) {
		this.itemMappingService = itemMappingService;
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public SupplierService getSupplierService() {
		return supplierService;
	}

	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public ItemSyncInterfaceErrorConstants getItemSyncInterfaceErrorConstants() {
		return itemSyncInterfaceErrorConstants;
	}

	public void setItemSyncInterfaceErrorConstants(
			ItemSyncInterfaceErrorConstants itemSyncInterfaceErrorConstants) {
		this.itemSyncInterfaceErrorConstants = itemSyncInterfaceErrorConstants;
	}

	public void setItemTypeMappingService(
			ItemTypeMappingService itemTypeMappingService) {
		this.itemTypeMappingService = itemTypeMappingService;
	}

	public void setBusinessUnitService(BusinessUnitService businessUnitService) {
		this.businessUnitService = businessUnitService;
	}
	
	private boolean getDactive(String status){
		if(status.equalsIgnoreCase(IntegrationConstants.ACTIVESTATUS)){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	private boolean getStatus(String status){
		if(status.equalsIgnoreCase(IntegrationConstants.ACTIVESTATUS)){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
