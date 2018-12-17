--Purpose    : Added column to Assignment_Rule_Action, changes made as a part of 4.3 upgrade 
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None

--ALTER TABLE Domain_Rule_Action DROP COLUMN LOA_SCHEME
--/
--Kuldeep - Above statement is not needed as it has been removed from PATCH_LOA_SCHEME.sql patch.
ALTER TABLE Assignment_Rule_Action ADD LOA_SCHEME NUMBER(19,0)
/
ALTER TABLE Assignment_Rule_Action 
	ADD 
 CONSTRAINT "ASS_RULE_ACTION_LOASCHME_FK" FOREIGN KEY ("LOA_SCHEME")
    REFERENCES "LIMIT_OF_AUTHORITY_SCHEME" ("ID")
/
COMMIT
/