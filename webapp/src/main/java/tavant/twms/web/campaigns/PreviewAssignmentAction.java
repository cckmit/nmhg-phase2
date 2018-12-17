/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.web.campaigns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignAssignmentService;
import tavant.twms.domain.campaign.CampaignAudit;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignRangeCoverage;
import tavant.twms.domain.campaign.CampaignServiceException;
import tavant.twms.domain.campaign.FieldModUpdateAudit;
import tavant.twms.domain.campaign.FieldModUpdateStatus;
import tavant.twms.domain.campaign.FieldModificationInventoryStatus;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.CampaignClaim;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.common.Document;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.i18n.I18nActionSupport;

@SuppressWarnings("serial")
public class PreviewAssignmentAction extends I18nActionSupport implements
		Preparable {

	private String notificationId;
	
	private String id;

	private CampaignNotification campaignNotification;

	private CampaignAssignmentService campaignAssignmentService;
	
	private FieldModificationInventoryStatus fieldModCode;
	
	private List<String> availableStatuses=new ArrayList<String>(Arrays.asList("Accept","Reject"));

	public FieldModificationInventoryStatus getFieldModCode() {
		return fieldModCode;
	}

	public void setFieldModCode(FieldModificationInventoryStatus fieldModCode) {
		this.fieldModCode = fieldModCode;
	}

	private String claimType;
	
	private String fromPendingCampaign;
	
	private Campaign campaign;
	
	private CampaignClaim claim;
	
	private String jsonString;
		
	private String UpdateCampaignNotificationStatus;
			

	public void prepare() throws Exception {
		Long idTobeUsed;
		if(notificationId!=null){
			idTobeUsed=Long.parseLong(notificationId);
		}else{
			idTobeUsed=Long.parseLong(id);
		}
		campaignNotification = campaignAssignmentService.findById(idTobeUsed);
		campaign = campaignNotification.getCampaign();
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(campaign.getBusinessUnitInfo().getName());
		if(isStolen() || isScrap()){
			addActionMessage("error.FPIClaim.stolenOrScrap", campaignNotification.getItem().getSerialNumber());
		}
	}

	@Override
	public String execute() {
		return SUCCESS;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CampaignNotification getCampaignNotification() {
		return campaignNotification;
	}

	public void setCampaignNotification(
			CampaignNotification campaignNotification) {
		this.campaignNotification = campaignNotification;
	}

	@Required
	public void setCampaignAssignmentService(
			CampaignAssignmentService campaignAssignmentService) {
		this.campaignAssignmentService = campaignAssignmentService;
	}

	public String createCampaignClaim() {
		claim=new CampaignClaim();
		campaign=campaignNotification.getCampaign();
		ItemReference itemReference = new ItemReference();
		itemReference.setReferredInventoryItem(campaignNotification.getItem());
		claim.addClaimedItem(new ClaimedItem(itemReference));
		setClaimType(ClaimType.CAMPAIGN.getType());
		return SUCCESS;
	}
	
	public String editCampaignNotification()
	{
		if(isStolen()){
			addActionError("message.stole.machineStolen",campaignNotification.getItem().getSerialNumber());
			return INPUT;
		}
		if(isScrap()){
			addActionError("message.scrap.machineScrapped", campaignNotification.getItem().getSerialNumber());
			return INPUT;
		}
		campaignNotification.setCampaignStatus(FieldModUpdateStatus.SUBMITTED);
		return SUCCESS;
	}
	
	public String updateCampaignNotification() 
	{
		try 
		{
			
        validate(campaignNotification);
         if(hasActionErrors()){			
             return INPUT;
         }
        campaignNotification.setCampaignStatus(FieldModUpdateStatus.SUBMITTED);
		addActionHistory(campaignNotification);
		campaignAssignmentService.update(campaignNotification);
		
	}
	catch(Exception e)
	{	
	}
	return SUCCESS;
}
	
	public String updateAndSaveCampaignNotification() {
		try {
            validate(campaignNotification);
             if(hasActionErrors()){			
                 return INPUT;
             }
             if(campaignNotification.getStatus().equalsIgnoreCase("Active"))
             {
            	 campaignNotification.setCampaignStatus(null); 
             }
             else if(UpdateCampaignNotificationStatus.equalsIgnoreCase(availableStatuses.get(0)))
			{
            	campaignNotification.setCampaignStatus(FieldModUpdateStatus.ACCEPTED); 
				campaignNotification.setNotificationStatus(CampaignNotification.INCOMPLETE);
			}
             else if(UpdateCampaignNotificationStatus.equalsIgnoreCase(availableStatuses.get(1)))
             {
             campaignNotification.setCampaignStatus(FieldModUpdateStatus.REJECTED); 
             }
			addActionHistory(campaignNotification);
			campaign.setNotificationsGenerated(Boolean.TRUE);
			campaignAssignmentService.update(campaignNotification);
		}
		catch(Exception e)
		{	
		}
		return SUCCESS;
	}
	
	 public List<ListOfValues> getReasonCodes(String className, CampaignNotification campaignNotification) {
	    	
	    	return campaignAssignmentService.getLovsForClass(className, campaignNotification);
	}
	 
	 public String getReasonDescription()  throws JSONException {
		 JSONArray details=new JSONArray();
		 details.put(fieldModCode.getDescription());
		 jsonString = details.toString();
		 return SUCCESS;
	 }
	 public String getActionHistory()
	 {		
		 if(id!=null)
		 {
		 campaignNotification = campaignAssignmentService.findById(Long.parseLong(id));
		 }
		 return SUCCESS;
	 }
	

	public String getClaimType() {
		return claimType;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	public String getFromPendingCampaign() {
		return fromPendingCampaign;
	}

	public void setFromPendingCampaign(String fromPendingCampaign) {
		this.fromPendingCampaign = fromPendingCampaign;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public CampaignClaim getClaim() {
		return claim;
	}

	public void setClaim(CampaignClaim claim) {
		this.claim = claim;
	}
	
	public void addActionHistory(CampaignNotification campaignNotification)
	{
		//storing action history
		FieldModUpdateAudit fieldModUpdateAudit = new FieldModUpdateAudit();
		fieldModUpdateAudit.setActionTaken(campaignNotification.getStatus());
		fieldModUpdateAudit.setComments(campaignNotification.getComments());
		fieldModUpdateAudit.setInactiveReason(campaignNotification.getFieldModInvStatus().getDescription());
		fieldModUpdateAudit.getD().setUpdatedOn(Clock.today());
		fieldModUpdateAudit.setForFieldModNotifcation(campaignNotification);
		campaignNotification.getFieldModUpdateAudit().add(fieldModUpdateAudit);    
	}
	
	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	public List<String> getAvailableStatuses() {
		return availableStatuses;
	}

	public void setAvailableStatuses(List<String> availableStatuses) {
		this.availableStatuses = availableStatuses;
	}
	
	public String getUpdateCampaignNotificationStatus() {
		return UpdateCampaignNotificationStatus;
	}

	public void setUpdateCampaignNotificationStatus(
			String updateCampaignNotificationStatus) {
		UpdateCampaignNotificationStatus = updateCampaignNotificationStatus;
	}

	@SuppressWarnings("unchecked")
	public void validate(CampaignNotification campaignNotification) throws CampaignServiceException {
		  
		if (!StringUtils.hasText(campaignNotification.getComments())) {
				addActionError("error.common.campaign.comments");
			}
		if(campaignNotification.getFieldModInvStatus()==null)
		{
			addActionError("error.common.campaign.reason");		
		}
		if(UpdateCampaignNotificationStatus!=null && UpdateCampaignNotificationStatus.equalsIgnoreCase("-1") )
		{
			campaignNotification.setCampaignStatus(FieldModUpdateStatus.SUBMITTED);
			addActionError("error.common.campaign.campaignStatus");
			
		}
		 
		
			
		}

	public boolean isScrap() {
		if ("SCRAP".equals(this.campaignNotification.getItem().getConditionType()
				.getItemCondition())) {
			return true;
		}
		return false;
	}

	public boolean isStolen() {
		if ("STOLEN".equals(this.campaignNotification.getItem().getConditionType()
				.getItemCondition())) {
			return true;
		}
		return false;
	}
	
	public boolean canDisplayCreateClaimButton(){
		User loggedInUser = getLoggedInUser();
		if(loggedInUser.isInternalUser()){
			if(loggedInUser.hasRole(Role.DSM_ADVISOR) && !loggedInUser.hasRole(Role.PROCESSOR)){
				return false;
			}
		}
		return true;
	}
}
