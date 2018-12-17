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

import tavant.twms.infra.GenericService;

public interface RoleGroupService extends
		GenericService<RoleGroup, Long, Exception> {
	public List<RoleGroup> findRolesFromScheme(RoleScheme roleScheme);

	public RoleGroup findGroupContainingRole(Role role, String purpose);

	public RoleGroup findRoleGroupByName(String name, RoleScheme roleScheme);

	public List<RoleGroup> findGroupsByNameAndDescription(RoleScheme scheme,
			String name, String description);

	public RoleGroup findByNameAndPurpose(String name, String purpose);

	public List<RoleGroup> findRoleGroupsByPurposes(List<String> purposes);
	
	public RoleGroup findGroupContainingRole(Role role, RoleScheme scheme);

}
