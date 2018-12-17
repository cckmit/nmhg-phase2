--Purpose    : Added scripts for including BUSINESS_UNIT_INFO into the unique constraint.
--Created On : 15-FEB-2011
--Created By : Kuldeep Patil
--Impact     : None

alter table inventory_item drop constraint UNIQUE_INVENTORY_ITEM
/
drop index UNIQUE_INVENTORY_ITEM
/
alter table inventory_item add constraint UNIQUE_INVENTORY_ITEM unique(SERIAL_NUMBER, OF_TYPE, CONDITION_TYPE, D_ACTIVE, BUSINESS_UNIT_INFO)
/
commit
/