--PURPOSE    : Patch for adding new column to item table, as part of NMHG TWMS implementation
--AUTHOR     : PRACHER PANCHOLI
--CREATED ON : 5-Nov-2012

ALTER TABLE item ADD (brand VARCHAR2(255))
/