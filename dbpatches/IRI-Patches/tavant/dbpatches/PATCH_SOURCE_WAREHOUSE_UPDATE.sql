--Purpose    : Patch for source warehouse update
--Author     : Hari Krishna Y D	
--Created On : 14 Feb 2009

ALTER TABLE inventory_item ADD(source_warehouse NUMBER)
/
ALTER TABLE INVENTORY_ITEM ADD (CONSTRAINT INV_ITEM_SRC_WAREHOUSE_FK 
FOREIGN KEY (SOURCE_WAREHOUSE) REFERENCES SOURCE_WAREHOUSE (ID))
/
COMMIT
/