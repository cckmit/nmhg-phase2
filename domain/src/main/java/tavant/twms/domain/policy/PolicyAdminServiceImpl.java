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

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;


/**
 * @author radhakrishnan.j
 *
 */
public class PolicyAdminServiceImpl implements PolicyAdminService {

    private PolicyRepository policyRepository;
    private OwnershipStateRepository ownershipStateRepository;
    public void createPolicy(Policy aNewPolicy) throws PolicyException {
        policyRepository.create(aNewPolicy);
    }

    public void updatePolicy(Policy aPolicy) throws PolicyException {
        policyRepository.update(aPolicy);
    }

    public Policy findPolicy(Long id) {
        return policyRepository.findBy(id);
    }

    public PageResult<RegisteredPolicy> findAllPolicies(ListCriteria policyListCriteria) {
        return policyRepository.findAllPolicies(policyListCriteria);
    }

    public List<OwnershipState> findAllOwnershipStates() {
    	return ownershipStateRepository.findAllOwnershipStates();
    }

    @Required
    public void setPolicyRepository(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Required
	public void setOwnershipStateRepository(
			OwnershipStateRepository ownershipStateRepository) {
		this.ownershipStateRepository = ownershipStateRepository;
	}
    
    

}
