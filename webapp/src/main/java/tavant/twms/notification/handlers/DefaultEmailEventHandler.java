package tavant.twms.notification.handlers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import tavant.twms.domain.notification.EventSubscriptionService;
import tavant.twms.domain.notification.NotificationMessage;
import tavant.twms.domain.notification.NotificationEvent;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.orgmodel.User;


public class DefaultEmailEventHandler extends EmailEventHandler {
	
	private EventSubscriptionService eventSubscriptionService;
	
	private static final Logger logger = Logger.getLogger(DefaultEmailEventHandler.class);
	
	public List<NotificationMessage> createEmailMessage(NotificationEvent emailEvent) {
		// Child specific implementation is required
		return null;
	}
	
	/**
	 * Checks if user has subscribed for the event passed
	 * @param user
	 * @param emailEvent
	 * @return
	 *  true: if user has subscribed for the vent
	 *  false: if user has not subscribed for the event 
	 */	
	public boolean checkUserSubscriptionForEvent(User user, NotificationEvent emailEvent){
		boolean userSubscribedForEvent = false;
		Set<EventState> eventStates = new HashSet<EventState>();
		if(user != null)
		{
			//retrieve the set for the subscribed states
			eventStates = new HashSet<EventState>(eventSubscriptionService.getSubscribedEventsForUser(user));
			if ( eventStates != null && eventStates.size() > 0)
			{
				EventState currentState = null;
				//iterate over the iterator and ensure that user is subscribed to the event and then only the 
				//mail should be sent to user else there is no point in doing that
				for(Iterator<EventState> stateIte = eventStates.iterator(); stateIte.hasNext();)
				{
					currentState = stateIte.next();
					if(currentState.getName().equalsIgnoreCase(emailEvent.getEventName()))
					{
						userSubscribedForEvent = true;
						logger.info("User " + user.getName() + " was subscribed to event "+ emailEvent.getEventName());
						break;					
					}
				}			
			}
		}
		return userSubscribedForEvent;
	}

	public EventSubscriptionService getEventSubscriptionService() {
		return eventSubscriptionService;
	}

	public void setEventSubscriptionService(
			EventSubscriptionService eventSubscriptionService) {
		this.eventSubscriptionService = eventSubscriptionService;
	}
	
}
