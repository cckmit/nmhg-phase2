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
package tavant.twms.domain.policy;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author radhakrishnan.j
 *
 */
@Transactional(readOnly=true)
public interface PolicyAdminService {
    /**
     * 
     * @param aNewPolicy
     * @throws PolicyException
     */
    @Transactional(readOnly=false)
    void createPolicy(Policy aNewPolicy) throws PolicyException;

    /**
     * 
     * @param aPolicy
     * @throws PolicyException
     */
    @Transactional(readOnly=false)
    void updatePolicy(Policy aPolicy) throws PolicyException;

    /**
     * 
     * @param id
     * @return
     */
    Policy findPolicy(Long id);

    /**
     * 
     * @param policyListCriteria
     * @return
     */
    PageResult<RegisteredPolicy> findAllPolicies(ListCriteria policyListCriteria);
    
    List<OwnershipState> findAllOwnershipStates();
}
