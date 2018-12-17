package tavant.twms.integration.layer.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import tavant.oagis.ItemMappingsDocumentDTO.ItemMappings;
import tavant.oagis.OEMXREFDocumentDTO.OEMXREF;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.supplier.ItemMapping;
import tavant.twms.domain.supplier.ItemMappingService;

public class SyncOEMXReference {
	private static Logger logger = Logger.getLogger(SyncOEMXReference.class
			.getName());

	private CatalogService catalogService;

	private OrgService orgService;

	private ItemMappingService itemMappingService;

	private TransactionTemplate tt;
	
	private ItemGroupService itemGroupService;

	public List<SyncResponse> sync(final OEMXREF oemXRef) {
		if (logger.isDebugEnabled()) {
			logger.debug("Received " + oemXRef.getItemMappingsArray().length
					+ " OEMXRefs for synchronising.");
		}

		final String dealerNumber = oemXRef.getCustomerNumber();
		List<SyncResponse> responses = new ArrayList<SyncResponse>();

		SyncResponse response = new SyncResponse();
		String customerNumber = oemXRef.getCustomerNumber();
		response.setBusinessId(customerNumber);
		response.setUniqueIdName("Customer Number");
		response.setUniqueIdValue(customerNumber);
		try {
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(
						TransactionStatus transactionStatus) {
					sync(oemXRef,dealerNumber);
				}
			});
			response.setSuccessful(true);
		} catch (RuntimeException e) {
			logger.error(e, e);
			response.setSuccessful(false);
			String message = e.getMessage();
			response.setException(new StringBuilder().append(message).append(
					" Error syncing OEMXRef with Customer Id ").append(
							dealerNumber).toString());
			response.setErrorCode(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
			response.setErrorType(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		}
		responses.add(response);

		return responses;
	}

	private String createSuffixedPartNumber(ServiceProvider dealer,
			String oemXRefNumber) {
		String dealerName = dealer.getName();
		if(dealerName.length() > 3){
			oemXRefNumber = oemXRefNumber + "-" + dealerName.substring(0, 3).toUpperCase() + "-" + dealer.getDealerNumber();
		}else{
			oemXRefNumber = oemXRefNumber + "-" + dealerName.toUpperCase() + "-" + dealer.getDealerNumber();
		}
		return oemXRefNumber;
	}
	
	public void sync(final OEMXREF oemXRef, final String dealerNumber) {
		ItemMappings[] mappings = oemXRef.getItemMappingsArray();
		try {
			ServiceProvider dealer = orgService.findDealerByNumber(dealerNumber);
			if (dealer == null) {
				throw new RuntimeException("Dealer with Number " + dealerNumber
						+ " not found");
			}
			for (final ItemMappings mapping : mappings) {

				Item item = catalogService.findItemByItemNumberOwnedByManuf(mapping.getItemNumber());
				if (item == null) {
					throw new RuntimeException("Item with Number "
							+ mapping.getItemNumber() + " not found");
				}
				
				Item oemItem = item.cloneMe();
				oemItem.setNumber(mapping.getOEMItemNumber());
				
//				if(isSuffixNeeded(mapping)){
//					oemItem.setNumber(createSuffixedPartNumber(dealer, mapping.getOEMItemNumber().trim()));
//				}
				try{
					Item existingOemItem = catalogService.findItemByItemNumberOwnedByServiceProvider(oemItem.getNumber(),dealer.getId());
					if(existingOemItem != null){
						catalogService.updateItem(existingOemItem);
						return;
					}
				}catch(CatalogException ce){
					//Does not exist. Hence continue to create one.
				}
				oemItem.setOwnedBy(dealer);
				catalogService.createItem(oemItem);
				
				ItemGroup itemGrp = itemGroupService.findById(oemItem.getModel().getId());
//				itemGrp.getIncludedItems().add(oemItem);
				oemItem.getBelongsToItemGroups().add(itemGrp);
				try {
					itemGroupService.update(itemGrp);
				} catch (Exception e) {
					throw new RuntimeException(" There was an error syncing oemXRef : "+oemItem.getNumber()+". Error is : "+e.getMessage());
				}

				ItemMapping itemMapping = new ItemMapping();
				itemMapping.setFromItem(item);
				itemMapping.setToItem(oemItem);
				itemMappingService.save(itemMapping);
			}
		} catch (CatalogException e) {
			throw new RuntimeException(e);
		}

	}

	private boolean isSuffixNeeded(final ItemMappings mapping) {
		return mapping.getItemNumber().trim().equalsIgnoreCase(mapping.getOEMItemNumber().trim());
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setTt(TransactionTemplate tt) {
		this.tt = tt;
	}

	public void setItemMappingService(ItemMappingService itemMappingService) {
		this.itemMappingService = itemMappingService;
	}

	public void setTransactionTemplate(TransactionTemplate tt) {
		this.tt = tt;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

}
