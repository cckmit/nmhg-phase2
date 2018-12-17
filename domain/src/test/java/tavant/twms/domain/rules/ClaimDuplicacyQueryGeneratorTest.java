package tavant.twms.domain.rules;

import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.CLAIM_RULES;
import static tavant.twms.domain.rules.PredicateEvaluator.ADD_DATE_DURATION;
import static tavant.twms.domain.rules.PredicateEvaluator.SUBTRACT_DATE_DURATION;
import junit.framework.TestCase;
import tavant.twms.domain.claim.Claim;

/**
 * ClaimDuplicacyQueryGenerator Tester.
 * 
 * @author <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * @version 1.0
 * @since
 * 
 * <pre>
 * 06 / 24 / 2007
 * </pre>
 */
public class ClaimDuplicacyQueryGeneratorTest extends TestCase {

	private final ClaimDuplicacyQueryGenerator fixture = new ClaimDuplicacyQueryGenerator();
	private static final String CLAIM_DUPLICACY_BASE_QUERY = "select count(*) from Claim claim where claim.id != ${claim.id}$";

	public void testBuildSelectClause() throws Exception {
		assertEquals("select count(*) from Claim claim", fixture
				.buildSelectAndFromClause());
	}

	public void testGetQueryForNothingSet() {
		String query = fixture.getQuery();

		assertEquals(CLAIM_DUPLICACY_BASE_QUERY, query);
	}

	// For all the following tests, if the test is for a non-nary predicate,
	// we wrap the predicate within an All or Any, since the generator requires
	// one of those for generating the full query.

	public void testGetQueryForLessThanAndIntegerField() {
		Value lhs = new DomainSpecificVariable(Claim.class,
				"claim.claimedItems.hoursInService", CLAIM_RULES);
		Value rhs = new Constant("2", "integer");
		LessThan lessThan = new LessThan(lhs, rhs);

		fixture.visit(wrapInAll(lessThan));

		String query = fixture.getQuery();

		assertEquals(
				CLAIM_DUPLICACY_BASE_QUERY
						+ " and (claim.claimedItems.hoursInService - ${claim.claimedItems.hoursInService}$ >= 2)",
				query);

	}

	public void testGetQueryForDateLessThanWithDays() {
		DomainSpecificVariable lhs = new DomainSpecificVariable(Claim.class,
				"claim.failureDate", CLAIM_RULES);
		Constant rhs = new Constant("2", "integer");
		int durationType = DateType.DurationType.DAY.getType();

		DateLesserBy lessThan = new DateLesserBy(lhs, rhs, durationType);

		fixture.visit(wrapInAll(lessThan));

		String query = fixture.getQuery();

		assertEquals(CLAIM_DUPLICACY_BASE_QUERY + " and (claim.failureDate >= "
				+ "'${" + ADD_DATE_DURATION + "(claim.failureDate, 2, "
				+ durationType + ")}$')", query);
	}

	public void testGetQueryForGreaterThan() {
		Value lhs = new DomainSpecificVariable(Claim.class,
				"claim.claimedItems.hoursInService", CLAIM_RULES);
		Value rhs = new Constant("2", "integer");
		GreaterThan lessThan = new GreaterThan(lhs, rhs);

		fixture.visit(wrapInAll(lessThan));

		String query = fixture.getQuery();

		assertEquals(
				CLAIM_DUPLICACY_BASE_QUERY
						+ " and (${claim.claimedItems.hoursInService}$ - claim.claimedItems.hoursInService >= 2)",
				query);
	}

	public void testGetQueryForDateGreaterThanWithDays() {
		DomainSpecificVariable lhs = new DomainSpecificVariable(Claim.class,
				"claim.failureDate", CLAIM_RULES);
		Constant rhs = new Constant("2", "integer");
		int durationType = DateType.DurationType.DAY.getType();

		DateGreaterBy greaterThan = new DateGreaterBy(lhs, rhs, durationType);

		fixture.visit(wrapInAll(greaterThan));

		String query = fixture.getQuery();

		assertEquals(CLAIM_DUPLICACY_BASE_QUERY + " and ('${"
				+ SUBTRACT_DATE_DURATION + "(claim.failureDate, 2, "
				+ durationType + ")}$' >= " + "claim.failureDate)", query);

	}

	public void testGetQueryForSameAsAndSimpleField() {
		DomainSpecificVariable dsv = new DomainSpecificVariable(Claim.class,
				"claim.claimedItems.hoursInService", CLAIM_RULES);
		IsSameAs isSameAs = new IsSameAs(dsv);

		fixture.visit(wrapInAll(isSameAs));

		String query = fixture.getQuery();

		assertEquals(
				CLAIM_DUPLICACY_BASE_QUERY
						+ " and (claim.claimedItems.hoursInService = ${claim.claimedItems.hoursInService}$)",
				query);
	}

	public void testGetQueryForSameAsAndOne2OneField() {
		DomainSpecificVariable dsv = new DomainSpecificVariable(Claim.class,
				"claim.forDealer", CLAIM_RULES);
		IsSameAs isSameAs = new IsSameAs(dsv);

		fixture.visit(wrapInAll(isSameAs));

		String query = fixture.getQuery();

		assertEquals(CLAIM_DUPLICACY_BASE_QUERY
				+ " and (claim.forDealer.id = ${claim.forDealer.id}$)", query);
	}

	public void testGetQueryForAllOfASimpleFieldAndOne2OneField() {
		DomainSpecificVariable dsv = new DomainSpecificVariable(Claim.class,
				"claim.claimedItems.hoursInService", CLAIM_RULES);
		IsSameAs isSameAs = new IsSameAs(dsv);

		dsv = new DomainSpecificVariable(Claim.class,
				"claim.claimedItems.hoursInService", CLAIM_RULES);
		Value rhs = new Constant("2", "integer");
		LessThan lessThan = new LessThan(dsv, rhs);

		All all = new All();
		all.addPredicate(isSameAs);
		all.addPredicate(lessThan);

		fixture.visit(all);

		String query = fixture.getQuery();

		assertEquals(
				CLAIM_DUPLICACY_BASE_QUERY
						+ " and (claim.claimedItems.hoursInService = ${claim.claimedItems.hoursInService}$ and "
						+ "claim.claimedItems.hoursInService - ${claim.claimedItems.hoursInService}$ >= 2)",
				query);
	}

	public void testGetQueryForAnyOfASimpleFieldAndOne2OneField() {
		DomainSpecificVariable dsv = new DomainSpecificVariable(Claim.class,
				"claim.claimedItems.hoursInService", CLAIM_RULES);
		IsSameAs isSameAs = new IsSameAs(dsv);

		dsv = new DomainSpecificVariable(Claim.class,
				"claim.claimedItems.hoursInService", CLAIM_RULES);
		Value rhs = new Constant("2", "integer");
		LessThan lessThan = new LessThan(dsv, rhs);

		Any any = new Any();
		any.addPredicate(isSameAs);
		any.addPredicate(lessThan);

		fixture.visit(any);

		String query = fixture.getQuery();

		assertEquals(
				CLAIM_DUPLICACY_BASE_QUERY
						+ " and (claim.claimedItems.hoursInService = ${claim.claimedItems.hoursInService}$ or "
						+ "claim.claimedItems.hoursInService - ${claim.claimedItems.hoursInService}$ >= 2)",
				query);
	}

	private All wrapInAll(Predicate p) {
		All all = new All();
		all.addPredicate(p);

		return all;
	}

}