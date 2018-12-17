 DECLARE
  V_PROCESSDEFINITION NUMBER(19);
--  V_EVENT_AUTOEXECUTE NUMBER(19);
--  V_EVENT_MANUALREVIEW NUMBER(19);
  V_EVENT_GOTONOTIFY NUMBER(19);
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
	
	
--	SELECT ID_ 
--	INTO V_EVENT_MANUALREVIEW 
--	FROM JBPM_EVENT 
--	WHERE TRANSITION_ = (SELECT ID_ 
--	FROM JBPM_TRANSITION 
--	WHERE PROCESSDEFINITION_=V_PROCESSDEFINITION
--	AND NAME_ = 'sendForManualReview');
--	
--	SELECT ID_ 
--	INTO V_EVENT_AUTOEXECUTE
--	FROM JBPM_EVENT
--	WHERE TRANSITION_ = (SELECT ID_ 
--	FROM JBPM_TRANSITION 
--	WHERE PROCESSDEFINITION_=V_PROCESSDEFINITION
--	AND NAME_ = 'goToRuleExecutionDueToPartCorrection');
	
	SELECT ID_ 
	INTO V_EVENT_GOTONOTIFY
	FROM JBPM_EVENT
	WHERE TRANSITION_ = (SELECT ID_ 
	FROM JBPM_TRANSITION 
	WHERE PROCESSDEFINITION_=V_PROCESSDEFINITION
	AND NAME_ = 'goToNotifyPayment' 
	AND FROM_ = (SELECT ID_ FROM JBPM_NODE 
	WHERE NAME_ = 'WaitForPartReturnsCompletion' 
	AND PROCESSDEFINITION_ = V_PROCESSDEFINITION));
	
	
--    INSERT	INTO JBPM_ACTION VALUES
--    (
--      HIBERNATE_SEQUENCE.NEXTVAL,
--      'S',
--      NULL,
--      1,
--      NULL,
--      0,
--      NULL,
--      NULL,
--      V_EVENT_MANUALREVIEW,
--      NULL,
--      NULL,
--      'claim.setState(tavant.twms.domain.claim.ClaimState.PROCESSOR_REVIEW);',
--      NULL,
--      NULL,
--      NULL,
--      NULL,
--      0,
--      NULL,
--      NULL
--    ); 
--	
--	INSERT INTO JBPM_ACTION VALUES
--    (
--      HIBERNATE_SEQUENCE.NEXTVAL,
--      'S',
--      NULL,
--      1,
--      NULL,
--      0,
--      NULL,
--      NULL,
--      V_EVENT_AUTOEXECUTE,
--      NULL,
--      NULL,
--      'claim.getRuleFailures().clear()',
--      NULL,
--      NULL,
--      NULL,
--      NULL,
--      0,
--      NULL,
--      NULL
--    ); 
	
	INSERT INTO JBPM_ACTION VALUES
    (
      HIBERNATE_SEQUENCE.NEXTVAL,
      'S',
      NULL,
      1,
      NULL,
      0,
      NULL,
      NULL,
      V_EVENT_GOTONOTIFY,
      NULL,
      NULL,
      'claim.setBomUpdationNeeded(true);',
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