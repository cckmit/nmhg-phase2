-- Purpose    : Patch for migrating SERVICING_LOCATION from CLAIM to CLAIM_AUDIT table
-- Courtesy   : Saibal
-- Created On : 05-Dec-2013

DECLARE
CURSOR c1 IS 
SELECT c.servicing_location serv_loc, ca.* FROM claim c, claim_Audit ca
WHERE (c.id=ca.for_claim OR ca.id=c.active_claim_audit)
;
BEGIN
  FOR i IN c1
  LOOP
      UPDATE claim_audit SET  SERVICING_LOCATION=i.serv_loc WHERE id=i.id;
      COMMIT;
  END LOOP;
END;
