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
package tavant.twms.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;

import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.worklist.WorkListItemService;

/**
 * @author pradipta.a
 */
public class AutoDebitServiceImpl implements AutoDebitService {

	RecoveryClaimService recoveryClaimService;

	WorkListItemService workListItemService;

	Logger logger = Logger.getLogger(AutoDebitServiceImpl.class);

	public void autoDebit() {
		List<RecoveryClaim> recClaims = recoveryClaimService
				.findClaimInState(RecoveryClaimState.REJECTED);
		List<RecoveryClaim> inRecoveryClaims = recoveryClaimService
				.findClaimInState(RecoveryClaimState.IN_RECOVERY);
		if (inRecoveryClaims != null && !inRecoveryClaims.isEmpty()) {
			recClaims.addAll(inRecoveryClaims);
		}
		List<String> states = new ArrayList<String>();
		states.add(WorkflowConstants.SUPPLIER_DISPUTED_CLAIMS_TASK_NAME);
		states.add(WorkflowConstants.SRA_REVIEW);
		for (RecoveryClaim recoveryClaim : recClaims) {
			TaskInstance taskInstance = workListItemService
					.findTaskForRecClaimsWithTaskNames(recoveryClaim.getId(),
							states);
			if(taskInstance != null){
				workListItemService.endTaskWithTransition(taskInstance,
						WorkflowConstants.AUTO_DEBIT);
			}

		}
	}

	public void setRecoveryClaimService(
			RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}
}
