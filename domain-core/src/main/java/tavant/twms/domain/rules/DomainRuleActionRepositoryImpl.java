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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Mar 6, 2007
 * Time: 2:42:33 PM
 */

package tavant.twms.domain.rules;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

public class DomainRuleActionRepositoryImpl
        extends GenericRepositoryImpl<DomainRuleAction, Long>
        implements DomainRuleActionRepository {

    @SuppressWarnings("unchecked")
    public List<DomainRuleAction> findByName(final String actionName) {
        return (List<DomainRuleAction>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria nameCriteria =
                        session.createCriteria(DomainRuleAction.class);
                nameCriteria.add(Expression.like("name", actionName,
                        MatchMode.ANYWHERE));
                return nameCriteria.list();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public List<DomainRuleAction> findByContext(String context)  {
        String query = " from DomainRuleAction dra where dra.context=:context";        
		Map<String, Object> params = new HashMap<String, Object>();		
		params.put("context", context);
		return findUsingQuery(query, params);
    }
    
	public DomainRuleAction findDomianRuleActionByUserStateAndContext(
			String user, String result, String context) {

		String query = "select dra from DomainRuleAction dra where dra.name =:userName "
				+ "and dra.state =:result and dra.context = :context";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", user);
		params.put("result", result);
		params.put("context", context);
		return findUniqueUsingQuery(query, params);

	}

	public DomainRuleAction findDomianRuleActionByLOASchemeStateAndContext(
			String loaName, String result, String context) {
		String query = "select dra from DomainRuleAction dra where dra.loaScheme.name =:loaName "
				+ "and dra.state =:result and dra.context = :context";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loaName", loaName);
		params.put("result", result);
		params.put("context", context);
		return findUniqueUsingQuery(query, params);

	}
}
