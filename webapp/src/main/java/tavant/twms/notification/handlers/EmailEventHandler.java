package tavant.twms.notification.handlers;

import java.util.List;

import tavant.twms.domain.notification.NotificationEvent;
import tavant.twms.domain.notification.NotificationMessage;

/**
 * Interface for handling different types of Events for creation of emailMessage entity 
 * @author vaibhav.fouzdar 
 */
public abstract class EmailEventHandler implements EventHandler{
	
	/**
	 * For each emailEvent it fetches other required info 
	 * and creates an emailMessage entity.   
	 * @param emailEvent
	 * @return
	 * For EntityType:
	 * Claim: ClaimEmailEventHandler
	 * Parts: PartsEmailEventHandler
	 * 
	 * If no match is found then
	 * 
	 * Default: DefaultEmailEventHandler
	 */
	public abstract List<NotificationMessage> createEmailMessage(NotificationEvent emailEvent);

	public Object createMessage(NotificationEvent event) {
		return createEmailMessage(event);
	}
}
