/*
 *   Copyright (c) 2008 Tavant Technologies
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
package tavant.twms.domain.additionalAttributes;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author pradipta.a
 */
public class AdditionalAttributeRepositoryImpl extends
        GenericRepositoryImpl<AdditionalAttributes, Long> implements AdditionalAttributesRepository {

    public PageResult<AdditionalAttributes> findAdditionalAttributes(String purpose,
            ListCriteria criteria) {
        PageSpecification pageSpecification = criteria.getPageSpecification();
        final StringBuffer fromAndWhereClause = new StringBuffer();
        fromAndWhereClause.append("from  AdditionalAttributes attributes ");
        if (purpose != null) {
            fromAndWhereClause.append(" where ");
            fromAndWhereClause.append("attributes.attributePurpose=" + "'" + purpose + "'");
        }
        if (criteria.isFilterCriteriaSpecified()) {
            if (purpose != null)
                fromAndWhereClause.append(" and ");
            else
            	fromAndWhereClause.append(" where ");
            String paramterizedFilterCriteria = criteria.getParamterizedFilterCriteria();
            fromAndWhereClause.append(paramterizedFilterCriteria);
        }
        final String queryWithoutSelect = fromAndWhereClause.toString();
        final String sortClause = criteria.getSortCriteriaString();
        final Map<String, Object> parameterMap = criteria.getParameterMap();
        return findPageUsingQuery(queryWithoutSelect, sortClause, pageSpecification, parameterMap);

    }
    
    public AdditionalAttributes findAdditionalAttributeByNameForPurpose(final String name, final AttributePurpose purpose){
    	return (AdditionalAttributes) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										" SELECT additionalAttribute from AdditionalAttributes additionalAttribute " +
										" join additionalAttribute.i18NAdditionalAttributeNames as i18NAttr " +
										" where i18NAttr.name=:name " +
										" and additionalAttribute.attributePurpose=:purpose")
								.setParameter("name", name)
								.setParameter("purpose", purpose)
								.uniqueResult();
					};
				});
    }
    
    @SuppressWarnings("unchecked")
	public List<AdditionalAttributes> findAddAttributeByPurpose(final AttributePurpose purpose)
    {
    	return (List<AdditionalAttributes>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										" SELECT additionalAttribute from AdditionalAttributes additionalAttribute "
										+" where additionalAttribute.attributePurpose=:purpose")
										.setParameter("purpose", purpose)
										.list();
					};
				});
    }
    
    @SuppressWarnings("unchecked")
    public List<AdditionalAttributes> findAttributesForEquipment (long id)
    {
  	return (List<AdditionalAttributes>) getHibernateTemplate().execute(
			new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					return session
							.createQuery(
									" SELECT additionalAttribute from AdditionalAttributes additionalAttribute "
									+" where additionalAttribute.attributePurpose=:purpose")
									
									.list();
				};
			});
}


}
