--Purpose    : To update Action and condition records from JBPM_ACTION and TRANSITION_CONDITION table to remove number of SMR resubmit allowed check
--Author     : Jitesh Jain
--Created On : 3-Apr-09

update jbpm_action set 
expression_ = 'claim.setState(tavant.twms.domain.claim.ClaimState.SERVICE_MANAGER_REVIEW);'
where id_ in (
select ja.id_
from jbpm_action ja, jbpm_event je, jbpm_transition jt
where ja.event_ = je.id_
and je.transition_ = jt.id_
and jt.name_ = 'Re-requests for SMR')
/
delete transition_conditions where 
task_node in (select id_ from jbpm_node where name_='ServiceManagerResponse')
and transition_name = 'Re-requests for SMR'
/
update transition_conditions set list_index = 0 where 
task_node in (select id_ from jbpm_node where name_='ServiceManagerResponse')
and transition_name = 'Submit'
/
COMMIT
/