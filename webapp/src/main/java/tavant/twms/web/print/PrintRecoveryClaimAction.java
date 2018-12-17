package tavant.twms.web.print;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.supplier.CostLineItem;
import tavant.twms.infra.HibernateCast;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.worklist.WorkListService;

import com.domainlanguage.money.Money;
import tavant.twms.domain.orgmodel.Address;

@SuppressWarnings("serial")
public class PrintRecoveryClaimAction extends PrintAction {
	private PrintRecoveryClaimObject printRecoveryClaimObject = new PrintRecoveryClaimObject();
	private Long recoveryClaimId;
	private Claim claim;
	private RecoveryClaim recoveryClaim;
	private RecoveryClaimService recoveryClaimService;
	private ConfigParamService configParamService;
	private List<HussmannPartReplacedInstalledDTO> hussmannPartReplacedInstalledDTOList
	= new ArrayList<HussmannPartReplacedInstalledDTO>();
	private Map<String, List<Object>> configParamValueForAllBus;
	private WorkListService workListService;
	private String selectedBU;
	private boolean supplerRole;
	private boolean supplierView;
	private List<MiscellaneousPartsObject> miscellaneousPartsObjectList = new ArrayList<MiscellaneousPartsObject>();

	public PrintRecoveryClaimObject getPrintRecoveryClaimObject() {
		return printRecoveryClaimObject;
	}

	public void setPrintRecoveryClaimObject(
			PrintRecoveryClaimObject printRecoveryClaimObject) {
		this.printRecoveryClaimObject = printRecoveryClaimObject;
	}


	public Long getRecoveryClaimId() {
		return recoveryClaimId;
	}

	public void setRecoveryClaimId(Long recoveryClaimId) {
		this.recoveryClaimId = recoveryClaimId;
	}

	public RecoveryClaim getRecoveryClaim() {
		return recoveryClaim;
	}

