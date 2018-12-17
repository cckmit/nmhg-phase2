package tavant.twms.domain.catalog;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;

public interface CatalogRepository extends GenericRepository<Item, Long>{

	@SuppressWarnings("unchecked")
	public List<String> findItemGroupsWithNameLike(
			final String partialProductName, final int pageNumber,
			final int pageSize);

	@SuppressWarnings("unchecked")
	public List<ItemGroup> findAllItemGroupsWithNameLike(
			final String partialProductName, final int pageNumber,
			final int pageSize);
	
	@SuppressWarnings("unchecked")
	public List<String> findItemGroupsOfTypeWithNameLike(
			final String partialProductName, final String itemGroupType,
			final int pageNumber, final int pageSize);

	@SuppressWarnings("unchecked")
	public List<String> findItemNumbersStartingWith(
			final String partialItemNumber, final int pageNumber,
			final int pageSize);
	@SuppressWarnings("unchecked")
	public List<Item> findItemsWhoseNumbersStartWith(
			final String partialItemNumber, final int pageNumber,
			final int pageSize);
	@SuppressWarnings("unchecked")
	public List<ItemGroup> findModelsWhoseNumbersStartWith(
			final String partialModelName, final int pageNumber,
			final int pageSize);
	@SuppressWarnings("unchecked")
	public List<Item> findParts(final String itemNameOrNumber);
	
	@SuppressWarnings("unchecked")
	public List<Item> findParts(final String itemNameOrNumber, final List<Object> itemGroup);

	public ItemGroup findItemGroup(final Long id);

	@SuppressWarnings("unchecked")
	public List<ItemGroup> findAllItemGroupsOfType(String itemGroupType);

	@SuppressWarnings("unchecked")
	public List<ItemGroup> findAllItemGroups();

	public List<ItemGroup> findAllProductCodes();

	public List<ItemGroup> findAllItemModels();

    public List<ItemGroup> findAllItemProductsAndModels(String partialItemGroup ,final int pageNumber,
			final int pageSize);

	public void createItemGroup(ItemGroup ig);

	public void updateItemGroup(ItemGroup ig);

	public ItemGroup findItemGroupByName(final String name);

	public ItemGroup findItemGroupByCode(final String code);

	@SuppressWarnings("unchecked")
	public PageResult<Item> findItemsWithNumberAndDescriptionLike(
			final String number, final String description, final ListCriteria criteria);

	@SuppressWarnings("unchecked")
	public List<Item> findItemsOwnedBy(final Long id);

	public List<Item> findProdutsWithNameStartingWith(
			final String productNamePrefix,
			final PageSpecification pageSpecification);

	public List<Item> findItemsForCriteria(final DetachedCriteria criteria,
				final PageSpecification pageSpecification);


	@SuppressWarnings("unchecked")
	public List<String> findProductsAndModelsWhoseNameStartsWith(
			final String name, final int firstResult, final int pageMaxResult);

	public ItemGroup findProductOrModelWhoseNameIs(final String name);

	@SuppressWarnings("unchecked")
	public List<Item> findOEMDealerParts(final String itemNameOrNumber,
			final Organization organization, final List<Object> itemGroup);
	
	@SuppressWarnings("unchecked")
	List<Item> findAllItemsWithPurposeWarrantyCoverage(final String purpose,final String partialItemNumber, final int pageNumber,
			final int pageSize);
	
	@SuppressWarnings("unchecked")
	Item findItemWithPurposeWarrantyCoverage(final String purpose, final String partItemNumber);
	
	@SuppressWarnings("unchecked")
	public PageResult<Item> findItems(ListCriteria criteria,
			Organization organization);

	public PageResult<Item> findItemsUsingDynamicQuery(
			String queryWithoutSelect, String orderByClause,
			String selectClause, PageSpecification pageSpecification,
			QueryParameters parameters);

	public Item findItemByItemNumberOwnedByManuf(final String itemNumber);
	
	public Item findItemByItemNumber(final String itemNumber) throws CatalogException;
	
	public Item findItemByItemNumberOwnedByManuf(final String itemNumber, final String itemType);
	
	public Item findItemByItemNumberOwnedByManufAndProduct(String itemNumber,ItemGroup product) throws CatalogException;

	public Item findItemByItemNumberOwnedByServiceProvider(final String itemNumber,final Long ownedById);
	
	public Item findItemByItemNumberAndPurposeOwnedByServiceProvider(final String puroose,final String itemNumber,final Long ownedById);

	public Item findSupplierItem(final String itemNumber, Long supplierId);

	public List<Item> findItemsWithModelName(final String modelName);
	
	public List<String> findAllUoms();
	
	public ResultSet findItems(final PageSpecification pageSpecification) ;

	public BigDecimal findItemCount();
	
	public  List<Item> findItemsByAlternateItemNumber(final String alternateItemNumber,final Long ownedById);

	public List<Item> findItemNumbersByModelName(final String modelName, final String itemNumber);

    public PageResult<ItemGroup> findAllModelsWithCriteria(ListCriteria listCriteria);
    public List<Item> findItemNumbersForNonSerializedClaim(final String itemNumber,final String itemType, final int pageNumber,
			final int pageSize);
    
    public List<ItemGroup> listAllProductCodesMatchingName(final String partialProductName);
    
    public PageResult<Item> findPartReturnItemsForDealer(final ListCriteria listCriteria,final Criteria criteria);
    
    @SuppressWarnings("unchecked")
    public ItemGroup findModelByModelName(final String modelName);

    @SuppressWarnings("unchecked")
    public List<Item> findItemsWithNumberLike(final String number);
    
    public PageResult<Item> findItemsForItemGroup(long itemGroup, ListCriteria criteria);
    
    public List<Item> findByIds(Collection<Long> ids);

    public ItemGroup findItemGroupByProductOrModelName(final String name, final String itemGroupType);

	public BrandItem findBrandItemById(Long id);

	public PageResult<Item> findAllItemsForContract(Contract contract, ListCriteria listCriteria);

	public PageResult<Item> findAllItemsOwnedByWithNumberLike(Long id, String number, ListCriteria listCriteria);

	public Item findItemByBrandPartNumber(String number, String brand);
	
	public ItemGroup findSeriesByGroupCode(final String seriesGroupCode);

	public List<ItemGroup> findAllProductsMatchingGroupCode(
			String partialProductName);
	
	public ItemGroup findItemGroupByDescription(final String description);
    
}