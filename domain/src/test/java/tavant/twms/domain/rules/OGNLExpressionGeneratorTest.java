/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.rules;

import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.CLAIM_RULES;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;
import ognl.Ognl;
import ognl.OgnlException;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.orgmodel.Dealership;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.Duration;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 */
public class OGNLExpressionGeneratorTest extends TestCase {

	private final String businessObjectModel = BusinessObjectModelFactory.CLAIM_RULES;
	private OGNLExpressionGenerator fixture = new OGNLExpressionGenerator();

	static {
		Clock.setDefaultTimeZone(TimeZone.getDefault());
		Clock.timeSource();
	}

	public void testAcceptGetOGNLExpressionVisitor() {
		Equals equals = new Equals(new Constant("8", Type.INTEGER),
				new Constant("8", Type.INTEGER));
		equals.accept(fixture);
		assertEquals("8==8", fixture.getExpressionString());
	}

	public void testAnd() {
		Equals condition1 = new Equals(new Constant("1", Type.LONG),
				new Constant("2", Type.LONG));
		Equals condition2 = new Equals(new Constant("3", Type.LONG),
				new Constant("4", Type.LONG));
		And and = new And(condition1, condition2);

		and.accept(fixture);
		assertEquals("(1==2 && 3==4)", fixture.getExpressionString());
	}

	public void testOr() {
		Equals condition1 = new Equals(new Constant("1", Type.INTEGER),
				new Constant("2", Type.INTEGER));
		Equals condition2 = new Equals(new Constant("3", Type.INTEGER),
				new Constant("4", Type.INTEGER));
		Or or = new Or(condition1, condition2);

		or.accept(fixture);
		assertEquals("(1==2 || 3==4)", fixture.getExpressionString());
	}

	public void testIsNotSetForInt() throws OgnlException {
		String distanceExpression = "claim.serviceInformation.serviceDetail.travelDetails.distance";
		DomainSpecificVariable travelDistance = new DomainSpecificVariable(
				Claim.class, distanceExpression, businessObjectModel);
		IsNotSet isNotSet = new IsNotSet(travelDistance);

		isNotSet.accept(fixture);
		TravelDetail travelDetail = new TravelDetail();
		travelDetail.setDistance(new BigDecimal(1));
		ServiceDetail serviceDetail = new ServiceDetail();
		serviceDetail.setTravelDetails(travelDetail);
		ServiceInformation serviceInformation = new ServiceInformation();
		serviceInformation.setServiceDetail(serviceDetail);
		Claim aClaim = new MachineClaim();
		aClaim.setServiceInformation(serviceInformation);

		Map<String, Object> context = new HashMap<String, Object>(1);
		context.put("claim", aClaim);

		assertEquals("(" + distanceExpression + " == null || "
				+ distanceExpression + " == 0)", fixture.getExpressionString());
		Object evaluatedResult = Ognl.getValue(fixture.getExpressionString(),
				context);
		assertTrue(evaluatedResult instanceof Boolean);
		assertFalse((Boolean) evaluatedResult);
	}

	public void testIsNotSetForDomainVariable() throws OgnlException {
		String paymentExpression = "claim.payment";
		DomainSpecificVariable payment = new DomainSpecificVariable(
				Claim.class, paymentExpression, businessObjectModel);
		IsNotSet isNotSet = new IsNotSet(payment);

		isNotSet.accept(fixture);
		Claim aClaim = new MachineClaim();
		aClaim.setPayment(new Payment());

		Map<String, Object> context = new HashMap<String, Object>(1);
		context.put("claim", aClaim);

		assertEquals("(" + paymentExpression + " == null)", fixture
				.getExpressionString());
		Object evaluatedResult = Ognl.getValue(fixture.getExpressionString(),
				context);
		assertTrue(evaluatedResult instanceof Boolean);
		assertFalse((Boolean) evaluatedResult);

		aClaim.setPayment(null);
		evaluatedResult = Ognl.getValue(fixture.getExpressionString(), context);
		assertTrue(evaluatedResult instanceof Boolean);
		assertTrue((Boolean) evaluatedResult);
	}

	public void testIsNotSetForMoney() throws OgnlException {
		String mealsExpenseExpression = "claim.payment.claimedAmount";
		DomainSpecificVariable mealsExpenseItem = new DomainSpecificVariable(
				Claim.class, mealsExpenseExpression, businessObjectModel);
		IsNotSet isNotSet = new IsNotSet(mealsExpenseItem);

		isNotSet.accept(fixture);
		Claim aClaim = new MachineClaim();
		Payment payment = new Payment();
		payment.setClaimedAmount(Money.dollars(5));
		aClaim.setPayment(payment);

		Map<String, Object> context = new HashMap<String, Object>(1);
		context.put("claim", aClaim);

		assertEquals("(" + mealsExpenseExpression + " == null || "
				+ mealsExpenseExpression + ".isZero())", fixture
				.getExpressionString());
		Object evaluatedResult = Ognl.getValue(fixture.getExpressionString(),
				context);
		assertTrue(evaluatedResult instanceof Boolean);
		assertFalse((Boolean) evaluatedResult);

		payment.setClaimedAmount(Money.dollars(0));
		evaluatedResult = Ognl.getValue(fixture.getExpressionString(), context);
		assertTrue(evaluatedResult instanceof Boolean);
		assertTrue((Boolean) evaluatedResult);
	}

	public void testForEach() throws OgnlException {
		DomainSpecificVariable _OEMPartNumberOfUnitsReplaced = new DomainSpecificVariable(
				OEMPartReplaced.class, "numberOfUnits", businessObjectModel);
		Constant expectedNumberOfUnits = new Constant("5", Type.INTEGER);
		GreaterThanOrEquals _5OrMorePartsReplaced = new GreaterThanOrEquals(
				_OEMPartNumberOfUnitsReplaced, expectedNumberOfUnits);

		Claim claim = new MachineClaim();
		claim.setServiceInformation(new ServiceInformation());
		claim.getServiceInformation().setServiceDetail(new ServiceDetail());
		ServiceDetail serviceDetail = claim.getServiceInformation()
				.getServiceDetail();

		OEMPartReplaced partReplaced = new OEMPartReplaced();
		partReplaced.setNumberOfUnits(1);

		serviceDetail.getOEMPartsReplaced().add(partReplaced);

		partReplaced = new OEMPartReplaced();
		partReplaced.setNumberOfUnits(5);
		serviceDetail.getOEMPartsReplaced().add(partReplaced);

		partReplaced = new OEMPartReplaced();
		partReplaced.setNumberOfUnits(7);
		serviceDetail.getOEMPartsReplaced().add(partReplaced);

		ForEachOf forEach = new ForEachOf();
		DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(
				Claim.class,
				"claim.serviceInformation.serviceDetail.oemPartsReplaced",
				businessObjectModel);
		forEach.setCollectionValuedVariable(domainSpecificVariable);
		forEach.setConditionToBeSatisfied(_5OrMorePartsReplaced);

		forEach.accept(fixture);

		String expressionString = fixture.getExpressionString();
		assertEquals(
				"claim.serviceInformation.serviceDetail.oemPartsReplaced.{ ? !(numberOfUnits >= 5) }.size==0",
				expressionString);

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("claim", claim);
		assertFalse((Boolean) Ognl.getValue(expressionString, context));

		assertTrue((Boolean) Ognl.getValue("claim==claim", context));
	}

