package tavant.twms.web.claim.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.jbpm.taskmgmt.exe.TaskInstance;

import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.MarketingGroupsLookup;
import tavant.twms.domain.claim.MultiInventorySearch;
import tavant.twms.domain.claim.claimsubmission.ClaimSubmissionUtil;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InvClassDealerMapping;
import tavant.twms.domain.inventory.InvClassDealerMappingService;
import tavant.twms.domain.inventory.InventoryClass;
import tavant.twms.domain.inventory.InventoryClassService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.orgmodel.BrandType;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.RegisteredPolicyStatusType;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.process.ClaimProcessService;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.xforms.TaskView;
import tavant.twms.worklist.WorkListItemService;

import com.domainlanguage.timeutil.Clock;

public abstract class AbstractNewClaimAction extends I18nActionSupport implements ServletResponseAware, TWMSWebConstants
{
	
	private final Logger logger = Logger.getLogger(AbstractNewClaimAction.class);
    private ClaimService claimService;
    private ClaimProcessService claimProcessService;
    private WorkListItemService workListItemService;
    private PolicyService policyService;
    private InventoryService inventoryService;
    private ConfigParamService configParamService;
    private DealershipRepository dealershipRepository;
    private CatalogService catalogService;

    private Long id;
    private TaskView task;
    private ServiceProvider dealer;

    private boolean isPageOne;
    private String claimType;

    // These attributes are related to multipleInventories Claim
    private HttpServletResponse response;
    private MultiInventorySearch multiCarSearch;
    private Integer pageNo=new Integer(0);
    private ListCriteria listCriteria;
    private List<InventoryItem> inventoryItems=new ArrayList<InventoryItem>();
    private List<Integer> pageNoList = new ArrayList<Integer>();
    private Long dealerId ;
    private String selectedItemsIds;
    private String campaignCode;
    private int previousCounter=0;
    private int nextCounter=0;
    private boolean nextButton;
    private boolean previousButton;
  
	// Used only for validation.
    protected String forSerialized;
    private int totalpages;
    private ClaimSubmissionUtil claimSubmissionUtil;
    
	private int rowIndex;
    
	private int subRowIndex;

	private final String FPI_WARRANTY_TYPE="FPI";
	
	private InvClassDealerMappingService invClassDlrMappingService;
	private InventoryClassService inventoryClassService;
	
    public boolean isShowPartSerialNumber() {
		return getConfigParamService()
					.getBooleanValue(
							ConfigName.SHOW_PART_SN_ON_INSTALLED_REMOVED_SECTION
									.getName());
	}
    
