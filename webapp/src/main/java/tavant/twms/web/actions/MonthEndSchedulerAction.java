package tavant.twms.web.actions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.classic.Validatable;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.integration.SyncTrackerDAO;
import tavant.twms.process.GenerateWpraScheduler;
import tavant.twms.process.GenerateWpraSchedulerImpl;
import tavant.twms.web.claim.ClaimsAction;

public class MonthEndSchedulerAction extends TwmsActionSupport {

	private static final Logger logger = Logger.getLogger(MonthEndSchedulerAction.class);
	private static final long serialVersionUID = 1L;
	private SyncTrackerDAO syncTrackerDAO;
	private GenerateWpraScheduler generateWpraScheduler;

	public SyncTrackerDAO getSyncTrackerDAO() {
		return syncTrackerDAO;
	}

	public void setSyncTrackerDAO(SyncTrackerDAO syncTrackerDAO) {
		this.syncTrackerDAO = syncTrackerDAO;
	}

	public GenerateWpraScheduler getGenerateWpraScheduler() {
		return generateWpraScheduler;
	}

	public void setGenerateWpraScheduler(GenerateWpraScheduler generateWpraScheduler) {
		this.generateWpraScheduler = generateWpraScheduler;
	}


	private Boolean schedulerToRun;
	private Boolean wpraSchedulerToRun;
	private Boolean updateCreditDate;

	private CalendarDate startDate;
	private CalendarDate endDate;
	private CalendarDate dateToUpdate;

	public Boolean isSchedulerToRun() {
		return schedulerToRun;
	}

	public void setSchedulerToRun(Boolean schedulerToRun) {
		this.schedulerToRun = schedulerToRun;
	}
	
	

	public Boolean isWpraSchedulerToRun() {
		return wpraSchedulerToRun;
	}

	public void setWpraSchedulerToRun(Boolean wpraSchedulerToRun) {
		this.wpraSchedulerToRun = wpraSchedulerToRun;
	}	
	

	public Boolean isUpdateCreditDate() {
		return updateCreditDate;
	}

	public void setUpdateCreditDate(Boolean updateCreditDate) {
		this.updateCreditDate = updateCreditDate;
	}

	public CalendarDate getStartDate() {
		return startDate;
	}

	public void setStartDate(CalendarDate startDate) {
		this.startDate = startDate;
	}

	public CalendarDate getEndDate() {
		return endDate;
	}

	public void setEndDate(CalendarDate endDate) {
		this.endDate = endDate;
	}	

	public CalendarDate getDateToUpdate() {
		return dateToUpdate;
	}

	public void setDateToUpdate(CalendarDate dateToUpdate) {
		this.dateToUpdate = dateToUpdate;
	}

	public void validate() {
		if(startDate==null)
			addActionError(getText("error.warrantyAdmin.emptyStartDate", new String[] {"WPRA Task"}));
		if(endDate==null)
			addActionError(getText("error.warrantyAdmin.emptyEndDate", new String[] {"WPRA Task"}));	
			
	}
	
	public String runMonthEndScheduler() {
		if (getLoggedInUser() != null
				&& getLoggedInUser().getBusinessUnits() != null
				&& !getLoggedInUser().getBusinessUnits().isEmpty()
				&& getLoggedInUser().getBusinessUnits().size() > 1) {
			addActionError(getText("error.warrantyAdmin.emptyStartDate"));
		} else if (isSchedulerToRun() != null) {
			if (isSchedulerToRun()) {
				Date date = new Date();
				long timeToFire = date.getTime()
						+ Long
								.parseLong(getText("label.monthEndScheduler.fiveMinutesToMilliSeconds")); // fire
				// after
				// 5
				String schedulerToRun = null; // minutes
				if (isBuConfigAMER()) {
					schedulerToRun = "creditSubmissionSchedulerForAmer";
				} else if (isBuConfigEMEA()) {
					schedulerToRun = "creditSubmissionScheduler";
				}
				if (schedulerToRun != null)
					syncTrackerDAO.updateNextFireTimeForCreditSubmission(
							timeToFire, schedulerToRun);
				addActionMessage(getText("message.monthEndScheduler.run"));
			} else {
				addActionMessage(getText("message.monthEndScheduler.notRun"));
			}
		}
		return SUCCESS;

	}
	public String runWpraScheduler() {
		if(startDate==null)
			addActionError(getText("error.warrantyAdmin.emptyStartDate", new String[] {"WPRA Task"}));
		if(endDate==null)
			addActionError(getText("error.warrantyAdmin.emptyEndDate", new String[] {"WPRA Task"}));	
	if(hasActionErrors())
		return "input";
		if (isWpraSchedulerToRun() != null) {
			if (isWpraSchedulerToRun()) {															
				generateWpraScheduler.executeTasksBetweenDate(startDate,endDate);
				addActionMessage(getText("message.wpraSchedular.run"));
			} else {
				addActionMessage(getText("message.wpraSchedular.notRun"));
			}
		}
		return SUCCESS;

	}
	
	public String updateCreditDate() {
		if(startDate==null)
			addActionError(getText("error.warrantyAdmin.emptyStartDate", new String[] {"Credit Date"}));
		if(endDate==null)
			addActionError(getText("error.warrantyAdmin.emptyEndDate", new String[] {"Credit Date"}));	
		if(dateToUpdate==null)
			addActionError(getText("error.warrantyAdmin.emptyDateToUpdate", new String[] {"Credit Date"}));	
	if(hasActionErrors())
		return INPUT;
	
		if (isUpdateCreditDate() != null) {
			if (isUpdateCreditDate()) {			
				try{					
				//generateWpraScheduler.executeTasksBetweenDate(startDate,endDate);
				addActionMessage(getText("message.creditDate.updated.successFull"));
				}
				catch(Exception e)
				{
					addActionMessage(getText("message.creditDate.updated.unSuccessFull"));
					logger.error(getText("message.creditDate.updated.unSuccessFull"), e);
					
				}
			} else {
				addActionMessage(getText("message.creditDate.updated.unSuccessFull"));
			}
		}
		return SUCCESS;

	}


}
