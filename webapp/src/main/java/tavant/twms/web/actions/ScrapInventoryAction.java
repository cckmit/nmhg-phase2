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
import tavant.twms.domain.inventory.InventoryScrapTransaction;
import tavant.twms.domain.inventory.InventoryScrapTransactionXMLConverter;
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
public class ScrapInventoryAction extends MultipleInventoryPickerAction
		implements Preparable, Validateable {

	private String scrapComments;
	
	private String scrapReason;

	private InventoryItem inventoryItem;

	private final List<InventoryItem> scrapInventoryItems = new ArrayList<InventoryItem>();

	private boolean searchForScrappedInventory;

	private AttributeService attributeService;

	private InventoryItemAttributeValueService inventoryItemAttributeValueService;

	private InventoryScrapTransaction inventoryScrapTransaction;

	private InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter;

	private List<Campaign> campaign;

	private CampaignRepository campaignRepository;

	private ConfigParam configParam;

	private ConfigParamService configParamService;

	private CalendarDate scrapDate;

	private CalendarDate unScrapDate;
	
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

	public String searchScrappedInventories() throws IOException {
		setSearchForScrappedInventory(true);
		super.searchInventories();
		return SUCCESS;
	}

	@Override
	protected void addSearchConstraints() {
		super.addSearchConstraints();
		if (isSearchForScrappedInventory()) {
			getInventorySearchCriteria().setConditionTypeIs(
					InventoryItemCondition.SCRAP);
		} else {
			getInventorySearchCriteria().setConditionTypeNot(
					InventoryItemCondition.SCRAP);
		}
	}

	public void prepare() throws Exception {

	}

	@Override
	public void validate() {

	}

	private void populateInventoryItemsForScrap() {
		for (InventoryItem invItemIter : this.getInventoryItems()) {
			scrapInventoryItems.add(invItemIter);
		}
	}

	public String scrapInventory() {
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventoryItem
				.getBusinessUnitInfo().getName());
		Attribute attributeName = new Attribute(
				AttributeConstants.SCRAP_COMMENTS);
		attributeService.createAttribute(attributeName);
		if (this.scrapDate == null) {
			addActionError("error.scrap.scrapDateRequired");
			return INPUT;
		}
		if (this.scrapDate != null && this.scrapDate.isAfter(Clock.today())) {
			addActionError("error.scrap.scrapDateLessThanOrEqualsToday");
			return INPUT;
		}
		InventoryItemAttributeValue scrapReason = new InventoryItemAttributeValue(
				attributeName, previousConditionScrapAttributes(inventoryItem
						.getConditionType()));
		inventoryItemAttributeValueService
				.createInventoryItemAttributeValue(scrapReason);
		int indexAtWhichAdded = inventoryItem.getInventoryItemAttrVals().size();
		inventoryItem.getInventoryItemAttrVals().add(indexAtWhichAdded,
				scrapReason);
		inventoryItem.setConditionType(InventoryItemCondition.SCRAP);
		getInventoryService().updateInventoryItem(inventoryItem);
		addActionMessage("message.scrap.machineScrapped", inventoryItem
				.getSerialNumber());
		campaignNotificationWarning();
		return SUCCESS;
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

	public String unScrapInventory() {
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventoryItem
				.getBusinessUnitInfo().getName());		
		InventoryItemAttributeValue invItemAttrVals = getLatestInventoryItemAttributeValue(inventoryItem.getInventoryItemAttrVals());
		InventoryScrapTransaction scrapComments = (InventoryScrapTransaction) inventoryScrapTransactionXMLConverter
				.convertXMLToObject(invItemAttrVals.getValue());
		Attribute attributeName = new Attribute(
				AttributeConstants.UN_SCRAP_COMMENTS);
		attributeService.createAttribute(attributeName);
		if (this.unScrapDate == null) {
			addActionError("error.scrap.unScrapDateRequired");
			return INPUT;
		} else {
			if (this.unScrapDate.isAfter(Clock.today())) {
				addActionError("error.scrap.unScrapDateLessThanOrEqualsToday");
				return INPUT;
			}
			if (this.unScrapDate.isBefore(scrapComments.getDateOfScrapOrUnscrap())) {
				addActionError("error.scrap.unScrapDateGreaterThanOrEqualsScrapDate",
						new String[] { inventoryItem.getSerialNumber() });
				return INPUT;
			}
		}
		InventoryItemAttributeValue unScrapReason = new InventoryItemAttributeValue(
				attributeName, previousConditionUnScrapAttributes(inventoryItem
						.getConditionType()));
		inventoryItemAttributeValueService
				.createInventoryItemAttributeValue(unScrapReason);
		inventoryItem.getInventoryItemAttrVals().add(unScrapReason);
		if (scrapComments.getPreviousItemCondition().equals("NEW"))
			inventoryItem.setConditionType(InventoryItemCondition.NEW);
		else
			inventoryItem.setConditionType(InventoryItemCondition.REFURBISHED);
		getInventoryService().updateInventoryItem(inventoryItem);
		addActionMessage("message.unScrap.machineUnScrapped");
		campaignNotificationWarning();
		return SUCCESS;
	}

	public String selectInventoriesForFleetScrap() {
		return SUCCESS;
	}

	@Override
	public String handleInventorySelection() throws IOException {
		super.handleInventorySelection();
		populateInventoryItemsForScrap();
		return SUCCESS;
	}

	public String confirmFleetScrap() {	
		if (this.scrapDate == null) {
			addActionError("error.scrap.scrapDateRequired");
			return INPUT;
		}
		if (this.scrapDate != null && this.scrapDate.isAfter(Clock.today())) {
			addActionError("error.scrap.scrapDateLessThanOrEqualsToday");
			return INPUT;
		}
		this.scrapInventoryItems.removeAll(Collections.singletonList(null));			
		return SUCCESS;
	}	

	public String fleetInventoryScrap() {
		for (InventoryItem inventryItem : scrapInventoryItems) {
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventryItem
					.getBusinessUnitInfo().getName());
			Attribute attributeName = new Attribute(
					AttributeConstants.SCRAP_COMMENTS);
			attributeService.createAttribute(attributeName);
			InventoryItemAttributeValue scrapReason = new InventoryItemAttributeValue(
					attributeName, previousConditionScrapAttributes(inventryItem
							.getConditionType()));
			int indexAtWhichAdded = inventryItem.getInventoryItemAttrVals().size();
			inventoryItemAttributeValueService
					.createInventoryItemAttributeValue(scrapReason);			
			inventryItem.getInventoryItemAttrVals().add(indexAtWhichAdded,
					scrapReason);
			inventryItem.setConditionType(InventoryItemCondition.SCRAP);
			getInventoryService().updateInventoryItem(inventryItem);			
		}
		addActionMessage("messages.scrap.selectedMachinesAreScrapped");
		return SUCCESS;
	}

	public String selectInventoriesForFleetUnScrap() {
		setSearchForScrappedInventory(true);
		return SUCCESS;
	}
	public String confirmFleetUnScrap() {
		this.scrapInventoryItems.removeAll(Collections.singletonList(null));	
		for (InventoryItem inventoryItem : scrapInventoryItems) {			
			InventoryItemAttributeValue invItemAttrVals = getLatestInventoryItemAttributeValue(inventoryItem.getInventoryItemAttrVals());			
			InventoryScrapTransaction scrapComments = (InventoryScrapTransaction) inventoryScrapTransactionXMLConverter
					.convertXMLToObject(invItemAttrVals.getValue());
			if (this.unScrapDate == null) {
				addActionError("error.scrap.unScrapDateRequired");
				return INPUT;
			} else {
				if (this.unScrapDate.isAfter(Clock.today())) {
					addActionError("error.scrap.unScrapDateLessThanOrEqualsToday");
					return INPUT;
				}
				if (this.unScrapDate.isBefore(scrapComments
						.getDateOfScrapOrUnscrap())) {
					addActionError("error.scrap.unScrapDateGreaterThanOrEqualsScrapDate",
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
	public String fleetInventoryUnScrap() {
		for (InventoryItem inventryItem : scrapInventoryItems) {			
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventryItem
					.getBusinessUnitInfo().getName());
			InventoryItemAttributeValue invItemAttrVals = getLatestInventoryItemAttributeValue(inventryItem.getInventoryItemAttrVals());
			InventoryScrapTransaction scrapComments = (InventoryScrapTransaction) inventoryScrapTransactionXMLConverter
					.convertXMLToObject(invItemAttrVals.getValue());
			Attribute attributeName = new Attribute(
					AttributeConstants.UN_SCRAP_COMMENTS);
			attributeService.createAttribute(attributeName);			
			InventoryItemAttributeValue unScrapReason = new InventoryItemAttributeValue(
					attributeName, previousConditionUnScrapAttributes(inventryItem
							.getConditionType()));
			inventoryItemAttributeValueService
					.createInventoryItemAttributeValue(unScrapReason);
			inventryItem.getInventoryItemAttrVals().add(unScrapReason);
			if (scrapComments.getPreviousItemCondition().equals("NEW"))
				inventryItem.setConditionType(InventoryItemCondition.NEW);
			else
				inventryItem
						.setConditionType(InventoryItemCondition.REFURBISHED);
			getInventoryService().updateInventoryItem(inventryItem);
		}
		addActionMessage("messages.scrap.selectedMachinesAreUnScrapped");
		return SUCCESS;
	}

	private String previousConditionScrapAttributes(
			InventoryItemCondition itemCondition) {
		setInventoryScrapTransaction(new InventoryScrapTransaction(
				this.scrapComments, Clock.today(), itemCondition
						.getItemCondition(), this.scrapDate));
		return this.inventoryScrapTransactionXMLConverter
				.convertObjectToXML(getInventoryScrapTransaction());
	}

	private String previousConditionUnScrapAttributes(
			InventoryItemCondition itemCondition) {
		setInventoryScrapTransaction(new InventoryScrapTransaction(
				this.scrapComments, Clock.today(), itemCondition
						.getItemCondition(), this.unScrapDate));
		return this.inventoryScrapTransactionXMLConverter
				.convertObjectToXML(getInventoryScrapTransaction());
	}
	
	public String getScrapComments() {
		return scrapComments;
	}

	public void setScrapComments(String scrapComments) {
		this.scrapComments = scrapComments;
	}

	public List<InventoryItem> getScrapInventoryItems() {
		return scrapInventoryItems;
	}

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public boolean isSearchForScrappedInventory() {
		return searchForScrappedInventory;
	}

	public void setSearchForScrappedInventory(boolean searchForScrappedInventory) {
		this.searchForScrappedInventory = searchForScrappedInventory;
	}

	public InventoryScrapTransaction getInventoryScrapTransaction() {
		return inventoryScrapTransaction;
	}

	public void setInventoryScrapTransaction(
			InventoryScrapTransaction inventoryScrapTransaction) {
		this.inventoryScrapTransaction = inventoryScrapTransaction;
	}

	public void setInventoryScrapTransactionXMLConverter(
			InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter) {
		this.inventoryScrapTransactionXMLConverter = inventoryScrapTransactionXMLConverter;
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

	public CalendarDate getScrapDate() {
		return scrapDate;
	}

	public void setScrapDate(CalendarDate scrapDate) {
		this.scrapDate = scrapDate;
	}

	public CalendarDate getUnScrapDate() {
		return unScrapDate;
	}

	public void setUnScrapDate(CalendarDate unScrapDate) {
		this.unScrapDate = unScrapDate;
	}

	public String getScrapReason() {
		return scrapReason;
	}

	public void setScrapReason(String scrapReason) {
		this.scrapReason = scrapReason;
	}

}
