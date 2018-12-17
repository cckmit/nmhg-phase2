package tavant.twms.web.common;

import tavant.twms.domain.orgmodel.EventState;

/**
 * This is just an object to populate on screen whether the event is subscribed to or not.
 * 
 * @author priyank.gupta
 *
 */
public class EmailNotificationSubscriptionObject 
{
	private EventState eventState;
	
	private Boolean isSubscribed = false;
	
	/**
	 * Default constructor
	 */
	public EmailNotificationSubscriptionObject()
	{
		
	}
	
	/**
	 * Parameterized constructor.
	 * 
	 * @param eventState
	 * @param isSubscribed
	 */
	public EmailNotificationSubscriptionObject(final EventState eventState, final Boolean isSubscribed)
	{
		this.eventState = eventState;
		this.isSubscribed = isSubscribed;
	}

	public EventState getEventState() 
	{
		return eventState;
	}

	public void setEventState(EventState eventState) 
	{
		this.eventState = eventState;
	}

	public Boolean getIsSubscribed() 
	{
		return isSubscribed;
	}

	public void setIsSubscribed(Boolean isSubscribed) 
	{
		this.isSubscribed = isSubscribed;
	}

}