	public void testForAny() throws OgnlException {
		DomainSpecificVariable _OEMPartNumberOfUnitsReplaced = new DomainSpecificVariable(
				OEMPartReplaced.class, "numberOfUnits", businessObjectModel);
		Constant expectedNumberOfUnits = new Constant("5", Type.INTEGER);
		GreaterThanOrEquals _5OrMorePartsReplaced = new GreaterThanOrEquals(
				_OEMPartNumberOfUnitsReplaced, expectedNumberOfUnits);

		Claim claim = new MachineClaim();
		claim.setServiceInformation(new ServiceInformation());
		claim.getServiceInformation().setServiceDetail(new ServiceDetail());
		ServiceDetail serviceDetail = claim.getServiceInformation()
				.getServiceDetail();

		OEMPartReplaced partReplaced = new OEMPartReplaced();
		partReplaced.setNumberOfUnits(1);

		serviceDetail.getOEMPartsReplaced().add(partReplaced);

		partReplaced = new OEMPartReplaced();
		partReplaced.setNumberOfUnits(5);
		serviceDetail.getOEMPartsReplaced().add(partReplaced);

		partReplaced = new OEMPartReplaced();
		partReplaced.setNumberOfUnits(7);
		serviceDetail.getOEMPartsReplaced().add(partReplaced);

		ForAnyOf forAnyOf = new ForAnyOf();
		DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(
				Claim.class,
				"claim.serviceInformation.serviceDetail.oemPartsReplaced",
				businessObjectModel);
		forAnyOf.setCollectionValuedVariable(domainSpecificVariable);
		forAnyOf.setConditionToBeSatisfied(_5OrMorePartsReplaced);

		forAnyOf.accept(fixture);

		String expressionString = fixture.getExpressionString();
		assertEquals("claim.serviceInformation.serviceDetail.oemPartsReplaced."
				+ "{ ? numberOfUnits >= 5 }" + ".size > 0", expressionString);

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("claim", claim);
		assertTrue((Boolean) Ognl.getValue(expressionString, context));
	}

	public void testGreaterThan() throws OgnlException {
		Constant value1 = new Constant("2.11", Type.BIGDECIMAL);
		Constant value2 = new Constant("2.12", Type.BIGDECIMAL);
		GreaterThan and = new GreaterThan(value1, value2);

		and.accept(fixture);
		String actual = fixture.getExpressionString();
		String expected = "new java.math.BigDecimal(2.11,@java.math.MathContext@DECIMAL32).compareTo(new java.math.BigDecimal(2.12,@java.math.MathContext@DECIMAL32))==1";
		assertEquals(expected, actual);
		assertFalse((Boolean) Ognl.getValue(actual, new Object()));
	}

	public void testGreaterThanOrEquals() throws OgnlException {
		Constant value1 = new Constant("2.11", Type.BIGDECIMAL);
		Constant value2 = new Constant("2.12", Type.BIGDECIMAL);
		GreaterThanOrEquals and = new GreaterThanOrEquals(value1, value2);

		and.accept(fixture);
		String actual = fixture.getExpressionString();
		String expected = "new java.math.BigDecimal(2.11,"
				+ "@java.math.MathContext@DECIMAL32).compareTo("
				+ "new java.math.BigDecimal(2.12,"
				+ "@java.math.MathContext@DECIMAL32)) >= 0";
		assertEquals(expected, actual);
		assertFalse((Boolean) Ognl.getValue(actual, new Object()));
	}

	public void testLessThanOrEquals() throws OgnlException {
		Constant value1 = new Constant("2.11", Type.BIGDECIMAL);
		Constant value2 = new Constant("2.11", Type.BIGDECIMAL);
		LessThanOrEquals and = new LessThanOrEquals(value1, value2);

		and.accept(fixture);
		String actual = fixture.getExpressionString();
		String expected = "new java.math.BigDecimal(2.11,@java.math.MathContext@DECIMAL32).compareTo(new java.math.BigDecimal(2.11,@java.math.MathContext@DECIMAL32)) <= 0";
		assertEquals(expected, actual);
		assertTrue((Boolean) Ognl.getValue(actual, new Object()));
	}

	public void testLessThan() throws OgnlException {
		Constant value1 = new Constant("2.11", Type.BIGDECIMAL);
		Constant value2 = new Constant("2.12", Type.BIGDECIMAL);
		LessThan and = new LessThan(value1, value2);

		and.accept(fixture);
		String actual = fixture.getExpressionString();
		String expected = "new java.math.BigDecimal(2.11,@java.math.MathContext@DECIMAL32).compareTo(new java.math.BigDecimal(2.12,@java.math.MathContext@DECIMAL32))==-1";
		assertEquals(expected, actual);
		assertTrue((Boolean) Ognl.getValue(actual, new Object()));
	}

	public void testMoneyComparison() throws OgnlException {
		Constant value1 = new Constant("USD 2.11", Type.MONEY);
		Constant value2 = new Constant("USD 2.12", Type.MONEY);
		LessThan and = new LessThan(value1, value2);

		and.accept(fixture);
		String actual = fixture.getExpressionString();
		String expected = "@com.domainlanguage.money.Money@valueOf(new java.math.BigDecimal(2.11,@java.math.MathContext@DECIMAL32),@java.util.Currency@getInstance(\"USD\")).compareTo(@com.domainlanguage.money.Money@valueOf(new java.math.BigDecimal(2.12,@java.math.MathContext@DECIMAL32),@java.util.Currency@getInstance(\"USD\")))==-1";
		assertEquals(expected, actual);
		assertTrue((Boolean) Ognl.getValue(actual, new Object()));
	}

	public void testIsNotSet_Money() throws OgnlException {
		DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(
				Claim.class, "claim.payment.claimedAmount", businessObjectModel);
		IsNotSet isNotSet = new IsNotSet(domainSpecificVariable);

		Map<String, Object> context = new HashMap<String, Object>();

		isNotSet.accept(fixture);

		String expressionString = fixture.getExpressionString();

		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setPayment(new Payment());
		context.put("claim", machineClaim);
		assertEquals(
				"(claim.payment.claimedAmount == null || claim.payment.claimedAmount.isZero())",
				expressionString);

		assertTrue((Boolean) Ognl.getValue(expressionString, context));

		machineClaim.getPayment().setClaimedAmount(Money.euros(0.0D));

		assertTrue((Boolean) Ognl.getValue(expressionString, context));

		machineClaim.getPayment().setClaimedAmount(Money.euros(0.1D));
		assertFalse((Boolean) Ognl.getValue(expressionString, context));
	}

