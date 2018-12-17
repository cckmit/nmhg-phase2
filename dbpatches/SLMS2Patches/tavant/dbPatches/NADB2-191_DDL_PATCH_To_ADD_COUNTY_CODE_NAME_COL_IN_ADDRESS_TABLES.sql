-- PURPOSE    : PATCH To ADD COUNTY_CODE_NAME_COL_IN_ADDRESS_TABLES
-- AUTHOR     : P RAGHAVENDRA RAJU.
-- CREATED ON : 06-AUGUST-2014

alter table address add (COUNTY_CODE_NAME varchar2(2000))
/
alter table address_for_transfer add (COUNTY_CODE_NAME varchar2(2000))
/