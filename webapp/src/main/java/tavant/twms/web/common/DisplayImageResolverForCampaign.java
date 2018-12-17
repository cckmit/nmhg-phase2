package tavant.twms.web.common;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import tavant.twms.common.NoValuesDefinedException;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.web.inbox.DefaultPropertyResolver;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class DisplayImageResolverForCampaign extends ImgColAwarePropertyResolver { 
	
		private final Logger logger = Logger.getLogger(DisplayImagePropertyResolver.class);
		private ConfigParamService configParamService;

		/* (non-JavaDoc)
		 * @see tavant.twms.infra.BeanProvider#getProperty(java.lang.String, java.lang.Object)
		 */

		@SuppressWarnings("unchecked")
		public Object getProperty(String propertyPath, Object target) {
			JSONObject obj;
			try{
				if(Constants.WARNING_IMG_FOR_CAMPAIGN.equals(propertyPath)){
					if(!((CampaignNotification)target).getNotificationStatus().equalsIgnoreCase("complete"))
					{
					Object parsedExpressionFromDate = Ognl.parseExpression("campaign.fromDate");
					//Object parsedExpressionTillDate = Ognl.parseExpression("campaign.tillDate");
					// checking if shipment date is null
					if(Ognl.getValue(parsedExpressionFromDate,target) != null 
							/*&& Ognl.getValue(parsedExpressionTillDate,target) != null*/){
						CalendarDate date = (CalendarDate) Ognl.getValue(parsedExpressionFromDate,target);
						//CalendarDate tillDate = (CalendarDate) Ognl.getValue(parsedExpressionTillDate,target);
						if(this.configParamService==null){
							initDomainRepository();
						}					
						
						//SelectedBusinessUnitsHolder.setSelectedBusinessUnit(((InventoryItem)target).getBusinessUnitInfo().getName());
											
						Long daysForCampaignYellowWarningWindow = this.configParamService.getLongValue
						(ConfigName.DAYS_FOR_CAMPAIGN_YELLOW_WARNING_WINDOW.getName());
						
						Long daysForCampaignRedWarningWindow = this.configParamService.getLongValue
						(ConfigName.DAYS_FOR_CAMPAIGN_RED_WARNING_WINDOW.getName());
						
						Integer daysSinceStartDate = new Integer(date.through(Clock.today()).lengthInDaysInt());
						// show warning image if Inventory lying in stock for more than allowed window
						// else a blank image
						if(date.plusDays(new Integer((int)daysForCampaignYellowWarningWindow.longValue()))
								.isBefore(Clock.today()) && date.plusDays(new Integer((int)daysForCampaignRedWarningWindow.longValue()))
								.isAfter(Clock.today())){
							
							obj = getImgColValue(getText("message.campaign.ageing",new String[]{daysSinceStartDate.toString()}), "image/yellowWarning.gif");
						}else
							if(date.plusDays(new Integer((int)daysForCampaignRedWarningWindow.longValue()))
									.isBefore(Clock.today())){
								
								obj = getImgColValue(getText("message.campaign.ageing",new String[]{daysSinceStartDate.toString()}),"image/redWarning.gif");
							}
							else
						{
							obj = getImgColValue("", "image/trans.gif");
						}
					}
					else{
						obj = getImgColValue("", "image/trans.gif");
					}
					}
					else{
						obj = getImgColValue("", "image/trans.gif");
					}
						
					return obj;

				}
				DefaultPropertyResolver resolver = new DefaultPropertyResolver();
				return resolver.getProperty(propertyPath, target);
				
			} catch (IndexOutOfBoundsException e) {
				this.logger.error("failed to evaluate expression[" + propertyPath
						+ "] on object [" + target + "]", e);
			} catch (OgnlException e) {
				this.logger.error("failed to evaluate expression[" + propertyPath
						+ "] on object [" + target + "]", e);
			}catch (NoValuesDefinedException e){
				this.logger.error("Business Unit configuration doesn't exist[" + propertyPath 
						+ "] on object [" + target +"]", e);
			}
			
			return getImgColValue("", "image/trans.gif");
		}
		
		private void initDomainRepository() {
	        BeanLocator beanLocator = new BeanLocator();
	        this.configParamService = (ConfigParamService) beanLocator.lookupBean("configParamService");
	    }
		public void setConfigParamService(ConfigParamService configParamService) {
			this.configParamService = configParamService;
		}
}
