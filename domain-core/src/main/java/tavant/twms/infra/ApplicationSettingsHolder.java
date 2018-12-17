package tavant.twms.infra;

/**
 * Master class for reading properties from twms.properties.
 * For introducing any new property, make an entry in twms.properties
 * and add getters/setters here and an entry for injection in app-context
 * as well.
 * @author ramalakshmi.p
 *
 */
public class ApplicationSettingsHolder {

	private String logoutRequired;

	private String googleAnalyticsCode;

	private String googleAnalyticsEnabled;

	private String externalUrl;
	
	private String captureShipentDateForMajorComp;

    private String ldapAuthenticationEnabled;
    
    private String cmsUrl;
    
    private String emailWpraTemplate;
    
    private String emailCannotShipTemplate;
    
    private String defaultLocation;
    
    private boolean appSSOEnabled;
    
    public String getEmailWpraTemplate() {
		return emailWpraTemplate;
	}

	public void setEmailWpraTemplate(String emailWpraTemplate) {
		this.emailWpraTemplate = emailWpraTemplate;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getExternalUrlForEmail() {
		return externalUrlForEmail;
	}

	public void setExternalUrlForEmail(String externalUrlForEmail) {
		this.externalUrlForEmail = externalUrlForEmail;
	}

	private String fromAddress;
    
    private String externalUrlForEmail;

	public String getCmsUrl() {
		return cmsUrl;
	}

	public void setCmsUrl(String cmsUrl) {
		this.cmsUrl = cmsUrl;
	}

	public String getLogoutRequired() {
		return logoutRequired;
	}

	public void setLogoutRequired(String logoutRequired) {
		this.logoutRequired = logoutRequired;
	}

	public String getGoogleAnalyticsCode() {
		return googleAnalyticsCode;
	}

	public void setGoogleAnalyticsCode(String googleAnalyticsCode) {
		this.googleAnalyticsCode = googleAnalyticsCode;
	}

	public String getGoogleAnalyticsEnabled() {
		return googleAnalyticsEnabled;
	}

	public void setGoogleAnalyticsEnabled(String googleAnalyticsEnabled) {
		this.googleAnalyticsEnabled = googleAnalyticsEnabled;
    }

	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	public String getCaptureShipentDateForMajorComp() {
		return captureShipentDateForMajorComp;
	}

	public void setCaptureShipentDateForMajorComp(String captureShipentDateForMajorComp) {
		this.captureShipentDateForMajorComp = captureShipentDateForMajorComp;
	}

    public String getLdapAuthenticationEnabled() {
        return ldapAuthenticationEnabled;
    }

    public void setLdapAuthenticationEnabled(String ldapAuthenticationEnabled) {
        this.ldapAuthenticationEnabled = ldapAuthenticationEnabled;
    }

	/**
	 * @return the defaultLocation
	 */
	public String getDefaultLocation() {
		return defaultLocation;
	}

	/**
	 * @param defaultLocation the defaultLocation to set
	 */
	public void setDefaultLocation(String defaultLocation) {
		this.defaultLocation = defaultLocation;
	}

	public boolean isAppSSOEnabled() {
    	return appSSOEnabled;
    }

	public void setAppSSOEnabled(boolean appSSOEnabled) {
    	this.appSSOEnabled = appSSOEnabled;
    }

	public String getEmailCannotShipTemplate() {
		return emailCannotShipTemplate;
	}

	public void setEmailCannotShipTemplate(String emailCannotShipTemplate) {
		this.emailCannotShipTemplate = emailCannotShipTemplate;
	}
	
}
