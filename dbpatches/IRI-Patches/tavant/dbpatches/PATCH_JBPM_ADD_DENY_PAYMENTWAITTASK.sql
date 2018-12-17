--Purpose    : added deny transition in PaymentWaitTask
--Author     : pratima.rajak
--Created On : 18-August-08


CREATE OR REPLACE PROCEDURE PROC_AdddenyTransition AS
 v_transition_id1           NUMBER := 0;
 v_processdefinition_id     NUMBER := 0; 
 v_node_id1                 NUMBER := 0; 
 v_node_id_end              NUMBER := 0; 
 v_event_id1                NUMBER := 0;
BEGIN 
  
SELECT id_
   INTO   v_processdefinition_id
   FROM jbpm_processdefinition
   WHERE name_ = 'ClaimSubmission';


SELECT id_
   INTO   v_node_id1 
   FROM jbpm_node
   WHERE name_ = 'WaitForPaymentResponse';

SELECT id_
   INTO   v_node_id_end 
   FROM jbpm_node
   WHERE name_ = 'DebitPaymentOnDenial';


SELECT hibernate_sequence.NEXTVAL
   INTO   v_transition_id1
   FROM DUAL;
    
SELECT hibernate_sequence.NEXTVAL
   INTO   v_event_id1
   FROM DUAL;

    INSERT INTO jbpm_event (id_,eventtype_,type_,graphelement_,transition_,task_,node_,processdefinition_)
        VALUES (v_event_id1, 'transition', 'T', v_transition_id1,
                v_transition_id1,NULL, NULL, NULL);  

   INSERT INTO jbpm_action                      (id_,class,name_,ispropagationallowed_,actionexpression_,isasync_,referencedaction_,actiondelegation_,event_,
      processdefinition_,timername_,duedate_,repeat_,transitionname_,timeraction_,expression_,eventindex_,exceptionhandler_,
         exceptionhandlerindex_)VALUES (hibernate_sequence.NEXTVAL, 'S', NULL, 1, NULL,0, NULL, NULL, v_event_id1,
                NULL,NULL,NULL, NULL, NULL, NULL,
               'isClaimDenied = false; claim.setState(tavant.twms.domain.claim.ClaimState.DENIED);',0, NULL,NULL);


   INSERT INTO jbpm_transition (id_,name_,processdefinition_,from_,to_,fromindex_) 
        values(v_transition_id1,'Deny',v_processdefinition_id,v_node_id1,v_node_id_end,1);




COMMIT;



END;
/
BEGIN
PROC_AdddenyTransition();
END;
/