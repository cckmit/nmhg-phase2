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
package tavant.twms.worklist.customer;

import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.QueryExecutionCallBack;
import tavant.twms.worklist.WorkListCriteria;

/**
 * @author kiran.sg
 */
public class CustomerWorkListDao extends HibernateDaoSupport {

	private static final Logger logger = Logger
			.getLogger(CustomerWorkListDao.class);

	@SuppressWarnings("unchecked")
	public InboxItemList getWarrantiesForCustomer(
			WorkListCriteria workListCriteria) {
		return execute(
				"warranty",
				"from Warranty warranty where warranty.customer.id = :customerId",
				workListCriteria);
	}

	private InboxItemList execute(String entity, String query,
			WorkListCriteria criteria) {
		Map<String, Object> params = criteria.getParameterMap();
		params.put("customerId", criteria.getUser().getId());
		HibernateCallback callBack = new QueryExecutionCallBack(entity, query,
				criteria, params);
		return (InboxItemList) getHibernateTemplate().execute(callBack);
	}

	@SuppressWarnings("unused")
	private Object execute(final String query, final Map<String, Object> params) {
		return getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
			        if(logger.isDebugEnabled())
			        {
			            logger.debug("Executing [" + query + "] with params " + params);
			        }
				return session.createQuery(query).setProperties(params).list();
			}
		});
	}

}
