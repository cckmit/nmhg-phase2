package tavant.twms.web.security.authn;

import org.acegisecurity.ui.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: Apr 1, 2008
 * Time: 10:15:27 PM
 */
public class TrustAwareWebAuthenticationDetails  extends WebAuthenticationDetails {

    private boolean passwordBasedAuthentication;

    public TrustAwareWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
    }

    public boolean isPasswordBasedAuthentication() {
        return passwordBasedAuthentication;
    }

    public void setPasswordBasedAuthentication(boolean passwordBasedAuthentication) {
        this.passwordBasedAuthentication = passwordBasedAuthentication;
    }
}
