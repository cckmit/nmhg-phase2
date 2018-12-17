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
package tavant.twms.domain.partreturn;

import static tavant.twms.domain.DomainTestHelper.getOrCreateFirstClaimedItemFromClaim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.ObjectNotFoundException;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemCriterion;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.common.Duration;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author vineeth.varghese
 * 
 */
public class PartReturnServiceImplTest extends DomainRepositoryTestCase {

    private CatalogService catalogService;

    private PartReturnService partReturnService;

    private PartReturnDefinitionRepository partReturnDefinitionRepository;

    private LocationRepository locationRepository;

    private PartReturnDefinition definition;

    static {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
    }

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        definition = new PartReturnDefinition();
        definition.getItemCriterion().setItem(catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-1"));

        PartReturnConfiguration configuration = new PartReturnConfiguration();
        configuration.setPaymentCondition(new PaymentCondition("PAY"));
        configuration.setReturnLocation(locationRepository.findByLocationCode("IR_KEN"));
        configuration.setDueDays(10);
        Duration duration = new Duration(Clock.today(), Clock.today());
        configuration.setDuration(duration);
        definition.addPartReturnConfiguration(configuration);
    }

    public void disable_testUpdateClaimPartsReturnData() throws Exception {
        Claim claim = getClaim();
        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(claim);
        claim.setFailureDate(CalendarDate.date(2006, 6, 6));
        claim.setFiledOnDate(CalendarDate.date(2006, 6, 6));

        /*
         * Ensuring the right date value of the
         * Claim.claimedItems.itemReference.referredInventoryItem.shipmentDate
         * is too much to do for now. Instead let it be an unserialized item
         * claim and lets stick the installation date. See
         * {@link Claim.getEquipmentBilledDate()}
         */
        ItemReference itemReference = new ItemReference(catalogService
                .findItemOwnedByManuf("MC-COUGAR-50-HZ-1"));
        claimedItem.setItemReference(itemReference);
        claim.setInstallationDate(CalendarDate.date(2006, 6, 6));
        claim.getServiceInformation();

        ServiceInformation serviceInformation = claim.getServiceInformation();
        Item item1 = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-2");
        serviceInformation.setCausalPart(item1);

        ServiceDetail serviceDetail = serviceInformation.getServiceDetail();
        List<OEMPartReplaced> partsReplaced = serviceDetail.getOEMPartsReplaced();

        Item item2 = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-1");
        Item item3 = catalogService.findItemOwnedByManuf("MC-PEGASUS-50-HZ-1");
        OEMPartReplaced partReplaced1 = new OEMPartReplaced(new ItemReference(item1), 5);
        OEMPartReplaced partReplaced2 = new OEMPartReplaced(new ItemReference(item2), 2);
        OEMPartReplaced partReplaced3 = new OEMPartReplaced(new ItemReference(item3), 1);
        partsReplaced.add(partReplaced1);
        partsReplaced.add(partReplaced2);
        partsReplaced.add(partReplaced3);
        // Before API Call Test
        assertFalse(partReplaced1.isPartToBeReturned());
        assertEquals(0, partReplaced1.getPartReturns().size());
        assertFalse(partReplaced2.isPartToBeReturned());
        assertEquals(0, partReplaced2.getPartReturns().size());
        assertFalse(partReplaced3.isPartToBeReturned());
        assertEquals(0, partReplaced3.getPartReturns().size());

        partReturnService.updatePartReturnsForClaim(claim, null);

        // After API Call Tests
        //Fix Me: It is taking more time to fix these things.. Will fix it once complete datasetup is done
        assertFalse(partReplaced1.getPartReturns().isEmpty());
        assertEquals(5, partReplaced1.getPartReturns().size());
        assertFalse(partReplaced2.isPartToBeReturned());
        assertEquals(0, partReplaced2.getPartReturns().size());
        assertFalse(partReplaced3.isPartToBeReturned());
        assertEquals(0, partReplaced3.getPartReturns().size());
    }

    public void testPositiveIsEligibleForPayment() {
        OEMPartReplaced partForNoReturn = new OEMPartReplaced();
        Claim claim = getClaim();
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForNoReturn);
        List<PartReturn> partReturns;

