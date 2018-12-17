--Purpose    : Create index on Inventory Item's current_owner and latest_buyer columns. 
--             Delete indexes on type and ownership_state columns as they have only 2 distinct values each
--Author     : Nandakumar Devi
--Created On : 27-JUL-09

CREATE INDEX INVITEM_CURRENTOWNER_IDX ON INVENTORY_ITEM(CURRENT_OWNER)
/
CREATE INDEX INVITEM_LATESTBUYER_IDX ON INVENTORY_ITEM(LATEST_BUYER)
/
DROP INDEX INVENTORYITEM_OWNERSHIPSTAT_IX
/
DROP INDEX INVENTORYITEM_TYPE_IX
/