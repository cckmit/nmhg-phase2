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
package tavant.twms.domain.query;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.security.SecurityHelper;

/**
 *
 * @author roopali.agrawal
 *
 */
public class SavedQueryRepositoryImpl extends
		GenericRepositoryImpl<SavedQuery, Long> implements SavedQueryRepository {
	public List<SavedQuery> findSavedQueriesByContextAndUser(String context,
			Long userId) {
		String queryString = "select sq from SavedQuery sq "
				+ "join sq.domainPredicate dp "
				+ "where sq.createdBy.id=:id and dp.context=:context and sq.temporary=:temporary";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", userId);
		params.put("context", context);
		params.put("temporary", Boolean.FALSE);
		return findUsingQuery(queryString, params);
	}

	public List<SavedQuery> findSavedQueriesByUserUsingContext(String context,
			Long userId) {
		String queryString = "select sq from SavedQuery sq "
				+ "join sq.createdBy cb "
				+ "where sq.searchQueryName is not null and "
				+ "cb.id=:id and sq.context=:context and sq.temporary=:temporary";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", userId);
		params.put("context", context);
		params.put("temporary", Boolean.FALSE);
		return findUsingQuery(queryString, params);
	}

	public boolean isQueryNameUniqueForUser(String searchQueryName,
			User loggedInUser) {
		String queryString = "select sq from SavedQuery sq "
				+ "where sq.searchQueryName=:searchQueryName and sq.createdBy = :loggedInUser";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("searchQueryName", searchQueryName);
		params.put("loggedInUser", loggedInUser);
		List<SavedQuery> savedQueries = findUsingQuery(queryString, params);
		return (savedQueries != null && !savedQueries.isEmpty());
	}

	public boolean isQueryNameUniqueForUserAndContext(String searchQueryName,
			User loggedInUser, String context) {
		String queryString = "select sq from SavedQuery sq "
				+ "where sq.searchQueryName=:searchQueryName and sq.createdBy = :loggedInUser" +
						" and sq.context = :context";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("searchQueryName", searchQueryName);
		params.put("loggedInUser", loggedInUser);
		params.put("context", context);
		List<SavedQuery> savedQueries = findUsingQuery(queryString, params);
		return (savedQueries != null && !savedQueries.isEmpty());
	}

	@SuppressWarnings("unchecked")
	public SavedQuery findByQueryName(final String saveQueryName,
			final User loggedInUser) {
		return (SavedQuery) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select sq from SavedQuery sq "
												+ "where sq.searchQueryName=:searchQueryName and sq.createdBy= :createdBy")
								.setParameter("searchQueryName", saveQueryName)
								.setParameter("createdBy", loggedInUser)
								.uniqueResult();
					}
				});
	}

	public SavedQuery findByIdAndCreatedBy(Long savedQueryId, User loggedInUser) {
		String queryString = "select sq from SavedQuery sq "
				+ "join sq.createdBy cb "
				+ "where sq.id=:id and sq.createdBy=:createdBy";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", savedQueryId);
		params.put("createdBy", loggedInUser);
		List<SavedQuery> savedQueries = findUsingQuery(queryString, params);
		return (savedQueries != null && !savedQueries.isEmpty()) ? savedQueries
				.get(0) : null;
	}

	public List<SavedQuery> findByName(String context) {
		String queryString = " from SavedQuery "
				+ "where createdBy.id=:id and context = :context";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("context", context);
		params.put("id", new SecurityHelper().getLoggedInUser().getId());
		return findUsingQuery(queryString, params);
	}

	public boolean doesQueryWithNameExists(final String savedQueryName)
			throws Exception {
		final String queryString = "select count(*) from SavedQuery where upper(searchQueryName) = :savedQueryName";
		final String finalQueryName = savedQueryName.toUpperCase();

		Long numberOfRows = (Long) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.createQuery(queryString);
						query.setParameter("savedQueryName", finalQueryName);
						return query.uniqueResult();
					}
				});
		if (numberOfRows != null) {
			return (numberOfRows.intValue() > 0) ? true : false;
		}
		return false;
	}

	public void deleteQueryWithId(final Long id){
		getHibernateTemplate().execute(
		new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String queryString ="delete from SavedQuery where id =:queryId";
				session.createQuery(queryString).setParameter("queryId", id).executeUpdate();
				return null;
			}

		});
	}

}
