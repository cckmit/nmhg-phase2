--Purpose    : Adding a new column commercial policy in policy criteria
--Author     : Lavin Hawes
--Created On : 02-Aug-10

ALTER TABLE POLICY_CRITERIA ADD APPL_FOR_COMM_POLICY_CLAIMS NUMBER(1,0) default 0
/
COMMIT
/