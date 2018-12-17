--Purpose    : Patch for altering unique constraint on Inventory item table
--Author     : kalyani	
--Created On : 17-APR-2013

DROP  INDEX unique_inventory_item
/
CREATE UNIQUE INDEX unique_inventory_item ON inventory_item ("SERIAL_NUMBER", "OF_TYPE", "CONDITION_TYPE", "D_ACTIVE", "BUSINESS_UNIT_INFO","SEQUENCE_NUMBER")
/


-- if it fails follow the below steps
--ALTER TABLE inventory_item DISABLE CONSTRAINT  unique_inventory_item;
--alter index unique_inventory_item disable;
--DROP  INDEX unique_inventory_item1 ;
--CREATE UNIQUE INDEX unique_inventory_item ON inventory_item ("SERIAL_NUMBER", "OF_TYPE", "CONDITION_TYPE", --"D_ACTIVE", "BUSINESS_UNIT_INFO","SEQUENCE_NUMBER")
--SELECT * FROM user_objects WHERE object_name LIKE 'UNIQUE%';
