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
package tavant.twms.domain.partreturn;

import java.util.List;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.CriteriaEvaluationPrecedence;
import tavant.twms.infra.GenericRepository;

/**
 * @author vineeth.varghese
 * 
 */
public interface PartReturnDefinitionRepository extends
        GenericRepository<PartReturnDefinition, Long> {

    public PaymentCondition findPaymentConditionForCode(String code);

    public List<PaymentCondition> findAllPaymentConditions();

    public PartReturnDefinition findPartReturnDefinition(Item forItem, Criteria withCriteria);

    boolean isUnique(PartReturnDefinition partReturnDefinition);

    CriteriaEvaluationPrecedence findEvaluationPrecedence(String forData);

    public List findPartReturnDefinitions(final Item forItem, final Criteria criteria);
}