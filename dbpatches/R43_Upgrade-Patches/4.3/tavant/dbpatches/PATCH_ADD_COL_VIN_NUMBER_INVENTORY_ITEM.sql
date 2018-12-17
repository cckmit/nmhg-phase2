--Purpose    : vin_number is  added to inventory_item table, changes made as a part of 4.3 upgrade 
--Author     : Mayank Vikram
--Created On : 18/03/10
--Impact     : None

--ALTER TABLE inventory_item ADD vin_number VARCHAR2(255 CHAR)
--/
--Manish - Moved this part to DB patch PATCH_ADD_COL_INV_ITEM.sql, this patch not needed.
COMMIT
/