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

import tavant.twms.integration.layer.IntegrationService;
import tavant.twms.integration.server.common.SendEmailService;
import tavant.twms.integration.server.common.dataaccess.SyncStatus;
import tavant.twms.integration.server.common.dataaccess.SyncTracker;
import tavant.twms.integration.server.common.dataaccess.SyncTrackerDAO;
import tavant.twms.integration.server.util.IntegrationServerConstants;

import com.nmhg.MI_Installation_OBServiceStub;

public class BookingsOutBoundResponseScheduler extends QuartzJobBean implements
		StatefulJob {
	protected static final Logger logger = Logger
			.getLogger(AbstractSubmissionJob.class);
	private Integer maxNoOfRetries;
	private SyncTrackerDAO syncTrackerDao;
	private SendEmailService sendEmailService;
	private String buName;
	private boolean reportingEnabled;

	public static final String CLAIM_STATE_UPDATION_STATUS = "COMPLETED";

	public static final String SYNC_TYPE = "Claim";

	private String urlForClaimStateUpdation;

	private EmailReportDetail bookingsSubmissionReportDetail;
	private IntegrationService integrationServiceProxy;

	public String getUrlForClaimStateUpdation() {
		return urlForClaimStateUpdation;
	}

	public void setUrlForClaimStateUpdation(String urlForClaimStateUpdation) {
		this.urlForClaimStateUpdation = urlForClaimStateUpdation;
	}

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		logger
				.error("max nof retries ...."
						+ maxNoOfRetries
						+ "for sync type..."
						+ IntegrationServerConstants.INSTALLATION_SYNC_JOB_UNIQUE_IDENTIFIER);
		/*
		 * List<Long> syncTrackerIdsTobeProcessed = syncTrackerDao
		 * .getSyncIdsForProcessing( buName, maxNoOfRetries,
		 * IntegrationServerConstants.INSTALLATION_SYNC_JOB_UNIQUE_IDENTIFIER);
		 */
		List<Long> syncTrackerIdsTobeProcessed = syncTrackerDao
				.getSyncIdsForProcessing(buName, maxNoOfRetries, getSyncType());

		logger.error(" ** This is executing from integration-server  : ** ");
		logger.error("Fetched records of Type in integration-server "
				+ IntegrationServerConstants.BOOKING_SYNC_JOB_UNIQUE_IDENTIFIER
				+ " - " + syncTrackerIdsTobeProcessed.size());
		logger.error("buName:" + buName);
		logger.error("maxNoOfRetries:" + 1);

		if (logger.isDebugEnabled()) {
			logger
					.debug("Fetched records of Type "
							+ IntegrationServerConstants.INSTALLATION_SYNC_JOB_UNIQUE_IDENTIFIER
							+ " - " + syncTrackerIdsTobeProcessed.size());
		}
		Date now = new Date();
		if (!syncTrackerIdsTobeProcessed.isEmpty()) {
			System.setProperty("java.io.tmpdir",
					IntegrationServerConstants.AXIS2_TEMP_LOCATION);
			syncTrackerDao.updateStatus(syncTrackerIdsTobeProcessed,
					SyncStatus.IN_PROGRESS);
			for (Long syncTrackerId : syncTrackerIdsTobeProcessed) {
				SyncTracker syncTracker = syncTrackerDao
						.findById(syncTrackerId);
				syncTracker
						.setBusinessUnitInfo(IntegrationServerConstants.NMHG_US);
				try {
					syncTracker.setUpdateDate(now);
					String response = updateSyncDetails(syncTracker);
					if ("SUCCESS".equalsIgnoreCase(response)) {
						syncTracker.setStatus(SyncStatus.COMPLETED);
						syncTracker.setProcessing_status(SyncTracker.SUCCESS);
						syncTracker.setErrorMessage(null);

					} else {
						syncTracker.setProcessing_status(SyncTracker.FAILURE);
						syncTracker.setStatus(SyncStatus.FAILED);
						syncTracker.setErrorMessage(response);
						syncTracker.setNoOfAttempts(syncTracker
								.getNoOfAttempts() + 1);
					}

				} catch (Exception e) {
					logger
							.error("Exception while Processing the"
									+ IntegrationServerConstants.INSTALLATION_SYNC_JOB_UNIQUE_IDENTIFIER
									+ " Sync  for synctrakerid:"
									+ syncTrackerId);
					populateFailureDetails(syncTracker, e.getMessage());
				} finally {
					syncTracker.setUpdateDate(new Date());
					syncTrackerDao.update(syncTracker);
				}
			}
		}

	}

	private void populateFailureDetails(SyncTracker syncTracker,
			String errorMessage) {
		syncTracker.setStatus(SyncStatus.FAILED);
		syncTracker.setProcessing_status(SyncTracker.FAILURE);
		syncTracker.setErrorMessage(errorMessage);
		syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
	}

	public void setIntegrationServiceProxy(
			IntegrationService integrationServiceProxy) {
		this.integrationServiceProxy = integrationServiceProxy;
	}

	public String updateSyncDetails(SyncTracker syncTracker) {
		return sendRequest(syncTracker);
	}

	private String sendRequest(SyncTracker syncTracker) {
		try {
			logger
					.error("Bookings Submission: Temp location while sending the request xml's to SAP : "
							+ System.getProperty("java.io.tmpdir"));
			com.nmhg.www.installationrequest.MTInstallationDocument installationDocument = com.nmhg.www.installationrequest.MTInstallationDocument.Factory
					.parse(syncTracker.getBodXML());
			MI_Installation_OBServiceStub proxy = getProxy();
			proxy.mI_Installation_OB(installationDocument);
		} catch (Exception e) {
			logger.error("error while processing Bookings Submission request",
					e);
			return e.getMessage();
		}
		return "SUCCESS";
	}

	private com.nmhg.MI_Installation_OBServiceStub getProxy()
			throws org.apache.axis2.AxisFault {
		bookingsSubmissionReportDetail = getBookingsSubmissionReportDetail();
		EndpointReference targetEPR = new EndpointReference(
				bookingsSubmissionReportDetail
						.getBookingsSubmsiionWebServiceURL());

		HttpTransportProperties.Authenticator basicAuthentication = new HttpTransportProperties.Authenticator();
		basicAuthentication.setUsername(bookingsSubmissionReportDetail
				.getUserName());
		basicAuthentication.setPassword(bookingsSubmissionReportDetail
				.getPassword());
		com.nmhg.MI_Installation_OBServiceStub proxy = new com.nmhg.MI_Installation_OBServiceStub();

		Options clientOptions = proxy._getServiceClient().getOptions();
		clientOptions.setProperty(
				org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
				basicAuthentication);
		clientOptions.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, "true");
		clientOptions.setTo(targetEPR);
		return proxy;
	}

	public String getBuName() {
		return buName;
	}

	public void setBuName(String buName) {
		this.buName = buName;
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

	private List<String> getSyncType() {
		List<String> syncTypes = new ArrayList<String>();
		syncTypes
				.add(IntegrationServerConstants.INSTALLATION_SYNC_JOB_UNIQUE_IDENTIFIER);
		return syncTypes;
	}

	public EmailReportDetail getBookingsSubmissionReportDetail() {
		return bookingsSubmissionReportDetail;
	}

	public void setBookingsSubmissionReportDetail(
			EmailReportDetail bookingsSubmissionReportDetail) {
		this.bookingsSubmissionReportDetail = bookingsSubmissionReportDetail;
	}

}
