package tavant.twms.integration.server.quartz.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.StringUtils;

import com.nmhg.batchclaim_response.ClaimSubmissionResponse.Exceptions.Error;
import com.nmhg.batchclaim_response.MTClaimSubmissionResponseDocument;
import tavant.twms.integration.layer.IntegrationService;
import tavant.twms.integration.server.common.dataaccess.SyncStatus;
import tavant.twms.integration.server.common.dataaccess.SyncTracker;
import tavant.twms.integration.server.common.dataaccess.SyncTrackerDAO;
import tavant.twms.integration.server.util.IntegrationServerConstants;

public class DealerBatchClaimJob extends QuartzJobBean implements StatefulJob {

	protected static final Logger logger = Logger.getLogger(DealerBatchClaimJob.class);
	private Integer maxNoOfRetries;
	private SyncTrackerDAO syncTrackerDao;
	private String buName;
	private IntegrationService integrationServiceProxy;

	private EmailReportDetail emailReportDetail;

	public String getSyncType() {
		return IntegrationServerConstants.BATCH_CLAIM_SYNC_JOB_UNIQUE_IDENTIFIER;
	}

	public String updateSyncDetails(SyncTracker syncTracker) {
		MTClaimSubmissionResponseDocument batchClaimResponse = syncBatchClaim(syncTracker);
	    populateSyncTracker(syncTracker, batchClaimResponse);
	    return sendRequest(syncTracker,batchClaimResponse);
	}


	private String sendRequest(SyncTracker syncTracker, MTClaimSubmissionResponseDocument batchClaimResponse)  {
		MTClaimSubmissionResponseDocument batchClaimResponsedoc;
		try {
			batchClaimResponsedoc = MTClaimSubmissionResponseDocument.Factory.parse(syncTracker.getRecord());
			com.nmhg.MI_ClaimSubmissionResponse_OBServiceStub  stub=getProxyForDealerBatchClaim();
			stub.mI_ClaimSubmissionResponse_OB(batchClaimResponsedoc);
			//ok done... now make the status SUCCESS
			syncTracker.setProcessing_status(SyncTracker.SUCCESS);
		} catch (Exception e) {
			syncTracker.setProcessing_status(SyncTracker.FAILURE);
			logger.error("Exception while processing Dealer Batch claim Request"+e.getMessage());
			return e.getMessage();
		}

		//it means response has been sent to them.
		return null;
	}


