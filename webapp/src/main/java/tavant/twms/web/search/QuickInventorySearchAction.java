package tavant.twms.web.search;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.customReports.CustomReportAnswer;
import tavant.twms.domain.inventory.*;
import tavant.twms.domain.orgmodel.BrandType;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.policy.WarrantyCoverageRequest;
import tavant.twms.domain.policy.WarrantyCoverageRequestService;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.infra.HibernateCast;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.opensymphony.xwork2.ActionContext;

/**
 * @author rakesh.r
 */

@SuppressWarnings("serial")
public class QuickInventorySearchAction extends I18nActionSupport {

	private String serialNumber;
	private String inventoryItem; // Required Inorder to forward it to chain
	// actions
	private InventoryService inventoryService;
	private List<InventoryItemComposition> inventoryItemComposition;
	private boolean allowInventorySelection; 
	private boolean isInternalUser;
	private String id;
	private String action;
	private ConfigParamService configParamService;
	private WarrantyCoverageRequestService warrantyCoverageRequestService;
	private WarrantyCoverageRequest warrantyCoverageRequest;
	private boolean duplicateSerialNumbers = false;
	private String inventoryId;
	private List<InventoryItem> inventoryItemsList = new ArrayList<InventoryItem>();
	private String actionName;
	private InventoryItemUtil inventoryItemUtil;
	private static final Logger logger = Logger
			.getLogger(QuickInventorySearchAction.class);
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String display() {
        try {
			searchInventoryAndValidationActions();
		} catch (ItemNotFoundException e) {
			return INPUT;
		}
		if (hasActionErrors()) {
			return INPUT;
		} else if ( duplicateSerialNumbers ){
			return DUPLICATE;
		} else {
			return SUCCESS;
		}

	}
	
	public boolean isPageReadOnly() {
		return false;
	}
	
	/**
	 * API to search inventory based on the complete SN and validate the chose
	 * action
	 *
	 * @throws ItemNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void searchInventoryAndValidationActions()
			throws ItemNotFoundException {
		InventoryItem inventoryItem = null;
		this.isInternalUser = this.orgService.isInternalUser(getLoggedInUser());
        List<InventoryItem> inventoryItemsListTemp = new ArrayList<InventoryItem>();
		if (StringUtils.hasText(serialNumber)) {
			try {
				serialNumber = serialNumber.trim();
                inventoryItemsListTemp = this.inventoryService
						.findItemBySerialNumber(serialNumber);
			} catch (ItemNotFoundException e) {
				addActionError("error.common.noResults");
				return;
			}
            /*List<Long> dealers = new ArrayList<Long>();
            //Add the old code , whatever dealer id we get
            dealers.add(getLoggedInUser().getBelongsToOrganization().getId());
            //Plus add the child dealers. Since if he has then he can file dr for them too
            for(Organization org : getLoggedInUser().getBelongsToOrganization().getChildOrgs()){
                dealers.add(org.getId());
            }

            for(InventoryItem item : inventoryItemsListTemp){
                if(dealers.contains(item.getCurrentOwner().getId()))
                {
                    inventoryItemsList.add(item);
                }
            }*/
            for(InventoryItem item : inventoryItemsListTemp){
                if(isInternalUser || inventoryItemUtil.checkLoggedInDealerOwner(item) || inventoryItemUtil.isLoggedInDealerShipToDealer(item) || validateSelectedActionAgainstInv(item,false)){
                    inventoryItemsList.add(item);
                }
            }

