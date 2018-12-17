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

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.Constants;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class DealerGroupServiceImpl extends
		GenericServiceImpl<DealerGroup, Long, Exception> implements
		DealerGroupService {

	private DealerGroupRepository dealerGroupRepository;

	public void setDealerGroupRepository(
			DealerGroupRepository dealerGroupRepository) {
		this.dealerGroupRepository = dealerGroupRepository;
	}

	@Override
	public GenericRepository<DealerGroup, Long> getRepository() {
		return dealerGroupRepository;
	}

	public DealerGroup findDealerGroupByName(String name,
			DealerScheme dealerScheme) {
		return dealerGroupRepository.findDealerGroupByName(name, dealerScheme);
	}
	
	public DealerGroup findDealerGroupByCode(String code,
			DealerScheme dealerScheme) {
		return dealerGroupRepository.findDealerGroupByCode(code, dealerScheme);
	}


	public List<DealerGroup> findDealerGroupsFromScheme(
			DealerScheme dealerScheme) {
		return dealerGroupRepository.findDealerGroupsFromScheme(dealerScheme);
	}

	public DealerGroup findGroupContainingServiceProviders(ServiceProvider serviceProvider,
			DealerScheme scheme) {
		return dealerGroupRepository.findGroupContainingServiceProviders(serviceProvider,
				scheme);
	}

	public List<DealerGroup> findGroupsByNameAndDescription(
			DealerScheme scheme, String name, String description) {
		return dealerGroupRepository.findGroupsByNameAndDescription(scheme,
				name, description);
	}

	public PageResult<DealerGroup> findPage(ListCriteria listCriteria,
			DealerScheme dealerScheme) {
		return dealerGroupRepository.findPage(listCriteria, dealerScheme);
	}

	public DealerGroup findByNameAndPurpose(String name, String purpose) {
		return dealerGroupRepository.findByNameAndPurpose(name, purpose);
	}
	
	public List<DealerGroup> findByNamesAndPurpose(List<String> names, String purpose) {
		return dealerGroupRepository.findByNamesAndPurpose(names, purpose);
	}

        public List<DealerGroup> findAllGroupsForOrganisationHierarchy() {
            return dealerGroupRepository.findAllGroupsForOrganisationHierarchy();
        }

	public List<DealerGroup> findGroupsForOrganisationHierarchy(String name) {
		return dealerGroupRepository.findGroupsForOrganisationHierarchy(name);
	}

	public List<String> findGroupsWithNameStartingWith(String name,
			PageSpecification pageSpecification, String purpose) {
		return dealerGroupRepository.findGroupsWithNameStartingWith(name,
				pageSpecification, purpose);
	}

	public DealerGroup findDealerGroupsForWatchedDealership(ServiceProvider dealer) {
		return dealerGroupRepository
				.findDealerGroupsForWatchedDealership(dealer);
	}

        public List<ServiceProvider> findProvidersAtAllLevelForGroup(String groupName) {
            return dealerGroupRepository.findProvidersAtAllLevelForGroup(groupName);
        }
	public boolean isDealerInWatchList(ServiceProvider dealer) {
		DealerGroup dealerGroup = dealerGroupRepository
				.findDealerGroupsForWatchedDealership(dealer);
		if (dealerGroup != null) {
			return true;
		}
		return false;
	}
	
	public boolean isDealerInTerritoryExclusion(ServiceProvider dealer) {
		DealerGroup dealerGroup = dealerGroupRepository.isDealerInTerritoryExclusion(dealer);
		if (dealerGroup != null) {
			return true;
		}
		return false;
	}

	public List<DealerGroup> findDealerGroupsByPurposes(List<String> purposes){
		return dealerGroupRepository.findDealerGroupsByPurposes(purposes);
	}
	
	public boolean isDealerGroupExistByNameAndDealer(String dealerGroupName, ServiceProvider dealer){
		return dealerGroupRepository.isDealerGroupExistByNameAndDealer(dealerGroupName,  dealer);
	}

	public List<DealerGroup> findDealerGroupsWithNameLike(final String name,
			final PageSpecification pageSpecification, final String purpose){
		return dealerGroupRepository.findDealerGroupsWithNameLike(name, pageSpecification, purpose);
	}

	public DealerGroup findGroupContainingDealership(final ServiceProvider dealership, final String purpose,
			final BusinessUnitInfo businessUnitInfo) {
		return dealerGroupRepository.findGroupContainingDealership(dealership, purpose, businessUnitInfo);
	}
}
