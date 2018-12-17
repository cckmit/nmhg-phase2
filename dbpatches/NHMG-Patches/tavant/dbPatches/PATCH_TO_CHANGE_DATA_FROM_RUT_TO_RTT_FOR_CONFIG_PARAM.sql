--PURPOSE    : PATCH TO ALTER CONFIG_PARAM TABLE FOR ETR TO TTR
--AUTHOR     : Raghavendra
--CREATED ON : 30-Jan-13

update config_param set DISPLAY_NAME='Can external user perform Retail Truck Transfer' , description='Can external user perform Retail Truck Transfer' where DISPLAY_NAME='Can external user perform Retail Unit Transfer'
/
COMMIT
/