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

import org.springframework.transaction.annotation.Transactional;
import tavant.twms.domain.claim.payment.rates.LaborRate;
import tavant.twms.domain.claim.payment.rates.TravelRate;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.DealerGroup;

import java.util.List;

@Transactional(readOnly = true)
public interface DealerSummaryService {

    List<LaborRate> findLaborRate(ServiceProvider serviceProvider, List<String> businessUnitNameList);

    List<TravelRate> findTravelRates(ServiceProvider serviceProvider, List<String> businessUnitNameList);

    List<CriteriaBasedValue> findCriteriaBasedValues(ServiceProvider serviceProvider,
                                                     List<String> businessUnitNameList);

    List<DealerGroup> findAllParentsOfServiceProvider(ServiceProvider serviceProvider, String purpose,
                                                      List<String> businessUnitNameList);


}
