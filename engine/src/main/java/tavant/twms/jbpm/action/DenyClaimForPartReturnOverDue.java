package tavant.twms.jbpm.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.scheduler.exe.Timer;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.jbpm.infra.BeanLocator;

import com.domainlanguage.timeutil.Clock;

public class DenyClaimForPartReturnOverDue extends Action{

	private static final String DATE_FORMAT = "MM-dd-yyyy";
	private final BeanLocator beanLocator = new BeanLocator();

	/*
	 * this jbpm action stands to deny part return claim based on a certain window period
	 */
	public void execute(ExecutionContext executionContext) throws Exception {
		org.jbpm.scheduler.exe.Timer timer = executionContext.getTimer();
		ConfigParamService configParamService = (ConfigParamService) this.beanLocator
				.lookupBean("configParamService");
		Claim claim = (Claim) executionContext.getVariable("claim");
		if (claim != null) {
			int dueDays =  configParamService.getLongValue(ConfigName.DAYS_FOR_WAITING_FOR_PART_RETURNS_CLAIM_DENIED.getName()).intValue();
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			Date date = sdf.parse(Clock.today().plusDays(dueDays).toString(DATE_FORMAT));
			timer.setDueDate(date);
		}

}}