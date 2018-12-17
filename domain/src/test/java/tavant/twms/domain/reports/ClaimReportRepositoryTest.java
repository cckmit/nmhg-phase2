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
package tavant.twms.domain.reports;

import static tavant.twms.domain.DomainTestHelper.getOrCreateFirstClaimedItemFromClaim;

import java.math.BigDecimal;
import java.util.*;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimRepository;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.definition.PaymentSectionRepository;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.Duration;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.LocationRepository;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnConfiguration;
import tavant.twms.domain.partreturn.PartReturnDefinition;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PartReturnTaskTriggerStatus;
import tavant.twms.domain.partreturn.PaymentCondition;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.partreturn.ShipmentService;
import tavant.twms.domain.supplier.CostLineItem;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * 
 * @author bibin.jacob
 * 
 */
public class ClaimReportRepositoryTest extends DomainRepositoryTestCase {
    ClaimReportRepository claimReportRepository;

    ClaimRepository claimRepository;

    PartReturnService partReturnService;

    DealershipRepository dealershipRepository;

    CatalogService catalogService;

    LocationRepository locationRepository;

    PartReturnDefinition definition;

    ShipmentService shipmentService;

    PaymentSectionRepository paymentSectionRepository;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        this.definition = new PartReturnDefinition();
        this.definition.getItemCriterion().setItem(
                this.catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-1"));
        PartReturnConfiguration configuration = new PartReturnConfiguration();
        configuration.setPaymentCondition(new PaymentCondition("PAY"));
        configuration.setReturnLocation(this.locationRepository.findByLocationCode("IR_KEN"));
        configuration.setDueDays(10);
        configuration.setDuration(new Duration(Clock.today(), Clock.today()));
        this.definition.addPartReturnConfiguration(configuration);
        Claim claim1 = getClaim();
        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(claim1);

        claim1.setState(ClaimState.DRAFT);
        claim1.setProcessedAutomatically();
        claim1.setFailureDate(CalendarDate.date(2006, 6, 5));
        claim1.setRepairDate(CalendarDate.date(2006, 6, 12));
        claim1.setFiledOnDate(CalendarDate.date(2006, 6, 6));
        Payment payment = new Payment();
        payment.setClaimedAmount(Money.dollars(new BigDecimal(10)));
        claim1.setPayment(payment);
        Item item = new Item();
        ItemGroup model = new ItemGroup();
        model.setGroupCode("COUGAR-50Hz");
        model.setItemGroupType("MODEL");
        item.setModel(model);
        item.setModel(model);
        ItemGroup product = new ItemGroup();
        product.setGroupCode("COUGAR");
        product.setItemGroupType("PRODUCT");
        item.setProduct(product);
        ItemReference itemReference = new ItemReference();
        itemReference.setReferredItem(item);
        claimedItem.setItemReference(itemReference);
        populatePartReturnInfo(claim1);
        this.claimRepository.save(claim1);
        flushAndClear();
        Claim claim2 = getClaim();
        claim2.setState(ClaimState.ACCEPTED);
        claim2.setFailureDate(CalendarDate.date(2006, 6, 5));
        claim1.setRepairDate(CalendarDate.date(2006, 6, 12));
        claim2.setFiledOnDate(CalendarDate.date(2006, 6, 6));
        populatePartReturnInfo(claim2);
        this.claimRepository.save(claim2);
        flushAndClear();
    }

    public void testfindAllClaimForCriteria() {
        ReportSearchCriteria reportSearchCriteria = new ReportSearchCriteria();
        populateSearchCriteria(reportSearchCriteria);
        List<Claim> matchingClaims = this.claimReportRepository
                .findAllClaimsForCriteria(reportSearchCriteria);
        assertNotNull(matchingClaims);
    }

    public void testfindClaimsForProcessingEfficiency() {
        ReportSearchCriteria reportSearchCriteria = new ReportSearchCriteria();
        populateSearchCriteria(reportSearchCriteria);
        List matchingClaims = this.claimReportRepository.findClaimsForProcessingEfficiency();
        assertNotNull(matchingClaims);
    }

