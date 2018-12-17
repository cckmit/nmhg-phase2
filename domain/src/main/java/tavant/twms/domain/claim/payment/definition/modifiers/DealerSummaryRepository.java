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
package tavant.twms.domain.claim.payment.definition.modifiers;

import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.claim.payment.rates.TravelRate;
import tavant.twms.domain.claim.payment.rates.LaborRate;

import java.util.List;

public interface DealerSummaryRepository {

    public List<CriteriaBasedValue> findCriteriaBasedValues(ServiceProvider serviceProvider,
                                                     List<String> businessUnitNameList);

    public List<DealerGroup> findAllParentsOfServiceProvider(ServiceProvider serviceProvider, String purpose,
                                                      List<String> businessUnitNameList);

    public List<TravelRate> findTravelRates(final ServiceProvider serviceProvider,
                                     final List<String> businessUnitNameList);

    public List<LaborRate> findLaborRates(final ServiceProvider serviceProvider, final List<String> businessUnitNameList);
}
