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
package tavant.twms.web.admin.campaign;

import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.CODE;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.COMPLETE_CODE;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.ID;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.LABEL;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.NODE_TYPE;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.NODE_TYPE_LEAF;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.SERVICE_PROCEDURE_ID;
import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.Document;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignAdminService;
import tavant.twms.domain.campaign.CampaignCoverage;
import tavant.twms.domain.campaign.CampaignLaborDetail;
import tavant.twms.domain.campaign.CampaignRangeCoverage;
import tavant.twms.domain.campaign.CampaignSerialNumberCoverage;
import tavant.twms.domain.campaign.CampaignServiceDetail;
import tavant.twms.domain.campaign.CampaignServiceException;
import tavant.twms.domain.campaign.HussPartsToReplace;
import tavant.twms.domain.campaign.NonOEMPartToReplace;
import tavant.twms.domain.campaign.OEMPartToReplace;
import tavant.twms.domain.campaign.validation.CampaignValidationService;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.MiscellaneousItem;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.common.I18NNonOemPartsDescription;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.ServiceProcedureDefinition;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.NationalAccount;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PaymentCondition;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.admin.jobcode.MergedTreeJSONifier;
import tavant.twms.web.claim.ClaimsAction;
import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

/**
 * @author Kiran.Kollipara
 */
