package tavant.twms.web.security.authn.decrypter;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class AesBasedUsernameDecrypter extends AbstractSaltBasedUsernameDecrypter {

    private static final Logger logger = Logger.getLogger(AesBasedUsernameDecrypter.class);

    public AesBasedUsernameDecrypter(String salt) {
		super(salt);
	}

    public String decryptUsingSalt(String encryptedText) {
		try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] keyBytes = new byte[16];
            byte[] b = salt.getBytes("UTF-8");
            int len = b.length;
            if (len > keyBytes.length) len = keyBytes.length;
            System.arraycopy(b, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte [] results = cipher.doFinal(Base64.decodeBase64(encryptedText.getBytes()));
            return new String(results,"UTF-8");
        } catch (Exception e) {
            logger.error(e, e);
			return "error.manageDealerUsers.authToken.notMatch";
		}
	}

    public static void main(String[] args) {
        AesBasedUsernameDecrypter aes = new AesBasedUsernameDecrypter("7vRbsM=M>*zaGg-oa+E5Guc]Hw=Rz}=T");
        System.out.println(aes.decryptUsingSalt("xMrMv0/Ksut5k/g5HbZpww=="));
    }
}
