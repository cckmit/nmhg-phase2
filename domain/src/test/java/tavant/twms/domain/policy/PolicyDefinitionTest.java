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
package tavant.twms.domain.policy;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import org.springframework.beans.factory.annotation.Required;
import static tavant.twms.domain.DomainTestHelper.getOrCreateFirstClaimedItemFromClaim;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.rules.*;
import tavant.twms.infra.DomainRepositoryTestCase;

import java.util.TimeZone;

/**
 * @author radhakrishnan.j
 * 
 */
public class PolicyDefinitionTest extends DomainRepositoryTestCase {
    private Claim machineClaim;

    private PartsClaim partsClaim;

    private ClaimedItem machineClaimedItem;

    private ClaimedItem partsClaimedItem;

    private InventoryItem inventoryItem;

    private PolicyDefinition fixture;

    private RuleExecutionTemplate ruleExecutionTemplate;

    static {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
    }

    /**
     * @param ruleExecutionTemplate the ruleExecutionTemplate to set
     */
    @Required
    public void setRuleExecutionTemplate(RuleExecutionTemplate ruleExecutionTemplate) {
        this.ruleExecutionTemplate = ruleExecutionTemplate;
    }

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        this.machineClaim = new MachineClaim();
        this.machineClaimedItem = getOrCreateFirstClaimedItemFromClaim(this.machineClaim);

        this.inventoryItem = new InventoryItem();
        this.inventoryItem.setType(InventoryType.RETAIL);

        CalendarDate shipmentDate = CalendarDate.date(2006, 1, 1);
        this.inventoryItem.setShipmentDate(shipmentDate);
        this.inventoryItem.setDeliveryDate(shipmentDate.plusMonths(4));
        this.inventoryItem.setHoursOnMachine(20);

        ItemReference itemReference = new ItemReference(this.inventoryItem);
        this.machineClaimedItem.setItemReference(itemReference);

        this.partsClaim = new PartsClaim();
        this.partsClaimedItem = getOrCreateFirstClaimedItemFromClaim(partsClaim);
        Item item = new Item();
        itemReference = new ItemReference(item);
        this.partsClaimedItem.setItemReference(itemReference);

        CoverageTerms coverageTerms = new CoverageTerms();
        coverageTerms.setMonthsCoveredFromDelivery(10);
        coverageTerms.setMonthsCoveredFromShipment(12);
        coverageTerms.setServiceHoursCovered(10);

        this.fixture = new PolicyDefinition();
        this.fixture.setCoverageTerms(coverageTerms);

        CalendarDate _12Mar2001 = CalendarDate.from("2001-3-12", "yyyy-M-d");
        CalendarDate _12Jan2001 = CalendarDate.from("2001-1-12", "yyyy-M-d");

