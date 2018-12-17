package tavant.twms.web.security.authn.decrypter;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AesBasedDecrypterForCMS extends
                                AbstractSaltBasedUsernameDecrypter {

                public AesBasedDecrypterForCMS(String salt) {
                                super(salt);
                }

                private static final String characterEncoding = "UTF-8";
                private static final String cipherTransformation = "AES/CBC/PKCS5Padding";
                private static final String aesEncryptionAlgorithm = "AES";

                public String decryptUsingSalt(String encryptedText) {
                                byte[] cipheredBytes = Base64.decodeBase64(encryptedText.getBytes());
                                byte[] keyBytes;
                                byte[] decryptedBytes;
                                try {
                                                keyBytes = getKeyBytes(salt);
                                                Cipher cipher = Cipher.getInstance(cipherTransformation);
                                                SecretKeySpec secretKeySpecy = new SecretKeySpec(keyBytes,
                                                                                aesEncryptionAlgorithm);
                                                IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);
                                                cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
                                                decryptedBytes = cipher.doFinal(cipheredBytes);
                                } catch (Exception e) {
                                                return "error.manageDealerUsers.authToken.notMatch";
                                }
                                return new String(decryptedBytes);
                }

                private byte[] getKeyBytes(String key) throws UnsupportedEncodingException {
                                byte[] keyBytes = new byte[16];
                                byte[] parameterKeyBytes = key.getBytes(characterEncoding);
                                System.arraycopy(parameterKeyBytes, 0, keyBytes, 0,
                                                                Math.min(parameterKeyBytes.length, keyBytes.length));
                                return keyBytes;
                }

}
