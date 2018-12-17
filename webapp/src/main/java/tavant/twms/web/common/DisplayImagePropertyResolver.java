/**
 This Java file is used to display image on the Web UI 
 */
package tavant.twms.web.common;

import ognl.Ognl;
import ognl.OgnlException;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

import tavant.twms.common.NoValuesDefinedException;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.web.inbox.DefaultPropertyResolver;
import tavant.twms.domain.common.Constants;

/**
 * @author jitesh.jain
 *
 */
public class DisplayImagePropertyResolver extends ImgColAwarePropertyResolver  {

	private final Logger logger = Logger.getLogger(DisplayImagePropertyResolver.class);
	private ConfigParamService configParamService;

	/* (non-JavaDoc)
	 * @see tavant.twms.infra.BeanProvider#getProperty(java.lang.String, java.lang.Object)
	 */

	@SuppressWarnings("unchecked")
	public Object getProperty(String propertyPath, Object target) {
		JSONObject obj;
		try{
			if(Constants.WARNING_IMG_FOR_STOCK_INV.equals(propertyPath)){
				Object parsedExpression = Ognl.parseExpression("shipmentDate");	
				// checking if shipment date is null
				if(Ognl.getValue(parsedExpression,target) != null){
					CalendarDate date = (CalendarDate) Ognl.getValue(parsedExpression,target);
					if(this.configParamService==null){
						initDomainRepository();
					}					
					
					//SelectedBusinessUnitsHolder.setSelectedBusinessUnit(((InventoryItem)target).getBusinessUnitInfo().getName());
										
					Long daysForStockInvWarningWindow = this.configParamService.getLongValue
					(ConfigName.DAYS_FOR_STOCK_IVENTORY_WARNING_WINDOW.getName());
					
					// show warning image if Inventory lying in stock for more than allowed window
					// else a blank image
					if(date.plusDays(new Integer((int)daysForStockInvWarningWindow.longValue()))
							.isBefore(Clock.today())){
	
						int daysSinceShipmentDate = date.through(Clock.today()).lengthInDaysInt();
						
						obj = getImgColValue(getText("message.stockWarning.ageing")+ " " + daysSinceShipmentDate + " " + getText("label.contractAdmin.days"),
											 "image/yellowWarning.gif");
					}else{
						obj = getImgColValue("", "image/trans.gif");
					}
				}else{
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
