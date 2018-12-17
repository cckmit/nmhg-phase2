--PURPOSE    : Patch for adding new column to customer table, as part of NMHG TWMS implementation
--AUTHOR     : PRACHER PANCHOLI
--CREATED ON : 5-Nov-2012

ALTER TABLE customer ADD (si_code VARCHAR2(100))
/
