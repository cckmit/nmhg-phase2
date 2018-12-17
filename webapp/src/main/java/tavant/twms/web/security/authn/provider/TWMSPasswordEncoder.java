package tavant.twms.web.security.authn.provider;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.springframework.dao.DataAccessException;

import tavant.twms.common.TWMSException;
import tavant.twms.dateutil.TWMSStringUtil;

public class TWMSPasswordEncoder implements PasswordEncoder {

	public String encodePassword(String rawPass, Object salt)
			throws DataAccessException {
		      // Return <digest alg> | hex(sha-1(digest+salt)) | hex(salt)
		try {
		      MessageDigest md;
			  md = MessageDigest.getInstance("SHA-1");
		      md.update(rawPass.getBytes());
		      md.update((byte[]) salt);
		      byte[] digest = md.digest();
			  return TWMSStringUtil.bytesToHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new TWMSException("Algorithm not supported", e);
		}
	}

	public boolean isPasswordValid(String encPass, String rawPass, Object salt)
			throws DataAccessException {
		    String pass1 = "" + encPass;
	        String pass2 = encodePassword(rawPass, salt);
	        return pass1.equals(pass2);
	}

}
