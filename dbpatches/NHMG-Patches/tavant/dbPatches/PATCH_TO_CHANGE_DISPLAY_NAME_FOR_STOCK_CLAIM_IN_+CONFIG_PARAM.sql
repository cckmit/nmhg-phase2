--PURPOSE    : PATCH_TO_CHANGE_DISPLAY_NAME_FOR_STOCK_CLAIM_IN_ CONFIG_PARAM
--AUTHOR     : Raghavendra
--CREATED ON : 17-APR-13

update config_param set description ='Can a claim be filed for stock',display_name='Can a claim be filed for stock' where description='Can a claim be filed for stock?'
/
COMMIT
/