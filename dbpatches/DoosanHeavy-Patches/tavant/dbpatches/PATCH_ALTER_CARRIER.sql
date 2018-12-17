--PURPOSE    : Patch for adding description columns to Carrier table, as part of Doosan Heavy TWMS implementation
--AUTHOR     : Kuldeep Patil
--CREATED ON : 26-June-2012

ALTER TABLE carrier ADD description VARCHAR2(255)
/