    public boolean isShowPartNotInstalledOnPartsClaim() {
		return getConfigParamService()
					.getBooleanValue(
							ConfigName.SHOW_SOLD_ALONE_PART_NOT_INSTALLED_ON_PARTS_CLAIM
									.getName());
	}
    
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}
	
	public void setSubRowIndex(int subRowIndex) {
		this.subRowIndex = subRowIndex;
	}
	
	public int getSubRowIndex() {
		return subRowIndex;
	}

    private String selectedBusinessUnit = null;

    public String showForm() {
        return SUCCESS;
    }
    
    public void setClaimSubmissionUtil(ClaimSubmissionUtil claimSubmissionUtil) {
        this.claimSubmissionUtil = claimSubmissionUtil;
    }
    
    public ClaimSubmissionUtil getClaimSubmissionUtil() {
		return claimSubmissionUtil;
	}

    public boolean displayBrandDropDown(){
        return configParamService.getBooleanValue(ConfigName.DISPLAY_BRAND_DROP_DOWN.getName());
    }
    
    public String saveDraft(Claim claim) {
        // This check is required, since we want to avoid issues caused by multiple attempts to save draft (typically,
        // when the user clicks the "Go To Page 1" button and comes back here.
    	boolean isSerialized = (!StringUtils.hasText(getForSerialized()) || TRUE.equals(getForSerialized()));
    
    	if((claim.getCompetitorModelBrand()!=null && !claim.getCompetitorModelBrand().isEmpty() && !claim.getCompetitorModelDescription().isEmpty() && !claim.getCompetitorModelTruckSerialnumber().isEmpty()))
          {
          	claim.getClaimedItems().get(0).getItemReference()
                  .setSerialized(false);
                  
          }
    	if(claim.getBrand()==null ||claim.getBrand().isEmpty() )
        {
        	if(claim.getItemReference().getReferredInventoryItem()!=null)
        	{
        		claim.setBrand(claim.getItemReference().getReferredInventoryItem().getBrandType());
        	}
        }
        prepareAttributesForClaim(claim, isSerialized);
        
        claim.setNcrWith30Days(claim.getInventoryClassFor30DayNcr() != null);
        
        if(claim.getId() == null) {
            claim.setFiledOnDate(Clock.today());
            this.claimService.initializeClaim(claim);
            //setPolicyOnClaimedItems(claim);
            this.claimService.createClaim(claim);
            
            
            this.claimProcessService.startClaimProcessingWithTransition(claim, "Draft");
            /** Perf Fix **/
            //this.claimService.updateClaim(claim);
        } else {
            this.claimService.updateClaim(claim);
        }
        
        if (claim.getServiceInformation().getServiceDetail()
				.getHussmanPartsReplacedInstalled() != null
				&& !claim.getServiceInformation().getServiceDetail()
						.getHussmanPartsReplacedInstalled().isEmpty()) {
        	setRowIndex(claim.getServiceInformation()
    				.getServiceDetail().getHussmanPartsReplacedInstalled().size());
		} else {
			setRowIndex(0);
		}
        setSubRowIndex(0);
        TaskInstance taskInstance =
            this.workListItemService.findTaskForClaimWithTaskName(claim.getId(), "Draft Claim");
        this.task = new TaskView(taskInstance);
        if(hasActionErrors()){
        	return INPUT;
        }
        else{
        	 return SUCCESS;
        }
       
    }
	

    protected Boolean validatePartsClaimforStandardPolicy(Claim claim) {
    	boolean stdwrntyValdn = false;
		if(claim.getType().equals(ClaimType.PARTS)){
			if(claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem()!=null){
				List<RegisteredPolicy> policies=listApplicablePolicies(claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem(),claim);
				if(!policies.isEmpty()){
					for(RegisteredPolicy policy:policies){
						if(policy.getWarrantyType().getType().equals(WarrantyType.STANDARD.getType())){
								stdwrntyValdn = true;
							break;	
						}
					}
				}
			}
			
		}
		return stdwrntyValdn;
	}
			

	private List<RegisteredPolicy> listApplicablePolicies(InventoryItem inventoryItem,Claim claim) {
		List<RegisteredPolicy> existingPolices = new ArrayList<RegisteredPolicy>();
		Warranty earlierWarranty = inventoryItem.getWarranty();
        if(earlierWarranty != null){
                for (RegisteredPolicy registeredPolicy : earlierWarranty.getPolicies()) {
                    if (registeredPolicy.getPolicyDefinition().getTransferDetails()
                            .isTransferable()
                            && !registeredPolicy.getWarrantyPeriod().getTillDate()
                            .isBefore(inventoryItem.getDeliveryDate())){
                        if(claim.getFailureDate().isBefore(registeredPolicy.getWarrantyPeriod().getTillDate().nextDay())){
                            if (RegisteredPolicyStatusType.ACTIVE.getStatus().
                                equals(registeredPolicy.getLatestPolicyAudit().getStatus())) {
                                    if(claim.getHoursInService().intValue() <= registeredPolicy.getLatestPolicyAudit().getServiceHoursCovered())
                                    	if(registeredPolicy.getPolicyDefinition().getApplicabilityTerms().isEmpty())
                                    		existingPolices.add(registeredPolicy);
                        }
                    }
                }
            }
        }
		Collections.sort(existingPolices);
			return existingPolices;

}

	void setPolicyOnClaimedItems(Claim theClaim) {
		if (!theClaim.canPolicyBeComputed()) {
			return;
		}

        for(ClaimedItem claimedItem : theClaim.getClaimedItems()) {
            try {
                Policy applicablePolicy = this.policyService.findApplicablePolicy(claimedItem);
                claimedItem.setApplicablePolicy(applicablePolicy);
            } catch (PolicyException e) {
                throw new RuntimeException("Failed to find policy for Claimed Item [ " + claimedItem + "]", e);
            }
        }
	}

    // This API Checks for the validity of invalid serial number
    // If the serial number is valid then it is set to serialized claim
    protected void checkIfInvExistsForNonSerializedSerialNumber(Claim claim)
    {
    	try
    	{
    		if(claim.getItemReference().getModel()!=null
    						&& claim.getItemReference().getUnszdSlNo() != null && 
    						StringUtils.hasText(claim.getItemReference().getUnszdSlNo()))
    		{
    			InventoryItem inventoryItem = this.inventoryService.findSerializedItem(claim.getItemReference().getUnszdSlNo(),
    					claim.getItemReference().getModel().getName());
    			if(inventoryItem!=null )
    			{
    				ItemReference itemReference = claim.getItemReference();
    				itemReference.setReferredInventoryItem(inventoryItem);
    			}
    		}
    	}
    	catch(ItemNotFoundException exception)
    	{

    	}
    }

    public Long getId() {
        return this.id;
    }

    public TaskView getTask() {
        return this.task;
    }

    public ServiceProvider getDealer() {
        return this.dealer;
    }

    public void setDealer(ServiceProvider dealer) {
        this.dealer = dealer;
    }

    public void setClaimProcessService(ClaimProcessService claimProcessService) {
        this.claimProcessService = claimProcessService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public ClaimService getClaimService() {
        return claimService;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public PolicyService getPolicyService() {
		return this.policyService;
	}

    public boolean isPageOne() {
        return this.isPageOne;
    }

    public void setPageOne(boolean isPageOne) {
        this.isPageOne = isPageOne;
    }

    public String getForSerialized() {
        return forSerialized;
    }

    public void setForSerialized(String forSerialized) {
        this.forSerialized = forSerialized;
    }

    public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public String getClaimType() {
		return claimType;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public ListCriteria getListCriteria() {
		return listCriteria;
	}

	public void setListCriteria() {
		ListCriteria criteria = new ListCriteria();
		criteria.addSortCriteria("serialNumber", true);
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageNumber(this.pageNo.intValue());
		pageSpecification.setPageSize(50);
		criteria.setPageSpecification(pageSpecification);
		this.listCriteria=criteria;
	}

	public List<InventoryItem> getInventoryItems() {
		return inventoryItems;
	}

	public void setInventoryItems(List<InventoryItem> inventoryItems) {
		this.inventoryItems = inventoryItems;
	}

	public List<Integer> getPageNoList() {
		return pageNoList;
	}

	public void setPageNoList(List<Integer> pageNoList) {
		this.pageNoList = pageNoList;
	}

	public Long getDealerId() {
		return dealerId;
	}

	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
	}

	public String getSelectedItemsIds() {
		return selectedItemsIds;
	}

	public void setSelectedItemsIds(String selectedItemsIds) {
		this.selectedItemsIds = selectedItemsIds;
	}

	 public MultiInventorySearch getMultiCarSearch() {
		return multiCarSearch;
	}

	public void setMultiCarSearch(MultiInventorySearch multiCarSearch) {
		this.multiCarSearch = multiCarSearch;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setServletResponse(HttpServletResponse httpServletResponse) {
		this.response = httpServletResponse;
	}

	public String getMultipleInventories() throws IOException {
	        if (wereNoSearchParametersSpecified(this.multiCarSearch)) {
	            return sendValidationResponse();
	        }else{
	        setListCriteria();
	        PageResult<InventoryItem> pageResult = null;
	        if(ClaimType.FIELD_MODIFICATION.getType().equals(claimType) ||
	        		ClaimType.CAMPAIGN.getType().equals(claimType)){
	        	pageResult= this.inventoryService.findAllInventoryItemsForCampaignMultiClaim
	        	(multiCarSearch, dealerId, getListCriteria(), campaignCode.toUpperCase());
	        }else {
	        	pageResult= this.inventoryService.findAllInventoryItemsForMultiClaim
	    	(this.multiCarSearch,this.dealerId,getListCriteria());
	        }
	        setTotalpages(pageResult.getNumberOfPagesAvailable());
	        setInventoryItems(pageResult.getResult());
	        setPreviousButton(false);
	        if(pageResult.getNumberOfPagesAvailable()<=10){
	        	setNextButton(false);
	        }else{
	        	setNextButton(true);
	        }
		        for (int i=0;i<pageResult.getNumberOfPagesAvailable();i++){
		        	this.pageNoList.add(new Integer(i+1));
		        	if(getNextCounter()!=-1 && i==9){
		        		break;
		        	}
		        }
	        }
	        return SUCCESS;
	    }

	private boolean wereNoSearchParametersSpecified(MultiInventorySearch multiInventorySearch) {
        if (multiInventorySearch != null && multiInventorySearch.getInventoryType() != null) {
            if ( multiInventorySearch.getModelNumber() == null
                    && multiInventorySearch.getSerialNumber() == null
                    && multiInventorySearch.getCustomer() == null
                    && multiInventorySearch.getDealerNumber()==null
                    && multiInventorySearch.getYearOfShipment()==null) {
                return true;
            }
        }

        return false;
    }

	public String sendValidationResponse() throws IOException {
        this.response.setHeader("Pragma", "no-cache");
        this.response.addHeader("Cache-Control", "must-revalidate");
        this.response.addHeader("Cache-Control", "no-cache");
        this.response.addHeader("Cache-Control", "no-store");
        this.response.setDateHeader("Expires", 0);
        this.response.setContentType("text/html");
        this.response.getWriter().write("<true>");
        this.response.flushBuffer();
        return null;
    }

	 public String getInventoriesForpage() throws IOException {
	    	setListCriteria();
	    	previousCounter=(getListCriteria().getPageSpecification().getPageNumber()+1)/10;
	    	if(getListCriteria().getPageSpecification().getPageNumber()+1-(previousCounter*10)==0 && previousCounter!=0){
	    		previousCounter--;
	    	}
	    	nextCounter=((getListCriteria().getPageSpecification().getPageNumber())+1)/10;
	    	if(getListCriteria().getPageSpecification().getPageNumber()+1-(nextCounter*10)==0 && nextCounter!=0){
	    		nextCounter--;
	    	}
	    	PageResult<InventoryItem> pageResult =null;
	        if(ClaimType.CAMPAIGN.getType().equals(claimType) ||
	        		ClaimType.FIELD_MODIFICATION.getType().equals(claimType)){
	        	pageResult= this.inventoryService.findAllInventoryItemsForCampaignMultiClaim
	        	(multiCarSearch, dealerId, getListCriteria(), campaignCode.toUpperCase());
	        }else if(ClaimType.MACHINE.getType().equals(claimType)){
	        	pageResult= this.inventoryService.findAllInventoryItemsForMultiClaim
	    	(this.multiCarSearch,this.dealerId,getListCriteria());
	        }
	        setTotalpages(pageResult.getNumberOfPagesAvailable());
	        setInventoryItems(pageResult.getResult());
		        for (int i=previousCounter*10;i<pageResult.getNumberOfPagesAvailable();i++){
		        	this.pageNoList.add(new Integer(i+1));
		        	if(((nextCounter+1)*10)-1==i){
						 break;
					 }
		        }
		        if(previousCounter<=0){
					 setPreviousButton(false);
				 }else{
					 setPreviousButton(true);
				 }
		        if((nextCounter+1)*10>=pageResult.getNumberOfPagesAvailable()){
		        	setNextButton(false);
		        }else{
		        	setNextButton(true);
		        }

	        return SUCCESS;
	    }


	 public String getInventoriesForNextButton() throws IOException {
		 setListCriteria();
		 nextCounter++;
		 previousCounter++;
		 getListCriteria().getPageSpecification().setPageNumber((nextCounter*10));
		 setPageNo(nextCounter*10);
		 PageResult<InventoryItem> pageResult =null;
		 if(ClaimType.CAMPAIGN.getType().equals(claimType)||
				 ClaimType.FIELD_MODIFICATION.getType().equals(claimType)){
			 pageResult= this.inventoryService.findAllInventoryItemsForCampaignMultiClaim
			 (multiCarSearch, dealerId, getListCriteria(), campaignCode.toUpperCase());
		 }else if(ClaimType.MACHINE.getType().equals(claimType)){
			 pageResult= this.inventoryService.findAllInventoryItemsForMultiClaim
			 (this.multiCarSearch,this.dealerId,getListCriteria());
		 }
		 setInventoryItems(pageResult.getResult());
		 setTotalpages(pageResult.getNumberOfPagesAvailable());
		 for (int i=nextCounter*10;i<pageResult.getNumberOfPagesAvailable();i++){
			 this.pageNoList.add(new Integer(i+1));
			 if(((nextCounter+1)*10)-1==i){
				 break;
			 }
		 }
		 setPreviousButton(true);
		 if(pageResult.getNumberOfPagesAvailable()<=((nextCounter+1)*10)){
			 setNextButton(false);
		 }else{
			 setNextButton(true);
		 }
		 return SUCCESS;
	    }


	 public String getInventoriesForPreviousButton() throws IOException {
		 setListCriteria();
		 nextCounter--;
		 previousCounter--;
		 getListCriteria().getPageSpecification().setPageNumber((previousCounter*10));
		 setPageNo(previousCounter*10);
		 PageResult<InventoryItem> pageResult =null;
		 if(ClaimType.CAMPAIGN.getType().equals(claimType)||
				 ClaimType.FIELD_MODIFICATION.getType().equals(claimType)){
			 pageResult= this.inventoryService.findAllInventoryItemsForCampaignMultiClaim
			 (multiCarSearch, dealerId, getListCriteria(), campaignCode.toUpperCase());
		 }else if(ClaimType.MACHINE.getType().equals(claimType)){
			 pageResult= this.inventoryService.findAllInventoryItemsForMultiClaim
			 (this.multiCarSearch,this.dealerId,getListCriteria());
		 }
		 setInventoryItems(pageResult.getResult());
		 setTotalpages(pageResult.getNumberOfPagesAvailable());
		 for (int i=previousCounter*10;i<pageResult.getNumberOfPagesAvailable();i++){
			 this.pageNoList.add(new Integer(i+1));
			 if(((previousCounter+1)*10)-1==i){
				 break;
			 }
		 }
		 setNextButton(true);
		 if(previousCounter<=0){
			 setPreviousButton(false);
		 }else{
			 setPreviousButton(true);
		 }
		 return SUCCESS;
	 }

	 public List<InventoryItem> getSelectedInventoryItems(){
		 if(selectedItemsIds !=null){
			 this.selectedItemsIds=this.selectedItemsIds.replaceAll(" ","");
		 }
		 String[] idStrArray =null;
		 if(selectedItemsIds !=null){
			 idStrArray = this.selectedItemsIds.split(",");
		 }		 
		 List<InventoryItem> selectedItems=this.inventoryService.findInventoryItemsForIds(getLongIdArray(idStrArray));
		 return selectedItems;
	 }

	private List<Long> getLongIdArray(String[]idStrArray){
		List<Long> idList = null;
		if(idStrArray != null){
			idList = new ArrayList<Long>();
			for (int i=0; i< idStrArray.length; i++) {
				try{
					Long id = Long.parseLong(idStrArray[i]);
					idList.add(id);
				}catch (NumberFormatException e) {
					//TODO: log error
				}
				
			}
		}
		
		return idList;		
	}
	 
	public InventoryService getInventoryService() {
		return inventoryService;
	}

	protected void selectedMultipleInventories(List<InventoryItem> items,Claim claim) {
        List<ClaimedItem> claimedItems = new ArrayList<ClaimedItem>();
        for (InventoryItem item : items) {
        	ClaimedItem claimedItem= new ClaimedItem();
        	ItemReference reference = new ItemReference();
        	reference.setReferredInventoryItem(item);
        	claimedItem.setItemReference(reference);
        	claimedItems.add(claimedItem);
		}
        claim.setClaimedItems(claimedItems);
    }

	public String getCampaignCode() {
		return campaignCode;
	}

	public void setCampaignCode(String campaignCode) {
		this.campaignCode = campaignCode;
	}

	public DealershipRepository getDealershipRepository() {
		return dealershipRepository;
	}

	public void setDealershipRepository(DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}

	public int getPreviousCounter() {
		return previousCounter;
	}

	public void setPreviousCounter(int previousCounter) {
		this.previousCounter = previousCounter;
	}

	public int getNextCounter() {
		return nextCounter;
	}

	public void setNextCounter(int nextCounter) {
		this.nextCounter = nextCounter;
	}

	public boolean isNextButton() {
		return nextButton;
	}

	public void setNextButton(boolean nextButton) {
		this.nextButton = nextButton;
	}

	public boolean isPreviousButton() {
		return previousButton;
	}

	public void setPreviousButton(boolean previousButton) {
		this.previousButton = previousButton;
	}

	public int getTotalpages() {
		return totalpages;
	}

	public void setTotalpages(int totalpages) {
		this.totalpages = totalpages;
	}

	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit;
	}

	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}

    public boolean isMultiSerialPerClaimAllowedFlag(){
		Boolean isMultiSerialPerClaimAllowed = this.configParamService.getBooleanValue("isMultipleSerialsPerClaimAllowed");
		if(isMultiSerialPerClaimAllowed !=null && isMultiSerialPerClaimAllowed.booleanValue()){
			return true;
		} else{
		  return false;
		}
	}

    public String getOEMPartCrossRefForDisplay(Item item, Item oemDealerItem,
            boolean isNumber, Organization dealership) {
        Organization loggedInUserOrganization = getLoggedInUser().getBelongsToOrganization();
        return catalogService.findOEMPartCrossRefForDisplay(item,oemDealerItem,
                                    isNumber, dealership, loggedInUserOrganization, Dealership.class);
    }

    public boolean getDisplayCPFlagOnClaimPgOne() {
		return configParamService.getBooleanValue(ConfigName.COMMERCIAL_POLICY_CLAIM_PAGE.getName());
	}

    public boolean isNonSerializedClaimAllowed(){
        return configParamService.getBooleanValue(ConfigName.NON_SERIALIZED_CLAIM_ALLOWED.getName());
    }

    public boolean isInvoiceNumberApplicable(){
        return configParamService.getBooleanValue(ConfigName.INVOICE_NUMBER_APPLICABLE.getName());
    }

    public boolean isPartsClaimWithoutHostAllowed() {
        return configParamService.getBooleanValue(ConfigName.PARTS_CLAIM_WITHOUT_HOST_ALLOWED.getName());
    }

	public boolean isDateCodeEnabled() {
		return this.configParamService
				.getBooleanValue(ConfigName.IS_DATE_CODE_ENABLED.getName());
	}

	public void prepareAttributesForClaim(Claim claim, boolean isSerialized) {
		claimSubmissionUtil.prepareAttributesForClaim(claim, isSerialized);
	}

    public CatalogService getCatalogService() {
        return catalogService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }
    
         
    //Validating the authorization number if authorization check box is checked.
    protected void checkAuthNumber(Claim claim){
    	if( claim!= null && claim.isCmsAuthCheck()==true && !org.springframework.util.StringUtils.hasText(claim.getAuthNumber())){
		    addActionError("error.newClaim.authNumberRequired");
		}
	}
    
    public Boolean isDealerAllowedToFileNCRWith30Days(){
    	ServiceProvider loggedInDealer = getLoggedInUsersDealership();
    	if(loggedInDealer.isAllowedNCRWith30Days()){
    		return true;
    	}
    	return false;
    }
    
    public boolean isDealerEligibleToFillSmrClaim(){
        	 boolean isEligible = false;
        Map<String, List<Object>> buValues = getConfigParamService().
                getValuesForAllBUs(ConfigName.SMR_CLAIM_ALLOWED.getName());
        for (String buName : buValues.keySet()) {
              Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
              if(booleanValue){
                 isEligible=true;
                 break;
              }
        }
        return isEligible;
    } 
    
    /**
     * This method is to validate the dealer marketing group code, truck marketing group code, warranty type
     * and claim type against the data present in MarketingGroupsLookup table. If the combination exist, then
     * claim can be filed.Otherwise, error/warning messages are thrown
     * @param claim
     */
    public void validateMarketingGroupCodes(Claim claim){
    	List<InventoryItem> inventoryItemsList = null;
		try {
			inventoryItemsList = getInventoryService().findItemBySerialNumber(claim.getSerialNumber());
		} catch (ItemNotFoundException e) {
			this.logger.error("No inventory item exists for the given serial number [" + claim.getSerialNumber() + "]");
		}
    	InventoryItem inventoryItem = null;
		if(!inventoryItemsList.isEmpty() )
			inventoryItem = inventoryItemsList.get(0);
		MarketingGroupsLookup lookup = new MarketingGroupsLookup();
		lookup.setClaimType(claim.getType().getType());
		lookup.setTruckMktgGroupCode(inventoryItem.getMarketingGroupCode());
		if(!claim.getNcrClaimCheck()){
			getClaimSubmissionUtil().setPolicyForClaim(claim);
		}
		if(claim.isOfType(ClaimType.MACHINE) && inventoryItem.getType().equals(InventoryType.RETAIL))
		lookup.setWarrantyType(claim.getApplicablePolicy()!=null?claim.getApplicablePolicy().getWarrantyType().getType():WarrantyType.POLICY.getType());
		
		if(claim.isOfType(ClaimType.CAMPAIGN)){
			lookup.setWarrantyType(FPI_WARRANTY_TYPE);
		}
		
		if(inventoryItem.getType().equals(InventoryType.STOCK) && !claim.isOfType(ClaimType.CAMPAIGN) ){
			lookup.setWarrantyType(WarrantyType.STANDARD.getType());
		}
		lookup.setDealerMktgGroupCode(new HibernateCast<Dealership>()
				.cast(claim.getForDealer()).getMarketingGroup());
		
		List<MarketingGroupsLookup> lookUpResults = getClaimService().lookUpMktgGroupCodes(lookup,false);
		StringBuilder dealerCode = new StringBuilder();
		boolean isDealerMarketingGroupExist = false;
		if(null != lookUpResults && !lookUpResults.isEmpty()){
			for (MarketingGroupsLookup lookUpResult : lookUpResults) {
				if (lookup.getDealerMktgGroupCode().equals(
						lookUpResult.getDealerMktgGroupCode())){
					isDealerMarketingGroupExist = true;
					break;
				}
			}	 
			if(!isDealerMarketingGroupExist)
				{	
				if(!isLoggedInUserAnInternalUser()){
				List<Organization> orgs = getLoggedInUser().getBelongsToOrganizations();
				boolean isMGForDealerOrgsExist = false;
				if(null !=orgs && orgs.size()>1 && inventoryItem.getType().equals(InventoryType.RETAIL)){
					for(MarketingGroupsLookup lookUpResult : lookUpResults){
						for(Organization org : orgs){
								if (InstanceOfUtil
										.isInstanceOfClass(Dealership.class, org) && isCreateClaimAllowed(inventoryItem)) {
									Dealership dealer = (Dealership) org;
									if(null !=dealer && null!=org.getName()){
										if(dealer.getMarketingGroup().equals(lookUpResult.getDealerMktgGroupCode())){
											isMGForDealerOrgsExist = true;
											String brand = orgService
													.findMarketingGroupCodeBrandByDealership(dealer);
											dealerCode.append("{"+org.getName() + "-" + brand+"},");
									}
									}
								}
						}
					}
				}	
					if (isMGForDealerOrgsExist){
						String[] message = {dealerCode.toString(),inventoryItem.getSerialNumber()};
						addActionError(
								"error.claim.marketingGroupCodeLookupForDealer",
								message);
					}
					else
						addActionError("error.claim.mktgGroupCodeLookupNoResultForDealer");
				}
					 if(isLoggedInUserAnInternalUser() && getLoggedInUser().hasRole(Role.PROCESSOR)){
						 if(!claim.isCmsAuthCheck())
							 addActionError("error.claim.marketingGroupCodeLookupForInternalUser", lookup.getDealerMktgGroupCode());
						 else
							 addActionWarning("error.claim.marketingGroupCodeLookup", lookup.getDealerMktgGroupCode()); 
					 }
						
					}
		}
		else
		{
			if(!isLoggedInUserAnInternalUser())
				addActionError("error.claim.mktgGroupCodeLookupNoResultForDealer");
			if(isLoggedInUserAnInternalUser() && getLoggedInUser().hasRole(Role.PROCESSOR)){
				if(!claim.isCmsAuthCheck())
					addActionError("error.claim.mktgGroupCodeLookupNoResultForDealer");
				else
					addActionWarning("error.claim.mktgGroupCodeLookupNoResultForDealer");
			}
		}
    }
    
    /**
     * This method is to validate the dealer marketing group code with BU configured allowed dealer
     * marketing group codes.
     */
	public void validateMktgGrpCodesForNonSerializedClaims(Claim claim) {
		String delaerMktgGrpCode = new HibernateCast<Dealership>().cast(
				claim.getForDealer()).getMarketingGroup();
		boolean allowedMktgGrpCode = false;
		String allowedDealerMktgGroup = getConfigParamService().getStringValue(
				ConfigName.ALLOWED_MKTG_GRP_CODES_NON_SERIALIZED_CLAIMS
						.getName());
		String mktgGrps[] = allowedDealerMktgGroup.split(",");
		for (String mktgGrpCode : mktgGrps) {
			if (delaerMktgGrpCode.equals(mktgGrpCode)) {
				allowedMktgGrpCode = true;
				break;
			}

		}
		if (!allowedMktgGrpCode) {
			if(!isLoggedInUserAnInternalUser()){
				List<Organization> orgs = getLoggedInUser().getBelongsToOrganizations();
			boolean isMGForDealerOrgsExist = false;
			StringBuilder dealerCode = new StringBuilder();
			if(null !=orgs && orgs.size()>1){
				for (String mktgGrpCode : mktgGrps) {
					for(Organization org : orgs){
							if (InstanceOfUtil
									.isInstanceOfClass(Dealership.class, org)) {
								Dealership dealer = (Dealership) org;
								if(null !=dealer && null!=org.getName()){
									if(dealer.getMarketingGroup().equals(mktgGrpCode)){
										isMGForDealerOrgsExist = true;
										String brand = orgService
												.findMarketingGroupCodeBrandByDealership(dealer);
										dealerCode.append("{"+org.getName() + "-" + brand+"},");
								}
								}
							}
					}
				}
			}	if(isMGForDealerOrgsExist)
				addActionError("error.claim.marketingGroupCodeLookupForDealer.nonserializedPartsClaim",dealerCode);
			else
				addActionError("error.claim.mktgGroupCodeLookupNoResultForDealer");
			}	
			if (isLoggedInUserAnInternalUser() && getLoggedInUser().hasRole(Role.PROCESSOR)){
				if(!claim.isCmsAuthCheck())
					addActionError("error.claim.marketingGroupCodeLookupForInternalUser",
							delaerMktgGrpCode);
				else
					addActionWarning("error.claim.marketingGroupCodeLookup",
							delaerMktgGrpCode);
			}
		}		
	}
	
	public boolean enableComponentDateCode() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ENABLE_COMPONENT_DATE_CODE.getName());
	}
	
	public Boolean isCreateClaimAllowed(InventoryItem item){
		 boolean canSearchOtherDealersRetail  = configParamService
	                .getBooleanValue(ConfigName.CAN_DEALER_SEARCH_OTHER_DEALERS_RETAIL.getName());
		 boolean createClaim = false;
		if (item.getType().equals(InventoryType.RETAIL)
				&& canSearchOtherDealersRetail && !isLoggedInUserAnInternalUser()) {
			String loggedInUserBrand = new HibernateCast<Dealership>()
					.cast(getLoggedInUsersDealership()).getBrand();
			if (isLoggedInUserDualBrandDealer() || item.getBrandType().equals(BrandType.UTILEV.getType())
					|| item.getBrandType().equals(loggedInUserBrand)) {
				createClaim = true;
			}else
				createClaim = false;
		}
		return createClaim;
	}
	
	public List<InventoryClass> getAllowed30DayNcrClasses() {
    	
    	ServiceProvider mLoggedInDealer = getLoggedInUsersDealership();
    	
    	List<InvClassDealerMapping> mDealerEligibleClassMappings = 
    			invClassDlrMappingService.findInvClassDealerMappings(mLoggedInDealer);
    	
        List<InventoryClass> mCurrentAllowed30DayNcrClasses = new ArrayList<InventoryClass>();
        
        for (InvClassDealerMapping icdm : mDealerEligibleClassMappings) {
        	mCurrentAllowed30DayNcrClasses.add(icdm.getInventoryClass());
        }
        Collections.sort(mCurrentAllowed30DayNcrClasses);
        
        return mCurrentAllowed30DayNcrClasses;
    }
    
    public List<InventoryClass> getAll30DayNcrClasses() {
    	List<InventoryClass> mAllClasses = inventoryClassService.findAll();
    	Collections.sort(mAllClasses);
    	return mAllClasses;
    }
    
    public void setInvClassDlrMappingService(InvClassDealerMappingService invClassDlrMappingService) {
		this.invClassDlrMappingService = invClassDlrMappingService;
	}
	
	public InvClassDealerMappingService getInvClassDlrMappingService() {
		return invClassDlrMappingService;
	}
	
	public void setInventoryClassService(InventoryClassService inventoryClassService) {
		this.inventoryClassService = inventoryClassService;
	}
	
	public InventoryClassService getInventoryClassService() {
		return inventoryClassService;
	}

}
