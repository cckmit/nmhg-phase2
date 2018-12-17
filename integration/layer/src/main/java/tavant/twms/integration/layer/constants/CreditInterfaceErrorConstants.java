package tavant.twms.integration.layer.constants;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.twms.domain.common.I18nDomainTextReader;

public class CreditInterfaceErrorConstants {

	public final static String CN001 = "CN001";
	
	public final static String CN002 = "CN002";
	
	public final static String CN003 = "CN003";
	
	public final static String CN004 = "CN004";
	
	public final static String CN005 = "CN005";
	
	public final static String CN006 = "CN006";
	
	public final static String CN007 = "CN007";
	
	public final static String CN008 = "CN008";
	
	private final static Map<String, String> errorCodeMap = new HashMap<String, String>();

	private I18nDomainTextReader i18nDomainTextReader;

	public I18nDomainTextReader getI18nDomainTextReader() {
		return i18nDomainTextReader;
	}

	public void setI18nDomainTextReader(I18nDomainTextReader i18nDomainTextReader) {
		this.i18nDomainTextReader = i18nDomainTextReader;
	}

	static {
		/**
		 * General Item Error Codes
		 */
		errorCodeMap.put(CN001,"creditNAPI.notExists.claim");
		errorCodeMap.put(CN002,"creditNAPI.notMatched.amountForUSD");
		errorCodeMap.put(CN003,"creditNAPI.notMatched.amount");
		errorCodeMap.put(CN004,"creditAPI.failure.currencyConversion");
		errorCodeMap.put(CN005,"creditNAPI.invalid.state");
		errorCodeMap.put(CN006,"creditNAPI.invalid.currencycode");
		errorCodeMap.put(CN007,"creditNAPI.credit_or_debit_identifier");
		errorCodeMap.put(CN008,"creditNAPI.exception");
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
