package tavant.twms.domain.catalog;

import java.util.List;

import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.GroupHierarchyException;
import tavant.twms.domain.common.GroupInclusionException;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class ItemGroupRepositoryImplTest extends DomainRepositoryTestCase {

    private ItemSchemeRepository itemSchemeRepository;

    private ItemGroupRepository itemGroupRepository;

    private CatalogService catalogService;

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void testSimpleCreateOperations() throws Exception {
        ItemScheme scheme = this.itemSchemeRepository.findById(Long.parseLong("2"));
        assertNotNull(scheme);
        ItemGroup group = scheme.createItemGroup("someName", "afgdf");
        this.itemGroupRepository.save(group);
        assertNotNull(group.getId());
    }

    public void testAdditionAndDeletionOfItems() throws Exception {
        ItemScheme scheme = this.itemSchemeRepository.findById(Long.parseLong("2"));
        ItemGroup group = scheme.createItemGroup("name", "sfdvdsds");
        assertNotNull(group.getScheme());
        assertNotNull(group.getForestName());
        this.itemGroupRepository.save(group);
        Long id = group.getId();
        Item item = this.catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-1");

        flush();

        // Test Addition.
        group.includeItem(item);
        this.itemGroupRepository.update(group);
        ItemGroup expected = this.itemGroupRepository.findById(id);
        assertTrue(expected.getIncludedItems().contains(item));

        // Test Deletion.
        group.removeItem(item);
        this.itemGroupRepository.update(group);
        expected = this.itemGroupRepository.findById(id);
        assertFalse(expected.getIncludedItems().contains(item));
    }

    public void testFindGroupsForAScheme() {
        ItemScheme scheme = this.itemSchemeRepository.findById(Long.parseLong("3"));
        ItemGroup group = createGroupForTest(scheme, "UNIGY_DUP");
        this.itemGroupRepository.save(group);
        assertNotNull(group.getId());
        // Before group belongs to the scheme.
        assertEquals(true, this.itemGroupRepository.findItemGroupsFromScheme(scheme).size() > 0);
        assertTrue(this.itemGroupRepository.findItemGroupsFromScheme(scheme).contains(group));
    }

    public void testFindWhereItemBelongs() throws Exception {
        ItemScheme scheme = this.itemSchemeRepository.findById(Long.parseLong("2"));
        ItemGroup group = createGroupForTest(scheme, "someName");
        this.itemGroupRepository.save(group);
        Item item = this.catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-1");

        // Add Item.
        group.includeItem(item);
        this.itemGroupRepository.update(group);

        Long id = group.getId();
        group = this.itemGroupRepository.findById(id);
        assertTrue(group.getIncludedItems().contains(item));
        assertEquals(scheme, group.getScheme());

        // Find Group containing the item.
        ItemGroup resultGroup = this.itemGroupRepository.findGroupContainingItem(item, scheme);
        assertEquals(group, resultGroup);
    }

    public void testFindGroupsByNameAndDescription() throws Exception {
        ItemScheme scheme = this.itemSchemeRepository.findById(Long.parseLong("2"));
        ItemGroup group1 = scheme.createItemGroup("someName", "uhgfdhgf");
        this.itemGroupRepository.save(group1);
        ItemGroup group2 = scheme.createItemGroup("someOtherName", "uhgfdhgf");
        this.itemGroupRepository.save(group2);
        group1.includeGroup(group2);
        this.itemGroupRepository.update(group1);

        List<ItemGroup> result = this.itemGroupRepository.findGroupsByNameAndDescription(scheme,
                "someO", "");
        assertEquals(1, result.size());
        assertTrue(result.contains(group2));
    }

    public void testCascadeSettingsForInclusionOfGroups() throws GroupInclusionException,
            GroupHierarchyException {
        ItemScheme scheme = this.itemSchemeRepository.findById(Long.parseLong("2"));
        ItemGroup group = createGroupForTest(scheme, "someName");
        this.itemGroupRepository.save(group);
        ItemGroup group2 = createGroupForTest(scheme, "name2");
        this.itemGroupRepository.save(group2);
        Long id1 = group.getId();
        Long id2 = group2.getId();
        assertEquals(group, this.itemGroupRepository.findById(id1));
        assertEquals(group2, this.itemGroupRepository.findById(id2));
        group.includeGroup(group2);
        this.itemGroupRepository.update(group);
        assertTrue(group.getConsistsOf().contains(group2));
        assertEquals(group, group2.getIsPartOf());
        this.itemGroupRepository.delete(group);
        assertNull(this.itemGroupRepository.findById(id2));
    }

    public void testFindPage() {
        ItemScheme scheme = this.itemSchemeRepository.findById(3L);
        ListCriteria criteria = new ListCriteria();
        PageResult<ItemGroup> pageResult = this.itemGroupRepository.findPage(new ListCriteria(),
                scheme);
        List<ItemGroup> groups = pageResult.getResult();
        assertEquals(10, groups.size());

        criteria.addFilterCriteria("itemGroup.description", "UNIGY");
        pageResult = this.itemGroupRepository.findPage(criteria, scheme);
        groups = pageResult.getResult();

        assertEquals(3, groups.size());
    }

    public void testFindItemGroupByName() {
        ItemScheme scheme = this.itemSchemeRepository.findById(3L);
        ItemGroup group = this.itemGroupRepository.findItemGroupByName("UNIGY", scheme);
        assertNotNull(group);
        assertEquals("UNIGY", group.getName());
        group = this.itemGroupRepository.findItemGroupByName("JUNK GROUP", scheme);
        assertNull(group);
    }

    public void testFindByNameAndPurpose() {
        ItemGroup ig = this.itemGroupRepository.findByNameAndPurpose("A",
                AdminConstants.ITEM_PRICE_PURPOSE);
        assertEquals(this.itemGroupRepository.findById(292057L), ig);
        ig = this.itemGroupRepository.findByNameAndPurpose("B", AdminConstants.ITEM_PRICE_PURPOSE);
        assertEquals(this.itemGroupRepository.findById(292058L), ig);
        ig = this.itemGroupRepository.findByNameAndPurpose("C", AdminConstants.ITEM_PRICE_PURPOSE);
        assertEquals(this.itemGroupRepository.findById(292059L), ig);
        ig = this.itemGroupRepository.findByNameAndPurpose("D", AdminConstants.ITEM_PRICE_PURPOSE);
        assertNull(ig);
        ig = this.itemGroupRepository.findByNameAndPurpose("anything",
                "A purpose that does not exist.");
        assertNull(ig);
    }

    public void testFindGroupsForGroupType() {
        List<ItemGroup> itemGroups = this.itemGroupRepository.findGroupsForGroupType("PRODUCT");
        for (ItemGroup group : itemGroups) {
            assertEquals(group.getItemGroupType(), "PRODUCT");
        }
    }

    public void testFindModelsForProduct() {
        ItemGroup itemGroup = this.itemGroupRepository.findById(5L);
        List<ItemGroup> itemGroups = this.itemGroupRepository.findModelsForProduct(itemGroup);
        for (ItemGroup group : itemGroups) {
            assertEquals(group.getItemGroupType(), "MODEL");
            assertTrue(group.getConsistsOf().isEmpty());
        }
    }

    private ItemGroup createGroupForTest(ItemScheme itemScheme, String name) {
        ItemGroup group = new ItemGroup();
        group.setName(name);
        group.setDescription("testing group");
        group.setItemGroupType("PRODUCT");
        group.setScheme(itemScheme);
        return group;
    }

    public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
        this.itemGroupRepository = itemGroupRepository;
    }

    public void setItemSchemeRepository(ItemSchemeRepository itemSchemeRepository) {
        this.itemSchemeRepository = itemSchemeRepository;
    }
}