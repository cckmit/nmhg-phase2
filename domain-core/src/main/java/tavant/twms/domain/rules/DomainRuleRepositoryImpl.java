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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Mar 1, 2007
 * Time: 7:27:43 PM
 */

package tavant.twms.domain.rules;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomainRuleRepositoryImpl extends
		GenericRepositoryImpl<DomainRule, Long> implements DomainRuleRepository {
	@SuppressWarnings("unchecked")
	public PageResult<DomainRule> findByNameInContext(final String name, final String context, final boolean useRuleGroup,
                                                   final PageSpecification pageSpec) {

		return (PageResult<DomainRule>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria criteria = session.createCriteria(DomainRule.class);
						criteria.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
						addContextConstraint(criteria, context, useRuleGroup);

                        return fetchPage(pageSpec, criteria);
					}

				});
	}

	@SuppressWarnings("unchecked")
	public List<DomainRule> findByNameInContext(final String name, final String context, final boolean useRuleGroup) {

		return (List<DomainRule>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria criteria = session.createCriteria(DomainRule.class);
						criteria.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
						addContextConstraint(criteria, context, useRuleGroup);

                        return criteria.list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<DomainRule> findByContext(final String context, final boolean useRuleGroup) {

		return (List<DomainRule>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
                        Criteria queryCriteria = session.createCriteria(DomainRule.class);
                        queryCriteria.add(Restrictions.ne("status", DomainRuleAudit.INACTIVE));
                        addContextConstraint(queryCriteria, context, useRuleGroup);
                        queryCriteria.setCacheable(true);
						return queryCriteria.list();
					}
				});
	}

	
	@SuppressWarnings("unchecked")
	public List<DomainRule> findProcessorAuthorityRules(final String context, final User processor) {

		return (List<DomainRule>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						String queryStr = "select domainRule from DomainRule domainRule join domainRule.ruleAudits as ruleAudit, " +
								" AssignmentRuleAction as ruleAction join ruleAction.userCluster.includedUsers as user " +
								" where domainRule.context = :context " +
								" and domainRule.status = 'ACTIVE' " +
								" and user = :processor " +
								" and ruleAudit.action = ruleAction.id " +
								" and ruleAudit.id = (select max(ra.id) from DomainRule r join r.ruleAudits ra where r.id=domainRule.id ) ";
						Query query = session.createQuery(queryStr.toString());
						HashMap<String,Object> parameterMap = new HashMap<String,Object>();
						parameterMap.put("processor", processor);
						parameterMap.put("context", context);
						query.setProperties(parameterMap);
						query.setCacheable(true);
						return query.list();
						
						
                        /*Criteria queryCriteria = session.createCriteria(DomainRule.class);
                        queryCriteria.add(Restrictions.ne("status", DomainRuleAudit.INACTIVE));
                        addContextConstraint(queryCriteria, context, useRuleGroup);
						return queryCriteria.list();*/
					}
				});
	}
	
	public PageResult<DomainRule> findAllInContext(String context, boolean useRuleGroup,
                                                   PageSpecification pageSpecification) {

		String query = " from DomainRule domainRule";
		ListCriteria allRulesCriteria = new ListCriteria();
        String filterField = useRuleGroup ? "domainRule.ruleGroup.context" : "domainRule.context";
        allRulesCriteria.addFilterCriteria(filterField, context);
		allRulesCriteria.setPageSpecification(pageSpecification);

		return findPage(query, allRulesCriteria);
	}

	public boolean isQuerySatisfied(final String query) {
		return (Boolean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						Long numResults = (Long) session.createQuery(query)
								.uniqueResult();
						return numResults > 0;
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<DomainRule> findByRuleNumber(final Integer ruleNumber) {
		return (List<DomainRule>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria criteria = session
								.createCriteria(DomainRule.class);
						criteria.add(Restrictions.eq("ruleNumber", ruleNumber));
						return criteria.list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<String> executeDuplicateClaimsQuery(final String query) {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						return session.createQuery(query).list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public PageResult<DomainRule> findAllInContext(String context,ListCriteria listCriteria, boolean useRuleGroup) {
		String query = " from DomainRule domainRule ";
		String filterField = useRuleGroup ? "domainRule.ruleGroup.context" : "domainRule.context";
        listCriteria.addFilterCriteria(filterField, context);
		return findPage(query, listCriteria);
	}

    private void addContextConstraint(Criteria queryCriteria, String context, boolean isRuleGroup) {
        if(isRuleGroup) {
            queryCriteria.createAlias("ruleGroup", "ruleGroup");
            queryCriteria.add(Expression.eq("ruleGroup.context", context));
        } else {
            queryCriteria.add(Expression.eq("context", context));
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<DomainRule> findByContextAndRuleApplicableTo(final String context, final boolean useRuleGroup, final Organization organization) {

        return (List<DomainRule>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Criteria queryCriteria = session.createCriteria(DomainRule.class);
                queryCriteria.add(Restrictions.ne("status", DomainRuleAudit.INACTIVE));
                addContextConstraint(queryCriteria, context, useRuleGroup);
                if (organization.getName().equalsIgnoreCase(AdminConstants.OEM)) {
                    queryCriteria.add(Restrictions.disjunction().add(Expression.eq("ruleApplicable", AdminConstants.NMHG_ONLY))
                            .add(Expression.eq("ruleApplicable", AdminConstants.BOTH)));
                } else {
                    queryCriteria.add(
                            Restrictions.disjunction().add(Expression.eq("ruleApplicable", AdminConstants.DEALER_OWNED))
                                    .add(Expression.eq("ruleApplicable", AdminConstants.BOTH))).add(
                                            Restrictions.disjunction().add(Restrictions.isNull("forDealer.id")).add(Expression.eq("forDealer.id", organization.getId())));
                }
                queryCriteria.setCacheable(true);
                return queryCriteria.list();
            }
        });
    }
    
}
