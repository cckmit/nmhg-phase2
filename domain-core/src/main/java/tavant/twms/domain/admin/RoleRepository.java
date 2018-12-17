package tavant.twms.domain.admin;



import java.util.List;

import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.RoleType;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@SuppressWarnings("unchecked")
public interface RoleRepository extends GenericRepository {

	public void createRole(Role role);
	
	public boolean findIfRoleExists(String name ,Long id);
	
	public Role findRoleById(Long id);
	
	public List<String> findAllRolesStartingWith(String prefix);
	
	public Role findRoleByName(String name);
	
	public PageResult<Role> fetchAllRoles(ListCriteria listCriteria);
	
	public PageResult<Role> fetchAllRolesByRoleType(ListCriteria listCriteria , List <RoleType> type);
	
	public List<Role> getAllRoles(List<RoleType> roleType);
		
	public List<SubjectArea> getAllSubjectAreas();
	
	public List<UserAction> getAllActions();
	
	public List<Role> findRolesByType(RoleType type);
}
