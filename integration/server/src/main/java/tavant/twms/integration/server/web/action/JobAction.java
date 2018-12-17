/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.web.action;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.quartz.*;
import tavant.twms.integration.server.quartz.job.FTPJobDetail;
import tavant.twms.integration.server.quartz.job.JobConstants;
import tavant.twms.integration.server.quartz.job.MailingJobDetail;

/**
 *
 * @author prasad.r
 */
public class JobAction extends AbstractAction {

    private String jobType;
    private String bugId;
    private String jobName;
    private String jobGroup;
    private String cronExpression;
    private String reportName;
    private String toAddress;
    private String ccAddress;
    private String sqlForMailJob;
    private String subject;
    private String mailContent;
    private String ftpAddress;
    private String ftpUserName;
    private String ftpPassword;
    private String sqlForFTPJob;
    private String action;
    private String triggerName;
    private String triggerGroup;
    private List<Trigger> triggers;
    private static final String EDIT_JOB_ACTION = "EDIT";
    private static final String DELETE_JOB_ACTION = "DELETE";
    private static final String RUN_JOB_ACTION = "RUN";
    private static final String UNSCHEDULE_JOB_ACTION = "UNSCHEDULE";
    private static final String PAUSE_JOB_ACTION = "PAUSE";
    private static final String RESUME_JOB_ACTION = "RESUME";

    @Override
    public void prepare() throws Exception {
        super.prepare();
        if (isValidString(getAction()) && EDIT_JOB_ACTION.equals(getAction())) {
            loadJobDetails();
        }
    }

    @Override
    public void validate() {
        super.validate();
        if (!isValidString(getAction()) || "UPDATE".equals(getAction())) {
            if (!isValidString(getId())) {
                addActionError("Please select the scheduler first !!!");
            }
            if (!isValidString(jobName)) {
                addActionError("Please enter the jobname");
            }
            if (!isValidString(jobGroup)) {
                addActionError("Please enter a valid String for Job Group");
            }
            if (!isValidString(cronExpression)) {
                addActionError("Please enter a valid cron expression");
            } else {
                try {
                    CronTrigger cronTrigger = new CronTrigger("CT-" + jobName, jobGroup, jobName, jobGroup, cronExpression);
                    cronTrigger.getFireTimeAfter(new Date());
                } catch (Exception ex) {
                    addActionError("Please enter a valid cron expression");
                }
            }
            if(JobConstants.REPORTING_JOB_GROUP.equals(getJobGroup())){
                if (!isValidString(reportName)) {
                    addActionError("Please enter the valid reprot name");
                }
                if (!isValidString(jobType)) {
                    addActionError("Please select the job type");
                } else if ("MAILJOB".equals(jobType)) {
                    validateMailJobParams();
                } else {
                    validateFTPJobParams();
                }
            }
        }
    }

