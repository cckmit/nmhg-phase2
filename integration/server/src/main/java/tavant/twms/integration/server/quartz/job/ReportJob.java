/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.quartz.job;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import tavant.twms.integration.server.common.SendEmailService;

/**
 *
 * @author prasad.r
 */
public class ReportJob implements StatefulJob {

    private static final Log logger = LogFactory.getLog("reportingLog"); 

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail detail = context.getJobDetail();
        JobDataMap dataMap = detail.getJobDataMap();
        String reportType = dataMap.getString(JobConstants.REPORT_TYPE);
        String bugId = dataMap.getString(JobConstants.BUG_ID);
        String toAddress=dataMap.getString(JobConstants.TO_ADDRESS);
        if(StringUtils.isEmpty(toAddress))
        	toAddress=JobConstants.TO_FTPMAILING_ADDRESS;
        	
        SendEmailService sendEmailService = null;
        try {
            ApplicationContext springAppContext = (ApplicationContext) context.getScheduler().getContext().get("springApplicationContext");
            sendEmailService = (SendEmailService) springAppContext.getBean("sendEmailService");
            if (JobConstants.MAIL.equals(reportType)
                    || JobConstants.FTP.equals(reportType)) {
                ReporterUtil.createAndSendCSVReport(dataMap, springAppContext);
            }
            else{
                throw new JobExecutionException("Unknown reporting type - " + reportType);
            }

        } catch (Exception ex) {
            logger.error("Failed to run job !!", ex);
            if(sendEmailService != null){
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw, true));
                sendEmailService.sendEmail(JobConstants.FROM_MAILING_ADDRESS
                        , toAddress, null, "Failed to execute Job for bug : " + bugId, sw.toString());
            }
        }
    }

}
