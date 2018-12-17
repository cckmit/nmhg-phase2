package tavant.twms.notification.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.email.EventMessageUtil;
import tavant.twms.domain.notification.NotificationEvent;
import tavant.twms.domain.notification.NotificationMessage;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.worklist.WorkListItemService;

public class ClaimSubmittedEventHandler extends DefaultEmailEventHandler {
	private ClaimService claimService;
	private OrgService orgService;
	private WorkListItemService workListItemService;
	private EventMessageUtil eventMessageUtil;
	private static final Logger logger = Logger.getLogger(ClaimSubmittedEventHandler.class);

	public Object createMessage(NotificationEvent event) {
		return createEmailMessage(event);
	}

	public List<NotificationMessage> createEmailMessage(NotificationEvent event) {
        List<NotificationMessage> messageList = new ArrayList<NotificationMessage>();
        HashMap<String, Object> paramMap = event.getParameterMap();
        Long claimId = Long.valueOf((String) paramMap.get("id"));
        if (claimId != null) {
            Claim claim = claimService.findClaim(claimId);
            if (claim != null) {
                if (EventState.CLAIM_FORWARDED.equals(event.getEventName())) {
                    checkEmailSubscriptions(claim.getAssignToUser(), event, claim, messageList);
                } else {
                    List<User> actorList = workListItemService.findAllActorForClaim(claim.getId());
                    for (User user : actorList) {
                        if (user.getEmail() == null) {
                            logger.error("ERROR:::User :" + user.getName() + " does not have a valid email address, event will not be created!!");
                            continue;
                        }
                        checkEmailSubscriptions(user, event, claim, messageList);
                    }
                }
            }
        }
        return messageList;
    }

	private List<NotificationMessage> checkEmailSubscriptions(User user, NotificationEvent event,Claim claim, List<NotificationMessage> messageList) {
		if(checkUserSubscriptionForEvent(user, event)){
            NotificationMessage emailMessage = new NotificationMessage();
			emailMessage.setNotifcationMessageParam(user.getEmail(),
					eventMessageUtil.getEmailTemplate(event.getEventName(), user,
					claim.getBusinessUnitInfo()), eventMessageUtil.getClaimParamForMessage(user, claim));
			messageList.add(emailMessage);
		}
		else{
			// if user has not subscribed for the event we do
			// not want to pick this emailEvent record again
			event.setNumberOfTrials(3);
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