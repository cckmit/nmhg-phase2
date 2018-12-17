DECLARE
  V_PROCESS_DEFINITION_id NUMBER(19,0) := NULL;
  V_transition_id         NUMBER(19,0) := NULL;
  V_EVENT_id              NUMBER(19,0) := NULL;
  V_ACTION_id             NUMBER(19,0) := NULL;
BEGIN
  SELECT MAX(ID_)
  INTO V_PROCESS_DEFINITION_id
  FROM JBPM_PROCESSDEFINITION
  WHERE NAME_ = 'SupplierRecovery';
  SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO V_TRANSITION_ID FROM DUAL;
  SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO V_EVENT_id FROM DUAL;
  SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO V_ACTION_id FROM DUAL;
  INSERT
  INTO JBPM_TRANSITION
    (
      ID_,
      NAME_,
      PROCESSDEFINITION_,
      FROM_,
      TO_,
      FROMINDEX_
    )
    VALUES
    (
      V_TRANSITION_ID,
      'Reopen',
      V_PROCESS_DEFINITION_id,
      (SELECT id_
      FROM JBPM_NODE
      WHERE NAME_            = 'Start'
      AND PROCESSDEFINITION_ = V_PROCESS_DEFINITION_id
      ),
      (SELECT id_
      FROM JBPM_NODE
      WHERE NAME_            = 'Reopen'
      AND PROCESSDEFINITION_ = V_PROCESS_DEFINITION_id
      ),
      (SELECT FROMINDEX_ +1
      FROM JBPM_TRANSITION
      WHERE PROCESSDEFINITION_ = V_PROCESS_DEFINITION_id
      AND NAME_                = 'checkIfAutoCloseApplies'
      )
    ) ;
  INSERT
  INTO JBPM_EVENT
    (
      ID_,
      EVENTTYPE_,
      TYPE_,
      GRAPHELEMENT_,
      TRANSITION_,
      PROCESSDEFINITION_,
      TASK_,
      NODE_
    )
    VALUES
    (
      V_EVENT_id,
      'transition',
      'T',
      V_TRANSITION_ID,
      V_TRANSITION_ID,
      NULL,
      NULL,
      NULL
    );
  INSERT
  INTO JBPM_ACTION
    (
      ID_,
      CLASS,
      ISPROPAGATIONALLOWED_,
      ISASYNC_,
      EVENT_,
      EXPRESSION_,
      EVENTINDEX_
    )
    VALUES
    (
      V_ACTION_ID,
      'S',
      1,
      0,
      V_EVENT_ID,
      'recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.REOPENED)',
      0
    );
  COMMIT;
END;