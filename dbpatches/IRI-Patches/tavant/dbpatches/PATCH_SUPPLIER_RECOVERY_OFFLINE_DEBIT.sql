--Purpose : JBPM Patch for offline debit of supplier recovery claims.
--Author: Smita Kadle
--Created On: Date 02 Jan 2009

INSERT INTO jbpm_node
(ID_, CLASS_, NAME_, PROCESSDEFINITION_, ISASYNC_, ACTION_, SUPERSTATE_, SIGNAL_, CREATETASKS_, ENDTASKS_, TASK_NAMES_TO_END, DECISIONEXPRESSION_, DECISIONDELEGATION, SUBPROCESSDEFINITION_, END_TRANSITION, NORMAL_TRANSITION, NODECOLLECTIONINDEX_)
VALUES
(hibernate_sequence.NEXTVAL,'D','IsDebitOfflineEnabled',(SELECT id_ FROM jbpm_processdefinition WHERE name_ = 'SupplierRecovery'),0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,(SELECT MAX(nodecollectionindex_)+1 FROM jbpm_node))
/
INSERT INTO jbpm_decisionconditions
(DECISION_, TRANSITIONNAME_, EXPRESSION_, INDEX_)
VALUES
((SELECT id_ FROM jbpm_node WHERE name_ = 'IsDebitOfflineEnabled'), 'goToNotifyDebit','#{recoveryClaim.contract.offlineDebitEnabled == false}',0)
/
INSERT INTO jbpm_decisionconditions
(DECISION_, TRANSITIONNAME_, EXPRESSION_, INDEX_)
VALUES
((SELECT id_ FROM jbpm_node WHERE name_ = 'IsDebitOfflineEnabled'), 'goToReadyForDebit','#{recoveryClaim.contract.offlineDebitEnabled == true}',1)
/
INSERT INTO JBPM_NODE (ID_, CLASS_, NAME_, PROCESSDEFINITION_, ISASYNC_, ACTION_, SUPERSTATE_, SIGNAL_, CREATETASKS_, ENDTASKS_, TASK_NAMES_TO_END, DECISIONEXPRESSION_, DECISIONDELEGATION, SUBPROCESSDEFINITION_, END_TRANSITION, NORMAL_TRANSITION, NODECOLLECTIONINDEX_) 
VALUES 
(HIBERNATE_SEQUENCE.NEXTVAL, 'A','ReadyForDebit',
(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='SupplierRecovery'),0,NULL,NULL,4,1,0,NULL,NULL,NULL,NULL,NULL,NULL,(SELECT MAX(nodecollectionindex_)+1 FROM jbpm_node))
/
INSERT INTO JBPM_FORM_NODES (FORM_TASK_NODE_FORM_ID,FORM_VALUE,FORM_TYPE) 
VALUES 
((SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ReadyForDebit'),'readyForOfflineDebit','actionUrl')
/
INSERT INTO JBPM_TASK (ID_, NAME_, PROCESSDEFINITION_, DESCRIPTION_, ISBLOCKING_, ISSIGNALLING_, DUEDATE_, ACTORIDEXPRESSION_, POOLEDACTORSEXPRESSION_, TASKMGMTDEFINITION_, TASKNODE_, STARTSTATE_, ASSIGNMENTDELEGATION_, SWIMLANE_, TASKCONTROLLER_) 
VALUES 
(HIBERNATE_SEQUENCE.NEXTVAL, 'Ready For Debit',
(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='SupplierRecovery'),NULL,0,1,NULL,NULL,NULL,
(SELECT ID_ FROM JBPM_MODULEDEFINITION WHERE NAME_='org.jbpm.taskmgmt.def.TaskMgmtDefinition' AND PROCESSDEFINITION_=(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='SupplierRecovery')),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_='ReadyForDebit'),NULL,NULL,
(SELECT ID_ FROM JBPM_SWIMLANE WHERE NAME_='recoveryProcessor' AND TASKMGMTDEFINITION_ = (SELECT ID_ FROM JBPM_MODULEDEFINITION 
WHERE NAME_='org.jbpm.taskmgmt.def.TaskMgmtDefinition' AND 
PROCESSDEFINITION_=(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='SupplierRecovery'))),NULL)
/
INSERT INTO jbpm_transition
(ID_, NAME_, PROCESSDEFINITION_, FROM_, TO_, FROMINDEX_)
VALUES
(hibernate_sequence.NEXTVAL,'goToReadyForDebit',(SELECT id_ FROM jbpm_processdefinition WHERE name_ = 'SupplierRecovery'),(SELECT id_ FROM jbpm_node WHERE name_ = 'IsDebitOfflineEnabled'),(SELECT ID_ FROM JBPM_NODE WHERE NAME_='ReadyForDebit'),1)
/
INSERT INTO jbpm_transition
(ID_, NAME_, PROCESSDEFINITION_, FROM_, TO_, FROMINDEX_)
VALUES
(hibernate_sequence.NEXTVAL,'goToNotifyDebit',(SELECT id_ FROM jbpm_processdefinition WHERE name_ = 'SupplierRecovery'),(SELECT id_ FROM jbpm_node WHERE name_ = 'IsDebitOfflineEnabled'),(SELECT ID_ FROM JBPM_NODE WHERE NAME_='NotifyDebit'),0)
/
UPDATE jbpm_transition SET to_ = (SELECT id_ FROM jbpm_node WHERE name_ = 'IsDebitOfflineEnabled') WHERE name_ = 'AmountGreaterThanZero'
/
COMMIT
/
ALTER TABLE CONTRACT ADD (OFFLINE_DEBIT_ENABLED NUMBER(1))
/
UPDATE CONTRACT SET OFFLINE_DEBIT_ENABLED = 0 WHERE OFFLINE_DEBIT_ENABLED IS NULL
/
COMMIT
/
INSERT INTO jbpm_node
(ID_, CLASS_, NAME_, PROCESSDEFINITION_, ISASYNC_, ACTION_, SUPERSTATE_, SIGNAL_, CREATETASKS_, ENDTASKS_, TASK_NAMES_TO_END, DECISIONEXPRESSION_, DECISIONDELEGATION, SUBPROCESSDEFINITION_, END_TRANSITION, NORMAL_TRANSITION, NODECOLLECTIONINDEX_)
VALUES
(hibernate_sequence.NEXTVAL,'D','CheckOfflineDebitState',(SELECT id_ FROM jbpm_processdefinition WHERE name_ = 'SupplierRecovery'),0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,(SELECT MAX(nodecollectionindex_)+1 FROM jbpm_node))
/
INSERT INTO jbpm_decisionconditions
(DECISION_, TRANSITIONNAME_, EXPRESSION_, INDEX_)
VALUES
((SELECT id_ FROM jbpm_node WHERE name_ = 'CheckOfflineDebitState'), 'OfflineAccepted','#{recoveryClaim.recoveryClaimState.state == "Ready for Debit"}',0)
/
INSERT INTO jbpm_decisionconditions
(DECISION_, TRANSITIONNAME_, EXPRESSION_, INDEX_)
VALUES
((SELECT id_ FROM jbpm_node WHERE name_ = 'CheckOfflineDebitState'), 'OfflineNoResponseAutoDebited','#{recoveryClaim.recoveryClaimState.state == "No Response Auto Debited"}',1)
/
INSERT INTO jbpm_decisionconditions
(DECISION_, TRANSITIONNAME_, EXPRESSION_, INDEX_)
VALUES
((SELECT id_ FROM jbpm_node WHERE name_ = 'CheckOfflineDebitState'), 'OfflineDisputeAutoDebited','#{recoveryClaim.recoveryClaimState.state == "Disputed and Auto Debited"}',2)
/
INSERT INTO jbpm_transition
(ID_, NAME_, PROCESSDEFINITION_, FROM_, TO_, FROMINDEX_)
VALUES
(hibernate_sequence.NEXTVAL,'OfflineAccepted',(SELECT id_ FROM jbpm_processdefinition WHERE name_ = 'SupplierRecovery'),(SELECT id_ FROM jbpm_node WHERE name_ = 'CheckOfflineDebitState'),(SELECT ID_ FROM JBPM_NODE WHERE NAME_='Closed Debited'),0)
/
INSERT INTO JBPM_EVENT
(ID_, EVENTTYPE_, TYPE_, GRAPHELEMENT_, TRANSITION_)
VALUES
(hibernate_sequence.NEXTVAL,'transition','T',(select id_ from jbpm_transition where name_='OfflineAccepted'),(select id_ from jbpm_transition where name_='OfflineAccepted'))
/
INSERT INTO JBPM_ACTION
(ID_, CLASS, NAME_, ISPROPAGATIONALLOWED_, ACTIONEXPRESSION_, ISASYNC_, REFERENCEDACTION_, ACTIONDELEGATION_, EVENT_, PROCESSDEFINITION_, TIMERNAME_, EXPRESSION_, DUEDATE_, REPEAT_, TRANSITIONNAME_, TIMERACTION_, EVENTINDEX_, EXCEPTIONHANDLER_, EXCEPTIONHANDLERINDEX_)
VALUES
(hibernate_sequence.NEXTVAL,'S',null,1,null,0,null,null,(select id_ from jbpm_event where transition_ in (select id_ from jbpm_transition where name_='OfflineAccepted')),null,null,'recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.DEBITTED_AND_CLOSED)',null,null,null,null,0,null,null)
/
INSERT INTO jbpm_transition
(ID_, NAME_, PROCESSDEFINITION_, FROM_, TO_, FROMINDEX_)
VALUES
(hibernate_sequence.NEXTVAL,'OfflineNoResponseAutoDebited',(SELECT id_ FROM jbpm_processdefinition WHERE name_ = 'SupplierRecovery'),(SELECT id_ FROM jbpm_node WHERE name_ = 'CheckOfflineDebitState'),(SELECT ID_ FROM JBPM_NODE WHERE NAME_='Closed Debited'),1)
/
INSERT INTO JBPM_EVENT
(ID_, EVENTTYPE_, TYPE_, GRAPHELEMENT_, TRANSITION_)
VALUES
(hibernate_sequence.NEXTVAL,'transition','T',(select id_ from jbpm_transition where name_='OfflineNoResponseAutoDebited'),(select id_ from jbpm_transition where name_='OfflineNoResponseAutoDebited'))
/
INSERT INTO JBPM_ACTION
(ID_, CLASS, NAME_, ISPROPAGATIONALLOWED_, ACTIONEXPRESSION_, ISASYNC_, REFERENCEDACTION_, ACTIONDELEGATION_, EVENT_, PROCESSDEFINITION_, TIMERNAME_, EXPRESSION_, DUEDATE_, REPEAT_, TRANSITIONNAME_, TIMERACTION_, EVENTINDEX_, EXCEPTIONHANDLER_, EXCEPTIONHANDLERINDEX_)
VALUES
(hibernate_sequence.NEXTVAL,'S',null,1,null,0,null,null,(select id_ from jbpm_event where transition_ in (select id_ from jbpm_transition where name_='OfflineNoResponseAutoDebited')),null,null,'recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.NO_RESPONSE_AND_AUTO_DEBITTED_CLOSED)',null,null,null,null,0,null,null)
/
INSERT INTO jbpm_transition
(ID_, NAME_, PROCESSDEFINITION_, FROM_, TO_, FROMINDEX_)
VALUES
(hibernate_sequence.NEXTVAL,'OfflineDisputeAutoDebited',(SELECT id_ FROM jbpm_processdefinition WHERE name_ = 'SupplierRecovery'),(SELECT id_ FROM jbpm_node WHERE name_ = 'CheckOfflineDebitState'),(SELECT ID_ FROM JBPM_NODE WHERE NAME_='Closed Debited'),2)
/
INSERT INTO JBPM_EVENT
(ID_, EVENTTYPE_, TYPE_, GRAPHELEMENT_, TRANSITION_)
VALUES
(hibernate_sequence.NEXTVAL,'transition','T',(select id_ from jbpm_transition where name_='OfflineDisputeAutoDebited'),(select id_ from jbpm_transition where name_='OfflineDisputeAutoDebited'))
/
INSERT INTO JBPM_ACTION
(ID_, CLASS, NAME_, ISPROPAGATIONALLOWED_, ACTIONEXPRESSION_, ISASYNC_, REFERENCEDACTION_, ACTIONDELEGATION_, EVENT_, PROCESSDEFINITION_, TIMERNAME_, EXPRESSION_, DUEDATE_, REPEAT_, TRANSITIONNAME_, TIMERACTION_, EVENTINDEX_, EXCEPTIONHANDLER_, EXCEPTIONHANDLERINDEX_)
VALUES
(hibernate_sequence.NEXTVAL,'S',null,1,null,0,null,null,(select id_ from jbpm_event where transition_ in (select id_ from jbpm_transition where name_='OfflineDisputeAutoDebited')),null,null,'recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.DISPUTED_AND_AUTO_DEBITTED_CLOSED)',null,null,null,null,0,null,null)
/
INSERT INTO jbpm_transition
(ID_, NAME_, PROCESSDEFINITION_, FROM_, TO_, FROMINDEX_)
VALUES
(hibernate_sequence.NEXTVAL,'goToCheckOfflineDebitState',(SELECT id_ FROM jbpm_processdefinition WHERE name_ = 'SupplierRecovery'),(SELECT id_ FROM jbpm_node WHERE name_ = 'ReadyForDebit'),(SELECT ID_ FROM JBPM_NODE WHERE NAME_='CheckOfflineDebitState'),0)
/
COMMIT
/