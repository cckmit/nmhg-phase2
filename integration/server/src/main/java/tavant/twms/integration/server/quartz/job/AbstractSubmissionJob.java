/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.quartz.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.StringUtils;

import tavant.twms.integration.server.common.SendEmailService;
import tavant.twms.integration.server.common.dataaccess.SyncStatus;
import tavant.twms.integration.server.common.dataaccess.SyncTracker;
import tavant.twms.integration.server.common.dataaccess.SyncTrackerDAO;

/**
 * The job class which can be extended to send xml to Web-Methods
 *
 * @author prasad.r
 */
public abstract class AbstractSubmissionJob extends QuartzJobBean implements StatefulJob {

    protected static final Logger logger = Logger.getLogger(AbstractSubmissionJob.class);
    private Integer maxNoOfRetries;
    private SyncTrackerDAO syncTrackerDao;
    private SendEmailService sendEmailService;
    private String buName;
    private EmailReportDetail emailReportDetail;
    private boolean reportingEnabled;
	

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    	logger.error("max nof retries ...."+maxNoOfRetries +"for sync type..."+getSyncType());
     List<Long> syncTrackerIdsTobeProcessed = syncTrackerDao.getIdsForProcessing(buName, maxNoOfRetries,  getSyncType());
     logger.error(" ** This is executing from integration-server  : ** ");
     logger.error("Fetched records of Type in integration-server " + getSyncType() + " - " + syncTrackerIdsTobeProcessed.size());
     logger.error("buName:"+buName);
     logger.error("maxNoOfRetries:"+1);
     
        if (logger.isDebugEnabled()) {
            logger.debug("Fetched records of Type " + getSyncType() + " - " + syncTrackerIdsTobeProcessed.size());
        }
        Date now = new Date();
        List<String> successfulClaims = new ArrayList<String>();
        List<String> noResponseClaims = new ArrayList<String>();
        Map<String, String> failedClaims = new HashMap<String, String>();
        boolean isSuccessful=false;
        if (!syncTrackerIdsTobeProcessed.isEmpty()) {
            syncTrackerDao.updateStatus(syncTrackerIdsTobeProcessed, SyncStatus.IN_PROGRESS);
            for (Long syncTrackerId : syncTrackerIdsTobeProcessed) {
                SyncTracker syncTracker = syncTrackerDao.findById(syncTrackerId);
                try {
                   syncTracker.setUpdateDate(now);
                    String response = updateSyncDetails(syncTracker);
                if (StringUtils.hasText(response)) {
                        if ("SUCCESS".equalsIgnoreCase(response)) {
                             isSuccessful = postProcessSuccessfulResponse(syncTracker);
                            if (isSuccessful) {
                                syncTracker.setStatus(SyncStatus.COMPLETED);
                                successfulClaims.add(syncTracker.getUniqueIdValue());
                                syncTracker.setErrorMessage(null);
                            } else {
                                failedClaims.put(syncTracker.getUniqueIdValue(), "Post Process Failed !!!");
                            }
                        } else {
                                syncTracker.setProcessing_status(SyncTracker.FAILURE);
                                syncTracker.setErrorMessage(response);
                                failedClaims.put(syncTracker.getUniqueIdValue(), response);
                        }
                    } else {
                    	if(getSyncType().equalsIgnoreCase("Claim")){
                    	  isSuccessful = postProcessSuccessfulResponse(syncTracker);
                         if (isSuccessful) {
                             syncTracker.setStatus(SyncStatus.COMPLETED);
                             successfulClaims.add(syncTracker.getUniqueIdValue());
                             syncTracker.setErrorMessage(null);
                         }
                        successfulClaims.add(syncTracker.getUniqueIdValue());
                    	}
                    }
                } catch (Exception e) {
                	 logger.error("Exception while Processing the" + getSyncType()+" Sync  for synctrakerid:"+syncTrackerId);
                      populateFailureDetails(syncTracker, e.getMessage());
                    failedClaims.put(syncTracker.getUniqueIdValue(), e.getMessage());

                } finally {
                    syncTrackerDao.update(syncTracker);
                }
            }
        }
        if (reportingEnabled)
            sendReport(now, successfulClaims, noResponseClaims, failedClaims);
    }
    
	public abstract String updateSyncDetails(SyncTracker syncTracker);

    private void populateFailureDetails(SyncTracker syncTracker,
                                        String errorMessage) {
        syncTracker.setStatus(SyncStatus.FAILED);
        syncTracker.setProcessing_status(SyncTracker.FAILURE);
        syncTracker.setErrorMessage(errorMessage);
        syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
    }

    public abstract String getSyncType();

    private void sendReport(Date startedAt, List<String> successfulClaims, List<String> noResponseClaims, Map<String, String> failedClaims) {
        Map<String, Object> messageParams = new HashMap<String, Object>();
        messageParams.put("buName", buName);
        messageParams.put("startDate", startedAt.toString());
        messageParams.put("endDate", new Date().toString());
        messageParams.put("successfulClaims", successfulClaims);
        messageParams.put("noResponseClaims", noResponseClaims);
        messageParams.put("failedClaims", failedClaims);
        sendEmailService.sendEmail(
                emailReportDetail.getFromUserEmail(),
                emailReportDetail.getToUserEmail(),
                emailReportDetail.getSubject() + " - " + buName + (failedClaims.isEmpty() ? " [SUCCESS]" : " [FAILED]"),
                emailReportDetail.getTemplate(),
                messageParams);

    }
  
    /**
     * Override this method if any post process needs to be done on success full submission.
     */
    protected boolean postProcessSuccessfulResponse(SyncTracker syncTracker) {
        return true;
    }

    public String getBuName() {
        return buName;
    }

    public void setBuName(String buName) {
        this.buName = buName;
    }

    public void setEmailReportDetail(EmailReportDetail emailReportDetail) {
        this.emailReportDetail = emailReportDetail;
    }
	public EmailReportDetail getEmailReportDetail() {
		return this.emailReportDetail;
	}

    public void setMaxNoOfRetries(Integer maxNoOfRetries) {
        this.maxNoOfRetries = maxNoOfRetries;
    }

    public void setSendEmailService(SendEmailService sendEmailService) {
        this.sendEmailService = sendEmailService;
    }

    public void setSyncTrackerDao(SyncTrackerDAO syncTrackerDao) {
        this.syncTrackerDao = syncTrackerDao;
    }

    public void setReportingEnabled(boolean reportingEnabled) {
        this.reportingEnabled = reportingEnabled;
    }
   
}
