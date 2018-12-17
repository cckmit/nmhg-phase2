/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */

package tavant.twms.web.i18n;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderSupport;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.springframework.util.DefaultPropertiesPersister;
import tavant.twms.domain.claim.payment.definition.PaymentSection;
import tavant.twms.domain.claim.payment.definition.PaymentVariableLevel;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;
import tavant.twms.infra.i18n.LocalizedMessages;
import tavant.twms.infra.i18n.LocalizedMessagesService;
import tavant.twms.web.actions.TwmsActionSupport;

public class I18nActionSupport extends TwmsActionSupport {
   private static final Logger logger = Logger.getLogger(I18nActionSupport.class);

    private TextProvider textProvider;

	private LocalizedMessagesService localizedMessagesService;

    /**
     * A kind of action forward that indicates that further setup is required
     * on the business side, before this action can be executed.
     */
    public static final String SETUP = "setup";

    public void init() {
    }

	/**
	 * The default addActionError method is not I18n-aware, i.e it just directly
	 * puts the message into the action error list. This overridden method
	 * instead takes a message key as its argument and then resolves it to the
	 * actual message. The actual message is the one that is put into the action
	 * error list.
	 */
	@Override
	public void addActionError(String messageKey) {
		super.addActionError(getText(messageKey));
	}

	/**
	 * The default addActionMessage method is not I18n-aware, i.e it just
	 * directly puts the message into the action message list. This overridden
	 * method instead takes a message key as its argument and then resolves it
	 * to the actual message. The actual message is the one that is put into the
	 * action message list.
	 */
	@Override
	public void addActionMessage(String messageKey) {
		super.addActionMessage(getText(messageKey));
	}
	
	/**
	 * The default addActionWarning method is not I18n-aware, i.e it just
	 * directly puts the message into the action warning list. This overridden
	 * method instead takes a message key as its argument and then resolves it
	 * to the actual message. The actual message is the one that is put into the
	 * action warning list.
	 */
	@Override
	public void addActionWarning(String messageKey) {
		super.addActionWarning(getText(messageKey));
	}

	/**
	 * The default addFieldError method is not I18n-aware, i.e it just direcly
	 * puts the message into the field error list. This overridden method
	 * instead takes a message key as one of its argument and then resolves it
	 * to the actual message. The actual message is the one that is put into the
	 * field error list.
	 */
	@Override
	public void addFieldError(String field, String messageKey) {
		super.addFieldError(field, getText(messageKey));
	}

	public void addActionError(String messageKey, Object parameterValue) {
		addActionError(messageKey, messageKey, new String[] { parameterValue
				.toString() });
	}

	public void addActionMessage(String messageKey, Object parameterValue) {
		addActionMessage(messageKey, messageKey, new String[] { parameterValue
				.toString() });
	}
	
	public void addActionWarning(String messageKey, Object parameterValue) {
		addActionWarning(messageKey, messageKey, new String[] { parameterValue
				.toString() });
	}
	
	public void addFieldError(String field, String messageKey,
			Object parameterValue) {
		addFieldError(field, messageKey, messageKey,
				new String[] { parameterValue.toString() });
	}

	public void addActionError(String messageKey, String[] parameterValues) {
		addActionError(messageKey, messageKey, parameterValues);
	}

	public void addActionMessage(String messageKey, String[] parameterValues) {
		addActionMessage(messageKey, messageKey, parameterValues);
	}

	public void addFieldError(String field, String messageKey,
			String[] parameterValues) {
		addFieldError(field, messageKey, messageKey, parameterValues);
	}

	public void addActionError(String messageKey, String defaultMessage,
			String[] parameterValues) {
		super.addActionError(getText(messageKey, defaultMessage,
				parameterValues));
	}

	public void addActionMessage(String messageKey, String defaultMessage,
			String[] parameterValues) {
		super.addActionMessage(getText(messageKey, defaultMessage,
				parameterValues));
	}
	
	public void addActionWarning(String messageKey, String defaultMessage,
			String[] parameterValues) {
		super.addActionWarning(getText(messageKey, defaultMessage,
				parameterValues));
	}

	public void addFieldError(String field, String messageKey,
			String defaultMessage, String[] parameterValues) {
		super.addFieldError(field, getText(messageKey, defaultMessage,
				parameterValues));
	}

	@Override
	public String getText(String aTextName) {
		return getTextProvider().getText(aTextName);
	}

	@Override
	public String getText(String aTextName, String defaultValue) {
		return getTextProvider().getText(aTextName, defaultValue);
	}

	@Override
	public String getText(String aTextName, String defaultValue, String obj) {
		return getTextProvider().getText(aTextName, defaultValue, obj);
	}

	@Override
	public String getText(String aTextName, List args) {
		return getTextProvider().getText(aTextName, args);
	}

	@Override
	public String getText(String key, String[] args) {
		return getTextProvider().getText(key, args);
	}

	@Override
	public String getText(String aTextName, String defaultValue, List args) {
		return getTextProvider().getText(aTextName, defaultValue, args);
	}

	@Override
	public String getText(String key, String defaultValue, String[] args) {
		return getTextProvider().getText(key, defaultValue, args);
	}

	@Override
	public String getText(String key, String defaultValue, List args,
			ValueStack stack) {
		return getTextProvider().getText(key, defaultValue, args, stack);
	}

	@Override
	public String getText(String key, String defaultValue, String[] args,
			ValueStack stack) {
		return getTextProvider().getText(key, defaultValue, args, stack);
	}

