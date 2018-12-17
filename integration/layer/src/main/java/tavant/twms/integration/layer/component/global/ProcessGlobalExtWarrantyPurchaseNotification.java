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

package tavant.twms.integration.layer.component.global;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import tavant.globalsync.extendedwarrantynotification.ExtWarrantyNotificationDocumentDTO;
import tavant.globalsync.extendedwarrantynotification.PurchaseNotificationTypeDTO;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.inventory.*;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.*;
import tavant.twms.infra.BaseDomain;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import static tavant.twms.integration.layer.util.CalendarUtil.convertToCalendarDate;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * 
 * 
 */

public class ProcessGlobalExtWarrantyPurchaseNotification {
			
	private TransactionTemplate transactionTemplate;
	
	private WarrantyService warrantyService;
	
	private InventoryService inventoryService;
	
	private InventoryItemRepository inventoryItemRepository;
	
	private PolicyDefinitionRepository policyDefinitionRepository;
	
	private ExtWarrantyPurchase extWarrantyPurchaseMemo;
	
	private InventoryTransactionService invTransactionService;

    private OrgService orgService;

    private CalendarDate calendarDate;
	
	CatalogService catalogService;

    private static Logger logger = Logger.getLogger(ProcessGlobalExtWarrantyPurchaseNotification.class
            .getName());
	
	public List<SyncResponse> sync(final ExtWarrantyNotificationDocumentDTO extnWntyNotificationDocumentDTO) {

        PurchaseNotificationTypeDTO purchaseNotificationTypeDTO = extnWntyNotificationDocumentDTO
                                                .getExtWarrantyNotification().getDataArea().getPurchaseNotification();
        if (logger.isDebugEnabled()) {
            logger.debug("Received Purchase Notification for serial number " + purchaseNotificationTypeDTO.
            		getSerialNumber());
        }
        List<SyncResponse> responses = new ArrayList<SyncResponse>();
    	SyncResponse response = new SyncResponse();    	    	

		try{	    				    	    	   	
			response.setUniqueIdName("SerialNumber|EquipmentItemNumber|WarrantyPlanCode");
	    	response.setUniqueIdValue(String.valueOf(purchaseNotificationTypeDTO.getSerialNumber()+" | " 
	    			+purchaseNotificationTypeDTO.getEquipmentItemNumber()+" | "
	    			+purchaseNotificationTypeDTO.getWarrantyPlanCode()));
	    	response.setBusinessId(String.valueOf(purchaseNotificationTypeDTO.getEquipmentItemNumber()));    	
			validate(extnWntyNotificationDocumentDTO, response);
			transactionTemplate.execute(new TransactionCallbackWithoutResult(){
			@Override
			protected void doInTransactionWithoutResult(
				TransactionStatus status) {
				extWarrantyPurchaseMemo = transform(extnWntyNotificationDocumentDTO);		
				sync(extWarrantyPurchaseMemo);
				}
			});
		}catch(Exception e){
			 logger.error(e, e);
				response = buildErrorResponse(response, e.getMessage(),
                        purchaseNotificationTypeDTO.getInvoiceNumber(),
                        purchaseNotificationTypeDTO.getSerialNumber(),
                        purchaseNotificationTypeDTO.getWarrantyPlanCode());
         } finally {
             responses.add(response);
         }            
         return responses;
	}

