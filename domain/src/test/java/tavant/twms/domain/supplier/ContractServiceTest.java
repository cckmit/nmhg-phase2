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
package tavant.twms.domain.supplier;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimRepository;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * 
 * @author kannan.ekanath
 * 
 */
public class ContractServiceTest extends DomainRepositoryTestCase {

    ContractService contractService;

    ClaimRepository claimRepository;

    public void testFindContracts() {
        Claim claim = new MachineClaim();
        CalendarDate builtOnDate = CalendarDate.date(2007, 5, 10);
        ClaimedItem claimedItem = getClaimedItemBuiltOn(builtOnDate);
        claim.addClaimedItem(claimedItem);
        claim.setRepairDate(CalendarDate.date(2007, 5, 12));
        OEMPartReplaced partReplaced = new OEMPartReplaced();
        partReplaced.setItemReference(new ItemReference(new Item()));
        Item item = (Item) getSession().load(Item.class, new Long(1));
        List<Contract> contracts = this.contractService.findContracts(claim, partReplaced, item, true);
        assertEquals(2, contracts.size());
        assertEquals(new Long(1), contracts.get(0).getId());
        assertEquals(new Long(2), contracts.get(1).getId());

        item = (Item) getSession().load(Item.class, new Long(4));
        assertTrue(this.contractService.findContracts(claim, partReplaced, item, true).isEmpty());
    }

    private ClaimedItem getClaimedItemBuiltOn(CalendarDate builtOnDate) {
        ClaimedItem claimedItem = new ClaimedItem();
        ItemReference itemRef = new ItemReference();
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setType(InventoryType.RETAIL);
        inventoryItem.setBuiltOn(builtOnDate);
        itemRef.setReferredInventoryItem(inventoryItem);
        claimedItem.setItemReference(itemRef);
        return claimedItem;
    }

    public void testUpdateSupplier() {

        Item item = (Item) getSession().load(Item.class, new Long(1));
        OEMPartReplaced partReplaced = new OEMPartReplaced();
        partReplaced.setItemReference(new ItemReference(item));
        partReplaced.setInventoryLevel(new Boolean(false));

        Contract contract = (Contract) getSession().load(Contract.class, new Long(2));

        Claim claim = new MachineClaim();
        RecoveryClaim recoveryClaim = new RecoveryClaim();
        claim.addRecoveryClaim(recoveryClaim);
        recoveryClaim.setId(claim.getId());
        claim.setFailureDate(CalendarDate.date(2006, 1, 1));
        claim.setRepairDate(CalendarDate.date(2006, 1, 1));
        ServiceInformation serviceInformation = new ServiceInformation();
        ServiceDetail serviceDetail = new ServiceDetail();
        serviceDetail.getOEMPartsReplaced().add(partReplaced);
        serviceInformation.setServiceDetail(serviceDetail);
        claim.setServiceInformation(serviceInformation);
        CalendarDate installationDate = Clock.today();
        claim.setInstallationDate(installationDate);
        // claim.addClaimedItem(getClaimedItemBuiltOn(Clock.today()));

        // Save the claim
        flushAndClear();
        this.claimRepository.save(claim);

        this.contractService.updateContract(claim, partReplaced, contract);

        // assertEquals(claim.getRecoveryClaim().getSupplier(),
        // contract.getSupplier());

        // assertEquals(claim.getRecoveryClaim().getContract().getId(), new
        // Long(2));

        item = (Item) getSession().load(Item.class, new Long(8));
        partReplaced.setItemReference(new ItemReference(item));
        try {
            this.contractService.updateContract(claim, partReplaced, contract);
            // fail("dint threw exception");
        } catch (HibernateException e) {
            // pass
        }
    }

