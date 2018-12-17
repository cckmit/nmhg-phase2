package tavant.twms.integration.server.quartz.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.StringUtils;


import tavant.twms.integration.layer.IntegrationService;
import tavant.twms.integration.server.common.SendEmailService;
import tavant.twms.integration.server.common.dataaccess.SyncStatus;
import tavant.twms.integration.server.common.dataaccess.SyncTracker;
import tavant.twms.integration.server.common.dataaccess.SyncTrackerDAO;
import tavant.twms.integration.server.util.IntegrationServerConstants;

public class CreditSubmissionScheduler extends QuartzJobBean implements StatefulJob {
	protected static final Logger logger = Logger.getLogger(AbstractSubmissionJob.class);
    private Integer maxNoOfRetries;
    private SyncTrackerDAO syncTrackerDao;
    private SendEmailService sendEmailService;
    private String buName;
    private boolean reportingEnabled;

    public static final String CLAIM_STATE_UPDATION_STATUS = "COMPLETED";
    
    public static final String SYNC_TYPE = "Claim";

    private String urlForClaimStateUpdation;
    
    private EmailReportDetail emailReportDetail;
    private IntegrationService integrationServiceProxy;

    public String getUrlForClaimStateUpdation() {
        return urlForClaimStateUpdation;
    }

    public void setUrlForClaimStateUpdation(String urlForClaimStateUpdation) {
        this.urlForClaimStateUpdation = urlForClaimStateUpdation;
    }

    private String updateClaimState(final SyncTracker syncTracker) {
    	integrationServiceProxy.updateCreditSubmissionDate(syncTracker.getUniqueIdValue());
        return ((String[]) integrationServiceProxy.updateClaimStatePostSubmission(syncTracker.getUniqueIdValue()))[0];
    }
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		logger.error("max nof retries ...."
				+ maxNoOfRetries
				+ "for sync type..."
				+ IntegrationServerConstants.CREDIT_SUBMISSION_JOB_UNIQUE_IDENTIFIER);
 		List<Long> syncTrackerIdsTobeProcessed = syncTrackerDao
				.getIdsForProcessing(
						IntegrationServerConstants.NMHG_EMEA,
						maxNoOfRetries,
						IntegrationServerConstants.CREDIT_SUBMISSION_JOB_UNIQUE_IDENTIFIER);
		logger.error(" ** This is executing from integration-server  : ** ");
		logger.error("Fetched records of Type in integration-server "
				+ IntegrationServerConstants.CREDIT_SUBMISSION_JOB_UNIQUE_IDENTIFIER
				+ " - " + syncTrackerIdsTobeProcessed.size());
		logger.error("buName:" + buName);
		logger.error("maxNoOfRetries:" + 1);

