package tavant.twms.web.security.authn.provider;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationServiceException;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.ldap.LdapAuthenticationProvider;
import org.acegisecurity.providers.ldap.LdapAuthenticator;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.apache.log4j.Logger;
import tavant.twms.security.model.OrgAwareUserDetailsService;



public class TavantLdapAuthenticationProvider extends LdapAuthenticationProvider {

	private OrgAwareUserDetailsService userDetailsService;
	private boolean ldapAuthenticationEnabled = true;
	private Logger logger = Logger.getLogger(TavantLdapAuthenticationProvider.class);
	
	public void setUserDetailsService(OrgAwareUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	public void setLdapAuthenticationEnabled(boolean ldapAuthenticationEnabled) {
		this.ldapAuthenticationEnabled = ldapAuthenticationEnabled;
	}

	public TavantLdapAuthenticationProvider(LdapAuthenticator authenticator) {
		super(authenticator);
	}

    public boolean supports(Class authentication) {
        return ldapAuthenticationEnabled && super.supports(authentication);
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
    }

    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
        UserDetails userDetails = super.retrieveUser(username, authentication);

        if (userDetails instanceof LdapUserDetails) {
            userDetails = userDetailsService.loadUserByUsername(username);
            if (logger.isInfoEnabled()) {
                logger.info("Retrieved User Details : " + userDetails.getUsername());
            }
        }
        return userDetails;
    }
}
