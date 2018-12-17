package tavant.twms.domain.rules;

import junit.framework.TestCase;
import ognl.OgnlException;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.TravelDetail;

/**
 * OgnlContextHelper Tester.
 * 
 * @author <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * @version 1.0
 * @since
 * 
 * <pre>
 * 06 / 27 / 2007
 * </pre>
 */
public class OgnlContextHelperTest extends TestCase {

	private final OgnlContextHelper fixture = new OgnlContextHelper();

	public void testReplaceOgnlVariablesForQueryWithNoOgnl()
			throws OgnlException {
		String query = "claim.id != 1 and claim.serviceInformation.serviceDetail.travelDetails.trips >= 3";

		assertEquals(query, fixture.replaceOgnlVariables(query));
	}

	public void testReplaceOgnlVariablesForQueryWithOgnl() throws OgnlException {
		String query = "select count(*) from Claim claim where claim.id != ${claim.id}$ and "
				+ "claim.serviceInformation.serviceDetail.travelDetails.trips - "
				+ "${claim.serviceInformation.serviceDetail.travelDetails.trips}$ >= 3";

		Claim claim = new MachineClaim();
		claim.setId(99L);

		TravelDetail travelDetail = new TravelDetail();
		ServiceDetail serviceDetail = new ServiceDetail();
		serviceDetail.setTravelDetails(travelDetail);
		ServiceInformation serviceInformation = new ServiceInformation();
		serviceInformation.setServiceDetail(serviceDetail);
		claim.setServiceInformation(serviceInformation);

		travelDetail.setTrips(12);

		fixture.put("claim", claim);

		String replacedQuery = "select count(*) from Claim claim where "
				+ "claim.id != 99 and claim.serviceInformation.serviceDetail.travelDetails.trips - 12 >= 3";

		assertEquals(replacedQuery, fixture.replaceOgnlVariables(query));
	}

}
