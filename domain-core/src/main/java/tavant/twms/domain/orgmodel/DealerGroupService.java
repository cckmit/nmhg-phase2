/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.orgmodel;

import java.util.List;

import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public interface DealerGroupService extends
		GenericService<DealerGroup, Long, Exception> {
	public List<DealerGroup> findDealerGroupsFromScheme(
			DealerScheme dealerScheme);

	public DealerGroup findGroupContainingServiceProviders(ServiceProvider dealership,
			DealerScheme scheme);

	public PageResult<DealerGroup> findPage(ListCriteria listCriteria,
			DealerScheme dealerScheme);

	public DealerGroup findDealerGroupByName(String name,
			DealerScheme dealerScheme);
	
	public DealerGroup findDealerGroupByCode(String code,
			DealerScheme dealerScheme);

	public List<DealerGroup> findGroupsByNameAndDescription(
			DealerScheme scheme, String name, String description);

	public DealerGroup findByNameAndPurpose(String name, String purpose);
	
	public List<DealerGroup> findByNamesAndPurpose(List<String> name, String purpose);

	public List<DealerGroup> findGroupsForOrganisationHierarchy(String name);

        public List<DealerGroup> findAllGroupsForOrganisationHierarchy();

	public List<String> findGroupsWithNameStartingWith(String name,
			PageSpecification pageSpecification, String purpose);

	public DealerGroup findDealerGroupsForWatchedDealership(ServiceProvider dealer);

        public List<ServiceProvider> findProvidersAtAllLevelForGroup(String groupName);

	public boolean isDealerInWatchList(ServiceProvider dealer);
	
	public List<DealerGroup> findDealerGroupsByPurposes(List<String> purposes);
	
	public boolean isDealerGroupExistByNameAndDealer(String dealerGroupName, ServiceProvider dealer);

	public List<DealerGroup> findDealerGroupsWithNameLike(final String name,
			final PageSpecification pageSpecification, final String purpose);

	public DealerGroup findGroupContainingDealership(final ServiceProvider dealership, final String purpose,
			final BusinessUnitInfo businessUnitInfo);
	
	public boolean isDealerInTerritoryExclusion(ServiceProvider dealer);
}
