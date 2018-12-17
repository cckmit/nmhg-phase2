--Purpose: Patch for modifying HUSS_PARTS_REPLACED_INSTALLED table for Multi Inventory Claims
--Author: Shraddha Nanda P 
--Created On: Date  09 JAN 2009

ALTER TABLE HUSS_PARTS_REPLACED_INSTALLED
ADD INVENTORY_LEVEL  NUMBER(1)
