package tavant.twms.domain.claim;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import tavant.twms.domain.catalog.ItemReference;

@Entity
@DiscriminatorValue("CAMPAIGN")
public class CampaignClaim extends Claim {

    public CampaignClaim() {
        super();
    }

    @Override
    public ClaimType getType() {
        return ClaimType.CAMPAIGN;
    }

    @Override
    public boolean canPolicyBeComputed() {
        return false;
    }

    public boolean canPolicyBeComputedForClaimedItem(ClaimedItem claimedItem) {
        return false;
    }    
    
}
