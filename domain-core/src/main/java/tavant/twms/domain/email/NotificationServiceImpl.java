package tavant.twms.domain.email;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import tavant.twms.domain.notification.MessageState;
import tavant.twms.domain.notification.NotificationMessage;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.security.SecurityHelper;



public class NotificationServiceImpl implements NotificationService {
	private EmailMessageRepository emailMessageRepository;
	private SendEmailService sendEmailService;
	private String fromAddress;
	private String externalUrlForEmail;
	private SecurityHelper securityHelper;
	
	private static final Logger logger = Logger.getLogger(NotificationServiceImpl.class);
	
	public void sendPendingMail(){
		securityHelper.populateSystemUser();
		List<NotificationMessage> emailMessageList = emailMessageRepository.getPendingEmailMessage();
		
		if(emailMessageList !=null && emailMessageList.size()>0){			
			for (NotificationMessage emailMessage : emailMessageList) {
				
				HashMap<String, Object> paramMap = emailMessage.getParameterMap();
				try
				{
                    paramMap.put("url", externalUrlForEmail);
                    sendEmailService.sendEmail(fromAddress, emailMessage.getRecipient(), paramMap.get("subject").toString(), emailMessage.getMessageTemplate(), paramMap);					
					emailMessage.setMessageState(MessageState.SENT);
					emailMessage.setNumberOfTrials(emailMessage.getNumberOfTrials() + 1);
                }
				catch(Exception ex)
				{
					emailMessage.setMessageState(MessageState.FAILED);
                    emailMessage.setException(ex.getMessage());
                    emailMessage.setNumberOfTrials(emailMessage.getNumberOfTrials() + 1);
                    //log the error message in the log my friends.
					logger.error(ex.toString());
				}
				
				//update the email message as SENT since the message has been sent successfully.
				emailMessageRepository.updateEmailMessage(emailMessage);
			}
		}
	}
	
	
	public void saveEmailMessage(NotificationMessage emailMessage){
		emailMessageRepository.save(emailMessage);
	}
	
	public void saveEmailMessageList(List<NotificationMessage> emailMessageList){
		emailMessageRepository.saveAllNotificationMessages(emailMessageList);
	}
	
	public EmailMessageRepository getEmailMessageRepository() {
		return emailMessageRepository;
	}

	public void setEmailMessageRepository(
			EmailMessageRepository emailMessageRepository) {
		this.emailMessageRepository = emailMessageRepository;
	}

	public SendEmailService getSendEmailService() {
		return sendEmailService;
	}

	public void setSendEmailService(SendEmailService sendEmailService) {
		this.sendEmailService = sendEmailService;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getExternalUrlForEmail() {
		return externalUrlForEmail;
	}

	public void setExternalUrlForEmail(String externalUrlForEmail) {
		this.externalUrlForEmail = externalUrlForEmail;
	}	
	
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
	
}
