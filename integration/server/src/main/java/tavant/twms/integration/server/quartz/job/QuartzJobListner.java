/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.quartz.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * Global job listener, which will be notified of all the events This we will
 * use to store all the job related timings in this bean.
 *
 * @author prasad.r
 */
public class QuartzJobListner implements JobListener {

    private Map<String, String> jobsCompletedOn = new HashMap<String, String>();
    private Map<String, String> jobStartedOn = new HashMap<String, String>();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");

    public String getName() {
        return this.getClass().getName();
    }

    public void jobToBeExecuted(JobExecutionContext jec) {
        jobStartedOn.put(jec.getJobDetail().getName(), format(new Date()));
        jobsCompletedOn.remove(jec.getJobDetail().getName());
    }

    public void jobExecutionVetoed(JobExecutionContext jec) {
        jobStartedOn.put(jec.getJobDetail().getName(), format(new Date()));
    }

    public void jobWasExecuted(JobExecutionContext jec, JobExecutionException jee) {
        jobsCompletedOn.put(jec.getJobDetail().getName(), format(new Date()));
    }

    private String format(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
        return dateFormat.format(date);
    }

    public String getLastStartTime(String jobName) {
        return jobStartedOn.get(jobName);
    }

    public String getLastCompletionTime(String jobName) {
        return jobsCompletedOn.get(jobName);
    }

    public long getLastExecutionTime(String jobName) {
        String startTime = jobStartedOn.get(jobName);
        String endTime = jobsCompletedOn.get(jobName);
        if (startTime != null && endTime != null) {
            try {
                return (DATE_FORMAT.parse(endTime).getTime() - DATE_FORMAT.parse(startTime).getTime()) / 1000;
            } catch (ParseException ex) {
                // ignore exception :)
            }
        }
        return 0;
    }
}
