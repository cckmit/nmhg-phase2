package tavant.twms.domain.admin;


import java.util.List;

import org.apache.commons.lang.StringUtils;

import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.RoleType;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class RoleServiceImpl extends GenericServiceImpl<Role,Long,Exception>
		implements RoleService {

	private RoleRepository roleRepository;
	
	@SuppressWarnings("unchecked")
	@Override
	public GenericRepository getRepository() { 
		return roleRepository;
	}

	public void setAdminRoleRepository(
			RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	public void createRole(Role role){
		 roleRepository.createRole(role);
	}

    public boolean findIfRoleExists(String name , Long id){
    	String roleName = StringUtils.stripToEmpty(name);
    	return roleRepository.findIfRoleExists(roleName.toUpperCase(), id);
    }
	

	public Role findRoleById(Long id){
		return roleRepository.findRoleById(id);
	}
	
	public Role findRoleByName(String name){
		return roleRepository.findRoleByName(name);
	}

	public List<String> findAllRolesStartingWith(String prefix) {
		return roleRepository.findAllRolesStartingWith(prefix);
	}
	
	public PageResult<Role> findAllRoles(ListCriteria listCriteria) {
		return roleRepository.fetchAllRoles(listCriteria);
	}
	
	public PageResult<Role> fetchAllRolesByRoleType(ListCriteria listCriteria ,List<RoleType> type) {
		return roleRepository.fetchAllRolesByRoleType(listCriteria,type);
	}
	
	public List<Role> getAllRoles(List <RoleType> type){
		return roleRepository.getAllRoles(type);
		
	}

	@SuppressWarnings("unchecked")
	public void updateRole(Role role) {
		roleRepository.update(role);
	}

	public List<UserAction> getAllActions() {
      return this.roleRepository.getAllActions();
	}

	public List<SubjectArea> getAllSubjectAreas() {
          return this.roleRepository.getAllSubjectAreas();
  
	}
	
	public List<Role> findRolesByType(RoleType type){
		return roleRepository.findRolesByType(type);
	}
}