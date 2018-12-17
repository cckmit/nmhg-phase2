--Purpose    : to add MANUFACTURING_SITE_INVENTORY in list of values and mapped it with                       inventory_item
--Author     : Pratima Rajak
--Created On : 17-08-2008

alter table inventory_item add MANUFACTURING_SITE_INVENTORY number(19)
/
ALTER TABLE inventory_item ADD (
  CONSTRAINT INV_ITEM_MANUFACTURINGSITE_FK 
 FOREIGN KEY (MANUFACTURING_SITE_INVENTORY) 
 REFERENCES LIST_OF_VALUES (ID))
/
commit
/