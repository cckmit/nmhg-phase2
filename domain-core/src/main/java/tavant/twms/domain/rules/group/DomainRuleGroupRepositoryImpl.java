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

package tavant.twms.domain.rules.group;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.orm.hibernate3.HibernateCallback;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import tavant.twms.annotations.common.DisableDeActivation;
import tavant.twms.domain.common.AdminConstants;

public class DomainRuleGroupRepositoryImpl extends GenericRepositoryImpl<DomainRuleGroup, Long> implements
        DomainRuleGroupRepository {
	
	/**
	 * Fetches both 'ACTIVE' & "DEACTIVATED" domain rule groups
	 */
	@DisableDeActivation
	@SuppressWarnings("unchecked")
    public PageResult<DomainRuleGroup> listAllRuleGroupsByContext(final String context, final ListCriteria listCriteria) {
        return (PageResult<DomainRuleGroup>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	listCriteria.addFilterCriteria("context", context);
                return findPage("from DomainRuleGroup group", listCriteria);
            }
        });
    }
	
    @SuppressWarnings("unchecked")
    public PageResult<DomainRuleGroup> findRuleGroupsByContext(final String context, final ListCriteria listCriteria) {
        return (PageResult<DomainRuleGroup>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return fetchPage(getRuleGroupByContextCriteria(session, context), listCriteria, null);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<DomainRuleGroup> findRuleGroupsByContextOrderedByPriority(final String context) {
        return (List<DomainRuleGroup>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return getRuleGroupByContextCriteria(session, context)
                        .addOrder(Order.asc("priority"))
                        .list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<DomainRuleGroup> findRuleGroupsByContext(final String context) {
        return (List<DomainRuleGroup>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return getRuleGroupByContextCriteria(session, context).setCacheable(true).list();
            }
        });
    }

    public Boolean doesAtLeastOneRuleGroupExistForContext(final String context) {
        return (Boolean) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Object numRuleGroupsInContext =
                        session.createQuery("select count(*) from DomainRuleGroup where context = :context")
                        .setParameter("context", context)
                        .uniqueResult();
                return ((Long) numRuleGroupsInContext) > 0;
            }
        });
    }

    public Long findNextAvailableRuleGroupPriorityForContext(String context) {
        return (Long) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Long maxPriority = (Long)
                        session.createQuery("select max(priority) from DomainRuleGroup").uniqueResult();
                return (maxPriority == null) ? 1 : maxPriority + 1;
            }
        });
    }

    public Long findNextAvailableRulePriorityForRuleGroup(final Long ruleGroupId) {
        return (Long) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        Long maxRuleGroupPriority = (Long)
                                session.createQuery("select max(rule.priority) from DomainRuleGroup ruleGroup " +
                                        "join ruleGroup.rules rule where ruleGroup.id = :id")
                                        .setParameter("id", ruleGroupId)
                                        .uniqueResult();
                        return maxRuleGroupPriority == null ? 1 : maxRuleGroupPriority + 1;
                    }
                });
    }

    private Criteria getRuleGroupByContextCriteria(Session session, String context) {    	
        return session.createCriteria(DomainRuleGroup.class)       
                .add(Expression.eq("context", context))
                .addOrder(Order.asc("priority"));
    }

	@SuppressWarnings("deprecation")
	public void updateRuleGroup(final DomainRuleGroup ruleGroup) {
		getHibernateTemplate().update(ruleGroup);
   		getSessionFactory().evictCollection("tavant.twms.domain.rules.group.DomainRuleGroup.rules");
	}
	
	@SuppressWarnings("unchecked")
	public List<DomainRuleGroup> findActiveRuleGroupsForContext(final String context){

        return (List<DomainRuleGroup>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	Criteria criteria = session.createCriteria(DomainRuleGroup.class);
            	criteria.add(Expression.eq("context", context));
            	criteria.add(Expression.eq("status", "ACTIVE"));
            	criteria.addOrder(Order.asc("priority"));
                return criteria.setCacheable(true).list();
            }
        });
    
	}
}