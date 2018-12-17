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

package tavant.twms.integration.server.common.dataaccess;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.oagis.ServerResponseHeaderDocumentDTO;
import tavant.twms.integration.server.util.BUNameUtil;
import tavant.twms.integration.server.util.IntegrationServerConstants;

public class SyncTrackerDAOImpl extends HibernateDaoSupport
		implements
			SyncTrackerDAO {

	private int maxResults;

	private int failureCountThreshold;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd-MM-yyyy");
	

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
						Criteria criteria = session
								.createCriteria(SyncTracker.class)
								.add(Expression.in("status", new SyncStatus[]{
										SyncStatus.FAILED,
										SyncStatus.TO_BE_PROCESSED}))
								.add(Expression.lt("noOfAttempts",
										failureCountThreshold))
								.add(Expression.eq("syncType", syncType))
								.addOrder(Order.asc("noOfAttempts"))
								.addOrder(Order.asc("createDate"))
								.setMaxResults(maxResults);
						return criteria.list();
					}
				});
	}

	public SyncTracker findSyncTrackerInProgressByBusinessId(
			final String syncType, final String businessId) {
		return (SyncTracker) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						List syncTrackers = session
								.createQuery(
										"from SyncTracker where "
												+ "syncType = :syncTypeParam and "
												+ "businessId = :businessIdParam and "
												+ "status = '"
												+ SyncStatus.IN_PROGRESS
														.getStatus() + "'")
								.setString("syncTypeParam", syncType)
								.setString("businessIdParam", businessId)
								.list();
						return syncTrackers.isEmpty() ? null : syncTrackers
								.get(0);
					}
				});
	}

	public void save(SyncTracker newSyncTracker) {
		getHibernateTemplate().save(newSyncTracker);
	}

	public void update(SyncTracker syncTrackerToBeUpdated) {
		getHibernateTemplate().update(syncTrackerToBeUpdated);
	}

	public void save(List<SyncTracker> syncTracks) {
		getHibernateTemplate().saveOrUpdateAll(syncTracks);
	}

	public SyncTracker save(String syncType, String bodXML,
			SyncStatus syncStatus) {
		SyncTracker syncTracker = new SyncTracker(syncType, bodXML);
		syncTracker.setStatus(syncStatus);
		if (syncType != null
				&& syncType
						.equalsIgnoreCase(IntegrationServerConstants.TECHNICIAN_SYNC_JOB_UNIQUE_IDENTIFIER)) {
			syncTracker.setBusinessUnitInfo(IntegrationServerConstants.NMHG_US);
		} else {
			syncTracker.setBusinessUnitInfo(BUNameUtil.getBusinessUnitName(
					bodXML, this));
		}
		save(syncTracker);
		return syncTracker;
	}

	public void updateStatus(final SyncTracker syncTracker) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				final Query query = session
						.createQuery("Update SyncTracker set status=:status,startTime=:startTime,updateDate=:updateDate"
								+ " where id =:id and noOfAttempts =:noOfAttempts ");
				query.setParameter("status", syncTracker.getStatus())
						.setParameter("id", syncTracker.getId())
						.setParameter("noOfAttempts",
								syncTracker.getNoOfAttempts())
						.setParameter("startTime", syncTracker.getStartTime())
						.setParameter("updateDate", syncTracker.getUpdateDate());
				return query.executeUpdate();
			}
		});
	}

	public void update(SyncTracker syncTracker, String serverHeader,
			String response) {
		ServerResponseHeaderDocumentDTO doc;
		try {
			doc = ServerResponseHeaderDocumentDTO.Factory.parse(serverHeader);
		} catch (XmlException e) {
			throw new RuntimeException(e);
		}
		if (doc != null) {
			syncTracker.setUniqueIdName(doc.getServerResponseHeader()
					.getUniqueIdentifier().getName());
			syncTracker.setUniqueIdValue(doc.getServerResponseHeader()
					.getUniqueIdentifier().getValue());
			String errorType = doc.getServerResponseHeader().getErrorType();
			String businessUnitName = doc.getServerResponseHeader()
					.getUniqueIdentifier().getBusinessUnitName();
			if (StringUtils.isNotBlank(businessUnitName)) {
				syncTracker.setBusinessUnitInfo(businessUnitName);
			}
			if (errorType == null || StringUtils.isEmpty(errorType)) {
				syncTracker.setProcessing_status(SyncTracker.TOBEPROCESSED);
				syncTracker.setStatus(SyncStatus.COMPLETED);
				syncTracker.setRecord(response);
			} else {
				syncTracker.setErrorType(errorType);
				syncTracker.setProcessing_status(SyncTracker.TOBEPROCESSED);
				syncTracker.setStatus(SyncStatus.FAILED);
				syncTracker.setErrorMessage(response);
			}
			syncTracker.setUpdateDate(new Date());
			update(syncTracker);
		}
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

	@SuppressWarnings("unchecked")
	public List<SyncTracker> findAll() {
		return getHibernateTemplate().loadAll(SyncTracker.class);
	}

	@SuppressWarnings("unchecked")
	public List<SyncTracker> search(final String uniqueIdValue,
			final String syncType, final String status, final Date fromDate,
			final Date toDate, final int page, final int rows) {
		return (List<SyncTracker>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						String sb = "select st "
								+ getQueryWithoutSelect(uniqueIdValue,
										syncType, status, fromDate, toDate);
						return session.createQuery(sb.toString())
								.setFirstResult(page * rows)
								.setMaxResults(rows).list();
					}
				});
	}

	private String getQueryWithoutSelect(final String uniqueIdValue,
			final String syncType, final String status, final Date fromDate,
			final Date toDate) {
		StringBuilder sb = new StringBuilder("from SyncTracker st where ");
		if (uniqueIdValue != null && uniqueIdValue.length() > 0) {
			sb.append("upper(st.uniqueIdValue) like '")
					.append(uniqueIdValue.toUpperCase()).append("%' and ");
		}
		if (syncType != null && syncType.length() > 0) {
			sb.append("st.syncType = '").append(syncType).append("' and ");
		}
		if (status != null && status.length() > 0) {
			sb.append("st.status.status = '").append(status).append("' and ");
		}
		if (fromDate != null) {
			sb.append("trunc(st.createDate) >= '")
					.append(DATE_FORMAT.format(fromDate)).append("' and ");
		}
		if (toDate != null) {
			sb.append("trunc(st.createDate) < '")
					.append(DATE_FORMAT.format(incrementDate(toDate)))
					.append("' and ");
		}
		sb.append("1=1");
		return sb.toString();
	}

	public long getCount(final String uniqueIdValue, final String syncType,
			final String status, final Date fromDate, final Date toDate) {
		return (Long) getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String query = "select count(id) "
						+ getQueryWithoutSelect(uniqueIdValue, syncType,
								status, fromDate, toDate);
				return session.createQuery(query).uniqueResult();
			}
		});
	}

	static Date incrementDate(final Date date) {
		Calendar result = Calendar.getInstance();
		result.setTime(date);
		result.add(Calendar.DATE, 1);
		return result.getTime();
	}

	@SuppressWarnings("unchecked")
	public List<SyncTracker> getClaimsForSubmission(final String syncType) {
		return (List<SyncTracker>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria criteria = session
								.createCriteria(SyncTracker.class)
								.add(Expression.in("status", new SyncStatus[]{
										SyncStatus.FAILED,
										SyncStatus.TO_BE_PROCESSED}))
								.add(Expression.eq("syncType", syncType));
						return criteria.list();
					}
				});
	}
	@SuppressWarnings("unchecked")
	public List<SyncTracker> getBUClaimsForSubmission(final String syncType,
			final String buName) {
		return (List<SyncTracker>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria criteria = session
								.createCriteria(SyncTracker.class)
								.add(Expression.in("status", new SyncStatus[]{
										SyncStatus.FAILED,
										SyncStatus.TO_BE_PROCESSED}))
								.add(Expression.eq("syncType", syncType))
								.add(Expression.eq("businessUnitInfo", buName));
						return criteria.list();
					}
				});
	}

	public List<Long> getItemIdsForProcessing(final String buName,
			final Integer maxRetries, final String syncType) {
		return (List<Long>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria c = session
								.createCriteria(SyncTracker.class)
								.add(Restrictions
										.lt("noOfAttempts", maxRetries))
								.add(Restrictions.eq("syncType", syncType))
								.add(Restrictions.in("status",
										new SyncStatus[]{SyncStatus.FAILED,
										SyncStatus.IN_PROGRESS,
												SyncStatus.TO_BE_PROCESSED}))
								.add(Restrictions.or(Restrictions.eq("processing_status", SyncTracker.FAILURE), Restrictions.isNull("processing_status")));
						c.setProjection(Projections.property("id"));
						c.addOrder(Order.asc("createDate"));
						return c.list();
					}
				});

	}
	public List<Long> getBatchClaimIdsForProcessing(final String buName,
			final Integer maxRetries, final String syncType) {
		return (List<Long>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria c = session
								.createCriteria(SyncTracker.class)
								.add(Restrictions
										.lt("noOfAttempts", maxRetries))
								.add(Restrictions.eq("syncType", syncType))
								.add(Restrictions.in("status",
										new SyncStatus[]{SyncStatus.FAILED,
										SyncStatus.IN_PROGRESS,
												SyncStatus.TO_BE_PROCESSED}))
								.add(Restrictions.or(Restrictions.eq("processing_status", SyncTracker.FAILURE), Restrictions.isNull("processing_status")));
						c.setProjection(Projections.property("id"));
						c.addOrder(Order.asc("createDate"));
						return c.list();
					}
				});

	}

	public List<Long> getIdsForProcessing(final String buName,
			final Integer maxRetries, final String syncType) {
		return (List<Long>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria c = session
								.createCriteria(SyncTracker.class)
								.add(Restrictions
										.lt("noOfAttempts", maxRetries))
								.add(Restrictions.eq("syncType", syncType))
								.add(Restrictions.eq("businessUnitInfo", buName))
								.add(Restrictions.in("status",
										new SyncStatus[]{SyncStatus.FAILED,
										SyncStatus.IN_PROGRESS,
												SyncStatus.TO_BE_PROCESSED}));
						c.add(Restrictions.or(Restrictions.eq("processing_status", SyncTracker.FAILURE), Restrictions.isNull("processing_status")));
						c.setProjection(Projections.property("id"));
						c.addOrder(Order.asc("createDate"));
						return c.list();
					}
				});
	}

	public List<Long> getIdsForProcessing(final String buName,
			final Integer maxRetries, final List<String> syncTypes) {
		final String[] syncType = new String[syncTypes.size()];
		for (int i = 0; i < syncTypes.size(); i++) {
			syncType[i] = syncTypes.get(i);
		}
		return (List<Long>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria c = session
								.createCriteria(SyncTracker.class)
								.add(Restrictions
										.lt("noOfAttempts", maxRetries))
								.add(Restrictions.in("syncType", syncType))
								.add(Restrictions.in("processing_status",
										new String[]{SyncTracker.FAILURE,
												SyncTracker.TOBEPROCESSED,}))
								.add(Restrictions.in("status",
										new SyncStatus[]{SyncStatus.COMPLETED,
												SyncStatus.FAILED,}));
						c.setProjection(Projections.property("id"));
						c.addOrder(Order.asc("createDate"));
						return c.list();
					}
				});
	}

	public void updateStatus(final List<Long> syncTrackerIds,
			final SyncStatus status) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuilder sb = new StringBuilder(
						"update SyncTracker set status = :status where id in ");
				List<List<Long>> inClauseParams = getInClauseParams(sb,
						syncTrackerIds, "id");
				Query q = session.createQuery(sb.toString());
				for (int i = 0; i < inClauseParams.size(); i++) {
					List<Long> list = inClauseParams.get(i);
					q = q.setParameterList("ids" + i, list);
				}
				q = q.setParameter("status", status);
				return q.executeUpdate();
			}
		});
	}

	private List<List<Long>> getInClauseParams(StringBuilder sb,
			List<Long> ids, String columnName) {
		List<List<Long>> inClauseParams = new ArrayList<List<Long>>();
		int i = 0, start = 0, end = (ids.size() > 1000 ? 1000 : ids.size()), size = ids
				.size();
		sb.append("(:ids").append(i).append(")");
		inClauseParams.add(ids.subList(start, end));
		while (end < ids.size()) {
			// we have already picked up 0 - 1000 need to pick up from 1001
			size -= 1000;
			start += 1000;
			end = (size > 1000) ? end + 1000 : end + size;
			i++;
			sb.append(" or ").append(columnName).append(" in (:ids").append(i)
					.append(")");
			inClauseParams.add(ids.subList(start, end));
		}
		return inClauseParams;
	}

	public String getBUForDivisionCode(final String divisionCode) {
		return (String) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				Query query = session
						.createSQLQuery("select BUSINESS_UNIT_INFO from DIVISION_BU_MAPPING where DIVISION_CODE = :divisionCode");
				query = query.setString("divisionCode", divisionCode);
				return query.uniqueResult();
			}
		});
	}

	public List<SyncType> findAllSyncTypes() {
		return getHibernateTemplate().execute(
				new HibernateCallback<List<SyncType>>() {
					public List<SyncType> doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createQuery("from SyncType").list();
					}
				});
	}

	public java.sql.Timestamp getSucccessfulJobDate() {
		return (java.sql.Timestamp) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						List<java.sql.Timestamp> jobStatus = session
								.createSQLQuery(
										"select jb.END_DATE from JOB_STATUS jb where jb.ID=(select max(jbstatus.ID) from JOB_STATUS jbstatus) and jb.STATUS='SUCCESS'")
								.list();
						return jobStatus.isEmpty() ? null : jobStatus.get(0);
					}
				});
	}

	public void updateJobStatus(final Date startDate, final Date endDate,
			final String italyQaNotificationJobStatus) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createSQLQuery("insert into JOB_STATUS (id,START_DATE,END_DATE,STATUS) values(job_status_sequence.nextval,?,?,?)");
				query.setTimestamp(0, startDate);
				query.setTimestamp(1, endDate);
				query.setString(2, italyQaNotificationJobStatus);
				return query.executeUpdate();
			}
		});
	}

	public List<Long> getSyncIdsForProcessing(String buName,
			final Integer maxRetries, List<String> syncTypes) {
		final String[] syncType = new String[syncTypes.size()];
		for (int i = 0; i < syncTypes.size(); i++) {
			syncType[i] = syncTypes.get(i);
		}
		return (List<Long>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria c = session
								.createCriteria(SyncTracker.class)
								.add(Restrictions
										.lt("noOfAttempts", maxRetries))
								.add(Restrictions.in("syncType", syncType))
								.add(Restrictions.or(Restrictions.eq("processing_status", SyncTracker.FAILURE), Restrictions.isNull("processing_status")))
								.add(Restrictions.in("status",
										new SyncStatus[]{SyncStatus.FAILED,
										SyncStatus.TO_BE_PROCESSED,
												SyncStatus.IN_PROGRESS,}));
						c.setProjection(Projections.property("id"));
						c.addOrder(Order.asc("createDate"));
						return c.list();
					}
				});
	}
}
