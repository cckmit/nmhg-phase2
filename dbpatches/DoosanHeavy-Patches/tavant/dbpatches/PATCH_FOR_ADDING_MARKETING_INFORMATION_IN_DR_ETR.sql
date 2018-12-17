--PURPOSE    : Patch to add marketing information in DR and ETR Page.
--AUTHOR     : Amrita Rout
--CREATED ON : 20-July-2012
alter table market add (
"TITLE"  VARCHAR2(255 CHAR))
/
alter table marketing_information add (
"TRADE_IN"  NUMBER(1,0))
/
alter table marketing_information add (
"FIRST_TIME_OWNER_OF_PRODUCT"  NUMBER(1,0))
/