/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
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
package tavant.twms.web.admin.campaign;

import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.CODE;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.COMPLETE_CODE;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.ID;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.LABEL;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.NODE_TYPE;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.NODE_TYPE_LEAF;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.SERVICE_PROCEDURE_ID;
import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignAdminService;
import tavant.twms.domain.campaign.CampaignCoverage;
import tavant.twms.domain.campaign.CampaignLaborDetail;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignRangeCoverage;
import tavant.twms.domain.campaign.CampaignSectionPrice;
import tavant.twms.domain.campaign.CampaignSerialNumberCoverage;
import tavant.twms.domain.campaign.CampaignSerialNumbers;
import tavant.twms.domain.campaign.CampaignServiceDetail;
import tavant.twms.domain.campaign.CampaignServiceException;
import tavant.twms.domain.campaign.HussPartsToReplace;
import tavant.twms.domain.campaign.NonOEMPartToReplace;
import tavant.twms.domain.campaign.OEMPartToReplace;
import tavant.twms.domain.campaign.validation.CampaignValidationService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.MiscellaneousItem;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.common.CurrencyConversionException;
import tavant.twms.domain.common.CurrencyConversionFactor;
import tavant.twms.domain.common.CurrencyExchangeRateRepository;
import tavant.twms.domain.common.I18NCampaignText;
import tavant.twms.domain.common.I18NNonOemPartsDescription;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.ServiceProcedureDefinition;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.orgmodel.NationalAccount;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PaymentCondition;
import tavant.twms.domain.partreturn.WarehouseService;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.common.Document;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.i18n.ProductLocale;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.admin.jobcode.MergedTreeJSONifier;
import tavant.twms.web.claim.ClaimsAction;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;

/**
 * @author Kiran.Kollipara
 */
