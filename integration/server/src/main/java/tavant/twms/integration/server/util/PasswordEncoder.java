package tavant.twms.integration.server.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.dao.DataAccessException;

/**
 * Copied from domain
 * @author prasad.r
 */
public class PasswordEncoder  {

	public String encodePassword(String rawPass, Object salt)
			throws DataAccessException {
		      // Return <digest alg> | hex(sha-1(digest+salt)) | hex(salt)
		try {
		      MessageDigest md;
			  md = MessageDigest.getInstance("SHA-1");
		      md.update(rawPass.getBytes());
		      md.update((byte[]) salt);
		      byte[] digest = md.digest();
			  return TwmsStringUtil.bytesToHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Invalid Password !!", e);
		}
	}

	public boolean isPasswordValid(String encPass, String rawPass, Object salt)
			throws DataAccessException {
		    String pass1 = "" + encPass;
	        String pass2 = encodePassword(rawPass, salt);
	        return pass1.equals(pass2);
	}

}