        assertTrue(partReturnService.isEligibleForPayment(claim));

        OEMPartReplaced partJustForReturn = new OEMPartReplaced();
        PartReturn partReturn = new PartReturn();
        partReturn.setPaymentCondition(new PaymentCondition("PAY"));
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partJustForReturn.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partJustForReturn);

        assertTrue(partReturnService.isEligibleForPayment(claim));

        OEMPartReplaced partForPayOnReturn = new OEMPartReplaced();
        partReturn = new PartReturn();
        partReturn.setPartReceived(true);
        partReturn.setPaymentCondition(new PaymentCondition("PAY_ON_RETURN"));
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partForPayOnReturn.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForPayOnReturn);

        assertTrue(partReturnService.isEligibleForPayment(claim));

        OEMPartReplaced partForPayOnInspection = new OEMPartReplaced();
        partReturn = new PartReturn();
        partReturn.setPartReceived(true);
        InspectionResult inspectionResult = new InspectionResult();
        inspectionResult.setAccepted(true);
        partReturn.setInspectionResult(inspectionResult);
        partReturn.setPaymentCondition(new PaymentCondition("PAY_ON_INSPECTION"));
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partForPayOnInspection.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForPayOnInspection);
        assertTrue(partReturnService.isEligibleForPayment(claim));
    }

    public void testNegativeIsEligibleForPayment() throws CatalogException {
        OEMPartReplaced partForNoReturn = new OEMPartReplaced();
        Claim claim = getClaim();
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForNoReturn);
        assertTrue(partReturnService.isEligibleForPayment(claim));

        OEMPartReplaced partJustForReturn = new OEMPartReplaced();
        PartReturn partReturn = new PartReturn();
        partReturn.setPaymentCondition(new PaymentCondition("PAY"));
        List<PartReturn> partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partJustForReturn.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partJustForReturn);
        assertTrue(partReturnService.isEligibleForPayment(claim));

        OEMPartReplaced partForPayOnReturn = new OEMPartReplaced();
        partReturn = new PartReturn();
        partReturn.setPaymentCondition(new PaymentCondition("PAY_ON_RETURN"));
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partForPayOnReturn.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForPayOnReturn);
        assertFalse(partReturnService.isEligibleForPayment(claim));

        partReturn.setPartReceived(true);
        OEMPartReplaced partForPayOnInspection = new OEMPartReplaced();
        partReturn = new PartReturn();
        partReturn.setPartReceived(true);
        InspectionResult inspectionResult = new InspectionResult();
        partReturn.setInspectionResult(inspectionResult);
        partReturn.setPaymentCondition(new PaymentCondition("PAY_ON_INSPECTION"));
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partForPayOnInspection.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForPayOnInspection);
        assertFalse(partReturnService.isEligibleForPayment(claim));

        Item item = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-2");
        PaymentCondition paymentCondition = new PaymentCondition("PAY_ON_INSPECTION");
        partForPayOnInspection = new OEMPartReplaced(new ItemReference(item), 2);
        PartReturn partReturn1 = new PartReturn();
        partReturn1.setPartReceived(true);
        partReturn1.setInspectionResult(inspectionResult);
        partReturn1.setPaymentCondition(paymentCondition);
        PartReturn partReturn2 = new PartReturn();
        partReturn2.setPartReceived(true);
        InspectionResult acceptedInspectionResult = new InspectionResult();
        acceptedInspectionResult.setAccepted(true);
        partReturn2.setInspectionResult(acceptedInspectionResult);
        partReturn2.setPaymentCondition(paymentCondition);
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn1);
        partReturns.add(partReturn2);
        partForPayOnInspection.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForPayOnInspection);
        assertFalse(partReturnService.isEligibleForPayment(claim));

    }

    public void testAcceptPartReturnAfterInspection() {
        OEMPartReplaced part = new OEMPartReplaced();
        part.setItemReference(new ItemReference(new Item()));
        List<PartReturn> partReturns = new ArrayList<PartReturn>();
        partReturns.add(new PartReturn());
        part.setPartReturns(partReturns);
        partReturnService.acceptPartAfterInspection(partReturns, "ACCEPT", "COE");
        InspectionResult inspectionResult = partReturns.get(0).getInspectionResult();
        assertNotNull(inspectionResult);
        assertTrue(inspectionResult.isAccepted());
        assertEquals("ACCEPT", inspectionResult.getComments());
        assertNull(inspectionResult.getFailureReason());
    }

    public void testRejectPartReturnAfterInspection() {
        OEMPartReplaced part = new OEMPartReplaced();
        part.setItemReference(new ItemReference(new Item()));
        List<PartReturn> partReturns = new ArrayList<PartReturn>();
        partReturns.add(new PartReturn());
        part.setPartReturns(partReturns);
        partReturnService.rejectPartAfterInspection(partReturns, "COE", "REJECT");
        InspectionResult inspectionResult = partReturns.get(0).getInspectionResult();
        assertNotNull(inspectionResult);
        assertFalse(inspectionResult.isAccepted());
        assertEquals("REJECT", inspectionResult.getComments());
        assertNotNull(inspectionResult.getFailureReason());
        assertEquals("Customer Operation Error", inspectionResult.getFailureReason()
                .getDescription());
    }

    public void xtestRejectPartReturnAfterInspectionWrongReason() {
        OEMPartReplaced part = new OEMPartReplaced();
        part.setItemReference(new ItemReference(new Item()));
        List<PartReturn> partReturns = new ArrayList<PartReturn>();
        partReturns.add(new PartReturn());
        part.setPartReturns(partReturns);
        try {
            partReturnService.rejectPartAfterInspection(partReturns, "WRONGCODE", "REJECT");
            fail("Wrong code dint throw exception");
        } catch (ObjectNotFoundException e) {
            // pass
        }
    }

    public void testLoadPaymentConditions() {
        PaymentCondition paymentCondition = partReturnDefinitionRepository
                .findPaymentConditionForCode("PAY");
        assertEquals(paymentCondition.getCode(), "PAY");
    }

    public void testPartReturnEditable() {
        // Just a simple JUNIT test, not a EngineRepoTestCase, move it out
        // somewhere
        PartReturn partReturn = new PartReturn();
        partReturn.setStatus(PartReturnStatus.PART_ACCEPTED);
        assertFalse(partReturn.isReturnDetailsEditable());
        partReturn.setStatus(PartReturnStatus.PART_TO_BE_SHIPPED);
        assertTrue(partReturn.isReturnDetailsEditable());
    }

    public void testCreatePartReturnDefinition() {
        partReturnDefinitionRepository.save(definition);
        getSession().flush();
        getSession().clear();
        assertNotNull(definition.getId());
    }

    // public void testUpdatePartReturnDefinition() {
    // this.partReturnDefinitionRepository.save(this.definition);
    // getSession().flush();
    // getSession().clear();
    // Long id = this.definition.getId();
    //
    // PaymentCondition paymentCondition = new
    // PaymentCondition("PAY_ON_RETURN");
    // this.partReturnDefinitionRepository.update(this.definition);
    //
    // PartReturnConfiguration configuration = new PartReturnConfiguration();
    // configuration.setPaymentCondition(paymentCondition);
    // try {
    // this.definition.set(configuration, new CalendarDuration(Clock.today(),
    // Clock.today()));
    // } catch (DurationOverlapException e) {
    // fail();
    // }
    // PartReturnDefinition definition2 =
    // this.partReturnDefinitionRepository.findById(id);
    // PartReturnConfigurationItem entryAsOf =
    // definition2.getEntryAsOf(Clock.today());
    // assertEquals(paymentCondition,
    // entryAsOf.getValue().getPaymentCondition());
    // }

    public void testIsUnique() throws CatalogException {
        PartReturnDefinition definition = new PartReturnDefinition();
        Item item = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-1");
        definition.setItemCriterion(new ItemCriterion(item));
        boolean isUnique = partReturnService.isUnique(definition);
        assertTrue("If warrenty type is null", isUnique);

        definition = new PartReturnDefinition();
        item = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-1");
        definition.setItemCriterion(new ItemCriterion(item));
        definition.getCriteria().setWarrantyType("");
        isUnique = partReturnService.isUnique(definition);
        assertTrue("If warrenty type is an empty string", isUnique);

        definition = new PartReturnDefinition();
        item = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-1");
        definition.setItemCriterion(new ItemCriterion(item));
        definition.getCriteria().setWarrantyType("STANDARD");
        isUnique = partReturnService.isUnique(definition);
        assertTrue("If warrenty type STANDARD", isUnique);

        definition = new PartReturnDefinition();
        item = catalogService.findItemOwnedByManuf("MC-COUGAR-60-HZ-3");
        definition.setItemCriterion(new ItemCriterion(item));
        isUnique = partReturnService.isUnique(definition);
        assertTrue("For a new Item", isUnique);
    }

    public void testPaymentConditionConsistency() throws CatalogException {
        Item item = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-2");
        OEMPartReplaced partReplaced = new OEMPartReplaced(new ItemReference(item), 2);
        List<PartReturn> partReturns = new ArrayList<PartReturn>();
        PartReturn partReturn1 = new PartReturn();
        partReturn1.setPaymentCondition(new PaymentCondition("PAY"));
        PartReturn partReturn2 = new PartReturn();
        partReturn1.setPaymentCondition(new PaymentCondition("PAY_ON_RETURN"));
        partReturns.add(partReturn1);
        partReturns.add(partReturn2);
        partReplaced.setPartReturns(partReturns);
        try {
            partReturnService.checkPaymentConditionConsistency(partReplaced);
            fail("Expected an IllegalArgumentException");
        } catch (IllegalStateException e) {

        }
    }

    public void disable_testUpdateExistingPartReturns() throws CatalogException {
        Claim claim = getClaim();
        ItemReference itemReference = new ItemReference(catalogService
                .findItemOwnedByManuf("MC-COUGAR-50-HZ-1"));
        claim.setItemReference(itemReference);
        claim.setFailureDate(CalendarDate.date(2006, 6, 6));
        claim.setFiledOnDate(CalendarDate.date(2006, 6, 6));
        claim.setInstallationDate(CalendarDate.date(2006, 6, 6));
        Item item1 = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-2");
        ServiceInformation serviceInformation = claim.getServiceInformation();
        serviceInformation.setCausalPart(item1);
        ServiceDetail serviceDetail = serviceInformation.getServiceDetail();
        Item item = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-2");
        OEMPartReplaced partReplaced = new OEMPartReplaced(new ItemReference(item), 5);
        List<OEMPartReplaced> oemPartsReplaced = new ArrayList<OEMPartReplaced>();
        oemPartsReplaced.add(partReplaced);
        serviceDetail.setOEMPartsReplaced(oemPartsReplaced);
        partReturnService.updatePartReturnsForClaim(claim, null);
        Collection<PartReturn> partReturns = partReplaced.getPartReturns();
        int countToBeTriggered = 0;
        for (PartReturn partReturn : partReturns) {
            if (PartReturnTaskTriggerStatus.TO_BE_TRIGGERED.equals(partReturn.getTriggerStatus())) {
                countToBeTriggered++;
            }
        }
        assertEquals(5, countToBeTriggered);
        partReplaced.setPartToBeReturned(true);
        partReplaced.setNumberOfUnits(4);
        partReplaced.setPartReturn(partReturns.iterator().next());

        partReturnService.updatePartReturnsForClaim(claim, null);
        countToBeTriggered = 0;
        int coutToBeEnded = 0;
        for (PartReturn partReturn : partReturns) {
            if (PartReturnTaskTriggerStatus.TO_BE_TRIGGERED.equals(partReturn.getTriggerStatus())) {
                countToBeTriggered++;
            } else if (PartReturnTaskTriggerStatus.TO_BE_ENDED
                    .equals(partReturn.getTriggerStatus())) {
                coutToBeEnded++;
            }
        }
        assertEquals(9, countToBeTriggered);
        assertEquals(0, coutToBeEnded);

    }

    public void d_testUpdateExistingPartReturnsForShipmentGeratedParts() throws CatalogException {
        Claim claim = getClaim();
        ItemReference itemReference = new ItemReference(catalogService
                .findItemOwnedByManuf("MC-COUGAR-50-HZ-1"));
        claim.setItemReference(itemReference);
        claim.setFailureDate(CalendarDate.date(2006, 6, 6));
        claim.setFiledOnDate(CalendarDate.date(2006, 6, 6));
        claim.setInstallationDate(CalendarDate.date(2006, 6, 6));
        Item item1 = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-2");
        ServiceInformation serviceInformation = claim.getServiceInformation();
        serviceInformation.setCausalPart(item1);
        ServiceDetail serviceDetail = serviceInformation.getServiceDetail();
        Item item = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-2");
        OEMPartReplaced partReplaced = new OEMPartReplaced(new ItemReference(item), 5);
        List<OEMPartReplaced> oemPartsReplaced = new ArrayList<OEMPartReplaced>();
        oemPartsReplaced.add(partReplaced);
        serviceDetail.setOEMPartsReplaced(oemPartsReplaced);
        partReturnService.updatePartReturnsForClaim(claim, null);
        Collection<PartReturn> partReturns = partReplaced.getPartReturns();
        int countToBeTriggered = 0;
        for (PartReturn partReturn : partReturns) {
            if (PartReturnTaskTriggerStatus.TO_BE_TRIGGERED.equals(partReturn.getTriggerStatus())) {
                countToBeTriggered++;
            }
        }
        assertEquals(5, countToBeTriggered);
        partReplaced.setPartToBeReturned(true);
        partReplaced.setNumberOfUnits(3);
        int count = 0;
        for (PartReturn partReturn : partReturns) {
            if (count < 1) {
                partReturn.setStatus(PartReturnStatus.PART_TO_BE_SHIPPED);
                count++;
            } else {
                partReturn.setStatus(PartReturnStatus.SHIPMENT_GENERATED);
                count++;
            }
        }
        partReplaced.setPartReturn(partReturns.iterator().next());
        partReturnService.updatePartReturnsForClaim(claim, null);
        int tobeShippedCount = 0;
        int shipmentGeneratedCount = 0;
        for (PartReturn partReturn : partReturns) {
            if (PartReturnTaskTriggerStatus.TO_BE_ENDED.equals(partReturn.getTriggerStatus())
                    && PartReturnStatus.PART_TO_BE_SHIPPED.equals(partReturn.getStatus())) {
                tobeShippedCount++;
            } else if (PartReturnTaskTriggerStatus.TO_BE_ENDED
                    .equals(partReturn.getTriggerStatus())) {
                shipmentGeneratedCount++;
            }
        }
        assertEquals(0, tobeShippedCount);
        assertEquals(0, shipmentGeneratedCount);

    }

    Claim getClaim() {
        Claim claim = new MachineClaim();
        ServiceInformation serviceInformation = new ServiceInformation();
        ServiceDetail serviceDetail = new ServiceDetail();
        claim.setServiceInformation(serviceInformation);
        serviceInformation.setServiceDetail(serviceDetail);
        return claim;
    }

    public void testPositiveCanMakeDecisionForPayment() {
        OEMPartReplaced partForNoReturn = new OEMPartReplaced();
        Claim claim = getClaim();
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForNoReturn);
        assertTrue(partReturnService.canMakePaymentDecision(claim));

        List<PartReturn> partReturns = null;
        OEMPartReplaced partJustForReturn = new OEMPartReplaced();
        PartReturn partReturn = new PartReturn();
        partReturn.setPaymentCondition(new PaymentCondition("PAY"));
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partJustForReturn.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partJustForReturn);
        assertTrue(partReturnService.canMakePaymentDecision(claim));

        OEMPartReplaced partForPayOnReturn = new OEMPartReplaced();
        partReturn = new PartReturn();
        partReturn.setPartReceived(true);
        partReturn.setPaymentCondition(new PaymentCondition("PAY_ON_RETURN"));
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partForPayOnReturn.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForPayOnReturn);
        assertTrue(partReturnService.canMakePaymentDecision(claim));

        OEMPartReplaced partForPayOnInspection = new OEMPartReplaced();
        partReturn = new PartReturn();
        partReturn.setPartReceived(true);
        InspectionResult inspectionResult = new InspectionResult();
        inspectionResult.setAccepted(true);
        partReturn.setInspectionResult(inspectionResult);
        partReturn.setPaymentCondition(new PaymentCondition("PAY_ON_INSPECTION"));
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partForPayOnInspection.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForPayOnInspection);
        assertTrue(partReturnService.canMakePaymentDecision(claim));

        // Irrespective of the inspection result, return true
        partForPayOnInspection = new OEMPartReplaced();
        inspectionResult.setAccepted(false);
        partReturn.setInspectionResult(inspectionResult);
        partReturn.setPaymentCondition(new PaymentCondition("PAY_ON_INSPECTION"));
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partForPayOnInspection.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForPayOnInspection);
        assertTrue(partReturnService.canMakePaymentDecision(claim));
    }

    public void testNegativeCanMakeDecisionForPayment() throws CatalogException {
        OEMPartReplaced partForNoReturn = new OEMPartReplaced();
        Claim claim = getClaim();
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForNoReturn);
        assertTrue(partReturnService.isEligibleForPayment(claim));

        OEMPartReplaced partJustForReturn = new OEMPartReplaced();
        PartReturn partReturn = new PartReturn();
        partReturn.setPaymentCondition(new PaymentCondition("PAY"));
        List<PartReturn> partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partJustForReturn.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partJustForReturn);
        assertTrue(partReturnService.isEligibleForPayment(claim));

        OEMPartReplaced partForPayOnReturn = new OEMPartReplaced();
        partReturn = new PartReturn();
        partReturn.setPaymentCondition(new PaymentCondition("PAY_ON_RETURN"));
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partForPayOnReturn.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForPayOnReturn);
        assertFalse(partReturnService.isEligibleForPayment(claim));

        partReturn.setPartReceived(true);
        OEMPartReplaced partForPayOnInspection = new OEMPartReplaced();
        partReturn = new PartReturn();
        partReturn.setPartReceived(true);
        InspectionResult inspectionResult = new InspectionResult();
        partReturn.setInspectionResult(inspectionResult);
        partReturn.setPaymentCondition(new PaymentCondition("PAY_ON_INSPECTION"));
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partForPayOnInspection.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForPayOnInspection);
        assertFalse(partReturnService.isEligibleForPayment(claim));

        Item item = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-2");
        PaymentCondition paymentCondition = new PaymentCondition("PAY_ON_INSPECTION");
        partForPayOnInspection = new OEMPartReplaced(new ItemReference(item), 2);
        PartReturn partReturn1 = new PartReturn();
        partReturn1.setPartReceived(true);
        partReturn1.setInspectionResult(inspectionResult);
        partReturn1.setPaymentCondition(paymentCondition);
        PartReturn partReturn2 = new PartReturn();
        partReturn2.setPartReceived(true);
        InspectionResult acceptedInspectionResult = new InspectionResult();
        acceptedInspectionResult.setAccepted(true);
        partReturn2.setInspectionResult(acceptedInspectionResult);
        partReturn2.setPaymentCondition(paymentCondition);
        PartReturn partReturn3 = new PartReturn();
        partReturn3.setPartReceived(true);
        InspectionResult rejectedInspectionResult = new InspectionResult();
        rejectedInspectionResult.setAccepted(false);
        partReturn3.setInspectionResult(rejectedInspectionResult);
        partReturn3.setPaymentCondition(paymentCondition);
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn1);
        partReturns.add(partReturn2);
        partReturns.add(partReturn3);
        partForPayOnInspection.setPartReturns(partReturns);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(partForPayOnInspection);
        assertFalse(partReturnService.isEligibleForPayment(claim));

    }

    /**
     * @param catalogService the catalogService to set
     */
    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * @param partReturnService the partReturnService to set
     */
    public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

    /**
     * @param partReturnDefinitionRepository the partReturnDefinitionRepository
     * to set
     */
    public void setPartReturnDefinitionRepository(
            PartReturnDefinitionRepository partReturnDefinitionRepository) {
        this.partReturnDefinitionRepository = partReturnDefinitionRepository;
    }

    public void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

}
