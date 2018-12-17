--Purpose    : To update Action and decision condition records from JBPM_ACTION and JBPM_EVENT table to popupate correct states for denied claims
--Author     : Jitesh Jain
--Created On : 27-May-09

update jbpm_decisionconditions set expression_ = '#{claim.claimDenied == true}' 
where decision_=1100000009880 and transitionname_='goToMailPaymentInfo'
/
update jbpm_decisionconditions set expression_ = '#{claim.claimDenied == false}' 
where decision_=1100000009880 and transitionname_='goToMailPayment'
/
update jbpm_action set expression_ = 'isClaimDenied = true; claim.setState(tavant.twms.domain.claim.ClaimState.DENIED);' 
where id_ in (
select ja.id_ from jbpm_transition jt,jbpm_event je,jbpm_action ja 
where jt.name_ in ('DenyOnNoReply','Deny')
and jt.id_ = je.transition_
and je.id_ = ja.event_
and ja.expression_ like '%isClaimDenied = false%')
/
COMMIT
/