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
package tavant.twms.domain.claim.payment;

import static tavant.twms.domain.DomainTestHelper.getOrCreateFirstClaimedItemFromClaim;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.policy.CoverageTerms;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.infra.BigDecimalFactory;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

// TODO: Need to mock the 'paymentModifierRepository' service. Right
// TODO: its more of an integration test.
public class PaymentServiceImplTest extends DomainRepositoryTestCase {
	CostCategoryRepository costCategoryRepository;

	PaymentService paymentService;

	private CatalogRepository catalogRepository;

	private DealershipRepository dealershipRepository;

	private boolean prettyPrintPaymentStatement = true;

	private ClaimService claimService;

	public void setDealershipRepository(
			DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}

	public void setCatalogRepository(CatalogRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	/**
	 * @param paymentService
	 *            the paymentService to set
	 */
	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	public void d_testCalculatePaymentForClaim()
			throws PaymentCalculationException {
		Claim theClaim = new MachineClaim();
		ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(theClaim);

		theClaim.setFiledOnDate(CalendarDate.from(2006, 10, 10));
		theClaim.setRepairDate(CalendarDate.date(2006, 1, 1));
		ItemReference itemReference = new ItemReference();
		Item equipmentItem = this.catalogRepository.findItem("D6060");
		itemReference.setReferredItem(equipmentItem);
		claimedItem.setItemReference(itemReference);

		PolicyDefinition policyPlan = new PolicyDefinition();
		CoverageTerms coverage = new CoverageTerms();
		policyPlan.setCoverageTerms(coverage);
		policyPlan.setWarrantyType(new WarrantyType("EXTENDED"));
		policyPlan.getLabels().add(
				(Label) getSession().load(Label.class, "Label1"));

		RegisteredPolicy registeredPolicy = new RegisteredPolicy();
		registeredPolicy.setPolicyDefinition(policyPlan);
		claimedItem.setApplicablePolicy(registeredPolicy);

		Dealership dealership = this.dealershipRepository.findByDealerId(7L);
		dealership.setPreferredCurrency(Currency.getInstance("INR"));
		theClaim.setForDealerShip(dealership);
		theClaim.setServiceInformation(new ServiceInformation());

		ServiceDetail serviceDetail = new ServiceDetail();
		theClaim.getServiceInformation().setServiceDetail(serviceDetail);

		TravelDetail travelDetail = new TravelDetail();
		travelDetail.setHours(new BigDecimal(3));
		travelDetail.setDistance(new BigDecimal(50));
		travelDetail.setTrips(2);
		serviceDetail.setTravelDetails(travelDetail);

		LaborDetail laborDetail = new LaborDetail();
		laborDetail.setHoursSpent(new BigDecimal(3));
		serviceDetail.getLaborPerformed().add(laborDetail);

		// The next assignment is in-sensible.. but for not its ok.
		Item partItem = this.catalogRepository.findItem("PRTVLV2");
		ItemReference itemRef = new ItemReference(partItem);
		OEMPartReplaced partReplaced = new OEMPartReplaced(itemRef, 2);
		serviceDetail.getOEMPartsReplaced().add(
				partReplaced);
		
		Payment payment = this.paymentService.calculatePaymentForClaim(theClaim);

		for (LineItemGroup lineItemGroup : payment.getLineItemGroups()) {
			lineItemGroup.setPercentageAcceptance(BigDecimalFactory
					.bigDecimalOf(50.00));
		}

		theClaim.setPayment(payment);

		payment = this.paymentService.calculatePaymentForClaim(theClaim);
		System.out.println(payment.prettyPrint());
		assertEquals(Money.dollars(1607.00), payment.getClaimedAmount());
		assertEquals(Money.dollars(542.49), payment.getTotalAmount());

		// Verify the Total Claim Section
		LineItemGroup lineItemGroup = payment
				.getLineItemGroup(Section.TOTAL_CLAIM);
		assertNotNull(lineItemGroup);
		assertEquals(Money.dollars(889.33), lineItemGroup.getGroupTotal());
		assertEquals(Money.dollars(542.49), lineItemGroup.getAcceptedTotal());
		assertEquals(1, lineItemGroup.getLineItems().size());

		LineItem li = lineItemGroup.getLineItem("Claim Bonus Percentage");
		assertNotNull(li);
		assertEquals(Money.dollars(195.65), li.getValue());

	}

	public void d_testCalculatePaymentForClaimWithParts()
			throws PaymentCalculationException {
		Claim theClaim = new MachineClaim();
		ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(theClaim);

		theClaim.setFiledOnDate(CalendarDate.from(2006, 10, 10));
		theClaim.setRepairDate(CalendarDate.date(2006, 1, 1));
		ItemReference itemReference = new ItemReference();
		Item equipmentItem = this.catalogRepository.findItem("D6060");
		itemReference.setReferredItem(equipmentItem);
		claimedItem.setItemReference(itemReference);

		PolicyDefinition policyPlan = new PolicyDefinition();
		CoverageTerms coverage = new CoverageTerms();
		policyPlan.setCoverageTerms(coverage);
		policyPlan.setWarrantyType(new WarrantyType("EXTENDED"));
		policyPlan.getLabels().add(
				(Label) getSession().load(Label.class, "Label1"));

		RegisteredPolicy registeredPolicy = new RegisteredPolicy();
		registeredPolicy.setPolicyDefinition(policyPlan);
		claimedItem.setApplicablePolicy(registeredPolicy);

		Dealership dealership = this.dealershipRepository.findByDealerId(7L);
		dealership.setPreferredCurrency(Currency.getInstance("INR"));
		theClaim.setForDealerShip(dealership);
		theClaim.setServiceInformation(new ServiceInformation());

		ServiceDetail serviceDetail = new ServiceDetail();
		Item partItem = this.catalogRepository.findItem("PRTVLV2");
		serviceDetail.getOEMPartsReplaced().add(
				new OEMPartReplaced(new ItemReference(partItem), 2));

		theClaim.getServiceInformation().setServiceDetail(serviceDetail);

		TravelDetail travelDetail = new TravelDetail();
		travelDetail.setHours(new BigDecimal(3));
		travelDetail.setDistance(new BigDecimal(50));
		travelDetail.setTrips(2);
		serviceDetail.setTravelDetails(travelDetail);

		LaborDetail laborDetail = new LaborDetail();
		laborDetail.setHoursSpent(new BigDecimal(3));
		serviceDetail.getLaborPerformed().add(laborDetail);

		Payment payment = this.paymentService.calculatePaymentForClaim(theClaim);

//		assertEquals(Money.dollars(2169.97), payment.getTotalAmount());
//		assertEquals(Money.dollars(1607.00), payment.getClaimedAmount());

		for (LineItemGroup lineItemGroup : payment.getLineItemGroups()) {
			lineItemGroup.setPercentageAcceptance(BigDecimalFactory
					.bigDecimalOf(50.00));
		}

		theClaim.setPayment(payment);

		payment = this.paymentService.calculatePaymentForClaim(theClaim);
		System.out.println(payment.prettyPrint());
//		assertEquals(Money.dollars(542.49), payment.getTotalAmount());
//		assertEquals(Money.dollars(1607.00), payment.getClaimedAmount());

		// Verify the Total Claim Section
		LineItemGroup lineItemGroup = payment
				.getLineItemGroup(Section.TOTAL_CLAIM);
//		assertNotNull(lineItemGroup);
//		assertEquals(Money.dollars(889.33), lineItemGroup.getGroupTotal());
//		assertEquals(Money.dollars(542.49), lineItemGroup.getAcceptedTotal());
//		assertEquals(1, lineItemGroup.getLineItems().size());

		LineItem li = lineItemGroup.getLineItem("Claim Bonus Percentage");
//		assertNotNull(li);
//		assertEquals(Money.dollars(195.65), li.getValue());
		/*
		 * TODO://This should have been Club Car Parts but there is some hard
		 * coding, need to find and fix
		 */
		LineItemGroup oemPartGrp = payment.getLineItemGroup("Club Car Parts");
//		assertNotNull(oemPartGrp);
//		assertTrue(oemPartGrp.getCurrentPartPaymentInfo().size() == 1);
		List<PartPaymentInfo> currentPartPaymentInfo = oemPartGrp
				.getCurrentPartPaymentInfo();
		PartPaymentInfo partPaymentInfo = currentPartPaymentInfo.get(0);
//		assertEquals("PRTVLV2", partPaymentInfo.getPartNumber());

		//this.paymentService.reopenClaimPayment(theClaim);

		oemPartGrp = payment.getLineItemGroup("Club Car Parts");
//		assertNotNull(oemPartGrp);
//		assertTrue(oemPartGrp.getCurrentPartPaymentInfo().size() == 0);
//		assertTrue(oemPartGrp.getPreviousPartPaymentInfo().size() == 1);
		//currentPartPaymentInfo = oemPartGrp
				//.getPreviousPartPaymentInfo();
		partPaymentInfo = currentPartPaymentInfo.get(0);
//		assertEquals("PRTVLV2", partPaymentInfo.getPartNumber());
		
		payment = this.paymentService.calculatePaymentForClaim(theClaim);
		System.out.println(payment.prettyPrint());
	}

	public void testPaymentWithCreditMemo() throws Exception {
		Claim theClaim = new MachineClaim();
		ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(theClaim);

		theClaim.setFiledOnDate(CalendarDate.from(2006, 10, 10));
		theClaim.setRepairDate(CalendarDate.date(2006, 1, 1));
		ItemReference itemReference = new ItemReference();
		Item equipmentItem = this.catalogRepository.findItem("D6060");
		itemReference.setReferredItem(equipmentItem);
		claimedItem.setItemReference(itemReference);

		PolicyDefinition policyPlan = new PolicyDefinition();
		CoverageTerms coverage = new CoverageTerms();
		policyPlan.setCoverageTerms(coverage);
		policyPlan.setWarrantyType(new WarrantyType("EXTENDED"));
		policyPlan.getLabels().add(
				(Label) getSession().load(Label.class, "Label1"));

		RegisteredPolicy registeredPolicy = new RegisteredPolicy();
		registeredPolicy.setPolicyDefinition(policyPlan);
		// theClaim.setApplicablePolicy(registeredPolicy);

		Dealership dealership = this.dealershipRepository.findByDealerId(7L);
		dealership.setPreferredCurrency(Currency.getInstance("INR"));
		theClaim.setForDealerShip(dealership);
		theClaim.setServiceInformation(new ServiceInformation());

		ServiceDetail serviceDetail = new ServiceDetail();
		theClaim.getServiceInformation().setServiceDetail(serviceDetail);

		TravelDetail travelDetail = new TravelDetail();
		travelDetail.setHours(new BigDecimal(3));
		travelDetail.setDistance(new BigDecimal(50));
		travelDetail.setTrips(2);
		serviceDetail.setTravelDetails(travelDetail);

		LaborDetail laborDetail = new LaborDetail();
		laborDetail.setHoursSpent(new BigDecimal(3));
		serviceDetail.getLaborPerformed().add(laborDetail);

		// The next assignment is in-sensible.. but for not its ok.
		Item partItem = this.catalogRepository.findItem("PRTVLV2");
		OEMPartReplaced part= new OEMPartReplaced(new ItemReference(partItem), 2);
		part.setInventoryLevel(new Boolean(false));
		serviceDetail.getOEMPartsReplaced().add(part);
		Payment payment = this.paymentService.calculatePaymentForClaim(theClaim);

		for (LineItemGroup lineItemGroup : payment.getLineItemGroups()) {
			lineItemGroup.setPercentageAcceptance(BigDecimalFactory
					.bigDecimalOf(50.00));
		}
		theClaim.setPayment(payment);
		this.claimService.createClaim(theClaim);
		// flushAndClear();
		// System.out.println(theClaim);
		CreditMemo activeCreditMemo = new CreditMemo();
		activeCreditMemo.setClaimNumber(theClaim.getId().toString());
		activeCreditMemo.setCreditMemoDate(Clock.today());
		activeCreditMemo.setCreditMemoNumber("12435");
		activeCreditMemo.setTaxAmount(Money.dollars(10));
		theClaim.getPayment().addActiveCreditMemo(activeCreditMemo);
		this.claimService.updateClaim(theClaim);
		flushAndClear();
		theClaim = this.claimService.findClaim(theClaim.getId());
		assertEquals(Money.dollars(10), theClaim.getPayment()
				.getActiveCreditMemo().getTaxAmount());
		//List<CreditMemo> creditMemos = theClaim.getPayment()
				//.getPreviousCreditMemos();
		// System.out.println(creditMemos);
		//assertEquals(0, creditMemos.size());
		activeCreditMemo = new CreditMemo();
		activeCreditMemo.setClaimNumber(theClaim.getId().toString());
		activeCreditMemo.setCreditMemoDate(Clock.today());
		activeCreditMemo.setCreditMemoNumber("34521");
		activeCreditMemo.setTaxAmount(Money.dollars(30));
		theClaim.getPayment().addActiveCreditMemo(activeCreditMemo);
		this.claimService.updateClaim(theClaim);
		flushAndClear();
		theClaim = this.claimService.findClaim(theClaim.getId());
		//creditMemos = theClaim.getPayment().getPreviousCreditMemos();
		// System.out.println(creditMemos);
		assertEquals(Money.dollars(30), theClaim.getPayment()
				.getActiveCreditMemo().getTaxAmount());
		//assertEquals(1, creditMemos.size());
		//assertEquals(Money.dollars(10), creditMemos.get(0).getTaxAmount());
	}

	public void setCostCategoryRepository(
			CostCategoryRepository costCategoryRepository) {
		this.costCategoryRepository = costCategoryRepository;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

}
