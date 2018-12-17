package tavant.twms.web.common;

import javax.servlet.ServletException;

import org.jbpm.scheduler.impl.SchedulerServlet;

@SuppressWarnings("serial")
public class JbpmSchedulerServlet extends SchedulerServlet {

	@Override
	public void init() throws ServletException {
		String schedulerProperty = System.getProperty("jbpmScheduled");
		if(schedulerProperty == null || "true".equalsIgnoreCase(schedulerProperty)){
			super.init();
		}
	}
}
