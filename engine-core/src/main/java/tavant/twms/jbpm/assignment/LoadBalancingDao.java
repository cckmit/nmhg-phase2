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
package tavant.twms.jbpm.assignment;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class LoadBalancingDao extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<String> findUsersSortedByLoad(final List<String> users) {
        return (List<String>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("select ti.actorId " +
                		"from TaskInstance ti " +
                		"where ti.isOpen = true " +
                		"and ti.actorId in (:users) " +
                		"group by ti.actorId " +
        	            "order by count(ti) asc")
                    .setParameterList("users", users)
                    .list();
            }
        });        
	}
}
