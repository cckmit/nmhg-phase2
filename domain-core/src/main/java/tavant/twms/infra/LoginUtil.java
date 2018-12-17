/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 *
 */
package tavant.twms.infra;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author prasad.r
 */
public class LoginUtil {

    private static final String LOGIN_ID = "LOGIN_ID";
    private static final String TIME_STAMP = "TIME_STAMP";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private static final String PRIVATE_KEY = "287d5925714f6721226377423d302e6620482437642a3f243447716a74";
    private static final String CHARSET = "UTF-8";

    public static void main(String[] args) throws Exception {
        String generateTokenKeySpec = generateTokenKeySpec("prasad");
        System.out.println(generateTokenKeySpec);
        String loginNameFromString = getLoginNameFromString(generateTokenKeySpec);
        System.out.println(loginNameFromString);
    }

    public static String getLoginNameFromString(String encodedString) {
        try {
            byte[] decodeBase64 = Base64.decodeBase64(encodedString.getBytes(CHARSET));
            String decodedValue = new String(encodeORDecode(decodeBase64, false), CHARSET);
            Map<String, String> map = getAttributes(decodedValue);
            String timeStampStr = map.get(TIME_STAMP);
            DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date parsedDate = DATE_FORMAT.parse(timeStampStr);
            long currentTimeInMillis = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime().getTime();
            if ((currentTimeInMillis - parsedDate.getTime()) > (2 * 60 * 1000)) { // should we externalize 2 minutes 
                return null;
            }
            return map.get(LOGIN_ID);
        } catch (Exception e) {

        }
        return null;
    }

    public static String generateTokenKeySpec(String key) throws Exception {
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put(LOGIN_ID, key);
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        String timeStamp = DATE_FORMAT.format(new Date());
        keyMap.put(TIME_STAMP, timeStamp);
        byte[] plainText = encodeORDecode(getBytesFromMap(keyMap), true);
        return new String(Base64.encodeBase64(plainText), CHARSET);
    }

    private static byte[] encodeORDecode(byte[] key, boolean isEncodingMode) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digestOfPrivateKey = md.digest(PRIVATE_KEY.getBytes(CHARSET));
        byte[] keyBytes = new byte[24];
        System.arraycopy(digestOfPrivateKey, 0, keyBytes, 0, 16);
        System.arraycopy(digestOfPrivateKey, 0, keyBytes, 16, 8);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "DESede");
        Cipher decipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        int cipherMode = isEncodingMode ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
        decipher.init(cipherMode, secretKey);
        byte[] plainText = decipher.doFinal(key);
        return plainText;
    }

    private static byte[] getBytesFromMap(Map<String, String> keyMap) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> entrySet = keyMap.entrySet();
        for (Iterator<Map.Entry<String, String>> it = entrySet.iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value);
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString().getBytes(CHARSET);
    }

    private static Map<String, String> getAttributes(String decodedValue) {
        Map<String, String> map = new HashMap<String, String>();
        for (StringTokenizer stringTokenizer = new StringTokenizer(decodedValue, ","); stringTokenizer.hasMoreTokens();) {
            String token = stringTokenizer.nextToken();
            String[] attrAndValue = token.split("=");
            map.put(attrAndValue[0], attrAndValue[1]);
        }
        return map;
    }
}
