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
public class FTPJobDetail extends JobDetail{


    public FTPJobDetail(String name, String group) {
        super(name, group, ReportJob.class);
    }
    
    public void setJobProperties(String ftpAddress, String ftpUserName, 
            String ftpPassword, String sql, String reportName, String bugId){
        getJobDataMap().put(JobConstants.FTP_ADDRESS, ftpAddress);
        getJobDataMap().put(JobConstants.FTP_USER_NAME, ftpUserName);
        getJobDataMap().put(JobConstants.FTP_PASSWORD, ftpPassword);
        getJobDataMap().put(JobConstants.REPORT_NAME, reportName);
        getJobDataMap().put(JobConstants.SQL, sql);
        getJobDataMap().put(JobConstants.REPORT_TYPE, JobConstants.FTP);
        getJobDataMap().put(JobConstants.BUG_ID, bugId);
    }

}
