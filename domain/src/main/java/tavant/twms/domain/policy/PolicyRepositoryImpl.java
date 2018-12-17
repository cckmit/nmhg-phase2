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
package tavant.twms.domain.policy;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class PolicyRepositoryImpl extends HibernateDaoSupport implements PolicyRepository {
    
    private static Logger logger = LogManager.getLogger(PolicyRepositoryImpl.class);
    
    public void create(Policy aNewPolicy) {
        getHibernateTemplate().save(aNewPolicy);
    }

    public void update(Policy aPolicy) {
        getHibernateTemplate().update(aPolicy);
    }
    
    

    public void delete(Policy aPolicy) {
        getHibernateTemplate().delete(aPolicy);
    }

    @SuppressWarnings("unchecked")
    public List<RegisteredPolicy> findPoliciesForInventoryItem(InventoryItem inventoryItem) {
        return getHibernateTemplate().find("select policyInvItemAssignment.policy " +
                        " from PolicyInvItemAssignment policyInvItemAssignment "
                        + " where policyInvItemAssignment.inventoryItem = ?", inventoryItem);
    }
    
    
    
    @SuppressWarnings("unchecked")
    public PageResult<RegisteredPolicy> findAllPolicies(final ListCriteria forCriteria) {
        final StringBuffer entityQuery = new StringBuffer("from Policy policy ");
        if( forCriteria.isFilterCriteriaSpecified() ) {
            entityQuery.append(" where ");
            String criteriaString = forCriteria.getParamterizedFilterCriteria();
            entityQuery.append(criteriaString);
        }
        if( logger.isDebugEnabled() ) {
            logger.debug(" findAllPolicies(ListCriteria) query -> "+entityQuery);
        }
        final PageSpecification pageSpecification = forCriteria.getPageSpecification();
        
        //Get the actual number of rows
        final Long totalRowCount = (Long)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery("select count(*) "+entityQuery);
                addParameters(forCriteria, query);
                return query.uniqueResult();
            }
        });

        //The order by clause would make sense for the fetch.
        if( forCriteria.isSortCriteriaSpecified() ) {
            entityQuery.append(" order by ");
            String sortCriteriaString = forCriteria.getSortCriteriaString();
            entityQuery.append(sortCriteriaString);
        }
        
        
        int pageCount = pageSpecification.convertRowsToPages(totalRowCount);

        if( logger.isInfoEnabled() ) {
            logger.info("findAllPolicies found ["+totalRowCount+"] records. " +
                        "["+pageCount+"] pages of size ["+pageSpecification.getPageSize()+"]");
        }
        
        
        if( totalRowCount > 0  ) {
            List<RegisteredPolicy> rowsInPage = (List<RegisteredPolicy>)getHibernateTemplate().execute(new HibernateCallback()  {
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    Integer from = pageSpecification.offSet();
                    int rowsPerPage = pageSpecification.getPageSize();
                    Query query = session.createQuery(entityQuery.toString());
                    addParameters(forCriteria, query);
                    return (List<RegisteredPolicy>)query
                        .setFirstResult(from)
                        .setMaxResults(rowsPerPage)
                        .list();
                }
            });
            
            return new PageResult<RegisteredPolicy>(rowsInPage,pageSpecification,pageCount);
        }
        List<RegisteredPolicy> emptyList = Collections.emptyList();
        return new PageResult<RegisteredPolicy>(emptyList,pageSpecification,pageCount);
    }

    public Policy findBy(Long id) {
        return (Policy)getHibernateTemplate().get(RegisteredPolicy.class, id);
    }

    /**
     * @param forCriteria
     * @param query
     */
    private void addParameters(final ListCriteria forCriteria, Query query) {
        Map<String, Object> parameterMap = forCriteria.getParameterMap();
        for (Iterator iter = parameterMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            query.setParameter((String)entry.getKey(), entry.getValue());
        }
    }

	@SuppressWarnings("unchecked")
	public List<RegisteredPolicy> findPoliciesForWarranty(final Warranty warranty) {
		return (List<RegisteredPolicy>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										" from RegisteredPolicy policy where policy.warranty.id =:warrantyId ")
								.setParameter("warrantyId", warranty.getId())
								.list();
					}
				});
	}
    
	 
	@SuppressWarnings("unchecked")
	public List<RegisteredPolicy> filterPolicyByServiceProvider(final List<RegisteredPolicy> policyIds,final ServiceProvider serviceProvider){
	
		return (List<RegisteredPolicy>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select regPolicy from RegisteredPolicy regPolicy join regPolicy.policyDefinition pd where regPolicy in(:policyIds) and "+
										"(:serviceProvider in elements(pd.applicableServiceProviders) "+
										"or ( exists(  SELECT specifiedDealerGroup FROM DealerGroup specifiedDealerGroup,DealerGroup dealerGroup join dealerGroup.includedDealers dealerInGroup WHERE "+
										":serviceProvider = dealerInGroup AND specifiedDealerGroup.nodeInfo.treeId = dealerGroup.nodeInfo.treeId "+
    			 						"AND specifiedDealerGroup.nodeInfo.lft <= dealerGroup.nodeInfo.lft AND dealerGroup.nodeInfo.rgt <= specifiedDealerGroup.nodeInfo.rgt and specifiedDealerGroup in elements(pd.applicableDealerGroups))) "+
										"or (pd.applicableServiceProviders IS EMPTY and pd.applicableDealerGroups IS EMPTY))")
    			 						.setParameterList("policyIds",policyIds).setParameter("serviceProvider", serviceProvider)
    			 						.list();
					}
				});
    }

}