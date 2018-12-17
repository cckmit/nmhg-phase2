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
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.claim.Claim;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.domain.claim.payment.rates.PriceFetchData;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;

/**
 * @author radhakrishnan.j
 * 
 */
public class ItemBasePriceRepositoryImpl extends
		GenericRepositoryImpl<ItemBasePrice, Long> implements
		ItemBasePriceRepository {
	public ItemBasePrice findByItem(Item item) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("item", item);
		return findUniqueUsingQuery("from ItemBasePrice where forItem=:item",
				params);
	}

	public void findByItem(Claim claim,
			List<PriceFetchData> priceFetchDataList) {
		throw new UnsupportedOperationException();

	}

	public void findCostPriceByItem(RecoveryClaimInfo recoveryClaimInfo, List<PriceFetchData> costPriceFetchList) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	

}
