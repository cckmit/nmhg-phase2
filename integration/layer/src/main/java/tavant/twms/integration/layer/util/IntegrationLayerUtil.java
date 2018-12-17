package tavant.twms.integration.layer.util;

import tavant.twms.integration.layer.constants.IntegrationConstants;

public class IntegrationLayerUtil extends IntegrationConstants {
	public static String getBuName(String name) {
		if (name.equalsIgnoreCase(IntegrationConstants.US)
				|| name.equalsIgnoreCase(IntegrationConstants.NMHG_US)) {
			return IntegrationConstants.US;
		} else if (name.equalsIgnoreCase(IntegrationConstants.EMEA)
				|| name.equalsIgnoreCase(IntegrationConstants.NMHG_EMEA)) {
			return IntegrationConstants.EMEA;
		}
		return null;
	}

	public static String getDealerNumber(String dealerNumber) {
		if (dealerNumber != null) {
			String[] dealerNumberArray = dealerNumber.split("_");
			String dealerNumberStr = null;
			if (dealerNumberArray.length > 1) {
				dealerNumberStr = dealerNumberArray[0];
			} else {
				dealerNumberStr = dealerNumber;
			}
			String dealerNumbers = dealerNumberStr.length() <= 6 ? dealerNumberStr
					: dealerNumberStr.substring(dealerNumberStr.length() - 6);
			return dealerNumbers;
		}
		return EMPTY_STRING;
	}
	public static String getDealerSiteNumber(String dealerSiteNumberString) {
		if (dealerSiteNumberString != null) {
			if (dealerSiteNumberString.length() >= 12) {
				String dealerSiteNumber = dealerSiteNumberString.substring(8,
						12);
				return dealerSiteNumber;
			} else if (dealerSiteNumberString.length() >= 9
					&& dealerSiteNumberString.length() < 12) {
				String dealerSiteNumber = dealerSiteNumberString.substring(8,
						dealerSiteNumberString.length());
				return dealerSiteNumber;
			}else{
				return dealerSiteNumberString;
			}
		}
		return EMPTY_STRING;
	}
	
	public static String getSubString(String sourceString, int maxLenghth) {
		if (sourceString!=null&&sourceString.length() > maxLenghth) {
			return sourceString.substring(0, maxLenghth);
		}
		return sourceString;
	}
	
	public static boolean isAMERBusinessUnit(String buName) {
		if (buName != null) {
			if (IntegrationConstants.US.equalsIgnoreCase(buName)
					|| IntegrationConstants.NMHG_US.equalsIgnoreCase(buName)) {
				return true;
			}
		}
		return false;
	}
	
	public static String getBusinessUnit(String name) {
		if (name.equalsIgnoreCase(IntegrationConstants.US)
				|| name.equalsIgnoreCase(IntegrationConstants.NMHG_US)) {
			return IntegrationConstants.NMHG_US;
		} else if (name.equalsIgnoreCase(IntegrationConstants.EMEA)
				|| name.equalsIgnoreCase(IntegrationConstants.NMHG_EMEA)) {
			return IntegrationConstants.NMHG_EMEA;
		}
		return null;
	}

}
