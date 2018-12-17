package tavant.twms.ognlcontext;

import tavant.twms.infra.IntegrationTestCase;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimRepository;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.rules.OgnlContextHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import junit.framework.Assert;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.BeansException;

/**
 * Created by IntelliJ IDEA.
 * User: rahul.k
 * Date: Feb 19, 2010
 * Time: 2:55:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class OgnlContextHelperTest extends IntegrationTestCase {

    ClaimRepository claimRepository;

    ClaimService claimService;

    BeanFactory beanFactory;

    public void testIsExchangeRateSetupForRepairDate() throws Exception {
        try {
        login("sedinap");
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit("Thermo King TSA");
        OgnlContextHelper context = new OgnlContextHelper(beanFactory);
        Claim claim = claimService.findClaim(new Long("1119887972760"));
        assertNotNull(claim);
        boolean response = context.isExchangeRateSetupForRepairDate(claim);
        Assert.assertTrue(response);
        } catch (Exception e) {
            throw e;
        }
    }

    public void setClaimRepository(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }


}
