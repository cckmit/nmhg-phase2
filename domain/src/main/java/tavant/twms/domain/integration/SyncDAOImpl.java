package tavant.twms.domain.integration;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.util.StringUtils;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;


import com.domainlanguage.time.CalendarDate;

public class SyncDAOImpl extends GenericRepositoryImpl<SyncTracker, Long> 
	implements  SyncDAO  
 {
	public PageResult<SyncTracker> findSyncTrackerObjects(
			String syncType, String transactionId,CalendarDate fromDate, CalendarDate toDate,
			ListCriteria listCriteria, String sortBy ,String order, String statusSelected) {
		getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("bu_name");
		SyncStatus status;
		StringBuffer sortAndOrder= new StringBuffer("syncTracker."+sortBy );
		if("Ascending".equalsIgnoreCase(order)){
			sortAndOrder.append(" asc");
		}else{
			sortAndOrder.append(" desc");
		}
		
		Map<String, Object> params =listCriteria.getParameterMap();
		StringBuffer syncTrackerSearchQuery= new StringBuffer();
		syncTrackerSearchQuery.append(" from SyncTracker syncTracker");
		
		if(syncType!=null && !syncType.equalsIgnoreCase("SELECT")){
			syncTrackerSearchQuery.append(" where syncTracker.syncType = :syncType");
			params.put("syncType",syncType);
		}
		
		if(transactionId != null && StringUtils.hasText(transactionId)){
			if(syncTrackerSearchQuery.toString().contains("where")){
				syncTrackerSearchQuery.append(" and syncTracker.uniqueIdValue like :transactionId");
			} else {
				syncTrackerSearchQuery.append(" where syncTracker.uniqueIdValue like :transactionId");
			}			
			params.put("transactionId", transactionId + "%");
		}
		
		if(fromDate != null){
			Calendar start_Date=fromDate.startAsTimePoint(TimeZone.getTimeZone("Universal")).asJavaCalendar();
			Date startDate= start_Date.getTime();
			if (syncTrackerSearchQuery.toString().contains("where")){
				syncTrackerSearchQuery.append(" and syncTracker.createDate >=:startDate");
			} else{
				syncTrackerSearchQuery.append(" where syncTracker.createDate >=:startDate");
			}
			
			params.put("startDate", startDate);
		}
		if(toDate != null){
			Calendar end_Date=toDate.startAsTimePoint(TimeZone.getTimeZone("Universal")).asJavaCalendar();
			Date endDate= end_Date.getTime();
			if (syncTrackerSearchQuery.toString().contains("where")){
				syncTrackerSearchQuery.append(" and syncTracker.createDate <=:endDate");	
			} else{
				syncTrackerSearchQuery.append(" and syncTracker.createDate <=:endDate");
			}
			
			params.put("endDate", endDate);
		}
		if (listCriteria.isFilterCriteriaSpecified()) {
			syncTrackerSearchQuery.append(" and ");
			String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
			syncTrackerSearchQuery.append(paramterizedFilterCriteria);
		}   
		 if(statusSelected !=null && !statusSelected.equalsIgnoreCase("SELECT")){
			if("Completed".equalsIgnoreCase(statusSelected)){
				status=SyncStatus.COMPLETED;
			}
			else{
				status=SyncStatus.FAILED;
			}
			if (syncTrackerSearchQuery.toString().contains("where")){
				syncTrackerSearchQuery.append(" and syncTracker.status = :status");	
			} else {
				syncTrackerSearchQuery.append(" where syncTracker.status = :status");
			}
			
			params.put("status",status);
		 }	
		
		return findPageUsingQuery(syncTrackerSearchQuery.toString(),sortAndOrder.toString(),
				listCriteria.getPageSpecification(), params);
	}

    public PageResult<SyncTracker> findSyncTrackerObjects(SyncTrackerCriteria criteria){
        SyncTrackerCriteria stc = (SyncTrackerCriteria) criteria;
        return findPageUsingQuery(stc.getSelectQuery(), stc.getSortCriteriaString(), stc.getPageSpecification(), stc.getParameterMap());
    }
}
