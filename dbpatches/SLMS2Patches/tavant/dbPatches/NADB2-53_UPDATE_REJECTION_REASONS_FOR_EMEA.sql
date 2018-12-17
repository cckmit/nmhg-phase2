CREATE OR REPLACE
PROCEDURE UPDATEREJECTIONREASONSFOREMEA
AS
  CURSOR ALL_REC
  IS
    SELECT id,
      rejection_reason
    FROM claim_audit
    WHERE rejection_reason IS NOT NULL;
BEGIN
  FOR EACH_REC IN ALL_REC
  LOOP
    INSERT
    INTO CLAIM_AUDIT_TO_REJECT_REASON
      (
        CLAIM_AUDIT_ID,
        REJECT_REASON_ID
      )
      VALUES
      (
        EACH_REC.id,
        each_rec.rejection_reason
      );
    COMMIT;
  END LOOP;
  COMMIT;
END UPDATEREJECTIONREASONSFOREMEA;