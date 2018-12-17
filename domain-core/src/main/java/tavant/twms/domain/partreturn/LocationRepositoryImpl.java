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
package tavant.twms.domain.partreturn;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author vineeth.varghese
 * 
 */
public class LocationRepositoryImpl extends GenericRepositoryImpl<Location, Long> implements LocationRepository {

    public Location findByLocationCode(final String code) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("code", code);
        return findUniqueUsingQuery("from Location location where location.code = :code", params);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Location findActiveLocationCode(final String code) {
    	final Map<String, Object> params = new HashMap<String, Object>();
    	params.put("code", code);
    	final String queryString = "from Location loc where loc.code = :code";
        return (Location)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	session.enableFilter("excludeInactive");
                Query query = session.createQuery(queryString);
                query.setProperties(params);
                return query.list().get(0);
            }
        });
    	
    }

}
