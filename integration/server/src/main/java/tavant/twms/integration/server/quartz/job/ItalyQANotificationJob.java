package tavant.twms.integration.server.quartz.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import tavant.twms.integration.layer.IntegrationService;
import tavant.twms.integration.server.common.dataaccess.SyncStatus;
import tavant.twms.integration.server.common.dataaccess.SyncTracker;
import tavant.twms.integration.server.common.dataaccess.SyncTrackerDAO;
import tavant.twms.integration.server.util.IntegrationServerConstants;

import com.nmhg.syncitalyqanotification.MTSyncItalyQANotificationSLMSDocument;

public class ItalyQANotificationJob extends QuartzJobBean
		implements
			StatefulJob {

	protected static final Logger logger = Logger
			.getLogger(AsyncResponseJob.class);
	private Integer maxNoOfRetries;
	private SyncTrackerDAO syncTrackerDao;
	private IntegrationService integrationServiceProxy;
	private EmailReportDetail emailReportDetail;

	public void updateSyncDetails(java.sql.Timestamp lastSchedulerTime) {
		Map<Map<Long, MTSyncItalyQANotificationSLMSDocument>, java.sql.Timestamp> italyClaimXmlWithEndDate = integrationServiceProxy
				.getClaimsForItalyClaimNotification(lastSchedulerTime);
		Iterator<Map.Entry<Map<Long, MTSyncItalyQANotificationSLMSDocument>, java.sql.Timestamp>> italyClaimXmlWithEndDateEntries = italyClaimXmlWithEndDate
				.entrySet().iterator();
		while (italyClaimXmlWithEndDateEntries.hasNext()) {
			System.setProperty("java.io.tmpdir",IntegrationServerConstants.AXIS2_TEMP_LOCATION);
			Map.Entry<Map<Long, MTSyncItalyQANotificationSLMSDocument>, java.sql.Timestamp> italyClaimXmlWithEndDateEntry = italyClaimXmlWithEndDateEntries
					.next();
			Map<Long, MTSyncItalyQANotificationSLMSDocument> docs = italyClaimXmlWithEndDateEntry
					.getKey();
			Iterator<Map.Entry<Long, MTSyncItalyQANotificationSLMSDocument>> entries = docs
					.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<Long, MTSyncItalyQANotificationSLMSDocument> entry = entries
						.next();
				SyncTracker syncTracker = syncTrackerDao.findById(entry
						.getKey());
				syncTracker.setUpdateDate(new Date());
				try {
					MTSyncItalyQANotificationSLMSDocument mTSyncItalyQANotificationSLMSDocument = entry
							.getValue();
					populateSyncTracker(syncTracker,
							mTSyncItalyQANotificationSLMSDocument);
					sendRequest(syncTracker,
							mTSyncItalyQANotificationSLMSDocument);
				} catch (Exception e) {
					logger.error("Exception while Processing the ItalyQA Notification for synctrakerid:"
							+ entry.getKey());
					populateFailureDetails(syncTracker);
				} finally {
					syncTracker.setUpdateDate(new Date());
					syncTrackerDao.update(syncTracker);
					//syncTrackerDao.save(italyClaimJobObj,null);
					syncTrackerDao.updateJobStatus(lastSchedulerTime,italyClaimXmlWithEndDateEntry.getValue(),IntegrationServerConstants.ITALY_QA_NOTIFICATION_JOB_STATUS);
				}
			}
		}
	}

	private String sendRequest(
			SyncTracker syncTracker,
			MTSyncItalyQANotificationSLMSDocument mTSyncItalyQANotificationSLMSDocument) {
		try {
			logger.error("ItalyQANotificationJob: Temp location while sending the request xml's to SAP : "+System.getProperty("java.io.tmpdir"));
			com.nmhg.MI_SyncItalyQANotification_SLMSServiceStub stub = getProxyForSync();
			stub.mI_SyncItalyQANotification_SLMS(mTSyncItalyQANotificationSLMSDocument);
		} catch (Exception e) {
			logger.error("Exception while Sending the  "
					+ syncTracker.getSyncType() +" xml To SAP system " + e.getMessage());
			return e.getMessage();
		}
		return null;
	}

	private com.nmhg.MI_SyncItalyQANotification_SLMSServiceStub getProxyForSync()
			throws AxisFault {
		HttpTransportProperties.Authenticator basicAuthentication = new HttpTransportProperties.Authenticator();
		emailReportDetail = getEmailReportDetail();
		EndpointReference targetEPR = new EndpointReference(
				emailReportDetail.getItalyQaNotificationUrl());
		basicAuthentication.setUsername(emailReportDetail.getUserName());
		basicAuthentication.setPassword(emailReportDetail.getPassword());
		com.nmhg.MI_SyncItalyQANotification_SLMSServiceStub proxy = new com.nmhg.MI_SyncItalyQANotification_SLMSServiceStub();
		Options clientOptions = proxy._getServiceClient().getOptions();
		clientOptions.setProperty(
				org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
				basicAuthentication);
		clientOptions.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, "true");
		clientOptions.setTo(targetEPR);
		return proxy;
	}

	private void populateSyncTracker(
			SyncTracker syncTracker,
			MTSyncItalyQANotificationSLMSDocument mTSyncItalyQANotificationSLMSDocument) {
		syncTracker.setStatus(SyncStatus.COMPLETED);
		syncTracker.setProcessing_status(SyncTracker.SUCCESS);
		syncTracker.setRecord(mTSyncItalyQANotificationSLMSDocument.toString());
	}


	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		java.sql.Timestamp previousFireTime = syncTrackerDao.getSucccessfulJobDate();
		if (previousFireTime == null) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
			Calendar cal = Calendar.getInstance(); 
			 previousFireTime = new java.sql.Timestamp(cal.getTimeInMillis());
			 syncTrackerDao.updateJobStatus(previousFireTime,previousFireTime,IntegrationServerConstants.ITALY_QA_NOTIFICATION_JOB_STATUS);
		} 
		updateSyncDetails(previousFireTime);
	}

	private void populateFailureDetails(SyncTracker syncTracker) {
		syncTracker.setStatus(SyncStatus.FAILED);
		syncTracker.setProcessing_status(SyncTracker.FAILURE);
		syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
	}

	public IntegrationService getIntegrationServiceProxy() {
		return integrationServiceProxy;
	}

	public void setIntegrationServiceProxy(
			IntegrationService integrationServiceProxy) {
		this.integrationServiceProxy = integrationServiceProxy;
	}

	public EmailReportDetail getEmailReportDetail() {
		return emailReportDetail;
	}

	public void setitalyQaReportDetail(EmailReportDetail emailReportDetail) {
		this.emailReportDetail = emailReportDetail;
	}

	public Integer getMaxNoOfRetries() {
		return maxNoOfRetries;
	}

	public void setMaxNoOfRetries(Integer maxNoOfRetries) {
		this.maxNoOfRetries = maxNoOfRetries;
	}

	public SyncTrackerDAO getSyncTrackerDao() {
		return syncTrackerDao;
	}

	public void setSyncTrackerDao(SyncTrackerDAO syncTrackerDao) {
		this.syncTrackerDao = syncTrackerDao;
	}

	public static Logger getLogger() {
		return logger;
	}

}
