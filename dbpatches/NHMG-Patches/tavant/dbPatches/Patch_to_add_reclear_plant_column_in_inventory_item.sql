-- Patch to add reclear_palnt column in inventory_item
-- Author: Saibal
-- Created On : 11-Mar-2013

alter table inventory_item add RECLEAR_PLANT varchar2(255)
/