package tavant.twms.web.security.authz.voter;

import java.util.Iterator;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationTrustResolver;
import org.acegisecurity.AuthenticationTrustResolverImpl;
import org.acegisecurity.ConfigAttribute;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.vote.AccessDecisionVoter;
import org.apache.log4j.Logger;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.authz.AuthorizationManager;
import tavant.twms.security.model.OrgAwareUserDetails;


/**
 * Voter Class which decides whether access can be granted or denied or
 * abstained on a resource for a user.
 * 
 */
public class SecurityVoter implements AccessDecisionVoter {
	private AuthorizationManager authorizationManager;
	AuthenticationTrustResolver trustResolver=new AuthenticationTrustResolverImpl();
	private Logger logger=Logger.getLogger(getClass());

	public void setAuthorizationManager(AuthorizationManager authorizationManager) {
		this.authorizationManager = authorizationManager;
	}

	/**
	 * @param configAttribute
	 * @return boolean
	 */
	public boolean supports(ConfigAttribute arg0) {
		return true;
	}

	/**
	 * @param Class
	 * @return boolean
	 */
	public boolean supports(Class arg0) {
		return true;
	}

	/**
	 * Returns "ACCESS_GRANTED" if user has permission to access a resource else
	 * "ACCESS_DENIED". If it is unable to decide it will return
	 * "ACCESS_ABSTAIN"
	 * 
	 * @param authentication
	 * @param securedObject
	 * @param configAttribute
	 * @return int
	 */
	public int vote(Authentication authentication, Object securedObject, ConfigAttributeDefinition configAttribute) {
		int result = ACCESS_ABSTAIN;
		//If Authenticated Anonymously we will abstain from voting, so that
		//Authenticated voter will authorize.Can be done also using the method supports(), 
		//but I thought this better since we are using the authentication object to check whether 
		//this is anonymous.
		if(!isAnonymous(authentication)){
			boolean validUser = isValidUser(authentication);
			if (validUser) {
				result=isAuthorizationGranted(authentication,securedObject,configAttribute);
			} else {
				logger.error("Access denied!User is not a valid authenticated user !!!");
				result = ACCESS_DENIED;
			}
		}
		return result;
	}	

	private int isAuthorizationGranted(Authentication authentication, Object securedObject, ConfigAttributeDefinition configAttribute) {
		int result=ACCESS_DENIED;
		if(isDefaultAccess(configAttribute)){
			result=ACCESS_GRANTED;
		}
		else if (authorizationManager.isPermissionGranted(((OrgAwareUserDetails) authentication.getPrincipal()).getOrgUser(),
				securedObject)) {
			result = ACCESS_GRANTED;
		}
		return result;
	}

	/**
	 * Checks whether if any kind of permission works for this resource.
	 * @param configAttribute 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isDefaultAccess(ConfigAttributeDefinition configAttribute) {
		for (Iterator<ConfigAttribute> iterator = configAttribute.getConfigAttributes(); iterator.hasNext();) {
			ConfigAttribute attribute= iterator.next();
			if(attribute.getAttribute().equals("*")){
				return true;
			}
		}
		return false;
	}

	/**
	 * Helper method
	 * 
	 * @param authentication
	 * @return boolean
	 */
	private boolean isValidUser(Authentication authentication) {
			return User.class.isAssignableFrom(((OrgAwareUserDetails) authentication.getPrincipal()).getOrgUser().getClass());
	}

	/**
	 * This Method checks if the Authentication is anonymous.
	 * @param authentication The Authentication Object.
	 * @return
	 */
	private boolean isAnonymous(Authentication authentication) {
		return trustResolver.isAnonymous(authentication);
	}
}
