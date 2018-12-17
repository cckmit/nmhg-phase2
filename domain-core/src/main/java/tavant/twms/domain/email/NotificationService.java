package tavant.twms.domain.email;

import java.util.List;

import tavant.twms.domain.notification.NotificationMessage;

public interface NotificationService {
	
	public void saveEmailMessage(NotificationMessage emailMessage);
	
	public void saveEmailMessageList(List<NotificationMessage> emailMessageList);

    public void sendPendingMail();

}
