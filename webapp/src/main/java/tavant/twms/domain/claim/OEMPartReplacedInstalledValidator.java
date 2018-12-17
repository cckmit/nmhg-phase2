package tavant.twms.domain.claim;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ExpressionValidator;

/***
 * Custom validator for validating Replaced/Installed Part based on TSA's requirement
 * @author ramalakshmi.p
 *
 */
public class OEMPartReplacedInstalledValidator extends ExpressionValidator{
	
	ConfigParamService configParamService;
	ClaimService claimService;
	InventoryService inventoryService;
	

	@Override
	public void validate(Object object) throws ValidationException {

		HussmanPartsReplacedInstalled replacedInstalledPart = (HussmanPartsReplacedInstalled) object;
		ValueStack ognlStack = ActionContext.getContext().getValueStack();
		Claim claim = (Claim) ognlStack.findValue(getExpression());
		if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)) {
			PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
			if (!partsClaim.getPartInstalled())
				return;
		}
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		if (!configParamService.getBooleanValue(ConfigName.BUPART_REPLACEABLEBY_NONBUPART.getName())) {
			setActionErrors(claimService.validateReplacedParts(replacedInstalledPart, claim));
		}
	}
	
	public void setActionErrors(Map<String, String[]> errorCodes) {
        Iterator<String> iterator = errorCodes.keySet().iterator();
        while (iterator.hasNext()) {
            String errorKey = iterator.next();
            String[] errorValue = errorCodes.get(errorKey);
            if(errorValue == null) {
            	getValidatorContext().addActionError(errorKey);
            } else {
            	getValidatorContext().addActionError(getValidatorContext().getText(errorKey, errorValue));
            }
        }
    }

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	
}
