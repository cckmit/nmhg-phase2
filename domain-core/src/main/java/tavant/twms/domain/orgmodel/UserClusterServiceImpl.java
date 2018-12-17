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

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class UserClusterServiceImpl extends GenericServiceImpl<UserCluster, Long, Exception> implements UserClusterService {

	private UserClusterRepository userClusterRepository;
	
	public void setUserClusterRepository(UserClusterRepository userClusterRepository) {
		this.userClusterRepository = userClusterRepository;
	}

	@Override
	public GenericRepository<UserCluster, Long> getRepository() {
		return userClusterRepository;
	}

	public UserCluster findByNameAndPurpose(String name, String purpose) {
		return userClusterRepository.findByNameAndPurpose(name, purpose);
	}

	public UserCluster findClusterContainingUser(User user, UserScheme scheme) {
		return userClusterRepository.findClusterContainingUser(user, scheme);
	}

	public List<UserCluster> findClustersByNameAndDescription(UserScheme scheme, String name, String description) {
		return userClusterRepository.findClustersByNameAndDescription(scheme, name, description);
	}

	public PageResult<UserCluster> findPage(ListCriteria listCriteria, UserScheme userScheme) {
		return userClusterRepository.findPage(listCriteria, userScheme);
	}

	public UserCluster findUserClusterByName(String name, UserScheme userScheme) {
		return userClusterRepository.findUserClusterByName(name, userScheme);
	}

	public List<UserCluster> findUserClustersFromScheme(UserScheme userScheme) {
		return userClusterRepository.findUserClustersFromScheme(userScheme);
	}
	
	public List<UserCluster> findUserClustersByPurpose(String purpose) {
		return userClusterRepository.findUserClustersByPurpose(purpose);
	}
	
    public boolean doUserClustersExistForPurpose(String purpose) {
        return userClusterRepository.doUserClustersExistForPurpose(purpose);
    }
}
