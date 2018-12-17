package tavant.twms.integration.layer.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.domainlanguage.money.Money;

import tavant.twms.domain.policy.ExtWarrantyPlan;
import tavant.twms.external.ExtWarrantyRequest;
import tavant.twms.external.IntegrationBridge;
import tavant.twms.integration.layer.IntegrationRepositoryTestCase;

public class ExtWarrantyDebitSubmissionTest extends IntegrationRepositoryTestCase {
	
	private IntegrationBridge integrationBridge;

	public void testExtWarrantyDebitSubmission() {
		
		ExtWarrantyRequest request = createExtWarrantyRequest();
		
		//integrationBridge.submitExtWarrantyDebit(request);
	}

	private ExtWarrantyRequest createExtWarrantyRequest() {
		ExtWarrantyRequest request = new ExtWarrantyRequest();
		request.setDealerNo("4811");
		request.setItemNumber("523911038");
		request.setSerialNumber("128022112");
		request.setDescription("Golf Car");
		request.setPurchaseDate(new Date());
		
		List<ExtWarrantyPlan> plans = new ArrayList<ExtWarrantyPlan>();
		ExtWarrantyPlan extWarrantyPlan = new ExtWarrantyPlan();
		extWarrantyPlan.setPlanCode("AB08");
		extWarrantyPlan.setAmount(Money.dollars(39.38));
		extWarrantyPlan.setPlanItemNumber("AB08");
		plans.add(extWarrantyPlan);

		request.setPlans(plans);
		
		return request;
	}

	public void setIntegrationBridge(IntegrationBridge integrationBridge) {
		this.integrationBridge = integrationBridge;
	}

}


