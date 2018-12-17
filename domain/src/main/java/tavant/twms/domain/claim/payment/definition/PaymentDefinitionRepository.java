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

package tavant.twms.domain.claim.payment.definition;

import java.util.List;

import tavant.twms.infra.GenericRepository;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.policy.Policy;

import com.domainlanguage.time.CalendarDate;

/**
 * @author sayedAamir
 */

public interface PaymentDefinitionRepository extends GenericRepository<PaymentDefinition, Long>{
    
    public PaymentDefinition findByDate(CalendarDate calendarDate);
    
    public List<Section> findAllSections();
    
    public PaymentDefinition findByPolicy(final Policy policy);
    
    public List<PaymentDefinition> findAllDefinitionsByDate(final CalendarDate calendarDate);
    
    public PaymentDefinition findPaymentDefinitionForCP();
    
    public void saveOrUpdate(PaymentDefinition paymentDefinition);

    public List<PaymentDefinition> findAllDefinitionsWithSections( List<Section> sections);
}
