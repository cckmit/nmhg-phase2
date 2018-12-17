--Purpose    : Adding a new column IS_POLICY_FORCED_ON_WR  in POLICY_DEFINITION 
--Author     : Lavin Hawes
--Created On : 08-Dec-10

ALTER TABLE POLICY_DEFINITION ADD IS_POLICY_FORCED_ON_WR NUMBER(1,0) default 0
/
COMMIT
/