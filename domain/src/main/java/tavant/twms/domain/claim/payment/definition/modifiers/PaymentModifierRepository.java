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

import java.util.List;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

import com.domainlanguage.time.CalendarDate;

public interface PaymentModifierRepository extends GenericRepository<PaymentModifier, Long> {

    void savePaymentVariable(PaymentVariable newPaymentVariable);
    
    public void updatePaymentVariable(PaymentVariable newPaymentVariable);
    
    List<PaymentVariable> findAllPaymentVariables();
    
    List<PaymentVariable> findPaymentVariablesBySection(final String sectionName);
    
    PaymentVariable findPaymentVariableByPK(Long paymentVariableId);
    
    PaymentVariable findPaymentVariableByName(String newVariableName);

    PaymentModifier findExactForCriteria(Criteria criteria, PaymentVariable paymentVariable,String customerType);
    
    CriteriaBasedValue findValue(Criteria criteria, PaymentVariable paymentVariable,CalendarDate asOfDate,String customerType);
    
    public void deactivatePaymentModifierForVariable(final Long paymentVariableId);
    
    public void deactivatePaymentVariableLevelForVariable(final Long paymentVariableId);    
    
    public void deactivateCriteriaEvaluationPrecedence(final PaymentVariable paymentVariable);
    
    public PageResult<PaymentModifier> findPage(final ListCriteria listCriteria, final Long paymentVariableId);
    
    public CriteriaBasedValue findModifierForClaim(final Claim claim, final Criteria criteria, final PaymentVariable paymentVariable,final CalendarDate asOfDate,final String customerType);

}