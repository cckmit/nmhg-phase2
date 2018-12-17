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
package tavant.twms.infra;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ognl.Ognl;
import ognl.OgnlException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author kamal.govindraj
 * 
 */
public class RepositoryImpl extends HibernateDaoSupport implements Repository {

	/*
	 * (non-Javadoc)
	 * @see tavant.twms.infra.Repository#delete(java.lang.Object)
	 */
	public void delete(Object entity) {
		getHibernateTemplate().delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * @see tavant.twms.infra.Repository#findAll(java.lang.Class)
	 */
	public List<?> findAll(final Class entityClass) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(entityClass);
				return criteria.list();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see tavant.twms.infra.Repository#findAll(java.lang.Class,
	 * tavant.twms.infra.PageSpecification)
	 */
	public PageResult<?> findAll(Class entityClass, PageSpecification pageSpecification) {
		ListCriteria listCriteria = new ListCriteria();
		listCriteria.setPageSpecification(pageSpecification);
		return findPage(getFromClause(entityClass), listCriteria);
	}

	/*
	 * (non-Javadoc)
	 * @see tavant.twms.infra.Repository#findById(java.io.Serializable)
	 */
	public Object findById(Class entityClass, Serializable id) {
		return getHibernateTemplate().get(entityClass, id);
	}

	/*
	 * (non-Javadoc)
	 * @see tavant.twms.infra.Repository#findByIds(java.util.Collection)
	 */
	public List<?> findByIds(Class entityClass, Collection<? extends Serializable> collectionOfIds) {
		final String propertyNameOfId = "id";
		return findByIds(entityClass, propertyNameOfId, collectionOfIds);
	}

	/*
	 * (non-Javadoc)
	 * @see tavant.twms.infra.Repository#findByIds(java.lang.String,
	 * java.util.Collection)
	 */
	public List<?> findByIds(final Class entityClass, final String propertyNameOfId,
			final Collection<? extends Serializable> collectionOfIds) {
		final Set<Serializable> idSet = new HashSet<Serializable>();
		idSet.addAll(collectionOfIds);
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria queryCriteria = session.createCriteria(entityClass, "entity");
				queryCriteria.add(Expression.in(propertyNameOfId, idSet));
				return queryCriteria.list();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see tavant.twms.infra.Repository#findEntitiesThatMatchPropertyValue(java.lang.String,
	 * java.lang.Object)
	 */
	public List<?> findEntitiesThatMatchPropertyValue(String property, Object entity) {
		Set<String> properties = new HashSet<String>();
		properties.add(property);
		return findEntitiesThatMatchPropertyValues(properties, entity);
	}

	/*
	 * (non-Javadoc)
	 * @see tavant.twms.infra.Repository#findEntitiesThatMatchPropertyValues(java.util.Set,
	 * java.lang.Object)
	 */
	public List<?> findEntitiesThatMatchPropertyValues(final Set<String> properties, final Object entity) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteriaQuery = session.createCriteria(entity.getClass());

