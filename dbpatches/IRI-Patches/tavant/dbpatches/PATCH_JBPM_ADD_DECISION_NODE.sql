--Purpose    : add one decision node and update transition in single token join node 
--Author     : pratima.rajak
--Created On : 20-July-08


CREATE OR REPLACE PROCEDURE PROC_JBPM_PATCH_DecisionNode AS
   v_node_id                     NUMBER := 0;
   v_processdefinition_id        NUMBER := 0;
   v_transition_id1              NUMBER := 0;
   v_transition_id2              NUMBER := 0;
   v_node_id1                    NUMBER := 0;
   v_node_id2                    NUMBER := 0;
   v_node_id3                    NUMBER := 0;
BEGIN
  
  SELECT hibernate_sequence.NEXTVAL
     INTO v_node_id
   FROM DUAL;
   
   
   SELECT id_
             INTO v_processdefinition_id
             FROM jbpm_processdefinition
   WHERE name_ = 'ClaimSubmission';
   
   SELECT id_
           INTO v_node_id1 
           FROM jbpm_node
   WHERE name_ = 'DebitPaymentOnDenial';
   
   SELECT id_
           INTO v_node_id2 
           FROM jbpm_node
   WHERE name_ = 'PolicyAndPaymentProcessorUpdate';
   
   
   SELECT id_
           INTO v_node_id3 
           FROM jbpm_node
   WHERE name_ = 'JoinAfterForwarded';
   
   
   
   SELECT hibernate_sequence.NEXTVAL
        INTO v_transition_id1
   FROM DUAL;
   
   SELECT hibernate_sequence.NEXTVAL
           INTO v_transition_id2
   FROM DUAL;
   
  INSERT INTO jbpm_node (id_,class_,name_,processdefinition_,isasync_,action_,superstate_,signal_,createtasks_,endtasks_,task_names_to_end,end_transition,normal_transition,decisiondelegation,decisionexpression_,subprocessdefinition_,nodecollectionindex_) 
               VALUES (v_node_id,'D','RepliesOrDeny',v_processdefinition_id,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
    
   
  INSERT INTO jbpm_transition (id_,name_,processdefinition_,from_,to_,fromindex_) 
        VALUES (v_transition_id1,'startDenialProcess',v_processdefinition_id,v_node_id,v_node_id1,0);
        
  INSERT INTO jbpm_transition (id_,name_,processdefinition_,from_,to_,fromindex_) 
        VALUES (v_transition_id2,'goToPolicyAndPaymentProcessorUpdate',v_processdefinition_id,v_node_id,v_node_id2,1);
        
  
  INSERT INTO jbpm_decisionconditions(decision_,transitionname_,expression_,index_)
        VALUES  (v_node_id,'startDenialProcess','#{claim.state.state=="Denied"}',0);
        
  INSERT INTO jbpm_decisionconditions(decision_,transitionname_,expression_,index_)
        VALUES  (v_node_id,'goToPolicyAndPaymentProcessorUpdate','#{!(claim.state.state=="Denied")}',1);
        
        
  UPDATE jbpm_transition set name_ = 'checkForRepliesOrDeny',to_ = v_node_id 
		           where name_ = 'goToPolicyAndPaymentProcessorUpdate' and from_ = v_node_id3;
		           
		           
 
 COMMIT;

END;
/

BEGIN
PROC_JBPM_PATCH_DecisionNode();
END;
/