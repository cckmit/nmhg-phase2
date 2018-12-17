/**
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
package tavant.twms.domain.supplier.contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.supplier.contract.CoverageCondition.ComparisonWith;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

/**
 * 
 * @author kannan.ekanath
 * 
 */
public class ContractServiceUnitTest extends TestCase {

    final Map<String, Section> allSections = new HashMap<String, Section>();

    private CatalogService catalogService;

    @Override
    protected void setUp() throws Exception {
        allSections.put(Section.OEM_PARTS, new Section(Section.OEM_PARTS));
        allSections.put(Section.NON_OEM_PARTS, new Section(Section.NON_OEM_PARTS));
        allSections.put(Section.LABOR, new Section(Section.LABOR));
        allSections.put(Section.TRAVEL_BY_DISTANCE, new Section(Section.TRAVEL_BY_DISTANCE));        
        allSections.put(Section.ITEM_FREIGHT_DUTY, new Section(Section.ITEM_FREIGHT_DUTY));
        allSections.put(Section.MEALS, new Section(Section.MEALS));
        allSections.put(Section.TOTAL_CLAIM, new Section(Section.TOTAL_CLAIM));
    }

    ContractService contractService = new ContractServiceImpl() {
        @Override
        protected List<Section> getAllSections() {
            return new ArrayList<Section>(allSections.values());
        }

    };

