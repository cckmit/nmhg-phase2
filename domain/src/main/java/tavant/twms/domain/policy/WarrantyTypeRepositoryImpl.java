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
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author radhakrishnan.j
 *
 */
public class WarrantyTypeRepositoryImpl extends HibernateDaoSupport implements WarrantyTypeRepository {

    @SuppressWarnings("unchecked")
    public List<WarrantyType> findAll() {
        return getHibernateTemplate().find("from WarrantyType");
    }

    public WarrantyType findById(Long id) {
        return (WarrantyType)getHibernateTemplate().get(WarrantyType.class,id);
    }

    public WarrantyType findByType(final String type) {
        return (WarrantyType)getHibernateTemplate().execute(new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from WarrantyType where type=:type")
                    .setParameter("type", type)
                    .uniqueResult();
            }
        });
    }

    
}
