/*
 *   Copyright (c)2006 Tavant Technologies
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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author kiran.sg
 */
public class RoleRepositoryImpl extends GenericRepositoryImpl<Role, Long>
		implements RoleRepository {

	public Role findRoleByName(String roleName) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(roleName!=null)
		    roleName=roleName.toUpperCase();
		params.put("roleName", roleName);
		return findUniqueUsingQuery("from Role where upper(name)=:roleName", params);
	}
	 

	public Role findByName(final String roleName) {
		return (Role) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createQuery(
						"from Role r where r.name =:nameParam").setString(
						"nameParam", roleName).uniqueResult();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<String> findRoleNamesStartingWith(final String name,
			final int pageNumber, final int pageSize) {

		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select role.name from Role role where role.name like :roleName")
								.setParameter("roleName", name + "%")
								.setFirstResult(pageSize * pageNumber)
								.setMaxResults(pageSize).list();
					};
				});
	}
}
