/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.common;

import java.io.File;
import java.util.Map;
import org.springframework.core.io.InputStreamSource;

/**
 *
 * @author prasad.r
 */
public interface SendEmailService {
    
	void sendEmail(String from, String to, String subject, String template,Map<String, Object> messageParams);

	void sendEmailWithAttachmentFile( String from, String to, String subject, String template, Map<String, Object> messageParams,  String attachmentFile);
	
	void sendEmailWithAttachmentFile( String from, String to, String subject, String template, Map<String, Object> messageParams,  File attachmentFile);
	
	void sendEmailWithAttachmentFileStream( String from, String to,  String subject,  String template,Map<String, Object> messageParams, String attachmentFileName, InputStreamSource inputStreamSource,  String contentType);
    
    void sendEmail(String from, String to, String cc, String subject, String mailContent);
    
    void sendEmailWithAttachmentFile(String from, String to, String cc, String subject, String mailContent, File attachmentFile);
    
}
