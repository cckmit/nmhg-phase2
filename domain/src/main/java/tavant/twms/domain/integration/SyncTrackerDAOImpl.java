package tavant.twms.domain.integration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.domain.orgmodel.User;


public class SyncTrackerDAOImpl extends HibernateDaoSupport implements
		SyncTrackerDAO {

	private int maxResults;

	private int failureCountThreshold;
	
	public static String FAILURE="FAILURE";

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

	public SyncTracker findSyncTrackerInProgressByBusinessId(
			final String syncType, final String businessId) {
		return (SyncTracker) getHibernateTemplate().execute(
				new HibernateCallback() {
					@SuppressWarnings("unchecked")
					public Object doInHibernate(Session session)
							throws HibernateException {
						List syncTrackers = session.createQuery(
								"from SyncTracker where "
										+ "syncType = :syncTypeParam and "
										+ "businessId = :businessIdParam and "
										+ "status = '"
										+ SyncStatus.IN_PROGRESS.getStatus()
										+ "'").setString("syncTypeParam",
								syncType).setString("businessIdParam",
								businessId).list();
						return syncTrackers.isEmpty() ? null : syncTrackers
								.get(0);
					}
				});
	}
	/**
	    * This method will returns the list of sync tracker ids for Claim
	    */
	public List<Long> getPreviousSyncTrackerIdsForCreditSubmission(
			final String claimNumber, final String syncType) {
		final List<SyncStatus> syncStatuses = new ArrayList<SyncStatus>();
		syncStatuses.add(SyncStatus.TO_BE_PROCESSED);
		syncStatuses.add(SyncStatus.FAILED);
		return (List<Long>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query q = session
								.createQuery("select s.id from SyncTracker s where s.uniqueIdValue like :claimNumber and syncType=:syncTypeForClaim and s.status in(:syncStausesList)");
						q.setParameter("claimNumber", "%"+claimNumber+"%");
						q.setParameter("syncTypeForClaim", syncType);
						q.setParameterList("syncStausesList", syncStatuses);
						return q.list();
					}
				});
	}
		 public void updateStatus(final List<Long> syncTrackerIds, final SyncStatus status){
		        getHibernateTemplate().execute(new HibernateCallback() {
		            public Object doInHibernate(Session session) throws HibernateException {
		                StringBuilder sb = new StringBuilder("update SyncTracker set status = :status where id in ");
		                List<List<Long>> inClauseParams = getInClauseParams(sb, syncTrackerIds, "id");
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
		 private List<List<Long>> getInClauseParams(StringBuilder sb, List<Long> ids, String columnName){
		        List<List<Long>> inClauseParams = new ArrayList<List<Long>>();
		        int i = 0, start = 0,end = (ids.size() > 1000 ? 1000 : ids.size()), size = ids.size();
		        sb.append("(:ids").append(i).append(")");
		        inClauseParams.add(ids.subList(start, end));
		        while(end < ids.size()){
		            // we have already picked up 0 - 1000 need to pick up from 1001
		            size -= 1000;
		            start += 1000;
		            end = (size > 1000) ? end + 1000 : end+size;
		            i++;
		            sb.append(" or ").append(columnName).append(" in (:ids").append(i).append(")");
		            inClauseParams.add(ids.subList(start, end));
		        }
		        return inClauseParams;
		    }

	public void save(SyncTracker newSyncTracker) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		hibernateTemplate.getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
		hibernateTemplate.save(newSyncTracker);
	}

	public void update(SyncTracker syncTrackerToBeUpdated) {
		getHibernateTemplate().update(syncTrackerToBeUpdated);
	}

	public void save(List<SyncTracker> syncTracks) {
		getHibernateTemplate().saveOrUpdateAll(syncTracks);
	}

	public void updateStatus(final SyncTracker syncTracker) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				final Query query = session
						.createQuery("Update SyncTracker set status=:status,startTime=:startTime,updateDate=:updateDate"
								+ " where id =:id and noOfAttempts =:noOfAttempts ");
				query
						.setParameter("status", syncTracker.getStatus())
						.setParameter("id", syncTracker.getId())
						.setParameter("noOfAttempts",
								syncTracker.getNoOfAttempts())
						.setParameter("startTime", syncTracker.getStartTime())
						.setParameter("updateDate", syncTracker.getUpdateDate());
				return query.executeUpdate();
			}
		});
	}

	public SyncTracker findById(final Long id) {
		getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("bu_name");
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
	public List<SyncTracker> search(final String syncType, final String status,
			final Date fromDate, final Date toDate) {
		return (List<SyncTracker>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria criteria = session
								.createCriteria(SyncTracker.class);
						if (syncType != null && syncType.length() > 0) {
							criteria = criteria.add(Restrictions.eq("syncType",
									syncType));
						}
						if (status != null && status.length() > 0) {
							criteria = criteria.add(Restrictions.eq(
									"status.status", status));
						}
						if (fromDate != null) {
							criteria = criteria.add(Restrictions.ge(
									"createDate", fromDate));
						}
						if (toDate != null) {
							criteria = criteria.add(Restrictions.le(
									"createDate", incrementDate(toDate)));
						}
						return criteria.list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public SyncTracker findByStatusAndUniqueValueForSyncType(final String syncType, final String status,
			final String uniqueIdValue) {
		return (SyncTracker) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria criteria = session
								.createCriteria(SyncTracker.class);
						if (syncType != null && syncType.length() > 0) {
							criteria = criteria.add(Restrictions.eq("syncType",
									syncType));
						}
						if (status != null && status.length() > 0) {
							criteria = criteria.add(Restrictions.eq(
									"status.status", status));
						}
						if(uniqueIdValue != null && uniqueIdValue.length() > 0){
							criteria = criteria.add(Restrictions.eq("uniqueIdValue", 
									uniqueIdValue));
						}
						return criteria.uniqueResult();
					}
				});
	}

	static final Date incrementDate(final Date date) {
		Calendar result = Calendar.getInstance();
		result.setTime(date);
		result.add(Calendar.DATE, 1);
		return result.getTime();
	}

	@SuppressWarnings("unchecked")
	public List<SyncTracker> getRecordsForProcessing(final String syncType,final Integer maxNoOfRetries) {
		return (List<SyncTracker>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria criteria = session.createCriteria(
								SyncTracker.class).add(
								Expression.in("status", new SyncStatus[] {
										SyncStatus.FAILED,
										SyncStatus.TO_BE_PROCESSED })).add(
								Expression.eq("syncType", syncType)).add(
								Expression.lt("noOfAttempts", maxNoOfRetries));						
						return criteria.list();
					}
				});
	}

    public List<Long> getIdsForProcessing(final String syncType,final Integer maxNoOfRetries){
        return (List<Long>) getHibernateTemplate().execute(
			new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException {

					Criteria criteria = session.createCriteria(
							SyncTracker.class).add(
							Expression.in("status", new SyncStatus[] {
									SyncStatus.FAILED,
									SyncStatus.TO_BE_PROCESSED })).add(
							Expression.eq("syncType", syncType)).add(
							Expression.lt("noOfAttempts", maxNoOfRetries));
                                            criteria.setProjection(Projections.property("id"));
                                            return criteria.list();
                                    }
			});
    }
	
    public List<Long> getIdsForRetryProcessing(final String syncType, final Integer maxNoOfRetries) {
		return (List<Long>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria criteria = session.createCriteria(
								SyncTracker.class).add(
								Expression.eq("status",SyncStatus.FAILED)).add(
								Expression.eq("syncType", syncType)).add(
								Expression.lt("noOfAttempts",maxNoOfRetries));
                        criteria.setProjection(Projections.property("id"));
						return criteria.list();
					}
				});
	}
    
        
	@SuppressWarnings("unchecked")
	public List<SyncTracker> getRecordsForRetryProcessing(final String syncType,final Integer maxNoOfRetries) {
		return (List<SyncTracker>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria criteria = session.createCriteria(
								SyncTracker.class).add(
								Expression.eq("status",SyncStatus.FAILED)).add(
								Expression.eq("syncType", syncType)).add(
								Expression.lt("noOfAttempts",maxNoOfRetries))
								.addOrder(Order.asc("id"));						
						return criteria.list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getSyncTypes(){
		 return (List<String>) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(final Session session) throws HibernateException, SQLException {
	                return session
	                        .createQuery(
	                                "select syncType.type from SyncType syncType order by syncType.type").list();
	            };
	        });
	}	


    /**
     * Returns the list of matching sync tracker objects for the given ids.
     * @param ids - collections of id's which needs to retrived
     * @return
     * Note: The size of returned list may not match with that of passed collection
     * if any of the id is not found in db
     */
	
	@SuppressWarnings("unchecked")
    public List<SyncTracker> findByIds(Collection<Long> ids) {
        final Set<Long> idSet = new HashSet<Long>();
        idSet.addAll(ids);
        return (List<SyncTracker>)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria queryCriteria = session.createCriteria(SyncTracker.class);
                queryCriteria.add( Restrictions.in("id", idSet) );
                return queryCriteria.list();
            }
        });
    }

    public void delete(Collection<Long> ids, final User loggedInUserId) {
       final Set<Long> idSet = new HashSet<Long>();
       idSet.addAll(ids);
       getHibernateTemplate().execute(new HibernateCallback() {
           public Object doInHibernate(Session session) throws HibernateException, SQLException {
               Query q = session.createQuery("update SyncTracker set deleted = :isDeleted, hiddenBy = :hiddenBy, hiddenOn = :hiddenOn,"
            		    + " dUpdatedOn = :updatedOn, dLastUpdatedBy = :lastUpdatedBy where id in (:ids)");
               q.setParameter("isDeleted", true);
               q.setParameter("hiddenBy", loggedInUserId.getName());
               q.setParameter("hiddenOn", new Date());
               q.setParameter("updatedOn", new Date());
               q.setParameter("lastUpdatedBy", loggedInUserId);
               q.setParameterList("ids", idSet);
               return q.executeUpdate();
           }
       });
   }

    public void updateAuditableColumns(Collection<Long> ids, final User user){
       final Set<Long> idSet = new HashSet<Long>();
       idSet.addAll(ids);
       getHibernateTemplate().execute(new HibernateCallback() {
           public Object doInHibernate(Session session) throws HibernateException, SQLException {
               Query q = session.createQuery("update SyncTracker set dUpdatedOn = :updatedOn, dLastUpdatedBy = :lastUpdatedBy,noOfAttempts=0 ,processing_status=:processing_status where id in (:ids)");
               q.setParameter("updatedOn", new Date());
               q.setParameter("lastUpdatedBy", user);
               q.setParameter("processing_status", FAILURE);
               q.setParameterList("ids", idSet);
               return q.executeUpdate();
           }
       });
       
    }
    
    public String getReferenceId(final String sequenceName){
    	return (String)getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String sqlQuery = "SELECT " + sequenceName + ".nextval FROM dual";
                return session.createSQLQuery(sqlQuery).uniqueResult().toString();
			}
		});
    }

	public boolean isClaimExistInSyncTracker(final String claimNumber,
			final String syncType) {
		final List<SyncStatus> syncStatuses = new ArrayList<SyncStatus>();
		List<Long> ids = (List<Long>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query q = session
								.createQuery("select s.id from SyncTracker s where s.uniqueIdValue like :claimNumber and syncType=:syncTypeForClaim ");
						q.setParameter("claimNumber", claimNumber);
						q.setParameter("syncTypeForClaim", syncType);
						return q.list();
					}
				});
		if (!ids.isEmpty()) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void updateNextFireTimeForCreditSubmission(final long timetoFire,final String creditSubmission) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createSQLQuery("update qrtz_triggers set NEXT_FIRE_TIME="+ timetoFire +" where TRIGGER_NAME=:creditSubmission");
				query.setParameter("creditSubmission", creditSubmission);
				return query.executeUpdate();
			}
		});
	}

            
}
