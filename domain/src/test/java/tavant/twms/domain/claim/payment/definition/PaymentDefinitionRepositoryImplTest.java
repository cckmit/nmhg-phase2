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
package tavant.twms.domain.claim.payment.definition;

import java.util.List;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.infra.DomainRepositoryTestCase;

public class PaymentDefinitionRepositoryImplTest extends
		DomainRepositoryTestCase {

	PaymentDefinitionRepository paymentDefinitionRepository;

	public PaymentDefinitionRepository getPaymentDefinitionRepository() {
		return paymentDefinitionRepository;
	}

	public void setPaymentDefinitionRepository(
			PaymentDefinitionRepository paymentDefinitionRepository) {
		this.paymentDefinitionRepository = paymentDefinitionRepository;
	}

	public void testFindPaymentDefinition() {
		CalendarDate startDate = CalendarDate.from("12/31/2000", "MM/dd/yyyy");
		List<PaymentDefinition> paymentDefinitionList = paymentDefinitionRepository
				.findAllDefinitionsByDate(startDate);
		assertNotNull(paymentDefinitionList);
		assertEquals(1, paymentDefinitionList.size());
		for (PaymentDefinition paymentDefinition : paymentDefinitionList) {
			assertNotNull(paymentDefinition.getBusinessUnitInfo());
		}
	}

}
