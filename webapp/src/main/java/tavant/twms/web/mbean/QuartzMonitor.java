package tavant.twms.web.mbean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

@ManagedResource(
		objectName="bean:name=quartzMonitor", description="Quartz Monitor"
)
public class QuartzMonitor implements JobListener{
	private Scheduler scheduler;
			
	private Map<String, String> jobsCompletedOn = new Hashtable<String, String>();
	
	private Map<String, String> jobStartedOn = new Hashtable<String, String>();
	
	private boolean autoStartUpEnabled; 

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
        try{
            scheduler.addGlobalJobListener(this);
        }catch(Throwable t){}// ignore all exception, we will not get started and ended time alone
	}
	
	public void startScheduler(){
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@ManagedOperation(
			description = "Pauses all the jobs"
	)
	public void pauseAllJobs(){
		errorIfNotAllowedOperation();
		try {
			scheduler.pauseAll();
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@ManagedOperation(
			description = "Resumes all the paused jobs"
	)
	public void resumeAllJobs(){
		errorIfNotAllowedOperation();
		try {
			scheduler.resumeAll();
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@ManagedOperation(
			description = "Pauses the job with the name passed"
	)
	@ManagedOperationParameters(
			@ManagedOperationParameter(name="jobName",description="Name of the quartz job to be paused"))
	public void pauseJob(String jobName){
		errorIfNotAllowedOperation();
		try {
			scheduler.pauseJob(jobName,getJobGroupName());
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage());
		}		
	}
	
	@ManagedOperation(
			description = "Resumes the job with the name passed if it was paused"
	)
	@ManagedOperationParameters(
		@ManagedOperationParameter(name="jobName",description="Name of the paused quartz job to resume"))
	public void resumeJob(String jobName){
		errorIfNotAllowedOperation();
		try {
			scheduler.resumeJob(jobName,getJobGroupName());
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage());
		}		
	}
	
	public String[] getJobNames(){
		try {
			return scheduler.getJobNames(getJobGroupName());
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public String getJobGroupName(){
		try {
			return scheduler.getJobGroupNames()[0];
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@ManagedOperation(
			description = "Runs the job with the name passed"
	)
	@ManagedOperationParameters(
		@ManagedOperationParameter(name="jobName",description="Name of the quartz job to run right away"))	
	public void runJobNow(String jobName){
		errorIfNotAllowedOperation();
		try {
			JobDetail jobDetail = scheduler.getJobDetail(jobName,getJobGroupName());
			if(jobDetail != null){
				scheduler.triggerJob(jobDetail.getName(), jobDetail.getGroup());
			}else{
				throw new RuntimeException("No jobs are registered with the Name"+jobName);
			}
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getStatus(Trigger trigger) throws SchedulerException{
		int triggerState = scheduler.getTriggerState(trigger.getName(), trigger.getGroup());
        String state = null;
        switch (triggerState) {
            case Trigger.STATE_BLOCKED :
                state = "RUNNING";
                break;
            case Trigger.STATE_ERROR :
                state = "ERROR";
                break;
            case Trigger.STATE_COMPLETE :
                state = "COMPLETED";
                break;
            case Trigger.STATE_PAUSED :
                state = "PAUSED";
                break;
            default :
                state = "PENDING";
                break;
        }
        return state;
	}
	
	@ManagedAttribute(description="Status of all jobs")
	public String getAllStatus() throws SchedulerException{
		errorIfNotAllowedOperation();
		String format = "|%1$-75s|%2$-20s|%3$-25s|%4$-25s|%5$-15s|%6$-25s\n";
		StringBuilder sb = new StringBuilder("<pre>");
		sb.append(String.format(format,"JobName","Status","Started On","Completed On","Time Taken(s)","Next Fire Time"));
		List<String> jobNames = Arrays.asList(getJobNames());
		Collections.sort(jobNames);
		for(String jobName:jobNames){
			Trigger[] triggers = scheduler.getTriggersOfJob(jobName, getJobGroupName());
            sb.append(String.format(format, jobName, getStatus(triggers[0]),
                    getLastStartTime(jobName),getLastCompletionTime(jobName), 
                    getLastExecutionTime(jobName), format(triggers[0].getNextFireTime())));
		}
		sb.append("</pre>");
		return sb.toString();
	}
	
	public String getLastStartTime(String jobName){
		return jobStartedOn.get(jobName);
    }
	
	public String getLastCompletionTime(String jobName){
		return jobsCompletedOn.get(jobName);
    }
	
	public void jobExecutionVetoed(JobExecutionContext jec) {
		jobStartedOn.put(jec.getJobDetail().getName(),format(new Date()));
	}

	public void jobToBeExecuted(JobExecutionContext jec) {
		jobStartedOn.put(jec.getJobDetail().getName(),format(new Date()));
        jobsCompletedOn.remove(jec.getJobDetail().getName());
	}

	public void jobWasExecuted(JobExecutionContext jec,
		JobExecutionException e) {
		jobsCompletedOn.put(jec.getJobDetail().getName(),format(new Date()));
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	private String format(Date date){
		return dateFormat.format(date);
	}

    private long getLastExecutionTime(String jobName){
        String startTime = jobStartedOn.get(jobName);
        String endTime = jobsCompletedOn.get(jobName);
        if(startTime != null && endTime != null){
            try {
                return (dateFormat.parse(endTime).getTime() - dateFormat.parse(startTime).getTime()) / 1000;
            } catch (ParseException ex) {
                // ignore exception :)
            }
        }
        return 0;
    }
    
	public boolean isAutoStartUpEnabled() {
		return autoStartUpEnabled;
	}

	public void setAutoStartUpEnabled(boolean autoStartUpEnabled) {
		this.autoStartUpEnabled = autoStartUpEnabled;
	}
	
	private void errorIfNotAllowedOperation(){
		if(!autoStartUpEnabled)
			throw new RuntimeException("Scheduler is not enabled");
	}
	
}
