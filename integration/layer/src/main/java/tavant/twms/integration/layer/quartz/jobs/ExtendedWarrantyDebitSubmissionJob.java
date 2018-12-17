package tavant.twms.integration.layer.quartz.jobs;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import tavant.twms.domain.integration.SyncStatus;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerDAO;
import tavant.twms.integration.layer.IntegrationPropertiesBean;
import tavant.twms.jbpm.infra.BeanLocator;

public class ExtendedWarrantyDebitSubmissionJob extends QuartzJobBean implements
		StatefulJob {

	private static final Logger log = Logger
			.getLogger(ExtendedWarrantyDebitSubmissionJob.class);

	private final BeanLocator beanLocator = new BeanLocator();

	private Integer maxNoOfRetries;

	private IntegrationPropertiesBean integrationPropertiesBean;

	private SyncTrackerDAO syncTrackerDAO;

	private static boolean isTaskRunning = false;	

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		
		if(isTaskRunning)
		{
			log.error("CreditSubmissionJob is still running");			
			return;
		}
		isTaskRunning = true;
		

		try {
			syncTrackerDAO = (SyncTrackerDAO) this.beanLocator
					.lookupBean("syncTrackerDAO");
			integrationPropertiesBean = (IntegrationPropertiesBean) this.beanLocator
					.lookupBean("integrationPropertiesBean");
			if (maxNoOfRetries == null) {
				maxNoOfRetries = new Integer(10);
			}
			List<SyncTracker> recordsToProcess = syncTrackerDAO
					.getRecordsForProcessing("ExtWarrantyDebitSubmit",maxNoOfRetries);
			log.info(" ** Fetched EW Debit Records To Be Processed ** ");

			String url = integrationPropertiesBean.getSupplierDebitSubmitURL();

			Date now = new Date();
			

			for (SyncTracker syncTracker : recordsToProcess) {
					syncTracker.setUpdateDate(now);
					try {
						String bodXML = syncTracker.getBodXML();
						log.info(" ** BOD XML : ** " + bodXML);

						WebMethodAxislClient axisClient = new WebMethodAxislClient();

						String ewDebitMethod = integrationPropertiesBean
								.getEwSubmitMethod();
						String ewDebitInParam = integrationPropertiesBean
								.getEwSubmitInParam();
						String ewDebitOutParam = integrationPropertiesBean
								.getEwSubmitOutParam();

						String response = axisClient.makeCall(url, bodXML,
								ewDebitMethod, ewDebitInParam, ewDebitOutParam);

						if (response == null) {
							syncTracker.setStatus(SyncStatus.COMPLETED);
						} else {
							syncTracker.setStatus(SyncStatus.ERROR);
							syncTracker.setErrorMessage(response);
						}

					} catch (Exception e) {
						log.error(" ** Exception submitting EW debit ** " + e);
						populateFailureDetails(syncTracker, e.getMessage());
					} finally {
						syncTrackerDAO.update(syncTracker);
					}
			}
		} finally{
			isTaskRunning=false;
		}
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