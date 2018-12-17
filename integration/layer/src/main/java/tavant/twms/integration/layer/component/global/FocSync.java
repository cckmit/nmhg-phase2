package tavant.twms.integration.layer.component.global;

import static tavant.twms.integration.layer.util.ExceptionUtil.getStackTrace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import tavant.globalsync.foc.FocDocument;
import tavant.twms.claim.FocBean;
import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.additionalAttributes.AttributeAssociationService;
import tavant.twms.domain.additionalAttributes.AttributePurpose;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAttributes;
import tavant.twms.domain.claim.ClaimNumberPatternService;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.foc.FocOrder;
import tavant.twms.domain.claim.foc.FocService;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.infra.DomainRepository;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.transformer.global.FocClaimTransformer;
import tavant.twms.process.ClaimProcessService;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.domain.catalog.ItemGroup;


import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import tavant.twms.domain.common.Constants;

public class FocSync {

	private static final String SYS_GENERATED_COMMENT = "Sys Generated Comment";

	private static final String ORDER_NO_NOT_PASSED = "ORDER NO NOT PASSED";

	private static final String ERROR_CONVERTING_FOC_DOCUMENT_TO_BEAN = "ERROR CONVERTING FOC DOCUMENT TO BEAN";

	private static final Logger logger = Logger.getLogger(FocSync.class
			.getName());
	
	public static final String EXPIRED_ORDER_REPOPULATED = "EXPIRED ORDER REPOPULATED";
	public static final String ORDER_NO_MANDATORY = "ORDER NO MANDATORY";
	public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";	
	public static final String CLAIM_INFO_ALREADY_RECEIEVED = "CLAIM_INFO_ALREADY_RECEIEVED";
	public static final String IMPROPER_USAGE_OF_CREATE = "IMPROPER_USAGE_OF_CREATE";
	
	private FocService focService;
	private FocClaimTransformer focClaimTransformer;
	TransactionTemplate tt;
	private ClaimService claimService;
	private EventService eventService;
	private ClaimProcessService claimProcessService;
	private InventoryService inventoryService;
    private AttributeAssociationService attributeAssociationService;
    private OrgService orgService;
    private DomainRepository domainRepository;
    private CatalogService catalogService;
	private PolicyService policyService;
	private ClaimNumberPatternService claimNumberPatternService;

	public FocDocument fetchFocOrder(String orderNo) {
		logger.debug("inside FocSync :"+ orderNo);
		FocOrder focOrder =  this.focService.fetchFOCOrderDetails(orderNo);
		if(focOrder==null){
			return FocDocument.Factory.newInstance();
		}
		XStream xstream = new XStream(new DomDriver());
		FocBean focBean  = (FocBean)xstream.fromXML(focOrder.getOrderInfo());		
		FocDocument focDoc = focClaimTransformer.convertBeanToDocument(focBean);
		logger.debug("inside FocSync returning document :"+ focDoc.getFoc().getDataArea().getCausalPartNumber());
		return focDoc;
	}

