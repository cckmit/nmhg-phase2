package tavant.twms.integration.layer.quartz.jobs;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import tavant.twms.domain.upload.SupportAutomationTaskService;
import tavant.twms.jbpm.infra.BeanLocator;

public class SupportAutomationTask extends QuartzJobBean {
	
	private static final Logger log = Logger
	.getLogger(CreditSubmissionJob.class);

	private BeanLocator beanLocator = new BeanLocator();

	private static boolean isTaskRunning = false;

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		
		if(isTaskRunning)
		{
			log.error("SupportAutomationTask is still running");			
			return;
		}
		isTaskRunning = true;
		

		SupportAutomationTaskService supportAutomationTaskService = (SupportAutomationTaskService) beanLocator
				.lookupBean("supportAutomationTaskService");

		try 
		{
			supportAutomationTaskService.execute();
		} 
		finally {
			isTaskRunning=false;
		}
	}

}
