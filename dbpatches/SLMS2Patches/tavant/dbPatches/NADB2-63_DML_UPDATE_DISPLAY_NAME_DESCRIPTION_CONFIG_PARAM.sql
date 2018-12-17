--PURPOSE    : PATCH_TO_UPDATE_DISPALY_NAME_AND_DESCRIPTION_IN_CONFIG_PARAM_FOR_FLAG_FOR_MANUAL_REVIEW
--AUTHOR     : Sumesh kumar.R
--CREATED ON : 29-JAN-2014
update config_param set description='Delivery Report/ PDI Audit Interval' where description='Flag for Manual Review'
/
update config_param set display_name='Delivery Report/ PDI Audit Interval' where display_name='Flag for Manual Review'
/
commit
/