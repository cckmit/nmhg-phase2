package tavant.twms.config;

import java.util.*;

/**
 * @author : janmejay.singh
 * Date: Jul 3, 2007
 * Time: 8:12:15 PM
 */
public class UiConfReader {

    private static Map<String, String> properties = new HashMap<String, String>();

    static {
        ResourceBundle resourceBundle = PropertyResourceBundle.getBundle("twms_ui_config");
        Enumeration<String> keys = resourceBundle.getKeys();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            properties.put(key, resourceBundle.getString(key));
        }
    }

    public static String getPropertyValue(String key) {
        if(properties.containsKey(key)) {
            return properties.get(key);
        } else {
            throw new IllegalArgumentException("Key '" + key + "', doesn't exist.");
        }
    }

    public static boolean isPropertySet(String key) {
        return Boolean.parseBoolean(getPropertyValue(key));
    }
}