	public void testIsNotSet_String() throws OgnlException {
		final DomainSpecificVariable dSV = new DomainSpecificVariable(
				Claim.class, "claim.forDealer.name", businessObjectModel);
		IsNotSet isNotSet = new IsNotSet(dSV);

		Map<String, Object> context = new HashMap<String, Object>();

		isNotSet.accept(fixture);

		String expressionString = fixture.getExpressionString();
		assertEquals("(claim.forDealer.name == null || "
				+ "claim.forDealer.name.trim() == \"\")", expressionString);

		MachineClaim claim = new MachineClaim();
		Dealership dealer = new Dealership();
		claim.setForDealerShip(dealer);
		context.put("claim", claim);

		dealer.setName(null);
		assertTrue((Boolean) Ognl.getValue(expressionString, context));

		context.put("claim", claim);

		dealer.setName("");
		assertTrue((Boolean) Ognl.getValue(expressionString, context));

		dealer.setName("    ");
		assertTrue((Boolean) Ognl.getValue(expressionString, context));

		dealer.setName("   Machine  ");
		assertFalse((Boolean) Ognl.getValue(expressionString, context));
	}

	public void testIsSet_String() throws OgnlException {
		final DomainSpecificVariable dSV = new DomainSpecificVariable(
				Claim.class, "claim.forDealer.name", businessObjectModel);
		IsSet isSet = new IsSet(dSV);

		Map<String, Object> context = new HashMap<String, Object>();

		isSet.accept(fixture);

		String expressionString = fixture.getExpressionString();
		assertEquals("!((claim.forDealer.name == null || "
				+ "claim.forDealer.name.trim() == \"\"))", expressionString);

		MachineClaim claim = new MachineClaim();
		Dealership dealer = new Dealership();
		claim.setForDealerShip(dealer);
		context.put("claim", claim);

		dealer.setName(null);
		assertFalse((Boolean) Ognl.getValue(expressionString, context));

		context.put("claim", claim);

		dealer.setName("");
		assertFalse((Boolean) Ognl.getValue(expressionString, context));

		dealer.setName("    ");
		assertFalse((Boolean) Ognl.getValue(expressionString, context));

		dealer.setName("   Machine  ");
		assertTrue((Boolean) Ognl.getValue(expressionString, context));
	}

	public void testAddition_Long() throws OgnlException {
		DomainSpecificVariable one = new DomainSpecificVariable(Claim.class,
				"claim.serviceInformation.serviceDetail.travelDetails.trips",
				businessObjectModel);
		DomainSpecificVariable two = new DomainSpecificVariable(Claim.class,
				"claim.serviceInformation.serviceDetail.travelDetails.hours",
				businessObjectModel);
		Addition addition = new Addition(one, two);
		addition = new Addition(addition, one);

		addition.accept(fixture);
		assertEquals(
				"((claim.serviceInformation.serviceDetail.travelDetails.trips + "
						+ "claim.serviceInformation.serviceDetail.travelDetails.hours) + "
						+ "claim.serviceInformation.serviceDetail.travelDetails.trips)",
				fixture.getExpressionString());

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		TravelDetail travelDetail = new TravelDetail();
		travelDetail.setTrips(1);
		travelDetail.setHours(new BigDecimal(2));
		ServiceDetail serviceDetail = new ServiceDetail();
		serviceDetail.setTravelDetails(travelDetail);
		ServiceInformation serviceInformation = new ServiceInformation();
		serviceInformation.setServiceDetail(serviceDetail);
		machineClaim.setServiceInformation(serviceInformation);
		context.put("claim", machineClaim);

		assertEquals(new BigDecimal(4), Ognl.getValue(fixture.getExpressionString(), context));
	}

	public void testIsTrue() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.serviceManagerRequest", businessObjectModel);
		IsTrue isTrue = new IsTrue(operand);

		isTrue.accept(fixture);
		assertEquals("(claim.serviceManagerRequest)", fixture
				.getExpressionString());

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setServiceManagerRequest(true);
		context.put("claim", machineClaim);

		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		machineClaim.setServiceManagerRequest(false);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testIsFalse() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.serviceManagerRequest", businessObjectModel);
		IsFalse isFalse = new IsFalse(operand);

		isFalse.accept(fixture);
		assertEquals("(!claim.serviceManagerRequest)", fixture
				.getExpressionString());

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setServiceManagerRequest(true);
		context.put("claim", machineClaim);

		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		machineClaim.setServiceManagerRequest(false);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testStartsWith() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.forDealer.name", businessObjectModel);
		Constant rhsVal = new Constant("foo", "string");
		StartsWith startsWith = new StartsWith(operand, rhsVal);

		startsWith.accept(fixture);
		assertEquals("claim.forDealer.name.toLowerCase().startsWith("
				+ "\"foo\".toLowerCase())", fixture.getExpressionString());

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		Dealership dealer = new Dealership();
		machineClaim.setForDealerShip(dealer);

		dealer.setName("fooBar");
		context.put("claim", machineClaim);

		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		dealer.setName("someValue");
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testDoesNotStartWith() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.forDealer.name", businessObjectModel);
		Constant rhsVal = new Constant("foo", "string");
		DoesNotStartWith doesNotStartWith = new DoesNotStartWith(operand,
				rhsVal);

		doesNotStartWith.accept(fixture);
		assertEquals("!(claim.forDealer.name.toLowerCase().startsWith("
				+ "\"foo\".toLowerCase()))", fixture.getExpressionString());

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		Dealership dealer = new Dealership();
		machineClaim.setForDealerShip(dealer);

		dealer.setName("fooBar");
		context.put("claim", machineClaim);

		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		dealer.setName("someValue");
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testEndsWith() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.forDealer.name", businessObjectModel);
		Constant rhsVal = new Constant("Bar", "string");
		EndsWith endsWith = new EndsWith(operand, rhsVal);

		endsWith.accept(fixture);
		assertEquals("claim.forDealer.name.toLowerCase().endsWith("
				+ "\"Bar\".toLowerCase())", fixture.getExpressionString());

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		Dealership dealer = new Dealership();
		machineClaim.setForDealerShip(dealer);

		dealer.setName("fooBar");
		context.put("claim", machineClaim);

		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		dealer.setName("someValue");
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testDoesNotEndWith() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.forDealer.name", businessObjectModel);
		Constant rhsVal = new Constant("Bar", "string");
		DoesNotEndWith doesNotEndWith = new DoesNotEndWith(operand, rhsVal);

		doesNotEndWith.accept(fixture);
		assertEquals("!(claim.forDealer.name.toLowerCase().endsWith("
				+ "\"Bar\".toLowerCase()))", fixture.getExpressionString());

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		Dealership dealer = new Dealership();
		machineClaim.setForDealerShip(dealer);

		dealer.setName("fooBar");
		context.put("claim", machineClaim);

		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		dealer.setName("someValue");
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testContains() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.forDealer.name", businessObjectModel);
		Constant rhsVal = new Constant("oBa", "string");
		Contains contains = new Contains(operand, rhsVal);

