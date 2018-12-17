-- Patch to add columns to part recovery audit table
-- Author: Deepak
-- Created On : 18-SEP-2013

alter table part_recovery_audit add (SHIPMENT_NUMBER VARCHAR2(255 CHAR))
/
alter table part_recovery_audit add (TRACKING_NO VARCHAR2(255 CHAR))
/