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
package tavant.twms.domain.rules;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;


/**
 * @author radhakrishnan.j
 */
public class DomainPredicateRepositoryImpl
        extends GenericRepositoryImpl<DomainPredicate, Long>
        implements DomainPredicateRepository {

    private RuleSerializer xmlSerializer;
    private CriteriaHelper criteriaHelper;
    private LovRepository lovRepository;

    public LovRepository getLovRepository() {
		return lovRepository;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public void setXmlSerializer(RuleSerializer xmlSerializer) {
        this.xmlSerializer = xmlSerializer;
    }

    @SuppressWarnings("unchecked")
    public PageResult<DomainPredicate> findByNameInContext(
            final String name, final String context,
            final PageSpecification pageSpec) {
        return (PageResult<DomainPredicate>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        Criteria queryCriteria =
                                session.createCriteria(DomainPredicate.class);
                        queryCriteria.add(Restrictions.like("name", name,
                                MatchMode.START));
                        queryCriteria.add(Restrictions.eq("context", context));
                        return fetchPage(pageSpec, queryCriteria);
                    }

                });
    }

    public PageResult<DomainPredicate> findAllInContext(
            String context, PageSpecification pageSpecification) {
        String query = " from DomainPredicate domainPredicate ";

        ListCriteria listCriteria = new ListCriteria();
        listCriteria.addFilterCriteria("domainPredicate.context", context);
        listCriteria.setPageSpecification(pageSpecification);

        return findPage(query, listCriteria);
    }

    @SuppressWarnings("unchecked")
    public List<DomainPredicate> findByName(final String name) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {

            public Object doInHibernate(Session session) {
                return getByNameCriteria(session, name).list();
            }

        });
    }

    public List<DomainPredicate> findByNameInContext(String name,
                                                     String context) {
        String query = " from DomainPredicate dp where upper(dp.name) like :name " +
                "and dp.context=:context";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "%" + name.toUpperCase() + "%");
        params.put("context", context);
        return findUsingQuery(query, params);
    }

    public PageResult<DomainPredicate> findAll(
            final String context, PageSpecification pageSpecification) {
        ListCriteria listCriteria = new ListCriteria();
        listCriteria.setPageSpecification(pageSpecification);
        listCriteria.addFilterCriteria("dp.context", context);
        return findPage(" from DomainPredicate dp ", listCriteria);
    }

    public List<DomainPredicate> findClashingPredicates(Long userId,String context,DomainPredicate newPredicate) {
		StringBuffer queryString = new StringBuffer("select dp from SavedQuery sq " +
				"join sq.createdBy cb " +
				"join sq.domainPredicate dp " +
				"where cb.id=:id and dp.context=:context and sq.temporary=:temporary and " +
				"(dp.name=:name)");
		Map<String, Object> params = new HashMap<String, Object>();

		if(newPredicate.getId()!=null)
		{
			queryString.append("and dp.id!=:domainPredicateId");
			params.put("domainPredicateId", newPredicate.getId());
		}

		params.put("context", context);
		params.put("id", userId);
		params.put("name", newPredicate.getName());
		params.put("temporary", Boolean.FALSE);
        return findUsingQuery(queryString.toString(), params);
	}

    @SuppressWarnings("unchecked")
    public List<DomainPredicate> findClashingPredicates(
            final DomainPredicate domainPredicate) {

        return getHibernateTemplate().executeFind(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException {

                        Criteria duplicatePredicateCriteria =
                                session.createCriteria(DomainPredicate.class);

                        // If this is an existing predicate, make sure that it
                        // doesn't get considered as a clash with itself!
                        DomainPredicateRepositoryImpl.this.criteriaHelper.neIfNotNull(duplicatePredicateCriteria,
                                "id", domainPredicate.getId());

                        duplicatePredicateCriteria.add(
                                Restrictions.eq("name", domainPredicate.getName()));

                        duplicatePredicateCriteria.add(
                                Restrictions.eq("context", domainPredicate.getContext()));

                        return duplicatePredicateCriteria.list();
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public List<DomainRule> findRulesUsingPredicate(
            DomainPredicate domainPredicate) {

        return getHibernateTemplate().find("from DomainRule rule " +
                "where rule.predicate = ?", domainPredicate);
    }

    @SuppressWarnings("unchecked")
    public Integer findMaxRuleNumberForContext() {
    	return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("select max(ruleNumber) from DomainRule rule").uniqueResult();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<DomainPredicate> findPredicatesReferringToPredicate(
            DomainPredicate domainPredicate) {

        String query = "select dp from DomainPredicate dp join " +
                "dp.refersToPredicates rtp where ? in rtp";

        return getHibernateTemplate().find(query, domainPredicate);
    }

    public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
        this.criteriaHelper = criteriaHelper;
    }

    @SuppressWarnings("unchecked")
    public PageResult<DomainPredicate> findAllNonSearchPredicates(
            final ListCriteria criteria) {

        return (PageResult<DomainPredicate>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        String queryWithoutSelect = "from DomainPredicate where upper(context) not like :searchContext ";
						if (criteria.isFilterCriteriaSpecified()) {
							queryWithoutSelect = queryWithoutSelect + " and ("
									+ criteria.getParamterizedFilterCriteria()
									+ " )";
						}
						if (criteria.isSortCriteriaSpecified()) {
							queryWithoutSelect = queryWithoutSelect
									+ " order by "
									+ criteria.getSortCriteriaString();
						}
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("searchContext", "%SEARCHES%");
						params.putAll(criteria.getParameterMap());
						PageResult<DomainPredicate> page = findPageUsingQuery(
								queryWithoutSelect, "id", criteria
										.getPageSpecification(), params);
						return page;

                    }
                });
    }
    
    @SuppressWarnings("unchecked")
    public List<DomainPredicate> findNonSearchPredicatesByName(final String name,final boolean includeSystemConditions) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {

            public Object doInHibernate(Session session) {
                Criteria byNameCriteria = getByNameCriteria(session, name);
                if(!includeSystemConditions){
                	byNameCriteria.add(Expression.or(Expression.eq("systemDefinedConditionName",""),Expression.isNull("systemDefinedConditionName")));
                }
                addNonSearchPredicateConstraint(byNameCriteria);
                return byNameCriteria.list();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public List<DomainPredicate> findNonSearchPredicatesByName(final String name, final String context,final boolean includeSystemConditions) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {

            public Object doInHibernate(Session session) {
                Criteria byNameCriteria = getByNameCriteria(session, name);
                if(!includeSystemConditions){
                	byNameCriteria.add(Expression.or(Expression.eq("systemDefinedConditionName",""),Expression.isNull("systemDefinedConditionName")));
                }
                
                addNonSearchPredicateConstraint(byNameCriteria);
                byNameCriteria.add(Restrictions.eq("context", context));
                return byNameCriteria.list();
            }
        });
    }

    private Criteria getByNameCriteria(Session session,
            final String name) {
        Criteria byNameCriteria = session.createCriteria(DomainPredicate.class);
        if(StringUtils.hasText(name)){
        	byNameCriteria.add(Restrictions.ilike("name", name, MatchMode.START));
        }

        return byNameCriteria;
    }

    private void addNonSearchPredicateConstraint(Criteria queryCriteria) {
        Criterion notSearchCriterion = Restrictions.not(
                Restrictions.ilike("context", "Searches", MatchMode.END));
               queryCriteria.add(notSearchCriterion);
    }

	public List<ListOfValues> findAllDescription(String classname) {
		return lovRepository.findAllActive(classname);
	}

    @SuppressWarnings("unchecked")
    public PageResult<DomainPredicate> findAllNonSearchPredicates(final ListCriteria criteria, final String context) {

        return (PageResult<DomainPredicate>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        String queryWithoutSelect = "from DomainPredicate where context = :searchContext ";
                        if (criteria.isFilterCriteriaSpecified()) {
                            queryWithoutSelect = queryWithoutSelect + " and ("
                                    + criteria.getParamterizedFilterCriteria()
                                    + " )";
                        }
                        if (criteria.isSortCriteriaSpecified()) {
                            queryWithoutSelect = queryWithoutSelect
                                    + " order by "
                                    + criteria.getSortCriteriaString();
                        }
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("searchContext", context);
                        params.putAll(criteria.getParameterMap());
                        PageResult<DomainPredicate> page = findPageUsingQuery(
                                queryWithoutSelect, "id", criteria
                                .getPageSpecification(), params);
                        return page;
                    }
                });
    }

	@SuppressWarnings("unchecked")
	public PageResult<DomainPredicate> findAllNonSearchPredicates(
			final ListCriteria criteria, final List<String> contexts) {
		 return (PageResult<DomainPredicate>) getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) {
	                        String queryWithoutSelect = "from DomainPredicate where context in (:searchContext)";
	                        if (criteria.isFilterCriteriaSpecified()) {
	                            queryWithoutSelect = queryWithoutSelect + " and ("
	                                    + criteria.getParamterizedFilterCriteria()
	                                    + " )";
	                        }
	                        if (criteria.isSortCriteriaSpecified()) {
	                            queryWithoutSelect = queryWithoutSelect
	                                    + " order by "
	                                    + criteria.getSortCriteriaString();
	                        }
	                        Map<String, Object> params = new HashMap<String, Object>();
	                        params.put("searchContext", contexts);
	                        params.putAll(criteria.getParameterMap());
	                        PageResult<DomainPredicate> page = findPageUsingQuery(
	                                queryWithoutSelect, "id", criteria
	                                .getPageSpecification(), params);
	                        return page;
	                    }
	                });
	}
	
	@SuppressWarnings("unchecked")
    public List<DomainPredicate> findNonSearchPredicatesByNameAndContexts(final String name, final List<String> contexts,final boolean includeSystemConditions) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {

            public Object doInHibernate(Session session) {
                Criteria byNameCriteria = getByNameCriteria(session, name);
                if(!includeSystemConditions){
                	byNameCriteria.add(Expression.or(Expression.eq("systemDefinedConditionName",""),Expression.isNull("systemDefinedConditionName")));
                }
                
                addNonSearchPredicateConstraint(byNameCriteria);
                byNameCriteria.add(Restrictions.in("context", contexts));
                return byNameCriteria.list();
            }
        });
    }
}
