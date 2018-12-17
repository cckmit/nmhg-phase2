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
package tavant.twms.domain.policy;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.payment.definition.PaymentDefinition;
import tavant.twms.domain.claim.payment.definition.PaymentDefinitionAdminService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class PolicyServiceImplTest extends DomainRepositoryTestCase {

    private PolicyService policyService;

    private CatalogService catalogService;

    private PaymentDefinitionAdminService paymentDefinitionAdminService;

    private OrgService orgService;

    private InventoryService inventoryService;

    public void testFindingPaymentDefinition() {
        Claim machineClaim = new MachineClaim();
        machineClaim.setFailureDate(Clock.today());
        PaymentDefinition paymentDefinition = this.paymentDefinitionAdminService
                .findBestPaymentDefinition(machineClaim, null);
        assertNotNull(paymentDefinition);
        assertEquals(8, paymentDefinition.getPaymentSections().size());
    }

    Claim createMachineClaim() {
        Claim aMachineClaim = new MachineClaim();
        Dealership dealership = this.orgService.findDealerByName("A-L-L EQUIPMENT");
        aMachineClaim.setForDealerShip(dealership);
        aMachineClaim.setInstallationDate(Clock.today());
        aMachineClaim.setRepairDate(Clock.today());
        aMachineClaim.setFailureDate(Clock.today());
        return aMachineClaim;
    }

    public void testFindApplicablePolicy_UnserializedMachineClaim() throws Exception {
        Claim aMachineClaim = createMachineClaim();
        Item item = this.catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-3");
        ItemReference itemReference = new ItemReference(item);
        ClaimedItem claimedItem = new ClaimedItem(itemReference);
        claimedItem.setHoursInService(40);
        claimedItem.setClaim(aMachineClaim);
        aMachineClaim.addClaimedItem(claimedItem);
        Policy policy = this.policyService.findApplicablePolicy(claimedItem);
        assertNotNull(policy);
    }

    public void testFindApplicablePolicy_MachineClaim_RegisteredForWarranty() throws Exception {
        Claim aMachineClaim = createMachineClaim();
        InventoryItem inventoryItem = this.inventoryService.findSerializedItem("LX3742");
        ItemReference itemReference = new ItemReference(inventoryItem);
        ClaimedItem claimedItem = new ClaimedItem(itemReference);
        claimedItem.setHoursInService(4000);
        claimedItem.setClaim(aMachineClaim);
        aMachineClaim.addClaimedItem(claimedItem);
        aMachineClaim.setFailureDate(CalendarDate.date(2006, 1, 1));
        Policy policy = this.policyService.findApplicablePolicy(claimedItem);
        assertNotNull(policy);
        assertEquals("STD-01", policy.getPolicyDefinition().getCode());
    }

    public void disable_testFindApplicablePolicy_MachineClaim_NotRegisteredForWarranty() throws Exception {
        Claim aMachineClaim = createMachineClaim();
        InventoryItem inventoryItem = this.inventoryService.findSerializedItem("SLNO1002");
        ItemReference itemReference = new ItemReference(inventoryItem);
        ClaimedItem claimedItem = new ClaimedItem(itemReference);
        claimedItem.setHoursInService(4000);
        claimedItem.setClaim(aMachineClaim);
        aMachineClaim.addClaimedItem(claimedItem);
        aMachineClaim.setFailureDate(CalendarDate.date(2006, 1, 1));
        Policy policy = this.policyService.findApplicablePolicy(claimedItem);
        assertNotNull(policy);
        assertEquals("STD-01", policy.getPolicyDefinition().getCode());

    }

    public void testFindApplicablePolicy_MachineClaim_InvInStock() throws Exception {
        Claim aMachineClaim = createMachineClaim();
        InventoryItem inventoryItem = this.inventoryService.findSerializedItem("SLNO1003");
        ItemReference itemReference = new ItemReference(inventoryItem);
        ClaimedItem claimedItem = new ClaimedItem(itemReference);
        claimedItem.setHoursInService(4000);
        claimedItem.setClaim(aMachineClaim);
        aMachineClaim.addClaimedItem(claimedItem);
        aMachineClaim.setFailureDate(CalendarDate.date(2006, 1, 1));
        Policy policy = this.policyService.findApplicablePolicy(claimedItem);
        assertNull(policy);
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

    public void setPaymentDefinitionAdminService(
            PaymentDefinitionAdminService paymentDefinitionAdminService) {
        this.paymentDefinitionAdminService = paymentDefinitionAdminService;
    }

    public void setPolicyService(PolicyService policyService) {
        this.policyService = policyService;
    }
//
//    //    public void testPolicyWithMaximumCoverage_Atleast2PoliciesSameCoverage() throws Exception {
//    //        // ( Policy3,$200), (Policy2,$400) and (Policy1,$400)
//    //        Money claimedAmt = Money.dollars(500);
//    //        Payment payment3 = new Payment();
//    //        Payment payment2 = new Payment();
//    //        Payment payment1 = new Payment();
//    //
//    //        payment3.setClaimedAmount(claimedAmt);
//    //        payment2.setClaimedAmount(claimedAmt);
//    //        payment1.setClaimedAmount(claimedAmt);
//    //
//    //        payment3.setTotalAmount(Money.dollars(200));
//    //        payment2.setTotalAmount(Money.dollars(400));
//    //        payment1.setTotalAmount(Money.dollars(400));
//    //
//    //        this.paymentServiceMock.expects(once()).method("calculatePaymentForClaimedItem").with(
//    //            new Constraint[] { eq(this.claim.getClaimedItems().get(0)), eq(this.policy1) }).will(
//    //            returnValue(payment3));
//    //        this.paymentServiceMock.expects(once()).method("calculatePaymentForClaimedItem").with(
//    //            new Constraint[] { eq(this.claim.getClaimedItems().get(0)), eq(this.policy2) }).will(
//    //            returnValue(payment2));
//    //        this.paymentServiceMock.expects(once()).method("calculatePaymentForClaimedItem").with(
//    //            new Constraint[] { eq(this.claim.getClaimedItems().get(0)), eq(this.policy3) }).will(
//    //            returnValue(payment1));
//    //
//    //        // assertEquals(this.policy2,
//    //        // this.fixture.policyWithMaximumCoverage(this.claim
//    //        // .getClaimedItems().get(0), this.policies));
//    //    }
//    //
//    //    public void testPolicyWithMaximumCoverage_LeastPriorityPolicyGivesFullCoverage()
//    //            throws Exception {
//    //        // ( Policy3,$200), (Policy2,$400) and (Policy1,$500)
//    //        Money claimedAmt = Money.dollars(500);
//    //        Payment payment3 = new Payment();
//    //        Payment payment2 = new Payment();
//    //        Payment payment1 = new Payment();
//    //
//    //        payment3.setClaimedAmount(claimedAmt);
//    //        payment2.setClaimedAmount(claimedAmt);
//    //        payment1.setClaimedAmount(claimedAmt);
//    //
//    //        payment1.setTotalAmount(Money.dollars(200));
//    //        payment2.setTotalAmount(Money.dollars(400));
//    //        payment3.setTotalAmount(Money.dollars(500));
//    //
//    //        this.paymentServiceMock.expects(once()).method("calculatePaymentForClaimedItem").with(
//    //            new Constraint[] { eq(this.claim.getClaimedItems().get(0)), eq(this.policy1) }).will(
//    //            returnValue(payment1));
//    //        this.paymentServiceMock.expects(once()).method("calculatePaymentForClaimedItem").with(
//    //            new Constraint[] { eq(this.claim.getClaimedItems().get(0)), eq(this.policy2) }).will(
//    //            returnValue(payment2));
//    //        this.paymentServiceMock.expects(once()).method("calculatePaymentForClaimedItem").with(
//    //            new Constraint[] { eq(this.claim.getClaimedItems().get(0)), eq(this.policy3) }).will(
//    //            returnValue(payment3));
//    //
//    //        // assertEquals(this.policy3,
//    //        // this.fixture.policyWithMaximumCoverage(this.claim
//    //        // .getClaimedItems().get(0), this.policies));
//    //    }
//    //
//    //    public void doNottestFindApplicablePolicy_PartsClaim() throws Exception {
//    //        PartsClaim partsClaim = new PartsClaim() {
//    //
//    //            @Override
//    //            public Currency getCurrencyForCalculation() {
//    //                return GlobalConfiguration.getInstance().getBaseCurrency();
//    //            }
//    //
//    //        };
//    //
//    //        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(partsClaim);
//    //
//    //        assertNull(this.fixture.findApplicablePolicy(claimedItem));
//    //    }
//    //
//    //    public void testFindPoliciesForRegistration() throws PolicyException {
//    //        ArrayList<PolicyDefinition> policyDefinitions = new ArrayList<PolicyDefinition>();
//    //        policyDefinitions.add(new PolicyDefinition() {
//    //            @Override
//    //            public boolean isStillAvailable(InventoryItem inventoryItem) throws PolicyException {
//    //                return true;
//    //            }
//    //
//    //            @Override
//    //            public boolean isAvailable(InventoryItem inventoryItem, CalendarDate aDate)
//    //                    throws PolicyException {
//    //                return true;
//    //            }
//    //
//    //        });
//    //        policyDefinitions.add(new PolicyDefinition() {
//    //
//    //            @Override
//    //            public boolean isStillAvailable(InventoryItem inventoryItem) throws PolicyException {
//    //                return false;
//    //            }
//    //
//    //            @Override
//    //            public boolean isAvailable(InventoryItem inventoryItem, CalendarDate aDate)
//    //                    throws PolicyException {
//    //                return false;
//    //            }
//    //
//    //        });
//    //
//    //        this.inventoryItem.setSerialNumber("KA-21-LB-9007");
//    //        CalendarDate _2YearsEarlier = Clock.today().plusMonths(-2);
//    //        this.inventoryItem.setShipmentDate(_2YearsEarlier.nextDay());
//    //        this.inventoryItem.setDeliveryDate(_2YearsEarlier.plusMonths(2));
//    //        this.policyDefinitionRepositoryMock.expects(once()).method("findPoliciesForInventory")
//    //                .with(eq(this.inventoryItem.getSerialNumber()),
//    //                    eq(this.inventoryItem.getDeliveryDate())).will(returnValue(policyDefinitions));
//    //        List<? extends Policy> policiesForRegistration = this.fixture
//    //                .findPoliciesAvailableForRegistration(this.inventoryItem);
//    //        assertEquals(1, policiesForRegistration.size());
//    //        assertEquals(policyDefinitions.get(0), policiesForRegistration.get(0));
//    //    }
//    //
//    //    static class MockRuleExecutionTemplate extends RuleExecutionTemplate {
//    //    }
}
