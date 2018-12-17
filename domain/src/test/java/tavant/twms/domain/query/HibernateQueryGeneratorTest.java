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

package tavant.twms.domain.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimRepository;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.common.CalendarIterator;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.rules.All;
import tavant.twms.domain.rules.Between;
import tavant.twms.domain.rules.Constant;
import tavant.twms.domain.rules.Contains;
import tavant.twms.domain.rules.DateType;
import tavant.twms.domain.rules.DoesNotContain;
import tavant.twms.domain.rules.DoesNotEndWith;
import tavant.twms.domain.rules.DoesNotStartWith;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.DomainSpecificVariable;
import tavant.twms.domain.rules.EndsWith;
import tavant.twms.domain.rules.Equals;
import tavant.twms.domain.rules.ForAnyOf;
import tavant.twms.domain.rules.ForEachOf;
import tavant.twms.domain.rules.GreaterThanOrEquals;
import tavant.twms.domain.rules.IsAfter;
import tavant.twms.domain.rules.IsDuringLast;
import tavant.twms.domain.rules.IsNotSet;
import tavant.twms.domain.rules.IsSet;
import tavant.twms.domain.rules.IsTrue;
import tavant.twms.domain.rules.NotBetween;
import tavant.twms.domain.rules.NotEquals;
import tavant.twms.domain.rules.Or;
import tavant.twms.domain.rules.Predicate;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.domain.rules.StartsWith;
import tavant.twms.domain.rules.Type;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.TypedQueryParameter;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.Duration;
import com.domainlanguage.timeutil.Clock;

public class HibernateQueryGeneratorTest extends DomainRepositoryTestCase {

    ClaimRepository claimRepository;

    InventoryService itemService;

    InventoryItem inventoryItem;

    Claim newClaim;

    CalendarDate failureDate = CalendarDate.date(2006, 5, 1);

    CalendarDate repairDate = this.failureDate.nextDay();

    private OrgService orgService;

    private PredicateAdministrationService predicateAdministrationService;

    private final String bom = BusinessObjectModelFactory.CLAIM_SEARCHES;

    private CostCategoryRepository costCategoryRepository;

    private ClaimService claimService;

    static {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
    }

    public ClaimRepository getClaimRepository() {
        return this.claimRepository;
    }

    @Required
    public void setClaimRepository(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public InventoryService getItemService() {
        return this.itemService;
    }

    @Required
    public void setItemService(InventoryService itemService) {
        this.itemService = itemService;
    }

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        this.inventoryItem = this.itemService.findSerializedItem("ABCD123456");

        // Test create
        this.newClaim = new MachineClaim(this.inventoryItem, this.failureDate, this.repairDate);
    }

    public void testStartsWithAndObjectEquals() {
//        Equals eq = new Equals(new DomainSpecificVariable(Claim.class, "claim.probableCause",
//                this.bom), new Constant("Machine", Type.STRING));
        StartsWith stEq = new StartsWith(new DomainSpecificVariable(Claim.class,
                "claim.probableCause", this.bom), new Constant("Mac", Type.STRING));
        All all = new All();
        all.setForOneToOne(false);
        all.setOneToOneVariable(null);
        all.getPredicates().add(stEq);

        DomainPredicate dp = new DomainPredicate();
        dp.setContext("ClaimRules");
        dp.setName("someName");
        dp.setPredicate(all);
        this.newClaim.setProbableCause("Machine");
        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(BusinessObjectModelFactory.CLAIM_SEARCHES);
        fixture.visit(dp);

        Query q = getQuery(fixture);
        List result = q.list();

        assertEquals(1, result.size());
        for (Object clm : result) {
            String cause = ((Claim) clm).getProbableCause();
            assertEquals(cause, "Machine");
        }

    }

    public void testIsNotSetForDomainVariable() {
        String dealerExpression = "claim.forDealer";
        DomainSpecificVariable dealer = new DomainSpecificVariable(Claim.class, dealerExpression, this.bom);
        IsNotSet isNotSet = new IsNotSet(dealer);

        this.newClaim.setProbableCause("Machine");

        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(BusinessObjectModelFactory.CLAIM_SEARCHES);
        isNotSet.accept(fixture);
        
        Query q = getQuery(fixture);
        List result = q.list();

        for (Object claim : result) {
            assertTrue(((Claim) claim).getForDealerShip() == null);
        }
    }