    public void testContractServiceUpdateClaim() {
        Claim claim = new MachineClaim();
        // setting default as zero as payment API is tested in Contract Service
        // Unit test
        Payment payment = new Payment();
        claim.setPayment(payment);
        payment.setTotalAmount(Money.dollars(0));
        ServiceInformation serviceInformation = new ServiceInformation();
        ServiceDetail serviceDetail = new ServiceDetail();
        // Add one part with supplier part return configured
        Item oemCataloguedItemSuppliedByVendors = (Item) getSession().load(Item.class, new Long(1));
        OEMPartReplaced partReplaced1 = new OEMPartReplaced();
        partReplaced1.setItemReference(new ItemReference(oemCataloguedItemSuppliedByVendors));
        List<PartReturn> partReturns = new ArrayList<PartReturn>();
        partReturns.add(new PartReturn());
        partReplaced1.setPartReturns(partReturns);

        // Add one with no config
        Item oemCatalogedItemSuppliedByOEM = (Item) getSession().load(Item.class, new Long(16));
        OEMPartReplaced partReplaced2 = new OEMPartReplaced();
        partReplaced2.setItemReference(new ItemReference(oemCatalogedItemSuppliedByOEM));
        partReturns = new ArrayList<PartReturn>();
        partReturns.add(new PartReturn());
        partReplaced2.setPartReturns(partReturns);
        serviceDetail.getOEMPartsReplaced().add(partReplaced1);
        serviceDetail.getOEMPartsReplaced().add(partReplaced2);
        serviceInformation.setServiceDetail(serviceDetail);
        serviceInformation.setCausalPart(oemCataloguedItemSuppliedByVendors);
        RecoveryClaim recoveryClaim = new RecoveryClaim();
        recoveryClaim.setClaim(claim);
        claim.addRecoveryClaim(recoveryClaim);
        claim.setServiceInformation(serviceInformation);

        this.contractService.updateSupplierRecovery(recoveryClaim);
        assertNull(partReplaced1.getSupplierPartReturn());
        assertNull(partReplaced2.getSupplierPartReturn());
    }

    public void testContractServiceCheckPartReturn() {
        // Set the part return to be null. For now, if the part
        // return is null then there is no supplier part return
        Item item = (Item) getSession().load(Item.class, new Long(1));
        OEMPartReplaced partReplaced = new OEMPartReplaced();
        partReplaced.setItemReference(new ItemReference(item));

        Claim claim = new MachineClaim();
        // setting default as zero as payment API is tested in Contract Service
        // Unit test
        Payment payment = new Payment();
        claim.setPayment(payment);
        payment.setTotalAmount(Money.dollars(0));
        ServiceInformation serviceInformation = new ServiceInformation();
        ServiceDetail serviceDetail = new ServiceDetail();
        serviceDetail.getOEMPartsReplaced().add(partReplaced);
        serviceInformation.setServiceDetail(serviceDetail);
        serviceInformation.setCausalPart(item);
        claim.setServiceInformation(serviceInformation);
        RecoveryClaim recoveryClaim = new RecoveryClaim();
        claim.addRecoveryClaim(recoveryClaim);
        recoveryClaim.setClaim(claim);
        // Item number 1 is configured to have a supplier part return
        this.contractService.updateSupplierRecovery(recoveryClaim);
        // Supplier part return is updated even if there is no due part return
        // this is the case with direct supplier recovery
        assertNull(partReplaced.getSupplierPartReturn());
    }

    public void testContractServiceWithUniqueSupplier() {
        Item item = (Item) getSession().load(Item.class, new Long(11));
        OEMPartReplaced partReplaced = new OEMPartReplaced();
        partReplaced.setItemReference(new ItemReference(item));
        partReplaced.setNumberOfUnits(2);
        partReplaced.setPricePerUnit(Money.dollars(4));
        partReplaced.setInventoryLevel(new Boolean(false));

        SupplierPartReturn supplierPartReturn = new SupplierPartReturn();
        partReplaced.setSupplierPartReturn(supplierPartReturn);
        supplierPartReturn.setOemPartReplaced(partReplaced);

        Claim claim = new MachineClaim();
        RecoveryClaim recoveryClaim = new RecoveryClaim();
        recoveryClaim.setClaim(claim);
        claim.addRecoveryClaim(recoveryClaim);
        claim.setFailureDate(CalendarDate.date(2006, 1, 1));
        claim.setRepairDate(Clock.today());
        ServiceInformation serviceInformation = new ServiceInformation();
        ServiceDetail serviceDetail = new ServiceDetail();
        serviceDetail.getOEMPartsReplaced().add(partReplaced);
        serviceInformation.setServiceDetail(serviceDetail);
        claim.setServiceInformation(serviceInformation);

        // Save the claim
        this.claimRepository.save(claim);
        claim.setPayment(new Payment());
        // getContract
        Contract contract = (Contract) getSession().load(Contract.class, new Long(1));

        assertTrue(this.contractService.updateContractOnParts(claim, partReplaced, contract));
        // contract will also be chosen
        assertNull(claim.getServiceInformation().getContract());

    }

    public void setContractService(ContractService contractService) {
        this.contractService = contractService;
    }

    public void setClaimRepository(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

}
