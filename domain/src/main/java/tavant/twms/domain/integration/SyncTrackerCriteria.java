/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tavant.twms.domain.integration;

import com.domainlanguage.time.CalendarDate;
import org.springframework.util.StringUtils;
import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.infra.ListCriteria;

/**
 *
 * @author prasad.r
 */
public class SyncTrackerCriteria extends ListCriteria{
    
    private String syncStatus;
    
    private String syncType;
    
    private CalendarDate fromDate;
    
    private CalendarDate toDate;
    
    private String transactionId;
    
    private String businessId;
    
    private boolean includeDeleted;

    private StringBuilder queryWithOutSelect = new StringBuilder(" from SyncTracker syncTracker where ");

    public SyncTrackerCriteria() {
    }

    public CalendarDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(CalendarDate fromDate) {
        this.fromDate = fromDate;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    public CalendarDate getToDate() {
        return toDate;
    }

    public void setToDate(CalendarDate toDate) {
        this.toDate = toDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getSelectQuery(){
        if(StringUtils.hasText(syncType)){
            queryWithOutSelect.append(" syncTracker.syncType = '").append(syncType).append("' and ");
        }
        if(StringUtils.hasText(syncStatus)){
            queryWithOutSelect.append(" syncTracker.status.status = '").append(syncStatus).append("' and ");
        }
        if(StringUtils.hasText(businessId)){
            queryWithOutSelect.append(" upper(syncTracker.businessId) like '").append(businessId.trim().toUpperCase()).append("%' and ");
        }
        if(StringUtils.hasText(transactionId)){
            queryWithOutSelect.append(" upper(syncTracker.uniqueIdValue) like '").append(transactionId.trim().toUpperCase()).append("%' and ");
        }
        if(fromDate != null){
            queryWithOutSelect.append(" trunc(syncTracker.createDate) >= ")
                    .append("to_date('").append(fromDate.toString(TWMSDateFormatUtil.DATE_FORMAT_CALENDAR_DD_MMM_YYYY))
                    .append("',").append(TWMSDateFormatUtil.DATE_FORMAT_SQL_DD_MMM_YYYY)
                    .append(") and ");
        }
        if(toDate != null){
            queryWithOutSelect.append(" trunc(syncTracker.createDate) <= ")
                    .append("to_date('").append(toDate.toString(TWMSDateFormatUtil.DATE_FORMAT_CALENDAR_DD_MMM_YYYY))
                    .append("',").append(TWMSDateFormatUtil.DATE_FORMAT_SQL_DD_MMM_YYYY)
                    .append(") and ");
        }
        if(!includeDeleted)
            queryWithOutSelect.append("syncTracker.deleted = 'N' and ");
        else
            queryWithOutSelect.append("syncTracker.deleted = 'Y' and ");
        if( isFilterCriteriaSpecified() ) {
            queryWithOutSelect.append(getParamterizedFilterCriteria());
        }
        if(queryWithOutSelect.toString().endsWith("and ")){
            queryWithOutSelect.delete(queryWithOutSelect.length()-4, queryWithOutSelect.length());
        }else if(queryWithOutSelect.toString().endsWith("where ")){
            queryWithOutSelect.delete(queryWithOutSelect.length()-6, queryWithOutSelect.length());
        }
        
        return queryWithOutSelect.toString();
    }
    
    public boolean isIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

}