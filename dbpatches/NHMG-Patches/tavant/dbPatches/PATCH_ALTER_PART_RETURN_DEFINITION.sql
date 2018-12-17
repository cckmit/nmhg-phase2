--PURPOSE    : Patch for adding new columns to Part Return Definition table, as part of Doosan Heavy TWMS implementation
--AUTHOR     : Kuldeep Patil
--CREATED ON : 26-June-2012

ALTER TABLE part_return_definition ADD (shipping_instructions VARCHAR2(4000), receiver_instructions VARCHAR2(4000))
/
