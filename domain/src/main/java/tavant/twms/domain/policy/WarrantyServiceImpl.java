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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;
import org.springframework.expression.ParseException;
import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.inventory.*;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.WarrantyTask.WarrantyTaskInstance;
import tavant.twms.domain.WarrantyTask.WarrantyTaskInstanceService;
import tavant.twms.infra.BaseDomain;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 * 
 */
public class WarrantyServiceImpl implements WarrantyService {

	private final Logger logger = Logger.getLogger(WarrantyServiceImpl.class
			.getName());

	private WarrantyRepository warrantyRepository;

	OrgService orgService;

	InventoryTransactionService invTransactionService;

	PolicyService policyService;

	private SecurityHelper securityHelper;

	PolicyRepository policyRepository;

	private WarrantyTaskInstanceService warrantyTaskInstanceService;

	private InventoryService inventoryService;

	private WarrantyCoverageRequestService warrantyCoverageRequestService;

	private ExtnWntyNotificationRepository extnWntyNotificationRepository;
	
	private LovRepository lovRepository;

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void setInvTransactionService(
			InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
	}

	public void setPolicyRepository(PolicyRepository policyRepository) {
		this.policyRepository = policyRepository;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public WarrantyRepository getWarrantyRepository() {
		return this.warrantyRepository;
	}

	public void setWarrantyRepository(WarrantyRepository warrantyRepository) {
		this.warrantyRepository = warrantyRepository;
	}

	public void setExtnWntyNotificationRepository(
			ExtnWntyNotificationRepository extnWntyNotificationRepository) {
		this.extnWntyNotificationRepository = extnWntyNotificationRepository;
	}

	public void delete(Warranty warranty) {
		this.warrantyRepository.delete(warranty);
	}

	public Warranty findById(Long id) {
		return this.warrantyRepository.findById(id);
	}

	public PageResult<Warranty> findWarranties(ServiceProvider forDealer,
			PageSpecification pageSpecification) {
		return this.warrantyRepository.findWarranties(forDealer,
				pageSpecification);
	}

	public PageResult<Warranty> listMatchingWarrantiesForDealer(
			WarrantyListCriteria warrantyListCriteria) {
		return this.warrantyRepository
				.listMatchingWarrantiesForDealer(warrantyListCriteria);
	}

	public Warranty findWarranty(InventoryItem inventoryItem) {
		return this.warrantyRepository.findBy(inventoryItem);
	}

	public void save(Warranty newWaranty) {
		this.warrantyRepository.save(newWaranty);
	}

	public void update(Warranty warranty) {
		this.warrantyRepository.update(warranty);
	}

	public List<TransactionType> listTransactionTypes() {
		return this.warrantyRepository.listTransactionTypes();
	}

	public List<MarketType> listMarketTypes() {
		return this.warrantyRepository.listMarketTypes();
	}

	public List<CompetitorMake> listCompetitorMake() {
		return this.warrantyRepository.listCompetitorMake();
	}

	public List<CompetitorModel> listCompetitorModel() {
		return this.warrantyRepository.listCompetitorModel();
	}

	public PageResult<Warranty> listDraftWarrantiesForDealer(
			InventoryListCriteria inventoryListCriteria) {
		return this.warrantyRepository
				.listDraftWarrantiesForDealer(inventoryListCriteria);
	}

	public PageResult<Warranty> listDraftWarrantiesForInternalUser(
			InventoryListCriteria inventoryListCriteria) {
		return this.warrantyRepository
				.listDraftWarrantiesForInternalUser(inventoryListCriteria);
	}

	public List<CompetitionType> listCompetitionTypes() {
		return this.warrantyRepository.listCompetitionTypes();
	}

	public List<WarrantyType> listWarrantyTypes() {
		return this.warrantyRepository.listWarrantyTypes();
	}

	public Warranty findByTransactionId(final Long invTrnxId) {
		return this.warrantyRepository.findByTransactionId(invTrnxId);
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
	
	public List<IndustryCode> listIndustryCode() {
		return this.warrantyRepository.listIndustryCode();
	}
	
	
	public List<MaintenanceContract> listMaintenanceContract() {
		return this.warrantyRepository.listMaintenanceContract();
	}

	public List<ContractCode> listContractCode() {
		return this.warrantyRepository.listContractCode();
	}
	
	public List<InternalInstallType> listInternalInstallType() {
		return this.warrantyRepository.listInternalInstallType();
	}

	public RegisteredPolicy register(Warranty warranty,
			PolicyDefinition policyDefinition, CalendarDuration forPeriod,
			Money price, String registrationComments,
			String purchaseOrderNumber, CalendarDate purchaseDate) {

		RegisteredPolicy registeredPolicy = new RegisteredPolicy();
		registeredPolicy.setPolicyDefinition(policyDefinition);
		registeredPolicy.setWarrantyPeriod(forPeriod);
		registeredPolicy.setPrice(price);
		registeredPolicy.setWarranty(warranty);
		registeredPolicy.setPurchaseOrderNumber(purchaseOrderNumber);
		registeredPolicy.setPurchaseDate(purchaseDate);

		RegisteredPolicyAudit audit = registeredPolicy.getLatestPolicyAudit();
		audit.setWarrantyPeriod(forPeriod);
		audit.setServiceHoursCovered(policyDefinition.getCoverageTerms()
				.getServiceHoursCovered());
		// Here the check on comments is not required.
		// case 1. CustomerWarrantyRegistration we pass the parameter as null
		// case 2. ModifyTransferReport, here if modifyDeleteComments is null,
		// then registration comments are also null
		// case3. Here we need to populated the registration comments as we
		// capture from ui
		audit.setComments(registrationComments);

		audit.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
		registeredPolicy.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
		/**
		 * Logic for reduced warranty coverage. We create a coverage with SD/ED-
		 * Delivery Date with end date with status as INACTIVE
		 */
		if (audit.getWarrantyPeriod() != null
				&& audit.getWarrantyPeriod().getFromDate() != null
				&& audit.getWarrantyPeriod().getTillDate() != null
				&& audit.getWarrantyPeriod().getFromDate()
						.equals(audit.getWarrantyPeriod().getTillDate())) {
			audit.setStatus(RegisteredPolicyStatusType.INACTIVE.getStatus());
			registeredPolicy.setStatus(RegisteredPolicyStatusType.INACTIVE.getStatus());
		}
		audit.setCreatedBy(this.securityHelper.getLoggedInUser());
		Clock.setDefaultTimeZone(TimeZone.getDefault());
		audit.setCreatedOn(Clock.now());

		warranty.getPolicies().add(registeredPolicy);
		return registeredPolicy;
	}

	public RegisteredPolicy createPolicyInProgress(Warranty warranty,
			PolicyDefinition policyDefinition, CalendarDuration forPeriod,
			Money price, String registrationComments, String purchaseOrderNumber) {
		RegisteredPolicy registeredPolicy = new RegisteredPolicy();
		registeredPolicy.setPolicyDefinition(policyDefinition);
		registeredPolicy.setWarrantyPeriod(forPeriod);
		registeredPolicy.setPrice(price);
		registeredPolicy.setWarranty(warranty);
		registeredPolicy.setPurchaseOrderNumber(purchaseOrderNumber);

		RegisteredPolicyAudit audit = registeredPolicy.getLatestPolicyAudit();
		audit.setWarrantyPeriod(forPeriod);
		audit.setServiceHoursCovered(policyDefinition.getCoverageTerms()
				.getServiceHoursCovered());
		if (registrationComments != null) {
			audit.setComments(registrationComments);
		}
		audit.setStatus(RegisteredPolicyStatusType.INPROGRESS.getStatus());
		registeredPolicy.setStatus(RegisteredPolicyStatusType.INPROGRESS.getStatus()) ;
		/**
		 * Logic for reduced warranty coverage. We create a coverage with SD/ED-
		 * Delivery Date with end date with status as INACTIVE
		 */
		if (audit.getWarrantyPeriod() != null
				&& audit.getWarrantyPeriod().getFromDate() != null
				&& audit.getWarrantyPeriod().getTillDate() != null
				&& audit.getWarrantyPeriod().getFromDate()
						.equals(audit.getWarrantyPeriod().getTillDate())) {
			audit.setStatus(RegisteredPolicyStatusType.INACTIVE.getStatus());
			registeredPolicy.setStatus(RegisteredPolicyStatusType.INACTIVE.getStatus()) ;
		}
		audit.setCreatedBy(this.securityHelper.getLoggedInUser());
		Clock.setDefaultTimeZone(TimeZone.getDefault());
		audit.setCreatedOn(Clock.now());

		warranty.getPolicies().add(registeredPolicy);
		return registeredPolicy;
	}

	public void notifyDebitForExtWarranty(DebitMemo debitMemo) {

		ServiceProvider dealer = orgService.findDealerByNumber(debitMemo
				.getDealerNumber());

		InventoryTransactionType extWarrantyTransactionType = invTransactionService
				.getTransactionTypeByName(InvTransationType.EXTENED_WNTY_PURCHASE
						.getTransactionType());

		InventoryTransaction transaction = warrantyRepository
				.findWarrantyBySerialNumberAndDealer(
						debitMemo.getSerialNumber(), dealer,
						extWarrantyTransactionType);

		if (transaction == null) {
			throw new RuntimeException(
					" There is no transaction associated with this invoice.");
		}
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
				registeredPolicy.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
				ExtWarrantyPlan plan = planCodeMap.get(registeredPolicy
						.getCode());
				registeredPolicy.setAmount(plan.getAmount());
				registeredPolicy.setTaxAmount(plan.getTaxAmount());
				policyRepository.update(registeredPolicy);
			}
		}
	}