	@Override
	public ResourceBundle getTexts() {
		return getTextProvider().getTexts();
	}

	@Override
	public ResourceBundle getTexts(String aBundleName) {
		return getTextProvider().getTexts(aBundleName);
	}

	protected TextProvider getTextProvider() {
		if (this.textProvider != null) {
			return this.textProvider;
		}
		/**
		 * This null check is needed because some testcases call this in constructor itself
		 * The service is setter based injection and calling it from constructor
		 * throws NPE
		 */
		if (this.localizedMessagesService !=null && this.localizedMessagesService.isMessageRepositoryEnabled()) {
			ResourceBundle bundle = new PropertyResourceBundle(getBundle());
			this.textProvider = new TextProviderSupport(bundle, this);
		} else {
			this.textProvider = new TextProviderSupport(getClass(), this);
		}
		return this.textProvider;
	}

	private Properties getBundle() {
		Locale locale = getLocale();
		LocalizedMessages localizedMessages = this.localizedMessagesService
				.findById(locale);
		if (localizedMessages == null && locale.getCountry() != null) {
			locale = new Locale(locale.getLanguage());
			localizedMessages = this.localizedMessagesService.findById(locale);
		}

		Properties properties = new Properties();
		DefaultPropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
		try {
			propertiesPersister.load(properties, new StringReader(
					localizedMessages.getMessages()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return properties;
	}

	public void setLocalizedMessagesService(
			LocalizedMessagesService localizedMessagesService) {
		this.localizedMessagesService = localizedMessagesService;
	}

    public Map<String, String> getPrettyPrintLineItems(Section section, PaymentSection paymentSection) {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put(getText(section.getI18NMessageKey(section.getName())), getText(section.getI18NMessageKey(section.getName())));
        LevelMap levelVariables = new LevelMap();
        levelVariables.put(0, getText(section.getI18NMessageKey(section.getName())));
        for (PaymentVariableLevel pvl : getSortedPaymentVariableLevels(paymentSection)) {
            PaymentVariable paymentVariable = pvl.getPaymentVariable();
            String amountValueAsFormula = paymentVariable.getAmountName() + " * "
                    + levelVariables.getPreviousValue(pvl.getLevel());
            if(logger.isDebugEnabled())
            {
                logger.debug("Formula is [" + amountValueAsFormula + "]");
            }
            // alter the levels name
            int levelToChange = levelVariables.getClosestKeyLessThanOrEqualTo(pvl.getLevel());
            String oldAmount = levelVariables.get(levelToChange).replaceAll("[(]", "")
                    .replaceAll("[)]", "").trim();
            String currentLevelAmountName = "( " + oldAmount + " + " + paymentVariable.getName()
                    + " )";
            if(logger.isDebugEnabled())
            {
                logger.debug("Changing the level [" + pvl.getLevel() + "] to be ["
                    + currentLevelAmountName + "]");
            }
            levelVariables.put(pvl.getLevel(), currentLevelAmountName);
            values.put(paymentVariable.getName(), amountValueAsFormula);
        }
        return values;
    }

    static class LevelMap extends HashMap<Integer, String> {

        private static final long serialVersionUID = 1L;

        Integer getPreviousKey(Integer key) {
            SortedSet<Integer> keys = new TreeSet<Integer>(keySet());
            if(logger.isDebugEnabled())
            {
                logger.debug("Keys are " + keys);
            }
            SortedSet<Integer> keysLessThanChosenKey = keys.headSet(key);
            if(logger.isDebugEnabled())
            {
                logger.debug("Keys less than [" + key + "] are " + keysLessThanChosenKey);
            }
            return keysLessThanChosenKey.isEmpty() ? null : keysLessThanChosenKey.last();
        }

        public Integer getClosestKeyLessThanOrEqualTo(Integer key) {
            return containsKey(key) ? key : getPreviousKey(key);
        }

        public String getPreviousValue(Integer key) {
            return get(getPreviousKey(key));
        }
    }

    @SuppressWarnings("unchecked")
    public List<PaymentVariableLevel> getSortedPaymentVariableLevels(PaymentSection paymentSection) {
        List<PaymentVariableLevel> paymentVarLevels = new ArrayList<PaymentVariableLevel>();
        paymentVarLevels.addAll(paymentSection.getPaymentVariableLevels());
        Collections.sort((paymentVarLevels), getComparatorForLevel());
        return paymentVarLevels;
    }


    private Comparator getComparatorForLevel() {
        Comparator comparator;
        comparator = new Comparator() {
            public int compare(Object obj1, Object obj2) {

                PaymentVariableLevel param1 = (PaymentVariableLevel) obj1;
                PaymentVariableLevel param2 = (PaymentVariableLevel) obj2;
                return new CompareToBuilder().append(param1.getLevel(), param2.getLevel())
                        .append(param1.getPaymentVariable().getName(),
                                param2.getPaymentVariable().getName()).toComparison();
            }
        };
        return comparator;
    }
	
	public void setActionErrors(Map<String, String[]> errorCodes) {
        Iterator<String> iterator = errorCodes.keySet().iterator();
        while (iterator.hasNext()) {
            String errorKey = iterator.next();
            String[] errorValue = errorCodes.get(errorKey);
            if(errorValue == null) {
            	addActionError(errorKey);
            } else {
            	addActionError(errorKey, errorValue);
            }
        }
    }
    

}
