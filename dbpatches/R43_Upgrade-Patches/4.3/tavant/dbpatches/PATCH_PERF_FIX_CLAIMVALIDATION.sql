--Purpose    : Performance fix for Claim Validation, changes made as a part of 4.3 upgrade 
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : Removed document reference from hussmann part replaced/installed as it is present under NonOEMPartReplaced

CREATE TABLE HUSS_PARTS_REP_INST_BACKUP
AS
(
	SELECT ID, INVOICE
	FROM HUSS_PARTS_REPLACED_INSTALLED
)
/
ALTER TABLE HUSS_PARTS_REPLACED_INSTALLED DROP COLUMN INVOICE
/
COMMIT
/