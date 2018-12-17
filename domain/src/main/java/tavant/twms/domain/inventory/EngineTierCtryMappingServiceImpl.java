package tavant.twms.domain.inventory;

import tavant.twms.domain.orgmodel.Country;

public class EngineTierCtryMappingServiceImpl implements EngineTierCtryMappingService{
	private EngineTierCtryMappingRepository engineTierCtryMappingRepository; 
	
	public DieselTier findDieselTier(final String id){
		return engineTierCtryMappingRepository.findDieselTier(id);
	}
	
	public DieselTier findDieselTierByTier(final String tier){
		return engineTierCtryMappingRepository.findDieselTierByTier(tier);
	}
	public Country findCountry(final String id){
		return engineTierCtryMappingRepository.findCountry(id);
	}
	
	public Country findCountryByName(final String name){
		return engineTierCtryMappingRepository.findCountryByName(name);
	}
	
	public EngineTierCtryMapping findDieselTierByCountry(Country country){
		return engineTierCtryMappingRepository.findDieselTierByCountry(country);
	}
	public EngineTierCtryMappingRepository getengineTierCtryMappingRepository() {
		return engineTierCtryMappingRepository;
	}
	public void setengineTierCtryMappingRepository(
			EngineTierCtryMappingRepository engineTierCtryMappingRepository) {
		this.engineTierCtryMappingRepository = engineTierCtryMappingRepository;
	}
	
	public EngineTierCtryMapping findByDieselTierAndByCountry(String dieselTier,String country){
		return engineTierCtryMappingRepository.findByDieselTierAndByCountry(dieselTier,country);
	}
	
	public TierTierMapping findTierTierMappingByInventoryTierAndCustomerTier(String inventoryTier, EngineTierCtryMapping customerTier){
		return engineTierCtryMappingRepository.findTierTierMappingByInventoryTierAndCustomerTier(inventoryTier, customerTier);
	}
}
