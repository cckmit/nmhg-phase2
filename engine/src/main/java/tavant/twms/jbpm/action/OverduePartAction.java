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

import com.domainlanguage.timeutil.Clock;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.scheduler.exe.Timer;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.jbpm.infra.BeanLocator;

/**
 * @author vineeth.varghese
 * @date Feb 25, 2007
 */
/**
 * @author priyank.gupta
 *
 */
@SuppressWarnings("serial")
public class OverduePartAction extends Action {

	private static final String DATE_FORMAT = "MM-dd-yyyy";
	private final BeanLocator beanLocator = new BeanLocator();
	
	public void execute(ExecutionContext executionContext) 
	throws Exception {
		Timer timer = executionContext.getTimer();
		OEMPartReplaced part=null;
		PartReturn partReturn = (PartReturn)executionContext.getVariable("partReturn");
		Claim claim = (Claim)executionContext.getVariable("claim");
		if(partReturn != null)
		{
			part = partReturn.getOemPartReplaced();
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            //Date date = sdf.parse(part.getDueDateForPartReturn().toString(DATE_FORMAT));
            //Fix for 245: duedate may contain the old value. For EMEA value updated while generating the wpra
            //For other business units, anyway the duedays will be available since code updated for setting the duedays while creating the part return data
            //For manual generation processor puts the duedays
            Date date = sdf.parse(Clock.today().plusDays(partReturn.getActualDueDays()).toString(DATE_FORMAT));
            timer.setDueDate(date);

		}
		
		//create an event as Part is moving to overdue state. And user would like to know when that happens
//		createEvent(claim, partReturn);
		//end of creating an event.
	}
	
	private void createEvent(Claim claim, PartReturn partReturn) {
		EventService eventService = (EventService) beanLocator.lookupBean("eventService");
		HashMap<String,Object> eventHashMap = new HashMap<String, Object>();
		eventHashMap.put("claimId",claim.getId().toString());
    	eventHashMap.put("partNumberString",partReturn.getOemPartReplaced().getItemReference().getReferredItem().getNumber());
    	eventHashMap.put("taskInstanceId", partReturn.getId().toString());
    	eventHashMap.put("subject", "Claim - " + claim.getClaimNumber() + " needs attention");
    	eventService.createEvent("partReturn", EventState.PART_MOVED_TO_OVERDUE, eventHashMap);
	}

}
