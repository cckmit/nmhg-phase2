package tavant.twms.domain.email;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.poi.util.IOUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;


import tavant.twms.domain.common.Document;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.partreturn.*;

public class SendEmailServiceImpl implements SendEmailService {
	private MimeMessageHelper mimeMessageHelper;
	private JavaMailSenderImpl mailSender;
	private VelocityEngine velocityEngine;
	protected EventService eventService;
   /* private PartReturnService partReturnService;
    private PartReplacedService partReplacedService;*/

    @ApplyEmailAspect
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
				if(messageParams.containsKey("Importance"))
				{
				    messageHelper.setPriority(1);  
				}				
			}
		};
		this.mailSender.send(preparator);
	}

    @ApplyEmailAspect
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
				if(messageParams.containsKey("Importance"))
				{
				    messageHelper.setPriority(1);  
				}
			}
		};
		this.mailSender.send(preparator);
	}

    @ApplyEmailAspect
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
				if(messageParams.containsKey("Importance"))
				{
				    messageHelper.setPriority(1);  
				}
			}
		};
		this.mailSender.send(preparator);
	}

    @ApplyEmailAspect
	public void sendEmailWithAttachmentFileStream(final String from,
			final String to, final String subject, final String template,
			final Map<String, Object> messageParams,
			final String attachmentFileName,
			final InputStreamSource inputStreamSource, final String contentType) {
        this.sendEmailWithAttachmentFileStream(from, new String[] {to}, subject, template, messageParams, attachmentFileName, inputStreamSource, contentType);
	}

    @ApplyEmailAspect
	public void sendEmailWithAttachmentFileStream(final String from,
            final String[] to,  final String subject,  final String template,
            final Map<String, Object> messageParams,
            final String attachmentFileName,
            final InputStreamSource inputStreamSource,  final String contentType) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(
                        mimeMessage, true, "UTF-8");
                messageHelper.setTo(to);
                messageHelper.setFrom(from);
                messageHelper.setSubject(subject);
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        SendEmailServiceImpl.this.velocityEngine, template.toLowerCase(), messageParams);
                messageHelper.setText(text, true);
                messageHelper.addAttachment(attachmentFileName,
                        new ByteArrayResource(IOUtils.toByteArray(inputStreamSource.getInputStream())),
                        contentType);
                if(messageParams.containsKey("Importance"))
				{
				    messageHelper.setPriority(1);  
				}
            }
        };
        this.mailSender.send(preparator);
	}


    @ApplyEmailAspect
	public void sendEmailWithMultipleAttachmentFileStream(final String from, final String to,
	        final String subject, final String template, final Map<String, Object> messageParams,final List<Document> documents)
            {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(
                        mimeMessage, true, "UTF-8");
                messageHelper.setTo(to);
                messageHelper.setFrom(from);
                messageHelper.setSubject(subject);
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        SendEmailServiceImpl.this.velocityEngine, template.toLowerCase(), messageParams);
                messageHelper.setText(text, true);
                if(messageParams.containsKey("Importance"))
				{
				    messageHelper.setPriority(1);  
				}
                for(Document doc:documents)
                {
                messageHelper.addAttachment(doc.getFileName(),
                        new ByteArrayResource(IOUtils.toByteArray(doc.getContent().getBinaryStream()),
                        doc.getContentType()));
                }
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
	
	/*public void createEmailEventForOverdue(Claim claim, PartReturn partReturn) {
		HashMap<String,Object> eventHashMap = new HashMap<String, Object>();
		eventHashMap.put("claimId",claim.getId().toString());
    	eventHashMap.put("partNumberString",partReturn.getOemPartReplaced().getItemReference().getReferredItem().getBrandItemNumber(claim.getBrand()));
    	eventHashMap.put("taskInstanceId", partReturn.getId().toString());
    	eventService.createEvent("partReturn", EventState.PART_MOVED_TO_OVERDUE, eventHashMap);
        updatePartReturn(claim);
	}*/

    /*private void updatePartReturn(Claim claim){
        //Update the part return status
        for(OEMPartReplaced part : claim.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced()) {
           // if(!part.getStatus().equals(PartReturnStatus.PART_MOVED_TO_OVERDUE)) {
                List<PartReturnAudit> audits = part.getPartReturnAudits();
                boolean auditFound =false;
                for(PartReturnAudit audit: audits){
                    if(audit.getPartReturnAction1() != null && audit.getPartReturnAction1().getActionTaken().equals(PartReturnStatus.PART_MOVED_TO_OVERDUE.getStatus())){
                      audit.setPartReturnAction1(new PartReturnAction(PartReturnStatus.PART_MOVED_TO_OVERDUE.getStatus(),audit.getPartReturnAction1().getValue().intValue()+1));
                      auditFound =true;
                      partReplacedService.updatePartAudit(audit);
                      break;
                    }
                }
                if(!auditFound){
                    part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_MOVED_TO_OVERDUE.getStatus(),1));
                    part.setComments(PartReturnStatus.PART_MOVED_TO_OVERDUE.getStatus());
                    partReturnService.updatePartStatus(part);
                    partReplacedService.updateOEMPartReplaced(part);
                }
           // }
        }
    }*/


    public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

    public EventService getEventService() {
        return eventService;
    }

    /* public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

    public void setPartReplacedService(PartReplacedService partReplacedService) {
        this.partReplacedService = partReplacedService;
    }*/
}