	public void setRecoveryClaim(RecoveryClaim recoveryClaim) {
		this.recoveryClaim = recoveryClaim;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public RecoveryClaimService getRecoveryClaimService() {
		return recoveryClaimService;
	}

	public void setRecoveryClaimService(RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}
	
	 public List<HussmannPartReplacedInstalledDTO> getHussmannPartReplacedInstalledDTOList() {
		return hussmannPartReplacedInstalledDTOList;
	}

	public void setHussmannPartReplacedInstalledDTOList(
			List<HussmannPartReplacedInstalledDTO> hussmannPartReplacedInstalledDTOList) {
		this.hussmannPartReplacedInstalledDTOList = hussmannPartReplacedInstalledDTOList;
	}
	
	public Map<String, List<Object>> getConfigParamValueForAllBus() {
		return configParamValueForAllBus;
	}

	public void setConfigParamValueForAllBus(
			Map<String, List<Object>> configParamValueForAllBus) {
		this.configParamValueForAllBus = configParamValueForAllBus;
	}

	public WorkListService getWorkListService() {
		return workListService;
	}

	public void setWorkListService(WorkListService workListService) {
		this.workListService = workListService;
	}
	
	public String printRecoveryClaim() {
		
		if (recoveryClaim == null) {
			recoveryClaim = recoveryClaimService.findRecoveryClaim(recoveryClaimId);

			recoveryClaim.setCurrentAssignee(fetchCurrentClaimAssignee(recoveryClaimId));
			if(null!=recoveryClaim){
				claim=recoveryClaim.getClaim();
				if(claim.getRecoveryClaims()!=null){
					for(RecoveryClaim recoveryClaim : claim.getRecoveryClaims()){
				        populateRecoveryClaimAssignee(recoveryClaim);
					}
				}
				printRecoveryClaimObject.setRecoveryClaim(recoveryClaim);
				printRecoveryClaimObject.setClaim(claim);
			}
		} else {
			printRecoveryClaimObject.setRecoveryClaim(recoveryClaim);
		}
		SelectedBusinessUnitsHolder
				.setSelectedBusinessUnit(printRecoveryClaimObject.getClaim()
						.getBusinessUnitInfo().getName());
		populateLoginUser(getLoggedInUser());
		supplerRole=getLoggedInUser().hasRole(Role.SUPPLIER);
		printRecoveryClaimObject.setHasSupplierRole(supplerRole);
		selectedBU = SelectedBusinessUnitsHolder
		.getSelectedBusinessUnit();
		String claimBusinessUnitName = claim.getBusinessUnitInfo()!=null ? claim.getBusinessUnitInfo().getName() : null;
		printRecoveryClaimObject.setSupplierView(supplierView);
		if(isBuConfigAMER()){
			printRecoveryClaimObject.setDatePatternForLoggedInUser(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN);
		}else{
			printRecoveryClaimObject.setDatePatternForLoggedInUser(TWMSDateFormatUtil.getDateFormatForLoggedInUser());
		}
		if(isBuConfigAMER()){
			printRecoveryClaimObject.setGifName("logo_NMHG.png");
		}else{
		printRecoveryClaimObject.setGifName("NMHG_Header.png");
		}
		if(!isBuConfigAMER()){
		printRecoveryClaimObject.setNmhgFooterGif("NMHG_Footer.png");
		}
		printRecoveryClaimObject.setBuTitle(selectedBU);
		printRecoveryClaimObject.setDealerOrSp((selectedBU.equalsIgnoreCase("EMEA")||(selectedBU.equalsIgnoreCase("AMER")) ? "Service Provider:" : "Dealer:"));
		printRecoveryClaimObject.setDisplayCPFlagOnClaimPgOne(configParamService.getBooleanValue(ConfigName.COMMERCIAL_POLICY_CLAIM_PAGE.getName()));
		printRecoveryClaimObject.setDealerJobNumberEnabled(configParamService.getBooleanValue(ConfigName.SHOW_DEALER_JOB_NUMBER.getName()));
		if(!claim.getType().getType().equalsIgnoreCase("CAMPAIGN")){
			if(isBuConfigAMER()){
				printRecoveryClaimObject.setFormatedFailureDate(printRecoveryClaimObject.getClaim().getFailureDate().toString(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN));
			}else{
			printRecoveryClaimObject.setFormatedFailureDate(printRecoveryClaimObject.getClaim().getFailureDate().toString(printRecoveryClaimObject.getDatePatternForLoggedInUser()));
			}
		}
		if(recoveryClaim!=null && recoveryClaim.getD().getCreatedOn()!=null){
			if(isBuConfigAMER()){
				printRecoveryClaimObject.setFormattedRecoveryClaimDate(recoveryClaim.getD().getCreatedOn().toString(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN));
			}else{
			printRecoveryClaimObject.setFormattedRecoveryClaimDate(recoveryClaim.getD().getCreatedOn().toString(printRecoveryClaimObject.getDatePatternForLoggedInUser()));
			}
		}
		printRecoveryClaimObject.setFormatedRepairStartDate(printRecoveryClaimObject.getClaim().getRepairStartDate().toString(printRecoveryClaimObject.getDatePatternForLoggedInUser()));
		printRecoveryClaimObject.setFormatedRepairDate(printRecoveryClaimObject.getClaim().getRepairDate().toString(printRecoveryClaimObject.getDatePatternForLoggedInUser()));
		printRecoveryClaimObject.setFormatedfiledOnDate (printRecoveryClaimObject.getClaim().getFiledOnDate().toString(printRecoveryClaimObject.getDatePatternForLoggedInUser()));
		if(printRecoveryClaimObject.getClaim().getInstallationDate() != null){
			if(isBuConfigAMER()){
				printRecoveryClaimObject.setFormatedInstallationDate(printRecoveryClaimObject.getClaim().getInstallationDate().toString(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN));
			}else
			{
				printRecoveryClaimObject.setFormatedInstallationDate(printRecoveryClaimObject.getClaim().getInstallationDate().toString(printRecoveryClaimObject.getDatePatternForLoggedInUser()));
			}
		}
		if(printRecoveryClaimObject.getClaim().getPurchaseDate() != null){
			if(isBuConfigAMER()){
				printRecoveryClaimObject.setFormatedPurchaseDate(printRecoveryClaimObject.getClaim().getPurchaseDate().toString(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN));
			}else{
				printRecoveryClaimObject.setFormatedPurchaseDate(printRecoveryClaimObject.getClaim().getPurchaseDate().toString(printRecoveryClaimObject.getDatePatternForLoggedInUser()));
			}
		
		}
		populateEndCustomerInformation();
		List<ClaimedItem> claimedItem = printRecoveryClaimObject.getClaim().getClaimedItems();
		for (ClaimedItem claimedItem2 : claimedItem) {
			
			//To get Product name for unserialized Item (not productfamily)						
			if (!claim.getType().equals(ClaimType.PARTS) || (new HibernateCast<PartsClaim>().cast(claim).getPartInstalled() && (claim.getCompetitorModelBrand() == null && claim.getCompetitorModelDescription() == null && claim.getCompetitorModelTruckSerialnumber() == null))) {
				printRecoveryClaimObject.setShowEquipmentInfoSection(true);
				if(!claimedItem2.getItemReference().isSerialized()){
					printRecoveryClaimObject.setUnserializedItemProduct(getProduct(claimedItem2.getItemReference().getModel()).getItemGroupDescription());				
					printRecoveryClaimObject.setUnserializedItemGroupCode(getProduct(claimedItem2.getItemReference().getModel()).getGroupCode());
				}				
			}
			if (claimedItem2.getItemReference().isSerialized() && claimedItem2.getItemReference().getReferredInventoryItem().isRetailed()) {

				printRecoveryClaimObject.setFormatedWarrantyEndDate(claimedItem2.getItemReference().getReferredInventoryItem().getWntyEndDate()!=null?claimedItem2.getItemReference().getReferredInventoryItem().getWntyEndDate().toString(printRecoveryClaimObject.getDatePatternForLoggedInUser()) : "");
				printRecoveryClaimObject.setFormatedWarrantyStartDate(claimedItem2.getItemReference().getReferredInventoryItem().getWntyStartDate()!=null?claimedItem2.getItemReference().getReferredInventoryItem().getWntyStartDate().toString(printRecoveryClaimObject.getDatePatternForLoggedInUser()):"");
			}
			else {
				printRecoveryClaimObject.setFormatedWarrantyEndDate("");
				printRecoveryClaimObject.setFormatedWarrantyStartDate("");
			}
		}
		
		//Logic for Service procedure
		if (claim.getType().equals(ClaimType.MACHINE)
				|| (claim.getType().equals(ClaimType.PARTS) && (new HibernateCast<PartsClaim>()
						.cast(claim).getPartInstalled() && (claim.getCompetitorModelBrand() == null && claim.getCompetitorModelDescription() == null && claim.getCompetitorModelTruckSerialnumber() == null)))) {
			printRecoveryClaimObject.setShowServiceProcedureSection(true);
		} else {
			printRecoveryClaimObject.setShowServiceProcedureSection(false);
		}
		User loggedInUser = getLoggedInUser();
		printRecoveryClaimObject.setInternalUser(isInternalUser(loggedInUser));
		printRecoveryClaimObject.setClaimAssigneeShownToDealer(configParamService.getBooleanValue(ConfigName.CLAIM_ASSIGNEE_SHOWN_TO_DEALER.getName()));
		printRecoveryClaimObject.setIsPartReplacedInstalledSectionVisible(configParamService.
				getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE.getName()));
		printRecoveryClaimObject.setBuPartReplacedByNonBUPart(configParamService.
				getBooleanValue(ConfigName.BUPART_REPLACEABLEBY_NONBUPART.getName()));
		printRecoveryClaimObject.setIsRootCauseVisible(configParamService.
				getBooleanValue(ConfigName.IS_ROOT_CAUSE_ALLOWED.getName()));
		
		//Labor split
		printRecoveryClaimObject.setLaborSplitEnabled(configParamService
				.getBooleanValue(ConfigName.ENABLE_LABOR_SPLIT.getName()));
		printRecoveryClaimObject
				.setLaborSplitOption(configParamService
								.getBooleanValue(ConfigName.ENABLE_LABOR_SPLIT.getName())&& configParamService
								.getBooleanValue(ConfigName.LABOR_SPLIT_DISTRIBUTION
										.getName()));
	
		if (printRecoveryClaimObject.getIsPartReplacedInstalledSectionVisible()
				&& !printRecoveryClaimObject.getBuPartReplacedByNonBUPart()) {
			if (printRecoveryClaimObject.getIsPartReplacedInstalledSectionVisible()
					&& !printRecoveryClaimObject.getBuPartReplacedByNonBUPart()) {
				printRecoveryClaimObject.setIsNonOEMPartsSectionVisible(true);
			}
		}	
		populateOemReplacedParts();
		populateNonOemReplacedParts();
        if(recoveryClaim.getContract() != null && 
                recoveryClaim.getContract().getSupplier() != null &&
                recoveryClaim.getContract().getSupplier().getAddress() != null){
            Address a = recoveryClaim.getContract().getSupplier().getAddress();
            StringBuilder s = new StringBuilder();
            s.append(a.getAddressLine1()).append(", ");
            if(!StringUtils.isEmpty(a.getAddressLine2()))
            	s.append(a.getAddressLine2()).append(", ");
            if(!StringUtils.isEmpty(a.getAddressLine3()))
            	s.append(a.getAddressLine3()).append(", ");
            if(!StringUtils.isEmpty(a.getAddressLine4()))
            	s.append(a.getAddressLine4()).append(", ");
            s.append(a.getCity()).append(", ").append(a.getCountry());
            printRecoveryClaimObject.setAddressInfo(s.toString());
        }else{
            printRecoveryClaimObject.setAddressInfo("");
        }
        
		printRecoveryClaimObject.setClaimProcessedAs(getClaimProcessedAsForDisplay(claim));
		populateRecoveryCostLineItems();
		 List<ManageDocumentObject>  documentObjects = new ArrayList<ManageDocumentObject>();
		 if(recoveryClaim.getActiveRecoveryClaimAudit()!=null && recoveryClaim.getActiveRecoveryClaimAudit().getAttachments()!=null &&!recoveryClaim.getActiveRecoveryClaimAudit().getAttachments().isEmpty()){
			 List<Document> documents = recoveryClaim.getActiveRecoveryClaimAudit().getAttachments();
			 for(Document document : documents){
				 ManageDocumentObject doc= new ManageDocumentObject();
				 doc.setName(document!=null ?
						 document.getFileName() : null );
				 doc.setDescription(document.getDocumentType()!=null ?
						 document.getDocumentType().getDescription() : null);
				 documentObjects.add(doc);
			 }
		 }
		 printRecoveryClaimObject.setDocumentObjects(documentObjects) ;
		 printRecoveryClaimObject.setFaultLocationNameWithCode(claim.getServiceInformation()!=null ?
				 claim.getServiceInformation().getFaultCodeDescription()+"("+claim.getServiceInformation().getFaultCode()+")" :
					 null);
		   if (!claim.getServiceInformation().getServiceDetail().getLaborPerformed().isEmpty())
		   {
                    List<LaborDetail> laborDetails=claim.getServiceInformation().getServiceDetail().getLaborPerformed();
                    List<JobCodes>  jobCodes = new ArrayList<JobCodes>();
				      for(LaborDetail laborDetail : laborDetails){
					   JobCodes jobCode = new JobCodes();
					   jobCode.setCode(laborDetail.getServiceProcedure()!=null ? laborDetail.getServiceProcedure().getDefinition().getCode():null);
					   jobCode.setDescription(laborDetail.getServiceProcedure()!=null ?(laborDetail.getServiceProcedure().getDefinedFor()!=null ? 
							   laborDetail.getServiceProcedure().getDefinedFor().getJobCodeDescription() :null):null);
					   jobCode.setStdLabHours(laborDetail.getLaborHrsEntered()!=null ? laborDetail.getLaborHrsEntered().toString() : null);
					   jobCode.setAddLabHours(laborDetail.getAdditionalLaborHours()!=null ? laborDetail.getAdditionalLaborHours().toString() : null);
					   jobCode.setReasonForAdditionalHours(laborDetail.getReasonForAdditionalHours());
					   jobCode.setHoursSpent(laborDetail.getHoursSpent());
					   jobCode.setLaborHrsEntered(laborDetail.getLaborHrsEntered());
					   jobCodes.add(jobCode);
					   
				   }
				      printRecoveryClaimObject.setCodes(jobCodes);
			  /*  printClaimObject.setJobCodeDescription(claim.getServiceInformation().getServiceDetail().getLaborPerformed().get(0).getServiceProcedure()!=null ?
			    		claim.getServiceInformation().getServiceDetail().getLaborPerformed().get(0).getServiceProcedure().getDefinedFor().getJobCodeDescription() : null);*/
		  }
		return SUCCESS;
	}
    
