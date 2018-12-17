--Purpose    :Foc Claim
--Author     : prashanth konda
--Created On : 20 feb 2008

CREATE OR REPLACE PROCEDURE PROC_FOC_JBPM_CHANGE AS 
v_wait_for_labor_node_id 	NUMBER;
v_jbpm_decision_node_id		NUMBER;
v_draft_claim_node_id           NUMBER;
v_start_node_id                 NUMBER;
processdefinitionId		NUMBER;
v_jbpm_task_id			NUMBER;
v_transition_for_taskNode       NUMBER; 
v_transition1_for_DecisionNode  NUMBER;
v_transition2_for_DecisionNode  NUMBER;

BEGIN

SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO v_wait_for_labor_node_id FROM DUAL;

SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO v_jbpm_decision_node_id FROM DUAL;

SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO v_jbpm_task_id  FROM DUAL;

SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO v_transition_for_taskNode FROM DUAL;

SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO v_transition1_for_DecisionNode FROM DUAL;

SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO v_transition2_for_DecisionNode FROM DUAL;

SELECT ID_ INTO processdefinitionId FROM jbpm_processdefinition  WHERE  NAME_ = 'ClaimSubmission';

SELECT ID_ INTO v_draft_claim_node_id from jbpm_node where name_='DraftClaim' and processdefinition_ = (SELECT ID_ FROM jbpm_processdefinition  WHERE  NAME_ = 'ClaimSubmission');

SELECT ID_ INTO v_start_node_id from jbpm_node where name_= 'Start' and processdefinition_ = (SELECT ID_ FROM jbpm_processdefinition  WHERE  NAME_ = 'ClaimSubmission');

--Create node to check for foc
 INSERT INTO jbpm_node  (id_,class_,name_,processdefinition_,isasync_,action_,superstate_,signal_,createtasks_,endtasks_,task_names_to_end,end_transition,normal_transition,decisiondelegation,decisionexpression_,subprocessdefinition_,nodecollectionindex_)   VALUES  (v_jbpm_decision_node_id,'D','IsFocClaim',processdefinitionId,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
 
 --create waiting for labor node
insert into jbpm_node   (id_,class_, name_,processdefinition_, isasync_, signal_, createtasks_, endtasks_, nodecollectionindex_ )  values  (v_wait_for_labor_node_id, 'A','WaitingForLabor',processdefinitionId,0,4,1,0,(select max(nodecollectionindex_) +1 from jbpm_node) );
 
INSERT INTO jbpm_transition (id_,name_,processdefinition_,from_,to_,fromindex_)  VALUES (v_transition1_for_DecisionNode,'Yes',processdefinitionId,v_jbpm_decision_node_id,v_wait_for_labor_node_id,0);

INSERT INTO jbpm_transition (id_,name_,processdefinition_,from_,to_,fromindex_)  VALUES (v_transition2_for_DecisionNode,'No',processdefinitionId,v_jbpm_decision_node_id,v_draft_claim_node_id,1);

INSERT INTO jbpm_decisionconditions(decision_,transitionname_,expression_,index_) VALUES  (v_jbpm_decision_node_id,'Yes','#{claim.foc}',0);
        
INSERT INTO jbpm_decisionconditions(decision_,transitionname_,expression_,index_) VALUES  (v_jbpm_decision_node_id,'No','#{!claim.foc}',1);

INSERT INTO jbpm_transition (id_,name_,processdefinition_,from_,to_,fromindex_) VALUES (v_transition_for_taskNode,'Submit Claim',processdefinitionId,v_wait_for_labor_node_id,(select id_ from jbpm_node where name_='generateClaimNumber'),0);

--create waiting for labor jbpm task
INSERT INTO JBPM_TASK  (ID_, NAME_, PROCESSDEFINITION_, DESCRIPTION_, ISBLOCKING_, ISSIGNALLING_, DUEDATE_, ACTORIDEXPRESSION_, POOLEDACTORSEXPRESSION_,TASKMGMTDEFINITION_, TASKNODE_, STARTSTATE_, ASSIGNMENTDELEGATION_, SWIMLANE_, TASKCONTROLLER_) 
VALUES (v_jbpm_task_id ,'Waiting For Labor', processdefinitionId,NULL,0,1,NULL,NULL,NULL,
	(SELECT ID_ FROM JBPM_MODULEDEFINITION WHERE PROCESSDEFINITION_=processdefinitionId AND NAME_='org.jbpm.taskmgmt.def.TaskMgmtDefinition'),
v_wait_for_labor_node_id,NULL,NULL,
(SELECT ID_ FROM JBPM_SWIMLANE WHERE NAME_='baserole' AND TASKMGMTDEFINITION_ = (SELECT ID_ FROM JBPM_MODULEDEFINITION WHERE NAME_='org.jbpm.taskmgmt.def.TaskMgmtDefinition' AND PROCESSDEFINITION_=processdefinitionId)
AND rownum<=1),NULL);


--Create the form
INSERT INTO JBPM_FORM_NODES (FORM_TASK_NODE_FORM_ID,FORM_VALUE,FORM_TYPE) 
VALUES 
(v_wait_for_labor_node_id,'draft_claim','inputForm');

INSERT INTO JBPM_EVENT (ID_,EVENTTYPE_,TYPE_,GRAPHELEMENT_,TRANSITION_) VALUES 
(HIBERNATE_SEQUENCE.NEXTVAL,'transition','T',v_transition1_for_DecisionNode,v_transition1_for_DecisionNode);


INSERT INTO JBPM_ACTION (ID_,CLASS,ISPROPAGATIONALLOWED_,ISASYNC_,event_,EVENTindex_,EXPRESSION_) 
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'S',1,0,(SELECT ID_ FROM JBPM_EVENT WHERE TRANSITION_ =v_transition1_for_DecisionNode),0,
          	'claim.setState(tavant.twms.domain.claim.ClaimState.DRAFT)');
          	
update JBPM_EVENT set transition_ =  v_transition2_for_DecisionNode where  transition_ = (select id_ from jbpm_transition where processdefinition_ = processdefinitionId  and from_= v_start_node_id  and to_ = v_draft_claim_node_id);

update JBPM_EVENT set GRAPHELEMENT_ =  v_transition2_for_DecisionNode where  GRAPHELEMENT_ = (select id_ from jbpm_transition where processdefinition_ = processdefinitionId  and from_= v_start_node_id  and to_ = v_draft_claim_node_id);

update jbpm_transition set to_ = v_jbpm_decision_node_id  where processdefinition_ = processdefinitionId  and from_= v_start_node_id  and to_ = v_draft_claim_node_id;

COMMIT;
END;
/

BEGIN
PROC_FOC_JBPM_CHANGE();
END;
/

