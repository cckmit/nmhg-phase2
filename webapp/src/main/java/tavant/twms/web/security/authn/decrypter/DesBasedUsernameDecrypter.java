package tavant.twms.web.security.authn.decrypter;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by IntelliJ IDEA. User: vikas.sasidharan Date: Apr 1, 2008 Time:
 * 9:49:48 PM
 */
public class DesBasedUsernameDecrypter extends
		AbstractSaltBasedUsernameDecrypter {

	public DesBasedUsernameDecrypter(String salt) {
		super(salt);
	}

	public String decryptUsingSalt(String encryptedUsername) {
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		byte[] initVector = { 8, 7, 6, 5, 4, 3, 2, 1 };

		IvParameterSpec iv = new IvParameterSpec(initVector);

		SecretKeySpec key = new SecretKeySpec(new byte[] { 1, 2, 3, 4, 5, 6, 7,
				8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
				24 }, "DESede");

		try {
			Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);

			byte[] original = Base64.decodeBase64(encryptedUsername.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, key, iv);

			return new String(cipher.doFinal(original));
		} catch (Exception e) {
			return null;
		}
	}
}
