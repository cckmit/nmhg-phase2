package tavant.twms.web.supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.StringUtils;


import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.UserComment;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.BasePartReturn;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfoService;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.ClaimProcessService;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.xforms.TaskView;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.WorkListItemService;

import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

@SuppressWarnings("serial")
public class SpecifySupplierRecoveryAction extends I18nActionSupport implements
		Preparable, Validateable {

	private RecoveryInfoService recoveryInfoService;

	private ClaimService claimService;
	
	private ContractService contractService;
	
	private WorkListItemService workListItemService;
	
	private TaskViewService taskViewService;
	
    private ConfigParamService configParamService;

	private RecoveryInfo recoveryInfo;

	private Claim claim;

	private List<Contract> contractForCausalPart;
	
	Map<OEMPartReplaced,List<Contract>> contractsForParts = new HashMap<OEMPartReplaced, List<Contract>>();
	
	private List<Contract> selectedContractForRemovedPart = new ArrayList<Contract>();
	
	private List<PartReturn> partReturnsAtQuantityLevel = new ArrayList<PartReturn>();
	
	public List<PartReturn> getPartReturnsAtQuantityLevel() {
		return partReturnsAtQuantityLevel;
	}

	private Contract selectedContractForCausalPart;

	private Long id;// this will be Task Id
	
	private TaskView task;
	
	private String comments;
	
	private List<OEMPartReplaced> completeListOfPartsReplaced = new ArrayList<OEMPartReplaced>();
	
	private boolean recoveryClaimsFlag = false;
	
	private String folderName;
	
	private boolean isActionSave = true;
	
	private boolean partLevel = true;
	
	private boolean levelChanged = false;

	private ClaimProcessService claimProcessService;
	
	private PartReturnProcessingService partReturnProcessingService;
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	public void prepare() throws Exception {
		if (claim == null) {
			claim = claimService.findClaim(getId());
		}
		
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim
				.getBusinessUnitInfo().getName());

		recoveryInfo = claim.getRecoveryInfo();
        if (recoveryClaimsFlag)
            contractService.createRecoveryClaims(this.recoveryInfo);

        if (recoveryInfo == null) {
            recoveryInfo = new RecoveryInfo();
            this.claim.setRecoveryInfo(recoveryInfo);
            recoveryInfo.setWarrantyClaim(claim);
        }

        if (isLevelChanged()
                && this.partLevel != this.recoveryInfo.isSavedAtPartLevel()) {
            recoveryInfo.getReplacedPartsRecovery().clear();
        } else if (!isLevelChanged()) {
            this.setPartLevel(this.recoveryInfo.isSavedAtPartLevel());
        }

        if (recoveryInfo.getCausalPartRecovery() != null) {
            selectedContractForCausalPart = recoveryInfo.getCausalPartRecovery().getContract();
        }

        populateListOfReplacedParts();//this is not required during operations on page 2
        populateValidContracts();// neither is this.this has significant performance hit
	}
	
	public String prepareSelectedContracts()
	{
		if (!contractService.isRecoverable(claim)) {
			addActionError("error.supplierRecovery.noEligibleContracts");
			return ERROR;
		}
		if (this.folderName!=null && this.folderName.equals("Pending Recovery Initiation") && this.task == null) {
			task = this.taskViewService.getTaskView(getId());
		}
		if (!this.isPartLevel()	&& !this.recoveryInfo.isSavedAtPartLevel()) {
			populateSelectedContractsForRemovedParts();
		}
		if(!this.isPartLevel())
			for(OEMPartReplaced partReplaced : this.completeListOfPartsReplaced) {
				List<PartReturn> activePartReturns = partReplaced.getActivePartReturns();
				if(!activePartReturns.isEmpty())
					partReturnsAtQuantityLevel.addAll(activePartReturns);
				else {
					for(int i=0; i<partReplaced.getNumberOfUnits(); i++)
						partReturnsAtQuantityLevel.add(null);
				}
				
			}
		return SUCCESS;
	}

	public String getReturnLocationForDisplay(PartReturn partReturn) {
		if(partReturn == null || partReturn.getReturnLocation()==null)
			return null;
		String returnLocation = partReturn.getReturnLocation().getCode();
		if(partReturn.getWarehouseLocation() != null) 
			returnLocation += (" ("+partReturn.getWarehouseLocation()+")");
		return returnLocation;
	}
	
	public String getReturnLocationForDisplay(OEMPartReplaced oemPartReplaced) {
		BasePartReturn bpr = oemPartReplaced.getActivePartReturn();
		String returnLocation = null;
		if(bpr != null && bpr.getReturnLocation() != null) {
			returnLocation = bpr.getReturnLocation().getCode();
			HashMap<String, Long> map = new HashMap<String, Long>();
			for(PartReturn pr : oemPartReplaced.getActivePartReturns()) {
				String warehouseLocation = pr.getWarehouseLocation();
				if(warehouseLocation != null) {
					if(map.containsKey(warehouseLocation))
						map.put(warehouseLocation, map.get(warehouseLocation)+1);
					else
						map.put(warehouseLocation, 1L);
				}
			}
			String bins = null;
			for(String bin : map.keySet()) {
				if(bins != null)
					bins = bins+(", "+bin+":"+map.get(bin));
				else
					bins = (bin+":"+map.get(bin));
			}
			if(bins != null)
				returnLocation += (" ("+bins+")");
		}
		return returnLocation;
	}
	
	public void populateSelectedContractsForRemovedParts()
	{
		for(OEMPartReplaced partReplaced : this.completeListOfPartsReplaced)
		{
			if(partReplaced.getItemReference().getUnserializedItem().equals(claim.getServiceInformation().getCausalPart())
					&& this.selectedContractForCausalPart != null){
				for(int i=0 ; i<partReplaced.getNumberOfUnits().intValue();i++){
					this.selectedContractForRemovedPart.add(null);
				}
			}else{
				
				int	parts = partReplaced.getNumberOfUnits();
				List<Long> infoObjectsUsedUp = new ArrayList<Long>();
				for(int i=0 ; i <parts ; i++){
					boolean foundSomething = false;
					for(RecoveryClaimInfo claimInfo :this.recoveryInfo.getReplacedPartsRecovery())
					{
						if(claimInfo.getContract()==null|| infoObjectsUsedUp.contains(claimInfo.getId()) || claimInfo.isCausalPartRecovery())
							continue;
						
					if(!foundSomething)//means you wont be finding any more contracts for this part
					{
						for(int k=0;k<parts-i;k++){
							this.selectedContractForRemovedPart.add(null);
						}
						break;
					}
				}
			  }
			}
		}
	}
	
	
		
	public void populateListOfReplacedParts() {
		this.completeListOfPartsReplaced.addAll(this.claim.getServiceInformation().getServiceDetail().getOemPartsReplaced());

		for (HussmanPartsReplacedInstalled hussPartReplaced : this.claim.getServiceInformation().getServiceDetail()
				.getHussmanPartsReplacedInstalled()) {
			this.completeListOfPartsReplaced.addAll(hussPartReplaced.getReplacedParts());
		}
	}
	
	public void populateValidContracts() {
		this.contractForCausalPart = contractService.findContract(claim, claim
				.getServiceInformation().getCausalPart(), true);

		for (OEMPartReplaced replacedPart : completeListOfPartsReplaced) {
			this.contractsForParts.put(replacedPart, 
					contractService.findContract(claim, replacedPart.getItemReference().getUnserializedItem(), false));
		}
	}
	
	private boolean supplierUsersAvailable(){
		if(selectedContractForCausalPart != null && selectedContractForCausalPart.getSupplier() != null){
			if(selectedContractForCausalPart.getSupplier().getUsers() == null
					|| selectedContractForCausalPart.getSupplier().getUsers().isEmpty()){
				return false;
			}
		}
		for(Contract contract : selectedContractForRemovedPart){
			if(contract != null && contract.getSupplier() != null){
				if(contract.getSupplier().getUsers() == null || contract.getSupplier().getUsers().isEmpty()){
					return false;
				}
			}
		}
		return true;
	}

	public String adjustRecoveryClaims()
	{
		if(!supplierUsersAvailable()){
			addActionError("error.supplier.noSupplier");
			return INPUT;
		}

        if(containsRecoveryClaim(this.recoveryInfo)){
            addActionError("error.common.simultaneousAction");
            return INPUT;
        }
		this.isActionSave = false;
		if(submitRecoveryInfo().equals(INPUT))
			return INPUT;
		contractService.createRecoveryClaims(this.recoveryInfo);
		if(isBuConfigAMER() && checkForPaymentSystemErrors(this.recoveryInfo.getWarrantyClaim())){
			return INPUT;	
		}
		return SUCCESS;
	}

    public Boolean containsRecoveryClaim(RecoveryInfo recoveryinfo) {

        for (RecoveryClaimInfo recClaimInfo : recoveryinfo.getReplacedPartsRecovery()) {
            if (recClaimInfo.getRecoveryClaim() != null && recClaimInfo.getRecoveryClaim().getRecoveryClaimState()!=null) {
                return true;
            }
        }
        return false;
    }


	public boolean checkForPaymentSystemErrors(Claim claim) {
		if(claim.getActiveClaimAudit().getIsPriceFetchReturnZero()){
			String[] zeroPricedPars = claim.getActiveClaimAudit()
					.getPriceFetchErrorMessage().split("#");
			addActionError("label.common.epo.system.has.return.zero.values.supplierrecovery.processor", zeroPricedPars[1]);
			return true;
		}
		return false;
	}
	
	public String initiateRecoveryClaims(){
		try {
            if(containsRecoveryClaim(this.recoveryInfo)){
                addActionError("error.common.simultaneousAction");
                return INPUT;
            }
			partReturnProcessingService.initiateRecoveryProcess(this.recoveryInfo);
			if(isBuConfigAMER() && checkForPaymentSystemErrors(this.recoveryInfo.getWarrantyClaim())){
				return INPUT;	
			}
		} catch (Exception e) {
			addActionError("error.initiateRecovery.unexpectedError");
			logger.error(e.getStackTrace());
			return INPUT;
		}
		claim.setPendingRecovery(false);
		claimService.updateClaim(claim);
		addActionMessage("message.supplierRecovery.recoveryInitiationSuccessful");
		return SUCCESS;
	}
	
	public String endRecoveryFlow() {
        if(containsRecoveryClaim(this.recoveryInfo)){
            addActionError("error.common.simultaneousAction");
            return INPUT;
        }
		claim.setPendingRecovery(false);
		recoveryInfo = claim.getRecoveryInfo();
		addUserCommentToRecoveryInfoObject();
		claimService.updateClaim(claim);
		addActionMessage("message.supplierRecovery.vendorRecoveryCancelledSuccessfully");
		return SUCCESS;
	}
	
	public String moveClaimToPendingRecoveryInitiation() {
		if (!contractService.isRecoverable(claim)) {
			addActionError("error.supplierRecovery.noElligibleContractsFound");
		} else {
			claim.setPendingRecovery(true);
			claimService.updateClaim(claim);
			addActionMessage("message.supplierRecovery.successfullyMovedToPendingRecoveryInitiationInbox");
		}
		return SUCCESS;
	}

	public String submitRecoveryInfo() {
		if (!this.selectedContractForRemovedPart.isEmpty()) {
			// this means that on the GUI the contract drop downs for the last parts in the replaced part
			// section are disabled due to some reason ,e.g they are same as Causal part and there is a causal part
			// contract selected.
			
			int expectedSizeOfList=0;
			if(isPartLevel())
				expectedSizeOfList = this.completeListOfPartsReplaced.size();
			else{
				for(OEMPartReplaced oemPartReplaced : this.completeListOfPartsReplaced)
				{
					expectedSizeOfList = expectedSizeOfList + oemPartReplaced.getNumberOfUnits().intValue();
				}
			}
			int increaseRequired= expectedSizeOfList - this.selectedContractForRemovedPart.size();
			for (int i = 0; i < increaseRequired ; i++) {
				this.selectedContractForRemovedPart.add(null);// because subsequent code expects the two lists to be of same size
			}
		}

		if (this.getClaim().getReopenRecoveryClaim().booleanValue()) {
			if (isPartLevel())
				return submitRecoveryInfoAtPartLevelForReopenedClaim();
			else
				return submitRecoveryInfoAtQuantityLevelForReopenedClaim();
		} else {
			if (isPartLevel())
				return submitRecoveryInfoAtPartLevel();
			else
				return submitRecoveryInfoAtQuantityLevel();
		}
	}
	
	private String submitRecoveryInfoAtPartLevelForReopenedClaim()
	{
		List<RecoveryClaimInfo> removalList = new ArrayList<RecoveryClaimInfo>();
		for (RecoveryClaimInfo recoveryClaimInfo : this.getRecoveryInfo().getReplacedPartsRecovery()) {
			if (recoveryClaimInfo.getRecoveryClaim() == null)
				removalList.add(recoveryClaimInfo);
		}
		this.getRecoveryInfo().getReplacedPartsRecovery().removeAll(removalList);
		removalList = null;
		
		if (selectedContractForCausalPart != null && selectedContractForCausalPart.getId() != null) {
			prepareCausalPartRecoveryClaimInfoForReopenedClaim();
			RecoveryClaimInfo causalPartRecoveryClaimInfo = this.recoveryInfo.getCausalPartRecovery();
			// start preparing the recovery info object from here
			if (selectedContractForCausalPart.getCollateralDamageToBePaid()) {
				populateCausalPartRecoveryClaimInfoforCollateral(causalPartRecoveryClaimInfo);
				for (RecoveryClaimInfo recoveryClaimInfo : this.getRecoveryInfo().getReplacedPartsRecovery()) {
					if (!recoveryClaimInfo.isCausalPartRecovery())
						recoveryClaimInfo.setContract(null);
				}
			} else {
				Map<Contract, List<RecoverablePart>> partContracts = new HashMap<Contract, List<RecoverablePart>>();
				prepareMapAtPartLevelWithCausalPartContract(partContracts);
				populateReplacedPartsRecoveryClaimInfoForReopenedClaim(partContracts);
			}
		} else {//no contract selected for causal part
			if (this.getRecoveryInfo().getCausalPartRecovery() != null)//i've already made sure that over here getCausalPartRecovery() will return only  
																	//that recoveryClaimInfo object which has a recovery claim in it which now needs to be denied
			{
				this.getRecoveryInfo().getCausalPartRecovery().setContract(null);//which means that the recovery claim in this object is to be denied
			}
			Map<Contract, List<RecoverablePart>> partContracts = new HashMap<Contract, List<RecoverablePart>>();
			if (INPUT.equals(prepareMapAtPartLevelWithNoCausalPartContract(partContracts)))
				return INPUT;
			populateReplacedPartsRecoveryClaimInfoForReopenedClaim(partContracts);
		}

		addUserCommentToRecoveryInfoObject();
		this.recoveryInfo.setSavedAtPartLevel(true);
		updateOemPartCostsOnRecoverableParts();
		this.recoveryInfoService.saveUpdate(this.recoveryInfo);
		if (isActionSave)
			addActionMessage("message.supplierRecovery.successMessage");
		return SUCCESS;
	}
	
	private String submitRecoveryInfoAtQuantityLevelForReopenedClaim() {

		List<RecoveryClaimInfo> removalList = new ArrayList<RecoveryClaimInfo>();
		for (RecoveryClaimInfo recoveryClaimInfo : this.getRecoveryInfo().getReplacedPartsRecovery()) {
			if (recoveryClaimInfo.getRecoveryClaim() == null)
				removalList.add(recoveryClaimInfo);
		}
		//here i'm just clearing off any recoveryClaimInfo objects that were created after the reopening of the warranty claim
		this.getRecoveryInfo().getReplacedPartsRecovery().removeAll(removalList);
		removalList = null;

		if (selectedContractForCausalPart != null && selectedContractForCausalPart.getId() != null) {
			prepareCausalPartRecoveryClaimInfoForReopenedClaim();
			RecoveryClaimInfo causalPartRecoveryClaimInfo = this.recoveryInfo.getCausalPartRecovery();
			if (selectedContractForCausalPart.getCollateralDamageToBePaid()) {
				populateCausalPartRecoveryClaimInfoforCollateral(causalPartRecoveryClaimInfo);
				for (RecoveryClaimInfo recoveryClaimInfo : this.getRecoveryInfo().getReplacedPartsRecovery()) {
					if (!recoveryClaimInfo.isCausalPartRecovery())
						recoveryClaimInfo.setContract(null);
				}
			} else {
				Map<Contract, List<RecoverablePart>> partContracts = prepareMapAtQuantityLevelWithCausalPartContract();
				populateReplacedPartsRecoveryClaimInfoForReopenedClaim(partContracts);
			}
		} else {// no casual part contract selected

			/*
			 * i've already made sure that over here getCausalPartRecovery() will return only that recoveryClaimInfo object which has a
			 * recovery claim in it which now needs to be denied
			 */
			if (this.getRecoveryInfo().getCausalPartRecovery() != null) {
				this.getRecoveryInfo().getCausalPartRecovery().setContract(null);
			}
			Map<Contract, List<RecoverablePart>> partContracts = new HashMap<Contract, List<RecoverablePart>>();
			if (INPUT.equals(prepareMapAtQuantityLevelWithNoCausalPartContract(partContracts)))
				return INPUT;
			populateReplacedPartsRecoveryClaimInfoForReopenedClaim(partContracts);
		}
		
		addUserCommentToRecoveryInfoObject();
		this.recoveryInfo.setSavedAtPartLevel(false);
		updateOemPartCostsOnRecoverableParts();
		this.recoveryInfoService.saveUpdate(this.recoveryInfo);
		if (isActionSave)
			addActionMessage("message.supplierRecovery.successMessage");
		return SUCCESS;

	}
	
	public String submitRecoveryInfoAtQuantityLevel(){
		if (!this.isCommentsValid()) {
			return INPUT;
		}
		if(recoveryInfo.getReplacedPartsRecovery()!=null)
		{
			RecoveryClaimInfo claimInfo = recoveryInfo.getCausalPartRecovery();
			recoveryInfo.getReplacedPartsRecovery().clear();
			recoveryInfo.setCausalPartRecovery(claimInfo);
		}
			
		if(selectedContractForCausalPart != null && selectedContractForCausalPart.getId() !=null)
		{
			RecoveryClaimInfo causalPartRecoveryClaimInfo =this.recoveryInfo.getCausalPartRecovery();
			if(causalPartRecoveryClaimInfo==null){
				causalPartRecoveryClaimInfo = new RecoveryClaimInfo();
				recoveryInfo.setCausalPartRecovery(causalPartRecoveryClaimInfo);
			}
			causalPartRecoveryClaimInfo.setContract(selectedContractForCausalPart);
			causalPartRecoveryClaimInfo.getRecoverableParts().clear();
			
			if (selectedContractForCausalPart.getCollateralDamageToBePaid()) {				
				populateCausalPartRecoveryClaimInfoforCollateral(causalPartRecoveryClaimInfo);
			}else{
				Map<Contract,List<RecoverablePart>> partContracts = prepareMapAtQuantityLevelWithCausalPartContract();
				createReplacedPartRecoveryClaimInfo(partContracts);
			}
		}else{//i.e there is no contract selected for causal part
			this.recoveryInfo.setCausalPartRecovery(null);
			Map<Contract,List<RecoverablePart>> partContracts = new HashMap<Contract, List<RecoverablePart>>();
			if(INPUT.equals(prepareMapAtQuantityLevelWithNoCausalPartContract(partContracts)))
				return INPUT;
			createReplacedPartRecoveryClaimInfo(partContracts);
		}
		
		addUserCommentToRecoveryInfoObject();
		this.recoveryInfo.setSavedAtPartLevel(false);
		updateOemPartCostsOnRecoverableParts();
		this.recoveryInfoService.saveUpdate(this.recoveryInfo);
		if(isActionSave)
			addActionMessage("message.supplierRecovery.successMessage");
		return SUCCESS;
	}
	
	private Map<Contract,List<RecoverablePart>> prepareMapAtQuantityLevelWithCausalPartContract(){
		Map<Contract,List<RecoverablePart>> partContracts = new HashMap<Contract, List<RecoverablePart>>();
		int selectedContractCounter = 0;
		for (OEMPartReplaced partReplaced : this.completeListOfPartsReplaced) {
			for (int i = 0; i < partReplaced.getNumberOfUnits().intValue(); i++) {
				if (selectedContractForRemovedPart.isEmpty() || selectedContractForRemovedPart.get(selectedContractCounter) == null) {
					// this can probably be optimised.if its null for one it will be null for all iterations in this loop.No need to check again and again
					populateCausalPartRecoveryClaimInfoforNonCollateral(partReplaced, i);
				} else if (selectedContractForRemovedPart.get(selectedContractCounter).getId() != null) {
					populateMapAtQuantityLevel(partContracts, partReplaced, i, selectedContractCounter);
				}
				selectedContractCounter++;
			}
		}
		return partContracts;
	}
	
	private void populateCausalPartRecoveryClaimInfoforCollateral(RecoveryClaimInfo claimInfo) {
		for (OEMPartReplaced oemPartReplaced : completeListOfPartsReplaced) {
			RecoverablePart removedPart = new RecoverablePart();
			removedPart.setOemPart(oemPartReplaced);
			removedPart.setQuantity(oemPartReplaced.getNumberOfUnits());
			
			claimInfo.addRecoverablePart(removedPart);
		}
	}
	
	public void populateCausalPartRecoveryClaimInfoforNonCollateral(OEMPartReplaced partReplaced, int partIndex) {
		RecoveryClaimInfo causalPartRecoveryClaimInfo = this.getRecoveryInfo().getCausalPartRecovery();
		if (claim.getServiceInformation().getCausalPart().equals(partReplaced.getItemReference().getUnserializedItem())) {
			if (causalPartRecoveryClaimInfo.getRecoverableParts().isEmpty()) {
				RecoverablePart part = new RecoverablePart();
				part.setOemPart(partReplaced);
				part.setQuantity(1);
				
				causalPartRecoveryClaimInfo.getRecoverableParts().add(part);
			} else {
				causalPartRecoveryClaimInfo.getRecoverableParts().get(0).setQuantity(
						causalPartRecoveryClaimInfo.getRecoverableParts().get(0).getQuantity() + 1);
			}
		}
	}

	private void prepareCausalPartRecoveryClaimInfoForReopenedClaim() {
		RecoveryClaimInfo causalPartRecoveryClaimInfo = this.recoveryInfo.getCausalPartRecovery();
		if (causalPartRecoveryClaimInfo == null || !causalPartRecoveryClaimInfo.getContract().equals(selectedContractForCausalPart)) {
			if (causalPartRecoveryClaimInfo!=null && !causalPartRecoveryClaimInfo.getContract().equals(selectedContractForCausalPart))
				causalPartRecoveryClaimInfo.setContract(null);// means the claim on this one has to be denied

			boolean foundSomething = false;
			/*
			 * here i am looking if there is some recovery claim which was created last to last time. This will come into play when the
			 * claim is reopened more than once
			 */
			for (RecoveryClaimInfo recoveryClaimInfo : this.getRecoveryInfo().getReplacedPartsRecovery()) {
				if (recoveryClaimInfo.getContract() == null && recoveryClaimInfo.getRecoveryClaim() != null
						&& recoveryClaimInfo.getRecoveryClaim().getContract().equals(selectedContractForCausalPart)) {
					recoveryClaimInfo.setContract(recoveryClaimInfo.getRecoveryClaim().getContract());
					recoveryClaimInfo.setCausalPartRecovery(true);
					recoveryClaimInfo.getRecoverableParts().clear();
					foundSomething = true;
				}
			}
			if (!foundSomething) {
				causalPartRecoveryClaimInfo = new RecoveryClaimInfo();
				causalPartRecoveryClaimInfo.setContract(selectedContractForCausalPart);
				recoveryInfo.setCausalPartRecovery(causalPartRecoveryClaimInfo);
			}

		} else {
			causalPartRecoveryClaimInfo.getRecoverableParts().clear();
		}
	}
	
	public void createReplacedPartRecoveryClaimInfo(Map<Contract,List<RecoverablePart>> partContracts)
	{
		for (Map.Entry<Contract, List<RecoverablePart>> entry :partContracts.entrySet()){
			RecoveryClaimInfo recoveryClaimInfo = new RecoveryClaimInfo();
			recoveryClaimInfo.setContract(entry.getKey());
			recoveryClaimInfo.setRecoverableParts(entry.getValue());
			this.recoveryInfo.addReplacedPartsRecovery(recoveryClaimInfo);
		}
	}
	
	private void populateReplacedPartsRecoveryClaimInfoForReopenedClaim(Map<Contract,List<RecoverablePart>> partContracts){
		
		for(RecoveryClaimInfo recoveryClaimInfo : this.getRecoveryInfo().getReplacedPartsRecovery())
		{
			if (recoveryClaimInfo.getContract() != null && !recoveryClaimInfo.isCausalPartRecovery()) {
				if (!partContracts.containsKey(recoveryClaimInfo.getContract()))
					recoveryClaimInfo.setContract(null);
				else {
					recoveryClaimInfo.getRecoverableParts().clear();
					recoveryClaimInfo.getRecoverableParts().addAll(partContracts.get(recoveryClaimInfo.getContract()));
					recoveryClaimInfo.setCausalPartRecovery(false);
					partContracts.remove(recoveryClaimInfo.getContract());
				}
			}
			else if (recoveryClaimInfo.getContract() == null && partContracts.containsKey(recoveryClaimInfo.getRecoveryClaim().getContract()))
			{
				recoveryClaimInfo.setContract(recoveryClaimInfo.getRecoveryClaim().getContract());
				recoveryClaimInfo.setCausalPartRecovery(false);
				recoveryClaimInfo.getRecoverableParts().clear();
				recoveryClaimInfo.getRecoverableParts().addAll(partContracts.get(recoveryClaimInfo.getContract()));
				partContracts.remove(recoveryClaimInfo.getContract());
			}
		}
		createReplacedPartRecoveryClaimInfo(partContracts);
	}
	
	
	private void prepareMapAtPartLevelWithCausalPartContract(Map<Contract, List<RecoverablePart>> partContracts) {
		for (int i = 0; i < completeListOfPartsReplaced.size(); i++) {
			if (selectedContractForRemovedPart.isEmpty() || selectedContractForRemovedPart.get(i) == null) {// replaced parts has only the
																											// causal part
				if (claim.getServiceInformation().getCausalPart().equals(
						completeListOfPartsReplaced.get(i).getItemReference().getUnserializedItem())) {
					addOemPartToCausalPartRecoveryClaimInfo(completeListOfPartsReplaced.get(i));
				}
			} else if (selectedContractForRemovedPart.get(i).getId() != null) {
				populateMapAtPartLevel(partContracts, selectedContractForRemovedPart.get(i), completeListOfPartsReplaced.get(i));
			}
		}
	}
	
	private String prepareMapAtQuantityLevelWithNoCausalPartContract(Map<Contract, List<RecoverablePart>> partContracts) {
		int selectedContractCounter = 0;
		boolean atleastOneContractSelected = false;
		for (OEMPartReplaced partReplaced : this.completeListOfPartsReplaced) {
			for (int i = 0; i < partReplaced.getNumberOfUnits().intValue(); i++) {
				if (selectedContractForRemovedPart.get(selectedContractCounter).getId() != null) {
					atleastOneContractSelected = true;
					populateMapAtQuantityLevel(partContracts, partReplaced, i, selectedContractCounter);
				}
				selectedContractCounter++;
			}
		}
		if (!atleastOneContractSelected && !isActionSave()) {
			addActionError("error.supplierRecovery.noSelectedContracts");
			return INPUT;
		}
		return null;
	}
	
	
	private String prepareMapAtPartLevelWithNoCausalPartContract(Map<Contract,List<RecoverablePart>> partContracts){
		boolean atleastOneContractSelected = false;
		
		for(int i = 0 ; i< selectedContractForRemovedPart.size() ; i++){
			if(selectedContractForRemovedPart.get(i) != null 
					&& selectedContractForRemovedPart.get(i).getId() != null )
			{
				atleastOneContractSelected = true;
				populateMapAtPartLevel(partContracts,selectedContractForRemovedPart.get(i),completeListOfPartsReplaced.get(i));
			}
		}
		if(!atleastOneContractSelected && !isActionSave()){
			addActionError("error.supplierRecovery.noSelectedContracts");
			return INPUT;
		}
		return null;
	}
	
	public String submitRecoveryInfoAtPartLevel() {
		if (!this.isCommentsValid()) {
			return INPUT;
		}
		if(recoveryInfo.getReplacedPartsRecovery()!=null)
		{
			RecoveryClaimInfo claimInfo = this.recoveryInfo.getCausalPartRecovery();
			recoveryInfo.getReplacedPartsRecovery().clear();
			recoveryInfo.setCausalPartRecovery(claimInfo);
		}
		
		if(selectedContractForCausalPart != null && selectedContractForCausalPart.getId() !=null)
		{
			RecoveryClaimInfo causalPartRecoveryClaimInfo =this.recoveryInfo.getCausalPartRecovery();
			if(causalPartRecoveryClaimInfo==null){
				causalPartRecoveryClaimInfo = new RecoveryClaimInfo();
				recoveryInfo.setCausalPartRecovery(causalPartRecoveryClaimInfo);
			}
			causalPartRecoveryClaimInfo.setContract(selectedContractForCausalPart);
			causalPartRecoveryClaimInfo.getRecoverableParts().clear();
			
			if (selectedContractForCausalPart.getCollateralDamageToBePaid()) {				
				populateCausalPartRecoveryClaimInfoforCollateral(causalPartRecoveryClaimInfo);
			} else {
				Map<Contract,List<RecoverablePart>> partContracts = new HashMap<Contract, List<RecoverablePart>>();
				prepareMapAtPartLevelWithCausalPartContract(partContracts);
				createReplacedPartRecoveryClaimInfo(partContracts);
			}			
		}
		else//no causal part contract selected
		{
			this.recoveryInfo.setCausalPartRecovery(null);
			Map<Contract,List<RecoverablePart>> partContracts = new HashMap<Contract, List<RecoverablePart>>();
			
			if(INPUT.equals(prepareMapAtPartLevelWithNoCausalPartContract(partContracts)))
				return INPUT;
			createReplacedPartRecoveryClaimInfo(partContracts);			
		}
		
		addUserCommentToRecoveryInfoObject();		
		this.recoveryInfo.setSavedAtPartLevel(true);
		updateOemPartCostsOnRecoverableParts();
		this.recoveryInfoService.saveUpdate(recoveryInfo);
		if(isActionSave)
			addActionMessage("message.supplierRecovery.successMessage");
		return SUCCESS;
	}
	
	private void populateMapAtPartLevel(Map<Contract,List<RecoverablePart>> partContracts ,Contract contract , OEMPartReplaced oemPartReplaced)
	{
		if(partContracts.containsKey(contract)){
			RecoverablePart recoverablePart = new RecoverablePart(oemPartReplaced,oemPartReplaced.getNumberOfUnits());
			partContracts.get(contract).add(recoverablePart);
		}
		else{
			List<RecoverablePart> partsCovered = new ArrayList<RecoverablePart>();
			partsCovered.add(new RecoverablePart(oemPartReplaced,oemPartReplaced.getNumberOfUnits()));
			partContracts.put(contract, partsCovered);
		}
	}
	
	private void populateMapAtQuantityLevel(Map<Contract,List<RecoverablePart>> partContracts,OEMPartReplaced partReplaced,int i,int selectedContractCounter )
	{
		if(partContracts.containsKey(selectedContractForRemovedPart.get(selectedContractCounter)))
		{
			boolean oemPartAlreadyAdded = false;
			for(RecoverablePart recoverablePart:
				partContracts.get(selectedContractForRemovedPart.get(selectedContractCounter)))
			{
				if(recoverablePart.getOemPart().equals(partReplaced)){
					recoverablePart.setQuantity(recoverablePart.getQuantity()+1);
					oemPartAlreadyAdded=true;
					break;
				}
			}
			if(!oemPartAlreadyAdded)
			{
				RecoverablePart part = new RecoverablePart();
				part.setOemPart(partReplaced);
				part.setQuantity(1);
				
				partContracts.get(selectedContractForRemovedPart.get(selectedContractCounter)).
						add(part);
			}
		}
		else{
			RecoverablePart recoverablePart = new RecoverablePart();
			recoverablePart.setOemPart(partReplaced);
			recoverablePart.setQuantity(1);
			
			List<RecoverablePart> list = new ArrayList<RecoverablePart>();
			list.add(recoverablePart);
			partContracts.put(selectedContractForRemovedPart.get(selectedContractCounter), 
					list);
		}
	}
	
	private void addOemPartToCausalPartRecoveryClaimInfo(OEMPartReplaced partReplaced) {
		RecoverablePart causalPart = new RecoverablePart();
		causalPart.setQuantity(partReplaced.getNumberOfUnits());
		causalPart.setOemPart(partReplaced);
		
		recoveryInfo.getCausalPartRecovery().addRecoverablePart(causalPart);
	}

	public List<RecoveryClaimInfo> getCompleteListOfRecoveryClaimInfoObjects()
	{
		List<RecoveryClaimInfo> list = new ArrayList<RecoveryClaimInfo>();
		list.add(this.recoveryInfo.getCausalPartRecovery());
		if(this.recoveryInfo.getReplacedPartsRecovery() != null)
			list.addAll(this.recoveryInfo.getReplacedPartsRecovery());
		
		return list;
	}
	
	public Contract getSelectedContractForOemPart(OEMPartReplaced oemPart) {
		if (oemPart.getItemReference().getUnserializedItem().equals(
				this.claim.getServiceInformation().getCausalPart())
				&& this.selectedContractForCausalPart != null) {
			return null;
		} else {
			for (RecoveryClaimInfo claimInfo : this.recoveryInfo.getReplacedPartsRecovery()) {
				if(claimInfo.getContract()!=null && !claimInfo.isCausalPartRecovery()){
					for (RecoverablePart part : claimInfo.getRecoverableParts()) {
						if (part.getOemPart().equals(oemPart))
							return claimInfo.getContract();
					}
				}
			}
			return null;
		}
	}
	
	private boolean isCommentsValid()
	{
		if (!StringUtils.hasText(this.comments)&& 
				!this.folderName.equals("Pending Recovery Initiation")) {
			addActionError("error.supplierRecovery.commentsMandatory");
			return false;
		}
		else
			return true;
	}
	
	private void addUserCommentToRecoveryInfoObject() {
		if (StringUtils.hasText(this.comments)) {
			UserComment latestComment = new UserComment();
			latestComment.setComment(comments);
			latestComment.setMadeBy(getLoggedInUser());
			latestComment.setMadeOn(Clock.now());
			recoveryInfo.addComments(latestComment);
		}
	}

	public boolean isShowPartSerialNumber() {
		return getConfigParamService()
					.getBooleanValue(
							ConfigName.SHOW_PART_SN_ON_INSTALLED_REMOVED_SECTION
									.getName());
	}
	
	private void updateOemPartCostsOnRecoverableParts() {
		if(!configParamService.getBooleanValue(ConfigName.IS_SUPPLIER_NEEDED_FOR_COST_FETCH.getName()))
		{
			for (RecoveryClaimInfo replaceRecClaimInfo : this.getRecoveryInfo().getReplacedPartsRecovery()) {
				for (RecoverablePart recoverablePart : replaceRecClaimInfo.getRecoverableParts()) {
					if (recoverablePart.getOemPart() != null) {
						recoverablePart.setMaterialCost(recoverablePart.getOemPart().getMaterialCost());
						recoverablePart.setCostPricePerUnit(recoverablePart.getOemPart().getCostPricePerUnit());
					}
				}
			}
		}
	}

	public RecoveryInfoService getRecoveryInfoService() {
		return recoveryInfoService;
	}

	public void setRecoveryInfoService(RecoveryInfoService recoveryInfoService) {
		this.recoveryInfoService = recoveryInfoService;
	}

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public ContractService getContractService() {
		return contractService;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public List<Contract> getContractForCausalPart() {
		return contractForCausalPart;
	}

	public void setContractForCausalPart(List<Contract> contractForCausalPart) {
		this.contractForCausalPart = contractForCausalPart;
	}

	
	public List<Contract> getContractsForRemovedPart(OEMPartReplaced partReplaced)
	{
		return contractsForParts.get(partReplaced);		
	}

	public RecoveryInfo getRecoveryInfo() {
		return recoveryInfo;
	}

	public void setRecoveryInfo(RecoveryInfo recoveryInfo) {
		this.recoveryInfo = recoveryInfo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Map<OEMPartReplaced, List<Contract>> getContractsForParts() {
		return contractsForParts;
	}

	public void setContractsForParts(
			Map<OEMPartReplaced, List<Contract>> contractsForParts) {
		this.contractsForParts = contractsForParts;
	}

	public List<Contract> getSelectedContractForRemovedPart() {
		return selectedContractForRemovedPart;
	}

	public void setSelectedContractForRemovedPart(
			List<Contract> selectedContractForRemovedPart) {
		this.selectedContractForRemovedPart = selectedContractForRemovedPart;
	}
	
	public List<OEMPartReplaced> getCompleteListOfPartsReplaced() {
		return completeListOfPartsReplaced;
	}

	public void setCompleteListOfPartsReplaced(
			List<OEMPartReplaced> completeListOfPartsReplaced) {
		this.completeListOfPartsReplaced = completeListOfPartsReplaced;
	}

	public Contract getSelectedContractForCausalPart() {
		return selectedContractForCausalPart;
	}

	public void setSelectedContractForCausalPart(
			Contract selectedContractForCausalPart) {
		this.selectedContractForCausalPart = selectedContractForCausalPart;
	}
		
	public boolean isRecoveryClaimsFlag() {
		return recoveryClaimsFlag;
	}

	public void setRecoveryClaimsFlag(boolean recoveryClaimsFlag) {
		this.recoveryClaimsFlag = recoveryClaimsFlag;
	}

	public WorkListItemService getWorkListItemService() {
		return workListItemService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public TaskViewService getTaskViewService() {
		return taskViewService;
	}

	public void setTaskViewService(TaskViewService taskViewService) {
		this.taskViewService = taskViewService;
	}

	public TaskView getTask() {
		return task;
	}

	public void setTask(TaskView task) {
		this.task = task;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public boolean isActionSave() {
		return isActionSave;
	}

	public void setActionSave(boolean isActionSave) {
		this.isActionSave = isActionSave;
	}

	public boolean isPartLevel() {
		return partLevel;
	}

	public void setPartLevel(boolean partLevel) {
		this.partLevel = partLevel;
	}

	public boolean isLevelChanged() {
		return levelChanged;
	}

	public void setLevelChanged(boolean levelChanged) {
		this.levelChanged = levelChanged;
	}

	public ClaimProcessService getClaimProcessService() {
		return claimProcessService;
	}

	public void setClaimProcessService(ClaimProcessService claimProcessService) {
		this.claimProcessService = claimProcessService;
	}
	
	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
	public void setPartReturnProcessingService(
			PartReturnProcessingService partReturnProcessingService) {
		this.partReturnProcessingService = partReturnProcessingService;
	}

}