		if (logger.isDebugEnabled()) {
			logger.debug("Fetched records of Type "
					+ IntegrationServerConstants.CREDIT_SUBMISSION_JOB_UNIQUE_IDENTIFIER
					+ " - " + syncTrackerIdsTobeProcessed.size());
		}
		processCreditSubmissionRecords(syncTrackerIdsTobeProcessed);
	}
	
	public void processCreditSubmissionRecords(
			List<Long> syncTrackerIdsTobeProcessed) {
		Date now = new Date();
		List<String> successfulClaims = new ArrayList<String>();
		List<String> noResponseClaims = new ArrayList<String>();
		Map<String, String> failedClaims = new HashMap<String, String>();
		boolean isSuccessful = false;
		if (!syncTrackerIdsTobeProcessed.isEmpty()) {
			System.setProperty("java.io.tmpdir",IntegrationServerConstants.AXIS2_TEMP_LOCATION);
			syncTrackerDao.updateStatus(syncTrackerIdsTobeProcessed,
					SyncStatus.IN_PROGRESS);
			for (Long syncTrackerId : syncTrackerIdsTobeProcessed) {
				SyncTracker syncTracker = syncTrackerDao
						.findById(syncTrackerId);
				try {
					syncTracker.setUpdateDate(now);
					String response = updateSyncDetails(syncTracker);
					if (StringUtils.hasText(response)) {
						if ("SUCCESS".equalsIgnoreCase(response)) {
							isSuccessful = postProcessSuccessfulResponse(syncTracker);
							if (isSuccessful) {
								syncTracker.setStatus(SyncStatus.COMPLETED);
								successfulClaims.add(syncTracker
										.getUniqueIdValue());
								syncTracker.setErrorMessage(null);
							} else {
								failedClaims.put(
										syncTracker.getUniqueIdValue(),
										"Post Process Failed !!!");
							}
						} else {
							syncTracker
									.setProcessing_status(SyncTracker.FAILURE);
							syncTracker.setErrorMessage(response);
							failedClaims.put(syncTracker.getUniqueIdValue(),
									response);
							syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
						}
					} else {
							isSuccessful = postProcessSuccessfulResponse(syncTracker);
							if (isSuccessful) {
								syncTracker.setStatus(SyncStatus.COMPLETED);
								successfulClaims.add(syncTracker
										.getUniqueIdValue());
								syncTracker.setErrorMessage(null);
								syncTracker.setProcessing_status(SyncTracker.SUCCESS);
							}
							successfulClaims
									.add(syncTracker.getUniqueIdValue());
					}
				} catch (Exception e) {
					logger.error("Exception while Processing the"
							+ IntegrationServerConstants.CREDIT_SUBMISSION_JOB_UNIQUE_IDENTIFIER
							+ " Sync  for synctrakerid:" + syncTrackerId);
					populateFailureDetails(syncTracker, e.getMessage());
					failedClaims.put(syncTracker.getUniqueIdValue(),
							e.getMessage());

				} finally {
					syncTracker.setUpdateDate(new Date());
					syncTrackerDao.update(syncTracker);
				}
			}
		}
		if (reportingEnabled)
			sendReport(now, successfulClaims, noResponseClaims, failedClaims);
		integrationServiceProxy
		.updateCreditForAcceptedAndDeniedClaims(IntegrationServerConstants.NMHG_EMEA);
	}

	private void populateFailureDetails(SyncTracker syncTracker,
			String errorMessage) {
		syncTracker.setStatus(SyncStatus.FAILED);
		syncTracker.setProcessing_status(SyncTracker.FAILURE);
		syncTracker.setErrorMessage(errorMessage);
		syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
	}
	private void sendReport(Date startedAt, List<String> successfulClaims,
			List<String> noResponseClaims, Map<String, String> failedClaims) {
		Map<String, Object> messageParams = new HashMap<String, Object>();
		messageParams.put("buName", buName);
		messageParams.put("startDate", startedAt.toString());
		messageParams.put("endDate", new Date().toString());
		messageParams.put("successfulClaims", successfulClaims);
		messageParams.put("noResponseClaims", noResponseClaims);
		messageParams.put("failedClaims", failedClaims);
		sendEmailService
				.sendEmail(emailReportDetail.getFromUserEmail(),
						emailReportDetail.getToUserEmail(),
						emailReportDetail.getSubject()
								+ " - "
								+ buName
								+ (failedClaims.isEmpty()
										? " [SUCCESS]"
										: " [FAILED]"),
						emailReportDetail.getTemplate(), messageParams);

	}
  
    protected boolean postProcessSuccessfulResponse(SyncTracker syncTracker) {
        try {
            String response = updateClaimState(syncTracker);
            boolean isUpdated = CLAIM_STATE_UPDATION_STATUS.equals(response);
            if (!isUpdated) {
                syncTracker.setStatus(SyncStatus.CLAIM_STATE_UPDATION_FAILED);
            }
            return isUpdated;
        } catch (Exception e) {
            syncTracker.setStatus(SyncStatus.CLAIM_STATE_UPDATION_FAILED);
            logger.error("Error updating claim state for sync tracker id : " + syncTracker.getId(),e);
        }
        return false;
    }

    public void setIntegrationServiceProxy(IntegrationService integrationServiceProxy) {
        this.integrationServiceProxy = integrationServiceProxy;
    }

    public void setCreditSubmissionReportDetail(EmailReportDetail emailReportDetail) {
        setEmailReportDetail(emailReportDetail);
    }
    public EmailReportDetail getCreditSubmissionReportDetail() {
    	return getEmailReportDetail();
    }

	public String updateSyncDetails(SyncTracker syncTracker){
		return sendRequest(syncTracker);
	}
	
	private String sendRequest(SyncTracker syncTracker) {
		try {
			logger.error("Credit Submission: Temp location while sending the request xml's to SAP : "+System.getProperty("java.io.tmpdir"));
			final com.nmhg.warrantyclaimcreditsubmission.MTCreditSubmissionDocument creditSubmissionDocument = com.nmhg.warrantyclaimcreditsubmission.MTCreditSubmissionDocument.Factory
					.parse(syncTracker.getBodXML());
			com.nmhg.MI_CreditSubmission_SLMSServiceStub proxy = getProxy();
			proxy.mI_CreditSubmission_SLMS(creditSubmissionDocument);
		} catch (Exception e) {
			logger.error("error while processing credit submission request", e);
			return e.getMessage();
		}
		return null;
	}

    private com.nmhg.MI_CreditSubmission_SLMSServiceStub getProxy() throws org.apache.axis2.AxisFault {
    	emailReportDetail=getCreditSubmissionReportDetail();
    	 EndpointReference targetEPR = 
    	            new EndpointReference(
    	            		emailReportDetail.getCreditSubmissionUrl());

    	HttpTransportProperties.Authenticator basicAuthentication=new HttpTransportProperties.Authenticator();
    	basicAuthentication.setUsername(emailReportDetail.getUserName());
		basicAuthentication.setPassword(emailReportDetail.getPassword());
		com.nmhg.MI_CreditSubmission_SLMSServiceStub proxy = new com.nmhg.MI_CreditSubmission_SLMSServiceStub();
		Options clientOptions = proxy._getServiceClient().getOptions();
		clientOptions.setProperty(
				org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
				basicAuthentication);
		clientOptions.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, "true");
		clientOptions.setTimeOutInMilliSeconds(6000);
		clientOptions.setTo(targetEPR);
		return proxy;
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