		contains.accept(fixture);
		assertEquals("claim.forDealer.name.toLowerCase().indexOf("
				+ "\"oBa\".toLowerCase()) != -1", fixture.getExpressionString());

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		Dealership dealer = new Dealership();
		machineClaim.setForDealerShip(dealer);

		dealer.setName("fooBar");
		context.put("claim", machineClaim);

		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		dealer.setName("someValue");
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testDoesNotContain() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.forDealer.name", businessObjectModel);
		Constant rhsVal = new Constant("oBa", "string");
		DoesNotContain doesNotContain = new DoesNotContain(operand, rhsVal);

		doesNotContain.accept(fixture);
		assertEquals("claim.forDealer.name.toLowerCase().indexOf("
				+ "\"oBa\".toLowerCase()) == -1", fixture.getExpressionString());

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		Dealership dealer = new Dealership();
		machineClaim.setForDealerShip(dealer);

		dealer.setName("fooBar");
		context.put("claim", machineClaim);

		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		dealer.setName("someValue");
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testEquals_String() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.forDealer.name", businessObjectModel);
		Constant rhsVal = new Constant("fooBar", "string");
		Equals equals = new Equals(operand, rhsVal);

		equals.accept(fixture);
		assertEquals("claim.forDealer.name.toLowerCase().equals("
				+ "\"fooBar\".toLowerCase())", fixture.getExpressionString());

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		Dealership dealer = new Dealership();
		machineClaim.setForDealerShip(dealer);

		dealer.setName("fooBar");
		context.put("claim", machineClaim);

		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		dealer.setName("someValue");
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testNotEquals_String() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.forDealer.name", businessObjectModel);
		Constant rhsVal = new Constant("fooBar", "string");
		NotEquals equals = new NotEquals(operand, rhsVal);

		equals.accept(fixture);
		assertEquals("!(claim.forDealer.name.toLowerCase().equals("
				+ "\"fooBar\".toLowerCase()))", fixture.getExpressionString());

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		Dealership dealer = new Dealership();
		machineClaim.setForDealerShip(dealer);

