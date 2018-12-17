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
package tavant.twms.domain.policy;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.CriteriaHibernateHelper;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.BitSetValueComputer;
import tavant.twms.infra.GenericRepositoryImpl;

import com.domainlanguage.time.CalendarDate;

/**
 * @author kiran.sg
 * 
 */
@SuppressWarnings("unchecked")
public class PolicyRatesRepositoryImpl extends
		GenericRepositoryImpl<PolicyRates, Long> implements
		PolicyRatesRepository {

	private CriteriaHibernateHelper criteriaHibernateHelper;

	@SuppressWarnings("unchecked")
	public BigDecimal findPolicyRateConfiguration(
			final PolicyRatesCriteria criteria,
			final PolicyDefinition policyDefinition, final CalendarDate asOfDate) {
		BigDecimal totalRate = (BigDecimal) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query hbmQuery = session
								.getNamedQuery("policyRateLookupQuery");
						Map<String, Object> params = new HashMap<String, Object>();

						ItemGroup dummyGroup = new ItemGroup();
						dummyGroup.setId(-1L);

						params.put("productType",
								criteria.getProductType() != null ? criteria
										.getProductType() : dummyGroup);
						DealerCriterion dealerCriterion = criteria
								.getDealerCriterion();

						ServiceProvider dummyDealer = new ServiceProvider();
						dummyDealer.setId(-1L);
						params.put("dealer",
								dealerCriterion != null ? dealerCriterion
										.getDealer() : dummyDealer);

						params.put("warrantyType",
								criteria.getWarrantyType() != null ? criteria
										.getWarrantyType() : "");

						params.put("state",
								criteria.getCustomerState() != null ? criteria
										.getCustomerState() : "");

						params
								.put(
										"registrationType",
										criteria.getWarrantyRegistrationType() != null ? criteria
												.getWarrantyRegistrationType()
												: WarrantyRegistrationType.ALL);
						
						params.put("allRegistrationTypes",WarrantyRegistrationType.ALL);

						params.put("policyDefinition", policyDefinition.getId());
						params.put("asOfDate", asOfDate);
						hbmQuery.setProperties(params);
						hbmQuery.setMaxResults(1);
						return hbmQuery.uniqueResult();

					}
				});
		return totalRate;
	}

	public PolicyRates findByCriteria(final PolicyRatesCriteria criteria) {
		return (PolicyRates) getHibernateTemplate().executeWithNativeSession(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						DetachedCriteria dCriteria = criteriaHibernateHelper
								.createCriteriaForUniqueCheck(
										PolicyRates.class, criteria,
										"forCriteria");

						// For Registration type
						Criterion registrationTypeIsNull = Expression
								.isNull("forCriteria.warrantyRegistrationType");
						if (criteria.getWarrantyRegistrationType() != null) {
							Criterion specifiedRegistrationType = Expression
									.eq(
											"forCriteria.warrantyRegistrationType",
											criteria
													.getWarrantyRegistrationType());
							dCriteria.add(Expression.or(
									specifiedRegistrationType,
									registrationTypeIsNull));
						} else {
							dCriteria.add(registrationTypeIsNull);
						}

						// For Customer State
						Criterion customerStateIsNull = Expression
								.isNull("forCriteria.customerState");
						if (criteria.getCustomerState() != null) {
							Criterion specifiedCustomerState = Expression.eq(
									"forCriteria.customerState", criteria
											.getCustomerState());
							dCriteria.add(Expression.or(specifiedCustomerState,
									customerStateIsNull));
						} else {
							dCriteria.add(customerStateIsNull);
						}

						return dCriteria.getExecutableCriteria(session)
								.uniqueResult();
					}
				});
	}

	@Required
	public void setCriteriaHibernateHelper(
			CriteriaHibernateHelper criteriaHibernateHelper) {
		this.criteriaHibernateHelper = criteriaHibernateHelper;
	}

	void updateRelevanceScore(PolicyRates entity) {
		BitSetValueComputer bitSetValueComputer = new BitSetValueComputer();
		Criteria forCriteria = entity.getForCriteria();
		DealerCriterion dealerCriterion = forCriteria.getDealerCriterion();
		boolean[] bits = new boolean[] {
				dealerCriterion != null && dealerCriterion.getDealer() != null,
				dealerCriterion != null
						&& dealerCriterion.getDealerGroup() != null,
				forCriteria.getClaimType() != null,
				forCriteria.getWarrantyType() != null,
				forCriteria.getProductType() != null };
		forCriteria.setRelevanceScore(bitSetValueComputer.compute(bits));
	}

	public void savePolicyRates(PolicyRates entity) {
		updateRelevanceScore(entity);
		super.save(entity);
	}

	public void updatePolicyRates(PolicyRates entity) {
		updateRelevanceScore(entity);
		super.update(entity);
	}

	public void deletePolicyRates(PolicyRates entity) {
		super.delete(entity);
	}

}