    public String printRecoveryClaimForSupplier(){
        return printRecoveryClaim();
    }
	
	private void populateEndCustomerInformation(){
		Claim claim = this.printRecoveryClaimObject.getClaim();
		if(claim.getClaimedItems() != null && claim.getClaimedItems().get(0).getItemReference() !=null 
				&& claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem() != null && 
				claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem().getType().getType().equals("RETAIL"))
		{	
			this.printRecoveryClaimObject.setIsOwnerInfoDisplayNeeded(true);
			if(claim.getMatchReadInfo() != null){
				//Match case info takes precedence
				this.printRecoveryClaimObject.setOwnerName(claim.getMatchReadInfo().getOwnerName());
				this.printRecoveryClaimObject.setOwnerCountry(claim.getMatchReadInfo().getOwnerCountry());
				this.printRecoveryClaimObject.setOwnerCity(claim.getMatchReadInfo().getOwnerCity());
				this.printRecoveryClaimObject.setOwnerState(claim.getMatchReadInfo().getOwnerState());
				this.printRecoveryClaimObject.setOwnerZipCode(claim.getMatchReadInfo().getOwnerZipcode());				
			}
			else{
				//Details against inventory should be displayed
				Party owner = claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem().getOwnedBy();
				this.printRecoveryClaimObject.setOwnerName(owner.getName());
				this.printRecoveryClaimObject.setOwnerCountry(owner.getAddress().getCountry() != null ? owner.getAddress().getCountry() : "");
				this.printRecoveryClaimObject.setOwnerCity(owner.getAddress().getCity() != null ? owner.getAddress().getCity() : "");
				this.printRecoveryClaimObject.setOwnerState(owner.getAddress().getState() != null ? owner.getAddress().getState() : "");
				this.printRecoveryClaimObject.setOwnerZipCode(owner.getAddress().getZipCode() != null ? owner.getAddress().getZipCode() : "");
			}
		}
		if ((ClaimType.MACHINE.getType().equals(claim.getType()
				.getType()) && !claim.getItemReference().isSerialized()) || 
				(claim.getType().equals("Parts") && new HibernateCast<PartsClaim>().cast(claim).getPartInstalled() 
						&& !claim.getItemReference().isSerialized()))
  		{
			this.printRecoveryClaimObject.setIsOwnerInfoDisplayNeeded(true);
			//Parts or Machine Non Serialized
			if(claim.getOwnerInformation()!=null){
			this.printRecoveryClaimObject.setOwnerName(claim.getOwnerInformation().getBelongsTo() != null ? claim.getOwnerInformation().getBelongsTo().getName() : "");
			this.printRecoveryClaimObject.setOwnerCountry(claim.getOwnerInformation().getCountry() != null ? claim.getOwnerInformation().getCountry() : "");
			this.printRecoveryClaimObject.setOwnerCity(claim.getOwnerInformation().getCity() != null ? claim.getOwnerInformation().getCity() : "");
			this.printRecoveryClaimObject.setOwnerState(claim.getOwnerInformation().getState() != null ? claim.getOwnerInformation().getState() : "");
			this.printRecoveryClaimObject.setOwnerZipCode(claim.getOwnerInformation().getZipCode() != null ? claim.getOwnerInformation().getZipCode() : "");
			}
  		}
	}
	 