    public void testfindPartReturns() throws Exception {
        ReportSearchCriteria reportSearchCriteria = new ReportSearchCriteria();
        populateSearchCriteria(reportSearchCriteria);
        // List partReturns =
        // this.claimReportRepository.findPartReturns(reportSearchCriteria);
        // assertNotNull(partReturns);
    }

    public void testfindAllDuePartReturns() {
        ReportSearchCriteria reportSearchCriteria = new ReportSearchCriteria();
        populateSearchCriteria(reportSearchCriteria);
        // List partReturns =
        // this.claimReportRepository.findAllDuePartReturns(reportSearchCriteria);
        // assertNotNull(partReturns);
    }

    /*
     * public void testfindSupplierRecovery() { ReportSearchCriteria
     * reportSearchCriteria = new ReportSearchCriteria();
     * populateSearchCriteria(reportSearchCriteria); List supplierRecovery =
     * this.claimReportRepository .findSupplierRecovery(reportSearchCriteria);
     * assertNotNull(supplierRecovery); }
     */

    public void testfindWarrantyPayout() {
        ReportSearchCriteria reportSearchCriteria = new ReportSearchCriteria();
        populateSearchCriteria(reportSearchCriteria);
        reportSearchCriteria.setOrderBy("month");
        List warrantyPayout = this.claimReportRepository.findWarrantyPayout(reportSearchCriteria);
        assertNotNull(warrantyPayout);
    }

    public void testfindClaimsByProduct() {
        ReportSearchCriteria reportSearchCriteria = new ReportSearchCriteria();
        populateSearchCriteria(reportSearchCriteria);
        List claimsByProduct = this.claimReportRepository.findClaimsByProduct(reportSearchCriteria);
        assertNotNull(claimsByProduct);
    }

    public void testfindClaimsByFault() {
        ReportSearchCriteria reportSearchCriteria = new ReportSearchCriteria();
        populateSearchCriteria(reportSearchCriteria);
        List claimsByProduct = this.claimReportRepository.findClaimsByFault(reportSearchCriteria);
        assertNotNull(claimsByProduct);
    }

    public void testfindTaxAmount() {
        ReportSearchCriteria reportSearchCriteria = new ReportSearchCriteria();
        populateSearchCriteria(reportSearchCriteria);
        List taxAmount = this.claimReportRepository.findTaxAmount();
        assertNotNull(taxAmount);
    }

    private Claim getClaim() {
        Claim claim = new MachineClaim();
        ServiceInformation serviceInformation = new ServiceInformation();
        ServiceDetail serviceDetail = new ServiceDetail();
        claim.setServiceInformation(serviceInformation);
        serviceInformation.setServiceDetail(serviceDetail);
        Dealership forDealer = new Dealership();
        forDealer.setName("Test Dealer");
        forDealer.setDealerNumber("123456");
        this.dealershipRepository.createDealership(forDealer);
        claim.setForDealerShip(forDealer);
        return claim;
    }