				for (String property : properties) {
					Object value = null;
					try {
						value = Ognl.getValue(property, entity);
					}
					catch (OgnlException e) {
						logger.error("Failed to evaluate property [" + property + "] on entity [" + entity + "]", e);
					}
					finally {
						criteriaQuery.add(value != null ? Expression.eq(property, value) : Expression.isNull(property));
					}
				}
				return criteriaQuery.list();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see tavant.twms.infra.Repository#findPage(java.lang.String,
	 * tavant.twms.infra.ListCriteria)
	 */
	public PageResult<?> findPage(String fromClause, ListCriteria listCriteria) {
		PageSpecification pageSpecification = listCriteria.getPageSpecification();
		final StringBuffer fromAndWhereClause = new StringBuffer();
		fromAndWhereClause.append(fromClause);
		if (listCriteria.isFilterCriteriaSpecified()) {
			fromAndWhereClause.append(" where ");
			String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
			fromAndWhereClause.append(paramterizedFilterCriteria);
		}
		final String queryWithoutSelect = fromAndWhereClause.toString();
		final String sortClause = listCriteria.getSortCriteriaString();
		final Map<String, Object> parameterMap = listCriteria.getParameterMap();
		return findPageUsingQuery(queryWithoutSelect, sortClause, pageSpecification, parameterMap);
	}

	public PageResult<?> fetchPage(PageSpecification pageSpecification, Criteria queryCriteria) {
		queryCriteria.setProjection(Projections.rowCount());
		long numberOfRows = (Long) queryCriteria.uniqueResult();
		Integer numberOfPages = pageSpecification.convertRowsToPages(numberOfRows);

		List rowsInPage = new ArrayList();
		PageResult<Object> page = new PageResult<Object>(rowsInPage, pageSpecification, numberOfPages);

		Integer pageOffset = pageSpecification.offSet();
		if (numberOfRows > 0 && numberOfRows > pageOffset) {
			Integer pageSize = pageSpecification.getPageSize();
			rowsInPage = queryCriteria.setProjection(null).setFirstResult(pageOffset).setMaxResults(pageSize).list();

			page = new PageResult<Object>(rowsInPage, pageSpecification, numberOfPages);
		}
		return page;
	}

	public PageResult<?> findPageUsingQuery(final String queryWithoutSelect, final String orderByClause,
			PageSpecification pageSpecification, final Map<String, Object> parameterMap) {
		return findPageUsingQuery(queryWithoutSelect, orderByClause, null, pageSpecification, (Object) parameterMap);
	}

	@SuppressWarnings("unchecked")
	public PageResult<?> findPageUsingQuery(final String queryWithoutSelect, final String orderByClause,
			final String selectClause, PageSpecification pageSpecification, final List parameterMap) {
		return findPageUsingQuery(queryWithoutSelect, orderByClause, selectClause, pageSpecification,
				(Object) parameterMap);
	}

	@SuppressWarnings("unchecked")
	public PageResult<?> findPageUsingQuery(final String queryWithoutSelect, final String orderByClause,
			final String selectClause, PageSpecification pageSpecification, final Object parameterMap) {
		final StringBuffer countQuery = new StringBuffer(" select count(*) ");
		countQuery.append(queryWithoutSelect);

		if (logger.isDebugEnabled()) {
			logger.debug("findPageUsingQuery(" + queryWithoutSelect + "," + orderByClause + ") count query is ["
					+ countQuery + "]");
		}
		Long numberOfRows = (Long) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(countQuery.toString());
				// query.setProperties(parameterMap);
				populateQueryParams(query, parameterMap);
				return query.uniqueResult();
			}
		});
		Integer numberOfPages = pageSpecification.convertRowsToPages(numberOfRows);

		List<Object> rowsInPage = new ArrayList();
		PageResult<?> page = new PageResult<Object>(rowsInPage, pageSpecification, numberOfPages);

		StringBuffer filterAndSort = new StringBuffer();
		if (selectClause != null && !("".equals(selectClause.trim())))
			filterAndSort.append(selectClause);
		filterAndSort.append(queryWithoutSelect);
		if (orderByClause != null && orderByClause.trim().length() > 0) {
			filterAndSort.append(" order by ");
			filterAndSort.append(orderByClause);
		}
		final String finalQuery = filterAndSort.toString();

		final Integer pageOffset = pageSpecification.offSet();
		if (numberOfRows > 0 && numberOfRows > pageOffset) {

			final Integer pageSize = pageSpecification.getPageSize();
			rowsInPage = getHibernateTemplate().executeFind(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					if (logger.isDebugEnabled()) {
						logger.debug(" Unpaginated query for findPage(" + queryWithoutSelect + ",listCriteria) [ "
								+ finalQuery + " ]");
					}

					Query query = session.createQuery(finalQuery);
					populateQueryParams(query, parameterMap);
					// query.setProperties(parameterMap);
					return query.setFirstResult(pageOffset).setMaxResults(pageSize).list();
				}
			});
			page = new PageResult<Object>(rowsInPage, pageSpecification, numberOfPages);
		}
		return page;
	}

	void populateQueryParams(Query query, Object params) {
		if (params instanceof List) {
			List paramsList = (List) params;
			int i = 0;
			for (Object param : paramsList)
				query.setParameter(i++, param);

		}
		else if (params instanceof Map) {
			query.setProperties((Map) params);
		}
		else {
			// todo
			throw new RuntimeException("....");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see tavant.twms.infra.Repository#save(java.lang.Object)
	 */
	public void save(Object entity) {
		getHibernateTemplate().save(entity);

	}

	/*
	 * (non-Javadoc)
	 * @see tavant.twms.infra.Repository#update(java.lang.Object)
	 */
	public void update(Object entity) {
		getHibernateTemplate().update(entity);
	}

	public List<?> findUsingNamedQuery(final String queryName, final Map<String, Object> params) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("findUsingNamedQuery(" + queryName + "," + params + ")");
				}
				Query query = session.getNamedQuery(queryName);
				query.setProperties(params);
				return query.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public Object findUniqueUsingNamedQuery(final String queryName, final Map<String, Object> params) {
		return getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("findUniqueUsingNamedQuery(" + queryName + "," + params + ")");
				}

				Query query = session.getNamedQuery(queryName);
				query.setProperties(params);
				return query.uniqueResult();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<?> findUsingQuery(final String queryString, final Map<String, Object> params) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("findUsingQuery(" + queryString + "," + params + ")");
				}

				Query query = session.createQuery(queryString);
				query.setProperties(params);
				return query.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public Object findUniqueUsingQuery(final String queryString, final Map<String, Object> params) {
		return getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("findUniqueUsingQuery(" + queryString + "," + params + ")");
				}

				Query query = session.createQuery(queryString);
				query.setProperties(params);
				return query.uniqueResult();
			}
		});
	}

	/**
	 * @param entityType
	 */
	protected String getFromClause(Class entityClass) {
		return " from " + entityClass.getSimpleName();
	}

    public PageResult<?> findPage(String fromClause,String whereClause, ListCriteria listCriteria) {
		PageSpecification pageSpecification = listCriteria.getPageSpecification();
		final StringBuffer fromAndWhereClause = new StringBuffer();
		fromAndWhereClause.append(fromClause);
        fromAndWhereClause.append(whereClause);
        if (listCriteria.isFilterCriteriaSpecified()) {
			fromAndWhereClause.append(" and ");
			String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
			fromAndWhereClause.append(paramterizedFilterCriteria);
		}
		final String queryWithoutSelect = fromAndWhereClause.toString();
		final String sortClause = listCriteria.getSortCriteriaString();
		final Map<String, Object> parameterMap = listCriteria.getParameterMap();
		return findPageUsingQuery(queryWithoutSelect, sortClause, pageSpecification, parameterMap);
	}
}