	public SyncResponse storeOrderDetails(final FocDocument focDocument){
		FocBean focBean = null;
		SyncResponse syncResponse = new SyncResponse();
		syncResponse.setBusinessId(focDocument.getFoc().getDataArea().getOrderNo());
		syncResponse.setUniqueIdName("OrderNo");
		syncResponse.setUniqueIdValue(focDocument.getFoc().getDataArea().getOrderNo());
		
		try {
			focBean = this.focClaimTransformer.covertDocumentToBean(focDocument);
		} catch (RuntimeException e1) {
			logger.error("Error transforming foc Document to bean in storeOrderDetails"+e1);
			syncResponse.setSuccessful(false);
			return syncResponse;
			
		}
		if (focBean != null 			
					&& !StringUtils.isBlank(focBean
							.getOrderNo())) {
			
			final FocBean claimBean = focBean;
				try {
					tt.execute(new TransactionCallbackWithoutResult() {
						protected void doInTransactionWithoutResult(
								TransactionStatus transactionStatus) {
							storeFocOrderData( claimBean);
						}
					});
					syncResponse.setSuccessful(true);
				} catch (RuntimeException e) {
					logger.error(e, e);
					String stackTrace = getStackTrace(e);
					if(IMPROPER_USAGE_OF_CREATE.equals(e.getMessage())){
						syncResponse.setErrorCode(SyncResponse.ERROR_CODE_VALIDATION_ERROR);
						syncResponse.setErrorType(SyncResponse.ERROR_CODE_VALIDATION_ERROR);				
					} else{
						syncResponse.setErrorCode(SyncResponse.ERROR_CODE_SYSTEM_ERROR);
						syncResponse.setErrorType(SyncResponse.ERROR_CODE_SYSTEM_ERROR);				
		                syncResponse.setException(stackTrace);				
					}
					syncResponse.setSuccessful(false);			
				}

			} else{
				syncResponse.setErrorCode(ORDER_NO_MANDATORY);
				syncResponse.setErrorType(SyncResponse.ERROR_CODE_VALIDATION_ERROR);
			}
		
		return syncResponse;
			
		}	

	
	private void storeFocOrderData(FocBean focBean) {
		FocOrder focOrder = this.focService
				.fetchFOCOrderDetails(focBean.getOrderNo());
		if(focOrder ==null){   
			focOrder = new FocOrder();
			String orderXml = new XStream(new DomDriver()).toXML(focBean);	
			focOrder.setOrderNo(focBean.getOrderNo());
			focOrder.setOrderInfo(orderXml);
			focOrder.setStatus(FocOrder.CLAIM_INFO_AWAITED);
			focOrder.getD().setActive(Boolean.TRUE);
			focOrder.getD().setCreatedOn(CalendarDate.from(Clock.now(),TimeZone.getDefault()));
			focOrder.getD().setInternalComments(EXPIRED_ORDER_REPOPULATED);
			focOrder.getD().setCreatedTime(new java.util.Date());
			try {
				this.focService.save(focOrder);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else{
			throw new RuntimeException(IMPROPER_USAGE_OF_CREATE);
		}
	}
	
		
	public SyncResponse storeClaim(final FocDocument focDocument){
		
    FocBean focBean = null; 
	SyncResponse syncResponse = new SyncResponse();
	syncResponse.setBusinessId(focDocument.getFoc().getDataArea().getOrderNo());
	syncResponse.setUniqueIdName("OrderNo");
	syncResponse.setUniqueIdValue(focDocument.getFoc().getDataArea().getOrderNo());
	
	
	try {
		focBean = this.focClaimTransformer.covertDocumentToBean(focDocument);
	} catch (RuntimeException e1) {
         syncResponse = createErrorResponse(e1, ERROR_CONVERTING_FOC_DOCUMENT_TO_BEAN);
		return syncResponse;
	}
	
	if (focDocument != null
				&& focDocument.getFoc() != null
				&& focDocument.getFoc().getDataArea() != null
				&& !StringUtils.isBlank(focDocument.getFoc().getDataArea()
						.getOrderNo())) {
	final FocBean claimBean = focBean;	
		try {
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(
						TransactionStatus transactionStatus) {
					//set the status to claim info recieved
					storeClaimData(claimBean);

					//Process the Claim.. create a draft Claim
					final FocOrder claimOrder = focService
					.fetchFOCOrderDetails(focDocument.getFoc()
							.getDataArea().getOrderNo());					
					storeDraftClaim(claimOrder,claimBean);
				}
			});
			
			syncResponse.setSuccessful(true);					
            			
		} catch (Exception e) {
			logger.error("Error while saving and processing", e);					   
			syncResponse = createErrorResponse(e, "ERROR WHILE SAVE AND PROCESSING");			
		}

		}else{
			syncResponse = createErrorResponse(null, ORDER_NO_NOT_PASSED);
		}
	
	return syncResponse;
		
	}
	
	private FocOrder storeClaimData( FocBean focBean) {
		FocOrder focOrder = this.focService
				.fetchFOCOrderDetails(focBean.getOrderNo());
		if(focOrder!=null){   
			 if(FocOrder.CLAIM_INFO_RECEIVED.equals(focOrder.getStatus())){
				 throw new RuntimeException(CLAIM_INFO_ALREADY_RECEIEVED);
			 }	
			String claimXml = new XStream(new DomDriver()).toXML(focBean);	
			focOrder.setClaimInfo(claimXml);
			focOrder.setStatus(FocOrder.CLAIM_INFO_RECEIVED);
			focOrder.getD().setActive(Boolean.TRUE);
			focOrder.getD().setUpdatedOn(CalendarDate.from(Clock.now(),TimeZone.getDefault()));
			try {
				this.focService.save(focOrder);
			} catch (Exception e) {
				throw new RuntimeException("ERROR SAVING ORDER "+e.getMessage(),e);
			}
		} else{
			throw new RuntimeException(ORDER_NOT_FOUND);
		}
		
		return focOrder;
	}

