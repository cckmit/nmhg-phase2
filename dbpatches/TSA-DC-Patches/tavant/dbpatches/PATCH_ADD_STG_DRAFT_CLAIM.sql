--Purpose    : Adding columns to stg_draft_claim
--Author     : Bharath kumar
--Created On : 23/04/2010
--Impact     : None

alter table stg_draft_claim add COMMERCIAL_POLICY	VARCHAR2(255 CHAR)
/
alter table stg_draft_claim add PART_SERIAL_NUMBER VARCHAR2(255 CHAR)
/
alter table stg_draft_claim add COMPETITOR_MODEL VARCHAR2(255 CHAR)
/
alter table stg_draft_claim add COMPETITOR_MODEL_ID VARCHAR2(255 CHAR)
/
alter table stg_draft_claim add IS_PART_INSTALLED_ON_TKTSA VARCHAR2(255 CHAR)
/
alter table stg_draft_claim add IS_PART_INSTALLED VARCHAR2(255 CHAR)
/
alter table stg_draft_claim add part_number  VARCHAR2(255 CHAR)
/
alter table stg_draft_claim add alarm_codes VARCHAR2(4000 BYTE)
/
commit
/



