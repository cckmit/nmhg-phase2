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
package tavant.twms.domain.policy;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

/**
 * @author kiran.sg
 * 
 */
public class PolicyRatesAdminServiceImpl extends
		GenericServiceImpl<PolicyRates, Long, Exception> implements
		PolicyRatesAdminService {

	private PolicyRatesRepository policyRatesRepository;

	public boolean isUnique(PolicyRates price) {
		boolean isUnique = false;
		PolicyRatesCriteria forCriteria = price.getForCriteria();
		PolicyRates example = null;
		example = policyRatesRepository.findByCriteria(forCriteria);
		if (example == null || same(price, example)) {
			isUnique = true;
		}
		return isUnique;
	}

	private boolean same(PolicyRates source, PolicyRates target) {
		return source.getId() != null && target.getId() != null
				&& source.getId().compareTo(target.getId()) == 0;
	}

	@Override
	public GenericRepository<PolicyRates, Long> getRepository() {
		return policyRatesRepository;
	}

	public PolicyRatesRepository getPolicyRatesRepository() {
		return policyRatesRepository;
	}

	public void setPolicyRatesRepository(PolicyRatesRepository policyRatesRepository) {
		this.policyRatesRepository = policyRatesRepository;
	}
	
}