	private ItemGroup getProduct(ItemGroup itemGroup) {
	    	ItemGroup product = null;	
	    	if (!ItemGroup.PRODUCT.equals(itemGroup.getIsPartOf().getItemGroupType())) {
	    		product = getProduct(itemGroup.getIsPartOf());
	    	} else {
	    		product = itemGroup.getIsPartOf();
	    	}
	    	return product;
	   }
	 private void populateOemReplacedParts(){
		 List<OEMPartReplaced> oemPartReplaced = claim.getServiceInformation().getServiceDetail().getOemPartsReplaced();
		 /**
			 * This logic is introduced as oempartreplaced subreport throws exception when there is no replaced part.
			 * So made the sub report inclusion conditional
			 */	
			if (oemPartReplaced != null && oemPartReplaced.size() > 0){
				printRecoveryClaimObject.setIsOEMReplacedPartPresent(Boolean.TRUE);
		    }	
			if (claim.getServiceInformation().getServiceDetail()
					.getHussmanPartsReplacedInstalled() != null
					&& !claim.getServiceInformation().getServiceDetail()
							.getHussmanPartsReplacedInstalled().isEmpty()) {
				for (HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled : claim
						.getServiceInformation().getServiceDetail()
						.getHussmanPartsReplacedInstalled()) {
					if (hussmanPartsReplacedInstalled != null) {
						for (OEMPartReplaced partReplaced : hussmanPartsReplacedInstalled.getReplacedParts()) {
							if( partReplaced != null ) {
								HussmannPartReplacedInstalledDTO hussmannPartReplacedInstalledDTO =
										new HussmannPartReplacedInstalledDTO(partReplaced,claim.getBrand());
								hussmannPartReplacedInstalledDTOList.
									add(hussmannPartReplacedInstalledDTO);
							}
						}
						for (InstalledParts hussmannInstalledpart : hussmanPartsReplacedInstalled.getHussmanInstalledParts()) {
							if( hussmannInstalledpart != null ) {
								HussmannPartReplacedInstalledDTO hussmannPartReplacedInstalledDTO =
										new HussmannPartReplacedInstalledDTO(hussmannInstalledpart,true,printRecoveryClaimObject.getBuPartReplacedByNonBUPart().booleanValue(),claim.getBrand());
								hussmannPartReplacedInstalledDTOList.
									add(hussmannPartReplacedInstalledDTO);
							}
						}
						for (InstalledParts nonHussmannInstalledpart : hussmanPartsReplacedInstalled.getNonHussmanInstalledParts()) {
								if( nonHussmannInstalledpart != null ) {
									HussmannPartReplacedInstalledDTO hussmannPartReplacedInstalledDTO =
											new HussmannPartReplacedInstalledDTO(nonHussmannInstalledpart,false,printRecoveryClaimObject.getBuPartReplacedByNonBUPart().booleanValue(),claim.getBrand());
									hussmannPartReplacedInstalledDTOList.
										add(hussmannPartReplacedInstalledDTO);
								}
							}
					}
				}
				
			}
	 }
	 public String getClaimProcessedAsForDisplay(Claim claim) {		
		 	if(!org.apache.commons.lang.StringUtils.isEmpty(claim.getPolicyCode())){
				return claim.getPolicyCode();
			}
			if (Constants.INVALID_ITEM_NO_WARRANTY.equals(claim.getClaimProcessedAs())) {
				return getText("label.common.noWarranty");
			} else if (Constants.VALID_ITEM_STOCK.equals(claim.getClaimProcessedAs())) {
				return getText("label.common.itemInStock");
			} else if (Constants.VALID_ITEM_NO_WARRANTY.equals(claim.getClaimProcessedAs())) {			
					return getText("label.common.noWarranty");
			} else if (Constants.VALID_ITEM_OUT_OF_WARRANTY.equals(claim.getClaimProcessedAs())) {
					return getText("label.common.outOfWarranty");			
			}
			return claim.getClaimedItems().get(0).getApplicablePolicy().getCode();
		}
	 public String fetchCurrentClaimAssignee(Long recoveryClaimId){
			User claimAssignee = workListService.getCurrentAssigneeForRecClaim(recoveryClaimId);
			if(claimAssignee == null){
				return "";
			}
			StringBuffer assignee = new StringBuffer();
			String firstName = claimAssignee.getFirstName();
			String lastName = claimAssignee.getLastName();
			String login = claimAssignee.getName();
			assignee.append(firstName == null ? "" : firstName);
			assignee.append(" ");
			assignee.append(lastName == null ? "" : lastName);
			assignee.append(" (");
			assignee.append(login);
			assignee.append(")");
			return assignee.toString();
		}
	 private void populateRecoveryCostLineItems(){
		 List<CostLineItem> costLineItems = recoveryClaim.getCostLineItems();
		 List<RecoveryCostLineObject> items=new ArrayList<RecoveryCostLineObject>();
		 RecoveryCostLineObject recoveryCostLineObject;
		 String sectionName=null;
		 if(null!=recoveryClaim.getContract() && null!=recoveryClaim.getContract().getSupplier() && null!=recoveryClaim.getContract().getSupplier().getPreferredCurrency() && null !=recoveryClaim.getContract().getSupplier().getPreferredCurrency()){
			 String currencyCode = recoveryClaim.getContract().getSupplier().getPreferredCurrency().getCurrencyCode();
			 for(CostLineItem costLineItem:costLineItems){
				 if(supplerRole ){
					 if(costLineItem.getRecoveredCost().isPositive() || costLineItem.getCostAfterApplyingContract().isPositive()||
							 costLineItem.getRecoveredCost().isZero() || 	costLineItem.getCostAfterApplyingContract().isZero() ){
					 recoveryCostLineObject = new RecoveryCostLineObject(); 
				 sectionName = costLineItem.getSection().getName();
				 if(Section.NON_OEM_PARTS.equalsIgnoreCase(sectionName)){
					 printRecoveryClaimObject.setShowMiscellaneousExpenseSection(true);
					 recoveryCostLineObject.setCostElement("Miscellaneous Expense & Outside Services");
				 }
				 else if(Section.TRAVEL_BY_HOURS.equalsIgnoreCase(sectionName)){
				   printRecoveryClaimObject.setActualValueForTravel(costLineItem.getRecoveredCost()); 
				   recoveryCostLineObject.setCostElement(getText(costLineItem.getMessageKey(sectionName)));
				   printRecoveryClaimObject.setShowTravelSection(true);
				   
				 }
				 else{
				 recoveryCostLineObject.setCostElement(getText(costLineItem.getMessageKey(sectionName)));
				 }
				 recoveryCostLineObject.setWarrantyClaimValue(getCostForSection("actualCost",costLineItem.getSection().getName(),currencyCode));
				 recoveryCostLineObject.setContractValue(getCostForSection("costAfterApplyingContract",costLineItem.getSection().getName(),currencyCode));
				 recoveryCostLineObject.setActualValue(costLineItem.getRecoveredCost());
				 items.add(recoveryCostLineObject);
					 }
				 }else{
					 recoveryCostLineObject = new RecoveryCostLineObject(); 
					 sectionName = costLineItem.getSection().getName();
					 if(Section.NON_OEM_PARTS.equalsIgnoreCase(sectionName)){
						 printRecoveryClaimObject.setShowMiscellaneousExpenseSection(true);
						 recoveryCostLineObject.setCostElement("Miscellaneous Expense & Outside Services");
					 }else if(Section.TRAVEL_BY_HOURS.equalsIgnoreCase(sectionName)){
						   recoveryCostLineObject.setCostElement(getText(costLineItem.getMessageKey(sectionName)));
						   printRecoveryClaimObject.setActualValueForTravel(costLineItem.getRecoveredCost());
						   printRecoveryClaimObject.setShowTravelSection(true);
					 }else{
					 recoveryCostLineObject.setCostElement(getText(costLineItem.getMessageKey(sectionName)));
					 }
					 recoveryCostLineObject.setWarrantyClaimValue(getCostForSection("actualCost",costLineItem.getSection().getName(),currencyCode));
					 recoveryCostLineObject.setContractValue(getCostForSection("costAfterApplyingContract",costLineItem.getSection().getName(),currencyCode));
					 recoveryCostLineObject.setActualValue(costLineItem.getRecoveredCost());
					 items.add(recoveryCostLineObject);
				 }
			 }
			 recoveryCostLineObject=new RecoveryCostLineObject();
			 recoveryCostLineObject.setCostElement("Total :");
			 recoveryCostLineObject.setWarrantyClaimValue(getTotalCostForSection("actualCost",currencyCode));
			 recoveryCostLineObject.setContractValue(getTotalCostForSection("costAfterApplyingContract",currencyCode));
			 recoveryCostLineObject.setActualValue(getTotalCostForSection("recoveredCost",currencyCode));
			 recoveryCostLineObject.setBold(true);
			 items.add(recoveryCostLineObject);
			 printRecoveryClaimObject.setRecoveryCostLineItems(items);
		 }
		
	 }
	 private void populateNonOemReplacedParts(){
		 Integer numberOfUnits=null;
		 Money pricePerUnit=null;
		 Money totalValue=null;
		 List<NonOEMPartReplaced> nonOEMPartReplacedList=claim.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced();
		 for(NonOEMPartReplaced nonOEMPartReplaced:nonOEMPartReplacedList){
			 numberOfUnits=null;
			 pricePerUnit=null;
			 totalValue=null;
			 MiscellaneousPartsObject misObject=new MiscellaneousPartsObject();
			 misObject.setDescription( nonOEMPartReplaced.getDescription());
			 numberOfUnits=nonOEMPartReplaced.getNumberOfUnits();
			 misObject.setNumberOfUnits(numberOfUnits);
			 pricePerUnit=nonOEMPartReplaced.getPricePerUnit();
				GlobalConfiguration globalConfiguration = GlobalConfiguration
				.getInstance();
			totalValue=numberOfUnits != null &&  pricePerUnit!=null ?pricePerUnit.times(numberOfUnits): globalConfiguration.zeroInBaseCurrency();
			misObject.setActualValue(totalValue);
			miscellaneousPartsObjectList.add(misObject);
		 }
	 }
	 public Money getCostForSection(String costType, String sectionName, String currencyCode) {
	        List<Money> amounts = new ArrayList<Money>();
	        Money m = null;
	        if ("actualCost".equals(costType)) {
	            m = this.recoveryClaim.getAcutalCostForSection(sectionName);
	        } else if ("costAfterApplyingContract".equals(costType)) {
	            m = this.recoveryClaim.getCostAfterApplyingContractForSection(sectionName);
	        } else if ("recoveredCost".equals(costType)) {
	            m = this.recoveryClaim.getRecoveredCostForSection(sectionName);
	        } else if("supplierCost".equals(costType)){
	        	m = this.recoveryClaim.getSupplierCostForSection(sectionName);
	        }else {
	            throw new RuntimeException("Cost type [" + costType + "] is invalid");
	        }
	        if (m != null) {
	            amounts.add(m);
	        }else{
	        	m = Money.valueOf(0.0, Currency.getInstance(currencyCode));
	        	amounts.add(m);
	        }
	        Money sum = Money.sum(amounts);
	        return sum;
	    }
	 public Money getTotalCostForSection(String costType, String currencyCode) {
			List<Money> amounts = new ArrayList<Money>();
			for (CostLineItem cli : recoveryClaim.getCostLineItems()) {
				if (cli.getSection().getName() != null && !Section.TOTAL_CLAIM.equals(cli.getSection().getName())) {
					amounts.add(getCostForSection(costType, cli.getSection().getName(), currencyCode));
				}
			}
			return Money.sum(amounts);
		}
	 private void populateRecoveryClaimAssignee(RecoveryClaim recoveryClaim){
			User claimAssignee = this.workListService.getCurrentAssigneeForRecClaim(recoveryClaim.getId());
			String firstName = null;
			String lastName = null;
			String login = null;		
			StringBuffer assignee = new StringBuffer();
			
			if(claimAssignee == null){
				recoveryClaim.setCurrentAssignee("");
			}				
			if(claimAssignee != null)
			{
				firstName = claimAssignee.getFirstName();
				lastName = claimAssignee.getLastName();
				
				if(login == null)
				{
					login = claimAssignee.getName();
				}
			}
			//Moved the code under single condition
			assignee.append(firstName == null ? "" : firstName);
			assignee.append(" ");
			assignee.append(lastName == null ? "" : lastName);
			
			if(login!=null){
				assignee.append(" (");
				assignee.append(login);
				assignee.append(")");
			}
			recoveryClaim.setCurrentAssignee(assignee.toString());
		}
	 private void populateLoginUser(User user){
			StringBuffer assignee = new StringBuffer();
			assignee.append(user.getFirstName() == null ? "" : user.getFirstName());
			assignee.append(" ");
			assignee.append(user.getLastName() == null ? "" : user.getLastName());
			
			if(user.getName()!=null){
				assignee.append(" (");
				assignee.append(user.getName());
				assignee.append(")");
			}
		 printRecoveryClaimObject.setLoggedInUserId(assignee.toString());
		 Date d=new Date();
		 printRecoveryClaimObject.setPrintDate(d.toString());
	 }
	 
	public boolean getSupplierView() {
		return supplierView;
	}

	public void setSupplierView(boolean supplierView) {
		this.supplierView = supplierView;
	}

	public List<MiscellaneousPartsObject> getMiscellaneousPartsObjectList() {
		return miscellaneousPartsObjectList;
	}

	public void setMiscellaneousPartsObjectList(
			List<MiscellaneousPartsObject> miscellaneousPartsObjectList) {
		this.miscellaneousPartsObjectList = miscellaneousPartsObjectList;
	}
}
