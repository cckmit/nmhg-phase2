package tavant.twms.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.security.SecurityHelper;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.partreturn.PartReturnWorkListDao;

public class RejectedPartsSchedulerImpl implements RejectedPartsScheduler{
	
	private static final Logger logger = Logger.getLogger(RejectedPartsSchedulerImpl.class);

	 private ConfigParamService configParamService;
	 
	 private WorkListItemService workListItemService;
	 
	 private PartReturnWorkListDao partReturnWorkListDao;
	 
	 public void setPartReturnWorkListDao(PartReturnWorkListDao partReturnWorkListDao) {
		this.partReturnWorkListDao = partReturnWorkListDao;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	@Override
	public void executeTasks() {
		populateDummyAuthentication();
		endTasksAfterWindowPeriod();
		
	}
	
	private void endTasksAfterWindowPeriod() {
		logger.info("Enter : endTasksAfterWindowPeriod() for RejectedPartsSchedulerImpl");
			 List<TaskInstance> taskInstToEnd = new ArrayList<TaskInstance>();
			 for(TaskInstance instance : partReturnWorkListDao.findAllRejectedPartsTasks()){
				 Claim claim = (Claim) instance.getVariable("claim");
				 String requestPartWindowPeriod = configParamService.getStringValueByBU(ConfigName.DEALER_REQUEST_FOR_PARTS.getName(), claim.getBusinessUnitInfo().getName());
				 if(null != claim.getCreditDate()){
					 Date lastUpdateDate_canval = new Date(claim.getCreditDate().breachEncapsulationOf_year()-1900,claim.getCreditDate().breachEncapsulationOf_month()-1,claim.getCreditDate().breachEncapsulationOf_day());
					 try{
		                if(((new Date().getTime() - lastUpdateDate_canval.getTime())/(1000 * 60 * 60 * 24)) > Integer.parseInt(requestPartWindowPeriod))
		                	taskInstToEnd.add(instance);
					 }
					 catch(NumberFormatException e){
						logger.info("Error parsing the window period : "+requestPartWindowPeriod);
					 }
				 }
			 }
			workListItemService.endAllTasksWithTransition(taskInstToEnd, "toEnd");
	}

	private void populateDummyAuthentication() {
		SecurityHelper securityHelper = new SecurityHelper();
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			securityHelper.populateSystemUser();
		}
	}

}