	void storeDraftClaim(FocOrder focOrder,FocBean focBean){	 	
/*	    String claimXmlStream = focOrder.getClaimInfo();
		FocBean focBean = (FocBean) new XStream(new DomDriver())
				.fromXML(claimXmlStream);*/
		try{
		Claim claim = new MachineClaim();
		claim.setEventService(this.eventService);
		setClaimFields(focBean, claim);
		prepareAttributesForClaim(claim);		
        this.claimService.initializeClaim(claim);
        this.claimService.createClaim(claim);
        this.claimService.updateOEMPartInformation(claim, null);
        this.claimProcessService.startClaimProcessingWithTransition(claim, "Draft");
        String claimNumber = this.claimNumberPatternService.generateNextClaimNumber(claim);
        claim.setClaimNumber(claimNumber);
        this.claimService.updateClaim(claim);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}



	private void setClaimFields(FocBean focBean, Claim claim) {
		
		InventoryItem inventoryItem = null;
		try {
			inventoryItem = this.inventoryService.findInventoryItemBySerialNumber(focBean
					.getSerialNumber());
		} catch (ItemNotFoundException e) {
			logger.error(e);
			throw new RuntimeException("SERIAL_NOT_AVAILABLE", e);
		}
		ItemReference itemReference = new ItemReference(inventoryItem);
		ClaimedItem claimedItem = new ClaimedItem();
		claimedItem.setHoursInService(new BigDecimal(0));
		claimedItem.setItemReference(itemReference);
		claimedItem.setClaim(claim);
		List<ClaimedItem> listOfClaimedItems = new ArrayList<ClaimedItem>();
		listOfClaimedItems.add(claimedItem);
		claim.setClaimedItems(listOfClaimedItems);
		setPolicyOnClaimedItems(claim);
		
		claim.setFoc(true);
		claim.setFocOrderNo(focBean.getOrderNo());
		claim.setRepairDate(focBean.getRepairDate());
		claim.setWorkOrderNumber(focBean.getWorkOrderNumber());
		claim.setFailureDate(focBean.getFailureDate());
		ServiceProvider serviceProvider = this.orgService.findServiceProviderByNumber(focBean.getServiceProviderNo());
		claim.setForDealer(serviceProvider);
	    claim.setForDealerShip(serviceProvider);
		User user = new SecurityHelper().getLoggedInUser();
		claim.setFiledBy(user);		
		claim.setLastUpdatedBy(user);
		claim.setLastModifiedBy(user);
        ServiceInformation serviceInformation = claim.getServiceInformation();        
        Item  causalPart= null;
		try {
			causalPart = this.catalogService.findItemOwnedByManuf(focBean.getCausalPart().getNumber());
		} catch (CatalogException e) {		
			e.printStackTrace();
		}		
		FaultCode faultCodeRef = (FaultCode)this.domainRepository.load(FaultCode.class,focBean.getFaultCodeRef().getId());
		FailureTypeDefinition faultFound =(FailureTypeDefinition) this.domainRepository.load(FailureTypeDefinition.class,focBean.getFaultFound().getId());
		//FailureCauseDefinition causedBy = (FailureCauseDefinition)this.domainRepository.load(FailureCauseDefinition.class,focBean.getCausedBy().getId());
		serviceInformation.setCausalPart(causalPart);
		serviceInformation.setFaultCodeRef(faultCodeRef);
		serviceInformation.setFaultFound(faultFound);
		//serviceInformation.setCausedBy(causedBy);
		List<HussmanPartsReplacedInstalled> hussmannPartsReplacedInstalled = focBean.getHussmanPartsReplacedInstalled();
		this.prepareHussmannInstalledParts(hussmannPartsReplacedInstalled);
		serviceInformation.getServiceDetail().getHussmanPartsReplacedInstalled().addAll(hussmannPartsReplacedInstalled);		
		claim.setCreatedOn(CalendarDate.from(Clock.now(),TimeZone.getDefault()));
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit("Hussmann");
		claim.setServiceManagerRequest(false);
		claim.setForMultipleItems(Boolean.FALSE);
		claim.setFiledOnDate(Clock.today());
		claim.setConditionFound(SYS_GENERATED_COMMENT);
		claim.setWorkPerformed(SYS_GENERATED_COMMENT);
		claim.setProbableCause(SYS_GENERATED_COMMENT);
		claim.setOtherComments(SYS_GENERATED_COMMENT);
	}
	

    private void prepareAttributesForClaim(Claim claim) {
        for (ClaimedItem claimedItem : claim.getClaimedItems()) {
            List<AdditionalAttributes> additionalAttributes = new ArrayList<AdditionalAttributes>();
            List<ItemGroup> itemGroups = new ArrayList<ItemGroup>();
            itemGroups.add(claimedItem.getItemReference().getReferredInventoryItem().getOfType().getModel());
            itemGroups.add(claimedItem.getItemReference().getReferredInventoryItem().getOfType().getProduct());
            if(!itemGroups.isEmpty()){
            	additionalAttributes = this.attributeAssociationService
                .findAttributesForItemGroups(itemGroups,  claim.getType(), AttributePurpose.CLAIMED_INVENTORY_PURPOSE);
        
            }
            for (AdditionalAttributes addAttribute : additionalAttributes) {
                claimedItem.addClaimAttributes(new ClaimAttributes(addAttribute, null));
            }
        }
    }

	
    private void prepareHussmannInstalledParts(
			List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled) {
		if (hussmanPartsReplacedInstalled != null
				&& hussmanPartsReplacedInstalled.size() > 0) {
			for (HussmanPartsReplacedInstalled element : hussmanPartsReplacedInstalled) {
				element.setReadOnly(true);
				if (element.getReplacedParts() != null
						&& element.getReplacedParts().size() > 0) {
					element.getReplacedParts().removeAll(
							Collections.singleton(null));
					for (OEMPartReplaced replacedPart : element
							.getReplacedParts()) {
						ItemReference itemReference = new ItemReference();
						try {
							Item item =fetchByName(replacedPart
									.getItemReference().getReferredItem().getNumber());
							if(item ==null){
								throw new RuntimeException("HUSSMANN INSTALLED PART NOT FOUND"+replacedPart
										.getItemReference().getReferredItem().getNumber());
							}													
							itemReference
									.setReferredItem(item);
							replacedPart.setItemReference(itemReference);
							replacedPart.setCostPricePerUnit(Money.dollars(0));
							replacedPart.setPricePerUnit(Money.dollars(0));
							replacedPart.setMaterialCost(Money.dollars(0));
							replacedPart.setReadOnly(true);
						} catch (CatalogException e) {
							logger.error(e);
						}

					}
				}
				if (element.getHussmanInstalledParts() != null
						&& element.getHussmanInstalledParts().size() > 0) {
					element.getHussmanInstalledParts().removeAll(
							Collections.singleton(null));
					for (InstalledParts installedPart : element
							.getHussmanInstalledParts()) {
	    					try {
							Item item =fetchByName(installedPart
									.getItem().getNumber());
							if(item == null){
								throw new RuntimeException("HUSSMANN INSTALLED PART NOT FOUND"+installedPart.getItem().getNumber());
							}
							installedPart.setItem(item);									
							installedPart.setIsHussmanPart(true);
							installedPart.setCostPricePerUnit(Money.dollars(0));
							installedPart.setPricePerUnit(Money.dollars(0));
							installedPart.setMaterialCost(Money.dollars(0));
							installedPart.setIsHussmanPart(Boolean.TRUE);
							installedPart.setReadOnly(true);
						    } catch (CatalogException e) {
								logger.error(e);
								
	    					}

							
							

					}

				}

			}
		}

	}
    
    
	void setPolicyOnClaimedItems(Claim theClaim) {
		if (!theClaim.canPolicyBeComputed()) {
			setClaimProcessedAs(theClaim);
			return;
		}
		for (ClaimedItem claimedItem : theClaim.getClaimedItems()) {
			try {
				Policy applicablePolicy = this.policyService
						.findApplicablePolicy(claimedItem);
				claimedItem.setApplicablePolicy(applicablePolicy);
			} catch (PolicyException e) {
				throw new RuntimeException(
						"Failed to find policy for Claimed Item [ "
								+ claimedItem + "]", e);
			}
		}
		setClaimProcessedAs(theClaim);
	}
    
	
	@SuppressWarnings("deprecation")
	public void setClaimProcessedAs(Claim claim) {
		if (!claim.getItemReference().isSerialized()) {
			claim.setClaimProcessedAs(Constants.INVALID_ITEM_NO_WARRANTY);
		} else if (claim.getItemReference().getReferredInventoryItem() != null
				&& InventoryType.STOCK.getType().equals(
						claim.getItemReference().getReferredInventoryItem()
								.getType().getType())) {
			claim.setClaimProcessedAs(Constants.VALID_ITEM_STOCK);			
		} else if (claim.getItemReference().getReferredInventoryItem() != null
				&& InventoryType.RETAIL.getType().equals(
						claim.getItemReference().getReferredInventoryItem()
								.getType().getType())) {
			if (claim.getItemReference().getReferredInventoryItem()
					.getWarranty().getPolicies() == null
					|| claim.getItemReference().getReferredInventoryItem()
							.getWarranty().getPolicies().isEmpty()) {
				claim.setClaimProcessedAs(Constants.VALID_ITEM_NO_WARRANTY);				
			} else if (claim.getClaimedItems().get(0).getApplicablePolicy() == null
					|| claim.getClaimedItems().get(0).getApplicablePolicy()
							.getCode() == null) {
				claim.setClaimProcessedAs(Constants.VALID_ITEM_OUT_OF_WARRANTY);				
			}
		}
		if(claim.getClaimedItems() != null && !claim.getClaimedItems().isEmpty() 
				&& claim.getClaimedItems().get(0).getApplicablePolicy() != null)
		{
			claim.setClaimProcessedAs(claim.getClaimedItems().get(0).getApplicablePolicy().getCode());
		}
	}

    public Item fetchByName(String name) throws CatalogException {
    	Item itemToReturn = null;
    	try
    	{
    		
    		itemToReturn = catalogService.findItemOwnedByManuf(name);
    	}
    	catch(CatalogException e)
    	{        
	    	//Part is not owned by manufacturer, try if its owned by service provider
	    	Organization serviceProviderOrg = new SecurityHelper().getLoggedInUser().getBelongsToOrganization();
	    	if(serviceProviderOrg != null)
	    	{        		
	    		itemToReturn = catalogService.findItemByItemNumberOwnedByServiceProvider(name,serviceProviderOrg.getId());
	    	}
       
    	}
        return itemToReturn;
    }
    
	protected void setTotalQtyForReplacedParts(Claim claim) {
		List<OEMPartReplaced> oemPartReplaced = claim.getServiceInformation()
				.getServiceDetail().getOemPartsReplaced();
		List<NonOEMPartReplaced> nonOemPartReplaced = claim
				.getServiceInformation().getServiceDetail()
				.getNonOEMPartsReplaced();
		for (OEMPartReplaced oemPart : oemPartReplaced) {
			if (oemPart.getInventoryLevel().booleanValue()) {
				oemPart.setNumberOfUnits(oemPart.getNumberOfUnits()
						* getApprovedClaimedItems(claim));
			}
		}
		for (NonOEMPartReplaced nonOemPart : nonOemPartReplaced) {
			if (nonOemPart.getInventoryLevel().booleanValue()) {
				nonOemPart.setNumberOfUnits(nonOemPart.getNumberOfUnits()
						* getApprovedClaimedItems(claim));
			}
		}
	}
    
    
	private int getApprovedClaimedItems(Claim claim) {
		int approvedClaimedItems = 0;
		List<ClaimedItem> claimedItems = claim.getClaimedItems();
		for (ClaimedItem claimedItem : claimedItems) {
			if (claimedItem.isProcessorApproved()) {
				approvedClaimedItems++;
			}
		}
		return approvedClaimedItems;
	}
	
	private SyncResponse createErrorResponse(
			 Exception e,String syncTypeMessage) {		
		SyncResponse response;
		response = new SyncResponse();
		response.setSuccessful(false);
		if(!StringUtils.isBlank(e.getMessage())){
		response.setException(new StringBuilder().append(
				syncTypeMessage).append(e.getMessage()).toString());
		}else{
			response.setException(new StringBuilder().append(
					syncTypeMessage).append(e.getStackTrace()).toString());	
		}
		response.setErrorCode(SyncResponse.ERROR_CODE_VALIDATION_ERROR);
		response.setErrorType(SyncResponse.ERROR_CODE_VALIDATION_ERROR);
		return response;
	}
	
    
	public void setFocClaimTransformer(FocClaimTransformer focClaimTransformer) {
		this.focClaimTransformer = focClaimTransformer;
	}
	
	public void setTransactionTemplate(TransactionTemplate tt) {
		this.tt = tt;
	}


	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}


	public void setClaimProcessService(ClaimProcessService claimProcessService) {
		this.claimProcessService = claimProcessService;
	}


	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}


	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void setAttributeAssociationService(
			AttributeAssociationService attributeAssociationService) {
		this.attributeAssociationService = attributeAssociationService;
	}

	public void setDomainRepository(DomainRepository domainRepository) {
		this.domainRepository = domainRepository;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setFocService(FocService focService) {
		this.focService = focService;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public void setClaimNumberPatternService(
			ClaimNumberPatternService claimNumberPatternService) {
		this.claimNumberPatternService = claimNumberPatternService;
	}

	public EventService getEventService() {
		return eventService;
	}

	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	
}
