--Surulee for NMHGSLMS-576
INSERT INTO DOMAIN_RULE_ACTION VALUES (7,'ClaimRules','Forward Claim to the Dealer','Forwarded','1','','Added for NMHGSLMS-576','','','','',1)
/
UPDATE JBPM_DECISIONCONDITIONS
SET EXPRESSION_ = '#{claimState=="Forwarded"}',
TRANSITIONNAME_ = 'ForwardDealer'
WHERE DECISION_ IN (SELECT 
DISTINCT JD.DECISION_
FROM JBPM_TRANSITION JT, jbpm_node JN, JBPM_DECISIONCONDITIONS JD
WHERE JT.name_ = 'ManualReviewNeeded'
AND JT.FROM_ = JN.ID_
and JT.PROCESSDEFINITION_ = 110000424072480
AND JD.DECISION_ = JN.ID_
AND JD.TRANSITIONNAME_ = JT.NAME_)
AND TRANSITIONNAME_ = 'ManualReviewNeeded'
/
UPDATE JBPM_TRANSITION
SET NAME_ = 'ForwardDealer',
TO_ = 110000424080740
where ID_ = 110000424074400
/
UPDATE JBPM_ACTION
SET EXPRESSION_ = 'claim.setInternalComment("Processing Engine");claim.setState(tavant.twms.domain.claim.ClaimState.FORWARDED);'
WHERE ID_ IN (110000424074440)
/
UPDATE JBPM_DELEGATION
SET CONFIGURATION_ = '
    <beanName>partReturnService</beanName>
    <methodName>updatePartReturnsForClaim</methodName>
    <parameters><variable>claim</variable><variable>null</variable></parameters>
    <transition name="goToClaimAutoAdjudication" to="ClaimAutoAdjudication"/>
  '
WHERE ID_ = 110000424074280
/
INSERT INTO JBPM_TRANSITION VALUES (110000426140200,'goToClaimRules',110000424072480,110000424073260,110000424074320,2)
/
UPDATE JBPM_TRANSITION
SET FROMINDEX_ = 3
WHERE ID_ = 110000424073360
/
UPDATE JBPM_TRANSITION
SET FROMINDEX_ = 4
WHERE ID_ = 110000424073420
/
UPDATE JBPM_DECISIONCONDITIONS
SET INDEX_ = 4
WHERE DECISION_ = 110000424073260
AND TRANSITIONNAME_ = 'Yes'
/
UPDATE JBPM_DECISIONCONDITIONS
SET INDEX_ = 3
WHERE DECISION_ = 110000424073260
AND TRANSITIONNAME_ = 'goToUpdatePartReturnInformation'
/
UPDATE JBPM_DECISIONCONDITIONS
SET EXPRESSION_ = '#{!claim.failureReportPending and claim.state.state=="Forwarded" and claim.latestAudit.updatedBy.name!="system"}'
WHERE DECISION_ = 110000424073260
AND TRANSITIONNAME_ = 'MoveToRepliesInbox'
/
INSERT INTO JBPM_DECISIONCONDITIONS VALUES (110000424073260,'goToClaimRules','#{!claim.failureReportPending and claim.state.state=="Forwarded" and claim.latestAudit.updatedBy.name=="system"}',2)
/
COMMIT
/