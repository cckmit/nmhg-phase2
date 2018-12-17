package tavant.twms.notification.handlers;

import tavant.twms.domain.notification.NotificationEvent;

public interface EventHandler {
	public Object createMessage(NotificationEvent event);
}
