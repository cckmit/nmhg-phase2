package tavant.twms.domain.email;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.notification.NotificationMessage;
import tavant.twms.infra.GenericRepository;


public interface EmailMessageRepository  extends GenericRepository<NotificationMessage, Long>
{
	@Transactional(readOnly = true)
	public List<NotificationMessage> getPendingEmailMessage();
	
	@Transactional(readOnly = false)
	public void saveAllNotificationMessages(List<NotificationMessage> notificationMessageList);
	
	@Transactional(readOnly = false)
	public void updateEmailMessage(NotificationMessage emailMessage);

    @Transactional(readOnly = true)
    public List<NotificationMessage> getNewPendingRecoveryEmailMessage(String recoveryClaimId);

    @Transactional(readOnly = true)
    public List<NotificationMessage> getPendingRecoveryEmailMessage(String recoveryClaimId);

    @Transactional(readOnly = true)
    public List<NotificationMessage> getAllPendingRecoveryEmailMessage(String recoveryClaimId);
    
    @Transactional(readOnly = true)
    public List<NotificationMessage> getAllPendingEmailMessages();
}