	private Map<String, ExtWarrantyPlan> createPlanCodeMap(
			List<ExtWarrantyPlan> plans) {
		Map<String, ExtWarrantyPlan> planCodeMap = new HashMap<String, ExtWarrantyPlan>();
		for (ExtWarrantyPlan extWarrantyPlan : plans) {
			planCodeMap.put(extWarrantyPlan.getPlanCode(), extWarrantyPlan);
		}
		return planCodeMap;
	}

	public RegisteredPolicy createRegisteredPolicyForAdmin(Warranty warranty,
			RegisteredPolicy registeredPolicyInput) {
		RegisteredPolicy registeredPolicy = new RegisteredPolicy();
		registeredPolicy.setPolicyDefinition(registeredPolicyInput
				.getPolicyDefinition());
		registeredPolicy.setPrice(registeredPolicyInput.getPolicyDefinition()
				.getAvailability().getPrice());
		registeredPolicy.setWarranty(warranty);
		registeredPolicy.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
		registeredPolicy.getPolicyAudits().add(
				registeredPolicyInput.getLatestPolicyAudit());
		createRegisteredPolicyAudit(registeredPolicy,
				RegisteredPolicyStatusType.ACTIVE.getStatus());
		warranty.getPolicies().add(registeredPolicy);
		return registeredPolicy;
	}

