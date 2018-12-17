--Purpose    : PATCH FOR Adding a role to show FOC claims Inbox
--Author     : Vamshi Gunda
--Created On : 07-Jul-09

insert into role (id, name, d_active, version) values ((select max(id)+1 from role),'viewFOCClaims',1,1)
/
insert into jbpm_delegation values (hibernate_sequence.nextval,'tavant.twms.jbpm.assignment.ExpressionAssignmentHandler','foc', NULL,(select id_ from jbpm_processdefinition where name_ ='ClaimSubmission'))
/
insert into jbpm_swimlane values (hibernate_sequence.nextval,'viewFOCClaims', NULL, NULL,(select id_ from jbpm_delegation where configuration_ = 'foc'),(select id_ from jbpm_moduledefinition where class_ = 'T' and processdefinition_ in (select id_ from jbpm_processdefinition where name_ = 'ClaimSubmission')))
/
update jbpm_delegation set configuration_ = '<expression>actor=ognl{claim.filedBy.name}</expression>' where configuration_ = 'foc'
/
update jbpm_task set swimlane_ = (select id_ from jbpm_swimlane where name_ = 'viewFOCClaims') where name_ = 'WaitingForLabor'
/
commit
/
