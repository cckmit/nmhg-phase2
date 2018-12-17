package tavant.twms.common;

import java.util.TimeZone;

import com.domainlanguage.timeutil.Clock;

/**
 * This will be a common utility across the application that can
 * have any static/not static block to be used both from web requests/
 * timer tasks/integration requests.
 * 
 * @author ramalakshmi.p
 *
 */
public class TWMSCommonUtil {

	
	/**
	 * With hibernate 3.5.1, default time zone is not set any more. Hence this static block is required
	 * throughout the application to default the time zone.
	 */
	static{
		Clock.setDefaultTimeZone(TimeZone.getDefault());
	}	
	
	public static boolean isNotNumber(String string) {
		try {
			double d = Double.parseDouble(string);
		} catch (NumberFormatException nfe) {
			return true;
		}
		return false;
	}

}
