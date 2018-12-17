--Purpose    : Patch for adding policy_code column to claim table.
--Author     : ROHIT MEHROTRA
--Created On : 09-MAY-2013

ALTER TABLE CLAIM ADD POLICY_CODE VARCHAR2(255)
/
