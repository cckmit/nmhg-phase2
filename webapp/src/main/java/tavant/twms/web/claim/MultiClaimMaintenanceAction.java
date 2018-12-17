package tavant.twms.web.claim;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimFolderNames;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.MatchReadInfo;
import tavant.twms.domain.claim.MatchReadMultiplyFactor;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.claimsubmission.ClaimSubmissionUtil;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.PaymentCalculationException;
import tavant.twms.domain.claim.payment.PaymentService;
import tavant.twms.domain.claim.validation.ValidationResults;
import tavant.twms.domain.common.AcceptanceReason;
import tavant.twms.domain.common.AccountabilityCode;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigOptionConstants;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.MSAService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.domain.orgmodel.UserClusterService;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnConfiguration;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.partreturn.PaymentCondition;
import tavant.twms.domain.rules.RuleAdministrationService;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.search.ClaimSearchSummaryAction;
import tavant.twms.worklist.WorkListItemService;

import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class MultiClaimMaintenanceAction extends ClaimSearchSummaryAction implements Preparable,ConfigOptionConstants{

	private final Logger logger = Logger.getLogger(this.getClass());
	private String contextName;
	private String isMultiClaimMaintenance;
	private String isTransferOrReProcess;
	private boolean attributeSelected;
	private boolean multiDealerClaimsSelected;
	List<Claim> claims = new ArrayList<Claim>(5);
	List<Claim> restoreClaimsList = new ArrayList<Claim>(5);
	private MultiClaimMaintenanceAttributes attributeMapper = new MultiClaimMaintenanceAttributes();
	private WorkListItemService workListItemService;
	private boolean appendOrEditProcessingNotes;
	private String multiClaimAction;
	private String transferTo;
	private List<PaymentCondition> paymentConditions;
	private PartReturnService partReturnService;
	private CatalogService catalogService;
	
	private PaymentService paymentService;
	private PartReturnProcessingService partReturnProcessingService;
	private List<String> countriesFromMSA = new ArrayList<String>();
	private MSAService msaService;
	private RuleAdministrationService ruleAdministrationService;

	private String stateCode;
	private String cityCode;
	private String zipCode;
	private static final String REPROCESS = "Re-process";
	private Integer pageNo=new Integer(0);
    private ListCriteria listCriteria;
    private List<Integer> pageNoList = new ArrayList<Integer>();
    private Map<String, Set<String>> claimActionMap;
    
    private static final String ASSIGN_RULES_STATE = "assign";
    private boolean partsReplacedInstalledSectionVisible;
    private String selectedBusinessUnit;
    private String selectedClaimsId;
    
    private ClaimSubmissionUtil claimSubmissionUtil;
   
   
	public MultiClaimMaintenanceAction(){
		super();
		this.contextName="ClaimSearches";
		this.attributeMapper.getProcessingNotes().setName(getText("title.attributes.internalComments"));
		this.attributeMapper.getAccountabilityCode().setName(getText("title.attributes.accountabilityCode"));
		this.attributeMapper.getRejectionReason().setName(getText("title.attributes.rejectionReason"));
		this.attributeMapper.getAcceptanceReason().setName(getText("title.attributes.acceptanceReason"));
		this.attributeMapper.getTechnician().setName(getText("title.attributes.technician"));
		this.attributeMapper.setListOfAttributes();
	}
	
	
	public void prepare() throws Exception {
		if(this.isTransferOrReProcess != null && StringUtils.hasText(isTransferOrReProcess)){
			this.paymentConditions = this.partReturnService.findAllPaymentConditions();
			this.countriesFromMSA = this.msaService.getCountriesFromMSA();
		}
		if(StringUtils.hasText(this.selectedBusinessUnit)/* && SelectedBusinessUnitsHolder.getSelectedBusinessUnit()== null*/){
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(this.selectedBusinessUnit);
		}
    }
	
    
    @Override
	public void validate() {
    	
    	if(this.isTransferOrReProcess != null && StringUtils.hasText(isTransferOrReProcess)){
    		setSearchedClaimsBasedOnCriteria();
    		if(getSelectedClaims()==null || getSelectedClaims().size()==0){
	    			addActionError("error.multiClaimMaintain.selectClaim");
	    			this.claims=this.restoreClaimsList;
	    	}
    		if("multiTransfer".equals(this.multiClaimAction) && 
    				(this.transferTo == null || !StringUtils.hasText(this.transferTo))){
    			addActionError("multiTransfer.validation.message");
    			
    		}
    	}else{
	    	if(!this.attributeMapper.isAnyAttributeSelected(isMultiDealerClaims())){
	    		addActionError("error.multiClaimMaintain.selectAttribute");
	    	}
	    	if(isAttributeSelected() &&
	    			!this.attributeMapper.isAttributeValueSet()){
	    		addActionError("error.multiClaimMaintain.setAttribute");
	    	}
	    	if(attributeMapper.getAcceptanceReason().isSelected() && 
	    			!attributeMapper.getRejectionReason().isSelected()){
	    		checkValidityForReasonUpdation(true,claims);
	    	}else if(attributeMapper.getRejectionReason().isSelected() && 
	    					!attributeMapper.getAcceptanceReason().isSelected()){
	    		checkValidityForReasonUpdation(false,claims); 
	    	}
	    	
	    	if(this.claims==null || this.claims.size()==0 ||
	    			(getActionErrors()!=null && getActionErrors().size()>0)){
	    		if(this.claims==null || this.claims.size()==0){
	    			addActionError("error.multiClaimMaintain.selectClaim");
	    		}
	    		this.claims=this.restoreClaimsList;
	    	}
	    	
	    	if (this.claims != null && this.claims.size() > 0) {
				if (isMultiBUClaimsSelected()) {
					addActionError("error.multiClaimMaintain.multiBUClaimsSelected");
					this.claims = this.restoreClaimsList;
				}

			}
    	}
    }
	
	public String claimsForMultipleMaintenance(){
		PageResult<Claim> pageResult =null;
		Long noOfRecords;
		setListCriteria(listCriteria);
		if (getServletRequest().getAttribute("domainPredicateId") != null) {
			setDomainPredicateId(getServletRequest().getAttribute("domainPredicateId").toString());
		}
		if (getServletRequest().getAttribute("savedQueryId") != null) {
			setSavedQueryId(getServletRequest().getAttribute("savedQueryId").toString());
		}
		if (getDomainPredicateId() != null && !("".equals(getDomainPredicateId().trim()))) {
			//if Number of records are less than bu config,then only we have to show that claims in UI if not show error message
			noOfRecords = getClaimService().findAllClaimsCountForClaimAttributes(Long.parseLong(getDomainPredicateId()),getListCriteria());
			if (noOfRecords != 0) {
				if (noOfRecords <= getConfigParamService().getLongValue(ConfigName.MAX_CLAIMS_ALLOWED_FOR_MULTI_CLAIM_MAINTENANCE.getName())) {
					pageResult = getClaimService().findAllClaimsForMultiMaintainance(Long.parseLong(getDomainPredicateId()),getListCriteria());
					this.setClaims(pageResult.getResult());		
					for (int i=0;i<pageResult.getNumberOfPagesAvailable();i++){
				        	this.pageNoList.add(new Integer(i+1));
				    }
				}else{
					addActionError("error.MultiClaimMaintainance.exceedsMaxClaimsCount");
				}				
			}
			
		} else {
			this.logger.error("domain Predicate Id is null ");
		}
		if(this.claims!=null && this.claims.size()>0){
			for (Iterator<Claim> iterator = this.claims.iterator(); iterator.hasNext();) {
				Claim claim = iterator.next();
				boolean isClosed =true;
				for (RecoveryClaim recoveryClaim : claim.getRecoveryClaims()) {
					if(!recoveryClaim.getRecoveryClaimState().getState().contains(ClaimState.CLOSED.getState())){
						isClosed=false;
						break;
					}
				}
				if(!isClosed){
					iterator.remove();
				}
			}
		}
		if(hasActionErrors()){
			return INPUT;			
		}
		addActionMessage("message.claimSearch.onlyClosedClaims");		
		return SUCCESS;
		
	}
	
	public String setClaimsForMaintenance(){
		this.attributeSelected=true;
		this.claims = getSelectedClaims();
		return SUCCESS;
	}
	
	public String updateClaimsForMaintenance(){
		validateSelectedAttributes();
		if(hasActionErrors())
			return INPUT;
		if(this.attributeMapper.getTechnician().isSelected() && !isMultiDealerClaimsSelected() &&  isAttributeSelected()){
			this.attributeMapper.setCommonTechnician();
		}
		if (this.attributeMapper.isAnyAttributeSelected(isMultiDealerClaimsSelected()) && isAttributeSelected()) {
			String bu = this.claims.get(0).getBusinessUnitInfo().getName();
			this.attributeMapper.setLovObjects(bu);
		}
		for (Iterator<Claim> iterator = this.claims.iterator(); iterator.hasNext();) {
			Claim claim = iterator.next();
			if(claim != null){
				claim.setUpdated(new Boolean(true));
				setSelectedAttributes(this.attributeMapper,claim);
				claim.setMultiClaimMaintenance(true);
				claim.setState(claim.getState());
				claimService.updateClaim(claim);
				addActionMessage("success.multiClaimMaintain.updated", claim.getClaimNumber());
			}
		}
		
		return SUCCESS;
	}
	
	private void validateSelectedAttributes() {
		if(attributeMapper.getAcceptanceReason().isSelected() && ((AcceptanceReason)attributeMapper.getAcceptanceReason().getAttribute()).getCode().equals(""))
			addActionError("error.MultiClaimMaintainance.invalidAcceptanceReason");		 
		if(attributeMapper.getRejectionReason().isSelected() && ((RejectionReason)attributeMapper.getRejectionReason().getAttribute()).getCode().equals(""))
			addActionError("error.MultiClaimMaintainance.invalidRejectionReason"); 
		if(attributeMapper.getAccountabilityCode().isSelected() && ((AccountabilityCode)attributeMapper.getAccountabilityCode().getAttribute()).getCode().equals(""))
		    addActionError("error.MultiClaimMaintainance.invalidReviewResponsibility");
		if(attributeMapper.getTechnician().isSelected() && (((User)attributeMapper.getTechnician().getAttribute()).getId() == null))				
		    addActionError("error.MultiClaimMaintainance.invalidTechnician");			
	}


	public String getIsMultiClaimMaintenance() {
		return this.isMultiClaimMaintenance;
	}

	public void setIsMultiClaimMaintenance(String isMultiClaimMaintenance) {
		this.isMultiClaimMaintenance = isMultiClaimMaintenance;
	}

	public List<Claim> getClaims() {
		return this.claims;
	}

	public void setClaims(List<Claim> claims) {
		this.claims = claims;
	}
	
	public String getContextName() {
		return this.contextName;
	}


	public void setContextName(String contextName) {
		this.contextName = contextName;
	}


	@Override
	public ListCriteria getCriteria(){
		ListCriteria listCriteria = new ListCriteria();
		return listCriteria;
	}


	public List<Claim> getRestoreClaimsList() {
		return this.restoreClaimsList;
	}


	public void setRestoreClaimsList(List<Claim> restoreClaimsList) {
		this.restoreClaimsList = restoreClaimsList;
	}


	public MultiClaimMaintenanceAttributes getAttributeMapper() {
		return this.attributeMapper;
	}


	public void setAttributeMapper(MultiClaimMaintenanceAttributes attributeMapper) {
		this.attributeMapper = attributeMapper;
	}
	
	private void setSelectedAttributes(MultiClaimMaintenanceAttributes mapper,Claim claim){
		if(mapper.getProcessingNotes().isSelected()){
			// fix for TWMS4.1-537
			if(this.appendOrEditProcessingNotes && claim.getInternalComment() != null){
				claim.setInternalComment(claim.getInternalComment().
						concat("  " +((String[])mapper.getProcessingNotes().getAttribute())[0]));
			}else{
				claim.setInternalComment(((String[])mapper.getProcessingNotes().getAttribute())[0]);
			}
			// End of Changes
		}
		if(mapper.getAccountabilityCode().isSelected()){
			claim.setAccountabilityCode((AccountabilityCode)mapper.getAccountabilityCode().getAttribute());
		}
		if(mapper.getRejectionReason().isSelected()){
			if(ClaimState.DENIED_AND_CLOSED.getState().equals(claim.getState().getState())){
				List<RejectionReason> tempRejectionList = new ArrayList<RejectionReason>();
				tempRejectionList.add((RejectionReason)mapper.getRejectionReason().getAttribute());
				claim.setRejectionReasons(tempRejectionList);
				claim.setAcceptanceReason(null);
			}
		}
		if(mapper.getAcceptanceReason().isSelected()){
			if(ClaimState.ACCEPTED_AND_CLOSED.getState().equals(claim.getState().getState())){
			claim.setAcceptanceReason((AcceptanceReason)mapper.getAcceptanceReason().getAttribute());
			claim.setRejectionReasons(null);
			}
		}
		if(mapper.getTechnician().isSelected()){
			claim.getServiceInformation().getServiceDetail().setTechnician((User)mapper.getTechnician().getAttribute());
		}
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}
	public boolean isAttributeSelected() {
		return this.attributeSelected;
	}


	public void setAttributeSelected(boolean attributeSelected) {
		this.attributeSelected = attributeSelected;
	}
	
	public Map<Long, String> getCommonTechnicians(){
		return this.attributeMapper.getCommonTechnicians(this.claims.get(0).getForDealer().getId(),this.claims.get(0).getBusinessUnitInfo().getName());
	}
	
	public boolean isMultiDealerClaimsSelected() {
		return this.multiDealerClaimsSelected;
	}


	public void setMultiDealerClaimsSelected(boolean multiDealerClaimsSelected) {
		this.multiDealerClaimsSelected = multiDealerClaimsSelected;
	}


	private boolean isMultiDealerClaims(){
		Long dealerId =null ;
		if(this.claims!=null && this.claims.size()>0){
			for (Iterator<Claim> iterator = this.claims.iterator(); iterator.hasNext();) {
				if(iterator.next()==null){
					iterator.remove();
				}
			}
		}
		if(claims!=null && claims.size()!=0){
		dealerId=this.claims.get(0).getForDealer().getId();
		}
		for (Claim claim : this.claims) {
			if(claim!=null && dealerId!=null && dealerId.longValue()!=claim.getForDealer().getId().longValue()){
				this.multiDealerClaimsSelected=true;
				break;
			}
		}
		return this.multiDealerClaimsSelected;
	}
	
	private boolean isMultiBUClaimsSelected() {
		boolean multiBUClaimsSelected = false;
		String selectedClaimBu = null;

		if (this.claims != null && !this.claims.isEmpty()) {
			for (Iterator<Claim> iterator = this.claims.iterator(); iterator.hasNext();) {
				if (iterator.next() == null) {
					iterator.remove();
				}
			}
		}
		if (this.claims != null && !this.claims.isEmpty()) {
			selectedClaimBu = this.claims.get(0).getBusinessUnitInfo().getName();
		}
		for (Claim claim : this.claims) {
			if (claim.getBusinessUnitInfo().getName() != null
					&& !selectedClaimBu.equalsIgnoreCase(claim.getBusinessUnitInfo().getName())) {
				multiBUClaimsSelected = true;
				break;
			}
		}
		return multiBUClaimsSelected;
	}
	
	private void checkValidityForReasonUpdation(boolean isAcceptanceSelected,List<Claim> claims){
		for (Claim claim : claims) {
			if(isAcceptanceSelected && 
					!ClaimState.ACCEPTED_AND_CLOSED.getState().equals(claim.getState().getState())){
				addActionError("error.MultiClaimMaintainance.acceptanceReason");
				break;
			}else if(!isAcceptanceSelected && 
					!ClaimState.DENIED_AND_CLOSED.getState().equals(claim.getState().getState())){
				addActionError("error.MultiClaimMaintainance.rejectionReason");
				break;
			}
		}
	}

	public boolean isAppendOrEditProcessingNotes() {
		return appendOrEditProcessingNotes;
	}


	public void setAppendOrEditProcessingNotes(boolean appendOrEditProcessingNotes) {
		this.appendOrEditProcessingNotes = appendOrEditProcessingNotes;
	}
			
	public String getIsTransferOrReProcess() {
		return isTransferOrReProcess;
	}


	public void setIsTransferOrReProcess(String isTransferOrReProcess) {
		this.isTransferOrReProcess = isTransferOrReProcess;
	}
	
	public String getMultiClaimAction() {
		return multiClaimAction;
	}


	public void setMultiClaimAction(String multiClaimAction) {
		this.multiClaimAction = multiClaimAction;
	}
	
	public String getTransferTo() {
		return transferTo;
	}


	public void setTransferTo(String transferTo) {
		this.transferTo = transferTo;
	}

	public String claimsForTransferOrReProcess(){
		setSearchedClaimsBasedOnCriteria();
		if(hasActionErrors()){
			return INPUT;
		}
		return SUCCESS;
	}


	private void setSearchedClaimsBasedOnCriteria() {
		PageResult<Claim> pageResult =null;
		Long noOfRecords;
		setListCriteria(listCriteria);
		if (getServletRequest().getAttribute("domainPredicateId") != null) {
			setDomainPredicateId(getServletRequest().getAttribute("domainPredicateId").toString());
		}
		if (getServletRequest().getAttribute("savedQueryId") != null) {
			setSavedQueryId(getServletRequest().getAttribute("savedQueryId").toString());
		}
		if (getDomainPredicateId() != null && !("".equals(getDomainPredicateId().trim()))) {
			//if Number of records are less than bu config,then only we have to show that claims in UI if not show error message
			noOfRecords = getClaimService().findAllClaimsCountForMultiTransferReProcess(Long.parseLong(getDomainPredicateId()),
							getListCriteria(), getLoggedInUser());
			if (noOfRecords != 0) {
				if (noOfRecords <= getConfigParamService().getLongValue(ConfigName.MAX_CLAIMS_ALLOWED_FOR_MULTI_CLAIM_MAINTENANCE.getName())) {
					pageResult = getClaimService().findAllClaimsForMultiTransferReProcess(
									Long.parseLong(getDomainPredicateId()),
									getListCriteria(), getLoggedInUser());
					this.setClaims(pageResult.getResult());		
					for (int i=0;i<pageResult.getNumberOfPagesAvailable();i++){
				        	this.pageNoList.add(new Integer(i+1));
				    }
				}else{
					addActionError("error.MultiClaimMaintainance.exceedsMaxClaimsCount");
				}
			}
		} else {
			this.logger.error("domain Predicate Id is null ");
		}
		
	}     
	
/*
 * 
 * We will capture Only action and claim number
 * if the logged in user is in the user cluster in any
 * authority rules. Else we will consider that s/he is
 * eligible for all the actions.
	
 */	
	@SuppressWarnings("unchecked")
	private void authorizeAllowedActionsForProcessor(List<Claim> claims, User loggedInUser, String action) {
		if (claims!=null && !claims.isEmpty())
		{
			for (Iterator iter = claims.iterator(); iter.hasNext();) {
				Claim claim = (Claim) iter.next();
				if (claim!=null && (ClaimState.PROCESSOR_REVIEW.equals(claim.getState())
						|| ClaimState.TRANSFERRED.equals(claim.getState())
						|| ClaimState.APPROVED.equals(claim.getState())
						|| ClaimState.FORWARDED.equals(claim.getState())
						|| ClaimState.MANUAL_REVIEW.equals(claim.getState())
						|| ClaimState.REPLIES.equals(claim.getState())))
				{
					ValidationResults validationResults = 
						this.ruleAdministrationService.executeProcessorAuthorityRules(claim, loggedInUser);
					Set<UserCluster> assignedToList = (Set<UserCluster>) validationResults
					.getAssignStateToUserGroupMap().get(ASSIGN_RULES_STATE);
					if (assignedToList!=null && !assignedToList.isEmpty())
					{
						UserCluster userCluster = (UserCluster) assignedToList.iterator().next();
						if (userCluster.getIncludedUsers().contains(loggedInUser))
						{
							if (claim.getAllowedActionsList()!=null &&
									!claim.getAllowedActionsList().isEmpty() &&
									!claim.getAllowedActionsList().contains(action))
							{
								if (claimActionMap==null)
									claimActionMap = new HashMap<String, Set<String>>();
								claimActionMap.put(claim.getClaimNumber(), claim.getAllowedActionsList());
							}
						}
					}
				}		
				
			}
		}
	}

	public String claimsForMultiTransfer(){
		List<String> taskNames = getTaskNames();
		authorizeAllowedActionsForProcessor(getSelectedClaims(), getLoggedInUser(), UserClusterService.ACTION_TRANSFER_TO);
		if (claimActionMap!=null && !claimActionMap.isEmpty())
		{
			StringBuffer claimNumber = new StringBuffer(50);
			String claimNumberToDisplay;
			for (Iterator iter = claimActionMap.keySet().iterator(); iter.hasNext();) {
				claimNumber.append( (String) iter.next());
				claimNumber.append(", ");
			} 
			claimNumberToDisplay = claimNumber.toString().substring(0, claimNumber.toString().length()-2);
			addActionWarning("transfer.processorAuthority.notAllowed", claimNumberToDisplay);
			setClaimsForTransferOrReProcess();
			return INPUT;
		}
		for (Iterator<Claim> iterator = getSelectedClaims().iterator(); iterator.hasNext();) {
			Claim claim = iterator.next();
			if(claim != null){
				claim.setUpdated(new Boolean(true));
				TaskInstance taskInstance = this.workListItemService
						.findTaskForClaimWithTaskNames(claim.getId(), taskNames);
				this.workListItemService.endTaskWithReassignment(taskInstance,
								"Transfer", this.transferTo.substring(this.transferTo.indexOf("(")+1, this.transferTo.indexOf(")")));
				addActionMessage("transfer.success", claim.getClaimNumber());
			}
				
		}
    	
    	return SUCCESS;
    }


	private List<String> getTaskNames() {
		List<String> taskNames = new ArrayList<String>();
		taskNames.add(ClaimFolderNames.PROCESSOR_REVIEW);
		taskNames.add(ClaimFolderNames.REJECTED_PART_RETURN);
		taskNames.add(ClaimFolderNames.REOPENED);
		taskNames.add(ClaimFolderNames.ON_HOLD);
		taskNames.add(ClaimFolderNames.ON_HOLD_FOR_PART_RETURN);
		taskNames.add(ClaimFolderNames.REPLIES);
		taskNames.add(ClaimFolderNames.TRANSFERRED);
		taskNames.add(ClaimFolderNames.PART_SHIPPED_NOT_RECEIVED);
		return taskNames;
	}
	
	public List<String> getEligibleProcessors() {
        List<String> allProcessors = new ArrayList<String>();
        allProcessors.addAll(getProcessors());
        allProcessors.remove(getLoggedInUser().getName());

        Collections.sort(allProcessors);
		return allProcessors;
	}

	public List<Claim> getSelectedClaims(){
		 if(selectedClaimsId !=null){
			 this.selectedClaimsId=this.selectedClaimsId.replaceAll(" ","");
		 }
		 String[] idStrArray =null;
		 if(selectedClaimsId !=null){
			 idStrArray = this.selectedClaimsId.split(",");
		 }	
		 List<Long> idList =getLongIdArray(idStrArray);
		 if(!idList.isEmpty()){
			 List<Claim> selectedClaims=this.claimService.findClaimsForIds(idList);
			 return selectedClaims;
		 }
		 return new ArrayList<Claim>();
	 }
	
	private List<Long> getLongIdArray(String[]idStrArray){
	 List<Long> idList = new ArrayList<Long>();
		if(idStrArray != null){
			for (int i=0; i< idStrArray.length; i++) {
				if(StringUtils.hasText(idStrArray[i])){
					try{
						Long id = Long.parseLong(idStrArray[i]);
						idList.add(id);
					}catch (NumberFormatException e) {
						logger.error(this.getClass().getName()+"Claim id has a string value");
					}
				}
			}
		}
		
		return idList;		
	}
	 


	public String claimsForMultiReProcess(){
		authorizeAllowedActionsForProcessor(getSelectedClaims(), getLoggedInUser(), UserClusterService.ACTION_RE_PROCESS);
		if (claimActionMap!=null && !claimActionMap.isEmpty())
		{
			StringBuffer claimNumber = new StringBuffer(50);
			String claimNumberToDisplay;
			for (Iterator iter = claimActionMap.keySet().iterator(); iter.hasNext();) {
				claimNumber.append( (String) iter.next());
				claimNumber.append(", ");
			} 
			claimNumberToDisplay = claimNumber.toString().substring(0, claimNumber.toString().length()-2);
			addActionWarning("reprocess.processorAuthority.notAllowed", claimNumberToDisplay);
			setClaimsForTransferOrReProcess();
			return INPUT;
		}

		for (Iterator<Claim> iterator = getSelectedClaims().iterator(); iterator.hasNext();) {
			Claim claim = iterator.next();
			if(claim != null){
				TaskInstance taskInstance = this.workListItemService.findTaskForClaimWithTaskNames(
						claim.getId(), getTaskNames());
				this.task = this.taskViewService.getTaskView(taskInstance.getId());
				this.task.setTakenTransition(getTakenTransition(this.multiClaimAction));
				processClaim(this.task.getClaim());
			}
				
		}
		return SUCCESS;
	}
	
	private void setClaimsForTransferOrReProcess() {
		PageResult<Claim> pageResult =null;
		setListCriteria(listCriteria);
		if (getServletRequest().getAttribute("domainPredicateId") != null) {
			setDomainPredicateId(getServletRequest().getAttribute("domainPredicateId").toString());
		}
		if (getServletRequest().getAttribute("savedQueryId") != null) {
			setSavedQueryId(getServletRequest().getAttribute("savedQueryId").toString());
		}
		if (getDomainPredicateId() != null && !("".equals(getDomainPredicateId().trim()))) {
			pageResult = getClaimService().findAllClaimsForMultiTransferReProcess
											(Long.parseLong(getDomainPredicateId()),getListCriteria(), 
													getLoggedInUser());
		} else {
			this.logger.error("domain Predicate Id is null ");
		}
		this.setClaims(pageResult.getResult());		
		for (int i=0;i<pageResult.getNumberOfPagesAvailable();i++){
	        	this.pageNoList.add(new Integer(i+1));
	    }
	}


	// if changed, please modify processClaim() API in other claim action class too
	private void processClaim(Claim claim) {
		/*prepareOEMPartCrossRef(claim);*/
		
		claimSubmissionUtil.removeInactiveJobcodes(claim.getServiceInformation());
        claimSubmissionUtil.removeInvalidJobCodes(claim.getServiceInformation(), getFailureStructure(claim));
		
        if(claim.getClaimCompetitorModel()!=null)
        {
        	claim.getClaimedItems().get(0).getItemReference()
                .setSerialized(false);

        }

		if (claim.getServiceInformation().getServiceDetail()
				.getHussmanPartsReplacedInstalled() != null
				&& !claim.getServiceInformation().getServiceDetail()
						.getHussmanPartsReplacedInstalled().isEmpty()) {
			prepareReplacedInstalledParts(claim);
		}

		if (isMatchReadApplicable()
				&& claim.getClaimedItems().get(0).getItemReference()
				.isSerialized()) {
			computeMatchReadScore(claim);
		}

		setTotalQtyForReplacedParts(claim);
		if (claim.getServiceInformation().getServiceDetail()
				.getLaborPerformed() != null) {
			for (LaborDetail labor : claim.getServiceInformation()
					.getServiceDetail().getLaborPerformed()) {
				if (labor.getEmptyAdditionalHours() != null
						&& labor.getEmptyAdditionalHours()) {
					labor.setAdditionalLaborHours(null);
				}
			}
		}
		setTotalLaborHoursForClaim(claim);

		if (claim.getAcceptanceReason() != null
				&& claim.getAcceptanceReason().getCode() == null) {
			claim.setAcceptanceReason(null);
		}

		if (claim.getAcceptanceReasonForCp() != null
				&& claim.getAcceptanceReasonForCp().getCode() == null) {
			claim.setAcceptanceReasonForCp(null);
		}
		if (claim.getRejectionReasons() != null				
				&& claim.getRejectionReasons().size()==0) {
			claim.setRejectionReasons(null);
		}

		if (claim.getAccountabilityCode() != null
				&& claim.getAccountabilityCode().getCode() == null) {
			claim.setAccountabilityCode(null);
		}

		if(task.isPartsClaim())
		{
			if(getConfigParamService()
				.getBooleanValue(ConfigName.CONSIDER_WARRANTY_COVERAGE_FOR_PART_CLAIM.getName()))
		setPolicyOnClaimedParts(claim);
		}
		else
		{
		setPolicyOnClaimedItems(claim);
		}  
		computePayment(claim);
		
		if (claim.getServiceInformation() != null) {
			if (claim.getServiceInformation().getServiceDetail() != null) {
				List<OEMPartReplaced> partsReplaced = claim
						.getServiceInformation().getServiceDetail()
						.getReplacedParts();
				for (OEMPartReplaced partReplaced : partsReplaced) {
					if (partReplaced.isPartToBeReturned()) {
						partReplaced.setPartReturn(getPartReturn(partReplaced));
					}
				}
			}
		}
		
		if (isProcessorReview()  &&
                null != task.getTakenTransition() && (task.getTakenTransition().equalsIgnoreCase("Accept") || task.getTakenTransition().equalsIgnoreCase("Hold")
                                                                                                                 || task.getTakenTransition().equalsIgnoreCase("Transfer"))) {
			this.claimService.updateOEMPartInformation(claim, null);
            List<OEMPartReplaced> removedParts = partReturnService.fetchRemovedParts(claim, null);
            if (!removedParts.isEmpty()) {
                partReturnProcessingService.endTasksForParts(removedParts);
                //If claim is re-opened and processor is removing some part then let's remove it from the wpra
                //and prepare due parts inbox too.
                if(getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())){
                    partReturnProcessingService.endPrepareDuePartsAndWpraTasksForParts(removedParts);
                }

            }
           // if(getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())){
            if(!claim.isNcr()){
                this.partReturnProcessingService.startPartReturnProcessForAllParts(claim);
            }
          //  }
            for (OEMPartReplaced removedPart : removedParts) {
               removedPart.setPartReturns(new ArrayList<PartReturn>());
            }
        } else if(isProcessorReview()){
            this.claimService.updateOEMPartInformation(claim, null);
            List<OEMPartReplaced> removedParts = partReturnService.fetchRemovedParts(claim, null);
            if (!removedParts.isEmpty()) {
                partReturnProcessingService.endTasksForParts(removedParts);
                //If claim is re-opened and processor is removing some part then let's remove it from the wpra
                //and prepare due parts inbox too.
                if(getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())){
                    partReturnProcessingService.endPrepareDuePartsAndWpraTasksForParts(removedParts);
                }

            }
            for (OEMPartReplaced removedPart : removedParts) {
                removedPart.setPartReturns(new ArrayList<PartReturn>());
            }

        }

		//Fix for SLMS-2043
        if(InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)){
        	PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
        	if(partsClaim.getPartInstalled() && 
        			(partsClaim.getCompetitorModelBrand() == null || partsClaim.getCompetitorModelBrand().isEmpty())){
        		if(claim.getHoursInService() != null && claim.getHoursOnTruck() != null){
        			claim.setHoursOnPart(claim.getHoursInService().subtract(claim.getHoursOnTruck()));
        		}
        	}
        }
        
		this.taskViewService.submitTaskView(this.task);
		updatePartReceivedCount(claim);
		addActionMessage("message.newClaim.re-processSuccess", claim
				.getClaimNumber());

	}
	
	private PartReturn getPartReturn(OEMPartReplaced partReplaced) {
		List<PartReturn> partReturns = partReplaced.getPartReturns();
		for (PartReturn partReturn : partReturns) {
			if (!partReturn.getStatus().equals(PartReturnStatus.REMOVED_BY_PROCESSOR)) {
				return partReturn;
			}
		}
		PartReturn partReturn = null;
		if (CollectionUtils.isNotEmpty(partReplaced.getPartReturns())) {
			partReturn = partReplaced.getPartReturns().get(0);
		}
		return partReturn;
	}

	private void prepareOEMPartCrossRef(Claim claim) {
		//TODO
		/*Organization organization = getLoggedInUser()
		.getBelongsToOrganization();

		if (InstanceOfUtil.isInstanceOfClass(Dealership.class, organization)) {
			prepareOEMCrossRefForDealer(claim);
		} else {
			prepareOEMCrossRefForInternalUser(claim);
		}*/
	}

	private void prepareOEMCrossRefForDealer(Claim claim) {
		if (claim.getServiceInformation().getCausalPart() != null) {
			Item item = getCatalogService().findPartForOEMDealerPart(claim
					.getServiceInformation().getCausalPart(), claim
					.getForDealerShip());
			if (item != null) {
				claim.getServiceInformation().setOemDealerCausalPart(
						claim.getServiceInformation().getCausalPart());
				claim.getServiceInformation().setCausalPart(item);
			}
		}
		if (claim.getServiceInformation().getServiceDetail() != null
				&& claim.getServiceInformation().getServiceDetail()
				.getOEMPartsReplaced() != null) {
			Iterator<OEMPartReplaced> oemPartReplacedIterator = claim
			.getServiceInformation().getServiceDetail()
			.getOEMPartsReplaced().iterator();
			while (oemPartReplacedIterator.hasNext()) {
				OEMPartReplaced oemPartReplaced = oemPartReplacedIterator
				.next();
				Item item = getCatalogService().findPartForOEMDealerPart(
						oemPartReplaced.getItemReference().getReferredItem(),
						claim.getForDealerShip());
				if (item != null) {
					oemPartReplaced.setOemDealerPartReplaced(oemPartReplaced
							.getItemReference().getReferredItem());
					oemPartReplaced.getItemReference().setReferredItem(item);
				}
			}
		}
	}

	private void prepareOEMCrossRefForInternalUser(Claim claim) {
		if (claim.getServiceInformation().getCausalPart() != null) {
			Item item = getCatalogService().findOEMDealerPartForPart(claim
					.getServiceInformation().getCausalPart(), claim
					.getForDealerShip());
			if (item != null) {
				claim.getServiceInformation().setOemDealerCausalPart(item);
			} else {
				claim.getServiceInformation().setOemDealerCausalPart(null);
			}
		}
		if (claim.getServiceInformation().getServiceDetail() != null
				&& claim.getServiceInformation().getServiceDetail()
				.getOEMPartsReplaced() != null) {
			Iterator<OEMPartReplaced> oemPartReplacedIterator = claim
			.getServiceInformation().getServiceDetail()
			.getOEMPartsReplaced().iterator();
			while (oemPartReplacedIterator.hasNext()) {
				OEMPartReplaced oemPartReplaced = oemPartReplacedIterator
				.next();
				Item item = getCatalogService().findOEMDealerPartForPart(
						oemPartReplaced.getItemReference().getReferredItem(),
						claim.getForDealerShip());
				if (item != null) {
					oemPartReplaced.setOemDealerPartReplaced(item);
				} else {
					oemPartReplaced.setOemDealerPartReplaced(null);
				}
			}
		}
	}

	public boolean isMatchReadApplicable() {
		return getConfigParamService()
		.getBooleanValue(ConfigName.MATCH_READ_APPLICABLITY.getName());
	}

	private void computeMatchReadScore(Claim claim) {
		long matchReadScore = 0;
		InventoryItem inventoryItem = claim.getClaimedItems().get(0)
		.getItemReference().getReferredInventoryItem();
		List<InventoryTransaction> transactionHistory = inventoryItem
		.getTransactionHistory();
		if (inventoryItem.getType().getType().equals(
				InventoryType.RETAIL.getType())
				&& ClaimState.DRAFT.getState().equals(
						claim.getState().getState())
						&& !(ClaimType.CAMPAIGN.getType().equals(claim.getType()
								.getType()))) {
			prepareMatchReadInfo(claim);
			int transactionSize = transactionHistory.size();
			InventoryTransaction latestTransaction = transactionHistory
			.get(transactionSize - 1);

			Address latestEndCustomerAddress = latestTransaction.getBuyer()
			.getAddress();
			MatchReadInfo matchReadInfo = claim.getMatchReadInfo();

			if (matchReadInfo.getOwnerCity().equalsIgnoreCase(
					latestEndCustomerAddress.getCity())) {
				matchReadScore += MatchReadMultiplyFactor.OWNER_CITY
				.getMultiplyFactor();
			}
			if (matchReadInfo.getOwnerName().equalsIgnoreCase(
					latestTransaction.getBuyer().getName())) {
				matchReadScore += MatchReadMultiplyFactor.OWNER_NAME
				.getMultiplyFactor();
			}
			if (matchReadInfo.getOwnerState().equalsIgnoreCase(
					latestEndCustomerAddress.getState())) {
				matchReadScore += MatchReadMultiplyFactor.OWNER_STATE
				.getMultiplyFactor();
			}
			if (matchReadInfo.getOwnerCountry().equalsIgnoreCase(
					latestEndCustomerAddress.getCountry())) {
				matchReadScore += MatchReadMultiplyFactor.OWNER_COUNTRY
				.getMultiplyFactor();
			}

			if (matchReadInfo.getOwnerZipcode().equalsIgnoreCase(
					latestEndCustomerAddress.getZipCode())) {
				matchReadScore += MatchReadMultiplyFactor.OWNER_ZIPCODE
				.getMultiplyFactor();
			}
			matchReadInfo.setScore(matchReadScore);
		}
	}

	protected void setTotalQtyForReplacedParts(Claim claim) {
		claimSubmissionUtil.setTotalQtyForReplacedParts(claim);
	}

	protected void setTotalLaborHoursForClaim(Claim claim) {
		List<LaborDetail> LaborDetails = claim.getServiceInformation()
		.getServiceDetail().getLaborPerformed();
		for (LaborDetail labor : LaborDetails) {
			if (labor != null) {
				labor
				.setHoursSpentForMultiClaim(labor
						.getHoursSpent()
						.multiply(
								new BigDecimal(
										getApprovedClaimedItems(claim))));
				if (labor.getAdditionalLaborHours() != null) {
					labor.setAdditionalHoursSpentForMultiClaim(labor
							.getAdditionalLaborHours().multiply(
									new BigDecimal(
											getApprovedClaimedItems(claim))));
				}
			}
		}
	}

	public boolean isProcessorReview() {
		return this.task.getBaseFormName().equals("processor_review");
	}

	void computePayment(Claim theClaim) {
		try {
			Payment payment = null;
			payment = this.paymentService.calculatePaymentForClaim(theClaim,null);
			theClaim.setPayment(payment);
		} catch (PaymentCalculationException e) {
			throw new RuntimeException(
					"Error occured while performing payment calculation.", e);
		}
	}

	private void updatePartReceivedCount(Claim claim) {
		if (ON_PART_RETURN
				.equals(getConfigParamService()
						.getStringValue(ConfigName.PART_RETURN_STATUS_TO_BE_CONSIDERED_FOR_PRC_MAX_QTY
								.getName()))) {
			for (OEMPartReplaced oemPartReplaced : claim
					.getServiceInformation().getServiceDetail()
					.getReplacedParts()) {
				if (oemPartReplaced != null
						&& oemPartReplaced.getItemReference() != null) {
					PartReturnConfiguration partReturnConfiguration = oemPartReplaced
					.getPartReturnConfiguration();
					if (partReturnConfiguration != null
							&& oemPartReplaced.getPartReturns() != null
							&& !oemPartReplaced.getPartReturns().isEmpty()
							&& partReturnConfiguration.getMaxQuantity() != null) {
						partReturnConfiguration
						.setQuantityReceived(partReturnConfiguration
								.getQuantityReceived()
								+ oemPartReplaced.getPartReturns()
								.size());
						partReturnService
						.updatePartReturnConfiguration(partReturnConfiguration);
					}					
				}
			}
		}
	}

	private void prepareMatchReadInfo(Claim claim) {
		if (!this.countriesFromMSA.contains(claim.getMatchReadInfo()
				.getOwnerCountry())) {
			claim.getMatchReadInfo().setOwnerState(getStateCode());
			claim.getMatchReadInfo().setOwnerCity(getCityCode());
			claim.getMatchReadInfo().setOwnerZipcode(getZipCode());
		}
	}

	public List<PaymentCondition> getPaymentConditions() {
		return paymentConditions;
	}

	public void setPaymentConditions(List<PaymentCondition> paymentConditions) {
		this.paymentConditions = paymentConditions;
	}

	public PartReturnService getPartReturnService() {
		return partReturnService;
	}

	public void setPartReturnService(PartReturnService partReturnService) {
		this.partReturnService = partReturnService;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	

	public PaymentService getPaymentService() {
		return paymentService;
	}

	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	public PartReturnProcessingService getPartReturnProcessingService() {
		return partReturnProcessingService;
	}

	public void setPartReturnProcessingService(
			PartReturnProcessingService partReturnProcessingService) {
		this.partReturnProcessingService = partReturnProcessingService;
	}

	public List<String> getCountriesFromMSA() {
		return countriesFromMSA;
	}

	public void setCountriesFromMSA(List<String> countriesFromMSA) {
		this.countriesFromMSA = countriesFromMSA;
	}

	public MSAService getMsaService() {
		return msaService;
	}

	public void setMsaService(MSAService msaService) {
		this.msaService = msaService;
	}

	

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	private String getTakenTransition(String action){
		if("multiReProcess".equals(action)){
			return REPROCESS;
		}
		return null; 
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


	public void setListCriteria(ListCriteria listCriteria) {
		ListCriteria criteria = new ListCriteria();
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageNumber(this.pageNo.intValue());
		pageSpecification.setPageSize(10);
		criteria.setPageSpecification(pageSpecification);
		this.listCriteria=criteria;
	}


	public List<Integer> getPageNoList() {
		return pageNoList;
	}


	public void setPageNoList(List<Integer> pageNoList) {
		this.pageNoList = pageNoList;
	}

	public RuleAdministrationService getRuleAdministrationService() {
		return ruleAdministrationService;
	}

	public void setRuleAdministrationService(
			RuleAdministrationService ruleAdministrationService) {
		this.ruleAdministrationService = ruleAdministrationService;
	}

	public Map<String, Set<String>> getClaimActionMap() {
		return claimActionMap;
	}

	public void setClaimActionMap(Map<String, Set<String>> claimActionMap) {
		this.claimActionMap = claimActionMap;
	}


	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit;
	}


	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}
	 
	public String getSelectedClaimsId() {
		return selectedClaimsId;
	}

	public void setSelectedClaimsId(String selectedClaimsId) {
		this.selectedClaimsId = selectedClaimsId;
	}
	
	void setPolicyOnClaimedItems(Claim theClaim) {
		claimSubmissionUtil.setPolicyOnClaimedItems(theClaim);
	}


	void setPolicyOnClaimedParts(Claim theClaim) {
		claimSubmissionUtil.setPolicyOnClaimedParts(theClaim);
	}


	public ClaimSubmissionUtil getClaimSubmissionUtil() {
		return claimSubmissionUtil;
	}


	public void setClaimSubmissionUtil(ClaimSubmissionUtil claimSubmissionUtil) {
		this.claimSubmissionUtil = claimSubmissionUtil;
	}
	
	private void prepareReplacedInstalledParts(Claim claim) {
		claimSubmissionUtil.prepareReplacedInstalledParts(claim);
	}
	
	private FailureStructure getFailureStructure(Claim claim) {
	        
	        Item causalPart = claim.getServiceInformation().getCausalPart();
	        
	        if (causalPart != null || ClaimType.CAMPAIGN.getType().equals(claim.getType().getType())) {
	            return this.failureStructureService.getFailureStructure(claim, causalPart);
	        } else {
	            return null;
	        }
	    }
	
		
}
