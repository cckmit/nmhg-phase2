package tavant.twms.dateutil;

public class TWMSStringUtil {

	  /**
	   * Maps 0 through 15 to hex characters.
	   */
	  public static final char[] hexMap = {'0', '1', '2', '3', '4', '5', '6', '7',
	                                       '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	  
		/**
	   * Returns the hex representation of a byte array.
	   * The implementation should be tuned for better performance.
	   * @param bytes The bytes to convert.
	   * @return A new string containing the hex representation of the bytes.
	   */
	  public static String bytesToHexString(byte[] bytes)
	  {
	    if (bytes == null)
	       return null;

	    StringBuffer sb = new StringBuffer(bytes.length * 2);
	    for (int i = 0 ; i < bytes.length ; i++)
	    {
	      byte b = bytes[i];
	      sb.append(hexMap[(b >>> 4) & 15]); // Must use UNsigned right-shift operator!
	      sb.append(hexMap[b & 15]);
	    }
	    return sb.toString();
	  }

	  /**
	   * Returns a byte array corresponding to a hex string.
	   * The implementation should be tuned for better performance.
	   * @param hexString The string to convert.
	   * @return A new byte array with the equivalent data.
	   */
	  public static byte[] hexStringToBytes(String hexString)
	  {
	    if (hexString == null)
	      return null;

	    // Get the strings's characters into an array for better
	    // performance.
	    int stringLength = hexString.length();
	    if (stringLength % 2 != 0)
	      throw new NumberFormatException("Invalid hex string: [" +
	        hexString + "]");
	    char[] chars = new char[stringLength];
	    hexString.getChars(0, stringLength, chars, 0);

	    // Convert the characters.
	    byte[] bytes = new byte[stringLength / 2];
	    for (int i = 0, j = 0 ; i < stringLength ; i += 2, j++)
	    {
	      byte upper = (byte)Character.digit(chars[i], 16);
	      byte lower = (byte)Character.digit(chars[i + 1], 16);
	      if (upper < 0 || lower < 0)
	         throw new NumberFormatException("Invalid hex string: [" +
	           hexString + "]");
	      bytes[j] = (byte)((upper << 4) + lower);
	    }

	    return bytes;
	  }

	
}
