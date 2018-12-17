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
package tavant.twms.domain.claim.payment.definition.modifiers;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.common.CriteriaEvaluationPrecedence;
import tavant.twms.infra.GenericService;

/**
 * @author radhakrishnan.j
 *
 */
public interface PaymentModifierAdminService extends GenericService<PaymentModifier, Long, Exception> {

    @Transactional(readOnly=false) 
    void createPaymentVariable(PaymentVariable newPaymentVariable);

    @Transactional(readOnly=false) 
    void createEvaluationPrecedence(CriteriaEvaluationPrecedence newEvalPrecedence);

    PaymentVariable findPaymentVariableById(Long paymentVariableId);

    List<PaymentVariable> findAllPaymentVariables();

    boolean isUnique(PaymentModifier definition);

    PaymentVariable findPaymentVariableByName(String newVariableName);
    
    @Transactional(readOnly=false) 
    public void updatePaymentVariable(PaymentVariable newPaymentVariable);
    
    @Transactional(readOnly=false) 
    public void deactivatePaymentModifierForVariable(
			final Long paymentVariableId);
    @Transactional(readOnly=false) 
    public void deactivatePaymentVariableLevelForVariable(final Long paymentVariableId);
    
    @Transactional(readOnly=false) 
    public void deactivateCriteriaEvaluationPrecedence(final PaymentVariable paymentVariable);
    
    public List<PaymentVariable> sortModifiersBasedOnName(List<PaymentVariable> paymentVariables);

}