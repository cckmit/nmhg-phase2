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
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.jbpm.infra.BeanLocator;

import com.domainlanguage.timeutil.Clock;

/**
 * 
 * @author pratima.rajak
 * this jbpm action stands to deny forwarded claim based on a certain window period i.e. due days taken from DB(table CONFIG_PARAM and CONFIG_VALUE)
 * varies from BU to BU. Setting the due date on timer.
 */
@SuppressWarnings("serial")
public class DenyForwardedClaimsAction extends Action{

	private static final String DATE_FORMAT = "MM-dd-yyyy";
	private final BeanLocator beanLocator = new BeanLocator();

	public void execute(ExecutionContext executionContext) throws Exception {
		Timer timer = executionContext.getTimer();
		ConfigParamService configParamService = (ConfigParamService) this.beanLocator
				.lookupBean("configParamService");
		Claim claim = (Claim) executionContext.getVariable("claim");
		if (claim != null) {
			int dueDays =  configParamService.getLongValue(ConfigName.DAYS_FOR_FORWARDED_CLAIM_DENIED.getName()).intValue();
			SimpleDateFormat dataFormat = new SimpleDateFormat(DATE_FORMAT);
			Date date = dataFormat.parse(Clock.today().plusDays(dueDays).toString(DATE_FORMAT));
			timer.setDueDate(date);
		}

}}
