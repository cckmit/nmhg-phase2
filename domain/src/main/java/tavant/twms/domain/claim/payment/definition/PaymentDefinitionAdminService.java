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

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.policy.Policy;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 * 
 */
@Transactional(readOnly = true)
public interface PaymentDefinitionAdminService {
    @Transactional(readOnly = false)
    public void save(PaymentDefinition newPaymentDefintion);

    @Transactional(readOnly = false)
    public void update(PaymentDefinition paymentDefintion);
    
    @Transactional(readOnly = false)
    public void updateAll(List<PaymentDefinition> paymentDefintions);

    @Transactional(readOnly = false)
    public void delete(PaymentDefinition paymentDefintion);

    public PaymentDefinition findById(Long id);

    public PaymentDefinition findByDate(CalendarDate forDate);

    public PageResult<PaymentDefinition> findAll(PageSpecification pageSpecification);

    public List<Section> findAllSections();
    
    public List<PaymentDefinition> findAll();
    
    public PaymentDefinition findBestPaymentDefinition(Claim claim,Policy policy);
    
    @Transactional(readOnly = false)
	public void saveOrUpdate(PaymentDefinition paymentDefinition);
}
