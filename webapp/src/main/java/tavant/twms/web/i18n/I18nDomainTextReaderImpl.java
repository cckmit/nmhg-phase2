package tavant.twms.web.i18n;

import java.util.Locale;

import com.opensymphony.xwork2.ActionContext;

import tavant.twms.domain.common.I18nDomainTextReader;

public class I18nDomainTextReaderImpl extends I18nActionSupport implements I18nDomainTextReader {

    private static final long serialVersionUID = 8945568820748520979L;

    public String getProperty(String propertyKey) {
        return getText(propertyKey);
    }
    
    public String getText(String key, String[] args) {
		return super.getText(key, args);
	}
    
    public void setLoggedInUserLocale(String buName) {
		if(getLoggedInUser() != null){
			Locale locale = new Locale(getLoggedInUser().getLocale().getLanguage(), "", buName.toUpperCase());
			ActionContext.getContext().setLocale(locale);	
		}		
	}

}