	private void createRegisteredPolicyAudit(RegisteredPolicy registeredPolicy,
			String status) {
		RegisteredPolicyAudit audit = registeredPolicy.getLatestPolicyAudit();
		audit.setStatus(status);
		/**
		 * Logic for reduced warranty coverage. We create a coverage with SD/ED- Delivery Date with end date
		 * with status as INACTIVE
		 */
		if(audit.getWarrantyPeriod() != null && audit.getWarrantyPeriod().getFromDate() != null && 
				audit.getWarrantyPeriod().getTillDate() != null ){
			if(audit.getWarrantyPeriod().getFromDate().equals(audit.getWarrantyPeriod().getTillDate())){
			audit.setStatus(RegisteredPolicyStatusType.INACTIVE.getStatus());
			registeredPolicy.setStatus(RegisteredPolicyStatusType.INACTIVE.getStatus());
		}
			else if (audit.getStatus().equalsIgnoreCase(RegisteredPolicyStatusType.INACTIVE.getStatus()) 
				&& !(audit.getWarrantyPeriod().getFromDate().equals(audit.getWarrantyPeriod().getTillDate()))){
				audit.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
				registeredPolicy.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
				}
			}
		
				
		if (audit.getServiceHoursCovered() == null) {
			audit.setServiceHoursCovered(registeredPolicy.getPolicyDefinition()
					.getCoverageTerms().getServiceHoursCovered());
		}
		audit.setCreatedBy(this.securityHelper.getLoggedInUser());
		Clock.setDefaultTimeZone(TimeZone.getDefault());
		audit.setCreatedOn(Clock.now());
	}
	
	public RegisteredPolicy activateRegisteredPolicyForAdmin(Warranty warranty,
			RegisteredPolicy registeredPolicy) {
		createRegisteredPolicyAudit(registeredPolicy,
				RegisteredPolicyStatusType.ACTIVE.getStatus());
		warranty.getPolicies().add(registeredPolicy);
		return registeredPolicy;
	}

	public RegisteredPolicy terminateRegisteredPolicyForAdmin(
			Warranty warranty, RegisteredPolicy registeredPolicy) {
		createRegisteredPolicyAudit(registeredPolicy,
				RegisteredPolicyStatusType.TERMINATED.getStatus());
		warranty.getPolicies().add(registeredPolicy);
		return registeredPolicy;
	}

	public RegisteredPolicy updateRegisteredPolicyForAdmin(Warranty warranty,
			RegisteredPolicy registeredPolicy, String status) {
		createRegisteredPolicyAudit(registeredPolicy, status);
		warranty.getPolicies().add(registeredPolicy);
		return registeredPolicy;
	}

	public RegisteredPolicy applyGoodWillPolicyForAdmin(Warranty warranty,
			RegisteredPolicy registeredPolicy) {
		RegisteredPolicyAudit audit = registeredPolicy.getLatestPolicyAudit();
		audit.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
		audit.setCreatedBy(this.securityHelper.getLoggedInUser());
		Clock.setDefaultTimeZone(TimeZone.getDefault());
		audit.setCreatedOn(Clock.now());
		registeredPolicy.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
		warranty.getPolicies().add(registeredPolicy);
		return registeredPolicy;
	}

	public void processWarrantyTransitionAdmin(List<InventoryItem> forItems)
			throws PolicyException {
		for (InventoryItem forItem : forItems) {
			Warranty warranty = forItem.getLatestWarranty();
			
			if(WarrantyStatus.REJECTED == forItems.get(0).getLatestWarranty().getStatus()){
            	revertPoliciesForWarrantyIfRejcted(warranty);
            }else{
            	createPoliciesForWarranty(warranty);
            }
			

			if (WarrantyStatus.ACCEPTED == warranty.getStatus()) {
				activatePolicies(warranty);
				if(!warranty.getTransactionType().getTrnxTypeKey().equalsIgnoreCase("ETR"))
					updateInventoryForWarrantyDates(warranty.getForItem());
				createInventoryTransaction(warranty);
				if (!warranty.getForTransaction().getInvTransactionType()
						.getTrnxTypeKey().equalsIgnoreCase("ETR")) {
					if (warrantyCoverageRequestService
							.hasPoliciesWithReducedCoverage(warranty)) {
						WarrantyCoverageRequest wcr = warrantyCoverageRequestService
								.storeReducedCoverageInformationForInventory(forItem);
					}
				}
			}
			if (WarrantyStatus.ACCEPTED == forItems.get(0).getLatestWarranty()
					.getStatus()
					|| WarrantyStatus.DELETED == forItems.get(0)
							.getLatestWarranty().getStatus()) {
				warranty.getForItem().setPendingWarranty(false);
			}
			
			/*if(WarrantyStatus.REJECTED == forItems.get(0).getLatestWarranty().getStatus()){
            	warranty.getForItem().setPendingWarranty(false);
            }*/
			
			if (WarrantyStatus.REJECTED == warranty.getStatus()
					|| WarrantyStatus.DELETED == warranty.getStatus()) {
				InventoryTransaction latestTransction = forItem
						.getLatestTransaction();
				forItem.setHoursOnMachine(latestTransction.getHoursOnMachine() == null ? new Long(
						0) : latestTransction.getHoursOnMachine());
			}
			if (InvTransationType.DR.getTransactionType().equals(
					warranty.getTransactionType().getTrnxTypeValue()) || InvTransationType.DEMO.getTransactionType().equals(   //SLMSPROD-1174, demo should work in the same way as that of End customer
							warranty.getTransactionType().getTrnxTypeValue()) )
				updateInventoryForWarrantyDates(warranty.getForItem());
			inventoryService.updateInventoryItem(warranty.getForItem());
		}

		if (WarrantyStatus.ACCEPTED == forItems.get(0).getLatestWarranty()
				.getStatus()) {
			closeOpenTasksWithStatusUpdate(forItems.get(0).getLatestWarranty()
					.getMultiDRETRNumber(), WarrantyStatus.ACCEPTED);
		} else if (WarrantyStatus.DELETED == forItems.get(0)
				.getLatestWarranty().getStatus()) {
			createWarrantyTaskInstance(forItems, forItems.get(0)
					.getLatestWarranty().getStatus());
		} else if (WarrantyStatus.SUBMITTED != forItems.get(0)
				.getLatestWarranty().getStatus()) {
			createWarrantyTaskInstance(forItems, forItems.get(0)
					.getLatestWarranty().getStatus());
		}

	}
	
