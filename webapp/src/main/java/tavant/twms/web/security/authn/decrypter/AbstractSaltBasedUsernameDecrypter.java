package tavant.twms.web.security.authn.decrypter;

import org.acegisecurity.AuthenticationServiceException;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: Apr 1, 2008
 * Time: 10:52:56 PM
 */
public abstract class AbstractSaltBasedUsernameDecrypter implements UsernameDecrypter {

    protected String salt;

    private static final Logger logger = Logger.getLogger(AbstractSaltBasedUsernameDecrypter.class);

    protected AbstractSaltBasedUsernameDecrypter(String salt) {
        this.salt = salt;
    }

    public String decrypt(String encryptedUsername) {

        String decryptedUsername = decryptUsingSalt(encryptedUsername);

        if(logger.isDebugEnabled()) {
            logger.debug("Decrypted username [" + encryptedUsername + "] to [" + decryptedUsername +
                    "], using salt [" + salt + "].");
            logger.debug("Performing post decryption checks.");
        }

        afterDecryption(encryptedUsername, decryptedUsername);

        return decryptedUsername;
    }

    protected abstract String decryptUsingSalt(String encryptedUsername);

    protected void afterDecryption(String encryptedUsername, String decryptedUsername) {
        /*if (encryptedUsername.equals(decryptedUsername)) {
            throw new AuthenticationServiceException("Trust based authentication was requested, but the username [" +
                    encryptedUsername + "] was not encrypted!.");
        }*/
    }
    
}