			if(!inventoryItemsList.isEmpty())	{
				if( inventoryItemsList.size() == 1 ){
					inventoryItem = inventoryItemsList.get(0);
				} else { // TODO: need to remove this block,the control does not come to this block, as there will be a unique serialNumber and inventoryItemsList will always have only one element				
					//remove inventory items on which user cannot perform chosen action
					for (Iterator<InventoryItem> iterator = inventoryItemsList.iterator(); iterator.hasNext();)
					{
						InventoryItem multipleInvItem = iterator.next();
						if(!validateSelectedActionAgainstInv(multipleInvItem,false)){
							iterator.remove();
						}
					}
					if( inventoryItemsList.size() == 1 ){
						inventoryItem = inventoryItemsList.get(0);
					}
				}	
			}
			if (inventoryItem != null) {
				setId(inventoryItem.getId().toString());
				this.inventoryItem = inventoryItem.getId().toString();
				this.inventoryItemComposition = inventoryItem.getComposedOf();
				ActionContext.getContext().getParameters().put(
						"claim.itemReference.referredInventoryItem",
						inventoryItem.getId().toString());
				ActionContext.getContext().getParameters().put("selectedBusinessUnit", inventoryItem.getBusinessUnitInfo());
				this.request.setAttribute("inventoryItem",inventoryItem.getId().toString());
				this.request.setAttribute("duplicateSerialNos", false);
				validateSelectedActionAgainstInv(inventoryItem,true);
			} else if ( !inventoryItemsList.isEmpty() && inventoryItemsList.size() > 0) {
				this.request.setAttribute("duplicateSerialNos", true);
				duplicateSerialNumbers = true;
				createActionForQuickSearch();
			}
			else if (inventoryItemsList.isEmpty()){
				addActionError("message.quicksearch.invItemActionNotAllowed");
			}
		} else {
			addActionError("error.inventory.emptySerialNumber");
		}
	}

	public void createActionForQuickSearch() {
		actionName = actionName.concat(".action");
		if( action.equalsIgnoreCase("EQUIPMENT HISTORY")) {
			actionName = actionName.concat("?id=%{id}");
		} else if( action.equalsIgnoreCase("DELIVERY REPORT")) {
			actionName = actionName.concat("?allowInventorySelection=true&inventoryItem=%{id}");
		} else if ( action.equalsIgnoreCase("DEALER TO DEALER")) {
			actionName = actionName.concat("?allowInventorySelection=true&inventoryItem=%{id}");
		} else if ( action.equalsIgnoreCase("EQUIPMENT TRANSFER")) {
			actionName = actionName.concat("?allowInventorySelection=true&inventoryItem=%{id}");
		} else if ( action.equalsIgnoreCase("EXTENDED WARRANTY")) {
			actionName = actionName.concat("?inventoryItems=%{id}");
		} else if ( action.equalsIgnoreCase("CREATE CLAIM")) {
			actionName = actionName.concat("?claim.forDealer=" + fetchDealershipName()
					+ "&claim.itemReference.referredInventoryItem=%{id}&selectedBusinessUnit=%{businessUnitInfo.name}");
		} else if ( action.equalsIgnoreCase("RETAIL MACHINE TRANSFER")) {
			actionName = actionName.concat("?inventoryItems=%{id}");
		}
		
		actionName = actionName.concat("&duplicateSerialNumber=true");
	}
	
	private String fetchDealershipName() {
		String dealerShipName = getLoggedInUsersDealership().getName();
		if ( dealerShipName.contains("&")){
			dealerShipName = dealerShipName.replaceAll("&", "%26");
		}
		if( dealerShipName.contains("<")) {
			dealerShipName = dealerShipName.replaceAll("<", "%3C");
		}
		if( dealerShipName.contains(">")) {
			dealerShipName = dealerShipName.replaceAll(">", "%3E");
		}
		if( dealerShipName.contains("#")) {
			dealerShipName = dealerShipName.replaceAll("#", "%23");
		}
		if( dealerShipName.contains("$")) {
			dealerShipName = dealerShipName.replaceAll("$", "%24");
		}
		if( dealerShipName.contains("/")) {
			dealerShipName = dealerShipName.replaceAll("/", "%2F");
		}
		if( dealerShipName.contains(";")) {
			dealerShipName = dealerShipName.replaceAll(";","%3B");
		}
		if( dealerShipName.contains("=")) {
			dealerShipName = dealerShipName.replaceAll("=","%3D");
		}
		if( dealerShipName.contains("?")) {
			dealerShipName = dealerShipName.replaceAll("?","%3F");
		}
		if( dealerShipName.contains("@")) {
			dealerShipName = dealerShipName.replaceAll("@","%40");
		}
		return dealerShipName;
	}
	public boolean checkLoggedInDealerOwner(InventoryItem item) {
		return inventoryItemUtil.checkLoggedInDealerOwner(item);

	}
	/**
	 * This method adds action error if called for a single inventory
	 * In case the search results in more that an inventory this API can be used to
	 * simply remove the inventory items  on which the user do not have permission
	 * @param item
	 */
	private boolean validateSelectedActionAgainstInv(InventoryItem item,boolean addActionErrors) {
        boolean canSearchOtherDealersRetail  = configParamService
                .getBooleanValue(ConfigName.CAN_DEALER_SEARCH_OTHER_DEALERS_RETAIL.getName());
		boolean customerTypeAllowed = inventoryService
				.customersBelongsToConfigParam(item);
		boolean toReturn = true;
		
		if (action.equalsIgnoreCase("EQUIPMENT HISTORY")) {
			/**
			 * STOCK EHP -> Only owing dealer/internal users/configured service
			 * providers
			 */
			if ((item.getType().equals(InventoryType.STOCK) && !allowWROnOthersStock()) || (item.getType().equals(InventoryType.RETAIL) && !canSearchOtherDealersRetail)) {
				if (!checkLoggedInDealerOwner(item) && !isInternalUser
						&& !customerTypeAllowed && !inventoryItemUtil.isLoggedInDealerShipToDealer(item)) {
					if(addActionErrors){
						addActionError("error.inventory.notAuthorized");
					}
					toReturn = false;
				}
			}
			if (item.getType().equals(InventoryType.STOCK)
					&& allowWROnOthersStock() && !isLoggedInUserAnInternalUser()) {
				String loggedInUserBrand = new HibernateCast<Dealership>()
						.cast(getLoggedInUsersDealership()).getBrand();
				if (!isLoggedInUserDualBrandDealer()
						&& !(item.getBrandType().equals(loggedInUserBrand) || item.getBrandType().equals(BrandType.UTILEV.getType()))
						&& !inventoryItemUtil.checkLoggedInDealerCurrentOwnerOrParent(item) && !inventoryItemUtil.isLoggedInDealerShipToDealer(item)) {
					if (addActionErrors) {
						addActionError("message.quicksearch.invItemActionNotAllowed");
					}
					toReturn = false;
				}
			}
			
			if (item.getType().equals(InventoryType.RETAIL)
					&& canSearchOtherDealersRetail && !isLoggedInUserAnInternalUser() && isBuConfigAMER()) {
				String loggedInUserBrand = new HibernateCast<Dealership>()
						.cast(getLoggedInUsersDealership()).getBrand();
				if (!isLoggedInUserDualBrandDealer()
						&& !(item.getBrandType().equals(loggedInUserBrand) || item.getBrandType().equals(BrandType.UTILEV.getType()))
						&& !inventoryItemUtil.checkLoggedInDealerCurrentOwnerOrParent(item) && !inventoryItemUtil.isLoggedInDealerShipToDealer(item)) {
					if (addActionErrors) {
						addActionError("message.quicksearch.invItemActionNotAllowed");
					}
					toReturn = false;
				}
			}
			if (item.getType().equals(InventoryType.OEM_STOCK)){
				if (!isInternalUser
						&& !customerTypeAllowed) {
					if(addActionErrors){
						addActionError("error.inventory.notAuthorizedForOEMStockSearch");
					}
					toReturn = false;
				}
			}
			/**
			 * RETAIl EHP -> Anyone can see EHP of retail
			 */
		} else if (action.equalsIgnoreCase("DELIVERY REPORT")) {
			if (item.getType().getType().equals("STOCK")) {
				/**
				 * Only owing dealer,inventoryAdmin or the configured service
				 * provider can file DR for OEM's inv
				 */
				if (allowWROnOthersStock() && !isLoggedInUserAnInternalUser()) {
					String loggedInUserBrand = new HibernateCast<Dealership>()
							.cast(getLoggedInUsersDealership()).getBrand();
					if (!isLoggedInUserDualBrandDealer()
							&& !(item.getBrandType().equals(loggedInUserBrand) || item.getBrandType().equals(BrandType.UTILEV.getType()))
							&& !inventoryItemUtil.checkLoggedInDealerCurrentOwnerOrParent(item) && !inventoryItemUtil.isLoggedInDealerShipToDealer(item)) {
						if (addActionErrors) {
							addActionError("error.inventory.notAuthorized.create.warrantyRegisteration");
						}
						toReturn = false;
					}
				}
				if(item.getPendingWarranty())
				{
					if(addActionErrors){
						if(item.getLatestWarranty().getStatus().getStatus().equals(WarrantyStatus.DRAFT.getStatus())) {
							addActionError("error.inventory.drsavedasdraft",item.getSerialNumber());
						} else {
							addActionError("error.inventory.drfiled",item.getSerialNumber());
						}
					}
					toReturn = false;
				}
				else if (!(checkLoggedInDealerOwner(item) || inventoryItemUtil.isLoggedInDealerShipToDealer(item))
						&& !getLoggedInUser().hasRole("inventoryAdmin")
						&& !(customerTypeAllowed && (inventoryItemUtil.stockBelongsToOEM(item))
								|| allowWROnOthersStock())) {
					if(!isInternalUser){
						if(addActionErrors){
							addActionError("error.inventory.notAuthorized.create.warrantyRegisteration");
						}
						toReturn = false;
					}else{
						if(addActionErrors){
							addActionError("error.inventory.notAuthorized");
						}	
						toReturn = false;
					}
				}
			} else {
				/**
				 * Inventory not in STOCK any more
				 */
				if(addActionErrors){
					addActionError("error.inventory.notAStock");
				}	
				toReturn = false;
			}
		}
		else if (action.equalsIgnoreCase("DEALER TO DEALER")) {
			if(isStolen(item)){
				if(!hasErrors()){
					addActionError("error.stolenInventory.action");
				}
				toReturn = false;
			}
			if (item.getType().getType().equals("STOCK")) {
				/**
				 * Only owing dealer,inventoryAdmin or the configured service
				 * provider can file DR for OEM's inv
				 */
				if (!(checkLoggedInDealerOwner(item) || inventoryItemUtil.isLoggedInDealerShipToDealer(item))
						&& !getLoggedInUser().hasRole("inventoryAdmin") && !customerTypeAllowed) {
					if(addActionErrors){
						addActionError("error.inventory.notAuthorized");
					}
					toReturn = false;
				}
			} else {
				/**
				 * Inventory not in STOCK any more
				 */
				if(addActionErrors){
					addActionError("error.inventory.notAStock");
				}	
				toReturn = false;
			}
		}
		else if (action.equalsIgnoreCase("EQUIPMENT TRANSFER")) {
			if (item.getType().getType().equals("STOCK")) {
				/**
				 * Inventory not in RETAIL any more
				 */
				if(addActionErrors){
					addActionError("error.inventory.notARetail");
				}	
				toReturn = false;
			} else {
				/**
				 * Any dealer,inventoryAdmin can do an ETR on a retailed
				 * inventory
				 */
				if(item.getPendingWarranty())
				{	
					if(addActionErrors){
						if(item.getLatestWarranty().getStatus().getStatus().equals(WarrantyStatus.DRAFT.getStatus())) {
							addActionError("error.inventory.etrSavedAsDraft",item.getSerialNumber());
						} else {
							addActionError("error.inventory.pendingWarrantyForETR",item.getSerialNumber());
						}
					}	
					toReturn = false;
				}
				else if (!isLoggedInUserADealer()
						&& !getLoggedInUser().hasRole("inventoryAdmin")
                        && !isLoggedInUserAnEnterpriseDealer()) {
					if(addActionErrors){
						addActionError("error.inventory.notAuthorized");
					}	
					toReturn = false;
				}
			}
		} else if (action.equalsIgnoreCase("EXTENDED WARRANTY")) {
			if(isStolen(item)){
				if(!hasErrors()){
					addActionError("error.stolenInventory.action");
				}
				toReturn = false;
			}
			if (item.getType().getType().equals("STOCK") || item.getType().getType().equals("OEM_STOCK")) {
				/**
				 * Inventory not in STOCK any more
				 */
				if(addActionErrors){
					addActionError("error.inventory.notARetail");
				}
				toReturn = false;
			} else {
                SelectedBusinessUnitsHolder.setSelectedBusinessUnit(item.getBusinessUnitInfo().getName());
                /**
				 * Any admin or inventory admin can purchse extended warranty
				 * Dealer can buy extended  warranty only if bu is enaled
				 */
                if (!isLoggedInUserAnInternalUser()
                        &&  !configParamService.getBooleanValue(ConfigName.CAN_EXTERNAL_USER_PURCHASE_EXTENDED_WARRANTY.getName())) {
                    if (addActionErrors) {
                        addActionError("error.inventory.notAuthorized");
                        toReturn = false;
                    }
                } else if (isLoggedInUserAnInternalUser()
                            && !getLoggedInUser().hasRole("inventoryAdmin")
						&& !getLoggedInUser().hasRole("admin")) {
					if(addActionErrors){
						addActionError("error.inventory.notAuthorized");
					}
					toReturn = false;
				}
                SelectedBusinessUnitsHolder.clearChosenBusinessUnitFilter();
            }
		} else if (action.equalsIgnoreCase("CREATE CLAIM")) {
			if (item.getType().getType().equals("STOCK")||item.getType().getType().equals("RETAIL")) {
				/**
				 * Only owingDealer,processor or the configured service provider
				 * can file claim on STOCK inventory
				 */
				if ((getLoggedInUser().hasRole("dealerWarrantyAdmin") 
						&& (checkLoggedInDealerOwner(item) || customerTypeAllowed))
						|| getLoggedInUser().hasRole("processor")) {
				} else {
					if(addActionErrors){
						addActionError("error.inventory.notAuthorized");
					}
					toReturn = false;
				}
			} else {
				/**
				 * Any dealer,processor can file claim on RETAIL inventory
				 */
				if (getLoggedInUser().hasRole("dealerWarrantyAdmin")
						|| getLoggedInUser().hasRole("processor")) {
				} else {
					if(addActionErrors){
						addActionError("error.inventory.notAuthorized");
					}	
					toReturn = false;
				}
			}
		}else if("RETAIL MACHINE TRANSFER".equals(action)){
			if(isStolen(item)){
				if(!hasErrors()){
					addActionError("error.stolenInventory.action");
				}
				toReturn = false;
			}
			if (item.getType().getType().equals("STOCK")) {
				/**
				 * Inventory not in STOCK any more
				 */
				if(addActionErrors){
					addActionError("error.inventory.notARetail");
				}
				toReturn = false;
			} else {
                SelectedBusinessUnitsHolder.setSelectedBusinessUnit(item.getBusinessUnitInfo().getName());
                /**
				 * Any admin or inventory admin can purchse extended warranty
				 * Dealer can buy extended  warranty only if bu is enaled
				 */
                if (!isLoggedInUserAnInternalUser()
                        &&  !configParamService.getBooleanValue(ConfigName.CAN_DEALER_PERFORM_RMT.getName())) {
                    if (addActionErrors) {
                        addActionError("error.inventory.notAuthorized");
                        toReturn = false;
                    }
                } else if (isLoggedInUserAnInternalUser()
                            && !getLoggedInUser().hasRole("inventoryAdmin")
						&& !getLoggedInUser().hasRole("admin")) {
					if(addActionErrors){
						addActionError("error.inventory.notAuthorized");
					}
					toReturn = false;
				}
                SelectedBusinessUnitsHolder.clearChosenBusinessUnitFilter();
            }
		}
		if (!getLoggedInUser().isInternalUser()
				&& item != null
				&& item.getConditionType().getItemCondition().equalsIgnoreCase(InventoryItemCondition.SCRAP.getItemCondition())) {
			if(!hasErrors()){
			addActionError("message.scrap.machineScrapped",
					item.getSerialNumber());
			}
			toReturn = false;
		}
		return toReturn;
	}	
	
	private boolean isInventoryWithPDI(InventoryItem item) {
		for(CustomReportAnswer customReportAnswer : item.getReportAnswers()){
 		   if("PDI".equalsIgnoreCase(customReportAnswer.getCustomReport().getReportType().getCode()));
 		     return true;
 	   }           
        return false;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public void setWarrantyCoverageRequestService(
			WarrantyCoverageRequestService warrantyCoverageRequestService) {
		this.warrantyCoverageRequestService = warrantyCoverageRequestService;
	}

	public boolean isInternalUser() {
		return isInternalUser;
	}

	public void setInternalUser(boolean isInternalUser) {
		this.isInternalUser = isInternalUser;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public WarrantyCoverageRequest getWarrantyCoverageRequest() {
		return warrantyCoverageRequest;
	}

	public void setWarrantyCoverageRequest(
			WarrantyCoverageRequest warrantyCoverageRequest) {
		this.warrantyCoverageRequest = warrantyCoverageRequest;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public String getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(String inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public String getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
	}

    public List<InventoryItem> getInventoryItemsList() {
        return inventoryItemsList;
    }

    public void setInventoryItemsList(List<InventoryItem> inventoryItemsList) {
        this.inventoryItemsList = inventoryItemsList;
    }

    public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public boolean isAllowInventorySelection() {
		return allowInventorySelection;
	}

	public void setAllowInventorySelection(boolean allowInventorySelection) {
		this.allowInventorySelection = allowInventorySelection;
	}

    public boolean isEligibleForExtendedWarrantyPurchase(){
        boolean isEligible = false;
        SelectedBusinessUnitsHolder.clearChosenBusinessUnitFilter();
        Map<String, List<Object>> buValues = configParamService.
                getValuesForAllBUs(ConfigName.CAN_EXTERNAL_USER_PURCHASE_EXTENDED_WARRANTY.getName());
        for (String buName : buValues.keySet()) {
              Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
              if(booleanValue){
                 isEligible=true;
                 break;
              }
        }
        return isEligible;
    }
    
    @SuppressWarnings("unchecked")
	public String showMajorComponentEquipmentHistory(){
		InventoryItem inventoryItem = null;
		if (StringUtils.hasText(serialNumber)) {
			try {
				inventoryItemsList = inventoryService
						.findMajorComponentBySerialNumber(serialNumber.trim());
			} catch (ItemNotFoundException e) {
				addActionError("error.common.noResults");
				return INPUT;
			}
			this.inventoryItemComposition = inventoryService.getComponentDetailForMajorComponent(inventoryItemsList);
			setComponentDecrition(inventoryItemsList,inventoryItemComposition);
			if(!inventoryItemsList.isEmpty()){
				if(inventoryItemsList.size() == 1 ){
					inventoryItem = inventoryItemsList.get(0);
				}else{
					addActionWarning("message.majorComponent.majorCompSearchForMultipleSNo",inventoryItemsList.get(0).getSerialNumber());
					return INPUT;
				}
			}
			if (inventoryItem != null) {
				setId(inventoryItem.getId().toString());				
			} 			
		}else {
			addActionError("error.inventory.emptySerialNumber");
			return INPUT;
		}
		return SUCCESS;    	
    }

    private void setComponentDecrition(List<InventoryItem> inventoryItemsList,
			List<InventoryItemComposition> inventoryItemCompositionList) {
    	for(InventoryItem inventoryItem:inventoryItemsList){
    		for(InventoryItemComposition inventoryItemComposition:inventoryItemCompositionList){
    			if(inventoryItemComposition.getPart().getId().equals(inventoryItem.getId())){
    				inventoryItem.setComponentDescription(inventoryItemComposition.getSerialTypeDescription());
    			}
    			
    		}
    	}
		
	}

	public InventoryItemUtil getInventoryItemUtil() {
        return inventoryItemUtil;
    }

    public void setInventoryItemUtil(InventoryItemUtil inventoryItemUtil) {
        this.inventoryItemUtil = inventoryItemUtil;
    }

	public void setInventoryItemComposition(List<InventoryItemComposition> inventoryItemComposition) {
		this.inventoryItemComposition = inventoryItemComposition;
	}

	public List<InventoryItemComposition> getInventoryItemComposition() {
		return inventoryItemComposition;
	}
    
	public boolean allowWROnOthersStock() {
		return configParamService.getBooleanValue(ConfigName.ALLOW_WNTY_REG_ON_OTHERS_STOCKS.getName());
	}
	
    public boolean isDealerEligibleToPerformRMT() {
        boolean isEligible = false;
        if (isLoggedInUserADealer()) {
            Map<String, List<Object>> buValues = configParamService.
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
    public boolean isStockClaimAllowed(){
    	return this.configParamService
                .getBooleanValue(ConfigName.STOCK_CLAIM_ALLOWED
                                .getName());
    }
	public boolean isD2DAllowed(){
    	return this.configParamService
                .getBooleanValue(ConfigName.D2D_ALLOWED
                                .getName());
    }
	public boolean isStolen(InventoryItem inventoryItem) {
        if ("STOLEN".equals(inventoryItem.getConditionType().getItemCondition())) {
                return true;
        }
        return false;
	}
	
	public String getInventorySerialNumberForMajorComponent(Long majorComponentId){
		InventoryItem inventoryItem = null;
		try {
			inventoryItem = inventoryService.findInventoryItemForMajorComponent(majorComponentId);
		} catch (Exception e) {
			logger.error("Could Not Fetch Inventory Item for the Major Component Id : " + majorComponentId);
			return null;
		}
		if(inventoryItem != null){
			return inventoryItem.getSerialNumber();
		}else{
			return null;
		}
	}
}