    public void testIsNotSetForInt() {
        String distanceExpression = "claim.serviceInformation.serviceDetail.travelDetails.distance";
        DomainSpecificVariable travelDistance = new DomainSpecificVariable(Claim.class,
                distanceExpression, this.bom);
        IsNotSet isNotSet = new IsNotSet(travelDistance);

        TravelDetail travelDetail = new TravelDetail();
        travelDetail.setDistance(new BigDecimal(1));
        ServiceDetail serviceDetail = new ServiceDetail();
        serviceDetail.setTravelDetails(travelDetail);
        ServiceInformation serviceInformation = new ServiceInformation();
        serviceInformation.setServiceDetail(serviceDetail);

        this.newClaim.setServiceInformation(serviceInformation);

        TravelDetail travelDetail1 = new TravelDetail();
        ServiceDetail serviceDetail1 = new ServiceDetail();
        serviceDetail1.setTravelDetails(travelDetail1);
        ServiceInformation serviceInformation1 = new ServiceInformation();
        serviceInformation1.setServiceDetail(serviceDetail1);
        Claim anotherClaim = new MachineClaim(this.inventoryItem, this.failureDate, this.repairDate);
        anotherClaim.setServiceInformation(serviceInformation1);

        this.claimRepository.save(this.newClaim);
        this.claimRepository.save(anotherClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);

        isNotSet.accept(fixture);
        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
        	BigDecimal val = ((Claim) clm).getServiceInformation().getServiceDetail().getTravelDetails()
                    .getDistance();
            assertTrue(val.equals(BigDecimal.ZERO));
        }
    }

    public void testIsNotSet_String() {
        final DomainSpecificVariable dSV = new DomainSpecificVariable(Claim.class,
                "claim.forDealer.name", this.bom);
        IsNotSet isNotSet = new IsNotSet(dSV);

        Dealership dealer = new Dealership();
        this.newClaim.setForDealerShip(dealer);
        getSession().save(dealer);
        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        isNotSet.accept(fixture);

        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            assertTrue(((Claim) clm).getForDealerShip().getName() == null);
        }

    }

    public void testIsSet_String() {
        final DomainSpecificVariable dSV = new DomainSpecificVariable(Claim.class,
                "claim.forDealer.name", this.bom);
        IsSet isSet = new IsSet(dSV);

        Dealership dealer = new Dealership();
        dealer.setName("   Machine  ");
        this.newClaim.setForDealerShip(dealer);
        getSession().save(dealer);
        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        isSet.accept(fixture);

        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            assertTrue(((Claim) clm).getForDealerShip().getName() != null);
        }

    }

    public void testEndsWith() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "claim.forDealer.name", this.bom);
        Constant rhsVal = new Constant("Bar", "string");
        EndsWith endsWith = new EndsWith(operand, rhsVal);

        Dealership dealer = new Dealership();
        dealer.setName("fooBar");
        this.newClaim.setForDealerShip(dealer);

        getSession().save(dealer);
        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        endsWith.accept(fixture);

        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            assertTrue(((Claim) clm).getForDealerShip().getName().endsWith("Bar"));
        }
    }

    public void testDoesNotStartWith() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "claim.forDealer.name", this.bom);
        Constant rhsVal = new Constant("foo", "string");
        DoesNotStartWith doesNotStartWith = new DoesNotStartWith(operand, rhsVal);

        Dealership dealer = new Dealership();
        dealer.setName("foo");
        this.newClaim.setForDealerShip(dealer);
        getSession().save(dealer);
        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        doesNotStartWith.accept(fixture);

        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            assertFalse(((Claim) clm).getForDealerShip().getName().startsWith("foo"));
        }

    }

    public void testDoesNotEndWith() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "claim.forDealer.name", this.bom);
        Constant rhsVal = new Constant("Bar", "string");
        DoesNotEndWith doesNotEndWith = new DoesNotEndWith(operand, rhsVal);

        Dealership dealer = new Dealership();
        dealer.setName("foo");
        this.newClaim.setForDealerShip(dealer);
        getSession().save(dealer);
        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        doesNotEndWith.accept(fixture);

        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            assertFalse(((Claim) clm).getForDealerShip().getName().endsWith("Bar"));
        }
    }

    public void testContains() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "claim.forDealer.name", this.bom);
        Constant rhsVal = new Constant("oBa", "string");
        Contains contains = new Contains(operand, rhsVal);

        Dealership dealer = new Dealership();
        dealer.setName("fooBar");
        this.newClaim.setForDealerShip(dealer);

        getSession().save(dealer);
        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        contains.accept(fixture);

        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            assertTrue(((Claim) clm).getForDealerShip().getName().contains("oBa"));
        }

    }

    public void testDoesNotContain() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "claim.forDealer.name", this.bom);
        Constant rhsVal = new Constant("oBa", "string");
        DoesNotContain doesNotContain = new DoesNotContain(operand, rhsVal);

        Dealership dealer = new Dealership();
        dealer.setName("someValue");
        this.newClaim.setForDealerShip(dealer);

        getSession().save(dealer);
        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        doesNotContain.accept(fixture);

        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            assertFalse(((Claim) clm).getForDealerShip().getName().contains("oBa"));
        }
    }

    public void testEquals_String() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class, "claim.type", this.bom);
        Constant rhsVal = new Constant("Machine", "string");
        Equals equals = new Equals(operand, rhsVal);

        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(BusinessObjectModelFactory.CLAIM_SEARCHES);
        equals.accept(fixture);

        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            String claimType = ((Claim) clm).getType().getType();
            assertTrue(claimType.equalsIgnoreCase("Machine"));
        }
    }

    public void doNottestEquals_Int() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "claim.applicablePolicy.policyDefinition.coverageTerms.serviceHoursCovered",
                this.bom);
        Constant rhsVal = new Constant("4", "integer");
        Equals equals = new Equals(operand, rhsVal);

        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        equals.accept(fixture);

        Query q = getQuery(fixture);
        List result = q.list();
        /*
         * for (Object clm : result) { assertTrue(((Claim)
         * clm).getForDealer().getName().equals("fooBar")); }
         */

    }

    public void testNotEquals_String() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "claim.forDealer.name", this.bom);
        Constant rhsVal = new Constant("fooBar", "string");
        NotEquals equals = new NotEquals(operand, rhsVal);

        Dealership dealer = new Dealership();
        dealer.setName("someValue");
        this.newClaim.setForDealerShip(dealer);

        getSession().save(dealer);
        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        equals.accept(fixture);

        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            assertFalse(((Claim) clm).getForDealerShip().getName().equals("foooBar"));
        }
    }

    public void testForAny() {
        DomainSpecificVariable _OEMPartNumberOfUnitsReplaced = new DomainSpecificVariable(
                OEMPartReplaced.class, "numberOfUnits", this.bom);
        Constant expectedNumberOfUnits = new Constant("5", Type.INTEGER);
        GreaterThanOrEquals _5OrMorePartsReplaced = new GreaterThanOrEquals(
                _OEMPartNumberOfUnitsReplaced, expectedNumberOfUnits);

        ForAnyOf forAnyOf = new ForAnyOf();
        DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.serviceDetail.oemPartsReplaced", this.bom);
        forAnyOf.setCollectionValuedVariable(domainSpecificVariable);
        forAnyOf.setConditionToBeSatisfied(_5OrMorePartsReplaced);

        List<Predicate> list = new ArrayList<Predicate>();
        list.add(forAnyOf);
        All all = new All(list);

        this.newClaim.setProbableCause("Machine");

        this.newClaim.setServiceInformation(new ServiceInformation());
        this.newClaim.getServiceInformation().setServiceDetail(new ServiceDetail());
        ServiceDetail serviceDetail = this.newClaim.getServiceInformation().getServiceDetail();

        OEMPartReplaced partReplaced = new OEMPartReplaced();
        partReplaced.setNumberOfUnits(1);
        partReplaced.setInventoryLevel(new Boolean(false));

        serviceDetail.getOEMPartsReplaced().add(partReplaced);

        partReplaced = new OEMPartReplaced();
        partReplaced.setNumberOfUnits(5);
        partReplaced.setInventoryLevel(new Boolean(false));
        serviceDetail.getOEMPartsReplaced().add(partReplaced);

        partReplaced = new OEMPartReplaced();
        partReplaced.setNumberOfUnits(7);
        partReplaced.setInventoryLevel(new Boolean(false));
        serviceDetail.getOEMPartsReplaced().add(partReplaced);

        /*
         * claimRepository.save(newClaim); assertNotNull(newClaim.getId());
         * flushAndClear();
         */
        /*
         * HibernateQueryGenerator fixture = new
         * HibernateQueryGenerator(BusinessObjectModelFactory.CLAIM_SEARCHES);
         * 
         * forAnyOf.accept(fixture);
         */

        SavedQuery sq = new SavedQuery();
        DomainPredicate dp = new DomainPredicate();
        dp.setPredicate(all);
        dp.setContext("ClaimSearches");
        dp.setName("someName");
        sq.setDomainPredicate(dp);
        sq.setCreatedBy(this.orgService.findUserById(new Long(1)));
        this.predicateAdministrationService.saveSavedQuery(sq);

        assertNotNull(sq.getId());
        this.claimRepository.save(this.newClaim);

        flushAndClear();

        Object result = this.claimService.findAllClaimsMatchingQuery(sq.getDomainPredicate()
                .getId(), new ListCriteria());

        /*
         * Query q = getQuery(fixture); List result = q.list();
         */// System.out.println(result);
        /*
         * for (Object clm : result) { boolean correctSize = false; List<OEMPartReplaced>
         * lst = ((Claim) clm).getServiceInformation()
         * .getServiceDetail().getOEMPartsReplaced(); for (OEMPartReplaced part :
         * lst) { correctSize = (part.getNumberOfUnits() >= 5); if (correctSize)
         * break; } assertTrue(correctSize); }
         */
    }

    public void testBetweenForDate() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "claim.failureDate", this.bom);
        Constant startingRhsVal = new Constant("02/28/2005", "date");
        // Constant endingRhsVal = new Constant("23/08/2006", "date");
        Between between = new Between(operand, startingRhsVal, new Constant("05/30/2007", "date"));

        // newClaim.setFailureDate(failureDate);

        Claim anotherClaim = new MachineClaim(this.inventoryItem, this.failureDate, this.repairDate);
        anotherClaim.setFailureDate(CalendarDate.date(2006, 4, 1));
        this.claimRepository.save(this.newClaim);
        this.claimRepository.save(anotherClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        // Test for non-inclusive (default).
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        between.accept(fixture);
        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            Claim c = (Claim) clm;
            assertTrue(((Claim) clm).getFailureDate().isAfter(CalendarDate.date(2005, 2, 28)));
        }
    }

    public void testIsAfter() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "claim.failureDate", this.bom);
        Constant startingRhsVal = new Constant("02/28/2005", "date");
        IsAfter isAfter = new IsAfter(operand, startingRhsVal);

        // newClaim.setFailureDate(failureDate);

        Claim anotherClaim = new MachineClaim(this.inventoryItem, this.failureDate, this.repairDate);
        anotherClaim.setFailureDate(CalendarDate.date(2004, 4, 1));
        this.claimRepository.save(this.newClaim);
        this.claimRepository.save(anotherClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        // Test for non-inclusive (default).
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        isAfter.accept(fixture);
        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            Claim c = (Claim) clm;
            assertTrue(((Claim) clm).getFailureDate().isAfter(CalendarDate.date(2005, 2, 28)));
        }
    }

    public void testIsTrue() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "claim.serviceManagerRequest", this.bom);
        IsTrue isTrue = new IsTrue(operand);

        this.newClaim.setServiceManagerRequest(true);

        Claim anotherClaim = new MachineClaim(this.inventoryItem, this.failureDate, this.repairDate);
        anotherClaim.setServiceManagerRequest(false);
        this.claimRepository.save(this.newClaim);
        this.claimRepository.save(anotherClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        isTrue.accept(fixture);
        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            assertTrue(((Claim) clm).isServiceManagerRequest());
        }

    }

    public void testOR() {
        Equals eq = new Equals(new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.causedBy", this.bom), new Constant("Machin", Type.STRING));
        StartsWith stEq = new StartsWith(new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.causedBy", this.bom), new Constant("T", Type.STRING));

        Or or = new Or(eq, stEq);

        ServiceInformation si = new ServiceInformation();
        si.setCausedBy("Machin");
        this.newClaim.setServiceInformation(si);

        Claim anotherClaim = new MachineClaim(this.inventoryItem, this.failureDate, this.repairDate);
        ServiceInformation si1 = new ServiceInformation();
        si1.setCausedBy("Toy");
        anotherClaim.setServiceInformation(si1);
        this.claimRepository.save(this.newClaim);
        this.claimRepository.save(anotherClaim);
        assertNotNull(this.newClaim.getId());
        flushAndClear();

        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        fixture.visit(or);

        Query q = getQuery(fixture);
        List result = q.list();
        for (Object clm : result) {
            assertTrue((((Claim) clm).getServiceInformation().getCausedBy().equals("Machin"))
                    || (((Claim) clm).getServiceInformation().getCausedBy().startsWith("T")));
        }
    }

    public void testIsDuringLastForDays() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "claim.failureDate", this.bom);
        Constant startingRhsVal = new Constant("1", "integer");
        int durationType = DateType.DurationType.WEEK.getType();
        CalendarDate today = Clock.today();

        IsDuringLast isDuringLast = new IsDuringLast(operand, startingRhsVal, durationType);

        CalendarDate dateForPositiveTest = CalendarIterator
                .getStartOfWeek(CalendarIterator.LAST, 1);
        this.failureDate = Duration.days(2).addedTo(dateForPositiveTest);
        this.newClaim.setFailureDate(this.failureDate);
        this.claimRepository.save(this.newClaim);
        flushAndClear();

        // Test for non-inclusive (default).
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        isDuringLast.accept(fixture);
        Query q = getQuery(fixture);
        List result = q.list();
        List<Long> ids = new ArrayList<Long>();
        for (Object clm : result) {
            ids.add(((Claim) clm).getId());
        }
        assertTrue(ids.contains(this.newClaim.getId()));

    }

    public void testAggregateFunction_OneToOneCombination() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "sum({alias}.numberOfUnits)", this.bom);
        Constant startingRhsVal = new Constant("1", "long");
        Equals eq = new Equals(operand, startingRhsVal);

        DomainSpecificVariable dsv = new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.serviceDetail.travelDetails.hours", this.bom);
        Equals eq1 = new Equals(dsv, new Constant("7", "integer"));
        List<Predicate> lst = new ArrayList<Predicate>();
        lst.add(eq);
        lst.add(eq1);
        All all = new All(lst);
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        all.accept(fixture);
        Query q = getQuery(fixture);
        q.list();

    }

    public void testOneToOne_OneToManyCombination() {
        DomainSpecificVariable dsv = new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.serviceDetail.travelDetails.hours", this.bom);
        Equals eq = new Equals(dsv, new Constant("7", "integer"));

        DomainSpecificVariable _OEMPartNumberOfUnitsReplaced = new DomainSpecificVariable(
                OEMPartReplaced.class, "numberOfUnits", this.bom);
        Constant expectedNumberOfUnits = new Constant("5", Type.INTEGER);
        GreaterThanOrEquals _5OrMorePartsReplaced = new GreaterThanOrEquals(
                _OEMPartNumberOfUnitsReplaced, expectedNumberOfUnits);

        ForAnyOf forAnyOf = new ForAnyOf();
        DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.serviceDetail.oemPartsReplaced", this.bom);
        forAnyOf.setCollectionValuedVariable(domainSpecificVariable);
        forAnyOf.setConditionToBeSatisfied(_5OrMorePartsReplaced);
        List<Predicate> lst = new ArrayList<Predicate>();
        lst.add(eq);
        lst.add(forAnyOf);
        All all = new All(lst);
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        all.accept(fixture);
        Query q = getQuery(fixture);
        q.list();

    }

    public void testOneToOne_OneToManyCombination_ReverseOrder() {
        DomainSpecificVariable dsv = new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.serviceDetail.travelDetails.hours", this.bom);
        Equals eq = new Equals(dsv, new Constant("7", "integer"));

        DomainSpecificVariable _OEMPartNumberOfUnitsReplaced = new DomainSpecificVariable(
                OEMPartReplaced.class, "numberOfUnits", this.bom);
        Constant expectedNumberOfUnits = new Constant("5", Type.INTEGER);
        GreaterThanOrEquals _5OrMorePartsReplaced = new GreaterThanOrEquals(
                _OEMPartNumberOfUnitsReplaced, expectedNumberOfUnits);

        ForAnyOf forAnyOf = new ForAnyOf();
        DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.serviceDetail.oemPartsReplaced", this.bom);
        forAnyOf.setCollectionValuedVariable(domainSpecificVariable);
        forAnyOf.setConditionToBeSatisfied(_5OrMorePartsReplaced);
        List<Predicate> lst = new ArrayList<Predicate>();
        lst.add(forAnyOf);
        lst.add(eq);
        All all = new All(lst);
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        all.accept(fixture);
        Query q = getQuery(fixture);
        q.list();

    }

    public void testAggregateFunction_OneToManyCombination() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "sum({alias}.numberOfUnits)", this.bom);
        Constant startingRhsVal = new Constant("1", "long");
        Equals eq = new Equals(operand, startingRhsVal);

        DomainSpecificVariable _OEMPartNumberOfUnitsReplaced = new DomainSpecificVariable(
                OEMPartReplaced.class, "numberOfUnits", this.bom);
        Constant expectedNumberOfUnits = new Constant("5", Type.INTEGER);
        GreaterThanOrEquals _5OrMorePartsReplaced = new GreaterThanOrEquals(
                _OEMPartNumberOfUnitsReplaced, expectedNumberOfUnits);

        ForAnyOf forAnyOf = new ForAnyOf();
        DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.serviceDetail.oemPartsReplaced", this.bom);
        forAnyOf.setCollectionValuedVariable(domainSpecificVariable);
        forAnyOf.setConditionToBeSatisfied(_5OrMorePartsReplaced);
        List<Predicate> lst = new ArrayList<Predicate>();
        lst.add(eq);
        lst.add(forAnyOf);
        All all = new All(lst);
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        all.accept(fixture);
        Query q = getQuery(fixture);
        q.list();

    }

    public void doNottestAggregateFunction_OneToOneCombination() {

        String incorrect = "select distinct claim from Claim claim where (claim.serviceInformation.serviceDetail.travelDetails.distance > 1) and (true = any (select case when oemPartsReplaced.numberOfUnits > 1 then true else false end from Claim claim1 join claim1.serviceInformation.serviceDetail.oemPartsReplaced oemPartsReplaced where claim1.id=claim.id))  ";
        String correct = "select distinct claim from Claim claim where ( ((true = any (select case when oemPartsReplaced.numberOfUnits > 1 then true else false end from claim join claim.serviceInformation.serviceDetail.oemPartsReplaced oemPartsReplaced)) ) and claim.serviceInformation.serviceDetail.travelDetails.distance > 1 )";
        Query q = getSession().createQuery(correct);
        q.list();
    }

    public void testAggregateFunction_BetweenCombination() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "sum({alias}.numberOfUnits)", this.bom);
        Constant startingRhsVal = new Constant("1", "long");
        Between eq = new Between(operand, startingRhsVal, new Constant("11", "long"));
        List<Predicate> lst = new ArrayList<Predicate>();
        lst.add(eq);
        All all = new All(lst);
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        all.accept(fixture);
        Query q = getQuery(fixture);
        q.list();

    }

    public void testAggregateFunction_NotBetweenCombination() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "sum({alias}.numberOfUnits)", this.bom);
        Constant startingRhsVal = new Constant("1", "long");
        NotBetween eq = new NotBetween(operand, startingRhsVal, new Constant("11", "long"));
        List<Predicate> lst = new ArrayList<Predicate>();
        lst.add(eq);
        All all = new All(lst);
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        all.accept(fixture);
        Query q = getQuery(fixture);
        q.list();

    }

    public void testTwoAggregateFunctionCombination() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "sum({alias}.numberOfUnits)", this.bom);
        Constant startingRhsVal = new Constant("1", "long");
        Equals eq = new Equals(operand, startingRhsVal);
        Equals simpleEq = new Equals(new DomainSpecificVariable(Claim.class,
                "sum({nonoemalias}.numberOfUnits)", this.bom), new Constant("3", "long"));
        List<Predicate> lst = new ArrayList<Predicate>();
        lst.add(eq);
        lst.add(simpleEq);
        All all = new All(lst);
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        all.accept(fixture);
        Query q = getQuery(fixture);
        q.list();

    }

    public void testOneAggregateAndOneSimpleFunctionCombination() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "sum({alias}.numberOfUnits)", this.bom);
        Constant startingRhsVal = new Constant("1", "long");
        Equals eq = new Equals(operand, startingRhsVal);
        Equals simpleEq = new Equals(new DomainSpecificVariable(Claim.class,
                "size(claim.serviceInformation.serviceDetail.oemPartsReplaced)", this.bom),
                new Constant("3", "integer"));
        List<Predicate> lst = new ArrayList<Predicate>();
        lst.add(eq);
        lst.add(simpleEq);
        All all = new All(lst);
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        all.accept(fixture);
        Query q = getQuery(fixture);
        q.list();

    }

    public void testAggregateAndForAnyCombination() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "sum({nonoemalias}.numberOfUnits)", this.bom);
        Constant startingRhsVal = new Constant("1", "long");
        Equals eq = new Equals(operand, startingRhsVal);

        DomainSpecificVariable _OEMPartNumberOfUnitsReplaced = new DomainSpecificVariable(
                OEMPartReplaced.class, "numberOfUnits", this.bom);
        Constant expectedNumberOfUnits = new Constant("5", Type.INTEGER);
        GreaterThanOrEquals _5OrMorePartsReplaced = new GreaterThanOrEquals(
                _OEMPartNumberOfUnitsReplaced, expectedNumberOfUnits);

        ForAnyOf forAnyOf = new ForAnyOf();
        DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.serviceDetail.oemPartsReplaced", this.bom);
        forAnyOf.setCollectionValuedVariable(domainSpecificVariable);
        forAnyOf.setConditionToBeSatisfied(_5OrMorePartsReplaced);

        List<Predicate> lst = new ArrayList<Predicate>();
        lst.add(eq);
        lst.add(forAnyOf);

        All all = new All(lst);
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        all.accept(fixture);
        Query q = getQuery(fixture);
        q.list();

    }

    public void testAggregateAndForEachCombination() {
        DomainSpecificVariable operand = new DomainSpecificVariable(Claim.class,
                "sum({nonoemalias}.numberOfUnits)", this.bom);
        Constant startingRhsVal = new Constant("1", "long");
        Equals eq = new Equals(operand, startingRhsVal);

        DomainSpecificVariable _OEMPartNumberOfUnitsReplaced = new DomainSpecificVariable(
                OEMPartReplaced.class, "numberOfUnits", this.bom);
        Constant expectedNumberOfUnits = new Constant("5", Type.INTEGER);
        GreaterThanOrEquals _5OrMorePartsReplaced = new GreaterThanOrEquals(
                _OEMPartNumberOfUnitsReplaced, expectedNumberOfUnits);

        ForEachOf forAnyOf = new ForEachOf();
        DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.serviceDetail.oemPartsReplaced", this.bom);
        forAnyOf.setCollectionValuedVariable(domainSpecificVariable);
        forAnyOf.setConditionToBeSatisfied(_5OrMorePartsReplaced);

        List<Predicate> lst = new ArrayList<Predicate>();
        lst.add(eq);
        lst.add(forAnyOf);

        All all = new All(lst);
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        all.accept(fixture);
        Query q = getQuery(fixture);
        q.list();

    }

    public void testForAnyAndForEachCombination() {
        DomainSpecificVariable _OEMPartNumberOfUnitsReplaced = new DomainSpecificVariable(
                OEMPartReplaced.class, "numberOfUnits", this.bom);
        Constant expectedNumberOfUnits = new Constant("5", Type.INTEGER);
        GreaterThanOrEquals _5OrMorePartsReplaced = new GreaterThanOrEquals(
                _OEMPartNumberOfUnitsReplaced, expectedNumberOfUnits);

        ForAnyOf forAnyOf = new ForAnyOf();
        DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.serviceDetail.oemPartsReplaced", this.bom);
        forAnyOf.setCollectionValuedVariable(domainSpecificVariable);
        forAnyOf.setConditionToBeSatisfied(_5OrMorePartsReplaced);

        DomainSpecificVariable dsv = new DomainSpecificVariable(NonOEMPartReplaced.class,
                "numberOfUnits", this.bom);
        Constant expected = new Constant("5", Type.INTEGER);
        GreaterThanOrEquals gt = new GreaterThanOrEquals(dsv, expected);

        ForEachOf forEachOf = new ForEachOf();
        DomainSpecificVariable dsv1 = new DomainSpecificVariable(Claim.class,
                "claim.serviceInformation.serviceDetail.oemPartsReplaced", this.bom);
        forEachOf.setCollectionValuedVariable(dsv1);
        forEachOf.setConditionToBeSatisfied(gt);

        List<Predicate> lst = new ArrayList<Predicate>();
        lst.add(forEachOf);
        lst.add(forAnyOf);

        All all = new All(lst);
        HibernateQueryGenerator fixture = new HibernateQueryGenerator(
                BusinessObjectModelFactory.CLAIM_SEARCHES);
        all.accept(fixture);
        Query q = getQuery(fixture);
        q.list();

    }

    public void doNottestHQL() {
        /*
         * DomainSpecificVariable dsv=new
         * DomainSpecificVariable(PartReturn.class,"partReturn.returnLocation.code",BusinessObjectModelFactory.PART_RETURN_SEARCHES);
         * Equals eq=new Equals(dsv,new Constant("abc",Type.STRING));
         * 
         * DomainSpecificVariable dsv1=new
         * DomainSpecificVariable(Claim.class,"claim.conditionFound",BusinessObjectModelFactory.PART_RETURN_SEARCHES);
         * Equals eq1=new Equals(dsv1,new Constant("Machine",Type.STRING));
         * 
         * 
         * HibernateQueryGenerator fixture=new
         * HibernateQueryGeneratorForPartReturn(); List<Predicate> lst=new
         * ArrayList<Predicate>(); lst.add(eq); lst.add(eq1); All all=new
         * All(lst); all.accept(fixture);
         */
        String queryString = "select count(*)  from Claim claim where ( claim.id in (select distinct claim22044881 from Claim claim22044881 where ( size(claim22044881.serviceInformation.serviceDetail.nonOEMPartsReplaced)!=67 )  and claim22044881.forDealer.id = 7 ) )  and claim.forDealer.id = 7 ";
        // String queryString="select partReturn from PartReturn partReturn";
        Query q = getSession().createQuery(queryString);
        // q.setParameter(0, "cvsfvb",Hibernate.STRING);
        // Query q=getQuery(fixture);
        List result = q.list();
        q.list();
    }

    private Query getQuery(HibernateQueryGenerator generator) {
        HibernateQuery hQuery = generator.getHibernateQuery();
        Session session = getSession();
        // System.out.println("Session is "+session);
        String queryString = hQuery.getSelectClause() + hQuery.getQueryWithoutSelect();
        // System.out.println("Query string is "+queryString);
        Query query = session.createQuery(queryString);
        int i = 0;
        for (TypedQueryParameter param : hQuery.getParameters()) {
            query.setParameter(i++, param.getValue(), param.getType());
        }
        return query;
    }

    public OrgService getOrgService() {
        return this.orgService;
    }

    @Required
    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

    public PredicateAdministrationService getPredicateAdministrationService() {
        return this.predicateAdministrationService;
    }

    @Required
    public void setPredicateAdministrationService(
            PredicateAdministrationService predicateAdministrationService) {
        this.predicateAdministrationService = predicateAdministrationService;
    }

    public CostCategoryRepository getCostCategoryRepository() {
        return this.costCategoryRepository;
    }

    @Required
    public void setCostCategoryRepository(CostCategoryRepository costCategoryRepository) {
        this.costCategoryRepository = costCategoryRepository;
    }

    public ClaimService getClaimService() {
        return this.claimService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

}
