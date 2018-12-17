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
package tavant.twms.domain.claim.payment.definition;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.security.SecurityHelper;

public class PaymentSectionRepositoryImpl extends HibernateDaoSupport implements PaymentSectionRepository {

    @SuppressWarnings("unchecked")
    public List<PaymentSection> getAllPaymentSections() {
        return getHibernateTemplate().find("from PaymentSection");
    }

    @SuppressWarnings("unchecked")
    public List<Section> getSections() {
        return getHibernateTemplate().find("from Section s order by s.displayPosition");
    }

    public Section getSectionWithName(final String name) {
        return (Section) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from Section s where s.name = :name")
                        .setParameter("name", name).uniqueResult();
            }

        });
    }
    
	@SuppressWarnings("unchecked")
	public List<Section> getSectionWithNameList(final String[] sectionNames) {
		return (List<Section>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createCriteria(Section.class)
								.add(Restrictions.in("name", sectionNames))
								.setResultTransformer(
										CriteriaSpecification.DISTINCT_ROOT_ENTITY)
								.list();
					}
				});
	}

    
}
