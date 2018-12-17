package tavant.twms.external;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;


public interface IntegrationBridge {
	
    public void sendClaim(Claim claim);
    
    public void sendRecoveryClaim(RecoveryClaim recoveryClaim);
    
    public PriceCheckResponse checkPrice(Claim claim);

    public PriceCheckResponse checkPrice(PriceCheckRequest priceCheckRequest,Claim claim);
    
    public void submitExtWarrantyDebit(ExtWarrantyRequest extWarrantyDebitSubmitRequest);
    
    public ExtWarrantyPriceCheckResponse checkPrice(ExtWarrantyRequest request);

}
