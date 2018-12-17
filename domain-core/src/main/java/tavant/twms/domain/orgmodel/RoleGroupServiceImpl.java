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

public class RoleGroupServiceImpl extends
		GenericServiceImpl<RoleGroup, Long, Exception> implements
		RoleGroupService {

	private RoleGroupRepository roleGroupRepository;

	@Override
	public GenericRepository<RoleGroup, Long> getRepository() {
		return (GenericRepository<RoleGroup, Long>) roleGroupRepository;
	}

	public RoleGroup findByNameAndPurpose(String name, String purpose) {
		return roleGroupRepository.findByNameAndPurpose(name, purpose);
	}

	public RoleGroup findGroupContainingRole(Role role, RoleScheme scheme) {
		return roleGroupRepository.findGroupContainingRole(role, scheme);
	}

	public RoleGroup findGroupContainingRole(Role role, String purpose) {
		return roleGroupRepository.findGroupContainingRole(role, purpose);
	}

	public List<RoleGroup> findGroupsByNameAndDescription(RoleScheme scheme,
			String name, String description) {
		return roleGroupRepository.findGroupsByNameAndDescription(scheme, name,
				description);
	}

	public RoleGroup findRoleGroupByName(String name, RoleScheme roleScheme) {
		return roleGroupRepository.findRoleGroupByName(name, roleScheme);
	}

	public List<RoleGroup> findRoleGroupsByPurposes(List<String> purposes) {
		return roleGroupRepository.findRoleGroupsByPurposes(purposes);
	}

	public List<RoleGroup> findRolesFromScheme(RoleScheme roleScheme) {
		return roleGroupRepository.findRolesFromScheme(roleScheme);
	}

	public RoleGroupRepository getRoleGroupRepository() {
		return roleGroupRepository;
	}

	public void setRoleGroupRepository(RoleGroupRepository roleGroupRepository) {
		this.roleGroupRepository = roleGroupRepository;
	}
}
