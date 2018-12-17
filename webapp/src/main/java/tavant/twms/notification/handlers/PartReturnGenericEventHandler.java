package tavant.twms.notification.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.email.EventMessageUtil;
import tavant.twms.domain.notification.NotificationEvent;
import tavant.twms.domain.notification.NotificationMessage;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.worklist.WorkListItemService;

/**
 * @author priyank.gupta
 * 	
 */
public class PartReturnGenericEventHandler extends DefaultEmailEventHandler {

	private ClaimService claimService;
	private OrgService orgService;
	private WorkListItemService workListItemService;
	private EventMessageUtil eventMessageUtil;

	private static final Logger logger = Logger
			.getLogger(PartReturnGenericEventHandler.class);

	public Object createMessage(NotificationEvent event) {
		return createEmailMessage(event);
	}

	public List<NotificationMessage> createEmailMessage(NotificationEvent event) {
		List<NotificationMessage> messageList = new ArrayList<NotificationMessage>();
		NotificationMessage emailMessage = null;
		Long claimId = null;
		HashMap<String, Object> paramMap = event.getParameterMap();
		Long partReturnId = null;
		Claim claim;
		partReturnId = Long.valueOf((String) paramMap.get("taskInstanceId"));
		claimId = Long.valueOf((String) paramMap.get("claimId"));

		//get the actors associated to the part return
		List<User> actorList = workListItemService.findAllActorForPartReturn(partReturnId);
		List<User> claimActorList = workListItemService.findAllActorForClaim(claimId);
		
		//we'll consider claim actors also. Also we have to make sure that we have unique actors only in the list
		if(claimActorList != null && claimActorList.size() > 0)
		{
			User currentUser = null;
			//compare claims associated users one by one and if they don't exist add them in actor list
			for(Iterator<User> ite = claimActorList.iterator(); ite.hasNext();)
			{
				currentUser = ite.next();
				if(!actorList.contains(currentUser))
				{
					actorList.add(currentUser);
				}
			}
		}
		
		//we need claim to decide which business unit to use
		if (claimId != null) 
		{
			claim = claimService.findClaim(claimId);

			if (claim != null) 
			{
				for (User user : actorList) 
				{
					if (user != null) 
					{
						// well if user doesn't have an email I wonder what he
						// expects? Get a notification in his dreams?? Bunch of
						// dimwits I say!
						if (user.getEmail() == null) {
							logger
									.error("ERROR:::User :"
											+ user.getName()
											+ " does not have a valid email address, event will not be created!!");
							continue;
						}

						// create emailMessage only if user has subscribed for
						// the event
						if (checkUserSubscriptionForEvent(user, event)) {
							emailMessage = new NotificationMessage();
							emailMessage.setNotifcationMessageParam(user
									.getEmail(), eventMessageUtil
									.getEmailTemplate(event.getEventName(),
											user, claim.getBusinessUnitInfo()),
									eventMessageUtil
											.getPartReturnParamForMessage(user,
													paramMap, claim));
							messageList.add(emailMessage);
						} else {
							// if user has not subscribed for the event we do
							// not want to pick this emailEvent record again
							event.setNumberOfTrials(3);
						}
					} // user null check
				}
				// } //claim Audit check
			}
		}
		return messageList;

	}

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public WorkListItemService getWorkListItemService() {
		return workListItemService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public EventMessageUtil getEventMessageUtil() {
		return eventMessageUtil;
	}

	public void setEventMessageUtil(EventMessageUtil eventMessageUtil) {
		this.eventMessageUtil = eventMessageUtil;
	}

}
