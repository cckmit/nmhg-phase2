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

package tavant.twms.integration.adapter;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SyncTrackerDAOImpl extends HibernateDaoSupport implements
		SyncTrackerDAO {

	private int maxResults;

	private int failureCountThreshold;

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public void setFailureCountThreshold(int failureCountThreshold) {
		this.failureCountThreshold = failureCountThreshold;
	}

	@SuppressWarnings("unchecked")
	public List<SyncTracker> getBusinessEntitiesToBeSynced(final String syncType) {
		return (List<SyncTracker>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria criteria = session.createCriteria(
								SyncTracker.class).add(
								Expression.in("status", new SyncStatus[] {
										SyncStatus.FAILED,
										SyncStatus.TO_BE_PROCESSED })).add(
								Expression.lt("noOfAttempts",
										failureCountThreshold)).add(
								Expression.eq("syncType", syncType)).addOrder(
								Order.asc("noOfAttempts")).addOrder(
								Order.asc("createDate")).setMaxResults(
								maxResults);
						return criteria.list();
					}
				});
	}

	public void deleteDuplicatesOfBusinessEntitiesToBeSynced(
			final String syncType) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				List list = session.createQuery(
						"select s1.id from SyncTracker as s1, SyncTracker as s2 "
								+ "where s1.syncType = s2.syncType and "
								+ "s1.businessId = s2.businessId and "
								+ "s1.status = '"
								+ SyncStatus.TO_BE_PROCESSED.getStatus()
								+ "' and " + "s2.status = '"
								+ SyncStatus.TO_BE_PROCESSED.getStatus()
								+ "' and " + "s1.id < s2.id and "
								+ "s1.syncType = :syncTypeParam").setString(
						"syncTypeParam", syncType).list();
				if (!list.isEmpty()) {
					session
							.createQuery(
									"delete SyncTracker where id in (:ids)")
							.setParameterList("ids", list).executeUpdate();
				}
				return null;
			}
		});
	}

	public SyncTracker findById(final Long id) {
		return (SyncTracker) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						return session
								.createQuery(
										"from SyncTracker tracker where tracker.id = :idParam")
								.setLong("idParam", id).uniqueResult();
					}
				});
	}

	public void save(SyncTracker newSyncTracker) {
		getHibernateTemplate().save(newSyncTracker);
	}

	public void update(SyncTracker syncTrackerToBeUpdated) {
		getHibernateTemplate().update(syncTrackerToBeUpdated);
	}

	@SuppressWarnings("unchecked")
	public List<SummaryDTO> getSummary(final Date startDate, final Date endDate) {
		return (List<SummaryDTO>) getHibernateTemplate().execute(
				new HibernateCallback() {
					@SuppressWarnings("unchecked")
					public Object doInHibernate(final Session session)
							throws HibernateException {
						Date incrEndDate = CalendarUtil.incrementDate(endDate,
								1);
						List<Object[]> resultSet = session
								.createQuery(
										"select syncType, status, count(*) as noOfRecords from SyncTracker s1 "
												+ "where s1.createDate >=:startDate and s1.createDate <:endDate "
												+ " group by s1.syncType, s1.status order by s1.syncType, s1.status")
								.setParameter("startDate", startDate)
								.setParameter("endDate", incrEndDate).list();
						List<SummaryDTO> summary = new ArrayList<SummaryDTO>();
						SummaryDTO dto = new SummaryDTO();
						for (Object[] row : resultSet) {
							if (!row[0].equals(dto.getSyncType())) {
								dto = new SummaryDTO();
								dto.setSyncType((String) row[0]);
								summary.add(dto);
							}
							if (SyncStatus.COMPLETED.equals(row[1])) {
								dto.setSucessful((Long) row[2]);
								dto.addProcessed(dto.getSucessful());
							} else if (SyncStatus.FAILED.equals(row[1])) {
								dto.setFailed((Long) row[2]);
								dto.addProcessed(dto.getFailed());
							} else if (SyncStatus.IN_PROGRESS.equals(row[1])) {
								dto.setInProgress((Long) row[2]);
								dto.addProcessed(dto.getInProgress());
							} else if (SyncStatus.TO_BE_PROCESSED
									.equals(row[1])) {
								dto.setToBeProcessed((Long) row[2]);
							}
						}
						return summary;
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<ErrorSummaryDTO> getErrorSummary(
			final SyncTrackerSearchCriteria syncTrackerSearchCriteria) {
		return (List<ErrorSummaryDTO>) getHibernateTemplate().execute(
				new HibernateCallback() {
					@SuppressWarnings("unchecked")
					public Object doInHibernate(final Session session)
							throws HibernateException {
						Date incrEndDate = CalendarUtil.incrementDate(
								syncTrackerSearchCriteria.getEndDate(), 1);
						List<Object[]> resultSet = session
								.createQuery(
										"select errorMessage,max(id),count(*) from SyncTracker s1 "
												+ "where s1.createDate >=:startDate and s1.createDate <:endDate and "
												+ " s1.syncType = :syncType and s1.errorMessage <> null "
												+ "group by s1.errorMessage ")
								.setParameter(
										"startDate",
										syncTrackerSearchCriteria
												.getStartDate())
								.setParameter("endDate", incrEndDate)
								.setParameter("syncType",
										syncTrackerSearchCriteria.getSyncType())
								.list();
						List<ErrorSummaryDTO> summary = new ArrayList<ErrorSummaryDTO>();

						for (Object[] row : resultSet) {
							ErrorSummaryDTO dto = new ErrorSummaryDTO();
							dto.setErrorMessage((String) row[0]);
							if (dto.getErrorMessage().length() > 50)
								dto.setErrorMessage(new StringBuffer(dto
										.getErrorMessage().substring(0, 60))
										.append("...").toString());
							dto.setId((Long) row[1]);
							dto.setNumberOfRecords((Long) row[2]);
							summary.add(dto);
						}
						return summary;
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<SyncTracker> getSyncDetails(
			final SyncTrackerSearchCriteria syncTrackerSearchCriteria) {
		List<SyncTracker> syncTrackerList = (List<SyncTracker>) getHibernateTemplate()
				.execute(new HibernateCallback() {
					public Object doInHibernate(final Session session)
							throws HibernateException {
						Criteria criteria = session
								.createCriteria(SyncTracker.class);
						criteria.add(Expression.eq("syncType",
								syncTrackerSearchCriteria.getSyncType()));
						criteria.add(Expression.between("createDate",
								syncTrackerSearchCriteria.getStartDate(),
								syncTrackerSearchCriteria.getEndDate()));
						if (syncTrackerSearchCriteria.getStatus() != null
								&& !syncTrackerSearchCriteria.getStatus()
										.equals(""))
							criteria.add(Expression.eq("status",
									new SyncStatus(syncTrackerSearchCriteria
											.getStatus())));
						if (syncTrackerSearchCriteria.getErrorMessage() != null
								&& !syncTrackerSearchCriteria.getErrorMessage()
										.equals(""))
							criteria.add(Expression.like("errorMessage", "%"
									+ syncTrackerSearchCriteria
											.getErrorMessage().trim() + "%"));
						criteria.addOrder(Order.asc("errorMessage"));
						return criteria.list();
					}
				});
		for (SyncTracker syncTracker : syncTrackerList) {
			if (syncTracker.getErrorMessage() != null) {
				if (syncTracker.getErrorMessage().length() > 65) {
					syncTracker.setErrorMessage(new StringBuffer(syncTracker
							.getErrorMessage().substring(0, 60)).append("...")
							.toString());
				}
			}

		}
		return syncTrackerList;
	}

	@SuppressWarnings("unchecked")
	public List<String> getSyncTypes() {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						final Query query = session
								.createQuery("select distinct syncType from SyncTracker");
						return query.list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<String> getStatuses() {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						final Query query = session
								.createQuery("select status from SyncStatus");
						return query.list();
					}
				});
	}

	public String getErrorMessageById(final Long id) {
		return (String) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				final Query query = session
						.createQuery("select errorMessage from SyncTracker where id = :id ");
				query.setLong("id", id);
				return (String) query.uniqueResult();
			}
		});
	}

	public void updateStatus(final SyncTracker syncTracker) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				final Query query = session
						.createQuery("Update SyncTracker set status=:status,startTime=:startTime,updateDate=:updateDate"
								+ " where id =:id and noOfAttempts =:noOfAttempts ");
				query
						.setParameter("status",
								syncTracker.getStatus())
						.setParameter("id", syncTracker.getId())
						.setParameter("noOfAttempts",
								syncTracker.getNoOfAttempts())
						.setParameter("startTime", syncTracker.getStartTime())
						.setParameter("updateDate", syncTracker.getUpdateDate());
				return query.executeUpdate();
			}
		});
	}
}