@SuppressWarnings("serial")
public class InitializeCampaignAction extends FileUploadAction implements
		Preparable {

    private static final Logger logger=Logger.getLogger(InitializeCampaignAction.class);

    private static final String SERIAL_NUMBERS = "SERIAL_NUMBERS";

	private static final String SERIAL_NUMBER_RANGES = "SERIAL_NUMBER_RANGES";

    public static final String SCRAP = "SCRAP";
    
    public static final String STOLEN = "STOLEN";

    private List<ItemGroup> products = new ArrayList<ItemGroup>();
	
	private Campaign campaign = new Campaign();
		
	private List<ListOfValues> campaignClasses = new ArrayList<ListOfValues>();

	private String id;

	private String campaignFor;

	private String attachOrDelete;

	private FailureStructureService failureStructureService;

	private MergedTreeJSONifier mergedTreeJSONifier;

	protected List<PaymentCondition> paymentConditions;

	private List<CampaignNotification> campaignNotifications;
	
	private List<Claim> claimsForCampaignNotifications;

	private PartReturnService partReturnService;

	private ProductLocaleService productLocaleService;

	public static final String SPECIFIED_HOURS = "specifiedLaborHours",
			WRAPPER_ID = "wrapperId",
			USE_SUGGESTED_HOURS = "useSuggestedHours";

	private static final String MANAGE_CAMPAIGN_PAGE="manageCampaign";

	private String locationPrefix;
	
	private String selectedBusinessUnit;

	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit;
	}

	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}

	private String jsonString;

	private WarehouseService warehouseService;

	private boolean forAllCampaignItems;

	private boolean active;

	private boolean removeItemsFromCampaign;

	private List<InventoryItem> itemsList;

	private List<ProductLocale> locales;

	private I18NCampaignText i18NcampaignDescription;

	private boolean redirectToPage1;

	private String defaultDescription;

	private int defaultLocaleIndex;

	private String defaultLocale=EN_US;

	private String index;

	private Boolean displayMessageToUser;
	
	private int rowIndex;

	private boolean partsReplacedInstalledSectionVisible;
	
	private boolean buPartReplaceableByNonBUPart;
	
	private ConfigParamService configParamService;

    private List<Currency> currencies;
    
    private Map<String,CostCategory> configuredCostCategories = new HashMap<String,CostCategory>();
    private LovRepository lovRepository;
    private Contract selectedContract; 
	private ContractService contractService;
	private boolean showWarning = false;
	private List<InventoryItem> itemsNotInCampaign = new ArrayList<InventoryItem>();	
	private int subRowIndex;
	
	private List<NationalAccount> selectedNationalAccounts=new ArrayList<NationalAccount>();
	
    private Party customerInformationToBeDisplayed;
    private Party dealerInformationToBeDisplayed;
    
	public CampaignStatistics campaignStatistics = new CampaignStatistics();

    CurrencyConversionFactor conversionFactor;
    	
    public void prepare() {
		if (StringUtils.hasText(id) || campaign.getId() != null) {
			Long idTobeUsed = id != null ? Long.parseLong(id) : campaign
					.getId();
			campaign = campaignAdminService.findById(idTobeUsed);
			String[] notificationStatus = {CampaignNotification.COMPLETE,CampaignNotification.INPROCESS};
			if (campaign.isNotificationsGenerated()) {
				campaignNotifications = campaignAssignmentService
						.findCampaignNotificationsForCampaign(campaign);
				if (campaignNotifications != null && !campaignNotifications.isEmpty()) {
					for (CampaignNotification campaignNotification : campaignNotifications) {
						if (Arrays.asList(notificationStatus).contains(
								campaignNotification.getNotificationStatus())) {
							claimsForCampaignNotifications = campaignAssignmentService
									.findAllClaimsForCampaignNotifications(campaignNotification);
							for (Claim claims : claimsForCampaignNotifications) {
								convertFromNaturalToBaseCurrency(claims);
								if(claims.getState().equals(ClaimState.ACCEPTED) ||claims.getState().equals(ClaimState.ACCEPTED_AND_CLOSED) || claims.getState().equals(ClaimState.PENDING_PAYMENT_SUBMISSION) ||
                                claims.getState().equals(ClaimState.PENDING_PAYMENT_RESPONSE)){
									this.campaignStatistics.unitsCompleted++;
								}
							}
				}
					}
				}
				campaignStatistics.setStatisticsData(campaignNotifications,claimsForCampaignNotifications);
			}	
		}
		populateCampaignFor();
		if(id!=null)
		{
		campaignClasses = lovRepository.findAll("CampaignClass");	
		}
		else
		{
        campaignClasses = lovRepository.findAllActive("CampaignClass");
		}
		products = itemGroupService.findGroupsForGroupType(PRODUCT);
		locales = productLocaleService.findAll();
		initializeDefaultLocaleIndex();
		partsReplacedInstalledSectionVisible = getConfigParamService().
		getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE.getName());
		buPartReplaceableByNonBUPart = getConfigParamService().getBooleanValue(ConfigName.BUPART_REPLACEABLEBY_NONBUPART.getName());
		this.setConfiguredCostCategories(configuredCostCategories);
		this.setCurrencies(currencies);
		if (this.selectedContract != null && this.selectedContract.getId() != null && campaign != null) {
			campaign.setContract(this.contractService.findContract(this.selectedContract.getId()));
		}else if(this.selectedContract != null && this.selectedContract.getId() == null && campaign != null){
			campaign.setContract(null);
		}
		
		if (this.campaign!=null && this.campaign.getHussPartsToReplace() != null
				&& !this.campaign.getHussPartsToReplace().isEmpty()) {
			prepareReplacedInstalledPartsDisplay(this.campaign);
		} else {
			setRowIndex(0);
		}	 
			setSubRowIndex(0);
   }
    
	private void prepareReplacedInstalledPartsDisplay(Campaign campaign) {		
		setRowIndex(campaign.getHussPartsToReplace().size());
	}

	@Override
	public void validate() {
		try {
			campaignValidationService.validate(campaign,campaignFor);
			List<InventoryItem> items = (campaign.getCampaignCoverage() != null ) ? campaign.getCampaignCoverage().getItems() : null;
			if ((items == null || items.isEmpty())
					&& SERIAL_NUMBERS.equals(campaignFor)) {
				super.validate();
			}
		} catch (CampaignServiceException e) {
			Set<String> errors = new HashSet<String>();
			List<String> actionErrors = e.actionErrors();
			errors.addAll(e.fieldErrors().values());
			errors.addAll(actionErrors);
			for (String anErrorMessage : errors) {
				addActionError(anErrorMessage);
			}
		}

	}

	public String load() {
		if (CampaignAdminService.CAMPAIGN_INACTIVE_STATUS
				.equalsIgnoreCase(campaign.getStatus())) {
            for (NonOEMPartToReplace nonOemParts : campaign.getNonOEMpartsToReplace()) {
                if(nonOemParts.getCampaignSectionPrice().isEmpty()){
                    List<CampaignSectionPrice> campaignNonOemPrices = new ArrayList<CampaignSectionPrice>();
                    for (Currency currency : currencies) {
                        CampaignSectionPrice price = new CampaignSectionPrice();
                        price.setPricePerUnit(Money.valueOf(new BigDecimal(0.00),currency));
                        price.setSectionName(CostCategory.NON_OEM_PARTS_COST_CATEGORY_CODE);
                        campaignNonOemPrices.add(price);
                    }
                    nonOemParts.setCampaignSectionPrice(campaignNonOemPrices);
                }
            }
            return MANAGE_CAMPAIGN_PAGE;
		}
		return SUCCESS;
	}

	public String process() throws Exception {
		/*TODO by hari: I don't think we are using this method, needs to be removed*/
		update();
		return SUCCESS;
	}

	public String showCustomerInformation(){
		return SUCCESS;
	}
	
	public String showDealerInformation(){
		return SUCCESS;
	}
	
	public String update() throws Exception {
		if (!StringUtils.hasText(campaign.getStatus())) {
			campaign.setStatus(CampaignAdminService.CAMPAIGN_DRAFT_STATUS);
		}		
		Long campaignId = campaign.getId();
		displayMessageToUser = Boolean.FALSE;
        validateCampaignDescription();                
		if("delete".equals(this.attachOrDelete) && campaignFor.equals(SERIAL_NUMBERS)){
			deleteItemsFromCampaign();
		}else{
			attachItemsToCampaign();
		}
		if (hasActionErrors()) {
			return INPUT;
		}
		removeInValidServiceProcedures(getFailureStructure());
		if (campaignId != null) {
			// QC-567: Claim AIR-1030980 is processed for SN 7062860 and campaign was completed but when I removed a different serial number the status of the campaign done has been modified in pending
			// Fix: We should not set the Notifications Generated as false; So commenting out the line which causes the issue
			//campaign.setNotificationsGenerated(false);
			campaignAdminService.update(campaign);
			String successActionMessage = ("delete".equalsIgnoreCase(this.attachOrDelete)&& campaignFor.equals(SERIAL_NUMBERS))?
					"success.campaign.inventoryItemRemoved" : "success.campaign.inventoryItemAdded";
			if (displayMessageToUser)
				addActionWarning(successActionMessage);
			if("delete".equalsIgnoreCase(this.attachOrDelete) && this.showWarning){
				addActionWarning("error.campaign.noItemRemoved");
			}
		} else {
			campaignAdminService.save(campaign);
		}
		/*if(campaign.getHussPartsToReplace() != null && campaign.getHussPartsToReplace().size() >0 ){
			setRowIndex(campaign.getHussPartsToReplace().size());
		}*/
		return SUCCESS;
	}
	// validate Non Oem Parts description for en_US Mandatory
	public void validateNonOemPartsDescription() {
	  if(campaign.getNonOEMpartsToReplace()!=null){
		  
		// checking duplicates
		Set<String> nonOEMPartToReplaceDescSet = new HashSet<String>(campaign
				.getNonOEMpartsToReplace().size());
		for (NonOEMPartToReplace nonOEMPartToReplace : campaign
				.getNonOEMpartsToReplace()) {
			
			if (!nonOEMPartToReplaceDescSet.add(nonOEMPartToReplace.getDescription())) {
				addActionError("error.campaign.duplicateNonOEMPartDescription", new String[]{nonOEMPartToReplace.getDescription()});
			}				
		}  
		  
		for (NonOEMPartToReplace nonOEMPartToReplace : campaign
				.getNonOEMpartsToReplace()) {
			for (I18NNonOemPartsDescription i18NonOemPartsDescription : nonOEMPartToReplace
					.getI18nNonOemPartsDescription()) {
				if (i18NonOemPartsDescription != null && EN_US.equalsIgnoreCase(
						i18NonOemPartsDescription.getLocale())
						&& !StringUtils.hasText(i18NonOemPartsDescription
								.getDescription())) {
					addActionError("error.campaign.nonOemDescptionfailureMessageUS");
				}
			}
			if (nonOEMPartToReplace.getNoOfUnits() == null || (nonOEMPartToReplace.getNoOfUnits() != null && nonOEMPartToReplace.getNoOfUnits().intValue() <= 0)) {
				addActionError("error.campaign.nonOemPartsQuantity");
			}
		 }		
       }
	}

	// validate Miscellaneous Parts
	public void validateMiscellaneousParts() {
		if (campaign.getMiscPartsToReplace() != null) {			
			// checking for duplicates Misc. Parts
			Set<String> miscPartNumbersSet = new HashSet<String>(campaign.getMiscPartsToReplace().size());
			for (NonOEMPartToReplace nonOEMPartToReplace : campaign
					.getMiscPartsToReplace()) {				
				MiscellaneousItem miscItem = nonOEMPartToReplace.getMiscItem();				
				if (miscItem != null && !miscPartNumbersSet.add(miscItem.getPartNumber())) { 
					addActionError("error.campaign.duplicateMiscItem", new String[]{miscItem.getPartNumber()});
				}				
			}
			
			// part number and qty validation
			for (NonOEMPartToReplace nonOEMPartToReplace : campaign
					.getMiscPartsToReplace()) {
				if (hasActionErrors()) {
					break;
				}
				MiscellaneousItem miscItem = nonOEMPartToReplace.getMiscItem();
				if (miscItem == null && nonOEMPartToReplace.getNoOfUnits() == null) {
					addActionError("error.campaign.miscPartRequired");
				}
				if (nonOEMPartToReplace.getNoOfUnits()!= null && miscItem == null) {
					addActionError("error.campaign.miscPartRequired");
				}
				if (miscItem != null && nonOEMPartToReplace.getNoOfUnits() == null || (nonOEMPartToReplace.getNoOfUnits()!= null
																	&& nonOEMPartToReplace.getNoOfUnits().intValue() <=0 )) {
					addActionError("error.campaign.miscPartQtyInvalid", new String[]{miscItem.getPartNumber()});
				}				 
			}
		}
	}

	public void initializeDefaultLocaleIndex(){
		for(int i=0;i<locales.size();i++){
			if(EN_US.equalsIgnoreCase(locales.get(i).getLocale())){
				defaultLocaleIndex=i;
				return;
			}
		}
	}
	//Saving I18N Campaign  description messages
	public String saveCampaignDescription(){
		//validateCampaignDescription();
		if(hasActionErrors()) {
			return INPUT;
		}
		try {
			campaignAdminService.update(campaign);
		} catch (Exception e) {
			addActionError("error.campaign.campaignUpdateFailed");
			return INPUT;
		}
		addActionMessage("message.campaign.i18nCreateSuccess");
		return SUCCESS;
	}

	//Saving I18N  Non Oem parts description messages
	public String saveNonOemDescription(){
	//	validateNonOemPartsDescription();
		if(hasActionErrors()) {
			return INPUT;
		}
		try {
			campaignAdminService.update(campaign);
		} catch (Exception e) {
			addActionError("error.campaign.campaignUpdateFailed");
			return INPUT;
		}
		addActionMessage("message.campaign.i18nCreateSuccess");
		return SUCCESS;
	}


	public void validateCampaignDescription(){
		for (I18NCampaignText i18nCampaignDesc : campaign.getI18nCampaignTexts()) {
		if(i18nCampaignDesc!=null && !StringUtils.hasText(i18nCampaignDesc.getDescription())&&
				EN_US.equalsIgnoreCase(i18nCampaignDesc.getLocale())){
			addActionError("error.campaign.failureMessageUS");
			}
		}

	}
	public String removeItems() throws Exception {
		Long campaignId = campaign.getId();
		deleteItemsFromCampaign();
		if (hasActionErrors()) {
			return INPUT;
		}
		if (campaignId != null) {
			campaign.setNotificationsGenerated(false);
			campaignAdminService.update(campaign);
		}
		return SUCCESS;
	}

	private void attachItemsToCampaign() throws ItemNotFoundException,
			CampaignServiceException { 
		List<InventoryItem> list;
        CampaignCoverage coverage = campaign.getCampaignCoverage();
        if(SERIAL_NUMBER_RANGES.equals(getCampaignFor())){
            displayMessageToUser = Boolean.TRUE;
            if(coverage.getRangeCoverage()==null || coverage.getRangeCoverage().getRanges() == null || coverage.getRangeCoverage().getRanges().isEmpty()){
                addActionError("error.campaign.noPatterns");
                return;
            }
			campaignAdminService.generateInventoryItemsForCampaign(campaign);
			if (campaign.getCampaignCoverage().getItems().isEmpty()) {
				addActionError("error.campaign.coverageItemsEmpty");
			}
			
        }else if(SERIAL_NUMBERS.equals(getCampaignFor())){
			list = removeDuplicates(parseExcelContent());
			if (!hasActionErrors() && isFirstTimeUploadNull(list)) {
				addActionError("error.campaign.invalidExcelUpload");
				return;
			}
			List<CampaignSerialNumbers> campaignSerialNumberList = getSerialNumbers();
			if (!hasActionErrors() && campaign.getId()== null && (list.isEmpty() || campaignSerialNumberList.isEmpty())) {
				addActionError("error.campaign.noInventoryItemfound");
				return;
			}
			if(hasActionErrors()|| validateSerialNumbersForScrap(list)){
				return ;
			}
			if(hasActionErrors()|| validateSerialNumbersForStolen(list)){
				return ;
			}
				CampaignSerialNumberCoverage serialNumberCoverage = coverage.getSerialNumberCoverage() == null ?
	                    new CampaignSerialNumberCoverage() : coverage.getSerialNumberCoverage();
	            serialNumberCoverage.setSerialNumbers(campaignSerialNumberList);
	            campaign.getCampaignCoverage().setSerialNumberCoverage(serialNumberCoverage);
			if(campaignNotifications != null && !campaignNotifications.isEmpty()){
				for(CampaignNotification campaignNotification:campaignNotifications){
					if(list.contains(campaignNotification.getItem())){
						list.remove(campaignNotification.getItem());
					}
				}
			}
			if(campaign.getId() != null){
				if (CollectionUtils.isNotEmpty(list) && 
					CollectionUtils.isNotEmpty(coverage.getSerialNumberCoverage().getItems())){
					list.removeAll(campaign.getCampaignCoverage().getItems());
					displayMessageToUser = Boolean.TRUE;
                }
			    if(coverage.getSerialNumberCoverage().getItems()==null)
				{
					coverage.getSerialNumberCoverage().setItems(list);
				}else{
                    coverage.getSerialNumberCoverage().getItems().addAll(list);
				}
				
			}else{
				coverage.getSerialNumberCoverage().setItems(list);
			}
        }
	}

	private List<InventoryItem> removeDuplicates(List<InventoryItem> inventoryItems) {
		// To remove duplicates if any
		if (CollectionUtils.isNotEmpty(inventoryItems))
		{
			Set<InventoryItem> uniqueInventoryItems = new HashSet<InventoryItem>(inventoryItems);
			return new ArrayList<InventoryItem>(uniqueInventoryItems);
		}
		return new ArrayList<InventoryItem>(1);
	}

	private void deleteItemsFromCampaign() throws ItemNotFoundException, CampaignServiceException{
		List<InventoryItem> listOfItemsToBeDeleted = new ArrayList<InventoryItem>();
        listOfItemsToBeDeleted = removeDuplicates(parseExcelContent());
        if (isFirstTimeUploadNull(listOfItemsToBeDeleted)) {
            addActionError("error.campaign.invalidExcelUpload");
            return;
        }
        CampaignCoverage coverage = campaign.getCampaignCoverage();
        List<CampaignSerialNumbers> campaignSerialNumberList = getSerialNumbers();
        if (campaign.getId()== null && (listOfItemsToBeDeleted.isEmpty() || campaignSerialNumberList.isEmpty()) ||
                (coverage == null || coverage.getItems().isEmpty()) ) {
            addActionError("error.campaign.noInventoryItemfound");
            return;
        }
        if(validateSerialNumbersForScrap(listOfItemsToBeDeleted)){
            return ;
        }
        if(!coverage.getItems().isEmpty() && (listOfItemsToBeDeleted.size() > 0)){
            for (int i = 0; i < listOfItemsToBeDeleted.size(); i++) {
                if(! coverage.getItems().contains(listOfItemsToBeDeleted.get(i))){
                    this.itemsNotInCampaign.add(listOfItemsToBeDeleted.get(i));
                }
            }

        }
        if(coverage.getItems().isEmpty()
                || !coverage.getItems().containsAll(listOfItemsToBeDeleted)){
            addActionError("error.campaign.selectedItemsNotInCampaign");
            this.showWarning = true;
            return;
        }

        if(listOfItemsToBeDeleted.isEmpty()){
            this.showWarning = true;
        }
		List<String> campaignExistsInventories = listInventoriesOfCampaignNotExists(listOfItemsToBeDeleted);
		if (campaignExistsInventories!=null && !campaignExistsInventories.isEmpty())
		{
			addActionError("error.campaign.campaignNotCompletedForInventories", campaignExistsInventories);
			return;
		}
		if(!hasActionErrors()){
			if (!listOfItemsToBeDeleted.isEmpty() && 
					campaign.getCampaignCoverage() != null){
				
                  if(campaign.getCampaignCoverage()!= null && campaign.getCampaignCoverage().getSerialNumberCoverage()!=null && !campaign.getCampaignCoverage().getSerialNumberCoverage().getItems().isEmpty() ){
                	  campaign.getCampaignCoverage().getSerialNumberCoverage().getItems().removeAll(listOfItemsToBeDeleted); 
                	  displayMessageToUser = Boolean.TRUE;
                  }
                  if(campaign.getCampaignCoverage()!= null &&  campaign.getCampaignCoverage().getRangeCoverage()!=null && !campaign.getCampaignCoverage().getRangeCoverage().getItems().isEmpty())
                  {
                	  campaign.getCampaignCoverage().getRangeCoverage().getItems().removeAll(listOfItemsToBeDeleted);  
                	  displayMessageToUser = Boolean.TRUE;
                }
			}
		}
		if (campaign.getCampaignCoverage().getItems().isEmpty()) {
			addActionError("error.campaign.coverageItemsEmpty");
		}
		this.removeItemsFromCampaign = true;
	}

	// This will return us the inventory items which has pending notifications and claim exists for the same
	private List<String> listInventoriesOfCampaignNotExists(List<InventoryItem> listOfItemsToBeDeleted) {

		List<String> uploadedInventories = new ArrayList<String>(5);
		if (CollectionUtils.isNotEmpty(listOfItemsToBeDeleted))
		{
			for (Iterator<InventoryItem> iterator = listOfItemsToBeDeleted.iterator(); iterator.hasNext();) {
				uploadedInventories.add(((InventoryItem) iterator.next()).getSerialNumber());
			}
		}
		if (CollectionUtils.isEmpty(uploadedInventories))
			return null;
		return campaignAssignmentService.findInventoriesForCampaignWithClaim(campaign, uploadedInventories);

	}

    /**
     * Method checks for Scrapped inventories.
     * If present shows validation error
     *
     * @param inventoryItemList
     * @return
     */
    private boolean validateSerialNumbersForScrap(List<InventoryItem> inventoryItemList) {
        StringBuffer scrapInventories = new StringBuffer();

        //Adding into a new list, since we need to show all scrap inventories during validation.
        for (InventoryItem inventoryItem : inventoryItemList) {
            if (SCRAP.equalsIgnoreCase(inventoryItem.getConditionType().getItemCondition())) {
                scrapInventories.append(inventoryItem.getSerialNumber());
                scrapInventories.append(", ");
            }
        }

        int serialNumbersLength = scrapInventories.length();
        if (serialNumbersLength > 0) {
            scrapInventories.delete(serialNumbersLength - 2, serialNumbersLength);
            addActionError("message.scrap.machineScrapped", scrapInventories);
            return true;
        }

        return false;
    }
    private boolean validateSerialNumbersForStolen(List<InventoryItem> inventoryItemList) {
        StringBuffer stolenInventories = new StringBuffer();

        //Adding into a new list, since we need to show all scrap inventories during validation.
        for (InventoryItem inventoryItem : inventoryItemList) {
            if (STOLEN.equalsIgnoreCase(inventoryItem.getConditionType().getItemCondition())) {
            	stolenInventories.append(inventoryItem.getSerialNumber());
            	stolenInventories.append(", ");
            }
        }

        int serialNumbersLength = stolenInventories.length();
        if (serialNumbersLength > 0) {
        	stolenInventories.delete(serialNumbersLength - 2, serialNumbersLength);
            addActionError("message.stole.machineStolen", stolenInventories);
            return true;
        }

        return false;
    }

    private boolean isFirstTimeUploadNull(List<InventoryItem> list) {
		return list.isEmpty() && campaign.getId() == null;
	}

	public String listCampaignItems() {
//		try{
//			campaignAssignmentService.generateNotificationForCampaignItems();
//		}
//		catch(Exception e)
//		{
//			logger.error(e.toString());
//		}
		return SUCCESS;
	}

	// TODO: Temp API to assign campaigns synchronously.
	public String assignNotifications() throws Exception {
		if(!StringUtils.hasText(campaign.getComments()))
		{
			addActionError("error.campaign.Comments");	
		}
		
		validateNonOemPartsDescription();
		validateHussPartsToReplace();
		validateReplacedInstalledPartsSection();
		validateCampaignJobCodesSection();
		validateMiscellaneousParts();
		if (hasActionErrors()) {
			return INPUT;
		}

        if (campaign != null && CampaignAdminService.CAMPAIGN_DRAFT_STATUS.equals(campaign.getStatus())) {
            campaign.setStatus(CampaignAdminService.CAMPAIGN_ACTIVE_STATUS);
            if (campaign.getD() != null) {
                campaign.getD().setActive(Boolean.TRUE);
            }
        }
		campaignAssignmentService.generateCampaignNotificationForCampaignItems(
		campaign.getId(), forAllCampaignItems);
		campaign.setNotificationsGenerated(Boolean.TRUE);
		campaignAdminService.deactivateNotificationBasedOnRelatedCampaign(campaign);
		papulateNationalAccounts();
		campaignAdminService.addActionHistoryAndUpdateCampaign(campaign);
		return SUCCESS;
	}
	
	private void validateHussPartsToReplace() {
		if (campaign.getHussPartsToReplace() != null && !campaign.getHussPartsToReplace().isEmpty()) {
			
			for (HussPartsToReplace hussPartsToReplace: campaign.getHussPartsToReplace()) {

				List<OEMPartToReplace> removedParts = hussPartsToReplace.getRemovedParts();
				List<OEMPartToReplace> installedParts = hussPartsToReplace.getInstalledParts();
				
				validateParts(removedParts, 1);				
				validateParts(installedParts, 2);								
			}			
		}
	}
	
	private void validateParts(List<OEMPartToReplace> parts, int type) {
		
		if (parts != null && !parts.isEmpty()) {
			// checking duplicates replaced/installed parts
			Set<String> partNumbersSet = new HashSet<String>(parts.size());
			for (OEMPartToReplace oemPartToReplace:parts) {	
				if (!partNumbersSet.add(oemPartToReplace.getItem().getNumber())) {
					addActionError("error.campaign.duplicateReplacedInstalledPart", new String[]{oemPartToReplace.getItem().getNumber()});
				}
			}
			
			for (OEMPartToReplace oemPartToReplace:parts) {				
				validatePartUnits(oemPartToReplace, type);
				if (type == 1) {
					if (((oemPartToReplace.getPaymentCondition() != null && oemPartToReplace.getPaymentCondition().trim().length() > 0)
							|| (oemPartToReplace.getDueDays() != null && oemPartToReplace.getDueDays().intValue() > 0))
			            	&& (oemPartToReplace.getReturnLocation() == null || oemPartToReplace.getReturnLocation().getCode() == null 
			            	&& oemPartToReplace.getReturnLocation().getCode().trim().length() == 0)) {
						addActionError("error.return.location.required");
					}
					if (((oemPartToReplace.getReturnLocation() != null && oemPartToReplace.getReturnLocation().getCode() != null 
							&& oemPartToReplace.getReturnLocation().getCode().trim().length() > 0)
							|| (oemPartToReplace.getDueDays() != null && oemPartToReplace.getDueDays().intValue() > 0))            										
							&& (oemPartToReplace.getPaymentCondition() == null || oemPartToReplace.getPaymentCondition().trim().length() == 0)) {
						addActionError("error.campaign.invalidPaymentCondition");
					}
					if (((oemPartToReplace.getReturnLocation() != null && oemPartToReplace.getReturnLocation().getCode() != null 
							&& oemPartToReplace.getReturnLocation().getCode().trim().length() > 0)
							|| (oemPartToReplace.getPaymentCondition() != null && oemPartToReplace.getPaymentCondition().trim().length() > 0))
			            	&& (oemPartToReplace.getDueDays() == null || oemPartToReplace.getDueDays().intValue() == 0)) {
						addActionError("error.dueDays.required");
					}						
				}
				if (hasActionErrors()) {
					break;
				}
			}			
		}
	}

	private void validatePartUnits(OEMPartToReplace oemPartToReplace, int type) {
		if (!(oemPartToReplace.getNoOfUnits() != null && oemPartToReplace.getNoOfUnits().intValue() > 0)) {
			String partType = null;
			if (type == 1) {
				partType = getText("label.claim.removedParts");
			}
			if (type == 2) {
				partType = getText("label.newClaim.hussmanPartsInstalled");
			}					
			addActionError("error.newClaim.invalidOEMPartUnitsWithParams", new String[]{partType});
		}
	}

	private void validateReplacedInstalledPartsSection() {
		if (campaign.getHussPartsToReplace() != null && !campaign.getHussPartsToReplace().isEmpty()) {
			
			for (HussPartsToReplace hussPartsToReplace: campaign.getHussPartsToReplace()) {

				List<OEMPartToReplace> removedParts = hussPartsToReplace.getRemovedParts();
				if (removedParts != null && !removedParts.isEmpty()) {
					if ((hussPartsToReplace.getInstalledParts() == null || hussPartsToReplace.getInstalledParts().isEmpty())) {
						if (buPartReplaceableByNonBUPart) {
							if (hussPartsToReplace.getNonOEMpartsToReplace() == null || hussPartsToReplace.getNonOEMpartsToReplace().isEmpty()) {
								addActionError("error.claim.selectAtleastOneOfInstallParts");
							}
						} else {
							addActionError("error.claim.selectAtleastOneOfTSAInstallParts");
						}
					}
				}
				if ((removedParts == null || removedParts.isEmpty()) && ((hussPartsToReplace.getInstalledParts() != null && !hussPartsToReplace.getInstalledParts().isEmpty()) || (hussPartsToReplace.getNonOEMpartsToReplace() != null || !hussPartsToReplace.getNonOEMpartsToReplace().isEmpty()))) {
					addActionError("error.claim.selectRemovedParts");
				}
			}			
		}
	}
	
	private void validateCampaignJobCodesSection() {
		List<CampaignLaborDetail> campaignLaborDetailLimits = campaign.getCampaignServiceDetail().getCampaignLaborLimits();
		
		if (campaignLaborDetailLimits != null && !campaignLaborDetailLimits.isEmpty()) {
			
			for (CampaignLaborDetail campaignLaborDetail: campaignLaborDetailLimits) {
				if (!campaignLaborDetail.isLaborStandardsUsed()) {
					if (campaignLaborDetail.getSpecifiedLaborHours() == null) {
						addActionError("error.campaign.invalidJobCodeDetails", new String[] {campaignLaborDetail.getServiceProcedureDefinition().getCode()});
					}
					if (campaignLaborDetail.getSpecifiedLaborHours() != null && campaignLaborDetail.getSpecifiedLaborHours().floatValue() <= 0.0) {
						addActionError("error.campaign.invalidJobCodeHours", new String[] {campaignLaborDetail.getServiceProcedureDefinition().getCode()});
					}
				}						
			}			
		}		
	}
	public String removeItemsFromCampaign() throws Exception {
		if(!StringUtils.hasText(campaign.getComments()))
		{
			addActionError("error.campaign.Comments");	
		}
		if (hasActionErrors()) {
			return INPUT;
		}
		papulateNationalAccounts();
		 campaignAdminService.addActionHistory(campaign);  
		
		// QC-567: Claim AIR-1030980 is processed for SN 7062860 and campaign was completed but when I removed a different serial number the status of the campaign done has been modified in pending
		// Fix: We should not set the Notifications Generated as false; So commenting out the line which causes the issue
        //campaign.setNotificationsGenerated(false);
		campaignAdminService.update(campaign);
		campaignAssignmentService.removeItemsFromCampaign(campaign.getId());
		addActionMessage("message.campaign.removedItemsSucessfully");
		return SUCCESS;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ItemGroup> getProducts() {
		return products;
	}

	public void setProducts(List<ItemGroup> products) {
		this.products = products;
	}

	public List<ListOfValues> getCampaignClasses() {
		return campaignClasses;
	}

	public boolean hasWarnings() {
		UploadSummary uploadSummary = getSummary();
		return uploadSummary.getTotalCount() != uploadSummary.getValidCount();
	}

	// Spring Injection
	private ItemGroupService itemGroupService;

	private CampaignAdminService campaignAdminService;

	private CampaignValidationService campaignValidationService;

	private CurrencyExchangeRateRepository currencyExchangeRateRepository;

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	public void setCampaignAdminService(
			CampaignAdminService campaignAdminService) {
		this.campaignAdminService = campaignAdminService;
	}
	
	public void setCurrencyExchangeRateRepository(
			CurrencyExchangeRateRepository currencyExchangeRateRepository) {
		this.currencyExchangeRateRepository = currencyExchangeRateRepository;
	}

	public void setCampaignValidationService(
			CampaignValidationService campaignValidationService) {
		this.campaignValidationService = campaignValidationService;
	}

	public String getCampaignFor() {
		return campaignFor;
	}

	public void setCampaignFor(String campaignFor) {
		this.campaignFor = campaignFor;
	}


	private void populateCampaignFor() {
		CampaignCoverage campaignCoverage = campaign.getCampaignCoverage();
		if (campaignCoverage != null) {
			if (campaignCoverage.getSerialNumberCoverage() != null) {
				campaignFor = SERIAL_NUMBERS;
			}else if (campaignCoverage.getRangeCoverage() != null) {
				campaignFor = SERIAL_NUMBER_RANGES;
			} 
		}
		getSelectedNationalAccounts().clear();
		getSelectedNationalAccounts().addAll(campaign.getApplicableNationalAccounts());
		
	}

	@Required
	public void setMergedTreeJsonifier(MergedTreeJSONifier jsonifier) {
		mergedTreeJSONifier = jsonifier;
	}

	private String getSelectedJobsJSON() {
		JSONArray selectedJobs = new JSONArray();
		CampaignServiceDetail serviceDetail = campaign
				.getCampaignServiceDetail();
		if (serviceDetail == null) {
			return "[]";
		}
		List<CampaignLaborDetail> labourDetail = serviceDetail
				.getCampaignLaborLimits();
		for (CampaignLaborDetail detail : labourDetail) {
			Map<String, Object> row = new HashMap<String, Object>();
			ServiceProcedureDefinition serviceProcedureDefinition = detail
					.getServiceProcedureDefinition();
			assert serviceProcedureDefinition != null;
			row.put(CODE, serviceProcedureDefinition.getActionDefinition()
					.getCode());
			row.put(COMPLETE_CODE, serviceProcedureDefinition.getCode());
			row.put(ID, serviceProcedureDefinition.getId());
			row.put(SERVICE_PROCEDURE_ID, serviceProcedureDefinition.getId());
			row.put(LABEL, serviceProcedureDefinition.getActionDefinition()
					.getName());
			row.put(SPECIFIED_HOURS, detail.getSpecifiedLaborHours());
			row.put(NODE_TYPE, NODE_TYPE_LEAF);
			row.put(WRAPPER_ID, detail.getId());
			row.put(USE_SUGGESTED_HOURS, detail.isLaborStandardsUsed());
			selectedJobs.put(row);
		}
		return selectedJobs.toString();
	}

	public String getSelectedJobsJsonString() {
		return getSelectedJobsJSON();
	}

	public String getJsonServiceProcedureTree() throws JSONException {
		FailureStructure failureStructure = getFailureStructure();
		if (failureStructure == null) {
			return "{}";
		}
		return mergedTreeJSONifier.getSerializedJSONStringForCampaign(failureStructure,
				new ClaimsAction.ServiceProcedureTreeFilter(), null);
	}
    
    public String getServiceProcedureTreeJSON() throws JSONException{
        return writeJsonResponse(getJsonServiceProcedureTree());
    }
	
	private void removeInValidServiceProcedures(FailureStructure failureStructure) {
		if (failureStructure != null) {
			Set<ServiceProcedureDefinition> allValidServiceProcedures = failureStructure.getAllServiceProcedureDefinitions();
			List<CampaignLaborDetail> laborDetailsToBeRemoved = new ArrayList<CampaignLaborDetail>();
			for (CampaignLaborDetail campaignLaborDetail : this.campaign.getCampaignServiceDetail().getCampaignLaborLimits()) {
				if (!allValidServiceProcedures.contains(campaignLaborDetail.getServiceProcedureDefinition())) {
					laborDetailsToBeRemoved.add(campaignLaborDetail);
					// fix for NMHGSLMS-1248
					if(null != campaignLaborDetail.getServiceProcedureDefinition()){
						addActionWarning("message.campaign.inValidJobCodeRemoved", campaignLaborDetail.getServiceProcedureDefinition().getCode());
					}					
				}
			}
			if(!laborDetailsToBeRemoved.isEmpty()){
				this.campaign.getCampaignServiceDetail().getCampaignLaborLimits().removeAll(laborDetailsToBeRemoved);
			}
		} else {
			this.campaign.getCampaignServiceDetail().getCampaignLaborLimits().clear();
		}
	}

	private FailureStructure getFailureStructure() {
        Collection<Item> items = populateItems();
        if(items.isEmpty())
            return null;
		return failureStructureService
				.getMergedFailureStructureForItems(items);
	}

	private Collection<Item> populateItems() {
		Set<Item> items = new HashSet<Item>();
		// fix for NMHGSLMS-1248
		if(null != campaign.getCampaignCoverage()){
			List<InventoryItem> inventoryItems = campaign.getCampaignCoverage()
					.getItems();
			for (InventoryItem inventoryItem : inventoryItems) {
				items.add(inventoryItem.getOfType());
			}
		}		
		return items;
	}

	public String getNationalAccountNames() {
		try {
			List<ServiceProvider> serviceProviders = orgService
					.findNationalAccountsWhoseNameStartsWith(getSearchPrefix(), 0, 10);
			return generateAndWriteComboboxJson(serviceProviders, "id", "name");
		} catch (Exception e) {
			logger.error("Error while generating JSON", e);
			throw new RuntimeException("Error while generating JSON", e);
		}
	}
	public String getNationalAccountNumber() {
		try {
			List<ServiceProvider> serviceProviders = orgService
					.findNationalAccountsWhoseNumberStartsWith(getSearchPrefix(), 0, 10);
			return generateAndWriteComboboxJson(serviceProviders, "id","serviceProviderNumber");
		} catch (Exception e) {
			logger.error("Error while generating JSON", e);
			throw new RuntimeException("Error while generating JSON", e);
		}
	}

	@Required
	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public List<PaymentCondition> getPaymentConditions() {
		return this.partReturnService
		.findAllPaymentConditions();
	}

	public void setPaymentConditions(List<PaymentCondition> paymentConditions) {
		this.paymentConditions = paymentConditions;
	}

	public void setPartReturnService(PartReturnService partReturnService) {
		this.partReturnService = partReturnService;
	}

	public String listLocations() {
        try {
        	if(selectedBusinessUnit!=null)
        	{
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(selectedBusinessUnit);
        	}
        	List<Location> locations = warehouseService.findWarehouseLocationsStartingWith(getSearchPrefix());
            return generateAndWriteComboboxJson(locations,"id","code");
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }
    }

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public String getLocationPrefix() {
		return locationPrefix;
	}

	public void setLocationPrefix(String locationPrefix) {
		this.locationPrefix = locationPrefix;
	}

	public void setWarehouseService(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	public String savePartialCampaign() throws Exception {
		String actionStringToReturn = update();
		/*validateCampaignDescription();*/
		if (hasActionErrors()) {
			return INPUT;
		}
		if (SUCCESS.equals(actionStringToReturn)) {
			String campaignCode = campaign.getCode();
			addActionMessage("message.campaign.saveSuccess",
					new String[] { campaignCode });
		}
		return actionStringToReturn;
	}

	public String viewPatternExample() {
		return SUCCESS;
	}

	public boolean isForAllCampaignItems() {
		return forAllCampaignItems;
	}

	public void setForAllCampaignItems(boolean forAllCampaignItems) {
		this.forAllCampaignItems = forAllCampaignItems;
	}

	public String selectCriteriaForCampaignNotification() {
		return SUCCESS;
	}


	// FIXME: Any exception that occurs comes up as a java script
    // Hence returning "{}" for any exception that occurs.
    public String getJSONifiedAttachmentList() {
        try {
            List<Document> attachments = campaign.getAttachments();
            if (attachments == null || attachments.size() <= 0) {
                return "[]";
            }
            return getDocumentListJSON(attachments).toString();
        } catch (Exception e) {
            return "[]";
        }
    }

	public String getAttachOrDelete() {
		return attachOrDelete;
	}

	public void setAttachOrDelete(String attachOrDelete) {
		this.attachOrDelete = attachOrDelete;
	}

	public boolean isRemoveItemsFromCampaign() {
		return removeItemsFromCampaign;
	}

	public void setRemoveItemsFromCampaign(boolean removeItemsFromCampaign) {
		this.removeItemsFromCampaign = removeItemsFromCampaign;
	}

	public List<InventoryItem> getItemsList() {
		return itemsList;
	}

	public void setItemsList(List<InventoryItem> itemsList) {
		this.itemsList = itemsList;
	}

	public boolean isSerialNumberCoverage() {
		return campaign.getCampaignCoverage() != null
				&& campaign.getCampaignCoverage().getSerialNumberCoverage() != null;
	}

	public ProductLocaleService getProductLocaleService() {
		return productLocaleService;
	}

	public void setProductLocaleService(ProductLocaleService productLocaleService) {
		this.productLocaleService = productLocaleService;
	}

	public I18NCampaignText getI18NcampaignDescription() {
		return i18NcampaignDescription;
	}

	public void setI18NcampaignDescription(I18NCampaignText ncampaignDescription) {
		i18NcampaignDescription = ncampaignDescription;
	}

	public boolean isRedirectToPage1() {
		return redirectToPage1;
	}

	public void setRedirectToPage1(boolean redirectToPage1) {
		this.redirectToPage1 = redirectToPage1;
	}

	public int getDefaultLocaleIndex() {
		return defaultLocaleIndex;
	}

	public void setDefaultLocaleIndex(int defaultLocaleIndex) {
		this.defaultLocaleIndex = defaultLocaleIndex;
	}

	public String getDefaultDescription() {
		return defaultDescription;
	}

	public void setDefaultDescription(String defaultDescription) {
		this.defaultDescription = defaultDescription;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public List<ProductLocale> getLocales() {
		return locales;
	}

	public void setLocales(List<ProductLocale> locales) {
		this.locales = locales;
	}

	public List<CampaignNotification> getCampaignNotifications() {
		return campaignNotifications;
	}

	public void setCampaignNotifications(
			List<CampaignNotification> campaignNotifications) {
		this.campaignNotifications = campaignNotifications;
	}

	
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public boolean isPartsReplacedInstalledSectionVisible() {
		return partsReplacedInstalledSectionVisible;
	}

	public void setPartsReplacedInstalledSectionVisible(
			boolean partsReplacedInstalledSectionVisible) {
		this.partsReplacedInstalledSectionVisible = partsReplacedInstalledSectionVisible;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = orgService.listUniqueCurrencies();
    }

	public Map<String,CostCategory> getConfiguredCostCategories() {
		return configuredCostCategories;
	}

	public void setConfiguredCostCategories(Map<String,CostCategory> configuredCostCategories) {
	    List<Object> costCategoryObjects = configParamService
        .getListofObjects(ConfigName.CONFIGURED_COST_CATEGORIES.getName());
        for (Object object : costCategoryObjects) {
        	CostCategory costCategory = new HibernateCast<CostCategory>().cast(object);
        	this.configuredCostCategories.put(costCategory.getCode(),costCategory);
        }
	}
	
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public Contract getSelectedContract() {
		return selectedContract;
	}

	public void setSelectedContract(Contract selectedContract) {
		this.selectedContract = selectedContract;
	}

	
	public boolean isShowWarning() {
		return showWarning;
	}

	public void setShowWarning(boolean showWarning) {
		this.showWarning = showWarning;
	}

	public List<InventoryItem> getItemsNotInCampaign() {
		return itemsNotInCampaign;
	}

	public void setItemsNotInCampaign(List<InventoryItem> itemsNotInCampaign) {
		this.itemsNotInCampaign = itemsNotInCampaign;
	}

	public String getCampaignCurrentStatus(){
		if(campaign != null){
			if(campaign.getTillDate() != null && Clock.today().isAfter(campaign.getTillDate())){
				return CampaignAdminService.CAMPAIGN_INACTIVE_STATUS;
			}

			return campaign.getStatus();
		}
		return null;
	}
	
	
	public String getCampaignUpdatedStatus(InventoryItem item){
		List<CampaignNotification> campaignNotice = campaignAssignmentService
		.findCampaignStatus(item);
		return campaignNotice.get(0).getNotificationStatus();
	}
	
	private void papulateNationalAccounts()
	{
		if(selectedNationalAccounts!=null)
		{   campaign.getApplicableNationalAccounts().clear();
			campaign.getApplicableNationalAccounts().addAll(getSelectedNationalAccounts());
		}	
	}
	
	public boolean isBuPartReplaceableByNonBUPart() {
		return buPartReplaceableByNonBUPart;
	}

	public void setBuPartReplaceableByNonBUPart(boolean buPartReplaceableByNonBUPart) {
		this.buPartReplaceableByNonBUPart = buPartReplaceableByNonBUPart;
	}
	
	public int getSubRowIndex() {
		return subRowIndex;
	}	

	public void setSubRowIndex(int subRowIndex) {
		this.subRowIndex = subRowIndex;
	}

	public List<NationalAccount> getSelectedNationalAccounts() {
		return selectedNationalAccounts;
	}

	public void setSelectedNationalAccounts(List<NationalAccount> selectedNationalAccounts) {
		this.selectedNationalAccounts = selectedNationalAccounts;
	}
	
	public String getCampaignOemRemovedInstalledPartTemplate() {
		 return SUCCESS;
	}
	
	public String getOemInstalledPartTemplate() {
		 return SUCCESS;
	}
	
	public String getOemRemovedPartTemplate() {
		 return SUCCESS;
	}

	public Party getCustomerInformationToBeDisplayed() {
		return customerInformationToBeDisplayed;
	}

	public void setCustomerInformationToBeDisplayed(
			Party customerInformationToBeDisplayed) {
		this.customerInformationToBeDisplayed = customerInformationToBeDisplayed;
	}
	
	public Party getDealerInformationToBeDisplayed() {
		return dealerInformationToBeDisplayed;
	}

	public void setDealerInformationToBeDisplayed(
			Party installingDealerInformationToBeDisplayed) {
		this.dealerInformationToBeDisplayed = installingDealerInformationToBeDisplayed;
	}

	public List<Claim> getClaimsForCampaignNotifications() {
		return claimsForCampaignNotifications;
	}

	public void setClaimsForCampaignNotifications(
			List<Claim> claimsForCampaignNotifications) {
		this.claimsForCampaignNotifications = claimsForCampaignNotifications;
	}
	
	Object convertFromNaturalToBaseCurrency(Claim claim) {
		if ((claim.getActiveClaimAudit().getPayment() != null && claim
				.getActiveClaimAudit().getState()
				.equals(ClaimState.ACCEPTED_AND_CLOSED))) {
			calculateAmountPaid(claim,claim.getActiveClaimAudit().getPayment(),claim.getCreditDate());
		    this.campaignStatistics.totalClaimsAccepted++;
			}
		else {
				Collections.reverse(claim.getClaimAudits());
				for(ClaimAudit claimHistory: claim.getClaimAudits()){
					if((claimHistory.getPreviousState().equals(ClaimState.ACCEPTED_AND_CLOSED))){ //previous state contains the actual state and state contains the previous state
							calculateAmountPaid(claim,claimHistory.getPayment(),claimHistory.getPayment().getD().getUpdatedOn()); // credit date will be null in audit history, so picking up payment date
							break;
				}
			}
		}
			return null;
	}

	private Object calculateAmountPaid(Claim claim, Payment payment,CalendarDate creditDate) {
		try{
            Money amountPaid;
            if(claim.getActiveClaimAudit() != null && claim.getActiveClaimAudit().getPayment() != null) {
		       amountPaid = claim.getActiveClaimAudit().getPayment().getTotalAmount();     //we should only fetch the latest payment made
            }  else{
                amountPaid = payment.getTotalAmount();
            }
		CalendarDate dateToBeUsed = creditDate;
		if (amountPaid != null) {
			Currency naturalCurrency = amountPaid
					.breachEncapsulationOfCurrency();
			Currency baseCurrency = Currency.getInstance("USD");
			if(dateToBeUsed==null){
				return Money.valueOf(0.0, baseCurrency);
			}
			CurrencyConversionFactor conversionFactor = this.currencyExchangeRateRepository
					.findConversionFactor(naturalCurrency,
							baseCurrency, dateToBeUsed);
			if (conversionFactor == null) {
				return Money.valueOf(0.0, baseCurrency);
			}
			Money convertedValue = conversionFactor.convert(amountPaid,
					dateToBeUsed);
			this.campaignStatistics.amountPaidForCompletedItems = this.campaignStatistics.amountPaidForCompletedItems
					.plus(convertedValue);
			}
	}
	catch (CurrencyConversionException ex) {
		throw new RuntimeException(
				"Failed to convert currencies for claim [" + claim + "]",
				ex);
		}
		return null;
	}
}
