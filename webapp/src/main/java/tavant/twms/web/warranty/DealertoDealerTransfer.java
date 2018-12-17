package tavant.twms.web.warranty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.inventory.*;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.warranty.WarrantyUtil;
import tavant.twms.infra.BaseDomain;
import tavant.twms.web.multipleinventorypicker.MultipleInventoryPickerAction;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

@SuppressWarnings("serial")
public class DealertoDealerTransfer extends MultipleInventoryPickerAction implements Preparable,
        Validateable {

    private ServiceProvider dealerSearch;

    private List<ServiceProvider> searchedDealers = new ArrayList<ServiceProvider>(5);

    private final List<MultipleInventoryAttributesMapper> selectedInventoryItems = new ArrayList<MultipleInventoryAttributesMapper>(
            5);

    private ServiceProvider selectedDealerForD2D;

    private InventoryTransaction invTransaction;

    private final String transactionTypeString = InvTransationType.DEALER_TO_DEALER.toString();
    
    private CampaignService campaignService;
    
    private InventoryItem inventoryItem = null;

    private WarrantyUtil warrantyUtil ;
    
    private List<OrganizationAddress> servicingLocations;
    
    private String shipToSiteNumber;
    
    private OrgService orgService;
    
    private static Logger logger = LogManager
			.getLogger(DealertoDealerTransfer.class);

    public void prepare() throws Exception {
    }

    @Override
    protected void addSearchConstraints() {
        super.addSearchConstraints();
        if(isLoggedInUserADealer()){
			getInventorySearchCriteria().setDealerId(getLoggedInUsersDealership().getId());
		}
        getInventorySearchCriteria().setInventoryType(InventoryType.STOCK);
        getInventorySearchCriteria().setConditionTypeNot(InventoryItemCondition.SCRAP);
        getInventorySearchCriteria().setWarrantyCheck(true);
    }

    @Override
	public String searchInventories() throws IOException {
        getInventorySearchCriteria().setAllowedDealers(orgService.getChildOrganizationsIds(getLoggedInUsersOrganization().getId()));
		super.searchInventories();
		return SUCCESS;
	}

    public String showInventorySelection(){
    	populateInvMapperItemsForD2D();
    	if (this.inventoryItem != null
				&& this.inventoryItem.getConditionType()
						.getItemCondition().equals("SCRAP")){
        	addActionError("message.scrap.machineScrapped",this.inventoryItem.getSerialNumber());
        }
    	if (this.inventoryItem != null
				&& this.inventoryItem.getConditionType()
						.getItemCondition().equals("STOLEN")){
        	addActionError("message.stole.machineStolen",this.inventoryItem.getSerialNumber());
        }
    	if(isBuConfigAMER() && this.inventoryItem !=null && this.inventoryItem.getPreOrderBooking()==Boolean.TRUE){
    		addActionError("error.preOrderBooking.D2D");
    	}
    	else if(this.inventoryItem !=null && this.inventoryItem.getPreOrderBooking()==Boolean.TRUE){
    		addActionWarning("warning.preOrderBooking");
    	}
        if (this.inventoryItem != null && !this.inventoryItem.isRetailed()
                && this.getInventoryItem().getPendingWarranty()) {
        	if(inventoryItem.getLatestWarranty().getStatus().getStatus()
        			.equals(WarrantyStatus.DRAFT.getStatus())) {
        		addActionError("error.inventory.drsavedasdraft", this.inventoryItem
        				.getSerialNumber());
        	} else {
        		addActionError("error.inventory.drfiled", this.inventoryItem
    					.getSerialNumber());
        	}
		}
        if(hasActionErrors()){
        	return INPUT;
        }
        return SUCCESS;
    }
    
    @Override
    public String handleInventorySelection() throws IOException {
        super.handleInventorySelection();
        populateInvMapperItemsForD2D();
        return SUCCESS;
    }   

    /**
     * This API is to populate mapper elements from inventory item
     */
    private void populateInvMapperItemsForD2D() {
        for (InventoryItem inventoryItem : this.getInventoryItems()) {
        	if(inventoryItem != null)
        	{
	            MultipleInventoryAttributesMapper mapper = new MultipleInventoryAttributesMapper();
	            mapper.setInventoryItem(inventoryItem);
	            this.selectedInventoryItems.add(mapper);
	            if(inventoryItem.getConditionType()
						.getItemCondition().equals("STOLEN")){
	            	addActionError("message.stole.machineStolen",inventoryItem.getSerialNumber());
	            }
        	}
        }
        for(MultipleInventoryAttributesMapper multipleInventoryAttributesMapper : selectedInventoryItems){
        	if(multipleInventoryAttributesMapper !=null && multipleInventoryAttributesMapper.getInventoryItem().getPreOrderBooking()==Boolean.TRUE){
        		addActionWarning("warning.preOrderBooking.D2D");
        	}
        }
        if(this.inventoryItem != null)
        {
        	MultipleInventoryAttributesMapper mapper = new MultipleInventoryAttributesMapper();
            mapper.setInventoryItem(this.inventoryItem);
            this.selectedInventoryItems.add(mapper);
        }
    }

    public String show() {
        return SUCCESS;
    }

    public String setSelectedInvsForD2D() {
        for (Iterator<MultipleInventoryAttributesMapper> iterator = this.selectedInventoryItems
                .iterator(); iterator.hasNext();) {
            MultipleInventoryAttributesMapper invItem = iterator.next();
            if (invItem == null) {
                iterator.remove();
            }
            if(invItem.getInventoryItem().getConditionType().getItemCondition().equals("STOLEN")){
            	addActionError("message.stole.machineStolen", invItem.getInventoryItem().getSerialNumber());
       			return INPUT;
            }
        }
        return SUCCESS;
    }

    public String showD2DConfirmation() {
    	setSelectedDealerForD2D();
    	servicingLocations = orgService.getAddressesForOrganization(selectedDealerForD2D);
        return SUCCESS;
    }

    public String searchCompanies() {
    	ServiceProvider loggedInDealer = this.getLoggedInUsersDealership();
        List<ServiceProvider> dealers = new ArrayList<ServiceProvider>();
        if (!this.selectedInventoryItems.isEmpty()) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(this.selectedInventoryItems
                    .get(0).getInventoryItem().getBusinessUnitInfo().getName());
        }
        if (StringUtils.hasText(this.dealerSearch.getDealerNumber()) && StringUtils.hasText(this.dealerSearch.getName())) {
            dealers = this.orgService.findDealersByNumberOrName(this.dealerSearch.getDealerNumber(),this.dealerSearch.getName());
        }
        else if (StringUtils.hasText(this.dealerSearch.getDealerNumber())) {
        	dealers = this.orgService.findDealersByNumber(this.dealerSearch.getDealerNumber());
        }
        else if (StringUtils.hasText(this.dealerSearch.getName())) {
            dealers = this.orgService.findDealersByName(this.dealerSearch.getName());
        }
        if (dealers != null && dealers.size() > 0) {
            this.searchedDealers = dealers;
        }
        for (Iterator<ServiceProvider> dealerItr = this.searchedDealers.iterator(); dealerItr.hasNext();) {
        	ServiceProvider dealer = dealerItr.next();
            for (Iterator<MultipleInventoryAttributesMapper> inventoryItr = this.selectedInventoryItems
                    .iterator(); inventoryItr.hasNext();) {
                MultipleInventoryAttributesMapper invItem = inventoryItr.next();
                // An inventory cannot be moved from D1 to D1 itself
                if (invItem.getInventoryItem().getDealer() != null) {
                    if (invItem.getInventoryItem().getDealer().getId().longValue() == dealer
                            .getId().longValue()) {
                        dealerItr.remove();
                        break;
                    } else if (loggedInDealer != null) {
                        if (dealer.getId().longValue() == loggedInDealer.getId().longValue()) {
                            dealerItr.remove();
                            break;
                        }
                    }
                }
            }
        }
        return SUCCESS;
    }

    public String performD2D() {
    	if(shipToSiteNumber == null || shipToSiteNumber.equals("-1")){
    		addActionError("error.d2d.shipToSiteNumber");
    		return INPUT;
    	}
        if (this.selectedDealerForD2D != null && this.selectedInventoryItems != null
                && this.selectedInventoryItems.size() > 0) {
            for (Iterator<MultipleInventoryAttributesMapper> invItemIter = this.selectedInventoryItems
                    .iterator(); invItemIter.hasNext();) {
                InventoryTransaction newTransaction = new InventoryTransaction();
                MultipleInventoryAttributesMapper invItemMapper = invItemIter.next();
                InventoryItem inventoryItem = invItemMapper.getInventoryItem();
                newTransaction.setTransactionOrder(new Long(inventoryItem.getTransactionHistory()
                        .size() + 1));
                Collections.sort(inventoryItem.getTransactionHistory());
                inventoryItem.getTransactionHistory().get(0).setStatus(BaseDomain.INACTIVE);
                newTransaction.setSeller(inventoryItem.getTransactionHistory().get(0).getBuyer());
                newTransaction.setOwnerShip(this.selectedDealerForD2D);
                newTransaction.setBuyer(this.selectedDealerForD2D);
                newTransaction.setInvTransactionType(this.invTransaction.getInvTransactionType());
                newTransaction.setTransactionDate(Clock.today());
                newTransaction.setStatus(BaseDomain.ACTIVE);
                newTransaction.setShipToSiteNumber(newTransaction.getModifiedSiteNumber(shipToSiteNumber));
                inventoryItem.setLatestBuyer(selectedDealerForD2D);
                inventoryItem.setCurrentOwner(selectedDealerForD2D);
                inventoryItem.setShipTo(selectedDealerForD2D);
                newTransaction.setTransactedItem(inventoryItem);
                inventoryItem.getTransactionHistory().add(newTransaction);
                getInventoryService().updateInventoryItem(inventoryItem);
                campaignService.updateDealershipForCampaignNotification(inventoryItem, inventoryItem.getTransactionHistory().get(0).getOwnerShip(), this.selectedDealerForD2D);
            }
        }
        addActionMessage("message.dealertodealer.success");
        return SUCCESS;
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

    public ServiceProvider getSelectedDealerForD2D() {
        return this.selectedDealerForD2D;
    }

    
    public void setSelectedDealerForD2D(ServiceProvider selectedDealerForD2D) {
    	this.selectedDealerForD2D = selectedDealerForD2D;
	}	
    
    public String setSelectedDealerForD2D() {
        this.selectedDealerForD2D = this.orgService.findDealerByName(this.selectedDealerForD2D
                .getName());
        return SUCCESS;
    }

    public List<MultipleInventoryAttributesMapper> getSelectedInventoryItems() {
        return this.selectedInventoryItems;
    }

    public String getTransactionTypeString() {
        return this.transactionTypeString;
    }

    public InventoryTransaction getInvTransaction() {
        return this.invTransaction;
    }

    public void setInvTransaction(InventoryTransaction invTransaction) {
        this.invTransaction = invTransaction;
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

	public List<OrganizationAddress> getServicingLocations() {
		return servicingLocations;
	}

	public void setServicingLocations(List<OrganizationAddress> servicingLocations) {
		this.servicingLocations = servicingLocations;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public String getShipToSiteNumber() {
		return shipToSiteNumber;
	}

	public void setShipToSiteNumber(String shipToSiteNumber) {
		this.shipToSiteNumber = shipToSiteNumber;
	}

}
