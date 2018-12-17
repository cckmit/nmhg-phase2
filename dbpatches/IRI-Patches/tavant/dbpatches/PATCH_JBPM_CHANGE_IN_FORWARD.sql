--Purpose    : add one transition with script in forward task node
--Author     : pratima.rajak
--Created On : 20-July-08


CREATE OR REPLACE PROCEDURE PROC_JBPM_PATCH_CHANGEFORWARD AS
    v_processdefinition_id    NUMBER := 0;
    v_node_id1                NUMBER := 0;
    v_node_id2                NUMBER := 0;
    v_event_id                NUMBER := 0;
    v_transition_id           NUMBER := 0;
BEGIN
  
  SELECT id_
          INTO v_processdefinition_id
          FROM jbpm_processdefinition
   WHERE name_ = 'ClaimSubmission';
   
   
  SELECT id_
        INTO v_node_id1 
        FROM jbpm_node
   WHERE name_ = 'Forwarded';
   
   SELECT id_
         INTO v_node_id2 
         FROM jbpm_node
   WHERE name_ = 'JoinAfterForwarded';
   
   
   SELECT hibernate_sequence.NEXTVAL
         INTO v_transition_id
   FROM DUAL;
   
    SELECT hibernate_sequence.NEXTVAL
      INTO v_event_id
   FROM DUAL;
   
   
  INSERT INTO jbpm_transition (id_,name_,processdefinition_,from_,to_,fromindex_) 
        VALUES (v_transition_id,'DenyOnNoReply',v_processdefinition_id,v_node_id1,v_node_id2,1);
        
  
  INSERT INTO jbpm_event (id_,eventtype_,type_,graphelement_,transition_,task_,node_,processdefinition_)
          VALUES (v_event_id, 'transition', 'T', v_transition_id,
                v_transition_id, NULL, NULL, NULL);
                
                
  INSERT INTO jbpm_action (id_,class,name_,ispropagationallowed_,actionexpression_,isasync_,referencedaction_,actiondelegation_,
  	            event_,processdefinition_,timername_,duedate_,repeat_,transitionname_,timeraction_,expression_,eventindex_,exceptionhandler_,exceptionhandlerindex_)
          VALUES (hibernate_sequence.NEXTVAL, 'S', NULL, 1, NULL,0, NULL, NULL, v_event_id,
                NULL,NULL,NULL, NULL, NULL, NULL,'isClaimDenied = false; claim.setState(tavant.twms.domain.claim.ClaimState.DENIED);',0, NULL,NULL);


 
 COMMIT;

END;
/

BEGIN
PROC_JBPM_PATCH_CHANGEFORWARD();
END;
/