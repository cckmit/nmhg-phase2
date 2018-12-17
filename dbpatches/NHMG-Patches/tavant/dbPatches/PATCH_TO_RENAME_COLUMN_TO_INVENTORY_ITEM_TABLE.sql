--Purpose    : Patch to modify column name in inventory_item table.
--Author     : Suneetha Nagaboyina
--Created On : 21-NOV-2012

ALTER TABLE inventory_item RENAME COLUMN disclaimer_for TO is_disclaimer
/
commit
/
