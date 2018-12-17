alter table "TWMS_OWNER"."SYNC_TRACKER" add(HIDDEN_BY VARCHAR2(50));

alter table "TWMS_OWNER"."SYNC_TRACKER" add(HIDDEN_ON DATE);

CREATE BITMAP INDEX IS_DELETED_IDX ON SYNC_TRACKER (IS_DELETED);
