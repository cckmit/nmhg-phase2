package tavant.twms.domain.catalog;

public class ItemTypeMappingServiceImpl implements ItemTypeMappingService{

	ItemTypeMappingRepository itemTypeMappingRepository;

	public ItemTypeMapping findByExternalItemType(String name) {
		return itemTypeMappingRepository.findByExternalItemType(name);
	}

	public ItemTypeMappingRepository getItemTypeMappingRepository() {
		return itemTypeMappingRepository;
	}

	public void setItemTypeMappingRepository(
			ItemTypeMappingRepository itemTypeMappingRepository) {
		this.itemTypeMappingRepository = itemTypeMappingRepository;
	}

}
