--Purpose :Insert new params and create new node related to partshippednotreceived inbox
--Author     : PRADYOT.ROUT		
--Created On : 17-aUG-2008
-- THE CODE FROM HERE IS RELATED TO JBPM CHANGES(PART SHIPPED AND NOT RECEIVED WITHIN WINDOW PERIOD
-- HAS TO BE DENIED OR MOVED INTO PART SHIPPED NOT RECEIVED INBOX)
-- INSERT NEW NODE,TASK AND FORM INTO RESPECTIVE TABLES
CREATE OR REPLACE PROCEDURE PROC_CREATE_PRTSHPNTRCVD_PATCH AS
     
        V_PROCESSDEFINITIONID   NUMBER := 0;
        V_JBPMNODEID            NUMBER := 0;
        V_JBPMTASKID            NUMBER := 0;
     BEGIN
        --SET THE PROCESSDEFINITION ID
        SELECT ID_
        INTO   V_PROCESSDEFINITIONID
        FROM JBPM_PROCESSDEFINITION
        WHERE NAME_ = 'ClaimSubmission';
     
        --FOR TASKNODE AND TASK ID
        SELECT HIBERNATE_SEQUENCE.NEXTVAL
        INTO   V_JBPMNODEID
        FROM DUAL;
     
        SELECT HIBERNATE_SEQUENCE.NEXTVAL
        INTO   V_JBPMTASKID
        FROM DUAL;
     
        --INSERTION INTO TABLES
        INSERT INTO JBPM_NODE
                    (ID_, CLASS_, NAME_,
                     PROCESSDEFINITION_, ISASYNC_, SIGNAL_, CREATETASKS_,
                     NODECOLLECTIONINDEX_,ENDTASKS_
                    )
             VALUES (V_JBPMNODEID, 'A', 'MoveClaimToPartsShippedNotreceivedInbox',
                     V_PROCESSDEFINITIONID, 0, 4, 1,
                     (SELECT MAX (NODECOLLECTIONINDEX_) + 1
                      FROM JBPM_NODE
                      WHERE PROCESSDEFINITION_ = V_PROCESSDEFINITIONID),0
                    );
     
        INSERT INTO JBPM_TASK
                    (ID_, NAME_,
                     PROCESSDEFINITION_, ISBLOCKING_, ISSIGNALLING_,
                     TASKMGMTDEFINITION_,
                     TASKNODE_,SWIMLANE_
                    )
             VALUES (V_JBPMTASKID, 'Part Shipped Not Received',
                     V_PROCESSDEFINITIONID, 0, 1,
                     (SELECT ID_
                      FROM JBPM_MODULEDEFINITION
                      WHERE PROCESSDEFINITION_ = V_PROCESSDEFINITIONID
                         AND CLASS_ = 'T'),
                     V_JBPMNODEID,(SELECT MAX(ID_) FROM JBPM_SWIMLANE WHERE NAME_='processor')
                    );
     
        INSERT INTO JBPM_FORM_NODES
                    (FORM_TASK_NODE_FORM_ID, FORM_VALUE, FORM_TYPE
                    )
             VALUES (V_JBPMNODEID, 'part_shipped_not_received', 'inputForm'
                    );

     		
     END;
/
BEGIN
PROC_CREATE_PRTSHPNTRCVD_PATCH();
END;
/
COMMIT
/