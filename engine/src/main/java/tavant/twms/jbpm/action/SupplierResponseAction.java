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
import java.util.HashMap;

import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.scheduler.exe.Timer;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.jbpm.infra.BeanLocator;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
/**
 * @author pradipta.a
 * @date Dec 5, 2007
 */

public class SupplierResponseAction extends Action {

	private static final String DATE_FORMAT = "MM-dd-yyyy";
	private final BeanLocator beanLocator = new BeanLocator();


	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		Timer timer = executionContext.getTimer();
		ConfigParamService configParamService = (ConfigParamService) this.beanLocator
				.lookupBean("configParamService");
        RecoveryClaim recoveryClaim = (RecoveryClaim) executionContext
				.getVariable("recoveryClaim");
		if (recoveryClaim != null) {
			int dueDays = recoveryClaim.getContract()
					.getSupplierResponsePeriod();
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			if(configParamService.getBooleanValue(ConfigName.AUTO_DISPUTE_VRCLAIMS_IF_NO_ACTION_FROM_SUPPLIER.getName())){
                timer.setDueDate(null);
			}
			else{
			   Date date = sdf.parse(Clock.today().plusDays(dueDays).toString(
					DATE_FORMAT));
               timer.setDueDate(date);
			}
		}
	}
}
