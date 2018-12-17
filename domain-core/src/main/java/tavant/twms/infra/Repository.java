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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;

public interface Repository {
	public Object findById(Class entityClass, Serializable id);

	public List<?> findByIds(Class entityClass, Collection<? extends Serializable> collectionOfIds);

	public List<?> findByIds(Class entityClass, String propertyNameOfId,
			Collection<? extends Serializable> collectionOfIds);

	public List<?> findAll(Class entityClass);

	public PageResult<?> findAll(Class entityClass, PageSpecification pageSpecification);

	public PageResult<?> findPage(String fromClause, ListCriteria listCriteria);

	public void save(Object entity);

	public void update(Object entity);

	public void delete(Object entity);

	public List<?> findEntitiesThatMatchPropertyValue(String property, Object entity);

	public List<?> findEntitiesThatMatchPropertyValues(Set<String> property, Object entity);

	public List<?> findUsingNamedQuery(final String queryName, final Map<String, Object> params);

	public Object findUniqueUsingNamedQuery(final String queryName, final Map<String, Object> params);

	public List<?> findUsingQuery(final String queryString, final Map<String, Object> params);

	public Object findUniqueUsingQuery(final String queryString, final Map<String, Object> params);

	public PageResult<?> fetchPage(PageSpecification pageSpecification, Criteria queryCriteria);

	public PageResult<?> findPageUsingQuery(final String queryWithoutSelect, final String orderByClause,
			PageSpecification pageSpecification, final Map<String, Object> parameterMap);

	public PageResult<?> findPageUsingQuery(final String queryWithoutSelect, final String orderByClause,
			final String selectClause, PageSpecification pageSpecification, final List parameterMap);

	public PageResult<?> findPageUsingQuery(final String queryWithoutSelect, final String orderByClause,
			final String selectClause, PageSpecification pageSpecification, final Object parameterMap);

    public PageResult<?> findPage(String fromClause,String whereClause, ListCriteria listCriteria);
}
