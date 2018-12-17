package tavant.twms.integration.layer.quartz.jobs;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.integration.SyncStatus;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerDAO;
import tavant.twms.integration.layer.IntegrationPropertiesBean;
import tavant.twms.jbpm.infra.BeanLocator;

public class CreditSubmissionJob extends QuartzJobBean implements StatefulJob {

	private static final Logger log = Logger
			.getLogger(CreditSubmissionJob.class);

	private final BeanLocator beanLocator = new BeanLocator();

	private Integer maxNoOfRetries;

	private IntegrationPropertiesBean integrationPropertiesBean;

	private SyncTrackerDAO syncTrackerDAO;

	private static boolean isTaskRunning = false;
	
	private ClaimService claimService;
	


	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		/*log.error(" ** This is executing from integration-layer  : ** ");
		if(isTaskRunning)
		{
			log.error("CreditSubmissionJob is still running");			
			return;
		}
		isTaskRunning = true;
		

		try {
			syncTrackerDAO = (SyncTrackerDAO) this.beanLocator
					.lookupBean("syncTrackerDAO");
			claimService = (ClaimService)this.beanLocator.lookupBean("claimService");
			integrationPropertiesBean = (IntegrationPropertiesBean) this.beanLocator
					.lookupBean("integrationPropertiesBean");
			if (maxNoOfRetries == null) {
				maxNoOfRetries = new Integer(10);
			}
			List<SyncTracker> recordsToProcess = syncTrackerDAO
					.getRecordsForProcessing("Claim",maxNoOfRetries);
			log.info(" ** Fetched Claim Records To Be Processed ** ");
			String url = integrationPropertiesBean.getCreditSubmitURL();
			Date now = new Date();
			
			for (SyncTracker syncTracker : recordsToProcess) {
					syncTracker.setUpdateDate(now);
					try {
						String bodXML = syncTracker.getBodXML();
						log.info(" ** BOD XML : ** " + bodXML);

						WebMethodAxislClient axisClient = new WebMethodAxislClient();

						String creditSubmitMethod = integrationPropertiesBean
								.getCreditSubmitMethod();
						String creditSubmitInParam = integrationPropertiesBean
								.getCreditSubmitInParam();
						String creditSubmitOutParam = integrationPropertiesBean
								.getCreditSubmitOutParam();

						String response = axisClient.makeCall(url, bodXML,
								creditSubmitMethod, creditSubmitInParam,
								creditSubmitOutParam);

						if (response == null) {
							syncTracker.setStatus(SyncStatus.COMPLETED);
							Claim claim = claimService.findClaimByNumber(syncTracker.getUniqueIdValue());
							if(claim != null){
								claim.setState(ClaimState.PENDING_PAYMENT_RESPONSE);
								claimService.updateClaim(claim);
							}
						} else {
							syncTracker.setStatus(SyncStatus.ERROR);
							syncTracker.setErrorMessage(response);
						}
					} catch (Exception e) {
						log.error(" ** Exception submitting claim records ** "
								+ e);
						populateFailureDetails(syncTracker, e.getMessage());
					} finally {
						syncTrackerDAO.update(syncTracker);
					}
			}
		} finally 
		{
				isTaskRunning = false;
		}*/
	}

	private void populateFailureDetails(SyncTracker syncTracker,
			String errorMessage) {
		syncTracker.setStatus(SyncStatus.FAILED);
		syncTracker.setErrorMessage(errorMessage);
		syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
	}

	public Integer getMaxNoOfRetries() {
		return maxNoOfRetries;
	}

	public void setMaxNoOfRetries(Integer maxNoOfRetries) {
		this.maxNoOfRetries = maxNoOfRetries;
	}

}
