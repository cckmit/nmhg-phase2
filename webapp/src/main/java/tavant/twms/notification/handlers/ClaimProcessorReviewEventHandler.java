package tavant.twms.notification.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.email.EventMessageUtil;
import tavant.twms.domain.notification.MessageState;
import tavant.twms.domain.notification.NotificationEvent;
import tavant.twms.domain.notification.NotificationMessage;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.worklist.WorkListItemService;

public class ClaimProcessorReviewEventHandler extends DefaultEmailEventHandler {
	private ClaimService claimService;
	private OrgService orgService;
	private WorkListItemService workListItemService;  
	private EventMessageUtil eventMessageUtil;
	
	public List<NotificationMessage> createEmailMessage(NotificationEvent event) {
		NotificationMessage emailMessage = null;
		List<NotificationMessage> messageList = new ArrayList<NotificationMessage>(); 
		Claim claim;		
		HashMap<String, Object> paramMap = event.getParameterMap();
		Long claimId = null;
		claimId = Long.valueOf((String)paramMap.get("id"));
		
		if(claimId != null){
			claim = claimService.findClaim(claimId);
			
			if(claim != null){
				//ClaimAudit claimAudit = claim.getClaimAuditForState(event.getEventName());
				//check if current claim state is same as for which event was recorded
				//if(claimAudit != null){
					List<User> actorList = workListItemService.findAllActorForClaim(claim.getId());		

					for (User user : actorList) {
						if(user != null){	
							
							//create emailMessage only if user has subscribed for the event
							if(checkUserSubscriptionForEvent(user, event)){
								emailMessage = new NotificationMessage();
								//emailMessage.setClaimNumber(claim.getClaimNumber());
								emailMessage.setCreationDate(new Date());
								emailMessage.setMessageState(MessageState.PENDING);
								emailMessage.setNumberOfTrials(new Long(0));
								emailMessage.setRecipient(user.getEmail());				
								
								emailMessage.setParameterMap(eventMessageUtil.getClaimParamForMessage(user, claim));
								emailMessage.setMessageTemplate(eventMessageUtil.getEmailTemplate(event.getEventName(), user, claim.getBusinessUnitInfo()));
								messageList.add(emailMessage);
							}
							else{
								// if user has not subscribed for the event we do 
								// not want to pick this emailEvent record again
								event.setNumberOfTrials(3);
							}
						} //user null check
					}
				//} //claim Audit check
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
