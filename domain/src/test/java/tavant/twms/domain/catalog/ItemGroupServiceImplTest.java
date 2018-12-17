package tavant.twms.domain.catalog;

import tavant.twms.domain.common.Purpose;
import tavant.twms.domain.common.PurposeService;
import tavant.twms.infra.DomainRepositoryTestCase;

public class ItemGroupServiceImplTest extends DomainRepositoryTestCase {
    
    private ItemGroupService itemGroupService;
    private PurposeService purposeService;
    private ItemSchemeService itemSchemeService;
    
    public void setItemSchemeService(ItemSchemeService itemSchemeService) {
        this.itemSchemeService = itemSchemeService;
    }

    public void setPurposeService(PurposeService purposeService) {
        this.purposeService = purposeService;
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

    /*
     * This test case has some basic assumptions.
     * 1. there is a purpose called PRODUCT STRUCTURE.
     * 2. A scheme for that purpose exists.
     */
    public void testCreateGroupForProdStruct() {
        ItemGroup group = new ItemGroup();
        group.setName("For Prod Struct");
        group.setDescription("testing group");
        group.setItemGroupType("PRODUCT");
        group.setGroupCode(group.getName());
        itemGroupService.createItemGroupForProductStructure(group);
        Purpose purpose = purposeService.findPurposeByName("PRODUCT STRUCTURE");
        ItemScheme scheme = itemSchemeService.findSchemeForPurpose(purpose);
        assertEquals(group.getScheme(), scheme);
    }

}
