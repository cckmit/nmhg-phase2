package tavant.twms.domain.orgmodel;

import java.util.List;

import tavant.twms.infra.DomainRepositoryTestCase;

public class DealerGroupRepositoryImplTest extends DomainRepositoryTestCase {
    private DealerSchemeRepository dealerSchemeRepository;
    private DealerGroupRepository dealerGroupRepository;
    private OrgService orgService;
    
    public void XtestSomething() {
    	//FIX-ME: Fix the test failures.
    }
    
//    public void XtestOwnershipOfDealer() {
//        DealerScheme scheme = dealerSchemeRepository.findById(1L);
//        Dealership dealership = orgService.findDealerById(10L);
//        DealerGroup group = dealerGroupRepository.findGroupContainingDealership(dealership, scheme);
//        assertNotNull(group);
//        dealership = orgService.findDealerById(7L);
//        group = dealerGroupRepository.findGroupContainingDealership(dealership, scheme);
//        assertNull(group);
//    }
    
    public void testFindForOrgHierarchy() {
    	List<DealerGroup> groups = dealerGroupRepository.findGroupsForOrganisationHierarchy("A");
    	DealerGroup group = dealerGroupRepository.findById(2L);
    	assertEquals(1, groups.size());
    	assertEquals(group, groups.get(0));
    	groups = dealerGroupRepository.findGroupsForOrganisationHierarchy("");
    	assertEquals(3, groups.size());
    	assertTrue(groups.contains(group));
    	group = dealerGroupRepository.findById(3L);
    	assertTrue(groups.contains(group));
    	group = dealerGroupRepository.findById(4L);
    	assertTrue(groups.contains(group));
    	groups = dealerGroupRepository.findGroupsForOrganisationHierarchy("D");
    	assertTrue(groups.isEmpty());
    }
    
    public void setDealerGroupRepository(DealerGroupRepository dealerGroupRepository) {
        this.dealerGroupRepository = dealerGroupRepository;
    }
    public void setDealerSchemeRepository(DealerSchemeRepository dealerSchemeRepository) {
        this.dealerSchemeRepository = dealerSchemeRepository;
    }
    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }
}
