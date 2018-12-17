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

import java.math.BigDecimal;

import junit.framework.TestCase;

import com.domainlanguage.money.Money;

/**
 * @author <a href="kiran.sg@tavant.com">kiran.sg</a>
 * @date Apr 18, 2006
 */
public class LineItemGroupTest extends TestCase {
	private LineItemGroup lineItemGroup;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		lineItemGroup = new LineItemGroup();
		lineItemGroup.setName("OEM Parts");
		lineItemGroup.setPercentageAcceptance(new BigDecimal(50));
		lineItemGroup.setGroupTotal(Money.dollars(20));
	}

	public void testAddLineItem() {
		LineItem lineItem = lineItemGroup.addLineItem("Discount Level 1", 1,
				lineItemGroup.getGroupTotal(), lineItemGroup.getTotalAtCostPrice(), new Double(5));
		assertNotNull(lineItem);
		assertEquals("Discount Level 1", lineItem.getName());
		LineItem item = lineItemGroup.getLineItem("Discount Level 1");
		assertNotNull(item);
		assertEquals(lineItem, item);
		assertEquals(Money.dollars(1), item.getValue());

		lineItem = lineItemGroup.addLineItem("Discount Level 2 - 1", 2, Money
				.dollars(9), lineItemGroup.getTotalAtCostPrice(), new Double(10));
		assertNotNull(lineItem);
		assertEquals("Discount Level 2 - 1", lineItem.getName());
		item = lineItemGroup.getLineItem("Discount Level 2 - 1");
		assertNotNull(item);
		assertEquals(lineItem, item);
		assertEquals(Money.dollars(0.90), item.getValue());

		lineItem = lineItemGroup.addLineItem("Discount Level 2 - 2", 2, Money
				.dollars(9), lineItemGroup.getTotalAtCostPrice(), new Double(20));
		assertNotNull(lineItem);
		assertEquals("Discount Level 2 - 2", lineItem.getName());
		item = lineItemGroup.getLineItem("Discount Level 2 - 2");
		assertNotNull(item);
		assertEquals(lineItem, item);
		assertEquals(Money.dollars(1.80), item.getValue());

		// Verify the totals
		assertEquals(Money.dollars(11.85), lineItemGroup.getAcceptedTotal());

		// Verify the Total Line Item Group
		assertEquals(3, lineItemGroup.getLineItems().size());
	}
}
