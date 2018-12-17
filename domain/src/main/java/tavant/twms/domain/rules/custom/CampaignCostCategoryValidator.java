package tavant.twms.domain.rules.custom;

import java.util.SortedMap;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.validation.CampaignClaimValidationService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.rules.SystemDefinedBusinessCondition;

public class CampaignCostCategoryValidator implements
		SystemDefinedBusinessCondition {
	CampaignClaimValidationService campaignClaimValidationService;	

	public boolean execute(SortedMap<String, Object> ruleExecutionContext) {
		Claim claim = (Claim) ruleExecutionContext.get("claim");
		
		if(claim.getType().equals(ClaimType.CAMPAIGN))
		{
			//Validate API returns
			
			return (!campaignClaimValidationService.isClaimWithinCampaignLimits(claim));			
		}
		return false;
	}
	
	
	public CampaignClaimValidationService getCampaignClaimValidationService() {
		return campaignClaimValidationService;
	}

	public void setCampaignClaimValidationService(
			CampaignClaimValidationService campaignClaimValidationService) {
		this.campaignClaimValidationService = campaignClaimValidationService;
	}

}
