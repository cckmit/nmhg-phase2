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
package tavant.twms.domain.catalog;

import java.util.Currency;
import java.util.List;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.payment.rates.PriceFetchData;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.infra.GenericRepository;

/**
 * @author radhakrishnan.j
 *
 */
public interface ItemBasePriceRepository extends GenericRepository<ItemBasePrice,Long> {
	
    public ItemBasePrice findByItem(Item item);
    
    public void findByItem(Claim claim,List<PriceFetchData> priceFetchDataList);
    
    public void findCostPriceByItem(RecoveryClaimInfo recoveryClaimInfo, List<PriceFetchData>costPriceFetchList);
    
}
