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

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
public interface TravelRatesAdminService extends GenericService<TravelRates, Long, Exception> {
	public TravelRate findTravelRate(Claim ofClaim, Policy policy,String customerType);

	public TravelRate findTravelRate(Criteria withCriteria,
			CalendarDate asOfDate, Currency inCurrency,String customerType);

	
    boolean isUnique(TravelRates definition);
    
	// TODO Method signature needs to be modified or approved by Radha.		
    public void saveOrUpdateTravelRates(TravelRates travelRates);
 
    @Transactional(readOnly=false)
    public void save(TravelRates entity);
    
    @Transactional(readOnly=false)
    public void update(TravelRates entity);
    
    @Transactional(readOnly=false)
    public void delete(TravelRates travelRates);
}