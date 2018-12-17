--Purpose    : Set the state to REOPENED_ON_HOLD when a reopened recovery claim is put on hold
--Created On : 08/Mar/2012
--Created By : raghuram.d
DECLARE
  v_processdefinition NUMBER(19);
  v_transition        NUMBER(19);
  v_event             NUMBER(19);
  v_action            NUMBER(19);
BEGIN
  SELECT ID_
  INTO v_processdefinition
  FROM JBPM_PROCESSDEFINITION
  WHERE NAME_  = 'SupplierRecovery'
  AND VERSION_ =
    (SELECT MAX(VERSION_)
    FROM JBPM_PROCESSDEFINITION
    WHERE NAME_ = 'SupplierRecovery'
    );
  SELECT id_
  INTO v_transition
  FROM JBPM_TRANSITION
  WHERE from_=
    (SELECT ID_
    FROM JBPM_NODE
    WHERE NAME_            = 'Reopen'
    AND processdefinition_ = v_processdefinition
    )
  AND to_=
    (SELECT ID_
    FROM JBPM_NODE
    WHERE NAME_            = 'On Hold'
    AND processdefinition_ = v_processdefinition
    );
  BEGIN
    SELECT id_
    INTO v_event
    FROM jbpm_event
    WHERE eventtype_='transition'
    AND transition_ =v_transition;
  EXCEPTION
  WHEN no_data_found THEN
    SELECT HIBERNATE_SEQUENCE.NEXTVAL-1 INTO v_event FROM DUAL;
    INSERT
    INTO JBPM_EVENT VALUES
      (
        v_event,
        'transition',
        'T',
        v_transition,
        v_transition,
        NULL,
        NULL,
        NULL
      );
  END;
  BEGIN
    SELECT id_ INTO v_action FROM jbpm_action WHERE class='S' AND EVENT_=v_event;
  EXCEPTION
  WHEN no_data_found THEN
    INSERT
    INTO JBPM_ACTION VALUES
      (
        HIBERNATE_SEQUENCE.NEXTVAL-1,
        'S',
        NULL,
        1,
        NULL,
        0,
        NULL,
        NULL,
        v_event,
        NULL,
        NULL,
        'recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.REOPENED_ON_HOLD)',
        NULL,
        NULL,
        NULL,
        NULL,
        0,
        NULL,
        NULL
      );
  END;
  COMMIT;
EXCEPTION
WHEN OTHERS THEN
  ROLLBACK;
END;
