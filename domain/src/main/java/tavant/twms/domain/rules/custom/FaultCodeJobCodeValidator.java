package tavant.twms.domain.rules.custom;

import tavant.twms.domain.rules.SystemDefinedBusinessCondition;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.HibernateCast;

import java.util.SortedMap;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Apr 27, 2009
 * Time: 11:05:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class FaultCodeJobCodeValidator  implements
        SystemDefinedBusinessCondition {

    public boolean execute(SortedMap<String, Object> ruleExecutionContext) {
		Claim claim = (Claim) ruleExecutionContext.get("claim");
		return !isFaultCodeandJobCodeAreExactMatch(claim);
	}

    private boolean isFaultCodeandJobCodeAreExactMatch(Claim claim){
        boolean isExactMatch = false;
        PartsClaim partsClaim = null;
        if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)) {
			partsClaim = new HibernateCast<PartsClaim>().cast(claim);
        }
        if(partsClaim==null || (partsClaim!=null && partsClaim.getPartInstalled())){
            String faultCodeDefinitionCode = claim.getServiceInformation().getFaultCodeRef().getDefinition().getCode();
            if(claim.getServiceInformation().getServiceDetail().getLaborPerformed().isEmpty()){
                isExactMatch = true;
            }
            for (LaborDetail laborDetail : claim.getServiceInformation().getServiceDetail().getLaborPerformed()) {
                String jobCodeDefinitionCode = laborDetail.getServiceProcedure().getDefinition().getCode();
                if(jobCodeDefinitionCode.contains(faultCodeDefinitionCode)){
                    isExactMatch=true;
                    break;
                }
            }
        }
        if(partsClaim!=null && !partsClaim.getPartInstalled())
        {
        	//This rule is not applicable for a parts claim w/r host as neither FC nor JC is captured there
        	isExactMatch = true;
        }
        return isExactMatch;
    }
}
