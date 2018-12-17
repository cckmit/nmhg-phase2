--Purpose    : Created  a new table to store the details of the failed rules and to insert internal comments for auto denied claim
--Author     : pradyot.rout
--Created On : 12-Jun-08

create table failed_rule (rule_detail number(19,0) not null, rule_action varchar2(255 char), rule_msg varchar2(255 char), rule_number varchar2(255 char))
/
commit
/

update jbpm_action
set expression_ = 'claim.setState(tavant.twms.domain.claim.ClaimState.DENIED); isClaimDenied = true; claim.setProcessedAutomatically();claim.setInternalComment("Auto Denied")'
where expression_ like 'claim.setState(tavant.twms.domain.claim.ClaimState.DENIED); isClaimDenied = true; claim.setProcessedAutomatically();'
/
commit
/