	 protected void revertPoliciesForWarrantyIfRejcted(Warranty warranty) throws PolicyException{
	        if (warranty.getLatestAudit() != null) {        	
	    		Warranty latestWarranty = null; 
	    		
	    		boolean isETRWarranty = InvTransationType.ETR.getTransactionType()
					.equals(warranty.getTransactionType().getTrnxTypeValue());
	    		if(isETRWarranty){
	    			latestWarranty = getLatestAcceptedWarranty(warranty.getForItem(), warranty);
	    		   	
	    		//latestWarranty.getForItem().setPendingWarranty(false);
	           for (RegisteredPolicy selectedRegisteredPolicy : latestWarranty.getPolicies()) {
	               RegisteredPolicy registeredPolicy = new RegisteredPolicy();                                
	               PolicyDefinition definition = selectedRegisteredPolicy.getPolicyDefinition();
	               registeredPolicy.setPolicyDefinition(definition);
	                
	               Integer hoursCovered = null;
	               if(isETRWarranty)
	              	registeredPolicy.setWarrantyPeriod(selectedRegisteredPolicy.getWarrantyPeriod());
	   					hoursCovered=selectedRegisteredPolicy.getLatestPolicyAudit().getServiceHoursCovered();
	   				
	               if(!isETRWarranty){
	               	registeredPolicy.setWarrantyPeriod(definition.warrantyPeriodFor(warranty.getForItem()));                	
	               }
	               //The policy audit is created when the warranty period is set.
	               //All operations on audit show follow this.
	               RegisteredPolicyAudit audit = registeredPolicy.getLatestPolicyAudit();
	               if(isETRWarranty){
	               	audit.setServiceHoursCovered(hoursCovered);                	
	               }else{
	               	audit.setServiceHoursCovered(definition.getCoverageTerms().getServiceHoursCovered());                	
	               }                
	               registeredPolicy.setPrice(selectedRegisteredPolicy.getPrice());
	               registeredPolicy.setWarranty(warranty);
	               registeredPolicy.setPurchaseOrderNumber(null);
	               registeredPolicy.setWarranty(warranty);                
	               audit.setWarrantyPeriod(registeredPolicy.getWarrantyPeriod());                
	               audit.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
	               registeredPolicy.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
	               /**
	       		 * Logic for reduced warranty coverage. We create a coverage with SD/ED- Delivery Date with end date
	       		 * with status as INACTIVE
	       		 */
	               if(audit.getWarrantyPeriod() != null && audit.getWarrantyPeriod().getFromDate() != null && 
	       				audit.getWarrantyPeriod().getTillDate() != null && 
	       				audit.getWarrantyPeriod().getFromDate().equals(audit.getWarrantyPeriod().getTillDate()))
	       		{
	       			audit.setStatus(RegisteredPolicyStatusType.INACTIVE.getStatus());
	       		    registeredPolicy.setStatus(RegisteredPolicyStatusType.INACTIVE.getStatus());
	       		}
	               audit.setComments(warranty.getRegistrationComments());
	               audit.setCreatedBy(this.securityHelper.getLoggedInUser());
	               Clock.setDefaultTimeZone(TimeZone.getDefault());
	               audit.setCreatedOn(Clock.now());
	               warranty.getPolicies().add(registeredPolicy);
	           }
	       }
	        }
	   }

	private void createInventoryTransaction(Warranty warranty) {
		InventoryTransaction newTransaction = new InventoryTransaction();
		newTransaction.setStatus(BaseDomain.ACTIVE);
		newTransaction.setTransactionOrder(new Long(warranty.getForItem()
				.getTransactionHistory().size() + 1));
		newTransaction.setInvTransactionType(warranty.getTransactionType());
		if(warranty.getCustomer() == null){
			warranty.setCustomer(warranty.getForDealer());
		}
		newTransaction.setBuyer(warranty.getCustomer());
		
			
		
		/*if(warranty.getCustomer()!= null){
            newTransaction.setBuyer(warranty.getCustomer());
            }else
            {
            newTransaction.setBuyer(warranty.getForDealer()) ;  
            }*/
		newTransaction.setTransactionDate(Clock.today());
		newTransaction.setTransactedItem(warranty.getForItem());
		if (InvTransationType.ETR.getTransactionType().equals(
				warranty.getTransactionType().getTrnxTypeValue())) {
			newTransaction.setSeller(warranty.getForItem().getLatestBuyer());
		} else {
			newTransaction.setSeller(warranty.getForDealer());
		}
		newTransaction.setOwnerShip(warranty.getForDealer());
		warranty.getForItem().getTransactionHistory().add(newTransaction);
		warranty.getForItem().setLatestBuyer(warranty.getCustomer());
		warranty.getForItem().setCurrentOwner(warranty.getForDealer());
		if (InvTransationType.DR.getTransactionType().equals(
				warranty.getTransactionType().getTrnxTypeValue())||InvTransationType.DR_RENTAL.getTransactionType().equals(
						warranty.getTransactionType().getTrnxTypeValue()) || InvTransationType.DEMO.getTransactionType().equals(            
								warranty.getTransactionType().getTrnxTypeValue())) {//SLMSPROD-1174, demo should work in the same way as that of End customer
			warranty.getForItem().setType(new InventoryType("RETAIL"));
			warranty.getForItem().setRegistrationDate(Clock.today());
		}
		warranty.setForTransaction(newTransaction);
		warranty.setStatus(WarrantyStatus.ACCEPTED);
	}