	private void validate(
			final ExtWarrantyNotificationDocumentDTO extnWntyNotificationDocumentDTO, SyncResponse response) {
        PurchaseNotificationTypeDTO purchaseNotificationTypeDTO = extnWntyNotificationDocumentDTO
                                                .getExtWarrantyNotification().getDataArea().getPurchaseNotification();
        StringBuilder errorMessage = new StringBuilder();
		if(!(StringUtils.hasText(purchaseNotificationTypeDTO.getNotificationType()))){					
			appendErrorMessage(errorMessage, "Notification Type can't be Null");					
		}    	    	
    	
		if(!StringUtils.hasText(purchaseNotificationTypeDTO.getSerialNumber())){
    		appendErrorMessage(errorMessage, " Serial Number can't be Null");    		
    	}
    	
    	if(!StringUtils.hasText(purchaseNotificationTypeDTO.getDealerNumber())){
    		appendErrorMessage(errorMessage, " Dealer Number can't be Null");
    	}
    	if(!StringUtils.hasText(purchaseNotificationTypeDTO.getEquipmentItemNumber())){
    		appendErrorMessage(errorMessage, " Equipment Item Number can't be Null");    		
    	}
        if(!StringUtils.hasText(extnWntyNotificationDocumentDTO.getExtWarrantyNotification().getDataArea().getBUName())){
            if (!StringUtils.hasText(purchaseNotificationTypeDTO.getWarrantyItemNumber())) {
                appendErrorMessage(errorMessage, "Both Business Unit Name and Warranty Item Number can't be Null. Atleast one of them should be specified");
            }
        }
    	if(!StringUtils.hasText(purchaseNotificationTypeDTO.getWarrantyPlanCode())){
    		appendErrorMessage(errorMessage, " Warranty Plan Code can't be Null");    		
    	}
    	if (StringUtils.hasText(errorMessage.toString())) {
			response.setErrorType(errorMessage.toString());
			throw new RuntimeException(errorMessage.toString());
		}
    	
	}
	
	private void appendErrorMessage(StringBuilder errorMessage, String appendMessage) {
		if (StringUtils.hasText(errorMessage.toString())) {
			errorMessage.append(", ");
		}
		errorMessage.append(appendMessage);
	}
	
