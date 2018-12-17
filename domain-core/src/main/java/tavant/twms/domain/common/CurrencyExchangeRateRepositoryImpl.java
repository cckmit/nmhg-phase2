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
package tavant.twms.domain.common;

import java.sql.SQLException;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
public class CurrencyExchangeRateRepositoryImpl extends GenericRepositoryImpl<CurrencyExchangeRate,Long> implements
        CurrencyExchangeRateRepository {

    public CurrencyExchangeRate findCurrencyExchangeRate(Currency fromCurrency, Currency toCurrency) {
        if(fromCurrency.equals(toCurrency)) {
            return CurrencyExchangeRate.sameCurrencyExchangeRate(fromCurrency);
        }
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("fromCurrency", fromCurrency);
        params.put("toCurrency", toCurrency);
        String query = "from CurrencyExchangeRate cer where cer.fromCurrency=:fromCurrency and cer.toCurrency=:toCurrency ";
        return findUniqueUsingQuery(query, params);
    }

	public CurrencyConversionFactor findConversionFactor(Currency fromCurrency, Currency toCurrency, CalendarDate asOfDate) {
		if( fromCurrency.equals(toCurrency)) {
			CurrencyConversionFactor factor = new CurrencyConversionFactor();
			factor.setParent(CurrencyExchangeRate.sameCurrencyExchangeRate(fromCurrency));
			factor.setDuration(new CalendarDuration(CalendarDate.date(1,1,1),CalendarDate.date(9999,12,31)));
			return factor;
		}
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("fromCurrency", fromCurrency);
        params.put("toCurrency", toCurrency);
        params.put("asOfDate",asOfDate);
        String query = "select ccf from CurrencyConversionFactor ccf join ccf.parent cer where cer.fromCurrency=:fromCurrency and cer.toCurrency=:toCurrency and " +
        		" ccf.duration.fromDate <= :asOfDate and :asOfDate <= ccf.duration.tillDate ";
        Object retVal = findUniqueUsingQuery(query, params);
		return (CurrencyConversionFactor)retVal;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Currency> findAllCurrencies() {
        return (List<Currency>) getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	Criteria currencyCriteria = session.createCriteria(CurrencyExchangeRate.class);
            	currencyCriteria.setProjection(Projections.distinct(Projections.property("fromCurrency")));
                return currencyCriteria.list();
            }
        });
	}
	
	
	 public CurrencyExchangeRate findCurrencyExchangeRateForSync(Currency fromCurrency, Currency toCurrency) {
	        Map<String,Object> params = new HashMap<String,Object>();
	        params.put("fromCurrency", fromCurrency);
	        params.put("toCurrency", toCurrency);
	        String query = "from CurrencyExchangeRate cer where cer.fromCurrency=:fromCurrency and cer.toCurrency=:toCurrency ";
	        return findUniqueUsingQuery(query, params);
	    }

		public CurrencyConversionFactor findConversionFactorForSync(Currency fromCurrency, Currency toCurrency, CalendarDate asOfDate) {
			Map<String,Object> params = new HashMap<String,Object>();
	        params.put("fromCurrency", fromCurrency);
	        params.put("toCurrency", toCurrency);
	        params.put("asOfDate",asOfDate);
	        String query = "select ccf from CurrencyConversionFactor ccf join ccf.parent cer where cer.fromCurrency=:fromCurrency and cer.toCurrency=:toCurrency and " +
	        		" ccf.duration.fromDate <= :asOfDate and :asOfDate <= ccf.duration.tillDate ";
	        Object retVal = findUniqueUsingQuery(query, params);
			return (CurrencyConversionFactor)retVal;
		}

    
}
