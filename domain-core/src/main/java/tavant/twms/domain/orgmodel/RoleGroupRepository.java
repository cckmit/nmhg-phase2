package tavant.twms.domain.orgmodel;

import java.util.List;

import tavant.twms.infra.GenericRepository;

public interface RoleGroupRepository extends GenericRepository<RoleGroup, Long> {

	public RoleGroup findByNameAndPurpose(String name, String purpose);

	public RoleGroup findGroupContainingRole(Role role, String purpose);

	public List<RoleGroup> findGroupsByNameAndDescription(RoleScheme scheme,
			String name, String description);

	public RoleGroup findRoleGroupByName(String name, RoleScheme roleScheme);

	public List<RoleGroup> findRoleGroupsByPurposes(List<String> purposes);

	public List<RoleGroup> findRolesFromScheme(RoleScheme roleScheme);
	
	public RoleGroup findGroupContainingRole(Role role, RoleScheme scheme);

}