	private SyncResponse buildErrorResponse(SyncResponse response, String message,
                                            String invoiceNumber, String serialNumber, String planCode) {
		response.setSuccessful(false);
		response.setException(new StringBuilder().append("Error syncing of Extended Warranty purchase for Serial Number # ")
                .append(serialNumber)
                .append(" with plan code # ")
                .append(planCode)
                .append(" on Invoice # ")
                .append(invoiceNumber)
                .append("\n")
                .append("The Reason for the Error is : ")
                .append(message)
				.append("\n").toString());
		response.setErrorCode(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		response.setErrorType(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		return response;
	}
	 
	private void sync(ExtWarrantyPurchase extWarrantyPurchaseMemo) throws RuntimeException{
		InventoryItem inventoryItem = null;
        boolean stock = false;         
        if (StringUtils.hasText(extWarrantyPurchaseMemo.getBuName())) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(extWarrantyPurchaseMemo.getBuName());
        } else if (StringUtils.hasText(extWarrantyPurchaseMemo.getWarrantyItemNumber())) {          
        	Item extendedWarrantyItem = null;
        	try {
        		extendedWarrantyItem = catalogService.findItemByItemNumberOwnedByManuf(extWarrantyPurchaseMemo.getWarrantyItemNumber());
//        		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(extendedWarrantyItem.getBusinessUnitInfo().getName());
        	} catch (CatalogException ce) {
        		throw new RuntimeException("Warranty Item #" + extWarrantyPurchaseMemo.getWarrantyItemNumber().trim() + " doesn't exist");
        	}
        } else {
            throw new RuntimeException("Both Business Unit Name and Warranty Item Number are null");
        }

        Item item=null;
		try {
			item = catalogService.findItemByItemNumberOwnedByManuf(extWarrantyPurchaseMemo.getEquipmentItemNumber().trim());
		}catch (CatalogException e) {
			throw new RuntimeException("Equipment Item #" + extWarrantyPurchaseMemo.getEquipmentItemNumber().trim() 
					+ " doesn't exist under "+SelectedBusinessUnitsHolder.getSelectedBusinessUnit()+" Business Unit.", e);
		}
		
		try {
			inventoryItem = inventoryItemRepository.findSerializedItem(extWarrantyPurchaseMemo.getSerialNumber().trim(), item.getModel().getName());
			if(InventoryItemCondition.SCRAP.getItemCondition().equalsIgnoreCase(inventoryItem.getConditionType().getItemCondition())){
				throw new RuntimeException("Equipment " + inventoryItem.getSerialNumber() + " has been scrapped");
			}
            if (InventoryType.STOCK.getType().equalsIgnoreCase(inventoryItem.getType().getType())) {
                stock = true;
            }
            
        } catch (ItemNotFoundException e) {
			throw new RuntimeException(e);			
		}
		Warranty warranty =null;
		if (!stock) {
            try {
                warranty = warrantyService.findWarranty(inventoryItem);
                if (warranty == null) {
                    throw new RuntimeException("Warranty doesn't exist for serial number #" +extWarrantyPurchaseMemo.getSerialNumber().trim());
                }
            }catch(Exception exception){
                throw new RuntimeException("Warranty doesn't exist for serial number #" +extWarrantyPurchaseMemo.getSerialNumber().trim(), exception);
            }
        }

        PolicyDefinition policyDefinition = policyDefinitionRepository.findPolicyDefinitionByCode(
                                                                        extWarrantyPurchaseMemo.getWarrantyPlanCode());
        if (policyDefinition == null) {
            throw new RuntimeException("A policy with code "
                    + extWarrantyPurchaseMemo.getWarrantyPlanCode()
                    + " does not exist for serial number #"
                    + extWarrantyPurchaseMemo.getSerialNumber().trim()
                    + " and warranty plan "
                    + extWarrantyPurchaseMemo.getWarrantyPlanCode());
        }
        if (policyDefinition != null && policyDefinition.isInActive()) {
        	throw new RuntimeException("A policy with code "+extWarrantyPurchaseMemo.getWarrantyPlanCode()+" is Inactive");
        }
        
        if (stock) {
            if (extWarrantyPurchaseMemo.getNotificationType().equalsIgnoreCase("SALE")) {
                ExtendedWarrantyNotification extnWntyNotification = new ExtendedWarrantyNotification();
                if(!policyDefinition.getAvailability().isAvailableFor(inventoryItem)){
                    throw new RuntimeException("Policy "
                                                + extWarrantyPurchaseMemo.getWarrantyPlanCode()
                                                + " is not applicable for serial number #"
                                                + extWarrantyPurchaseMemo.getSerialNumber().trim());
                }
                List<ExtendedWarrantyNotification> alreadyStaged = warrantyService
                                                .findStagedExtnWntyPurchaseNotification(inventoryItem, policyDefinition);
                if (alreadyStaged == null || alreadyStaged.isEmpty() || alreadyStaged.size() == 0) {
                    extnWntyNotification = new ExtendedWarrantyNotification();
                    extnWntyNotification.setNotificationType(extWarrantyPurchaseMemo.getNotificationType());
                    extnWntyNotification.setForUnit(inventoryItem);
                    extnWntyNotification.setPolicy(policyDefinition);
                    merge(extnWntyNotification, extWarrantyPurchaseMemo);
                    try {
                        warrantyService.save(extnWntyNotification);
                    } catch (Exception e) {
                        logger.error(e);
                        throw new RuntimeException("An error occured while saving extended warranty notification");
                    }
                } else if (alreadyStaged.size() == 1) {
                    extnWntyNotification = alreadyStaged.get(0);
                    merge(extnWntyNotification, extWarrantyPurchaseMemo);
                    try {
                        warrantyService.update(extnWntyNotification);
                    } catch (Exception e) {
                        logger.error(e);
                        throw new RuntimeException("An error occured while updating extended warranty notification");
                    }

                } else {
                    throw new RuntimeException("There is more than one purchase notifications staged for serial number #"
                            + extWarrantyPurchaseMemo.getSerialNumber().trim()
                            + " and warranty plan "
                            + extWarrantyPurchaseMemo.getWarrantyPlanCode());
                }
            } else if (extWarrantyPurchaseMemo.getNotificationType().equalsIgnoreCase("WARRANTY RMA")) {
                throw new RuntimeException("Notification Type is invalid, since unit is in STOCK");
            } else {
                throw new RuntimeException("Invalid Transaction Type");
            }
        } else {
            List<Object> registeredPolicies = warranty.getPolicies() != null
                                            ? (Arrays.asList(warranty.getPolicies().toArray()))
                                            : new ArrayList<Object>();

            if(extWarrantyPurchaseMemo.getNotificationType().equalsIgnoreCase("SALE"))
            {
                for(Object registeredPolicy: registeredPolicies){
                    RegisteredPolicy regPolicy = (RegisteredPolicy)registeredPolicy;
                    if(regPolicy.getCode().equalsIgnoreCase(extWarrantyPurchaseMemo.getWarrantyPlanCode())
                            && regPolicy.getLatestPolicyAudit().getStatus().equalsIgnoreCase(RegisteredPolicyStatusType.ACTIVE.getStatus())){
                        throw new RuntimeException("Policy "
                                                    + extWarrantyPurchaseMemo.getWarrantyPlanCode()
                                                    + " already applied on serial number #"
                                                    + extWarrantyPurchaseMemo.getSerialNumber().trim());
                    }
                }

                if(!policyDefinition.getAvailability().isAvailableFor(inventoryItem)){
                    throw new RuntimeException("Policy "
                                                + extWarrantyPurchaseMemo.getWarrantyPlanCode()
                                                + " is not applicable for serial number #"
                                                + extWarrantyPurchaseMemo.getSerialNumber().trim());
                }
                CalendarDuration forTime=null;
                try {
                    forTime = policyDefinition.warrantyPeriodFor(inventoryItem);
                } catch (PolicyException e) {
                    throw new RuntimeException(e);
                }
                CalendarDate purchaseDate = convertToCalendarDate(extWarrantyPurchaseMemo.getInvoiceDate());
                warrantyService.register(warranty, policyDefinition, forTime, policyDefinition.getAvailability().getPrice(),
                                        "Extended Warranty Purchase Notification", extWarrantyPurchaseMemo.getSalesOrderNumber(),
                                        purchaseDate);
                } else if(extWarrantyPurchaseMemo.getNotificationType().equalsIgnoreCase("WARRANTY RMA")){
                boolean isPolicyFound = false;
                for(Object registeredPolicy: registeredPolicies){
                    RegisteredPolicy regPolicy = (RegisteredPolicy)registeredPolicy;
                    if(regPolicy.getCode().equalsIgnoreCase(extWarrantyPurchaseMemo.getWarrantyPlanCode())
                            && regPolicy.getLatestPolicyAudit().getStatus().equalsIgnoreCase(RegisteredPolicyStatusType.ACTIVE.getStatus())){
                        isPolicyFound = true;
                        warrantyService.terminateRegisteredPolicyForAdmin(warranty, regPolicy);
                    }
                }
                if(!isPolicyFound){
                    throw new RuntimeException("Policy "
                                                + extWarrantyPurchaseMemo.getWarrantyPlanCode()
                                                + " not present on serial number #"
                                                + extWarrantyPurchaseMemo.getSerialNumber().trim() 
                                                + ", hence RMA is not possible");
                }
            } else {
                throw new RuntimeException("Invalid Transaction Type");
            }
            warrantyService.save(warranty.getForItem().getWarranty());
            warrantyService.updateInventoryForWarrantyDates(inventoryItem);
            updateInventory(inventoryItem,extWarrantyPurchaseMemo.getNotificationType(),extWarrantyPurchaseMemo);
        }

	}

    private void merge(ExtendedWarrantyNotification extnWntyNotification,
                       ExtWarrantyPurchase extWarrantyPurchaseMemo) {

        ServiceProvider purchasingDealer = orgService.findDealerByNumber(extWarrantyPurchaseMemo.getDealerNumber());
        if (purchasingDealer == null) {
            throw new RuntimeException("Dealer Number provided is incorrect on notification for serial number #"
                    + extWarrantyPurchaseMemo.getSerialNumber().trim()
                    + " and warranty plan "
                    + extWarrantyPurchaseMemo.getWarrantyPlanCode());
        }
        extnWntyNotification.setInvoiceDate(convertToCalendarDate(extWarrantyPurchaseMemo.getInvoiceDate()));
        extnWntyNotification.setInvoiceNumber(extWarrantyPurchaseMemo.getInvoiceNumber());
        extnWntyNotification.setPurchasingDealer(purchasingDealer);
        extnWntyNotification.setSalesOrderLineNumber(extWarrantyPurchaseMemo.getSalesOrderLineNumber());
        extnWntyNotification.setSalesOrderNumber(extWarrantyPurchaseMemo.getSalesOrderNumber());
    }

    private void updateInventory(InventoryItem inventoryItem,String notificationType,ExtWarrantyPurchase extWarrantyPurchaseMemo) {
		InventoryTransaction newTransaction = new InventoryTransaction();
    	 newTransaction.setTransactionOrder(new Long(inventoryItem.getTransactionHistory().size() + 1));
    	 Collections.sort(inventoryItem.getTransactionHistory());
    	 newTransaction.setTransactedItem(inventoryItem); 
    	 if(notificationType.equalsIgnoreCase("SALE")){
    		 newTransaction.setSeller(inventoryItem.getDealer());
    		 newTransaction.setOwnerShip(inventoryItem.getDealer());		  			
         	 newTransaction.setBuyer(inventoryItem.getOwnedBy());
         	 newTransaction.setInvTransactionType(invTransactionService.getTransactionTypeByName(
         			 InvTransationType.EXTENED_WNTY_PURCHASE.getTransactionType()));	 
    	 } else if(notificationType.equalsIgnoreCase("WARRANTY RMA")){
    		 newTransaction.setSeller(inventoryItem.getDealer());
    		 newTransaction.setOwnerShip(inventoryItem.getDealer());		  			
         	 newTransaction.setBuyer(inventoryItem.getOwnedBy());
         	 newTransaction.setInvTransactionType(invTransactionService
 					.getTransactionTypeByName("RMA"));
    	 }
    	 
     	 newTransaction.setTransactionDate(Clock.today());
     	 newTransaction.setStatus(BaseDomain.ACTIVE);
     	 newTransaction.getD().setActive(false);
     	 if(StringUtils.hasText(extWarrantyPurchaseMemo.getSalesOrderNumber())){
     		 newTransaction.setSalesOrderNumber(extWarrantyPurchaseMemo.getSalesOrderNumber().trim());
     	 }
     	 if(StringUtils.hasText(extWarrantyPurchaseMemo.getInvoiceNumber())){
     		 newTransaction.setInvoiceNumber(extWarrantyPurchaseMemo.getInvoiceNumber().trim());
     	 }
     	 if(StringUtils.hasText(extWarrantyPurchaseMemo.getInvoiceDate())){
     		 newTransaction.setInvoiceDate(convertToCalendarDate(extWarrantyPurchaseMemo
 					.getInvoiceDate().trim()));
     	 }
     	 inventoryItem.getTransactionHistory().add(newTransaction);		         	 
     	 inventoryService.updateInventoryItem(inventoryItem);
	}    			
	
	
	private ExtWarrantyPurchase transform(final ExtWarrantyNotificationDocumentDTO extNotificationDto) {
        PurchaseNotificationTypeDTO notificationDto = extNotificationDto.getExtWarrantyNotification()
                                                                    .getDataArea().getPurchaseNotification();
        ExtWarrantyPurchase memo = new ExtWarrantyPurchase();
		memo.setNotificationType(notificationDto.getNotificationType());
		memo.setDealerNumber(notificationDto.getDealerNumber());
		memo.setSerialNumber(notificationDto.getSerialNumber());
		memo.setEquipmentItemNumber(notificationDto.getEquipmentItemNumber());
		memo.setWarrantyItemNumber(notificationDto.getWarrantyItemNumber());
		memo.setWarrantyPlanCode(notificationDto.getWarrantyPlanCode());
		memo.setSalesOrderNumber(notificationDto.getSalesOrderNumber());
		memo.setSalesOrderLineNumber(notificationDto.getSalesOrderLineNumber());
		memo.setInvoiceNumber(notificationDto.getInvoiceNumber());
		memo.setInvoiceDate(notificationDto.getInvoiceDate());
        memo.setBuName(extNotificationDto.getExtWarrantyNotification().getDataArea().getBUName());
        return memo;
	}
	
	
	
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public void setInventoryItemRepository(
			InventoryItemRepository inventoryItemRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
	}

	public void setPolicyDefinitionRepository(
			PolicyDefinitionRepository policyDefinitionRepository) {
		this.policyDefinitionRepository = policyDefinitionRepository;
	}

	public void setInvTransactionService(
			InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}


	public CalendarDate getCalendarDate() {
		return calendarDate;
	}


	public void setCalendarDate(CalendarDate calendarDate) {
		this.calendarDate = calendarDate;
	}
	
	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }
}