    private void populatePartReturnInfo(Claim claim) throws Exception {
        Location destination = this.locationRepository.findByLocationCode("IR_LOC");
        Shipment shipment = new Shipment(destination);
        shipment.setShipmentDate(new Date(2006, 6, 15));
        Supplier supplier = (Supplier) getSession().load(Supplier.class, 31L);
        ItemReference itemReference = new ItemReference(this.catalogService
                .findItemOwnedByManuf("ATTCH-UNIGY-50-HZ-1"));
        claim.getClaimedItems().get(0).setItemReference(itemReference);
        claim.setInstallationDate(CalendarDate.date(2006, 6, 4));

        ServiceInformation serviceInformation = claim.getServiceInformation();
        serviceInformation.setFaultCode("A017");
        Item item1 = this.catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-2");
        serviceInformation.setCausalPart(item1);
        ServiceDetail serviceDetail = serviceInformation.getServiceDetail();
        List<OEMPartReplaced> partsReplaced = serviceDetail.getOEMPartsReplaced();
        List<PartReturn> partReturns;

        Item item2 = this.catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-1");
        Item item3 = this.catalogService.findItemOwnedByManuf("MC-PEGASUS-50-HZ-1");
        PartReturn partReturn = new PartReturn();
        partReturn.setPaymentCondition(new PaymentCondition("PAY"));
        partReturn.setDueDate(CalendarDate.date(2006, 6, 12));
        partReturn.setReturnLocation(destination);
        partReturn.setTriggerStatus(PartReturnTaskTriggerStatus.TO_BE_TRIGGERED);
        Dealership forDealer = new Dealership();
        forDealer.setName("Test Dealer");
        forDealer.setDealerNumber("123456");
        this.dealershipRepository.createDealership(forDealer);
        partReturn.setReturnedBy(forDealer);
        CostLineItem lineItem = new CostLineItem();
        lineItem.setActualCost(Money.dollars(10.80));
        lineItem.setCostAfterApplyingContract(Money.dollars(140.80));
        lineItem.setRecoveredCost(Money.dollars(5.80));
        Section section = this.paymentSectionRepository.getSectionWithName("OEM Parts");
        lineItem.setSection(section);
        List<CostLineItem> costLineItems = new ArrayList<CostLineItem>();
        costLineItems.add(lineItem);

        RecoveryClaim recoveryClaim = new RecoveryClaim();
        recoveryClaim.setCostLineItems(costLineItems);
        recoveryClaim.setSupplier(supplier);
        OEMPartReplaced partReplaced1 = new OEMPartReplaced(new ItemReference(item1), 5);
        claim.addRecoveryClaim(recoveryClaim);
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partReplaced1.setPartReturns(partReturns);
        partReplaced1.setShipment(shipment);
        shipment.addPart(partReturn);
        partReturn.setOemPartReplaced(partReplaced1);
        partReturn.getOemPartReplaced().setShipment(shipment);

        OEMPartReplaced partReplaced2 = new OEMPartReplaced(new ItemReference(item2), 2);
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partReplaced2.setPartReturns(partReturns);
        partReplaced2.setShipment(null);
        shipment.addPart(partReturn);

        // partReturn.setOemPartReplaced(partReplaced2);

        OEMPartReplaced partReplaced3 = new OEMPartReplaced(new ItemReference(item3), 1);
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partReplaced3.setPartReturns(partReturns);
        partReplaced3.setShipment(shipment);
        shipment.addPart(partReturn);
        // partReturn.setOemPartReplaced(partReplaced3);

        partsReplaced.add(partReplaced1);
        partsReplaced.add(partReplaced2);
        partsReplaced.add(partReplaced3);

        this.shipmentService.createShipmentForParts(partReturns);
        this.partReturnService.updatePartReturnsForClaim(claim, null);

    }

    private void populateSearchCriteria(ReportSearchCriteria reportSearchCriteria) {
        // String modelFault =
        // "claim.itemReference.unserializedItem.product.itemGroupType,claim.serviceInformation.faultCode";
        CalendarDate startDate = CalendarDate.date(2003, 4, 1);
        CalendarDate endDate = CalendarDate.date(2008, 5, 1);
        reportSearchCriteria.setStartDate(startDate);
        reportSearchCriteria.setEndDate(endDate);
        reportSearchCriteria.setOrderBy("modelFault");
        Collection<Long> dealers = new TreeSet<Long>();
        dealers.add(41L);
        Collection<Long> suppliers = new TreeSet<Long>();
        suppliers.add(31L);
        reportSearchCriteria.setSelectedSuppliers(suppliers);
        reportSearchCriteria.setSelectedDealers(dealers);
        // reportSearchCriteria.setOrderBy(modelFault);
    }

    public void setClaimReportRepository(ClaimReportRepository claimReportRepository) {
        this.claimReportRepository = claimReportRepository;
    }

    public void setClaimRepository(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

    public void setDealershipRepository(DealershipRepository dealershipRepository) {
        this.dealershipRepository = dealershipRepository;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public void setShipmentService(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    public void setPaymentSectionRepository(PaymentSectionRepository paymentSectionRepository) {
        this.paymentSectionRepository = paymentSectionRepository;
    }

}
