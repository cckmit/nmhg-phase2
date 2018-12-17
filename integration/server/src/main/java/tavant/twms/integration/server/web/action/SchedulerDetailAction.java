/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.web.action;


import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import tavant.twms.integration.server.quartz.job.JobConstants;

/**
 *
 * @author prasad.r
 */
public class SchedulerDetailAction extends AbstractAction {


    private String jobGroup;
    private String jobType;
    private int page = 1;
    
    
    public String listJobDetails() throws SchedulerException, JSONException {
        List<JobDetail> details = getJobDetails(getScheduler());
        JSONObject json = new JSONObject();
        JSONArray rows = new JSONArray();
        for (JobDetail jobDetail : details) {
            String jobName = jobDetail.getName();
            String jobGroupName = jobDetail.getGroup();
            JSONObject row = new JSONObject();
            row.put("jobName", jobName);
            row.put("groupName", jobGroupName);
            row.put("started.on", getQuartzJobListner().getLastStartTime(jobName));
            row.put("completed.on", getQuartzJobListner().getLastCompletionTime(jobName));
            row.put("time.taken", getQuartzJobListner().getLastExecutionTime(jobName));
            Trigger[] triggers = getScheduler().getTriggersOfJob(jobName, jobGroupName);
            row.put("numTriggers", triggers.length);
            if (triggers.length > 0) {
                Trigger  t = triggers[0];
                for (int i = 1; i < triggers.length; i++) {
                    Trigger trigger = triggers[i];
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        if(cronTrigger.getNextFireTime().before(t.getNextFireTime())){
                            t = trigger;
                        }
                    }
                }
                row.put("status", getStatus(t));
                row.put("nextFireTime",(t.getNextFireTime() != null) ? DATE_FORMAT.format(t.getNextFireTime()) : "");
            }
            row.put("actions", "&id=" + getId() + "&jobName=" + jobName + "&jobGroup=" + jobGroupName);
            rows.put(row);
        }
        json.put("rows", rows);
        json.put("total", rows.length());
        json.put("records", rows.length());
        json.put("page", getPage());
        return writeJSONResponse(json.toString());
    }

    private String getStatus(Trigger trigger) throws SchedulerException {
        int triggerState = getScheduler().getTriggerState(trigger.getName(), trigger.getGroup());
        String state = null;
        switch (triggerState) {
            case Trigger.STATE_BLOCKED:
                state = "RUNNING";
                break;
            case Trigger.STATE_ERROR:
                state = "ERROR";
                break;
            case Trigger.STATE_COMPLETE:
                state = "COMPLETED";
                break;
            case Trigger.STATE_PAUSED:
                state = "PAUSED";
                break;
            default:
                state = "PENDING";
                break;
        }
        return state;

    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String createJob() {
        jobGroup = JobConstants.REPORTING_JOB_GROUP;
        jobType = JobConstants.MAIL_JOB;
        return SUCCESS;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
