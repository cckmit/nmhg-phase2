package tavant.twms.integration.web.util;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class PropertiesUtil {
	static PropertyResourceBundle props    = (PropertyResourceBundle)ResourceBundle.getBundle ("main");	
	public static String getProperty(String key){
        String value = props.getString(key);
        return value;
	}
}