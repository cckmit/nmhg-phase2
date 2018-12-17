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
package tavant.twms.domain.laborType;

import java.sql.SQLException;

import tavant.twms.domain.laborType.LaborType;

import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author
 * 
 */
public class LaborTypeRepositoryImpl extends
		GenericRepositoryImpl<LaborType, Long> implements LaborTypeRepository {	
	 
	public LaborType findByName(final String laborType) {
        return (LaborType) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from LaborType lt where lt.laborType =:laborType and status = 'ACTIVE'").setString(
                        "laborType", laborType).uniqueResult();
            }
        });
    }
        
	@SuppressWarnings("unchecked")
	public PageResult<LaborType> findByActive(final ListCriteria criteria) {
		StringBuffer baseQuery = new StringBuffer("from LaborType where status = 'ACTIVE' ");
		if (criteria.isFilterCriteriaSpecified()) {
			baseQuery.append(" and ");
			String paramterizedFilterCriteria = criteria.getParamterizedFilterCriteria();
			baseQuery.append(paramterizedFilterCriteria);
		}
		final Map<String, Object> parameterMap = criteria.getParameterMap();
		 return findPageUsingQuery(baseQuery.toString(), getSortCriteriaString(criteria), criteria.getPageSpecification(), parameterMap);
	}
	private String getSortCriteriaString(ListCriteria criteria) {
		if (criteria.getSortCriteria().size() > 0) {
			StringBuffer dynamicQuery = new StringBuffer();
			for (String columnName : criteria.getSortCriteria().keySet()) {
				dynamicQuery.append(columnName);
				dynamicQuery.append(" ");
				dynamicQuery.append(criteria.getSortCriteria().get(columnName));
				dynamicQuery.append(",");
			}
			dynamicQuery.deleteCharAt(dynamicQuery.length() - 1);
			return dynamicQuery.toString();
		}
		return "";
	}
	
}