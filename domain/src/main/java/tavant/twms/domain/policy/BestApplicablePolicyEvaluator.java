/*
 *   Copyright (c) 2007 Tavant Technologies
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

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;

/**
 * @author vineeth.varghese
 */
public interface BestApplicablePolicyEvaluator {

    ApplicablePolicy findBestApplicablePolicy(ClaimedItem claimedItem,
            List<? extends Policy> policies) throws PolicyException;
    
    ApplicablePolicy findBestApplicablePolicy(Claim claim,
            List<? extends Policy> policies) throws PolicyException;

	List<String> findApplicablePolicesCodes(Claim claim,
			List<? extends Policy> policies);

	List<String> findApplicablePolicyCodes(ClaimedItem claimedItem,
			List<? extends Policy> policies);

}
