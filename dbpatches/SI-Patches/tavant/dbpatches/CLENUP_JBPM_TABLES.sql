create or replace
PROCEDURE CLEANUP_JBPM_TABLES_PROC(
    ENDDATE IN DATE )
AS
  CURSOR jbpmMgtIntance_cur
  IS
    SELECT DISTINCT taskmgmtinstance_
    FROM jbpm_taskinstance
    WHERE TRUNC(end_) < ENDDATE;
  countOfOpenTasks NUMBER ;
  contextInstance  NUMBER ;
  processInstance  NUMBER ;
BEGIN
dbms_output.put_line ('Started the cleanup process - ' || to_char(sysdate, 'dd-mm-yyyy hh24:mi:ss'));
  FOR rec IN jbpmMgtIntance_cur
  LOOP
    BEGIN
      SELECT COUNT(1)
      INTO countOfOpenTasks
      FROM jbpm_taskinstance
      WHERE isopen_         = 1
      AND taskmgmtinstance_ = rec.taskmgmtinstance_;
      IF countOfOpenTasks   = 0 THEN
        -- means this record can be deleted
        SELECT ID_,
          processinstance_
        INTO contextInstance,
          processInstance
        FROM jbpm_moduleinstance
        WHERE processinstance_ =
          (SELECT processinstance_
          FROM jbpm_moduleinstance
          WHERE id_ = rec.taskmgmtinstance_
          )
        AND name_ = 'org.jbpm.context.exe.ContextInstance';
        INSERT
        INTO jbpm_processinstance_bak -- to backup table
          (
            ID_,
            VERSION_,
            START_,
            END_,
            ISSUSPENDED_,
            PROCESSDEFINITION_,
            ROOTTOKEN_,
            SUPERPROCESSTOKEN_
          )
        SELECT ID_,
          VERSION_,
          START_,
          END_,
          ISSUSPENDED_,
          PROCESSDEFINITION_,
          ROOTTOKEN_,
          SUPERPROCESSTOKEN_
        FROM jbpm_processinstance
        WHERE ID_ = processInstance;
        INSERT
        INTO jbpm_moduleinstance_bak
          (
            ID_,
            CLASS_,
            PROCESSINSTANCE_,
            TASKMGMTDEFINITION_,
            NAME_
          )
        SELECT ID_,
          CLASS_,
          PROCESSINSTANCE_,
          TASKMGMTDEFINITION_,
          NAME_
        FROM jbpm_moduleinstance
        WHERE processinstance_ = processinstance;
        INSERT
        INTO jbpm_token_bak
          (
            ID_,
            VERSION_,
            NAME_,
            START_,
            END_,
            NODEENTER_,
            NEXTLOGINDEX_,
            ISABLETOREACTIVATEPARENT_,
            ISTERMINATIONIMPLICIT_,
            ISSUSPENDED_,
            NODE_,
            PROCESSINSTANCE_,
            PARENT_,
            SUBPROCESSINSTANCE_
          )
        SELECT ID_,
          VERSION_,
          NAME_,
          START_,
          END_,
          NODEENTER_,
          NEXTLOGINDEX_,
          ISABLETOREACTIVATEPARENT_,
          ISTERMINATIONIMPLICIT_,
          ISSUSPENDED_,
          NODE_,
          PROCESSINSTANCE_,
          PARENT_,
          SUBPROCESSINSTANCE_
        FROM jbpm_token
        WHERE processinstance_ = processinstance;
        INSERT
        INTO jbpm_variableinstance_bak
          (
            ID_,
            CLASS_,
            NAME_,
            CONVERTER_,
            TOKEN_,
            TOKENVARIABLEMAP_,
            PROCESSINSTANCE_,
            BYTEARRAYVALUE_,
            LONGVALUE_,
            STRINGVALUE_,
            DOUBLEVALUE_,
            STRINGIDCLASS_,
            LONGIDCLASS_,
            DATEVALUE_,
            TASKINSTANCE_
          )
        SELECT ID_,
          CLASS_,
          NAME_,
          CONVERTER_,
          TOKEN_,
          TOKENVARIABLEMAP_,
          PROCESSINSTANCE_,
          BYTEARRAYVALUE_,
          LONGVALUE_,
          STRINGVALUE_,
          DOUBLEVALUE_,
          STRINGIDCLASS_,
          LONGIDCLASS_,
          DATEVALUE_,
          TASKINSTANCE_
        FROM jbpm_variableinstance
        WHERE processinstance_ = processinstance;
        INSERT
        INTO jbpm_timer_bak
          (
            ID_,
            NAME_,
            DUEDATE_,
            REPEAT_,
            TRANSITIONNAME_,
            EXCEPTION_,
            ISSUSPENDED_,
            ACTION_,
            TOKEN_,
            PROCESSINSTANCE_,
            TASKINSTANCE_,
            GRAPHELEMENTTYPE_,
            GRAPHELEMENT_
          )
        SELECT ID_,
          NAME_,
          DUEDATE_,
          REPEAT_,
          TRANSITIONNAME_,
          EXCEPTION_,
          ISSUSPENDED_,
          ACTION_,
          TOKEN_,
          PROCESSINSTANCE_,
          TASKINSTANCE_,
          GRAPHELEMENTTYPE_,
          GRAPHELEMENT_
        FROM jbpm_timer
        WHERE processinstance_ = processinstance;
        INSERT INTO jbpm_tokenvariablemap_bak
          ( ID_, TOKEN_, CONTEXTINSTANCE_
          )
        SELECT ID_,
          TOKEN_,
          CONTEXTINSTANCE_
        FROM jbpm_tokenvariablemap
        WHERE contextinstance_ = contextInstance;
        INSERT
        INTO jbpm_taskinstance_bak
          (
            ID_,
            CLASS_,
            NAME_,
            DESCRIPTION_,
            ACTORID_,
            CREATE_,
            START_,
            END_,
            DUEDATE_,
            PRIORITY_,
            ISCANCELLED_,
            ISSUSPENDED_,
            ISOPEN_,
            ISSIGNALLING_,
            ISBLOCKING_,
            TASK_,
            TOKEN_,
            SWIMLANINSTANCE_,
            TASKMGMTINSTANCE_
          )
        SELECT ID_,
          CLASS_,
          NAME_,
          DESCRIPTION_,
          ACTORID_,
          CREATE_,
          START_,
          END_,
          DUEDATE_,
          PRIORITY_,
          ISCANCELLED_,
          ISSUSPENDED_,
          ISOPEN_,
          ISSIGNALLING_,
          ISBLOCKING_,
          TASK_,
          TOKEN_,
          SWIMLANINSTANCE_,
          TASKMGMTINSTANCE_
        FROM jbpm_taskinstance
        WHERE taskmgmtinstance_ = rec.taskmgmtinstance_;
        INSERT
        INTO jbpm_swimlaneinstance_bak
          (
            ID_,
            NAME_,
            ACTORID_,
            SWIMLANE_,
            TASKMGMTINSTANCE_
          )
        SELECT ID_,
          NAME_,
          ACTORID_,
          SWIMLANE_,
          TASKMGMTINSTANCE_
        FROM jbpm_swimlaneinstance
        WHERE taskmgmtinstance_ = rec.taskmgmtinstance_;
        COMMIT;
      END IF ;
    EXCEPTION        -- exception handlers begin
    WHEN OTHERS THEN -- handles all other errors
      dbms_output.put_line ('Exception occured while deleting taskmgmtinstance_ :' || rec.taskmgmtinstance_);
      ROLLBACK;
    END;
  END LOOP ;
  DELETE
  FROM jbpm_processinstance
  WHERE id_ IN
    (SELECT id_ FROM jbpm_processinstance_bak
    );
  DELETE
  FROM jbpm_moduleinstance
  WHERE id_ IN
    (SELECT id_ FROM jbpm_moduleinstance_bak
    );
  DELETE FROM jbpm_token WHERE id_ IN
    (SELECT id_ FROM jbpm_token_bak
    );
  DELETE
  FROM jbpm_variableinstance
  WHERE id_ IN
    (SELECT id_ FROM jbpm_variableinstance_bak
    );
  DELETE FROM jbpm_timer WHERE id_ IN
    (SELECT id_ FROM jbpm_timer_bak
    );
  DELETE
  FROM jbpm_tokenvariablemap
  WHERE id_ IN
    (SELECT id_ FROM jbpm_tokenvariablemap_bak
    );
  DELETE
  FROM jbpm_taskinstance
  WHERE id_ IN
    (SELECT id_ FROM jbpm_taskinstance_bak
    );
  DELETE
  FROM jbpm_swimlaneinstance
  WHERE id_ IN
    (SELECT id_ FROM jbpm_swimlaneinstance_bak
    );
    COMMIT;
    dbms_output.put_line ('Ended the cleanup process - ' || to_char(sysdate, 'dd-mm-yyyy hh24:mi:ss'));
END CLEANUP_JBPM_TABLES_PROC;
/

DECLARE
  ENDDATE DATE;
BEGIN
  ENDDATE := '01-12-11';

  CLEANUP_JBPM_TABLES_PROC(
    ENDDATE => ENDDATE
  );
END;
/