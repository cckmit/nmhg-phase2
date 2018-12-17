--PURPOSE    : PATCH FOR ADDING ADDITIONAL COLUMN WPRA to part_return_action table
--AUTHOR     : Deepak Patel
--CREATED ON : 02-DEC-2012

alter table part_return_action add (WPRA_NUMBER VARCHAR2(255))
/