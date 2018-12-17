--PURPOSE    : PATCH TO UPDATE columns to capture for NMHGSLMS-291
--AUTHOR     : Raghavendra
--CREATED ON : 02-SEP-13

update upload_mgt set columns_to_capture=62 where id=4
/
commit
/