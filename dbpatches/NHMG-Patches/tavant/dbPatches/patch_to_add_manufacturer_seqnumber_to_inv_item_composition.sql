-- Patch to add MANUFACTURER ,sequence_number,component_serial_type 
--column in inventory_item_composition 
-- Author: Saibal
-- Created On : 11-Mar-2013

alter table inventory_item_composition add MANUFACTURER varchar2(255)
/
alter table inventory_item_composition add COMPONENT_SERIAL_TYPE varchar2(255)
/
alter table inventory_item_composition add SEQUENCE_NUMBER number
/