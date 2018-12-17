--Purpose    : Adding columns to stg_draft_claim
--Author     : Bharath kumar
--Created On : 17/05/2010
--Impact     : None

--alter table stg_draft_claim add REPLACED_IR_PARTS_SERIAL_NUM VARCHAR2(4000 BYTE)
--/
--alter table stg_draft_claim add INSTALLED_IR_PARTS VARCHAR2(4000 BYTE)
--/
--alter table stg_draft_claim add INSTALLED_IR_PARTS_QUANTITY VARCHAR2(4000 BYTE)
--/
--alter table stg_draft_claim add INSTALLED_IR_PARTS_SERIAL_NUM VARCHAR2(4000 BYTE)
--/
--update upload_mgt set columns_to_capture=51 where NAME_OF_TEMPLATE='draftWarrantyClaims'
--/
--Kuldeep - Moved this part to DB patch PATCH_ADD_STG_DRAFT_CLAIM.sql, this patch not needed.
commit
/