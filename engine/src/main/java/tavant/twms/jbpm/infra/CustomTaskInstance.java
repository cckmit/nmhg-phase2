package tavant.twms.jbpm.infra;

import org.jbpm.taskmgmt.exe.TaskInstance;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;

public class CustomTaskInstance extends TaskInstance {

    protected Long claimId;
    
    protected Long partReturnId;

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }
    
    public Long getPartReturnId() {
        return partReturnId;
    }

    public void setPartReturnId(Long partReturnId) {
        this.partReturnId = partReturnId;
    }
	
	@Override
    public void setActorId(String actorId) {
        super.setActorId(actorId);
        if ("ClaimSubmission".equals(getTaskMgmtInstance().getProcessInstance().getProcessDefinition().getName()) &&
                !"Forwarded Internally".equals(getName()) && !"Forwarded Externally".equals(getName())) {
            Claim claim = (Claim) getTaskMgmtInstance().getProcessInstance().getContextInstance().getVariable("claim");
            OrgService orgService = (OrgService) new BeanLocator().lookupBean("orgService");
            User assignToUser = orgService.findUserByName(actorId);
            claim.setAssignToUser(assignToUser);
        }
    }
}
