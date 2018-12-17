package tavant.twms.integration.server.quartz.job;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import tavant.twms.integration.layer.IntegrationService;

public class BookingsOutBoundScheduler extends QuartzJobBean implements
		StatefulJob {
	protected static final Logger logger = Logger
			.getLogger(AbstractSubmissionJob.class);
	private IntegrationService integrationServiceProxy;

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		integrationServiceProxy.saveAndsendUnitTransaction();
	}

	public void setIntegrationServiceProxy(
			IntegrationService integrationServiceProxy) {
		this.integrationServiceProxy = integrationServiceProxy;
	}
}
