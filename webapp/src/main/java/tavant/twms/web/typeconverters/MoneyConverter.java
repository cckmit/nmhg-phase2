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
package tavant.twms.web.typeconverters;

import com.domainlanguage.money.Money;
import ognl.DefaultTypeConverter;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Currency;
import java.util.Map;

public class MoneyConverter extends DefaultTypeConverter {

    private Logger logger = Logger.getLogger(MoneyConverter.class);

    @Override
    public Object convertValue(Map ctx, Object obj, Class toType) {

        Object returnedValue = null; 

        if (toType == Money.class) {
            
            Assert.notEmpty((String[])obj,"object should hold the values for currency and amount");
            
            String currencyType = ((String[]) obj)[0];
            String amount = ((String[]) obj)[1];
            
            if(StringUtils.hasText(amount)) {
                
                Assert.hasText(currencyType,"currency has not been set");
                // TODO this is because breachEncapsulationOfCurrency().symbol gives $ instead of USD
                // will be removed once the currency code is passed instead of symbol 
                if(currencyType.equals("$")){
                    currencyType="USD";
                }
                Currency currency = Currency.getInstance(currencyType);

                try {
                    returnedValue = Money.valueOf(Double.parseDouble(amount), currency);
                } catch (NumberFormatException exception) {
                    returnedValue = Money.valueOf(new Double(0.00), currency);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Entered string for money fields" + amount);
                    }
                }
                
                if (logger.isDebugEnabled()) {
                    logger.debug("Converted amount [" + amount 
                            + "] to Money [" + returnedValue + "]");
                }
            }else if(StringUtils.hasText(currencyType)){
                //Assert.hasText(currencyType,"currency has not been set");
                // TODO this is because breachEncapsulationOfCurrency().symbol gives $ instead of USD
                // will be removed once the currency code is passed instead of symbol
                if(currencyType.equals("$")){
                    currencyType="USD";
                }
                Currency currency = Currency.getInstance(currencyType);
                returnedValue = Money.valueOf(new Double(0.00),currency);

                if (logger.isDebugEnabled()) {
                    logger.debug("Created zero value money object since the entered value is null");
                }
            } else {
            	returnedValue = null;
            }
        } else if (toType == String.class) {

            Money money = (Money) obj;
            
            if(money != null) {
                returnedValue = money.toString();
                
                if (logger.isDebugEnabled()) {
                    logger.debug("Converted Money [" + money 
                            + "] to amount [" + returnedValue + "]");
                }
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Ignoring unsupported object type : [" 
                        + toType + "]");
            }            
        }

        return returnedValue;
    }

}