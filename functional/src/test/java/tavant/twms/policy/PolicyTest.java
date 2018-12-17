package tavant.twms.policy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionService;
import tavant.twms.infra.IntegrationTestCase;

public class PolicyTest extends IntegrationTestCase {
	
	private PolicyDefinitionService policyDefinitionService;
	public void setPolicyDefinitionService(
			PolicyDefinitionService policyDefinitionService) {
		this.policyDefinitionService = policyDefinitionService;
	}
	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}
	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}
	private OrgService orgService;
	private DealerGroupService dealerGroupService;
	private CatalogService catalogService;
	public void testUpdatePolicy()

	{
		login("fboselli");
		Set<ServiceProvider> serviceProviderList = new HashSet<ServiceProvider>();
		ServiceProvider serviceProvider = orgService.findDealerById(Long.parseLong("1100005065940"));
				serviceProviderList.add(serviceProvider);	
		Set<DealerGroup> dealerGroupList = new HashSet<DealerGroup>();
		dealerGroupList.add(dealerGroupService.findByNameAndPurpose("Standard Group", AdminConstants.STANDARD));
				PolicyDefinition policy = policyDefinitionService.findPolicyDefinitionById(1100000003820L);
		policy.setApplicableServiceProviders(serviceProviderList);
		policy.setApplicableDealerGroups(dealerGroupList);	
		policyDefinitionService.update(policy);		
		assertEquals(true, policy.getApplicableServiceProviders().contains(orgService.findDealerById(Long.parseLong("1100005065940"))));
		assertEquals(true, policy.getApplicableDealerGroups().contains(dealerGroupService.findByNameAndPurpose("Standard Group", AdminConstants.STANDARD)));
		flushAndClear();		
		policy = policyDefinitionService.findPolicyDefinitionById(1100000003820L);
	}

	}
