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

import org.hibernate.Criteria;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author radhakrishnan.j
 */
public interface GenericRepository<T, ID extends Serializable> {
    public static final String SORT_ASC = "asc";
    public static final String SORT_DESC = "dsc";

    public T findById(ID id);
    
    public List<T> findByIds(Collection<ID> collectionOfIds);    
    
    public List<T> findByIds(String propertyNameOfId,Collection<ID> collectionOfIds);    
    
    public List<T> findAll();
    
    public PageResult<T> findAll(PageSpecification pageSpecification);    
    
    public PageResult<T> findPage(String queryString,ListCriteria listCriteria);

    public PageResult<T> findPage(String selectClause, String fromClause, ListCriteria listCriteria);

    public PageResult<T> fetchPage(Criteria queryCriteria, ListCriteria listCriteria,
                                   List<String> alreadyAddedAliases);

    public void save(T entity);

    public void saveAll(List<T> entitiesToSave);

    public void update(T entity);

    public void updateAll(List<T> entitiesToUpdate);

    public void delete(T entity);
    
    public void deleteAll(List<T> entitiesToDelete);

    public List<T> findEntitiesThatMatchPropertyValue(String property,T entity);
    
    public List<T> findEntitiesThatMatchPropertyValues(Set<String> property,T entity);

    public void setCriteriaHelper(CriteriaHelper criteriaHelper);

    public CriteriaHelper getCriteriaHelper();
}