	private com.nmhg.MI_ClaimSubmissionResponse_OBServiceStub  getProxyForDealerBatchClaim() throws AxisFault {
		emailReportDetail=getEmailReportDetail();
		EndpointReference targetEPR = 
			new EndpointReference(
					emailReportDetail.getDealerBatchClaimWebServiceURL());

		HttpTransportProperties.Authenticator basicAuthentication=new HttpTransportProperties.Authenticator();
		basicAuthentication.setUsername(emailReportDetail.getUserName());
		basicAuthentication.setPassword(emailReportDetail.getPassword());
		com.nmhg.MI_ClaimSubmissionResponse_OBServiceStub  proxy = new com.nmhg.MI_ClaimSubmissionResponse_OBServiceStub();
		Options clientOptions = proxy._getServiceClient().getOptions();
		clientOptions.setProperty(
				org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
				basicAuthentication);
		clientOptions.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, "true");
		clientOptions.setTo(targetEPR);
		clientOptions.setTimeOutInMilliSeconds(6000);
		return proxy;
	}

	private MTClaimSubmissionResponseDocument syncBatchClaim(
			final SyncTracker syncTracker) {
		String response = ((String[]) integrationServiceProxy
				.syncBatchClaim(syncTracker.getBodXML()))[0];
		try {
			syncTracker.setRecord(response);
			return MTClaimSubmissionResponseDocument.Factory.parse(response);
		} catch (XmlException e) {
			throw new RuntimeException(e);
		}
	}

	private void populateSyncTracker(SyncTracker syncTracker,
			MTClaimSubmissionResponseDocument claimSubmissionResponse) {
		ArrayList<String> errorMessageList = new ArrayList<String>();
		if (claimSubmissionResponse
				.getMTClaimSubmissionResponse()
				.getStatus()
				.toString()
				.equalsIgnoreCase(
						IntegrationServerConstants.PROCESSING_STATUS_FAILURE)) {
			syncTracker.setStatus(SyncStatus.FAILED);
			for (Error error : claimSubmissionResponse
					.getMTClaimSubmissionResponse().getExceptions()
					.getErrorArray()) {
				errorMessageList.add(error.getErrorMessage());
			}
			String errorMessage = StringUtils.collectionToDelimitedString(
					errorMessageList, "$$$$");
			StringBuffer errorMessageHolder = new StringBuffer();
			errorMessageHolder.append("-------------Errors While Proceesing "
					+ syncTracker.getSyncType() + " Sync-------------");
			errorMessageHolder.append("\n");
			if (syncTracker.getErrorMessage() != null) {
				errorMessageHolder.append(syncTracker.getErrorMessage());
				errorMessageHolder.append("\n");
			}
			if (errorMessage != null) {
				errorMessageHolder.append(errorMessage);
				errorMessageHolder.append("\n");
			}
			errorMessageHolder
					.append("-----------------------------------------------------------");
			syncTracker.setErrorMessage(errorMessageHolder.toString());
			syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
		} else {
			syncTracker.setStatus(SyncStatus.COMPLETED);
			syncTracker.setRecord(claimSubmissionResponse.toString());
			if (syncTracker.getErrorMessage() != null) {
				syncTracker.setErrorMessage(null);
				syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
			}
		}
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		List<Long> idsTobeProcessed = syncTrackerDao.getItemIdsForProcessing(buName, maxNoOfRetries,getSyncType());

		if (logger.isDebugEnabled()) {
			logger.debug("Fetched records of Type Common - " + idsTobeProcessed.size());
		}
		Date now = new Date();
		if (!idsTobeProcessed.isEmpty()) {
			System.setProperty("java.io.tmpdir",IntegrationServerConstants.AXIS2_TEMP_LOCATION);
			syncTrackerDao.updateStatus(idsTobeProcessed, SyncStatus.IN_PROGRESS);
			for (Long syncTrackerId : idsTobeProcessed) {
				SyncTracker syncTracker = syncTrackerDao.findById(syncTrackerId);
				try {
					syncTracker.setUpdateDate(now);
					updateSyncDetails(syncTracker);
					/*if(processingStatus!=null){
						syncTracker.setProcessing_status(SyncTracker.FAILURE);
					}else{
						syncTracker.setProcessing_status(SyncTracker.SUCCESS);
					}*/
				}catch (Exception e) {
					logger.error("Exception while Processing the Dealer Batch  Sync  for synctrakerid:"+syncTrackerId);
					populateFailureDetails(syncTracker,e.getMessage());
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
		 if(getMaxNoOfRetries() >= syncTracker.getNoOfAttempts())
		 	syncTracker.setProcessing_status(SyncTracker.FAILURE);
		 syncTracker.setErrorMessage(errorMessage);
		 syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
	 }


	public void setIntegrationServiceProxy(IntegrationService integrationServiceProxy) {
		this.integrationServiceProxy = integrationServiceProxy;
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


	public String getBuName() {
		return buName;
	}


	public void setBuName(String buName) {
		this.buName = buName;
	}

	public static Logger getLogger() {
		return logger;
	}


	public IntegrationService getIntegrationServiceProxy() {
		return integrationServiceProxy;
	}

	public EmailReportDetail getEmailReportDetail() {
		return emailReportDetail;
	}

	public void setDealerBatchClaimReportDetail(EmailReportDetail emailReportDetail) {
		this.emailReportDetail = emailReportDetail;
	}
}
