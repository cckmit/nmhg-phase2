--Purpose    :INSERT TRANSITION WNTY CLAIM REOPENED WHEN A WNTY CLAIM IS REOPENED WITH A CORRESPONDING RECOVERY CLAIM IN NEW STATE
--Author     : jitesh jain
--Created On : 28-May-09

insert into jbpm_transition values (
HIBERNATE_SEQUENCE.NEXTVAL,'Wnty Claim Reopened',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_='Supplier Recovery Admin' AND PROCESSDEFINITION_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_='End' AND PROCESSDEFINITION_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')),6)
/
insert into jbpm_transition values (
HIBERNATE_SEQUENCE.NEXTVAL,'Wnty Claim Reopened',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_='Supplier Contract' AND PROCESSDEFINITION_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_='End' AND PROCESSDEFINITION_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')),3)
/
insert into jbpm_event values (
HIBERNATE_SEQUENCE.NEXTVAL,'transition','T',
(select jt.id_  from jbpm_transition jt, jbpm_node jn 
where jt.processdefinition_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')
and jt.FROM_ = jn.id_
and jn.name_ = 'Supplier Contract'
and jt.name_='Wnty Claim Reopened'),
(select jt.id_  from jbpm_transition jt, jbpm_node jn 
where jt.processdefinition_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery') 
and jt.FROM_ = jn.id_
and jn.name_ = 'Supplier Contract'
and jt.name_='Wnty Claim Reopened'),
null,
null,
null)
/
insert into jbpm_event values (
HIBERNATE_SEQUENCE.NEXTVAL,'transition','T',
(select jt.id_  from jbpm_transition jt, jbpm_node jn 
where jt.processdefinition_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')
and jt.FROM_ = jn.id_
and jn.name_ = 'Supplier Recovery Admin'
and jt.name_='Wnty Claim Reopened'),
(select jt.id_  from jbpm_transition jt, jbpm_node jn 
where jt.processdefinition_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery') 
and jt.FROM_ = jn.id_
and jn.name_ = 'Supplier Recovery Admin'
and jt.name_='Wnty Claim Reopened'),
null,
null,
null)
/
Insert into jbpm_action 
(ID_,CLASS,NAME_,ISPROPAGATIONALLOWED_,ACTIONEXPRESSION_,ISASYNC_,REFERENCEDACTION_,ACTIONDELEGATION_,EVENT_,
PROCESSDEFINITION_,TIMERNAME_,EXPRESSION_,DUEDATE_,REPEAT_,TRANSITIONNAME_,TIMERACTION_,EVENTINDEX_,
EXCEPTIONHANDLER_,EXCEPTIONHANDLERINDEX_) 
values (HIBERNATE_SEQUENCE.NEXTVAL,'S',null,1,null,0,null,null,
(select je.id_ from jbpm_transition jt, jbpm_node jn, jbpm_event je 
where jt.processdefinition_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')
and jt.FROM_ = jn.id_
and jn.name_ = 'Supplier Contract'
and jt.name_='Wnty Claim Reopened'
and jt.id_ = je.transition_),
null,null,'recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.WNTY_CLAIM_REOPENED)',null,null,null,null,
0,null,null)
/
Insert into jbpm_action 
(ID_,CLASS,NAME_,ISPROPAGATIONALLOWED_,ACTIONEXPRESSION_,ISASYNC_,REFERENCEDACTION_,ACTIONDELEGATION_,EVENT_,
PROCESSDEFINITION_,TIMERNAME_,EXPRESSION_,DUEDATE_,REPEAT_,TRANSITIONNAME_,TIMERACTION_,EVENTINDEX_,
EXCEPTIONHANDLER_,EXCEPTIONHANDLERINDEX_) 
values (HIBERNATE_SEQUENCE.NEXTVAL,'S',null,1,null,0,null,null,
(select je.id_ from jbpm_transition jt, jbpm_node jn, jbpm_event je 
where jt.processdefinition_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')
and jt.FROM_ = jn.id_
and jn.name_ = 'Supplier Recovery Admin'
and jt.name_='Wnty Claim Reopened'
and jt.id_ = je.transition_),
null,null,'recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.WNTY_CLAIM_REOPENED)',null,null,null,null,0,null,null)
/
commit
/
update jbpm_node 
set nodecollectionindex_ = (select max(nodecollectionindex_) + 1 from jbpm_node where processdefinition_ in 
(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery') )
where (processdefinition_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery') and name_ = 'End')
/
Insert into jbpm_node (ID_,CLASS_,NAME_,PROCESSDEFINITION_,ISASYNC_,ACTION_,SUPERSTATE_,SIGNAL_,CREATETASKS_,ENDTASKS_,
TASK_NAMES_TO_END,DECISIONEXPRESSION_,DECISIONDELEGATION,SUBPROCESSDEFINITION_,END_TRANSITION,NORMAL_TRANSITION,NODECOLLECTIONINDEX_) 
values (HIBERNATE_SEQUENCE.NEXTVAL,'B','SupplierEndFork',
(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')
,0,null,null,null,null,null,'Awaiting Supplier Response,SupplierResponseScheduler',null,null,null,null,null,
(select max(nodecollectionindex_)-1 from jbpm_node where processdefinition_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')))
/
update jbpm_transition set to_ = (select id_ from jbpm_node where name_ = 'SupplierEndFork') where id_ in (
select id_ from jbpm_transition where
name_ = 'Wnty Claim Reopened'
and from_ in (SELECT ID_ FROM JBPM_NODE WHERE NAME_='Supplier Contract' AND PROCESSDEFINITION_ in 
(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')))
/
insert into jbpm_transition values (
HIBERNATE_SEQUENCE.NEXTVAL,'toEnd',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_='SupplierEndFork' AND PROCESSDEFINITION_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_='End' AND PROCESSDEFINITION_ in (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery')),0)
/
commit
/

