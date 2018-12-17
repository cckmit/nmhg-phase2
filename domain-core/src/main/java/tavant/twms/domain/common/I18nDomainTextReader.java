package tavant.twms.domain.common;

public interface I18nDomainTextReader {
	
    public String getProperty(String propertyKey);
    
    public String getText(String key, String[] args);
    
    public void setLoggedInUserLocale(String buName);
}
