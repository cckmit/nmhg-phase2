package tavant.twms.web.security.authn.provider;

import org.acegisecurity.providers.dao.DaoAuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.AuthenticationException;
import org.apache.log4j.Logger;
import tavant.twms.web.security.authn.TrustAwareWebAuthenticationDetails;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: Apr 1, 2008
 * Time: 10:28:48 PM
 */
public class TrustAwareDaoAuthenticationProvider extends DaoAuthenticationProvider {
    private boolean dbAuthenticationEnabled = true;
    private static final Logger logger = Logger.getLogger(TrustAwareDaoAuthenticationProvider.class);

    public boolean isDbAuthenticationEnabled() {
        return dbAuthenticationEnabled;
    }

    public void setDbAuthenticationEnabled(boolean dbAuthenticationEnabled) {
        this.dbAuthenticationEnabled = dbAuthenticationEnabled;
    }

    public boolean supports(Class authentication) {
        return dbAuthenticationEnabled && super.supports(authentication);
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        Object authenticationDetails = authentication.getDetails();

        if((authenticationDetails instanceof TrustAwareWebAuthenticationDetails) &&
                !((TrustAwareWebAuthenticationDetails) authenticationDetails).isPasswordBasedAuthentication()) {

            if(logger.isDebugEnabled()) {
                logger.debug("'Encypted username' based authentication detected. Hence, skipping password check.");    
            }

            return;
        }

        if(logger.isDebugEnabled()) {
            logger.debug("Password based authentication detected. Hence, performing password check.");
        }

        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
