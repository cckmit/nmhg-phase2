package tavant.twms.domain.inventory;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class DieselTierWaiverServiceImpl extends GenericServiceImpl<DieselTierWaiver, Long, Exception> implements DieselTierWaiverService{
	DieselTierWaiverRepository dieselTierWaiverRepository;
	public DieselTierWaiverRepository getDieselTierWaiverRepository() {
		return dieselTierWaiverRepository;
	}
	public void setDieselTierWaiverRepository(
			DieselTierWaiverRepository dieselTierWaiverRepository) {
		this.dieselTierWaiverRepository = dieselTierWaiverRepository;
	}
	public DieselTierWaiver findByCountryAndDieselTier(String country,String dieselTier){
		return dieselTierWaiverRepository.findByCountryAndDieselTier(country,dieselTier);
	}
	
	public DieselTierWaiver findLatestDieselTierWaiver(InventoryItem inventoryItem){
		return dieselTierWaiverRepository.findLatestDieselTierWaiver(inventoryItem);
	}
	@Override
	public GenericRepository<DieselTierWaiver, Long> getRepository() {
		return dieselTierWaiverRepository;
	}
}