	public void updateInventoryForWarrantyDates(InventoryItem inventoryItem) {
		Warranty latestWarranty = inventoryItem.getLatestWarranty();
		updateInventoryForWarrantyDates(inventoryItem, latestWarranty);
	}
	
	public void updateInventoryForWarrantyDates(InventoryItem inventoryItem, Warranty warranty) {
		CalendarDate shipmentDate = inventoryItem.getShipmentDate();
		CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
        CalendarDate wntyStartDate = null;
		CalendarDate wntyEndDate = null;
		if (warranty != null) {
			for (RegisteredPolicy policy : warranty.getPolicies()) {
				RegisteredPolicyAudit latestPolicyAudit = policy.getLatestPolicyAudit();
                // for EMEA BU wnty end date should be standard policies last end date , for amer it will be any policy end date.
				if (latestPolicyAudit != null) {
                    if(!"AMER".equals(inventoryItem.getBusinessUnitInfo().getName()) && WarrantyType.STANDARD.getType().equalsIgnoreCase(policy.getWarrantyType().getType()) && (policy.getPolicyDefinition()!=null && policy.getPolicyDefinition().getApplicabilityTerms()!=null &&
                            policy.getPolicyDefinition().getApplicabilityTerms().isEmpty())){
                        if(wntyEndDate == null || (wntyEndDate != null && wntyEndDate.isBefore(latestPolicyAudit.getWarrantyPeriod().getTillDate()))){
                            wntyEndDate = latestPolicyAudit.getWarrantyPeriod().getTillDate();
                        }
                    }
					else if("AMER".equals(inventoryItem.getBusinessUnitInfo().getName())) {
                        if(wntyEndDate == null || (wntyEndDate != null && wntyEndDate.isBefore(latestPolicyAudit.getWarrantyPeriod().getTillDate()))){
							wntyEndDate = latestPolicyAudit.getWarrantyPeriod().getTillDate();
						}
                        if(wntyStartDate == null || wntyEndDate.isBefore(latestPolicyAudit.getWarrantyPeriod().getFromDate())) {
                        	wntyStartDate = latestPolicyAudit.getWarrantyPeriod().getFromDate();
                        }
					}
				}
			}
		}
		
        if(inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)){
            if(null != deliveryDate && null != shipmentDate && deliveryDate.isAfter(shipmentDate.plusMonths(6))) {
            	wntyStartDate = inventoryItem.getShipmentDate();
            } else {
            	wntyStartDate = inventoryItem.getDeliveryDate();
            }
        }
        if(wntyEndDate == null){
            wntyEndDate = wntyStartDate;
        }
        
