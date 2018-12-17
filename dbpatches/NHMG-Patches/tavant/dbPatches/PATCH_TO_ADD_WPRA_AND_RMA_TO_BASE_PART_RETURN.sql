--PURPOSE    : PATCH FOR ADDING ADDITIONAL COLUMN WPRA and RMA Number to Base_part_return table
--AUTHOR     : Deepak Patel
--CREATED ON : 22-NOV-2012

alter table base_part_return add (RMA_NUMBER VARCHAR2(255 CHAR))
/
