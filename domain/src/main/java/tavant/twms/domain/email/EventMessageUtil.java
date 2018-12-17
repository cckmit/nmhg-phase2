package tavant.twms.domain.email;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.notification.EventStateService;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;


public class EventMessageUtil 
{
	private HashMap<String, String> templateMap;
	
	private EventStateService eventStateService;
	
	private OrgService orgService;
	
	/**
	 * Creates a xmlString 
	 * @param user
	 * @param claim
	 * @return
	 */
	public HashMap<String, Object> getClaimParamForMessage(User user,Claim claim){			
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		//To figure out the right name for the user we have to do some checks and use the best option.
		//See if First Name Last Name can be used if not then lets settle with his/her login name.
		String userName = null;
		if(user.getFirstName() != null && user.getFirstName().length() > 0)
		{
			userName = user.getFirstName();
			if(user.getLastName() != null && user.getLastName().length() > 0)
			{
				userName = userName + " " + user.getLastName();
			}
		}
		else
		{
			userName = user.getName();
		}
		paramMap.put("userName", userName);
		paramMap.put("claimNumber", claim.getClaimNumber());
		paramMap.put("state", claim.getState().getState());		
		paramMap.put("subject", "Claim - " + claim.getClaimNumber() + " needs attention");
		return paramMap;
	}

	
	/**
	 * @param user
	 * @param paramHashMap
	 * @return
	 */
	public HashMap<String, Object> getPartReturnParamForMessage(User user,HashMap paramHashMap, Claim claim){			
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		//To figure out the right name for the user we have to do some checks and use the best option.
		//See if First Name Last Name can be used if not then lets settle with his/her login name.
		String userName = null;
		if(user.getFirstName() != null && user.getFirstName().length() > 0)
		{
			userName = user.getFirstName();
			if(user.getLastName() != null && user.getLastName().length() > 0)
			{
				userName = userName + " " + user.getLastName();
			}
		}
		else
		{
			userName = user.getName();
		}
					
		//if part numbers are available we should use it.
		if(paramHashMap.containsKey("partNumberString"))
		{
			paramMap.put("partNumberString", paramHashMap.get("partNumberString"));
		}
		paramMap.put("userName", userName);
		paramMap.put("claimNumber", claim.getClaimNumber());
		paramMap.put("state", claim.getState().getState());	
		if(paramHashMap.containsKey("subject")){
			paramMap.put("subject", paramHashMap.get("subject"));
		}
		
		return paramMap;
	}
	
	
	
	/**
	 * Returns a lower case template name created using eventName, user role, locale and file extension.
	 *    
	 * @param eventName
	 * @param user
	 * @return
	 *  String: eventName_userRole_locale.vm
	 */
	public String getEmailTemplate(String eventName, User user, BusinessUnitInfo businessUnitInfo){
		StringBuffer templateName = new StringBuffer();
		
		if(StringUtils.hasText(eventName)){
			Locale locale = user.getLocale();
			if(locale == null){
				locale = Locale.ENGLISH;
			}
			templateName.append(businessUnitInfo.getName())
						.append("_")
						.append(eventName)
						.append("_")
						.append(getUserRole(orgService.getRolesForUser(user), eventName))
						.append("_")
						.append(locale.getLanguage())
						.append(".vm");
			return templateName.toString().toLowerCase();
		}
		return null;
	}
	
	/**
	 * Returns highest user role for a given set of user roles
	 * @param roles
	 * @return
	 */
	public String getUserRole(Set<Role> roles, String eventName){
    	String userRole = null;
    	EventState eventState = eventStateService.findEventStateByName(eventName);
    	for (Role role : roles) 
    	{
    		//although in an ideal scenario it'll never be sent back; but just keep updating in case
    		//due to data issue it is actually sent back.
    		userRole = role.getName();   		
    		
    		if(eventState != null)
    		{
    			for(Role eventRoles : eventState.getRoles())
    			{
    				if(role.getName().equals(eventRoles.getName()))
    				{
    					return role.getName();
    				}
    			}
    		}
    		else
    		{
    			return role.getName();
    		}
		}
    	return userRole;
    }    

	public HashMap<String, String> getTemplateMap() {
		return templateMap;
	}

	public void setTemplateMap(HashMap<String, String> templateMap) {
		this.templateMap = templateMap;
	}


	public EventStateService getEventStateService() 
	{
		return eventStateService;
	}


	public void setEventStateService(EventStateService eventStateService) 
	{
		this.eventStateService = eventStateService;
	}


	public OrgService getOrgService() {
		return orgService;
	}


	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}	
	
}
