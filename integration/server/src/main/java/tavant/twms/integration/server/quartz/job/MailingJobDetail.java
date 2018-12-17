/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.quartz.job;

import org.quartz.JobDetail;

/**
 *
 * @author prasad.r
 */
public class MailingJobDetail extends JobDetail{
    

    public MailingJobDetail(String name, String group) {
        super(name, group, ReportJob.class);
    }

    public void setJobProperties(String toAddress, String fromAddress, String sql,
            String subject, String mailBody, String reportName, String bugId){
        getJobDataMap().put(JobConstants.CC_ADDRESS, fromAddress);
        getJobDataMap().put(JobConstants.TO_ADDRESS, toAddress);
        getJobDataMap().put(JobConstants.SQL, sql);
        getJobDataMap().put(JobConstants.SUBJECT, subject);
        getJobDataMap().put(JobConstants.MAIL_BODY_CONTENT, mailBody);
        getJobDataMap().put(JobConstants.REPORT_NAME, reportName);
        getJobDataMap().put(JobConstants.REPORT_TYPE, JobConstants.MAIL);
        getJobDataMap().put(JobConstants.BUG_ID, bugId);
    }
    
}
