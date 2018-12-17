--ALTER TABLE RECOVERY_INFO ADD (
  --CONSTRAINT UC_REC_INFO_CLAIM UNIQUE (WARRANTY_CLAIM)
--)
--/
--KULDEEP : Moved this part to PATCH_SUPPLIER_RECOVERY.sql under RECOVERY_INFO table creation script, so this script is not needed.
commit
/