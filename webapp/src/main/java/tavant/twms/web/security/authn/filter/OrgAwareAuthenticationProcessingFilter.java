/**
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
package tavant.twms.web.security.authn.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.event.authentication.InteractiveAuthenticationSuccessEvent;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.login.LoginHistoryService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.RoleCategory;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.ApplicationSettingsHolder;
import tavant.twms.infra.LoginUtil;
import tavant.twms.security.model.OrgAwareUserDetails;
import tavant.twms.web.security.authn.TrustAwareWebAuthenticationDetails;
import tavant.twms.web.security.authn.decrypter.UsernameDecrypter;

/**
 * This filter delegates the actual authentication task to its parent {{@link AuthenticationProcessingFilter}.
 * If the authentication was successful then the Org {@link User} object
 * corresponding to the authenticated user is stored in session, from where the
 * action classes pick it up for processing.
 *
 * @author vikas.sasidharan
 *
 */
public class OrgAwareAuthenticationProcessingFilter extends
		AuthenticationProcessingFilter {

	private Logger logger = Logger
			.getLogger(OrgAwareAuthenticationProcessingFilter.class);

	private static final String LOGIN_HISTORY_PARAM_NAME = "logLoginHistory";
    private String sessionLocaleAttributeName;
    private String usernameRequestParamName = "j_username";
    private String passwordRequestParamName = "j_password";

	private LoginHistoryService loginHistoryService;

	private ConfigParamService configParamService;

    public static final String PASSWD_BASED_AUTHN_INDICATOR = "twms.security.authn.usePasswordBasedAuthentication";
    private static final String SSO_PARAM = "app";
    private UsernameDecrypter aesUsernameDecrypter;
    private UsernameDecrypter desUsernameDecrypter;
    private ApplicationSettingsHolder applicationSettings;
    private OrgService orgService;

    public void setSessionLocaleAttributeName(String sessionLocaleAttributeName) {
        this.sessionLocaleAttributeName = sessionLocaleAttributeName;
    }

    @Override
    /**
	 * Delegates actual authentication to the parent. If, and only if, the
	 * authentication was successful the Org {@link User} object corresponding
	 * to the authenticated user is stored in session, from where the action
	 * classes pick it up for processing.
     */
	public Authentication attemptAuthentication(HttpServletRequest request)
			throws AuthenticationException {
        Authentication authentication = super.attemptAuthentication(request);
		User orgUser = null;
		OrgAwareUserDetails orgUserDetails = null;
		if (authentication.isAuthenticated()) {

            Object authenticatedUser = authentication.getPrincipal();

            if (authenticatedUser instanceof OrgAwareUserDetails) {
				orgUserDetails = (OrgAwareUserDetails) authenticatedUser;
				orgUser = orgUserDetails.getOrgUser();
				
				final HttpSession session = request.getSession();
				session.setAttribute(sessionLocaleAttributeName, orgUser
						.getLocale());
                session.setAttribute(sessionLocaleAttributeName + "_XML",
						"<locale><language>"
								+ orgUser.getLocale().getLanguage()
								+ "</language></locale>");// TODO: Clean this up.
                if (StringUtils.hasText(request.getParameter(SSO_PARAM))) {
                    if (StringUtils.hasText(orgUser.getPreferredBu())) {
                        for (BusinessUnit businessUnit : orgUser.getBusinessUnits()) {
                            if (businessUnit.getName().equals(orgUser.getPreferredBu())) {
                                orgUserDetails.setCurrentBusinessUnit(businessUnit);
                            }
                        }
                    } else if (orgUser.getBusinessUnits().size() == 1) {
                        orgUserDetails.setDefaultBusinessUnit(orgUser.getBusinessUnits().iterator().next());
                    }
                }
            }

        } else {
            if (logger.isDebugEnabled()) {
				logger
						.debug("["
								+ authentication
								+ "] is not authenticated. Not storing locale in session");
            }
        }

        return authentication;
    }


	protected String obtainUsername(HttpServletRequest request) {
		String userName = request.getParameter(usernameRequestParamName);

		// Wierd...bt gotta put this hack, the encrypted user name
		// comes with a '+' which is a special charce
		 userName = StringUtils.replace(userName, " ", "+");

		if (!isPasswordBasedAuthentication(request)) {
            if(StringUtils.hasText(request.getParameter(SSO_PARAM))){
                userName = LoginUtil.getLoginNameFromString(request.getParameter(SSO_PARAM));
            }else{
                Assert.notNull(aesUsernameDecrypter);
                Assert.notNull(desUsernameDecrypter);
                if (request.getSession().getAttribute(AdminConstants.AESENCRYPTION).toString().equalsIgnoreCase(AdminConstants.YES)) {
                    userName = aesUsernameDecrypter.decrypt(userName);
                    request.setAttribute(AdminConstants.AESENCRYPTION, null);
                }else if(request.getSession().getAttribute(AdminConstants.AESENCRYPTION).toString().equalsIgnoreCase(AdminConstants.NO)) {
                    request.setAttribute(AdminConstants.AESENCRYPTION, null);
                } 
                else {
                    userName = desUsernameDecrypter.decrypt(userName);
                }
            }
		}

		return userName;
	}


    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(passwordRequestParamName);
    }

	protected void setDetails(HttpServletRequest request,
			UsernamePasswordAuthenticationToken authRequest) {
        super.setDetails(request, authRequest);

		TrustAwareWebAuthenticationDetails authenticationDetails = (TrustAwareWebAuthenticationDetails) authRequest
				.getDetails();

		authenticationDetails
				.setPasswordBasedAuthentication(isPasswordBasedAuthentication(request));
    }

    protected boolean isPasswordBasedAuthentication(HttpServletRequest request) {
    	applicationSettings.setLogoutRequired("true");
        return StringUtils.hasText(request.getParameter(PASSWD_BASED_AUTHN_INDICATOR));
    }

    public String getUsernameRequestParamName() {
        return usernameRequestParamName;
    }

    public void setUsernameRequestParamName(String usernameRequestParamName) {
        this.usernameRequestParamName = usernameRequestParamName;
    }

    public String getPasswordRequestParamName() {
        return passwordRequestParamName;
    }

    public void setPasswordRequestParamName(String passwordRequestParamName) {
        this.passwordRequestParamName = passwordRequestParamName;
    }


	public ApplicationSettingsHolder getApplicationSettings() {
		return applicationSettings;
	}

	public void setApplicationSettings(ApplicationSettingsHolder applicationSettings) {
		this.applicationSettings = applicationSettings;
	}

	public LoginHistoryService getLoginHistoryService() {
		return loginHistoryService;
	}

	public void setLoginHistoryService(LoginHistoryService loginHistoryService) {
		this.loginHistoryService = loginHistoryService;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public UsernameDecrypter getAesUsernameDecrypter() {
		return aesUsernameDecrypter;
	}

	public void setAesUsernameDecrypter(UsernameDecrypter aesUsernameDecrypter) {
		this.aesUsernameDecrypter = aesUsernameDecrypter;
	}

	public UsernameDecrypter getDesUsernameDecrypter() {
		return desUsernameDecrypter;
	}

	public void setDesUsernameDecrypter(UsernameDecrypter desUsernameDecrypter) {
		this.desUsernameDecrypter = desUsernameDecrypter;
	}

    public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
	
	private boolean isFleetUser(User user){
		for(Role role : user.getRoles()){
			if(role.getRoleCategory().equals(RoleCategory.WARRANTY)){
				return false;
			}
		}
		return true;
	}

	@Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
    	boolean requiresAuthentication = super.requiresAuthentication(request, response);
    	if(requiresAuthentication)
    		return true;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return false;
        }
        if(!requiresAuthentication){ // this is the case where fleet might be hitting some warranty action
            requiresAuthentication = StringUtils.hasText(request.getParameter(SSO_PARAM));
        }
        return requiresAuthentication;
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
        boolean requiresAuthentication = super.requiresAuthentication(request, response); // will true if and only if action is authencateUser, which means we need to send to home.action
        if(!requiresAuthentication){
            SecurityContextHolder.getContext().setAuthentication(authResult);
            String url = request.getRequestURI();
            if (!"".equals(request.getContextPath())) {
                url = url.substring(request.getContextPath().length());
            }            
            RequestDispatcher rs = request.getSession().getServletContext().getRequestDispatcher(url);
            try {
                rs.forward(request, response);
            } catch (ServletException ex) {ex.printStackTrace();}
        }
    }
    
    @Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authResult) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Authentication success: " + authResult.toString());
		}

		SecurityContextHolder.getContext().setAuthentication(authResult);

		if (logger.isDebugEnabled()) {
			logger.debug("Updated SecurityContextHolder to contain the following Authentication: '" + authResult + "'");
		}
		
		String targetUrl = determineTargetUrl(request);
		
		// Fleet SSO Implementation Start
		
		String userName = obtainUsername(request);
		User user = orgService.findUserByName(userName);
		
		if(isFleetUser(user) && !user.getRoles().isEmpty()){
			targetUrl="/goToFleet.action";
		}
		// Fleet SSO Implementation End

		if (logger.isDebugEnabled()) {
			logger.debug("Redirecting to target URL from HTTP Session (or default): " + targetUrl);
		}

		if(!super.requiresAuthentication(request, response))
			onSuccessfulAuthentication(request, response, authResult);

		getRememberMeServices().loginSuccess(request, response, authResult);

		// Fire event
		if (this.eventPublisher != null) {
			eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
		}

		sendRedirect(request, response, targetUrl);
	}
    
    
    
}
