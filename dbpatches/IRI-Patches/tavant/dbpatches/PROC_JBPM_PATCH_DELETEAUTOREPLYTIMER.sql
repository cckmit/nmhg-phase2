--Purpose    : Delete an existing timer for auto reply from dealer for forwarded claim 
--Author     : pratima.rajak
--Created On : 18-July-08

CREATE OR REPLACE PROCEDURE PROC_JBPM_PATCH_delAutoRep AS
   V_delegation_id1         NUMBER := 0;
   V_delegation_id2         NUMBER := 0;
   v_task_id                NUMBER := 0;
  BEGIN
  
   SELECT id_
   INTO   v_task_id
   FROM jbpm_task
   WHERE name_ = 'Forwarded Externally';


   SELECT id_
   INTO   V_delegation_id1
   FROM jbpm_delegation
   where configuration_ like '%TimeoutAfterForwardedExternally%';

   SELECT id_
   INTO   V_delegation_id2
   FROM jbpm_delegation
   WHERE classname_ = 'tavant.twms.jbpm.action.ForwardedClaimsAutoReplyAction'; 

   
   DELETE FROM jbpm_transition 
              WHERE name_ = 'TimeoutAfterForwardedExternally';


   DELETE FROM jbpm_action where  timername_ = 'Forwarded Externally';

   DELETE FROM jbpm_action where actiondelegation_ = V_delegation_id2;
   
   DELETE FROM jbpm_action where actiondelegation_ = V_delegation_id1;


   DELETE FROM jbpm_delegation
              WHERE id_ = V_delegation_id1;

   
   DELETE FROM jbpm_delegation
              WHERE id_ = V_delegation_id2;

    
   DELETE FROM jbpm_event
              WHERE task_ = v_task_id;
  
 COMMIT;


END;
/
BEGIN
PROC_JBPM_PATCH_delAutoRep();
END;
/