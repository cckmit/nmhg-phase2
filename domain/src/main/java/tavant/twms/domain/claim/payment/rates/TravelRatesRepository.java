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

import java.util.List;

import tavant.twms.domain.claim.Criteria;
import tavant.twms.infra.GenericRepository;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
public interface TravelRatesRepository extends GenericRepository<TravelRates, Long> {

    List<TravelRate> findTravelRateConfiguration(Criteria criteria,CalendarDate asOfDate,String customerType);

    TravelRates findByCriteria(Criteria criteria,final TravelRates price);
}
