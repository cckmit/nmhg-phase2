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
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.inventory.ContractCode;
import tavant.twms.domain.inventory.IndustryCode;
import tavant.twms.domain.inventory.InternalInstallType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryListCriteria;
import tavant.twms.domain.inventory.MaintenanceContract;
import tavant.twms.domain.inventory.TransactionType;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 */
@Transactional(readOnly = true)
public interface WarrantyService {
	public Warranty findById(Long id);

	@Transactional(readOnly = false)
	public void save(Warranty newWaranty);

	@Transactional(readOnly = false)
	public void update(Warranty warranty);

	@Transactional(readOnly = false)
	public void delete(Warranty warranty);

	public PageResult<Warranty> findWarranties(ServiceProvider forDealer,
			PageSpecification pageSpecification);

	public PageResult<Warranty> listDraftWarrantiesForDealer(
			InventoryListCriteria inventoryListCriteria);

	public PageResult<Warranty> listMatchingWarrantiesForDealer(
			WarrantyListCriteria warrantyListCriteria);

	public Warranty findWarranty(InventoryItem inventoryItem);

	public List<TransactionType> listTransactionTypes();

	public List<MarketType> listMarketTypes();

	public List<CompetitionType> listCompetitionTypes();

	public List<WarrantyType> listWarrantyTypes();

	public List<CompetitorMake> listCompetitorMake();

	public List<CompetitorModel> listCompetitorModel();

	public List<IndustryCode> listIndustryCode();

	public List<MaintenanceContract> listMaintenanceContract();

	public List<ContractCode> listContractCode();
	
	public List<InternalInstallType> listInternalInstallType();

	public RegisteredPolicy register(Warranty warranty,
			PolicyDefinition policyDefinition, CalendarDuration forPeriod,
			Money price, String registrationComments,
			String purchaseOrderNumber, CalendarDate purchaseDate);

	public RegisteredPolicy createPolicyInProgress(Warranty warranty,
			PolicyDefinition policyDefinition, CalendarDuration forPeriod,
			Money price, String registrationComments, String purchaseOrderNumber);

	public void notifyDebitForExtWarranty(DebitMemo debitMemo);

	public RegisteredPolicy activateRegisteredPolicyForAdmin(Warranty warranty,
			RegisteredPolicy registeredPolicy);

	public RegisteredPolicy activateRegisteredPolicyBasedOnInstallationDate(
			Warranty warranty, RegisteredPolicy registeredPolicy,
			String comments);

	public RegisteredPolicy createRegisteredPolicyForAdmin(Warranty warranty,
			RegisteredPolicy registeredPolicy);

	public RegisteredPolicy terminateRegisteredPolicyForAdmin(
			Warranty warranty, RegisteredPolicy registeredPolicy);

	public RegisteredPolicy updateRegisteredPolicyForAdmin(Warranty warranty,
			RegisteredPolicy registeredPolicy, String status);

	public RegisteredPolicy applyGoodWillPolicyForAdmin(Warranty warranty,
			RegisteredPolicy registeredPolicy);

	public Warranty findByTransactionId(final Long invTrnxId);

	public PageResult<Warranty> listDraftWarrantiesForInternalUser(
			InventoryListCriteria inventoryListCriteria);

	@Transactional(readOnly = false)
	public void submitWarrantyReport(List<InventoryItem> forItems)
			throws PolicyException;

	@Transactional(readOnly = false)
	public void processWarrantyTransitionAdmin(List<InventoryItem> forItems)
			throws PolicyException;

	@Transactional(readOnly = false)
	public void processWarrantyTransitionDealer(List<InventoryItem> forItems);

	public String getWarrantyMultiDRETRNumber();

	public void createPoliciesForWarranty(Warranty warranty)
			throws PolicyException;

	public WarrantyType findWarrantyTypeByType(String type);

	public void updateInventoryForWarrantyDates(InventoryItem inventoryItem);
	
	public void updateInventoryForWarrantyDates(InventoryItem inventoryItem, Warranty warranty);

	public List<ExtendedWarrantyNotification> findAllStagedExtnWntyPurchaseNotificationForInv(
			InventoryItem inventoryItem);

	public List<ExtendedWarrantyNotification> findStagedExtnWntyPurchaseNotification(
			InventoryItem inventoryItem, PolicyDefinition policy);

	@Transactional(readOnly = false)
	public void save(ExtendedWarrantyNotification extnWntyNotification);

	@Transactional(readOnly = false)
	public void update(ExtendedWarrantyNotification extnWntyNotification);

	@Transactional(readOnly = false)
	public void createPoliciesForWarrantyForMajorCompReg(Warranty warranty,
			InventoryItem majorComponent) throws PolicyException;

	@Transactional(readOnly = false)
	public void createInventoryAndCreateWarrantyReport(
			List<InventoryItem> forItems, Boolean isManualApproval);

	@Transactional(readOnly = false)
	public void createInventoryAndCreateWarranty(List<InventoryItem> forItems);

	@Transactional(readOnly = false)
	public void removeInventoryAndCreateWarranty(List<InventoryItem> forItems,
			List<InventoryItem> deletedItems);

	@Transactional(readOnly = false)
	public void removeInventoryAndWarranty(List<InventoryItem> forItems,
			List<InventoryItem> deletedItems);

	public TransactionType findTransactionType(String trxType);

	public MarketType findMarketType(String marketType);

	public CompetitionType findCompetitionType(String competitionType);

	public CompetitorMake findCompetitorMake(String competitorMake);

	public CompetitorModel findCompetitorModel(String competitorModel);

	public ContractCode findContractCode(String contractCode);
	public ContractCode findCCode(String contractCode);

	
	public InternalInstallType findInternalInstallType(String internalInstallType);

	public InternalInstallType findInternalInstallTypeByName(String internalInstallType);

	public IndustryCode findIndustryCode(String industryCode);

	public MaintenanceContract findMaintenanceContract(
			String maintenanceContract);

	@Transactional(readOnly = false)
	public void updateWarrantyAndInventoryBOM(InventoryItem inventoryItem,
			Warranty warranty);

	public BigDecimal findWRCount(String businessUnit);
	
	public List<ListOfValues> getLovsForClass(String className, Warranty warranty);
	
	public MaintenanceContract findMaintenanceContractByName(final String maintenanceContract);

	public Long getIndustryCode(String siCode);

	public List<Warranty> getwarrantyesByUpdateDateTime(Date lastupdate, String buName);
	
	//public CountyCodeMapping findCountyCode(String countyName);

	public IndustryCode findIndustryCodeByIndustryCode(String siCode);
	
	public WarrantyAudit findWarrantyAuditFromWarranty(Warranty warranty, Date lastupdate);

}
