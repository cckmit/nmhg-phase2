package tavant.twms.notification.handlers;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import tavant.twms.domain.email.NotificationService;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.notification.NotificationEvent;
import tavant.twms.domain.notification.NotificationMessage;
import tavant.twms.security.SecurityHelper;

public class EventProcessorImpl implements EventProcessor {
	
	private EventService eventService;
	private NotificationService notificationService;
	private HashMap<String, EmailEventHandler> eventHandlerMap;
	private static final Logger logger = Logger.getLogger(EventProcessorImpl.class);

    private TransactionTemplate transactionTemplate;
	private SecurityHelper securityHelper;
	
	public void processPendingEvent() {
		securityHelper.populateSystemUser();

        for (NotificationEvent event : eventService.fetchPendingEvent()) {
            try {
                processEvent(event);
                event.setPending(false);
            } catch(Exception e) {
                logger.error("Error creating message for eventId:" + event.getId(), e);
            } finally {
                event.setNumberOfTrials(event.getNumberOfTrials() + 1);
            }

            try {
                eventService.update(event);
            } catch (Exception ignored) {
                logger.error("Error updating event : " + event, ignored);
            }
        }
	}

    @SuppressWarnings("unchecked")
    protected void processEvent(final NotificationEvent detachedEvent) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                NotificationEvent attachedEvent = eventService.findById(detachedEvent.getId());

                if (logger.isInfoEnabled()) {
                    logger.info("Processing pending event : " + attachedEvent);
                }

                try {
                    String eventName = attachedEvent.getEventName();
                    EventHandler eventHandler = getEventHandler(eventName);
                    List<NotificationMessage> notificationMessages =
                            (List<NotificationMessage>) eventHandler.createMessage(attachedEvent);

                    if (!CollectionUtils.isEmpty(notificationMessages)) {
                        notificationService.saveEmailMessageList(notificationMessages);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e); // To initiate tx rollback.
                }
            }
        });
    }
	
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	/**
	 * Returns appropriate handler for the given event name.
	 * If no handler could be found for given event name then DefaulEmailEventHandler is returned 
	 * @param eventName
	 * @return
	 */
	private EventHandler getEventHandler(String eventName){		
		EventHandler eventHandler = eventHandlerMap.get(eventName);
		if(eventHandler == null){
			eventHandler = eventHandlerMap.get("defaultHandler");
		}
		return eventHandler;
	}

	public EventService getEventService() {
		return eventService;
	}

	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public HashMap<String, EmailEventHandler> getEventHandlerMap() {
		return eventHandlerMap;
	}

	public void setEventHandlerMap(
			HashMap<String, EmailEventHandler> eventHandlerMap) {
		this.eventHandlerMap = eventHandlerMap;
	}

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}
