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
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.worklist.WorkListItemService;

public class ClaimClosedEventHandler extends DefaultEmailEventHandler {
	private ClaimService claimService;
	private OrgService orgService;
	private WorkListItemService workListItemService;
	private EventMessageUtil eventMessageUtil;

	private static final Logger logger = Logger
			.getLogger(ClaimClosedEventHandler.class);

	public Object createMessage(NotificationEvent event) {
		return createEmailMessage(event);
	}

	public List<NotificationMessage> createEmailMessage(NotificationEvent event) {
		List<NotificationMessage> messageList = new ArrayList<NotificationMessage>();
		NotificationMessage emailMessage = null;
		Claim claim;
		HashMap<String, Object> paramMap = event.getParameterMap();
		Long claimId = null;
		claimId = Long.valueOf((String) paramMap.get("id"));

		if (claimId != null) {
			claim = claimService.findClaim(claimId);
			if (claim != null) {
				List<User> actorList = workListItemService
						.findAllParticipantsForClaim(claim.getId());
				for (User user : actorList) {
					if (user != null) {
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
									eventMessageUtil.getClaimParamForMessage(
											user, claim));
							messageList.add(emailMessage);
						} else {
							// if user has not subscribed for the event we do
							// not want to pick this emailEvent record again
							event.setNumberOfTrials(3);
						}
					} // user null check
				}
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
