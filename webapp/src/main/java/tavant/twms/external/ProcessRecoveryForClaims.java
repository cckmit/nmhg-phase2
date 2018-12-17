package tavant.twms.external;

import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.web.actions.TwmsActionSupport;
import tavant.twms.worklist.WorkListItemService;

@SuppressWarnings("serial")
public class ProcessRecoveryForClaims extends TwmsActionSupport{
   
	private static final Logger logger = Logger.getLogger(ProcessRecoveryForClaims.class);
	private ContractService contractService;
	private ClaimService claimService;
	private WorkListItemService workListItemService;
	
	@Override
    public String execute() {
        List<Claim> claimsForRecovery = fetchClaims();
        for (Claim claim : claimsForRecovery) {
        	try{
	        	String contractUpdateStatus = updateContract(claim);
	        	String internalComment = claim.getInternalComment();
	        	if ("UNIQUE".equalsIgnoreCase(contractUpdateStatus)) {//To Be Removed
	        		claim.setInternalComment(internalComment+" Created Recovery Claim (TSESA-150)");
	        		TaskInstance taskInstance=workListItemService.findTaskForClaimWithTaskName(claim.getId(), "ClosedClaim");
	    			this.workListItemService.endTaskWithTransition(taskInstance, "ProcessRecovery");
	        	}
        	}catch(Exception ex){
        		logger.error(" !!! TSESA:150 - Recovery claim creation failed for Claim Number "+claim.getClaimNumber());
        		logger.error(ex);
        	}
		}
        return SUCCESS;
    }

	private List<Claim> fetchClaims() {
		List<Claim> claimsForRecovery = claimService.findClaimsForRecovery();
		return claimsForRecovery;
	}
	
	private List<Claim> fetchClaims(int pageNumber, int pageSize) {
		List<Claim> claimsForRecovery = claimService.findClaimsForRecovery(pageNumber, pageSize);
		return claimsForRecovery;
	}
	
	public String updateContract(Claim claim) {
        Item causalPart = claim.getServiceInformation().getCausalPart();
        List<Contract> contracts = contractService.findContract(claim, causalPart, true);
        if(contracts == null || contracts.size() == 0){
        	return null;
        }
        
        if(contracts.size() > 1){
        	return "MULTIPLE";
        }
        if(contracts != null && contracts.size() == 1){
        	claim.getServiceInformation().setContract(contracts.get(0));
        	return "UNIQUE";
        }
        return null;
    }
	
	public String createRecoveryAsIs(){
		int pageSize = 50;
		int pageNumber = 1;
		List<Claim> claimsForRecovery = fetchClaims(pageNumber, pageSize);
		while(claimsForRecovery!=null && claimsForRecovery.size() > 0) {
	        for (Claim claim : claimsForRecovery) {
	        	try{
		        	String internalComment = claim.getInternalComment();
		        	if (claim.getServiceInformation().getContract() != null) {//To Be Removed
		        		claim.setInternalComment(internalComment+" Created Recovery Claim As Is (TSESA-150)");
		        		TaskInstance taskInstance=workListItemService.findTaskForClaimWithTaskName(claim.getId(), "ClosedClaim");
		    			this.workListItemService.endTaskWithTransition(taskInstance, "ProcessRecovery");
		        	}else{
		        		logger.error(" !!! TSESA:150 - No Contract for claim with Claim Number "+claim.getClaimNumber());
		        	}
	        	}catch(Exception ex){
	        		logger.error(" !!! TSESA:150 - Recovery claim creation failed for Claim Number "+claim.getClaimNumber());
	        		logger.error(ex);
	        	}
			}
	        pageNumber++;
	        claimsForRecovery = fetchClaims(pageNumber, pageSize);
		}
        return SUCCESS;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}
}
