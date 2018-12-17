--PURPOSE    : PATCH FOR SOURCE
--AUTHOR     : Lavin
--CREATED ON : 22-APRIL-10
update inventory_item set source='MAJORCOMPREGISTRATION' where source='MAJORREGISTRATION'
/
COMMIT
/


