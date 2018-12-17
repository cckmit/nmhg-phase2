--PURPOSE    : PATCH TO UPDATE DISPLAY VALUE FOR config_param_option
--AUTHOR     : ROHIT MEHROTRA
--CREATED ON : 06-JUNE-11
--IMPACT     : BU CONFIGURATION

update config_param_option set display_value='Only Retail Inventories With PDI', value='Only Retail Inventories With PDI' where display_value='Only Retail Inventories with PDI'
/
COMMIT
/
