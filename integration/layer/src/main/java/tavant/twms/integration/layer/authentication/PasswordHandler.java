package tavant.twms.integration.layer.authentication;
import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

/**
 * This class is used by the DealerAPIAuthenticationTestCase to set the password for authentication.
 * @author TWMSUSER
 */
public class PasswordHandler implements CallbackHandler {


    public PasswordHandler() {
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

            // There is nothing we could actually do here...

            WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
            pc.setPassword("tavant");
    }
    

}
