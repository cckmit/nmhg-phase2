package tavant.twms.domain.catalog;

import java.util.List;

import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@SuppressWarnings("unchecked")
public interface MiscellaneousItemConfigRepository extends GenericRepository {

	public void createMiscItem(MiscellaneousItem miscellaneousItem);
	
	public boolean findIfMiscellaneousPartExists(String name);
	
	public boolean isDataForDealerGroupConfigured(DealerGroup dealerGroup, List<MiscellaneousItem> miscItems) ;
	
	public boolean isDataForServiceProviderConfigured(ServiceProvider serviceProvider, List<MiscellaneousItem> miscItems);
	
	public boolean findIfConfigurationWithSameNameExists(String name);
	
	public MiscellaneousItem findMiscellaneousItemById(Long id);
	
	public List<String> findAllPartNumbersStartingWith(String prefix);
	
	public MiscellaneousItem findMiscellaneousItemByPartNumber(String partNumber);
	
	public List<MiscellaneousItemConfiguration> findAllMiscellaneousItemConfigs();
	
	public MiscellaneousItemCriteria findForDealerGroup(Long dealerGroupId);

	public PageResult<MiscellaneousItem> fetchAllMiscellaneousPart(ListCriteria listCriteria);

	public PageResult<MiscellaneousItemCriteria> findAllConfigurations(
			String string, ListCriteria listCriteria);

	public MiscellaneousItemCriteria findMiscPartConfigById(Long miscPartConfigId);
	
	public List<MiscellaneousItem> findMiscellanousPartForDealer(Long dealer, String partNumberSearchPrefix);
	
	public List<MiscellaneousItem> findMiscellanousParts(String partNumberSearchPrefix);
	
	public MiscellaneousItemConfiguration findMiscellanousPartConfigurationForDealerAndMiscPart(final Long dealerId,final String miscPartNumber);
	
	public MiscellaneousItemConfiguration findMiscellanousPartConfigurationForMiscPart(final String miscPartNumber);
	
	public boolean isMiscItemConfiguredForAll(List<MiscellaneousItem> miscItems);
}
