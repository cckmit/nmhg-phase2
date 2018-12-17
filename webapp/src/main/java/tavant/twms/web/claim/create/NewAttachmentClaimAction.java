package tavant.twms.web.claim.create;

import tavant.twms.domain.claim.AttachmentClaim;
import tavant.twms.domain.claim.Claim;


public class NewAttachmentClaimAction extends NewSerializedClaimAction {

    public NewAttachmentClaimAction() {
        super();
    }

    private AttachmentClaim claim;

    public AttachmentClaim getClaim() {
        return claim;
    }

    public void setClaim(AttachmentClaim claim) {
        this.claim = claim;
    }

    @Override
    public Claim getClaimDetail() {
        return claim;
    }

}
