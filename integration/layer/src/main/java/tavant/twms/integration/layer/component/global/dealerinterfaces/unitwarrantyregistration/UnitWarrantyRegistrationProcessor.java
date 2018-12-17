package tavant.twms.integration.layer.component.global.dealerinterfaces.unitwarrantyregistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyAudit;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.warranty.WarrantyUtil;

public class UnitWarrantyRegistrationProcessor {

	private static final Logger logger = Logger.getLogger("dealerAPILogger");

	private WarrantyService warrantyService;

	private WarrantyUtil warrantyUtil;
		
	private InventoryService inventoryService;
	
	private InventoryTransactionService invTransactionService;
	
	private ConfigParamService configParamService;
	
	
	private Boolean isManualApprovalNeeded = Boolean.FALSE;
		
	public final ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public final void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public InventoryTransactionService getInvTransactionService() {
		return invTransactionService;
	}

	public void setInvTransactionService(InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
	}

	public final WarrantyUtil getWarrantyUtil() {
		return warrantyUtil;
	}

	public final void setWarrantyUtil(WarrantyUtil warrantyUtil) {
		this.warrantyUtil = warrantyUtil;
	}
	
	public final InventoryService getInventoryService() {
		return inventoryService;
	}

	public final void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public WarrantyService getWarrantyService() {
		return warrantyService;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public void register(Warranty warranty, Map<String, String[]> errorCodeMap) {

		List<InventoryItem> itemsForTask = prepareWarranty(warranty);
		
		if(this.isManualApprovalNeeded){
			warranty.setStatus(WarrantyStatus.SUBMITTED);
		}
		else{
			warranty.setStatus(WarrantyStatus.ACCEPTED);
		}
		
		try {
			warrantyService.submitWarrantyReport(itemsForTask);
			warrantyService.processWarrantyTransitionAdmin(itemsForTask);
			
		} catch (PolicyException pe) {
			errorCodeMap.put("dealerAPI.warrantyRegistration.errorInSavingWarranty", null);
		}	

	}

	public void saveDraft(Warranty warranty, Map<String, String[]> errorCodeMap) {

		List<InventoryItem> itemsForTask = prepareWarranty(warranty);
		try {
			warrantyService.submitWarrantyReport(itemsForTask);			
			
		} catch (PolicyException pe) {
			errorCodeMap.put("dealerAPI.warrantyRegistration.errorInSavingWarrantyDraft", null);
		}
	}
	
	private List<InventoryItem> prepareWarranty(Warranty warranty) {		
		
		String multiDRETRNumber = null;
		if (!warranty.getForItem().getPendingWarranty()) {
			multiDRETRNumber = warrantyService.getWarrantyMultiDRETRNumber();
		}
		warranty.setMultiDRETRNumber(multiDRETRNumber);
		
		if (warranty.isDraft()) {
			warranty.getForItem().setPendingWarranty(true);
		}
		
		warranty.setDeliveryDate(warranty.getForItem().getDeliveryDate());
		warranty.setTransactionType(invTransactionService.getTransactionTypeByName("DR"));		
		
		List<InventoryItem> itemsForTask = new ArrayList<InventoryItem>();
		InventoryItem inventoryItem = warranty.getForItem();
		if(inventoryItem.getCurrentOwner().getId() != warrantyUtil.getLoggedInUser().getBelongsToOrganization().getId()  && this.warrantyUtil.canPerformD2D()) {			
			inventoryItem = this.warrantyUtil.performD2D(inventoryItem, warrantyUtil.getLoggedInUser().getBelongsToOrganization(), false);
		}		
		itemsForTask.add(inventoryItem);
		
		isManualApprovalNeeded = this.configParamService.getBooleanValue(ConfigName.MANUAL_APPROVAL_FLOW_FOR_DR.getName());
		if(!isManualApprovalNeeded){
			warranty.getForItem().setFleetNumber(warranty.getFleetNumber());	
			warranty.getForItem().setVinNumber(warranty.getEquipmentVIN());
			warranty.getForItem().setOperator(warranty.getOperator());
			warranty.getForItem().setInstallingDealer(warranty.getInstallingDealer());
			warranty.getForItem().setOem(warranty.getOem());
			
			warranty.getForItem().setInstallationDate(warranty.getInstallationDate());
		}		
		return itemsForTask;
	}	
}