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
import java.util.List;
import java.util.Map;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemBasePrice;
import tavant.twms.domain.catalog.ItemBasePriceRepository;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.infra.GenericService;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import tavant.twms.domain.supplier.recovery.RecoverablePart;

/**
 * @author radhakrishnan.j
 * 
 */
public interface ItemPriceAdminService
		extends
		GenericService<AdministeredItemPrice, Long, PriceAdministrationException> {
	
	public List<PriceFetchData> findPrice(Claim claim,Policy policy);
	
	public Money findPrice(Item replacedPart, Claim ofClaim, Policy policy);

	public Money findPrice(Item forItem, Criteria withCriteria,
			CalendarDate asOfDate, Currency inCurrency);
        
    boolean isUnique(AdministeredItemPrice itemPrice);
    
    public void populateCostPriceForRecoverableParts(RecoveryClaimInfo recoveryClaimInfo);
    
	// TODO Method signature needs to be modified or approved by Radha.
    public void saveOrUpdateItemPrice(ItemBasePrice price, Criteria forCriteria);
    
	public void setItemBasePriceRepository(
			ItemBasePriceRepository itemBasePriceRepository);
    
    public void populateCostPriceForRecoverablePart(RecoveryClaimInfo recoveryClaimInfo, RecoverablePart recoverablePart);
}
