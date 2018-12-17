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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tavant.twms.infra.GenericRepositoryImpl;

public class RoleGroupRepositoryImpl extends
		GenericRepositoryImpl<RoleGroup, Long> implements RoleGroupRepository {

	public RoleGroup findRoleGroupByName(String name, RoleScheme roleScheme) {
		String query = "select rg from RoleGroup rg where rg.scheme =:scheme and rg.name=:name";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("scheme", roleScheme);
		params.put("name", name);
		return findUniqueUsingQuery(query, params);
	}

	public List<RoleGroup> findRolesFromScheme(RoleScheme roleScheme) {
		String query = "select rg from RoleGroup rg where rg.scheme=:scheme";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("scheme", roleScheme);
		return findUsingQuery(query, params);
	}

	public RoleGroup findGroupContainingRole(Role role, String purpose) {
		String query = "select rg from RoleGroup ic join rg.includedRoles as role join rg.scheme.purposes "
				+ "purposes where role=:aRole and purposes.name=:name ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", purpose);
		params.put("aRole", role);
		return findUniqueUsingQuery(query, params);
	}

	public RoleGroup findGroupContainingRole(Role role, RoleScheme scheme) {
		String query = "select rg from RoleGroup rg join rg.includedRoles as role where role=:aRole and rg.scheme=:scheme ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("scheme", scheme);
		params.put("aRole", role);
		return findUniqueUsingQuery(query, params);
	}

	public List<RoleGroup> findGroupsByNameAndDescription(RoleScheme scheme,
			String name, String description) {
		String query = "select rg from RoleGroup rg where rg.scheme=:scheme and rg.name like :nameParam and rg.description like :descriptionParam order by rg.name";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("scheme", scheme);
		params.put("nameParam", name + "%");
		params.put("descriptionParam", description + "%");
		return findUsingQuery(query, params);
	}

	public RoleGroup findByNameAndPurpose(String name, String purpose) {
		String query = "select rg from RoleGroup rg where rg.name =:name and rg.scheme = (select roleScheme from RoleScheme roleScheme join roleScheme.purposes as purpose "
				+ " where purpose.name=:purpose)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("purpose", purpose);
		return findUniqueUsingQuery(query, params);
	}

	public List<RoleGroup> findRoleGroupsByPurposes(List<String> purposes) {
		String query = "select distinct rg from RoleGroup rg join rg.scheme scheme join scheme.purposes purpose join  "
				+ " rg.includedRoles as role where "
				+ " purpose.name in (:purposes)";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("purposes", purposes);
		return findUsingQuery(query, params);
	}
}
