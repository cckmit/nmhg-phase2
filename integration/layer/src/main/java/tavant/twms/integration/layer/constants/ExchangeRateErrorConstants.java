package tavant.twms.integration.layer.constants;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.twms.domain.common.I18nDomainTextReader;

public class ExchangeRateErrorConstants {

	
	
	private final static Map<String, String> errorCodeMap = new HashMap<String, String>();
	

	private I18nDomainTextReader i18nDomainTextReader;

	public I18nDomainTextReader getI18nDomainTextReader() {
		return i18nDomainTextReader;
	}

	public void setI18nDomainTextReader(I18nDomainTextReader i18nDomainTextReader) {
		this.i18nDomainTextReader = i18nDomainTextReader;
	}

	public final static String EX001 = "EX001";
	public final static String EX002 = "EX002";
	public final static String EX003 = "EX003";
	public final static String EX004 = "EX004";
	public final static String EX005 = "EX005";
	public final static String EX006 = "EX006";
	public final static String EX007 = "EX007";
	public final static String EX008 = "EX008";
	public final static String EX009 = "EX009";
	public final static String EX0010 = "EX0010";

	

	
	

	static {
		/**
		 * General Item Error Codes
		 */
		errorCodeMap.put(EX001,"exr.startDae.notNull");
		errorCodeMap.put(EX002,"exr.endtDae.notNull");
		errorCodeMap.put(EX003,"exr.fromCur.notNull");
		errorCodeMap.put(EX004,"exr.toCur.notNull");
		errorCodeMap.put(EX005,"exr.rate.notNull");
		errorCodeMap.put(EX007,"exr.error.runtime.exception");
		errorCodeMap.put(EX008,"exr.error.xml");
		errorCodeMap.put(EX009,"exr.rate.defined.between.start.end.date");
		errorCodeMap.put(EX0010,"exr.rate.missing.between.start.end.date");

		
		
		
		
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
