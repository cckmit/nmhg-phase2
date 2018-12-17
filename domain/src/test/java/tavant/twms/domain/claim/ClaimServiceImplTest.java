/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.claim;


import java.util.Currency;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.PaymentService;

public class ClaimServiceImplTest extends MockObjectTestCase {

    ClaimServiceImpl fixture = new ClaimServiceImpl();

    Mock claimFactoryMock;

    Mock claimRepositoryMock;

    Mock claimMock;

    Mock paymentServiceMock;
    
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        claimRepositoryMock = mock(ClaimRepository.class);
        fixture.setClaimRepository((ClaimRepository) claimRepositoryMock.proxy());

        paymentServiceMock = mock(PaymentService.class);
        fixture.setPaymentService((PaymentService) paymentServiceMock.proxy());

        claimMock = mock(Claim.class);

        fixture.setClaimCurrencyConversionAdvice(new CurrencyConversionAdvice() {

            public Object convertFromBaseToNaturalCurrency(Claim claim) {
                // TODO Auto-generated method stub
                return null;
            }

            public Object convertFromNaturalToBaseCurrency(Claim claim) {
                // TODO Auto-generated method stub
                return null;
            }

			public Object convertCurrencyOnlyFromBaseToNatural(Claim claim) {
				// TODO Auto-generated method stub
				return null;
			}

			public Object convertCurrencyOnlyFromNaturalToBase(Claim claim) {
				// TODO Auto-generated method stub
				return null;
			}

			public Object convertFromNaturalToBaseCurrencyForRecClaim(RecoveryClaim recClaim) {
				// TODO Auto-generated method stub
				return null;
			}

			public Money convertMoneyFromBaseToNaturalCurrency(Money valueToConvert, CalendarDate asOfDate, Currency naturalCurrency) {
				// TODO Auto-generated method stub
				return null;
			}

			public Money convertMoneyFromNaturalToBaseCurrency(Money valueToConvert, Claim claim) {
				// TODO Auto-generated method stub
				return null;
			}
        });
        
        fixture.setClaimXMLConverter(new ClaimXMLConverter(){

			@Override
			public String convertObjectToXML(Object object) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object convertXMLToObject(String xml) {
				// TODO Auto-generated method stub
				return null;
			}
        	
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUpdateClaim() {
    	Claim proxyClaim=(Claim)claimMock.proxy();
        claimRepositoryMock.expects(once()).method("update").with(eq(proxyClaim));        
        fixture.updateClaim(proxyClaim);
    }

    public void testFindClaim() {
        Long id = new Long(0);
        Claim claim = (Claim) claimMock.proxy();
        claimRepositoryMock.expects(once()).method("find").with(eq(id)).will(returnValue(claim));

        assertSame(claim, fixture.findClaim(id));
    }

    public void testUpdatePaymentInformation() throws Exception {
        Claim claim = (Claim) claimMock.proxy();
        Payment payment = new Payment();
        paymentServiceMock.expects(once()).method("calculatePaymentForClaim").with(eq(claim)).will(
                returnValue(payment));
        claimRepositoryMock.expects(once()).method("update").with(eq(claim));

        fixture.updatePaymentInformation(claim);

    }
}
