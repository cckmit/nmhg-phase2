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
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author vineeth.varghese
 *
 */
public class CarrierRepositoryImpl extends GenericRepositoryImpl<Carrier, Long> implements CarrierRepository {

    /* (non-Javadoc)
     * @see tavant.twms.domain.partreturn.CarrierRepository#findCarrierById(java.lang.Long)
     */
    public Carrier findCarrierById(Long id) {
        return (Carrier)getHibernateTemplate().get(Carrier.class, id);
    }

    /* (non-Javadoc)
     * @see tavant.twms.domain.partreturn.CarrierRepository#findCarrierByName(java.lang.String)
     */
    public Carrier findCarrierByName(final String name) {
        return (Carrier)getHibernateTemplate().execute(new HibernateCallback() {            
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from Carrier carrier where carrier.name = :name")
                    .setParameter("name", name).uniqueResult();
            };
        });
    }
    
    @SuppressWarnings("unchecked")    
    public List<Carrier> findAllCarriers() {        
        return getHibernateTemplate().find("from Carrier");
    }    
}
