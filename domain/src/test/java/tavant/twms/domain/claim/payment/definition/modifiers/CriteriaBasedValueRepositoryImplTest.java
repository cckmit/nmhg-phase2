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

import tavant.twms.domain.common.CriteriaEvaluationPrecedence;
import tavant.twms.infra.DomainRepositoryTestCase;

public class CriteriaBasedValueRepositoryImplTest extends DomainRepositoryTestCase {
    CriteriaBasedValueRepository criteriaBasedValueRepository;

    public void setCriteriaBasedValueRepository(CriteriaBasedValueRepository criteriaBasedValueRepository) {
        this.criteriaBasedValueRepository = criteriaBasedValueRepository;
    }

    public void testCreateEvaluationPrecedence() {
        CriteriaEvaluationPrecedence aNewEvaluationPrecedence = new CriteriaEvaluationPrecedence();
        aNewEvaluationPrecedence.setForData("Payment Modifiers");

        criteriaBasedValueRepository.save(aNewEvaluationPrecedence);
        assertNotNull(aNewEvaluationPrecedence.getId());

        getSession().flush();
        getSession().clear();
    }

    public void testFind() {
        testCreateEvaluationPrecedence();
        CriteriaEvaluationPrecedence evalPrecedence = criteriaBasedValueRepository
                .findEvaluationPrecedence("Payment Modifiers");
        assertNotNull(evalPrecedence);
    }
}