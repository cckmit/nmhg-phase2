/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.common;

import java.io.File;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.StringUtils;

/**
 *
 * @author prasad.r
 */
public class SendEmailServiceImpl implements SendEmailService {

    private MimeMessageHelper mimeMessageHelper;
    private JavaMailSenderImpl mailSender;
    private VelocityEngine velocityEngine;

    public void sendEmail(final String from, final String to,
            final String subject, final String template,
            final Map<String, Object> messageParams) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(
                        mimeMessage);
                messageHelper.setTo(to);
                messageHelper.setFrom(from); // could be parameterized...
                messageHelper.setSubject(subject);
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        SendEmailServiceImpl.this.velocityEngine, template.toLowerCase(), messageParams);
                messageHelper.setText(text, true);
            }
        };
        this.mailSender.send(preparator);
    }

    public void sendEmailWithAttachmentFile(final String from, final String to,
            final String subject, final String template,
            final Map<String, Object> messageParams, final String attachmentFile) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(
                        mimeMessage, true);
                messageHelper.setTo(to);
                messageHelper.setFrom(from);
                messageHelper.setSubject(subject);
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        SendEmailServiceImpl.this.velocityEngine, template.toLowerCase(), messageParams);
                messageHelper.setText(text, true);

                FileSystemResource fileSystemResource = new FileSystemResource(
                        new File(attachmentFile));
                messageHelper.addAttachment("RegistrationNumbers.pdf", fileSystemResource);
            }
        };
        this.mailSender.send(preparator);
    }

    public void sendEmailWithAttachmentFile(final String from, final String to,
            final String subject, final String template,
            final Map<String, Object> messageParams, final File attachmentFile) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(
                        mimeMessage, true);
                messageHelper.setTo(to);
                messageHelper.setFrom(from);
                messageHelper.setSubject(subject);
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        SendEmailServiceImpl.this.velocityEngine, template.toLowerCase(), messageParams);
                messageHelper.setText(text, true);

                FileSystemResource fileSystemResource = new FileSystemResource(
                        attachmentFile);
                messageHelper.addAttachment(attachmentFile.getName(),
                        fileSystemResource);
            }
        };
        this.mailSender.send(preparator);
    }

    public void sendEmailWithAttachmentFileStream(final String from,
            final String to, final String subject, final String template,
            final Map<String, Object> messageParams,
            final String attachmentFileName,
            final InputStreamSource inputStreamSource, final String contentType) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(
                        mimeMessage, true);
                messageHelper.setTo(to);
                messageHelper.setFrom(from);
                messageHelper.setSubject(subject);
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        SendEmailServiceImpl.this.velocityEngine, template.toLowerCase(), messageParams);
                messageHelper.setText(text, true);
                messageHelper.addAttachment(attachmentFileName,
                        inputStreamSource, contentType);
            }
        };
        this.mailSender.send(preparator);
    }

    public MimeMessageHelper getMimeMessageHelper() {
        return this.mimeMessageHelper;
    }

    public void setMimeMessageHelper(MimeMessageHelper mimeMessageHelper) {
        this.mimeMessageHelper = mimeMessageHelper;
    }

    public JavaMailSenderImpl getMailSender() {
        return this.mailSender;
    }

    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public VelocityEngine getVelocityEngine() {
        return this.velocityEngine;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public void sendEmail(final String from, final String to, final String cc, final String subject, 
            final String mailContent) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(
                        mimeMessage);
                messageHelper.setTo(StringUtils.tokenizeToStringArray(to, ","));
                if(cc != null)
                    messageHelper.setCc(StringUtils.tokenizeToStringArray(cc, ","));
                messageHelper.setFrom(from); 
                messageHelper.setSubject(subject);
                messageHelper.setText(mailContent, true);
            }
        };
        this.mailSender.send(preparator);
        
    }

    public void sendEmailWithAttachmentFile(final String from, final String to, final String cc, final String subject, 
            final String mailContent, final File attachmentFile) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(
                        mimeMessage,true);
                messageHelper.setTo(StringUtils.tokenizeToStringArray(to, ","));
                if(cc != null)
                    messageHelper.setCc(StringUtils.tokenizeToStringArray(cc, ","));
                messageHelper.setFrom(from); 
                messageHelper.setSubject(subject);
                messageHelper.setText(mailContent, true);
                messageHelper.addAttachment(attachmentFile.getName(), attachmentFile);
            }
        };
        this.mailSender.send(preparator);
        
    }
}
