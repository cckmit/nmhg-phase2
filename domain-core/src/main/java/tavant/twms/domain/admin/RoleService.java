package tavant.twms.domain.admin;



import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.RoleType;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@Transactional(readOnly=true)
public interface RoleService extends GenericService<Role,Long,Exception> {
	
	@Transactional(readOnly=false)
	public void createRole(Role role);
	
	public boolean findIfRoleExists(String name, Long id);
	
	public Role findRoleById(Long id);
	
	public List<String> findAllRolesStartingWith(String prefix);
	
	public Role findRoleByName(String name);
	
	public PageResult<Role> findAllRoles(ListCriteria criteria);
	
	public PageResult<Role> fetchAllRolesByRoleType(ListCriteria listCriteria ,List<RoleType> type);
	
	public List<Role> getAllRoles(List<RoleType> type);
	
	public List<UserAction> getAllActions();
	
	public List<SubjectArea> getAllSubjectAreas();

	@Transactional(readOnly=false)
	public void updateRole(Role role);
	
	public List<Role> findRolesByType(RoleType type);

}