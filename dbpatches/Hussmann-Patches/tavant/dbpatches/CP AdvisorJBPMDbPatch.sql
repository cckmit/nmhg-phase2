INSERT INTO JBPM_NODE (ID_, CLASS_, NAME_, PROCESSDEFINITION_, ISASYNC_, ACTION_, SUPERSTATE_, SIGNAL_, CREATETASKS_, 
ENDTASKS_, TASK_NAMES_TO_END, DECISIONEXPRESSION_, DECISIONDELEGATION, SUBPROCESSDEFINITION_, END_TRANSITION, 
NORMAL_TRANSITION, NODECOLLECTIONINDEX_) 
VALUES 
(HIBERNATE_SEQUENCE.NEXTVAL, 'A','CPReview',
(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),0,NULL,NULL,4,1,0,NULL,NULL,NULL,NULL,NULL,NULL,
(select max(nodecollectionindex_)+1 from jbpm_node))
/
INSERT INTO JBPM_NODE (ID_, CLASS_, NAME_, PROCESSDEFINITION_, ISASYNC_, ACTION_, SUPERSTATE_, SIGNAL_, CREATETASKS_, 
ENDTASKS_, TASK_NAMES_TO_END, DECISIONEXPRESSION_, DECISIONDELEGATION, SUBPROCESSDEFINITION_, END_TRANSITION, 
NORMAL_TRANSITION, NODECOLLECTIONINDEX_) 
VALUES 
(HIBERNATE_SEQUENCE.NEXTVAL, 'V','ForkForCPreview',
(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),0,NULL,NULL,4,1,0,NULL,NULL,NULL,NULL,NULL,NULL,
(select max(nodecollectionindex_)+1 from jbpm_node))
/
INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'CP Review',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ForkForCPreview' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'CPReview' and 
processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),'0')
/
INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'Forwarded Internally',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ForkForCPreview' and 
processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ForwardedInternally' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),'1')
/
INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'Review',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'CPReview' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'JoinAfterAdviceRequest' and 
processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),'0')
/
INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'Seek Review',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ProcessorReview' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ForkForCPreview' and processdefinition_ = 
(select id_ from jbpm_processdefinition where name_='ClaimSubmission')),'8')
/
INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'Seek Review',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'Replies' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ForkForCPreview' and processdefinition_ = 
(select id_ from jbpm_processdefinition where name_='ClaimSubmission')),'8')
/
INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'Transfer',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'CPReview' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'CPReview' and 
processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),'1')
/
INSERT INTO JBPM_DELEGATION (ID_, CLASSNAME_, CONFIGURATION_, CONFIGTYPE_, PROCESSDEFINITION_) 
VALUES 
(HIBERNATE_SEQUENCE.NEXTVAL,'tavant.twms.jbpm.assignment.ExpressionAssignmentHandler',
'<expression>actor=ognl{cpAdvisor}</expression>',NULL,(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'))
/
INSERT INTO  JBPM_SWIMLANE (ID_, NAME_, ACTORIDEXPRESSION_, POOLEDACTORSEXPRESSION_, ASSIGNMENTDELEGATION_, TASKMGMTDEFINITION_) 
VALUES 
(HIBERNATE_SEQUENCE.NEXTVAL,'cpAdvisor',NULL,NULL,
(SELECT ID_ FROM JBPM_DELEGATION WHERE PROCESSDEFINITION_=(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission') AND CLASSNAME_='tavant.twms.jbpm.assignment.ExpressionAssignmentHandler'
AND CONFIGURATION_ LIKE '%<expression>actor=ognl{cpAdvisor}</expression>%' AND 
ID_ NOT IN (SELECT DISTINCT ASSIGNMENTDELEGATION_ FROM JBPM_SWIMLANE)),
(SELECT ID_ FROM JBPM_MODULEDEFINITION WHERE PROCESSDEFINITION_=(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission') 
AND NAME_='org.jbpm.taskmgmt.def.TaskMgmtDefinition')
)
/
INSERT INTO JBPM_TASK (ID_, NAME_, PROCESSDEFINITION_, DESCRIPTION_, ISBLOCKING_, ISSIGNALLING_, DUEDATE_, ACTORIDEXPRESSION_, POOLEDACTORSEXPRESSION_, TASKMGMTDEFINITION_, TASKNODE_, STARTSTATE_, ASSIGNMENTDELEGATION_, SWIMLANE_, TASKCONTROLLER_) 
VALUES 
(HIBERNATE_SEQUENCE.NEXTVAL, 'CP Review',
(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),NULL,0,1,NULL,NULL,NULL,
(SELECT ID_ FROM JBPM_MODULEDEFINITION WHERE PROCESSDEFINITION_=(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission') AND NAME_='org.jbpm.taskmgmt.def.TaskMgmtDefinition'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_='CPReview'),NULL,NULL,
(SELECT ID_ FROM JBPM_SWIMLANE WHERE NAME_='cpAdvisor' AND TASKMGMTDEFINITION_ = (SELECT ID_ FROM JBPM_MODULEDEFINITION 
WHERE NAME_='org.jbpm.taskmgmt.def.TaskMgmtDefinition' AND 
PROCESSDEFINITION_=(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'))),NULL)
/
INSERT INTO JBPM_FORM_NODES (FORM_TASK_NODE_FORM_ID,FORM_VALUE,FORM_TYPE) 
VALUES 
((SELECT ID_ FROM JBPM_NODE WHERE NAME_='CPReview'),'review_request','inputForm')
/
INSERT INTO JBPM_EVENT (ID_,EVENTTYPE_,TYPE_,GRAPHELEMENT_,TRANSITION_,TASK_,NODE_,PROCESSDEFINITION_) 
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'transition','T',(SELECT ID_ FROM JBPM_TRANSITION WHERE NAME_ = 'Seek Review' and FROM_=(select id_ from jbpm_node where name_='ProcessorReview')),(SELECT ID_ FROM JBPM_TRANSITION WHERE NAME_ = 'Seek Review' and FROM_=(select id_ from jbpm_node where name_='ProcessorReview')),NULL,NULL,NULL)
/
INSERT INTO JBPM_ACTION (ID_, CLASS, NAME_, ISPROPAGATIONALLOWED_, ACTIONEXPRESSION_, ISASYNC_, REFERENCEDACTION_, ACTIONDELEGATION_, EVENT_, PROCESSDEFINITION_, TIMERNAME_, EXPRESSION_, DUEDATE_, REPEAT_, TRANSITIONNAME_, TIMERACTION_, EVENTINDEX_, EXCEPTIONHANDLER_, EXCEPTIONHANDLERINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'S',NULL,1,null,0,null,null,(SELECT ID_ FROM JBPM_EVENT WHERE TRANSITION_ = (SELECT ID_ FROM JBPM_TRANSITION WHERE NAME_ = 'Seek Review' and FROM_=(select id_ from jbpm_node where name_='ProcessorReview'))),null,null,'claim.setState(tavant.twms.domain.claim.ClaimState.CP_REVIEW)',null,null,null,null,0,null,null)
/
INSERT INTO JBPM_EVENT (ID_,EVENTTYPE_,TYPE_,GRAPHELEMENT_,TRANSITION_,TASK_,NODE_,PROCESSDEFINITION_) 
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'transition','T',(SELECT ID_ FROM JBPM_TRANSITION WHERE NAME_ = 'Seek Review' and FROM_=(select id_ from jbpm_node where name_='Replies')),(SELECT ID_ FROM JBPM_TRANSITION WHERE NAME_ = 'Seek Review' and FROM_=(select id_ from jbpm_node where name_='Replies')),NULL,NULL,NULL)
/
INSERT INTO JBPM_ACTION (ID_, CLASS, NAME_, ISPROPAGATIONALLOWED_, ACTIONEXPRESSION_, ISASYNC_, REFERENCEDACTION_, ACTIONDELEGATION_, EVENT_, PROCESSDEFINITION_, TIMERNAME_, EXPRESSION_, DUEDATE_, REPEAT_, TRANSITIONNAME_, TIMERACTION_, EVENTINDEX_, EXCEPTIONHANDLER_, EXCEPTIONHANDLERINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'S',NULL,1,null,0,null,null,(SELECT ID_ FROM JBPM_EVENT WHERE TRANSITION_ = (SELECT ID_ FROM JBPM_TRANSITION WHERE NAME_ = 'Seek Review' and FROM_=(select id_ from jbpm_node where name_='Replies'))),null,null,'claim.setState(tavant.twms.domain.claim.ClaimState.CP_REVIEW)',null,null,null,null,0,null,null)
/
insert into jbpm_event
(id_,eventtype_,type_,graphelement_,transition_)
values
(hibernate_sequence.nextval,'transition','T',
 (select id_ from jbpm_transition where from_ in (select id_ from jbpm_node where name_ = 'CPReview') and fromindex_ = 1),
 (select id_ from jbpm_transition where from_ in (select id_ from jbpm_node where name_ = 'CPReview') and fromindex_ = 1))
/ 
insert into jbpm_action
 (id_,class,ispropagationallowed_,isasync_,event_,expression_,eventindex_)
 values
 (hibernate_sequence.nextval,'S',1,0,
 (select id_ from jbpm_event where transition_ in ((select id_ from jbpm_transition where from_ in (select id_ from jbpm_node where name_ = 'CPReview') and fromindex_ = 1))),
 'claim.setState(tavant.twms.domain.claim.ClaimState.CP_TRANSFER);',0 )
 /
commit
/