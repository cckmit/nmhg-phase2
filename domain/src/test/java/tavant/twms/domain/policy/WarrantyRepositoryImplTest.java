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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.inventory.InvTransationType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemRepository;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.inventory.InventoryTransactionType;
import tavant.twms.domain.inventory.TransactionType;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.OrganizationRepository;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class WarrantyRepositoryImplTest extends DomainRepositoryTestCase {
	private InventoryItemRepository inventoryItemRepository;

	private WarrantyRepository warrantyRepository;

	private PolicyDefinitionRepository policyDefinitionRepository;

	private OrganizationRepository organizationRepository;

	private WarrantyService warrantyService;

	private OrgService orgService;

	private InventoryTransactionService invTransactionService;

	private PolicyService policyService;

	private PolicyRepository policyRepository;

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	
	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}


	public void setPolicyRepository(PolicyRepository policyRepository) {
		this.policyRepository = policyRepository;
	}


	public void setInvTransactionService(
			InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	@Required
	public void setPolicyDefinitionRepository(
			PolicyDefinitionRepository policyDefinitionRepository) {
		this.policyDefinitionRepository = policyDefinitionRepository;
	}

	@Required
	public void setWarrantyRepository(WarrantyRepository warrantyRepository) {
		this.warrantyRepository = warrantyRepository;
	}

	@Required
	public void setInventoryItemRepository(
			InventoryItemRepository inventoryItemRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
	}

	@Required
	public void setOrganizationRepository(
			OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	public void testSave() {
		Warranty warranty = new Warranty();

		Customer customer = new Customer();
		warranty.setCustomer(customer);

		InventoryItem inventoryItem = this.inventoryItemRepository
				.findInventoryItem(new Long(1));
		warranty.setForItem(inventoryItem);

	}

	/*
	 * public void testFindWarranties() { Dealership thisIsNotUsed = new
	 * Dealership();
	 * 
	 * PageSpecification pageSpec = new PageSpecification();
	 * 
	 * pageSpec.setPageNumber(0); pageSpec.setPageSize(10); PageResult<Warranty>
	 * pageOfWarranties = warrantyRepository.findWarranties(thisIsNotUsed,
	 * pageSpec); List<Warranty> pageContent = pageOfWarranties.getResult();
	 * assertTrue(pageContent.size()==10); }
	 */

	public void testListTransactionTypes() {
		List<TransactionType> typeList = this.warrantyRepository
				.listTransactionTypes();

		assertFalse(typeList.isEmpty());
	}

	public void testListMarketTypes() {
		List<MarketType> typeList = this.warrantyRepository.listMarketTypes();

		assertFalse(typeList.isEmpty());
	}

	public void testListCompetetionTypes() {
		List<CompetitionType> typeList = this.warrantyRepository
				.listCompetitionTypes();

		assertFalse(typeList.isEmpty());
	}

	public void testFindWarrantyBySerialNumberAndDealer() {

		DebitMemo debitMemo = createDebitMemo();

		Dealership dealer = orgService.findDealerByNumber(debitMemo.getDealerNumber());

		InventoryTransactionType extWarrantyTransactionType = invTransactionService
				.getTransactionTypeByName(InvTransationType.EXTENED_WNTY_PURCHASE
						.getTransactionType());

		InventoryTransaction transaction = warrantyRepository
				.findWarrantyBySerialNumberAndDealer(debitMemo.getSerialNumber(), dealer,
						extWarrantyTransactionType);

		transaction.getTransactedItem();

		Warranty warranty = warrantyRepository.findBy(transaction
				.getTransactedItem());

		transaction.setInvoiceDate(debitMemo.getInvoiceDate());
		transaction.setInvoiceNumber(debitMemo.getDebitMemoNumber());

		try {
			invTransactionService.update(transaction);
		} catch (Exception e) {
			logger.error(e);
		}

		List<RegisteredPolicy> policies = policyService
				.getPoliciesForWarranty(warranty);
		Map<String, ExtWarrantyPlan> planCodeMap = createPlanCodeMap(debitMemo
				.getPlans());

		for (RegisteredPolicy registeredPolicy : policies) {
			RegisteredPolicyAudit audit = registeredPolicy
					.getLatestPolicyAudit();
			if (audit != null
					&& audit.getStatus().equals(
							RegisteredPolicyStatusType.INPROGRESS.getStatus())) {
				audit.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
				ExtWarrantyPlan plan = planCodeMap.get(registeredPolicy
						.getCode());
				registeredPolicy.setAmount(plan.getAmount());
				registeredPolicy.setTaxAmount(plan.getTaxAmount());
				policyRepository.update(registeredPolicy);
			}
		}
	}


	private DebitMemo createDebitMemo() {
		DebitMemo debitMemo = new DebitMemo();
		debitMemo.setDealerNumber("730720");
		debitMemo.setSerialNumber("1234849");
		debitMemo.setInvoiceDate(CalendarDate.date(2006, 9, 1));
		debitMemo.setDebitMemoNumber("123123123");
		
		List<ExtWarrantyPlan> plans = new ArrayList<ExtWarrantyPlan>();
		ExtWarrantyPlan extWarrantyPlan = new ExtWarrantyPlan();
		extWarrantyPlan.setPlanCode("AB08");
		extWarrantyPlan.setAmount(Money.dollars(39.38));
		extWarrantyPlan.setTaxAmount(Money.dollars(10));
		plans.add(extWarrantyPlan);
		
		debitMemo.setPlans(plans);
		return debitMemo;
	}

	private Map<String, ExtWarrantyPlan> createPlanCodeMap(
			List<ExtWarrantyPlan> plans) {
		Map<String, ExtWarrantyPlan> planCodeMap = new HashMap<String, ExtWarrantyPlan>();
		for (ExtWarrantyPlan extWarrantyPlan : plans) {
			planCodeMap.put(extWarrantyPlan.getPlanCode(), extWarrantyPlan);
		}
		return planCodeMap;
	}

	public void disable_testUpdateWarranty() {
		Warranty warranty = new Warranty();
		Customer customer = new Customer();
		customer.setName("test1");
		Address address = new Address();
		address.setAddressLine1("1234");
		AddressForTransfer addressForTransfer = new AddressForTransfer();
		addressForTransfer.setAddressLine("1234");
		customer.setAddresses(new ArrayList<Address>());
		customer.getAddresses().add(address);
		customer.setCompanyName("testCompnay");
		this.organizationRepository.save(customer);
		warranty.setCustomer(customer);
		warranty.setAddressForTransfer(addressForTransfer);
		InventoryItem inventoryItem = this.inventoryItemRepository
				.findInventoryItem(17L);
		warranty.setInventoryItem(inventoryItem);
		inventoryItem.setDeliveryDate(Clock.today());
		inventoryItem.setHoursOnMachine(12);
		List<PolicyDefinition> policyDefinitions = this.policyDefinitionRepository
				.findPoliciesForInventory(inventoryItem, Clock.today());
		CalendarDuration forPeriod = new CalendarDuration();
		forPeriod.setFromDate(Clock.today().plusDays(-30));
		forPeriod.setTillDate(Clock.today());
		for (PolicyDefinition policyDefinition : policyDefinitions) {
			this.warrantyService.register(warranty, policyDefinition,
					forPeriod, policyDefinition.getAvailability().getPrice(),
					null, null, null);
		}
		this.warrantyRepository.save(warranty);
		Set<RegisteredPolicy> policies = warranty.getPolicies();
		PolicyDefinition pdefTest = policies.iterator().next()
				.getPolicyDefinition();
		warranty.getPolicies().removeAll(policies);
		this.warrantyService.register(warranty, pdefTest, forPeriod, pdefTest
				.getAvailability().getPrice(), null, null, null);
		this.warrantyRepository.update(warranty);
		assertTrue(warranty.getPolicies().size() == 1);
	}

	public void testFindBy() {
		InventoryItem inventoryItem = this.inventoryItemRepository
				.findById(new Long(1));
		Warranty warranty = this.warrantyRepository.findBy(inventoryItem);
		assertNotNull(warranty);
		assertEquals(inventoryItem, warranty.getForItem());
	}
}
