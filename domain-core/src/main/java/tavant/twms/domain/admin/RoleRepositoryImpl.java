package tavant.twms.domain.admin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.RoleType;
import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

@SuppressWarnings("unchecked")
public class RoleRepositoryImpl extends
		GenericRepositoryImpl implements RoleRepository {

	private CriteriaHelper criteriaHelper = getCriteriaHelper();
	
	public void createRole(Role role){
		getHibernateTemplate().save(role);
	}
	
	public boolean findIfRoleExists(String name, Long id){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", name);		
		map.put("id", new Long(-1));
		if (id != null)
		map.put("id", id);
		final String sql = "select count(*) from Role role where upper(role.name) = :name" +
				" and role.id not in (:id)" ; 

		final Map<String,Object> params = map;
		 Long count = (Long)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
                      Query query = session.createQuery(sql);
                      query.setProperties(params);
                      return query.uniqueResult();				
			}
		 });
		 
		 return count != null && count.longValue() > 0 ? true : false;
	}
	public Role findRoleById(final Long id) {
        return getHibernateTemplate().get(Role.class, id);
   }

	public List<String> findAllRolesStartingWith(String prefix) {
		final String queryString = "select role.name from Role role "
				+ "where upper(role.name) like :prefix order by role.name ";		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("prefix", prefix.toUpperCase() + "%");
		final Map<String, Object> parameters = params; 
		 return (List<String>)getHibernateTemplate().execute(new HibernateCallback(){
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
	                      Query query = session.createQuery(queryString);
	                      query.setProperties(parameters);
	                      return query.list();				
				}
			 });
	}

	public List<Role> getAllRoles(final List<RoleType> roleTypeList) {
			return (List<Role>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
					String queryStr = "Select role from Role role ";
						if(roleTypeList != null){
							queryStr += " where role.roleType in (:roleTypeList)"; 
						}
						queryStr +=" order by upper(role.name)";						
						Query q = session.createQuery(queryStr);
						if(roleTypeList!= null){
							q.setParameterList("roleTypeList", roleTypeList);	
						}
						
						return q.list();
						
						
					}
				});
	}
	
	
	
	
	public Role findRoleByName(String name) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", name);		
		final String sql = "select role from Role role where name = :name";          
		final Map<String,Object> params = map;
		 return (Role)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
                      Query query = session.createQuery(sql);
                     
                      query.setProperties(params);
                      return query.uniqueResult();				
			}
		 });
	}


	public PageResult<Role> fetchAllRoles(ListCriteria listCriteria) {
		return findPage("from Role role", listCriteria);
	}
	public PageResult<Role> fetchAllRolesByRoleType(ListCriteria listCriteria, List<RoleType> roleTypeList){
		PageSpecification pageSpecification = listCriteria.getPageSpecification();
        final StringBuffer fromAndWhereClause = new StringBuffer();
        fromAndWhereClause.append("from Role role");
		if( listCriteria.isFilterCriteriaSpecified() ) {
            fromAndWhereClause.append(" where ");
            String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
			fromAndWhereClause.append(paramterizedFilterCriteria);
			fromAndWhereClause.append(" and ");
        }
		else {
			  fromAndWhereClause.append(" where ");
		}
		fromAndWhereClause.append("role.roleType in (:roleTypeList)");
        String queryWithoutSelect = fromAndWhereClause.toString();
        String sortClause = listCriteria.getSortCriteriaString();
        Map<String, Object> parameterMap = listCriteria.getParameterMap();
        parameterMap.put("roleTypeList", roleTypeList);
        return findPageUsingQuery(queryWithoutSelect, sortClause, pageSpecification,parameterMap);
	}
	
	public List<UserAction> getAllActions() {
		final String sql = " from UserAction action ";          		
		return findUsingQuery(sql, new HashMap<String,Object>());
	}

	public List<SubjectArea> getAllSubjectAreas() {
		final String sql = " from SubjectArea subjectArea order by upper(name) ";		
		return findUsingQuery(sql, new HashMap<String,Object>());		
	}
	
	public List<Role> findRolesByType(final RoleType type){
		return (List<Role>) getHibernateTemplate().execute(new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						    String queryString = "select role from Role role where role.roleType =:roleType ";
					        Query query = getSession().createQuery(queryString).setParameter("roleType", type);
					        return query.list();
					}
				});	
	}
}
