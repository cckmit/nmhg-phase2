package tavant.twms.domain.inventory;

import tavant.twms.infra.GenericRepository;

public interface DieselTierWaiverRepository extends GenericRepository<DieselTierWaiver, Long> {
	DieselTierWaiver findByCountryAndDieselTier(String country,String dieselTier);
	
	public DieselTierWaiver findLatestDieselTierWaiver(InventoryItem inventoryItem);
}
