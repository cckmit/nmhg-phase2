package tavant.twms.domain.catalog;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@Transactional(readOnly=true)
public interface MiscellaneousItemConfigService extends GenericService<MiscellaneousItemCriteria,Long,Exception> {

	public PageResult<MiscellaneousItemCriteria> findAllConfigurations(ListCriteria listCriteria);
	
	@Transactional(readOnly=false)
	public void createMiscItem(MiscellaneousItem miscellaneousItem);
	
	public boolean findIfMiscellaneousPartExists(String name);
	
	public boolean findIfConfigurationWithSameNameExists(String name);
	
	public boolean isDataForDealerGroupConfigured(DealerGroup dealerGroup, List<MiscellaneousItem> miscItems) ;
	
	public boolean isDataForServiceProviderConfigured(ServiceProvider serviceProvider, List<MiscellaneousItem> miscItems);

	public MiscellaneousItem findMiscellaneousItemById(Long id);
	
	public List<String> findAllPartNumbersStartingWith(String prefix);
	
	public MiscellaneousItem findMiscellaneousItemByPartNumber(String partNumber);
	
	public List<MiscellaneousItemConfiguration> findAllMiscellaneousItemConfigs();
	
	public MiscellaneousItemCriteria findForDealerGroup(Long dealerId);
	
	public PageResult<MiscellaneousItem> findAllMiscellaneousPart(ListCriteria criteria);

	@Transactional(readOnly=false)
	public void updateMiscellaneousItem(MiscellaneousItem miscellaneousItem);

	public MiscellaneousItemCriteria findMiscPartConfigById(Long miscPartConfigId);
	
	public List<MiscellaneousItem> findMiscellanousPartForDealer(Long dealerId, String partNumberSearchPrefix);
	
	public List<MiscellaneousItem> findMiscellanousParts(String partNumberSearchPrefix);
	
	public MiscellaneousItemConfiguration findMiscellanousPartConfigurationForDealerAndMiscPart(Long dealerId,String miscPartNumber);
	
	public MiscellaneousItemConfiguration findMiscellanousPartConfigurationForMiscPart(String miscPartNumber);

	public boolean isMiscItemConfiguredForAll(List<MiscellaneousItem> miscItems);

}
