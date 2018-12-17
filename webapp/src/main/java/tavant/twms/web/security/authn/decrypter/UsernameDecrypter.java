package tavant.twms.web.security.authn.decrypter;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: Apr 1, 2008
 * Time: 9:45:08 PM
 */
public interface UsernameDecrypter {
    String decrypt(String encryptedUserName);
}
