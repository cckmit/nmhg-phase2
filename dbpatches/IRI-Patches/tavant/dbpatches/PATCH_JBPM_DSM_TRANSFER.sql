--PURPOSE    : PATCH FOR CREATING NEW AUDIT WHEN DSM TRANSFERS THE CLAIM
--AUTHOR     : PRADYOT ROUT
--CREATED ON : 9-MAR-09

insert into jbpm_event 
  (ID_, EVENTTYPE_, TYPE_, GRAPHELEMENT_, TRANSITION_, TASK_, NODE_, PROCESSDEFINITION_)
  values (hibernate_sequence.nextval,'transition', 'T', (select id_ from jbpm_transition where 
  to_=(select id_ from jbpm_node where name_='ServiceManagerReview')
  and name_='Transfer'
  and processdefinition_=(select id_ from jbpm_processdefinition where name_='ClaimSubmission')),
  (select id_ from jbpm_transition where 
  to_=(select id_ from jbpm_node where name_='ServiceManagerReview')
  and name_='Transfer'
and processdefinition_=(select id_ from jbpm_processdefinition where name_='ClaimSubmission')),NULL, NULL, NULL)
/
insert into jbpm_action
(ID_, CLASS, NAME_, ISPROPAGATIONALLOWED_, ACTIONEXPRESSION_, 
ISASYNC_, REFERENCEDACTION_, ACTIONDELEGATION_, EVENT_, 
PROCESSDEFINITION_, TIMERNAME_, EXPRESSION_, DUEDATE_, REPEAT_, 
TRANSITIONNAME_, TIMERACTION_, EVENTINDEX_, EXCEPTIONHANDLER_, EXCEPTIONHANDLERINDEX_)
values
(hibernate_sequence.nextval,'S',null,1,null,
0,null,null,
(select id_ from jbpm_event where transition_=(select id_ from jbpm_transition where 
to_=(select id_ from jbpm_node where name_='ServiceManagerReview')
and name_='Transfer'
and processdefinition_=(select id_ from jbpm_processdefinition where name_='ClaimSubmission'))),
null,null,
'claim.setState(tavant.twms.domain.claim.ClaimState.SERVICE_MANAGER_REVIEW);',null,null,null,null,0,null,null
)
/
COMMIT
/