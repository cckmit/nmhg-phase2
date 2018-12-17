package tavant.twms.domain.catalog;


public interface ItemTypeMappingRepository {

	public ItemTypeMapping findByExternalItemType(String name);

}
