--PURPOSE    : Patch for adding new column to item table, as part of NMHG TWMS implementation
--AUTHOR     : PRACHER PANCHOLI
--CREATED ON : 27-Nov-2012

ALTER TABLE item ADD (service_category VARCHAR2(400))
/