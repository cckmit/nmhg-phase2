--PURPOSE    : PATCH_TO_ADD_NEW_COLUMN_IN_WAREHOUSE_TABLE
--AUTHOR     : Raghavendra
--CREATED ON : 17-APR-13



ALTER TABLE warehouse ADD business_name VARCHAR2(255 CHAR)
/
commit
/