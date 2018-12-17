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
package tavant.twms.domain.claim.payment.definition.modifiers;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupRepository;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.DurationOverlapException;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.time.CalendarDate;

/**
 * @author Kiran.Kollipara
 *
 */
public class PaymentModifiersRepositoryImplTest extends DomainRepositoryTestCase {
    private PaymentModifierRepository paymentModifierRepository;
    private ItemGroupRepository itemGroupRepository;
    private DealershipRepository dealershipRepository;
    private PaymentVariable paymentVariable;

    
    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        paymentVariable = new PaymentVariable();
        paymentVariable.setId(1L);
        paymentVariable.setName("Claim Bonus Percentage");
    }
    
    public void testFindAllPaymentVariables_BusinessUnitAware() {
    	//if you don't apply filter then this assertion will fail
		List<PaymentVariable> paymentVariableList = paymentModifierRepository
				.findAllPaymentVariables();
		assertEquals(4, paymentVariableList.size());
		for (PaymentVariable paymentVariable : paymentVariableList) {
			assertNotNull(paymentVariable.getBusinessUnitInfo());
		}
	}
    public void testSaveItem() {
        List<PaymentModifier> existingEntity = paymentModifierRepository.findAll();
        for (PaymentModifier entity : existingEntity) {
            paymentModifierRepository.delete(entity);
        }
        flushAndClear();

        Criteria forCriteria = new Criteria();
        forCriteria.setWarrantyType("Standard");
        ItemGroup productType = itemGroupRepository.findById(new Long(1));
        assertNotNull(productType);
        forCriteria.setProductType(productType);

        PaymentModifier paymentModifier = new PaymentModifier();
        paymentModifier.setForPaymentVariable(paymentVariable);
        paymentModifier.setForCriteria(forCriteria);
        CalendarDate startDate = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
        CalendarDate endDate = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
        try {
            paymentModifier.set(10.0, new CalendarDuration(startDate, endDate));
        } catch (DurationOverlapException e) {
            fail();
        }
        paymentModifierRepository.save(paymentModifier);
        flushAndClear();

        PaymentModifier modifier = paymentModifierRepository.findById(paymentModifier.getId());
        assertNotNull(modifier);
        assertEquals(10.0, modifier.getValueAsOf(startDate));
        assertEquals(10.0, modifier.getValueAsOf(endDate));
        assertNull(modifier.getValueAsOf(startDate.previousDay()));
        assertNull(modifier.getValueAsOf(endDate.nextDay()));
    }

    public void testUpdateItem() {
        PaymentModifier paymentModifier = paymentModifierRepository.findById(new Long(1));
        paymentModifier.getEntries().clear();
        CalendarDate startDate = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
        CalendarDate endDate = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
        try {
            paymentModifier.set(10.00, new CalendarDuration(startDate, endDate));
        } catch (DurationOverlapException e) {
            fail();
        }
        paymentModifierRepository.update(paymentModifier);
        flushAndClear();

        PaymentModifier example = paymentModifierRepository.findById(new Long(1));
        assertNotNull(example.getEntryAsOf(startDate));
    }

    public void findPaymentModifier_Default() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(1L));
        criteria.setWarrantyType("GOODWILL");
        criteria.setClaimType("Parts");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(1L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(1.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_ProductTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(5L));
        criteria.setWarrantyType("GOODWILL");
        criteria.setClaimType("Parts");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(2.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_WarrantyTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(1L));
        criteria.setWarrantyType("STANDARD");
        criteria.setClaimType("Parts");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(3.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_ProductTypeAndWarrantyTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(5L));
        criteria.setWarrantyType("STANDARD");
        criteria.setClaimType("Parts");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(4.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_ClaimTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(1L));
        criteria.setWarrantyType("GOODWILL");
        criteria.setClaimType("Machine");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(5.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_ClaimTypeAndProductTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(5L));
        criteria.setWarrantyType("GOODWILL");
        criteria.setClaimType("Machine");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(6.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_ClaimTypeAndWarrantyTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(1L));
        criteria.setWarrantyType("STANDARD");
        criteria.setClaimType("Machine");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(7.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_ClaimTypeWarrantyTypeAndProductTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(5L));
        criteria.setWarrantyType("STANDARD");
        criteria.setClaimType("Machine");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(8.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_DealerMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(1L));
        criteria.setWarrantyType("GOODWILL");
        criteria.setClaimType("Parts");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(9.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_DealerAndProductTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(5L));
        criteria.setWarrantyType("GOODWILL");
        criteria.setClaimType("Parts");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(10.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_DealerAndWarrantyTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(1L));
        criteria.setWarrantyType("STANDARD");
        criteria.setClaimType("Parts");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(11.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_DealerWarrantyTypeAndProductTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(5L));
        criteria.setWarrantyType("STANDARD");
        criteria.setClaimType("Parts");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(12.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_DealerAndClaimTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(1L));
        criteria.setWarrantyType("GOODWIL");
        criteria.setClaimType("Machine");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(13.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_DealerClaimTypeAndProductTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(5L));
        criteria.setWarrantyType("GOODWIL");
        criteria.setClaimType("Machine");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(14.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_DealerClaimTypeAndWarrantyTypeMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(1L));
        criteria.setWarrantyType("STANDARD");
        criteria.setClaimType("Machine");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(15.0, paymentModifier.getValue());
    }

    public void testfindPaymentModifier_AllMatch() {
        Criteria criteria = new Criteria();
        criteria.setProductType(itemGroupRepository.findById(5L));
        criteria.setWarrantyType("STANDARD");
        criteria.setClaimType("Machine");
        criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

        CriteriaBasedValue paymentModifier = paymentModifierRepository.findValue(criteria, paymentVariable,CalendarDate.date(2007, 1, 1));
        assertNotNull(paymentModifier);
        assertEquals(16.0, paymentModifier.getValue());
    }

    @Required
    public void setPaymentModifierRepository(PaymentModifierRepository paymentModifierRepository) {
        this.paymentModifierRepository = paymentModifierRepository;
    }

    @Required
    public void setDealershipRepository(DealershipRepository dealershipRepository) {
        this.dealershipRepository = dealershipRepository;
    }

    @Required
    public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
        this.itemGroupRepository = itemGroupRepository;
    }
}