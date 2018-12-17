package tavant.twms.web.claim.create;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.validation.ValidationResults;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.inventory.InvClassDealerMapping;
import tavant.twms.domain.inventory.InvClassDealerMappingService;
import tavant.twms.domain.inventory.InventoryClass;
import tavant.twms.domain.inventory.InventoryClassService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.xforms.TaskView;
import tavant.twms.web.xforms.TaskViewService;

import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class NewClaimAction extends I18nActionSupport implements Preparable , TWMSWebConstants{
	
	public static final String THIRD_PARTY_MULTI_CAMPAIGN = "thirdpartymultiCampaign";

	public static final String THIRD_PARTY_CLAIM_TYPE = "thirdpartyclaimtype";
	
	public static final String COMPETITOR_MODEL = "ClaimCompetitorModel";

    private TaskViewService taskViewService;

	private ClaimService claimService;

    private String claimType;

    private InventoryItem inventoryItem;

    private TaskView task;

    private Long id;

    private ValidationResults messages;

    private ServiceProvider dealer;

    private Campaign campaign;

    private Claim claim;

    private List<ClaimType> claimTypes = new ArrayList<ClaimType>();    
    
    private List<ListOfValues> competitorModels = new ArrayList<ListOfValues>();

    private String fromPendingCampaign;

    private CampaignNotification campaignNotification;

    private boolean multiSerialPerClaimAllowedFlag;   

   	private String selectedBusinessUnit = null;

    private String thirdParty;

    private String forSerialized;
    
    private String partHostedOnMachine;
    
    private String thirdPartyName;
    
    private String isMultiClaimMaintenance;
    
    private String isTransferOrReProcess;
    
    protected String context; 
    
	private LovRepository lovRepository;
	
	private String partInstalledOn;
	
	private InvClassDealerMappingService invClassDlrMappingService;
	private InventoryClassService inventoryClassService;

	public LovRepository getLovRepository() {
		return lovRepository;
	}	

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}
    public void prepare() throws Exception {    	
    	
    	setCompetitorModelsForDialog();
        if (this.id != null) {
            this.task = this.taskViewService.getTaskView(this.id);
            this.claim = this.task.getClaim();
        }
        if(!StringUtils.hasText(this.selectedBusinessUnit) && getCurrentBusinessUnit() != null)
    	{
        	this.selectedBusinessUnit = getCurrentBusinessUnit().getName();
    	}
        if(SelectedBusinessUnitsHolder.getSelectedBusinessUnit() == null) {
        	SelectedBusinessUnitsHolder.setSelectedBusinessUnit(selectedBusinessUnit);
        }
    }

    public Claim getClaim() {
        return this.claim;
    }

    public boolean toBeChecked(String partInstalledOn) {
		if (partInstalledOn.equals(this.partInstalledOn)) {
			return true;
		} else {
			return false;
		}
	}
    
    public void setCompetitorModelsForDialog()
    {
        this.competitorModels=this.lovRepository.findAllActive(COMPETITOR_MODEL);
    }
    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public String showForm() {
    	SortedSet<BusinessUnit> businessUnits = getSecurityHelper().getLoggedInUser().getBusinessUnits();
      	if(businessUnits != null && businessUnits.size() > 1 && (this.claim == null || this.claim.getBusinessUnitInfo() == null ))
    	{
    		//multi line dealer
    		return INPUT;
    	}else if(StringUtils.hasText(getIsMultiClaimMaintenance()) || 
    				StringUtils.hasText(getIsTransferOrReProcess())){
    		return "multiclaim";
		}
    	else
    	{
    		return SUCCESS;
    	}
    }



    public String showInvoiceUploadForm() {
        return SUCCESS;
    }

    /**
     * Since we have a different set of JSPs for Third party implementation; we want the returned jsps to be
     * different from usual JSPs. Hence a new method to actually return the success strings. This will help
     * in choosing only Third party JSP explicitly for respective actions.
     *
     * @return a string which will help in identifying which third party JSP should be used to display to user.
     * TODO : This method needs a revamping before it can be marked complete
     */
    public String chooseThirdPartyClaimType()
    {
    	//I'd like to control the claim types being returned for 3rd party so let me
    	//define my own and capture value for it; then I can do my custom filtering.
    	String claimTypeToReturn = null;

    	//TODO a line to make a temp code fix; remove it later else it won't work properly
    	 if (this.claim != null) {
             this.claimType = this.claim.getType().toString();
         }

    	claimTypeToReturn = chooseClaimTypeAndDealer();
    	for(int i = 0; i < claimTypes.size() ; i++) {
    		if((claimTypes.get(i)).getType().contains("Parts")) {
    			claimTypes.remove(i);
    		}
    	}
    	if ("Machine".equalsIgnoreCase(claimTypeToReturn))
    	{
    		return "Machine";
    	}

    	if("Campaign".equalsIgnoreCase(claimTypeToReturn)) {
    		return "Campaign";
    	}
    	return null;
    }

    public boolean displayBrandDropDownForPartsClaim(){
        return getConfigParamService().getBooleanValue(ConfigName.DISPLAY_BRAND_DROP_DOWN.getName());
    }

    public String chooseClaimTypeAndDealer() {
    	
        if (this.claim != null) {
            this.claimType = this.claim.getType().toString();
        }
        setClaimTypes();
        populateMultiSerialPerClaimSubmissionAllowedFlag();
        if (claim!=null && claim.getId() != null) {
            if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)) {
                PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
                if (!partsClaim.getPartInstalled()||(partsClaim.getPartInstalled() && (claim.getCompetitorModelBrand()!=null && !claim.getCompetitorModelBrand().isEmpty() && !claim.getCompetitorModelDescription().isEmpty() && !claim.getCompetitorModelTruckSerialnumber().isEmpty()))) {
                    return PARTS;
                }
            }
        }
        if (PARTS.equals(this.claimType) && FALSE.equals(this.forSerialized)) {
        	if(FALSE.equals(partHostedOnMachine))
        		return PARTS;
        	else
        		return PARTS_NON_SERIALIZED;
        }else if (PARTS.equals(this.claimType)) {
            return PARTS;
        }else if (CAMPAIGN.equals(this.claimType)) {
        	if( claim!=null && claim.getForMultipleItems()){
            	return MULTI_CAMPAIGN;
            }
            return CAMPAIGN;
        }else if (MACHINE.equals(this.claimType) && FALSE.equals(this.forSerialized)) {
            if(displayBrandDropDownForPartsClaim()){
                return NON_SERIALIZED;
            }else{
                return NON_SERIALIZED_WITHOUT_BRAND;
            }
        }
        else if (MACHINE.equals(this.claimType)) {
            return MACHINE;
        }else if (ATTACHMENT.equals(this.claimType) && FALSE.equals(this.forSerialized)) {
            return ATTACHMENT_NON_SERIALIZED;
        } else if (ATTACHMENT.equals(this.claimType)) {
            return ATTACHMENT;
        }

        String type = ((ClaimType) claimTypes.get(0)).getType();

        //When page loads for the first time, set the value of claim type from first value of BU List.
        if (!StringUtils.hasText(claimType)) {
            this.claimType = type;
        }

        return type;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setTaskViewService(TaskViewService taskViewService) {
        this.taskViewService = taskViewService;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskView getTask() {
        return this.task;
    }

    public void setTask(TaskView task) {
        // HACK: do nothing; Ognl complains that it cant set the task property
        // back onto
        // the action because the field is private (in the absence of this
        // method, it tries
        // to do a direct field access). But we use the 'id' property in the
        // prepare() method
        // and populate the task - we don't need it being set from the form.
        // Thus adding this
        // NOOP method
    }

    public ValidationResults getMessages() {
        return this.messages;
    }

    public void setDealer(ServiceProvider dealer) {
        this.dealer = dealer;
    }

    public ServiceProvider getDealer() {
        return this.dealer;
    }

    public List<String> getInventoryTypes(){
		List<String> inventoryTypes = new ArrayList<String>();
		inventoryTypes.add(InventoryType.STOCK.getType());
		inventoryTypes.add(InventoryType.RETAIL.getType());
		return inventoryTypes;
	}

	public List<ClaimType> getClaimTypes() {
		return claimTypes;
	}

	public void setClaimTypes(List<ClaimType> claimTypes) {
		this.claimTypes = claimTypes;
	}

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public void populateMultiSerialPerClaimSubmissionAllowedFlag(){
		Boolean isMultiSerialPerClaimAllowed = getConfigParamService().getBooleanValue("isMultipleSerialsPerClaimAllowed");
		if(isMultiSerialPerClaimAllowed !=null){
			this.multiSerialPerClaimAllowedFlag = isMultiSerialPerClaimAllowed.booleanValue();
		} else{
		  this.multiSerialPerClaimAllowedFlag = false;
		}
	}

   public String redirectToClaimType(){
	   	if(inventoryItem == null){
	   		addActionError("error.newClaim.invalidInventory");
	   		return ERROR;
	   	}
        if(inventoryItem.getOfType().getItemType().equalsIgnoreCase(MACHINE)){
           this.claimType = MACHINE;
            return MACHINE;
        }
        else if(inventoryItem.getOfType().getItemType().equalsIgnoreCase(PARTS)){
            this.claimType = PARTS;
            return PARTS;
        }
        else if(inventoryItem.getOfType().getItemType().equalsIgnoreCase(ATTACHMENT)){
            this.claimType = ATTACHMENT;
            return ATTACHMENT;
        }
       return MACHINE;
    }

	public void setClaimTypes() {
		List<ClaimType> tempClaimTypes = new ArrayList<ClaimType>();
		tempClaimTypes =  this.claimService.fetchAllClaimTypesForBusinessUnit();
		this.claimTypes =tempClaimTypes;
		if(this.claimType!=null && this.claimTypes.size()>0){
			claimTypes=new ArrayList<ClaimType>();
			claimTypes.add(ClaimType.getUIDisplayName(this.claimType));
			for (ClaimType claimType : tempClaimTypes) {
				if(!claimType.getType().equals(this.claimType)){
					claimTypes.add(ClaimType.getUIDisplayName(claimType.getType()));
				}
			}
		}
	}


	public String chooseClaimServiceProviderType()
	{
		String claimServiceProviderType = "dealer";

		if(StringUtils.hasText(getThirdParty()))
		{
			claimServiceProviderType = "thirdparty";
		}
		
		if(StringUtils.hasText(getIsMultiClaimMaintenance()) || 
				StringUtils.hasText(getIsTransferOrReProcess())){
			return "multiclaim";
		}
		
		return claimServiceProviderType;
	}


	public String getFromPendingCampaign() {
		return fromPendingCampaign;
	}

	public void setFromPendingCampaign(String fromPendingCampaign) {
		this.fromPendingCampaign = fromPendingCampaign;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public CampaignNotification getCampaignNotification() {
		return campaignNotification;
	}

	public void setCampaignNotification(CampaignNotification campaignNotification) {
		this.campaignNotification = campaignNotification;
	}

	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit;
	}

	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}

	public boolean getMultiSerialPerClaimAllowedFlag() {
      return this.multiSerialPerClaimAllowedFlag;
	}

	public void setMultiSerialPerClaimAllowedFlag(
			boolean multiSerialPerClaimAllowedFlag) {
		this.multiSerialPerClaimAllowedFlag = multiSerialPerClaimAllowedFlag;
	}
	
	public boolean getDisplayCPFlagOnClaimPgOne() {
		return getConfigParamService().getBooleanValue(ConfigName.COMMERCIAL_POLICY_CLAIM_PAGE.getName());
	}

    public String getForSerialized() {
        return forSerialized;
    }

    public void setForSerialized(String forSerialized) {
        this.forSerialized = forSerialized;
    }

    public boolean isInvoiceNumberApplicable(){
        return getConfigParamService().getBooleanValue(ConfigName.INVOICE_NUMBER_APPLICABLE.getName());
    }

    public boolean isNonSerializedClaimAllowed(){
        return getConfigParamService().getBooleanValue(ConfigName.NON_SERIALIZED_CLAIM_ALLOWED.getName());
    }
    
    public boolean isItemNumberDisplayRequired() {
		 return getConfigParamService().getBooleanValue(ConfigName.IS_ITEM_NUMBER_DISPLAY_REQUIRED
					.getName());
		}

	public boolean isDateCodeEnabled() {
		return getConfigParamService().getBooleanValue(ConfigName.IS_DATE_CODE_ENABLED.getName());
	}
	
    public boolean isPartsClaimWithoutHostAllowed() {
        return getConfigParamService().getBooleanValue(ConfigName.PARTS_CLAIM_WITHOUT_HOST_ALLOWED.getName());
    }
    
    public String getThirdParty()
	{
		return thirdParty;
	}

	public void setThirdParty(String thirdParty)
	{
		this.thirdParty = thirdParty;
	}

	public String getThirdPartyName() 
	{
		return thirdPartyName;
	}

	public void setThirdPartyName(String thirdPartyName) 
	{
		this.thirdPartyName = thirdPartyName;
	}

	public String getIsMultiClaimMaintenance() {
		return isMultiClaimMaintenance;
	}

	public void setIsMultiClaimMaintenance(String isMultiClaimMaintenance) {
		this.isMultiClaimMaintenance = isMultiClaimMaintenance;
	}

	public String getIsTransferOrReProcess() {
		return isTransferOrReProcess;
	}

	public void setIsTransferOrReProcess(String isTransferOrReProcess) {
		this.isTransferOrReProcess = isTransferOrReProcess;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getPartHostedOnMachine() {
		return partHostedOnMachine;
	}

	public void setPartHostedOnMachine(String partHostedOnMachine) {
		this.partHostedOnMachine = partHostedOnMachine;
	}

	public void setCompetitorModels(List<ListOfValues> competitorModels) {
		this.competitorModels = competitorModels;
	}

	public List<ListOfValues> getCompetitorModels() {
		return competitorModels;
	}
	
	public String getPartInstalledOn() {
		return partInstalledOn;
	}

	public void setPartInstalledOn(String partInstalledOn) {
		this.partInstalledOn = partInstalledOn;		
	}

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }
    
    public Boolean isDealerAllowedToFileNCRWith30Days(){
    	ServiceProvider loggedInDealer = getLoggedInUsersDealership();
    	if(loggedInDealer.isAllowedNCRWith30Days()){
    		return true;
    	}
    	return false;
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
public boolean isTechnicianEnable(){
    boolean isEligible = false;
    Map<String, List<Object>> buValues = getConfigParamService().
            getValuesForAllBUs(ConfigName.ENABLE_TECHNICIAN.getName());
    for (String buName : buValues.keySet()) {
          Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
          if(booleanValue){
             isEligible=true;
             break;
          }
    }
    return isEligible;
}

public boolean displayNCRandBT30DayNCROnClaimPage(){
	return getConfigParamService().getBooleanValue(ConfigName.DISPLAY_NCR_AND_BT_30DAY_NCR_ON_CLAIM_PAGE.getName());
}

	public boolean displayEmissionOnClaimPage() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ENABLE_EMISSION.getName());
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
