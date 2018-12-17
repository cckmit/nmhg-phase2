package tavant.twms.external;

import java.util.Currency;
import java.util.List;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.payment.rates.PriceFetchData;


public class MockIntegrationBridgeImpl implements IntegrationBridge{

	private String processClaimWebServiceUrl;

	private String priceCheckWebServiceUrl;

	
	public void setProcessClaimWebServiceUrl(String processClaimWebServiceUrl) {
		this.processClaimWebServiceUrl = processClaimWebServiceUrl;
	}

	public void setPriceCheckWebServiceUrl(String priceCheckWebServiceUrl) {
		this.priceCheckWebServiceUrl = priceCheckWebServiceUrl;
	}

	public PriceCheckResponse checkPrice(Claim claim) {
		/**
		 * Mock class for test cases
		 */
		return null;
	}

	public void sendClaim(Claim claim) {
		/**
		 * Mock class for test cases
		 */
	}

	public PriceCheckResponse checkPrice(Claim claim,
			List<PriceFetchData> priceFetchList) {
		// TODO Auto-generated method stub
		return null;
	}

	public void submitExtWarrantyDebit(Claim claim) {
		// TODO Auto-generated method stub
		
	}

	public ExtWarrantyPriceCheckResponse checkPrice(
			ExtWarrantyRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	public void submitExtWarrantyDebit(
			ExtWarrantyRequest extWarrantyDebitSubmitRequest) {
		// TODO Auto-generated method stub
		
	}


	public void submitSupplierDebit(RecoveryClaim recoveryClaim) {
		// TODO Auto-generated method stub
		
	}

	public void sendRecoveryClaim(RecoveryClaim recoveryClaim) {
		// TODO Auto-generated method stub
		
	}

	public PriceCheckResponse checkPrice(PriceCheckRequest priceCheckRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
