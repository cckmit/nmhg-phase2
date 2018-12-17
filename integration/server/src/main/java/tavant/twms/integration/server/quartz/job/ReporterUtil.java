/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.quartz.job;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.mail.MessagingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.quartz.JobDataMap;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import tavant.twms.integration.server.common.SendEmailService;

/**
 *
 * @author prasad.r
 */
public class ReporterUtil {

    private static final Log logger = LogFactory.getLog("reportingLog");

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("_MM_dd_yyyy_HH_mm_ss_z");
    
    /**
     * Creates a CSV report by firing SQL on a given connection and returns the
     * file name
     *
     * @param connection
     * @param sql
     * @return
     */
    static void createCSVReport(Connection connection, String sql, File file) {
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(sql);
            resultSet = ps.executeQuery();
            FileWriter fileWriter = new FileWriter(file);
            CSVWriter writer = new CSVWriter(fileWriter, ',');
            writer.writeAll(resultSet, true);
            writer.close();
            fileWriter.close();
        } catch (Exception ex) {
            logger.error("Error Creating report", ex);
            throw new RuntimeException("Error Creating report", ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                // ignore Exception
            }
        }

    }

    static void mailReport(File f, String ccAddress, String toAddress,
            String host, String subject, String mailBody, String bugId, SendEmailService sendEmailService) throws MessagingException, IOException {
        if (bugId != null && !"".equals(bugId)) {
            subject = "(" + bugId + ") " + subject;
            toAddress = toAddress + "," + bugId.substring(0, bugId.indexOf("-")).toLowerCase() + "-issues@tavant.com";
        }
        sendEmailService.sendEmailWithAttachmentFile(JobConstants.FROM_MAILING_ADDRESS, 
                toAddress, ccAddress, subject, mailBody, f);
    }

 
    private static void createAndSendCSVReport(Connection connection, JobDataMap dataMap, File f, SendEmailService sendEmailService) {
        String reportType = dataMap.getString(JobConstants.REPORT_TYPE);
        String sql = dataMap.getString(JobConstants.SQL);
        String bugId = dataMap.getString(JobConstants.BUG_ID);
        String host = dataMap.getString(JobConstants.EMAIL_SERVER);
        try {
            createCSVReport(connection, sql, f);
            if (JobConstants.MAIL.equals(reportType)) {
                String ccAddress = dataMap.getString(JobConstants.CC_ADDRESS);
                String toAddress = dataMap.getString(JobConstants.TO_ADDRESS);
                String subject = dataMap.getString(JobConstants.SUBJECT);
                String mailBodyContent = dataMap.getString(JobConstants.MAIL_BODY_CONTENT);
                mailReport(f, ccAddress, toAddress, host, subject, mailBodyContent, bugId, sendEmailService);
            } else {
                String ftpAddress = dataMap.getString(JobConstants.FTP_ADDRESS);
                String ftpUserName = dataMap.getString(JobConstants.FTP_USER_NAME);
                String ftpPassword = dataMap.getString(JobConstants.FTP_PASSWORD);
                ftpReport(f, ftpAddress, ftpUserName, ftpPassword, bugId, host, sendEmailService);
            }
        } catch (Exception e) {
            logger.error("Error in creating and sending reprot", e);
            throw new RuntimeException("Error in creating and sending reprot", e);
        }
    }

    static void ftpReport(File f, String ftpAddress, String ftpUserName, String ftpPassword,
            String bugId, String host, SendEmailService sendEmailService) throws MessagingException, SocketException, IOException {
        String toAddress = bugId.substring(0, bugId.indexOf("-")).toLowerCase() + "-issues@tavant.com";
        FTPClient fTPClient = new FTPClient();
        fTPClient.connect(ftpAddress);
        boolean loggedIn = fTPClient.login(ftpUserName, ftpPassword);
        if (!loggedIn) {
            throw new RuntimeException("Couldn't log in to FTP Server !!!");
        }
        FileInputStream fis = new FileInputStream(f);
        boolean success = fTPClient.storeFile(f.getName(), fis);
        if (!success) {
            throw new RuntimeException("Failed to store file on FTP Server !!!");
        }
        fis.close();
        fTPClient.logout();
        fTPClient.disconnect();
        sendEmailService.sendEmailWithAttachmentFile(JobConstants.FROM_MAILING_ADDRESS, toAddress, null, bugId, "Report is FTPed to " + ftpAddress, f);
    }


    public static void createAndSendCSVReport(final JobDataMap dataMap, final ApplicationContext springAppContext) {
        JdbcTemplate jdbcTemplate = (JdbcTemplate) springAppContext.getBean("jdbcTemplate");
        String fileName = dataMap.getString(JobConstants.REPORT_NAME) + DATE_FORMAT.format(new Date()) + ".csv";
        final SendEmailService sendEmailService = (SendEmailService) springAppContext.getBean("sendEmailService");
        final File f = new File(System.getProperty("java.io.tmpdir"),fileName);
        jdbcTemplate.execute(new ConnectionCallback() {

            public Object doInConnection(Connection con) throws SQLException, DataAccessException {
                createAndSendCSVReport(con, dataMap, f, sendEmailService);
                return null;
            }
        });
        boolean delete = f.delete();
        if(!delete){
            logger.error("Unable to delete generated reprot file - " + f.getAbsolutePath());
        }
    }
}
