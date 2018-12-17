update jbpm_action
SET expression_ = 'claim.setInternalComment("Credit Submission"); claim.setState(tavant.twms.domain.claim.ClaimState.PENDING_PAYMENT_SUBMISSION);'
WHERE expression_ = 'claim.setInternalComment("Credit Submission"); claim.setState(tavant.twms.domain.claim.ClaimState.PENDING_PAYMENT_RESPONSE);'
/
COMMIT
/