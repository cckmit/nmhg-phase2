/*
 *   Copyright (c)2007 Tavant Technologies*   All Rights Reserved.
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
 *
 *
 * User: kapil.pandit
 * Date: Jan 10, 2007
 * Time: 5:28:17 PM
 */

package tavant.twms.integration.layer.component;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.XmlException;

import tavant.supplier.IRInvoiceTypeDTO;
import tavant.supplier.ProcessIriInvoiceDocumentDTO;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CreditMemo;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.PaymentComponent;
import tavant.twms.domain.claim.payment.RecoveryPayment;
import tavant.twms.domain.common.AcceptanceReason;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.external.IntegrationBridge;
import tavant.twms.integration.layer.IntegrationRepositoryTestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class ProcessSupplierDebitSubmissionTest extends
		IntegrationRepositoryTestCase {

	ProcessSupplierDebitSubmission processSupplierDebitSubmission;
	
	private IntegrationBridge integrationBridge;

	@SuppressWarnings("unused")
	public void testProcessClaim() {
		RecoveryClaim recoveryClaim = getRecoveryClaim();
		String actualBod = processSupplierDebitSubmission
				.syncSupplierDebit(recoveryClaim);
		System.out.println(actualBod);
		IRInvoiceTypeDTO dto = getIRInvoiceTypeDTO(actualBod);
		
	//	integrationBridge.sendRecoveryClaim(recoveryClaim);
	}

	private RecoveryClaim getRecoveryClaim() {
		RecoveryClaim recoveryClaim = new RecoveryClaim();
		recoveryClaim.setId(1L);
		recoveryClaim.setRecoveryPayment(createRecoveryPayment());
		Claim claim = getClaim();
		recoveryClaim.setClaim(claim);
		recoveryClaim.setContract(createContract());
		recoveryClaim.setSupplier(createSupplier());
		return recoveryClaim;
	}

	private RecoveryPayment createRecoveryPayment() {
		RecoveryPayment recoveryPayment = new RecoveryPayment();
		recoveryPayment.setId(1L);
		recoveryPayment.setTotalRecoveryAmount(Money.dollars(100));
		recoveryPayment.setPreviousPaidAmount(Money.dollars(0));
		return recoveryPayment;
	}

	private IRInvoiceTypeDTO getIRInvoiceTypeDTO(String actualBod) {
		ProcessIriInvoiceDocumentDTO dto = null;
		try {
			dto = ProcessIriInvoiceDocumentDTO.Factory
					.parse((String) actualBod);
		} catch (XmlException e) {
			e.printStackTrace();
		}

		return dto.getProcessIriInvoice().getDataArea().getIriInvoice();
	}

	private MachineClaim getClaim() {
		MachineClaim claim = new MachineClaim();

		claim.setId(Long.valueOf(1));
		claim.setClaimNumber("1");
		claim.setConditionFound("new");
		claim.setFiledOnDate(CalendarDate.date(2007, 10, 20));

		List<ClaimedItem> claimedItems = new ArrayList<ClaimedItem>();
		ClaimedItem claimedItem = new ClaimedItem();
		claimedItem
				.setItemReference(createItemReferenceForInventoryItem("SerialNumber"));
		claimedItems.add(claimedItem);
		claim.setClaimedItems(claimedItems);

		AcceptanceReason acceptanceReason = new AcceptanceReason();
		acceptanceReason.setCode("Acc Reason");
		claim.setAcceptanceReason(acceptanceReason);
		setDealerInfo(claim);
		setPaymentInfo(claim);
		claim.setServiceInformation(createServiceInformation());
		return claim;
	}

	private void setDealerInfo(MachineClaim claim) {
		Dealership dealership = new Dealership();
		dealership.setDealerNumber("some dealer");
		dealership.setName("some dealer");
		dealership.setPreferredCurrency(Currency.getInstance("USD"));
		claim.setForDealerShip(dealership);
	}

	private void setPaymentInfo(MachineClaim claim) {
		/*Set<PaymentComponent> paymentComponents = new HashSet<PaymentComponent>();
		paymentComponents.add(createPaymentComp(
				CostCategory.LABOR_COST_CATEGORY_CODE, 10));
		paymentComponents.add(createPaymentComp(
				CostCategory.NON_OEM_PARTS_COST_CATEGORY_CODE, 10));
		paymentComponents.add(createPaymentComp(
				CostCategory.OEM_PARTS_COST_CATEGORY_CODE, 10));
		paymentComponents.add(createPaymentComp(
				CostCategory.FREIGHT_DUTY_CATEGORY_CODE, 10));*/

		Payment payment = new Payment();
		/**
		 * Adding previous credit memos for iteration
		 */
		List<CreditMemo> previousCreditMemos = new ArrayList<CreditMemo>();
		previousCreditMemos.add(new CreditMemo());
		//payment.setPreviousCreditMemos(previousCreditMemos);

		//payment.setPaymentComponents(paymentComponents);
		payment.setClaimedAmount(Money.dollars(100));
		payment.setTotalAmount(Money.dollars(1000));

		claim.setPayment(payment);
	}

	/*private PaymentComponent createPaymentComp(String category, int amount) {
		PaymentComponent payComp = new PaymentComponent();
		CostCategory costCategory = new CostCategory();
		costCategory.setCode(category);
		payComp.setForCategory(costCategory);
		payComp.setClaimedAmount(Money.dollars(amount));
		return payComp;
	}*/

	private ServiceInformation createServiceInformation() {
		ServiceInformation serviceInformation = new ServiceInformation();
		serviceInformation.setCausalPart(createItem("PRTVLV3"));
		serviceInformation.setContract(createContract());
		serviceInformation.setServiceDetail(createServiceDetail());
		return serviceInformation;
	}

	private Contract createContract() {
		Contract contract = new Contract();
		contract.setSupplier(createSupplier());
		contract.setId(1L);
		return contract;
	}

	private Supplier createSupplier() {
		Supplier supplier = new Supplier();
		supplier.setId(1L);
		supplier.setSupplierNumber("8787934");
		return supplier;
	}

	private ServiceDetail createServiceDetail() {
		ServiceDetail serviceDetail = new ServiceDetail();
		serviceDetail.setOEMPartsReplaced(createOEMPartsReplaced());
		serviceDetail.setNonOEMPartsReplaced(createNonOEMPartsReplaced());
		return serviceDetail;
	}

	private List<OEMPartReplaced> createOEMPartsReplaced() {
		List<OEMPartReplaced> oemList = new ArrayList<OEMPartReplaced>();
		oemList.add(createOEMPart("OEMPart-01", 2, 10));
		oemList.add(createOEMPart("OEMPart-02", 2, 20));
		return oemList;
	}

	private OEMPartReplaced createOEMPart(String partNumber, int noOfUnits,
			int pricePerUnit) {
		OEMPartReplaced oemPartReplaced = new OEMPartReplaced();
		oemPartReplaced.setId(1L);
		oemPartReplaced.setNumberOfUnits(noOfUnits);
		oemPartReplaced.setPricePerUnit(Money.dollars(pricePerUnit));
		oemPartReplaced
				.setItemReference(createItemReferenceForItem(partNumber));
		return oemPartReplaced;
	}

	private ItemReference createItemReferenceForItem(String partNumber) {
		ItemReference itemReference = new ItemReference();
		Item item = createItem(partNumber);
		itemReference.setReferredItem(item);
		return itemReference;
	}

	private Item createItem(String partNumber) {
		Item item = new Item();
		item.setId(1L);
		item.setNumber(partNumber);
		return item;
	}

	private ItemReference createItemReferenceForInventoryItem(
			String serialNumber) {
		ItemReference itemReference = new ItemReference();
		InventoryItem inventoryItem = new InventoryItem();
		inventoryItem.setSerialNumber(serialNumber);
		Item item = new Item();
		item.setNumber("test");
		inventoryItem.setOfType(item);
		itemReference.setReferredInventoryItem(inventoryItem);
		return itemReference;
	}

	private List<NonOEMPartReplaced> createNonOEMPartsReplaced() {
		List<NonOEMPartReplaced> nonOemList = new ArrayList<NonOEMPartReplaced>();
		nonOemList.add(createNONOEMPart("NON-OEMPart-01", 2, 10));
		nonOemList.add(createNONOEMPart("NON-OEMPart-02", 2, 20));
		return nonOemList;
	}

	private NonOEMPartReplaced createNONOEMPart(String partNumber,
			int numberOfUnits, int pricePerUnit) {
		NonOEMPartReplaced nonOEMPartReplaced = new NonOEMPartReplaced();
		nonOEMPartReplaced.setNumberOfUnits(numberOfUnits);
		nonOEMPartReplaced.setPricePerUnit(Money.dollars(pricePerUnit));
		nonOEMPartReplaced.setDescription("test Desctiption");
		return nonOEMPartReplaced;
	}

	public void setProcessSupplierDebitSubmission(
			ProcessSupplierDebitSubmission processSupplierDebitSubmission) {
		this.processSupplierDebitSubmission = processSupplierDebitSubmission;
	}

	public void setIntegrationBridge(IntegrationBridge integrationBridge) {
		this.integrationBridge = integrationBridge;
	}

}
