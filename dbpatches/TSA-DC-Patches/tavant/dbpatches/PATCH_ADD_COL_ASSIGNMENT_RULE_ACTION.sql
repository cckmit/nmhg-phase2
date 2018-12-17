--Purpose    : Added column to Assignment_Rule_Action
--Author     : Bharath
--Created On : 22-Feb-10

ALTER TABLE Domain_Rule_Action DROP COLUMN LOA_SCHEME
/
ALTER TABLE Assignment_Rule_Action ADD LOA_SCHEME NUMBER(19,0)
/
ALTER TABLE Assignment_Rule_Action 
	ADD 
 CONSTRAINT "ASS_RULE_ACTION_LOASCHME_FK" FOREIGN KEY ("LOA_SCHEME")
    REFERENCES "LIMIT_OF_AUTHORITY_SCHEME" ("ID")
/
COMMIT
/