        inventoryItem.setWntyStartDate(wntyStartDate);
		inventoryItem.setWntyEndDate(wntyEndDate);
	}

	public void submitWarrantyReport(List<InventoryItem> forItems)
			throws PolicyException {
		this.inventoryService.createSerializedPartsForInventories(forItems);
		for (InventoryItem forItem : forItems) {
			Warranty warranty = forItem.getLatestWarranty();
			warranty.getLatestAudit().setStatus(warranty.getStatus());
			createPoliciesForWarranty(warranty);
			if (!WarrantyStatus.DRAFT.equals(warranty.getStatus())
					&&( InvTransationType.DR.getTransactionType().equals(
							warranty.getTransactionType().getTrnxTypeValue())|| InvTransationType.DR_RENTAL.getTransactionType().equals(
									warranty.getTransactionType().getTrnxTypeValue()) || InvTransationType.DR_MODIFY.getTransactionType().equals(
											warranty.getTransactionType().getTrnxTypeValue()) || InvTransationType.DEMO.getTransactionType().equals(
													warranty.getTransactionType().getTrnxTypeValue()) ))//SLMSPROD-1174, demo should work in the same way as that of End customer
				updateInventoryForWarrantyDates(warranty.getForItem());
			inventoryService.updateInventoryItem(forItem);
		}
		createWarrantyTaskInstance(forItems, forItems.get(0)
				.getLatestWarranty().getStatus());
	}

	public void processWarrantyTransitionDealer(List<InventoryItem> forItems) {
		for (InventoryItem forItem : forItems) {
			Warranty warranty = forItem.getLatestWarranty();
			if (InvTransationType.DR.getTransactionType().equals(
					warranty.getTransactionType().getTrnxTypeValue()))
				updateInventoryForWarrantyDates(warranty.getForItem());
			inventoryService.updateInventoryItem(forItem);
		}
		if (WarrantyStatus.DELETED == forItems.get(0).getLatestWarranty()
				.getStatus()) {
			closeAllOpenTasks(forItems.get(0).getLatestWarranty()
					.getMultiDRETRNumber());
		} else {
			createWarrantyTaskInstance(forItems, forItems.get(0)
					.getLatestWarranty().getStatus());
		}
	}

	private void createWarrantyTaskInstance(List<InventoryItem> forItems,
			WarrantyStatus status) {
		WarrantyTaskInstance task = null;
		task = getOpenTaskByDRETRNumber(forItems.get(0).getLatestWarranty()
				.getMultiDRETRNumber());
		if (task == null) {
			task = new WarrantyTaskInstance();
		}
		task.setActive(true);
		task.getForItems().clear();
		task.getForItems().addAll(forItems);
		task.setStatus(status);
		task.setWarrantyAudit(forItems.get(0).getLatestWarranty()
				.getLatestAudit());
		task.setMultiDRETRNumber(forItems.get(0).getLatestWarranty()
				.getMultiDRETRNumber());
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(forItems.get(0)
				.getBusinessUnitInfo().getName());
		warrantyTaskInstanceService.createWarrantyTaskInstance(task);

	}

	private void closeAllOpenTasks(String multiDRETRNumber) {
		WarrantyTaskInstance openTask = warrantyTaskInstanceService
				.findActiveTaskWarranty(multiDRETRNumber);
		if (openTask != null) {
			openTask.setActive(false);
			warrantyTaskInstanceService.updateWarrantyTaskInstance(openTask);
		}
	}
	
	private WarrantyTaskInstance getOpenTaskByDRETRNumber(
			String multiDRETRNumber) {
		return warrantyTaskInstanceService
				.findActiveTaskWarranty(multiDRETRNumber);

	}

	private void closeOpenTasksWithStatusUpdate(String multiDRETRNumber,
			WarrantyStatus status) {
		WarrantyTaskInstance openTask = warrantyTaskInstanceService
				.findActiveTaskWarranty(multiDRETRNumber);
		if (openTask != null) {
			openTask.setActive(false);
			openTask.setStatus(status);
			warrantyTaskInstanceService.updateWarrantyTaskInstance(openTask);
		}
	}

	public void deleteWarrantyRegistration(List<InventoryItem> forItems)
			throws PolicyException {
		for (InventoryItem forItem : forItems) {
			Warranty warranty = forItem.getLatestWarranty();
			createPoliciesForWarranty(warranty);
			warranty.getForItem().setPendingWarranty(false);
			boolean isDRWarranty = InvTransationType.DR.getTransactionType()
					.equals(warranty.getTransactionType().getTrnxTypeValue());
			if (isDRWarranty && forItem.getType().equals(InventoryType.STOCK)) {
				resetAdditionalAttributes(forItem);
				//updateInventoryForWarrantyDates(warranty.getForItem());
			} else {
				forItem.setDeliveryDate(forItem.getWntyStartDate());
			}
			inventoryService.updateInventoryItem(warranty.getForItem());
			// warranty.getStatus().setStatus("DELETED");
			warranty.setStatus(WarrantyStatus.DELETED);
		}
		if (WarrantyStatus.DELETED.name() == forItems.get(0)
				.getLatestWarranty().getStatus().name()) {
			createWarrantyTaskInstance(forItems, forItems.get(0)
					.getLatestWarranty().getStatus());
		}
	}

	private void resetAdditionalAttributes(InventoryItem inventoryItem) {
		inventoryItem.setInstallationDate(null);
		inventoryItem.setOem(null);
		inventoryItem.setInstallingDealer(null);
		inventoryItem.setVinNumber(null);
		inventoryItem.setOperator(null);
		inventoryItem.setFleetNumber(null);
		inventoryItem.setDeliveryDate(null);
		inventoryItem.setWntyStartDate(null);
		inventoryItem.setWntyEndDate(null);
		inventoryItem.setHoursOnMachine(new Long(0));
	}

	private Warranty getLatestAcceptedWarranty(InventoryItem inventoryItem, Warranty currentWarranty){
		List<String> transactionTypes = new ArrayList<String>();
		transactionTypes.add(InvTransationType.DR.getTransactionType());
		transactionTypes.add(InvTransationType.DR_MODIFY.getTransactionType());
		transactionTypes.add(InvTransationType.DR_RENTAL.getTransactionType());
		transactionTypes.add(InvTransationType.ETR.getTransactionType());
		transactionTypes.add(InvTransationType.ETR_MODIFY.getTransactionType());
		Warranty latestAcceptedWarranty = null;
		for(Warranty warranty : inventoryItem.getWarranties(transactionTypes)){
			if(!warranty.equals(currentWarranty) && warranty.getStatus().getStatus().equalsIgnoreCase(WarrantyStatus.ACCEPTED.getStatus())){
				latestAcceptedWarranty = warranty;
			}
		}
		return latestAcceptedWarranty;
	}
	
	public void createPoliciesForWarranty(Warranty warranty)
			throws PolicyException {
		if (warranty.getLatestAudit() != null) {
			Warranty latestWarranty = null;
			boolean isETRWarranty = InvTransationType.ETR.getTransactionType()
					.equals(warranty.getTransactionType().getTrnxTypeValue());
			if (isETRWarranty) {
				latestWarranty = getLatestAcceptedWarranty(warranty.getForItem(), warranty);
			}
			
			if (null != warranty.getLatestAudit()){
			for (RegisteredPolicy selectedRegisteredPolicy : warranty
					.getLatestAudit().getSelectedPolicies()) {
				RegisteredPolicy registeredPolicy = new RegisteredPolicy();
				PolicyDefinition definition = selectedRegisteredPolicy
						.getPolicyDefinition();
				registeredPolicy.setPolicyDefinition(definition);
				// For ETR we copy the coverages as existing from a previous
				// warranty
				Integer hoursCovered = null;
				if (isETRWarranty)
					for (RegisteredPolicy existingPolicy : latestWarranty
							.getPolicies()) {
						if (existingPolicy.getPolicyDefinition().getId() == definition
								.getId()) {
							registeredPolicy.setWarrantyPeriod(existingPolicy
									.getWarrantyPeriod());
							hoursCovered = existingPolicy
									.getLatestPolicyAudit()
									.getServiceHoursCovered();
						}
					}
				if (!isETRWarranty) {
					registeredPolicy.setWarrantyPeriod(definition
							.warrantyPeriodFor(warranty.getForItem()));
				}
				// The policy audit is created when the warranty period is set.
				// All operations on audit show follow this.
				RegisteredPolicyAudit audit = registeredPolicy
						.getLatestPolicyAudit();
			
					audit.setServiceHoursCovered(definition.getCoverageTerms()
							.getServiceHoursCovered());
				
				registeredPolicy.setPrice(selectedRegisteredPolicy.getPrice());
				registeredPolicy.setWarranty(warranty);
				registeredPolicy.setPurchaseOrderNumber(null);
				registeredPolicy.setWarranty(warranty);
				audit.setWarrantyPeriod(registeredPolicy.getWarrantyPeriod());
				audit.setStatus(RegisteredPolicyStatusType.SUSPENDED
						.getStatus());
				registeredPolicy.setStatus(RegisteredPolicyStatusType.SUSPENDED.getStatus());
				/**
				 * Logic for reduced warranty coverage. We create a coverage
				 * with SD/ED- Delivery Date with end date with status as
				 * INACTIVE
				 */
				if (audit.getWarrantyPeriod() != null
						&& audit.getWarrantyPeriod().getFromDate() != null
						&& audit.getWarrantyPeriod().getTillDate() != null
						&& audit.getWarrantyPeriod()
								.getFromDate()
								.equals(audit.getWarrantyPeriod().getTillDate())) {
					audit.setStatus(RegisteredPolicyStatusType.INACTIVE
							.getStatus());
					registeredPolicy.setStatus(RegisteredPolicyStatusType.INACTIVE.getStatus());
				}
				audit.setComments(definition.getComments());
				audit.setCreatedBy(this.securityHelper.getLoggedInUser());
				Clock.setDefaultTimeZone(TimeZone.getDefault());
				audit.setCreatedOn(Clock.now());
				warranty.getPolicies().add(registeredPolicy);
			}
		}
		}
	}

	private void activatePolicies(Warranty warranty) throws PolicyException {
		Warranty latestAudit = warranty.getForItem().getLatestWarranty();
		for (RegisteredPolicy registeredPolicy : warranty.getPolicies()) {
			PolicyDefinition definition = registeredPolicy
					.getPolicyDefinition();
			RegisteredPolicyAudit audit = registeredPolicy
					.getLatestPolicyAudit();
			for (RegisteredPolicy existingPolicy : latestAudit.getPolicies()) {
				if (existingPolicy.getPolicyDefinition().getId() == definition
						.getId()) {
					audit.setWarrantyPeriod(existingPolicy.getWarrantyPeriod());
					audit.setServiceHoursCovered(existingPolicy
							.getLatestPolicyAudit().getServiceHoursCovered());
				}
			}
			audit.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
			registeredPolicy.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
			/**
			 * Logic for reduced warranty coverage. We create a coverage with
			 * SD/ED- Delivery Date with end date with status as INACTIVE
			 */
			if (audit.getWarrantyPeriod() != null
					&& audit.getWarrantyPeriod().getFromDate() != null
					&& audit.getWarrantyPeriod().getTillDate() != null
					&& audit.getWarrantyPeriod().getFromDate()
							.equals(audit.getWarrantyPeriod().getTillDate())) {
				audit.setStatus(RegisteredPolicyStatusType.INACTIVE.getStatus());
				registeredPolicy.setStatus(RegisteredPolicyStatusType.INACTIVE.getStatus());
			}
			audit.setComments(definition.getComments());
			audit.setCreatedBy(this.securityHelper.getLoggedInUser());
			Clock.setDefaultTimeZone(TimeZone.getDefault());
			audit.setCreatedOn(Clock.now());
			warranty.getPolicies().add(registeredPolicy);
		}
	}

	public void setWarrantyTaskInstanceService(
			WarrantyTaskInstanceService warrantyTaskInstanceService) {
		this.warrantyTaskInstanceService = warrantyTaskInstanceService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public String getWarrantyMultiDRETRNumber() {
		return warrantyRepository.getWarrantyMultiDRETRNumber();
	}

	public void setWarrantyCoverageRequestService(
			WarrantyCoverageRequestService warrantyCoverageRequestService) {
		this.warrantyCoverageRequestService = warrantyCoverageRequestService;
	}

	public RegisteredPolicy activateRegisteredPolicyBasedOnInstallationDate(
			Warranty warranty, RegisteredPolicy registeredPolicy,
			String comments) {
		createRegisteredPolicyAudit(registeredPolicy,
				RegisteredPolicyStatusType.ACTIVE.getStatus());
		RegisteredPolicyAudit audit = registeredPolicy.getLatestPolicyAudit();
		audit.setComments(comments);
		warranty.getPolicies().add(registeredPolicy);
		return registeredPolicy;
	}

	public WarrantyType findWarrantyTypeByType(String type) {
		return warrantyRepository.findWarrantyTypeByType(type);
	}

	public List<ExtendedWarrantyNotification> findAllStagedExtnWntyPurchaseNotificationForInv(
			InventoryItem invItem) {
		return extnWntyNotificationRepository
				.findExtnWntyPurchaseNotificationForInv(invItem);
	}

	public List<ExtendedWarrantyNotification> findStagedExtnWntyPurchaseNotification(
			InventoryItem invItem, PolicyDefinition policy) {
		return extnWntyNotificationRepository
				.findExtnWntyPurchaseNotificationForInv(invItem, policy);
	}

	public void save(ExtendedWarrantyNotification extnWntyNotification) {
		extnWntyNotificationRepository.save(extnWntyNotification);
	}

	public void update(ExtendedWarrantyNotification extnWntyNotification) {
		extnWntyNotificationRepository.update(extnWntyNotification);
	}

	public void createPoliciesForWarrantyForMajorCompReg(Warranty warranty,
			InventoryItem majorCompInvnItem) throws PolicyException {
		if (warranty.getLatestAudit() != null) {
			for (RegisteredPolicy selectedRegisteredPolicy : warranty
					.getLatestAudit().getSelectedPolicies()) {
				RegisteredPolicy registeredPolicy = new RegisteredPolicy();
				PolicyDefinition definition = selectedRegisteredPolicy
						.getPolicyDefinition();
				registeredPolicy.setPolicyDefinition(definition);
				registeredPolicy.setWarrantyPeriod(definition
						.warrantyPeriodFor(warranty.getForItem()));

				RegisteredPolicyAudit audit = registeredPolicy
						.getLatestPolicyAudit();
				registeredPolicy.setPrice(selectedRegisteredPolicy.getPrice());
				registeredPolicy.setWarranty(warranty);
				audit.setWarrantyPeriod(registeredPolicy.getWarrantyPeriod());
				audit.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
				registeredPolicy.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());

				if (audit.getWarrantyPeriod() != null
						&& audit.getWarrantyPeriod().getFromDate() != null
						&& audit.getWarrantyPeriod().getTillDate() != null
						&& audit.getWarrantyPeriod()
								.getFromDate()
								.equals(audit.getWarrantyPeriod().getTillDate()))
					audit.setStatus(RegisteredPolicyStatusType.INACTIVE
							.getStatus());
				registeredPolicy.setStatus(RegisteredPolicyStatusType.INACTIVE.getStatus());

				audit.setCreatedBy(this.securityHelper.getLoggedInUser());
				Clock.setDefaultTimeZone(TimeZone.getDefault());
				audit.setCreatedOn(Clock.now());
				warranty.getPolicies().add(registeredPolicy);
			}
		}
		save(warranty);
		updateInventoryForWarrantyDates(majorCompInvnItem);
		inventoryService.updateInventoryItem(majorCompInvnItem);
	}

	public void createInventoryAndCreateWarrantyReport(
			List<InventoryItem> forItems, Boolean isManualApproval) {

		this.submitWarrantyReport(forItems);
		if (!isManualApproval) {
			for (InventoryItem item : forItems) {
				item.getLatestWarranty().setStatus(WarrantyStatus.ACCEPTED);
				WarrantyAudit audit = new WarrantyAudit();
				audit.setStatus(WarrantyStatus.ACCEPTED);
				audit.setExternalComments("Auto Accepted");
				item.getLatestWarranty().getWarrantyAudits().add(audit);
			}
			this.processWarrantyTransitionAdmin(forItems);
		}
		//this.processWarrantyTransitionAdmin(forItems);
	}

	public void createInventoryAndCreateWarranty(List<InventoryItem> forItems) {
		this.inventoryService.createSerializedPartsForInventories(forItems);
		this.processWarrantyTransitionAdmin(forItems);

	}

	public void removeInventoryAndCreateWarranty(List<InventoryItem> forItems,
			List<InventoryItem> deletedItems) {
		this.createInventoryAndCreateWarranty(forItems);
		this.inventoryService.deleteInventoryItems(deletedItems);
	}

	public void removeInventoryAndWarranty(List<InventoryItem> forItems,
			List<InventoryItem> deletedItems) {
		this.deleteWarrantyRegistration(forItems);
		this.inventoryService.deleteInventoryItems(deletedItems);
	}

	public CompetitionType findCompetitionType(String competitionType) {

		return warrantyRepository.findCompetitionType(competitionType);
	}

	public CompetitorMake findCompetitorMake(String competitorMake) {

		return warrantyRepository.findCompetitorMake(competitorMake);
	}

	public CompetitorModel findCompetitorModel(String competitorModel) {

		return warrantyRepository.findCompetitorModel(competitorModel);
	}

	public MarketType findMarketType(String marketType) {

		return warrantyRepository.findMarketType(marketType);
	}

	public TransactionType findTransactionType(String trxType) {

		return warrantyRepository.findTransactionType(trxType);
	}

	public void updateWarrantyAndInventoryBOM(InventoryItem inventoryItem,
			Warranty warranty) {
		if (inventoryItem != null)
			this.inventoryService.updateInventoryItem(inventoryItem);
		this.update(warranty);
	}

	public BigDecimal findWRCount(String businessUnit) {

		return warrantyRepository.findWRCount(businessUnit);
	}

