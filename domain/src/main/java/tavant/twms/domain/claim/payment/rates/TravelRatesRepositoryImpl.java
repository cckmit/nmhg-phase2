/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.claim.payment.rates;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.CriteriaHibernateHelper;
import tavant.twms.domain.common.CurrencyExchangeRateRepository;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.BitSetValueComputer;
import tavant.twms.infra.GenericRepositoryImpl;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 * 
 */
@SuppressWarnings("unchecked")
public class TravelRatesRepositoryImpl extends GenericRepositoryImpl<TravelRates, Long> implements
        TravelRatesRepository {

    private CriteriaHibernateHelper criteriaHibernateHelper;
    
    private CurrencyExchangeRateRepository currencyExchangeRateRepository;

/*    @Override
    public void save(TravelRates entity) {
        updateRelevanceScore(entity);
        super.save(entity);
    }

    @Override
    public void update(TravelRates entity) {
        updateRelevanceScore(entity);
        super.update(entity);
    }*/

    @SuppressWarnings("unchecked")
    public List<TravelRate> findTravelRateConfiguration(final Criteria criteria,
            final CalendarDate asOfDate,final String customerType) {
        List<TravelRate> travelRate = (List<TravelRate>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException,
                            SQLException {
                        Query hbmQuery = session.getNamedQuery("travelRateLookupQuery");
                        Map<String, Object> params = new HashMap<String, Object>();

                        ItemGroup dummyGroup = new ItemGroup();
                        dummyGroup.setId(-1L);

                        params.put("productType", criteria.getProductType() != null ? criteria
                                .getProductType() : dummyGroup);
                        DealerCriterion dealerCriterion = criteria.getDealerCriterion();

                        ServiceProvider dummyDealer = new ServiceProvider();
                        dummyDealer.setId(-1L);
                        params.put("dealer", dealerCriterion != null ? dealerCriterion.getDealer()
                                : dummyDealer);

                        params.put("warrantyType", criteria.getWarrantyType() != null ? criteria
                                .getWarrantyType() : "");
                        params.put("claimType", criteria.getClaimType() != null ? criteria
                                .getClaimType() : "");
                        params.put("asOfDate", asOfDate);
                        params.put("customerType", StringUtils.hasText(customerType) ? customerType : "");
                        hbmQuery.setProperties(params);
                        return hbmQuery.list();

                    }
                });
        return travelRate;
    }

    public TravelRates findByCriteria(final Criteria criteria,final TravelRates price) {
        DetachedCriteria dCriteria = TravelRatesRepositoryImpl.this.criteriaHibernateHelper
				.createCriteriaForUniqueCheck(TravelRates.class, criteria,
						"forCriteria");
		if (price.getCustomerType() != null && price.isCustomerSpecified()) {
			dCriteria.add(Restrictions.eq("customerType", price
					.getCustomerType()));
		} else {
			dCriteria.add(Restrictions.or(Restrictions
					.eq("customerType", "ALL"), Restrictions
					.isNull("customerType")));
		}
		final DetachedCriteria finalCriteria = dCriteria;
		return (TravelRates) getHibernateTemplate().executeWithNativeSession(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						return finalCriteria.getExecutableCriteria(session)
								.uniqueResult();
					}
				});
    }

    @Required
    public void setCriteriaHibernateHelper(CriteriaHibernateHelper criteriaHibernateHelper) {
        this.criteriaHibernateHelper = criteriaHibernateHelper;
    }

    void updateRelevanceScore(TravelRates entity) {
        BitSetValueComputer bitSetValueComputer = new BitSetValueComputer();
        Criteria forCriteria = entity.getForCriteria();
        DealerCriterion dealerCriterion = forCriteria.getDealerCriterion();
        boolean[] bits = new boolean[] {
                dealerCriterion != null && dealerCriterion.getDealer() != null,
                dealerCriterion != null && dealerCriterion.getDealerGroup() != null,
                forCriteria.getClaimType() != null, forCriteria.getWarrantyType() != null,
                forCriteria.getProductType() != null };
        forCriteria.setRelevanceScore(bitSetValueComputer.compute(bits));
    }
    
    
    
}