		dealer.setName("fooBar");
		context.put("claim", machineClaim);

		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		dealer.setName("someValue");
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testBetweenForInteger() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class,
				"claim.serviceInformation.serviceDetail.travelDetails.trips",
				businessObjectModel);
		Constant startingRhsVal = new Constant("0", "integer");
		Constant endingRhsVal = new Constant("10", "integer");
		Between between = new Between(operand, startingRhsVal, endingRhsVal);

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();

		TravelDetail travelDetail = new TravelDetail();
		ServiceDetail serviceDetail = new ServiceDetail();
		serviceDetail.setTravelDetails(travelDetail);
		ServiceInformation serviceInformation = new ServiceInformation();
		serviceInformation.setServiceDetail(serviceDetail);

		machineClaim.setServiceInformation(serviceInformation);

		context.put("claim", machineClaim);

		// Test for inclusive (default).

		between.accept(fixture);
		assertEquals(
				"(claim.serviceInformation.serviceDetail.travelDetails.trips >= 0 && "
						+ "claim.serviceInformation.serviceDetail.travelDetails.trips <= 10)",
				fixture.getExpressionString());

		travelDetail.setTrips(-1);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		travelDetail.setTrips(6);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		travelDetail.setTrips(13);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		travelDetail.setTrips(0);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		travelDetail.setTrips(10);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		fixture = new OGNLExpressionGenerator();

		// Test for non-inclusive.
		between.setInclusive(false);

		between.accept(fixture);
		assertEquals(
				"(claim.serviceInformation.serviceDetail.travelDetails.trips > 0 && "
						+ "claim.serviceInformation.serviceDetail.travelDetails.trips < 10)",
				fixture.getExpressionString());

		travelDetail.setTrips(-1);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		travelDetail.setTrips(6);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		travelDetail.setTrips(13);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		travelDetail.setTrips(0);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		travelDetail.setTrips(10);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

	}

	public void testBetweenForMoney() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.payment.claimedAmount", businessObjectModel);
		Constant startingRhsVal = new Constant("1", "money");
		Constant endingRhsVal = new Constant("10", "money");
		Between between = new Between(operand, startingRhsVal, endingRhsVal);

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		Payment payment = new Payment();
		machineClaim.setPayment(payment);

		context.put("claim", machineClaim);

		// Test for inclusive (default).

		between.accept(fixture);
		assertEquals(
				"(claim.payment.claimedAmount.compareTo("
						+ "@com.domainlanguage.money.Money@valueOf("
						+ "new java.math.BigDecimal(1,@java.math.MathContext@DECIMAL32)"
						+ ",@tavant.twms.domain.common.GlobalConfiguration@getInstance()"
						+ ".baseCurrency)) >= 0 && claim.payment.claimedAmount.compareTo("
						+ "@com.domainlanguage.money.Money@valueOf(new java.math."
						+ "BigDecimal(10,@java.math.MathContext@DECIMAL32),@tavant.twms"
						+ ".domain.common.GlobalConfiguration@getInstance().baseCurrency)"
						+ ") <= 0)", fixture.getExpressionString());

		payment.setClaimedAmount(Money.dollars(0));
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		payment.setClaimedAmount(Money.dollars(6));
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		payment.setClaimedAmount(Money.dollars(13));
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		payment.setClaimedAmount(Money.dollars(1));
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		payment.setClaimedAmount(Money.dollars(10));
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		fixture = new OGNLExpressionGenerator();

		// Test for non-inclusive.
		between.setInclusive(false);

		between.accept(fixture);
		assertEquals(
				"(claim.payment.claimedAmount.compareTo("
						+ "@com.domainlanguage.money.Money@valueOf("
						+ "new java.math.BigDecimal(1,@java.math.MathContext@DECIMAL32)"
						+ ",@tavant.twms.domain.common.GlobalConfiguration@getInstance()"
						+ ".baseCurrency)) > 0 && claim.payment.claimedAmount.compareTo("
						+ "@com.domainlanguage.money.Money@valueOf(new java.math."
						+ "BigDecimal(10,@java.math.MathContext@DECIMAL32),@tavant.twms"
						+ ".domain.common.GlobalConfiguration@getInstance().baseCurrency)"
						+ ") < 0)", fixture.getExpressionString());

		payment.setClaimedAmount(Money.dollars(0));
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		payment.setClaimedAmount(Money.dollars(6));
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		payment.setClaimedAmount(Money.dollars(13));
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		payment.setClaimedAmount(Money.dollars(1));
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		payment.setClaimedAmount(Money.dollars(10));
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

	}

	public void testBetweenForDate() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("03/11/2006", "date");
		Constant endingRhsVal = new Constant("08/23/2006", "date");
		Between between = new Between(operand, startingRhsVal, endingRhsVal);

		Map<String, Object> context = new HashMap<String, Object>();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		// Test for inclusive (default).

		between.accept(fixture);
		assertEquals("(claim.failureDate.compareTo(@com.domainlanguage.time."
				+ "CalendarDate@from(\"03/11/2006\",\"M/d/yyyy\")) >= 0 && "
				+ "claim.failureDate.compareTo(@com.domainlanguage.time."
				+ "CalendarDate@from(\"08/23/2006\",\"M/d/yyyy\")) <= 0)",
				fixture.getExpressionString());

		failureDate = CalendarDate.date(2006, 3, 10);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = CalendarDate.date(2006, 5, 12);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = CalendarDate.date(2006, 8, 24);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = CalendarDate.date(2006, 3, 11);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = CalendarDate.date(2006, 8, 23);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		fixture = new OGNLExpressionGenerator();
		// Test for non-inclusive.
		between.setInclusive(false);

		between.accept(fixture);
		assertEquals("(claim.failureDate.compareTo(@com.domainlanguage.time."
				+ "CalendarDate@from(\"03/11/2006\",\"M/d/yyyy\")) > 0 && "
				+ "claim.failureDate.compareTo(@com.domainlanguage.time."
				+ "CalendarDate@from(\"08/23/2006\",\"M/d/yyyy\")) < 0)",
				fixture.getExpressionString());

		failureDate = CalendarDate.date(2006, 3, 10);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = CalendarDate.date(2006, 5, 12);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = CalendarDate.date(2006, 8, 24);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = CalendarDate.date(2006, 3, 11);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = CalendarDate.date(2006, 8, 23);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testNotBetweenForInteger() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class,
				"claim.serviceInformation.serviceDetail.travelDetails.trips",
				businessObjectModel);
		Constant startingRhsVal = new Constant("0", "integer");
		Constant endingRhsVal = new Constant("10", "integer");
		NotBetween notBetween = new NotBetween(operand, startingRhsVal,
				endingRhsVal);

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();

		TravelDetail travelDetail = new TravelDetail();
		ServiceDetail serviceDetail = new ServiceDetail();
		serviceDetail.setTravelDetails(travelDetail);
		ServiceInformation serviceInformation = new ServiceInformation();
		serviceInformation.setServiceDetail(serviceDetail);

		machineClaim.setServiceInformation(serviceInformation);

		context.put("claim", machineClaim);

		// Test for inclusive (default).
		notBetween.setInclusive(true);

		notBetween.accept(fixture);
		assertEquals(
				"!((claim.serviceInformation.serviceDetail.travelDetails.trips >= 0 && "
						+ "claim.serviceInformation.serviceDetail.travelDetails.trips <= 10))",
				fixture.getExpressionString());

		travelDetail.setTrips(6);

		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		travelDetail.setTrips(13);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		travelDetail.setTrips(10);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		travelDetail.setTrips(0);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		fixture = new OGNLExpressionGenerator();

		// Test for non-inclusive.
		notBetween.setInclusive(false);

		notBetween.accept(fixture);
		assertEquals(
				"!((claim.serviceInformation.serviceDetail.travelDetails.trips > 0 && "
						+ "claim.serviceInformation.serviceDetail.travelDetails.trips < 10))",
				fixture.getExpressionString());

		travelDetail.setTrips(6);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		travelDetail.setTrips(13);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		travelDetail.setTrips(10);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		travelDetail.setTrips(0);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotBetweenForMoney() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.payment.claimedAmount", businessObjectModel);
		Constant startingRhsVal = new Constant("0", "money");
		Constant endingRhsVal = new Constant("10", "money");
		NotBetween notBetween = new NotBetween(operand, startingRhsVal,
				endingRhsVal);

		Map<String, Object> context = new HashMap<String, Object>();
		MachineClaim machineClaim = new MachineClaim();
		Payment payment = new Payment();
		machineClaim.setPayment(payment);

		context.put("claim", machineClaim);

		// Test for inclusive (default).
		notBetween.accept(fixture);
		assertEquals(
				"!((claim.payment.claimedAmount.compareTo("
						+ "@com.domainlanguage.money.Money@valueOf("
						+ "new java.math.BigDecimal(0,@java.math.MathContext@DECIMAL32)"
						+ ",@tavant.twms.domain.common.GlobalConfiguration@getInstance()"
						+ ".baseCurrency)) >= 0 && claim.payment.claimedAmount.compareTo("
						+ "@com.domainlanguage.money.Money@valueOf(new java.math."
						+ "BigDecimal(10,@java.math.MathContext@DECIMAL32),@tavant.twms"
						+ ".domain.common.GlobalConfiguration@getInstance().baseCurrency)"
						+ ") <= 0))", fixture.getExpressionString());

		payment.setClaimedAmount(Money.dollars(6));

		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		payment.setClaimedAmount(Money.dollars(13));
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		payment.setClaimedAmount(Money.dollars(10));
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		payment.setClaimedAmount(Money.dollars(0));
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		fixture = new OGNLExpressionGenerator();

		// Test for non-inclusive.
		notBetween.setInclusive(false);

		notBetween.accept(fixture);
		assertEquals(
				"!((claim.payment.claimedAmount.compareTo("
						+ "@com.domainlanguage.money.Money@valueOf("
						+ "new java.math.BigDecimal(0,@java.math.MathContext@DECIMAL32)"
						+ ",@tavant.twms.domain.common.GlobalConfiguration@getInstance()"
						+ ".baseCurrency)) > 0 && claim.payment.claimedAmount.compareTo("
						+ "@com.domainlanguage.money.Money@valueOf(new java.math."
						+ "BigDecimal(10,@java.math.MathContext@DECIMAL32),@tavant.twms"
						+ ".domain.common.GlobalConfiguration@getInstance().baseCurrency)"
						+ ") < 0))", fixture.getExpressionString());

		payment.setClaimedAmount(Money.dollars(6));
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		payment.setClaimedAmount(Money.dollars(13));
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		payment.setClaimedAmount(Money.dollars(10));
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		payment.setClaimedAmount(Money.dollars(0));
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

	}

	public void testNotBetweenForDate() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("03/11/2006", "date");
		Constant endingRhsVal = new Constant("08/23/2006", "date");
		NotBetween notBetween = new NotBetween(operand, startingRhsVal,
				endingRhsVal);

		Map<String, Object> context = new HashMap<String, Object>();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		// Test for inclusive (default).
		notBetween.setInclusive(true);
		fixture = new OGNLExpressionGenerator();
		notBetween.accept(fixture);
		assertEquals("!((claim.failureDate.compareTo(@com.domainlanguage.time"
				+ ".CalendarDate@from(\"03/11/2006\",\"M/d/yyyy\")) >= 0 && "
				+ "claim.failureDate.compareTo(@com.domainlanguage.time."
				+ "CalendarDate@from(\"08/23/2006\",\"M/d/yyyy\")) <= 0))",
				fixture.getExpressionString());

		failureDate = CalendarDate.date(2006, 3, 10);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = CalendarDate.date(2006, 5, 12);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = CalendarDate.date(2006, 8, 24);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = CalendarDate.date(2006, 3, 11);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = CalendarDate.date(2006, 8, 23);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		fixture = new OGNLExpressionGenerator();

		// Test for non-inclusive.
		notBetween.setInclusive(false);

		notBetween.accept(fixture);
		assertEquals("!((claim.failureDate.compareTo(@com.domainlanguage.time"
				+ ".CalendarDate@from(\"03/11/2006\",\"M/d/yyyy\")) > 0 && "
				+ "claim.failureDate.compareTo(@com.domainlanguage.time."
				+ "CalendarDate@from(\"08/23/2006\",\"M/d/yyyy\")) < 0))",
				fixture.getExpressionString());

		failureDate = CalendarDate.date(2006, 3, 10);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = CalendarDate.date(2006, 5, 12);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = CalendarDate.date(2006, 8, 24);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = CalendarDate.date(2006, 3, 11);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = CalendarDate.date(2006, 8, 23);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

	}

	public void testWithinLastForDays() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.DAY.getType();
		CalendarDate today = Clock.today();

		IsWithinLast isWithinLast = new IsWithinLast(operand, startingRhsVal,
				durationType);

		isWithinLast.accept(fixture);
		assertEquals("isWithinLast(claim.failureDate,4," + durationType + ")",
				fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.days(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.days(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.days(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.days(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testWithinLastForWeeks() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsWithinLast isWithinLast = new IsWithinLast(operand, startingRhsVal,
				durationType);

		isWithinLast.accept(fixture);
		assertEquals("isWithinLast(claim.failureDate,4," + durationType + ")",
				fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testWithinLastForMonths() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsWithinLast isWithinLast = new IsWithinLast(operand, startingRhsVal,
				durationType);

		isWithinLast.accept(fixture);
		assertEquals("isWithinLast(claim.failureDate,4," + durationType + ")",
				fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.months(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testWithinLastForDays_withDateToCompare() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.DAY.getType();
		CalendarDate today = Clock.today();

		IsWithinLast isWithinLast = new IsWithinLast(operand, dateToCompare,
				startingRhsVal, durationType);

		isWithinLast.accept(fixture);
		assertEquals("isWithinLast(claim.failureDate,claim.repairDate,4,"
				+ durationType + ")", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);
		context.put("claim", machineClaim);

		failureDate = Duration.days(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.days(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.days(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.days(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testWithinLastForWeeks_withDateToCompare() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsWithinLast isWithinLast = new IsWithinLast(operand, dateToCompare,
				startingRhsVal, durationType);

		isWithinLast.accept(fixture);
		assertEquals("isWithinLast(claim.failureDate,claim.repairDate,4,"
				+ durationType + ")", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testWithinLastForMonths_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsWithinLast isWithinLast = new IsWithinLast(operand, dateToCompare,
				startingRhsVal, durationType);

		isWithinLast.accept(fixture);
		assertEquals("isWithinLast(claim.failureDate,claim.repairDate,4,"
				+ durationType + ")", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.months(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testNotWithinLastForDays() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.DAY.getType();
		CalendarDate today = Clock.today();

		IsNotWithinLast isNotWithinLast = new IsNotWithinLast(operand,
				startingRhsVal, durationType);

		isNotWithinLast.accept(fixture);
		assertEquals("!(isWithinLast(claim.failureDate,4," + durationType
				+ "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.days(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.days(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.days(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.days(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotWithinLastForWeeks() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsNotWithinLast isNotWithinLast = new IsNotWithinLast(operand,
				startingRhsVal, durationType);

		isNotWithinLast.accept(fixture);
		assertEquals("!(isWithinLast(claim.failureDate,4," + durationType
				+ "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotWithinLastForMonths() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsNotWithinLast isNotWithinLast = new IsNotWithinLast(operand,
				startingRhsVal, durationType);

		isNotWithinLast.accept(fixture);
		assertEquals("!(isWithinLast(claim.failureDate,4," + durationType
				+ "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.months(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotWithinLastForDays_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.DAY.getType();
		CalendarDate today = Clock.today();

		IsNotWithinLast isNotWithinLast = new IsNotWithinLast(operand,
				dateToCompare, startingRhsVal, durationType);

		isNotWithinLast.accept(fixture);
		assertEquals("!(isWithinLast(claim.failureDate,claim.repairDate,4,"
				+ durationType + "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.days(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.days(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.days(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.days(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotWithinLastForWeeks_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsNotWithinLast isNotWithinLast = new IsNotWithinLast(operand,
				dateToCompare, startingRhsVal, durationType);

		isNotWithinLast.accept(fixture);
		assertEquals("!(isWithinLast(claim.failureDate,claim.repairDate,4,"
				+ durationType + "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotWithinLastForMonths_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);

		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsNotWithinLast isNotWithinLast = new IsNotWithinLast(operand,
				dateToCompare, startingRhsVal, durationType);

		isNotWithinLast.accept(fixture);
		assertEquals("!(isWithinLast(claim.failureDate,claim.repairDate,4,"
				+ durationType + "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.months(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testWithinNextForDays() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.DAY.getType();
		CalendarDate today = Clock.today();

		IsWithinNext isWithinNext = new IsWithinNext(operand, startingRhsVal,
				durationType);

		isWithinNext.accept(fixture);
		assertEquals("isWithinNext(claim.failureDate,4," + durationType + ")",
				fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.days(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.days(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.days(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.days(12).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testWithinNextForWeeks() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsWithinNext isWithinNext = new IsWithinNext(operand, startingRhsVal,
				durationType);

		isWithinNext.accept(fixture);
		assertEquals("isWithinNext(claim.failureDate,4," + durationType + ")",
				fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(12).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testWithinNextForMonths() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsWithinNext isWithinNext = new IsWithinNext(operand, startingRhsVal,
				durationType);

		isWithinNext.accept(fixture);
		assertEquals("isWithinNext(claim.failureDate,4," + durationType + ")",
				fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(12).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testWithinNextForDays_withDateToCompare() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.DAY.getType();
		CalendarDate today = Clock.today();

		IsWithinNext isWithinNext = new IsWithinNext(operand, dateToCompare,
				startingRhsVal, durationType);

		isWithinNext.accept(fixture);
		assertEquals("isWithinNext(claim.failureDate,claim.repairDate,4,"
				+ durationType + ")", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);
		context.put("claim", machineClaim);

		failureDate = Duration.days(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.days(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.days(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.days(12).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testWithinNextForWeeks_withDateToCompare() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsWithinNext isWithinNext = new IsWithinNext(operand, dateToCompare,
				startingRhsVal, durationType);

		isWithinNext.accept(fixture);
		assertEquals("isWithinNext(claim.failureDate,claim.repairDate,4,"
				+ durationType + ")", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(12).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testWithinNextForMonths_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsWithinNext isWithinNext = new IsWithinNext(operand, dateToCompare,
				startingRhsVal, durationType);

		isWithinNext.accept(fixture);
		assertEquals("isWithinNext(claim.failureDate,claim.repairDate,4,"
				+ durationType + ")", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);
		context.put("claim", machineClaim);

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(12).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testNotWithinNextForDays() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.DAY.getType();
		CalendarDate today = Clock.today();

		IsNotWithinNext isNotWithinNext = new IsNotWithinNext(operand,
				startingRhsVal, durationType);

		isNotWithinNext.accept(fixture);
		assertEquals("!(isWithinNext(claim.failureDate,4," + durationType
				+ "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.days(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.days(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.days(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.days(15).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotWithinNextForWeeks() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsNotWithinNext isNotWithinNext = new IsNotWithinNext(operand,
				startingRhsVal, durationType);

		isNotWithinNext.accept(fixture);
		assertEquals("!(isWithinNext(claim.failureDate,4," + durationType
				+ "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(15).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotWithinNextForMonths() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsNotWithinNext isNotWithinNext = new IsNotWithinNext(operand,
				startingRhsVal, durationType);

		isNotWithinNext.accept(fixture);
		assertEquals("!(isWithinNext(claim.failureDate,4," + durationType
				+ "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(15).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotWithinNextForDays_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.DAY.getType();
		CalendarDate today = Clock.today();

		IsNotWithinNext isNotWithinNext = new IsNotWithinNext(operand,
				dateToCompare, startingRhsVal, durationType);

		isNotWithinNext.accept(fixture);
		assertEquals("!(isWithinNext(claim.failureDate,claim.repairDate,4,"
				+ durationType + "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.days(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.days(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.days(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.days(15).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotWithinNextForWeeks_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsNotWithinNext isNotWithinNext = new IsNotWithinNext(operand,
				dateToCompare, startingRhsVal, durationType);

		isNotWithinNext.accept(fixture);
		assertEquals("!(isWithinNext(claim.failureDate,claim.repairDate,4,"
				+ durationType + "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(15).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotWithinNextForMonths_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsNotWithinNext isNotWithinNext = new IsNotWithinNext(operand,
				dateToCompare, startingRhsVal, durationType);

		isNotWithinNext.accept(fixture);
		assertEquals("!(isWithinNext(claim.failureDate,claim.repairDate,4,"
				+ durationType + "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(15).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testDuringLastForWeeks() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsDuringLast isDuringLast = new IsDuringLast(operand, startingRhsVal,
				durationType);

		isDuringLast.accept(fixture);
		assertEquals("isDuringLast(claim.failureDate,4," + durationType + ")",
				fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		do {
			today = today.previousDay();
		} while (today.dayOfWeek() != Calendar.SUNDAY);

		// Sunday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		today = Duration.weeks(4).subtractedFrom(today);
		// Monday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

	}

	public void testDuringLastForMonths() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsDuringLast isDuringLast = new IsDuringLast(operand, startingRhsVal,
				durationType);

		isDuringLast.accept(fixture);
		assertEquals("isDuringLast(claim.failureDate,4," + durationType + ")",
				fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.months(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		today = today.month().start().previousDay();
		// End of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		today = Duration.months(4).subtractedFrom(today);

		// Start of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testDuringLastForWeeks_withDateToCompare() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsDuringLast isDuringLast = new IsDuringLast(operand, dateToCompare,
				startingRhsVal, durationType);

		isDuringLast.accept(fixture);
		assertEquals("isDuringLast(claim.failureDate,claim.repairDate,4,"
				+ durationType + ")", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		do {
			today = today.previousDay();
		} while (today.dayOfWeek() != Calendar.SUNDAY);

		// Sunday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		today = Duration.weeks(4).subtractedFrom(today);
		// Monday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

	}

	public void testDuringLastForMonths_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsDuringLast isDuringLast = new IsDuringLast(operand, dateToCompare,
				startingRhsVal, durationType);

		isDuringLast.accept(fixture);
		assertEquals("isDuringLast(claim.failureDate,claim.repairDate,4,"
				+ durationType + ")", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.months(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		today = today.month().start().previousDay();
		// End of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		today = Duration.months(4).subtractedFrom(today);

		// Start of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testNotDuringLastForWeeks() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsNotDuringLast isNotDuringLast = new IsNotDuringLast(operand,
				startingRhsVal, durationType);

		isNotDuringLast.accept(fixture);
		assertEquals("!(isDuringLast(claim.failureDate,4," + durationType
				+ "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		do {
			today = today.previousDay();
		} while (today.dayOfWeek() != Calendar.SUNDAY);

		// Sunday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		today = Duration.weeks(4).subtractedFrom(today);

		// Monday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

	}

	public void testNotDuringLastForMonths() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsNotDuringLast isNotDuringLast = new IsNotDuringLast(operand,
				startingRhsVal, durationType);

		isNotDuringLast.accept(fixture);
		assertEquals("!(isDuringLast(claim.failureDate,4," + durationType
				+ "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.months(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		today = today.month().start().previousDay();
		// End of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		today = Duration.months(4).subtractedFrom(today);
		// Start of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

	}

	public void testNotDuringLastForWeeks_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsNotDuringLast isNotDuringLast = new IsNotDuringLast(operand,
				dateToCompare, startingRhsVal, durationType);

		isNotDuringLast.accept(fixture);
		assertEquals("!(isDuringLast(claim.failureDate,claim.repairDate,4,"
				+ durationType + "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		do {
			today = today.previousDay();
		} while (today.dayOfWeek() != Calendar.SUNDAY);

		// Sunday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		today = Duration.weeks(4).subtractedFrom(today);

		// Monday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

	}

	public void testNotDuringLastForMonths_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsNotDuringLast isNotDuringLast = new IsNotDuringLast(operand,
				dateToCompare, startingRhsVal, durationType);

		isNotDuringLast.accept(fixture);
		assertEquals("!(isDuringLast(claim.failureDate,claim.repairDate,4,"
				+ durationType + "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.months(23).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(4).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		today = today.month().start().previousDay();
		// End of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		today = Duration.months(4).subtractedFrom(today);
		// Start of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

	}

	public void testDuringNextForWeeks() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsDuringNext isDuringNext = new IsDuringNext(operand, startingRhsVal,
				durationType);

		isDuringNext.accept(fixture);
		assertEquals("isDuringNext(claim.failureDate,4," + durationType + ")",
				fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(17).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		do {
			today = today.nextDay();
		} while (today.dayOfWeek() != Calendar.MONDAY);

		// Monday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		today = Duration.weeks(4).addedTo(today);

		// Sunday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testDuringNextForMonths() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsDuringNext isDuringNext = new IsDuringNext(operand, startingRhsVal,
				durationType);

		isDuringNext.accept(fixture);
		assertEquals("isDuringNext(claim.failureDate,4," + durationType + ")",
				fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(12).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		today = today.month().end().nextDay();
		// Start of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		today = Duration.months(4).addedTo(today);
		// End of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testDuringNextForWeeks_withDateToCompare() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsDuringNext isDuringNext = new IsDuringNext(operand, dateToCompare,
				startingRhsVal, durationType);

		isDuringNext.accept(fixture);
		assertEquals("isDuringNext(claim.failureDate,claim.repairDate,4,"
				+ durationType + ")", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(17).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		do {
			today = today.nextDay();
		} while (today.dayOfWeek() != Calendar.MONDAY);

		// Monday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		today = Duration.weeks(4).addedTo(today);

		// Sunday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testDuringNextForMonths_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsDuringNext isDuringNext = new IsDuringNext(operand, dateToCompare,
				startingRhsVal, durationType);

		isDuringNext.accept(fixture);
		assertEquals("isDuringNext(claim.failureDate,claim.repairDate,4,"
				+ durationType + ")", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(12).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		today = today.month().end().nextDay();
		// Start of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		today = Duration.months(4).addedTo(today);
		// End of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));
	}

	public void testNotDuringNextForWeeks() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsNotDuringNext isNotDuringNext = new IsNotDuringNext(operand,
				startingRhsVal, durationType);

		isNotDuringNext.accept(fixture);
		assertEquals("!(isDuringNext(claim.failureDate,4," + durationType
				+ "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(17).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		do {
			today = today.nextDay();
		} while (today.dayOfWeek() != Calendar.MONDAY);

		// Monday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		today = Duration.weeks(4).addedTo(today);

		// Sunday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotDuringNextForMonths() throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsNotDuringNext isNotDuringNext = new IsNotDuringNext(operand,
				startingRhsVal, durationType);

		isNotDuringNext.accept(fixture);
		assertEquals("!(isDuringNext(claim.failureDate,4," + durationType
				+ "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();

		context.put("claim", machineClaim);

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(12).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		today = today.month().end().nextDay();
		// Start of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		today = Duration.months(4).addedTo(today);
		// End of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotDuringNextForWeeks_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.WEEK.getType();
		CalendarDate today = Clock.today();

		IsNotDuringNext isNotDuringNext = new IsNotDuringNext(operand,
				dateToCompare, startingRhsVal, durationType);

		isNotDuringNext.accept(fixture);
		assertEquals("!(isDuringNext(claim.failureDate,claim.repairDate,4,"
				+ durationType + "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.weeks(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.weeks(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.weeks(17).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		do {
			today = today.nextDay();
		} while (today.dayOfWeek() != Calendar.MONDAY);

		// Monday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		today = Duration.weeks(4).addedTo(today);

		// Sunday Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void testNotDuringNextForMonths_withDateToCompare()
			throws OgnlException {
		DomainSpecificVariable operand = new DomainSpecificVariable(
				Claim.class, "claim.failureDate", businessObjectModel);
		DomainSpecificVariable dateToCompare = new DomainSpecificVariable(
				Claim.class, "claim.repairDate", businessObjectModel);
		Constant startingRhsVal = new Constant("4", "integer");
		int durationType = DateType.DurationType.MONTH.getType();
		CalendarDate today = Clock.today();

		IsNotDuringNext isNotDuringNext = new IsNotDuringNext(operand,
				dateToCompare, startingRhsVal, durationType);

		isNotDuringNext.accept(fixture);
		assertEquals("!(isDuringNext(claim.failureDate,claim.repairDate,4,"
				+ durationType + "))", fixture.getExpressionString());

		Map<String, Object> context = new OgnlContextHelper();
		CalendarDate failureDate;
		MachineClaim machineClaim = new MachineClaim();
		machineClaim.setRepairDate(today);

		context.put("claim", machineClaim);

		failureDate = Duration.months(1).subtractedFrom(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		failureDate = Duration.months(1).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(4).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = Duration.months(12).addedTo(today);
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		today = today.month().end().nextDay();
		// Start of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.previousDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));

		today = Duration.months(4).addedTo(today);
		// End of month Check
		failureDate = today;
		machineClaim.setFailureDate(failureDate);
		assertEquals(false, Ognl.getValue(fixture.getExpressionString(),
				context));

		failureDate = today.nextDay();
		machineClaim.setFailureDate(failureDate);
		assertEquals(true, Ognl
				.getValue(fixture.getExpressionString(), context));
	}

	public void allQueryPredicate() throws OgnlException {
		DomainSpecificVariable dsv = new DomainSpecificVariable(Claim.class,
				"claim.claimedItems.hoursInService", CLAIM_RULES);
		IsSameAs isSameAs = new IsSameAs(dsv);

		dsv = new DomainSpecificVariable(Claim.class,
				"claim.claimedItems.hoursInService", CLAIM_RULES);
		Value rhs = new Constant("2", "integer");
		LessThan lessThan = new LessThan(dsv, rhs);

		All all = new All();
		all.setQueryPredicate(true);
		all.addPredicate(isSameAs);
		all.addPredicate(lessThan);

		fixture.visit(all);

		String baseQuery = "select count(*) from Claim claim where claim.id != $[claim.id] and (claim.claimedItems.hoursInService = "
				+ "$[claim.claimedItems.hoursInService] and claim.claimedItems.hoursInService - $[claim.claimedItems.hoursInService] >= 2)";

		String ognlExpression = "executeQuery(\"" + baseQuery + "\")";

		assertEquals(ognlExpression, fixture.getExpressionString());

		final Claim sampleClaim = new MachineClaim();
		sampleClaim.setId(99L);

		Map<String, Object> ctx = getQueryValidatingContext(baseQuery, true);

		assertTrue((Boolean) Ognl.getValue(ognlExpression, ctx));

		ctx = getQueryValidatingContext(baseQuery, false);

		assertFalse((Boolean) Ognl.getValue(ognlExpression, ctx));
	}

	public void anyQueryPredicate() throws OgnlException {
		DomainSpecificVariable dsv = new DomainSpecificVariable(Claim.class,
				"claim.claimedItems.hoursInService", CLAIM_RULES);
		IsSameAs isSameAs = new IsSameAs(dsv);

		dsv = new DomainSpecificVariable(Claim.class,
				"claim.claimedItems.hoursInService", CLAIM_RULES);
		Value rhs = new Constant("2", "integer");
		LessThan lessThan = new LessThan(dsv, rhs);

		Any any = new Any();
		any.setQueryPredicate(true);
		any.addPredicate(isSameAs);
		any.addPredicate(lessThan);

		fixture.visit(any);

		String baseQuery = "select count(*) from Claim claim where claim.id != $[claim.id] and (claim.claimedItems.hoursInService = "
				+ "$[claim.claimedItems.hoursInService] or claim.claimedItems.hoursInService - $[claim.claimedItems.hoursInService] >= 2)";

		String ognlExpression = "executeQuery(\"" + baseQuery + "\")";

		assertEquals(ognlExpression, fixture.getExpressionString());

		final Claim sampleClaim = new MachineClaim();
		sampleClaim.setId(99L);

		Map<String, Object> ctx = getQueryValidatingContext(baseQuery, true);

		assertTrue((Boolean) Ognl.getValue(ognlExpression, ctx));

		ctx = getQueryValidatingContext(baseQuery, false);

		assertFalse((Boolean) Ognl.getValue(ognlExpression, ctx));
	}

	@SuppressWarnings("serial")
	private Map<String, Object> getQueryValidatingContext(
			final String baseQuery, final boolean result) {

		return new HashMap<String, Object>(1) {

			@SuppressWarnings("unused")
			public boolean executeQuery(String query) {
				assertEquals(baseQuery, query);
				return result;
			}
		};
	}
}