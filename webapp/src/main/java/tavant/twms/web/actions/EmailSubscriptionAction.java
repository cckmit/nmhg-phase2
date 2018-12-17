package tavant.twms.web.actions;

import com.opensymphony.xwork2.Preparable;

import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.notification.EventSubscriptionService;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.web.common.EmailNotificationSubscriptionObject;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.*;

@SuppressWarnings("serial")
public class EmailSubscriptionAction extends I18nActionSupport implements Preparable 
{

	private List<EventState> roleSubscribedEvents;
	
	private List<EventState> userSubscribedEvents;
	
	private List<EmailNotificationSubscriptionObject> listOfNotificationsToDisplay = new ArrayList<EmailNotificationSubscriptionObject>();
	
	private EventSubscriptionService eventSubscriptionService;
	
	private HashMap<String, Boolean> eventSubscription = new HashMap<String, Boolean>();
	
	/**
	 * This method creates the list containing event states which are subscribed and not subscribed.
	 */
	public String prepareForUIDisplay()
	{
		//get all the roles of the user and then get all the distinct events associated with those roles
		//so that we can display the same on UI when required.
		final User currentUser = getLoggedInUser();
		EventState roleEventState = null;
		EventState userEventState = null;
		boolean isChecked = false;
		roleSubscribedEvents = eventSubscriptionService.getSubscribedEventsForRoles(
				orgService.getRolesForUser(currentUser));
		//This is a good coding practice as we don't want this list to be modified at all
		roleSubscribedEvents =Collections.unmodifiableList(roleSubscribedEvents);
		
		userSubscribedEvents = eventSubscriptionService.getSubscribedEventsForUser(currentUser);
		//This is a good coding practice as we don't want this list to be modified at all
		userSubscribedEvents = Collections.unmodifiableList(userSubscribedEvents);
		
		//clear the collection before starting looping as we always start afresh
		listOfNotificationsToDisplay.clear();
		
		//now that we have both the list lets see what we need to show as checked and what we need to show as 
		//unchecked or un-subscribed.
		for(Iterator<EventState> roleSubEventsIte = roleSubscribedEvents.iterator(); roleSubEventsIte.hasNext();)
		{
			//get the event state subscribed to roles
			roleEventState = roleSubEventsIte.next();
			if (!getCurrentBusinessUnit().getName().equals(AdminConstants.NMHGAMER)
					|| !(roleEventState.getName().equals(EventState.SERVICE_MANAGER_RESPONSE)
							|| roleEventState.getName().equals(EventState.SERVICE_MANAGER_REVIEW)
							|| roleEventState.getName().equals(EventState.WAITING_FOR_LABOR))) {
				isChecked = false;
				for (Iterator<EventState> userSubEventsIte = userSubscribedEvents
						.iterator(); userSubEventsIte.hasNext();) {
					//get the event state subscribed to user
					userEventState = userSubEventsIte.next();
					if (userEventState.getId().equals(roleEventState.getId())) {
						isChecked = true;
						break;
					}
				}
				//If we found that the event state was equal with one of the subscribed ones; then we must mark it
				//as checked else we should just leave it at that and mark it as unchecked in the list
				if (isChecked) {
					listOfNotificationsToDisplay
							.add(new EmailNotificationSubscriptionObject(
									roleEventState, new Boolean(true)));
				} else {
					listOfNotificationsToDisplay
							.add(new EmailNotificationSubscriptionObject(
									roleEventState, new Boolean(false)));
				}
			}
		}
		
		return SUCCESS;
	}
	
	/**
	 * This method will recreate the mappings of User and it's subscribed events based on the fresh selections
	 * made by user on the screen.
	 */
	public String saveOrUpdateSubscriptions()
	{
		User currentUserToUpdate = orgService.findUserById(getLoggedInUser().getId());
		EmailNotificationSubscriptionObject emailSubsObject = null;
		EventState currentEventStateObj = null;
		Set<EventState> eventStateSet = new HashSet<EventState>();
		
		//get the list of all the event associated to the roles.
		roleSubscribedEvents = eventSubscriptionService
				.getSubscribedEventsForRoles(orgService.getRolesForUser(getLoggedInUser()));
		roleSubscribedEvents = Collections.unmodifiableList(roleSubscribedEvents);
				
		if(listOfNotificationsToDisplay != null && listOfNotificationsToDisplay.size() > 0)
		{
			for(Iterator<EmailNotificationSubscriptionObject> ite = listOfNotificationsToDisplay.iterator(); ite.hasNext();)
			{
				emailSubsObject =  ite.next();
				for(Iterator<EventState> rolesubsIterator = roleSubscribedEvents.iterator(); rolesubsIterator.hasNext();)
				{
					currentEventStateObj = rolesubsIterator.next();
					if(emailSubsObject.getIsSubscribed() && currentEventStateObj.getId().equals(emailSubsObject.getEventState().getId()))
					{
						eventStateSet.add(currentEventStateObj);
					}					
				}
			}
		}
		//update the user will latest even state set so that mappings are recreated or mapped.
		currentUserToUpdate.setEventState(eventStateSet);
		
		//Now persist the mappings to database.
		eventSubscriptionService.updateUserEventStateMapping(currentUserToUpdate);
		
		//populate the data for display on UI
		prepareForUIDisplay();
				
		addActionMessage("label.email.subscriptionSuccess");
		//And, yes my dear friends we are done persisting, whatever it is that we are persisting!
		return SUCCESS;
	}
	
	/**
	 * Just return success message as we do all preparation in our prepare method.
	 * 
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	public String execute()
	{
		return SUCCESS;
	}

	/**
	 * Getter for Event State
	 * 
	 * @returnany issues
	 */
	public List<EventState> getRoleSubscribedEvents() {
		return roleSubscribedEvents;
	}

	/**
	 * Setter for Event State
	 * 
	 * @return
	 */
	public void setRoleSubscribedEvents(List<EventState> roleSubscribedEvents) {
		this.roleSubscribedEvents = roleSubscribedEvents;
	}

	/**
	 * Getter for event subscription service
	 * 
	 * @return
	 */
	public EventSubscriptionService getEventSubscriptionService() 
	{
		return eventSubscriptionService;
	}

	/**
	 * Setter for event subscription service
	 * 
	 * @param eventSubscriptionService
	 */
	public void setEventSubscriptionService(EventSubscriptionService eventSubscriptionService) 
	{
		this.eventSubscriptionService = eventSubscriptionService;
	}

	/**
	 * @return
	 */
	public List<EventState> getUserSubscribedEvents() 
	{
		return userSubscribedEvents;
	}

	/**
	 * @param userSubscribedEvents
	 */
	public void setUserSubscribedEvents(List<EventState> userSubscribedEvents) 
	{
		this.userSubscribedEvents = userSubscribedEvents;
	}

	/**
	 * @return
	 */
	public List<EmailNotificationSubscriptionObject> getListOfNotificationsToDisplay() 
	{
		return listOfNotificationsToDisplay;
	}

	/**
	 * @param listOfNotificationsToDisplay
	 */
	public void setListOfNotificationsToDisplay(List<EmailNotificationSubscriptionObject> listOfNotificationsToDisplay) 
	{
		this.listOfNotificationsToDisplay = listOfNotificationsToDisplay;
	}

	public HashMap<String, Boolean> getEventSubscription() 
	{
		return eventSubscription;
	}

	public void setEventSubscription(HashMap<String, Boolean> eventSubscription) 
	{
		this.eventSubscription = eventSubscription;
	}

	public void prepare() throws Exception 
	{
		//do nothing here as this is implemented due to interface/super-class constraints	
	}

}
