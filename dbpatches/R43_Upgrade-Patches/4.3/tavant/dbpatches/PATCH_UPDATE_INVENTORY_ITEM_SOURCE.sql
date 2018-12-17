--PURPOSE    : PATCH FOR SOURCE
--AUTHOR     : Lavin
--CREATED ON : 22-APRIL-10

--update inventory_item set source='MAJORCOMPREGISTRATION' where source='MAJORREGISTRATION'
--/
--Manish - Moved this part to DB patch PATCH_ADD_COL_INV_ITEM.sql, this patch not needed.
COMMIT
/