        // Delivery date is date on which it was delivered to Dealer.
        this.inventoryItem.setShipmentDate(_12Jan2001);
        this.inventoryItem.setDeliveryDate(_12Mar2001);
    }

    public void testCovers_MachineClaim_FailureDateBeforeDateOfDelivery() throws PolicyException {
        this.machineClaimedItem.setHoursInService(9);
        this.machineClaim.setFailureDate(this.inventoryItem.getShipmentDate().previousDay());
        assertFalse(this.fixture.covers(this.machineClaimedItem, null));
    }

    public void testCovers_MachineClaim_FailureDateAfterWarrantyExpiry() throws PolicyException {
        this.machineClaimedItem.setHoursInService(9);
        CalendarDate expiryDayByShipment = this.inventoryItem.getShipmentDate().plusMonths(
                this.fixture.getCoverageTerms().getMonthsCoveredFromShipment());
        CalendarDate expiryDayByDelivery = this.inventoryItem.getDeliveryDate().plusMonths(
                this.fixture.getCoverageTerms().getMonthsCoveredFromDelivery());
        this.machineClaim
                .setFailureDate(expiryDayByShipment.isAfter(expiryDayByDelivery) ? expiryDayByDelivery
                        : expiryDayByShipment);
        assertFalse(this.fixture.covers(this.machineClaimedItem, null));
    }

    public void testCovers_MachineClaim_DateCoveredButHoursNotCovered() throws PolicyException {
        this.inventoryItem.setDeliveryDate(null);
        this.machineClaim.setFailureDate(this.inventoryItem.getShipmentDate().nextDay());
        this.machineClaimedItem.setHoursInService(11);
        assertFalse(this.fixture.covers(this.machineClaimedItem, null));
    }

    public void testCovers_MachineClaim_ClaimCovered() throws PolicyException {
        this.machineClaim.setFailureDate(this.inventoryItem.getDeliveryDate().nextDay());
        this.machineClaimedItem.setHoursInService(10);
        assertTrue(this.fixture.covers(this.machineClaimedItem, null));
    }

    public void testCovers_PartsClaim_FailureDateAfterLastDayOfCoverage() throws PolicyException {
        CalendarDate _12Mar2001 = CalendarDate.from("2001-3-12", "yyyy-M-d");
        this.partsClaim.setInstallationDate(_12Mar2001);

        CoverageTerms coverageTerms = new CoverageTerms();
        coverageTerms.setMonthsCoveredFromDelivery(10);
        coverageTerms.setMonthsCoveredFromShipment(12);

        CalendarDate _12Jan2002 = _12Mar2001.plusMonths(12);
        this.partsClaim.setFailureDate(_12Jan2002);

        PolicyDefinition fixture = new PolicyDefinition();
        fixture.setCoverageTerms(coverageTerms);

        assertFalse(fixture.covers(this.partsClaimedItem, null));
    }

    public void testCovers_PartsClaim_DateCoveredButHoursNotCovered() throws PolicyException {
        CalendarDate _12Jan2001 = CalendarDate.from("2001-1-12", "yyyy-M-d");

        CoverageTerms coverageTerms = new CoverageTerms();
        coverageTerms.setMonthsCoveredFromDelivery(10);
        coverageTerms.setMonthsCoveredFromShipment(12);
        coverageTerms.setServiceHoursCovered(1000);

        CalendarDate _11Jan2002 = _12Jan2001.plusMonths(12);

        this.partsClaim.setInstallationDate(_12Jan2001);
        this.partsClaim.setFailureDate(_11Jan2002.previousDay());
        this.partsClaimedItem.setHoursInService(1001);

        PolicyDefinition fixture = new PolicyDefinition();
        fixture.setCoverageTerms(coverageTerms);

        assertFalse(fixture.covers(this.partsClaimedItem, null));
    }

    public void testCovers_PartsClaim_claimCovered() throws PolicyException {
        CalendarDate _12Jan2001 = CalendarDate.from("2001-1-12", "yyyy-M-d");

        CoverageTerms coverageTerms = new CoverageTerms();
        coverageTerms.setMonthsCoveredFromDelivery(10);
        coverageTerms.setMonthsCoveredFromShipment(12);
        coverageTerms.setServiceHoursCovered(1000);

        CalendarDate _12Jan2002 = _12Jan2001.plusMonths(10);
        CalendarDate failureDate = _12Jan2002.previousDay();

        this.partsClaim.setInstallationDate(_12Jan2001);
        this.partsClaim.setFailureDate(failureDate);
        this.partsClaimedItem.setHoursInService(1000);

        PolicyDefinition fixture = new PolicyDefinition();
        fixture.setCoverageTerms(coverageTerms);

        assertTrue(fixture.covers(this.partsClaimedItem, null));
    }

    public void testIsStillAvailableFor_Yes() throws PolicyException {
        CalendarDate today = Clock.today();
        CalendarDate shipmentDate = today.plusMonths(-36);
        CalendarDate deliveryDate = shipmentDate.plusMonths(4);
        this.inventoryItem.setHoursOnMachine(10);
        this.inventoryItem.setShipmentDate(shipmentDate);
        this.inventoryItem.setDeliveryDate(deliveryDate);
        CoverageTerms coverageTerms = this.fixture.getCoverageTerms();
        coverageTerms.setServiceHoursCovered(10);
        coverageTerms.setMonthsCoveredFromDelivery(46);
        coverageTerms.setMonthsCoveredFromShipment(48);
        assertTrue(this.fixture.isStillAvailable(this.inventoryItem));
    }

    public void testIsStillAvailableFor_HoursExpired() throws PolicyException {
        CalendarDate today = Clock.today();
        CalendarDate shipmentDate = today.plusMonths(-36);
        CalendarDate deliveryDate = shipmentDate.plusMonths(4);
        this.inventoryItem.setHoursOnMachine(11);
        this.inventoryItem.setShipmentDate(shipmentDate);
        this.inventoryItem.setDeliveryDate(deliveryDate);
        CoverageTerms coverageTerms = this.fixture.getCoverageTerms();
        coverageTerms.setServiceHoursCovered(10);
        coverageTerms.setMonthsCoveredFromDelivery(46);
        coverageTerms.setMonthsCoveredFromShipment(48);
        assertFalse(this.fixture.isStillAvailable(this.inventoryItem));
    }

    public void testIsStillAvailableFor_TimeExpired() throws PolicyException {
        CalendarDate today = Clock.today();

        // Y'day is last day of the 2 yr window from '_2YearsEalier'

        CalendarDate shipmentDate = today.plusMonths(-24);
        CalendarDate deliveryDate = shipmentDate.plusMonths(2);
        this.inventoryItem.setHoursOnMachine(10);
        this.inventoryItem.setShipmentDate(shipmentDate);
        this.inventoryItem.setDeliveryDate(deliveryDate);
        CoverageTerms coverageTerms = this.fixture.getCoverageTerms();
        coverageTerms.setServiceHoursCovered(10);
        coverageTerms.setMonthsCoveredFromDelivery(22);
        coverageTerms.setMonthsCoveredFromShipment(24);
        assertFalse(this.fixture.isStillAvailable(this.inventoryItem));
    }

    public void testIsStillAvailableFor_CheckShouldFailForStockInventory() {
        CalendarDate today = Clock.today();

        // Y'day is last day of the 2 yr window from '_2YearsEalier'

        CalendarDate shipmentDate = today.plusMonths(-24);
        this.inventoryItem.setType(InventoryType.STOCK);
        this.inventoryItem.setHoursOnMachine(10);
        this.inventoryItem.setShipmentDate(shipmentDate);
        this.inventoryItem.setDeliveryDate(null);
        CoverageTerms coverageTerms = this.fixture.getCoverageTerms();
        coverageTerms.setServiceHoursCovered(10);
        coverageTerms.setMonthsCoveredFromDelivery(22);
        coverageTerms.setMonthsCoveredFromShipment(24);
        Exception ex = null;
        try {
            this.fixture.isStillAvailable(this.inventoryItem);
        } catch (PolicyException e) {
            ex = e;
        }
        assertNotNull(ex);
    }

    public void testIsApplicableFor_Yes() {
        MachineClaim claim = new MachineClaim();

        DomainSpecificVariable claimType = new DomainSpecificVariable(Claim.class,
                "claim.type.type", BusinessObjectModelFactory.CLAIM_RULES);
        Equals equals = new Equals(claimType, new Constant("Machine", Type.STRING));
        DomainPredicate domainPredicate = new DomainPredicate();
        domainPredicate.setPredicate(equals);

        DomainRule domainRule = new DomainRule();
        domainRule.setPredicate(domainPredicate);
/*       domainRule.setName("Test Rule");*/
        domainRule.setDescription("Test Rule");
/*       domainRule.setFailureMessage("Test Rule failed");*/
        DomainRuleAudit dra = new DomainRuleAudit();
        dra.setName("Test Rule");
        dra.setFailureMessage("Test Rule failed");
        domainRule.getRuleAudits().add(dra);

        this.fixture.getApplicabilityTerms().add(domainRule);
        assertTrue(this.fixture.isApplicable(claim, this.ruleExecutionTemplate));
    }

    public void testIsApplicableFor_No() {
        MachineClaim claim = new MachineClaim();

        DomainSpecificVariable claimType = new DomainSpecificVariable(Claim.class,
                "claim.type.type", BusinessObjectModelFactory.CLAIM_RULES);
        Equals equals = new Equals(claimType, new Constant("Parts", Type.STRING));
        DomainPredicate domainPredicate = new DomainPredicate();
        domainPredicate.setPredicate(equals);

        DomainRule domainRule = new DomainRule();
        domainRule.setPredicate(domainPredicate);
/*        domainRule.setName("Rule 1");*/
        domainRule.setDescription("Rule that evaluates to false");
/*        domainRule.setFailureMessage("Rule 1 failed");*/
        DomainRuleAudit dra = new DomainRuleAudit();
        dra.setName("Rule 1");
        dra.setFailureMessage("Rule 1 failed");
        domainRule.getRuleAudits().add(dra);
        this.fixture.getApplicabilityTerms().add(domainRule);

        equals = new Equals(claimType, new Constant("Machine", Type.STRING));
        domainPredicate = new DomainPredicate();
        domainPredicate.setPredicate(equals);
        domainRule = new DomainRule();
        domainRule.setPredicate(domainPredicate);
/*        domainRule.setName("Rule 2");*/
        domainRule.setDescription("Rule that evaluates to true");
/*        domainRule.setFailureMessage("Rule 2 failed");*/
        dra = new DomainRuleAudit();
        dra.setName("Rule 2");
        dra.setFailureMessage("Rule 2 failed");
        domainRule.getRuleAudits().add(dra);
        this.fixture.getApplicabilityTerms().add(domainRule);

        assertFalse(this.fixture.isApplicable(claim, this.ruleExecutionTemplate));
    }
}
