package tavant.twms.domain.inventory;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.orgmodel.Country;

@Transactional(readOnly = true)
public interface EngineTierCtryMappingService {
	
	DieselTier findDieselTier(final String id);
	
	DieselTier findDieselTierByTier(final String tier);

	Country findCountry(final String id);

	Country findCountryByName(final String name);

	EngineTierCtryMapping findDieselTierByCountry(Country country);
	
	EngineTierCtryMapping findByDieselTierAndByCountry(String dieselTier,String country);
	
	TierTierMapping findTierTierMappingByInventoryTierAndCustomerTier(String inventoryTier, EngineTierCtryMapping customerTier);
}
