package tavant.twms.jbpm.infra;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.TaskInstanceFactory;
import org.jbpm.taskmgmt.exe.TaskInstance;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.supplier.SupplierPartReturn;

public class CustomTaskInstanceFactoryImpl implements TaskInstanceFactory {

    public TaskInstance createTaskInstance(ExecutionContext executionContext) {
        CustomTaskInstance ti = new CustomTaskInstance();
        if ("ClaimSubmission".equals(executionContext.getProcessDefinition().getName())) {
            Claim claim = (Claim) executionContext.getVariable("claim");
            ti.setClaimId(claim.getId());
        }
        else if("PartsReturn".equals(executionContext.getProcessDefinition().getName())){
        	PartReturn partReturn = (PartReturn) executionContext.getVariable("partReturn");
        	ti.setPartReturnId(partReturn.getId());
        	Claim claim = (Claim) executionContext.getVariable("claim");
            ti.setClaimId(claim.getId());
        }
        else if("SupplierRecovery".equals(executionContext.getProcessDefinition().getName())){
        	RecoveryClaim recoveryClaim = (RecoveryClaim) executionContext.getVariable("recoveryClaim");
            ti.setClaimId(recoveryClaim.getId());
        }
        else if("SupplierPartReturn".equals(executionContext.getProcessDefinition().getName())){
        	RecoveryClaim recoveryClaim = (RecoveryClaim) executionContext.getVariable("recoveryClaim");
            ti.setClaimId(recoveryClaim.getId());
            SupplierPartReturn supplierPartReturn = (SupplierPartReturn) executionContext.getVariable("supplierPartReturn");
        	ti.setPartReturnId(supplierPartReturn.getId());
        }
        return ti;
    }
}
