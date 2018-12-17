--Purpose    : Process definition changes for TKTSA-468
--Author     : Bharath
--Created On : 2/12/2010

DECLARE
  V_PROCESSDEFINITION NUMBER(19);
  V_TRANSITION        NUMBER(19);
  V_EVENT             NUMBER(19);
BEGIN
  SELECT ID_
  INTO V_PROCESSDEFINITION
  FROM JBPM_PROCESSDEFINITION
  WHERE NAME_  = 'ClaimSubmission'
  AND VERSION_ =
    (SELECT MAX(VERSION_)
    FROM JBPM_PROCESSDEFINITION
    WHERE NAME_ = 'ClaimSubmission'
    );
  SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO V_TRANSITION FROM DUAL;
  INSERT
  INTO JBPM_TRANSITION VALUES
    (
      V_TRANSITION,
      'ApproveAndTransferToNextUser',
      V_PROCESSDEFINITION,
      (SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'Replies' and processdefinition_ = V_PROCESSDEFINITION
      ),
      (SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'PendingAuthorization' and processdefinition_ = V_PROCESSDEFINITION
      ),
      9
    );
  SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO V_EVENT FROM DUAL;
  INSERT
  INTO JBPM_EVENT VALUES
    (
      V_EVENT,
      'transition',
      'T',
      V_TRANSITION,
      V_TRANSITION,
      NULL,
      NULL,
      NULL
    );
  INSERT
  INTO JBPM_ACTION VALUES
    (
      HIBERNATE_SEQUENCE.NEXTVAL,
      'S',
      NULL,
      1,
      NULL,
      0,
      NULL,
      NULL,
      V_EVENT,
      V_PROCESSDEFINITION,
	  NULL,
      'claim.setState(tavant.twms.domain.claim.ClaimState.APPROVED);',      
      NULL,
      NULL,
      NULL,
      NULL,
      0,
      NULL,
      NULL
    );
  COMMIT;
EXCEPTION
WHEN OTHERS THEN
  ROLLBACK;
END;
/