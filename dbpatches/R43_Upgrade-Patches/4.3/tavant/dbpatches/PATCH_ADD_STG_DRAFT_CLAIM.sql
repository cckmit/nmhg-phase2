--Purpose    : Adding columns to stg_draft_claim, changes done as a part of 4.3 upgrade
--Author     : Kuldeep Patil
--Created On : 11-Oct-2010
--Impact     : None

ALTER TABLE STG_DRAFT_CLAIM ADD COMMERCIAL_POLICY VARCHAR2(255 CHAR)
/
ALTER TABLE STG_DRAFT_CLAIM ADD PART_SERIAL_NUMBER VARCHAR2(255 CHAR)
/
ALTER TABLE STG_DRAFT_CLAIM ADD COMPETITOR_MODEL VARCHAR2(255 CHAR)
/
ALTER TABLE STG_DRAFT_CLAIM ADD COMPETITOR_MODEL_ID VARCHAR2(255 CHAR)
/
ALTER TABLE STG_DRAFT_CLAIM ADD IS_PART_INSTALLED_ON_TKTSA VARCHAR2(255 CHAR)
/
ALTER TABLE STG_DRAFT_CLAIM ADD IS_PART_INSTALLED VARCHAR2(255 CHAR)
/
ALTER TABLE STG_DRAFT_CLAIM ADD PART_NUMBER  VARCHAR2(255 CHAR)
/
ALTER TABLE STG_DRAFT_CLAIM ADD ALARM_CODES VARCHAR2(4000 BYTE)
/
ALTER TABLE STG_DRAFT_CLAIM ADD IS_SERIALIZED VARCHAR2(255 CHAR)
/
ALTER TABLE STG_DRAFT_CLAIM ADD REPLACED_IR_PARTS_SERIAL_NUM VARCHAR2(4000 BYTE)
/
ALTER TABLE STG_DRAFT_CLAIM ADD INSTALLED_IR_PARTS VARCHAR2(4000 BYTE)
/
ALTER TABLE STG_DRAFT_CLAIM ADD INSTALLED_IR_PARTS_QUANTITY VARCHAR2(4000 BYTE)
/
ALTER TABLE STG_DRAFT_CLAIM ADD INSTALLED_IR_PARTS_SERIAL_NUM VARCHAR2(4000 BYTE)
/
UPDATE UPLOAD_MGT SET COLUMNS_TO_CAPTURE=51 WHERE NAME_OF_TEMPLATE='draftWarrantyClaims'
/
--update upload_mgt set columns_to_capture=46 where id = 4
--/
alter table STG_DRAFT_CLAIM add CONTAINER_NUMBER	VARCHAR2(4000 BYTE)
/
COMMIT
/