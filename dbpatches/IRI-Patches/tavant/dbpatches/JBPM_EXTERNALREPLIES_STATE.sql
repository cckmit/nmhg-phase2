--Purpose    : Introduced a new claim state to handle dealer reply flow
--Author     : Ramalakshmi P
--Created On : 26-Mar-09

update jbpm_action set expression_ = 'claim.setState(tavant.twms.domain.claim.ClaimState.EXTERNAL_REPLIES);' 
where event_ in (select id_ from jbpm_event where transition_ in (select id_ from jbpm_transition where name_ = 'goToReplies'
and from_ in (select id_ from jbpm_node where name_ = 'PolicyAndPaymentProcessorUpdate')))
/
COMMIT
/