    public void testContractProrating() throws CatalogException {
        Claim claim = new MachineClaim();
        ServiceInformation serviceInformation = new ServiceInformation();
        claim.setServiceInformation(serviceInformation);
        ServiceDetail serviceDetail = new ServiceDetail();
        serviceInformation.setServiceDetail(serviceDetail);
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setType(InventoryType.STOCK);
        inventoryItem.setBuiltOn(CalendarDate.date(2007, 10, 12));
        claim.setRepairDate(CalendarDate.date(2007, 10, 14));
        CoverageCondition coverageCondition1 = new CoverageCondition(5,
                ComparisonWith.DATE_OF_MANUFACTURE);
        CoverageCondition coverageCondition2 = new CoverageCondition(5, ComparisonWith.ENERGY_UNITS);
        ClaimedItem claimedItem = new ClaimedItem();
        claimedItem.setHoursInService(3);
        ItemReference itemReference = new ItemReference();
        itemReference.setReferredInventoryItem(inventoryItem);
        claimedItem.setItemReference(itemReference);
        claim.addClaimedItem(claimedItem);
        Contract contract = new Contract();
        contract.setCollateralDamageToBePaid(true);
        RecoveryFormula formula = new RecoveryFormula(80, Money.dollars(20), Money.dollars(5),
                Money.dollars(1000));
        CompensationTerm compensationTerm = new CompensationTerm(
                allSections.get(Section.OEM_PARTS), formula);
        compensationTerm.setPriceType(CompensationTerm.DEALER_NET_PRICE);
        contract.addCompensationTerm(compensationTerm);
        contract.addCompensationTerm(new CompensationTerm(allSections.get(Section.LABOR), formula));
        contract.addCompensationTerm(new CompensationTerm(allSections.get(Section.TRAVEL_BY_DISTANCE), formula));
        contract.addCompensationTerm(new CompensationTerm(allSections
                .get(Section.ITEM_FREIGHT_DUTY), formula));
        contract.addCompensationTerm(new CompensationTerm(allSections.get(Section.MEALS), formula));
        contract.getCoverageConditions().add(coverageCondition1);
        contract.getCoverageConditions().add(coverageCondition2);

        Item supplierItem = new Item();
        supplierItem.setCostPrice(Money.dollars(10));

        OEMPartReplaced part1 = new OEMPartReplaced(null, 4);
        part1.setSupplierItem(supplierItem);
        part1.setPricePerUnit(Money.dollars(10));

        OEMPartReplaced part2 = new OEMPartReplaced(null, 1);
        part2.setSupplierItem(supplierItem);
        part2.setPricePerUnit(Money.dollars(10));

        OEMPartReplaced part3 = new OEMPartReplaced(null, 1);
        part3.setSupplierItem(supplierItem);
        part3.setPricePerUnit(Money.dollars(10));

        OEMPartReplaced part4 = new OEMPartReplaced(null, 1);
        part4.setSupplierItem(supplierItem);
        part4.setPricePerUnit(Money.dollars(10));

        OEMPartReplaced part5 = new OEMPartReplaced(null, 2);
        part5.setSupplierItem(supplierItem);
        part5.setPricePerUnit(Money.dollars(10));

        serviceDetail.setOEMPartsReplaced(Arrays.asList(new OEMPartReplaced[] { part1, part2,
                part3, part4, part5 }));

        Payment payment = new Payment();
        // Added all parts, now proceed to add payment data
        claim.setPayment(payment);
        RecoveryClaim recoveryClaim = new RecoveryClaim();
        claim.addRecoveryClaim(recoveryClaim);
        recoveryClaim.setContract(contract);
        recoveryClaim.setClaim(claim);
        LineItemGroup partsGroup = payment.addLineItemGroup(Section.OEM_PARTS);
//        partsGroup.setGroupTotal(Money.dollars(200));
//        partsGroup.setAcceptedTotal(Money.dollars(180));

        LineItemGroup laborGroup = payment.addLineItemGroup(Section.LABOR);
//        laborGroup.setGroupTotal(Money.dollars(150));
//        laborGroup.setAcceptedTotal(Money.dollars(150));

        LineItemGroup travelGroup = payment.addLineItemGroup(Section.TRAVEL_BY_DISTANCE);
//        travelGroup.setGroupTotal(Money.dollars(100));
//        travelGroup.setAcceptedTotal(Money.dollars(100));

        LineItemGroup freightGroup = payment.addLineItemGroup(Section.ITEM_FREIGHT_DUTY);
//        freightGroup.setGroupTotal(Money.dollars(50));
//        freightGroup.setAcceptedTotal(Money.dollars(50));

        LineItemGroup mealsGroup = payment.addLineItemGroup(Section.MEALS);
//        mealsGroup.setGroupTotal(Money.dollars(50));
//        mealsGroup.setAcceptedTotal(Money.dollars(50));

        LineItemGroup totalsGroup = payment.addLineItemGroup(Section.TOTAL_CLAIM);
//        totalsGroup.setGroupTotal(Money.dollars(480));
//        totalsGroup.setAcceptedTotal(Money.dollars(480));

        payment.setTotalAmount(Money.dollars(480));

        contractService.updateSupplierCostLineItems(recoveryClaim, claim.getServiceInformation()
                .getServiceDetail().getOEMPartsReplaced());

        assertFalse(recoveryClaim.getCostLineItems().isEmpty());
        assertFalse(recoveryClaim.getCostLineItems().isEmpty());
        assertFalse(recoveryClaim.getCostLineItems().isEmpty());

        // others dont have cost line items
        /*
         * assertNull(part3.getSupplierPartReturn());
         * assertNull(part4.getSupplierPartReturn());
         */
        // There should have been 4 cost line items for each supplier
        // part
        // return
        Money partsRemainingAmount = payment.getAcceptedTotalAfterGlobalModifiersProrated(
                partsGroup.getName()).minus(
                claim.getServiceInformation().getServiceDetail().getTotalCostOfParts());
        assertEquals(6, recoveryClaim.getCostLineItems().size());

        // Check the groups total
        Money afterContract = recoveryClaim.getContract().getCompensationTermForSection(
                Section.OEM_PARTS).getRecoveryFormula().apply(Money.dollars(90),
                recoveryClaim.getFactor(Section.OEM_PARTS));
        assertEquals(afterContract, recoveryClaim.getCostLineItem(Section.OEM_PARTS)
                .getCostAfterApplyingContract());

        assertEquals(afterContract, Money.dollars(252));

        Money laborAmount = payment.getAcceptedTotalAfterGlobalModifiersProrated(laborGroup
                .getName());
        assertEquals(laborAmount, recoveryClaim.getCostLineItem(Section.LABOR).getActualCost());
        afterContract = recoveryClaim.getContract().getCompensationTermForSection(Section.LABOR)
                .getRecoveryFormula().apply(laborAmount, 1);
        assertEquals(afterContract, recoveryClaim.getCostLineItem(Section.LABOR)
                .getCostAfterApplyingContract());
        assertEquals(afterContract, Money.dollars(140));
        Money travelAmount = payment.getAcceptedTotalAfterGlobalModifiersProrated(travelGroup
                .getName());
        assertEquals(travelAmount, recoveryClaim.getCostLineItem(Section.TRAVEL_BY_DISTANCE).getActualCost());
        afterContract = recoveryClaim.getContract().getCompensationTermForSection(Section.TRAVEL_BY_DISTANCE)
                .getRecoveryFormula().apply(travelAmount, 1);
        assertEquals(afterContract, recoveryClaim.getCostLineItem(Section.TRAVEL_BY_DISTANCE)
                .getCostAfterApplyingContract());
        assertEquals(afterContract, Money.dollars(100));

        Money freightAmount = payment.getAcceptedTotalAfterGlobalModifiersProrated(mealsGroup
                .getName());
        assertEquals(freightAmount, recoveryClaim.getCostLineItem(Section.ITEM_FREIGHT_DUTY)
                .getActualCost());
        afterContract = recoveryClaim.getContract().getCompensationTermForSection(
                Section.ITEM_FREIGHT_DUTY).getRecoveryFormula().apply(freightAmount, 1);
        assertEquals(afterContract, recoveryClaim.getCostLineItem(Section.ITEM_FREIGHT_DUTY)
                .getCostAfterApplyingContract());
        assertEquals(afterContract, Money.dollars(60));

        Money mealsAmount = payment.getAcceptedTotalAfterGlobalModifiersProrated(mealsGroup
                .getName());
        assertEquals(mealsAmount, recoveryClaim.getCostLineItem(Section.MEALS).getActualCost());
        afterContract = recoveryClaim.getContract().getCompensationTermForSection(Section.MEALS)
                .getRecoveryFormula().apply(mealsAmount, 1);
        assertEquals(afterContract, recoveryClaim.getCostLineItem(Section.MEALS)
                .getCostAfterApplyingContract());
        assertEquals(afterContract, Money.dollars(60));

        Money nonOemPartsAmount = Money.dollars(0);
        assertEquals(nonOemPartsAmount, recoveryClaim.getCostLineItem(Section.NON_OEM_PARTS)
                .getActualCost());
        assertEquals(Money.dollars(0), recoveryClaim.getCostLineItem(Section.NON_OEM_PARTS)
                .getCostAfterApplyingContract());

    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }
}
