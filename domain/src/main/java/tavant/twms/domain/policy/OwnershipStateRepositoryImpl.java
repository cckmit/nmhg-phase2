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

package tavant.twms.domain.policy;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

public class OwnershipStateRepositoryImpl extends GenericRepositoryImpl<OwnershipState, Long>
        implements OwnershipStateRepository {

    @SuppressWarnings("unchecked")
    public List<OwnershipState> findAllOwnershipStates() {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from OwnershipState order by id").list();
            }
        });
    }

    public OwnershipState findOwnershipStateByName(String name) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        return findUniqueUsingQuery(
                "from OwnershipState where name=:name ", params);
    }
}
