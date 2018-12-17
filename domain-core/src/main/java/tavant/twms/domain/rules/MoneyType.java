/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.rules;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import tavant.twms.domain.common.GlobalConfiguration;

import com.domainlanguage.money.Money;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MoneyType")
public class MoneyType extends NumberType implements LiteralSupport {
    Set<Class<? extends Predicate>> predicates;
    
    @Override
    public Set<Class<? extends Predicate>> supportedPredicates() {
        return this.predicates ;
    }
    

    
    public MoneyType() {
        predicates = new HashSet<Class<? extends Predicate>>();

        predicates.add(Equals.class);
        predicates.add(NotEquals.class);
        predicates.add(LessThan.class);
        predicates.add(LessThanOrEquals.class);
        predicates.add(GreaterThan.class);
        predicates.add(GreaterThanOrEquals.class);
        predicates.add(IsSet.class);
        predicates.add(IsNotSet.class);
    }

    public Object getJavaObject(String literal) {
        String[] currencyCodeAndValue = literal.trim().split("\\s");
        MathContext mathContext = new MathContext(16, RoundingMode.HALF_EVEN);
        if (currencyCodeAndValue.length == 2) {
            Currency currency = Currency.getInstance(currencyCodeAndValue[0]);
            BigDecimal bigDecimal = new BigDecimal(currencyCodeAndValue[1], mathContext);
            return Money.valueOf(bigDecimal, currency);
        } else {
            BigDecimal bigDecimal = new BigDecimal(currencyCodeAndValue[0], mathContext);
            // Currency code was not specified.
            return Money.valueOf(bigDecimal, GlobalConfiguration.getInstance().getBaseCurrency());
        }
    }

    public String getEvaluableExpression(String literal) {
        String[] currencyCodeAndValue = literal.trim().split("\\s");
        if (logger.isDebugEnabled()) {
            logger.debug(MessageFormat.format(" Money literal {0} splits to {1}", literal, Arrays
                    .asList(currencyCodeAndValue)));
        }        
        if (currencyCodeAndValue.length == 1) {
            // Currency code was not specified.
            return "@com.domainlanguage.money.Money@valueOf(new java.math.BigDecimal(" + currencyCodeAndValue[0]
                    + ",@java.math.MathContext@DECIMAL32),@tavant.twms.domain.common.GlobalConfiguration@getInstance().baseCurrency)";
        } else {
            return "@com.domainlanguage.money.Money@valueOf(new java.math.BigDecimal(" + currencyCodeAndValue[1]
                    + ",@java.math.MathContext@DECIMAL32),@java.util.Currency@getInstance(\"" + currencyCodeAndValue[0] + "\"))";
        }
    }

    public String getName() {
        return MONEY;
    }

    public boolean supportsLiteral() {
        return true;
    }

    public boolean isLiteralValid(String literal) {
        try {
            getJavaObject(literal);
            return true;
        } catch (RuntimeException e) {
            logger.error(" '" + literal + "' is not a valid '" + getName() + "' literal");
        }
        return false;
    }



    public void setPredicates(Set<Class<? extends Predicate>> predicates) {
        this.predicates = predicates;
    }

    public String getLiteralForDefaultValue() {
        return null;
    }
    
    
    
}