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

import tavant.twms.domain.inventory.ContractCode;
import tavant.twms.domain.inventory.IndustryCode;
import tavant.twms.domain.inventory.InternalInstallType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryListCriteria;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionType;
import tavant.twms.domain.inventory.MaintenanceContract;
import tavant.twms.domain.inventory.TransactionType;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author radhakrishnan.j
 * 
 */
public interface WarrantyRepository {
	public void save(Warranty warranty);

	public void update(Warranty warranty);

	public void delete(Warranty warranty);

	public Warranty findById(Long id);

	public PageResult<Warranty> findWarranties(ServiceProvider dealer,
			PageSpecification pageSpec);

	public PageResult<Warranty> listMatchingWarrantiesForDealer(
			WarrantyListCriteria warrantyListCriteria);
    
    public PageResult<Warranty> listDraftWarrantiesForDealer(InventoryListCriteria inventoryListCriteria);

	public Warranty findBy(InventoryItem inventoryItem);

	public List<TransactionType> listTransactionTypes();

	public List<CompetitionType> listCompetitionTypes();

	public List<MarketType> listMarketTypes();

	public List<WarrantyType> listWarrantyTypes();
	
	public List<CompetitorMake> listCompetitorMake();
	
	public List<CompetitorModel> listCompetitorModel();

	public Warranty findByTransactionId(final Long invTrnxId);

	public InventoryTransaction findWarrantyBySerialNumberAndDealer(String serialNumber,
			Party dealer, InventoryTransactionType inventoryTransactionType);
	
    public PageResult<Warranty> listDraftWarrantiesForInternalUser(InventoryListCriteria inventoryListCriteria);

    public String getWarrantyMultiDRETRNumber();

    public WarrantyType findWarrantyTypeByType(final String type);
    
    public TransactionType findTransactionType(String trxType);
	
	public MarketType findMarketType(String marketType);
	
	public CompetitionType findCompetitionType(String competitionType);
	
	public CompetitorMake findCompetitorMake(String competitorMake);
	
	public CompetitorModel findCompetitorModel(String competitorModel);

	public BigDecimal findWRCount(String businessUnit);

	public List<IndustryCode> listIndustryCode();

	public List<MaintenanceContract> listMaintenanceContract();

	public List<ContractCode> listContractCode();
	
	public List<InternalInstallType> listInternalInstallType();

	public ContractCode findContractCode(String cCode);
	
	public ContractCode findCCode(String cCode);
	
	public InternalInstallType findInternalInstallType(Long internalInstallTypeId);
	
	public InternalInstallType findInternalInstallTypeByName(String internalInstallType);
	
	public IndustryCode findIndustryCode(String iCode);
	
	public MaintenanceContract findMaintenanceContract(String mContract);
	
	public MaintenanceContract findMaintenanceContractByName(final String maintenanceContract);

	public Long getIndustryCode(String siCode);

	public List<Warranty> getwarrantyesByUpdateDateTime(Date lastupdate , String buName);
	
/*	public CountyCodeMapping findCountyCode(String countyName);
*/	
	public IndustryCode findIndustryCodeByIndustryCode(final String industryCode);
	
	public WarrantyAudit findWarrantyAuditFromWarranty(final Warranty warranty, final Date lastupdate);
}
