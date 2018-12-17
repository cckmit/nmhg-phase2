package tavant.twms.domain.catalog;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class MiscellaneousItemConfigServiceImpl extends GenericServiceImpl<MiscellaneousItemCriteria,Long,Exception>
		implements MiscellaneousItemConfigService {

	private MiscellaneousItemConfigRepository miscellaneousItemConfigRepository;
	
	@SuppressWarnings("unchecked")
	@Override
	public GenericRepository getRepository() { 
		return miscellaneousItemConfigRepository;
	}

	public void setMiscellaneousItemConfigRepository(
			MiscellaneousItemConfigRepository miscellaneousItemConfigRepository) {
		this.miscellaneousItemConfigRepository = miscellaneousItemConfigRepository;
	}

	public PageResult<MiscellaneousItemCriteria> findAllConfigurations(ListCriteria listCriteria) {        
		return miscellaneousItemConfigRepository.findAllConfigurations("from MiscellaneousItemCriteria mic",listCriteria);
	}

	public void createMiscItem(MiscellaneousItem miscellaneousItem){
		 miscellaneousItemConfigRepository.createMiscItem(miscellaneousItem);
	}

    public boolean findIfMiscellaneousPartExists(String name){
    	String itemName = StringUtils.stripToEmpty(name);
    	return miscellaneousItemConfigRepository.findIfMiscellaneousPartExists(itemName.toUpperCase());
    }
	
    public boolean isDataForServiceProviderConfigured(ServiceProvider serviceProvider, List<MiscellaneousItem> miscItems){
    	return miscellaneousItemConfigRepository.isDataForServiceProviderConfigured(serviceProvider, miscItems);
    }
    
    public boolean isDataForDealerGroupConfigured(DealerGroup dealerGroup, List<MiscellaneousItem> miscItems){
    	return miscellaneousItemConfigRepository.isDataForDealerGroupConfigured(dealerGroup, miscItems);
    }

	public boolean findIfConfigurationWithSameNameExists(String name) {
		String configName = StringUtils.stripToEmpty(name);
		return miscellaneousItemConfigRepository.findIfConfigurationWithSameNameExists(configName.toUpperCase());
	}

	public MiscellaneousItem findMiscellaneousItemById(Long id){
		return miscellaneousItemConfigRepository.findMiscellaneousItemById(id);
	}
	
	public MiscellaneousItem findMiscellaneousItemByPartNumber(String partNumber){
		return miscellaneousItemConfigRepository.findMiscellaneousItemByPartNumber(partNumber);
	}

	public List<String> findAllPartNumbersStartingWith(String prefix) {
		return miscellaneousItemConfigRepository.findAllPartNumbersStartingWith(prefix);
	}
	
	public List<MiscellaneousItemConfiguration> findAllMiscellaneousItemConfigs(){
		return miscellaneousItemConfigRepository.findAllMiscellaneousItemConfigs(); 
	}

	public MiscellaneousItemCriteria findForDealerGroup(Long dealerGroupId) {
		return miscellaneousItemConfigRepository.findForDealerGroup(dealerGroupId);
	}
	
	public List<MiscellaneousItem> findMiscellanousPartForDealer(Long dealerId, String partNumberSearchPrefix) {
		return miscellaneousItemConfigRepository.findMiscellanousPartForDealer(dealerId, partNumberSearchPrefix);
	}
	
	public List<MiscellaneousItem> findMiscellanousParts(String partNumberSearchPrefix) {
		return miscellaneousItemConfigRepository.findMiscellanousParts(partNumberSearchPrefix);
	}
	
	public PageResult<MiscellaneousItem> findAllMiscellaneousPart(ListCriteria listCriteria) {
		return miscellaneousItemConfigRepository.fetchAllMiscellaneousPart(listCriteria);
	}

	@SuppressWarnings("unchecked")
	public void updateMiscellaneousItem(MiscellaneousItem miscellaneousItem) {
		miscellaneousItemConfigRepository.update(miscellaneousItem);
	}

	public MiscellaneousItemCriteria findMiscPartConfigById(Long miscPartConfigId) {
		return miscellaneousItemConfigRepository.findMiscPartConfigById(miscPartConfigId);
	}

	public MiscellaneousItemConfiguration findMiscellanousPartConfigurationForDealerAndMiscPart(Long dealerId, String miscPartNumber) {		
		return miscellaneousItemConfigRepository.findMiscellanousPartConfigurationForDealerAndMiscPart(dealerId, miscPartNumber);
	}
	
	public MiscellaneousItemConfiguration findMiscellanousPartConfigurationForMiscPart(String miscPartNumber){
		return miscellaneousItemConfigRepository.findMiscellanousPartConfigurationForMiscPart(miscPartNumber);
	}
	
	public boolean isMiscItemConfiguredForAll(List<MiscellaneousItem> miscItems)
	{
		return miscellaneousItemConfigRepository.isMiscItemConfiguredForAll(miscItems);
	}
}
