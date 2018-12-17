package tavant.twms.domain.claim;

import java.util.List;

import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.claimsubmission.ClaimSubmissionUtil;
import tavant.twms.domain.failurestruct.Assembly;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.FailureStructureService;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ExpressionValidator;

//TODO: Should not extend ExpressionValidation...just use ValidatorSupport
public class ServiceInformationValidator extends ExpressionValidator {

    private ClaimSubmissionUtil claimSubmissionUtil;

    private FailureStructureService failureStructureService;

    private ServiceInformation si;

    private FailureStructure fs;

    @Override
    public void validate(Object object) throws ValidationException {
        this.si = (ServiceInformation) object;
        Claim claim = (Claim) ActionContext.getContext().getValueStack().findValue(getExpression());
        this.fs = failureStructureService.getFailureStructure(claim, si.getCausalPart());

        validateFaultCode();
        validateForInactiveJobcodes();
        validateForInvalidJobCodes();
    }

    private void validateFaultCode() {
        if (si.getFaultCodeRef() == null) {
            return;
        }
        if(fs!=null){
        Assembly assembly = fs.getAssembly(si.getFaultCode());
        if (assembly == null || assembly.getFaultCode() == null) {
            getValidatorContext().addActionError("error.newClaim.faultCodeInvalid");
        }
        }
        //Assembly assembly1 = fs.getAssembly(si.getCausedBy());
        FailureCauseDefinition causedBy =si.getCausedBy();        
        if (causedBy == null || causedBy.getId() == null) {
            getValidatorContext().addActionError("error.newClaim.invalidCausedBy");
        }
    }

    private void validateForInvalidJobCodes() {
        List<String> invalidJobCodes = claimSubmissionUtil.removeInvalidJobCodes(si, fs);
        if (invalidJobCodes.size() > 0) {
            getValidatorContext().addActionMessage(getValidatorContext().getText("error.newClaim.jobCodeInvalid",
                    new String[] { StringUtils.collectionToDelimitedString(invalidJobCodes, ", ") } ));
        }
    }

    private void validateForInactiveJobcodes() {
        List<String> inactiveJobCodes = claimSubmissionUtil.removeInactiveJobcodes(si);
        if (inactiveJobCodes.size() > 0) {
            getValidatorContext().addActionMessage(getValidatorContext().getText("error.newClaim.jobCodeInactive",
                    new String[]{StringUtils.collectionToDelimitedString(inactiveJobCodes, ", ")}));
        }
    }

    public void setClaimSubmissionUtil(ClaimSubmissionUtil claimSubmissionUtil) {
        this.claimSubmissionUtil = claimSubmissionUtil;
    }

    public void setFailureStructureService(FailureStructureService failureStructureService) {
        this.failureStructureService = failureStructureService;
    }

}
