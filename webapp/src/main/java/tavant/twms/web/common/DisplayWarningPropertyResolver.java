package tavant.twms.web.common;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.json.JSONObject;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.web.actions.TaskWrapper;
import tavant.twms.web.inbox.DefaultPropertyResolver;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.Duration;
import com.domainlanguage.timeutil.Clock;

public class DisplayWarningPropertyResolver extends ImgColAwarePropertyResolver{
	private final Logger logger = Logger.getLogger(DisplayWarningPropertyResolver.class);
	private ConfigParamService configParamService;

	@SuppressWarnings("unchecked")
	public Object getProperty(String propertyPath, Object root) {
		try {
			if("warningImg".equals(propertyPath)) {
				Object parsedExpression = Ognl.parseExpression("claim.lastUpdatedOnDate");
				
				 Date today=null;
				CalendarDate date=null;
				 if(parsedExpression.toString().equals("claim.lastUpdatedOnDate"))
			        {
						today= (Date) Ognl.getValue(parsedExpression, new TaskWrapper((TaskInstance)root));	
									
						Calendar cal = Calendar.getInstance();
					    cal.setTime(today);
					    int year = cal.get(Calendar.YEAR);
					    int month = cal.get(Calendar.MONTH)+1;
					    int day = cal.get(Calendar.DAY_OF_MONTH);										
						date=CalendarDate.date(year,month,day);
			        }
			        else{
			        	date = (CalendarDate) Ognl.getValue(parsedExpression, new TaskWrapper(
								(TaskInstance) root));	
			        }
				
				/*CalendarDate date = (CalendarDate) Ognl.getValue(parsedExpression, new TaskWrapper(
						(TaskInstance) root));*/
				if(this.configParamService==null){
					initDomainRepository();
				}
				Long daysForExternalRepliesWindow = this.configParamService.getLongValue(ConfigName.DAYS_FOR_EXTERNAL_REPLIES_WINDOW.getName());
				Long daysConfiguredForYellowWarning = this.configParamService.getLongValue(ConfigName.DAYS_CONFIGURED_FOR_YELLOW_WARNING.getName());
				
				JSONObject obj ;
				if(Clock.today().isAfter
						(date.plusDays(new Integer((int)daysConfiguredForYellowWarning.longValue()))) ||
						Clock.today().equals
						(date.plusDays(new Integer((int)daysConfiguredForYellowWarning.longValue())))){
					int remainingdaysLeft =Math.abs(
						Clock.today().through(date.plusDays(new Integer((int)daysForExternalRepliesWindow.longValue()))).lengthInDaysInt());
					Long daysConfiguredForRedWarning = this.configParamService.getLongValue(ConfigName.DAYS_CONFIGURED_FOR_RED_WARNING.getName());
					if(Clock.today().isAfter
							(date.plusDays(new Integer((int)daysConfiguredForRedWarning.longValue())))){
						obj = getImgColValue("You have only "+remainingdaysLeft+" day(s) to act on this claim", "image/redWarning.gif");
					}else {
						obj = getImgColValue("You have only "+remainingdaysLeft+" day(s) to act on this claim", "image/yellowWarning.gif");
					}
				}else{
					obj = getImgColValue("", "image/trans.gif");
				}
				return obj;
			}else if("warningImgForProcessor".equals(propertyPath)){
				Object parsedExpression = Ognl.parseExpression("claim");
				Claim claim = (Claim) Ognl.getValue(parsedExpression, new TaskWrapper(
						(TaskInstance) root));
				if(this.configParamService==null){
					initDomainRepository();
				}
				Date lastUpdatedDate=claim.getLastUpdatedOnDate();
				Calendar cal = Calendar.getInstance();
			    cal.setTime(lastUpdatedDate);
			    int year = cal.get(Calendar.YEAR);
			    int month = cal.get(Calendar.MONTH)+1;
			    int day = cal.get(Calendar.DAY_OF_MONTH);
					
				CalendarDate latestDate = CalendarDate.date(year,month,day);
				Long daysForInternalAgeingPeriod = this.configParamService.getLongValue(ConfigName.DAYS_FOR_INTERNAL_AGEING_PERIOD.getName());
				Duration duration= Duration.days(new Integer((int)daysForInternalAgeingPeriod.longValue()));
				JSONObject obj ;
				if(Clock.today().isAfter
						(latestDate.plus(duration)) || Clock.today().equals(latestDate.plus(duration))){
					int daysAfterConfiguredTime=Math.abs(Clock.today().through(latestDate).lengthInDaysInt());
					obj = getImgColValue("The claim is "+daysAfterConfiguredTime+" day(s) old", "image/yellowWarning.gif");
				}else{
					obj = getImgColValue("", "image/trans.gif");
				}
				return obj;
			}
			DefaultPropertyResolver resolver = new DefaultPropertyResolver();
			return resolver.getProperty(propertyPath, new TaskWrapper(
						(TaskInstance) root));
			
		} catch (OgnlException e) {
			this.logger.error("failed to evaluate expression[" + propertyPath
					+ "] on object [" + root + "]", e);
		} catch (IndexOutOfBoundsException e) {
			this.logger.error("failed to evaluate expression[" + propertyPath
					+ "] on object [" + root + "]", e);
		}
		return null;
	}

	private void initDomainRepository() {
        BeanLocator beanLocator = new BeanLocator();
        this.configParamService = (ConfigParamService) beanLocator.lookupBean("configParamService");
    }
	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
}
