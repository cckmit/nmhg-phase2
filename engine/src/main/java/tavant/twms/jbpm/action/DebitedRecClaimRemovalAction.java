/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.jbpm.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.scheduler.exe.Timer;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.jbpm.infra.BeanLocator;

import com.domainlanguage.timeutil.Clock;

/**
 * @author pradipta.a
 * @date January 22, 2008
 */
@SuppressWarnings("serial")
public class DebitedRecClaimRemovalAction extends Action {

	private static final String DATE_FORMAT = "MM-dd-yyyy";
	private final BeanLocator beanLocator = new BeanLocator();

	@Override
	public void execute(ExecutionContext executionContext)
	throws Exception {
		Timer timer = executionContext.getTimer();
		ConfigParamService configParamService = (ConfigParamService) this.beanLocator.lookupBean("configParamService");
		RecoveryClaim recClaim = (RecoveryClaim)executionContext.getVariable("recoveryClaim");
		if(recClaim != null)
		{
			int dueDays = configParamService.getLongValue(ConfigName.DAYS_FOR_SHOWING_DEBITTED_REC_CLAIMS.getName()).intValue();
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			Date date = sdf.parse(Clock.today().plusDays(dueDays).toString(DATE_FORMAT));
			timer.setDueDate(date);
		}
	}

}
