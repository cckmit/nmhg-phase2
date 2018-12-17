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
package tavant.twms.domain.claim.payment.definition.modifiers;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.bu.BusinessUnitInfoType;
import tavant.twms.domain.claim.payment.rates.LaborRate;
import tavant.twms.domain.claim.payment.rates.TravelRate;

public class DealerSummaryRepositoryImpl extends HibernateDaoSupport implements DealerSummaryRepository{

    @SuppressWarnings("unchecked")
    public List<CriteriaBasedValue> findCriteriaBasedValues(final ServiceProvider serviceProvider,
                                                            final List<String> businessUnitNameList) {
        return (List< CriteriaBasedValue>) getHibernateTemplate().execute(new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery("criteriaBasedValueForDealerSummary");
                query.setParameter("dealer",serviceProvider);
                query.setParameterList("buName",businessUnitNameList, Hibernate.custom(BusinessUnitInfoType.class));
                return query.list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<DealerGroup> findAllParentsOfServiceProvider(final ServiceProvider serviceProvider, final String purpose,
                                                             final List<String> businessUnitNameList) {
         return (List<DealerGroup>)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery("parentsForServiceProvider");
                query.setParameter("serviceProvider",serviceProvider);
                query.setParameter("purpose",purpose);
                query.setParameterList("buName",businessUnitNameList, Hibernate.custom(BusinessUnitInfoType.class));
                return query.list();
            }
        });

    }

    @SuppressWarnings("unchecked")
    public List<TravelRate> findTravelRates(final ServiceProvider serviceProvider,
                                            final List<String> businessUnitNameList) {
        return (List<TravelRate>)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query hbmQuery = session.getNamedQuery("travelRateForDealerSummary");
				hbmQuery.setParameter("dealer",serviceProvider);
                hbmQuery.setParameterList("buName",businessUnitNameList, Hibernate.custom(BusinessUnitInfoType.class));
                return hbmQuery.list();
			}
		});
    }

    @SuppressWarnings("unchecked")
    public List<LaborRate> findLaborRates(final ServiceProvider serviceProvider, final List<String> businessUnitNameList) {
        return (List<LaborRate>)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query hbmQuery = session.getNamedQuery("laborRateForDealerSummary");
				Map<String,Object> params = new HashMap<String,Object>();
                hbmQuery.setParameter("dealer",serviceProvider);
                hbmQuery.setParameterList("buName",businessUnitNameList, Hibernate.custom(BusinessUnitInfoType.class));
                hbmQuery.setProperties(params);
                return hbmQuery.list();
			}
		});
    }
}
