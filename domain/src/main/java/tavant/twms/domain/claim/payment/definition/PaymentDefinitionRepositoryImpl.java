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
import java.util.ArrayList;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.policy.Policy;

import com.domainlanguage.time.CalendarDate;

/**
 * @author sayedAamir
 */
public class PaymentDefinitionRepositoryImpl extends GenericRepositoryImpl<PaymentDefinition,Long> implements PaymentDefinitionRepository {

	private CriteriaHelper criteriaHelper;
	
    
    public CriteriaHelper getCriteriaHelper() {
		return criteriaHelper;
	}

	public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
		this.criteriaHelper = criteriaHelper;
	}

	@SuppressWarnings("unchecked")
    public List<Section> findAllSections() {
        return getHibernateTemplate().find("from Section s order by s.displayPosition");
    }

    @SuppressWarnings("unchecked")
    public PaymentDefinition findByDate(final CalendarDate calendarDate) {
        return (PaymentDefinition) getHibernateTemplate().execute(new HibernateCallback(){
          public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createCriteria(PaymentDefinition.class,"pd")
                      .add( Expression.le("pd.forDuration.fromDate", calendarDate) )
                      .add( Expression.ge("pd.forDuration.tillDate", calendarDate) )
                      .setMaxResults(1)
                      .uniqueResult();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public PaymentDefinition findByPolicy(final Policy policy) {
        return (PaymentDefinition) getHibernateTemplate().execute(new HibernateCallback(){
          public Object doInHibernate(Session session) throws HibernateException, SQLException {
        	    return session.createCriteria(PaymentDefinition.class).createCriteria("criteria")
                      .add( Restrictions.eq("policyDefinition", policy.getPolicyDefinition()) )               
                      .uniqueResult();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
	public List<PaymentDefinition> findAllDefinitionsByDate(final CalendarDate calendarDate) {
        return (List<PaymentDefinition>) getHibernateTemplate().execute(new HibernateCallback(){
          public Object doInHibernate(Session session) throws HibernateException, SQLException {
        	  return session.createCriteria(PaymentDefinition.class,"pd")
                      .add( Expression.le("pd.forDuration.fromDate", calendarDate) )
                      .add( Expression.ge("pd.forDuration.tillDate", calendarDate) ).list();
            }
        });
    }


    @SuppressWarnings("unchecked")
	public List<PaymentDefinition> findAllDefinitionsWithSections(final List<Section> sections) {
       return (List<PaymentDefinition>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
                        List<Long> sectionIds = new ArrayList<Long>();
                        for (Section section : sections) {
                            sectionIds.add(section.getId());
                        }
                        return session
								.createQuery(
										"select distinct(pd) from PaymentDefinition pd join pd.paymentSections ps join ps.section sect  "
												+ "where sect.id in (:sectionIds) "
												+ "order by pd.id")
								.setParameterList("sectionIds",sectionIds).list();
					}

				});
    }

    public void saveOrUpdate(PaymentDefinition paymentDefinition) {
		getHibernateTemplate().saveOrUpdate(paymentDefinition);
	}

	public PaymentDefinition findPaymentDefinitionForCP() {
		return (PaymentDefinition) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(PaymentDefinition.class, "pd").createCriteria("criteria").add(
						Expression.eq("applForCommPolicyClaims", true)).setMaxResults(1).uniqueResult();
			}
		});
	}
}
