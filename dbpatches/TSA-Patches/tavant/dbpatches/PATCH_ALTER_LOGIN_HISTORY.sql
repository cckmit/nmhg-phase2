--Purpose    : Alter login history table for modifying id length and also adding foreign constraint
--Author     : Varun
--Created On : 11-May-11

alter table login_history modify id number(19)
/
ALTER TABLE login_history ADD CONSTRAINT D_LOG_HISTORY_FK
Foreign Key (LOGGED_IN_USER) REFERENCES "TWMS_OWNER"."ORG_USER" ("ID")
/
COMMIT
/