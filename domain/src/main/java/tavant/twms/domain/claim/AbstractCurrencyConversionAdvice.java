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
package tavant.twms.domain.claim;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.common.CurrencyExchangeRateRepository;
import tavant.twms.domain.common.CurrencyFieldCollector;

/**
 * @author radhakrishnan.j
 * 
 */
public abstract class AbstractCurrencyConversionAdvice {

    protected CurrencyExchangeRateRepository currencyExchangeRateRepository;

    protected CurrencyFieldCollector currencyFieldCollector;

    @Required
    public void setCurrencyExchangeRateRepository(
            CurrencyExchangeRateRepository currencyExchangeRateRepository) {
        this.currencyExchangeRateRepository = currencyExchangeRateRepository;
    }

    @Required
    public void setCurrencyFieldCollector(CurrencyFieldCollector currencyFieldCollector) {
        this.currencyFieldCollector = currencyFieldCollector;
    }

}