package tavant.twms.web.inbox;

import java.lang.reflect.Member;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.infra.BeanProvider;

import com.domainlanguage.time.CalendarDate;

public class DefaultPropertyResolver implements BeanProvider {

	private static final Logger logger = Logger.getLogger(DefaultPropertyResolver.class);
	
	public Object getProperty(String propertyPath, Object root) {
		try {
			Object object = root;
			String[] properties = StringUtils.tokenizeToStringArray(
					propertyPath, ".");
			for (int i = 0; i < properties.length; i++) {
				if (logger.isDebugEnabled()) {
					logger
							.debug("Extracting " + properties[i] + " : "
									+ object);
				}
				
				Map context = Ognl.createDefaultContext(this);

				/* Create an anonymous inner class to handle special conversion */
				Ognl.setTypeConverter(context, new ognl.DefaultTypeConverter() {
					public Object convertValue(Map context, Object value, Class toType) {
						Object result = null;

						if (value instanceof CalendarDate) {

							CalendarDate calendarDate = (CalendarDate) value;

							if (calendarDate != null) {
								String dateFormat = TWMSDateFormatUtil.getDateFormatForLoggedInUser();
								result = calendarDate.toString(dateFormat);

								if (logger.isDebugEnabled()) {
									logger.debug("Converted CalendarDate ["
											+ calendarDate
											+ "] to  date string ["
											+ result + "]");
								}
							}
						} else {
							try{
							 Date today=null;
						      
						        if(value instanceof Timestamp)
						        {
						        	today=(Date)value;						        		        	
						        	Calendar cal = Calendar.getInstance();
								    cal.setTime(today);
								    int year = cal.get(Calendar.YEAR);
								    int month = cal.get(Calendar.MONTH)+1;
								    int day = cal.get(Calendar.DAY_OF_MONTH);
						        	
									CalendarDate latestDate = CalendarDate.date(year,month,day);
						        	
									if (latestDate != null) {
										String dateFormat = TWMSDateFormatUtil.getDateFormatForLoggedInUser();
										result = latestDate.toString(dateFormat);
									}
						        	return result;
						        	
						        }
							}
							catch(Exception e)
							{
								logger.error(e.getMessage());	
							}
							result = super.convertValue(context, value, toType);
						}
						
						return result;
					}
				});
				
				if(properties[i].toLowerCase().endsWith("date")
                        || properties[i].toLowerCase().endsWith("createdon")
                        || properties[i].toLowerCase().endsWith("updatedon"))
				{			
					if(object == null)
					{
						object = Ognl.getValue( properties[i], context, root, CalendarDate.class );
					}
					else
					{
						object = Ognl.getValue( properties[i], context, object, CalendarDate.class );
					}
				}
				else
				{				
					//object = Ognl.getValue( properties[i], context, root, Object.class );
					object = Ognl.getValue(properties[i], object);
				}	
				
				if (object == null) {
					break;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Value of " + properties[i] + " : " + object);
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug(" Value of " + propertyPath + " : " + object);
			}
			return object;
		} catch (OgnlException e) {
			logger.error("Error encountered while fetching value of property"
					+ propertyPath + " of object" + root, e);
		}
		return null;
	}

}
