package tavant.twms.web.warranty;

import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;
import static tavant.twms.web.documentOperations.DocumentAction.getUnitDocumentListJSON;
import tavant.twms.domain.orgmodel.Country;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.*;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.WarrantyTask.WarrantyTaskInstanceService;
import tavant.twms.domain.campaign.CampaignRangeCoverage;
import tavant.twms.domain.campaign.CampaignRepository;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.inventory.EngineTierCtryMapping;
import tavant.twms.domain.inventory.EngineTierCtryMappingService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemAdditionalComponents;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.TierTierMapping;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import common.Logger;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.util.StringUtils;
import tavant.twms.web.util.DocumentTransportUtils;
import tavant.twms.worklist.common.BUSetting;
import tavant.twms.worklist.common.BuSettingsService;

/**
 * Created by IntelliJ IDEA. User: pradyot.rout Date: Sep 1, 2008 Time: 12:18:13
 * AM To change this template use File | Settings | File Templates.
 */
public class ManageWarrantyAction extends BaseManageWarrantyAction implements
		Preparable, Validateable {

	private static final Logger logger = Logger
			.getLogger(ManageWarrantyAction.class);
	private Warranty warranty;
	private WarrantyAudit warrantyAudit;	
    private String warrantyComments;
    private EngineTierCtryMappingService engineTierCtryMappingService;
    private Long warrantyTaskInstanceId;
    private List<ListOfValues> additionalComponentTypes;
    private List<ListOfValues> additionalComponentSubTypes;
    private CustomerService customerService;
    private boolean forPrintPDI;

	private boolean pdiGeneration;
	private Customer operator;
	
    public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public Customer getOperator() {
		return operator;
	}

	public void setOperator(Customer operator) {
		this.operator = operator;
	}

	public boolean isPrintPdf(){
    	return this.buSettingsService.getBooleanSetting(BUSetting.PRINT_PDI,getCurrentBusinessUnit().getName());
    }   
    
    public boolean isForPrintPDI() {
		return forPrintPDI;
	}

	public void setForPrintPDI(boolean forPrintPDI) {
		this.forPrintPDI = forPrintPDI;
	}

	
	public boolean isPdiGeneration() {
		return pdiGeneration;
	}

	public void setPdiGeneration(boolean pdiGeneration) {
		this.pdiGeneration = pdiGeneration;
	}

	
	public List<ListOfValues> getAdditionalComponentSubTypes() {
		return additionalComponentSubTypes;
	}

	public void setAdditionalComponentSubTypes(
			List<ListOfValues> additionalComponentSubTypes) {
		this.additionalComponentSubTypes = additionalComponentSubTypes;
	}

	public List<ListOfValues> getAdditionalComponentTypes() {
		return additionalComponentTypes;
	}

	public void setAdditionalComponentTypes(List<ListOfValues> additionalComponentTypes) {
		this.additionalComponentTypes = additionalComponentTypes;
	}

    
	public Long getWarrantyTaskInstanceId() {
		return warrantyTaskInstanceId;
	}

	public void setWarrantyTaskInstanceId(Long warrantyTaskInstanceId) {
		this.warrantyTaskInstanceId = warrantyTaskInstanceId;
	}

	public void setEngineTierCtryMappingService(
			EngineTierCtryMappingService engineTierCtryMappingService) {
		this.engineTierCtryMappingService = engineTierCtryMappingService;
	}
	
	private ClaimService claimService;
	
	private CampaignRepository campaignRepository;


	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public CampaignRepository getCampaignRepository() {
		return campaignRepository;
	}

	public void setCampaignRepository(CampaignRepository campaignRepository) {
		this.campaignRepository = campaignRepository;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}
	
	private CatalogService catalogService;

	public void prepare() throws Exception {
		if (getId() != null) {
			setWarrantyTaskInstance(getWarrantyTaskInstanceService().findById(Long.parseLong(getId())));			
			warranty = getWarrantyTaskInstance().getWarrantyAudit().getForWarranty();
			if (InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, warranty.getInstallingDealer()))
				setInstallingDealer(new HibernateCast<ServiceProvider>().cast(warranty.getInstallingDealer()));
			setTransactionType(warranty.getTransactionType().getTrnxTypeKey());
			setMarketingInformation(warranty.getMarketingInformation());
			for (InventoryItem invItem : getWarrantyTaskInstance().getForItems()) {
				prepareInventoryItem(invItem);
				displayCountyCodeAndName(warranty);
			}			
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(warranty.getForItem().getBusinessUnitInfo().getName());
			setWarrantyTaskInstanceId(Long.parseLong(getId()));
			if(null!=warranty.getCustomer()){
				operator=customerService.findCustomerById(warranty.getCustomer().getId());
			}	
		}
		
		prepareCommonAttachments();
		if(warranty != null){
			prepareMarketingInformation(warranty.getForItem());
		}
		populateCustomerTypes();
		 additionalComponentTypes = this.lovRepository.findAllActive("AdditionalComponentType");
			additionalComponentSubTypes = this.lovRepository.findAllActive("AdditionalComponentSubType");
	}

    private void displayCountyCodeAndName(Warranty warranty) {
    	if (warranty!=null && warranty.getCustomer() != null
				&& warranty.getCustomer().getAddress() != null) {
			if (warranty.getCustomer().getAddress()
					.getCountyCodeWithName() != null) {
				warranty.getAddressForTransfer().setCountyCodeWithName(
						warranty.getCustomer().getAddress()
								.getCountyCodeWithName());
			} else if (warranty.getCustomer().getAddress().getState() != null
					&& warranty.getCustomer().getAddress().getCounty() != null) {
				String countyName = msaService
						.findCountyNameByStateAndCode(warranty.getCustomer().getAddress().getState(),
								warranty.getCustomer().getAddress()
										.getCounty());
				warranty.getAddressForTransfer().setCountyCodeWithName(
						warranty.getCustomer().getAddress().getCounty()
								+ "-" + countyName);
			}
		}
	}

	@Override
    public void validate(){
    	if(!WarrantyStatus.ACCEPTED.getStatus().equals(getStatus().getStatus())){
    		if(getWarrantyAudit()!= null && (getWarrantyAudit().getExternalComments() == null
    				|| "".equals(getWarrantyAudit().getExternalComments()))){
    			addActionError("error.warrantyCoverage.commentsMandatory");
    		}
    	}
    	if(getActionErrors() != null && !getActionErrors().isEmpty()){
    		if(WarrantyStatus.REPLIED.getStatus().equals(getStatus().getStatus())) {
    			prepareForwardedInventoryForError();
    		} else if(WarrantyStatus.RESUBMITTED.getStatus().equals(getStatus().getStatus())) {
    			prepareResubmittedInventoryForError();
    		} else if(WarrantyStatus.FORWARDED.getStatus().equals(getStatus().getStatus())
    				|| WarrantyStatus.REJECTED.getStatus().equals(getStatus().getStatus())) {
				preparePendingForApprovalError();
			} else {
				getInventoryItemMappings().clear();
				for (InventoryItem invItem : getWarrantyTaskInstance().getForItems()) {
					prepareInventoryItem(invItem);
				}
			}
    		warranty = getWarrantyTaskInstance().warrantyAudit.getForWarranty();
    	}

    }
    
    private void prepareResubmittedInventoryForError() {
    	for(MultipleInventoryAttributesMapper mapper : getInventoryItemMappings()) {
			mapper.getAttachments().removeAll(Collections.singleton(null));
    	}
    }
    
    private void prepareForwardedInventoryForError() {
    	for(MultipleInventoryAttributesMapper mapper : getInventoryItemMappings()) {
			mapper.getAttachments().removeAll(Collections.singleton(null));
			for (RegisteredPolicy registeredPolicy : mapper.getInventoryItem().getLatestWarranty().getPolicies()) {
				mapper.getSelectedPolicies().add(registeredPolicy);
			}
		}
    }
    
	private void preparePendingForApprovalError() {
		for (MultipleInventoryAttributesMapper inventoryItemMapping : getInventoryItemMappings()) {
			InventoryItem inventoryItem = inventoryItemMapping
					.getInventoryItem();
			inventoryItem.setDeliveryDate(inventoryItemMapping
					.getWarrantyDeliveryDate());
		}
	}

    public String preview() {
		warranty = getWarrantyTaskInstance().warrantyAudit.getForWarranty();
		if (isAdditionalInformationDetailsApplicable() || warranty.getMarketingInformation()!=null) {
			setMarketingInformation(warranty.getMarketingInformation());
		}
		return SUCCESS;
	}

	public String detail() {		
		if(!getWarrantyTaskInstance().getActive()){
			addActionError("error.pendingWarranty.alreadyActedOn");
   		 	return ERROR;
		}
		if(folderName!= null && folderName.equalsIgnoreCase("Rejected") && warranty != null && warranty.getInvalidItdrAttachment() != null && warranty.getInvalidItdrAttachment()){
        	addActionWarning("error.rejectedWarranty.invalidItdr");
        }
		warranty = getWarrantyTaskInstance().warrantyAudit.getForWarranty();
		if (isAdditionalInformationDetailsApplicable() || warranty.getMarketingInformation()!=null) {
			setMarketingInformation(warranty.getMarketingInformation());
		}		
		// retrieving customer object for displaying customer details
		if(null != warranty && null != warranty.getCustomer()){
			operator=customerService.findCustomerById(warranty.getCustomer().getId());
		}
		return SUCCESS;
	}
	
	private boolean isWarrantyTaskActive(){
		if(warrantyTaskInstanceId != null && getWarrantyTaskInstanceService().findById(warrantyTaskInstanceId).getActive()){
			return true;
		}
		return false;
	}
	
	private boolean validateAttachmentsDuringResubmission(MultipleInventoryAttributesMapper inventoryItemMapping){
		
		Warranty warranty = inventoryItemMapping.getInventoryItem().getLatestWarranty();
		warranty.getAttachments().removeAll(Collections.singleton(null));
		inventoryItemMapping.getAttachments().removeAll(Collections.singleton(null));
	
		Date currentAttachmentsMaxDate=null;
		Date attachmentsBeforeResubmissionMaxDate=null;
		if (inventoryItemMapping.getInventoryItem().getBusinessUnitInfo().getName().equals(AdminConstants.NMHGAMER)) {
			currentAttachmentsMaxDate=getMaxDateFromListOfAttachments(inventoryItemMapping.getAttachments(), AdminConstants.PDI);
			attachmentsBeforeResubmissionMaxDate=getMaxDateFromListOfAttachments(warranty.getAttachments(), AdminConstants.PDI);
		
		} else if (inventoryItemMapping.getInventoryItem().getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			currentAttachmentsMaxDate=getMaxDateFromListOfAttachments(inventoryItemMapping.getAttachments(), AdminConstants.ITDR);
			attachmentsBeforeResubmissionMaxDate=getMaxDateFromListOfAttachments(warranty.getAttachments(), AdminConstants.ITDR);
		}
		if ( currentAttachmentsMaxDate==null || (attachmentsBeforeResubmissionMaxDate!=null && currentAttachmentsMaxDate!=null  && !currentAttachmentsMaxDate.after(attachmentsBeforeResubmissionMaxDate)))
			return false;
		return true;
	}
	
	private Date getMaxDateFromListOfAttachments(List<Document> documents,String type)
	{
		Date maxDate=null;
		for(Document document :documents)
		{
			if(document.getUnitDocumentType()!=null && document.getUnitDocumentType().getDescription().equalsIgnoreCase(type))
			{
				if(maxDate==null || document.getD().getCreatedTime().after( maxDate) )
				{
					maxDate=document.getD().getCreatedTime();	
				}
			} 
		}
		
		return maxDate;	
	}
	
	private boolean validateInventoryItemMappingForAttachments(){
		for(MultipleInventoryAttributesMapper inventoryItemMapping : getInventoryItemMappings()){
			if (!validateAttachmentsDuringResubmission(inventoryItemMapping)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isDemoTruckMoreThan80Hours(InventoryItem inventoryItem) {
		if (isAdditionalInformationDetailsApplicable()
				&& getMarketingInformation().getContractCode() != null
				&& getMarketingInformation().getContractCode().getContractCode().equalsIgnoreCase(AdminConstants.DEMO)
				&& inventoryItem.getHoursOnMachine() > 80){
			return true;
		}
		return false;
	}
	
	private boolean isDocumentAttachedOfType(
			List<Document> attachments, String documentType) {
		boolean documentAttached = false;
		for (Document attachment : attachments) {
			if (attachment.getUnitDocumentType() != null
					&& attachment.getUnitDocumentType().getDescription().equalsIgnoreCase(documentType)) {
				documentAttached = true;
				break;
			}
		}
		return documentAttached;
	}
	
	private void validateAttachments(MultipleInventoryAttributesMapper inventoryItemMapping) {
		inventoryItemMapping.getAttachments().removeAll(Collections.singleton(null));
		InventoryItem inventoryItem = inventoryItemMapping.getInventoryItem();
		String businessUnit = inventoryItem.getBusinessUnitInfo().getName();
		List<Document> attachments = inventoryItemMapping.getAttachments();
		boolean documentsAttached = true;
		boolean drTransaction = getTransactionType().equals(InventoryTransaction.DR);
		boolean etrTransaction = getTransactionType().equals(AdminConstants.ETR);
		if(attachments == null || attachments.isEmpty()) {
			documentsAttached = false;
		}
		if(businessUnit.equals(AdminConstants.NMHGAMER)) {
			if(!documentsAttached || !isDocumentAttachedOfType(attachments, AdminConstants.PDI)) {
				addActionError("error.amerDeliveryReport.pdi.mandatory", inventoryItem.getSerialNumber());
			}
			if(!documentsAttached || !isDocumentAttachedOfType(attachments, AdminConstants.AUTHORIZATION)) {
				if(etrTransaction || (drTransaction && isDemoTruckMoreThan80Hours(inventoryItem))) {
					addActionError("error.amerDeliveryReport.authorization.mandatory", inventoryItem.getSerialNumber());
				}
			}
		} else if(businessUnit.equals(AdminConstants.NMHGEMEA)) {
			if(!documentsAttached || !isDocumentAttachedOfType(attachments, AdminConstants.ITDR)) {
				addActionError("error.emeaDeliveryReport.itdr.mandatory", inventoryItem.getSerialNumber());
			}
		}
	}
	
	private void validateInventoryItemMappingAttachments() {
		for(MultipleInventoryAttributesMapper inventoryItemMapping : getInventoryItemMappings()) {
    		validateAttachments(inventoryItemMapping);
    	}
	}
	
	private void validateDocumentType() {
		for(MultipleInventoryAttributesMapper mapper : getInventoryItemMappings()) {
			List<Document> attachments = mapper.getAttachments();
			attachments.removeAll(Collections.singleton(null));
			for (Document attachment : attachments){
				if(attachment.getUnitDocumentType() == null){
					addActionError("error.document.documentTypeMandatory", attachment.getFileName());
				}
			}
		}
	}
	
	private void validateDeliveryDate() {
		for(MultipleInventoryAttributesMapper mapper : getInventoryItemMappings()) {
			InventoryItem inventoryItem = mapper.getInventoryItem();
			String serialNumber = inventoryItem.getSerialNumber();
			CalendarDate deliveryDate = mapper.getWarrantyDeliveryDate();
			if(deliveryDate.isAfter(Clock.today())) {
				addActionError("error.deliveryDateCannotBeInFuture", serialNumber);
			}
			if(getTransactionType().equalsIgnoreCase(InventoryTransaction.DR)) {
				if(deliveryDate.isBefore(inventoryItem.getShipmentDate())) {
					addActionError("error.deliveryDateBeforeShipment", serialNumber);
				}
			} else if(getTransactionType().equalsIgnoreCase(AdminConstants.ETR)) {
				if (deliveryDate.isBefore(inventoryItem.getWarranty().getDeliveryDate())) {
					addActionError("error.transferDateBeforeDeliveryDate", serialNumber);
				}
			}
		}
	}
	
	public String processWarrantyTransitionAdmin() {
		if(!isWarrantyTaskActive()){
   		 addActionError("error.pendingWarranty.alreadyActedOn");
   		 return ERROR;
   	 	}
		if(!(WarrantyStatus.REJECTED.getStatus().equals(getStatus().getStatus()) || WarrantyStatus.RESUBMITTED.getStatus().equals(getStatus().getStatus()))){
			warranty.setInvalidItdrAttachment(Boolean.FALSE);
		}
        if (!WarrantyStatus.DELETED.getStatus().equals(getStatus().getStatus())) {
            if (isAdditionalInformationDetailsApplicable() || getMarketingInformation() != null){
            	if(isCustomerDetailsNeededForDR_Rental()?true:!warranty.getCustomerType().equalsIgnoreCase(AdminConstants.DEALER_RENTAL)){
            		if(!warranty.getCustomerType().equalsIgnoreCase(AdminConstants.CUSTOMER_TYPE_DEMO)) {          	//SLMSPROD-1174, demo should work in the same way as that of End customer
                validateMarketingInfo();
                if(customer!=null && !warranty.getCustomer().equals(customer)){
                	validateCustomerForDisclaimer();
                		}
            		}
            	}
            }
            validateInventoryItemMappingForPolicy();
            validateAdditionalComponents(warranty.getForItem());
            validateDocumentType();
            validateDeliveryDate();
        }
        if(WarrantyStatus.RESUBMITTED.getStatus().equals(getStatus().getStatus()) 
        		&& warranty.getInvalidItdrAttachment()!= null 
        		&& warranty.getInvalidItdrAttachment() && !validateInventoryItemMappingForAttachments()){
        	addActionError("error.rejectedWarranty.invalidItdr");
   		 return INPUT;
        }
        if(WarrantyStatus.RESUBMITTED.getStatus().equals(getStatus().getStatus())) {
        	validateInventoryItemMappingAttachments();
        }
        if(hasErrors()){
            return INPUT;
        }
        List<InventoryItem> itemsForTask = new ArrayList<InventoryItem>();
    	List<InventoryItem> inventories = new ArrayList<InventoryItem>();
    	List<InventoryItem> inventoriesToBeDeleted = new ArrayList<InventoryItem>();
        for (MultipleInventoryAttributesMapper inventoryItemMapping : getInventoryItemMappings()) {
        	
        	 InventoryItem inventoryItem = inventoryItemMapping.getInventoryItem();
        	 if (WarrantyStatus.ACCEPTED.getStatus().equals(getStatus().getStatus()))
			 {
        		if(inventoryItem.getWarranty().getOem()!=null && inventoryItemMapping.getOem() != null && inventoryItemMapping.getOem().getCode()!=null)
				inventoryItem.setOem(inventoryItem.getWarranty().getOem());
				inventoryItem.setVinNumber(inventoryItemMapping.getEquipmentVIN());
				inventoryItem.setOperator(inventoryItem.getWarranty().getOperator());
				inventoryItem.setFleetNumber(inventoryItemMapping.getFleetNumber());
				inventoryItem.setInstallationDate(inventoryItemMapping.getInstallationDate());
				/*for(RegisteredPolicy registeredPolicy : inventoryItem.getWarranty().getPolicies()){
					registeredPolicy.setWarrantyPeriod(registeredPolicy.getPolicyDefinition().warrantyPeriodFor(inventoryItem));
					registeredPolicy.getLatestPolicyAudit().setWarrantyPeriod(registeredPolicy.getWarrantyPeriod());
					registeredPolicy.getLatestPolicyAudit().setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
				}*/
				if(!warranty.getTransactionType().getTrnxTypeKey().equalsIgnoreCase("ETR"))
					warrantyService.updateInventoryForWarrantyDates(inventoryItem);
				 
			 }
        	 if (WarrantyStatus.DELETED.getStatus().equals(getStatus().getStatus()) || WarrantyStatus.REJECTED.getStatus().equals(getStatus().getStatus())) {
        	 List<InventoryItem> inventoryItems = getInventoryService().getPartsToBeDeleted(inventoryItem);	
        	 	inventoryItem.setDisclaimerInfo(null);
				inventoryItem.setWaiverDuringDr(null);
				inventoryItem.setIsDisclaimer(Boolean.FALSE);
				if (inventoryItems.size() > 0) {
					inventoriesToBeDeleted.addAll(inventoryItems);
				}
				
        	 }
        	 else
        	 {
        	inventories.add(inventoryItem);
        	 }
			itemsForTask.add(inventoryItem);
			if(WarrantyStatus.RESUBMITTED.getStatus().equals(getStatus().getStatus())){
				inventoryItem.setPendingWarranty(true);
			}
			if(!isForPrintPDI() && !(WarrantyStatus.REJECTED.getStatus().equals(getStatus().getStatus()) && folderName.equals("Rejected"))){
				Warranty warranty = setWarrantyForChangesMade(inventoryItemMapping);
			}
			// updating customer's industry code in case if it is changed
			if(null != warranty && null != warranty.getMarketingInformation().getIndustryCode()){
				operator=customerService.findCustomerById(warranty.getCustomer().getId());
				if(null != operator && operator.getSiCode()!=null && null != warranty.getMarketingInformation().getIndustryCode().getIndustryCode() && !(operator.getSiCode().equalsIgnoreCase(warranty.getMarketingInformation().getIndustryCode().getIndustryCode()))){
					operator.setSiCode(warranty.getMarketingInformation().getIndustryCode().getIndustryCode());
					customerService.updateCustomer(operator);
				}				
			}
						
			final List<Document> warrantyAttachments = inventoryItem.getLatestWarranty().getAttachments();
                        warrantyAttachments.clear();
			warrantyAttachments.addAll(
					inventoryItemMapping.getAttachments());
			warrantyAttachments.addAll(getCommonAttachments());
            DocumentTransportUtils.markDocumentsAsAttached(warrantyAttachments);
        }
        if(WarrantyStatus.RESUBMITTED.getStatus().equals(getStatus().getStatus())){
        	warranty.setInvalidItdrAttachment(Boolean.FALSE);
        }
			if (!itemsForTask.isEmpty()) {		
				 if(isForPrintPDI() && !hasErrors()){
			                for(InventoryItem inventoryItem: itemsForTask) {
								 warranty.setMarketingInformation(getMarketingInformation()); //SLMSPROD-1778, contract code is not getting saved while printing 
								 if(!(WarrantyStatus.REJECTED.getStatus().equals(getStatus().getStatus()) && folderName.equals("Rejected"))){
									 		warrantyService.update(inventoryItem.getLatestWarranty());
			                } 
						getInventoryService().updateInventoryItem(inventoryItem);
					 }
		                setPdiGeneration(true);
		                return INPUT;
				 }
		     else{
				warrantyService.removeInventoryAndCreateWarranty(itemsForTask,inventoriesToBeDeleted);
				addActionMessage("message.success.warrantyETRProcess",itemsForTask.get(0).getLatestWarranty().getStatus());
				return SUCCESS;
		      }
			}		
		return SUCCESS;
	}

	private void validateAdditionalComponents(InventoryItem inventoryItem) {
		List<InventoryItemAdditionalComponents> invAddComposition = inventoryItem.getAdditionalComponents();
		StringBuffer descForPartNumber  = new StringBuffer();
		StringBuffer numberForDesc = new StringBuffer();
		boolean isDescBlank = false;
		boolean isPartNumberBlank = false;
		if (invAddComposition != null && invAddComposition.size() > 0) {
			invAddComposition.removeAll(Collections.singleton(null));
			inventoryItem.getAdditionalComponents().removeAll(Collections.singleton(null));
			for (InventoryItemAdditionalComponents part : invAddComposition) {
				if(!StringUtils.hasText(part.getType())){
					addActionError("dealerAPI.warrantyRegistration.typeMandatory");
				}
				if(!StringUtils.hasText(part.getSubType())){
					addActionError("dealerAPI.warrantyRegistration.subTypeMandatory");
				}
				if (StringUtils.hasText(part.getPartNumber()) && !StringUtils.hasText(part.getPartDescription())) {
					descForPartNumber.append(part.getPartNumber());
					descForPartNumber.append(",");
					isDescBlank = true;
				}
				if (!StringUtils.hasText(part.getPartNumber()) && StringUtils.hasText(part.getPartDescription())) {
					numberForDesc.append(part.getPartDescription());
					numberForDesc.append(",");
					isPartNumberBlank = true;
				}
			}

			if(isDescBlank){
				descForPartNumber.deleteCharAt(descForPartNumber.length()-1);
				addActionError("dealerAPI.warrantyRegistration.partDescription", new String[]{descForPartNumber.toString()});
			}
			if(isPartNumberBlank){
				numberForDesc.deleteCharAt(numberForDesc.length()-1);
				addActionError("dealerAPI.warrantyRegistration.partNumber", new String[]{numberForDesc.toString()});
			}
		}
	}

	private void validateCustomerForDisclaimer(){
		EngineTierCtryMapping mapping;
		Country customerCountry = engineTierCtryMappingService.findCountryByName(customer.getAddress().getCountry());
		for(MultipleInventoryAttributesMapper inventoryItemMapping : getInventoryItemMappings()) {
			mapping = engineTierCtryMappingService.findDieselTierByCountry(customerCountry);
			TierTierMapping tierTierMapping = null;
			if(mapping != null){
				tierTierMapping = engineTierCtryMappingService.findTierTierMappingByInventoryTierAndCustomerTier(inventoryItemMapping.getInventoryItem().getDieselTier(), mapping);
			}
			if(tierTierMapping != null){
				addActionError("error.disclaimer.newCustomer");
				return;
			}
		}
		
	}
	
	public String processWarrantyTransitionDealer() {
		validateDocumentType();
		if(WarrantyStatus.REPLIED.getStatus().equals(getStatus().getStatus())) {
        	validateInventoryItemMappingAttachments();
        }
        if(hasErrors()){
            return INPUT;
        }
		List<InventoryItem> itemsForTask = new ArrayList<InventoryItem>();
		for (MultipleInventoryAttributesMapper inventoryItemMapping : getInventoryItemMappings()) {
			InventoryItem inventoryItem = inventoryItemMapping
					.getInventoryItem();
			if(!isWarrantyTaskActive()){
       		 addActionError("error.pendingWarranty.alreadyActedOn");
       		 return INPUT;
       	 	}
			itemsForTask.add(inventoryItem);
			Warranty warranty = inventoryItem.getLatestWarranty();
			warranty.setStatus(getStatus());
			warrantyAudit.setStatus(getStatus());
			warranty.getWarrantyAudits().add(warrantyAudit);
			List<Document> warrantyAttachments =
                                warranty.getAttachments();
			warrantyAttachments.clear();
			warrantyAttachments.addAll(
					inventoryItemMapping.getAttachments());
			warrantyAttachments.addAll(getCommonAttachments());

                        DocumentTransportUtils.markDocumentsAsAttached(warrantyAttachments);
		}
		if (!itemsForTask.isEmpty()) {
			warrantyService.processWarrantyTransitionDealer(itemsForTask);
		}

		addActionMessage("message.success.warrantyETRProcess",itemsForTask.get(0).getLatestWarranty().getStatus());
		return SUCCESS;
	}

	public Warranty getWarranty() {
		return warranty;
	}

	public void setWarranty(Warranty warranty) {
		this.warranty = warranty;
	}

	public void setWarrantyTaskInstanceService(
			WarrantyTaskInstanceService warrantyTaskInstanceService) {
		this.warrantyTaskInstanceService = warrantyTaskInstanceService;
	}

	public WarrantyAudit getWarrantyAudit() {
		return warrantyAudit;
	}

	public void setWarrantyAudit(WarrantyAudit warrantyAudit) {
		this.warrantyAudit = warrantyAudit;
	}

	public boolean isGenericAttachmentRequired() {
        final boolean genericAttachmentAllowedForBU =
                getConfigParamService().getBooleanValue(ConfigName.ENABLE_GENERIC_ATTACHMENT.getName()).booleanValue();
        return genericAttachmentAllowedForBU && (getInventoryItemMappings() == null ||
                getInventoryItemMappings().size() > 1);
    }

	private void removeUnselectedPolicies(
			MultipleInventoryAttributesMapper mapper) {
		for (Iterator<RegisteredPolicy> iterator = mapper.getSelectedPolicies()
				.iterator(); iterator.hasNext();) {
			if (iterator.next() == null) {
				iterator.remove();
			}
		}
	}

	/* This API sets the changes made by admin while processing warranty */
	private Warranty setWarrantyForChangesMade(
			MultipleInventoryAttributesMapper inventoryItemMapping) {
		removeUnselectedPolicies(inventoryItemMapping);
		InventoryItem inventoryItem = inventoryItemMapping.getInventoryItem();
		Warranty latestWarranty = inventoryItem.getLatestWarranty();
		latestWarranty.setForItem(inventoryItem); 
		if(!latestWarranty.getTransactionType().getTrnxTypeKey().equalsIgnoreCase("ETR")
				&& inventoryItemMapping.getWarrantyDeliveryDate()!=null){
			inventoryItem.setDeliveryDate(inventoryItemMapping.getWarrantyDeliveryDate());
			latestWarranty.setDeliveryDate(inventoryItem.getDeliveryDate());
		}else{
			latestWarranty.setDeliveryDate(inventoryItemMapping.getWarrantyDeliveryDate());
		}
		latestWarranty.setAddressForTransfer(getAddressForTransfer());
		latestWarranty.setCustomer(this.customer);
        latestWarranty.setMarketingInformation(getMarketingInformation());
        if (isAdditionalInformationDetailsApplicable() || getMarketingInformation()!=null) {
			latestWarranty.setMarketingInformation(getMarketingInformation());
		}
		 if(isForPrintPDI() && !hasErrors()) {
	            latestWarranty.getLatestAudit().getSelectedPolicies().clear();
	            computePriceForPolicies(inventoryItemMapping);
	            latestWarranty.getLatestAudit().setSelectedPolicies(inventoryItemMapping.getSelectedPolicies());
	            latestWarranty.getLatestAudit().setExternalComments(warrantyAudit.getExternalComments());
		 }
		 else{
		latestWarranty.getPolicies().clear();
        WarrantyAudit latestWarrantyAudit = new WarrantyAudit();
        latestWarrantyAudit.setExternalComments(warrantyAudit.getExternalComments());
        computePriceForPolicies(inventoryItemMapping);
        latestWarrantyAudit.setSelectedPolicies(inventoryItemMapping
				.getSelectedPolicies());
		latestWarrantyAudit.setStatus(getStatus());
        latestWarranty.getWarrantyAudits().add(latestWarrantyAudit);
		latestWarranty.setStatus(getStatus());
		 }
		if(WarrantyStatus.ACCEPTED.getStatus().equals(getStatus().getStatus())){
			latestWarranty.setDraft(false);
		}
        return latestWarranty;
	}
	
	public boolean canModifyDRorETR() {
		return canMondifyBasedOnWindowPeriod() && canMondifyBasedOnOutStandingFMod()
		 			&& canMondifyBasedOnClaims();
	}
	
	public boolean canMondifyBasedOnClaims(){
		return this.claimService
		.findAllPreviousClaimsForItem(warranty.getForItem().getId()).size() == 0 ? true:false;		
	}
	
	public boolean canMondifyBasedOnOutStandingFMod(){
		return this.campaignRepository
			.findPendingNotificationsForItem(warranty.getForItem()).size() == 0 ? true:false;
	}
	
	
	public boolean canMondifyBasedOnWindowPeriod(){
		String dateToBeConsideredForDRDeletion = getConfigParamService()
		.getStringValue(ConfigName.DATE_TO_BE_CONSIDERED_FOR_DELIVERY_REPORT_DELETION
				.getName());
		int daysToBeConsideredForDRDeletion = getConfigParamService().getLongValue(
				ConfigName.DAYS_TO_BE_CONSIDERED_FOR_DELIVERY_REPORT_DELETION
				.getName()).intValue();
		CalendarDate todaysDate = Clock.today();		
		if ("Delivery Date".equals(dateToBeConsideredForDRDeletion)
				&& this.warranty.getDeliveryDate() != null) {
			CalendarDate deleteDRDateBasedOnDD = this.warranty.getDeliveryDate()
			.plusDays(daysToBeConsideredForDRDeletion);
			if (!deleteDRDateBasedOnDD.isBefore(todaysDate)) {
				return true;
			} else {
				return false;
			}
		} else if (this.warranty.getForTransaction().getTransactionDate() != null) {
			CalendarDate deleteDRDateBasedOnSubmitDate = this.warranty
			.getForTransaction().getTransactionDate().plusDays(
					daysToBeConsideredForDRDeletion);
			if (!deleteDRDateBasedOnSubmitDate.isBefore(todaysDate)) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}


	public boolean isAdditionalInformationDetailsApplicable() {
		return this.getConfigParamService()
				.getBooleanValue(ConfigName.ADDITIONAL_INFORMATION_DETAILS_APPLICABLITY
						.getName());
	}

	public String getWarrantyComments() {
		return warrantyComments;
	}

	public void setWarrantyComments(String warrantyComments) {
		this.warrantyComments = warrantyComments;
	}
	
	public CalendarDate getWarrantyEndDate(InventoryItem inventoryItem, PolicyDefinition policyDefinition) {
		CalendarDate warrantyEndDate;
		CalendarDate shipmentDate = inventoryItem.getShipmentDate();
		CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
		if(inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			if(deliveryDate.isAfter(shipmentDate.plusMonths(6))){
				warrantyEndDate = shipmentDate.plusMonths(policyDefinition.getCoverageTerms().getMonthsCoveredFromShipment());
			}else{
				warrantyEndDate = deliveryDate.plusMonths(policyDefinition.getCoverageTerms().getMonthsCoveredFromDelivery());
			}
		} else {
			CalendarDate cutOffDate = getCutOffDate(shipmentDate, policyDefinition);
			Integer monthsCoveredFromDelivery = policyDefinition.getCoverageTerms().getMonthsCoveredFromDelivery();
			if(deliveryDate.isBefore(cutOffDate)) {
				warrantyEndDate = deliveryDate.plusMonths(monthsCoveredFromDelivery);
			} else {
				warrantyEndDate = cutOffDate.plusMonths(monthsCoveredFromDelivery);
			}
		}
		return warrantyEndDate;
	}
	
	public CalendarDate getWarrantyStartDate(InventoryItem inventoryItem, PolicyDefinition policyDefinition) {
		CalendarDate shipmentDate = inventoryItem.getShipmentDate();
		CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
		if (inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			if (deliveryDate.isAfter(shipmentDate.plusMonths(6))) {
				return shipmentDate;
			} else {
				return deliveryDate;
			}
		} else {
			CalendarDate cutOffDate = getCutOffDate(shipmentDate, policyDefinition);
			if(deliveryDate.isBefore(cutOffDate)) {
				return deliveryDate;
			} else {
				return cutOffDate;
			}
		}
	}
	
	public String getMonthsCovered(InventoryItem inventoryItem, PolicyDefinition policyDefinition){
		StringBuffer monthsCovered = new StringBuffer();
		CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
		String monthsCoveredFromDelivery = policyDefinition.getCoverageTerms().getMonthsCoveredFromDelivery().toString();
		String monthsCoveredFromShipment = policyDefinition.getCoverageTerms().getMonthsCoveredFromShipment().toString();
		if(inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			if(deliveryDate.isAfter(inventoryItem.getShipmentDate().plusMonths(6))){
				monthsCovered.append(monthsCoveredFromShipment);
				monthsCovered.append(" Months from Shipment");
			}else{
				monthsCovered.append(monthsCoveredFromDelivery);
				monthsCovered.append(" Months from Delivery");
			}
		} else {
			monthsCovered.append(monthsCoveredFromDelivery);
			monthsCovered.append(" Months from Warranty Start Date");
		}
		return monthsCovered.toString();
	}
	

	public String getJSONifiedAttachmentsList() {
		try {
			List<Document> attachments = this.warranty.getAttachments();
			if (attachments == null || attachments.size() <= 0) {
				return "[]";
			}
			return getDocumentListJSON(attachments).toString();
		} catch (Exception e) {
			return "[]";
		}
	}
	
	public String getJSONifiedCommonAttachmentList() {
		try {
			List<Document> attachments = this.warranty.getAttachments();
			if (attachments == null || attachments.size() <= 0) {
				return "[]";
			}
			return getUnitDocumentListJSON(attachments).toString();
		} catch (Exception e) {
			return "[]";
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ListOfValues> getLovsForClass(String className,
			Warranty warranty) {
		return warrantyService.getLovsForClass(className, warranty);
	}
	
	public boolean displayStockUnitDiscountDetails() {
		return getConfigParamService()
				.getBooleanValue(ConfigName.ENABLE_STOCK_UNIT_DISCOUNT_DETAILS.getName());
	}
}
