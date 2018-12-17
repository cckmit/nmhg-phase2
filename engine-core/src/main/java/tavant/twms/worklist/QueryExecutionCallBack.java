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
package tavant.twms.worklist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.ListCriteria;

/**
 * @author kannan.ekanath
 *
 */
public class QueryExecutionCallBack implements HibernateCallback {
    private static final Logger logger = Logger.getLogger(QueryExecutionCallBack.class);

    private final String entityToFind;

    private final String queryString;

    private final ListCriteria criteria;

    private final Map<String, Object> params;
    
    private final boolean isDistinctEntity;
    
    private final String entityName;

    public QueryExecutionCallBack(String entityToFind, String queryString, ListCriteria criteria, Map<String, Object> params) {
        this.entityToFind = entityToFind;
        this.queryString = queryString;
        this.criteria = criteria;
        this.params = params;
        isDistinctEntity = this.entityToFind.trim().startsWith("distinct");
        entityName = entityToFind.trim().replace("distinct", "").trim();
    }
    
    public Object doInHibernate(Session session) throws HibernateException, SQLException {
        int count = executeCountQuery(session);
        List entities = executeFilterQuery(session);
        return new InboxItemList(entities, count);
    }

    protected int executeCountQuery(Session session) {
        String countQueryString = getCountQuery();
        if(logger.isInfoEnabled())
        {
            logger.info("Count query is [" + countQueryString + "]");
        }
        Map<String, Object> bindParams = getBindParams();
        if(logger.isInfoEnabled())
        {
            logger.info("Count query params " + bindParams);
        }
        Query countQuery = session.createQuery(countQueryString).setProperties(bindParams);
        int count = ((Long) countQuery.uniqueResult()).intValue();
        if(logger.isInfoEnabled())
        {
            logger.info("Count is [" + count + "]");
        }
        return count;
    }

    protected List executeFilterQuery(Session session) {
        String filterQueryString = getFilterQuery();
        if(logger.isInfoEnabled())
        {
            logger.info("Filter query is [" + filterQueryString + "]");
        }
        Map<String, Object> bindParams = getBindParams();
        if(logger.isInfoEnabled())
        {
            logger.info("Count query params " + bindParams);
        }
        Query filterQuery = session.createQuery(filterQueryString).setProperties(bindParams);
        filterQuery.setFirstResult(this.criteria.getPageSpecification().offSet());
        filterQuery.setMaxResults(this.criteria.getPageSpecification().getPageSize());
        return(transformResults(filterQuery.list()));
    }

    protected Map<String, Object> getBindParams() {
        Map<String, Object> bindParams = this.criteria.getParameterMap();
        bindParams.putAll(this.params);
        return bindParams;
    }

    protected String getFilterQuery() {
        StringBuffer workListDynamicQuery = new StringBuffer();
        workListDynamicQuery.append("select ").append(this.entityToFind);
        if(isDistinctEntity){ // this is needed to aviod ORA-01791: not a SELECTed expression exception
            String sortCriteriaString = this.criteria.getSortCriteriaString();
            sortCriteriaString = sortCriteriaString.replaceAll("asc", "");
            sortCriteriaString = sortCriteriaString.replaceAll("desc", "");
            workListDynamicQuery.append(" , ").append(sortCriteriaString.trim());
        }
        workListDynamicQuery.append(" ");
        workListDynamicQuery.append(this.queryString);
        addFilterCriteria(workListDynamicQuery);
        addSortCriteria(workListDynamicQuery);
        if(isDistinctEntity) {// add entityToFind.id to sort criteria to aviod duplicate results IN PAGINATED results
            workListDynamicQuery.append(", ").append(entityName).append(".id desc");
        }
        return workListDynamicQuery.toString();
    }

    protected String getCountQuery() {
        StringBuffer countDynamicQuery = new StringBuffer();
        countDynamicQuery.append("select count( ").append(this.entityToFind).append(") ");
        countDynamicQuery.append(this.queryString);
        addFilterCriteria(countDynamicQuery);
        return countDynamicQuery.toString();
    }

    private void addSortCriteria(StringBuffer query) {
        if( this.criteria.isSortCriteriaSpecified() ) {
            query.append(" order by ");
            query.append(this.criteria.getSortCriteriaString());
        }
    }

    private void addFilterCriteria(StringBuffer query) {
        if(this.criteria.isFilterCriteriaSpecified()) {
            query.append(" and ");
            query.append(this.criteria.getParamterizedFilterCriteria());
        }
    }

    protected List transformResults(List list) {
        if(isDistinctEntity){
            List results = new ArrayList(list.size());
            for (int i = 0; i < list.size(); i++) {
                Object[] resultArray = (Object[]) list.get(i);
                results.add(resultArray[0]); // first object is the result we are interested in
            }
            return results;
        }
        return list;
    }
}
