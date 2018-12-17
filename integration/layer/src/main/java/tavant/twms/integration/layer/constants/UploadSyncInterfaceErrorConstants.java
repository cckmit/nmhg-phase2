package tavant.twms.integration.layer.constants;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.twms.domain.common.I18nDomainTextReader;

public class UploadSyncInterfaceErrorConstants {

	public final static String CU001 = "CU001";

	public final static String CU007 = "CU007";
	
	public final static String CU008 = "CU008";

	public final static String CU019 = "CU019";

	public final static String CU004 = "CU004";

	public final static String CU003 = "CU003";

	public final static String CU018 = "CU018";

	public final static String CU0029 = "CU0029";
	
	public final static String CU0030 = "CU0030";
	
	public final static String CU0031 = "CU0031";

	public final static String CU0036 = "CU0036";

	public final static String CU0037 = "CU0037";

	public final static String CU0039 = "CU0039";

	public final static String CU0040 = "CU0040";

	public final static String CU0041 = "CU0041";

    public final static String CU0042 = "CU0042";

    public final static String CU0043 = "CU0043";

    public final static String CU0044 = "CU0044";

    public final static String CU0045 = "CU0045";

    public final static String CU0046 = "CU0046";

    public final static String CU0050 = "CU0050";
    
    public final static String CU0051 = "CU0051";
    
    public final static String CU0053 = "CU0053";
    
    public final static String CU0056 = "CU0056";
    
    public final static String CU0057 = "CU0057";
    
    public final static String CU0058 = "CU0058";
    
    public final static String CU0059 = "CU0059";
    
    public final static String CU0060 = "CU0060";

    public final static String CU0061 = "CU0061";
    
    public final static String CU0062 = "CU0062";
    
    public final static String CU0063 = "CU0063";
    
    public final static String CU0064 = "CU0064";
    
    public final static String CU0065 = "CU0065";
    
    public final static String CU0066 = "CU0066";
    
    public final static String CU0067 = "CU0067";
    
    public final static String CU0068 = "CU0068";
    
    public final static String CU0069 = "CU0069";
    
    public final static String CU0070 = "CU0070";
    
    public final static String CU0071 = "CU0071";
    
    public final static String CU0072 = "CU0072";
    
    public final static String CU0073 = "CU0073";



	
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
	         * General Customer Error Codes
	         */
		 	errorCodeMap.put(CU001,"uploadSync.error.businessUnit");
		 	errorCodeMap.put(CU007,"uploadSync.error.city");
		    errorCodeMap.put(CU008,"uploadSync.error.country");
		    errorCodeMap.put(CU019,"uploadSync.error.status");
		    errorCodeMap.put(CU004,"uploadSync.error.customerName");
		    errorCodeMap.put(CU003,"uploadSync.error.customerNumberNotSpecifed ");
		    errorCodeMap.put(CU018,"uploadSync.error.invalidCurrency");
		    errorCodeMap.put(CU0029,"uploadSync.error.primaryAddressInvalid");
		    errorCodeMap.put(CU0030,"uploadSync.error.shipAddressInvalid");
		    errorCodeMap.put(CU0031,"uploadSync.error.invalidShipToSiteNumber");
            errorCodeMap.put(CU0036,"uploadSync.error.noMarketingGroup");
            errorCodeMap.put(CU0037,"uploadSync.error.noBrand");
            errorCodeMap.put(CU0039,"uploadSync.error.noSellingLocation");
            errorCodeMap.put(CU0040,"uploadSync.error.noServiceProviderType");
            errorCodeMap.put(CU0041,"uploadSync.error.noServiceProviderDesc");
            errorCodeMap.put(CU0042,"uploadSync.error.invalidDualBrandDealership");
            errorCodeMap.put(CU0043,"uploadSync.error.invalidFamilyCode");
            errorCodeMap.put(CU0044,"uploadSync.error.noXmlacknowledgementFooter");
            errorCodeMap.put(CU0045,"uploadSync.error.noNetwork");
            errorCodeMap.put(CU0046,"uploadSync.error.noLanguage");
            errorCodeMap.put(CU0050,"uploadSync.error.noState");
            errorCodeMap.put(CU0051,"uploadSync.invalid.businessUnit");
            errorCodeMap.put(CU0053,"uploadSync.error.syncingCustomerName");
            errorCodeMap.put(CU0056,"uploadSync.nullCheck.companyName");
            errorCodeMap.put(CU0057,"uploadSync.invalid.companyType");
            errorCodeMap.put(CU0058,"uploadSync.invalid.dealerStatus");
            errorCodeMap.put(CU0059,"uploadSync.invalid.status");
            errorCodeMap.put(CU0060,"uploadSync.invalid.shipToChanged");
            errorCodeMap.put(CU0061,"uploadSync.nullCheck.isPrimary");
            errorCodeMap.put(CU0062,"uploadSync.nullCheck.zipcode");
            errorCodeMap.put(CU0063,"uploadSync.nullCheck.isEndCustomer");
            errorCodeMap.put(CU0064,"uploadSync.error.invalidBillToSiteNumber");
            errorCodeMap.put(CU0065,"uploadSync.error.invalidIsPrimary");
            errorCodeMap.put(CU0066,"uploadSync.error.dealer.does.not.exist");
            errorCodeMap.put(CU0067,"uploadSync.invalid.currency");
            errorCodeMap.put(CU0068,"uploadSync.exception.customerSync");
            errorCodeMap.put(CU0069,"uploadSync.currency.null");
            
            errorCodeMap.put(CU0070,"uploadSync.currency.changed");
            errorCodeMap.put(CU0071,"uploadSync.customer.type.changed");
            errorCodeMap.put(CU0071,"uploadSync.customer.type.changed.active.inventory");
            errorCodeMap.put(CU0073,"uploadSync.currency.value.null");
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

}