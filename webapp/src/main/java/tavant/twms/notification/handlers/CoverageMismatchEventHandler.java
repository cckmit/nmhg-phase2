package tavant.twms.notification.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.email.EventMessageUtil;
import tavant.twms.domain.notification.EventStateService;
import tavant.twms.domain.notification.NotificationEvent;
import tavant.twms.domain.notification.NotificationMessage;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.worklist.WorkListItemService;

public class CoverageMismatchEventHandler extends DefaultEmailEventHandler {
	private ClaimService claimService;
	private OrgService orgService;
	private WorkListItemService workListItemService;  
	private EventMessageUtil eventMessageUtil;
	private EventStateService eventStateService;
	private static final Logger logger = Logger.getLogger(ClaimSubmittedEventHandler.class);
	
	public Object createMessage(NotificationEvent event) {
		return createEmailMessage(event);		
	}
	
	public List<NotificationMessage> createEmailMessage(NotificationEvent event) {
		List<NotificationMessage> messageList = new ArrayList<NotificationMessage>();
		NotificationMessage emailMessage = null;
		Claim claim;		
		HashMap<String, Object> paramMap = event.getParameterMap();
		Long claimId = null;
		claimId = Long.valueOf((String)paramMap.get("id"));
		if(claimId != null){
			claim = claimService.findClaim(claimId);
			if(claim != null){
				List<String> listOfRoles = new ArrayList<String>();
				EventState eventState = this.eventStateService.findEventStateByName(event.getEventName());
				for (Role role : eventState.getRoles()) {
					listOfRoles.add(role.getName());
				}
				String selectedBU = SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
				SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
				List<User> actorList = orgService.findAllUsersByRole(listOfRoles);
				SelectedBusinessUnitsHolder.setSelectedBusinessUnit(selectedBU);
				boolean noSubscribers = true;
				for (User user : actorList) {
					if (user == null)
						continue;
					if (user.getEmail() == null) {
						logger.error("ERROR:::User :"
								+ user.getName()
								+ " does not have a valid email address, event will not be created!!");
						continue;
					}
					// create emailMessage only if user has subscribed for event
					if (checkUserSubscriptionForEvent(user, event)) {
						emailMessage = new NotificationMessage();
						emailMessage.setNotifcationMessageParam(
								user.getEmail(), eventMessageUtil
										.getEmailTemplate(event.getEventName(),
												user,
												claim.getBusinessUnitInfo()),
								eventMessageUtil.getClaimParamForMessage(user,
										claim));
						messageList.add(emailMessage);
						noSubscribers = false;
					}
				}
				if (noSubscribers)
					event.setNumberOfTrials(3);
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

	public void setEventStateService(EventStateService eventStateService) {
		this.eventStateService = eventStateService;
	}

	public EventStateService getEventStateService() {
		return eventStateService;
	}

}