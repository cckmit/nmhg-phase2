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
package tavant.twms.domain.orgmodel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class UserClusterRepositoryImpl extends GenericRepositoryImpl<UserCluster, Long> implements UserClusterRepository {

	public UserCluster findByNameAndPurpose(String name, String purpose) {
		String query = "select uc from UserCluster uc where uc.name =:name and uc.scheme = (select userScheme from UserScheme userScheme join userScheme.purposes as purpose " +
        " where purpose.name=:purpose and userScheme.businessUnitInfo = uc.businessUnitInfo )";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("name",name);
        params.put("purpose",purpose);
        return findUniqueUsingQuery(query, params);
	}

	public UserCluster findClusterContainingUser(User user, UserScheme scheme) {
		String query = "select uc from UserCluster uc join uc.includedUsers as user where user=:aUser and uc.scheme =:scheme ";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("scheme",scheme);
        params.put("aUser", user);
        return findUniqueUsingQuery(query, params);
	}

	public List<UserCluster> findClustersByNameAndDescription(UserScheme scheme, String name, String description) {
		String query = "select uc from UserCluster uc where uc.scheme =:scheme and upper(uc.name) like :nameParam and upper(uc.description) like :descriptionParam order by uc.name";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("scheme",scheme);        
        params.put("nameParam", name.toUpperCase() + "%");
        params.put("descriptionParam", description.toUpperCase() + "%");      
        return findUsingQuery(query, params);
	}

	@SuppressWarnings("unchecked")
	public PageResult<UserCluster> findPage(final ListCriteria listCriteria, final UserScheme userScheme) {
		PageSpecification pageSpecification = listCriteria.getPageSpecification();
        final StringBuffer countQuery = new StringBuffer(" select count(*) ");
        
        final StringBuffer fromAndWhereClause = new StringBuffer();
        final String fromClause = "from UserCluster userCluster where userCluster.scheme =:scheme";
        fromAndWhereClause.append(fromClause);
        
        if( listCriteria.isFilterCriteriaSpecified() ) {
            fromAndWhereClause.append(" and ");
            fromAndWhereClause.append(listCriteria.getParamterizedFilterCriteria());
        }
        
        countQuery.append(fromAndWhereClause);
        
        if( logger.isDebugEnabled() ) {
            logger.debug("findPage("+fromClause+",listCriteria) count query is ["+countQuery+"]");
        }
        
        Long numberOfRows = (Long)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery(countQuery.toString());
                query.setParameter("scheme", userScheme);
                for(Map.Entry<String,Object> parameterSpecification : listCriteria.getParameterMap().entrySet() ) {
                    String name = parameterSpecification.getKey();
                    Object value = parameterSpecification.getValue();
                    query.setParameter(name,value);
                }
                return query.uniqueResult();
            }
        });
        Integer numberOfPages = pageSpecification.convertRowsToPages(numberOfRows);                

        List<UserCluster> rowsInPage = new ArrayList<UserCluster>();
        PageResult<UserCluster> page = new PageResult<UserCluster>(rowsInPage,pageSpecification,numberOfPages);                

        if( logger.isDebugEnabled() ) {
            logger.debug(" fetchPage("+pageSpecification+",...,...) found (rows="+numberOfRows+",pages="+numberOfPages+")");
        }
        
        
        final Integer pageOffset = pageSpecification.offSet();
        if( numberOfRows > 0 && numberOfRows > pageOffset) {
            
            final Integer pageSize = pageSpecification.getPageSize();
            rowsInPage = (List<UserCluster>)getHibernateTemplate().execute(new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    StringBuffer filterAndSort = new StringBuffer(fromAndWhereClause);
                                        
                    if( listCriteria.isSortCriteriaSpecified() ) {
                        filterAndSort.append( " order by ");
                        filterAndSort.append( listCriteria.getSortCriteriaString() );
                    }
                    
                    if( logger.isDebugEnabled() ) {
                        logger.debug(" Unpaginated query for findPage("+fromClause+",listCriteria) [ "+filterAndSort+" ]");
                    }
                    
                    Query query = session.createQuery(filterAndSort.toString());
                    for(Map.Entry<String,Object> parameterSpecification : listCriteria.getParameterMap().entrySet() ) {
                        String name = parameterSpecification.getKey();
                        Object value = parameterSpecification.getValue();
                        query.setParameter(name,value);
                    }
                    
                    query.setParameter("scheme", userScheme);
                    return query
                        .setFirstResult(pageOffset)
                        .setMaxResults(pageSize)
                        .list();
                }
            });
            page = new PageResult<UserCluster>(rowsInPage,pageSpecification,numberOfPages);
        }
        return page;
	}

	public UserCluster findUserClusterByName(String name, UserScheme userScheme) {
		String query = "select uc from UserCluster uc where uc.scheme =:scheme and uc.name=:name";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("scheme",userScheme);
        params.put("name",name);
        return findUniqueUsingQuery(query, params);
	}

	public List<UserCluster> findUserClustersFromScheme(UserScheme userScheme) {
		String query = "select uc from UserCluster uc where uc.scheme =:scheme";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("scheme",userScheme);
        return findUsingQuery(query, params);
	}

    @SuppressWarnings("unchecked")
    public List<UserCluster> findUserClustersByPurpose(String purpose) {
        return getClusterByPurposeCriteria(purpose).list();
	}

    public boolean doUserClustersExistForPurpose(String purpose) {
        Criteria clusterByPurposeCriteria =
                getClusterByPurposeCriteria(purpose);
        clusterByPurposeCriteria.setProjection(Projections.rowCount());

        long rowCount = (Long) clusterByPurposeCriteria.uniqueResult();

        return rowCount > 0;
    }

    @SuppressWarnings("unchecked")
	protected Criteria getClusterByPurposeCriteria(final String purpose) {
		return (Criteria) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(UserCluster.class);
				criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
				criteria.createAlias("scheme", "scheme");
				criteria.createAlias("scheme.purposes", "purpose");

				criteria.add(Expression.eq("purpose.name", purpose));

				return criteria;
			}

		});

	}
}
