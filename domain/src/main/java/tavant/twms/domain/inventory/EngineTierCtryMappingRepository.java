package tavant.twms.domain.inventory;

import tavant.twms.domain.orgmodel.Country;
import tavant.twms.infra.GenericRepository;

public interface EngineTierCtryMappingRepository extends
		GenericRepository<EngineTierCtryMapping, Long> {

	DieselTier findDieselTier(final String id);

	DieselTier findDieselTierByTier(final String tier);

	Country findCountry(final String id);

	Country findCountryByName(final String name);

	EngineTierCtryMapping findDieselTierByCountry(Country country);

	EngineTierCtryMapping findByDieselTierAndByCountry(String dieselTier,String country);
	
	TierTierMapping findTierTierMappingByInventoryTierAndCustomerTier(String inventoryTier, EngineTierCtryMapping customerTier);
}
