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
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author aniruddha.chaturvedi
 *
 */
public interface UserClusterRepository extends GenericRepository<UserCluster, Long> {
	public List<UserCluster> findUserClustersFromScheme(UserScheme userScheme);
    
    public UserCluster findClusterContainingUser(User user, UserScheme scheme);
    
    public PageResult<UserCluster> findPage(ListCriteria listCriteria, UserScheme userScheme);

    public UserCluster findUserClusterByName(String name, UserScheme userScheme);

    public List<UserCluster> findClustersByNameAndDescription(UserScheme scheme, String name, String description);

    public UserCluster findByNameAndPurpose(String name, String purpose);
    
    public List<UserCluster> findUserClustersByPurpose(String purpose);

    public boolean doUserClustersExistForPurpose(String purpose);
}
