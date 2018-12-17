--Purpose    : Performance fix for Claim Validation
--Created On : 03-Jun-2010
--Created By : Ramalakshmi P
--Impact     : Removed document reference from hussmann part replaced/installed as it is present under NonOEMPartReplaced


ALTER TABLE HUSS_PARTS_REPLACED_INSTALLED DROP COLUMN INVOICE
/
COMMIT
/