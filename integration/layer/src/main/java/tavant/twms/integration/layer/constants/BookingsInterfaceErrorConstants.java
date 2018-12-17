package tavant.twms.integration.layer.constants;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.twms.domain.common.I18nDomainTextReader;

public class BookingsInterfaceErrorConstants {

	
	private final static Map<String, String> errorCodeMap = new HashMap<String, String>();
	

	private I18nDomainTextReader i18nDomainTextReader;

	public I18nDomainTextReader getI18nDomainTextReader() {
		return i18nDomainTextReader;
	}

	public void setI18nDomainTextReader(I18nDomainTextReader i18nDomainTextReader) {
		this.i18nDomainTextReader = i18nDomainTextReader;
	}

	public final static String B001 = "B001";
	public final static String B002 = "B002";
	public final static String B003 = "B003";
	public final static String B004 = "B004";
	public final static String B005 = "B005";
	public final static String B006 = "B006";
	public final static String B007 = "B007";
	public final static String B008 = "B008";


	
	

	static {
		/**
		 * General Item Error Codes
		 */
		errorCodeMap.put(B001,"bookings.nullCheck.buName");
		errorCodeMap.put(B002,"bookings.nullCheck.unitSerialNumber");
		errorCodeMap.put(B003,"bookings.nullCheck.transactionDateTime");
		errorCodeMap.put(B004,"bookings.nullCheck.transactionType");
		errorCodeMap.put(B005,"bookings.inventory.retailed");
		errorCodeMap.put(B007,"bookings.error.runtime.exception");
		errorCodeMap.put(B008,"bookings.error.inventory.not.exists");
		
		
		
		
	}

	public String getErrorMessage(final String errorCode) {
		String messageKey = errorCodeMap.get(errorCode);
		return messageKey;
	}
	
	public String getPropertyMessage(final String messageKey) {
		return i18nDomainTextReader.getProperty(messageKey);
	}

	public String getPropertyMessageFromErrorCode(final String errorCode) {
		if(StringUtils.hasText(errorCode)){
			return i18nDomainTextReader.getProperty(errorCodeMap.get(errorCode).trim());
		}
		return null;
	}
	public String getPropertyMessage(final String errorCode, String[] args) {
		return i18nDomainTextReader.getText(getErrorMessage(errorCode), args);
	}

}
