package tavant.twms.integration.server.util;

import java.util.regex.Matcher;

import org.springframework.util.StringUtils;

import tavant.twms.integration.server.common.dataaccess.SyncTrackerDAO;

/**
 *
 * @author prasad.r
 */

/**
 * Utility class for finding the BU name from the input XML.
 * Caches the Division Code - BU Name in a map to avoid querying
 * @author prasad.r
 */
public class BUNameUtil {

    public static String getBusinessUnitName(String xml,SyncTrackerDAO syncTrackerDAO){
        String businessUnitName = null;
        Matcher m = IntegrationServerConstants.BU_NAME_REGEX.matcher(xml);
        if(m.find()){
        	businessUnitName = m.group(2);
        	if(StringUtils.hasText(businessUnitName)){
        		if(!(businessUnitName.equalsIgnoreCase(IntegrationServerConstants.NMHG_EMEA)||businessUnitName.equalsIgnoreCase(IntegrationServerConstants.NMHG_US))){
        			if (businessUnitName
        					.equalsIgnoreCase(IntegrationServerConstants.DIVISON_CODE_US)) {
        				businessUnitName=IntegrationServerConstants.NMHG_US;
        			} else if (businessUnitName
        					.equalsIgnoreCase(IntegrationServerConstants.DIVISON_CODE_EMEA)) {
        				businessUnitName=IntegrationServerConstants.NMHG_EMEA;
        			}
        			else{
        				businessUnitName=null;
        			}
        		}
        	}
        }else {
            String divisionCode = null;
            m =IntegrationServerConstants.DIVISION_CODE_REGEX.matcher(xml);
            if(m.find()){
                divisionCode = m.group(2);
            }
            if(StringUtils.hasText(divisionCode)){
            	 if (divisionCode
        				.equalsIgnoreCase(IntegrationServerConstants.DIVISON_CODE_US)) {
            		businessUnitName=IntegrationServerConstants.NMHG_US;
        		} else if (divisionCode
        				.equalsIgnoreCase(IntegrationServerConstants.DIVISON_CODE_EMEA)) {
        			businessUnitName=IntegrationServerConstants.NMHG_EMEA;
        		}
            }else{
            	businessUnitName=IntegrationServerConstants.NMHG_EMEA;
            }
        }
        return businessUnitName;
    }

}
