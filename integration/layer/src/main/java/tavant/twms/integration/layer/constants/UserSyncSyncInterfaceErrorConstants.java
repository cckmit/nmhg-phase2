package tavant.twms.integration.layer.constants;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.twms.domain.common.I18nDomainTextReader;

public class UserSyncSyncInterfaceErrorConstants {


    
    public final static String TK001 = "TK001";
    
    public final static String TK002 = "TK002";
    
    public final static String TK003 = "TK003";
    
    public final static String TK004 = "TK004";
    
    public final static String TK005 = "TK005";
    
    public final static String TK006 = "TK006";
    
    public final static String TK007 = "TK007";
    
    public final static String TK008 = "TK008";
    
    public final static String TK009 = "TK009";
    
    public final static String TK010 = "TK010";
    
    public final static String TK011 = "TK011";
    
    public final static String TK012 = "TK012";
    
    public final static String TK013 = "TK013";
    
    public final static String TK014 = "TK014";

	private final static Map<String, String> errorCodeMap = new HashMap<String, String>();
	
	

	static {
	        /**
	         * General Customer Error Codes
	         */
		 	errorCodeMap.put(TK001,"technicianSync.user.id.isrequired");
		 	errorCodeMap.put(TK002,"technicianSync.user.dealer.code.isrequired");
		 	errorCodeMap.put(TK003,"technicianSync.user.dealer.code.isnotvalid");
		 	errorCodeMap.put(TK004,"technicianSync.certification.title.required");
		 	errorCodeMap.put(TK005,"technicianSync.certification.dateachevied.required");
		 	errorCodeMap.put(TK006,"technicianSync.certification.brand.required");
		 	errorCodeMap.put(TK007,"technicianSync.certification.dateexpired.required");
		 	errorCodeMap.put(TK008,"technicianSync.certification.Category.mappin.does.not.exist");
		 	errorCodeMap.put(TK009,"technicianSync.invalid.dealer.code");
		 	errorCodeMap.put(TK010,"technicianSync.unknown.error");
		 	errorCodeMap.put(TK011,"technicianSync.dateacheived.is.required");
		 	errorCodeMap.put(TK012,"technicianSync.brand.is.required");
		 	errorCodeMap.put(TK013,"technicianSync.date.expired.is.requird");
		 	errorCodeMap.put(TK014,"technicianSync.login.is.dealer");
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
		 return i18nDomainTextReader.getProperty(errorCodeMap.get(errorCode)!=null?errorCodeMap.get(errorCode).trim():null);
		}
		return null;
	}
	 public String getPropertyMessage(final String errorMessageKey, String[] args) {
	    	return i18nDomainTextReader.getText(errorMessageKey, args);
	    }
	 private I18nDomainTextReader i18nDomainTextReader;
		
	 public I18nDomainTextReader getI18nDomainTextReader() {
		return i18nDomainTextReader;
	}

	public void setI18nDomainTextReader(I18nDomainTextReader i18nDomainTextReader) {
		this.i18nDomainTextReader = i18nDomainTextReader;
	}
	public String getPropertyMessageVlaue(final String errorMessageKey, String[] args) {
    	return i18nDomainTextReader.getText(errorCodeMap.get(errorMessageKey), args);
    }
}