@SuppressWarnings("serial")
public class SaveCampaignAction extends I18nActionSupport implements
		Preparable, Validateable {

	private static final Logger logger = Logger
			.getLogger(SaveCampaignAction.class);

	private CampaignAdminService campaignAdminService;

	private CampaignValidationService campaignValidationService;

	private CatalogService catalogService;

	private Campaign campaign;

	private String number;

	private String partCriterion;

	private String jsonString;
	
	private boolean partsReplacedInstalledSectionVisible;
	
	private boolean buPartReplaceableByNonBUPart;
	
	private String campaignFor;
	
	private static final String SERIAL_NUMBERS = "SERIAL_NUMBERS";
	
	private static final String SERIAL_NUMBER_RANGES = "SERIAL_NUMBER_RANGES";

	private FailureStructureService failureStructureService;

	private MergedTreeJSONifier mergedTreeJSONifier;

	private List<PaymentCondition> paymentConditions;

	private PartReturnService partReturnService;

    public static final String SPECIFIED_HOURS = "specifiedLaborHours",
			WRAPPER_ID = "wrapperId",
			USE_SUGGESTED_HOURS = "useSuggestedHours";

	private String locationPrefix;

	private List<ItemGroup> products = new ArrayList<ItemGroup>();

	private ItemGroupService itemGroupService;

	private List<Currency> currencies;
	private ConfigParamService configParamService;
	private Map<String,CostCategory> configuredCostCategories = new HashMap<String,CostCategory>();
	private Contract selectedContract;
	private ContractService contractService;
	
	private List<NationalAccount> selectedNationalAccounts=new ArrayList<NationalAccount>();

    public void prepare() throws Exception {
        // this action is only used for populating the part description
        if("oem_part_details".equals(ActionContext.getContext().getName())) return;
		products = itemGroupService.findGroupsForGroupType(PRODUCT);
		paymentConditions = partReturnService.findAllPaymentConditions();
		this.setConfiguredCostCategories(configuredCostCategories);
		this.setCurrencies(currencies);
		populateCampaignFor();
		partsReplacedInstalledSectionVisible = getConfigParamService().
		getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE.getName());
		buPartReplaceableByNonBUPart = getConfigParamService().getBooleanValue(ConfigName.BUPART_REPLACEABLEBY_NONBUPART.getName());
		if (this.selectedContract != null && this.selectedContract.getId() != null && campaign != null) {
			campaign.setContract(this.contractService.findContract(this.selectedContract.getId()));
		}else if(this.selectedContract != null && this.selectedContract.getId() == null && campaign != null){
			campaign.setContract(null);
		}
    }

	@Override
	public void validate() {
		try {
			campaignValidationService.validate(campaign,campaignFor);
			List<InventoryItem> items = campaign.getCampaignCoverage()
			.getItems();
			if ((items == null || items.isEmpty())
					&& SERIAL_NUMBERS.equals(campaignFor)) {
				super.validate();
			}
		} catch (CampaignServiceException e) {
			Set<String> errors = new HashSet<String>();
			errors.addAll(e.fieldErrors().values());
			errors.addAll(e.actionErrors());

			for (String anErrorMessage : errors) {
				addActionError(anErrorMessage);
			}
		}
	}

	public String load() {
		if (campaign.getId() != null) {
			campaign = campaignAdminService.findById(campaign.getId());
		}
		return SUCCESS;
	}

	public String save() throws Exception {
		campaignAdminService.save(campaign);
		return SUCCESS;
	}
	// validate Non Oem Parts description for en_US Mandatory
	public void validateNonOemPartsDescription() {		
		if (campaign.getNonOEMpartsToReplace() != null) {
			
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
					if (i18NonOemPartsDescription != null
							&& EN_US.equalsIgnoreCase(i18NonOemPartsDescription.getLocale())
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


	public String update() throws Exception {
		
		if (!StringUtils.hasText(campaign.getStatus())) {
			campaign.setStatus(CampaignAdminService.CAMPAIGN_DRAFT_STATUS);
		}
		if(!StringUtils.hasText(campaign.getComments()))
		{
			addActionError("error.campaign.Comments");	
		}
		validateReplacedInstalledPartsSection();
		validateHussPartsToReplace();
		validateNonOemPartsDescription();		
		validateCampaignJobCodesSection();
		validateMiscellaneousParts();
		if(hasActionErrors()){			
            return INPUT;
		}
		papulateNationalAccounts();
		campaignAdminService.addActionHistory(campaign);
		campaignAdminService.update(campaign);
		String campaignCode = campaign.getCode().toString();
		addActionMessage("message.campaign.saveSuccess",
				new String[] { campaignCode });
		return SUCCESS;
	}
	
	public String updateForPrevious() throws Exception {
		campaignAdminService.update(campaign);
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
			
			// checking remaining validations
			for (OEMPartToReplace oemPartToReplace:parts) {	
				if (hasActionErrors()) {
					break;
				}
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
	public String deactivate() throws Exception {
		if(!StringUtils.hasText(campaign.getComments()))
		{
			addActionError("error.campaign.Comments");	
		}
		if(hasActionErrors()){			
            return INPUT;
		}
		campaign.setStatus(CampaignAdminService.CAMPAIGN_INACTIVE_STATUS);
		if (campaign.getD() != null) {
			campaign.getD().setActive(Boolean.FALSE);
		}
		papulateNationalAccounts();
		campaignAdminService.addActionHistory(campaign);
		campaignAdminService.deactivateCampaign(campaign);
		String campaignCode = campaign.getCode().toString();
		addActionMessage("message.campaign.deactivatedSuccess",
				new String[] { campaignCode });
		return SUCCESS;
	}

	public String activate() throws Exception {	
		if(!StringUtils.hasText(campaign.getComments()))
		{
			addActionError("error.campaign.Comments");	
		}
		validateReplacedInstalledPartsSection();
		validateHussPartsToReplace();
		validateNonOemPartsDescription();		
		validateCampaignJobCodesSection();		
		validateMiscellaneousParts();
		if(hasActionErrors()){			
            return INPUT;
		}
		campaign.setStatus(CampaignAdminService.CAMPAIGN_ACTIVE_STATUS);
		if (campaign.getD() != null) {
			campaign.getD().setActive(Boolean.TRUE);
		}
		papulateNationalAccounts();
		campaignAdminService.addActionHistory(campaign);
		campaignAdminService.activateCampaign(campaign);
		String campaignCode = campaign.getCode().toString();
		addActionMessage("message.campaign.activatedSuccess",
				new String[] { campaignCode });
		return SUCCESS;
	}

	
	public String delete() throws Exception {
		String code = campaign.getCode();
		campaignAdminService.delete(campaign);
		addActionMessage("message.campaign.deleteSuccess",
				new String[] { code });
		return SUCCESS;
	}

	public String getOEMPartDetails() {
		try {
			Item item = catalogService.findItemOwnedByManuf(number);
			JSONArray oneEntry = new JSONArray();
			oneEntry.put(item.getDescription());
			jsonString = oneEntry.toString();
		} catch (CatalogException e) {
			logger.error("Invalid item number entered", e);
		}
		return SUCCESS;
	}

	public String  listParts() {
		return getOemPartItemNumbersStartingWith(getSearchPrefix());

	}

	String getOemPartItemNumbersStartingWith(String prefix) {
        try {
            List<Item> items = new ArrayList<Item>();
            if (StringUtils.hasText(prefix)) {
            	String currentSelectedBusinessUnit = SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
            	//Commented for perf fix instead we will use the new Native SQL query
                //items = catalogService.findParts(prefix.toUpperCase());
            	
            	//since all types of parts should come we have a list of all the types created.
            	List<Object> itemGroupSet = new ArrayList<Object>(3);
            	itemGroupSet.add("PART");
            	itemGroupSet.add("KIT");
            	itemGroupSet.add("OPTION");
            	items = catalogService.fetchManufParts(currentSelectedBusinessUnit, prefix, 10, itemGroupSet, false);
            }
            return generateAndWriteComboboxJson(items,"id","number");
        } catch (Exception e) {
            logger.error("Error while generating JSON",e);
            throw new RuntimeException("Error while generating JSON", e);
        }

    }

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public String getJsonString() {
		return jsonString;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPartCriterion() {
		return partCriterion;
	}

	public void setPartCriterion(String partCriterion) {
		this.partCriterion = partCriterion;
	}

	public void setCampaignAdminService(
			CampaignAdminService campaignAdminService) {
		this.campaignAdminService = campaignAdminService;
	}

	public void setCampaignValidationService(
			CampaignValidationService campaignValidationService) {
		this.campaignValidationService = campaignValidationService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public void setMergedTreeJSONifier(MergedTreeJSONifier mergedTreeJSONifier) {
		this.mergedTreeJSONifier = mergedTreeJSONifier;
	}

	public List<PaymentCondition> getPaymentConditions() {
		return paymentConditions;
	}

	public void setPaymentConditions(List<PaymentCondition> paymentConditions) {
		this.paymentConditions = paymentConditions;
	}

	public void setPartReturnService(PartReturnService partReturnService) {
		this.partReturnService = partReturnService;
	}

	public String getLocationPrefix() {
		return locationPrefix;
	}

	public void setLocationPrefix(String locationPrefix) {
		this.locationPrefix = locationPrefix;
	}

	public List<ItemGroup> getProducts() {
		return products;
	}

	public void setProducts(List<ItemGroup> products) {
		this.products = products;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	@Required
	public void setMergedTreeJsonifier(MergedTreeJSONifier jsonifier) {
		mergedTreeJSONifier = jsonifier;
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
			if (campaignCoverage.getRangeCoverage() != null) {
				campaignFor = SERIAL_NUMBER_RANGES;
			} else if (campaignCoverage.getSerialNumberCoverage() != null) {
				campaignFor = SERIAL_NUMBERS;
			}
		}
		getSelectedNationalAccounts().clear();
		getSelectedNationalAccounts().addAll(campaign.getApplicableNationalAccounts());
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

	private FailureStructure getFailureStructure() {
		return failureStructureService
				.getMergedFailureStructureForItems(populateItems());
	}

	private Collection<Item> populateItems() {
		Set<Item> items = new HashSet<Item>();
		List<InventoryItem> inventoryItems = campaign.getCampaignCoverage()
				.getItems();
		for (InventoryItem inventoryItem : inventoryItems) {
			items.add(inventoryItem.getOfType());
		}
		return items;
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

    public String getDefaultLocale() {
		return EN_US;
	}

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = orgService.listUniqueCurrencies();
    }
	
    public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
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
	
	private void papulateNationalAccounts()
	{
		if(selectedNationalAccounts!=null)
		{   campaign.getApplicableNationalAccounts().clear();
			campaign.getApplicableNationalAccounts().addAll(getSelectedNationalAccounts());
		}	
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
	public boolean isPartsReplacedInstalledSectionVisible() {
		return partsReplacedInstalledSectionVisible;
	}

	public void setPartsReplacedInstalledSectionVisible(
			boolean partsReplacedInstalledSectionVisible) {
		this.partsReplacedInstalledSectionVisible = partsReplacedInstalledSectionVisible;
	}
	
	public boolean isBuPartReplaceableByNonBUPart() {
		return buPartReplaceableByNonBUPart;
	}

	public void setBuPartReplaceableByNonBUPart(boolean buPartReplaceableByNonBUPart) {
		this.buPartReplaceableByNonBUPart = buPartReplaceableByNonBUPart;
	}

	public List<NationalAccount> getSelectedNationalAccounts() {
		return selectedNationalAccounts;
	}

	public void setSelectedNationalAccounts(
			List<NationalAccount> selectedNationalAccounts) {
		this.selectedNationalAccounts = selectedNationalAccounts;
	}
	

}