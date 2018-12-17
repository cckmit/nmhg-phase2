package tavant.twms.domain.catalog;

import java.util.List;

import tavant.twms.infra.GenericRepository;

public interface BrandItemRepository extends GenericRepository<BrandItem, Long> {
	
	/**
	 * 
	 * @param itemNumber
	 * @param brand
	 * @return BrandItem object
	 */
	public BrandItem findBrandByItemIdAndBrand(Item item,
			String brand);
	/**
	 * 
	 * @param item
	 * @return List Of Item's Brands
	 */
    public List<BrandItem> fetchItemBrands(final Item item);
	public BrandItem findBrandItemByName(String name);
	
	public List<BrandItem> findBrandItems(final String number,final String name);
	  
	public BrandItem findUniqueBrandItemByNMHGItemNumber(final String itemNumber);
	
	public BrandItem findUniqueBrandItemByNumberAndBrand(final String itemNumber,final String brand);
}
