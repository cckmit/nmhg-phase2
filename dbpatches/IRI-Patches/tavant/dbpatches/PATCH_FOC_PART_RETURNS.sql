--- jbpm Patch for foc to trigger part returns immediately
-- Author Prashanth konda
-- Date 24th feb 2009


INSERT INTO jbpm_decisionconditions(decision_,transitionname_,expression_,index_) VALUES  ((SELECT ID_  from jbpm_node where name_='ContinueAfterPartReturns' and processdefinition_ = (SELECT ID_ FROM jbpm_processdefinition  WHERE  NAME_ = 'ClaimSubmission')),'PutInWaitingForLabor','#{claim.state.state=="draft"}',
(select max(index_)+1 from jbpm_decisionconditions where decision_ =(SELECT ID_  from jbpm_node where name_='ContinueAfterPartReturns' and processdefinition_ = (SELECT ID_ FROM jbpm_processdefinition  WHERE  NAME_ = 'ClaimSubmission')) ));
/        

INSERT INTO jbpm_transition (id_,name_,processdefinition_,from_,to_,fromindex_)  
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'PutInWaitingForLabor',
(SELECT ID_ FROM jbpm_processdefinition  WHERE  NAME_ = 'ClaimSubmission'),
(SELECT ID_  from jbpm_node where name_='ContinueAfterPartReturns' and processdefinition_ = (SELECT ID_ FROM jbpm_processdefinition  WHERE  NAME_ = 'ClaimSubmission')),
(SELECT ID_  from jbpm_node where name_='WaitingForLabor' and processdefinition_ = (SELECT ID_ FROM jbpm_processdefinition  WHERE  NAME_ = 'ClaimSubmission')),
(select max(fromindex_)+1 from jbpm_transition where from_ =(SELECT ID_  from jbpm_node where name_='ContinueAfterPartReturns' and processdefinition_ = (SELECT ID_ FROM jbpm_processdefinition  WHERE  NAME_ = 'ClaimSubmission')) ));
/

update jbpm_transition set to_ = (select id_ from jbpm_node where name_ ='TriggerPartReturns' and processdefinition_ = (SELECT id_ FROM jbpm_processdefinition WHERE name_ ='ClaimSubmission') )
where processdefinition_ = (SELECT id_ FROM jbpm_processdefinition WHERE name_ ='ClaimSubmission') and from_ = (select id_ from jbpm_node where name_ ='IsFocClaim'
and processdefinition_ = (SELECT id_ FROM jbpm_processdefinition WHERE name_ ='ClaimSubmission') ) and name_ = 'Yes'
and to_ = (select id_ from jbpm_node where name_ ='WaitingForLabor' and processdefinition_ = (SELECT id_ FROM jbpm_processdefinition WHERE name_ ='ClaimSubmission') )
/


commit
/