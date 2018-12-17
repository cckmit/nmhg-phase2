package tavant.twms.external;

import java.util.List;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.PaymentCalculationException;
import tavant.twms.domain.claim.payment.PaymentService;
import tavant.twms.domain.policy.Policy;

public class MockPaymentService implements PaymentService {

	public Payment calculatePaymentForClaim(Claim theClaim)
			throws PaymentCalculationException {
		throw new UnsupportedOperationException();
	}

	public Payment calculatePaymentForClaimedItem(ClaimedItem claimedItem,
			Policy applicablePolicy) throws PaymentCalculationException {
		throw new UnsupportedOperationException();	}

	public List<CostCategory> findAllCostCategories() {
		throw new UnsupportedOperationException();	}

	public CostCategory findCostCategoryByCode(String categoryCode) {
		throw new UnsupportedOperationException();	}

	public void reopenClaimPayment(Claim claim) {
	}

	public Payment calculatePaymentForDeniedClaim(Claim claim)
			throws PaymentCalculationException {
		// TODO Auto-generated method stub
		return null;
	}

}
