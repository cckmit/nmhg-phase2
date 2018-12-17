--Purpose    : Performance fix for inventory search by VIN number by creating an index and removed redundant indices as a part of 4.3 upgrade 
--Created On : 01-Jun-2010
--Created By : Ramalakshmi P
--Impact     : Predefined Inventory Search - Retail


CREATE INDEX INV_ITEM_VIN_NUMBER_IDX ON INVENTORY_ITEM(UPPER(VIN_NUMBER))
/
drop index IX_TAV66_5_INT_COMM
/
drop index INVENTORY_ITEM_I1
/
--CREATE INDEX INV_ITEM_UPPSERIAL_NUMBER_IDX ON INVENTORY_ITEM(UPPER(SERIAL_NUMBER))
--/
COMMIT
/