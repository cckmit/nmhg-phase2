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
package tavant.twms.domain.watchlist;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author radhakrishnan.j
 *
 */
public class WatchListRepositoryImpl extends GenericRepositoryImpl<WatchedPart,Long> implements WatchListRepository {

    /**
     * 
     */
    public boolean isWatched(final ServiceProvider dealer) {
        final String query = "select count(*) from WatchedDealership wd where wd.dealer=:dealer";
        Long count = (Long)getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(query)
                    .setEntity("dealer", dealer)
                    .uniqueResult();
            }
        });
        return count.intValue() > 0;
    }

    /**
     * 
     */
    public boolean isWatched(final Item item) {
        final String query = "select count(*) from WatchedPart wi where wi.item=:item";
        Long count = (Long)getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(query)
                    .setEntity("item", item)
                    .uniqueResult();
            }
        });
        return count.intValue() > 0;
    }

}
