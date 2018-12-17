-- Purpose    : Updating claim with internal comment as "Processing Engine" while setting state after rule execution
-- Author     : Jitesh Jain
-- Created On : 12-Jul-09

update jbpm_action set expression_ = 'claim.setInternalComment("Processing Engine"); ' || expression_ where event_ in (
select id_ from jbpm_event where transition_ in (
select id_ from jbpm_transition where from_ in (
select id_ from jbpm_node where name_ ='IsProcessorReviewNeeded'))) and expression_ not like '%Auto Denied%'
/
update jbpm_action set 
expression_ = 'claim.setInternalComment("Auto Denied"); claim.setState(tavant.twms.domain.claim.ClaimState.DENIED); isClaimDenied = true; claim.setProcessedAutomatically();'
where event_ in (
select id_ from jbpm_event where transition_ in (
select id_ from jbpm_transition where from_ in (
select id_ from jbpm_node where name_ ='IsProcessorReviewNeeded'))) and expression_ like '%claim.setInternalComment("Auto Denied")%'
/
update jbpm_action set expression_ = 'claim.setInternalComment("Credit Submission"); ' || expression_ where event_ in (
select id_ from jbpm_event where transition_ in (
select id_ from jbpm_transition where from_ in (
select id_ from jbpm_node where name_ ='NotifyPayment')))
/
COMMIT
/