    public String createJob() {
        if (!isValidJobName() && !"UPDATE".equals(getAction())) {
            return INPUT;
        }
        try {
            if(JobConstants.REPORTING_JOB_GROUP.equals(jobGroup)){
                if ("UPDATE".equals(getAction())) {
                    unscheduleJob();
                    scheduleJob();
                } else {
                    scheduleJob();
                }
            }else{
                CronTrigger t = (CronTrigger) getScheduler().getTrigger(getTriggerName(), getTriggerGroup());
                t.setCronExpression(cronExpression);
                getScheduler().rescheduleJob(t.getName(), t.getGroup(), t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }

    private void scheduleJob() throws ParseException, SchedulerException {
        CronTrigger trigger = new CronTrigger("CT-" + jobName, jobGroup, jobName, jobGroup, cronExpression);
        if (JobConstants.MAIL_JOB.equals(jobType)) {
            MailingJobDetail detail = new MailingJobDetail(jobName, jobGroup);
            detail.setJobProperties(toAddress, ccAddress, sqlForMailJob, subject, mailContent, reportName, bugId);
            getScheduler().scheduleJob(detail, trigger);
        } else {
            FTPJobDetail detail = new FTPJobDetail(jobName, jobGroup);
            detail.setJobProperties(ftpAddress, ftpUserName, ftpPassword, sqlForFTPJob, reportName, bugId);
            getScheduler().scheduleJob(detail, trigger);
        }
    }

    public String handleJobAction() {
        try {
            JobDetail jobDetail = getScheduler().getJobDetail(jobName, jobGroup);
            if (jobDetail != null) {
                if (EDIT_JOB_ACTION.equals(getAction())) {
                    return "editJob";
                } else {
                    if (DELETE_JOB_ACTION.equals(getAction())) {
                        getScheduler().deleteJob(jobName, jobGroup);
                        addActionMessage("Successfully Deleted Job !!");
                    } else if (RUN_JOB_ACTION.equals(getAction())) {
                        getScheduler().triggerJob(jobName, jobGroup);
                        addActionMessage("Successfully Ran Job !!");
                    } else if (UNSCHEDULE_JOB_ACTION.equals(getAction())) {
                        StringBuilder sb = unscheduleJob();
                        if (sb.length() > 0) {
                            addActionMessage(sb.toString());
                        } else {
                            addActionMessage("Successfully Un-Scheduled Job !!");
                        }
                    }else if(PAUSE_JOB_ACTION.equals(getAction())){
                        getScheduler().pauseJob(jobName, jobGroup);
                        addActionMessage("Successfully Paused Job !!");
                    }else if(RESUME_JOB_ACTION.equals(getAction())){
                        getScheduler().resumeJob(jobName, jobGroup);
                        addActionMessage("Successfully Resumed Job !!");
                    }
                    return "showJobDetails";
                }
            }
        } catch (Exception qse) {
        }
        return INPUT;
    }

    private StringBuilder unscheduleJob() throws SchedulerException {
        StringBuilder sb = new StringBuilder();
        Trigger[] triggers = getScheduler().getTriggersOfJob(jobName, jobGroup);
        if (triggers != null) {
            for (Trigger trigger : triggers) {
                boolean b = getScheduler().unscheduleJob(trigger.getName(), jobGroup);
                if (!b) {
                    sb.append("Failed to remove trigger - ").append(trigger.getName());
                }
            }
        }
        return sb;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCcAddress() {
        return ccAddress;
    }

    public void setCcAddress(String ccAddress) {
        this.ccAddress = ccAddress;
    }

    public String getFtpAddress() {
        return ftpAddress;
    }

    public void setFtpAddress(String ftpAddress) {
        this.ftpAddress = ftpAddress;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public String getFtpUserName() {
        return ftpUserName;
    }

    public void setFtpUserName(String ftpUserName) {
        this.ftpUserName = ftpUserName;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getSqlForFTPJob() {
        return sqlForFTPJob;
    }

    public void setSqlForFTPJob(String sqlForFTPJob) {
        this.sqlForFTPJob = sqlForFTPJob;
    }

    public String getSqlForMailJob() {
        return sqlForMailJob;
    }

    public void setSqlForMailJob(String sqlForMailJob) {
        this.sqlForMailJob = sqlForMailJob;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    private void validateMailJobParams() {
        if (!isValidString(ccAddress)) {
            addActionError("Enter Valid CC Address");
        }
        if ((!isValidString(toAddress))) {
            addActionError("Enter Valid TO Address");
        }
        if (!isValidString(sqlForMailJob)) {
            addActionError("Enter Valid SQL for mail Job");
        }
        if (!isValidString(subject)) {
            addActionError("Please enter subject for mail");
        }
        if (!isValidString(mailContent)) {
            addActionError("Please enter the mail body content");
        }
    }

    private void validateFTPJobParams() {
        if (!isValidString(ftpAddress)) {
            addActionError("Enter Valid FTP Address");
        }
        if ((!isValidString(ftpUserName))) {
            addActionError("Enter Valid FTP User Name");
        }
        if (!isValidString(sqlForFTPJob)) {
            addActionError("Enter Valid SQL for FTP Job");
        }
        if (!isValidString(ftpPassword)) {
            addActionError("Enter Valid FTP Password");
        }
    }

    private boolean isValidJobName() {
        List<JobDetail> details;
        try {
            details = getJobDetails(getScheduler());
            for (JobDetail jobDetail : details) {
                if (jobName.equals(jobDetail.getName())) {
                    addActionError("Job Name is already in use, Please enter a different Name");
                    return false;
                }
            }
        } catch (SchedulerException ex) {
        }
        return true;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    private void loadJobDetails() throws SchedulerException {
        JobDetail jobDetail = getScheduler().getJobDetail(jobName, jobGroup);
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        bugId = jobDataMap.getString(JobConstants.BUG_ID);
        toAddress = jobDataMap.getString(JobConstants.TO_ADDRESS);
        jobType = (isValidString(toAddress)) ? JobConstants.MAIL_JOB : (isValidString(ftpAddress)) ? JobConstants.FTP_JOB : null;
        Trigger [] schTriggers = getScheduler().getTriggersOfJob(jobName, jobGroup);
        if(schTriggers.length > 1){
            this.triggers = Arrays.asList(schTriggers);
        }else{
            CronTrigger ct = (CronTrigger)schTriggers[0];
            cronExpression = ct.getCronExpression();
            triggerGroup = ct.getGroup();
            triggerName = ct.getName();
        }
        reportName = jobDataMap.getString(JobConstants.REPORT_NAME);
        if (JobConstants.MAIL_JOB.equals(jobType)) {
            ccAddress = jobDataMap.getString(JobConstants.CC_ADDRESS);
            sqlForMailJob = jobDataMap.getString(JobConstants.SQL);
            subject = jobDataMap.getString(JobConstants.SUBJECT);
            mailContent = jobDataMap.getString(JobConstants.MAIL_BODY_CONTENT);
        } else if(JobConstants.FTP_JOB.equals(jobType)){
            ftpAddress = jobDataMap.getString(JobConstants.FTP_ADDRESS);
            ftpUserName = jobDataMap.getString(JobConstants.FTP_USER_NAME);
            ftpPassword = jobDataMap.getString(JobConstants.FTP_PASSWORD);
            sqlForFTPJob = jobDataMap.getString(JobConstants.SQL);
        }


    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }
    
    public String getBugId() {
        return bugId;
    }

    public void setBugId(String bugId) {
        this.bugId = bugId;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }
    
    
}
