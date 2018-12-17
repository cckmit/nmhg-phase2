package tavant.twms.integration.layer.component.global.InstallBase;

import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO;
import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.integration.layer.constants.InstallBaseSyncInterfaceErrorConstants;
import tavant.twms.integration.layer.constants.IntegrationConstants;

public class InstallBaseValidator  {

    private CatalogService catalogService;
    
    private ItemGroupService itemGroupService;
    
	private InstallBaseSyncInterfaceErrorConstants installBaseSyncInterfaceErrorConstants;

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    private OrgService orgService;

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }
    public InstallBaseSyncInterfaceErrorConstants getInstallBaseSyncInterfaceErrorConstants() {
		return installBaseSyncInterfaceErrorConstants;
	}

	public void setInstallBaseSyncInterfaceErrorConstants(
			InstallBaseSyncInterfaceErrorConstants installBaseSyncInterfaceErrorConstants) {
		this.installBaseSyncInterfaceErrorConstants = installBaseSyncInterfaceErrorConstants;
	}

	public void validate(InventorySync inventorySync ,boolean createInventoryItem,final Map<String,String> errorMessageCodes) {
		validateCommonFields(inventorySync, errorMessageCodes);
		if (inventorySync.getSyncType() != null
				&& (IntegrationConstants.MACHINE_SALE
						.equalsIgnoreCase(inventorySync.getSyncType()
								.toString()) && createInventoryItem==true)) {
			validateStockFields(inventorySync, errorMessageCodes);
			validateMecineSaleAndRmaFileds(inventorySync, errorMessageCodes);
			validateOptions(inventorySync, errorMessageCodes);
			if(inventorySync.getPreOrderBooking()){
				validateCustomerInfo(inventorySync.getCustomerInfo(), errorMessageCodes);
			}
		}
		if (errorMessageCodes!=null && errorMessageCodes.size()>0) {
			throw new RuntimeException(errorMessageCodes.toString());
		}
	}
	
	public void validateForRetailedInventory(InventorySync inventorySync ,boolean createInventoryItem,final Map<String,String> errorMessageCodes) {
		
		validateCustomerInfo(inventorySync.getCustomerInfo(), errorMessageCodes);	
		validateRetailedCustomer(inventorySync.getCustomerInfo(), errorMessageCodes);	
		ServiceProvider serviceProvider = null;
		if (!StringUtils.hasText(inventorySync.getInstallingDealerNumber())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0064, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0064));
		} else {			
					serviceProvider = orgService.findServiceProviderByNumber(inventorySync
							.getInstallingDealerNumber().trim());
			if (serviceProvider == null) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0065, installBaseSyncInterfaceErrorConstants.getPropertyMessage(InstallBaseSyncInterfaceErrorConstants.I0065,
						new String[]{inventorySync.getInstallingDealerNumber()}));
				}
		}
		if (errorMessageCodes!=null && errorMessageCodes.size()>0) {
			throw new RuntimeException(errorMessageCodes.toString());
		}
		}
	
	public void validateCommonFields(
			InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync inventorySync,final Map<String,String> errorMessageCodes) {
		if (inventorySync.getDivisionCode() == null||!StringUtils.hasText(inventorySync.getDivisionCode())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I001, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I001));
		}

		if (inventorySync.getSyncType() == null ||!StringUtils.hasText(inventorySync.getSyncType().toString())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I002, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I002));
		}
		if (inventorySync.getProductCode() == null||!StringUtils.hasText(inventorySync.getProductCode())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0059, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0059));
		}
		if (inventorySync.getModelCode() == null||!StringUtils.hasText(inventorySync.getModelCode())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0060, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0060));
		}
		if (inventorySync.getInventoryDetail()==null||inventorySync.getInventoryDetail().getItemNumber() == null
				|| !StringUtils.hasText(inventorySync.getInventoryDetail()
						.getItemNumber())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I003, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I003));
		} else {
			Item item = getItem(inventorySync.getInventoryDetail()
					.getItemNumber().trim(),inventorySync.getProductCode(),errorMessageCodes);
			if(item==null){
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0044,installBaseSyncInterfaceErrorConstants.getPropertyMessage(
							InstallBaseSyncInterfaceErrorConstants.I0044,
							new String[] { inventorySync.getInventoryDetail()
					.getItemNumber().trim() }));
			}else{
			if (IntegrationConstants.PART.equalsIgnoreCase(item.getItemType())) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I004, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I004));
			}
			}
		}
		if (inventorySync.getInventoryDetail()==null||!StringUtils.hasText(inventorySync.getInventoryDetail()
				.getSerialNumber())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I005, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I005));
		}
		if (inventorySync.getInventoryDetail()==null||!StringUtils.hasText(inventorySync.getInventoryDetail()
				.getMktGroupCode())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I006, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I006));
		}
		if (!StringUtils.hasText(inventorySync.getBuildPlant())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0016, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0016));
			if (inventorySync.getSalesOrderNumber() == null
					|| !StringUtils
							.hasText(inventorySync.getSalesOrderNumber())) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0056, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0056));
			}

		}
		validateComponents(inventorySync, errorMessageCodes);
	}

	private void validateStockFields(InventorySync inventorySync,final Map<String,String> errorMessageCodes) {
		if (inventorySync.getPreOrderBooking()) {
			validateCustomerInfo(inventorySync.getCustomerInfo(), errorMessageCodes);
		}
		ServiceProvider serviceProvider = null;
		if (!StringUtils.hasText(inventorySync.getShipToNumber())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0025, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0025));
		} else {
			serviceProvider = orgService
					.findServiceProviderByNumber(inventorySync
							.getShipToNumber().trim());
			if (serviceProvider == null) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0026, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0026));
			}
		}
		if (!StringUtils.hasText(inventorySync.getShipToLocation())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0027, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0027));
		} 
	}

	private void validateCustomerInfo(InventorySync.CustomerInfo customerInfo,final Map<String,String> errorMessageCodes) {
		if (customerInfo == null) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0017, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0017));
		}else{
			if (!StringUtils.hasText(customerInfo.getCustomerNumber())) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0018, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0018));
			}
			if (!StringUtils.hasText(customerInfo.getFirstName())) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0019, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0019));
			}
			if (!StringUtils.hasText(customerInfo.getAddressLine1())) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0020, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0020));
			}
			if (!StringUtils.hasText(customerInfo.getCity())) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0021, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0021));
			}
			/*if (!StringUtils.hasText(customerInfo.getState())) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0022, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0022));
			}*/
			if (!StringUtils.hasText(customerInfo.getCountry())) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0023, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0023));
			}
			/*if (!StringUtils.hasText(customerInfo.getSICode())) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0024, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0024));
			}*/
		}
	}

	
	private void validateRetailedCustomer(InventorySync.CustomerInfo customerInfo,final Map<String,String> errorMessageCodes) {
		if (customerInfo == null) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0017, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0017));
		}else{
			
			if (!StringUtils.hasText(customerInfo.getState())) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0022, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0022));
			}
			
			if (!StringUtils.hasText(customerInfo.getSICode())) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0024, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0024));
			}
			
			if (!StringUtils.hasText(customerInfo.getCountyCode())&&(StringUtils.hasText(customerInfo.getCountry())&&customerInfo.getCountry().toString().equalsIgnoreCase("US"))) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0067, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0067));
			}
		}
	}

	private void validateOptions(
			InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync inventorySyncTypeDTO,final Map<String,String> errorMessageCodes) {
		if (inventorySyncTypeDTO.getOptions() != null
				&& inventorySyncTypeDTO.getOptions().getOptionArray() != null
				&& inventorySyncTypeDTO.getOptions().getOptionArray().length > 0) {
			for (InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync.Options.Option optionDTO : inventorySyncTypeDTO
					.getOptions().getOptionArray()) {
				if (optionDTO.getOptionCode() == null
						|| !StringUtils.hasText(optionDTO.getOptionCode())) {
					if (optionDTO.getOptionType() != null
							&& !optionDTO
									.getOptionType()
									.equals(InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync.Options.Option.OptionType.D)) {
						errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0035, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0035));
					}
				}
				if (optionDTO.getOrderOptionLineNumber() == null
						|| !StringUtils.hasText(optionDTO
								.getOrderOptionLineNumber())) {
					errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0036, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0036));
				}
				if (optionDTO.getOptionType() == null) {
					errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0037, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0037));
				}
				if (optionDTO.getOptionDescription() == null
						|| !StringUtils.hasText(optionDTO
								.getOptionDescription())) {
					errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0038, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0038));
				}
				if (optionDTO.getActiveInactiveStatus() == null
						|| !StringUtils.hasText(optionDTO
								.getActiveInactiveStatus())) {
					errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0039, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0039));
				}

			}
		}
	}

	private void validateComponents(
			InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync inventorySyncTypeDTO,final Map<String,String> errorMessageCodes) {
		if (inventorySyncTypeDTO.getComponents() != null
				&& inventorySyncTypeDTO.getComponents().getComponentArray() != null
				&& inventorySyncTypeDTO.getComponents().getComponentArray().length > 0) {
			for (InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync.Components.Component componentDTO : inventorySyncTypeDTO
					.getComponents().getComponentArray()) {
				if (componentDTO.getSequenceNumber() == null
						|| !StringUtils.hasText(componentDTO
								.getSequenceNumber())) {
					errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0029, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0029));
				}
				if (componentDTO.getComponentSerialType() == null
						|| !StringUtils.hasText(componentDTO
								.getComponentSerialType())) {
					errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0030, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0030));
				}
				if (componentDTO.getSerialTypeDescription() == null
						|| !StringUtils.hasText(componentDTO
								.getSerialTypeDescription())) {
					errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0031, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0031));
				}
				if (componentDTO.getManufacturer() == null
						|| !StringUtils.hasText(componentDTO.getManufacturer())) {
					errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0032, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0032));
				}
				if (componentDTO.getComponentPartSerialNumber() == null
						|| !StringUtils.hasText(componentDTO
								.getComponentPartSerialNumber())) {
					errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0033, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0033));
				}
				if (componentDTO.getComponentPartNumber() == null
						|| !StringUtils.hasText(componentDTO
								.getComponentPartNumber())) {
					errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0034, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0034));
				}
			}
		}
	}

	private Item getItem(String number,String productCode,Map<String,String> errorMessageCodes) {
		Item item=null;
		try {
			ItemGroup product = itemGroupService
					.findItemGroupByCodeAndType(productCode.trim(),ItemGroup.PRODUCT);
			if(product!=null){
			item = catalogService.findItemByItemNumberOwnedByManufAndProduct(number.trim(),product);
			}else{
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0061,installBaseSyncInterfaceErrorConstants.getPropertyMessage(
						InstallBaseSyncInterfaceErrorConstants.I0061,
						new String[] {productCode }));
			}
		} catch (CatalogException e) {
			/*throw new RuntimeException(
					installBaseSyncInterfaceErrorConstants.getPropertyMessage(
							InstallBaseSyncInterfaceErrorConstants.I0044,
							new String[] { number }), e);*/
			item=null;
		}
		return item;
	}
	
	private void validateMecineSaleAndRmaFileds(InventorySync inventorySync,final Map<String,String> errorMessageCodes) {
		if (!StringUtils.hasText(inventorySync.getShipFromOrgCode())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I007, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I007));
		}
		if (inventorySync.getDeliveryDateTime() == null) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I008, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I008));
		}
		/*if (inventorySync.getRegistrationDate() == null) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I009, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I009));
		}*/
		if (inventorySync.getBillToPurchaseOrder() == null
				|| !StringUtils.hasText(inventorySync.getBillToPurchaseOrder())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0010, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0010));
		}
		if (inventorySync.getOrderReceivedDate() == null) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0011, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0011));
		}
		if (inventorySync.getActualCTSDate() == null) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0012, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0012));
		}
		if (!StringUtils.hasText(inventorySync.getOrderType())) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0013, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0013));
		}
		if (inventorySync.getMDECapacity() == null) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0014, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0014));
		}
		if (inventorySync.getModelPower() == null) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0015, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0015));
		}
		if (inventorySync.getBrandType() == null) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0057, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0057));
		}
		if (inventorySync.getShipToLocation() == null||inventorySync.getShipToLocation().isEmpty()) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0027, installBaseSyncInterfaceErrorConstants.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0027));
		}
	}
	public ItemGroupService getItemGroupService() {
		return itemGroupService;
	}
	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

}
