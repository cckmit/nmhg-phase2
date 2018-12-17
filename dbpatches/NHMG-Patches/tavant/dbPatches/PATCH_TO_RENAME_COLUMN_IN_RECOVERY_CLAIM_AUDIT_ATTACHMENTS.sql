--Purpose    : Patch to modify column name in rec_claim_audit_attachments table.
--Author     : Suneetha Nagaboyina
--Created On : 21-NOV-2012

ALTER TABLE rec_claim_audit_attachments RENAME COLUMN RECOVERY_CLAIM_AUDIT TO REC_CLAIM_AUDIT
/
commit
/
