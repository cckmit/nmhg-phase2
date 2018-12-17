
package tavant.twms.web.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignRepository;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemAttributeValue;
import tavant.twms.domain.inventory.InventoryItemAttributeValueService;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryStolenTransaction;
import tavant.twms.domain.inventory.InventoryStolenTransactionXMLConverter;
import tavant.twms.domain.orgmodel.Attribute;
import tavant.twms.domain.orgmodel.AttributeConstants;
import tavant.twms.domain.orgmodel.AttributeService;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.multipleinventorypicker.MultipleInventoryPickerAction;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

/**
 * @author fatima.marneni
 */
@SuppressWarnings("serial")
public class StolenInventoryAction extends MultipleInventoryPickerAction
		implements Preparable, Validateable {

	private String stolenComments;
	
	private String stolenReason;

	private InventoryItem inventoryItem;

	private final List<InventoryItem> stolenInventoryItems = new ArrayList<InventoryItem>();

	private boolean searchForStolenInventory;

	private AttributeService attributeService;

	private InventoryItemAttributeValueService inventoryItemAttributeValueService;

	private InventoryStolenTransaction inventoryStolenTransaction;

	private InventoryStolenTransactionXMLConverter inventoryStolenTransactionXMLConverter;

	private List<Campaign> campaign;

	private CampaignRepository campaignRepository;

	private ConfigParam configParam;

	private ConfigParamService configParamService;

	private CalendarDate stolenDate;

	private CalendarDate unStolenDate;
	
	private Set<Campaign> campaigns = new HashSet<Campaign>();

	@Override
	public String searchInventories() throws IOException {
		if (isLoggedInUserADealer()) {
			getInventorySearchCriteria().setDealerNumber(
					getLoggedInUsersDealership().getDealerNumber());
		}
		super.searchInventories();
		return SUCCESS;
	}

	public String searchStolenInventories() throws IOException {
		setSearchForStolenInventory(true);
		super.searchInventories();
		return SUCCESS;
	}

	@Override
	protected void addSearchConstraints() {
		super.addSearchConstraints();
		if (isSearchForStolenInventory()) {
			getInventorySearchCriteria().setConditionTypeIs(
					InventoryItemCondition.STOLEN);
		} else {
			getInventorySearchCriteria().setConditionTypeNot(
					InventoryItemCondition.STOLEN);
		}
	}

	public void prepare() throws Exception {

	}

	@Override
	public void validate() {

	}

	private void populateInventoryItemsForStolen() {
		for (InventoryItem invItemIter : this.getInventoryItems()) {
			stolenInventoryItems.add(invItemIter);
		}
	}

	public String stolenInventory() {
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventoryItem
				.getBusinessUnitInfo().getName());
		Attribute attributeName = new Attribute(
				AttributeConstants.STOLEN_COMMENTS);
		attributeService.createAttribute(attributeName);
		if (this.stolenDate == null) {
			addActionError("error.stolen.stolenDateRequired");
			return INPUT;
		}
		if (this.stolenDate != null && this.stolenDate.isAfter(Clock.today())) {
			addActionError("error.stolen.stolenDateLessThanOrEqualsToday");
			return INPUT;
		}
		InventoryItemAttributeValue stolenReason = new InventoryItemAttributeValue(
				attributeName, previousConditionStolenAttributes(inventoryItem
						.getConditionType()));
		inventoryItemAttributeValueService
				.createInventoryItemAttributeValue(stolenReason);
		int indexAtWhichAdded = inventoryItem.getInventoryItemAttrVals().size();
		if(indexAtWhichAdded!=0 && inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded-1).getAttribute().getName().equalsIgnoreCase("unStolenComments")){
			if(this.stolenDate!=null && this.stolenDate.isBefore(getStolenTransaction(inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded-1).getValue()).getDateOfStolenOrUnstolen())){
				addActionError("error.stolen.cannotMarkBeforePrevious");
				return INPUT;
			}
		}
		inventoryItem.getInventoryItemAttrVals().add(indexAtWhichAdded,
				stolenReason);
		inventoryItem.setConditionType(InventoryItemCondition.STOLEN);
		getInventoryService().updateInventoryItem(inventoryItem);
		addActionMessage("message.stole.machineStolen", inventoryItem
				.getSerialNumber());
		campaignNotificationWarning();
		return SUCCESS;
	}
	
    public InventoryStolenTransaction getStolenTransaction(String attrValue) {
        return (InventoryStolenTransaction) inventoryStolenTransactionXMLConverter
                        .convertXMLToObject(attrValue);
}

	public void campaignNotificationWarning() {
		this.campaigns = this.campaignRepository
		.getCampaignsForInventoryItem(this.inventoryItem);
		if(this.campaigns != null && !this.campaigns.isEmpty()){
			List<ListOfValues> campaignClassForWarning= configParamService.getListOfValues(
					ConfigName.CAMPAIGN_CLASS_FOR_WARNING_ON_EHP.getName());
			if(campaignClassForWarning != null)
				for(ListOfValues value :campaignClassForWarning){
					if(value != null && value.getCode()!= null)
						for(Campaign campain:this.campaigns){
							if(value.getCode().equals(campain.getCampaignClass().getCode())){
								addActionWarning("ehp.campaign.warning", campain.getCampaignClass().getDescription());
								break;
							}
						}
				}
		}
	}

	public String unStolenInventory() {
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventoryItem
				.getBusinessUnitInfo().getName());		
		InventoryItemAttributeValue invItemAttrVals = getLatestInventoryItemAttributeValue(inventoryItem.getInventoryItemAttrVals());
		InventoryStolenTransaction stolenComments = (InventoryStolenTransaction) inventoryStolenTransactionXMLConverter
				.convertXMLToObject(invItemAttrVals.getValue());
		Attribute attributeName = new Attribute(
				AttributeConstants.UN_STOLEN_COMMENTS);
		attributeService.createAttribute(attributeName);
		if (this.unStolenDate == null) {
			addActionError("error.stolen.unStolenDateRequired");
			return INPUT;
		} else {
			if (this.unStolenDate.isAfter(Clock.today())) {
				addActionError("error.stolen.unStolenDateLessThanOrEqualsToday");
				return INPUT;
			}
			if (this.unStolenDate.isBefore(stolenComments.getDateOfStolenOrUnstolen())) {
				addActionError("error.stolen.unStolenDateGreaterThanOrEqualsStolenDate",
						new String[] { inventoryItem.getSerialNumber() });
				return INPUT;
			}
		}
		InventoryItemAttributeValue unStolenReason = new InventoryItemAttributeValue(
				attributeName, previousConditionUnStolenAttributes(inventoryItem
						.getConditionType()));
		inventoryItemAttributeValueService
				.createInventoryItemAttributeValue(unStolenReason);
		inventoryItem.getInventoryItemAttrVals().add(unStolenReason);
		inventoryItem.setConditionType(new InventoryItemCondition(stolenComments.getPreviousItemCondition()));
		getInventoryService().updateInventoryItem(inventoryItem);
		addActionMessage("message.unStolen.machineUnStolen");
		campaignNotificationWarning();
		return SUCCESS;
	}

	public String selectInventoriesForFleetStolen() {
		return SUCCESS;
	}

	@Override
	public String handleInventorySelection() throws IOException {
		super.handleInventorySelection();
		populateInventoryItemsForStolen();
		return SUCCESS;
	}

	public String confirmFleetStolen() {	
		if (this.stolenDate == null) {
			addActionError("error.stolen.stolenDateRequired");
			return INPUT;
		}
		if (this.stolenDate != null && this.stolenDate.isAfter(Clock.today())) {
			addActionError("error.stolen.stolenDateLessThanOrEqualsToday");
			return INPUT;
		}
		this.stolenInventoryItems.removeAll(Collections.singletonList(null));			
		return SUCCESS;
	}	

	public String fleetInventoryStolen() {
		for (InventoryItem inventryItem : stolenInventoryItems) {
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventryItem
					.getBusinessUnitInfo().getName());
			Attribute attributeName = new Attribute(
					AttributeConstants.STOLEN_COMMENTS);
			attributeService.createAttribute(attributeName);
			InventoryItemAttributeValue stolenReason = new InventoryItemAttributeValue(
					attributeName, previousConditionStolenAttributes(inventryItem
							.getConditionType()));
			int indexAtWhichAdded = inventryItem.getInventoryItemAttrVals().size();
			inventoryItemAttributeValueService
					.createInventoryItemAttributeValue(stolenReason);			
			inventryItem.getInventoryItemAttrVals().add(indexAtWhichAdded,
					stolenReason);
			inventryItem.setConditionType(InventoryItemCondition.STOLEN);
			getInventoryService().updateInventoryItem(inventryItem);			
		}
		addActionMessage("messages.stolen.selectedMachinesAreStolen");
		return SUCCESS;
	}

	public String selectInventoriesForFleetUnStolen() {
		setSearchForStolenInventory(true);
		return SUCCESS;
	}
	public String confirmFleetUnStolen() {
		this.stolenInventoryItems.removeAll(Collections.singletonList(null));	
		for (InventoryItem inventoryItem : stolenInventoryItems) {			
			InventoryItemAttributeValue invItemAttrVals = getLatestInventoryItemAttributeValue(inventoryItem.getInventoryItemAttrVals());			
			InventoryStolenTransaction stolenComments = (InventoryStolenTransaction) inventoryStolenTransactionXMLConverter
					.convertXMLToObject(invItemAttrVals.getValue());
			if (this.unStolenDate == null) {
				addActionError("error.stolen.unStolenDateRequired");
				return INPUT;
			} else {
				if (this.unStolenDate.isAfter(Clock.today())) {
					addActionError("error.stolen.unStolenDateLessThanOrEqualsToday");
					return INPUT;
				}
				if (this.unStolenDate.isBefore(stolenComments
						.getDateOfStolenOrUnstolen())) {
					addActionError("error.stolen.unStolenDateGreaterThanOrEqualsStolenDate",
							new String[] { inventoryItem.getSerialNumber() });					
					return INPUT;
				}
			}			
		}
		return SUCCESS;
	}	
	private InventoryItemAttributeValue getLatestInventoryItemAttributeValue(List<InventoryItemAttributeValue> invItemAttrVals) {		
		List<InventoryItemAttributeValue> invItemAttrVals2 = new ArrayList<InventoryItemAttributeValue>();
		for (InventoryItemAttributeValue iiav : invItemAttrVals) {
			if (iiav.getAttribute().getName() != null && !TWMSWebConstants.DATA_SOURCE.equals(iiav.getAttribute().getName())) {
				invItemAttrVals2.add(iiav);
			}
		}
		Assert.isTrue(!invItemAttrVals2.isEmpty(),"No Latest Inventory Item Attribute");
		return invItemAttrVals2.get(invItemAttrVals2.size()-1);
		
	}
	public String fleetInventoryUnStolen() {
		for (InventoryItem inventryItem : stolenInventoryItems) {			
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventryItem
					.getBusinessUnitInfo().getName());
			InventoryItemAttributeValue invItemAttrVals = getLatestInventoryItemAttributeValue(inventryItem.getInventoryItemAttrVals());
			InventoryStolenTransaction stolenComments = (InventoryStolenTransaction) inventoryStolenTransactionXMLConverter
					.convertXMLToObject(invItemAttrVals.getValue());
			Attribute attributeName = new Attribute(
					AttributeConstants.UN_STOLEN_COMMENTS);
			attributeService.createAttribute(attributeName);			
			InventoryItemAttributeValue unStolenReason = new InventoryItemAttributeValue(
					attributeName, previousConditionUnStolenAttributes(inventryItem
							.getConditionType()));
			inventoryItemAttributeValueService
					.createInventoryItemAttributeValue(unStolenReason);
			inventryItem.getInventoryItemAttrVals().add(unStolenReason);
			if (stolenComments.getPreviousItemCondition().equals("NEW"))
				inventryItem.setConditionType(InventoryItemCondition.NEW);
			else
				inventryItem
						.setConditionType(InventoryItemCondition.REFURBISHED);
			getInventoryService().updateInventoryItem(inventryItem);
		}
		addActionMessage("messages.stolen.selectedMachinesAreUnStolen");
		return SUCCESS;
	}

	private String previousConditionStolenAttributes(
			InventoryItemCondition itemCondition) {
		setInventoryStolenTransaction(new InventoryStolenTransaction(
				this.stolenComments, Clock.today(), itemCondition
						.getItemCondition(), this.stolenDate));
		return this.inventoryStolenTransactionXMLConverter
				.convertObjectToXML(getInventoryStolenTransaction());
	}

	private String previousConditionUnStolenAttributes(
			InventoryItemCondition itemCondition) {
		setInventoryStolenTransaction(new InventoryStolenTransaction(
				this.stolenComments, Clock.today(), itemCondition
						.getItemCondition(), this.unStolenDate));
		return this.inventoryStolenTransactionXMLConverter
				.convertObjectToXML(getInventoryStolenTransaction());
	}
	
	public String getStolenComments() {
		return stolenComments;
	}

	public void setStolenComments(String stolenComments) {
		this.stolenComments = stolenComments;
	}

	public List<InventoryItem> getStolenInventoryItems() {
		return stolenInventoryItems;
	}

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public boolean isSearchForStolenInventory() {
		return searchForStolenInventory;
	}

	public void setSearchForStolenInventory(boolean searchForStolenInventory) {
		this.searchForStolenInventory = searchForStolenInventory;
	}

	public InventoryStolenTransaction getInventoryStolenTransaction() {
		return inventoryStolenTransaction;
	}

	public void setInventoryStolenTransaction(
			InventoryStolenTransaction inventoryStolenTransaction) {
		this.inventoryStolenTransaction = inventoryStolenTransaction;
	}

	public void setInventoryStolenTransactionXMLConverter(
			InventoryStolenTransactionXMLConverter inventoryStolenTransactionXMLConverter) {
		this.inventoryStolenTransactionXMLConverter = inventoryStolenTransactionXMLConverter;
	}

	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	public void setInventoryItemAttributeValueService(
			InventoryItemAttributeValueService inventoryItemAttributeValueService) {
		this.inventoryItemAttributeValueService = inventoryItemAttributeValueService;
	}

	public List<Campaign> getCampaign() {
		return campaign;
	}

	public void setCampaign(List<Campaign> campaign) {
		this.campaign = campaign;
	}	

	public CampaignRepository getCampaignRepository() {
		return campaignRepository;
	}

	public void setCampaignRepository(CampaignRepository campaignRepository) {
		this.campaignRepository = campaignRepository;
	}

	public Set<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(Set<Campaign> campaigns) {
		this.campaigns = campaigns;
	}

	public ConfigParam getConfigParam() {
		return configParam;
	}

	public void setConfigParam(ConfigParam configParam) {
		this.configParam = configParam;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public CalendarDate getStolenDate() {
		return stolenDate;
	}

	public void setStolenDate(CalendarDate stolenDate) {
		this.stolenDate = stolenDate;
	}

	public CalendarDate getUnStolenDate() {
		return unStolenDate;
	}

	public void setUnStolenDate(CalendarDate unStolenDate) {
		this.unStolenDate = unStolenDate;
	}

	public String getStolenReason() {
		return stolenReason;
	}

	public void setStolenReason(String stolenReason) {
		this.stolenReason = stolenReason;
	}

}
