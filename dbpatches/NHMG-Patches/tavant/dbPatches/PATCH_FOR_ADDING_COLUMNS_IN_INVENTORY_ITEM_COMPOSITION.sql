--PURPOSE    : PATCH TO ADD STATUS,SERIAL_TYPE_DESCRIPTION COLUMNS  FOR INVENTORY_ITEM_COMPOSITION TABLE
--AUTHOR     : KALYANI
--CREATED ON : 17-MAY-13
ALTER TABLE INVENTORY_ITEM_COMPOSITION  ADD (STATUS VARCHAR2(255 BYTE),SERIAL_TYPE_DESCRIPTION VARCHAR2(255 BYTE))
/