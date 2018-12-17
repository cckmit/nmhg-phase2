-- Purpose : Include d_active in the unique constraint UNIQUE_INVENTORY_ITEM so that deactivated serial number can be uploaded again.
-- Author  : raghuram.d
-- Date    : 12-Nov-09

alter table inventory_item drop constraint UNIQUE_INVENTORY_ITEM
/
drop index UNIQUE_INVENTORY_ITEM
/
alter table inventory_item add constraint UNIQUE_INVENTORY_ITEM unique(serial_number,of_type,condition_type,d_active)
/