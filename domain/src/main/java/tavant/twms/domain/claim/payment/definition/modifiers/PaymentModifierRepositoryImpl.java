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
package tavant.twms.domain.claim.payment.definition.modifiers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.CriteriaHibernateHelper;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

import com.domainlanguage.time.CalendarDate;

/**
 * @author Kiran.Kollipara
 *
 */
public class PaymentModifierRepositoryImpl extends GenericRepositoryImpl<PaymentModifier, Long> implements
        PaymentModifierRepository {

    private CriteriaHibernateHelper criteriaHibernateHelper;

    @SuppressWarnings("unchecked")
    public List<PaymentVariable> findAllPaymentVariables() {
        return getHibernateTemplate().find("from PaymentVariable");
    }
    
    public void savePaymentVariable(PaymentVariable newPaymentVariable) {
        getHibernateTemplate().save(newPaymentVariable);
    }
    
    public void updatePaymentVariable(PaymentVariable newPaymentVariable){
    	getHibernateTemplate().update(newPaymentVariable);
    }
    public PaymentVariable findPaymentVariableByPK(Long paymentVariableId) {
        return (PaymentVariable) getHibernateTemplate().load(PaymentVariable.class, paymentVariableId);
    }
    
    public PaymentVariable findPaymentVariableByName(final String newVariableName) {
        if (logger.isDebugEnabled()) {
            logger.debug("findPaymentVariableByName(" + newVariableName + ")");
        }
        return (PaymentVariable) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createCriteria(PaymentVariable.class)
                        .add(Expression.eq("name", newVariableName))
                        .uniqueResult();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public List<PaymentVariable> findPaymentVariablesBySection(final String sectionName) {
        if (logger.isDebugEnabled()) {
            logger.debug("findPaymentVariablesBySection(" + sectionName + ")");
        }
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createCriteria(PaymentVariable.class).createAlias("section", "section")
                        .add(Expression.eq("section.name", sectionName))
                        .setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
                        .list();             
            }
        });
    }
    
    public void deactivateCriteriaEvaluationPrecedence(
			final PaymentVariable paymentVariable) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createSQLQuery(
						"update criteria_evaluation_precedence set d_active = 0 where d_active = 1 and for_data = '"
								+ paymentVariable.getName()+"'").executeUpdate();
			}

		});
	}
    
    public void deactivatePaymentModifierForVariable(
			final Long paymentVariableId) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createSQLQuery(
						"update payment_modifier set d_active=0 where for_Payment_Variable="
								+ paymentVariableId).executeUpdate();
			}

		});
	}
    
    public void deactivatePaymentVariableLevelForVariable(final Long paymentVariableId) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createSQLQuery(
						"update payment_variable_level set d_active=0 where payment_variable="
								+ paymentVariableId).executeUpdate();
			}

		});
	}
    
    public PaymentModifier findExactForCriteria(final Criteria criteria, final PaymentVariable paymentVariable,final String customerType) {
        return (PaymentModifier) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                DetachedCriteria dCriteria = null;
                dCriteria = criteriaHibernateHelper.createCriteriaForUniqueCheck(PaymentModifier.class, criteria,"forCriteria");
                dCriteria.add(Expression.eq("forPaymentVariable.id", paymentVariable.getId()));
				dCriteria.add(Restrictions.eq("customerType",customerType));				
                org.hibernate.Criteria executableCriteria = dCriteria.getExecutableCriteria(session);
				return executableCriteria.uniqueResult();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public CriteriaBasedValue findValue(final Criteria criteria, final PaymentVariable paymentVariable,final CalendarDate asOfDate,final String customerType) {
    	CriteriaBasedValue rate = (CriteriaBasedValue)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				org.hibernate.Criteria valueCriteria = session.createCriteria(CriteriaBasedValue.class);
				org.hibernate.Criteria modifierCriteria = valueCriteria.createCriteria("parent","parent");
                criteriaHibernateHelper.addCriteria(modifierCriteria,criteria,"forCriteria");
                valueCriteria.add( Expression.le("duration.fromDate",asOfDate) );
                valueCriteria.add( Expression.ge("duration.tillDate",asOfDate) );
                valueCriteria.add(Expression.eq("parent.forPaymentVariable", paymentVariable));
                valueCriteria.add(Expression.or(Expression.eq("parent.customerType",customerType )
                		,Expression.eq("parent.customerType","ALL")));
                valueCriteria.add(Expression.eq("parent.d.active", Boolean.TRUE));
                valueCriteria.addOrder(Order.desc("parent.forCriteria.relevanceScore"));
                return valueCriteria
                	.setMaxResults(1)
                	.uniqueResult();
			}
    	});
    	return rate;
    }

    @Required
    public void setCriteriaHibernateHelper(CriteriaHibernateHelper criteriaHibernateHelper) {
        this.criteriaHibernateHelper = criteriaHibernateHelper;
    }
    
	public PageResult<PaymentModifier> findPage(final ListCriteria listCriteria, final Long paymentVariableId) {

		PageSpecification pageSpecification = listCriteria.getPageSpecification();
		final StringBuffer countQuery = new StringBuffer(" select count(*) ");

		final StringBuffer fromAndWhereClause = new StringBuffer();
		final String fromClause = "from PaymentModifier config join config.forPaymentVariable as pv where config.forPaymentVariable = pv.id and pv.id = :paymentVariableId";
		fromAndWhereClause.append(fromClause);

		if (listCriteria.isFilterCriteriaSpecified()) {
			fromAndWhereClause.append(" and ");
			fromAndWhereClause.append(listCriteria.getParamterizedFilterCriteria());
		}

		countQuery.append(fromAndWhereClause);
		Long numberOfRows = (Long) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(countQuery.toString());
				query.setParameter("paymentVariableId", paymentVariableId);
				for (Map.Entry<String, Object> parameterSpecification : listCriteria.getParameterMap().entrySet()) {
					String name = parameterSpecification.getKey();
					Object value = parameterSpecification.getValue();
					query.setParameter(name, value);
				}
				return query.uniqueResult();
			}
		});
		Integer numberOfPages = pageSpecification.convertRowsToPages(numberOfRows);
		List<PaymentModifier> rowsInPage = new ArrayList<PaymentModifier>();
		PageResult<PaymentModifier> page = new PageResult<PaymentModifier>(rowsInPage, pageSpecification, numberOfPages);

		final Integer pageOffset = pageSpecification.offSet();
		if (numberOfRows > 0 && numberOfRows > pageOffset) {

			final Integer pageSize = pageSpecification.getPageSize();
			rowsInPage = (List<PaymentModifier>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					StringBuffer filterAndSort = new StringBuffer(fromAndWhereClause);

					if (listCriteria.isSortCriteriaSpecified()) {
						filterAndSort.append(" order by ");
						filterAndSort.append(listCriteria.getSortCriteriaString());
					}
					Query query = session.createQuery("select config "+filterAndSort.toString()).setParameter("paymentVariableId", paymentVariableId);
					for (Map.Entry<String, Object> parameterSpecification : listCriteria.getParameterMap().entrySet()) {
						String name = parameterSpecification.getKey();
						Object value = parameterSpecification.getValue();
						query.setParameter(name, value);
					}
					return query.setFirstResult(pageOffset).setMaxResults(pageSize).list();
				}
			});
			page = new PageResult<PaymentModifier>(rowsInPage, pageSpecification, numberOfPages);
		}
		return page;
	}
    
    @SuppressWarnings("unchecked")
    public CriteriaBasedValue findModifierForClaim(final Claim claim, final Criteria criteria, final PaymentVariable paymentVariable,final CalendarDate asOfDate,final String customerType) {
         return (CriteriaBasedValue)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery("paymentModifierApplicableForClaim");
                query.setParameter("dealer",criteria.getDealerCriterion().getDealer());
                query.setParameter("buName",claim.getBusinessUnitInfo().getName());
                query.setParameter("claimType",criteria.getClaimType());
                query.setParameter("warrantyType",criteria.getWarrantyType());
                query.setParameter("productType",criteria.getProductType());
                query.setParameter("customerType",customerType);
                query.setParameter("forPaymentVariable",paymentVariable);
                query.setParameter("asOfDate",asOfDate);
                query.setParameter("servicingLocation", claim.getServicingLocation());
                List results = query.setMaxResults(1).list();
				return (results == null || results.size() == 0) ? null : results.get(0);
            }
        });
    }
}