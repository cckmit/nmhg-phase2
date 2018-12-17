package tavant.twms.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.I18nInterceptor;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

@SuppressWarnings("serial")
public class BUSpecificLabelsLocaleInterceptor extends I18nInterceptor {

    private static final Logger log = Logger.getLogger(BUSpecificLabelsLocaleInterceptor.class);
    
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("intercept '" + invocation.getProxy().getNamespace()
					+ "/" + invocation.getProxy().getActionName() + "' { ");
		}
		//get requested locale
        Map params = invocation.getInvocationContext().getParameters();
        Object requested_locale = params.remove(parameterName);
        if (requested_locale != null && requested_locale.getClass().isArray()
                && ((Object[]) requested_locale).length == 1) {
            requested_locale = ((Object[]) requested_locale)[0];
        }

        if (log.isDebugEnabled()) {
            log.debug("requested_locale=" + requested_locale);
        }
        
		// save it in session
		Map session = invocation.getInvocationContext().getSession();
		if (session != null) {
			// set locale for action
			Object locale = session.get(attributeName);
			if (locale != null && locale instanceof Locale) {
				if (log.isDebugEnabled()) {
					log.debug("apply locale=" + locale);
				}

				// First seeing if thread local BU name is set
				String buToSet = SelectedBusinessUnitsHolder
						.getSelectedBusinessUnit();
				if (buToSet != null) {
					// User is performing a BU specific action - fr__ASG
					Locale localeToSave = new Locale(((Locale) locale)
							.getLanguage(), "", buToSet.toUpperCase());
					saveLocale(invocation, localeToSave);
				} else {
					// User may still be performing a BU specific action but he
					// may be a single line dealer and hence
					// thread local variable is null
					
					BusinessUnit bu = null;
                    String prefferedBu = null;
					
					try
					{
						bu = new SecurityHelper().getDefaultBusinessUnit();
						if(bu==null){
							SortedSet<BusinessUnit> businessUnits = new SecurityHelper()
									.getLoggedInUser().getBusinessUnits();
							if (CollectionUtils.isNotEmpty(businessUnits)){
                                if(businessUnits.size() == 1) {
								    bu = businessUnits.first();
							    }else if(businessUnits.size() > 1){
                                    prefferedBu = new SecurityHelper().getLoggedInUser().getPreferredBu();
                                }
                            }
						}
					}
					catch(IllegalStateException e)
					{
						log.debug("Illegal attempt to retrieve the logged-in user in an unauthenticated context from BUSpecificLabelsLocaleInterceptor" + e);
					}
					if (bu != null) {
						buToSet = bu.getName();
						// User is performing a BU specific action - fr__ASG
						Locale localeToSave = new Locale(((Locale) locale)
								.getLanguage(), "", buToSet.toUpperCase());
						saveLocale(invocation, localeToSave);
					}else if( null != prefferedBu && StringUtils.hasText(prefferedBu)){
                        Locale localeToSave = new Locale(((Locale) locale)
                                .getLanguage(), "", prefferedBu);
                        saveLocale(invocation, localeToSave);
                    }
                    else {
						saveLocale(invocation, (Locale) locale);
					}
				}

			}
		}

		if (log.isDebugEnabled()) {
			log.debug("before Locale="
					+ invocation.getStack().findValue("locale"));
		}

		final String result = invocation.invoke();
		if (log.isDebugEnabled()) {
			log.debug("after Locale="
					+ invocation.getStack().findValue("locale"));
		}

		if (log.isDebugEnabled()) {
			log.debug("intercept } ");
		}
		return result;
	}
}
