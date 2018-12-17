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
package tavant.twms.rules.model;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class RuleRepositoryImpl extends HibernateDaoSupport implements
		RuleRepository {

	public void save(RuleSet rule) {
		getHibernateTemplate().save(rule);
	}

	public void update(RuleSet rule) {
		getHibernateTemplate().update(rule);
	}

	public List loadAll() {
		return getHibernateTemplate().loadAll(RuleSet.class);
	}

	public RuleSet load(Long id) {
		return (RuleSet) getHibernateTemplate().load(RuleSet.class, id);
	}

	public void delete(Long id) {
		RuleSet rule = load(id);
		getHibernateTemplate().delete(rule);
	}

	@SuppressWarnings("unchecked")
	public List<RuleSet> findRuleWithName(String name) {
		return getHibernateTemplate().find("from Rule r where r.name=?", name);
	}

}
