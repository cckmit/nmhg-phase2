--PURPOSE    : PATCH_TO_ADD_COLUMNS_IN_stg_draft_claim _for_competitorModel
--AUTHOR     : Raghavendra
--CREATED ON : 02-SEP13

alter table stg_draft_claim add(BRAND_ON_COMPETITOR_MODEL varchar2(2000),COMPETITOR_MODEL_SERIAL_NUMBER varchar2(2000))
/
commit
/