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
package tavant.twms.domain.claim.payment.rates;

import java.util.Currency;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.policy.Policy;
import tavant.twms.infra.GenericService;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
public interface LaborRatesAdminService extends GenericService<LaborRates, Long, Exception> {
	public Money findLaborRate(Claim ofClaim, Policy policy,String customerType);

	public Money findLaborRate(Criteria withCriteria,
			CalendarDate asOfDate, Currency inCurrency,String customerType);

    boolean isUnique(LaborRates configuration);
         	
    @Transactional(readOnly=false)
    public void updateLaborRates(LaborRates laborRates,String comments) throws Exception;
    
    @Transactional(readOnly=false)
    public void saveLaborRates(LaborRates laborRates,String comments) throws Exception;
   
    @Transactional(readOnly=false)
    public void delete(LaborRates laborRates);
        
}