public ContractCode findContractCode(String cCode) {

	return warrantyRepository.findContractCode(cCode);
}

public ContractCode findCCode(String cCode) {

	return warrantyRepository.findCCode(cCode);
}

public InternalInstallType findInternalInstallType(String internalInstallType){
	try{
		return warrantyRepository.findInternalInstallType(new Long(internalInstallType));
	}catch(ParseException e){
		logger.error("Error occured while Parsing InternalInstallTypeId : " + internalInstallType);
		return null;
	}
}

public InternalInstallType findInternalInstallTypeByName(String internalInstallType){
	try{
		return warrantyRepository.findInternalInstallTypeByName(internalInstallType);
	}catch(ParseException e){
		logger.error("Error occured while Parsing InternalInstallTypeId : " + internalInstallType);
		return null;
	}
}

public IndustryCode findIndustryCode(String iCode) {

	return warrantyRepository.findIndustryCode(iCode);
}

public MaintenanceContract findMaintenanceContract(String mContract) {

	return warrantyRepository.findMaintenanceContract(mContract);
}

public List<ListOfValues> getLovsForClass(String className, Warranty warranty) {
	
	return lovRepository.findAllActive(className);
}

public MaintenanceContract findMaintenanceContractByName(final String maintenanceContract){
	return warrantyRepository.findMaintenanceContractByName(maintenanceContract);
}

public Long getIndustryCode(String siCode){
	return warrantyRepository.getIndustryCode(siCode);
}

public List<Warranty> getwarrantyesByUpdateDateTime(Date lastupdate, String buName) {
	return warrantyRepository.getwarrantyesByUpdateDateTime(lastupdate,buName);
}

/*public CountyCodeMapping findCountyCode(String countyCode){
	
	return warrantyRepository.findCountyCode(countyCode);
	
}*/
	public IndustryCode findIndustryCodeByIndustryCode(final String industryCode){
		
		return warrantyRepository.findIndustryCodeByIndustryCode(industryCode);
		
	}
	
	public WarrantyAudit findWarrantyAuditFromWarranty(final Warranty warranty, final Date lastupdate){
		return warrantyRepository.findWarrantyAuditFromWarranty(warranty, lastupdate);
	}


}