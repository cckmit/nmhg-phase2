package tavant.twms.web.warranty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.inventory.InvTransationType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.inventory.InventoryTransactionType;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.warranty.WarrantyUtil;
import tavant.twms.infra.BaseDomain;
import tavant.twms.infra.HibernateCast;
import tavant.twms.web.multipleinventorypicker.MultipleInventoryPickerAction;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

@SuppressWarnings("serial")
public class RetailMachineTransfer extends MultipleInventoryPickerAction implements Preparable,
        Validateable {

    private static Logger logger = LogManager.getLogger(PurchaseExtendedWarranty.class);

    private InventoryTransactionService invTransactionService;

    private final List<MultipleInventoryAttributesMapper> inventoryItemsForRMT = new ArrayList<MultipleInventoryAttributesMapper>(
            5);

    private ServiceProvider dealerSearch;

    private List<ServiceProvider> searchedDealers = new ArrayList<ServiceProvider>(5);

    private ServiceProvider selectedDealerForRMT;

    private InventoryTransactionType inventoryTransactionType;

    private CampaignService campaignService;

    private InventoryItem inventoryItem;

    private WarrantyUtil warrantyUtil;

    private WarrantyService warrantyService;

    private OrgService orgService;
    
    private boolean bulkRTT=false; 
  
    @Override
    protected void addSearchConstraints() {
        super.addSearchConstraints();
        getInventorySearchCriteria().setInventoryType(InventoryType.RETAIL);
        getInventorySearchCriteria().setConditionTypeNot(InventoryItemCondition.SCRAP);
        getInventorySearchCriteria().setWarrantyCheck(true);
        /*
         * Dealer Id will be null if current logged in user is not a dealer
         * Fix for SLMSPROD-821 and SLMSPROD-867
         */
        if (getInventorySearchCriteria().getDealerId() != null) {
        List<Long> childDealers = orgService.getChildOrganizationsIds(getInventorySearchCriteria().getDealerId());
        childDealers.add(getInventorySearchCriteria().getDealerId());
        getInventorySearchCriteria().setAllowedDealers(childDealers);
    }
    }

    @Override
    public String handleInventorySelection() throws IOException {
    	super.handleInventorySelection();
        populateInventoryItemsForRMT();
        this.setSize(this.inventoryItemsForRMT.size());
        return SUCCESS;
    }
    
    public boolean isPageReadOnlyAdditional() {
		boolean isReadOnlyDealer = false;
		Set<Role> roles = getLoggedInUser().getRoles();
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(Role.READ_ONLY_DEALER)) {
				isReadOnlyDealer = true;
				break;
			}
		}
		return isReadOnlyDealer;
	}

    private Boolean inventoryItemsInitialized(){
    	if(CollectionUtils.isNotEmpty(getInventoryItems()) && getInventoryItems().size() > 0){
    		for(InventoryItem inventoryItem : getInventoryItems()){
    			if(inventoryItem != null){
    				return Boolean.FALSE;
    			}
    		}
    	}
    	return Boolean.TRUE;
    }
    
    public String show() {
    	if(!isBulkRTT() && (CollectionUtils.isEmpty(getInventoryItems()) || getInventoryItems().size() == 0 || inventoryItemsInitialized())){
    		addActionError("error.newClaim.invalidInventory");
    		return ERROR;
    	}
        if(!this.getInventoryItems().isEmpty()){
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(this.getInventoryItems().get(0).getBusinessUnitInfo().getName());
            boolean isBuSet = getConfigParamService().getBooleanValue(ConfigName.CAN_DEALER_PERFORM_RMT.getName());
            InventoryItem item = getInventoryItems().get(0);
            //parent dealer can do rmt on behalf of child
            //NMHGSLMS-387
            List<Long> dealersId = orgService.getChildOrganizationsIds(getLoggedInUser().getBelongsToOrganization().getId());
            dealersId.add(getLoggedInUser().getBelongsToOrganization().getId());
            if(isLoggedInUserADealer()
                   && isBuSet && !dealersId.contains(item.getCurrentOwner().getId())){
                addActionError("error.inventory.notAuthorized");
                return NONE;
            }
        }
        if (!this.getInventoryItems().isEmpty()){
        	for(InventoryItem invItem:this.getInventoryItems()){
        		if(invItem.getConditionType().getItemCondition().equals("STOLEN")){
        			addActionError("message.stole.machineStolen", invItem.getSerialNumber());
        			return NONE;
        		}
        	}
        }
        setTransationType(InvTransationType.RMT);
        populateInventoryItemsForRMT();
        this.setSize(this.inventoryItemsForRMT.size());
        //Fix for SLMSPROD-1238
        //validateForDealerRental();
        return SUCCESS;
    }

	private boolean validateForDealerRental() {
		if (inventoryItemsForRMT != null) {
			for (MultipleInventoryAttributesMapper multipleInventoryAttributesMapper : inventoryItemsForRMT) {
				if (multipleInventoryAttributesMapper != null
						&& multipleInventoryAttributesMapper.getInventoryItem() != null) {
					if (multipleInventoryAttributesMapper.getInventoryItem()
							.getTransactionHistory() != null) {
						List<InventoryTransaction> transactions = multipleInventoryAttributesMapper
								.getInventoryItem().getTransactionHistory();
						if (warrantyService.findByTransactionId(transactions.get(transactions.size() - 1).getId()) !=null && warrantyService.findByTransactionId(transactions.get(transactions.size() - 1).getId()).getCustomerType().equalsIgnoreCase(AdminConstants.DEALER_RENTAL)) {
							addActionError("error.RMT.DealerRental");
							return Boolean.TRUE;
						}
					}
				}
			}
		}
		return Boolean.FALSE;
	}
    
     public String showRMTForQuickSearch() {
        if(getInventoryItems()!=null && getInventoryItems().isEmpty()
                && getInventoryItem()!=null){
             getInventoryItems().add(inventoryItem);
        }
        if(!this.getInventoryItems().isEmpty()){
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(this.getInventoryItems().get(0).getBusinessUnitInfo().getName());
            boolean isBuSet = getConfigParamService().getBooleanValue(ConfigName.CAN_DEALER_PERFORM_RMT.getName());
            InventoryItem item = getInventoryItems().get(0);
            //parent dealer can do rmt on behalf of child
            //NMHGSLMS-387
            List<Long> dealersId = orgService.getChildOrganizationsIds(getLoggedInUser().getBelongsToOrganization().getId());
            dealersId.add(getLoggedInUser().getBelongsToOrganization().getId());
            if(isLoggedInUserADealer()
                    && isBuSet && !dealersId.contains(item.getCurrentOwner().getId())){
                addActionError("error.inventory.notAuthorized");
                return NONE;
            }
        }
        for (Iterator<InventoryItem> invIterator = this.getInventoryItems()
				.iterator(); invIterator.hasNext();) {
			InventoryItem invItem = invIterator.next();
			if (invItem == null || invItem.getId() == null) {
				invIterator.remove();
			}
		}
        setTransationType(InvTransationType.RMT);
        populateInventoryItemsForRMT();
        this.setSize(this.inventoryItemsForRMT.size());
        return SUCCESS;
    }

    public String searchCompanies() {
        fetchComapanies();
        return SUCCESS;
    }

    public String setSelectedInvsForRMT() {    	
    	/*if(validateForDealerRental()){
    		return INPUT;
    	}*/
    	for (Iterator<MultipleInventoryAttributesMapper> iterator = this.inventoryItemsForRMT.iterator(); iterator.hasNext();) {
    		MultipleInventoryAttributesMapper invItemForRMT = iterator.next();
    		if(invItemForRMT==null){
    			iterator.remove();
    		}
    		//Fix for SLMSPROD-1238
            if(null != invItemForRMT && null != invItemForRMT.getInventoryItem() && null != invItemForRMT.getInventoryItem().getConditionType() && 
            		"STOLEN".equalsIgnoreCase(invItemForRMT.getInventoryItem().getConditionType().getItemCondition())){
            	addActionError("message.stole.machineStolen", invItemForRMT.getInventoryItem().getSerialNumber());
       			return INPUT;
            }
		}
        return SUCCESS;
    }

    public String setSelectedDealerForRMT() {
        this.selectedDealerForRMT = this.orgService.findDealerByName(this.selectedDealerForRMT
                .getName());
        if(isBuConfigAMER()){
            if(!getLoggedInUser().hasRole(ROLE_INVENTORY_ADMIN)){
            	String loggedInUserBrand = new HibernateCast<Dealership>().cast(getLoggedInUsersDealership()
                		).getBrand();
                String selectedDealerBrand = new HibernateCast<Dealership>().cast(this.selectedDealerForRMT
                		).getBrand();
                if(!loggedInUserBrand.equals(selectedDealerBrand) && !isLoggedInUserDualBrandDealer()){
                	return INPUT;
                }
            }
        }
        return SUCCESS;
    }

    public String performRMT() {
        setInventoryTransactionType();
        if (this.selectedDealerForRMT != null && this.inventoryItemsForRMT != null
                && this.inventoryItemsForRMT.size() > 0) {
            for (Iterator<MultipleInventoryAttributesMapper> invItemIter = this.inventoryItemsForRMT
                    .iterator(); invItemIter.hasNext();) {
            	InventoryTransaction newTransaction = new InventoryTransaction();
                MultipleInventoryAttributesMapper invItemMapper = invItemIter.next();
                InventoryItem inventoryItem = invItemMapper.getInventoryItem();
                Collections.sort(inventoryItem.getTransactionHistory());
                newTransaction.setTransactionOrder(new Long(inventoryItem.getTransactionHistory().size() + 1));
                InventoryTransaction latestTransaction = inventoryItem.getTransactionHistory().get(0);
                newTransaction.setTransactedItem(inventoryItem);
                newTransaction.setInvoiceDate(latestTransaction.getInvoiceDate());
                newTransaction.setInvoiceNumber(latestTransaction.getInvoiceNumber());
                newTransaction.setSalesOrderNumber(latestTransaction.getSalesOrderNumber());
                newTransaction.setSeller(this.selectedDealerForRMT);
                newTransaction.setOwnerShip(this.selectedDealerForRMT);
                newTransaction.setBuyer(latestTransaction.getBuyer());
                newTransaction.setInvTransactionType(getInventoryTransactionType());
                newTransaction.setTransactionDate(Clock.today());
                newTransaction.setStatus(BaseDomain.ACTIVE);
                inventoryItem.setLatestBuyer(latestTransaction.getBuyer());
                inventoryItem.setCurrentOwner(selectedDealerForRMT);
                inventoryItem.setShipTo(selectedDealerForRMT);
                inventoryItem.getTransactionHistory().add(newTransaction);
                getInventoryService().updateInventoryItem(inventoryItem);
                campaignService.updateDealershipForCampaignNotification(inventoryItem, latestTransaction.getOwnerShip(), this.selectedDealerForRMT);
            }
        }
        addActionMessage("message.RMTPerformedForItem");
        return SUCCESS;
    }

    public void prepare() throws Exception {

    }

    @Override
    public void validate() {

    }

    private void populateInventoryItemsForRMT() {
        for (Iterator<InventoryItem> invItemIter = this.getInventoryItems().iterator(); invItemIter
                .hasNext();) {
            InventoryItem inventoryItem = invItemIter.next();
            MultipleInventoryAttributesMapper mapper = new MultipleInventoryAttributesMapper();
            mapper.setInventoryItem(inventoryItem);
            this.inventoryItemsForRMT.add(mapper);
        }
    }

    private void fetchComapanies() {
        List<ServiceProvider> dealers = new ArrayList<ServiceProvider>();
        if (!this.inventoryItemsForRMT.isEmpty()) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(this.inventoryItemsForRMT
                    .get(0).getInventoryItem().getBusinessUnitInfo().getName());
        }
        if (StringUtils.hasText(this.dealerSearch.getDealerNumber()) && StringUtils.hasText(this.dealerSearch.getName())) {
            dealers = this.orgService.findDealersByNumberOrName(this.dealerSearch.getDealerNumber(),this.dealerSearch.getName());
        }
        else if (StringUtils.hasText(this.dealerSearch.getDealerNumber()))
        {
        	dealers = this.orgService.findDealersByNumber(this.dealerSearch.getDealerNumber());
        }
        else if (StringUtils.hasText(this.dealerSearch.getName())) {
            dealers = this.orgService.findDealersByName(this.dealerSearch.getName());
        }
        if (dealers != null && dealers.size() > 0) {
            this.searchedDealers = dealers;
        }
        Organization retailingDealer = this.inventoryItemsForRMT.get(0).getInventoryItem().getDealer();
        for (Iterator iterator = this.searchedDealers.iterator(); iterator.hasNext();) {
        	ServiceProvider dealer = new HibernateCast<ServiceProvider>().cast(iterator.next());
            if (retailingDealer.getId().longValue() == dealer.getId().longValue()) {
                iterator.remove();
            }
        }
    }

    public List<MultipleInventoryAttributesMapper> getInventoryItemsForRMT() {
        return this.inventoryItemsForRMT;
    }

    public ServiceProvider getDealerSearch() {
        return this.dealerSearch;
    }

    public void setDealerSearch(ServiceProvider dealerSearch) {
        this.dealerSearch = dealerSearch;
    }

    public List<ServiceProvider> getSearchedDealers() {
        return this.searchedDealers;
    }

    public void setSearchedDealers(List<ServiceProvider> searchedDealers) {
        this.searchedDealers = searchedDealers;
    }

    public ServiceProvider getSelectedDealerForRMT() {
        return this.selectedDealerForRMT;
    }

    public void setSelectedDealerForRMT(ServiceProvider selectedDealerForRMT) {
        this.selectedDealerForRMT = selectedDealerForRMT;
    }

    public InventoryTransactionType getInventoryTransactionType() {
        return this.inventoryTransactionType;
    }

    public void setInventoryTransactionType() {
        this.inventoryTransactionType = this.invTransactionService
                .getTransactionTypeByName(InvTransationType.RMT.getTransactionType());
    }

    public void setInvTransactionService(InventoryTransactionService invTransactionService) {
        this.invTransactionService = invTransactionService;
    }

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public WarrantyUtil getWarrantyUtil() {
        return warrantyUtil;
    }

    public void setWarrantyUtil(WarrantyUtil warrantyUtil) {
        this.warrantyUtil = warrantyUtil;
    }

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}
    
	public String getDealerNumber(String dealerName){
		if(dealerName != null){
			try {
				String splitString[] = dealerName.split("-");
				return splitString[splitString.length - 1];
			} catch (Exception e) {
				logger.error(e);
				return "";
			}
		}
		return "";
    }

    public OrgService getOrgService() {
        return orgService;
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }
    
    
    public boolean isBulkRTT() {
		return bulkRTT;
}

	public void setBulkRTT(boolean bulkRTT) {
		this.bulkRTT = bulkRTT;
	}

}
