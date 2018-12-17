package tavant.twms.domain.email;

import java.util.List;
import java.util.Map;
import java.io.File;


import org.springframework.core.io.InputStreamSource;

import tavant.twms.domain.common.Document;



public interface SendEmailService {

	void sendEmail(String from, String to, String subject, String template,Map<String, Object> messageParams);

	void sendEmailWithAttachmentFile( String from, String to, String subject, String template, Map<String, Object> messageParams,  String attachmentFile);
	
	void sendEmailWithAttachmentFile( String from, String to, String subject, String template, Map<String, Object> messageParams,  File attachmentFile);
	
	void sendEmailWithAttachmentFileStream( String from, String to,  String subject,  String template,Map<String, Object> messageParams, String attachmentFileName, InputStreamSource inputStreamSource,  String contentType);
	
	void sendEmailWithAttachmentFileStream( String from, String[] to,  String subject,  String template,Map<String, Object> messageParams, String attachmentFileName, InputStreamSource inputStreamResource,  String contentType);
	
	public void sendEmailWithMultipleAttachmentFileStream(final String from, final String to,
            final String subject, final String template, final Map<String, Object> messageParams,final List<Document> documents);
	
	//public void createEmailEventForOverdue(Claim claim, PartReturn partReturn);
}
