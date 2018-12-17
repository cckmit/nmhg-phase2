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
package tavant.twms.domain.reports;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.domain.claim.Claim;

import com.domainlanguage.time.CalendarDate;

/**
 * @author bibin.jacob
 * 
 */
public class ClaimsReport extends HibernateDaoSupport {

    @SuppressWarnings("unchecked")
    public List getAllClaims1(final String dealer,final CalendarDate startDate,final CalendarDate endDate) {
                return (List) getHibernateTemplate().execute(new HibernateCallback() {
                        public Object doInHibernate(Session session)
                                        throws HibernateException, SQLException {
                            StringBuffer query=new StringBuffer("from Claim cl where ");
                            if(dealer!=null && dealer.length()>0)
                            {
                                query.append("cl.filedBy.name=").append(dealer).append(" and ");
                            }
                            query.append("cl.filedOnDate between ").append(startDate).append(" and ").append(endDate);
                            query.append(" ORDER BY cl.filedBy.name");
                final List claims = session.createQuery(query.toString()).list();
                                return claims;
                        }

                });
}
    
    @SuppressWarnings("unchecked")
    public List<Claim> getAllClaims(final String dealer,final CalendarDate startDate,final CalendarDate endDate) {
        return getHibernateTemplate()
            .find("from Claim");
    }    

}
