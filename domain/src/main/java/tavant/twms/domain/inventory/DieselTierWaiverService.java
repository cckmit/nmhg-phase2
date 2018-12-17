package tavant.twms.domain.inventory;

import tavant.twms.infra.GenericService;

public interface DieselTierWaiverService extends GenericService<DieselTierWaiver, Long, Exception>{

	DieselTierWaiver findByCountryAndDieselTier(String country,String dieselTier);
	
	public DieselTierWaiver findLatestDieselTierWaiver(InventoryItem inventoryItem);
}
