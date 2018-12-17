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
package tavant.twms.domain.claim.payment;

import junit.framework.TestCase;
import tavant.twms.domain.claim.payment.definition.Section;

import com.domainlanguage.money.Money;

/*
 * Copyright (c)2006 Tavant Technologies All Rights Reserved.
 * 
 * This software is furnished under a license and may be used and copied only in
 * accordance with the terms of such license and with the inclusion of the above
 * copyright notice. This software or any other copies thereof may not be
 * provided or otherwise made available to any other person. No title to and
 * ownership of the software is hereby transferred.
 * 
 * The information in this software is subject to change without notice and
 * should not be construed as a commitment by Tavant Technologies.
 */

/**
 * @author <a href="radhakrishnan.j@tavant.com">radhakrishnan.j</a>
 * @date Aug 14, 2006
 */
public class PaymentTest extends TestCase {
	Payment fixture;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		fixture = new Payment();
	}

	/**
	 * Test method for
	 * {@link tavant.twms.domain.claim.payment.Payment.
	 */
	public void testGetComponent_NonExistentComponent() {
		//assertNull(fixture.getComponent(new CostCategory("someCategory")));
	}

	/**
	 * Test method for
	 * {@link tavant.twms.domain.claim.payment.Payment
	 */
	public void testAddNewComponent() {
		CostCategory someCategory = new CostCategory("someCategory");
		someCategory.setName("someCategory");
		//assertNull(fixture.getComponent(someCategory));
		//PaymentComponent component = fixture.addNewComponent(someCategory);
		//assertEquals(component, fixture.getComponent(someCategory));
	}

	public void testGetLineItemGroup_NonExistentGroup() {
		assertNull(fixture.getLineItemGroup("someGroup"));
	}

	public void testGetLineItemGroup() {
		LineItemGroup lineItemGroup = fixture.addLineItemGroup("someGroup");
		LineItemGroup itemGroup = fixture.getLineItemGroup("someGroup");
		assertNotNull(itemGroup);
		assertEquals("someGroup", itemGroup.getName());
		assertEquals(lineItemGroup, itemGroup);
	}

	public void testGetLineItemGroupsAcceptedTotal() {
		LineItemGroup group1 = fixture.addLineItemGroup("group 1");
		group1.setAcceptedTotal(Money.dollars(10));
		LineItemGroup group2 = fixture.addLineItemGroup("group 2");
		group2.setAcceptedTotal(Money.dollars(10));
		LineItemGroup group3 = fixture.addLineItemGroup(Section.TOTAL_CLAIM);
		group3.setAcceptedTotal(Money.dollars(10));
		assertEquals(Money.dollars(20), fixture
				.getLineItemGroupsAcceptedTotal());
	}

	public void testGetAcceptedTotalAfterGlobalModifiersProrated() {
		fixture.setTotalAmount(Money.dollars(20));
		LineItemGroup group1 = fixture.addLineItemGroup("group 1");
		group1.setGroupTotal(Money.dollars(10));
		group1.setAcceptedTotal(Money.dollars(5));
		LineItemGroup group2 = fixture.addLineItemGroup("group 2");
		group2.setGroupTotal(Money.dollars(20));
		group2.setAcceptedTotal(Money.dollars(15));
		
		LineItemGroup group3 = fixture.addLineItemGroup(Section.TOTAL_CLAIM);
		group3.setGroupTotal(Money.dollars(20));
		group3.setAcceptedTotal(Money.dollars(12));
		assertEquals(Money.dollars(3), fixture
				.getAcceptedTotalAfterGlobalModifiersProrated("group 1"));
		assertEquals(Money.dollars(9), fixture
				.getAcceptedTotalAfterGlobalModifiersProrated("group 2"));
	}

}
