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
package tavant.twms.infra;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.SecurityHelper;

import ognl.Ognl;
import ognl.OgnlException;


/**
 * @author radhakrishnan.j
 * 
 */
public class GenericRepositoryImpl<T, ID extends Serializable> extends HibernateDaoSupport
    implements GenericRepository<T, ID> {
    
    protected static Logger logger = LogManager.getLogger(GenericRepositoryImpl.class);
    private Class<T> entityType;
    private String fromClauseForEntity;
    private SecurityHelper securityHelper;
    private CriteriaHelper criteriaHelper;

    public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
    public GenericRepositoryImpl() {
        super();
        this.entityType = getEntityType();
        getFromClause();
    }

    public void setEntityType(Class<T> entityType) {
		this.entityType = entityType;
		getFromClause();
	}

	protected void getFromClause() {
		if (this.entityType!=null) {
			this.fromClauseForEntity = " from " + this.entityType.getSimpleName();
		}
	}

	@SuppressWarnings("unchecked")
    public List<T> findByIds(Collection<ID> collectionOfIds) {
        final String propertyNameOfId = "id";
        return findByIds(propertyNameOfId, collectionOfIds);
    }



    /**
     * @param propertyNameOfId
     * @param collectionOfIds
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<T> findByIds(final String propertyNameOfId, Collection<ID> collectionOfIds) {
        final Set<ID> idSet = new HashSet<ID>();
        idSet.addAll(collectionOfIds);
        return (List<T>)getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria queryCriteria = session.createCriteria(GenericRepositoryImpl.this.entityType,"entity");
                queryCriteria.add( Restrictions.in(propertyNameOfId, idSet) );
                return queryCriteria.list();
            }
        });
    }



    public void delete(T entity) {
        getHibernateTemplate().delete(entity);
    }

    public void deleteAll(List<T> entitiesToDelete) {
        for (T entityToDelete: entitiesToDelete) {
            getHibernateTemplate().delete(entityToDelete);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(GenericRepositoryImpl.this.entityType);
                return criteria.list();
            }
        });
    }

    public PageResult<T> findAll(PageSpecification pageSpecification) {
        ListCriteria listCriteria = new ListCriteria();
        listCriteria.setPageSpecification(pageSpecification);
        return findPage(this.fromClauseForEntity, listCriteria);
    }

    @SuppressWarnings("unchecked")
    public T findById(ID id) {
        return (T)getHibernateTemplate().get(this.entityType,id);
    }

    public void save(T entity) {
        getHibernateTemplate().save(entity);
    }

    public void saveAll(List<T> entitiesToSave) {
        for (T entityToSave : entitiesToSave) {
            getHibernateTemplate().save(entityToSave);
        }
    }

    public void update(T entity) {
        getHibernateTemplate().update(entity);
    }

    public void updateAll(List<T> entitiesToUpdate) {
        for (T entityToUpdate : entitiesToUpdate) {
            getHibernateTemplate().update(entityToUpdate);
        }
    }

    @SuppressWarnings("unchecked")
    protected final Class<T> getEntityType() {
        Class<? extends Object> thisClass = getClass();
		Type genericSuperclass = thisClass.getGenericSuperclass();
		if( genericSuperclass instanceof ParameterizedType ) {
			Type[] argumentTypes = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
	        Class<T> entityBeanType = (Class<T>)argumentTypes[0];
	        return entityBeanType;
		} else {
			return null;
		}
    }

    @SuppressWarnings("unchecked")
    protected List<T> findUsingNamedQuery(final String queryName,final Map<String,Object> params) {
        return (List<T>)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(queryName);
                query.setProperties(params);
                return query.list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected T findUniqueUsingNamedQuery(final String queryName,final Map<String,Object> params) {
        return (T)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                
                Query query = session.getNamedQuery(queryName);
                query.setProperties(params);
                return query.uniqueResult();
            }
        });
    }

    
    @SuppressWarnings("unchecked")
    protected List<T> findUsingQuery(final String queryString,final Map<String,Object> params) {
        return (List<T>)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery(queryString);
                query.setProperties(params);
                return query.list();
            }
        });
    }
    
    
    @SuppressWarnings("unchecked")
    protected T findUniqueUsingQuery(final String queryString,final Map<String,Object> params) {
        return (T)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery(queryString);
                query.setProperties(params);
                return query.uniqueResult();
            }
        });
    }
    
    /**
     * @param listCriteria
     * @return
     */
    public PageResult<T> findPageWithSpecificPageSize(final String fromClause,final ListCriteria listCriteria, final int pageSize) {
        PageSpecification pageSpecification = listCriteria.getPageSpecification().setPageSize(pageSize);
        final StringBuffer fromAndWhereClause = new StringBuffer();
        fromAndWhereClause.append(fromClause);
        if( listCriteria.isFilterCriteriaSpecified() ) {
            fromAndWhereClause.append(" where ");
            String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
			fromAndWhereClause.append(paramterizedFilterCriteria);
        }
        final String queryWithoutSelect = fromAndWhereClause.toString();
        final String sortClause = listCriteria.getSortCriteriaString();
        final Map<String, Object> parameterMap = listCriteria.getParameterMap();
        return findPageUsingQuery(queryWithoutSelect, sortClause, pageSpecification,parameterMap);
    }

    /**
     * @param listCriteria
     * @return
     */
    public PageResult<T> findPage(final String fromClause,final ListCriteria listCriteria) {
        PageSpecification pageSpecification = listCriteria.getPageSpecification();
        final StringBuffer fromAndWhereClause = new StringBuffer();
        fromAndWhereClause.append(fromClause);
        if( listCriteria.isFilterCriteriaSpecified() ) {
            fromAndWhereClause.append(" where ");
            String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
			fromAndWhereClause.append(paramterizedFilterCriteria);
        }
        final String queryWithoutSelect = fromAndWhereClause.toString();
        final String sortClause = listCriteria.getSortCriteriaString();
        final Map<String, Object> parameterMap = listCriteria.getParameterMap();
        return findPageUsingQuery(queryWithoutSelect, sortClause, pageSpecification,parameterMap);
    }

    public PageResult<T> findPage(String selectClause, String fromClause, ListCriteria listCriteria) {
        final StringBuilder queryWithoutSelect = new StringBuilder(fromClause);
        if (listCriteria.isFilterCriteriaSpecified()) {
            queryWithoutSelect.append(" where ");
            String parameterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
            queryWithoutSelect.append(parameterizedFilterCriteria);
        }
        return findPageUsingQuery(queryWithoutSelect.toString(), listCriteria.getSortCriteriaString(), selectClause, listCriteria.getPageSpecification(), new QueryParameters(listCriteria.getParameterMap()));
    }

    @SuppressWarnings("unchecked")
    protected PageResult<T> findPageUsingQuery(final String queryWithoutSelect, final String orderByClause, PageSpecification pageSpecification,final Map<String, Object> parameterMap) {
    	return findPageUsingQuery(queryWithoutSelect, orderByClause,null, pageSpecification, new QueryParameters(parameterMap));
    }
    
    //@SuppressWarnings("unchecked")
    /*protected PageResult<T> findPageUsingQuery(final String queryWithoutSelect, final String orderByClause,final String selectClause, PageSpecification pageSpecification,final List<QueryParameter> positionalParameters,final Map<String,Object> namedParameters) {
    	return findPageUsingQuery(queryWithoutSelect, orderByClause,selectClause, pageSpecification, (Object)positionalParameters,namedParameters);
    }*/

    @SuppressWarnings("unchecked")
	public Long findSizeUsingQuery(final String queryWithoutSelect,final Map<String, Object> parameters) {
    	final StringBuffer countQuery = new StringBuffer(" select count(*) ");
        countQuery.append(queryWithoutSelect);
        
        Long numberOfRows = (Long)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(countQuery.toString());
				populateQueryParams(query, new QueryParameters(parameters));
				return query.uniqueResult();
				
            }
        });
    	return numberOfRows;
    }
    
    @SuppressWarnings("unchecked")
	public PageResult<T> findPageUsingQuery(final String queryWithoutSelect, final String orderByClause,final String selectClause,PageSpecification pageSpecification,final QueryParameters parameters) {
		final StringBuffer countQuery = new StringBuffer(" select count(*) ");
        countQuery.append(queryWithoutSelect);
        
        Long numberOfRows = (Long)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(countQuery.toString());
				//query.setProperties(parameterMap);
				populateQueryParams(query, parameters);
				return query.uniqueResult();
				
            }
        });
        Integer numberOfPages = pageSpecification.convertRowsToPages(numberOfRows);

        List<T> rowsInPage = new ArrayList<T>();
        PageResult<T> page = new PageResult<T>(rowsInPage,pageSpecification,numberOfPages);

        StringBuffer filterAndSort = new StringBuffer();
        if(selectClause!=null && !("".equals(selectClause.trim()))) {
			filterAndSort.append(selectClause);
		}
        filterAndSort.append(queryWithoutSelect);
        if( orderByClause!=null && orderByClause.trim().length() > 0 ) {
            filterAndSort.append( " order by ");
            filterAndSort.append( orderByClause );
        }
        //The below code needs to be modified , need to find a better way to do it. - TO DO
        if(filterAndSort.toString().startsWith("from CampaignNotification campaignNotification join campaignNotification.campaign campaign join"))
        filterAndSort = new StringBuffer("select campaignNotification ").append(filterAndSort);
        final String finalQuery = filterAndSort.toString();
        //if(pageSpecification.getPageSize()%500 == 0){
        //	pageSpecification.setPageNumber(0);
        //}
        
        final Integer pageOffset = pageSpecification.offSet();
        if( numberOfRows > 0 && numberOfRows > pageOffset) {            
            final Integer pageSize = pageSpecification.getPageSize();
            rowsInPage = (List<T>)getHibernateTemplate().execute(new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query = session.createQuery(finalQuery);
                    populateQueryParams(query, parameters);
                    //query.setProperties(parameterMap);                    
                    return query
                        .setFirstResult(pageOffset)
                        .setMaxResults(pageSize)
                        .list();
                }
            });
            page = new PageResult<T>(rowsInPage,pageSpecification,numberOfPages);
        }
        return page;
	}
    
    /**
     * @param listCriteria
     * @param queryCriteria
     * @return
     */
    @SuppressWarnings("unchecked")
    public PageResult<T> fetchPage(Criteria queryCriteria, ListCriteria listCriteria,
                                   List<String> alreadyAddedAliases) {
        List<String> aliasCache = (alreadyAddedAliases == null) ? new ArrayList<String>(10) : alreadyAddedAliases;
        addSortingAndFilteringRestrictions(queryCriteria, listCriteria, aliasCache);

        PageSpecification pageSpecification = listCriteria.getPageSpecification();
        queryCriteria.setProjection(Projections.rowCount());
        Long numberOfRows = (Long) queryCriteria.uniqueResult();
        Integer numberOfPages = pageSpecification.convertRowsToPages(numberOfRows);

        List<T> rowsInPage = new ArrayList<T>();
        PageResult<T> page = new PageResult<T>(rowsInPage, pageSpecification, numberOfPages);

        Integer pageOffset = pageSpecification.offSet();
        
        if (numberOfRows > 0 && numberOfRows > pageOffset) {
            Integer pageSize = pageSpecification.getPageSize();
            rowsInPage = (List<T>) queryCriteria
                    .setProjection(null)
                    .setResultTransformer(Criteria.ROOT_ENTITY)
                    .setFirstResult(pageOffset)
                    .setMaxResults(pageSize)
                    .list();

            page = new PageResult<T>(rowsInPage, pageSpecification, numberOfPages);
        }
        return page;
    }

    private void addSortingAndFilteringRestrictions(Criteria queryCriteria, ListCriteria listCriteria,
                                                    List<String> alreadyAddedAliases) {
        // Add sorting options.
        for (Map.Entry<String, String> sortOption : listCriteria.getSortCriteria().entrySet()) {
            String propertyName = sortOption.getKey();

            Order sortOrder = (SORT_ASC.equalsIgnoreCase(sortOption.getValue())) ?
                    Order.asc(propertyName) : Order.desc(propertyName);

            criteriaHelper.processNestedAssociations(queryCriteria, propertyName, Criteria.LEFT_JOIN,
                    alreadyAddedAliases);
            queryCriteria.addOrder(sortOrder);
        }

        // Add filtering options.
        for (Map.Entry<String, String> filterOption : listCriteria.getFilterCriteria().entrySet()) {
            String propertyName = filterOption.getKey();
            criteriaHelper.processNestedAssociations(queryCriteria, propertyName, Criteria.LEFT_JOIN,
                    alreadyAddedAliases);

            if (propertyName.endsWith("Date")) {
                criteriaHelper.dateLike(queryCriteria, propertyName, filterOption.getValue());
            } else {
                criteriaHelper.likeIfNotNull(queryCriteria, propertyName, filterOption.getValue(), MatchMode.START);
            }
        }
    }

    /**
     * @param queryCriteria
     * @return
     */
    @SuppressWarnings("unchecked")
    protected PageResult<T> fetchPage(
            PageSpecification pageSpecification,
            Criteria queryCriteria
            ) {
        queryCriteria.setProjection(Projections.rowCount());
        Long numberOfRows = (Long)queryCriteria.uniqueResult();
        Integer numberOfPages = pageSpecification.convertRowsToPages(numberOfRows);

        List<T> rowsInPage = new ArrayList<T>();
        PageResult<T> page = new PageResult<T>(rowsInPage,pageSpecification,numberOfPages);

        Integer pageOffset = pageSpecification.offSet();
        if( numberOfRows > 0 && numberOfRows > pageOffset) {
            Integer pageSize = pageSpecification.getPageSize();
            rowsInPage = queryCriteria.setProjection(null)
                .setFirstResult(pageOffset)
                .setMaxResults(pageSize)
                .list();
            
            page = new PageResult<T>(rowsInPage,pageSpecification,numberOfPages);
        }
        return page;
    }

    @SuppressWarnings("unchecked")
    public List<T> findEntitiesThatMatchPropertyValues(final Set<String> properties, final T entity) {
        return (List<T>)getHibernateTemplate().execute( new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteriaQuery = session.createCriteria(entity.getClass());
                
                for(String property : properties ) {
                    Object value = null;
                    try {
                        value = Ognl.getValue(property,entity);
                    } catch (OgnlException e) {
                        logger.error("Failed to evaluate property ["+property+"] on entity ["+entity+"]",e);
                    } finally {
                        criteriaQuery.add( value!=null ? Restrictions.eq(property,value) : Restrictions.isNull(property) );
                    }
                }
                
                return criteriaQuery.list();
            }
            
        });
    }

    public List<T> findEntitiesThatMatchPropertyValue(String property, T entity) {
        Set<String> properties = new HashSet<String>();
        properties.add(property);
        return findEntitiesThatMatchPropertyValues(properties, entity);
    }
    
    protected void populateQueryParams(Query query,QueryParameters parameters){
    	List<TypedQueryParameter> positionalParameters = parameters.getPositionalParameters();
    	Map<String,Object> namedParameters=parameters.getNamedParameters();
    	//positional parameters should be set before named parameters.
    	if(positionalParameters!=null && !positionalParameters.isEmpty())
    	{
    		List<TypedQueryParameter> paramsList=positionalParameters;
    		int i=0;
    		for(TypedQueryParameter param:paramsList) {
				query.setParameter(i++,param.getValue(),param.getType());
			}
    		
    	}
    	if(namedParameters!=null && !namedParameters.isEmpty()){
    		Set<Map.Entry<String, Object>> entrySet=namedParameters.entrySet();
    		for(Map.Entry<String, Object> entry:entrySet){
    			if(entry.getValue() instanceof TypedQueryParameter){
    				TypedQueryParameter qp=(TypedQueryParameter)entry.getValue();
    				query.setParameter(entry.getKey(), qp.getValue(),qp.getType());
    			}else if(entry.getValue() instanceof Collection){
    				query.setParameterList(entry.getKey(), (Collection)entry.getValue());
    			}else{
    				query.setParameter(entry.getKey(), entry.getValue());
    			}
    		}
    		//query.setProperties(namedParameters);
    	}
    }
    
    @SuppressWarnings("unchecked")
	public PageResult<T> findPageUsingQueryForDistinctItems(final String queryWithoutSelect, final String orderByClause,
			final String selectClause,PageSpecification pageSpecification,
			final QueryParameters parameters,final String distinctClause) {
		final StringBuffer countQuery = new StringBuffer(" select count( "+distinctClause+" ) ");
        countQuery.append(queryWithoutSelect);
        
        Long numberOfRows = (Long)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(countQuery.toString());
				//query.setProperties(parameterMap);
				populateQueryParams(query, parameters);
				return query.uniqueResult();
				
            }
        });
        Integer numberOfPages = pageSpecification.convertRowsToPages(numberOfRows);

        List<T> rowsInPage = new ArrayList<T>();
        PageResult<T> page = new PageResult<T>(rowsInPage,pageSpecification,numberOfPages);

        StringBuffer filterAndSort = new StringBuffer();
        if(selectClause!=null && !("".equals(selectClause.trim()))) {
			filterAndSort.append(selectClause);
		}
        filterAndSort.append(queryWithoutSelect);
        if( orderByClause!=null && orderByClause.trim().length() > 0 ) {
            filterAndSort.append( " order by ");
            filterAndSort.append( orderByClause );
        }
        final String finalQuery = filterAndSort.toString();
        
        //if(pageSpecification.getPageSize()%500 == 0){
        //	pageSpecification.setPageNumber(0);
        //}
        
        final Integer pageOffset = pageSpecification.offSet();
        if( numberOfRows > 0 && numberOfRows > pageOffset) {
            
            final Integer pageSize = pageSpecification.getPageSize();
            rowsInPage = (List<T>)getHibernateTemplate().execute(new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query = session.createQuery(finalQuery);
                    populateQueryParams(query, parameters);
                    //query.setProperties(parameterMap);
                    return query
                        .setFirstResult(pageOffset)
                        .setMaxResults(pageSize)
                        .list();
                }
            });
            page = new PageResult<T>(rowsInPage,pageSpecification,numberOfPages);
        }
        return page;
	}

	/**
	 * @return business units for logged in user
	 */
    @SuppressWarnings("unchecked")
	public Collection<String> getBusinessUnitsForLoggedInUser() {
		User user = securityHelper.getLoggedInUser();
    	Set<BusinessUnit> businessUnits= user.getBusinessUnits();
    	Collection<String> bus = new ArrayList<String>(1);
    	for(BusinessUnit businessUnit:businessUnits)
    	{
    		bus.add(businessUnit.getName());
    	}
		return bus;
	}

    public CriteriaHelper getCriteriaHelper() {
        return criteriaHelper;
    }

    public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
        this.criteriaHelper = criteriaHelper;
    }

    @SuppressWarnings("unchecked")
    protected List<T> findUsingQueryDisableCurrentDisable(final String queryString,final Map<String,Object> params) {
        return (List<T>)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.disableFilter("currentOwner");
                Query query = session.createQuery(queryString);
                query.setProperties(params);
                return query.list();
            }
        });
    }

    public List<Long> getChildOrganizationIds(final Long orgId){
        return (List<Long>) getHibernateTemplate().execute(
                new HibernateCallback() {

                    /**
                     * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
                     */
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session.createSQLQuery("select org.child_org as id from org_owner_association org where org.PARENT_ORG=:orgId").addScalar("id", Hibernate.LONG)
                                .setParameter("orgId", orgId).setCacheable(true).list();

                    };
                });
    }

    public List<Organization> getChildOrganizations(final Long orgId){
        return (List<Organization>) getHibernateTemplate().execute(
                new HibernateCallback() {

                    /**
                     * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
                     */
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        //return session.createSQLQuery("select o.id as id from org_owner_association org, organization o where org.PARENT_ORG=:orgId and org.child_org=o.id").addScalar("id",Hibernate.LONG)
                        return session.createSQLQuery("select org.child_org as id from org_owner_association org where org.PARENT_ORG=:orgId").addScalar("id",Hibernate.LONG)
                                .setResultTransformer(Transformers.aliasToBean(Organization.class)).setParameter("orgId", orgId).setCacheable(true).list();

                    };
                });
    }
}
