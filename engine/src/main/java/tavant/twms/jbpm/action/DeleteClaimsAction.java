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
import java.util.Calendar;
import java.util.Date;

import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.scheduler.exe.Timer;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.jbpm.infra.BeanLocator;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;


/**
 * This jbpm action stands to delete draft based on filed_date or
 * lastupdated_date and the corresponding due_date. filed_date/lastupdated_date taken from DB(table CONFIG_PARAM and CONFIG_VALUE)
 * varies from BU to BU. Setting the due date on timer.this class has to be deleted
 */
@SuppressWarnings("serial")
public class DeleteClaimsAction extends Action {

	private static final String DATE_FORMAT = "MM-dd-yyyy";
	private final BeanLocator beanLocator = new BeanLocator();

	public void execute(ExecutionContext executionContext) throws Exception {
		ConfigParamService configParamService = (ConfigParamService) this.beanLocator
				.lookupBean("configParamService");
		Claim claim = (Claim) executionContext.getVariable("claim");
		if (claim != null) {
			int daysParam = getDaysParam(configParamService);
			CalendarDate filedDate = claim.getFiledOnDate();
			Date lastUpdatedDate=claim.getLastUpdatedOnDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastUpdatedDate);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH)+1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
			CalendarDate updatedDate = CalendarDate.date(year,month,day);
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			Date dueDate = new Date();
			if ("filedOn".equals(getDateParam(configParamService))) {
				dueDate = dateFormat.parse(filedDate.plusDays(daysParam)
						.toString(DATE_FORMAT));
			} else {
				if(updatedDate!=null)
				{
				dueDate = dateFormat.parse(updatedDate.plusDays(daysParam)
						.toString(DATE_FORMAT));
				}else // if there is no updated date for that claim then file date is considered as updated date
				{
					dueDate = dateFormat.parse(filedDate.plusDays(daysParam)
							.toString(DATE_FORMAT));
				}
			}
			Timer timer = executionContext.getTimer();
			timer.setDueDate(dueDate);
		}
	}

	private int getDaysParam(ConfigParamService configParamService) {
		return configParamService.getLongValue(
				ConfigName.DAYS_FOR_DRAFTCLAIM_DELETION.getName()).intValue();
	}

	private String getDateParam(ConfigParamService configParamService) {
		return configParamService
				.getStringValue(ConfigName.DATE_TOUSER_FOR_DRAFTCLAIM_DELETION
						.getName());
	}
}
